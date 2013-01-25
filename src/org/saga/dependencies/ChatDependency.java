package org.saga.dependencies;

import net.milkbowl.vault.chat.Chat;

import org.anjocaido.groupmanager.GroupManager;
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
import org.saga.player.Proficiency;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Bundle;

import com.earth2me.essentials.chat.EssentialsChat;

public class ChatDependency {

	/**
	 * Manager instance.
	 */
	private static ChatDependency manager;


	/**
	 * Prefix insert for settlements.
	 */
	public static final String SETTLEMENT_INSERT = "[SETTLEMENT]";

	/**
	 * Prefix insert for factions.
	 */
	public static final String FACTION_INSERT = "[FACTION]";
	
	/**
	 * Prefix insert for faction colour 1.
	 */
	public static final String FACTION_COLOUR1_INSERT = "[FACTION_COL1]";
	
	/**
	 * Prefix insert for faction colour 2.
	 */
	public static final String FACTION_COLOUR2_INSERT = "[FACTION_COL2]";

	/**
	 * Prefix insert for roles.
	 */
	public static final String ROLE_INSERT = "[ROLE]";
	
	/**
	 * Prefix insert for ranks.
	 */
	public static final String RANK_INSERT = "[RANK]";
	
	
	/**
	 * GroupManager chat.
	 */
	private GroupManager groupManager = null;
	
	/**
	 * EssentialsChat chat.
	 */
	private EssentialsChat essentialsChat = null;
	
	/**
	 * Vault chat.
	 */
	private Chat vaultChat = null;

	

	// Initialisation:
	/**
	 * Enables the manager.
	 * 
	 */
	public static void enable() {

		
		manager = new ChatDependency();

		// No hooking:
		if(!EconomyConfiguration.config().canHook()) return;

		final PluginManager pluginManager = Saga.plugin().getServer().getPluginManager();

		// Essentials chat:
		Plugin plugin = pluginManager.getPlugin("EssentialsChat");
		if (plugin != null && plugin.isEnabled()) {
		
			manager.essentialsChat = (EssentialsChat)plugin;
			SagaLogger.info("Using EssentialsChat chat.");
			return;
			
		}
		
		// Group manager:
		plugin = pluginManager.getPlugin("GroupManager");
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
		manager.essentialsChat = null;
		manager.vaultChat = null;
		
		manager = null;

	}
	

	
	// Operation:
	/**
	 * Modifies chat format.
	 * 
	 * @param format old format
	 * @param sagaPlayer saga player
	 * @return new format
	 */
	public static String format(String format, SagaPlayer sagaPlayer) {

		
		// Faction prefix if no chat managers present:
		if(manager.essentialsChat == null && manager.vaultChat == null && manager.groupManager == null){

			Faction faction = sagaPlayer.getFaction();
	    	if(faction == null || !faction.isFormed()) return format;
	    	
	    	ChatColor color1 = faction.getColour1();
	    	ChatColor color2 = faction.getColour2();
	    	ChatColor reset = ChatColor.RESET;
			
			return "<" +color1 + faction.getName() + "-" + color2 + "%1$s" + reset + "> %2$s";
			
		}
		
    	// Settlement:
		Bundle bundle = sagaPlayer.getBundle();
		if(bundle != null){
			format = format.replace(SETTLEMENT_INSERT, GeneralConfiguration.config().settlementFormat.replace(GeneralConfiguration.INSERT_STRING, bundle.getName()));
		}else{
			format = format.replace(SETTLEMENT_INSERT, "");
		}

    	// Faction:
		Faction faction = sagaPlayer.getFaction();
		if(faction != null && faction.isFormed()){
			format = format.replace(FACTION_INSERT, GeneralConfiguration.config().factionFormat.replace(GeneralConfiguration.INSERT_STRING, faction.getName()));
			format = format.replace(FACTION_COLOUR1_INSERT, faction.getColour1().toString());
			format = format.replace(FACTION_COLOUR2_INSERT, faction.getColour2().toString());
		}else{
			format = format.replace(FACTION_INSERT, "");
			format = format.replace(FACTION_COLOUR1_INSERT, "");
			format = format.replace(FACTION_COLOUR2_INSERT, "");
		}

		// Role:
		Proficiency role = sagaPlayer.getRole();
		if(role != null){
			format = format.replace(ROLE_INSERT, GeneralConfiguration.config().roleFormat.replace(GeneralConfiguration.INSERT_STRING, role.getName()));
		}else{
			format = format.replace(ROLE_INSERT, "");
		}
		
		// Rank:
		Proficiency rank = sagaPlayer.getRank();
		if(rank != null){
			format = format.replace(RANK_INSERT, GeneralConfiguration.config().rankFormat.replace(GeneralConfiguration.INSERT_STRING, rank.getName()));
		}else{
			format = format.replace(RANK_INSERT, "");
		}
		
    	return format;
    	
		
	}

	

	// Temporary:
	/**
	 * Updates player prefix.
	 * 
	 * @param sagaPlayer saga player
	 * @param faction faction, null if none
	 */
	public static void updatePrefix(SagaPlayer sagaPlayer, Faction faction) {

		
		Player player = sagaPlayer.getPlayer();
		if(player == null) return;

		if(GeneralConfiguration.isDisabled(player.getWorld())) return;
		
		// Prefix:
		String prefix = "";
		
		// GroupManager:
		if(manager.groupManager != null){

			setGroupManagerPrefix(player, prefix);
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
	 * Sets GroupManager prefix.
	 *
	 * @param player player
	 * @param prefix prefix
	*/
	private static void setGroupManagerPrefix(Player player, String prefix) {
	
	
		final OverloadedWorldHolder handler = manager.groupManager.getWorldsHolder().getWorldData(player);
		if (handler == null) {
		return;
		}
		
		handler.getUser(player.getName()).getVariables().addVar("prefix", prefix);
		
	
	}
	

}
