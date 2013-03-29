package org.saga.config;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.saga.SagaLogger;
import org.saga.abilities.Ability;
import org.saga.abilities.AbilityDefinition;
import org.saga.exceptions.InvalidAbilityException;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;

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
	
	
	// Scores:
	/**
	 * Normal ability cap.
	 */
	private Integer normalAbilityCap;
	
	/**
	 * Maximum ability score.
	 */
	public Integer maxAbilityScore;
	
	
	// Energy:
	/**
	 * Base energy points.
	 */
	private Integer baseEnergyPoins;

	/**
	 * Energy gained for one food.
	 */
	private Integer energyRegenPerFood;

	/**
	 * Energy regeneration seconds.
	 */
	private Double regenDelaySeconds;
	
	/**
	 * Minimum food level required for the energy to regenerate.
	 */
	private Integer energyMinimumFood;
	
	
	
	// Definitions;
	/**
	 * Abilities.
	 */
	private ArrayList<AbilityDefinition> definitions; 
	
	
	
	// Initialisation:
	/**
	 * Completes construction.
	 * 
	 */
	public void complete() {
		
		
		// Scores:
		if(normalAbilityCap == null){
			normalAbilityCap = 0;
			SagaLogger.nullField(getClass(), "normalAbilityCap");
		}
		
		if(maxAbilityScore == null){
			maxAbilityScore = 0;
			SagaLogger.nullField(getClass(), "maxAbilityScore");
		}
		
		// Energy:
		if(baseEnergyPoins == null){
			baseEnergyPoins = 0;
			SagaLogger.nullField(getClass(), "baseEnergyPoins");
		}
		
		if(energyRegenPerFood == null){
			energyRegenPerFood = 0;
			SagaLogger.nullField(getClass(), "energyRegenPerFood");
		}
		
		if(regenDelaySeconds == null){
			regenDelaySeconds = 0.0;
			SagaLogger.nullField(getClass(), "regenDelaySeconds");
		}
		
		if(energyMinimumFood == null){
			energyMinimumFood = 0;
			SagaLogger.nullField(getClass(), "energyMinimumFood");
		}
		
		
		
		// Definitions:
		if(definitions == null){
			SagaLogger.nullField(getClass(), "definitions");
			definitions = new ArrayList<AbilityDefinition>();
		}
		for (int i = 0; i < definitions.size(); i++) {
			
			if(definitions.get(i) == null){
				SagaLogger.nullField(getClass(), "definitions element");
				definitions.remove(i);
				i--;
				continue;
			}
			try {
				definitions.get(i).complete();
			} catch (Exception e) {
				SagaLogger.severe(getClass(), "failed to complete " + definitions.get(i).getName() + " definition: " + e.getClass().getSimpleName() + ":" + e.getMessage());
				definitions.remove(i);
				i--;
				continue;
			}
			
		}

		
	}
	
	
	// Scores:
	/**
	 * Gets the normal ability cap.
	 * 
	 * @return normal ability cap
	 */
	public Integer getNormalAbilityCap() {
		return normalAbilityCap;
	}
	
	
	
	// Energy:
	/**
	 * Gets the base energy points.
	 * 
	 * @return base energy points
	 */
	public Integer getBaseEnergyPoins() {
		return baseEnergyPoins;
	}
	
	/**
	 * Gets the energy for a single level.
	 * 
	 * @return energy per food level
	 */
	public int getEnergyPerFoodCost() {
		return energyRegenPerFood;
	}
	
	/**
	 * Gets the tick delay in seconds.
	 * 
	 * @return regeneration delay in seconds
	 */
	public Double getEnergyRegenSeconds() {
		return regenDelaySeconds;
	}
	
	/**
	 * Gets the minimum food level required for energy regeneration.
	 * 
	 * @return minimum food level for energy regeneration
	 */
	public Integer getEnergyMinimumFood() {
		return energyMinimumFood;
	}
	
	
	
	// Definitions:
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
	 * Gets the ability definitions.
	 * 
	 * @return definitions
	 */
	public ArrayList<AbilityDefinition> getDefinitions() {
		
		return new ArrayList<AbilityDefinition>(definitions);
		
	}
	
	
	
	// Creation:
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
			
		} catch (Throwable e) {
			
			throw new InvalidAbilityException(name, e.getClass().getSimpleName() + ":" + e.getMessage());

		}
		
		
	}
	
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
	 * Loads configuration.
	 * 
	 * @return configuration
	 */
	public static AbilityConfiguration load(){


		// Create config:
		if(!WriterReader.checkExists(Directory.ABILITY_CONFIG)){

			try {
				WriterReader.unpackConfig(Directory.ABILITY_CONFIG);
			}
			catch (IOException e) {
				SagaLogger.severe(AbilityConfiguration.class, "failed to create default configuration: " + e.getClass().getSimpleName());
			}
			
		}
		
		// Read config:
		AbilityConfiguration config;
		try {
			
			config = WriterReader.readConfig(Directory.ABILITY_CONFIG, AbilityConfiguration.class);
			
		} catch (IOException e) {
			
			SagaLogger.severe(AbilityConfiguration.class, "failed to read configuration: " + e.getClass().getSimpleName());
			config = new AbilityConfiguration();
			
		} catch (JsonParseException e) {

			SagaLogger.severe(AbilityConfiguration.class, "failed to parse configuration: " + e.getClass().getSimpleName());
			SagaLogger.info("message: " + e.getMessage());
			config = new AbilityConfiguration();
			
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
