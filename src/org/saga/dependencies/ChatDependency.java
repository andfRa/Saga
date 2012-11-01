package org.saga.dependencies;

import net.milkbowl.vault.chat.Chat;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.config.EconomyConfiguration;
import org.saga.config.GeneralConfiguration;
import org.saga.factions.Faction;
import org.saga.player.SagaPlayer;

public class ChatDependency {

	/**
	 * Manager instance.
	 */
	private static ChatDependency manager;


	/**
	 * GroupManager chat.
	 */
	private GroupManager groupManager = null;
	
	
	/**
	 * Vault chat.
	 */
	private Chat vaultChat = null;

	

	/**
	 * Enables the manager.
	 * 
	 */
	public static void enable() {

		
		manager = new ChatDependency();

		// No hooking:
		if(!EconomyConfiguration.config().canHook()) return;

		final PluginManager pluginManager = Saga.plugin().getServer().getPluginManager();
		
		// Group manager:
		Plugin plugin = pluginManager.getPlugin("GroupManager");
		if (plugin != null && plugin.isEnabled()) {
		
			manager.groupManager = (GroupManager)plugin;
			SagaLogger.info("Using GroupManager chat.");
			return;
			
		}
		
		// Vault:
		try {
			Class.forName("net.milkbowl.vault.chat.Chat");
			
			RegisteredServiceProvider<net.milkbowl.vault.chat.Chat> economyProvider = Saga.plugin().getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
	        if (economyProvider != null) {
	        	manager.vaultChat = economyProvider.getProvider();
	        }
	        
	        if(manager.vaultChat != null){
	        	SagaLogger.info("Using Vault chat.");
	        	return;
	        }
		}
		catch (ClassNotFoundException e) {}
		
		SagaLogger.info("Using default chat.");
		

	}
	
	/**
	 * Disables the manager.
	 * 
	 */
	public static void disable() {

		manager.groupManager = null;
		manager.vaultChat = null;
		
		manager = null;

	}
	
	
	/**
	 * Updates player prefix.
	 * 
	 * @param sagaPlayer saga player
	 * @param faction faction, null if none
	 */
	public static void updatePrefix(SagaPlayer sagaPlayer, Faction faction) {

		
		Player player = sagaPlayer.getPlayer();
		if(player == null) return;

		// Prefix:
		String prefix = "";
		if(!GeneralConfiguration.isDisabled(player.getWorld()) && faction != null && faction.isFormed()) prefix = faction.getColour1() + faction.getName() + GeneralConfiguration.config().prefixSeparator + faction.getColour2();
		
		// GroupManager:
		if(manager.groupManager != null){

			setGroupManagerPrefix(player.getLocation().getWorld().getName(), player.getName(), prefix);
			return;
			
		}
		
		if(manager.vaultChat != null){
			
			manager.vaultChat.setPlayerPrefix(player, prefix);
			return;
			
		}
		
		
	}
	
	/**
	 * Updates player prefix.
	 * 
	 * @param sagaPlayer saga player
	 */
	public static void updatePrefix(SagaPlayer sagaPlayer) {
		updatePrefix(sagaPlayer, sagaPlayer.getFaction());
	}
	
	/**
	 * Modifies chat format if no chat plugins are detected.
	 * 
	 * @param format old format
	 * @param sagaPlayer saga player
	 * @return new format
	 */
	public static String modifyChatFormat(String format, SagaPlayer sagaPlayer) {

		
		if(manager.vaultChat != null || manager.groupManager != null) return format;

    	// No faction or not formed yet:
    	Faction faction = sagaPlayer.getFaction();
    	if(faction == null || !faction.isFormed()) return format;
    	
    	ChatColor color1 = faction.getColour1();
    	ChatColor color2 = faction.getColour2();
    	ChatColor reset = ChatColor.RESET;
    	
    	return "<" +color1 + faction.getName() + GeneralConfiguration.config().prefixSeparator + color2 + "%1$s" + reset + "> %2$s";
    	
		
	}
	
	
	
	/**
	 * Sets GroupManager prefix.
	 * 
	 * @param worldName world name
	 * @param playerName player name
	 * @param prefix prefix
	 */
	private static void setGroupManagerPrefix(String worldName, String playerName, String prefix) {

		 OverloadedWorldHolder owh;
	        if (worldName == null) {
	            owh = manager.groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
	        } else {
	            owh = manager.groupManager.getWorldsHolder().getWorldData(worldName);
	        }
	        if (owh == null) {
	            return;
	        }
	        User user = owh.getUser(playerName);
	        if (user == null) {
	            return;
	        }
	        user.getVariables().addVar("prefix", prefix);
		
	}
	
	
	
	// Types:
	/**
	 * Transaction type.
	 * 
	 * @author andf
	 *
	 */
	public enum TransactionType{
		
		SELL,
		BUY,
		INVALID;
		
	}
	
}
