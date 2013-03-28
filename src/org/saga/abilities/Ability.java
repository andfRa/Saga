package org.saga.abilities;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.saga.Clock;
import org.saga.Clock.SecondTicker;
import org.saga.SagaLogger;
import org.saga.abilities.AbilityDefinition.ActivationAction;
import org.saga.config.AbilityConfiguration;
import org.saga.exceptions.InvalidAbilityException;
import org.saga.listeners.events.SagaDamageEvent;
import org.saga.messages.AbilityMessages;
import org.saga.messages.effects.StatsEffectHandler;
import org.saga.player.SagaLiving;
import org.saga.player.SagaPlayer;
import org.saga.saveload.SagaCustomSerialization;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.chat.ChatUtil;

public abstract class Ability extends SagaCustomSerialization implements SecondTicker{


	/**
	 * Ability name.
	 */
	private String name;
	
	/**
	 * Definition.
	 */
	transient private AbilityDefinition definition;
	
	/**
	 * Active for.
	 */
	private Integer active;
	
	/**
	 * Cooldown.
	 */
	private Integer cooldown;

	/**
	 * Saga player.
	 */
	transient private SagaLiving sagaLiving = null;

	/**
	 * Clock is enabled if true.
	 */
	transient private boolean clock;
	
	/**
	 * Cooldown last value.
	 */
	transient private Integer lastCooldown;

	/**
	 * Ticks at which the ability was triggered.
	 */
	transient private Integer triggeredTicks;
	
	
	
	// Initialisation:
	/**
	 * Used by gson.
	 * 
	 */
	public Ability() {
	}
	
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Ability(AbilityDefinition definition) {
		
		this.definition = definition;
		this.name = definition.getName();
		this.cooldown = 0;
		this.active = 0;
		this.clock = false;
		this.lastCooldown = -1;
		
		this.triggeredTicks = 0;
		
	}

	/**
	 * Goes trough all the fields and makes sure everything has been set after gson load.
	 * If not, it fills the field with defaults.
	 * 
	 * @param definition ability definition
	 * @return true if everything was correct.
	 */
	public boolean complete() throws InvalidAbilityException{
		
		
		boolean integrity = true;
		
		if (name == null) {
			SagaLogger.severe(this, "name");
			name = ChatUtil.className(getClass());
			integrity = false;
		}
		
		if (cooldown == null) {
			SagaLogger.nullField(this, "cooldown");
			cooldown = 0;
			integrity = false;
		}
		
		if (active == null) {
			SagaLogger.nullField(this, "active");
			active = 0;
			integrity = false;
		}
		
		definition = AbilityConfiguration.config().getDefinition(name);
		if(definition == null){
			throw new InvalidAbilityException(name);
		}
		
		// Transient:
		triggeredTicks = 0;
		clock = false;
		updateClock();
		
		lastCooldown = -1;
		
		return integrity;
		

	}
	
	
	
	// Clock:
	/**
	 * Updates the clock state.
	 * Use this method with {@link #checkClock()} if you need second ticks.
	 * 
	 * @return current clock state.
	 */
	public final boolean updateClock() {

		// Clock running:
		if(clock){
			clock = checkClock();
		}
		
		// Clock not running:
		else{
			clock = checkClock();
			if(clock) Clock.clock().enableSecondTick(this);
		}
		
		return clock;
		
	}
	
	/**
	 * Checks if the clock should be running.
	 * 
	 * @return true if clock should be running
	 */
	public boolean checkClock() {
		
		return active > 0 || cooldown > 0;
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.SecondTicker#clockSecondTick()
	 */
	@Override
	public boolean clockSecondTick() {
		
		if(cooldown == 1 && sagaLiving instanceof SagaPlayer){
			((SagaPlayer) sagaLiving).message(AbilityMessages.cooldownEnd(this));
		}
		
		if(cooldown > 0) cooldown --;
		if(active > 0) active --;
		
		// Clock state:
		return updateClock();
		
	}

	
	
	// Parameters:
	/**
	 * Gets the definition.
	 * 
	 * @return definition
	 */
	public AbilityDefinition getDefinition() {
		return definition;
	}
	
	/**
	 * Gets the ability name
	 * 
	 * @return ability name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the entity.
	 * 
	 * @param sagaLiving saga entity
	 */
	public void setSagaLiving(SagaLiving sagaLiving) {
		
		this.sagaLiving = sagaLiving;
		
	}
	
	/**
	 * Gets the saga entity.
	 * 
	 * @return saga entity
	 */
	public SagaLiving getSagaLiving() {
		return sagaLiving;
	}

	/**
	 * Gets the ability score.
	 * 
	 * @return ability score
	 */
	public Integer getScore() {

		return sagaLiving.getAbilityScore(getName());

	}
	
	/**
	 * Gets the active.
	 * 
	 * @return the active
	 */
	public Integer getActive() {
		return active;
	}

	/**
	 * Gets the cooldown.
	 * 
	 * @return the cooldown
	 */
	public Integer getCooldown() {
	
	
		return cooldown;
	}

	/**
	 * Gets the total cooldown.
	 * 
	 * @return the total cooldown
	 */
	public Integer getTotalCooldown() {
		return getDefinition().getCooldown(getScore());
	}
	
	
	
	// Trigger requirements:
	/**
	 * Checks if the entity has enough of the required item.
	 * 
	 * @return true if enough items
	 */
	public boolean checkItems() {

		if(!(sagaLiving instanceof SagaPlayer)) return true;
		
		PlayerInventory inventory = ((SagaPlayer) sagaLiving).getPlayer().getInventory();
		
		Material usedItem = getDefinition().getUsedItem();
		Integer usedAmount = getDefinition().getUsedAmount(getScore());
		
		if(usedItem == Material.AIR) return true;
		
		return inventory.contains(usedItem, usedAmount);

		
	}
	
	/**
	 * Uses the required items.
	 * 
	 */
	public void useItems() {

		if(!(sagaLiving instanceof SagaPlayer)) return;
		
		Material material = definition.getUsedItem();
		Integer amount = definition.getUsedAmount(getScore());
		
		// Nothing to remove.
		if(material.equals(Material.AIR) || amount == 0) return;
		
		((SagaPlayer) sagaLiving).removeItem(material, amount);
		
	}
	
	/**
	 * Uses the required items.
	 * 
	 * @param material used material
	 * @param amount used amount
	 */
	public void useItems(Material material, Integer amount) {

		if(!(sagaLiving instanceof SagaPlayer)) return;
		
		// Nothing to remove.
		if(material.equals(Material.AIR) || amount == 0) return;
		
		((SagaPlayer) sagaLiving).removeItem(new ItemStack(material, amount));
		
	}

	
	/**
	 * Checks energy requirement.
	 * 
	 * @return true of the energy is high enough
	 */
	public boolean checkEnergy() {
		return sagaLiving.getEnergy() >= definition.getUsedEnergy(getScore());
	}
	
	/**
	 * Uses the required energy.
	 * 
	 */
	public void useEnergy() {

		int used = definition.getUsedEnergy(getScore());
		if(used == 0.0) return;
		
		sagaLiving.modEnergy(-used);
		
	}
	
	
	/**
	 * Checks the item in hand.
	 * 
	 * @return true if item in hand correct
	 */
	public boolean checkHeldItem() {

		if(!(sagaLiving instanceof SagaPlayer)) return true;
		
		PlayerInventory inventory = ((SagaPlayer) sagaLiving).getPlayer().getInventory();
		
		ItemStack itemHand = inventory.getItemInHand();
		if(itemHand == null) itemHand = new ItemStack(Material.AIR);
		
		return getDefinition().getItemRestrictions().contains(itemHand.getType()) || getDefinition().getItemRestrictions().size() == 0;

	}
	
	
	/**
	 * Checks if enough ticks has passed after last trigger.
	 * 
	 * @return true if enough ticks passed
	 */
	public boolean checkCooldownTicks() {
		return sagaLiving.getTicksLived() - triggeredTicks > definition.getCooldownTicks();
	}
	
	/**
	 * Starts the cooldown.
	 * 
	 */
	protected void startCooldown() {

		this.cooldown = definition.getCooldown(getScore());
		
		// Update clock:
		updateClock();

	}

	/**
	 * Checks if on cooldown.
	 * 
	 * @return true if on cooldown
	 */
	protected boolean isCooldown() {
		return cooldown > 0;
	}
	
	
	
	// Pretriggers and aftertriggers:
	/**
	 * Called before the ability is triggered.
	 * 
	 * @return true if can be triggered
	 */
	public final boolean handlePreTrigger() {
		
		
		if(!checkHeldItem()) return false;

		// Score:
		Integer score = getScore();
		if(score < 1) return false;
		
		// Cooldown ticks:
		if(!checkCooldownTicks()){
			
			return false;
			
		}
		
		// Cooldown:
		if(isCooldown()){
			
			// Prevent cooldown spam:
			if(getCooldown() != lastCooldown && getDefinition().getActivationAction() != ActivationAction.NONE && !useSilentPreTrigger()){
				getSagaLiving().message(AbilityMessages.onCooldown(this));
			}
			lastCooldown = getCooldown();
			
			return false;
			
		}
		
		// Item cost:
		if(!checkItems()){
			
			if(!useSilentPreTrigger()) sagaLiving.message(AbilityMessages.insufficientItems(this, definition.getUsedItem(), definition.getUsedAmount(getScore())));
			return false;
			
		}
		
		// Energy:
		if(!checkEnergy()){
			
			if(!useSilentPreTrigger()) sagaLiving.message(AbilityMessages.insuficientEnergy(getName()));
			return false;
			
		}
		
		return true;
		
		
	}
	
	/**
	 * Called after the ability is triggered.
	 * 
	 */
	public void handleAfterTrigger() {
		
		useItems();
		useEnergy();
		startCooldown();
		triggeredTicks = sagaLiving.getTicksLived();
		
		if(sagaLiving instanceof SagaPlayer){

			// Ability effect:
			StatsEffectHandler.playAbility((SagaPlayer) sagaLiving, this);
			
			// Statistics:
			StatisticsManager.manager().addAbilityUse(this);
			
			
		}
		
	}

	/**
	 * Specifies if messages should be ignore in the pre-trigger.
	 * 
	 * @return true if ignore
	 */
	public boolean useSilentPreTrigger(){
		return false;
	}

	
	/**
	 * Called before the ability is triggered.
	 * 
	 * @param event event
	 * @return true if can be triggered
	 */
	public boolean handleInteractPreTrigger(PlayerInteractEvent event) {
		return false;
	}

	/**
	 * Called before the ability is triggered.
	 * 
	 * @param event event
	 * @return true if can be triggered
	 */
	public boolean handleAttackPreTrigger(SagaDamageEvent event) {
		return false;
	}

	/**
	 * Called before the ability is triggered.
	 * 
	 * @param event event
	 * @return true if can be triggered
	 */
	public boolean handleDefendPreTrigger(SagaDamageEvent event) {
		return false;
	}
	
	/**
	 * Called before the ability is triggered.
	 * 
	 * @param event event
	 * @return true if can be triggered
	 */
	public boolean handleProjectileHitPreTrigger(ProjectileHitEvent event) {
		return false;
	}
	
	/**
	 * Called before the ability is triggered.
	 * 
	 * @param event event
	 * @return true if can be triggered
	 */
	public boolean handleShearPreTrigger(PlayerShearEntityEvent event) {
		return false;
	}
	
	/**
	 * Called before the ability is triggered.
	 * 
	 * @param event event
	 * @return true if can be triggered
	 */
	public boolean handleFoodLevelChangePreTrigger(FoodLevelChangeEvent event) {
		return false;
	}

	/**
	 * Called before the ability is triggered.
	 * 
	 * @param event event
	 * @return true if can be triggered
	 */
	public boolean handleTargetedPreTrigger(EntityTargetEvent event) {
		return false;
	}
	
	
	
	// Triggering:
	/**
	 * Triggers the ability.
	 * 
	 * @param event event
	 * @return true if triggered
	 */
	public boolean triggerInteract(PlayerInteractEvent event) {
		return false;
	}
	
	/**
	 * Triggers the ability.
	 * 
	 * @param event event
	 * @return true if triggered
	 */
	public boolean triggerAttack(SagaDamageEvent event) {
		return false;
	}

	/**
	 * Triggers the ability.
	 * 
	 * @param event event
	 * @return true if triggered
	 */
	public boolean triggerDefend(SagaDamageEvent event) {
		return false;
	}

	/**
	 * Triggers projectile hit.
	 * 
	 * @param event event
	 * @return true if triggered
	 */
	public boolean triggerProjectileHit(ProjectileHitEvent event) {
		return false;
	}

	/**
	 * Triggers shear.
	 * 
	 * @param event event
	 * @return true if triggered
	 */
	public boolean triggerShear(PlayerShearEntityEvent event) {
		return false;
	}

	/**
	 * Triggers food level change.
	 * 
	 * @param event event
	 * @return true if triggered
	 */
	public boolean triggerFoodLevelChange(FoodLevelChangeEvent event) {
		return false;
	}

	/**
	 * Triggers getting targeted.
	 * 
	 * @param event event
	 * @return true if triggered
	 */
	public boolean triggerTargeted(EntityTargetEvent event) {
		return false;
	}


	
	// Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof Ability){
			return ((Ability) obj).getName().equalsIgnoreCase(getName());
		}
		return false;
		
	}

	
}
