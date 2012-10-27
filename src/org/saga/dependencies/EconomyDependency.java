package org.saga.dependencies;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.player.SagaPlayer;

public class EconomyDependency {

	/**
	 * Manager instance.
	 */
	private static EconomyDependency manager;

	/**
	 * Vault economy.
	 */
	private Economy vaultEconomy = null;
	
	

	/**
	 * Enables the manager.
	 * 
	 */
	public static void enable() {

		
		manager = new EconomyDependency();

		final PluginManager pluginManager = Saga.plugin().getServer().getPluginManager();
		Plugin plugin = null;
		 
		// Vault:
		plugin = pluginManager.getPlugin("Vault");
		if (plugin != null && plugin.isEnabled()) {
		
			RegisteredServiceProvider<Economy> economyProvider = Saga.plugin().getServer().getServicesManager().getRegistration(Economy.class);
	        if (economyProvider != null) {
	        	manager.vaultEconomy = economyProvider.getProvider();
	        }
	        if(manager.vaultEconomy != null){
	        	SagaLogger.info("Using Vault economy.");
	        	return;
	        }
			
		}
		
		SagaLogger.warning("Economy plugin not found, using default.");
		

	}
	
	/**
	 * Disables the manager.
	 * 
	 */
	public static void disable() {

		manager.vaultEconomy = null;
		
		manager = null;

	}
	

	/**
	 * Adds coins to the player.
	 * 
	 * @param sagaPlayer saga player
	 * @param amount amount
	 */
	public static void addCoins(SagaPlayer sagaPlayer, Double amount){
	
		
		// Vault:
		if(manager.vaultEconomy != null){
			
			String player = sagaPlayer.getName();
			
//			if(!vaultEconomy.hasAccount(player)){
//				
//				if(!vaultEconomy.createPlayerAccount(player)){
//					SagaLogger.severe(getClass(), "failed to create vault player account for " + player);
//					return;
//				}
//				
//			}
			
			EconomyResponse response = manager.vaultEconomy.depositPlayer(player, amount);
			
			if(response.type != ResponseType.SUCCESS){
				SagaLogger.severe(EconomyDependency.class, "failed to add coins to " + player + " player: " + response.errorMessage);
			}else{
				return;
			}
			
		}
		
		sagaPlayer.addCoins(amount);
	
		
	}
	
	/**
	 * Removes coins from the player.
	 * 
	 * @param sagaPlayer saga player
	 * @param amount amount
	 * @return true if coins were removed.
	 */
	public static boolean removeCoins(SagaPlayer sagaPlayer, Double amount){
	
		
		// Vault:
		if(manager.vaultEconomy != null){
			
			String player = sagaPlayer.getName();
			
			EconomyResponse response = manager.vaultEconomy.withdrawPlayer(player, amount);
			
			if(response.type == ResponseType.SUCCESS){
				return true;
			}else{
				SagaLogger.severe(EconomyDependency.class, "failed to remove coins from " + player + " player: " + response.errorMessage);
				return false;				
			}
			
		}
		
		return sagaPlayer.removeCoins(amount);
		
		
	}
	
	/**
	 * Gets players coins.
	 * 
	 * @param sagaPlayer saga player
	 * @return currency amount of currency the trader has
	 */
	public static Double getCoins(SagaPlayer sagaPlayer){
		
		
		// Vault:
		if(manager.vaultEconomy != null){
			
			String player = sagaPlayer.getName();
			
			return manager.vaultEconomy.getBalance(player);
			
		}
		
		return sagaPlayer.getCoins();

		
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
