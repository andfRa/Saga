package org.saga.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.saga.Saga;
import org.saga.constants.IOConstants.WriteReadType;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.WriterReader;

import com.google.gson.JsonParseException;

public class BalanceConfiguration {


	/**
	 * Instance of the configuration.
	 */
	transient private static BalanceConfiguration instance;
	
	/**
	 * Gets the instance.
	 * 
	 * @return instance
	 */
	public static BalanceConfiguration config() {
		return instance;
	}
	
	
	// Plugin:
	/**
	 * Saga save interval.
	 */
	public Integer saveInterval;
	
	
	// Player:
	/**
	 * Maximum stamina.
	 */
	public Double maximumStamina;

	/**
	 * Stamina gain per second.
	 */
	public Double staminaPerSecond;

	// Profession general:
	/**
	 * Maximum level.
	 */
	public Integer maximumLevel;
	
	/**
	 * Maximum skill level.
	 */
	public Integer maximumSkillLevel;
	
	/**
	 * Maximum amount of abilities.
	 */
	public Integer abilitiesLimit;
	
	
	// Experience:
	/**
	 * Experience regeneration speed.
	 */
	public Double expRegenSpeed;
	
	/**
	 * Experience regeneration limit.
	 */
	public Integer expRegenLimit;
	
	
	// Other
	/**
	 * Time in seconds that the an ability remains active.
	 */
	public Short abilitySelectedTime;
	
	/**
	 * Base lightning damage.
	 */
	public Integer baseLightningDamage;
	
	/**
	 * Base fireball damage for players.
	 */
	public Integer pvpBaseFireballDamage;

	/**
	 * Base fireball damage for creatures.
	 */
	public Integer pvcBaseFireballDamage;
	
	/**
	 * Bug report message.
	 */
	public String bugReportMessage;

	/**
	 * Administrator chat name color.
	 */
	public ChatColor adminChatNameColor;
	
	/**
	 * Administrator chat color.
	 */
	public ChatColor adminChatMessageColor;
	

	// Level:
	/**
	 * Experience drop.
	 */
	public Double experienceDrop;
	
	/**
	 * Experience remain.
	 */
	public Double experienceRemain;
	
	
	// Reward:
	/**
	 * Experience reward.
	 */
	public Integer expReward;
	
	/**
	 * Coin reward.
	 */
	public Double coinReward;

	/**
	 * True to stop all creeper explosion land damage.
	 */
	public Boolean stopCreeperExplosions;
	
	// Xray:
	/**
	 * The amount of stone mined required for the statistics to update.
	 */
	public Integer xrayUpdateInterval;
	
	/**
	 * The ratio that indicates a xray usage.
	 */
	public Hashtable<Material, Double> xrayIndicationRatios;
	
	
	
	// Potions:
	/**
	 * Harmful splash potions.
	 */
	private HashSet<Short> harmfulSplashPotions;
	
	
	// Blocks:
	/**
	 * Data set on block place:
	 */
	private Hashtable<Material, Byte> blockPlaceData;
	
	// Statistics:
	/**
	 * Statistics update interval.
	 */
	public Integer statisticsUpdateAge;
	
	
	// Initialization:
	/**
	 * Used by gson.
	 */
	public BalanceConfiguration() {
		
	}
	
	/**
	 * Goes trough all the fields and makes sure everything has been set after gson load.
	 * If not, it fills the field with defaults.
	 * 
	 * @return true if everything was correct.
	 */
	public boolean complete() {
		
		
		boolean integrity=true;
		
		// Plugin:
		if(saveInterval == null){
			Saga.severe(getClass(), "saveInterval field failed to initialize", "setting default");
			saveInterval= 60;
			integrity=false;
		}
		
		// Player:
		if(maximumStamina == null){
			Saga.severe(getClass(), "failed to initialize maximumStamina field", "setting default");
			maximumStamina= 100.0;
			integrity=false;
		}
		if(staminaPerSecond == null){
			Saga.severe(getClass(), "failed to initialize staminaPerSecond field", "setting default");
			staminaPerSecond= 0.1;
			integrity=false;
		}
		
		if(abilitiesLimit == null){
			Saga.severe(getClass(), "failed to initialize abilitiesLimit field", "setting default");
			abilitiesLimit= 1;
			integrity=false;
		}
		
		if(expRegenSpeed == null){
			Saga.severe(getClass(), "expRegenSpeed field failed to initialize", "setting default");
			expRegenSpeed= 0.0;
			integrity=false;
		}
		
		if(expRegenLimit == null){
			Saga.severe(getClass(), "expRegenLimit field failed to initialize", "setting default");
			expRegenLimit= 0;
			integrity=false;
		}
		
		if(maximumLevel == null){
			Saga.severe(getClass(), "maximumLevel field failed to initialize", "setting default");
			maximumLevel= 1;
			integrity=false;
		}
		
		if(maximumSkillLevel == null){
			Saga.severe(getClass(), "maximumSkillLevel field failed to initialize", "setting default");
			maximumSkillLevel= 1;
			integrity=false;
		}
		
		// Other:
		if(abilitySelectedTime == null){
			Saga.severe(getClass(), "failed to initialize abilitySelectedTime field", "setting default");
			abilitySelectedTime= 3;
			integrity=false;
		}
		if(baseLightningDamage == null){
			Saga.severe(getClass(), "failed to initialize baseLightningDamage field", "setting default");
			baseLightningDamage= 1;
			integrity=false;
		}
		
		if(pvpBaseFireballDamage == null){
			Saga.severe(getClass(), "pvpBaseFireballDamage field failed to initialize", "setting default");
			pvpBaseFireballDamage= 4;
			integrity=false;
		}
		
		if(pvcBaseFireballDamage == null){
			Saga.severe(getClass(), "pvcBaseFireballDamage field failed to initialize", "setting default");
			pvcBaseFireballDamage= 8;
			integrity=false;
		}
		
		if(bugReportMessage == null){
			Saga.severe(getClass(), "failed to initialize bugReportMessage field", "setting default");
			bugReportMessage = "";
			integrity=false;
		}
		
		if(adminChatNameColor == null){
			Saga.severe(getClass(), "failed to initialize adminChatNameColor field", "setting default");
			adminChatNameColor = ChatColor.DARK_GREEN;
			integrity=false;
		}
		
		if(adminChatMessageColor == null){
			Saga.severe(getClass(), "failed to initialize adminChatMessageColor field", "setting default");
			adminChatMessageColor = ChatColor.GREEN;
			integrity=false;
		}
		
		if(adminChatMessageColor == null){
			Saga.severe(getClass(), "failed to initialize adminChatMessageColor field", "setting default");
			adminChatMessageColor = ChatColor.GREEN;
			integrity=false;
		}
		
		if(experienceDrop == null){
			Saga.severe(getClass(), "failed to initialize experienceDrop field", "setting default");
			experienceDrop = 0.5;
			integrity=false;
		}
		
		if(experienceDrop > 1){
			Saga.severe(getClass(), "failed to normalize experienceDrop field", "setting default");
			experienceDrop = 1.0;
			integrity=false;
		}
		
		if(experienceDrop < 0){
			Saga.severe(getClass(), "failed to normalize experienceDrop field", "setting default");
			experienceDrop = 0.0;
			integrity=false;
		}
		
		if(experienceRemain == null){
			Saga.severe(getClass(), "failed to initialize experienceRemain field", "setting default");
			experienceRemain = 0.5;
			integrity=false;
		}
		
		if(experienceRemain > 1){
			Saga.severe(getClass(), "failed to normalize experienceRemain field", "setting default");
			experienceRemain = 1.0;
			integrity=false;
		}
		
		if(experienceRemain < 0){
			Saga.severe(getClass(), "failed to normalize experienceRemain field", "setting default");
			experienceRemain = 0.0;
			integrity=false;
		}
		
		if(stopCreeperExplosions == null){
			Saga.severe(getClass(), "stopCreeperExplosions field failed to initialize", "setting default");
			stopCreeperExplosions = false;
			integrity=false;
		}
		
		if(xrayUpdateInterval == null){
			Saga.severe(getClass(), "xrayUpdateInterval field failed to initialize", "setting default");
			xrayUpdateInterval = 3000;
			integrity=false;
		}
		
		if(xrayIndicationRatios == null){
			Saga.severe(getClass(), "xrayIndicationRatios field failed to initialize", "setting default");
			xrayIndicationRatios = new Hashtable<Material, Double>();
			integrity=false;
		}
		
		if(harmfulSplashPotions == null){
			Saga.severe(getClass(), "badSplashPotions field failed to initialize", "setting default");
			harmfulSplashPotions = new HashSet<Short>();
			integrity=false;
		}
		if(harmfulSplashPotions.remove(null) == true){
			Saga.severe(getClass(), "harmfulSplashPotions field element(s) failed to initialize", "removing element");
			integrity=false;
		}
		
		if(statisticsUpdateAge == null){
			Saga.severe(getClass(), "statisticsUpdateAge field failed to", "setting default");
			statisticsUpdateAge = 1;
			integrity=false;
		}
		
		if(expReward == null){
			Saga.severe(getClass(), "expReward field failed to", "setting default");
			expReward = 1;
			integrity=false;
		}
		
		if(coinReward == null){
			Saga.severe(getClass(), "coinReward field failed to", "setting default");
			coinReward = 0.0;
			integrity=false;
		}
		
		if(blockPlaceData == null){
			Saga.severe(getClass(), "blockPlaceData field failed to", "setting default");
			blockPlaceData = new Hashtable<Material, Byte>();
			integrity=false;
		}
		
		return integrity;
		
		
	}

	
	// Potions:
	/**
	 * Gets bad splash potions.
	 * 
	 * @return bad splash potions
	 */
	public HashSet<Short> getHarmfulSplashPotions() {
		return new HashSet<Short>(harmfulSplashPotions);
	}

	
	// Reward:
	/**
	 * Gets the exp reward.
	 * 
	 * @param reward reward
	 * @return exp reward
	 */
	public Double getExpReward(int reward) {

		return (double)reward * expReward;
		
	}
	
	/**
	 * Gets the coin reward.
	 * 
	 * @param reward reward
	 * @return coin reward
	 */
	public Double getCoinReward(int reward) {

		return coinReward * reward;
		
	}
	
	
	// Xray:
	/**
	 * Checks if the ratio for the given material indicates xray usage.
	 * 
	 * @param material material
	 * @param ratio ratio
	 * @return true if there is an indication
	 */
	public boolean checkXrayIndication(Material material, Double ratio) {

		Double maximumRatio = xrayIndicationRatios.get(material);
		if(maximumRatio == null) return false;
		
		return maximumRatio < ratio;
		
	}
	
	
	// Block place:
	/**
	 * Handles block data value change on block place.
	 * 
	 * @param block block
	 */
	public void handleDataChange(Block block) {

		
		Byte value = blockPlaceData.get(block.getType());
		if(value == null) return;
		
		block.setData(value);
		
		// Statistics:
		StatisticsManager.manager().onBlockDataChange();
		
		
	}
	
	
	// Load unload:
	/**
	 * Loads the configuration.
	 * 
	 * @return experience configuration
	 */
	public static BalanceConfiguration load(){
		
		
		boolean integrityCheck = true;
		
		// Load:
		BalanceConfiguration config;
		try {
			config = WriterReader.readBalanceConfig();
		} catch (FileNotFoundException e) {
			
			Saga.severe(BalanceConfiguration.class, "missing configuration ", "creating defaults");
			config = new BalanceConfiguration();
			integrityCheck = false;
			
		} catch (IOException e) {
			
			Saga.severe(BalanceConfiguration.class, "failed to load configuration: " + e.getClass() + ":" + e.getMessage(), "creating defaults");
			config = new BalanceConfiguration();
			integrityCheck = false;
			
		} catch (JsonParseException e) {
			
			Saga.severe(BalanceConfiguration.class, "failed to parse configuration: " + e.getClass() + ":" + e.getMessage(), "creating defaults");
			Saga.info("Parse message :" + e.getMessage());
			config = new BalanceConfiguration();
			integrityCheck = false;
			
		}
		
		// Integrity check and complete:
		integrityCheck = config.complete() && integrityCheck;
		
		// Write default if integrity check failed:
		if (!integrityCheck) {
			
			Saga.severe("Integrity check failed for " + BalanceConfiguration.class.getSimpleName());
			Saga.info("Writing " + BalanceConfiguration.class.getSimpleName() + " with default values.");
			try {
				WriterReader.writeBalanceConfig(config, WriteReadType.CONFIG_DEFAULTS);
			} catch (IOException e) {
				Saga.severe(BalanceConfiguration.class, "configuration write failure", "ignoring write");
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
