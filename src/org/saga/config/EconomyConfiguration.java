package org.saga.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.bukkit.Material;
import org.saga.SagaLogger;
import org.saga.economy.TradeDeal;
import org.saga.economy.TradeDeal.TradeDealType;
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


	// Trade deals:
	/**
	 * Item trade deal weights.
	 */
	public Hashtable<Material, Double> itemWeights;
	
	/**
	 * Weight items.
	 */
	transient public Material[] items;
	
	/**
	 * Weights for items.
	 */
	transient public Double[] weights;
	
	/**
	 * Material prices.
	 */
	public Hashtable<Material, Double> prices;
	
	/**
	 * Deal price spread.
	 */
	public TwoPointFunction priceSpread;
	
	/**
	 * Deal amount.
	 */
	public TwoPointFunction amount;
	
	/**
	 * Deal amount spread.
	 */
	public TwoPointFunction amountSpread;
	
	/**
	 * Deal days.
	 */
	public TwoPointFunction days;
	
	/**
	 * Deal day spread.
	 */
	public TwoPointFunction daysSpread;
	
	/**
	 * Deals per player.
	 */
	public TwoPointFunction dealsPerPlayer;
	
	/**
	 * Deals speed per player.
	 */
	public TwoPointFunction dealsCreatePerPlayer;

	/**
	 * Automation sell multiplier.
	 */
	public Double automSellMult;

	/**
	 * Automation buy multiplier.
	 */
	public Double automBuyMult;
	
	
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
	
	/**
	 * Ability upgrade costs.
	 */
	private Hashtable<String, TwoPointFunction> abilityUpgradeCosts;

	
	
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

		if(prices == null){
			SagaLogger.severe(getClass(), "prices field not initialized");
			prices = new Hashtable<Material, Double>();
			integrity=false;
		}
		
		if(priceSpread == null){
			SagaLogger.severe(getClass(), "priceSpreads field not initialized");
			priceSpread = new TwoPointFunction(0.0);
			integrity = false;
		}
		
		if(itemWeights == null){
			SagaLogger.severe(getClass(), "itemWeights field not initialized");
			itemWeights = new Hashtable<Material, Double>();
			integrity=false;
		}
		
		createWeights();
		
		if(amount == null){
			SagaLogger.severe(getClass(), "amount field not initialized");
			amount = new TwoPointFunction(0.0);
			integrity = false;
		}

		if(amountSpread == null){
			SagaLogger.severe(getClass(), "amountSpread field not initialized");
			amountSpread = new TwoPointFunction(0.0);
			integrity = false;
		}
		
		if(days == null){
			SagaLogger.severe(getClass(), "days field not initialized");
			days = new TwoPointFunction(0.0);
			integrity = false;
		}

		if(daysSpread == null){
			SagaLogger.severe(getClass(), "daysSpread field not initialized");
			daysSpread = new TwoPointFunction(0.0);
			integrity = false;
		}
		
		if(dealsPerPlayer == null){
			SagaLogger.severe(getClass(), "dealsPerPlayer field not initialized");
			dealsPerPlayer = new TwoPointFunction(0.5);
			integrity=false;
		}

		if(dealsCreatePerPlayer == null){
			SagaLogger.severe(getClass(), "dealsCreatePerPlayer field not initialized");
			dealsCreatePerPlayer = new TwoPointFunction(0.5);
			integrity=false;
		}

		if(automBuyMult == null){
			SagaLogger.severe(getClass(), "automBuyMult field not initialized");
			automBuyMult = 1.0;
			integrity=false;
		}

		if(automSellMult == null){
			SagaLogger.severe(getClass(), "automSellMult field not initialized");
			automSellMult = 1.0;
			integrity=false;
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
		
		if(abilityUpgradeCosts == null){
			abilityUpgradeCosts = new Hashtable<String, TwoPointFunction>();
			SagaLogger.nullField(this, "abilityUpgradeCosts");
		}
		Collection<TwoPointFunction> costs = abilityUpgradeCosts.values();
		for (TwoPointFunction twoPointFunction : costs) {
			twoPointFunction.complete();
		}
		
		if(abilityUpgradeCosts.get("default") == null){
			abilityUpgradeCosts.put("default", new TwoPointFunction(0.0));
			SagaLogger.severe(this, "abilityUpgradeCosts missing default element");
		}
		
		return integrity;
		
		
	}

	/**
	 * Fills in the transient weight tables.
	 * 
	 */
	private void createWeights() {

		
		// Fill tables:
		Set<Entry<Material, Double>> sweights = itemWeights.entrySet();
		
		weights = new Double[sweights.size()];
		items = new Material[sweights.size()];
		
		int i = 0;
		Double sum = 0.0;
		for (Entry<Material, Double> entry : sweights) {
			
			items[i] = entry.getKey();
			weights[i] = entry.getValue();
			
			sum += weights[i];
			
			i++;
			
		}
		
		// Normalize:
		for (int j = 0; j < weights.length; j++) {
			weights[j] = weights[j] / sum;
		}
		
		// Combine:
		for (int j = 1; j < weights.length; j++) {
			weights[j] = weights[j - 1] + weights[j];
		}
		
		
		
	}
	
	
	// Trade deals:
	/**
	 * Creates a random trade deal.
	 * 
	 * @return random trade deal, null if none
	 */
	public TradeDeal createTradeDeal() {
		
		
		Double rand = random.nextDouble();
		Material tdMaterial = null;
		
		for (int i = 0; i < weights.length; i++) {
			
			if(weights[i] >= rand){
				tdMaterial = items[i];
				break;
			}
					
		}
		
		if(tdMaterial == null) return null;		
		
		// Type:
		TradeDealType type = TradeDealType.EXPORT;
		if(random.nextBoolean()) type = TradeDealType.IMPORT;
		
		Double price = prices.get(tdMaterial);
		if(price == null) return null;
		
		Double tdPrice = nextGaussian(price, priceSpread.value(price));
		Integer tdAmount = nextGaussian(amount.value(tdPrice).intValue(), amountSpread.value(tdPrice));
		Integer tdDays = nextGaussian(days.value(tdPrice).intValue(), daysSpread.value(tdPrice));
		
		return new TradeDeal(type, tdMaterial, tdPrice, tdAmount, tdDays);
		
		
	}
	
	/**
	 * Gets all materials available for trade deals.
	 * 
	 * @return available trade deal materials
	 */
	public Set<Material> getAllDealMaterials() {

		return itemWeights.keySet();

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
	
	
	// Abilities:
	/**
	 * Gets ability upgrade cost.
	 * 
	 * @param ability ability name
	 * @param score ability score
	 * @return upgrade cost
	 */
	public Double getAbilityUpgradeCost(String ability, Integer score) {


		TwoPointFunction function = abilityUpgradeCosts.get(ability);
		if(function == null) function =  abilityUpgradeCosts.get("default");
		if(function == null) return 0.0;

		return function.value(score);
		
		
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
			
			SagaLogger.severe(ChunkGroupConfiguration.class, "failed to read configuration: " + e.getClass().getSimpleName());
			config = new EconomyConfiguration();
			
		} catch (JsonParseException e) {

			SagaLogger.severe(ChunkGroupConfiguration.class, "failed to parse configuration: " + e.getClass().getSimpleName());
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
