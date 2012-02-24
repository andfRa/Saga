package org.saga.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.saga.Saga;
import org.saga.constants.IOConstants.WriteReadType;
import org.saga.economy.TradeDeal;
import org.saga.economy.TradeDeal.TradeDealException;
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
	 * Player initial currency.
	 */
	public Double playerInitialCurrency;
	
	/**
	 * Currency material.
	 */
	public Material currencyItem;

	/**
	 * Currency item worth.
	 */
	public Double currencyItemWorth;

	/**
	 * Currency name.
	 */
	public String currencyName;

	/**
	 * Currency decimal name.
	 */
	public String currencyDecimalName;

	
	/**
	 * Open buy sign title.
	 */
	public String openBuySignTitle;
	
	/**
	 * Closed buy sign title.
	 */
	public String closedBuySignTitle;
	
	/**
	 * Open sell sign title.
	 */
	public String openSellSignTitle;
	
	/**
	 * Closed sell sign title.
	 */
	public String closedSellSignTitle;
	
	/**
	 * Invalid sign title.
	 */
	public String invalidSignTitle;
	
	/**
	 * Invalid material.
	 */
	public String invalidMaterial;
	
	
	// Trading deals:
	/**
	 * Trading deals.
	 */
	public ArrayList<TradeDeal> tradingDeals;
	
	/**
	 * Trade deal relative amount spread.
	 */
	public Double dealAmountSpread;
	
	/**
	 * Trade deal relative transactions spread.
	 */
	public Double dealTransactionsSpread;
	
	/**
	 * Trade deal relative value spread.
	 */
	public Double dealValueSpread;
	
	/**
	 * Trade deal relative days left spread.
	 */
	public Double dealDaysLeftSpread;
	
	/**
	 * Deals per player.
	 */
	private TwoPointFunction dealsPerPlayer;
	
	/**
	 * Deals gain per player.
	 */
	private TwoPointFunction dealsGainPerPlayer;

	
	// Exchange:
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
		
		if(currencyItem == null){
			Saga.severe(getClass(), "currencyItem field not initialized", "setting default");
			currencyItem = Material.GOLD_INGOT;
			integrity = false;
		}
		
		if(playerInitialCurrency == null){
			Saga.severe(getClass(), "playerInitialCurrency field not initialized", "setting default");
			playerInitialCurrency = 0.0;
			integrity = false;
		}
		
//		if(tradingPostInitialCurrency == null){
//			Saga.severe(this, "tradingPostInitialCurrency field not initialized", "setting default");
//			tradingPostInitialCurrency = 0.0;
//			integrity = false;
//		}
		
		if(currencyItemWorth == null){
			Saga.severe(getClass(), "currencyItemWorth field not initialized", "setting default");
			currencyItemWorth = 0.1;
			integrity = false;
		}
		
		if(currencyName == null){
			Saga.severe(getClass(), "currencyName field not initialized", "setting default");
			currencyName = "coins";
			integrity = false;
		}
		
		if(currencyDecimalName == null){
			Saga.severe(getClass(), "currencyDecimalName field not initialized", "setting default");
			currencyDecimalName = "cents";
			integrity = false;
		}
		
		if(openSellSignTitle == null){
			Saga.severe(getClass(), "openSellSignTitle field not initialized", "setting default");
			openSellSignTitle = "=[" + ChatColor.DARK_GREEN + "SELL" + ChatColor.BLACK + "]=";
			integrity=false;
		}
		
		if(closedSellSignTitle == null){
			Saga.severe(getClass(), "closedSellSignTitle field not initialized", "setting default");
			closedSellSignTitle = "=[" + ChatColor.DARK_GRAY + "SELL" + ChatColor.BLACK + "]=";
			integrity=false;
		}
		
		if(invalidSignTitle == null){
			Saga.severe(getClass(), "invalidSignTitle field not initialized", "setting default");
			invalidSignTitle = "=[" + ChatColor.DARK_RED + "INVALID" + ChatColor.BLACK + "]=";
			integrity=false;
		}

		if(openBuySignTitle == null){
			Saga.severe(getClass(), "openBuySignTitle field not initialized", "setting default");
			openBuySignTitle = "=[" + ChatColor.DARK_GREEN + "BUY" + ChatColor.BLACK + "]=";
			integrity=false;
		}
		
		if(closedBuySignTitle == null){
			Saga.severe(getClass(), "closedBuySignTitle field not initialized", "setting default");
			closedBuySignTitle = "=[" + ChatColor.DARK_GRAY + "BUY" + ChatColor.BLACK + "]=";
			integrity=false;
		}
		
		if(invalidMaterial == null){
			Saga.severe(getClass(), "invalidMaterial field not initialized", "setting default");
			invalidMaterial = ChatColor.DARK_RED + "invalid material";
			integrity=false;
		}
		
		if(tradingDeals == null){
			Saga.severe(getClass(), "tradingDeals field not initialized", "adding two examples");
			tradingDeals = new ArrayList<TradeDeal>();
			tradingDeals.add(new TradeDeal(TradeDealType.EXPORT, Material.WOOD, 10, 100, 1.2, 4));
			tradingDeals.add(new TradeDeal(TradeDealType.IMPORT, Material.STONE, 64, 200, 10.4, 5));
			integrity=false;
		}
		for (int i = 0; i < tradingDeals.size(); i++) {
			if(tradingDeals.get(i) == null){
				Saga.severe(getClass(), "tradingDeals field element not initialized", "removing element");
				tradingDeals.remove(i);
				i--;
				continue;
			}
			try {
				tradingDeals.get(i).complete();
			} catch (TradeDealException e) {
				Saga.severe(getClass(), "exception for tradingDeals field element: "+e.getClass().getSimpleName() + ":" + e.getMessage(), "removing element");
				tradingDeals.remove(i);
				i--;
				continue;
			}
		}
		
		if(dealAmountSpread == null){
			Saga.severe(getClass(), "dealAmountSpread field not initialized", "setting default");
			dealAmountSpread = 0.0;
			integrity=false;
		}
		
		if(dealTransactionsSpread == null){
			Saga.severe(getClass(), "dealTransactionsSpread field not initialized", "setting default");
			dealTransactionsSpread = 0.0;
			integrity=false;
		}
		
		if(dealValueSpread == null){
			Saga.severe(getClass(), "dealValueSpread field not initialized", "setting default");
			dealValueSpread = 0.0;
			integrity=false;
		}
		
		if(dealDaysLeftSpread == null){
			Saga.severe(getClass(), "dealDaysLeftSpread field not initialized", "setting default");
			dealDaysLeftSpread = 0.0;
			integrity=false;
		}
		
		if(dealsPerPlayer == null){
			Saga.severe(getClass(), "dealsPerPlayer field not initialized", "setting default");
			dealsPerPlayer = new TwoPointFunction(0.5);
			integrity=false;
		}
		
		if(dealsGainPerPlayer == null){
			Saga.severe(getClass(), "dealsGainPerPlayer field not initialized", "setting default");
			dealsGainPerPlayer = new TwoPointFunction(0.25);
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

	
	// Interaction:
	/**
	 * Gets a random trade deal with uniform distribution.
	 * 
	 * @return random trade deal
	 */
	public TradeDeal nextTradeDeal() {
		return tradingDeals.get(random.nextInt(tradingDeals.size()));
	}
	
	
	// Calculations:
	/**
	 * Calculates trade deals per player.
	 * 
	 * @param playerCount player count
	 * @return deals gain per player
	 */
	public Integer calculateDealsPerPlayer(Integer playerCount) {

		if(playerCount < dealsPerPlayer.getXMin()){
			return 0;
		}
		return dealsPerPlayer.value(playerCount.shortValue()).intValue();
		
	}
	
	/**
	 * Calculates trade deals gain per player.
	 * 
	 * @param playerCount player count
	 * @return deals gain per player
	 */
	public Integer calculateDealsGainPerPlayer(Integer playerCount) {

		if(playerCount < dealsGainPerPlayer.getXMin()){
			System.out.println("only " + playerCount + " players, returning zero gain");
			return 0;
		}
		return dealsGainPerPlayer.value(playerCount.shortValue()).intValue();
		
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
	 * Returns a normal distribution value with maximum on the value and with spread less or equal to value * spreadRelative.
	 * 
	 * @param value
	 * @param spreadRelative
	 * @return
	 */
	public static Double nextGaussian(Double value, Double spreadRelative) {

		
		Double nextRandom = random.nextGaussian();
		while(nextRandom > 1.0){
			nextRandom--;
		}
		while(nextRandom < -1.0){
			nextRandom++;
		}
		
//		if(nextRandom > 1){
//			nextRandom = 1.0;
//		}else if(nextRandom < -1){
//			nextRandom = -1.0;
//		}
		return value * ( 1 + spreadRelative * nextRandom);
		
		
	}
	
	/**
	 * Returns a normal distribution integer value with maximum on the value and with spread less or equal to value * spreadRelative.
	 * 
	 * @param value
	 * @param spreadRelative
	 * @return
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
	
	
	
}
