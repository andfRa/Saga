package org.saga.abilities;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.player.SagaLiving;

public class Ignite extends Ability{
	
	/**
	 * Duration key.
	 */
	private static String DURATION_KEY = "fireticks";
	
	
	
	// Initialisation:
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Ignite(AbilityDefinition definition) {
		
        super(definition);
	
	}
	
	
	
	// Ability usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#handleInteractPreTrigger(org.bukkit.event.player.PlayerInteractEvent)
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

		
		// Only fireball:
		if(!(event.getProjectile() instanceof org.bukkit.entity.Fireball)) return false;
		
		// Duration:
		Integer duration = getDefinition().getFunction(DURATION_KEY).randomIntValue(getScore());
		if(duration < 1) return false;
		
		// Ignite:
		SagaLiving defenderPlayer = event.sagaDefender;
		Creature defenderCreature = event.creatureDefender;
		
		if(defenderPlayer != null){
			
			LivingEntity player = defenderPlayer.getWrapped();
			
			player.setFireTicks(player.getFireTicks() + duration);
			
		}
		
		else if(defenderCreature != null){
			
			defenderCreature.setFireTicks(defenderCreature.getFireTicks() + duration);
			
		}
		
		else{
			
			return false;
			
		}
		
		return true;
		
		
	}
	
	
}
