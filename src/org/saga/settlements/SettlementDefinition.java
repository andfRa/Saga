package org.saga.settlements;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import org.saga.Saga;
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
	private TwoPointFunction buildingPoints;
	
	/**
	 * Enabled roles.
	 */
	private Hashtable<String, TwoPointFunction> roles;
	
	
	/**
	 * Buildings.
	 */
	private Hashtable<String, TwoPointFunction> buildings;

	
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
			Saga.severe("SettlementDefinition failed to initialize activePlayers field. Setting default.");
			activePlayers = new TwoPointFunction(0.0);
			integrity = false;
		}
		
		if(buildingPoints == null){
			Saga.severe("SettlementDefinition failed to initialize buildingPoints field. Setting default.");
			buildingPoints = new TwoPointFunction(0.0);
			integrity=false;
		}
		
		if(roles == null){
			roles = new Hashtable<String, TwoPointFunction>();
			Saga.severe(SettlementDefinition.class, "roles field failed to initialize", "setting default");
			integrity = false;
		}
		Enumeration<String> eRoles = roles.keys();
		while (eRoles.hasMoreElements()) {
			String role = (String) eRoles.nextElement();
			integrity = roles.get(role).complete() && integrity;
		}
		
		if(buildings == null){
			buildings = new Hashtable<String, TwoPointFunction>();
			Saga.severe(SettlementDefinition.class, "buildings field failed to initialize", "setting default");
			integrity = false;
		}
		Enumeration<String> eBuildings = buildings.keys();
		while (eBuildings.hasMoreElements()) {
			String building = (String) eBuildings.nextElement();
			integrity = buildings.get(building).complete() && integrity;
		}
		
		if(levelUpExp == null){
			levelUpExp = new TwoPointFunction(10000.0);
			Saga.severe(SettlementDefinition.class, "levelUpExp field failed to initialize", "setting default");
			integrity = false;
		}
		integrity = integrity && levelUpExp.complete();
		
		if(expSpeed == null){
			expSpeed = new TwoPointFunction(0.0);
			Saga.severe(SettlementDefinition.class, "expSpeed field failed to initialize", "setting default");
			integrity = false;
		}
		integrity = integrity && expSpeed.complete();
		
		if(claims == null){
			SagaLogger.severe(getClass(), "claims field failed to initialize");
			claims = new TwoPointFunction(1.0);
			integrity=false;
		}
		integrity = claims.complete() && integrity;
		
		if(ownerRole == null){
			SagaLogger.nullField(getClass(),"ownerRole");
			ownerRole = "";
			integrity=false;
		}
		
		if(defaultRole == null){
			SagaLogger.nullField(getClass(),"defaultRole");
			defaultRole = "";
			integrity=false;
		}
		
		return integrity;
		
		
	}
	
	
	// Interaction:
	/**
	 * Check if the settlement can level up.
	 * 
	 * @param settlement settlement
	 * @return true if can level up
	 */
	public boolean canLevelUp(Settlement settlement) {

		
		// Active players:
		if(settlement.getActivePlayerCount() < activePlayers.value(settlement.getLevel())){
			return false;
		}
		
		return true;
		
		
	}

	/**
	 * Gets the activePlayers.
	 * 
	 * @param level level
	 * @return the activePlayers
	 */
	public Integer getActivePlayers(Integer level) {
		return activePlayers.value(level).intValue();
	}

	/**
	 * Get building points.
	 * 
	 * @param level level
	 * @return building points
	 */
	public Integer getBuildingPoints(Integer level) {
		return buildingPoints.value(level).intValue();
	}

	
	// Roles:
	/**
	 * Gets the building role hierarchy.
	 * 
	 * @return promotion hierarchy
	 */
	public ArrayList<String> getRoles() {
		return new ArrayList<String>(roles.keySet());
	}
	
	/**
	 * Gets the total available roles.
	 * 
	 * @param roleName role name
	 * @param level level
	 * @return amount of available roles
	 */
	public Integer getTotalRoles(String roleName, Integer level) {
		
		
		TwoPointFunction amount = roles.get(roleName);
		if(amount == null || amount.getXMin() > level){
			return 0;
		}
		return new Double(amount.value(level)).intValue();
		
		
	}
	

	// Buildings:
	/**
	 * Gets the total available buildings.
	 * 
	 * @param buildingName building name
	 * @param level settlement level
	 * @return amount of enabled buildings
	 */
	public Integer getTotalBuildings(String buildingName, Integer level) {
		
		TwoPointFunction amount = buildings.get(buildingName);
		if(amount == null || amount.getXMin() > level){
			return 0;
		}
		return new Double(amount.value(level)).intValue();
		
		
	}
	
	/**
	 * Gets all building names enabled by this building
	 * 
	 * @param level settlement level
	 * @return enabled building names
	 */
	public HashSet<String> getAllBuildings(Integer level) {
		
		
		HashSet<String> allBuildings = new HashSet<String>();
		
		Enumeration<String> buildingNames =  buildings.keys();
		
		while (buildingNames.hasMoreElements()) {
			
			String buildingName = (String) buildingNames.nextElement();
			
			if(getTotalBuildings(buildingName, level) > 0){
				allBuildings.add(buildingName);
			}
			
		}
		
		return allBuildings;

		
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
	public Double getClaims(Integer level) {

		return claims.value(level);
		
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
		definition.buildings = new Hashtable<String, TwoPointFunction>();
		definition.complete();
		return definition;


	}
	
	
}
