package org.saga.economy;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.saga.economy.EconomyManager.TransactionType;



public interface Trader{
	
	
	/**
	 * Gets the trading name.
	 * 
	 * @return trading name
	 */
	public String getTradingName();

	/**
	 * Checks if the transaction is active.
	 * 
	 * @return true if active
	 */
	public boolean isActive(TransactionType type, Material material);
	
	/**
	 * Adds coins
	 * 
	 * @param amount amount
	 */
	public void addCoins(Double amount);
	
	/**
	 * Removes coins.
	 * 
	 * @param amount amount
	 */
	public void removeCoins(Double amount);

	/**
	 * Gets traders currency.
	 * 
	 * @return currency amount of currency the trader has
	 */
	public Double getCoins();

	
	/**
	 * Adds currency
	 * 
	 * @param itemStack item stack
	 */
	public void addItem(ItemStack itemStack);
	
	/**
	 * Removes currency
	 * 
	 * @param itemStack irem stack
	 */
	public void removeItem(ItemStack itemStack);
	
	/**
	 * Gets item count.
	 * 
	 * @param material material
	 * @return 
	 */
	public Integer getItemCount(Material material);
	
	
	/**
	 * Gets all transactions.
	 * 
	 * @return transactions, empty if none
	 */
	public ArrayList<Transaction> getTransactions();

	/**
	 * Gets all trade deals.
	 * 
	 * @return trade deals, empty if none
	 */
	public ArrayList<TradeDeal> getTradeDeals();

	
	
}
