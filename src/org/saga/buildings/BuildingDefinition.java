package org.saga.buildings;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import org.saga.Saga;
import org.saga.player.Proficiency;
import org.saga.player.SagaPlayer;
import org.saga.utility.TwoPointFunction;

public class BuildingDefinition {

	
	/**
	 * Building point cost.
	 */
	private TwoPointFunction pointCost;
	
	/**
	 * Building point cost.
	 */
	private TwoPointFunction moneyCost;
	
	/**
	 * Building specific function.
	 */
	private TwoPointFunction levelFunction;

	/**
	 * Permissions for the building.
	 */
	private Hashtable<String, BuildingPermission> permissions;

	
	// Proficiencies:
	/**
	 * Available professions for the building.
	 */
	private ArrayList<String> professions;

	/**
	 * Available classes for the building.
	 */
	private ArrayList<String> classes;
	
	/**
	 * Available roles hierarchy for the building.
	 */
	private ArrayList<String> roles;

	/**
	 * Available ranks for the building.
	 */
	private ArrayList<String> ranks;

	/**
	 * Roles for the building.
	 */
	private Hashtable<String, TwoPointFunction> roleAmounts;
	
	/**
	 * Skills.
	 */
	private HashSet<String> skills;
	
	
	// Buildings:
	/**
	 * Buildings that are enabled.
	 */
	private Hashtable<String, TwoPointFunction> enabledBuildings;
	
	
	// Info:
	/**
	 * Description.
	 */
	private String description;
	
	
	// Initialization:
	/**
	 * Used by gson.
	 * 
	 */
	private BuildingDefinition() {
	}
	
	/**
	 * Initializes.
	 * 
	 * @param pointCost
	 * @param moneyCost
	 * @param levelFunction
	 */
	public BuildingDefinition(TwoPointFunction pointCost, TwoPointFunction moneyCost, TwoPointFunction levelFunction) {
		
		this.pointCost = pointCost;
		this.moneyCost = moneyCost;
		this.levelFunction = levelFunction;
		
	}

	/**
	 * Completes.
	 * 
	 * @return integrity
	 */
	public boolean complete() {
		

		boolean integrity=true;
		
		if(pointCost == null){
			pointCost = new TwoPointFunction(10000.0);
			Saga.severe(BuildingDefinition.class, "failed to initialize pointCost field", "setting default");
			integrity = false;
		}
		integrity = pointCost.complete() && integrity;
		
		if(moneyCost == null){
			moneyCost = new TwoPointFunction(10000.0);
			Saga.severe(BuildingDefinition.class, "failed to initialize moneyCost field", "setting default");
			integrity = false;
		}
		integrity = moneyCost.complete() && integrity;
		
		if(levelFunction == null){
			levelFunction = new TwoPointFunction(10000.0);
			Saga.severe(BuildingDefinition.class, "failed to initialize levelFunction field", "setting default");
			integrity = false;
		}
		integrity = levelFunction.complete() && integrity;
		
		if(permissions == null){
			permissions = new Hashtable<String, BuildingDefinition.BuildingPermission>();
			Saga.severe(BuildingDefinition.class, "failed to initialize permissions field", "setting default");
			integrity = false;
		}
		
		if(this.roleAmounts == null){
			this.roleAmounts = new Hashtable<String, TwoPointFunction>();
			Saga.severe(BuildingDefinition.class, "failed to initialize roleAmounts field", "setting default");
			integrity = false;
		}
		Enumeration<String> proficiencies = this.roleAmounts.keys();
		while (proficiencies.hasMoreElements()) {
			String proficiency = (String) proficiencies.nextElement();
			integrity = this.roleAmounts.get(proficiency).complete() && integrity;
		}
		
		if(professions == null){
			professions = new ArrayList<String>();
			Saga.severe(BuildingDefinition.class, "failed to initialize professions field", "setting default");
			integrity = false;
		}
		for (int i = 0; i < professions.size(); i++) {
			if(professions.get(i) == null){
				professions.remove(i);
				i--;
				Saga.severe(BuildingDefinition.class, "failed to initialize professions field element", "setting default");
				integrity = false;
				continue;
			}
		}
		
		if(classes == null){
			classes = new ArrayList<String>();
			Saga.severe(BuildingDefinition.class, "failed to initialize classes field", "setting default");
			integrity = false;
		}
		for (int i = 0; i < classes.size(); i++) {
			if(classes.get(i) == null){
				classes.remove(i);
				i--;
				Saga.severe(BuildingDefinition.class, "failed to initialize classes field element", "setting default");
				integrity = false;
				continue;
			}
		}
		
		if(roles == null){
			roles = new ArrayList<String>();
			Saga.severe(BuildingDefinition.class, "failed to initialize roles field", "setting default");
			integrity = false;
		}
		for (int i = 0; i < roles.size(); i++) {
			if(roles.get(i) == null){
				roles.remove(i);
				i--;
				Saga.severe(BuildingDefinition.class, "failed to initialize roles field element", "setting default");
				integrity = false;
				continue;
			}
		}
		
		if(ranks == null){
			ranks = new ArrayList<String>();
			Saga.severe(BuildingDefinition.class, "failed to initialize ranks field", "setting default");
			integrity = false;
		}
		for (int i = 0; i < ranks.size(); i++) {
			if(ranks.get(i) == null){
				ranks.remove(i);
				i--;
				Saga.severe(BuildingDefinition.class, "failed to initialize ranks field element", "setting default");
				integrity = false;
				continue;
			}
		}
		
		if(enabledBuildings == null){
			enabledBuildings = new Hashtable<String, TwoPointFunction>();
			Saga.severe(BuildingDefinition.class, "failed to initialize enabledBuildings field", "setting default");
			integrity = false;
		}
		Enumeration<String> eBuildings = enabledBuildings.keys();
		while (eBuildings.hasMoreElements()) {
			String building = (String) eBuildings.nextElement();
			integrity = enabledBuildings.get(building).complete() && integrity;
		}
		
		if(skills == null){
			skills = new HashSet<String>();
			Saga.severe(BuildingDefinition.class, "skills field failed to initialize", "setting default");
			integrity = false;
		}
		
		if(skills.remove(null)){
			Saga.severe(BuildingDefinition.class, "skills field failed to initialize element", "removing element");
			integrity = false;
		}
		
		if(description == null){
			description = "<no description>";
			Saga.severe(BuildingDefinition.class, "description field failed to initialize", "setting default");
			integrity = false;
		}
		
		return integrity;
		
		
	}
	
	
	// Interaction:
	/**
	 * Gets the building point cost.
	 * 
	 * @name level level
	 * @return the building point cost
	 */
	public Integer getPointCost(Short level) {
		return pointCost.value(level).intValue();
	}

	/**
	 * Gets the money cost.
	 * 
	 * @name level level
	 * @return the money cost
	 */
	public Integer getMoneyCost(Short level) {
		return moneyCost.value(level).intValue();
	}
	
	/**
	 * Gets the building specific function.
	 * 
	 * @return building specific function
	 */
	public TwoPointFunction getLevelFunction() {
		return levelFunction;
	}
	
	/**
	 * Returns the zero element.
	 * 
	 * @return zero level definition
	 */
	public static BuildingDefinition zeroDefinition(){
		
		
		BuildingDefinition requirements = new BuildingDefinition();
		requirements.pointCost =  new TwoPointFunction(10000.0);
		requirements.moneyCost = new TwoPointFunction(10000.0);
		requirements.levelFunction = new TwoPointFunction(10000.0);
		requirements.complete();
		return requirements;
		
		
	}
	
	/**
	 * Gets building permission for the given player based on players proficiencies.
	 * 
	 * @param sagaPlayer saga player
	 * @return building permission, none if not found
	 */
	public BuildingPermission getBuildingPermission(SagaPlayer sagaPlayer) {
		
		
		BuildingPermission buildingPermission = BuildingPermission.NONE;
		ArrayList<Proficiency> proficiencies = sagaPlayer.getAllProficiencies();
		
		for (Proficiency proficiency : proficiencies) {
			
			BuildingPermission proficiencyPermission = permissions.get(proficiency.getName());
			if(proficiencyPermission != null && proficiencyPermission.isHigher(buildingPermission)){
				buildingPermission = proficiencyPermission;
			}
			
		}
		
		return buildingPermission;
		
		
	}
	
	/**
	 * Gets the building role hierarchy.
	 * 
	 * @return promotion hierarchy
	 */
	public ArrayList<String> getRoles() {
		return new ArrayList<String>(roles);
	}
	
	/**
	 * Gets the total available roles.
	 * 
	 * @param roleName role name
	 * @param level level
	 * @return amount of available roles
	 */
	public Integer getTotalRoles(String roleName, Short level) {
		
		
		TwoPointFunction amount = roleAmounts.get(roleName);
		if(amount == null || amount.getXMin() > level){
			return 0;
		}
		return new Double(amount.value(level)).intValue();
		
		
	}
	
	/**
	 * Gets the total available buildings.
	 * 
	 * @param buildingName building name
	 * @param level level
	 * @return amount of enabled buildings
	 */
	public Integer getTotalBuildings(String buildingName, Short level) {
		
		
		TwoPointFunction amount = enabledBuildings.get(buildingName);
		if(amount == null || amount.getXMin() > level){
			return 0;
		}
		return new Double(amount.value(level)).intValue();
		
		
	}
	
	/**
	 * Gets all building names enabled by this building
	 * 
	 * @param level building level
	 * @return enabled building names
	 */
	public HashSet<String> getBuildings(Short level) {
		
		
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
	
	/**
	**
	 * Gets all role names enabled by this building
	 * 
	 * @param level building level
	 * @return enabled role names
	 */
	public HashSet<String> getRoles(Short level) {
		
		
		HashSet<String> roles = new HashSet<String>();
		
		Enumeration<String> roleNames =  this.roleAmounts.keys();
		
		while (roleNames.hasMoreElements()) {
			String roleName = (String) roleNames.nextElement();
			if(getTotalRoles(roleName, level) > 0){
				roles.add(roleName);
			}
		}
		
		return roles;

		
	}


	// Proficiencies:
	/**
	 * Check if the building has a promotion profession.
	 * 
	 * @param professionName profession name
	 * @return true if has a profession to promote to
	 */
	public boolean hasProfession(String professionName) {
		return professions.contains(professionName);
	}

	/**
	 * Check if the building has a promotion class.
	 * 
	 * @param className class name
	 * @return true if has a class to promote to
	 */
	public boolean hasClass(String className) {
		return classes.contains(className);
	}
	
	/**
	 * Check if the building has a promotion rank.
	 * 
	 * @param rankName rank name
	 * @return true if has a rank to promote to
	 */
	public boolean hasRank(String rankName) {
		return ranks.contains(rankName);
	}
	
	/**
	 * Check if the building has a promotion role.
	 * 
	 * @param roleName role name
	 * @return true if has a role to promote to
	 */
	public boolean hasRole(String roleName) {
		return roles.contains(roleName);
	}
	
	/**
	 * Gets the professions.
	 * 
	 * @return the professions
	 */
	public ArrayList<String> getProfessions() {
		return new ArrayList<String>(professions);
	}
	
	/**
	 * Gets the classes.
	 * 
	 * @return the classes
	 */
	public ArrayList<String> getClasses() {
		return new ArrayList<String>(classes);
	}
	
	
	
	/**
	 * Gets classes and professions.
	 * 
	 * @return classes and professions
	 */
	public ArrayList<String> getSelectable() {

		ArrayList<String> result = getProfessions();
		result.addAll(getClasses());
		return result;
		
	}
	

	// Skills:
	/**
	 * Check if the ability.
	 * 
	 * @param skillName skill name
	 * @return true if has a skill
	 */
	public boolean hasSkill(String skillName) {
		return skills.contains(skillName);
	}
	
	/**
	 * Gets the skills.
	 * 
	 * @return the skills
	 */
	public HashSet<String> getSkills() {
		return new HashSet<String>(skills);
	}

	// Info:
	/**
	 * Gets the description.
	 * 
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	
	// Types:
	/**
	 * Building permissions.
	 * 
	 * @author andf
	 *
	 */
	public static enum BuildingPermission{
		
		
		NONE,
		LOW,
		MEDIUM,
		HIGH,
		FULL;
		
		
		/**
		 * Checks if the permissions is higher than the given permission.
		 * 
		 * @param permission given permission
		 * @return true if higher
		 */
		public boolean isHigher(BuildingPermission permission) {
			return this.compareTo(permission) > 0;
		}
		
		/**
		 * Checks if the permissions is lower than the given permission.
		 * 
		 * @param permission given permission
		 * @return true if lower
		 */
		public boolean isLower(BuildingPermission permission) {
			return this.compareTo(permission) < 0;
		}
		
	}
	
	/**
	 * Invalid proficiency.
	 * 
	 * @author andf
	 *
	 */
	public static class InvalidProficiencyException extends Exception{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private String proficiencyName;
		
		
		/**
		 * Sets proficiency name.
		 * 
		 * @param proficiencyName proficiency name
		 */
		public InvalidProficiencyException(String proficiencyName) {
			this.proficiencyName = proficiencyName;
		}
		
		
		/**
		 * Returns proficiency name.
		 * 
		 * @return proficiency name
		 */
		public String getProficiencyName() {
			return proficiencyName;
		}
		
		
	}
	
}
