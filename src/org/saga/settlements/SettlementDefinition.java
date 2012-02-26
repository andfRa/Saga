package org.saga.settlements;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import org.saga.Saga;
import org.saga.utility.TwoPointFunction;

public class SettlementDefinition {

	
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
	 * Buildings that are enabled.
	 */
	private Hashtable<String, TwoPointFunction> enabledBuildings;

	/**
	 * Experience requirement.
	 */
	private TwoPointFunction expRequirement;

	/**
	 * Experience speed.
	 */
	private TwoPointFunction expSpeed;
	

	// Initialization:
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
		
		if(enabledBuildings == null){
			enabledBuildings = new Hashtable<String, TwoPointFunction>();
			Saga.severe(SettlementDefinition.class, "enabledBuildings field failed to initialize", "setting default");
			integrity = false;
		}
		Enumeration<String> eBuildings = enabledBuildings.keys();
		while (eBuildings.hasMoreElements()) {
			String building = (String) eBuildings.nextElement();
			integrity = enabledBuildings.get(building).complete() && integrity;
		}
		
		if(expRequirement == null){
			expRequirement = new TwoPointFunction(10000.0);
			Saga.severe(SettlementDefinition.class, "expRequirement field failed to initialize", "setting default");
			integrity = false;
		}
		integrity = integrity && expRequirement.complete();
		
		if(expSpeed == null){
			expSpeed = new TwoPointFunction(0.0);
			Saga.severe(SettlementDefinition.class, "expSpeed field failed to initialize", "setting default");
			integrity = false;
		}
		integrity = integrity && expSpeed.complete();
		
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
		
		TwoPointFunction amount = enabledBuildings.get(buildingName);
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
		
		
		HashSet<String> buildings = new HashSet<String>();
		
		Enumeration<String> buildingNames =  enabledBuildings.keys();
		
		while (buildingNames.hasMoreElements()) {
			String buildingName = (String) buildingNames.nextElement();
			if(getTotalBuildings(buildingName, level) > 0){
				buildings.add(buildingName);
			}
		}
		
		return buildings;

		
	}
	
	
	// Experience:
	/**
	 * Gets experience requirement.
	 * 
	 * @param level settlement level
	 * @return requirement
	 */
	public Double getExpRequired(Integer level) {
		
		return expRequirement.value(level);

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
	
	
	// Other:
	/**
	* Returns the default definition.
	*
	* @return default definition
	*/
	public static SettlementDefinition defaultDefinition(){


		SettlementDefinition definition = new SettlementDefinition();
		definition.activePlayers = new TwoPointFunction(0.0);
		definition.enabledBuildings = new Hashtable<String, TwoPointFunction>();
		definition.complete();
		return definition;


	}
	
	
}
