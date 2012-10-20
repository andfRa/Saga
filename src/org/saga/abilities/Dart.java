package org.saga.abilities;

import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.player.SagaLiving;

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

	/* 
	 * Trigger indication.
	 * 
	 * @see org.saga.abilities.Ability#hasAttackPreTrigger()
	 */
	@Override
	public boolean hasInteractPreTrigger() {
		return true;
	}
	

	
	// Usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#trigger()
	 */
	@Override
	public boolean triggerInteract(PlayerInteractEvent event) {
		

		// Shoot:
		SagaLiving<?> sagaLiving = getSagaLiving();
		sagaLiving.shootArrow(getDefinition().getFunction(SPEED_KEY).value(getScore()));

		return true;
		
		
	}
	
	
}
