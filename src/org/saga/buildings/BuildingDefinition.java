package org.saga.buildings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.saga.Clock.DaytimeTicker.Daytime;
import org.saga.SagaLogger;
import org.saga.settlements.Settlement;
import org.saga.utility.TwoPointFunction;
import org.saga.utility.items.RecepieBlueprint;

public class BuildingDefinition {

	
	/**
	 * Replace string for craft amount.
	 */
	public final static String CRAFT_AMOUNT_REPLACE = "#craft_amount";
	
	/**
	 * Replace string for craft daytime.
	 */
	public final static String CRAFT_DAYTIME_REPLACE = "#craft_daytime";
	
	/**
	 * Replace string for perform daytime.
	 */
	public final static String PERFORM_DAYTIME_REPLACE = "#perform_daytime";
	
	
	
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
	 * Attributes.
	 */
	private HashSet<String> attributes;
	
	
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
	private Daytime craftTime;
	
	
	// Functions:
	/**
	 * Building functions.
	 */
	private Hashtable<String, TwoPointFunction> functions;
	
	
	// Upgrading:
	/**
	 * Building max score.
	 */
	private Integer maxScore;
	
	/**
	 * Upgrade cost.
	 */
	private TwoPointFunction upgradeCost;

	
	// Proficiencies:
	/**
	 * Available roles.
	 */
	private Hashtable<String, Double> roles;
	
	/**
	 * Available ranks.
	 */
	private Hashtable<String, Double> ranks;
	
	
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
		
		if(attributes == null){
			attributes = new HashSet<String>();
			SagaLogger.nullField(BuildingDefinition.class, "attributes");
		}
		
		if(attributes.remove(null)){
			SagaLogger.nullField(BuildingDefinition.class, "attributes element");
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
		
		if(craftTime == null){
			craftTime = Daytime.NONE;
			SagaLogger.nullField(BuildingDefinition.class, "resources");
		}
		
		if(functions == null){
			functions = new Hashtable<String, TwoPointFunction>();
			SagaLogger.nullField(this, "functions");
		}
		Collection<TwoPointFunction> functionsElements = functions.values();
		for (TwoPointFunction function : functionsElements) {
			function.complete();
		}
		
		if(maxScore == null){
			maxScore = 0;
			SagaLogger.nullField(BuildingDefinition.class, "maxScore");
		}
		
		if(upgradeCost == null){
			upgradeCost = new TwoPointFunction(Double.MAX_VALUE);
			SagaLogger.nullField(BuildingDefinition.class, "upgradeCost");
		}
		upgradeCost.complete();
		
		if(roles == null){
			roles = new Hashtable<String, Double>();
			SagaLogger.nullField(BuildingDefinition.class, "roles");
		}
		
		if(ranks == null){
			ranks = new Hashtable<String, Double>();
			SagaLogger.nullField(BuildingDefinition.class, "ranks");
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
		return craftTime;
	}
	
	
	
	// Functions:
	/**
	 * Gets a function for the given key.
	 * 
	 * @param key key
	 * @return function for the given key, 0 if none
	 */
	public TwoPointFunction getFunction(String key) {

		TwoPointFunction function = functions.get(key);
		
		if(function == null){
			SagaLogger.severe(this, "failed to retrive function for " + key + " key");
			return new TwoPointFunction(0.0);
		}
		
		return function;

	}
	
	
	
	// Upgrading:
	/**
	 * Get buildings max score.
	 * 
	 * @return max score
	 */
	public Integer getMaxScore() {
		return maxScore;
	}
	
	/**
	 * Gets building upgrade cost.
	 * 
	 * @param score building score
	 * @return upgrade cost
	 */
	public Double getUpgradeCost(Integer score) {
		return upgradeCost.value(score);
	}
	
	
	
	// Proficiencies:
	/**
	 * Gets all role names.
	 * 
	 * @return all role names
	 */
	public Set<String> getAllRoles() {
		return roles.keySet();
	}
	
	/**
	 * Gets amount of roles available.
	 * 
	 * @param role role name
	 * @return roles available
	 */
	public Double getRoles(String role) {
		Double amount = roles.get(role);
		if(amount == null) return 0.0;
		return amount;
	}
	
	/**
	 * Gets all rank names.
	 * 
	 * @return all rank names
	 */
	public Set<String> getAllRanks() {
		return ranks.keySet();
	}
	
	/**
	 * Gets amount of ranks available.
	 * 
	 * @param rank rank name
	 * @return ranks available
	 */
	public Double getRanks(String rank) {
		Double amount = ranks.get(rank);
		if(amount == null) return 0.0;
		return amount;
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
			.replaceAll(PERFORM_DAYTIME_REPLACE, getPerformTime().toString())
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
