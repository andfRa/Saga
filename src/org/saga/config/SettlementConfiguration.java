package org.saga.config;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.saga.SagaLogger;
import org.saga.buildings.Building;
import org.saga.buildings.BuildingDefinition;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.utility.TwoPointFunction;

public class SettlementConfiguration {


	/**
	 * Instance of the configuration.
	 */
	transient private static SettlementConfiguration instance;
	
	/**
	 * Gets the instance.
	 * 
	 * @return instance
	 */
	public static SettlementConfiguration config() {
		return instance;
	}
	

	
	// Definitions:
	/**
	 * Building definitions.
	 */
	private ArrayList<BuildingDefinition> buildingDefinitions;

	
	// Claims:
	/**
	 * Claims gained per minute.
	 */
	private TwoPointFunction claimsPerMinute;

	/**
	 * Maximum number of claims when a settlement is formed.
	 */
	private Integer initClaims;

	/**
	 * Maximum number of claims a settlement can have.
	 */
	private Integer maxClaims;
	
	/**
	 * No delete claims.
	 */
	private Integer noDeleteSize;
	

	// Claim improvements:
	/**
	 * The amount of building points for settlement size.
	 */
	private TwoPointFunction buildPoints;

	
	// Requirements:
	/**
	 * Active players.
	 */
	private TwoPointFunction requiredActiveMembers;

	
	// Hierarchy:
	/**
	 * Hierarchy level names.
	 */
	private Hashtable<Integer, String> hierarchyNames;

	/**
	 * Role assigned to joined members.
	 */
	private String defaultRole;
	
	
	// Time:
	/**
	 * Time in days when the player is set inactive.
	 */
	public Short inactiveSetDays;
	
	
	// Signs:
	/**
	 * Colour for enabled signs.
	 */
	public ChatColor enabledSignColor;
	
	/**
	 * Colour for disabled signs.
	 */
	public ChatColor disabledSignColor;
	
	/**
	 * Colour for invalid signs.
	 */
	public ChatColor invalidSignColor;
	
	
	// Formation:
	/**
	 * Amount of members needed for the chunk group to form.
	 */
	public Integer formationAmount;

	/**
	 * Minimum name length.
	 */
	private Integer minNameLength;

	/**
	 * Maximum name length.
	 */
	private Integer maxNameLength;
	
	
	
	// Commands:
	/**
	 * Member only commands.
	 */
	private HashSet<String> memberOnlyCommands;

	
	
	// Initialisation:
	/**
	 * Completes the configuration.
	 * 
	 * @return true if everything was correct.
	 */
	public void complete() {
		
		
		if(claimsPerMinute == null){
			SagaLogger.nullField(getClass(), "claimsPerMinute");
			claimsPerMinute = new TwoPointFunction(0.0);
		}
		claimsPerMinute.complete();
		
		if(initClaims == null){
			SagaLogger.nullField(getClass(), "initClaims");
			initClaims = 1;
		}

		if(maxClaims == null){
			SagaLogger.nullField(getClass(), "maxClaims");
			maxClaims = 15;
		}
		
		if(noDeleteSize == null){
			SagaLogger.nullField(getClass(), "noDeleteLevel");
			noDeleteSize = 25;
		}
		

		if(buildPoints == null){
			SagaLogger.nullField(this, "buildPoints");
			buildPoints = new TwoPointFunction(0.0);
		}
		
		
		if(requiredActiveMembers == null){
			SagaLogger.nullField(this, "requiredActiveMembers");
			requiredActiveMembers = new TwoPointFunction(5.0);
		}
		

		if(hierarchyNames == null){
			hierarchyNames = new Hashtable<Integer, String>();
			SagaLogger.nullField(this, "hierarchyNames");
		}
		
		if(defaultRole == null){
			SagaLogger.nullField(this,"defaultRole");
			defaultRole = "";
		}
		
		
		if(inactiveSetDays == null){
			SagaLogger.nullField(getClass(), "inactiveSetDays");
			inactiveSetDays = 1;
		}
		
		if(buildingDefinitions == null){
			SagaLogger.nullField(getClass(), "buildingDefinitions");
			buildingDefinitions = new ArrayList<BuildingDefinition>();
		}
		for (BuildingDefinition definition : buildingDefinitions) {
			definition.complete();
		}

		if(enabledSignColor == null){
			SagaLogger.nullField(getClass(), "enabledSignColor");
			enabledSignColor = ChatColor.DARK_GREEN;
		}
		
		if(disabledSignColor == null){
			SagaLogger.nullField(getClass(), "disabledSignColor");
			disabledSignColor = ChatColor.DARK_GRAY;
		}
		
		if(invalidSignColor == null){
			SagaLogger.nullField(getClass(), "invalidSignColor");
			invalidSignColor = ChatColor.DARK_RED;
		}
		
		if(formationAmount == null){
			SagaLogger.nullField(getClass(), "formationAmount");
			formationAmount = 2;
		}

		if(minNameLength == null){
			SagaLogger.nullField(getClass(), "minNameLength");
			minNameLength = 1;
		}
		
		if(maxNameLength == null){
			SagaLogger.nullField(getClass(), "maxNameLength");
			maxNameLength = 15;
		}
		
		if(memberOnlyCommands == null){
			SagaLogger.nullField(getClass(), "memberOnlyCommands");
			memberOnlyCommands = new HashSet<String>();
		}
		if(memberOnlyCommands.remove(null)){
			SagaLogger.nullField(getClass(), "memberOnlyCommands");
		}
		
		
	}

	
	
	// Creation:
	/**
	 * Gets the minimum name length.
	 * 
	 * @return minimum name length
	 */
	public Integer getMinNameLength() {
		return minNameLength;
	}
	
	/**
	 * Gets the maximum name length.
	 * 
	 * @return maximum name length
	 */
	public Integer getMaxNameLength() {
		return maxNameLength;
	}
	
	
	
	// Buildings:
	/**
	 * Gets a new building with the given name.
	 * 
	 * @param name building name
	 * @return building with the given name
	 * @throws InvalidBuildingException if building definition is missing
	 */
	public Building createBuilding(String name) throws InvalidBuildingException {

		
		BuildingDefinition definition = SettlementConfiguration.config().getBuildingDefinition(name);
		
		if(definition == null){
			
			throw new InvalidBuildingException(name, "missing definition");
			
		}
		
		try {
			
			Class<?> cl = Class.forName(definition.getBuildingClass());
			Class<? extends Building> clca = cl.asSubclass(Building.class);
			Constructor<? extends Building> co = clca.getConstructor(BuildingDefinition.class);
			return co.newInstance(definition);
			
		} catch (Throwable e) {
			
			throw new InvalidBuildingException(name, e.getClass().getSimpleName() + ":" + e.getMessage());

		}
		
		
	}
	
	/**
	 * Gets definition for the given building.
	 * 
	 * @param name building name
	 * @return building definition, null if not found
	 */
	public BuildingDefinition getBuildingDefinition(String name) {

		for (BuildingDefinition definition : buildingDefinitions) {
			if(definition.getName().equals(name)) return definition;
		}
		
		return null;
		
	}
	
	/**
	 * Gets definition for the given building.
	 * 
	 * @return building definitions
	 */
	public ArrayList<BuildingDefinition> getBuildingDefinitions() {

		return new ArrayList<BuildingDefinition>(buildingDefinitions);
		
	}
	
	
	/**
	 * Gets all building names.
	 * 
	 * @return buildings names
	 */
	public Collection<String> getBuildingNames() {
		
		HashSet<String> names = new HashSet<String>();
		
		for (BuildingDefinition definition : buildingDefinitions) {
			names.add(definition.getName());
		}
		
		return names;
		
	}
	
	
	/**
	 * Gets building max score.
	 * 
	 * @return building max score
	 */
	public Integer getMaxBldgScore() {

		int maxScore = 0;
		
		for (BuildingDefinition bldgDef : buildingDefinitions) {
			
			if(bldgDef.getMaxScore() > maxScore) maxScore = bldgDef.getMaxScore();
			
		}
		
		return maxScore;
		
		
	}
	
	
	
	// Commands:
	/**
	 * Checks if the command is member only.
	 * 
	 * @param command command
	 * @return true if member only
	 */
	public boolean checkMemberOnlyCommand(String command) {
		
		return new HashSet<String>(memberOnlyCommands).contains(command);
		
	}
	
	
	
	// Claims:
	/**
	 * Gets claims per minute.
	 * 
	 * @param onlinePlayers players online
	 * @return amount of claim points per minute
	 */
	public Double getClaimsPerMinute(Integer onlinePlayers) {
		return claimsPerMinute.value(onlinePlayers);
	}
	
	/**
	 * Gets the initial amount of claims the faction can have.
	 * 
	 * @return initial claims
	 */
	public Integer getInitialClaims() {
		return initClaims;
	}
	
	/**
	 * Gets the maximum amount of claims the faction can have.
	 * 
	 * @return max claims
	 */
	public Integer getMaxClaims() {
		return maxClaims;
	}

	/**
	 * Gets the settlement size for which the settlement can't be deleted.
	 * 
	 * @return no delete size
	 */
	public Integer getNoDeleteSize() {
		return noDeleteSize;
	}
	
	
	
	// Claim improvements:
	/**
	 * Get building points.
	 * 
	 * @param level level
	 * @return building points
	 */
	public Integer getBuildPoints(Integer level) {
		return buildPoints.value(level).intValue();
	}
	
	
	
	// Requirements:
	/**
	 * Gets the amount of active 
	 * 
	 * @param size settlement size
	 * @return active members required
	 */
	public Integer getRequiredActiveMembers(Integer size) {
		return requiredActiveMembers.intValue(size);
	}
	
	
	
	// Roles:
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
		Set<Integer> roleHierarchy = hierarchyNames.keySet();
		
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
		Set<Integer> roleHierarchy = hierarchyNames.keySet();
		
		for (Integer hierarchy : roleHierarchy) {
			if(hierarchy < minHierarchy || minHierarchy == -1) minHierarchy = hierarchy;
		}
		
		return minHierarchy;
		
	}
	
	/**
	 * Gets the default role.
	 * 
	 * @return default role
	 */
	public String getDefaultRole() {
		return defaultRole;
	}
	
	
	
	// Loading and unloading:
	/**
	 * Loads configuration.
	 * 
	 * @return configuration
	 */
	public static SettlementConfiguration load(){


		// Create config:
		if(!WriterReader.checkExists(Directory.SETTLEMENT_CONFIG)){

			try {
				WriterReader.unpackConfig(Directory.SETTLEMENT_CONFIG);
			}
			catch (IOException e) {
				SagaLogger.severe(SettlementConfiguration.class, "failed to create default configuration: " + e.getClass().getSimpleName());
			}
			
		}
		
		SettlementConfiguration config;
		try {
			
			config = WriterReader.read(Directory.SETTLEMENT_CONFIG, SettlementConfiguration.class);
			
		} catch (IOException e) {
			
			SagaLogger.severe(SettlementConfiguration.class, "failed to read configuration: " + e.getClass().getSimpleName());
			config = new SettlementConfiguration();
			
		} catch (JsonParseException e) {

			SagaLogger.severe(SettlementConfiguration.class, "failed to parse configuration: " + e.getClass().getSimpleName());
			SagaLogger.info("message: " + e.getMessage());
			config = new SettlementConfiguration();
			
		}
		
		// Set instance:
		instance = config;
		
		config.complete();
		
		return config;
		
		
	}
	
	/**
	 * Unloads the instance.
	 * 
	 */
	public static void unload(){
		instance = null;
	}
	
	
}
