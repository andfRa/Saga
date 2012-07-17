package org.saga.buildings;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

import org.saga.Clock.DaytimeTicker.Daytime;
import org.saga.SagaLogger;
import org.saga.settlements.Settlement;
import org.saga.utility.ItemBlueprint;
import org.saga.utility.TwoPointFunction;

public class BuildingDefinition {


	/**
	 * Building class.
	 */
	private String className;

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
	
	
	// Availability:
	/**
	 * Coin cost.
	 */
	private TwoPointFunction coinCost;
	
	/**
	 * Number of buildings available.
	 */
	private TwoPointFunction available;
	
	
	// Crafting:
	/**
	 * Number of storage areas available.
	 */
	private TwoPointFunction storageAreas;
	
	/**
	 * Items crafted by the buildings.
	 */
	private Hashtable<Double, ItemBlueprint> craftable;
	
	/**
	 * Amount of items crafted.
	 */
	private TwoPointFunction craftAmount;

	
	// Timings:
	/**
	 * Perform time.
	 */
	private Daytime performTime;
	
	/**
	 * Craft time.
	 */
	private Daytime craftTime;
	
	
	// Info:
	/**
	 * Description.
	 */
	private String description;
	
	
	
	// Initialisation:
	/**
	 * Initialises.
	 * 
	 * @param pointCost
	 * @param moneyCost
	 * @param levelFunction
	 */
	public BuildingDefinition(TwoPointFunction pointCost, TwoPointFunction moneyCost, TwoPointFunction levelFunction) {
		
		this.coinCost = moneyCost;
		this.levelFunction = levelFunction;
		
	}

	/**
	 * Completes the definition.
	 * 
	 */
	public void complete() {

		
		if(className == null){
			SagaLogger.nullField(this, "className");
			className = "invalid";
		}
		
		if(levelFunction == null){
			levelFunction = new TwoPointFunction(10000.0);
			SagaLogger.nullField(BuildingDefinition.class, "levelFunction");
		}
		levelFunction.complete();
		
		if(this.roleAmounts == null){
			this.roleAmounts = new Hashtable<String, TwoPointFunction>();
			SagaLogger.nullField(BuildingDefinition.class, "roleAmounts");
		}
		Enumeration<String> proficiencies = this.roleAmounts.keys();
		while (proficiencies.hasMoreElements()) {
			String proficiency = (String) proficiencies.nextElement();
			this.roleAmounts.get(proficiency).complete();
		}
		
		if(professions == null){
			professions = new ArrayList<String>();
			SagaLogger.nullField(BuildingDefinition.class, "professions");
		}
		for (int i = 0; i < professions.size(); i++) {
			if(professions.get(i) == null){
				professions.remove(i);
				i--;
				SagaLogger.nullField(BuildingDefinition.class, "professions element");
				continue;
			}
		}
		
		if(classes == null){
			classes = new ArrayList<String>();
			SagaLogger.nullField(BuildingDefinition.class, "classes");
		}
		for (int i = 0; i < classes.size(); i++) {
			if(classes.get(i) == null){
				classes.remove(i);
				i--;
				SagaLogger.nullField(this, "classes element");
				continue;
			}
		}
		
		if(roles == null){
			roles = new ArrayList<String>();
			SagaLogger.nullField(BuildingDefinition.class, "roles");
		}
		for (int i = 0; i < roles.size(); i++) {
			if(roles.get(i) == null){
				roles.remove(i);
				i--;
				SagaLogger.nullField(BuildingDefinition.class, "roles element");
				continue;
			}
		}
		
		if(ranks == null){
			ranks = new ArrayList<String>();
			SagaLogger.nullField(BuildingDefinition.class, "ranks");
		}
		for (int i = 0; i < ranks.size(); i++) {
			if(ranks.get(i) == null){
				ranks.remove(i);
				i--;
				SagaLogger.nullField(BuildingDefinition.class, "ranks element");
				continue;
			}
		}
		
		if(attributes == null){
			attributes = new HashSet<String>();
			SagaLogger.nullField(BuildingDefinition.class, "attributes");
		}
		
		if(attributes.remove(null)){
			SagaLogger.nullField(BuildingDefinition.class, "attributes element");
		}
		
		if(abilities == null){
			abilities = new HashSet<String>();
			SagaLogger.nullField(BuildingDefinition.class, "abilities");
		}
		
		if(abilities.remove(null)){
			SagaLogger.nullField(BuildingDefinition.class, "abilities element");
		}
		
		if(coinCost == null){
			coinCost = new TwoPointFunction(10000.0);
			SagaLogger.nullField(BuildingDefinition.class, "coinCost");
		}
		coinCost.complete();
		
		if(available == null){
			available = new TwoPointFunction(0.0);
			SagaLogger.nullField(BuildingDefinition.class, "available");
		}
		available.complete();
		
		
		if(storageAreas == null){
			storageAreas = new TwoPointFunction(0.0);
			SagaLogger.nullField(BuildingDefinition.class, "storageAreas");
		}
		storageAreas.complete();
		
		
		if(craftable == null){
			craftable = new Hashtable<Double, ItemBlueprint>();
			SagaLogger.nullField(BuildingDefinition.class, "craftable");
		}
		Set<Entry<Double, ItemBlueprint>> craEntries = craftable.entrySet();
		for (Entry<Double, ItemBlueprint> entry : craEntries) {
			entry.getValue().complete();
		}
		
		if(craftAmount == null){
			craftAmount = new TwoPointFunction(0.0);
			SagaLogger.nullField(BuildingDefinition.class, "craftAmount");
		}
		craftAmount.complete();
		
		
		if(performTime == null){
			performTime = Daytime.NONE;
			SagaLogger.nullField(BuildingDefinition.class, "performTime");
		}
		
		if(craftTime == null){
			craftTime = Daytime.NONE;
			SagaLogger.nullField(BuildingDefinition.class, "craftTime");
		}
		
		
		if(description == null){
			description = "<no description>";
			SagaLogger.nullField(BuildingDefinition.class, "description");
		}
		
		
	}
	
	
	
	// Naming:
	/**
	 * Gets the class name.
	 * 
	 * @return class name
	 */
	public String getClassName() {
		return className;
	}

	
	
	// Roles:
	/**
	 * Gets the building role hierarchy.
	 * 
	 * @return promotion hierarchy
	 */
	public ArrayList<String> getRoles() {
		return new ArrayList<String>(roles);
	}
	
	/**
	 * Gets the amount or roles available.
	 * 
	 * @param roleName role name
	 * @param level level
	 * @return amount of roles available
	 */
	public Integer getAvailableRoles(String roleName, Integer level) {
		
		
		TwoPointFunction amount = roleAmounts.get(roleName);
		if(amount == null || amount.getXMin() > level){
			return 0;
		}
		return new Double(amount.value(level)).intValue();
		
		
	}
	
	/**
	**
	 * Gets all role names enabled by this building
	 * 
	 * @param level building level
	 * @return enabled role names
	 */
	public HashSet<String> getRoles(Integer level) {
		
		
		HashSet<String> roles = new HashSet<String>();
		
		Enumeration<String> roleNames =  this.roleAmounts.keys();
		
		while (roleNames.hasMoreElements()) {
			String roleName = (String) roleNames.nextElement();
			if(getAvailableRoles(roleName, level) > 0){
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
	public ArrayList<String> getSelectable2() {

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
	
	
	
	// Availability:
	/**
	 * Gets the number of available buildings.
	 * 
	 * @param level building level
	 * @return number of buildings
	 */
	public Integer getAvailableAmount(Integer level) {
		
		return available.intValue(level);

	}
	
	/**
	 * Gets the required settlement level.
	 * 
	 * @return required settlement level
	 */
	public Integer getRequiredLevel() {

		return available.getXMin().intValue();

	}
	
	/**
	 * Checks the requirements for the given building.
	 * 
	 * @param settlement settlement
	 * @param buildingLevel building level
	 * @return true if the requirements are met
	 */
	public boolean checkRequirements(Settlement settlement, Integer buildingLevel) {

		
		// Building not available:
		if(getRequiredLevel() > settlement.getLevel()) return false;
		
		return true;
		
		
	}

	
	
	// Crafting:
	/**
	 * Gets the amount of storage areas available.
	 * 
	 * @param buildingLevel building level
	 * @return storage areas available
	 */
	public Integer getAvailableStorages(Integer buildingLevel) {
		return storageAreas.intValue(buildingLevel);
	}
	
	/**
	 * Gets craftable items table.
	 * 
	 * @return craftable items table
	 */
	public Hashtable<Double, ItemBlueprint> getCraftable() {
		return new Hashtable<Double, ItemBlueprint>(craftable);
	}
	
	/**
	 * Gets the amount of crafted items.
	 * 
	 * @param level building level
	 * @return amount of crafted items
	 */
	public Integer getCraftAmount(Integer level) {
		return craftAmount.intValue(level);
	}
	
	
	
	// Timings:
	/**
	 * Gets the perform time.
	 * 
	 * @return perform time
	 */
	public Daytime getPerformTime() {
		return performTime;
	}

	/**
	 * Gets the craft time.
	 * 
	 * @return craft time
	 */
	public Daytime getCraftTime() {
		return craftTime;
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
