package org.saga.abilities;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.saga.Clock;
import org.saga.Clock.SecondTicker;
import org.saga.SagaLogger;
import org.saga.config.AbilityConfiguration;
import org.saga.config.ExperienceConfiguration;
import org.saga.exceptions.InvalidAbilityException;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.listeners.events.SagaInteractEntityEvent;
import org.saga.messages.AbilityMessages;
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
	transient private SagaPlayer sagaPlayer = null;

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
		
		definition = AbilityConfiguration.config().getDefinition(getName());
		if(definition == null){
			throw new InvalidAbilityException(getName());
		}
		
		// Transient:
		clock = false;
		if(cooldown > 0 || active > 0){
			startClock();
		}
		
		lastCooldown = -1;
		
		return integrity;
		

	}
	
	
	
	
	// Clock:
	/**
	 * Starts the clock.
	 * 
	 */
	private void startClock() {
		
		Clock.clock().registerSecondTick(this);
		
		clock = true;
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.SecondTicker#clockSecondTick()
	 */
	@Override
	public boolean clockSecondTick() {
		
		
		if(cooldown == 1){
			getSagaPlayer().message(AbilityMessages.cooldownEnd(this));
		}
		
		if(cooldown > 0) cooldown --;
		if(active > 0) active --;
		
		// Stop clock:
		if(cooldown <= 0 && active <= 0){
			clock = false;
			return false;
		}else{
			return true;
		}
		
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
	 * Sets the player.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void setPlayer(SagaPlayer sagaPlayer) {
		
		this.sagaPlayer = sagaPlayer;
		
	}
	
	/**
	 * Gets the saga player
	 * 
	 * @return saga player
	 */
	public SagaPlayer getSagaPlayer() {
		return sagaPlayer;
	}

	/**
	 * Gets the ability score.
	 * 
	 * @return ability score
	 */
	public Integer getScore() {

		return sagaPlayer.getAbilityScore(getName());

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

		Double exp = ExperienceConfiguration.config().getExp(this, value);
		sagaPlayer.awardExp(exp);
		
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

		
		Material material = definition.getUsedItem();
		Integer amount = definition.getUsedAmount(getScore());
		
		// Nothing to remove.
		if(material.equals(Material.AIR) || amount == 0) return;
		
		getSagaPlayer().removeItem(material, amount);
		
		
	}
	
	/**
	 * Uses the required items.
	 * 
	 * @param material used material
	 * @param amount used amount
	 */
	public void useItems(Material material, Integer amount) {

		
		// Nothing to remove.
		if(material.equals(Material.AIR) || amount == 0) return;
		
		getSagaPlayer().removeItem(new ItemStack(material, amount));
		
		
	}
	
	/**
	 * Checks if the ability can be activated.
	 * 
	 * @return true if can be activated
	 */
	public boolean checkActivation() {

		PlayerInventory inventory = sagaPlayer.getPlayer().getInventory();
		
		ItemStack itemHand = inventory.getItemInHand();
		if(itemHand == null) itemHand = new ItemStack(Material.AIR);
		
		return getDefinition().getActivationItems().contains(itemHand.getType());
		

	}
	
	/**
	 * Checks if the player has enough of the required item.
	 * 
	 * @return true if enough items
	 */
	public boolean checkCost() {

		
		PlayerInventory inventory = sagaPlayer.getPlayer().getInventory();
		
		Material usedItem = getDefinition().getUsedItem();
		Integer usedAmount = getDefinition().getUsedAmount(getScore());
		
		if(usedItem == Material.AIR) return true;
		
		return inventory.contains(usedItem, usedAmount);

		
	}
	
	/**
	 * Checks if the player has enough of the required item.
	 * 
	 * @param material used item
	 * @param amount used amount
	 * @return true if enough
	 */
	public boolean checkCost(Material material, Integer amount) {

		
		PlayerInventory inventory = sagaPlayer.getPlayer().getInventory();
		
		if(material == Material.AIR) return true;
		
		return inventory.contains(material, amount);

		
	}
	
	
	/**
	 * Starts the cooldown.
	 * 
	 */
	protected void startCooldown() {

		this.cooldown = definition.getCooldown(getScore());
		
		// Start clock:
		if(!clock) startClock();

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
			if(getCooldown() != lastCooldown){
				getSagaPlayer().message(AbilityMessages.onCooldown(this));
			}
			lastCooldown = getCooldown();
			
			return false;
			
		}
		
		if(getScore() < 1) return false;
		
		if(!checkCost()){
			sagaPlayer.message(AbilityMessages.insufficientItems(this, definition.getUsedItem(), definition.getUsedAmount(getScore())));
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
		
	}
	
	
	
	// Triggering:
	/**
	 * Triggers the ability.
	 * 
	 * @param event event
	 * @return true if triggered
	 */
	public boolean trigger(SagaInteractEntityEvent event) {
		return false;
	}
	
	/**
	 * Triggers the ability.
	 * 
	 * @param event event
	 * @return true if triggered
	 */
	public boolean trigger(PlayerInteractEvent event) {
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
