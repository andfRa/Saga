package org.saga.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;

import org.bukkit.Material;
import org.saga.Clock.DaytimeTicker.Daytime;
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
	
	
	// Wages:
	/**
	 * Wage multiplier for settlement levels.
	 */
	private TwoPointFunction factionWageLevelMultiplier;

	/**
	 * Wage multiplier for hierarchy levels.
	 */
	private TwoPointFunction factionWageHierarchyMultiplier;

	/**
	 * Time when wages are payed.
	 */
	private Daytime factionWagesTime;
	
	
	
	// Initialisation:
	/**
	 * Used by gson.
	 */
	public EconomyConfiguration() {
		
	}
	
	/**
	 * Fixes all problematic fields.
	 * 
	 */
	public void complete() {
		
		
		if(playerCoins == null){
			SagaLogger.nullField(getClass(), "playerCoins");
			playerCoins = 0.0;
		}
		
		if(coinName == null){
			SagaLogger.nullField(getClass(), "coinName");
			coinName = "coins";
		}

		if(exchangeDistance == null){
			SagaLogger.nullField(getClass(), "exchangeDistance");
			exchangeDistance = 10.0;
		}
		
		if(guardianRuneRechargeCost == null){
			SagaLogger.nullField(getClass(), "guardianRuneRechargeCost");
			guardianRuneRechargeCost = 1000.0;
		}
		
		if(respecCost == null){
			SagaLogger.nullField(getClass(), "respecCost");
			respecCost= new TwoPointFunction(10000.0);
		}
		respecCost.complete();
		
		if(chunkGroupRenameCost == null){
			SagaLogger.nullField(getClass(), "chunkGroupRenameCost");
			chunkGroupRenameCost= 1000.0;
		}
		
		if(factionRenameCost == null){
			SagaLogger.nullField(getClass(), "factionRenameCost");
			factionRenameCost= 1000.0;
		}
		
		if(prices == null){
			SagaLogger.nullField(getClass(), "prices");
			prices = new Hashtable<Material, Double>();
		}
		
		if(buyMult == null){
			SagaLogger.nullField(getClass(), "buyMult");
			buyMult = 1.0;
		}

		if(sellMult == null){
			SagaLogger.nullField(getClass(), "sellMult");
			sellMult = 1.0;
		}

		if(factionWageLevelMultiplier == null){
			SagaLogger.nullField(getClass(), "factionWageLevelMultiplier");
			factionWageLevelMultiplier= new TwoPointFunction(0.0);
		}
		factionWageLevelMultiplier.complete();

		if(factionWageHierarchyMultiplier == null){
			SagaLogger.nullField(getClass(), "factionWageLevelMultiplier");
			factionWageLevelMultiplier= new TwoPointFunction(0.0);
		}
		factionWageLevelMultiplier.complete();
		
		if(factionWagesTime == null){
			SagaLogger.nullField(getClass(), "factionWagesTime");
			factionWagesTime= Daytime.NONE;
		}
		
		
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
	
	
	
	// Wages:
	/**
	 * Calculates wage for settlement.
	 * 
	 * @param settleLevel settlement level
	 * @return wage for a single settlement
	 */
	public double calcWage(Integer settleLevel) {

		return factionWageLevelMultiplier.value(settleLevel);
		
	}
	
	/**
	 * Calculates the wages different hierarchy levels get.
	 * 
	 * @param rawWage raw wage
	 * @return wages for hierarchy levels
	 */
	public Hashtable<Integer, Double> calcHierarchyWages(Double rawWage) {

		
		Hashtable<Integer, Double> wages = new Hashtable<Integer, Double>();
		
		int min = FactionConfiguration.config().getDefinition().getHierarchyMin();
		int max = FactionConfiguration.config().getDefinition().getHierarchyMax();
		
		for (int hiera = min; hiera <= max; hiera++) {
			
			wages.put(hiera, rawWage * factionWageHierarchyMultiplier.value(hiera));
			
		}
		
		return wages;
		
		
	}
	
	/**
	 * Gets the faction wages time.
	 * 
	 * @return faction wages time
	 */
	public Daytime getFactionWagesTime() {
		return factionWagesTime;
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
			
			SagaLogger.severe(GeneralConfiguration.class, "configuration not found");
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
