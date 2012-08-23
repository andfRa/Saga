package org.saga.config;

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.saga.SagaLogger;
import org.saga.player.GuardianRune;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.statistics.StatisticsManager;

import com.google.gson.JsonParseException;

public class GeneralConfiguration {


	/**
	 * Instance of the configuration.
	 */
	transient private static GeneralConfiguration instance;
	
	/**
	 * Gets the instance.
	 * 
	 * @return instance
	 */
	public static GeneralConfiguration config() {
		return instance;
	}
	
	
	// Saving:
	/**
	 * Saga save interval.
	 */
	public Integer saveInterval;
	
	
	// Chat:
	/**
	 * Administrator chat name colour.
	 */
	public ChatColor adminChatNameColor;
	
	/**
	 * Administrator chat colour.
	 */
	public ChatColor adminChatMessageColor;
	
	/**
	 * Special chat name colour.
	 */
	public ChatColor specialChatNameColor;
	
	/**
	 * Special chat colour.
	 */
	public ChatColor specialChatMessageColor;
	
	

	// Terrain damage:
	/**
	 * True to stop all creeper explosion land damage.
	 */
	public Boolean stopCreeperExplosions;
	
	
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
	
	
	// Guardian rune:
	/**
	 * True if the guard rune in enabled.
	 */
	private Boolean guardRuneEnabled;
	
	/**
	 * Worlds where the guardian rune is disabled.
	 */
	private HashSet<String> guardRuneDisableWorlds;
	
	
	// Worlds:
	/**
	 * Default world name.
	 */
	private String defaultWorld;
	
	
	// X-ray:
	/**
	 * Minimum amount of stone required for x-ray detection.
	 */
	private Integer xrayMinStone;
	
	/**
	 * X-ray diamond ratio.
	 */
	private Double xrayDiamondRatio;
	
	
	
	// Initialisation:
	/**
	 * Used by gson.
	 */
	public GeneralConfiguration() {
		
	}
	
	/**
	 * Fixes all fields.
	 * 
	 */
	public void complete() {
		
		
		// Plugin:
		if(saveInterval == null){
			SagaLogger.nullField(getClass(), "saveInterval");
			saveInterval= 60;
		}

		if(adminChatNameColor == null){
			SagaLogger.nullField(getClass(), "adminChatNameColor");
			adminChatNameColor = ChatColor.DARK_GREEN;
		}
		
		if(adminChatMessageColor == null){
			SagaLogger.nullField(getClass(), "adminChatMessageColor");
			adminChatMessageColor = ChatColor.GREEN;
		}

		if(specialChatNameColor == null){
			SagaLogger.nullField(getClass(), "specialChatNameColor");
			specialChatNameColor = ChatColor.DARK_BLUE;
		}
		
		if(specialChatMessageColor == null){
			SagaLogger.nullField(getClass(), "specialChatMessageColor");
			specialChatMessageColor = ChatColor.BLUE;
		}
		
		if(stopCreeperExplosions == null){
			SagaLogger.nullField(getClass(), "stopCreeperExplosions");
			stopCreeperExplosions = false;
		}
		
		if(harmfulSplashPotions == null){
			SagaLogger.nullField(getClass(), "badSplashPotions");
			harmfulSplashPotions = new HashSet<Short>();
		}
		
		if(harmfulSplashPotions.remove(null) == true){
			SagaLogger.nullField(getClass(), "harmfulSplashPotions element");
		}
		
		if(statisticsUpdateAge == null){
			SagaLogger.nullField(getClass(), "statisticsUpdateAge");
			statisticsUpdateAge = 1;
		}
		
		if(blockPlaceData == null){
			SagaLogger.nullField(getClass(), "blockPlaceData");
			blockPlaceData = new Hashtable<Material, Byte>();
		}
		
		if(guardRuneEnabled == null){
			SagaLogger.nullField(getClass(), "guardRuneEnabled");
			guardRuneEnabled = false;
		}
		
		if(guardRuneDisableWorlds == null){
			SagaLogger.nullField(getClass(), "guardRuneDisableWorlds");
			guardRuneDisableWorlds = new HashSet<String>();
		}
		
		if(guardRuneDisableWorlds.remove(null) == true){
			SagaLogger.nullField(getClass(), "guardRuneDisableWorlds element");
		}

		if(defaultWorld == null){
			SagaLogger.nullField(getClass(), "defaultWorld");
			defaultWorld = "world";
		}

		if(xrayMinStone == null){
			SagaLogger.nullField(getClass(), "xrayMinStone");
			xrayMinStone = 1000;
		}

		if(xrayDiamondRatio == null){
			SagaLogger.nullField(getClass(), "xrayDiamondRatio");
			xrayDiamondRatio = 1.0;
		}
		
		
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

	
	// Block data:
	/**
	 * Modifies block data.
	 * 
	 * @param block block
	 */
	public void modifyBlockData(Block block) {

		
		Byte data = blockPlaceData.get(block.getType());
		if(data == null) return;
		
		block.setData(data);
		
		// Statistics:
		StatisticsManager.manager().onBlockDataChange();
		
		
	}
	
	/**
	 * Gets the new data value for a block.
	 * 
	 * @param block block
	 * @return new data value, -1 if no modification
	 */
	public Byte getNewBlockData(Block block) {

		Byte data = blockPlaceData.get(block.getType());
		if(data == null) return -1;
		
		return data;

	}
	
	
	
	// Guard rune:
	/**
	 * Checks if the guard rune is enabled.
	 * 
	 * @return true if enabled
	 */
	public Boolean isRuneEnabled() {
		return guardRuneEnabled;
	}
	
	/**
	 * Checks if the rune is enabled in the given world
	 * 
	 * @param rune rune
	 * @param world world
	 * @return true if enabled
	 */
	public boolean isRuneEnabled(GuardianRune rune, World world) {

		return !guardRuneDisableWorlds.contains(world.getName());

	}
	
	
	
	// Worlds:
	/**
	 * Gets the default world.
	 * 
	 * @return default world
	 */
	public String getDefaultWorld() {
		return defaultWorld;
	}
	
	
	
	// X-ray:
	/**
	 * Gets minimum stone amount for x-ray indication.
	 * 
	 * @return minimum amount of stone
	 */
	public Integer getXrayMinStone() {
		return xrayMinStone;
	}
	
	/**
	 * Get x-ray trigger diamond ratio
	 * 
	 * @return x-ray diamond ratio
	 */
	public Double getXrayDiamondRatio() {
		return xrayDiamondRatio;
	}
	
	
	
	// Load unload:
	/**
	 * Loads configuration.
	 * 
	 * @return configuration
	 */
	public static GeneralConfiguration load(){


		// Create config:
		if(!WriterReader.checkExists(Directory.GENERAL_CONFIG)){

			try {
				WriterReader.unpackConfig(Directory.GENERAL_CONFIG);
			}
			catch (IOException e) {
				SagaLogger.severe(GeneralConfiguration.class, "failed to create default configuration: " + e.getClass().getSimpleName());
			}
			
		}
		
		GeneralConfiguration config;
		try {
			
			config = WriterReader.read(Directory.GENERAL_CONFIG, GeneralConfiguration.class);
			
		} catch (IOException e) {
			
			SagaLogger.severe(GeneralConfiguration.class, "failed to read configuration: " + e.getClass().getSimpleName());
			config = new GeneralConfiguration();
			
		} catch (JsonParseException e) {

			SagaLogger.severe(AttributeConfiguration.class, "failed to parse configuration: " + e.getClass().getSimpleName());
			SagaLogger.info("message: " + e.getMessage());
			config = new GeneralConfiguration();
			
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
