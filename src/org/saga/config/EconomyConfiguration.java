package org.saga.config;

import java.io.IOException;
import java.util.Collection;
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
import org.saga.settlements.Settlement;
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
	private TwoPointFunction claimPointCost;
	
	/**
	 * Building point cost.
	 */
	private TwoPointFunction buildPointCost;
	
	
	// Settlement wages:
	/**
	 * Settlement wage weights.
	 */
	private Hashtable<Integer, Double> settlementWageWeights;
	
	/**
	 * Time when wages are paid.
	 */
	private Daytime settlementWagesTime;

	
	/**
	 * The percent of coins the member takes.
	 */
	private Double settlementMemberShare;

	/**
	 * The percent of coins the settlement takes.
	 */
	private Double settlementShare;

	/**
	 * The percent of coins the faction takes.
	 */
	private Double settlementFactionShare;
	
	
	// Faction wages:
	/**
	 * Settlement wage weights.
	 */
	private Hashtable<Integer, Double> factionWageWeights;

	/**
	 * Time when wages are paid.
	 */
	private Daytime factionWagesTime;
	
	/**
	 * The percent of coins the member takes.
	 */
	private Double factionMemberShare;

	/**
	 * The percent of coins the faction takes.
	 */
	private Double factionShare;

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
			claimPointCost = new TwoPointFunction(0.0);
		}

		if(buildPointCost == null){
			SagaLogger.nullField(getClass(), "buildPointCost");
			buildPointCost = new TwoPointFunction(0.0);
		}
		
		
		if(settlementWageWeights == null){
			SagaLogger.nullField(getClass(), "settlementWageWeights");
			settlementWageWeights= new Hashtable<Integer, Double>();
		}

		if(settlementWagesTime == null){
			SagaLogger.nullField(getClass(), "settlementWagesTime");
			settlementWagesTime= Daytime.NONE;
		}
		
		if(settlementMemberShare == null){
			SagaLogger.nullField(getClass(), "settlementMemberShare");
			settlementMemberShare = 0.1;
		}
		
		if(settlementShare == null){
			SagaLogger.nullField(getClass(), "settlementPercent");
			settlementShare = 0.1;
		}
		
		if(settlementFactionShare == null){
			SagaLogger.nullField(getClass(), "factionShare");
			settlementFactionShare = 0.1;
		}
		
		
		if(factionWageWeights == null){
			SagaLogger.nullField(getClass(), "factionWageWeights");
			factionWageWeights= new Hashtable<Integer, Double>();
		}
		
		if(factionWagesTime == null){
			SagaLogger.nullField(getClass(), "factionWagesTime");
			factionWagesTime= Daytime.NONE;
		}
		
		if(factionMemberShare == null){
			SagaLogger.nullField(getClass(), "factionMemberShare");
			factionMemberShare = 0.1;
		}
		
		if(factionShare == null){
			SagaLogger.nullField(getClass(), "factionShare");
			factionShare = 0.1;
		}
		
		if(factionKillReward == null){
			SagaLogger.nullField(getClass(), "factionKillReward");
			factionKillReward = new TwoPointFunction(0.0);
		}
		factionKillReward.complete();
		
		
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
	 * @param claims total settlement claims available
	 * @return claim point cost
	 */
	public Double getClaimPointCost(Integer claims) {
		return claimPointCost.value(claims);
	}
	
	/**
	 * Gets the building point cost.
	 * 
	 * @param bpoints total building points available
	 * @return build point point cost
	 */
	public Double getBuildPointCost(Integer bpoints) {
		return buildPointCost.value(bpoints);
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
	
	
	
	// Settlement wages:
	/**
	 * Gets settlement wage weight for given player.
	 * 
	 * @param settlement settlement
	 * @param sagaPlayer player
	 * @return wage
	 */
	public Double getWageWeigth(Settlement settlement, SagaPlayer sagaPlayer) {

		Proficiency role = settlement.getRole(sagaPlayer.getName());
		
		Double wage = settlementWageWeights.get(role.getHierarchy());
		if(wage == null) wage = 0.0;
		
		return wage;
		
	}
	
	/**
	 * Gets settlement wage percent.
	 * 
	 * @param hierarchy hierarchy level
	 * @return wage percent
	 */
	public double getSettlementWagePercent(Integer hierarchy) {
		
		double sum = 0.0;
		
		Collection<Double> weights = settlementWageWeights.values();
		for (Double weight : weights) {
			sum+= weight;
		}
		if(sum == 0.0) return 0.0;
		
		Double hierWeight = settlementWageWeights.get(hierarchy);
		if(hierWeight == null) hierWeight = 0.0;
		
		return hierWeight/sum;
		
	}
	
	/**
	 * Gets the settlement wages time.
	 * 
	 * @return settlement wages time
	 */
	public Daytime getSettlementWagesTime() {
		return settlementWagesTime;
	}
	

	/**
	 * Gets the settlement member share percent.
	 * 
	 * @return member percent
	 */
	public Double getSettlementMemberPercent() {
		double total = settlementMemberShare + settlementShare + settlementFactionShare;
		if(total == 0.0) return 0.0;
		return settlementMemberShare/total;
	}
	
	/**
	 * Gets the settlement share percent.
	 * 
	 * @return settlement percent
	 */
	public Double getSettlementPercent() {
		double total = settlementMemberShare + settlementShare + settlementFactionShare;
		if(total == 0.0) return 0.0;
		return settlementShare/total;
	}
	
	/**
	 * Gets faction share percent.
	 * 
	 * @return faction percent
	 */
	public Double getSettlementFactionPercent() {
		double total = settlementMemberShare + settlementShare + settlementFactionShare;
		if(total == 0.0) return 0.0;
		return settlementFactionShare/total;
	}
	
	
	
	// Faction wages:
	/**
	 * Gets faction wage weight for given player.
	 * 
	 * @param faction faction
	 * @param sagaPlayer player
	 * @return wage
	 */
	public Double getWageWeigth(Faction faction, SagaPlayer sagaPlayer) {

		Proficiency rank = faction.getRank(sagaPlayer.getName());
		
		Double wage = settlementWageWeights.get(rank.getHierarchy());
		if(wage == null) wage = 0.0;
		
		return wage;
		
	}

	/**
	 * Gets faction wage percent.
	 * 
	 * @param hierarchy hierarchy level
	 * @return wage percent
	 */
	public double getFactionWagePercent(Integer hierarchy) {
		
		double sum = 0.0;
		
		Collection<Double> weights = factionWageWeights.values();
		for (Double weight : weights) {
			sum+= weight;
		}
		if(sum == 0.0) return 0.0;
		
		Double hierWeight = factionWageWeights.get(hierarchy);
		if(hierWeight == null) hierWeight = 0.0;
		
		return hierWeight/sum;
		
	}
	
	/**
	 * Gets the faction member share percent.
	 * 
	 * @return member percent
	 */
	public Double getFactionMemberPercent() {
		double total = factionMemberShare + factionShare;
		if(total == 0.0) return 0.0;
		return factionMemberShare/total;
	}
	
	/**
	 * Gets the faction share percent.
	 * 
	 * @return faction percent
	 */
	public Double getFactionPercent() {
		double total = factionMemberShare + factionShare;
		if(total == 0.0) return 0.0;
		return factionShare/total;
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
