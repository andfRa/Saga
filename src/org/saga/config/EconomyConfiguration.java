package org.saga.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;

import org.bukkit.Material;
import org.saga.SagaLogger;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.utility.TwoPointFunction;

import com.google.gson.JsonParseException;

public class EconomyConfiguration {


	/**
	 * Instance of the configuration.
	 */
	transient private static EconomyConfiguration instance;
	
	/**
	 * Gets the instance.
	 * 
	 * @return instance
	 */
	public static EconomyConfiguration config() {
		return instance;
	}
	
	/**
	 * Random generator.
	 */
	private static Random random = new Random();
	
	
	/**
	 * Player initial coins.
	 */
	public Double playerCoins;

	/**
	 * Coin name.
	 */
	public String coinName;

	
	// Player:
	/**
	 * Exchange distance.
	 */
	public Double exchangeDistance;

	
	// Other prices:
	/**
	 * Guardian stone price.
	 */
	public Double guardianRuneRechargeCost;

	
	// Attributes:
	/**
	 * Respecification cost.
	 */
	private TwoPointFunction respecCost;

	
	// Rename:
	/**
	 * Faction rename cost.
	 */
	public Double factionRenameCost;
	
	/**
	 * Chunk group rename cost.
	 */
	public Double chunkGroupRenameCost;

	
	// Prices:
	/**
	 * Material prices.
	 */
	public Hashtable<Material, Double> prices;

	/**
	 * Sell multiplier.
	 */
	public Double sellMult;

	/**
	 * Buy multiplier.
	 */
	public Double buyMult;
	
	
	
	// Initialisation:
	/**
	 * Used by gson.
	 */
	public EconomyConfiguration() {
		
	}
	
	/**
	 * Goes trough all the fields and makes sure everything has been set after gson load.
	 * If not, it fills the field with defaults.
	 * 
	 * @return true if everything was correct.
	 */
	public boolean complete() {
		
		
		boolean integrity = true;
		
		
		if(playerCoins == null){
			SagaLogger.severe(getClass(), "playerCoins field not initialized");
			playerCoins = 0.0;
			integrity = false;
		}
		
		if(coinName == null){
			SagaLogger.severe(getClass(), "coinName field not initialized");
			coinName = "coins";
			integrity = false;
		}

		if(exchangeDistance == null){
			SagaLogger.severe(getClass(), "exchangeDistance field not initialized");
			exchangeDistance = 10.0;
			integrity=false;
		}
		
		if(guardianRuneRechargeCost == null){
			SagaLogger.severe(getClass(), "guardianRuneRechargeCost field not initialized");
			guardianRuneRechargeCost = 1000.0;
			integrity=false;
		}
		
		if(respecCost == null){
			SagaLogger.severe(getClass(), "respecCost field failed to initialize");
			respecCost= new TwoPointFunction(10000.0);
			integrity=false;
		}
		
		if(chunkGroupRenameCost == null){
			SagaLogger.severe(getClass(), "chunkGroupRenameCost field failed to initialize");
			chunkGroupRenameCost= 1000.0;
			integrity=false;
		}
		
		if(factionRenameCost == null){
			SagaLogger.severe(getClass(), "factionRenameCost field failed to initialize");
			factionRenameCost= 1000.0;
			integrity=false;
		}
		
		if(prices == null){
			SagaLogger.severe(getClass(), "prices field not initialized");
			prices = new Hashtable<Material, Double>();
			integrity=false;
		}
		
		if(buyMult == null){
			SagaLogger.nullField(getClass(), "buyMult");
			buyMult = 1.0;
			integrity=false;
		}

		if(sellMult == null){
			SagaLogger.nullField(getClass(), "sellMult");
			sellMult = 1.0;
			integrity=false;
		}
		
		return integrity;
		
		
	}

	
	
	// Prices:
	/**
	 * Gets the price for the given material
	 * 
	 * @param material material
	 * @return price, null if none
	 */
	public Double getPrice(Material material) {
		
		Double price = prices.get(material);
		
		return price;
	
	}
	
	/**
	 * Gets the sell multiplier.
	 * 
	 * @return sell multiplier
	 */
	public Double getSellMult() {
		return sellMult;
	}
	
	/**
	 * Gets the buy multiplier.
	 * 
	 * @return buy multiplier
	 */
	public Double getBuyMult() {
		return buyMult;
	}
	
	
	
	// Attributes:
	/**
	 * Gets the respec cost.
	 * 
	 * @param score attribute score
	 * @return coin cost
	 */
	public Double getRespecCost(Integer score) {
		return respecCost.value(score);
	}
	
	

	// Util:
	/**
	 * Returns a random normal distributed value.
	 * 
	 * @param value value
	 * @param spread spread
	 * @return random value with normal distribution
	 */
	public static Double nextGaussian(Double value, Double spread) {

		
		Double nextRandom = random.nextGaussian();
		
		while(nextRandom > 2.0 || nextRandom < -2.0){
			nextRandom = random.nextGaussian();
		}
		
		return value  + nextRandom * spread / 2;
		
		
	}
	
	/**
	 * Returns a random normal distributed value.
	 * 
	 * @param value value
	 * @param spread spread
	 * @return random value with normal distribution
	 */
	public static Integer nextGaussian(Integer value, Double spreadRelative) {

		return (int) Math.round(nextGaussian(value.doubleValue(), spreadRelative));
		
	}
	
	
	
	
	// Load unload:
	/**
	 * Loads configuration.
	 * 
	 * @return configuration
	 */
	public static EconomyConfiguration load(){
		
		
		EconomyConfiguration config;
		try {
			
			config = WriterReader.read(Directory.ECONOMY_CONFIG, EconomyConfiguration.class);
			
		} catch (FileNotFoundException e) {
			
			SagaLogger.severe(BalanceConfiguration.class, "configuration not found");
			config = new EconomyConfiguration();
			
		} catch (IOException e) {
			
			SagaLogger.severe(SettlementConfiguration.class, "failed to read configuration: " + e.getClass().getSimpleName());
			config = new EconomyConfiguration();
			
		} catch (JsonParseException e) {

			SagaLogger.severe(SettlementConfiguration.class, "failed to parse configuration: " + e.getClass().getSimpleName());
			SagaLogger.info("message: " + e.getMessage());
			config = new EconomyConfiguration();
			
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
