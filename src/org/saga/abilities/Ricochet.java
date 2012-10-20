package org.saga.abilities;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.saga.Saga;
import org.saga.listeners.events.SagaEntityDamageEvent;

public class Ricochet extends Ability{

	
	/**
	 * Disarm chance key.
	 */
	transient private static String RANGE_KEY = "range";
	

	/**
	 * Ricochet metadata name.
	 */
	transient private static String RICOCHET_METADATA_NAME = "ricochet";
	
	/**
	 * Ricochet metadata.
	 */
	transient private static MetadataValue RICOCHET_METADATA = new MetadataValue() {
		@Override
		public Object value() {
			return "";
		}
		@Override
		public void invalidate() {
		}
		@Override
		public Plugin getOwningPlugin() {
			return Saga.plugin();
		}
		@Override
		public String asString() {
			return "";
		}
		@Override
		public short asShort() {
			return 0;
		}
		@Override
		public long asLong() {
			return 0;
		}
		@Override
		public int asInt() {
			return 0;
		}
		@Override
		public float asFloat() {
			return 0;
		}
		@Override
		public double asDouble() {
			return 0;
		}
		@Override
		public byte asByte() {
			return 0;
		}
		@Override
		public boolean asBoolean() {
			return false;
		}
	};

	
	/**
	 * Target.
	 */
	transient private LivingEntity target = null;
	
	/**
	 * Arrow:
	 */
	transient private Arrow arrow = null;
	
	
	
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Ricochet(AbilityDefinition definition) {
		
        super(definition);
	
	}

	
	// Ability usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#onPlayerInteractPlayer(org.bukkit.event.player.PlayerInteractEntityEvent, org.saga.player.SagaPlayer)
	 */
	@Override
	public boolean triggerProjectileHit(ProjectileHitEvent event) {

			
		// Only arrows:
		if(!(event.getEntity() instanceof Arrow)){
			System.out.println("not arrow");
			return false;
		}
		
		Arrow arrow = (Arrow) event.getEntity();
    	Location loc = arrow.getLocation();
    	final LivingEntity shooter = arrow.getShooter();
    	
		// Ricochet arrows don't ricochet:
		if(arrow.getMetadata(RICOCHET_METADATA_NAME).size() > 0){
			return false;
		}
		
    	// Get closest:
    	Double minDistanceSq = Double.MAX_VALUE;
    	double radius = getDefinition().getFunction(RANGE_KEY).value(getScore());
    	double radiusSq = radius*radius;
    	LivingEntity target = null;
    	List<Entity> nearby = arrow.getNearbyEntities(radius, radius, radius);
    	for (Entity entity : nearby) {
			
    		if(!(entity instanceof LivingEntity)) continue;
    		
    		// Ignore shooter:
    		if(entity == shooter) continue;
    		
    		// Inside radius:
    		double distanceSq = entity.getLocation().distanceSquared(loc);
    		if(distanceSq > radiusSq) continue;
    		
    		// Closest:
    		if(distanceSq < minDistanceSq){
    			minDistanceSq = distanceSq;
    			target = (LivingEntity) entity;
    		}
    		
		}
    	
    	// Shoot arrow towards closest:
    	if(target == null) return false;

    	this.target = target;
    	this.arrow = arrow;
    	
    	// Schedule ricochet:
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Saga.plugin(), new Runnable() {
			@Override
			public void run() {
					handleRicochet();
				}
			}, 1);
	
		return false;
		
		
	}
	
	@Override
	public boolean triggerAttack(SagaEntityDamageEvent event) {
		
		
		// Successful hit doesn't need ricochet:
		if(event.getProjectile() == arrow){
			target = null;
			arrow = null;
		}
		
		return false;
		
		
	}
	
	/**
	 * Handles ricochet.
	 * 
	 */
	private void handleRicochet() {

		
		if(target == null || arrow == null){
			return;
		}
	
		Location origLoc = arrow.getLocation();
		origLoc.add(0, 0.5, 0);
		Float speed = (float)arrow.getVelocity().length();
		
		LivingEntity shooter = getSagaLiving().getLivingEntity();
		Vector velocity = target.getLocation().subtract(origLoc).toVector().normalize();
		
		Arrow newArrow = origLoc.getWorld().spawnArrow(origLoc, velocity, speed, 0);
    	newArrow.setShooter(shooter);
    	newArrow.setMetadata(RICOCHET_METADATA_NAME, RICOCHET_METADATA);
		
    	// Handle after trigger:
		handleAfterTrigger();
		
		// Remove arrow variables:
		arrow.remove();
		target = null;
		arrow = null;
		origLoc = null;
		speed = null;
		
		
	}
	
	
}
