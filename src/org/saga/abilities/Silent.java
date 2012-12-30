package org.saga.abilities;

import org.bukkit.entity.Creature;
import org.bukkit.event.entity.EntityTargetEvent;
import org.saga.config.VanillaConfiguration;

public class Silent extends Ability{

	
	/**
	 * Detection distance multiplier key.
	 */
	transient private static String DETECTION_DISTANCE_MULTIPLIER_KEY = "distance multiplier";

	
	
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Silent(AbilityDefinition definition) {
        super(definition);
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#useSilentPreTrigger()
	 */
	@Override
	public boolean useSilentPreTrigger() {
		return true;
	}
	
	
	
	// Usage:
	/* 
	 * Pretrigger.
	 * 
	 * @see org.saga.abilities.Ability#handleTargetedPreTrigger(org.bukkit.event.entity.EntityTargetEvent)
	 */
	@Override
	public boolean handleTargetedPreTrigger(EntityTargetEvent event) {
		return handlePreTrigger();
	}
	
	/* 
	 * Stop detection if too far.
	 * 
	 * @see org.saga.abilities.Ability#triggerTargeted(org.bukkit.event.entity.EntityTargetEvent)
	 */
	@Override
	public boolean triggerTargeted(EntityTargetEvent event) {
		
		
		// Only creatures:
		if(!(event.getEntity() instanceof Creature)) return false;
		
		Double maxDistance = VanillaConfiguration.getTargetDistance(event.getEntity()) * getDefinition().getFunction(DETECTION_DISTANCE_MULTIPLIER_KEY).value(getScore());
		Double maxDistanceSq = maxDistance*maxDistance;
		
		Double distanceSq = event.getEntity().getLocation().distanceSquared(event.getTarget().getLocation());

		if(distanceSq > maxDistanceSq) event.setCancelled(true);
		
		return true;
		
		
	}

	
}
