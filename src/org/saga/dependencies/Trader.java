package org.saga.dependencies;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface Trader{
	
	/**
	 * Gets the trading name.
	 * 
	 * @return trading name
	 */
	public String getName();

	
	/**
	 * Adds coins.
	 * 
	 * @param amount amount
	 * @return true if coins were added
	 */
	public boolean addCoins(Double amount);
	
	/**
	 * Removes coins.
	 * 
	 * @param amount amount
	 * @return true if coins were removed
	 */
	public boolean removeCoins(Double amount);

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
	 * @param item material
	 * @return 
	 */
	public Integer getAmount(Material material);
	
	
	/**
	 * Gets item price.
	 * 
	 * @return item price, null if none
	 */
	public Double getSellPrice(Material material);
	
	/**
	 * Gets item price.
	 * 
	 * @return item price, null if none
	 */
	public Double getBuyPrice(Material material);
	
}
