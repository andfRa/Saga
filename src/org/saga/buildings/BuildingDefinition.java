package org.saga.buildings;

import java.util.ArrayList;
import java.util.HashSet;

import org.saga.Clock.DaytimeTicker.Daytime;
import org.saga.SagaLogger;
import org.saga.settlements.Settlement;
import org.saga.utility.TwoPointFunction;
import org.saga.utility.items.RecepieBlueprint;

public class BuildingDefinition {

	
	/**
	 * Replace string for craft amount.
	 */
	public final static String CRAFT_AMOUNT_REPLACE = "#caft_amount";
	
	/**
	 * Replace string for craft daytime.
	 */
	public final static String CRAFT_DAYTIME_REPLACE = "#craft_daytime";
	
	
	/**
	 * Building name.
	 */
	private String name;

	/**
	 * Building class.
	 */
	private String buildingClass;


	
	// Proficiencies:
	/**
	 * Available roles hierarchy for the building.
	 */
	private ArrayList<String> roles;

	/**
	 * Available ranks for the building.
	 */
	private ArrayList<String> ranks;

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
	 * Building points.
	 */
	private Integer buildPoints;
	
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
	 * Storage area size.
	 */
	private Integer storageSize;
	
	/**
	 * Recipes for the building.
	 */
	private HashSet<RecepieBlueprint> recipes;
	
	/**
	 * Amount of resources crafted.
	 */
	private TwoPointFunction craftAmount;

	/**
	 * Related buildings.
	 */
	private ArrayList<String> relatedBuildings;
	
	
	// Timings:
	/**
	 * Perform time.
	 */
	private Daytime performTime;
	
	/**
	 * Resources craft time.
	 */
	private Daytime resourceTime;
	
	
	// Info:
	/**
	 * Description.
	 */
	private String description;
	
	/**
	 * Building effect.
	 */
	private String effect;
	
	
	
	// Initialisation:
	/**
	 * Initialises.
	 * 
	 * @param name building name
	 */
	public BuildingDefinition(String name) {
		
		this.name = name;
		
	}

	/**
	 * Completes the definition.
	 * 
	 */
	public void complete() {

		
		if(name == null){
			SagaLogger.nullField(this, "name");
			name = "invalid";
		}
		
		if(buildingClass == null){
			SagaLogger.nullField(this, "buildingClass");
			buildingClass = "invalid";
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
		
		if(buildPoints == null){
			buildPoints = 1;
			SagaLogger.nullField(BuildingDefinition.class, "buildPoints");
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
		
		if(storageSize == null){
			storageSize = 1;
			SagaLogger.nullField(BuildingDefinition.class, "storageSize");
		}
		
		if(recipes == null){
			recipes = new HashSet<RecepieBlueprint>();
			SagaLogger.nullField(BuildingDefinition.class, "recipes");
		}
		for (RecepieBlueprint recipe : recipes) {
			recipe.complete();
		}
		
		if(craftAmount == null){
			craftAmount = new TwoPointFunction(0.0);
			SagaLogger.nullField(BuildingDefinition.class, "craftAmount");
		}
		craftAmount.complete();
		
		if(relatedBuildings == null){
			relatedBuildings = new ArrayList<String>();
			SagaLogger.nullField(BuildingDefinition.class, "relatedBuildings");
		}
		
		if(performTime == null){
			performTime = Daytime.NONE;
			SagaLogger.nullField(BuildingDefinition.class, "performTime");
		}
		
		if(resourceTime == null){
			resourceTime = Daytime.NONE;
			SagaLogger.nullField(BuildingDefinition.class, "resources");
		}
		
		
		if(description == null){
			description = "<no description>";
			SagaLogger.nullField(BuildingDefinition.class, "description");
		}
		
		if(effect == null){
			effect = "";
			SagaLogger.nullField(BuildingDefinition.class, "effect");
		}
		
		
	}
	
	
	
	// Naming:
	/**
	 * Gets building name.
	 * 
	 * @return building name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the class name.
	 * 
	 * @return class name
	 */
	public String getBuildingClass() {
		return buildingClass;
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

	
	// Proficiencies:
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
	 * Gets the amount of building points required.
	 * 
	 * @return amount of building points
	 */
	public Integer getBuildPoints() {
		return buildPoints;
	}
	
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
	 * Gets the size of the storage area.
	 * 
	 * @return storage area size
	 */
	public Integer getStorageSize() {
		return storageSize;
	}
	
	/**
	 * Gets building recipes.
	 * 
	 * @return building recipes
	 */
	public HashSet<RecepieBlueprint> getRecipes() {
		return new HashSet<RecepieBlueprint>(recipes);
	}
	
	/**
	 * Gets the amount of crafted resources.
	 * 
	 * @param level building level
	 * @return amount of crafted resources
	 */
	public Integer getCraftAmount(Integer level) {
		return craftAmount.intValue(level);
	}
	
	/**
	 * Gets all related buildings.
	 * 
	 * @return related buildings
	 */
	public ArrayList<String> getRelatedBuildings() {
		return new ArrayList<String>(relatedBuildings);
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
		return resourceTime;
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
	
	/**
	 * Gets the building effect.
	 * 
	 * @return building effect
	 */
	public String getEffect(Integer bldgScore) {
		
		return effect
			.replaceAll(CRAFT_AMOUNT_REPLACE, getCraftAmount(bldgScore).toString())
			.replaceAll(CRAFT_DAYTIME_REPLACE, getCraftTime().toString())
		;
		
	}
	

	
	// Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
	
}
