package org.saga.abilities;


import java.util.HashSet;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.Clock;
import org.saga.Clock.SecondTicker;
import org.saga.Saga;
import org.saga.abilities.AbilityDefinition.ActivationType;
import org.saga.config.AbilityConfiguration;
import org.saga.config.AbilityConfiguration.InvalidAbilityException;
import org.saga.player.PlayerMessages;
import org.saga.player.SagaPlayer;
import org.saga.utility.SagaCustomSerialization;

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
	 * True if the clock is enabled.
	 */
	transient private boolean clockEnabled;
	
	/**
	 * Saga player.
	 */
	transient private SagaPlayer sagaPlayer = null;
	
	/**
	 * Last triggered cooldown.
	 */
	transient private Integer triggeredCooldown;
	
	
	// Initialization:
	/**
	 * Initializes using definition.
	 * 
	 * @param definition ability definition
	 */
	public Ability(AbilityDefinition definition) {
		
		this.definition = definition;
		this.name = definition.getName();
		this.cooldown = 0;
		this.active = 0;
		this.triggeredCooldown = -1;
		
	}

	/**
	 * Used by gson.
	 * 
	 */
	public Ability() {
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
			Saga.severe(this, "name field failed to initialize", "settin default");
			name = getStaticName();
			integrity = false;
		}
		
		if (cooldown == null) {
			Saga.severe(this, "cooldown field failed to initialize", "settin default");
			cooldown = 0;
			integrity = false;
		}
		
		if (active == null) {
			Saga.severe(this, "active field failed to initialize", "settin default");
			active = 0;
			integrity = false;
		}
		
		definition = AbilityConfiguration.config().getDefinition(getName());
		if(definition == null){
			throw new InvalidAbilityException(getName());
		}
		
		// Transient:
		clockEnabled = false;
		if(cooldown > 0 || active > 0){
			startClock();
		}
		triggeredCooldown = -1;
		
		return integrity;
		

	}
	

	// Activation:
	/**
	 * Starts activate.
	 * 
	 * @param active activate
	 */
	protected void activate() {
		

		this.active = getDefinition().getActiveFor(getSkillLevel());
		
		// Start clock:
		if(!isClockEnabled() && active > 0){
			startClock();
		}

		
	}

	/**
	 * Deactivates the ability.
	 * 
	 */
	public void deactivate() {

		this.active = 0;

	}

	/**
	 * Gets the time the ability is active for.
	 * 
	 * @return ability active time
	 */
	public Integer getActive() {
		return active;
	}
	
	/**
	 * Checks if the ability is on cooldown.
	 * 
	 * @return true if on cooldown
	 */
	public boolean isActive() {
		
		// Always active:
		ActivationType type = getDefinition().getActivationType();
		if(type.equals(ActivationType.INSTANT) || type.equals(ActivationType.PASSIVE)) return true;
		
		return active > 0;
		
	}
	
	/**
	 * Gets the activation type.
	 * 
	 * @return activation type
	 */
	public ActivationType getActivationType() {
		return definition.getActivationType();
	}
	
	
	// Cooldown:
	/**
	 * Starts cooldown.
	 * 
	 * @param cooldown cooldown
	 */
	protected void startCooldown(Integer cooldown) {
		
		this.cooldown = cooldown;
		
		// Start clock:
		if(!isClockEnabled() && cooldown > 0){
			startClock();
		}
		
	}

	/**
	 * Starts the cooldown.
	 * 
	 */
	protected void startCooldown() {

		startCooldown(definition.getCooldown(getSkillLevel()));

	}
	
	/**
	 * Checks if the ability is on cooldown.
	 * 
	 * @return true if on cooldown
	 */
	public boolean isOnCooldown() {
		return cooldown > 0;
	}
	
	/**
	 * Gets the cooldown.
	 * 
	 * @return cooldown
	 */
	public Integer getCooldown() {
		return cooldown;
	}

	/**
	 * Gets the total cooldown.
	 * 
	 * @return total cooldown
	 */
	public Integer getTotalCooldown() {
		return definition.getCooldown(getSkillLevel());
	}

	
	// Material usage:
	/**
	 * Checks if there is enough materials to use the ability.
	 * 
	 * @return true can be used
	 */
	public boolean checkUsageMaterials() {
		
		
		Material usedMaterial = getUsedMaterial();
		Integer usedAmount = getAbsoluteUsedAmount();
		
		if(usedMaterial.equals(Material.AIR) || usedAmount == 0){
			return true;
		}
		
		return sagaPlayer.getItemCount(usedMaterial) >= usedAmount;
		
		
	}
	
	/**
	 * Uses the required materials.
	 * 
	 */
	public void useMaterials() {

		
		Integer amount = definition.getUsedAmount(getSkillLevel());
		Material material = definition.getUsedMaterial();
		
		// Nothing to remove.
		if(material.equals(Material.AIR) || amount == 0){
			return;
		}
		
		getSagaPlayer().removeItem(new ItemStack(material, amount));
		
		
	}

	/**
	 * Gets the usedMaterial.
	 * 
	 * @return the usedMaterial
	 */
	public Material getUsedMaterial() {
		return definition.getUsedMaterial();
	}

	/**
	 * Gets the absolute amount of used material.
	 * 
	 * @return amount of used material
	 */
	public Integer getAbsoluteUsedAmount() {
		return definition.getAbsoluteUsedAmount(getSkillLevel());
	}

	
	// Inform:
	/**
	 * Informs cooldown.
	 * 
	 */
	protected void informCooldown() {

		
		// Stop cooldown inform spam and and for passive abilities:
		Integer cooldown = getCooldown();
		if(cooldown == this.triggeredCooldown || getActivationType() == ActivationType.PASSIVE) return;
		
		triggeredCooldown = cooldown;
		
		sagaPlayer.message(PlayerMessages.onCooldown(this));

		
	}
	
	/**
	 * Informs cooldown end.
	 * 
	 */
	protected void informCooldownEnd() {

		sagaPlayer.message(PlayerMessages.cooldownEnd(this));
		
		sagaPlayer.playEffect(Effect.CLICK1, 0);
		
	}

	/**
	 * Informs that the ability is already activated.
	 * 
	 */
	protected void informAlreadyActive() {

		sagaPlayer.message(PlayerMessages.alreadyActive(this));
		
	}

	/**
	 * Informs that the ability was activated.
	 * 
	 */
	protected void informActivated() {

		sagaPlayer.message(PlayerMessages.activated(this));
		
	}
	
	/**
	 * Informs that the ability was deactivated.
	 * 
	 */
	protected void informDeactivated() {

		sagaPlayer.message(PlayerMessages.deactivated(this));
		
		sagaPlayer.playEffect(Effect.CLICK2, 0);
		
	}
	
	/**
	 * Informs that there isn't enough materials.
	 * 
	 */
	protected void informNotEnoughMaterials() {
		
		Material usedMaterial = getUsedMaterial();
		Integer usedAmount = getAbsoluteUsedAmount();
		
		sagaPlayer.message(PlayerMessages.insufficientMaterials(this, usedMaterial, usedAmount));
		
	}
	
	
	// Clock:
	/**
	 * Starts the clock.
	 * 
	 */
	private void startClock() {
		
		Clock.clock().registerSecondTick(this);
		
		clockEnabled = true;
		
	}
	
	/**
	 * Stops the clock.
	 * 
	 */
	private void stopClock() {
		
		Clock.clock().unregisterSecondTick(this);
		
		clockEnabled = false;
		
	}
	
	/**
	 * Checks if the clock is enables.
	 * 
	 * @return true if enabled
	 */
	public boolean isClockEnabled() {
		return clockEnabled;
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.SecondTicker#clockSecondTick()
	 */
	@Override
	public void clockSecondTick() {
		

		// Cooldown:
		if(cooldown > 0){
			cooldown --;
			
			// Inform cooldown:
			if(cooldown <= 0){
				
				informCooldownEnd();
			
			}
			
		}
		
		// Active:
		if(active > 0){
			
			active --;

			// Deactivate:
			if(active <= 0){
				
				handleDeactivate();
				
			}
			
		}
		
		// Stop clock:
		if(cooldown <= 0 && active <= 0){
				
			stopClock();
			
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
	 * Gets the static ability name
	 * 
	 * @return static ability name
	 */
	public String getStaticName() {
		return getClass().getSimpleName().replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2").toLowerCase();
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
	 * Returns the skill.
	 * 
	 * @return skill
	 */
	public Integer getSkillLevel() {
		
		HashSet<String> baseSkills = getDefinition().getBaseSkills();
		return getSagaPlayer().getModifiedSkillMultiplier(baseSkills);
		
	}
	
	
	// Experience:
	/**
	 * Awards experience for usage.
	 * 
	 * @param expval experience value
	 * @return experience awarded
	 */
	public Double awardExperience(Integer expval) {
	
		Double exp = getDefinition().getExpReward(expval);
		
		getSagaPlayer().onAbilityExp(this, sagaPlayer.matchProficiency(this), exp);
		
		return exp;
		
	}

	/**
	 * Awards experience for usage.
	 * 
	 * @return experience awarded
	 */
	public Double awardExperience() {

		return awardExperience(0);
		
	}

	
	// Ability usage:
	/**
	 * Called on activation.
	 * 
	 * @return activated if true
	 */
	public boolean handleActivate() {
		
		
		// Activate:
		switch (definition.getActivationType()) {
		
		case INSTANT:
			
			return false;
			
		case TIMED:
			
			// Already active:
			if(isActive()){
				informAlreadyActive();
				return false;
			}

			// Cooldown:
			if(isOnCooldown()){
				informCooldown();
				return false;
			}

			// Materials:
			if(!checkUsageMaterials()){
				informNotEnoughMaterials();
				return false;
			}
			
			// Activate event:
			if(!onPreActivate()) return false;

			// Activate:
			activate();
			
			// Inform:
			informActivated();

			// Use materials:
			useMaterials();
			
			break;

		case SINGLE_USE:

			// Already active:
			if(isActive()){
				informAlreadyActive();
				return false;
			}
			
			// Cooldown:
			if(isOnCooldown()){
				informCooldown();
				return false;
			}

			// Activate event:
			if(!onPreActivate()) return false;

			// Activate:
			activate();
			
			// Inform:
			informActivated();
			
			break;
		
		case TOGGLE:

			// Cooldown:
			if(isOnCooldown()){
				informCooldown();
				return false;
			}
			
			if(isActive()){
				
				// Deactivate:
				handleDeactivate();
				
				return false;
				
			}else{

				// Activate event:
				if(!onPreActivate()) return false;

				// Activate:
				activate();
				
				// Inform:
				informActivated();
				
				return true;
				
			}
			
		case PASSIVE:
	
			return true;
	
		default:
			
			break;
		}
		
		return true;
		
		
	}

	/**
	 * Called on deactivation.
	 * 
	 * @return deactivated if true
	 */
	public void handleDeactivate() {
		
		
		// Activate:
		switch (definition.getActivationType()) {
		
		case INSTANT:
			
			return;
			
		case TIMED:

			// Cooldown:
			startCooldown();

			// Deactivate event:
			if(!onPreDeactivate()) return;

			// Deactivate:
			deactivate();
			
			// Inform:
			informDeactivated();
			
			return;
			
		case SINGLE_USE:

			// Cooldown:
			startCooldown();

			// Deactivate event:
			if(!onPreDeactivate()) return;

			// Deactivate:
			deactivate();
			
			// Inform:
			informDeactivated();
			
			return;
			
		default:

			// Deactivate event:
			if(!onPreDeactivate()) return;

			// Deactivate:
			deactivate();
			
			// Inform:
			informDeactivated();
			
			return;
			
		}
		
		
	}
	
	/**
	 * Handles before use.
	 * 
	 * @return true if can proceed with use.
	 */
	public final boolean handlePreUse() {

		
		ActivationType type = getDefinition().getActivationType();
		
		// Materials:
		switch (type) {
		case TIMED:
			
			// Active:
			if(!isActive()){
				return false;
			}
			
			break;

		default:

			// Active:
			if(!isActive()){
				return false;
			}
			
			// Cooldown:
			if(isOnCooldown()){
				informCooldown();
				return false;
			}
			
			// Materials:
			if(!checkUsageMaterials()){
				informNotEnoughMaterials();
				return false;
			}
			
			break;
			
		}
		
		return true;
	
		
	}

	/**
	 * Called when the ability was used.
	 * 
	 */
	public final void handleAfterUse() {

		
		// Deactivate
		switch (definition.getActivationType()) {
		
		case INSTANT:
			
			// Cooldown:
			startCooldown();

			// Use materials:
			useMaterials();
			
			break;

		case TIMED:
			
			break;

		case SINGLE_USE:
			
			// Cooldown:
			startCooldown();

			// Use materials:
			useMaterials();
			
			handleDeactivate();	
			
			break;
		
		case TOGGLE:

			// Cooldown:
			startCooldown();

			// Use materials:
			useMaterials();
			
			break;
			
		case PASSIVE:

			// Cooldown:
			startCooldown();

			// Use materials:
			useMaterials();
			
			break;
	
		default:

			// Cooldown:
			startCooldown();

			// Use materials:
			useMaterials();
			
			break;
			
		}
		
		
	}

	
	// Event:
	/**
	 * Called when the ability was activated.
	 * 
	 * @return true if activated
	 */
	public boolean onPreActivate() {
		return true;
	}

	/**
	 * Called when the ability was deactivated.
	 * 
	 * @return true if deactivated
	 */
	public boolean onPreDeactivate() {
		return true;
	}
	
	/**
	 * Triggers the ability.
	 * 
	 * @param event event
	 * @return true of triggered
	 */
	public boolean instant(PlayerInteractEvent event) {
		return false;
	}

	/**
	 * Called when the player interacts.
	 * 
	 * @param event event
	 * @return true if gets deactivated
	 */
	public boolean onPlayerInteract(PlayerInteractEvent event) {
		return true;
	}
	
	/**
	 * Called when the player gets damaged by a creature.
	 * 
	 * @param event event
	 */
	public boolean onHitByCreature(EntityDamageByEntityEvent event, Creature creature) {
		
		return false;
		
	}
	
	/**
	 * Called when the player damages a creature.
	 * 
	 * @param event event
	 */
	public boolean onHitCreature(EntityDamageByEntityEvent event, Creature creature) {
		
		return false;
		
	}
	
	/**
	 * Called when the player gets damaged by a player.
	 * 
	 * @param event event
	 * @return true if gets deactivated
	 */
	public boolean onHitByPlayer(EntityDamageByEntityEvent event, SagaPlayer attacker) {
		
		return false;
		
	}
	
	/**
	 * Called when the player hits a player.
	 * 
	 * @param event event
	 * @return true if gets deactivated
	 */
	public boolean onHitPlayer(EntityDamageByEntityEvent event, SagaPlayer defender) {
		
		return false;
		
	}

	
	/**
	 * Called when the player gets shot by a creature.
	 * 
	 * @param event event
	 */
	public boolean onShotByCreature(EntityDamageByEntityEvent event, Creature creature, Projectile projectile) {
		
		return false;
		
	}
	
	/**
	 * Called when the player shoots a creature.
	 * 
	 * @param event event
	 */
	public boolean onShotCreature(EntityDamageByEntityEvent event, Creature creature, Projectile projectile) {
		
		return false;
		
	}

	/**
	 * Called when the player gets shot by a player.
	 * 
	 * @param event event
	 */
	public boolean onShotByPlayer(EntityDamageByEntityEvent event, SagaPlayer attacker, Projectile projectile) {
		
		return false;
		
	}
	
	/**
	 * Called when the player shoots a player.
	 * 
	 * @param event event
	 */
	public boolean onShotPlayer(EntityDamageByEntityEvent event, SagaPlayer defender, Projectile projectile) {
		
		return false;
		
	}
	

	/**
	 * Called when magic is casted by the player.
	 * 
	 * @param event event
	 */
	public boolean onSpelledPlayer(EntityDamageByEntityEvent event, SagaPlayer defender, Projectile projectile) {

		return false;
		
	}
	
	/**
	 * Called when magic is casted by the player.
	 * 
	 * @param event event
	 */
	public boolean onSpelledByPlayer(EntityDamageByEntityEvent event, SagaPlayer defender, Projectile projectile) {
		
		return false;
		
	}
	

	/**
	 * Called when the player interacts with an player.
	 * 
	 * @param event event
	 */
	public boolean onPlayerInteractPlayer(PlayerInteractEntityEvent event, SagaPlayer tragetPlayer) {

		return false;
		
	}
	
	/**
	 * Called when the player interacts with an creature.
	 * 
	 * @param event event
	 */
	public boolean onPlayerInteractCreature(PlayerInteractEntityEvent event, Creature targetCreature) {
		
		return false;
		
	}
	
	/**
	 * Called when a projectile fired by the player hits a target.
	 * 
	 * @param event event
	 */
	public boolean onProjectileHit(ProjectileHitEvent event) {
		
		return false;
		
	}

	/**
	 * Called when the player damages a block.
	 * 
	 * @param event event
	 */
	public boolean onBlockDamage(BlockDamageEvent event) {
		
		return false;
		
	}
	
	
	
}
