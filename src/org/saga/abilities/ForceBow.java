package org.saga.abilities;

import org.bukkit.entity.Creature;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.player.SagaPlayer;

public class ForceBow extends Ability{

	
	/**
	 * Speed key.
	 */
	private static String SPEED_KEY = "speed";
	
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public ForceBow(AbilityDefinition definition) {
		
        super(definition);
	
	}

	
	// Ability usage:
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
		SagaPlayer attackerPlayer = event.attackerPlayer;
		SagaPlayer defenderPlayer = event.defenderPlayer;
		Creature defenderCreature = event.defenderCreature;
		
		if(defenderPlayer != null){
			
			attackerPlayer.pushAwayEntity(defenderPlayer.getPlayer(), speed);
			
		}
		
		else if(defenderCreature != null){
			
			attackerPlayer.pushAwayEntity(defenderCreature, speed);
			
		}
		
		else{
			
			return false;
			
		}

		return true;
		
		
	}
	
	
}
