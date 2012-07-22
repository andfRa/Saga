package org.saga.abilities;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.saga.messages.AbilityEffects;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;

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
		
		
//		SagaPlayer sagaPlayer = getSagaPlayer();
//		
//		Vector shootDirection = event.getPlayer().getEyeLocation().getDirection().normalize();
//		Location shootLocation1 = event.getPlayer().getEyeLocation().add(shootDirection.multiply(0.75)).add(new Vector(0, -1, 0));
//		Location shootLocation2 = event.getPlayer().getEyeLocation().add(shootDirection.multiply(2.75));
//		
//		sagaPlayer.shootFireball(1.0, shootLocation1);
//		sagaPlayer.shootFireball(1.5, shootLocation2);
		
		
		SagaPlayer sagaPlayer = getSagaPlayer();

		
		Vector shootDirection = event.getPlayer().getEyeLocation().getDirection().normalize();
//		Vector pendicularDirection = new Vector(- shootDirection.getZ(), 0, shootDirection.getX()).normalize();
		
//		Location shootLocation1 = event.getPlayer().getEyeLocation().add(shootDirection).add(pendicularDirection.multiply( 0.75)).add(new Vector(0, -0.75, 0));
		Location shootLocation1 = event.getPlayer().getEyeLocation().add(shootDirection);

		sagaPlayer.shootFireball(getDefinition().getFunction(SPEED_KEY).value(getModifiedScore()), shootLocation1);

		// Statistics:
		StatisticsManager.manager().onAbilityUse(getName(), 0.0);

		// Effect:
		AbilityEffects.playSpellCast(sagaPlayer);
		
		return true;
		
		
	}
	
	
}
