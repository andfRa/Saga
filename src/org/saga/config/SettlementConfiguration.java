package org.saga.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.saga.SagaLogger;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.settlements.Settlement;
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
	

	
	// Claim points:
	/**
	 * Claims gained per minute.
	 */
	private TwoPointFunction claimsPerMinute;

	/**
	 * Number of claims when a settlement is formed.
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
	
	
	// Build points:
	/**
	 * Build points gained per minute.
	 */
	private TwoPointFunction buildPointsPerMinute;

	/**
	 * Number of build points when a settlement is formed.
	 */
	private Integer initBuildPoints;

	/**
	 * Maximum number of build points a settlement can have.
	 */
	private Integer maxBuildPoints;
	
	

	// Claim improvements:
	/**
	 * The amount of building points for settlement size.
	 */
	private TwoPointFunction buildPoints; // TODO Remove unused build points per claims function.

	
	
	// Requirements:
	/**
	 * Active players.
	 */
	private TwoPointFunction requiredActiveMembers;

	/**
	 * Required buildings.
	 */
	private Hashtable<String, Integer> requiredBuildings;

	
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
		
		
		if(buildPointsPerMinute == null){
			SagaLogger.nullField(getClass(), "buildPointsPerMinute");
			buildPointsPerMinute = new TwoPointFunction(0.0);
		}
		buildPointsPerMinute.complete();
		
		if(initBuildPoints == null){
			SagaLogger.nullField(getClass(), "initBuildPoints");
			initBuildPoints = 1;
		}

		if(maxBuildPoints == null){
			SagaLogger.nullField(getClass(), "maxBuildPoints");
			maxBuildPoints = 15;
		}

		
		if(buildPoints == null){
			SagaLogger.nullField(this, "buildPoints");
			buildPoints = new TwoPointFunction(0.0);
		}
		
		if(requiredActiveMembers == null){
			SagaLogger.nullField(this, "requiredActiveMembers");
			requiredActiveMembers = new TwoPointFunction(5.0);
		}

		if(requiredBuildings == null){
			SagaLogger.nullField(this, "requiredBuildings");
			requiredBuildings = new Hashtable<String, Integer>();
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
	 * Gets the initial amount of claims the settlement can have.
	 * 
	 * @return initial claims
	 */
	public Integer getInitialClaims() {
		return initClaims;
	}
	
	/**
	 * Gets the maximum amount of claims the settlement can have.
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
	
	
	
	// Build points:
	/**
	 * Gets build points per minute.
	 * 
	 * @param onlinePlayers players online
	 * @return amount of build points per minute
	 */
	public Double getBuildPointsPerMinute(Integer onlinePlayers) {
		return buildPointsPerMinute.value(onlinePlayers);
	}
	
	/**
	 * Gets the initial amount of build points the settlement can have.
	 * 
	 * @return initial build points
	 */
	public Integer getInitialBuildPoints() {
		return initBuildPoints;
	}
	
	/**
	 * Gets the maximum amount of build points the settlement can have.
	 * 
	 * @return max build points
	 */
	public Integer getMaxBuildPoints() {
		return maxBuildPoints;
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
	
	/**
	 * Gets building requirements.
	 * 
	 * @return building requirements
	 */
	public Set<Entry<String, Integer>> getBuildingRequirements() {
		return requiredBuildings.entrySet();
	}
	
	/**
	 * Gets sorted requirements.
	 * 
	 * @return building requirements
	 */
	public List<Entry<String, Integer>> getSortedBuildingRequirements() {
		
		List<Entry<String, Integer>> bldgReq = new ArrayList<Entry<String,Integer>>(requiredBuildings.entrySet());

		// Sort by claims:
		Comparator<Entry<String, Integer>> comp = new Comparator<Entry<String,Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o1.getValue() - o2.getValue();
			}
		};
		Collections.sort(bldgReq, comp);
		
		return bldgReq;
		
	}
	

	
	/**
	 * Check if the building requirements are met.
	 * 
	 * @param settlement settlement
	 * @return true if building requirements are met
	 */
	public boolean checkBuildingRequirements(Settlement settlement) {

		int totalClaims = settlement.getTotalClaims();
		Set<Entry<String, Integer>> requirments = requiredBuildings.entrySet();
		
		for (Entry<String, Integer> requirement : requirments) {
			
			String bldgName = requirement.getKey();
			Integer bldgClaims = requirement.getValue();
			
			if(totalClaims >= bldgClaims && settlement.getFirstBuilding(bldgName) == null) return false;
			
		}
		
		return true;
		
	}

	/**
	 * Gets building requirements.
	 * 
	 * @param settlement settlement
	 * @return required buildings
	 */
	public ArrayList<String> getSortedRequiredBuildings(Settlement settlement) {
		
		Integer totalClaims = settlement.getTotalClaims();
		List<Entry<String, Integer>> requirements = getSortedBuildingRequirements();
		ArrayList<String> required = new ArrayList<String>();
		
		for (Entry<String, Integer> requirement : requirements) {
			if(totalClaims >= requirement.getValue()){
				required.add(requirement.getKey());
			}
		}
		
		return required;
		
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
		
		// Read config:
		SettlementConfiguration config;
		try {
			
			config = WriterReader.readConfig(Directory.SETTLEMENT_CONFIG, SettlementConfiguration.class);
			
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
