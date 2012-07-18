package org.saga.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.saga.SagaLogger;
import org.saga.abilities.Ability;
import org.saga.player.SagaPlayer;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.utility.TwoPointFunction;

import com.google.gson.JsonParseException;

public class ExperienceConfiguration {

	
	/**
	 * Instance of the configuration.
	 */
	transient private static ExperienceConfiguration instance;
	
	/**
	 * Gets the instance.
	 * 
	 * @return instance
	 */
	public static ExperienceConfiguration config() {
		return instance;
	}

	
	
	/**
	 * Maximum level.
	 */
	public Integer maximumLevel;
	
	/**
	 * Experience needed to level up.
	 */
	private TwoPointFunction levelUpExp;

	/**
	 * Block break experience.
	 */
	private Hashtable<Material, Hashtable<Byte, Double>> blockExp;
	
	/**
	 * Player kill experience.
	 */
	private TwoPointFunction playerExp;
	
	/**
	 * Creature kill experience.
	 */
	private Hashtable<String, Double> creatureExp;
	
	/**
	 * Ability experience.
	 */
	private Hashtable<String, TwoPointFunction> abilityExp;
	
	/**
	 * Spawner enchant points multiplier.
	 */
	public Double spawnerEncPointMult;
	
	/**
	 * Spawner exp multiplier.
	 */
	public Double spawnerExpMult;
	
	
	
	
	// Initialisation:
	/**
	 * Used by gson.
	 * 
	 */
	public ExperienceConfiguration() {
	}
	
	/**
	 * Completes.
	 * 
	 * @return integrity check
	 */
	public boolean complete() {
		

		boolean integrity = true;
		
		// Set instance:
		instance = this;

		if(maximumLevel == null){
			SagaLogger.nullField(getClass(), "maximumLevel");
			maximumLevel= 1;
		}
		
		if(levelUpExp == null){
			SagaLogger.severe(getClass(), "levelUpExp field not initialized");
			levelUpExp = new TwoPointFunction(0.0);
			integrity=false;
		}
		
		if(blockExp == null){
			blockExp = new Hashtable<Material, Hashtable<Byte,Double>>();
			SagaLogger.severe(this, "blockExp field failed to intialize");
			integrity = false;
		}
		
		if(playerExp == null){
			playerExp = new TwoPointFunction(0.0);
			SagaLogger.severe(this, "playerExp field failed to intialize");
			integrity = false;
		}
		
		if(creatureExp == null){
			creatureExp = new Hashtable<String, Double>();
			SagaLogger.severe(this, "creatureExp field failed to intialize");
			integrity = false;
		}
		
		if(abilityExp == null){
			abilityExp = new Hashtable<String, TwoPointFunction>();
			SagaLogger.severe(this, "abilityExp field failed to intialize");
			integrity = false;
		}
		Collection<TwoPointFunction> abExpVals = abilityExp.values();
		for (TwoPointFunction abExpVal : abExpVals) {
			abExpVal.complete();
		}
		
		if(spawnerEncPointMult == null){
			spawnerEncPointMult = 1.0;
			SagaLogger.nullField(this, "spawnerEncPointMult");
			integrity = false;
		}
		
		if(spawnerExpMult == null){
			spawnerExpMult = 1.0;
			SagaLogger.nullField(this, "spawnerExpMult");
			integrity = false;
		}
		
		return integrity;
		
		
	}


	
	
	// Levels:
	/**
	 * Gets the experience required to level up.
	 * 
	 * @param level level
	 * @return experience required
	 */
	public Integer getLevelExp(Integer level) {
		
		return levelUpExp.value(level).intValue();

	}
	
	/**
	 * Gets the maximum level.
	 * 
	 * @return maximum level
	 */
	public Integer getMaxLevel() {
		
		return levelUpExp.getXMax().intValue();

	}
	

	
	
	// Experience:
	/**
	 * Gets the experience for a block break.
	 * 
	 * @param block block
	 * @return experience
	 */
	public Double getExp(Block block) {

		
		Hashtable<Byte, Double> datas = blockExp.get(block.getType());
		if(datas == null) return 0.0;
		
		Double exp = datas.get(new Byte(block.getData()));
		if(exp == null) return 0.0;
			
		return exp;
		
		
	}

	/**
	 * gets the experience for a creature kill.
	 * 
	 * @param sagaPlayer saga player
	 * @return experience
	 */
	public Double getExp(SagaPlayer sagaPlayer) {

		
		Double exp = playerExp.value(sagaPlayer.getLevel());
		if(exp == null) return 0.0;
		
		return exp;
		
		
	}
	
	/**
	 * Gets the experience for a creature kill.
	 * 
	 * @param creature creature
	 * @return experience
	 */
	public Double getExp(Creature creature) {

		
		Double exp = creatureExp.get(creature.getClass().getSimpleName().toLowerCase().replace("craft", ""));
		
		if(exp == null) exp = creatureExp.get("default");
		
		if(exp == null) return 0.0;
		
		return exp;
		
		
	}
	
	/**
	 * Gets the experience for ability usage.
	 * 
	 * @param ability ability
	 * @param value ability specific value
	 * @return
	 */
	public Double getExp(Ability ability, Integer value) {

		
		Double exp = null;
		
		TwoPointFunction expFun = abilityExp.get(ability.getName());
		if(expFun != null) exp = expFun.value(value);
		
		if(exp == null) return 0.0;
		
		return exp;
		
		
	}
	
	
	
	// Load unload:
	/**
	 * Loads configuration.
	 * 
	 * @return configuration
	 */
	public static ExperienceConfiguration load(){

		
		ExperienceConfiguration config;
		try {
			
			config = WriterReader.read(Directory.EXPERIENCE_CONFIG, ExperienceConfiguration.class);
			
		} catch (FileNotFoundException e) {
			
			SagaLogger.severe(BalanceConfiguration.class, "configuration not found");
			config = new ExperienceConfiguration();
			
		} catch (IOException e) {
			
			SagaLogger.severe(SettlementConfiguration.class, "failed to read configuration: " + e.getClass().getSimpleName());
			config = new ExperienceConfiguration();
			
		} catch (JsonParseException e) {

			SagaLogger.severe(SettlementConfiguration.class, "failed to parse configuration: " + e.getClass().getSimpleName());
			SagaLogger.info("message: " + e.getMessage());
			config = new ExperienceConfiguration();
			
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
	
	
}
