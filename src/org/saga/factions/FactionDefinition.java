package org.saga.factions;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

import org.saga.SagaLogger;
import org.saga.utility.TwoPointFunction;

public class FactionDefinition {


	/**
	 * Rank assigned to joined members.
	 */
	private String defaultRank;

	/**
	 * Rank assigned to faction owner.
	 */
	private String ownerRank;
	
	
	/**
	 * Hierarchy.
	 */
	private Hashtable<Integer, TwoPointFunction> hierarchy;

	/**
	 * Hierarchy level names.
	 */
	private Hashtable<Integer, String> hierarchyNames;
	

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
		

		if(hierarchy == null){
			hierarchy = new Hashtable<Integer, TwoPointFunction>();
			SagaLogger.nullField(getClass(), "hierarchy");
		}
		Collection<TwoPointFunction> roleAmounts = hierarchy.values();
		for (TwoPointFunction function : roleAmounts) {
			function.complete();
		}
		
		if(hierarchyNames == null){
			hierarchyNames = new Hashtable<Integer, String>();
			SagaLogger.nullField(getClass(), "hierarchyNames");
		}
		
		if(ownerRank == null){
			SagaLogger.nullField(getClass(), "ownerRank");
			ownerRank = "novice";
		}
		
		if(defaultRank == null){
			SagaLogger.nullField(getClass(), "defaultRank");
			defaultRank = "warmaster";
		}
		
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
	
	
	// Ranks:
	/**
	 * Gets the total ranks available.
	 * 
	 * @param factLevel faction level
	 * @param hierarchyLevel hierarchy level
	 * @return amount of ranks available
	 */
	public Integer getAvailableRanks2(Integer factLevel, Integer hierarchyLevel) {
		
		
		TwoPointFunction function = hierarchy.get(hierarchyLevel);
		if(function == null) return 0;
		
		return function.intValue(factLevel);
		
		
	}

	/**
	 * Gets hierarchy level name.
	 * 
	 * @param hierarchy hierarchy level
	 * @return hierarchy level name, null if none
	 */
	public String getHierarchyName(Integer hierarchy) {
		
		String roleName = hierarchyNames.get(hierarchy);
		if(roleName == null) return null;
		
		return roleName;
		
	}
	
	/**
	 * Gets max hierarchy level.
	 * 
	 * @return max hierarchy level
	 */
	public Integer getHierarchyMax() {

		
		Integer maxHierarchy = 0;
		Set<Integer> roleHierarchy = hierarchy.keySet();
		
		for (Integer hierarchy : roleHierarchy) {
			if(hierarchy > maxHierarchy) maxHierarchy = hierarchy;
		}
		
		return maxHierarchy;
		
		
	}

	/**
	 * Gets min hierarchy level.
	 * 
	 * @return min hierarchy level
	 */
	public Integer getHierarchyMin() {

		
		Integer minHierarchy = -1;
		Set<Integer> roleHierarchy = hierarchy.keySet();
		
		for (Integer hierarchy : roleHierarchy) {
			if(hierarchy < minHierarchy || minHierarchy == -1) minHierarchy = hierarchy;
		}
		
		return minHierarchy;
		
		
	}
	
	
	/**
	 * Gets the default rank.
	 * 
	 * @return default rank
	 */
	public String getDefaultRank() {
		return defaultRank;
	}
	
	/**
	 * Gets owner rank.
	 * 
	 * @return owner rank
	 */
	public String getOwnerRank() {
		return ownerRank;
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
