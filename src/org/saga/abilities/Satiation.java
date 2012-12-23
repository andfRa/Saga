package org.saga.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.saga.utility.TwoPointFunction;

public class Satiation extends Ability{

	
	/**
	 * Food level gain multiplier key.
	 */
	transient private static String GAIN_MULTIPLIER_KEY = "gain multiplier";

	/**
	 * Food level loose multiplier key.
	 */
	transient private static String LOOSE_MULTIPLIER_KEY = "lose multiplier";

	
	
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Satiation(AbilityDefinition definition) {
        super(definition);
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#useSilentPreTrigger()
	 */
	@Override
	public boolean useSilentPreTrigger() {
		return true;
	}
	
	
	
	// Usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#triggerFoodLevelChange(org.bukkit.event.entity.FoodLevelChangeEvent)
	 */
	@Override
	public boolean triggerFoodLevelChange(FoodLevelChangeEvent event) {
		
		if(event.getEntity() instanceof Player){
			
			Player player = (Player) event.getEntity();
			
			int current = player.getFoodLevel();
			int change = event.getFoodLevel() - current;
			
			// Gain:
			if(change > 0){
				
				change = TwoPointFunction.randomRound(change * getDefinition().getFunction(GAIN_MULTIPLIER_KEY).value(getScore()));
				event.setFoodLevel(current + change);
				
			}else{
				
				change = TwoPointFunction.randomRound(change * getDefinition().getFunction(LOOSE_MULTIPLIER_KEY).value(getScore()));
				event.setFoodLevel(current + change);
				
			}
		
		}
		
		return false;
		
	}

	
}
