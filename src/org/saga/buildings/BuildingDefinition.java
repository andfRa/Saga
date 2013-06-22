package org.saga.buildings;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

import org.saga.Clock.DaytimeTicker.Daytime;
import org.saga.SagaLogger;
import org.saga.buildings.production.SagaRecipe;
import org.saga.settlements.Settlement;
import org.saga.utility.TwoPointFunction;

public class BuildingDefinition {

	
	/**
	 * Building name.
	 */
	private String name;

	/**
	 * Building class.
	 */
	private String buildingClass;


	
	// Availability:
	/**
	 * Building points.
	 */
	private Integer buildPoints;
	
	/**
	 * Number of buildings available.
	 */
	private TwoPointFunction available;
	
	
	// Crafting:
	/**
	 * Number of storage areas available.
	 */
	private Integer storages;
	
	/**
	 * Storage area size.
	 */
	private Integer storageSize;
	
	
	// Production:
	/**
	 * Production recipes for the building.
	 */
	private SagaRecipe[] production;
	
	
	// Timings:
	/**
	 * Perform time.
	 */
	private Daytime performTime;
	
	
	// Functions:
	/**
	 * Building functions.
	 */
	private Hashtable<String, TwoPointFunction> functions;
	
	
	
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
		
		if(buildPoints == null){
			buildPoints = 1;
			SagaLogger.nullField(BuildingDefinition.class, "buildPoints");
		}
		
		if(available == null){
			available = new TwoPointFunction(0.0);
			SagaLogger.nullField(BuildingDefinition.class, "available");
		}
		available.complete();
		
		
		if(storages == null){
			storages = 0;
			SagaLogger.nullField(BuildingDefinition.class, "storages");
		}
		
		if(storageSize == null){
			storageSize = 1;
			SagaLogger.nullField(BuildingDefinition.class, "storageSize");
		}
		
		if(production == null){
			production = new SagaRecipe[0];
			SagaLogger.nullField(BuildingDefinition.class, "production");
		}
		for (SagaRecipe recipie : production) {
			recipie.complete();
		}
		
		
		if(performTime == null){
			performTime = Daytime.NONE;
			SagaLogger.nullField(BuildingDefinition.class, "performTime");
		}
		

		if(functions == null){
			functions = new Hashtable<String, TwoPointFunction>();
			SagaLogger.nullField(this, "functions");
		}
		Collection<TwoPointFunction> functionsElements = functions.values();
		for (TwoPointFunction function : functionsElements) {
			function.complete();
		}
		
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
		
		if(level < available.getXMin()) return 0;
		
		return available.intValue(level);

	}
	
	/**
	 * Gets the required claimed chunks.
	 * 
	 * @return required claimed chunks
	 */
	public Integer getRequiredClaimed() {

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
		if(getRequiredClaimed() > settlement.getSize()) return false;
		
		return true;
		
	}

	
	
	// Crafting:
	/**
	 * Gets the amount of storage areas available.
	 * 
	 * @return storage areas available
	 */
	public Integer getStorages() {
		return storages;
	}
	
	/**
	 * Gets the size of the storage area.
	 * 
	 * @return storage area size
	 */
	public Integer getStorageSize() {
		return storageSize;
	}
	
	
	
	// Production:
	/**
	 * Gets all production recipes.
	 * 
	 * @return production recipes
	 */
	public SagaRecipe[] getProductionRecipes() {
		return production;
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
	 */
	public String getEffect() {
		return effect;
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
