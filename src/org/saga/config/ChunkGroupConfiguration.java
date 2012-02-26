package org.saga.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.saga.Saga;
import org.saga.buildings.Building;
import org.saga.buildings.BuildingDefinition;
import org.saga.buildings.MissingBuildingDefinitionException;
import org.saga.constants.IOConstants.WriteReadType;
import org.saga.settlements.SettlementDefinition;
import org.saga.utility.TwoPointFunction;
import org.saga.utility.WriterReader;

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
	
	
	// Constants:
	/**
	 * No role representation.
	 */
	public static String VOID_PROFICIENCY = "void";
	
	/**
	 * Used to indicate that all professions can be set roles.
	 */
	public static String ALL_PROFICIENCIES = "all";
	
	
	// General:
	/**
	 * Maximum level for buildings.
	 */
	public Short settlementMaximumLevel;
	
	/**
	 * Maximum level for structures.
	 */
	public Short structureMaximumLevel;
	
	
	// Settlement claim:
	/**
	 * Settlement claims.
	 */
	private TwoPointFunction settlementClaims;


	// Leveling:
	/**
	 * Settlement definition.
	 */
	private SettlementDefinition settlementDefinition;
	
	/**
	 * Level when automatic delete is disabled.
	 */
	public Integer noDeleteLevel;
	
	
	// Proficiencies:
	/**
	 * Role assigned to settlement owner.
	 */
	public String settlementOwnerRole;
	
	/**
	 * Role assigned to joined members.
	 */
	public String settlementDefaultRole;

	/**
	 * The amount of top proficiencies.
	 */
	private TwoPointFunction topProfieciencyAmount;
	
	/**
	 * The scaling of lower proficiencies.
	 */
	private TwoPointFunction lowerProfieciencyScaling;
	
	
	// Time:
	/**
	 * Time in days when the player is set inactive.
	 */
	public Short inactiveSetDays;
	
	
	// Buildings:
	/**
	 * Buildings.
	 */
	private ArrayList<Building> buildings;

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
	 * Color for enabled signs.
	 */
	public ChatColor enabledSignColor;
	
	/**
	 * Color for disabled signs.
	 */
	public ChatColor disabledSignColor;
	
	/**
	 * Color for invalid signs.
	 */
	public ChatColor invalidSignColor;

	
	// Formation:
	/**
	 * Amount of members needed for the chunnk group to form.
	 */
	public Integer formationAmount;
	
	
	// Commands:
	/**
	 * Member only commands.
	 */
	private HashSet<String> memberOnlyCommands;

	
	// Initialization:
	/**
	 * Used by gson.
	 */
	public ChunkGroupConfiguration() {
		
	}
	
	/**
	 * Goes trough all the fields and makes sure everything has been set after gson load.
	 * If not, it fills the field with defaults.
	 * 
	 * @return true if everything was correct.
	 */
	public boolean complete() {
		
		
		boolean integrity=true;
		String config = "chunk group configuration";

		// General:
		if(settlementMaximumLevel == null){
			Saga.severe("Failed to initialize settlementMaximumLevel field for " + config + ". Setting default.");
			settlementMaximumLevel= 1;
			integrity=false;
		}
		if(structureMaximumLevel == null){
			Saga.severe("Failed to initialize structureMaximumLevel field for " + config + ". Setting default.");
			structureMaximumLevel= 1;
			integrity=false;
		}
		
		// Settlement claim:
		if(settlementClaims == null){
			Saga.severe("Failed to initialize settlementClaims field for " + config + ". Setting default.");
			settlementClaims = new TwoPointFunction(1.0);
			integrity=false;
		}
		integrity = settlementClaims.complete() && integrity;
		
		// Proficiencies:
		if(topProfieciencyAmount == null){
			Saga.severe("Failed to initialize topProfieciencyAmount field for " + config + ". Setting default.");
			topProfieciencyAmount = new TwoPointFunction(1.0);
			integrity=false;
		}
		integrity = topProfieciencyAmount.complete() && integrity;
		
		if(lowerProfieciencyScaling == null){
			Saga.severe("Failed to initialize lowerProfieciencyScaling field for " + config + ". Setting default.");
			lowerProfieciencyScaling = new TwoPointFunction(1.0);
			integrity=false;
		}
		integrity = lowerProfieciencyScaling.complete() && integrity;
		
		// Roles:
		if(settlementOwnerRole == null){
			Saga.severe("Failed to initialize settlementOwnerRole field for " + config + ". Setting default.");
			settlementOwnerRole = VOID_PROFICIENCY;
			integrity=false;
		}
		if(settlementDefaultRole == null){
			Saga.severe("Failed to initialize settlementDefaultRole field for " + config + ". Setting default.");
			settlementDefaultRole = VOID_PROFICIENCY;
			integrity=false;
		}
		
		// Leveling:
		if(settlementDefinition == null){
			Saga.severe("Failed to initialize settlementDefinitions field for " + config + ". Setting default.");
			settlementDefinition = SettlementDefinition.defaultDefinition();
			integrity=false;
		}
		integrity = settlementDefinition.complete() && integrity;
		
		if(noDeleteLevel == null){
			Saga.severe(getClass(), "noDeleteLevel field failed to initialize", "setting default");
			noDeleteLevel = 5;
			integrity=false;
		}
		
		// Time:
		if(inactiveSetDays == null){
			Saga.severe(config + " failed to initialize inactiveSetDays field. Setting default.");
			inactiveSetDays = 1;
			integrity=false;
		}
		
		// Buildings:
		if(buildingDefinitions == null){
			Saga.severe(config + " failed to initialize buildingDefinitions field. Adding two examples.");
			buildingDefinitions = new Hashtable<String, BuildingDefinition>();
			buildingDefinitions.put("first building", BuildingDefinition.zeroDefinition());
			buildingDefinitions.put("second building", BuildingDefinition.zeroDefinition());
			integrity=false;
		}
		Enumeration<String> buildingNames = buildingDefinitions.keys();
		while (buildingNames.hasMoreElements()) {
			String building = buildingNames.nextElement();
			BuildingDefinition definition = buildingDefinitions.get(building);
			if(definition == null){
				Saga.severe(config + " failed to initialize buildingDefinitions element. Removing element.");
				buildingDefinitions.remove(building);
				integrity=false;
				continue;
			}
			integrity = definition.complete() && integrity;
		}

		if(buildings == null){
			Saga.severe(config + " failed to initialize buildings field. setting default");
			buildings = new ArrayList<Building>();
			integrity=false;
		}
		for (int i = 0; i < buildings.size(); i++) {
			if(buildings.get(i) == null){
				Saga.severe(config + " failed to initialize buildings field element. Removing element.");
				buildings.remove(i);
				i--;
				integrity=false;
				continue;
			}
			try {
				integrity = buildings.get(i).complete() && integrity;
			} catch (MissingBuildingDefinitionException e) {
				Saga.severe(config + " failed to complete buildings field element: "+ e.getClass().getSimpleName() + ":" + e.getMessage() + ". Removing element.");
				buildings.remove(i);
				i--;
				integrity=false;
				continue;
			}
			
			
		}
		
		if(arenaTopSign == null){
			Saga.severe(this.getClass(), "failed to initialize arenaTopSign field", "setting default");
			arenaTopSign= ChatColor.AQUA + "=[TOP]=";
			integrity=false;
		}
		
		if(arenaCountdownSign == null){
			Saga.severe(this.getClass(), "failed to initialize arenaCountdownSign field", "setting default");
			arenaCountdownSign= ChatColor.AQUA + "=[COUNT]=";
			integrity=false;
		}
		
		if(enabledSignColor == null){
			Saga.severe(this.getClass(), "failed to initialize enabledSignColor field", "setting default");
			enabledSignColor = ChatColor.DARK_GREEN;
			integrity=false;
		}
		
		if(disabledSignColor == null){
			Saga.severe(this.getClass(), "failed to initialize disabledSignColor field", "setting default");
			disabledSignColor = ChatColor.DARK_GRAY;
			integrity=false;
		}
		
		if(invalidSignColor == null){
			Saga.severe(this.getClass(), "failed to initialize invalidSignColor field", "setting default");
			invalidSignColor = ChatColor.DARK_RED;
			integrity=false;
		}
		
		if(formationAmount == null){
			Saga.severe(this.getClass(), "formationAmount field failed to initialize", "setting default");
			formationAmount = 2;
			integrity=false;
		}
		
		if(memberOnlyCommands == null){
			Saga.severe(this.getClass(), "memberOnlyCommands field failed to initialize", "setting default");
			memberOnlyCommands = new HashSet<String>();
			integrity=false;
		}
		if(memberOnlyCommands.remove(null)){
			Saga.severe(this.getClass(), "memberOnlyCommands field element failed to initialize", "removing element");
			integrity=false;
		}
		
		return integrity;
		
		
	}

	
	// Calculation:
	/**
	 * Calculates claim points the settlement has.
	 * 
	 * @param level level
	 */
	public Double calculateSettlementClaims(Integer level) {

		return settlementClaims.value(level);
		
	}

	/**
	 * Calculates the amount of proficiencies.
	 * 
	 * @param buildingLevel building level
	 * @return top roles amount
	 */
	public Short calculateTopProfieciencyAmount(Short buildingLevel) {
		return topProfieciencyAmount.value(buildingLevel.shortValue()).shortValue();
	}
	
	/**
	 * Calculates the scaling of lower proficiencies.
	 * 
	 * @param profLevel role level
	 * @return lower roles amount
	 */
	public Double calculateLowerProfieciencyScaling(Integer profLevel) {
		return lowerProfieciencyScaling.value(profLevel.shortValue());
	}
	
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
	 * @return building with the given name, null if not found
	 * @throws MissingBuildingDefinitionException if building definition is missing
	 */
	public Building newBuilding(String name) throws MissingBuildingDefinitionException {

		for (int i = 0; i < buildings.size(); i++) {
			if(!buildings.get(i).getName().equalsIgnoreCase(name) && !buildings.get(i).getName().replaceAll(" ", "").equalsIgnoreCase(name)) continue;
			 Building building = buildings.get(i).blueprint();
			 building.complete();
			 return building;
		}
		return null;
		
	}
	
	/**
	 * Gets level definition for the given building.
	 * 
	 * @param name name
	 * @return definition, null if not found
	 */
	public BuildingDefinition getBuildingDefinition(String name) {

		return buildingDefinitions.get(name);
		
	}
	
	/**
	 * Gets all buildings.
	 * 
	 * @return buildings
	 */
	public ArrayList<Building> getBuildings() {
		return new ArrayList<Building>(buildings);
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
	 * Loads the configuration.
	 * 
	 * @return experience configuration
	 */
	public static ChunkGroupConfiguration load(){
		
		
		boolean integrityCheck = true;
		
		// Load:
		String configName = "chunk group configuration";
		ChunkGroupConfiguration config;
		try {
			config = WriterReader.readChunkGroupConfig();
		} catch (FileNotFoundException e) {
			Saga.severe("Missing " + configName + ". Loading defaults.");
			config = new ChunkGroupConfiguration();
			integrityCheck = false;
		} catch (IOException e) {
			Saga.severe("Failed to load " + configName + ". Loading defaults.");
			config = new ChunkGroupConfiguration();
			integrityCheck = false;
		} catch (JsonParseException e) {
			Saga.severe("Failed to parse " + configName + ". Loading defaults.");
			Saga.info("Parse message :" + e.getMessage());
			config = new ChunkGroupConfiguration();
			integrityCheck = false;
		}

		// Set instance:
		instance = config;
		
		// Integrity check and complete:
		integrityCheck = config.complete() && integrityCheck;
		
		// Write default if integrity check failed:
		if (!integrityCheck) {
			Saga.severe("Integrity check failed for " + configName);
			Saga.info("Writing " + configName + " with fixed default values. Edit and rename to use it.");
			try {
				WriterReader.writeChunkGroupConfig(config, WriteReadType.CONFIG_DEFAULTS);
			} catch (IOException e) {
				Saga.severe(configName + " write failure. Ignoring write.");
			}
		}
		
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
