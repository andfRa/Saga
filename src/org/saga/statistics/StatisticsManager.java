package org.saga.statistics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityDeathEvent;
import org.saga.Clock;
import org.saga.Clock.HourTicker;
import org.saga.Saga;
import org.saga.config.BalanceConfiguration;
import org.saga.player.SagaPlayer;
import org.saga.utility.WriterReader;

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
	 * Class kills.
	 */
	private Hashtable<String, Hashtable<String, Integer>> classKills;

	/**
	 * Skill upgrades.
	 */
	private Hashtable<String, Integer> skillUpgrades;

	/**
	 * Coins used for upgrades.
	 */
	private Hashtable<String, Double> skillUpgradeCoins;

	/**
	 * Guardian stone breaks.
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
	 * Experience awarded by proficiencies.
	 */
	private Hashtable<String, Integer> profExp; 
	
	/**
	 * Players that were awarded proficiency experience.
	 */
	private Hashtable<String, HashSet<String>> profExpPlayers; 

	/**
	 * Players experience.
	 */
	private Hashtable<String, Hashtable<String,Double>> exp; 

	/**
	 * Player levels.
	 */
	private Hashtable<String, Integer> playerLevels; 
	
	
	/**
	 * Last date.
	 */
	private Long startDate = null;
	
	
	// Initialization:
	/**
	 * Initializes.
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
		
		if(classKills == null){
			Saga.severe(getClass(), "classKills field failed to initialize", "setting default");
			classKills = new Hashtable<String, Hashtable<String,Integer>>();
			integrity=false;
		}
		
		if(skillUpgrades == null){
			Saga.severe(getClass(), "skillUpgrades field failed to initialize", "setting default");
			skillUpgrades = new Hashtable<String, Integer>();
			integrity=false;
		}
		
		if(skillUpgradeCoins == null){
			Saga.severe(getClass(), "skillUpgradeCoins field failed to initialize", "setting default");
			skillUpgradeCoins = new Hashtable<String, Double>();
			integrity=false;
		}
		
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
			Saga.severe(getClass(), "oreMining field failed to initialize", "setting default");
			xrayStatistics = new Hashtable<String, Hashtable<Material,Integer>>();
			integrity=false;
		}
		
		if(blockDataChanges == null){
			Saga.severe(getClass(), "blockDataChanges field failed to initialize", "setting default");
			blockDataChanges = 0;
			integrity=false;
		}
		
		if(profExp == null){
			Saga.severe(getClass(), "profExp field failed to initialize", "setting default");
			profExp = new Hashtable<String, Integer>();
			integrity=false;
		}
		
		if(profExpPlayers == null){
			Saga.severe(getClass(), "profExpPlayers field failed to initialize", "setting default");
			profExpPlayers = new Hashtable<String, HashSet<String>>();
			integrity=false;
		}
	
		if(exp == null){
			Saga.severe(getClass(), "exp field failed to initialize", "setting default");
			exp = new Hashtable<String, Hashtable<String,Double>>();
			integrity=false;
		}
		
		if(playerLevels == null){
			Saga.severe(getClass(), "playerLevels field failed to initialize", "setting default");
			playerLevels = new Hashtable<String, Integer>();
			integrity=false;
		}
		
		return integrity;
		
		
	}
	
	/**
	 * Resets the statistics.
	 * 
	 */
	public void reset() {

		
		classKills = new Hashtable<String, Hashtable<String,Integer>>();
		skillUpgrades = new Hashtable<String, Integer>();
		skillUpgradeCoins = new Hashtable<String, Double>();
		guardRuneRestores = 0;
		guardRuneRecharges = 0;
		startDate = System.currentTimeMillis();
		abilityExp = new Hashtable<String, Double>();
		abilityUsage = new Hashtable<String, Integer>();
		xrayStatistics = new Hashtable<String, Hashtable<Material,Integer>>();
		blockDataChanges = 0;
		profExp = new Hashtable<String, Integer>();
		profExpPlayers = new Hashtable<String, HashSet<String>>();
		exp = new Hashtable<String, Hashtable<String,Double>>();
		playerLevels = new Hashtable<String, Integer>();
		
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
	
	
	// Kills:
	/**
	 * Adds a class kill.
	 * 
	 * @param attacker attacker
	 * @param defender defender
	 */
	private void addClassKill(String attacker, String defender) {

		Hashtable<String, Integer> allKills = classKills.get(attacker);
		if(allKills == null) allKills = new Hashtable<String, Integer>();
		classKills.put(attacker, allKills);
		
		Integer kills = allKills.get(defender);
		if(kills == null) kills = 0;
		
		kills++;
		
		allKills.put(defender, kills);
		
	}
	
	/**
	 * Gets class kills.
	 * 
	 * @return class kills
	 */
	public Hashtable<String, Hashtable<String, Integer>> getClassKills() {
		return classKills;
	}
	
	/**
	 * Gets the kills by the class.
	 * 
	 * @param attacker attacker class
	 * @param defender defender class
	 * @return kills
	 */
	public Integer getClazzKills(String attacker, String defender) {

		Hashtable<String, Integer> allKills = classKills.get(attacker);
		if(allKills == null) return 0;
		
		Integer kills = allKills.get(defender);
		if(kills == null) return 0;
		
		return kills;
		
	}

	
	// Skills:
	/**
	 * Gets skill upgrades.
	 * 
	 * @param skillName skill name
	 * @return upgrades
	 */
	public Integer getSkillUpgrades(String skillName) {
		
		Integer value = skillUpgrades.get(skillName);
		if(value == null) value = 0;
		
		return value;
		
	}
	
	/**
	 * Gets skill upgrade coin costs.
	 * 
	 * @param skillName skill name
	 * @return upgrade coin costs
	 */
	public Double getSkillUpgradeCoins(String skillName) {
		
		Double value = skillUpgradeCoins.get(skillName);
		if(value == null) value = 0.0;
		
		return value;
		
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
	
	
	// Xray:
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
	 * Called when a player kills a player.
	 * 
	 * @param attacker attacker
	 * @param defender defender
	 */
	public void onPlayerKillPlayer(SagaPlayer attacker, SagaPlayer defender, EntityDeathEvent event) {

		
		// Classes:
		String attackerClass = "none";
		String defenderClass = "none";

		if(attacker.getClazz() != null){
			attackerClass = attacker.getClazz().getName();
		}

		if(defender.getClazz() != null){
			defenderClass = defender.getClazz().getName();
		}
		
		addClassKill(attackerClass, defenderClass);
		
		
	}
	
	/**
	 * Called when a player upgrades his skill.
	 * 
	 * @param skillName skill name
	 * @param levelCost level cost
	 * @param coinCost coin cost
	 */
	public void onSkillUpgrade(String skillName, Double coinCost) {

		
		// Upgrades:
		Integer upgrades = skillUpgrades.get(skillName);
		if(upgrades == null) upgrades = 0;
		skillUpgrades.put(skillName, upgrades + 1);
		
		// Upgrade coin costs:
		Double upgradeCoins = skillUpgradeCoins.get(skillName);
		if(upgradeCoins == null) upgradeCoins = 0.0;
		skillUpgradeCoins.put(skillName, upgradeCoins + coinCost);
		
		
	}
	

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

	/**
	 * Called when experience is awarded for skill block breaking.
	 * 
	 * @param skillName ability name
	 * @param exp experience awarded
	 * @param sagaPlayer player
	 */
	public void onSkillBlockExp(String skillName, Integer exp, SagaPlayer sagaPlayer) {

		profExp.put(skillName, getBlockSkillExperience(skillName) + exp);
		
		HashSet<String> players = getBlockSkillPlayers(skillName);
		players.add(sagaPlayer.getName());
		profExpPlayers.put(skillName, players);
		
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
	
	/**
	 * Gets the experience awarded for block breaks.
	 * 
	 * @param name skill name
	 * @return experience awarded
	 */
	public Integer getBlockSkillExperience(String name) {
		
		Integer exp = profExp.get(name);
		if(exp == null) exp = 0;
		
		return exp;
		
	}
	
	/**
	 * Gets all skills
	 * 
	 * @return
	 */
	public ArrayList<String> getBlockSkills() {

		return new ArrayList<String>(profExp.keySet());
		
	}
	
	/**
	 * Gets all players that used a block skill.
	 * 
	 * @return players who used a block skill.
	 */
	public HashSet<String> getBlockSkillPlayers(String skillName) {

		HashSet<String> players = profExpPlayers.get(skillName);
		if(players == null) players = new HashSet<String>();
		
		return players;
		
	}

	/**
	 * Counts all players that used a block skill.
	 * 
	 * @return count of players who used a block skill.
	 */
	public Integer countBlockSkillPlayers(String skillName) {

		HashSet<String> players = profExpPlayers.get(skillName);
		if(players == null) return 0;
		
		return players.size();
		
	}
	
	
	// Experience:
	/**
	 * Called when a player receives experience from a source.
	 * 
	 * @param sagaPlayer saga player
	 * @param source source
	 * @param amount amount received
	 */
	public void onExp(SagaPlayer sagaPlayer, String source, Double amount) {

		
		Hashtable<String, Double> playerExp = exp.get(sagaPlayer.getName());
		if(playerExp == null){
			playerExp = new Hashtable<String, Double>();
			exp.put(sagaPlayer.getName(), playerExp);
		}
		
		Double sourceExp = playerExp.get(source);
		if(sourceExp == null) sourceExp = 0.0; 
		
		sourceExp += amount;
		playerExp.put(source, sourceExp);
		
		
	}
	
	/**
	 * Called when a player receives experience from a source.
	 * 
	 * @param sagaPlayer saga player
	 * @param proficiency proficiency
	 * @param source source
	 * @param amount amount received
	 */
	public void onExp(SagaPlayer sagaPlayer, String proficiency, String source, Double amount) {
		
		onExp(sagaPlayer, proficiency + "(" + source + ")", amount);
		
	}
	
	/**
	 * Gets the experience for the given source.
	 * 
	 * @param source source
	 * @return experience
	 */
	public Double getSourceExp(String source) {

		
		Collection<Hashtable<String, Double>> souceExps = exp.values();
		
		Double exp = 0.0;
		
		for (Hashtable<String, Double> souceExp : souceExps) {
			
			Double expSinge = souceExp.get(source);
			if(expSinge != null) exp += expSinge;
			
		}
		
		return exp;

		
	}
	
	/**
	 * Counts the users for the given experience source.
	 * 
	 * @param source source
	 * @return users count
	 */
	public Integer countExpUsers(String source) {

		
		Collection<Hashtable<String, Double>> souceExps = exp.values();
		
		Integer sources = 0;
		
		for (Hashtable<String, Double> souceExp : souceExps) {
			
			if(souceExp.get(source) != null) sources += 1;
			
		}
		
		return sources;

		
	}
	
	/**
	 * Gets all experience sources.
	 * 
	 * @return all experience sources
	 */
	public HashSet<String> getExpSources() {

		
		HashSet<String> sources = new HashSet<String>();
		
		Collection<Hashtable<String, Double>> souceExps = exp.values();
		
		for (Hashtable<String, Double> souceExp : souceExps) {
			
			sources.addAll(new HashSet<String>(souceExp.keySet()));
			
		}
		
		return sources;
		
		
	}
	
	
	// Levels:
	/**
	 * Called on level change.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void onLevelChange(SagaPlayer sagaPlayer) {

		playerLevels.put(sagaPlayer.getName(), sagaPlayer.getLevel());

	}
	
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
		
		Double step = (width.doubleValue() - 1.0) / BalanceConfiguration.config().maximumLevel.doubleValue();

		Double[] histogram = new Double[width];
		for (int i = 0; i < histogram.length; i++) {
			histogram[i] = 0.0;
		}
		
		Collection<Integer> levels = playerLevels.values();
		for (Integer level : levels) {
			
			if(level > BalanceConfiguration.config().maximumLevel) continue;
			
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
			
			manager = WriterReader.readLastStatistics();
			
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
			
			WriterReader.writeLastStatistics(instance);
			
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
			
			WriterReader.writeStatisticsArchive(instance, GregorianCalendar.getInstance());
			
		} catch (IOException e) {
			
			Saga.severe(StatisticsManager.class, "write failed", "ignoring write");
			Saga.info("Write failure cause:" + e.getClass().getSimpleName() + ":" + e.getMessage());
			
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
