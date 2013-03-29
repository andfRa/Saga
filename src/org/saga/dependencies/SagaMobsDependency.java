package org.saga.dependencies;

import org.bukkit.entity.Creature;
import org.saga.SagaLogger;
import org.saga.mobs.SagaMobs;
import org.saga.player.SagaLiving;

public class SagaMobsDependency {

	/**
	 * Manager instance.
	 */
	private static SagaMobsDependency manager;
	
	/**
	 * Metadata key for saga mobs.
	 */
	public static final String SAGA_MOB_KEY = "saga mob";
	
	
	/**
	 * True if saga mobs is enabled
	 */
	private boolean enabled = false;
	
	
	
	// Initiate:
	/**
	 * Enables the manager.
	 * 
	 */
	public static void enable() {
		
		manager = new SagaMobsDependency();
		
	}
	
	/**
	 * Disables the manager.
	 * 
	 */
	public static void disable() {
		
		manager.enabled = false;
		manager = null;
		
	}
	
	/**
	 * Registers the dependency.
	 * 
	 * @param sagaMobs saga mobs
	 */
	public static void register(SagaMobs sagaMobs) {
		
		manager.enabled = true;
		
		SagaLogger.info("Saga mobs registered.");
		
	}
	

	
	// Mobs:
	/**
	 * Finds the Saga living associated with the creature.
	 * 
	 * @param creature creature
	 * @return Saga living, null if none
	 */
	public static SagaLiving findSagaCreature(Creature creature) {
		
		if(!manager.enabled) return null;
		if(creature.getMetadata(SAGA_MOB_KEY).size() == 0) return null;
		
		return org.saga.mobs.SagaMobs.findSagaLiving(creature.getUniqueId());
		
	}
	

}
