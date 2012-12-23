package org.saga.abilities;

import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.messages.effects.StatsEffectHandler;
import org.saga.player.SagaLiving;
import org.saga.player.SagaPlayer;

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
	public boolean triggerInteract(PlayerInteractEvent event) {
		

		// Shoot:
		SagaLiving<?> sagaLiving = getSagaLiving();
		sagaLiving.shootArrow(getDefinition().getFunction(SPEED_KEY).value(getScore()));

		if(getSagaLiving() instanceof SagaPlayer) StatsEffectHandler.playAnimateArm((SagaPlayer) getSagaLiving());
		
		return true;
		
		
	}
	
	
}
