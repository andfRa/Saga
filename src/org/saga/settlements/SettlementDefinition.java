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
			activePlayers = new TwoPointFunction(Double.MAX_VALUE, Short.MAX_VALUE, Double.MAX_VALUE);
			integrity = false;
		}
		
		if(buildingPoints == null){
			Saga.severe("SettlementDefinition failed to initialize buildingPoints field. Setting default.");
			buildingPoints = new TwoPointFunction(0.0, 0.0);
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
		if(settlement.getActivePlayerCount() < activePlayers.calculateValue(settlement.getLevel())){
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
	public Integer getActivePlayers(Short level) {
		return activePlayers.calculateValue(level).intValue();
	}

	/**
	 * Get building points.
	 * 
	 * @param level level
	 * @return building points
	 */
	public Integer getBuildingPoints(Short level) {
		return buildingPoints.calculateValue(level).intValue();
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
	public Integer getTotalRoles(String roleName, Short level) {
		
		
		TwoPointFunction amount = roles.get(roleName);
		if(amount == null || amount.getXRequired() > level){
			return 0;
		}
		return new Double(amount.calculateValue(level)).intValue();
		
		
	}
	

	// Buildings:
	/**
	 * Gets the total available buildings.
	 * 
	 * @param buildingName building name
	 * @param level settlement level
	 * @return amount of enabled buildings
	 */
	public Integer getTotalBuildings(String buildingName, Short level) {
		
		TwoPointFunction amount = enabledBuildings.get(buildingName);
		if(amount == null || amount.getXRequired() > level){
			return 0;
		}
		return new Double(amount.calculateValue(level)).intValue();
		
		
	}
	
	/**
	 * Gets all building names enabled by this building
	 * 
	 * @param level settlement level
	 * @return enabled building names
	 */
	public HashSet<String> getAllBuildings(Short level) {
		
		
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
	
	
	// Other:
	/**
	 * Returns the default definition.
	 * 
	 * @return default definition
	 */
	public static SettlementDefinition defaultDefinition(){
		
		
		SettlementDefinition definition = new SettlementDefinition();
		definition.activePlayers = new TwoPointFunction(Double.MAX_VALUE, Short.MAX_VALUE, Double.MAX_VALUE);
		definition.enabledBuildings = new Hashtable<String, TwoPointFunction>();
		definition.complete();
		return definition;
		
		
	}
	
	
}
