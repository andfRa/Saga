package org.saga.utility;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.saga.SagaLogger;

public class InventoryUtil {

	
	/**
	 * Gets item count.
	 * 
	 * @param material item material
	 * @param items inventory contents
	 * @return item count
	 */
	public static Integer getItemCount(Material material, ItemStack[] items) {

		int count = 0;
		for (int i = 0; i < items.length; i++) {
			if(items[i] != null && items[i].getType().equals(material) && items[i].getDurability() == 0 && items[i].getEnchantments().size() == 0){
				count += items[i].getAmount();
			}
		}
		return count;
		
	}
	
	/**
	 * Adds an item to an inventory, drops it if there is no room.
	 * 
	 * @param itemStack item stack
	 * @param inventory inventory
	 * @param dropLocation drop location
	 * @return true if the item was dropped
	 */
	public static boolean addItem(ItemStack itemStack, Inventory inventory, Location dropLocation) {

		
		ItemStack remaining = inventory.addItem(itemStack).get(0);

		if(remaining != null){
			dropLocation.getWorld().dropItemNaturally(dropLocation, remaining);
			return true;
		}
		return false;
		
		
	}
	
	/**
	 * Removes an item from an inventory.
	 * 
	 * @param itemStack item stack
	 * @param inventory inventory
	 */
	public static void removeItem(ItemStack itemStack, Inventory inventory) {

		ItemStack remaining = inventory.removeItem(itemStack).get(0);
	
		if(remaining != null){
			SagaLogger.warning(InventoryUtil.class, "failed to remove " + itemStack + " from the inventory");
		}
		
	}
	
	/**
	 * Removes an item.
	 * 
	 * @param material material
	 * @param toRemove amount to remove
	 * @param removeEnch if true, then enchanted items will also be removed
	 * @param player player
	 */
	public static void removeItem(Material material, Integer toRemove, boolean removeEnch, Player player) {

		
		Inventory inventory = player.getInventory();
		Integer toDelete = toRemove;
		
		HashMap<Integer, ? extends ItemStack> all = inventory.all(material);
		Set<Integer> slots = all.keySet();
		
		for (Integer first : slots) {
			
			ItemStack itemStack = inventory.getItem(first);
			
			// Ignore enchanted:
			if(!removeEnch && itemStack.getEnchantments().size() > 0) continue;
			
			int amount = itemStack.getAmount();

            if (amount <= toDelete) {
                toDelete -= amount;
                // clear the slot, all used up
                inventory.clear(first);
            } else {
                // split the stack and store
                itemStack.setAmount(amount - toDelete);
                inventory.setItem(first, itemStack);
                toDelete = 0;
            }

             // Bail when done
             if (toDelete <= 0) {
                 break;
             }
             
		}
		
		 if(toDelete > 0){
			 SagaLogger.severe("Failed to remove " + toDelete + " " + material + " from players " + player.getName() + " inventory");
		 }
	
		
	}
	
	
	
	
}
