package org.saga.abilities;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.player.SagaPlayer;

public class Ignite extends Ability{

	
	/**
	 * Duration key.
	 */
	private static String DURATION_KEY = "fireticks";
	
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
		SagaPlayer defenderPlayer = event.getDefenderPlayer();
		Creature defenderCreature = event.getDefenderCreature();
		
		if(defenderPlayer != null){
			
			Player player = defenderPlayer.getPlayer();
			
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
