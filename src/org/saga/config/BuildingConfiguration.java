package org.saga.config;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.saga.SagaLogger;
import org.saga.buildings.Building;
import org.saga.buildings.BuildingDefinition;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;

public class BuildingConfiguration {


	/**
	 * Instance of the configuration.
	 */
	transient private static BuildingConfiguration instance;
	
	/**
	 * Gets the instance.
	 * 
	 * @return instance
	 */
	public static BuildingConfiguration config() {
		return instance;
	}
	

	
	/**
	 * Building definitions.
	 */
	private ArrayList<BuildingDefinition> definitions;

	
	
	// Initialisation:
	/**
	 * Completes the configuration.
	 * 
	 * @return true if everything was correct.
	 */
	public void complete() {
		
		
		if(definitions == null){
			SagaLogger.nullField(getClass(), "definitions");
			definitions = new ArrayList<BuildingDefinition>();
		}
		for (BuildingDefinition definition : definitions) {
			definition.complete();
		}
		
		
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

		
		BuildingDefinition definition = BuildingConfiguration.config().getBuildingDefinition(name);
		
		if(definition == null){
			
			throw new InvalidBuildingException(name, "missing definition");
			
		}
		
		try {
			
			Class<?> cl = Class.forName(definition.getBuildingClass());
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

		for (BuildingDefinition definition : definitions) {
			if(definition.getName().equals(name)) return definition;
		}
		
		return null;
		
	}
	
	/**
	 * Gets definition for the given building.
	 * 
	 * @return building definitions
	 */
	public ArrayList<BuildingDefinition> getBuildingDefinitions() {

		return new ArrayList<BuildingDefinition>(definitions);
		
	}
	
	
	/**
	 * Gets all building names.
	 * 
	 * @return buildings names
	 */
	public Collection<String> getBuildingNames() {
		
		HashSet<String> names = new HashSet<String>();
		
		for (BuildingDefinition definition : definitions) {
			names.add(definition.getName());
		}
		
		return names;
		
	}
	
	
	
	// Load unload:
	/**
	 * Loads configuration.
	 * 
	 * @return configuration
	 */
	public static BuildingConfiguration load(){


		// Create config:
		if(!WriterReader.checkExists(Directory.BUILDING_CONFIG)){

			try {
				WriterReader.unpackConfig(Directory.BUILDING_CONFIG);
			}
			catch (IOException e) {
				SagaLogger.severe(BuildingConfiguration.class, "failed to create default configuration: " + e.getClass().getSimpleName());
			}
			
		}
		
		BuildingConfiguration config;
		try {
			
			config = WriterReader.read(Directory.BUILDING_CONFIG, BuildingConfiguration.class);
			
		} catch (IOException e) {
			
			SagaLogger.severe(BuildingConfiguration.class, "failed to read configuration: " + e.getClass().getSimpleName());
			config = new BuildingConfiguration();
			
		} catch (JsonParseException e) {

			SagaLogger.severe(BuildingConfiguration.class, "failed to parse configuration: " + e.getClass().getSimpleName());
			SagaLogger.info("message: " + e.getMessage());
			config = new BuildingConfiguration();
			
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
