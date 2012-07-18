package org.saga.dependencies.spout;

import org.saga.player.SagaPlayer;

public class ClientManager {
	
	
//	/**
//	 * Manager instance.
//	 */
//	private static ClientManager manager;
//
//	/**
//	 * Spout.
//	 */
//	private SpoutServer spoutServer = null;
	
	
	// Spout client:
	public static boolean showStats(SagaPlayer sagaPlayer) {

		
//		if(manager.spoutServer != null){
//			
//			// Spout player:
//			SpoutPlayer spoutplayer = manager.spoutServer.getPlayer(sagaPlayer.getName());
//			if(spoutplayer == null) return false;
//			
//			// Stats popup:
//			SpoutStatsPopup stats = new SpoutStatsPopup(sagaPlayer);
//			spoutplayer.getMainScreen().attachPopupScreen(stats);
//			
//			return true;
//			
//		}

		return false;

	}
	
	
	
	/**
	 * Enables the manager.
	 * 
	 */
	public static void enable() {

		
//		manager = new ClientManager();
//
//		final PluginManager pluginManager = Saga.plugin().getServer().getPluginManager();
//		Plugin plugin = null;
//		
//		// Spout:
//		plugin = pluginManager.getPlugin("Spout");
//		if (plugin != null && plugin.isEnabled()) {
//			
//			try {
//				
//				manager.spoutServer = Spout.getServer();
//				
//				PluginManager pm = Bukkit.getServer().getPluginManager();
//				
//				// Register events:
//				pm.registerEvents(new SpoutScreenListener(), org.getspout.spout.Spout.getInstance());
//				
//				SagaLogger.info("Using Spout.");
//				
//				return;
//				
//			}
//			catch (Exception e) {
//				SagaLogger.severe(ClientManager.class, "failed to enable spout: " + e.getClass()+ ":" + e.getMessage());
//				manager.spoutServer = null;
//			}
//			
//		}
		

	}
	
	/**
	 * Disables the manager.
	 * 
	 */
	public static void disable() {
		
//		manager = null;

	}


}
