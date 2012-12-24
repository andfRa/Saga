package org.saga.abilities;

import org.bukkit.Material;
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
import org.saga.config.ExperienceConfiguration;
import org.saga.exceptions.InvalidAbilityException;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.messages.AbilityMessages;
import org.saga.messages.effects.StatsEffectHandler;
import org.saga.player.SagaLiving;
import org.saga.player.SagaPlayer;
import org.saga.saveload.SagaCustomSerialization;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.text.TextUtil;

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
	transient private SagaLiving<?> sagaLiving = null;

	/**
	 * Clock is enabled if true.
	 */
	transient private boolean clock;
	
	/**
	 * Cooldown last value.
	 */
	transient private Integer lastCooldown;
	
	
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
			name = TextUtil.className(getClass());
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

	
	
	// Interaction:
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
	public void setSagaLiving(SagaLiving<?> sagaLiving) {
		
		this.sagaLiving = sagaLiving;
		
	}
	
	/**
	 * Gets the saga entity.
	 * 
	 * @return saga entity
	 */
	public SagaLiving<?> getSagaLiving() {
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

	/**
	 * Awards exp for ability usage.
	 * 
	 * @param value ability dependent value
	 * @return exp awarded
	 */
	public Double awardExp(Integer value) {

		if(!(sagaLiving instanceof SagaPlayer)) return 0.0;
		
		Double exp = ExperienceConfiguration.config().getExp(this, value);
		((SagaPlayer) sagaLiving).awardExp(exp);
		
		// Statistics:
		StatisticsManager.manager().addExp("ability", getName(), exp);
		
		return exp;

	}
	
	
	
	// Trigger handling:
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
	 * Checks if the ability can be activated.
	 * 
	 * @return true if can be activated
	 */
	public boolean checkActivation() {

		if(!(sagaLiving instanceof SagaPlayer)) return true;
		
		PlayerInventory inventory = ((SagaPlayer) sagaLiving).getPlayer().getInventory();
		
		ItemStack itemHand = inventory.getItemInHand();
		if(itemHand == null) itemHand = new ItemStack(Material.AIR);
		
		return getDefinition().getItemRestrictions().contains(itemHand.getType()) || getDefinition().getItemRestrictions().size() == 0;

	}
	
	/**
	 * Checks if the entity has enough of the required item.
	 * 
	 * @return true if enough items
	 */
	public boolean checkCost() {

		if(!(sagaLiving instanceof SagaPlayer)) return true;
		
		PlayerInventory inventory = ((SagaPlayer) sagaLiving).getPlayer().getInventory();
		
		Material usedItem = getDefinition().getUsedItem();
		Integer usedAmount = getDefinition().getUsedAmount(getScore());
		
		if(usedItem == Material.AIR) return true;
		
		return inventory.contains(usedItem, usedAmount);

		
	}
	
	/**
	 * Checks if the entity has enough of the required item.
	 * 
	 * @param material used item
	 * @param amount used amount
	 * @return true if enough
	 */
	public boolean checkCost(Material material, Integer amount) {

		if(!(sagaLiving instanceof SagaPlayer)) return true;
		
		PlayerInventory inventory = ((SagaPlayer) sagaLiving).getPlayer().getInventory();
		
		if(material == Material.AIR) return true;
		
		return inventory.contains(material, amount);

		
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
	
	
	/**
	 * Called before the ability is triggered.
	 * 
	 * @return true if can be triggered
	 */
	public boolean handlePreTrigger() {
		
		
		if(!checkActivation()) return false;
		
		if(isCooldown()){
			
			// Prevent cooldown spam:
			if(getCooldown() != lastCooldown && getDefinition().getActivationAction() != ActivationAction.NONE && !useSilentPreTrigger()){
				getSagaLiving().message(AbilityMessages.onCooldown(this));
			}
			lastCooldown = getCooldown();
			
			return false;
			
		}
		
		if(getScore() < 1) return false;
		
		if(!checkCost()){
			
			// Prevent cost spam:
			if(!useSilentPreTrigger()) sagaLiving.message(AbilityMessages.insufficientItems(this, definition.getUsedItem(), definition.getUsedAmount(getScore())));
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
		startCooldown();

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
	public boolean handlePreTrigger(PlayerInteractEvent event) {
		return handlePreTrigger();
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
	public boolean triggerAttack(SagaEntityDamageEvent event) {
		return false;
	}

	/**
	 * Triggers the ability.
	 * 
	 * @param event event
	 * @return true if triggered
	 */
	public boolean triggerDefend(SagaEntityDamageEvent event) {
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
