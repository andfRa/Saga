package org.saga.player;

import java.util.ArrayList;

import org.saga.abilities.Ability;
import org.saga.abilities.AbilityDefinition;
import org.saga.config.AbilityConfiguration;

public class StatsManager {

	/** Attribute modifiers. */
	public double[] modifiers;
	
	/** Available abilities. */
	public Ability[] abilities;
	
	
	public StatsManager(SagaLiving sagaLiving) {

		
	}
	
	private void initAbilities(SagaLiving sagaLiving) {
		
		
		
		ArrayList<AbilityDefinition> definitions = AbilityConfiguration.config().getDefinitions();
		ArrayList<Ability> abilities = new ArrayList<Ability>();
		
		for (AbilityDefinition definition : definitions) {
		
			int curScore = sagaLiving.getRawAbilityScore(definition.getName());
			if(curScore == 0) continue;
			int maxScore = definition.findScore(sagaLiving);
			
			int score = curScore > maxScore ? maxScore : curScore;
			if(score <= 0) continue;
			
			Ability ability = sagaLiving.getAbility(definition.getName());
			
			
			
		}
		
		
		
	
		
	}
	
	
}
