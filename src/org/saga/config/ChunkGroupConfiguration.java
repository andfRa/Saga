package org.saga.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collection;
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
	

	
	// Definitions:
	/**
	 * Settlement definition.
	 */
	private SettlementDefinition definition;
	
	/**
	 * Building definitions.
	 */
	private Hashtable<String, BuildingDefinition> buildingDefinitions;
	
	
	// Levels:
	/**
	 * Level when automatic delete is disabled.
	 */
	public Integer noDeleteLevel;
	

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
		

		if(definition == null){
			SagaLogger.nullField(getClass(), "settlementDefinition");
			definition = SettlementDefinition.defaultDefinition();
		}
		definition.complete();
		
		if(noDeleteLevel == null){
			SagaLogger.nullField(getClass(), "noDeleteLevel");
			noDeleteLevel = 5;
		}
		
		if(inactiveSetDays == null){
			SagaLogger.nullField(getClass(), "inactiveSetDays");
			inactiveSetDays = 1;
		}
		
		if(buildingDefinitions == null){
			SagaLogger.nullField(getClass(), "buildingDefinitions");
			buildingDefinitions = new Hashtable<String, BuildingDefinition>();
		}
		Collection<BuildingDefinition> definitions = buildingDefinitions.values();
		for (BuildingDefinition definition : definitions) {
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
		
		if(memberOnlyCommands == null){
			SagaLogger.nullField(getClass(), "memberOnlyCommands");
			memberOnlyCommands = new HashSet<String>();
		}
		if(memberOnlyCommands.remove(null)){
			SagaLogger.nullField(getClass(), "memberOnlyCommands");
		}
		
		
	}

	
	
	// Settlement:
	/**
	 * Gets level definition for the given settlement.
	 * 
	 * @return definition, zero definition if not found
	 */
	public SettlementDefinition getSettlementDefinition() {

		return definition;
		
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
	
	
	
	// Loading and unloading:
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
