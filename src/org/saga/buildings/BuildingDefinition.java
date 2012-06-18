package org.saga.buildings;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import org.saga.SagaLogger;
import org.saga.utility.TwoPointFunction;

public class BuildingDefinition {


	/**
	 * Building class.
	 */
	private String className;
	
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
	 * Attributes.
	 */
	private HashSet<String> attributes;
	
	/**
	 * Abilities.
	 */
	private HashSet<String> abilities;
	
	
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
	
	
	// Initialisation:
	/**
	 * Used by gson.
	 * 
	 */
	private BuildingDefinition() {
	}
	
	/**
	 * Initialises.
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
		
		if(className == null){
			SagaLogger.nullField(this, "className");
			className = "invalid";
		}
		
		if(pointCost == null){
			pointCost = new TwoPointFunction(10000.0);
			SagaLogger.nullField(BuildingDefinition.class, "pointCost");
			integrity = false;
		}
		integrity = pointCost.complete() && integrity;
		
		if(moneyCost == null){
			moneyCost = new TwoPointFunction(10000.0);
			SagaLogger.nullField(BuildingDefinition.class, "moneyCost");
			integrity = false;
		}
		integrity = moneyCost.complete() && integrity;
		
		if(levelFunction == null){
			levelFunction = new TwoPointFunction(10000.0);
			SagaLogger.nullField(BuildingDefinition.class, "levelFunction");
			integrity = false;
		}
		integrity = levelFunction.complete() && integrity;
		
		if(this.roleAmounts == null){
			this.roleAmounts = new Hashtable<String, TwoPointFunction>();
			SagaLogger.nullField(BuildingDefinition.class, "roleAmounts");
			integrity = false;
		}
		Enumeration<String> proficiencies = this.roleAmounts.keys();
		while (proficiencies.hasMoreElements()) {
			String proficiency = (String) proficiencies.nextElement();
			integrity = this.roleAmounts.get(proficiency).complete() && integrity;
		}
		
		if(professions == null){
			professions = new ArrayList<String>();
			SagaLogger.nullField(BuildingDefinition.class, "professions");
			integrity = false;
		}
		for (int i = 0; i < professions.size(); i++) {
			if(professions.get(i) == null){
				professions.remove(i);
				i--;
				SagaLogger.nullField(BuildingDefinition.class, "professions element");
				integrity = false;
				continue;
			}
		}
		
		if(classes == null){
			classes = new ArrayList<String>();
			SagaLogger.nullField(BuildingDefinition.class, "classes");
			integrity = false;
		}
		for (int i = 0; i < classes.size(); i++) {
			if(classes.get(i) == null){
				classes.remove(i);
				i--;
				SagaLogger.nullField(this, "classes element");
				integrity = false;
				continue;
			}
		}
		
		if(roles == null){
			roles = new ArrayList<String>();
			SagaLogger.nullField(BuildingDefinition.class, "roles");
			integrity = false;
		}
		for (int i = 0; i < roles.size(); i++) {
			if(roles.get(i) == null){
				roles.remove(i);
				i--;
				SagaLogger.nullField(BuildingDefinition.class, "roles element");
				integrity = false;
				continue;
			}
		}
		
		if(ranks == null){
			ranks = new ArrayList<String>();
			SagaLogger.nullField(BuildingDefinition.class, "ranks");
			integrity = false;
		}
		for (int i = 0; i < ranks.size(); i++) {
			if(ranks.get(i) == null){
				ranks.remove(i);
				i--;
				SagaLogger.nullField(BuildingDefinition.class, "ranks element");
				integrity = false;
				continue;
			}
		}
		
		if(enabledBuildings == null){
			enabledBuildings = new Hashtable<String, TwoPointFunction>();
			SagaLogger.nullField(BuildingDefinition.class, "enabledBuildings");
			integrity = false;
		}
		Enumeration<String> eBuildings = enabledBuildings.keys();
		while (eBuildings.hasMoreElements()) {
			String building = (String) eBuildings.nextElement();
			integrity = enabledBuildings.get(building).complete() && integrity;
		}
		
		if(attributes == null){
			attributes = new HashSet<String>();
			SagaLogger.nullField(BuildingDefinition.class, "attributes");
			integrity = false;
		}
		
		if(attributes.remove(null)){
			SagaLogger.nullField(BuildingDefinition.class, "attributes element");
			integrity = false;
		}
		
		if(abilities == null){
			abilities = new HashSet<String>();
			SagaLogger.nullField(BuildingDefinition.class, "abilities");
			integrity = false;
		}
		
		if(abilities.remove(null)){
			SagaLogger.nullField(BuildingDefinition.class, "abilities element");
			integrity = false;
		}
		
		if(description == null){
			description = "<no description>";
			SagaLogger.nullField(BuildingDefinition.class, "description");
			integrity = false;
		}
		
		return integrity;
		
		
	}
	
	
	// Interaction:
	/**
	 * Gets the class name.
	 * 
	 * @return class name
	 */
	public String getClassName() {
		return className;
	}
	
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
	

	// Attributes:
	/**
	 * Check if the building allows the attribute.
	 * 
	 * @param name attributes name
	 * @return true if has a attributes
	 */
	public boolean hasAttribute(String name) {
		return attributes.contains(name);
	}
	
	/**
	 * Gets the attributes.
	 * 
	 * @return the attributes
	 */
	public HashSet<String> getAttributes() {
		return new HashSet<String>(attributes);
	}
	
	
	// Abilities:
	/**
	 * Check if the building allows the ability.
	 * 
	 * @param name ability name
	 * @return true if has the ability
	 */
	public boolean hasAbility(String name) {
		return abilities.contains(name);
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

	
	// Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if(className == null) return "null";
		return className;
	}
	
}
