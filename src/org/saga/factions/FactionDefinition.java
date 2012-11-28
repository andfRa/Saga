package org.saga.factions;

import org.saga.SagaLogger;
import org.saga.utility.TwoPointFunction;

public class FactionDefinition {


	/**
	 * Experience requirement.
	 */
	private TwoPointFunction levelUpExp;

	/**
	 * Experience speed.
	 */
	private TwoPointFunction expSpeed;
	
	
	
	// Initialisation:
	/**
	 * Used by gson.
	 * 
	 */
	private FactionDefinition() {
	}

	/**
	 * Fixes all problematic fields.
	 * 
	 */
	public void complete() {
		

		if(expSpeed == null){
			expSpeed = new TwoPointFunction(0.0);
			SagaLogger.nullField(getClass(), "expSpeed");
		}
		expSpeed.complete();
		
		if(levelUpExp == null){
			levelUpExp = new TwoPointFunction(10000.0);
			SagaLogger.nullField(getClass(), "levelUpExp");
		}
		levelUpExp.complete();

		
	}
	
	

	// Experience:
	/**
	 * Gets experience speed.
	 * 
	 * @param claimed claimed settlements
	 * @return speed
	 */
	public Double getExpSpeed(Integer claimed) {
		
		return expSpeed.value(claimed);

	}
	
	/**
	 * Gets experience requirement.
	 * 
	 * @param level faction level
	 * @return experience requirement
	 */
	public Double getLevelUpExp(Integer level) {
		
		return levelUpExp.value(level);

	}
	
	/**
	 * Gets maximum level.
	 * 
	 * @return maximum level
	 */
	public Integer getMaxLevel() {

		return levelUpExp.getXMax().intValue();

	}
	
	
	
	// Other:
	/**
	 * Returns the default definition.
	 * 
	 * @return default definition
	 */
	public static FactionDefinition defaultDefinition(){
		
		
		FactionDefinition definition = new FactionDefinition();
		return definition;
		
		
	}
	
	
}
