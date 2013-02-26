package org.saga.config;

import java.io.IOException;
import java.util.Hashtable;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.saga.SagaLogger;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.utility.TwoPointFunction;

public class FactionConfiguration {


	/**
	 * Instance of the configuration.
	 */
	transient private static FactionConfiguration instance;
	
	/**
	 * Gets the instance.
	 * 
	 * @return instance
	 */
	public static FactionConfiguration config() {
		return instance;
	}
	

	/**
	 * If true then only faction vs faction pvp is allowed.
	 */
	public Boolean factionOnlyPvp;
	
	
	// Creation:
	/**
	 * Amount of members needed for the faction to form.
	 */
	public Integer formationAmount;

	/**
	 * Minimum name length.
	 */
	private Integer minNameLength;

	/**
	 * Maximum name length.
	 */
	private Integer maxNameLength;
	
	
	// Claiming:
	/**
	 * Claims gained per minute.
	 */
	private TwoPointFunction claimsPerMinute;

	/**
	 * Maximum number of claims when a faction is formed.
	 */
	private Integer initClaims;

	/**
	 * Maximum number of claims a faction can have.
	 */
	private Integer maxClaims;

	/**
	 * Chunk bundle claim speed.
	 */
	private TwoPointFunction claimSpeed;

	/**
	 * Chunk bundle automatic unclaim speed.
	 */
	private TwoPointFunction unclaimSpeed;

	/**
	 * Claim speed multiplier for multiple members.
	 */
	private TwoPointFunction memberSpeedMult;
	
	/**
	 * Members required from both factions to be able to claim.
	 */
	private Integer toClaimMembers;

	/**
	 * Determines how much claims a settlement is worth.
	 */
	private TwoPointFunction claimPoints;
	
	/**
	 * Determines if claimed storage areas are considered open.
	 */
	private Boolean openClaimedStorageAreas;
	
	
	// Wars:
	/**
	 * Minutes after which a settlement can star to be sieged.
	 */
	private Integer siegePrepareMinutes;

	/**
	 * Siege points gained per second per member.
	 */
	private TwoPointFunction siegePtsPerSecond;
	
	
	/**
	 * The long remind interval.
	 */
	private Integer siegeLongRemindIntervalMinutes;
	

	/**
	 * The minutes in which the short reminders are started.
	 */
	private Integer siegeShortRemindStartMinutes;
	
	/**
	 * The short remind interval.
	 */
	private Integer siegeShortRemindIntervalMinutes;

	/**
	 * The siege in progress remind interval.
	 */
	private Integer siegeInProgressRemindIntervalMinutes;
	
	
	
	// Ranks:
	/**
	 * Rank assigned to joined members.
	 */
	private String defaultRank;

//	/**
//	 * Rank assigned to faction owner.
//	 */
//	private String ownerRank;

	/**
	 * Hierarchy minimum.
	 */
	private Integer hierarchyMin;
	
	/**
	 * Hierarchy maximum.
	 */
	private Integer hierarchyMax;
	
	/**
	 * Hierarchy level names.
	 */
	private Hashtable<Integer, String> hierarchyNames;
	
	
	
	// Initialisation:
	/**
	 * Fixes all problematic fields.
	 * 
	 */
	public void complete() {
		
		
		if(factionOnlyPvp == null){
			SagaLogger.nullField(getClass(), "factionOnlyPvp");
			factionOnlyPvp = true;
		}
		
		if(formationAmount == null){
			SagaLogger.nullField(getClass(), "formationAmount");
			formationAmount = 3;
		}
		
		if(minNameLength == null){
			SagaLogger.nullField(getClass(), "minNameLength");
			minNameLength = 1;
		}
		
		if(maxNameLength == null){
			SagaLogger.nullField(getClass(), "maxNameLength");
			maxNameLength = 5;
		}
		
		if(claimsPerMinute == null){
			SagaLogger.nullField(getClass(), "claimsPerMinute");
			claimsPerMinute = new TwoPointFunction(0.0);
		}
		claimsPerMinute.complete();

		if(claimSpeed == null){
			SagaLogger.nullField(getClass(), "claimSpeed");
			claimSpeed = new TwoPointFunction(0.0);
		}
		claimSpeed.complete();

		if(unclaimSpeed == null){
			SagaLogger.nullField(getClass(), "unclaimSpeed");
			unclaimSpeed = new TwoPointFunction(0.0);
		}
		unclaimSpeed.complete();

		if(memberSpeedMult == null){
			SagaLogger.nullField(getClass(), "memberSpeedMult");
			memberSpeedMult = new TwoPointFunction(0.0);
		}
		memberSpeedMult.complete();

		if(toClaimMembers == null){
			SagaLogger.nullField(getClass(), "toClaimMembers");
			toClaimMembers = 3;
		}

		if(initClaims == null){
			SagaLogger.nullField(getClass(), "initClaims");
			initClaims = 1;
		}

		if(maxClaims == null){
			SagaLogger.nullField(getClass(), "maxClaims");
			maxClaims = 250;
		}
		
		if(claimPoints == null){
			SagaLogger.nullField(getClass(), "claimPoints");
			claimPoints = new TwoPointFunction(0.0);
		}
		claimPoints.complete();

		if(openClaimedStorageAreas == null){
			SagaLogger.nullField(getClass(), "openClaimedStorageAreas");
			openClaimedStorageAreas = false;
		}
		
		// Wars:
		if(siegePrepareMinutes == null){
			SagaLogger.nullField(getClass(), "siegePrepareMinutes");
			siegePrepareMinutes = 10;
		}
		
		if(siegePtsPerSecond == null){
			SagaLogger.nullField(getClass(), "siegePtsPerSecond");
			siegePtsPerSecond = new TwoPointFunction(0.0);
		}
		
		
		if(siegeLongRemindIntervalMinutes == null){
			SagaLogger.nullField(getClass(), "siegeLongRemindIntervalMinutes");
			siegeLongRemindIntervalMinutes = 15;
		}
		
		if(siegeShortRemindStartMinutes == null){
			SagaLogger.nullField(getClass(), "siegeShortRemindStartMinutes");
			siegeShortRemindStartMinutes = 10;
		}

		if(siegeShortRemindIntervalMinutes == null){
			SagaLogger.nullField(getClass(), "siegeShortRemindIntervalMinutes");
			siegeShortRemindIntervalMinutes = 1;
		}
		
		if(siegeInProgressRemindIntervalMinutes == null){
			SagaLogger.nullField(getClass(), "siegeInProgressRemindIntervalMinutes");
			siegeInProgressRemindIntervalMinutes = 1;
		}
		
		
		// Ranks:
		if(hierarchyMin == null){
			SagaLogger.nullField(getClass(), "hierarchyMin");
			hierarchyMin = 0;
		}
		
		if(hierarchyMax == null){
			SagaLogger.nullField(getClass(), "hierarchyMax");
			hierarchyMax = 1;
		}
		
		if(hierarchyNames == null){
			hierarchyNames = new Hashtable<Integer, String>();
			SagaLogger.nullField(getClass(), "hierarchyNames");
		}
		
//		if(ownerRank == null){
//			SagaLogger.nullField(getClass(), "ownerRank");
//			ownerRank = "novice";
//		}
		
		if(defaultRank == null){
			SagaLogger.nullField(getClass(), "defaultRank");
			defaultRank = "novice";
		}
		
		
	}

	
	
	// Creation:
	/**
	 * Gets the minimum name length.
	 * 
	 * @return minimum name length
	 */
	public Integer getMinNameLength() {
		return minNameLength;
	}
	
	/**
	 * Gets the maximum name length.
	 * 
	 * @return maximum name length
	 */
	public Integer getMaxNameLength() {
		return maxNameLength;
	}
	
	
	
	// Faction claiming:
	/**
	 * Gets claims per minute.
	 * 
	 * @param onlinePlayers players online
	 * @return amount of claim points per minute
	 */
	public Double getClaimsPerMinute(Integer onlinePlayers) {
		return claimsPerMinute.value(onlinePlayers);
	}
	
	/**
	 * Gets claim speed.
	 * 
	 * @param size chunk bundle size
	 * @return claim per minute
	 */
	public Double getClaimSpeed(Integer size) {
		return claimSpeed.value(size);
	}

	/**
	 * Gets unclaim speed.
	 * 
	 * @param size chunk bundle level
	 * @return unclaim per minute
	 */
	public Double getUnclaimSpeed(Integer size) {
		return unclaimSpeed.value(size);
	}
	

	/**
	 * Gets member bonus speed.
	 * 
	 * @param memberCount amount of members
	 * @return member bonus speed
	 */
	public Double getMemberMultiplier(Integer memberCount) {
		return memberSpeedMult.value(memberCount);
	}
	
	/**
	 * Gets the members required from both factions to be able to claim settlements.
	 * 
	 * @return members required to claim
	 */
	public Integer getToClaimMembers() {
		return toClaimMembers;
	}
	
	/**
	 * Gets the initial amount of claims the faction can have.
	 * 
	 * @return initial claims
	 */
	public Integer getInitialClaims() {
		return initClaims;
	}
	
	/**
	 * Gets the maximum amount of claims the faction can have.
	 * 
	 * @return max claims
	 */
	public Integer getMaxClaims() {
		return maxClaims;
	}
	
	/**
	 * Gets the amount claim points the settlement is worth.
	 * 
	 * @param size settlement size
	 * @return claim points
	 */
	public Double getClaimPoints(Integer size) {
		return claimPoints.value(size);
	}
	
	/**
	 * Checks if claimed storage areas are open for factions.
	 * 
	 * @return true if open
	 */
	public Boolean isOpenClaimedStorageAreas() {
		return openClaimedStorageAreas;
	}
	
	
	// Wars:
	/**
	 * Gets the minutes given to prepare for a siege.
	 * 
	 * @return minutes to prepare
	 */
	public Integer getSiegePrepareMinutes() {
		return siegePrepareMinutes;
	}
	
	/**
	 * Gets the siege point gain speed.
	 * 
	 * @param dif attackers - defenders
	 * @return claims per second
	 */
	public double getSiegePtsPerSecond(int dif) {
		return siegePtsPerSecond.value(dif);
	}
	
	/**
	 * Gets the long interval of the siege reminder.
	 * 
	 * @return long interval reminder interval in minutes
	 */
	public Integer getSiegeLongRemindIntervalMinutes() {
		return siegeLongRemindIntervalMinutes;
	}
	
	/**
	 * Gets the minutes remaining when the short interval must be used.
	 * 
	 * @return minutes remaining when the short interval kicks in
	 */
	public Integer getSiegeShortRemindStartMinutes() {
		return siegeShortRemindStartMinutes;
	}
	
	/**
	 * Gets the short interval of the siege reminder.
	 * 
	 * @return short interval reminder interval in minutes
	 */
	public Integer getSiegeShortRemindIntervalMinutes() {
		return siegeShortRemindIntervalMinutes;
	}
	
	/**
	 * Gets the interval after which the factions are reminded of the siege when it is in progress.
	 * 
	 * @return reminder interval when the siege is in progress
	 */
	public Integer getSiegeInProgressRemindIntervalMinutes() {
		return siegeInProgressRemindIntervalMinutes;
	}
	

	// Ranks:
	/**
	 * Gets hierarchy level name.
	 * 
	 * @param hierarchy hierarchy level
	 * @return hierarchy level name, null if none
	 */
	public String getHierarchyName(Integer hierarchy) {
		
		String roleName = hierarchyNames.get(hierarchy);
		if(roleName == null) return null;
		
		return roleName;
		
	}

	/**
	 * Gets min hierarchy level.
	 * 
	 * @return min hierarchy level
	 */
	public Integer getHierarchyMin() {
		return hierarchyMin;
	}
	
	/**
	 * Gets max hierarchy level.
	 * 
	 * @return max hierarchy level
	 */
	public Integer getHierarchyMax() {
		return hierarchyMax;
	}

	
	/**
	 * Gets the default rank.
	 * 
	 * @return default rank
	 */
	public String getDefaultRank() {
		return defaultRank;
	}
	
//	/**
//	 * Gets owner rank.
//	 * 
//	 * @return owner rank
//	 */
//	public String getOwnerRank() {
//		return ownerRank;
//	}
	
	
	
	// Load unload:
	/**
	 * Loads configuration.
	 * 
	 * @return configuration
	 */
	public static FactionConfiguration load(){


		// Create config:
		if(!WriterReader.checkExists(Directory.FACTION_CONFIG)){

			try {
				WriterReader.unpackConfig(Directory.FACTION_CONFIG);
			}
			catch (IOException e) {
				SagaLogger.severe(FactionConfiguration.class, "failed to create default configuration: " + e.getClass().getSimpleName());
			}
			
		}
		
		FactionConfiguration config;
		try {
			
			config = WriterReader.read(Directory.FACTION_CONFIG, FactionConfiguration.class);
			
		} catch (IOException e) {
			
			SagaLogger.severe(FactionConfiguration.class, "failed to read configuration: " + e.getClass().getSimpleName());
			config = new FactionConfiguration();
			
		} catch (JsonParseException e) {

			SagaLogger.severe(FactionConfiguration.class, "failed to parse configuration: " + e.getClass().getSimpleName());
			SagaLogger.info("message: " + e.getMessage());
			config = new FactionConfiguration();
			
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
