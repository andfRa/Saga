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
import org.saga.Clock;
import org.saga.Clock.HourTicker;
import org.saga.SagaLogger;
import org.saga.abilities.Ability;
import org.saga.chunks.ChunkBundle;
import org.saga.config.AttributeConfiguration;
import org.saga.config.GeneralConfiguration;
import org.saga.config.SettlementConfiguration;
import org.saga.messages.GeneralMessages;
import org.saga.player.SagaPlayer;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.utility.text.RomanNumeral;
import org.saga.utility.text.TextUtil;

import com.google.gson.JsonParseException;

public class StatisticsManager implements HourTicker{


	/**
	 * Instance of the configuration.
	 */
	transient private static StatisticsManager instance;
	
	/**
	 * Gets the instance.
	 * 
	 * @return instance
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
		
		if(foundVeins == null){
			SagaLogger.nullField(getClass(), "foundVeins");
			foundVeins = new Hashtable<String, Hashtable<Material,Integer>>();
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
		
		foundVeins = new Hashtable<String, Hashtable<Material,Integer>>();
		
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
	public void clockHourTick() {
		
		
		// Check if a day has passed:
		Integer ageDays = new Double(calcStatisticsAge() / (60.0 * 60.0 * 1000.0)).intValue();
		if(ageDays < GeneralConfiguration.config().statisticsUpdateAge) return;
		
		SagaLogger.info("Resetting statistics.");
		
		archive();
		
		reset();
		
		
	}


	
	// X-ray:
	/**
	 * Adds ore mined.
	 * 
	 * @param name player name
	 * @param material material
	 * @param amount amount
	 */
	public void addOreMined(String name, Material material, Integer amount) {

		Hashtable<Material, Integer> blocks = xrayStatistics.get(name);
		if(blocks == null) blocks = new Hashtable<Material, Integer>();
	
		Integer oldAmount = blocks.get(material);
		if(oldAmount == null) oldAmount = 0;
		
		blocks.put(material, oldAmount + amount);
		xrayStatistics.put(name, blocks);
		
	}
	
	/**
	 * Gets ore mined.
	 * 
	 * @param name player name
	 * @param material material
	 * @return amount mined
	 */
	public Integer getOreMined(String name, Material material) {

		Hashtable<Material, Integer> blocks = xrayStatistics.get(name);
		if(blocks == null) return 0;
	
		Integer amount = blocks.get(material);
		if(amount == null) return 0;
		
		return amount;
		
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
	 * Called when xray statistics are updated.
	 * 
	 * @param name player name
	 * @param material material
	 * @param amount amount
	 */
	public void onXrayStatisticsUpdate(String name, Material material, Integer amount) {

		addOreMined(name, material, amount);
		
	}

	/**
	 * Called when a block data is changed.
	 * 
	 */
	public void onBlockDataChange() {

		blockDataChanges++;
		
	}

	
	
	
	// X-ray:
	/**
	 * Gets all players for who xray statistics exist.
	 * 
	 * @return players with xray statistics
	 */
	public ArrayList<String> getXrayPlayers() {
		
		return new ArrayList<String>(xrayStatistics.keySet());
		
	}
	
	/**
	 * Gets a ratio for the given material.
	 * 
	 * @param name player name
	 * @param material material
	 * @return ratio
	 */
	public Double getOreRatio(String name, Material material) {

		
		Double materialAmount = getOreMined(name, material).doubleValue();
		Double stoneAmount = getOreMined(name, Material.STONE).doubleValue();
		
		// Avoid infinity:
		if(stoneAmount == 0.0) stoneAmount = 0.00000000001;
		
		return materialAmount / stoneAmount;
		
		
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

		
		Hashtable<Material, Integer> blocks = foundVeins.get(name);
		if(blocks == null) blocks = new Hashtable<Material, Integer>();
	
		Integer oldAmount = blocks.get(material);
		if(oldAmount == null) oldAmount = 0;
		
		blocks.put(material, oldAmount + 1);
		foundVeins.put(name, blocks);
		
		
	}
	
	public Integer getFoundVeins(String name, Material material) {

		Hashtable<Material, Integer> blocks = foundVeins.get(name);
		if(blocks == null) return 0;
	
		Integer amount = blocks.get(material);
		if(amount == null) return 0;
		
		return amount;
		
	}
	
	public ArrayList<String> getVeinFoundPlayers() {
		
		return new ArrayList<String>(foundVeins.keySet());
		
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
		ArrayList<String> names = getVeinFoundPlayers();
		
		for (String name : names) {
			ratios.add(getVeinRatio(name, material));
		}
		
		return ratios.toArray(new Double[ratios.size()]);
		
		
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
	
	public void setBuildings(ChunkBundle chunkBundle) {

		
		Collection<String> bldgNames = SettlementConfiguration.config().getBuildingNames();
		
		
		for (String bldgName : bldgNames) {
		
			String key = "buildings" + "." + "set" + "." + bldgName + "." + chunkBundle.getId();
			clearValue(key);
			
			int count = chunkBundle.getBuildings(bldgName).size();
			if(count == 0) continue;

			setValue(key, count);
			
		}
		
		
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
		
		boolean integrity = true;
		
		// Load:
		StatisticsManager manager;
		try {
			
			manager = WriterReader.read(Directory.STATISTICS, "last" , StatisticsManager.class);
			
		} catch (FileNotFoundException e) {
			
			manager = new StatisticsManager("");
			manager.reset();
			
		} catch (IOException e) {
			
			SagaLogger.severe(StatisticsManager.class, "failed to load");
			manager = new StatisticsManager("");
			integrity = false;
			
		} catch (JsonParseException e) {
			
			SagaLogger.severe(StatisticsManager.class, "failed to parse");
			SagaLogger.info("Parse message :" + e.getMessage());
			manager = new StatisticsManager("");
			integrity = false;
			
		}
		
		// Integrity check and complete:
		integrity = manager.complete() && integrity;
		
		
		// Set instance:
		instance = manager;
		
		// Clock:
		Clock.clock().registerHourTick(instance);
		
		return manager;
		
		
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
		
		// Clock:
		Clock.clock().registerHourTick(instance);
		
		save();
		
		instance = null;
		
		
	}
	
	
	public static void main(String[] args) {
		
	}
	
}
