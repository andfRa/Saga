package org.saga.abilities;

import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.TwoPointFunction;

public class Dart extends Ability{

	
	// Initialization:
	/**
	 * Initializes using definition.
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
	public boolean instant(PlayerInteractEvent event) {
		

		// Check pre use:
		if(!handlePreUse()){
			return false;
		}

		SagaPlayer sagaPlayer = getSagaPlayer();
		TwoPointFunction primaryFunction = getDefinition().getPrimaryFunction();
		Integer skillLevel = getSkillLevel();
		
		// Shoot:
		sagaPlayer.shootArrow(primaryFunction.calculateValue(skillLevel));

		// Award exp:
		Integer awardedExp = awardExperience();
		
		// Statistics:
		StatisticsManager.manager().onAbilityUse(getName(), awardedExp);
		
		return true;
		
		
	}
	
	
}
