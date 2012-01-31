package org.saga.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.saga.Saga;
import org.saga.constants.IOConstants.WriteReadType;
import org.saga.player.Proficiency;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.player.ProficiencyDefinition;
import org.saga.utility.WriterReader;

import com.google.gson.JsonParseException;

public class ProficiencyConfiguration {
	

	/**
	 * Instance of the configuration.
	 */
	transient private static ProficiencyConfiguration instance;
	
	/**
	 * Gets the instance.
	 * 
	 * @return instance
	 */
	public static ProficiencyConfiguration config() {
		return instance;
	}
	
	/**
	 * If true, initial profession will be set on join.
	 */
	public Boolean initialProfession;
	
	/**
	 * If true, initial profession will be set on join.
	 */
	public Boolean initialClass;
	
	
	// Proficiencies.
	/**
	 * Proficiency definitions.
	 */
	private ArrayList<ProficiencyDefinition> definitions;

	// Initialization:
	/**
	 * Used by gson.
	 * 
	 */
	public ProficiencyConfiguration() {
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

		if(initialProfession == null){
			initialProfession = false;
			Saga.severe(getClass(), "initialProfession field failed to initalize", "setting default");
			integrity=false;
		}
		
		if(initialClass == null){
			initialClass = false;
			Saga.severe(getClass(), "initialClass field failed to initalize", "setting default");
			integrity=false;
		}
		
		if(definitions == null){
			definitions = new ArrayList<ProficiencyDefinition>();
			Saga.severe(getClass(), "profiecncyDefinitions field failed to initalize", "setting default");
			integrity=false;
		}
		for (int i = 0; i < definitions.size() && i >= 0; i++) {
			if(definitions.get(i) == null){
				Saga.severe(getClass(), "definitions field element failed to initalize", "setting default");
				definitions.remove(i);
				i--;
				continue;
			}	
			
			integrity = definitions.get(i).complete() && integrity;
				
		}
		
		return integrity;
		
		
	}

	
	// Interaction:
	/**
	 * Gets a proficiency definition.
	 * 
	 * @param name proficiency name
	 * @return definition, null if not found
	 */
	public ProficiencyDefinition getDefinition(String name){
		
		ArrayList<ProficiencyDefinition> definitions = new ArrayList<ProficiencyDefinition>(this.definitions);
		
		for (ProficiencyDefinition definition : definitions) {
			
			if(definition.getName().equalsIgnoreCase(name)) return definition;
			
		}
		
		return null;
		
	}
	
	/**
	 * Gets a proficiency.
	 * 
	 * @param name proficiency name
	 * @return proficiency, null if not found
	 * @throws InvalidProficiencyException when there is no proficiency associated with the given name
	 */
	public Proficiency createProficiency(String name) throws InvalidProficiencyException{
		

		ProficiencyDefinition definition = ProficiencyConfiguration.config().getDefinition(name);
		
		if(definition == null){
			
			throw new InvalidProficiencyException(name, "missing definition");
			
		}
		
		return new Proficiency(definition);
		
		
	}
	
	
	
	/**
	 * Gets proficiency names for the given type.
	 * 
	 * @param type type
	 * @return proficiency names
	 */
	public ArrayList<String> getProficiencyNames(ProficiencyType type) {

		
		ArrayList<ProficiencyDefinition> definitions = new ArrayList<ProficiencyDefinition>(this.definitions);
		ArrayList<String> proficiencyNames = new ArrayList<String>();
		
		for (ProficiencyDefinition definition : definitions) {
			
			if(definition.getType().equals(type)) proficiencyNames.add(definition.getName());
			
		}
		
		return proficiencyNames;
		
		
	}
	
	
	// Load unload:
	/**
	 * Loads proficiency information.
	 * 
	 * @return proficiency information
	 */
	public static ProficiencyConfiguration load(){
		
		
		boolean integrity = true;
		ProficiencyConfiguration config;
		
		// Fields:
		try {
			
			config = WriterReader.readProficiencyConfig();
			
		} catch (FileNotFoundException e) {
			
			Saga.severe(ProficiencyConfiguration.class, "missing configuration", "loading defaults");
			config = new ProficiencyConfiguration();
			integrity = false;
			
		} catch (IOException e) {
			
			Saga.severe(ProficiencyConfiguration.class, "failed to read configuration: " + e.getClass().getSimpleName() + ":" + e.getMessage(), "loading defaults");
			config = new ProficiencyConfiguration();
			integrity = false;
			
		} catch (JsonParseException e) {
			
			Saga.severe(ProficiencyConfiguration.class, "failed to parse configuration: " + e.getClass().getSimpleName() + ":" + e.getMessage(), "loading defaults");
			Saga.info("Parse message :" + e.getMessage());
			config = new ProficiencyConfiguration();
			integrity = false;
			
		}

		// Set instance:
		instance = config;
		
		// Complete:
		integrity = config.complete() && integrity;
		
		// Write default if integrity check failed:
		if (!integrity) {
			
			Saga.severe(ProficiencyConfiguration.class, "integrity check failed", "writing defaults configuration");
			try {
				WriterReader.writeProficiencyConfig(config, WriteReadType.CONFIG_DEFAULTS);
			} catch (IOException e) {
				Saga.severe(ProficiencyConfiguration.class, "failed to write configuration: " + e.getClass().getSimpleName() + ":" + e.getMessage(), "write canceled");
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
		

		/**
		 * Sets a proficiency name and cause.
		 * 
		 * @param name name
		 * @param cause cause
		 */
		public InvalidProficiencyException(String name, String cause) {
			super("proficiency name=" + name + ", cause=" + cause);
		}
		
		
		
	}
	
	
}
