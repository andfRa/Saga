package org.saga.dependencies;

import java.util.Hashtable;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.player.SagaPlayer;
import org.sk89q.SagaCommandsManager;

import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * Permissions manager. Hooks up with different permission plugins.
 * 
 * @author andf
 *
 */
public class PermissionsManager {

	
	/**
	 * Manager instance.
	 */
	private static PermissionsManager manager;

	
	
	/**
	 * Permission for saga administrator mode.
	 */
	public static String ADMIN_MODE_PERMISSION = "saga.admin.adminmode";
	
	/**
	 * Permission for saga administrator chat.
	 */
	public static String ADMIN_CHAT_PERMISSION = "saga.admin.chat";
	
	/**
	 * Permission for saga special chat.
	 */
	public static String SPECIAL_CHAT_PERMISSION = "saga.special.chat";
	
	/**
	 * Permission for building in the wilderness.
	 */
	public static String WILDERNESS_BUILD_PERMISSION = "saga.user.wilderness.build";
	
	
	/**
	 * Permission descriptions.
	 */
	public final static Hashtable<String, String> PERMISSION_DESCRIPTIONS = new Hashtable<String, String>(){
		
		private static final long serialVersionUID = 1L;

		{
			put(WILDERNESS_BUILD_PERMISSION, "Build outside settlements.");
		}
		
	};
	
	
	/**
	 * Commands map.
	 */
	private SagaCommandsManager<Player> commandMap;
	
	
	/**
	 * Group manager.
	 */
	private GroupManager groupManager = null;

	/**
	 * Group manager.
	 */
	private PermissionsEx permissionsEx = null;
	
	
	
	/**
	 * Enables the manager.
	 * 
	 */
	public static void enable() {

		
		manager = new PermissionsManager();
		
		final PluginManager pluginManager = Saga.plugin().getServer().getPluginManager();
		Plugin plugin = null;
		 
		// Commands map:
		manager.commandMap = new SagaCommandsManager<Player>() {

			@Override
			public boolean hasPermission(Player player, String perm) {
				return PermissionsManager.hasPermission(player, perm);
			}
			
		};
		
		// Group manager:
		plugin = pluginManager.getPlugin("GroupManager");
		if (plugin != null && plugin.isEnabled()) {
		
			manager.groupManager = (GroupManager)plugin;
			SagaLogger.info("Using GroupManager.");
			return;
			
		}
		
		// PermissionsEx:
		plugin = pluginManager.getPlugin("PermissionsEx");
		if (plugin != null && plugin.isEnabled()) {
			
			manager.permissionsEx = (PermissionsEx)plugin;
			SagaLogger.info("Using PermissionsEx.");
			return;
			
		}
		
		SagaLogger.warning("Permissions plugin not found, using defaults.");
		

	}
	
	/**
	 * Disables the manager.
	 * 
	 */
	public static void disable() {

		
		manager.commandMap = null;
		manager.groupManager = null;
		manager.permissionsEx = null;
		
		manager = null;
		

	}
	
	
	
	/**
	 * Checks if the player has permission.
	 * 
	 * @param player player
	 * @param permission permission node
	 * @return true if has permission
	 */
	public static boolean hasPermission(Player player, String permission) {

		
		// Full access:
		if(player.isOp()) return true;

		// GroupManager:
		if(manager.groupManager != null){
			
			final AnjoPermissionsHandler handler = manager.groupManager.getWorldsHolder().getWorldPermissions(player);
			if (handler == null) return false;
			return handler.has(player, permission);
			
		}
		
		// PermissionsEx:
		if(manager.permissionsEx != null){
			
			String world = player.getLocation().getWorld().getName();
			return manager.permissionsEx.has(player, permission, world);
			
		}
		
		// Default:
		if(permission.startsWith("saga.user")) return true;
		
		return false;
		
		
	}

	/**
	 * Checks if the player has permission. Checks administrator mode.
	 * 
	 * @param sagaPlayer saga player
	 * @param permission permission node
	 * @return true if has permission
	 */
	public static boolean hasPermission(SagaPlayer sagaPlayer, String permission) {

		
		// Admin mode:
		if(permission.startsWith("saga") && sagaPlayer.isAdminMode()) return true;
		
		Player player = sagaPlayer.getPlayer();
		if(player == null) return false;
		
		return hasPermission(player, permission);
		
		
	}

	
	/**
	 * Gets the command map.
	 * 
	 * @return commands manager
	 */
	public static SagaCommandsManager<Player> getCommandMap() {

		return manager.commandMap;
		
	}
	
	
}
