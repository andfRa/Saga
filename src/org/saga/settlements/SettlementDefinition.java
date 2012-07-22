package org.saga.settlements;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

import org.saga.SagaLogger;
import org.saga.utility.TwoPointFunction;

public class SettlementDefinition{

	
	/**
	 * Active players.
	 */
	private TwoPointFunction activePlayers;

	
	/**
	 * The amount of building points.
	 */
	private TwoPointFunction buildPoints;

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
	
	
	/**
	 * Settlement claims.
	 */
	private TwoPointFunction claims;

	/**
	 * Role assigned to settlement owner.
	 */
	public String ownerRole;
	
	/**
	 * Role assigned to joined members.
	 */
	public String defaultRole;

	

	// Initialisation:
	/**
	 * Used by gson.
	 * 
	 */
	private SettlementDefinition() {
		
	}

	/**
	 * Completes.
	 * 
	 * @return integrity
	 */
	public boolean complete() {
		

		boolean integrity=true;
		
		if(activePlayers == null){
			SagaLogger.nullField(this, "activePlayers");
			activePlayers = new TwoPointFunction(0.0);
			integrity = false;
		}
		
		if(buildPoints == null){
			SagaLogger.nullField(this, "buildPoints");
			buildPoints = new TwoPointFunction(0.0);
			integrity=false;
		}
		
		if(hierarchy == null){
			hierarchy = new Hashtable<Integer, TwoPointFunction>();
			SagaLogger.nullField(this, "hierarchy");
			integrity = false;
		}
		Collection<TwoPointFunction> roleAmounts = hierarchy.values();
		for (TwoPointFunction function : roleAmounts) {
			function.complete();
		}
		
		if(hierarchyNames == null){
			hierarchyNames = new Hashtable<Integer, String>();
			SagaLogger.nullField(this, "hierarchyNames");
			integrity = false;
		}
		
		if(levelUpExp == null){
			levelUpExp = new TwoPointFunction(10000.0);
			SagaLogger.nullField(this, "levelUpExp");
			integrity = false;
		}
		integrity = integrity && levelUpExp.complete();
		
		if(expSpeed == null){
			expSpeed = new TwoPointFunction(0.0);
			SagaLogger.nullField(this, "expSpeed");
			integrity = false;
		}
		integrity = integrity && expSpeed.complete();
		
		if(claims == null){
			SagaLogger.nullField(this, "claims");
			claims = new TwoPointFunction(1.0);
			integrity=false;
		}
		integrity = claims.complete() && integrity;
		
		if(ownerRole == null){
			SagaLogger.nullField(this,"ownerRole");
			ownerRole = "";
			integrity=false;
		}
		
		if(defaultRole == null){
			SagaLogger.nullField(this,"defaultRole");
			defaultRole = "";
			integrity=false;
		}
		
		return integrity;
		
		
	}
	

	
	// Requirements:
	/**
	 * Gets the activePlayers.
	 * 
	 * @param level level
	 * @return the activePlayers
	 */
	public Integer getActivePlayers(Integer level) {
		return activePlayers.value(level).intValue();
	}

	
	// Building points:
	/**
	 * Get building points.
	 * 
	 * @param level level
	 * @return building points
	 */
	public Integer getBuildPoints(Integer level) {
		return buildPoints.value(level).intValue();
	}

	
	
	// Roles:
	/**
	 * Gets the building role hierarchy.
	 * 
	 * @return hierarchyLevel hierarchy level
	 */
	public Integer getAvailableRoles(Integer settlLevel, Integer hierarchyLevel) {
		
		
		TwoPointFunction function = hierarchy.get(hierarchyLevel);
		if(function == null) return 0;
		
		return function.intValue(settlLevel);
		
		
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
	
	

	// Experience:
	/**
	 * Gets experience requirement.
	 * 
	 * @param level settlement level
	 * @return requirement
	 */
	public Double getLevelUpExp(Integer level) {
		
		return levelUpExp.value(level);

	}
	
	/**
	 * Gets experience speed.
	 * 
	 * @param players players online
	 * @return speed
	 */
	public Double getExpSpeed(Integer players) {
		
		return expSpeed.value(players);

	}
	
	/**
	 * Gets maximum level.
	 * 
	 * @return maximum level
	 */
	public Integer getMaxLevel() {

		return levelUpExp.getXMax().intValue();

	}
	
	
	
	// Claiming:
	/**
	 * Gets the number of claims available.
	 * 
	 * @param level level
	 * @return number of claims
	 */
	public Integer getClaims(Integer level) {

		return claims.intValue(level);
		
	}

	
	
	// Other:
	/**
	* Returns the default definition.
	*
	* @return default definition
	*/
	public static SettlementDefinition defaultDefinition(){


		SettlementDefinition definition = new SettlementDefinition();
		definition.activePlayers = new TwoPointFunction(0.0);
		definition.complete();
		return definition;


	}
	
	
}
