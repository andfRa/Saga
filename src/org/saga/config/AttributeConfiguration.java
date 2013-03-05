package org.saga.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.saga.SagaLogger;
import org.saga.attributes.Attribute;
import org.saga.attributes.DamageType;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;

public class AttributeConfiguration {

	
	/**
	 * Instance of the configuration.
	 */
	transient private static AttributeConfiguration instance;
	
	/**
	 * Gets the instance.
	 * 
	 * @return instance
	 */
	public static AttributeConfiguration config() {
		return instance;
	}

	/**
	 * Maximum attribute score.
	 */
	public Integer maxAttributeScore;
	
	/**
	 * Damage penalty totals.
	 * If Minecraft damage is below these values, then a penalty is applied to the resulting damage.
	 */
	private Hashtable<DamageType, Double> damagePenaltyValues;
	
	/**
	 * Attributes.
	 */
	private ArrayList<Attribute> attributes; 
	
	
	
	
	// Initialisation:
	/**
	 * Completes construction.
	 * 
	 */
	public void complete() {
		
		
		if(maxAttributeScore == null){
			SagaLogger.nullField(getClass(), "maxAttributeScore");
			maxAttributeScore= 1;
		}
		
		if(damagePenaltyValues == null){
			SagaLogger.nullField(getClass(), "damagePenaltyValues");
			damagePenaltyValues= new Hashtable<DamageType, Double>();
		}
		
		if(attributes == null){
			SagaLogger.nullField(getClass(), "attributes");
			attributes = new ArrayList<Attribute>();
		}
		if(attributes.remove(null)) SagaLogger.nullField(getClass(), "attributes element");
		
		for (Attribute attribute : attributes) {
			attribute.complete();
		}
		
		
	}
	
	
	
	
	// Getters:
	/**
	 * Get damage penalty value.
	 * 
	 * @param type damage type
	 * @return penalty value, null if none
	 */
	public Double getPenaltyValue(DamageType type) {

		Double penalty = damagePenaltyValues.get(type);
		if(penalty == null) penalty = 0.0;
		
		return penalty;
		
	}
	
	/**
	 * Gets the attributes.
	 * 
	 * @return attributes
	 */
	public ArrayList<Attribute> getAttributes() {
		return new ArrayList<Attribute>(attributes);
	}
	
	/**
	 * Gets the attribute names.
	 * 
	 * @return attribute names
	 */
	public ArrayList<String> getAttributeNames() {

		
		ArrayList<Attribute> attributes = getAttributes();
		ArrayList<String> attributeNames = new ArrayList<String>();
		
		Iterator<Attribute> it = attributes.iterator();
		while (it.hasNext()) {
			attributeNames.add(it.next().getName());
		}
		
		return attributeNames;
		
		
	}
	
	
	
	// Load unload:
	/**
	 * Loads configuration.
	 * 
	 * @return configuration
	 */
	public static AttributeConfiguration load(){


		// Create config:
		if(!WriterReader.checkExists(Directory.ATTRIBUTE_CONFIG)){

			try {
				WriterReader.unpackConfig(Directory.ATTRIBUTE_CONFIG);
			}
			catch (IOException e) {
				SagaLogger.severe(AttributeConfiguration.class, "failed to create default configuration: " + e.getClass().getSimpleName());
			}
			
		}
		
		AttributeConfiguration config;
		try {
			
			config = WriterReader.read(Directory.ATTRIBUTE_CONFIG, AttributeConfiguration.class);
			
		} catch (IOException e) {
			
			SagaLogger.severe(AttributeConfiguration.class, "failed to read configuration: " + e.getClass().getSimpleName());
			config = new AttributeConfiguration();
			
		} catch (JsonParseException e) {

			SagaLogger.severe(AttributeConfiguration.class, "failed to parse configuration: " + e.getClass().getSimpleName());
			SagaLogger.info("message: " + e.getMessage());
			config = new AttributeConfiguration();
			
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
