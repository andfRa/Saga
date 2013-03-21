package org.saga.abilities;

import org.bukkit.entity.Creature;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.player.SagaLiving;

public class ForceBow extends Ability{

	/**
	 * Speed key.
	 */
	private static String SPEED_KEY = "speed";
	
	
	
	// Initialisation:
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public ForceBow(AbilityDefinition definition) {
		
        super(definition);
	
	}
	

	
	// Usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#handleAttackPreTrigger(org.saga.listeners.events.SagaEntityDamageEvent)
	 */
	@Override
	public boolean handleAttackPreTrigger(SagaEntityDamageEvent event) {
		return handlePreTrigger();
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#onPlayerInteractPlayer(org.bukkit.event.player.PlayerInteractEntityEvent, org.saga.player.SagaPlayer)
	 */
	@Override
	public boolean triggerAttack(SagaEntityDamageEvent event) {

		
		// Only arrow:
		if(!(event.getProjectile() instanceof org.bukkit.entity.Arrow)) return false;
		
		// Speed:
		Integer speed = getDefinition().getFunction(SPEED_KEY).randomIntValue(getScore());
		if(speed < 0) return false;
		
		// Force:
		SagaLiving sagaAttacker = event.sagaAttacker;
		SagaLiving sagaDefender = event.sagaDefender;
		Creature defenderCreature = event.creatureDefender;
		
		if(sagaDefender != null){
			
			sagaAttacker.pushAwayEntity(sagaDefender.getWrapped(), speed);
			
		}
		
		else if(defenderCreature != null){
			
			sagaAttacker.pushAwayEntity(defenderCreature, speed);
			
		}
		
		else{
			
			return false;
			
		}

		return true;
		
		
	}
	
}
