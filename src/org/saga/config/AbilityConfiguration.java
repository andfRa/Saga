package org.saga.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.saga.Saga;
import org.saga.abilities.Ability;
import org.saga.abilities.AbilityDefinition;
import org.saga.constants.IOConstants.WriteReadType;
import org.saga.utility.WriterReader;

import com.google.gson.JsonParseException;

public class AbilityConfiguration {

	
	/**
	 * Instance of the configuration.
	 */
	transient private static AbilityConfiguration instance;
	
	/**
	 * Gets the instance.
	 * 
	 * @return instance
	 */
	public static AbilityConfiguration config() {
		return instance;
	}
	
	/**
	 * Abilities.
	 */
	private ArrayList<AbilityDefinition> definitions; 
	
	
	
	// Initialization:
	/**
	 * Used by gson.
	 * 
	 */
	public AbilityConfiguration() {
	}
	
	/**
	 * Completes construction.
	 * 
	 */
	public boolean complete() {
		
		
		boolean integrity = true;
		

		if(definitions == null){
			Saga.severe(getClass(), "failed to intialize definitions field", "setting default");
			definitions = new ArrayList<AbilityDefinition>();
			integrity = false;
		}
		for (int i = 0; i < definitions.size(); i++) {
			
			if(definitions.get(i) == null){
				Saga.severe(getClass(), "failed to intialize definitions field element", "removing element");
				definitions.remove(i);
				i--;
				continue;
			}
			try {
				integrity = definitions.get(i).complete() && integrity;
			} catch (Exception e) {
				Saga.severe(getClass(), "failed to complete " + definitions.get(i).getName() + " definition: " + e.getClass().getSimpleName() + ":" + e.getMessage(), "removing element");
				definitions.remove(i);
				i--;
				continue;
			}
			
		}

		return integrity;
		
		
	}
	
	/**
	 * Gets the ability definition.
	 * 
	 * @param name ability name
	 * @return definition, null if none
	 */
	public AbilityDefinition getDefinition(String name) {
		
		for (AbilityDefinition definition : definitions) {
			if(definition.getName().equalsIgnoreCase(name)) return definition;
		}
		return null;
		
	}
	
	/**
	 * Creates an ability.
	 * 
	 * @param name ability name
	 * @return ability
	 * @throws InvalidAbilityException if ability creation fails
	 */
	public static Ability createAbility(String name) throws InvalidAbilityException{
		
		
		AbilityDefinition definition = AbilityConfiguration.config().getDefinition(name);
		
		if(definition == null){
			
			throw new InvalidAbilityException(name, "missing definition");
			
		}
		
		try {
			
			Class<?> cl = Class.forName(definition.getClassName());
			Class<? extends Ability> clca = cl.asSubclass(Ability.class);
			Constructor<? extends Ability> co = clca.getConstructor(AbilityDefinition.class);
			return co.newInstance(definition);
			
		} catch (Exception e) {
			
			throw new InvalidAbilityException(name, e.getClass().getSimpleName() + ":" + e.getMessage());

		}
		
		
	}
	
	
	// Interaction:
	/**
	 * Gets all ability names.
	 * 
	 * @return ability names
	 */
	public ArrayList<String> getAbilityNames() {

		ArrayList<String> abilityNames = new ArrayList<String>();
		
		ArrayList<AbilityDefinition> definitions = new ArrayList<AbilityDefinition>(this.definitions);
		for (AbilityDefinition definition : definitions) {
			abilityNames.add(definition.getName());
		}
		
		return abilityNames;
		
	}

	// Load unload:
	/**
	 * Loads the configuration.
	 * 
	 * @return experience configuration
	 */
	public static AbilityConfiguration load(){
		
		
		boolean integrityCheck = true;
		
		// Load:
		String configName = "ability configuration";
		AbilityConfiguration config;
		try {
			config = WriterReader.readAbilityConfig();
		} catch (FileNotFoundException e) {
			Saga.severe("Missing " + configName + ". Loading defaults.");
			config = new AbilityConfiguration();
			integrityCheck = false;
		} catch (IOException e) {
			Saga.severe("Failed to load " + configName + ". Loading defaults.");
			config = new AbilityConfiguration();
			integrityCheck = false;
		} catch (JsonParseException e) {
			Saga.severe("Failed to parse " + configName + ". Loading defaults.");
			Saga.info("Parse message :" + e.getMessage());
			config = new AbilityConfiguration();
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
				WriterReader.writeAbilityConfig(config, WriteReadType.CONFIG_DEFAULTS);
			} catch (IOException e) {
				Saga.severe("Profession information write failure. Ignoring write.");
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
	
	
	// Types:
	/**
	 * Used when an invalid ability request is made.
	 * 
	 * @author andf
	 *
	 */
	public static class InvalidAbilityException extends Exception{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * Sets a ability name.
		 * 
		 * @param name name
		 */
		public InvalidAbilityException(String name) {
			super("ability name="+name);
		}
		
		/**
		 * Sets a ability name and cause.
		 * 
		 * @param name name
		 * @param cause cause
		 */
		public InvalidAbilityException(String name, String cause) {
			super("ability name=" + name + ", cause=" + cause);
		}
		
		
	}
	
	
	public static void main(String[] args) {
		
		
		AbilityConfiguration.load();
		
		try {
			createAbility("bash");
		} catch (InvalidAbilityException e) {
			e.printStackTrace();
		}
		
	}
	
}
