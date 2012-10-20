package org.saga.abilities;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.saga.messages.effects.StatsEffectHandler;
import org.saga.player.SagaLiving;
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
	@Override
	public boolean triggerInteract(PlayerInteractEvent event) {
		
		
		SagaLiving<?> sagaLiving = getSagaLiving();
		
		Vector shootDirection = event.getPlayer().getEyeLocation().getDirection().normalize();

		Location shootLocation = event.getPlayer().getEyeLocation().add(shootDirection);

		sagaLiving.shootFireball(getDefinition().getFunction(SPEED_KEY).value(getScore()), shootLocation);

		// Effect:
		StatsEffectHandler.playSpellCast(sagaLiving);
		
		if(getSagaLiving() instanceof SagaPlayer) StatsEffectHandler.playAnimateArm((SagaPlayer) getSagaLiving());
		
		return true;
		
		
	}
	
}
