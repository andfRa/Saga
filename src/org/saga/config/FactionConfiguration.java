package org.saga.config;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.saga.Saga;
import org.saga.constants.IOConstants.WriteReadType;
import org.saga.factions.FactionDefinition;
import org.saga.utility.TwoPointFunction;
import org.saga.utility.WriterReader;

import com.google.gson.JsonParseException;

public class FactionConfiguration {


	/**
	 * Instance of the configuration.
	 */
	transient private static FactionConfiguration instance;
	
	/**
	 * Gets the instance.
	 * 
	 * @return instance
	 */
	public static FactionConfiguration config() {
		return instance;
	}
	
	
	/**
	 * Faction definition.
	 */
	public FactionDefinition factionDefinition;

	/**
	 * If true then only faction vs faction pvp is allowed.
	 */
	public Boolean factionOnlyPvp;
	
	/**
	 * Message when pvp is denied.
	 */
	public String pvpFactionOnlyMessage;

	/**
	 * Prefix name separator.
	 */
	public String prefixNameSeparator;

	/**
	 * Amount of members needed for the faction to form.
	 */
	public Integer formationAmount;
	
	/**
	 * Rank assigned to joined members.
	 */
	public String factionDefaultRank;

	/**
	 * Rank assigned to faction owner.
	 */
	public String factionOwnerRank;

	/**
	 * Faction levels per active players.
	 */
	public TwoPointFunction levelsPerActivePlayers;
	
	// Initialization:
	/**
	 * Used by gson.
	 */
	public FactionConfiguration() {
		
	}
	
	/**
	 * Goes trough all the fields and makes sure everything has been set after gson load.
	 * If not, it fills the field with defaults.
	 * 
	 * @return true if everything was correct.
	 */
	public boolean complete() {
		
		
		boolean integrity = true;
		
		if(factionOnlyPvp == null){
			Saga.severe(getClass(), "failed to initialize factionOnlyPvp field", "setting default");
			factionOnlyPvp = true;
			integrity=false;
		}
		
		if(pvpFactionOnlyMessage == null){
			Saga.severe(getClass(), "failed to initialize pvpFactionOnlyMessage field", "setting default");
			pvpFactionOnlyMessage = "Only factions can take part in pvp.";
			integrity=false;
		}
		
		if(prefixNameSeparator == null){
			Saga.severe(getClass(), "failed to initialize prefixNameSeparator field", "setting default");
			prefixNameSeparator = "-";
			integrity=false;
		}
		
		if(formationAmount == null){
			Saga.severe(getClass(), "formationAmount field failed to initialize", "setting default");
			formationAmount = 3;
			integrity=false;
		}
		
		if(factionDefinition == null){
			Saga.severe(getClass(), "factionDefinition field not initialized", "setting default");
			factionDefinition = FactionDefinition.defaultDefinition();
			integrity=false;
		}
		
		if(factionDefaultRank == null){
			Saga.severe(getClass(), "factionDefaultRank field not initialized", "setting default");
			factionDefaultRank = "novice";
			integrity=false;
		}
		
		if(factionOwnerRank == null){
			Saga.severe(getClass(), "factionDefaultRank field not initialized", "setting default");
			factionDefaultRank = "novice";
			integrity=false;
		}
		
		if(factionOwnerRank == null){
			Saga.severe(getClass(), "factionOwnerRank field not initialized", "setting default");
			factionOwnerRank = "grandmaster";
			integrity=false;
		}
		
		if(levelsPerActivePlayers == null){
			Saga.severe(getClass(), "levelsPerActivePlayers field not initialized", "setting default");
			levelsPerActivePlayers = new TwoPointFunction(0.0, (short) 50, 0.0);
			integrity=false;
		}
		
		return integrity;
		
		
	}

	
	
	
	// Load unload:
	/**
	 * Loads the configuration.
	 * 
	 * @return experience configuration
	 */
	public static FactionConfiguration load(){
		
		
		boolean integrityCheck = true;
		
		// Load:
		FactionConfiguration config;
		try {
			config = WriterReader.readFactionConfig();
		} catch (FileNotFoundException e) {
			Saga.severe(FactionConfiguration.class, "file not found", "loading defaults");
			config = new FactionConfiguration();
			integrityCheck = false;
		} catch (IOException e) {
			Saga.severe(FactionConfiguration.class, "failed to load", "loading defaults");
			config = new FactionConfiguration();
			integrityCheck = false;
		} catch (JsonParseException e) {
			Saga.severe(FactionConfiguration.class, "failed to parse", "loading defaults");
			Saga.info("Parse message :" + e.getMessage());
			config = new FactionConfiguration();
			integrityCheck = false;
		}
		
		// Integrity check and complete:
		integrityCheck = config.complete() && integrityCheck;
		
		// Write default if integrity check failed:
		if (!integrityCheck) {
			Saga.severe(FactionConfiguration.class, "integrity check failed", "writing default fixed version");
			try {
				WriterReader.writeFactionConfig(config, WriteReadType.CONFIG_DEFAULTS);
			} catch (IOException e) {
				Saga.severe(FactionConfiguration.class, "write failed", "ignoring write");
				Saga.info("Write fail cause:" + e.getClass().getSimpleName() + ":" + e.getMessage());
			}
		}
		
		// Set instance:
		instance = config;
		
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
