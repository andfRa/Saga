package org.saga.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

import org.saga.Saga;
import org.saga.constants.IOConstants.WriteReadType;
import org.saga.utility.TwoPointFunction;
import org.saga.utility.WriterReader;

import com.google.gson.JsonParseException;

public class GuildConfiguration {
	

	/**
	 * Instance of the configuration.
	 */
	transient private static GuildConfiguration instance;
	
	/**
	 * Gets the instance.
	 * 
	 * @return instance
	 */
	public static GuildConfiguration config() {
		return instance;
	}
	
	
	// Proficiency Information.
	/**
	 * Pays for killed levels.
	 */
	private TwoPointFunction levelPay;

	/**
	 * Pay limit.
	 */
	public Double payLimit;
	
	/**
	 * Pay days.
	 */
	public Integer payDays;
	
	
	/**
	 * Guilds.
	 */
	private Hashtable<String, String> guilds;
	
	/**
	 * Base world.
	 */
	public String baseWorld;
	
	
	// Initialization:
	/**
	 * Used by gson.
	 * 
	 */
	public GuildConfiguration() {
	}
	
	/**
	 * Goes trough all the fields and makes sure everything has been set after gson load.
	 * If not, it fills the field with defaults.
	 * Abilities are completed separately.
	 * 
	 * @return true if everything was correct.
	 */
	private boolean complete() {
		
		
		boolean integrity=true;

		if(levelPay == null){
			levelPay = new TwoPointFunction(0.0, 0.0);
			Saga.severe(getClass(), "levelPay field failed to initalize", "setting default");
			integrity=false;
		}
		
		if(payLimit == null){
			payLimit = 10.0;
			Saga.severe(getClass(), "payLimit field failed to initalize", "setting default");
			integrity=false;
		}
		
		if(payDays == null){
			payDays = 7;
			Saga.severe(getClass(), "payDays field failed to initalize", "setting default");
			integrity=false;
		}
		
		if(guilds == null){
			guilds = new Hashtable<String, String>();
			Saga.severe(getClass(), "guilds field failed to initalize", "setting default");
			integrity=false;
		}
			
		if(baseWorld == null){
			baseWorld = "world";
			Saga.severe(getClass(), "baseWorld field failed to initalize", "setting default");
			integrity=false;
		}
		
		
		return integrity;
		
		
	}

	
	// Interaction:
	/**
	 * Gets the pay for a level.
	 * 
	 * @param level level
	 * @return pay
	 */
	public Double getPay(Integer level) {

		
		if(levelPay.getXRequired() > level){
			return 0.0;
		}
		
		return levelPay.calculateValue(level.shortValue());
		
		
	}
	
	/**
	 * Gets a guild for a profession.
	 * 
	 * @param name profession name
	 * @return guild
	 */
	public String getGuild(String name) {
		return guilds.get(name);
	}
	
	
	// Load unload:
	/**
	 * Loads proficiency information.
	 * 
	 * @return proficiency information
	 */
	public static GuildConfiguration load(){
		
		
		boolean integrity = true;
		GuildConfiguration config;
		
		// Fields:
		try {
			
			config = WriterReader.readGuildConfig();
			
		} catch (FileNotFoundException e) {
			
			Saga.severe(GuildConfiguration.class, "missing configuration", "loading defaults");
			config = new GuildConfiguration();
			integrity = false;
			
		} catch (IOException e) {
			
			Saga.severe(GuildConfiguration.class, "failed to read configuration: " + e.getClass().getSimpleName() + ":" + e.getMessage(), "loading defaults");
			config = new GuildConfiguration();
			integrity = false;
			
		} catch (JsonParseException e) {
			
			Saga.severe(GuildConfiguration.class, "failed to parse configuration: " + e.getClass().getSimpleName() + ":" + e.getMessage(), "loading defaults");
			Saga.info("Parse message: " + e.getMessage());
			config = new GuildConfiguration();
			integrity = false;
			
		}

		// Set instance:
		instance = config;
		
		// Complete:
		integrity = config.complete() && integrity;
		
		// Write default if integrity check failed:
		if (!integrity) {
			
			Saga.severe(GuildConfiguration.class, "integrity check failed", "writing defaults configuration");
			try {
				WriterReader.writeProficiencyConfig(config, WriteReadType.CONFIG_DEFAULTS);
			} catch (IOException e) {
				Saga.severe(GuildConfiguration.class, "failed to write configuration: " + e.getClass().getSimpleName() + ":" + e.getMessage(), "write canceled");
			}
			
		}
		
		return config;
		
		
	}
	
	/**
	 * Unloads configuration.
	 * 
	 */
	public static void unload(){
		instance = null;
	}
	
	
	// Types:
	/**
	 * Used when an invalid proficiency request is made.
	 * 
	 * @author andf
	 *
	 */
	public static class InvalidProficiencyException extends Exception{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * Sets a proficiency name.
		 * 
		 * @param name name
		 */
		public InvalidProficiencyException(String name) {
			super("proficiency name="+name);
		}
		
		
	}
	
	
}
