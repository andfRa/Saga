package org.saga.config;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.saga.Clock.DaytimeTicker.Daytime;
import org.saga.SagaLogger;
import org.saga.buildings.production.SagaItem;
import org.saga.buildings.production.SagaPricedItem;
import org.saga.factions.Faction;
import org.saga.player.Proficiency;
import org.saga.player.SagaPlayer;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.utility.TwoPointFunction;

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
	 * True if economy is enabled.
	 */
	public Boolean enabled;

	
	/**
	 * Player initial coins.
	 */
	public Double playerCoins;

	/**
	 * Coin name.
	 */
	public String coinName;

	
	/**
	 * Exchange distance.
	 */
	public Double exchangeDistance;

	
	/**
	 * Guardian stone price.
	 */
	public Double guardianRuneRechargeCost;

	
	/**
	 * Reset cost.
	 */
	private Double attributeResetCost;


	// Create rename:
	/**
	 * Faction create cost.
	 */
	private Double factionCreateCost;
	
	/**
	 * Settlement create cost.
	 */
	private Double settlementCreateCost;
	
	/**
	 * Faction rename cost.
	 */
	private Double factionRenameCost;
	
	/**
	 * Settlement rename cost.
	 */
	private Double settlementRenameCost;

	
	// Upgrade cost:
	/**
	 * Claim point cost.
	 */
	private Double claimPointCost;
	
	/**
	 * Building point cost.
	 */
	private Double buildPointCost;
	
	
	
	// Faction wages:
	/**
	 * The amount of wage for claim points.
	 */
	private TwoPointFunction factionClaimsPointsWage;

	/**
	 * Wage multiplier for hierarchy levels.
	 */
	private TwoPointFunction factionWageHierarchyMultiplier;

	/**
	 * Time when wages are payed.
	 */
	private Daytime factionWagesTime;
	
	/**
	 * Amount rewarded for a kill for a hierarchy level.
	 */
	private TwoPointFunction factionKillReward;
	
	

	// Trading post:
	/**
	 * Exports.
	 */
	private SagaPricedItem[] exports;
	
	/**
	 * Imports.
	 */
	private SagaPricedItem[] imports;
	
	/**
	 * Trading post automatic exports.
	 */
	private SagaPricedItem[] tradingPostExports;

	
	
	// Options:
	/**
	 * True to enable hooking with other economy plugins.
	 */
	private Boolean enableHooking; 
	
	
	
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
		
		if(enabled == null){
			SagaLogger.nullField(getClass(), "enabled");
			enabled = true;
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
		
		if(attributeResetCost == null){
			SagaLogger.nullField(getClass(), "attributeResetCost");
			attributeResetCost= Double.MAX_VALUE;
		}
		
		if(settlementCreateCost == null){
			SagaLogger.nullField(getClass(), "settlementCreateCost");
			settlementCreateCost = 1000.0;
		}
		
		if(factionCreateCost == null){
			SagaLogger.nullField(getClass(), "factionCreateCost");
			factionCreateCost= 1000.0;
		}
		
		if(settlementRenameCost == null){
			SagaLogger.nullField(getClass(), "settlementRenameCost");
			settlementRenameCost = 1000.0;
		}
		
		if(factionRenameCost == null){
			SagaLogger.nullField(getClass(), "factionRenameCost");
			factionRenameCost= 1000.0;
		}
		
		
		if(claimPointCost == null){
			SagaLogger.nullField(getClass(), "claimPointCost");
			claimPointCost = 0.0;
		}

		if(buildPointCost == null){
			SagaLogger.nullField(getClass(), "buildPointCost");
			buildPointCost = 0.0;
		}

		
		if(factionClaimsPointsWage == null){
			SagaLogger.nullField(getClass(), "factionClaimsPointsWage");
			factionClaimsPointsWage= new TwoPointFunction(0.0);
		}
		factionClaimsPointsWage.complete();
		
		if(factionClaimsPointsWage.getXMin() != 0){
			SagaLogger.warning(getClass(), "factionClaimsPointsWage x1 must be 0 to preserver wages linearity");
		}

		if(factionWageHierarchyMultiplier == null){
			SagaLogger.nullField(getClass(), "factionWageHierarchyMultiplier");
			factionWageHierarchyMultiplier= new TwoPointFunction(0.0);
		}
		factionWageHierarchyMultiplier.complete();
		
		if(factionKillReward == null){
			SagaLogger.nullField(getClass(), "factionKillReward");
			factionKillReward = new TwoPointFunction(0.0);
		}
		factionKillReward.complete();
		
		if(factionWagesTime == null){
			SagaLogger.nullField(getClass(), "factionWagesTime");
			factionWagesTime= Daytime.NONE;
		}
		
		
		if(exports == null){
			SagaLogger.nullField(getClass(), "exports");
			exports = new SagaPricedItem[0];
		}
		for (SagaPricedItem item : exports) {
			item.complete();
		}
		
		if(imports == null){
			SagaLogger.nullField(getClass(), "imports");
			imports = new SagaPricedItem[0];
		}
		for (SagaPricedItem item : imports) {
			item.complete();
		}
		
		if(tradingPostExports == null){
			SagaLogger.nullField(getClass(), "tradingPostExports");
			tradingPostExports = new SagaPricedItem[0];
		}
		for (SagaPricedItem item : tradingPostExports) {
			item.complete();
		}

		
		if(enableHooking == null){
			SagaLogger.nullField(getClass(), "enableHooking");
			enableHooking= true;
		}
		
		
	}

	
	
	// Status:
	/**
	 * Checks if the economy is enabled
	 * 
	 * @return true if enabled
	 */
	public Boolean isEnabled() {
		return enabled;
	}
	

	
	// Attributes:
	/**
	 * Gets the attribute reset cost.
	 * 
	 * @return single attribute reset coin cost
	 */
	public Double getResetCost() {
		return attributeResetCost;
	}
	
	
	
	// Rename and create:
	/**
	 * Gets the factionCreateCost.
	 * 
	 * @return the factionCreateCost
	 */
	public Double getFactionCreateCost() {
		return factionCreateCost;
	}

	/**
	 * Gets the settlementCreateCost.
	 * 
	 * @return the settlementCreateCost
	 */
	public Double getSettlementCreateCost() {
		return settlementCreateCost;
	}

	/**
	 * Gets the factionRenameCost.
	 * 
	 * @return the factionRenameCost
	 */
	public Double getFactionRenameCost() {
		return factionRenameCost;
	}

	/**
	 * Gets the settlementRenameCost.
	 * 
	 * @return the settlementRenameCost
	 */
	public Double getSettlementRenameCost() {
		return settlementRenameCost;
	}
	
	
	
	// Upgrade costs:
	/**
	 * Gets the cost of buying new claim points.
	 * 
	 * @return claim points cost.
	 */
	public Double getClaimPointCost() {
		return claimPointCost;
	}
	
	/**
	 * Gets the building point cost.
	 * 
	 * @return building point cost
	 */
	public Double getBuildPointCost() {
		return buildPointCost;
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
	
	
	
	// Faction wages:
	/**
	 * Calculates wage for settlement.
	 * 
	 * @param claimPoints claim points
	 * @return wage for a single settlement
	 */
	public double calcWage(Double claimPoints) {

		return factionClaimsPointsWage.value(claimPoints);
		
	}
	
	/**
	 * Calculates the wages different hierarchy levels get.
	 * 
	 * @param rawWage raw wage
	 * @return wages for hierarchy levels
	 */
	public Hashtable<Integer, Double> calcHierarchyWages(Double rawWage) {

		
		Hashtable<Integer, Double> wages = new Hashtable<Integer, Double>();
		
		int min = FactionConfiguration.config().getHierarchyMin();
		int max = FactionConfiguration.config().getHierarchyMax();
		
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

	/**
	 * Gets the reward for a killer player with a certain hierarchy level.
	 * 0 hierarchy if none.
	 * 
	 * @param killedPlayer, player killed
	 * @param killedFaction faction where the killed player was from, null if none
	 * @return reward gained
	 */
	public Double getFactionKillReward(SagaPlayer killedPlayer, Faction killedFaction) {
	
		Proficiency rank = null;
		if(killedFaction != null) rank = killedFaction.getRank(killedPlayer.getName());
		
		if(rank != null) return factionKillReward.value(rank.getHierarchy());
	
		return factionKillReward.value(0);
		
	}
	
	
	
	// Trading post:
	/**
	 * Gets the import item for the given saga item.
	 * 
	 * @param sagaItem saga item
	 * @return export item, null if none
	 */
	public SagaPricedItem getExportItem(SagaItem sagaItem) {

		for (int i = 0; i < exports.length; i++) {
			if(sagaItem.checkRepresents(exports[i])) return exports[i];
		}
		
		return null;
		
	}
	
	/**
	 * Gets the import item for the given saga item.
	 * 
	 * @param sagaItem saga item
	 * @return import item, null if none
	 */
	public SagaPricedItem getImportItem(SagaItem sagaItem) {

		for (int i = 0; i < imports.length; i++) {
			if(sagaItem.checkRepresents(imports[i])) return imports[i];
		}
		
		return null;
		
	}
	
	/**
	 * Gets trading post exports.
	 * 
	 * @return trading post exports
	 */
	public SagaPricedItem[] getTradingPostExports() {
		return tradingPostExports;
	}
	
	
	
	// Hooking:
	/**
	 * Checks if Saga can hook into other economy managers.
	 * 
	 * @return true if Saga can hook
	 */
	public boolean canHook() {
		return enableHooking;
	}
	
	
	
	// Load unload:
	/**
	 * Loads configuration.
	 * 
	 * @return configuration
	 */
	public static EconomyConfiguration load(){

		
		// Create config:
		if(!WriterReader.checkExists(Directory.ECONOMY_CONFIG)){

			try {
				WriterReader.unpackConfig(Directory.ECONOMY_CONFIG);
			}
			catch (IOException e) {
				SagaLogger.severe(EconomyConfiguration.class, "failed to create default configuration: " + e.getClass().getSimpleName());
			}
			
		}
		
		EconomyConfiguration config;
		try {
			
			config = WriterReader.read(Directory.ECONOMY_CONFIG, EconomyConfiguration.class);
			
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
