package org.saga.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.saga.SagaLogger;
import org.saga.abilities.Ability;
import org.saga.abilities.AbilityManager;
import org.saga.attributes.AttributeManager;
import org.saga.chunks.Bundle;
import org.saga.chunks.BundleManager;
import org.saga.chunks.SagaChunk;
import org.saga.config.AbilityConfiguration;
import org.saga.exceptions.InvalidAbilityException;
import org.saga.shape.RelativeShape.Orientation;

public class SagaLiving <T extends LivingEntity>{

	
	// Living entity:
	transient protected T livingEntity = null;
	
	
	// Positioning:
	/**
	 * Last chunk the entity was on.
	 */
	transient public SagaChunk lastSagaChunk = null;
	

	// Attributes:
	/**
	 * Attribute scores.
	 */
	private Hashtable<String, Integer> attributeScores;
	
	
	// Abilities:
	/**
	 * All abilities.
	 */
	private ArrayList<Ability> abilities;
	
	
	// Managers:
	/**
	 * Ability manager.
	 */
	transient protected AbilityManager abilityManager;
	
	/**
	 * Attribute manager.
	 */
	transient protected AttributeManager attributeManager;
	

	
	// Initialisation:
	/**
	 * Used by gson.
	 * 
	 */
	protected SagaLiving() {
	}
	
	/**
	 * Creates a Saga living entity.
	 * 
	 */
	public SagaLiving(String name) {

		this.abilities = new ArrayList<Ability>();
		this.attributeScores = new Hashtable<String, Integer>();
		this.abilityManager = new AbilityManager(this);
		this.attributeManager = new AttributeManager(this);
	
		syncAbilities();
		
	}
	
	/**
	 * Fixes all problematic fields.
	 * 
	 */
	protected void complete() {


		// Abilities:
		if(abilities == null){
			abilities = new ArrayList<Ability>();
			SagaLogger.nullField(this, "abilities");
		}
		
		for (int i = 0; i < abilities.size(); i++) {
			
			Ability ability = abilities.get(i);
			
			if(ability == null){
				SagaLogger.nullField(this, "abilities element");
				abilities.remove(i);
				i--;
				continue;
			}
			
			try {
				
				ability.setSagaLiving(this);
				ability.complete();
				
			} catch (InvalidAbilityException e) {
				SagaLogger.severe(this, "abilities element invalid: " + e.getMessage());
				abilities.remove(i);
				i--;
				continue;
			}
			
		}

		if(attributeScores == null){
			attributeScores = new Hashtable<String, Integer>();
			SagaLogger.nullField(this, "attributeScores");
		}
		
		this.abilityManager = new AbilityManager(this);
		this.attributeManager = new AttributeManager(this);
		
		syncAbilities();
		
		
	}
	
	
	
	// Entity:
	/**
	 * Sets the living entity.
	 * 
	 * @param livingEntity living entity
	 */
	public void setLivingEntity(T livingEntity) {
		
		this.livingEntity = livingEntity;
		
	}
	
	/**
	 * Sets the living entity.
	 * 
	 */
	public void removeLivingEntity() {

		this.livingEntity = null;
		
	}
	
	/**
	 * Gets the living entity.
	 * 
	 * @return living entity, null if not wrapped
	 */
	public T getLivingEntity() {
		return livingEntity;
	}
	
	

	// Managers:
	/**
	 * Gets the ability manager.
	 * 
	 * @return ability manager
	 */
	public AbilityManager getAbilityManager() {
		return abilityManager;
	}
	
	/**
	 * Gets the attribute manager.
	 * 
	 * @return attribute manager
	 */
	public AttributeManager getAttributeManager() {
		return attributeManager;
	}
	
	
	/**
	 * Updates everything.
	 * 
	 */
	public void update() {
		abilityManager.update();
	}
	
	
	
	// Attributes:
	/**
	 * Gets the score for the given attribute.
	 * 
	 * @param attrName attribute name
	 * @return attribute score
	 */
	public Integer getRawAttributeScore(String attrName) {

		Integer score = attributeScores.get(attrName);
		if(score == null) return 0;
		return score;

	}
	
	/**
	 * Gets the score for the given attribute. Includes bonuses.
	 * 
	 * @param attrName attribute name
	 * @return attribute score
	 */
	public Integer getAttributeScore(String attrName) {

		
		Integer score = attributeScores.get(attrName);
		if(score == null) score = 0;
		
		return score + getAttrScoreBonus(attrName);

		
	}

	/**
	 * Gets the bonus for the given attribute.
	 * 
	 * @param name attribute name
	 * @return attribute bonus
	 */
	public Integer getAttrScoreBonus(String attrName) {

		Integer bonus = 0;
		
		return bonus;
		
	}

	/**
	 * Sets attribute score.
	 * 
	 * @param attribute attribute name
	 * @param score score
	 */
	public void setAttributeScore(String attribute, Integer score) {
		
		this.attributeScores.put(attribute, score);
		abilityManager.update();
		
	}

	
	/**
	 * Gets the used attribute points.
	 * 
	 * @return total attribute points
	 */
	public Integer getUsedAttributePoints() {

		
		Collection<Integer> attrVals = attributeScores.values();
		Integer total = 0;
		for (Integer score : attrVals) {
			total += score;
		}
		
		return total;
				
		
	}

	
	
	// Abilities:
	/**
	 * Gets an ability with the given name.
	 * 
	 * @param name ability name
	 * @return ability, null if none
	 */
	public Ability getAbility(String name) {

		
		HashSet<Ability> abilities = getAbilities();
		
		for (Ability ability : abilities) {
			if(ability.getName().equals(name)) return ability;
		}
		
		return null;
		
		
	}
	
	/**
	 * Gets the score for the giver ability. Includes bonuses.
	 * 
	 * @param name ability name
	 * @return ability score
	 */
	public Integer getAbilityScore(String name) {

		
		Ability ability = getAbility(name);
		if(ability == null) return 0;
		
		return ability.getDefinition().getScore(this);

		
	}
	
	/**
	 * Checks if the entity has an ability.
	 * 
	 * @param name ability name
	 * @return true if the entity has the ability
	 */
	public boolean hasAbility(String name) {
		return getAbility(name) != null;	
	}
	
	/**
	 * Gets abilities.
	 * 
	 * @return abilities
	 */
	public HashSet<Ability> getAbilities() {
		return new HashSet<Ability>(abilities);
	}

	/**
	 * Creates and adds all missing abilities.
	 * 
	 */
	private void syncAbilities() {

		
		ArrayList<String> abilityNames = AbilityConfiguration.config().getAbilityNames();
		for (String abilityName : abilityNames) {
			if(!hasAbility(abilityName)){
				
				try {
					Ability ability = AbilityConfiguration.createAbility(abilityName);
					ability.setSagaLiving(this);
					abilities.add(ability);
				}
				catch (InvalidAbilityException e) {
					SagaLogger.severe(this, "failed to create ability: " + e.getClass().getSimpleName() + ":" + e.getMessage());
				}
				
			}
		}
		

	}


	
	// Ability usage:
	/**
	 * Shoots a fireball.
	 * 
	 * @param accuracy accuracy. Can be in the range 0-10
	 */
	public void shootFireball(Double speed) {

		
		// Ignore if the living entity isn't online:
		if(livingEntity == null) return;
		
		// Shooter:
		Location shootLocation = livingEntity.getEyeLocation();

		// Direction vector:
		Vector directionVector = shootLocation.getDirection().normalize();
		
		// Shoot shift vector:
		double startShift = 2;
		Vector shootShiftVector = new Vector(directionVector.getX() * startShift, directionVector.getY() * startShift, directionVector.getZ() * startShift);
		
		// Shift shoot location:
		shootLocation = shootLocation.add(shootShiftVector.getX(), shootShiftVector.getY(), shootShiftVector.getZ());
		
		// Create the fireball:
		Fireball fireballl = shootLocation.getWorld().spawn(shootLocation, Fireball.class);
		fireballl.setVelocity(directionVector.multiply(speed));
		
		// Remove fire:
		if(fireballl instanceof Fireball){
			((Fireball) fireballl).setIsIncendiary(false);
		}
		
	}
	
	/**
	 * Shoots a fireball.
	 * 
	 * @param speed speed
	 * @param shootLocation shoot location
	 */
	public void shootFireball(Double speed, Location shootLocation) {

		
		// Ignore if no entity is wrapped:
		if(livingEntity == null) return;
		
		// Direction vector:
		Vector directionVector = shootLocation.getDirection().normalize();
		
		// Create the fireball:
		Fireball fireball = shootLocation.getWorld().spawn(shootLocation, Fireball.class);
		fireball.setVelocity(directionVector.multiply(speed));
		
		// Set shooter:
		fireball.setShooter(livingEntity);
		
		// Remove fire:
		if(fireball instanceof Fireball){
			((Fireball) fireball).setIsIncendiary(false);
		}
		
	}
	
	/**
	 * Shoots an arrow.
	 * 
	 * @param speed arrow speed
	 */
	public void shootArrow(double speed) {

		
		// Ignore if no entity is wrapped:
		if(livingEntity == null) return;
		
		Arrow arrow = livingEntity.launchProjectile(Arrow.class);
		
		// Velocity vector:
		Vector velocity = arrow.getVelocity().clone();
		velocity.normalize().multiply(speed);
		
		// Set velocity:
		arrow.setVelocity(velocity);
		
		// Play effect:
		livingEntity.getLocation().getWorld().playEffect(getLocation(), Effect.BOW_FIRE, 0);
		
		
	}

	/**
	 * Pushes away an entity from the entity.
	 * 
	 * @param entity entity
	 * @param speed speed
	 */
	public void pushAwayEntity(Entity entity, double speed) {

		
		// Ignore if no entity is wrapped:
		if(livingEntity == null) return;
		
		// Get velocity unit vector:
		Vector unitVector = entity.getLocation().toVector().subtract(getLocation().toVector()).normalize();
		
		// Set speed and push entity:
		entity.setVelocity(unitVector.multiply(speed));
		
		
	}


	
	// Positioning:
	/**
	 * Gets the entity location.
	 * 
	 * @return entity location, null if not wrapped
	 */
	public Location getLocation() {

		if(livingEntity == null) return null;

		return livingEntity.getLocation();
		
	}

	/**
	 * Moves the entity to the given location.
	 * Must be used when the teleport is part of an ability.
	 * 
	 * @param location location
	 */
	public void teleport(Location location) {

		if(livingEntity != null) livingEntity.teleport(location);
		
	}
	
	/**
	 * Puts a entity on the given blocks centre.
	 * 
	 * @param locationBlock block the entity will be placed on
	 */
	public void teleportCentered(Block locationBlock) {
		
		Location location = locationBlock.getRelative(BlockFace.UP).getLocation();
		
		teleport(location.add(0.5, 0, 0.5));
		
	}

	/**
	 * Gets entity orientation.
	 * 
	 * @return orientation, {@link Orientation#NORTH} if not online
	 */
	public Orientation getOrientation(){
		
		
		if(livingEntity == null) return Orientation.NORTH;
		
		Location entityLocation = livingEntity.getEyeLocation();
		double yaw = entityLocation.getYaw();
		
		if( (yaw >= 315.0 && yaw <= 360) || (yaw >= 0 && yaw <= 45.0) || (yaw <= 0 && yaw >= -45.0) || (yaw <= -315 && yaw >= -360.0) ){
			
			return Orientation.WEST;
			
		}
		if( (yaw >= 45.0 && yaw <= 135.0) || (yaw >= -315.0 && yaw <= -225.0) ){
			
			return Orientation.NORTH;
			
		}
		if( (yaw >= 135.0 && yaw <= 225.0) || (yaw >= -225.0 && yaw <= -135.0) ){
			
			return Orientation.EAST;
			
		}if( (yaw >= 225.0 && yaw <= 315.0) || (yaw >= -135.0 && yaw <= -45.0) ){
			
			return Orientation.SOUTH;
			
		}
		
		return Orientation.NORTH;

		
	}	
	
	
	
	// Saga chunk:
	/**
	 * Gets the Saga chunk the entity is in.
	 * 
	 * @return Saga chunk, null if not found
	 */
	public SagaChunk getSagaChunk(){
		
		if(livingEntity == null) return null;
		
		Location location = getLocation();
		
		if(lastSagaChunk != null && lastSagaChunk.represents(location)){
			return lastSagaChunk;
		}
		
		return BundleManager.manager().getSagaChunk(location);
				
	}
	
	
	
	// Effects:
	/**
	 * Plays an effect.
	 * 
	 * @param effect effect
	 * @param value effect value
	 */
	public void playGlobalEffect(Effect effect, int value) {
		
		if(livingEntity == null) return;
		
		livingEntity.getLocation().getWorld().playEffect(getLocation(), effect, value);
		
	}
	
	/**
	 * Plays an effect.
	 * 
	 * @param effect effect
	 * @param value effect value
	 * @param location location
	 */
	public void playGlobalEffect(Effect effect, int value, Location location) {

		location.getWorld().playEffect(location, effect, value);
		
	}
	

	/**
	 * Plays a sound.
	 * 
	 * @param sound sound
	 * @param volume volume
	 * @param pitch pitch
	 */
	public void playGlobalSound(Sound sound, float volume, float pitch) {

		if(livingEntity == null) return;
		getLocation().getWorld().playSound(getLocation(), sound, volume, pitch);
		
	}
	

	
	// Entities:
	/**
	 * Returns the entity distance to a location.
	 * 
	 * @param location location
	 * @return distance, 0 if entity not online
	 */
	public Double getDistance(Location location) {
		
		if(livingEntity == null) return 0.0;
		
		return getLocation().distance(location);
		
	}
	
	/**
	 * Gets nearby entities.
	 * 
	 * @param x x radius
	 * @param y y radius
	 * @param z z radius
	 * @return nearby entities
	 */
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		
		if(livingEntity == null) return new ArrayList<Entity>();
		
		return livingEntity.getNearbyEntities(x, y, z);
		
	}

	

	// Messages:
	/**
	 * Sends the entity a message.
	 * 
	 * @param message message
	 */
	public void message(String message) {
		return;
	}
	
	
	
	// Bundles:
	/**
	 * Gets living entities chunk bundle.
	 * 
	 * @return the registered chunk bundle, null if none
	 */
	public Bundle getBundle() {
		return null;
	}

	
	
	// Items:
	/**
	 * Damages living entities tool if possible.
	 * 
	 */
	public void damageTool() {

		return;
		
	}
	
}
