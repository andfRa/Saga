package org.saga.statistics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.saga.Clock;
import org.saga.Clock.HourTicker;
import org.saga.SagaLogger;
import org.saga.abilities.Ability;
import org.saga.chunks.Bundle;
import org.saga.config.AttributeConfiguration;
import org.saga.config.GeneralConfiguration;
import org.saga.config.SettlementConfiguration;
import org.saga.factions.Faction;
import org.saga.factions.FactionClaimManager;
import org.saga.messages.GeneralMessages;
import org.saga.player.Proficiency;
import org.saga.player.SagaPlayer;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.settlements.Settlement;
import org.saga.utility.text.RomanNumeral;
import org.saga.utility.text.TextUtil;

public class StatisticsManager implements HourTicker{


	/**
	 * Instance of the manager.
	 */
	transient private static StatisticsManager instance;
	
	/**
	 * Gets the manager.
	 * 
	 * @return manager
	 */
	public static StatisticsManager manager() {
		return instance;
	}

	
	
	/**
	 * Ore mining.
	 */
	private Hashtable<String, Hashtable<Material, Integer>> xrayStatistics;

	/**
	 * Block data changes.
	 */
	private Integer blockDataChanges; 

	
	
	
	/**
	 * Players sell coins.
	 */
	private Hashtable<String, Hashtable<Material,Double>> playerSellCoins; 

	/**
	 * Players buy coins.
	 */
	private Hashtable<String, Hashtable<Material,Double>> playerBuyCoins; 

	/**
	 * Players sell amount.
	 */
	private Hashtable<String, Hashtable<Material,Integer>> playerSellAmount; 

	/**
	 * Players buy coins.
	 */
	private Hashtable<String, Hashtable<Material,Integer>> playerBuyAmount; 

	/**
	 * Found veins.
	 */
	private Hashtable<String, Hashtable<Material, Integer>> foundVeins;

	
	/**
	 * Player levels.
	 */
	private Hashtable<String, Integer> playerLevels; 

	/**
	 * Player attributes.
	 */
	private Hashtable<String, Hashtable<String, Integer>> playerAttributes;
	
	/**
	 * Experience gained.
	 */
	private Hashtable<String, Hashtable<String, Double>> expGained;
	
	
	/**
	 * Double values.
	 */
	private Hashtable<String, Double> doubleValues;
	

	
	/**
	 * Last date.
	 */
	private Long startDate = null;
	
	
	// Initialisation:
	/**
	 * Initialises.
	 * 
	 * @param str nothing
	 */
	public StatisticsManager(String str) {
		
		reset();
		
	}
	
	/**
	 * Goes trough all the fields and makes sure everything has been set after gson load.
	 * If not, it fills the field with defaults.
	 * 
	 * @return true if everything was correct.
	 */
	public boolean complete() {
		
		
		boolean integrity = true;
		
		if(startDate == null){
			SagaLogger.nullField(getClass(), "startDate");
			startDate = System.currentTimeMillis();
			integrity=false;
		}
		
		if(xrayStatistics == null){
			SagaLogger.nullField(getClass(), "xrayStatistics");
			xrayStatistics = new Hashtable<String, Hashtable<Material,Integer>>();
			integrity=false;
		}
		
		if(blockDataChanges == null){
			SagaLogger.nullField(getClass(), "blockDataChanges");
			blockDataChanges = 0;
			integrity=false;
		}
		
		if(playerLevels == null){
			SagaLogger.severe(getClass(), "playerLevels");
			playerLevels = new Hashtable<String, Integer>();
			integrity=false;
		}

		if(playerSellCoins == null){
			SagaLogger.nullField(getClass(), "playerSellCoins");
			playerSellCoins = new Hashtable<String, Hashtable<Material,Double>>();
			integrity=false;
		}

		if(playerBuyCoins == null){
			SagaLogger.nullField(getClass(), "playerBuyCoins");
			playerBuyCoins = new Hashtable<String, Hashtable<Material,Double>>();
			integrity=false;
		}

		if(playerSellAmount == null){
			SagaLogger.nullField(getClass(), "playerSellAmount");
			playerSellAmount = new Hashtable<String, Hashtable<Material,Integer>>();
			integrity=false;
		}

		if(playerBuyAmount == null){
			SagaLogger.nullField(getClass(), "playerBuyAmount");
			playerBuyAmount = new Hashtable<String, Hashtable<Material,Integer>>();
			integrity=false;
		}
		
		if(expGained == null){
			SagaLogger.nullField(getClass(), "expGained");
			expGained = new Hashtable<String, Hashtable<String,Double>>();
		}
		
		if(doubleValues == null){
			SagaLogger.nullField(getClass(), "doubleValues");
			doubleValues = new Hashtable<String, Double>();
		}
		
		// Import attributes:
		if(playerAttributes != null){
		
			SagaLogger.info(getClass(), "importing attributes");
			
			Set<String> attributes = playerAttributes.keySet();
			for (String attribute : attributes) {
				
				Hashtable<String, Integer> players = playerAttributes.get(attribute);
				
				Set<String> playerNames = players.keySet();
				
				for (String playerName : playerNames) {
					
					Double score = players.get(playerName).doubleValue();
					
					doubleValues.put("attributes" + "." + attribute + "." + playerName, score);
					
				}
				
				
			}
			
			playerAttributes = null;
			
		}
		
		// Import found veins:
		if(foundVeins != null){
			
			Set<String> players = foundVeins.keySet();
			
			for (String player : players) {
				
				Set<Material> materials = foundVeins.get(player).keySet();
				
				for (Material material : materials) {
					
					Integer veins = foundVeins.get(player).get(material);
					if(veins == 0) continue;
					
					setValue("found_veins" + "." + material.toString() + "." + player, veins);
					
				}
				
			}
			
			foundVeins = null;
			
		}
		
		return integrity;
		
		
	}
	
	/**
	 * Resets the statistics.
	 * 
	 */
	public void reset() {

		
		startDate = System.currentTimeMillis();
		
		xrayStatistics = new Hashtable<String, Hashtable<Material,Integer>>();
		blockDataChanges = 0;
		
		playerLevels = new Hashtable<String, Integer>();
		
		playerBuyCoins = new Hashtable<String, Hashtable<Material,Double>>();
		playerSellCoins = new Hashtable<String, Hashtable<Material,Double>>();
		playerBuyAmount = new Hashtable<String, Hashtable<Material,Integer>>();
		playerSellAmount = new Hashtable<String, Hashtable<Material,Integer>>();
		
		expGained = new Hashtable<String, Hashtable<String,Double>>();
		
		
		doubleValues = new Hashtable<String, Double>();
		
		
	}

	
	// Clock:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.HourTicker#clockHourTick()
	 */
	@Override
	public boolean clockHourTick() {
		
		
		// Check if a day has passed:
		Integer ageDays = new Double(calcStatisticsAge() / (60.0 * 60.0 * 1000.0)).intValue();
		if(ageDays < GeneralConfiguration.config().statisticsUpdateAge) return true;
		
		SagaLogger.info("Resetting statistics.");
		
		archive();
		
		reset();
		
		return true;
		
		
	}

	
	
	// Age:
	/**
	 * Calculates the milliseconds the statistics are active.
	 * 
	 * @return age in milliseconds
	 */
	public long calcStatisticsAge() {

		return System.currentTimeMillis() - startDate;
		
	}
	
	

	// Events:
	/**
	 * Called when a block data is changed.
	 * 
	 */
	public void onBlockDataChange() {

		blockDataChanges++;
		
	}

	
	
	// Block:
	/**
	 * Gets the times the block data was changed.
	 * 
	 * @return times the block data was changed
	 */
	public Integer getBlockDataChanges() {
		return blockDataChanges;
	}

	
	// Levels:
	public Integer[] getLevels() {

		return playerLevels.values().toArray(new Integer[0]);
		
	}
	
	
	
	// Economy:
	public void onPlayerBuy(SagaPlayer sagaPlayer, Material material, Integer amount, Double cost) {

		
		// Coins:
		Hashtable<Material, Double> cMaterials = playerBuyCoins.get(sagaPlayer.getName());
		if(cMaterials == null){
			cMaterials = new Hashtable<Material, Double>();
			playerBuyCoins.put(sagaPlayer.getName(), cMaterials);
		}
		
		Double coins = cMaterials.get(material);
		if(coins == null) coins = 0.0;
		
		coins += cost;
		cMaterials.put(material, coins);
		

		// Coins:
		Hashtable<Material, Integer> aMaterials = playerBuyAmount.get(sagaPlayer.getName());
		if(aMaterials == null){
			aMaterials = new Hashtable<Material, Integer>();
			playerBuyAmount.put(sagaPlayer.getName(), aMaterials);
		}
		
		Integer amounts = aMaterials.get(material);
		if(amounts == null) amounts = 0;
		
		amounts += amount;
		aMaterials.put(material, amounts);
		

	}
	
	public void onPlayerSell(SagaPlayer sagaPlayer, Material material, Integer amount, Double cost) {

		
		// Coins:
		Hashtable<Material, Double> cMaterials = playerSellCoins.get(sagaPlayer.getName());
		if(cMaterials == null){
			cMaterials = new Hashtable<Material, Double>();
			playerSellCoins.put(sagaPlayer.getName(), cMaterials);
		}
		
		Double coins = cMaterials.get(material);
		if(coins == null) coins = 0.0;
		
		coins += cost;
		cMaterials.put(material, coins);
		

		// Coins:
		Hashtable<Material, Integer> aMaterials = playerSellAmount.get(sagaPlayer.getName());
		if(aMaterials == null){
			aMaterials = new Hashtable<Material, Integer>();
			playerSellAmount.put(sagaPlayer.getName(), aMaterials);
		}
		
		Integer amounts = aMaterials.get(material);
		if(amounts == null) amounts = 0;
		
		amounts += amount;
		aMaterials.put(material, amounts);
		

	}
	
	public Integer countBuyPlayers(Material material) {

		return playerBuyCoins.size();
		
	}
	
	public Integer countSellPlayers(Material material) {

		return playerSellCoins.size();
		
	}
	
	
	public Double getBuyCoins(Material material) {

		
		Double coins = 0.0;
		
		Collection<Hashtable<Material, Double>> materials = playerBuyCoins.values();

		for (Hashtable<Material, Double> hashtable : materials) {
			Double matCoins = hashtable.get(material);
			if(matCoins != null) coins += matCoins;
		}
		
		return coins;
		
		
	}
	
	public Integer getBuyAmount(Material material) {

		
		Integer amount = 0;
		
		Collection<Hashtable<Material, Integer>> materials = playerBuyAmount.values();

		for (Hashtable<Material, Integer> hashtable : materials) {
			Integer matAmount = hashtable.get(material);
			if(matAmount != null) amount += matAmount;
		}
		
		return amount;
		
		
	}
	
	public Double getSellCoins(Material material) {

		
		Double coins = 0.0;
		
		Collection<Hashtable<Material, Double>> materials = playerSellCoins.values();

		for (Hashtable<Material, Double> hashtable : materials) {
			Double matCoins = hashtable.get(material);
			if(matCoins != null) coins += matCoins;
		}
		
		return coins;
		
		
	}
	
	public Integer getSellAmount(Material material) {

		
		Integer amount = 0;
		
		Collection<Hashtable<Material, Integer>> materials = playerSellAmount.values();

		for (Hashtable<Material, Integer> hashtable : materials) {
			Integer matAmount = hashtable.get(material);
			if(matAmount != null) amount += matAmount;
		}
		
		return amount;
		
		
	}
	
	public ArrayList<Material> getAllEcoMaterials() {


		HashSet<Material> allMaterials = new HashSet<Material>();

		// Buy materials:
		Collection<Hashtable<Material, Double>> buyMaterials = playerBuyCoins.values();
		for (Hashtable<Material, Double> hashtable : buyMaterials) {
			allMaterials.addAll(hashtable.keySet());
		}

		// Sell materials:
		Collection<Hashtable<Material, Double>> sellMaterials = playerSellCoins.values();
		for (Hashtable<Material, Double> hashtable : sellMaterials) {
			allMaterials.addAll(hashtable.keySet());
		}
		
		ArrayList<Material> sortedMaterials = new ArrayList<Material>(allMaterials);
		
		// Sort:
		Collections.sort(sortedMaterials);
		
		return sortedMaterials;
		
		
	}

	
	
	// X-ray indicator:
	public void addFoundVein(String name, Material material) {

		modifyValue("found_veins" + "." + material.toString() + "." + name, 1);
		
	}
	
	public Integer getFoundVeins(String name, Material material) {

		return (int)getValue("found_veins" + "." + material.toString() + "." + name);
		
	}
	
	public ArrayList<String> getVeinFoundPlayers(Material material) {
		
		
		TreeSet<String> names = getSubCategs("found_veins" + "." + material.toString(), false);
		
		return new ArrayList<String>(names);
		
	}
	
	public Double getVeinRatio(String name, Material material) {

		
		Double materialAmount = getFoundVeins(name, material).doubleValue();
		Double stoneAmount = getFoundVeins(name, Material.STONE).doubleValue();
		
		// Avoid infinity:
		if(stoneAmount == 0.0) stoneAmount = 1.0;
		
		return materialAmount / stoneAmount;
		
		
	}
	
	public Double[] getVeinRatios(Material material) {

		
		Collection<Double> ratios = new ArrayList<Double>();
		ArrayList<String> names = getVeinFoundPlayers(material);
		
		for (String name : names) {
			ratios.add(getVeinRatio(name, material));
		}
		
		return ratios.toArray(new Double[ratios.size()]);
		
		
	}
	
	public void confirmXray(String name, Material material, Double ratio) {

		setValue("xray_confirmed" + "." + material.toString() + "." + name, ratio);
		
	}
	
	public boolean isXrayConfirmed(String name, Material material) {

		return getValue("xray_confirmed" + "." + material.toString() + "." + name) > 0.0;
		
	}
	
	
	
	// Level and attributes:
	public void setLevel(SagaPlayer sagaPlayer) {

		playerLevels.put(sagaPlayer.getName(), sagaPlayer.getLevel());

	}
	
	
	
	// Experience:
	public void addExp(String category, String subcategory, Double amount) {

		
		Hashtable<String, Double> subcats = expGained.get(category);
		if(subcats == null){
			subcats = new Hashtable<String, Double>();
			expGained.put(category, subcats);
		}
		
		Double exp = subcats.get(subcategory);
		if(exp == null) exp = 0.0;

		exp+= amount;
		subcats.put(subcategory, exp);
		

	}
	
	public Hashtable<String, Hashtable<String, Double>> getExpGained() {
		return new Hashtable<String, Hashtable<String,Double>>(expGained);
	}
	
	public Double getExpGained(String category) {
		
		
		Hashtable<String, Double> subcategs = expGained.get(category);
		if(subcategs == null) subcategs = new Hashtable<String, Double>();

		Double exp = 0.0;
		Collection<Double> exps = subcategs.values();
		for (Double amount : exps) {
			exp += amount;
		}
		
		return exp;
		
		
	}
	
	public Double getExpGained(String category, String subcategory) {
		
		
		Hashtable<String, Double> subcateg = expGained.get(category);
		if(subcateg == null) subcateg = new Hashtable<String, Double>();

		Double exp = subcateg.get(subcategory);
		if(exp == null) exp = 0.0;
		
		return exp;
		
		
	}
	
	public ArrayList<String> getExpCategories() {

		
		ArrayList<String> sortedCategs = new ArrayList<String>(expGained.keySet());
		Collections.sort(sortedCategs);
		
		return sortedCategs;
		
		
	}
	
	public ArrayList<String> getExpSubcategories() {

		
		HashSet<String> subcategs = new HashSet<String>();
		
		Collection<Hashtable<String, Double>> allSubcategs = expGained.values();
		for (Hashtable<String, Double> subcateg : allSubcategs) {
			subcategs.addAll(subcateg.keySet());
		}
		
		ArrayList<String> sortedSubcategs = new ArrayList<String>(subcategs);
		Collections.sort(sortedSubcategs);
		
		return sortedSubcategs;
		
		
	}

	
	
	// Values:
	public void modifyValue(String key, Double mod) {

		doubleValues.put(key, getValue(key) + mod);
		
	}
	
	public void modifyValue(String key, Integer mod) {

		modifyValue(key, mod.doubleValue());
		
	}
	
	public void setValue(String key, Double value) {

		doubleValues.put(key, value);
		
	}

	public void setValue(String key, Integer value) {

		setValue(key, value.doubleValue());
		
	}
	
	public double getValue(String key) {

		Double value = doubleValues.get(key);
		if(value == null) return 0.0;
		
		return value;
	
	}
	
	public void clearCateg(String category) {

		
		Set<String> allCategs = doubleValues.keySet();
		
		for (String fullCateg : allCategs) {
			
			if(!fullCateg.startsWith(category + ".")) continue;
		
			doubleValues.remove(fullCateg);
			
		}
		
		
	}
	
	public void clearValue(String key) {

		doubleValues.remove(key);
		
	}
	
	public double getSumValue(String category, boolean ignoreBottom) {

		
		Double sum = getValue(category);
		
		Collection<String> subCategs = getSubCategs(category, ignoreBottom);
		
		for (String subCateg : subCategs) {
			sum += getSumValue(category + "." + subCateg, ignoreBottom);
		}
		
		return sum;
		
		
	}
	
	public static int calcCategDepth(String category) {

		int depth = 0;
		while(category.contains(".")){
			
			category = category.replaceFirst("\\.", "");
			depth++;
			
		}
		
		return depth;
		
	}
	
	public static String formatCategName(String category) {

		
		String[] subCategs = category.split("\\.");
		
		return TextUtil.repeat(GeneralMessages.TAB, calcCategDepth(category)) + subCategs[subCategs.length - 1];
		
		
	}

	public TreeSet<String> getSubCategs(String category, boolean ignoreBottom) {

		
		Set<String> allCategs = doubleValues.keySet();
		TreeSet<String> subCategs = new TreeSet<String>();
		
		for (String fullCateg : allCategs) {
			
			if(fullCateg.length() == 0) continue;
			
			if(!fullCateg.startsWith(category + ".")) continue;
			
			String subCateg = fullCateg.replaceFirst(category + "\\.", "");
			
			if(ignoreBottom){
				int lastIndex = subCateg.lastIndexOf(".");
				if(lastIndex != -1) subCateg = subCateg.substring(0, lastIndex);
			}
			
			String[] subSuperCategs = subCateg.split("\\.");
			
			if(subSuperCategs.length > 1){
				
				String subSuperCateg = subSuperCategs[0];
					
				for (int i = 1; i < subSuperCategs.length; i++) {
					
					if(!subCategs.contains(subSuperCateg)) subCategs.add(subSuperCateg);
					
					subSuperCateg += "." + subSuperCategs[i];
					
				}
				
			}
			
			subCategs.add(subCateg);
			
		}
		
		return subCategs;
		
		
	}
	
	
	
	// Histogram:
	public Double[] createHistogramData(String category) {


		Collection<String> subCategs = StatisticsManager.manager().getSubCategs(category, false);
	
		ArrayList<Double> data = new ArrayList<Double>(); 
		
		for (String subCateg : subCategs) {
			
			data.add(StatisticsManager.manager().getValue(category + "." + subCateg));
			
		}
		
		return data.toArray(new Double[data.size()]);
		
		
	}
	
	
	
	// Updating:
	public void setAttributes(SagaPlayer sagaPlayer) {

		
		ArrayList<String> attributes = AttributeConfiguration.config().getAttributeNames();
		for (String attribute : attributes) {
			
			Integer score = sagaPlayer.getRawAttributeScore(attribute);
			if(score < 1) continue;
			
			setValue("attributes" + "." + attribute + "." + sagaPlayer.getName(), score);
			
		}
		
		
	}
	
	
	public void addGuardRuneRestore(SagaPlayer sagaPlayer) {

		modifyValue("guardrune" + "." + "restore" + "." + sagaPlayer.getName(), 1);
		
	}
	
	public void addGuardRuneRestoreException(SagaPlayer sagaPlayer) {

		modifyValue("guardrune" + "." + "restore_exception" + "." + sagaPlayer.getName(), 1);
		
	}
	
	public void addGuardRuneRecharge(SagaPlayer sagaPlayer) {
		
		modifyValue("guardrune" + "." + "recharge" + "." + sagaPlayer.getName(), 1);
		
	}

	
	public void addAbilityUse(Ability ability) {

		String abilityName = ability.getName() + "." + ability.getName() + " " + RomanNumeral.binaryToRoman(ability.getScore());
		SagaPlayer sagaPlayer = ability.getSagaPlayer();
		
		modifyValue("abilities" + "." + "used" + "." + abilityName  + "." + sagaPlayer.getName(), 1);
		
	}
	
	
	public void setBuildings(Bundle bundle) {

		
		Collection<String> bldgNames = SettlementConfiguration.config().getBuildingNames();
		
		
		for (String bldgName : bldgNames) {
		
			String key = "buildings" + "." + "set" + "." + bldgName + "." + bundle.getId();
			clearValue(key);
			
			int count = bundle.getBuildings(bldgName).size();
			if(count == 0) continue;

			setValue(key, count);
			
		}
		
		
	}
	
	
	public void addWages(Faction faction, Proficiency rank, Double wage) {

		modifyValue("wages.factions." + rank.getName() + "." + faction.getName(), wage);
		
	}
	
	
	public void addBundleClaimed(Faction faction, Bundle bundle) {

		modifyValue("faction_claiming" + "." + "claimed" + "." + faction.getName(), 1);
		
	}

	public void addBundleSeized(Faction faction, Bundle bundle) {

		modifyValue("faction_claiming" + "." + "seized" + "." + faction.getName(), 1);
		
	}
	
	public void addBundleLost(Faction faction, Bundle bundle) {

		modifyValue("faction_claiming" + "." + "lost" + "." + faction.getName(), 1);
		
	}

	public void setBundlesOwned(Faction faction) {

		setValue("faction_claiming" + "." + "owned" + "." + faction.getName(), FactionClaimManager.manager().findSettlements(faction.getId()).length);
		
	}
	
	
	public void setLevel(Faction faction) {

		setValue("factions.levels" + "." + faction.getName(), faction.getLevel());
		
	}
	
	public void setLevel(Settlement settlement) {

		setValue("settlements.levels" + "." + settlement.getName(), settlement.getLevel());
		
	}
	

	public void setWallet(SagaPlayer sagaPlayer) {

		setValue("wallet" + "." + sagaPlayer.getName(), sagaPlayer.getCoins());
		
	}
	
	
	
	// Load unload:
	/**
	 * Loads the manager.
	 * 
	 * @return experience configuration
	 */
	public static StatisticsManager load(){

		
		// Inform:
		SagaLogger.info("Loading statistics.");
		
		// New:
		if(!WriterReader.checkExists(Directory.STATISTICS, "last")){
			
			instance = new StatisticsManager("");
			instance.reset();
			save();
        	
        }
		
		// Load:
		else{
			
			try {
				
				instance = WriterReader.read(Directory.STATISTICS, "last" , StatisticsManager.class);
				
			} catch (FileNotFoundException e) {
				
				instance = new StatisticsManager("");
				instance.reset();
				
			} catch (IOException e) {
				
				SagaLogger.severe(StatisticsManager.class, "failed to load");
				instance = new StatisticsManager("");
				
			} catch (JsonParseException e) {
				
				SagaLogger.severe(StatisticsManager.class, "failed to parse");
				SagaLogger.info("Parse message :" + e.getMessage());
				instance = new StatisticsManager("");
				
			}
			
        }
		
		// Integrity check and complete:
		instance.complete();
		
		// Clock:
		Clock.clock().enableHourTicking(instance);
		
		return instance;
		
		
	}
	
	/**
	 * Saves the statistics.
	 * 
	 */
	public static void save(){

		
		// Inform:
		SagaLogger.info("Saving statistics.");
		
		try {
			
			WriterReader.write(Directory.STATISTICS, "last", instance);
			
		} catch (IOException e) {
			
			SagaLogger.severe(StatisticsManager.class, "write failed");
			SagaLogger.info("Write failure cause:" + e.getClass().getSimpleName() + ":" + e.getMessage());
			
		}
		
		
	}
	
	/**
	 * Archives the statistics.
	 * 
	 */
	public void archive(){

		
		// Inform:
		SagaLogger.info("Archiving statistics.");
		
		try {
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			String name = dateFormat.format(GregorianCalendar.getInstance().getTime());
			
			WriterReader.write(Directory.STATISTICS, name, instance);
			
		} catch (IOException e) {
			
			SagaLogger.severe(SettlementConfiguration.class, "failed to read statistics: " + e.getClass().getSimpleName());
			SagaLogger.info("message: " + e.getMessage());
			
			SagaLogger.severe(StatisticsManager.class, "write failed ");
			SagaLogger.info("Write failure cause:" + e.getClass().getSimpleName() + ":" + e.getMessage());
			e.printStackTrace();
			
		}
		
		
	}
	
	/**
	 * Unloads the statistics.
	 * 
	 */
	public static void unload(){

		
		// Inform:
		SagaLogger.info("Unloading statistics.");
		
		save();
		
		instance = null;
		
		
	}
	
	
}
