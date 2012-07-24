package org.saga.config;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.saga.SagaLogger;
import org.saga.factions.FactionDefinition;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;

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
	public FactionDefinition definition;

	/**
	 * If true then only faction vs faction pvp is allowed.
	 */
	public Boolean factionOnlyPvp;
	
	/**
	 * Prefix name separator.
	 */
	public String prefixNameSeparator;

	/**
	 * Amount of members needed for the faction to form.
	 */
	public Integer formationAmount;
	
	
	
	// Initialisation:
	/**
	 * Fixes all problematic fields.
	 * 
	 */
	public void complete() {
		
		
		if(definition == null){
			SagaLogger.nullField(getClass(), "definition");
			definition = FactionDefinition.defaultDefinition();
		}
		definition.complete();
		
		if(factionOnlyPvp == null){
			SagaLogger.nullField(getClass(), "factionOnlyPvp");
			factionOnlyPvp = true;
		}
		
		if(prefixNameSeparator == null){
			SagaLogger.nullField(getClass(), "prefixNameSeparator");
			prefixNameSeparator = "-";
		}
		
		if(formationAmount == null){
			SagaLogger.nullField(getClass(), "formationAmount");
			formationAmount = 3;
		}
		
		if(definition == null){
			SagaLogger.nullField(getClass(), "definition");
			definition = FactionDefinition.defaultDefinition();
		}
		
		
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
			
			SagaLogger.severe(SettlementConfiguration.class, "failed to read configuration: " + e.getClass().getSimpleName());
			config = new FactionConfiguration();
			
		} catch (JsonParseException e) {

			SagaLogger.severe(SettlementConfiguration.class, "failed to parse configuration: " + e.getClass().getSimpleName());
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
