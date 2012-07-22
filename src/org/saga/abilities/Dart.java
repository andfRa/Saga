package org.saga.abilities;

import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;

public class Dart extends Ability{

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
	public Dart(AbilityDefinition definition) {
		
        super(definition);
	
	}

	
	// Usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#trigger()
	 */
	@Override
	public boolean trigger(PlayerInteractEvent event) {
		

		// Shoot:
		SagaPlayer sagaPlayer = getSagaPlayer();
		sagaPlayer.shootArrow(getDefinition().getFunction(SPEED_KEY).value(getModifiedScore()));

		// Statistics:
		StatisticsManager.manager().onAbilityUse(getName(), 0.0);
		
		return true;
		
		
	}
	
	
}
