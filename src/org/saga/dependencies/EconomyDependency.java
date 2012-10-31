package org.saga.dependencies;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.config.EconomyConfiguration;
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

		// No hooking:
		if(!EconomyConfiguration.config().canHook()) return;
		
		// Vault:
		try {
			Class.forName("net.milkbowl.vault.economy.Economy");
			
			RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider = Saga.plugin().getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	        if (economyProvider != null) {
	        	manager.vaultEconomy = economyProvider.getProvider();
	        }
	        
	        if(manager.vaultEconomy != null){
	        	SagaLogger.info("Using Vault economy.");
	        	return;
	        }
		}
		catch (ClassNotFoundException e) {}
		
		SagaLogger.info("Using default economy.");
		

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
