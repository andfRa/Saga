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

import org.bukkit.Material;
import org.saga.Clock;
import org.saga.Clock.HourTicker;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.config.AttributeConfiguration;
import org.saga.config.BalanceConfiguration;
import org.saga.config.ChunkGroupConfiguration;
import org.saga.config.ExperienceConfiguration;
import org.saga.player.SagaPlayer;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.utility.MathUtil;

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
	 * Guardian rune res.
	 */
	private Integer guardRuneRestores;

	/**
	 * Guardian stone fixes.
	 */
	private Integer guardRuneRecharges;

	
	
	
	
	/**
	 * Ability usage.
	 */
	private Hashtable<String, Integer> abilityUsage;

	/**
	 * Ability awarded experience.
	 */
	private Hashtable<String, Double> abilityExp;

	
	
	
	
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
			Saga.severe(getClass(), "startDate field failed to initialize", "setting default");
			startDate = System.currentTimeMillis();
			integrity=false;
		}
		
		if(guardRuneRestores == null){
			Saga.severe(getClass(), "guardRuneRestores field failed to initialize", "setting default");
			guardRuneRestores = 0;
			integrity=false;
		}
		
		if(guardRuneRecharges == null){
			Saga.severe(getClass(), "guardRuneRecharges field failed to initialize", "setting default");
			guardRuneRecharges = 0;
			integrity=false;
		}
		
		if(abilityUsage == null){
			Saga.severe(getClass(), "abilityUsage field failed to initialize", "setting default");
			abilityUsage = new Hashtable<String, Integer>();
			integrity=false;
		}
		
		if(abilityExp == null){
			Saga.severe(getClass(), "abilityExp field failed to initialize", "setting default");
			abilityExp = new Hashtable<String, Double>();
			integrity=false;
		}
		
		if(xrayStatistics == null){
			Saga.severe(getClass(), "xrayStatistics field failed to initialize", "setting default");
			xrayStatistics = new Hashtable<String, Hashtable<Material,Integer>>();
			integrity=false;
		}
		
		if(blockDataChanges == null){
			Saga.severe(getClass(), "blockDataChanges field failed to initialize", "setting default");
			blockDataChanges = 0;
			integrity=false;
		}
		
		if(playerLevels == null){
			SagaLogger.severe(getClass(), "playerLevels");
			playerLevels = new Hashtable<String, Integer>();
			integrity=false;
		}

		if(playerAttributes == null){
			SagaLogger.severe(getClass(), "playerAttributes");
			playerAttributes = new Hashtable<String, Hashtable<String,Integer>>();
			integrity=false;
		}

		if(playerSellCoins == null){
			Saga.severe(getClass(), "playerSellCoins field failed to initialize", "setting default");
			playerSellCoins = new Hashtable<String, Hashtable<Material,Double>>();
			integrity=false;
		}

		if(playerBuyCoins == null){
			Saga.severe(getClass(), "playerBuyCoins field failed to initialize", "setting default");
			playerBuyCoins = new Hashtable<String, Hashtable<Material,Double>>();
			integrity=false;
		}

		if(playerSellAmount == null){
			Saga.severe(getClass(), "playerSellAmount field failed to initialize", "setting default");
			playerSellAmount = new Hashtable<String, Hashtable<Material,Integer>>();
			integrity=false;
		}

		if(playerBuyAmount == null){
			Saga.severe(getClass(), "playerBuyAmount field failed to initialize", "setting default");
			playerBuyAmount = new Hashtable<String, Hashtable<Material,Integer>>();
			integrity=false;
		}
		
		if(foundVeins == null){
			Saga.severe(getClass(), "foundVeins field failed to initialize", "setting default");
			foundVeins = new Hashtable<String, Hashtable<Material,Integer>>();
			integrity=false;
		}
		
		if(expGained == null){
			SagaLogger.nullField(getClass(), "expGained");
			expGained = new Hashtable<String, Hashtable<String,Double>>();
		}
		
		return integrity;
		
		
	}
	
	/**
	 * Resets the statistics.
	 * 
	 */
	public void reset() {

		
		guardRuneRestores = 0;
		guardRuneRecharges = 0;
		
		startDate = System.currentTimeMillis();
		
		abilityExp = new Hashtable<String, Double>();
		abilityUsage = new Hashtable<String, Integer>();
		
		xrayStatistics = new Hashtable<String, Hashtable<Material,Integer>>();
		blockDataChanges = 0;
		
		playerLevels = new Hashtable<String, Integer>();
		playerAttributes = new Hashtable<String, Hashtable<String,Integer>>();
		
		playerBuyCoins = new Hashtable<String, Hashtable<Material,Double>>();
		playerSellCoins = new Hashtable<String, Hashtable<Material,Double>>();
		playerBuyAmount = new Hashtable<String, Hashtable<Material,Integer>>();
		playerSellAmount = new Hashtable<String, Hashtable<Material,Integer>>();
		
		foundVeins = new Hashtable<String, Hashtable<Material,Integer>>();
		
		expGained = new Hashtable<String, Hashtable<String,Double>>();
		
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
		if(ageDays < BalanceConfiguration.config().statisticsUpdateAge) return;
		
		Saga.info("Resetting statistics.");
		
		archive();
		
		reset();
		
		
	}

	

	
	// Abilities:
	/**
	 * Gets ability uses.
	 * 
	 * @param abilityName ability name
	 * @return uses
	 */
	public Integer getAbilityUses(String abilityName) {

		Integer value = abilityUsage.get(abilityName);
		if(value == null) value = 0;
		
		return value;
		
	}

	/**
	 * Gets ability rewarded experience.
	 * 
	 * @param abilityName ability name
	 * @return experience
	 */
	public Double getAbilityExp(String abilityName) {

		Double value = abilityExp.get(abilityName);
		if(value == null) value = 0.0;
		
		return value;
		
	}
	
	
	// Guardian stones:
	/**
	 * Gets the guardStoneBreaks.
	 * 
	 * @return the guardStoneBreaks
	 */
	public Integer getGuardStoneBreaks() {
		return guardRuneRestores;
	}
	
	/**
	 * Gets the guardStoneFixes.
	 * 
	 * @return the guardStoneFixes
	 */
	public Integer getGuardStoneFixes() {
		return guardRuneRecharges;
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
	 * Called when a guardian stone restored items.
	 * 
	 */
	public void onGuardanRuneRestore() {

		guardRuneRestores++;
		
	}
	
	/**
	 * Called when a guardian stone is recharged.
	 * 
	 */
	public void onGuardanRuneRecharge() {

		guardRuneRecharges++;
		
	}

	/**
	 * Called when a player uses his ability.
	 * 
	 * @param abilityName ability name
	 * @param rewardedExp exp rewarded
	 */
	public void onAbilityUse(String abilityName, Double rewardedExp) {

		
		// Upgrades:
		Integer uses = abilityUsage.get(abilityName);
		if(uses == null) uses = 0;
		abilityUsage.put(abilityName, uses + 1);
		
		// Upgrade level costs:
		Double expReward = abilityExp.get(abilityName);
		if(expReward == null) expReward = 0.0;
		abilityExp.put(abilityName, expReward + rewardedExp);

		
	}
	
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
	/**
	 * Gets the histogram representing levels.
	 * 
	 * @param width width
	 * @param normalize normalization
	 * @param multiplier multiplier
	 * @return histogram data
	 */
	public Double[] getLevelHistogram(Integer width, boolean normalize) {

		
		if(width < 1) width = 1;
		
		Double step = (width.doubleValue() - 1.0) / ExperienceConfiguration.config().maximumLevel.doubleValue();

		Double[] histogram = new Double[width];
		for (int i = 0; i < histogram.length; i++) {
			histogram[i] = 0.0;
		}
		
		Collection<Integer> levels = playerLevels.values();
		for (Integer level : levels) {
			
			if(level > ExperienceConfiguration.config().maximumLevel) continue;
			
			Integer index = new Double(level.doubleValue() * step).intValue();
			histogram[index] = histogram[index] + 1;
					
		}
		
		if(normalize){
			
			Double sum = 0.0;
			for (int i = 0; i < histogram.length; i++) {
				sum += histogram[i];
			}
			
			if(sum < 1) sum = 1.0;
			
			for (int i = 0; i < histogram.length; i++) {
				histogram[i] = histogram[i] / sum;
			}
			
		}
		
		return histogram;
		
		
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
	
	private Collection<Double> getAllVeinRatios(Material material) {

		
		Collection<Double> ratios = new ArrayList<Double>();
		ArrayList<String> names = getVeinFoundPlayers();
		
		for (String name : names) {
			ratios.add(getVeinRatio(name, material));
		}
		
		return ratios;
		
		
	}
	
	public Double[] getVeinHistogram(Material material, Integer width, boolean normalize) {

		
		if(width < 1) width = 1;
		
		Collection<Double> ratios = getAllVeinRatios(material);
		Double maxRatio = MathUtil.max(ratios);
		if(maxRatio < 0.00001) maxRatio = 0.00001;
		
		Double step = (width.doubleValue() - 1.0) / maxRatio;

		Double[] histogram = new Double[width];
		for (int i = 0; i < histogram.length; i++) {
			histogram[i] = 0.0;
		}
		
		for (Double ratio : ratios) {
			
			if(ratio == 0) continue;
			
			System.out.println("adding value:" + ratio + " to " + new Double(ratio * step).intValue());
			
			Integer index = new Double(ratio * step).intValue();
			histogram[index] = histogram[index] + 1;
					
		}
		
		if(normalize){
			
			Double sum = 0.0;
			for (int i = 0; i < histogram.length; i++) {
				sum += histogram[i];
			}
			
			if(sum < 1) sum = 1.0;
			
			for (int i = 0; i < histogram.length; i++) {
				histogram[i] = histogram[i] / sum;
			}
			
		}
		
		return histogram;
		
		
	}
	
	
	
	
	// Level and attributes:
	public void setLevel(SagaPlayer sagaPlayer) {

		playerLevels.put(sagaPlayer.getName(), sagaPlayer.getLevel());

	}
	
	public void setAttribute2(SagaPlayer sagaPlayer, String attribute, Integer score) {

		
		Hashtable<String, Integer> players = playerAttributes.get(attribute);
		if(players == null){
			players = new Hashtable<String, Integer>();
			playerAttributes.put(attribute, players);
		}
		
		players.put(sagaPlayer.getName(), score);
		
		
	}
	
	public void setAttributes(SagaPlayer sagaPlayer) {

		
		ArrayList<String> attributes = AttributeConfiguration.config().getAttributeNames();
		for (String attribute : attributes) {
			
			Integer score = sagaPlayer.getAttributeScore(attribute);
			if(score <= 0) continue;
			setAttribute2(sagaPlayer, attribute, score);
			
		}
		
		
	}
	
	public Integer getAttributeScoreTotal(String attribute) {

		
		Hashtable<String, Integer> players = playerAttributes.get(attribute);
		if(players == null) players = new Hashtable<String, Integer>();
		
		Integer totalScore = 0;
		Collection<Integer> scores = players.values();
		for (Integer score : scores) {
			totalScore += score;
		}
		
		return totalScore;
		
		
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

	
	
	
	// Load unload:
	/**
	 * Loads the manager.
	 * 
	 * @return experience configuration
	 */
	public static StatisticsManager load(){

		
		// Inform:
		Saga.info("Loading statistics.");
		
		boolean integrity = true;
		
		// Load:
		StatisticsManager manager;
		try {
			
			manager = WriterReader.read(Directory.STATISTICS, "last" , StatisticsManager.class);
			
		} catch (FileNotFoundException e) {
			
			manager = new StatisticsManager("");
			
		} catch (IOException e) {
			
			Saga.severe(StatisticsManager.class, "failed to load", "loading defaults");
			manager = new StatisticsManager("");
			integrity = false;
			
		} catch (JsonParseException e) {
			
			Saga.severe(StatisticsManager.class, "failed to parse", "loading defaults");
			Saga.info("Parse message :" + e.getMessage());
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
		Saga.info("Saving statistics.");
		
		try {
			
			WriterReader.write(Directory.STATISTICS, "last", instance);
			
		} catch (IOException e) {
			
			Saga.severe(StatisticsManager.class, "write failed", "ignoring write");
			Saga.info("Write failure cause:" + e.getClass().getSimpleName() + ":" + e.getMessage());
			
		}
		
	}
	
	/**
	 * Archives the statistics.
	 * 
	 */
	public void archive(){

		// Inform:
		Saga.info("Archiving statistics.");
		
		try {
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			String name = dateFormat.format(GregorianCalendar.getInstance().getTime());
			
			WriterReader.write(Directory.STATISTICS, name, instance);
			
		} catch (IOException e) {
			
			SagaLogger.severe(ChunkGroupConfiguration.class, "failed to read statistics: " + e.getClass().getSimpleName());
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
		Saga.info("Unloading statistics.");
		
		// Clock:
		Clock.clock().registerHourTick(instance);
		
		save();
		
		instance = null;
		
		
	}
	
	
}
