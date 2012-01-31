package org.saga.abilities;

import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;

public class Fireball extends Ability{

	
	// Initialization:
	/**
	 * Initializes using definition.
	 * 
	 * @param definition ability definition
	 */
	public Fireball(AbilityDefinition definition) {
		
        super(definition);
	
	}

	
	// Usage:
	@Override
	public boolean instant(PlayerInteractEvent event) {
		

		// Check pre use:
		if(!handlePreUse()){
			return false;
		}
		
		SagaPlayer sagaPlayer = getSagaPlayer();
		
		sagaPlayer.shootFireball();

		// Award exp:
		Integer awardedExp = awardExperience();
		
		// Statistics:
		StatisticsManager.manager().onAbilityUse(getName(), awardedExp);

		// Effect:
		sagaPlayer.playeSpellEffect();
		
		return true;
		
		
	}
	
	
}
