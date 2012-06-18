package org.saga.config;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.saga.SagaLogger;
import org.saga.factions.FactionDefinition;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.utility.TwoPointFunction;

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
			SagaLogger.severe(getClass(), "failed to initialize factionOnlyPvp field");
			factionOnlyPvp = true;
			integrity=false;
		}
		
		if(pvpFactionOnlyMessage == null){
			SagaLogger.severe(getClass(), "failed to initialize pvpFactionOnlyMessage field");
			pvpFactionOnlyMessage = "Only factions can take part in pvp.";
			integrity=false;
		}
		
		if(prefixNameSeparator == null){
			SagaLogger.severe(getClass(), "failed to initialize prefixNameSeparator field");
			prefixNameSeparator = "-";
			integrity=false;
		}
		
		if(formationAmount == null){
			SagaLogger.severe(getClass(), "formationAmount field failed to initialize");
			formationAmount = 3;
			integrity=false;
		}
		
		if(factionDefinition == null){
			SagaLogger.severe(getClass(), "factionDefinition field not initialized");
			factionDefinition = FactionDefinition.defaultDefinition();
			integrity=false;
		}
		
		if(factionDefaultRank == null){
			SagaLogger.severe(getClass(), "factionDefaultRank field not initialized");
			factionDefaultRank = "novice";
			integrity=false;
		}
		
		if(factionOwnerRank == null){
			SagaLogger.severe(getClass(), "factionDefaultRank field not initialized");
			factionDefaultRank = "novice";
			integrity=false;
		}
		
		if(factionOwnerRank == null){
			SagaLogger.severe(getClass(), "factionOwnerRank field not initialized");
			factionOwnerRank = "grandmaster";
			integrity=false;
		}
		
		if(levelsPerActivePlayers == null){
			SagaLogger.severe(getClass(), "levelsPerActivePlayers field not initialized");
			levelsPerActivePlayers = new TwoPointFunction(1.0);
			integrity=false;
		}
		
		return integrity;
		
		
	}

	
	
	
	// Load unload:
	/**
	 * Loads configuration.
	 * 
	 * @return configuration
	 */
	public static FactionConfiguration load(){

		
		FactionConfiguration config;
		try {
			
			config = WriterReader.read(Directory.FACTION_CONFIG, FactionConfiguration.class);
			
		} catch (FileNotFoundException e) {
			
			SagaLogger.severe(BalanceConfiguration.class, "configuration not found");
			config = new FactionConfiguration();
			
		} catch (IOException e) {
			
			SagaLogger.severe(ChunkGroupConfiguration.class, "failed to read configuration: " + e.getClass().getSimpleName());
			config = new FactionConfiguration();
			
		} catch (JsonParseException e) {

			SagaLogger.severe(ChunkGroupConfiguration.class, "failed to parse configuration: " + e.getClass().getSimpleName());
			SagaLogger.info("message: " + e.getMessage());
			config = new FactionConfiguration();
			
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
