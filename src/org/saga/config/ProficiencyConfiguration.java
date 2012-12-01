package org.saga.config;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.saga.SagaLogger;
import org.saga.player.Proficiency;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.player.ProficiencyDefinition;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;

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
	
	
	
	// Proficiencies.
	/**
	 * Proficiency definitions.
	 */
	private ArrayList<ProficiencyDefinition> definitions;

	
	
	// Initialisation:
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

		if(definitions == null){
			definitions = new ArrayList<ProficiencyDefinition>();
			SagaLogger.severe(getClass(), "profiecncyDefinitions field failed to initalize");
			integrity=false;
		}
		for (int i = 0; i < definitions.size() && i >= 0; i++) {
			if(definitions.get(i) == null){
				SagaLogger.severe(getClass(), "definitions field element failed to initalize");
				definitions.remove(i);
				i--;
				continue;
			}	
			
			definitions.get(i).complete();
				
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
	 * Gets all definitions.
	 * 
	 * @return all proficiency definitions
	 */
	public ArrayList<ProficiencyDefinition> getDefinitions() {
		return new ArrayList<ProficiencyDefinition>(definitions);
	}

	/**
	 * Gets proficiency definitions.
	 * 
	 * @param type proficiency type
	 * @param hierarchy hierarchy level
	 * @return proficiencies with given type and hierarchy level
	 */
	public ArrayList<ProficiencyDefinition> getDefinitions(ProficiencyType type, Integer hierarchy){
		
		ArrayList<ProficiencyDefinition> definitions = new ArrayList<ProficiencyDefinition>(this.definitions);
		ArrayList<ProficiencyDefinition> filteredDefs = new ArrayList<ProficiencyDefinition>();
		
		for (ProficiencyDefinition definition : definitions) {
			
			if(definition.getType() == type && definition.getHierarchyLevel() == hierarchy) filteredDefs.add(definition);
			
		}
		
		return filteredDefs;
		
	}

	/**
	 * Gets proficiency definitions.
	 * 
	 * @param type proficiency type
	 * @return proficiencies with given type
	 */
	public ArrayList<ProficiencyDefinition> getDefinitions(ProficiencyType type){
		
		ArrayList<ProficiencyDefinition> definitions = new ArrayList<ProficiencyDefinition>(this.definitions);
		ArrayList<ProficiencyDefinition> filteredDefs = new ArrayList<ProficiencyDefinition>();
		
		for (ProficiencyDefinition definition : definitions) {
			
			if(definition.getType() == type) filteredDefs.add(definition);
			
		}
		
		return filteredDefs;
		
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
	

	
	// Load unload:
	/**
	 * Loads configuration.
	 * 
	 * @return configuration
	 */
	public static ProficiencyConfiguration load(){


		// Create config:
		if(!WriterReader.checkExists(Directory.PROFICIENCY_CONFIG)){

			try {
				WriterReader.unpackConfig(Directory.PROFICIENCY_CONFIG);
			}
			catch (IOException e) {
				SagaLogger.severe(ProficiencyConfiguration.class, "failed to create default configuration: " + e.getClass().getSimpleName());
			}
			
		}
		
		ProficiencyConfiguration config;
		try {
			
			config = WriterReader.read(Directory.PROFICIENCY_CONFIG, ProficiencyConfiguration.class);
			
		} catch (IOException e) {
			
			SagaLogger.severe(ProficiencyConfiguration.class, "failed to read configuration: " + e.getClass().getSimpleName());
			config = new ProficiencyConfiguration();
			
		} catch (JsonParseException e) {

			SagaLogger.severe(ProficiencyConfiguration.class, "failed to parse configuration: " + e.getClass().getSimpleName());
			SagaLogger.info("message: " + e.getMessage());
			config = new ProficiencyConfiguration();
			
		}
		
		// Set instance:
		instance = config;
		
		config.complete();
		
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
