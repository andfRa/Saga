package org.saga.config;

import java.io.FileNotFoundException;
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
	
	
	// Guard rune:
	/**
	 * Worlds where the guardian rune is disabled.
	 */
	private HashSet<String> guardRuneDisableWorlds;
	
	
	
	
	// Initialisation:
	/**
	 * Used by gson.
	 */
	public BalanceConfiguration() {
		
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
		
		if(guardRuneDisableWorlds == null){
			SagaLogger.nullField(getClass(), "guardRuneDisableWorlds");
			guardRuneDisableWorlds = new HashSet<String>();
		}
		
		if(guardRuneDisableWorlds.remove(null) == true){
			SagaLogger.nullField(getClass(), "guardRuneDisableWorlds element");
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
	 * Checks if the rune is enabled in the given world
	 * 
	 * @param rune rune
	 * @param world world
	 * @return true if enabled
	 */
	public boolean isRuneEnabled(GuardianRune rune, World world) {

		return !guardRuneDisableWorlds.contains(world.getName());

	}
	
	
	
	// Load unload:
	/**
	 * Loads configuration.
	 * 
	 * @return configuration
	 */
	public static BalanceConfiguration load(){

		
		BalanceConfiguration config;
		try {
			
			config = WriterReader.read(Directory.BALANCE_CONFIG, BalanceConfiguration.class);
			
		} catch (FileNotFoundException e) {
			
			SagaLogger.severe(BalanceConfiguration.class, "configuration not found");
			config = new BalanceConfiguration();
			
		} catch (IOException e) {
			
			SagaLogger.severe(BalanceConfiguration.class, "failed to read configuration: " + e.getClass().getSimpleName());
			config = new BalanceConfiguration();
			
		} catch (JsonParseException e) {

			SagaLogger.severe(AttributeConfiguration.class, "failed to parse configuration: " + e.getClass().getSimpleName());
			SagaLogger.info("message: " + e.getMessage());
			config = new BalanceConfiguration();
			
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
