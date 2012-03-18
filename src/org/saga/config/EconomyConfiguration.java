package org.saga.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.bukkit.Material;
import org.saga.Saga;
import org.saga.constants.IOConstants.WriteReadType;
import org.saga.economy.TradeDeal;
import org.saga.economy.TradeDeal.TradeDealType;
import org.saga.utility.TwoPointFunction;
import org.saga.utility.WriterReader;

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

	
	// Skills:
	/**
	 * Skill upgrade costs.
	 */
	private TwoPointFunction skillUpgradeCost;

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
	
	// Initialization:
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
			Saga.severe(getClass(), "playerCoins field not initialized", "setting default");
			playerCoins = 0.0;
			integrity = false;
		}
		
		if(coinName == null){
			Saga.severe(getClass(), "coinName field not initialized", "setting default");
			coinName = "coins";
			integrity = false;
		}

		if(prices == null){
			Saga.severe(getClass(), "prices field not initialized", "setting default");
			prices = new Hashtable<Material, Double>();
			integrity=false;
		}
		
		if(priceSpread == null){
			Saga.severe(getClass(), "priceSpreads field not initialized", "setting default");
			priceSpread = new TwoPointFunction(0.0);
			integrity = false;
		}
		
		if(itemWeights == null){
			Saga.severe(getClass(), "itemWeights field not initialized", "setting default");
			itemWeights = new Hashtable<Material, Double>();
			integrity=false;
		}
		
		createWeights();
		
		if(amount == null){
			Saga.severe(getClass(), "amount field not initialized", "setting default");
			amount = new TwoPointFunction(0.0);
			integrity = false;
		}

		if(amountSpread == null){
			Saga.severe(getClass(), "amountSpread field not initialized", "setting default");
			amountSpread = new TwoPointFunction(0.0);
			integrity = false;
		}
		
		if(days == null){
			Saga.severe(getClass(), "days field not initialized", "setting default");
			days = new TwoPointFunction(0.0);
			integrity = false;
		}

		if(daysSpread == null){
			Saga.severe(getClass(), "daysSpread field not initialized", "setting default");
			daysSpread = new TwoPointFunction(0.0);
			integrity = false;
		}
		
		if(dealsPerPlayer == null){
			Saga.severe(getClass(), "dealsPerPlayer field not initialized", "setting default");
			dealsPerPlayer = new TwoPointFunction(0.5);
			integrity=false;
		}

		if(dealsCreatePerPlayer == null){
			Saga.severe(getClass(), "dealsCreatePerPlayer field not initialized", "setting default");
			dealsCreatePerPlayer = new TwoPointFunction(0.5);
			integrity=false;
		}

		if(automBuyMult == null){
			Saga.severe(getClass(), "automBuyMult field not initialized", "setting default");
			automBuyMult = 1.0;
			integrity=false;
		}

		if(automSellMult == null){
			Saga.severe(getClass(), "automSellMult field not initialized", "setting default");
			automSellMult = 1.0;
			integrity=false;
		}
		
		if(exchangeDistance == null){
			Saga.severe(getClass(), "exchangeDistance field not initialized", "setting default");
			exchangeDistance = 10.0;
			integrity=false;
		}
		
		if(guardianRuneRechargeCost == null){
			Saga.severe(getClass(), "guardianRuneRechargeCost field not initialized", "setting default");
			guardianRuneRechargeCost = 1000.0;
			integrity=false;
		}
		
		if(skillUpgradeCost == null){
			Saga.severe(getClass(), "skillUpgradeCost field failed to initialize", "setting default");
			skillUpgradeCost= new TwoPointFunction(10000.0);
			integrity=false;
		}
		
		if(respecCost == null){
			Saga.severe(getClass(), "respecCost field failed to initialize", "setting default");
			respecCost= new TwoPointFunction(10000.0);
			integrity=false;
		}
		
		if(chunkGroupRenameCost == null){
			Saga.severe(getClass(), "chunkGroupRenameCost field failed to initialize", "setting default");
			chunkGroupRenameCost= 1000.0;
			integrity=false;
		}
		
		if(factionRenameCost == null){
			Saga.severe(getClass(), "factionRenameCost field failed to initialize", "setting default");
			factionRenameCost= 1000.0;
			integrity=false;
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
	
	// Skills:
	/**
	 * Gets the skill coin cost.
	 * 
	 * @param currentMultiplier current skill multiplier
	 * @return coin cost
	 */
	public Double getSkillCoinCost(Integer currentMultiplier) {
		return skillUpgradeCost.value(currentMultiplier);
	}
	
	/**
	 * Gets the skill coin cost.
	 * 
	 * @param level player level
	 * @return coin cost
	 */
	public Double getRespecCost(Integer level) {
		return respecCost.value(level);
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
	 * Loads the configuration.
	 * 
	 * @return experience configuration
	 */
	public static EconomyConfiguration load(){
		
		
		boolean integrityCheck = true;
		
		// Load:
		EconomyConfiguration config;
		try {
			config = WriterReader.readEconomyConfig();
		} catch (FileNotFoundException e) {
			Saga.severe(EconomyConfiguration.class, "file not found", "loading defaults");
			config = new EconomyConfiguration();
			integrityCheck = false;
		} catch (IOException e) {
			Saga.severe(EconomyConfiguration.class, "failed to load", "loading defaults");
			config = new EconomyConfiguration();
			integrityCheck = false;
		} catch (JsonParseException e) {
			Saga.severe(EconomyConfiguration.class, "failed to parse", "loading defaults");
			Saga.info("Parse message :" + e.getMessage());
			config = new EconomyConfiguration();
			integrityCheck = false;
		}
		
		// Integrity check and complete:
		integrityCheck = config.complete() && integrityCheck;
		
		// Write default if integrity check failed:
		if (!integrityCheck) {
			Saga.severe(EconomyConfiguration.class, "integrity check failed", "writing default fixed version");
			try {
				WriterReader.writeEconomyConfig(config, WriteReadType.CONFIG_DEFAULTS);
			} catch (IOException e) {
				Saga.severe(EconomyConfiguration.class, "write failed", "ignoring write");
				Saga.info("Write fail cause:" + e.getClass().getSimpleName() + ":" + e.getMessage());
			}
		}
		
		// Set instance:
		instance = config;
		
		return config;
		
		
	}
	
	/**
	 * Unloads the instance.
	 * 
	 */
	public static void unload(){
		instance = null;
	}
	
	public static void main(String[] args) {


		for (int i = 0; i < 100; i++) {
			System.out.println(nextGaussian(20.0, 4.0));
		}
		
		
	}
	
}
