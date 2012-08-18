package org.saga.abilities;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.saga.messages.effects.AbilityEffects;
import org.saga.player.SagaPlayer;

public class Fireball extends Ability{

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
	public Fireball(AbilityDefinition definition) {
		
        super(definition);
	
	}

	
	// Usage:
	@Override
	public boolean trigger(PlayerInteractEvent event) {
		
		
		SagaPlayer sagaPlayer = getSagaPlayer();
		
		Vector shootDirection = event.getPlayer().getEyeLocation().getDirection().normalize();

		Location shootLocation = event.getPlayer().getEyeLocation().add(shootDirection);

		sagaPlayer.shootFireball(getDefinition().getFunction(SPEED_KEY).value(getScore()), shootLocation);

		// Effect:
		AbilityEffects.playSpellCast(sagaPlayer);
		
		return true;
		
		
	}
	
	
}
