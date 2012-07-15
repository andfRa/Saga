package org.saga.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.saga.SagaLogger;
import org.saga.buildings.Building;
import org.saga.buildings.BuildingDefinition;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.settlements.SettlementDefinition;

import com.google.gson.JsonParseException;

public class ChunkGroupConfiguration {


	/**
	 * Instance of the configuration.
	 */
	transient private static ChunkGroupConfiguration instance;
	
	/**
	 * Gets the instance.
	 * 
	 * @return instance
	 */
	public static ChunkGroupConfiguration config() {
		return instance;
	}
	
	

	
	// Leveling:
	/**
	 * Settlement definition.
	 */
	private SettlementDefinition settlementDefinition;
	
	/**
	 * Level when automatic delete is disabled.
	 */
	public Integer noDeleteLevel;

	
	

	// Time:
	/**
	 * Time in days when the player is set inactive.
	 */
	public Short inactiveSetDays;
	
	
	
	// Buildings:
	/**
	 * Building definitions.
	 */
	private Hashtable<String, BuildingDefinition> buildingDefinitions;
	
	/**
	 * Sign for arena top.
	 */
	public String arenaTopSign;
	
	/**
	 * Sign for arena top.
	 */
	public String arenaCountdownSign;
	
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
	
	
	
	// Commands:
	/**
	 * Member only commands.
	 */
	private HashSet<String> memberOnlyCommands;

	
	
	// Initialisation:
	/**
	 * Fixes all fields.
	 * 
	 * @return true if everything was correct.
	 */
	public boolean complete() {
		
		
		boolean integrity=true;
		String config = "chunk group configuration";

		if(settlementDefinition == null){
			SagaLogger.severe("Failed to initialize settlementDefinitions field for " + config + ". Setting default.");
			settlementDefinition = SettlementDefinition.defaultDefinition();
			integrity=false;
		}
		integrity = settlementDefinition.complete() && integrity;
		
		if(noDeleteLevel == null){
			SagaLogger.severe(getClass(), "noDeleteLevel field failed to initialize");
			noDeleteLevel = 5;
			integrity=false;
		}
		
		if(inactiveSetDays == null){
			SagaLogger.severe(config + " failed to initialize inactiveSetDays field. Setting default.");
			inactiveSetDays = 1;
			integrity=false;
		}
		
		if(buildingDefinitions == null){
			SagaLogger.severe(config + " failed to initialize buildingDefinitions field. Adding two examples.");
			buildingDefinitions = new Hashtable<String, BuildingDefinition>();
			integrity=false;
		}
		Enumeration<String> buildingNames = buildingDefinitions.keys();
		while (buildingNames.hasMoreElements()) {
			String building = buildingNames.nextElement();
			BuildingDefinition definition = buildingDefinitions.get(building);
			if(definition == null){
				SagaLogger.severe(config + " failed to initialize buildingDefinitions element. Removing element.");
				buildingDefinitions.remove(building);
				integrity=false;
				continue;
			}
			integrity = definition.complete() && integrity;
		}

		if(arenaTopSign == null){
			SagaLogger.severe(this.getClass(), "failed to initialize arenaTopSign field");
			arenaTopSign= ChatColor.AQUA + "=[TOP]=";
			integrity=false;
		}
		
		if(arenaCountdownSign == null){
			SagaLogger.severe(this.getClass(), "failed to initialize arenaCountdownSign field");
			arenaCountdownSign= ChatColor.AQUA + "=[COUNT]=";
			integrity=false;
		}
		
		if(enabledSignColor == null){
			SagaLogger.severe(this.getClass(), "failed to initialize enabledSignColor field");
			enabledSignColor = ChatColor.DARK_GREEN;
			integrity=false;
		}
		
		if(disabledSignColor == null){
			SagaLogger.severe(this.getClass(), "failed to initialize disabledSignColor field");
			disabledSignColor = ChatColor.DARK_GRAY;
			integrity=false;
		}
		
		if(invalidSignColor == null){
			SagaLogger.severe(this.getClass(), "failed to initialize invalidSignColor field");
			invalidSignColor = ChatColor.DARK_RED;
			integrity=false;
		}
		
		if(formationAmount == null){
			SagaLogger.severe(this.getClass(), "formationAmount field failed to initialize");
			formationAmount = 2;
			integrity=false;
		}
		
		if(memberOnlyCommands == null){
			SagaLogger.severe(this.getClass(), "memberOnlyCommands field failed to initialize");
			memberOnlyCommands = new HashSet<String>();
			integrity=false;
		}
		if(memberOnlyCommands.remove(null)){
			SagaLogger.severe(this.getClass(), "memberOnlyCommands field element failed to initialize");
			integrity=false;
		}
		
		return integrity;
		
		
	}

	
	
	// Settlement:
	/**
	 * Gets level definition for the given settlement.
	 * 
	 * @return definition, zero definition if not found
	 */
	public SettlementDefinition getSettlementDefinition() {

		return settlementDefinition;
		
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

		
		BuildingDefinition definition = ChunkGroupConfiguration.config().getBuildingDefinition(name);
		
		if(definition == null){
			
			throw new InvalidBuildingException(name, "missing definition");
			
		}
		
		try {
			
			Class<?> cl = Class.forName(definition.getClassName());
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

		return buildingDefinitions.get(name);
		
	}
	
	/**
	 * Gets all building names.
	 * 
	 * @return buildings names
	 */
	public Collection<String> getBuildingNames() {
		
		return buildingDefinitions.keySet();
		
	}
	
	
	/**
	 * Checks if the command is member only.
	 * 
	 * @param command command
	 * @return true if member only
	 */
	public boolean checkMemberOnlyCommand(String command) {
		
		return new HashSet<String>(memberOnlyCommands).contains(command);
		
	}
	
	
	
	// Load unload:
	/**
	 * Loads configuration.
	 * 
	 * @return configuration
	 */
	public static ChunkGroupConfiguration load(){

		
		ChunkGroupConfiguration config;
		try {
			
			config = WriterReader.read(Directory.CHUNKGROUP_CONFIG, ChunkGroupConfiguration.class);
			
		} catch (FileNotFoundException e) {
			
			SagaLogger.severe(BalanceConfiguration.class, "configuration not found");
			config = new ChunkGroupConfiguration();
			
		} catch (IOException e) {
			
			SagaLogger.severe(ChunkGroupConfiguration.class, "failed to read configuration: " + e.getClass().getSimpleName());
			config = new ChunkGroupConfiguration();
			
		} catch (JsonParseException e) {

			SagaLogger.severe(ChunkGroupConfiguration.class, "failed to parse configuration: " + e.getClass().getSimpleName());
			SagaLogger.info("message: " + e.getMessage());
			config = new ChunkGroupConfiguration();
			
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
