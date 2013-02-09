package org.saga.utility;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.saga.SagaLogger;
import org.saga.buildings.production.SagaItem;

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

	
	
	/**
	 * Takes a Saga item from a players inventory.
	 * 
	 * @param requested requested item
	 * @param inventory inventory
	 * @return taken item
	 */
	public static SagaItem takeItem(SagaItem requested, Inventory inventory) {

		SagaItem taken = new SagaItem(requested);
		ItemStack item = requested.createItem();
		
		ItemStack remaining = inventory.removeItem(item).get(0);
		if(remaining != null) requested.modifyAmount(-remaining.getAmount());
		
		return taken;
		
	}
	
	/**
	 * Gives a Saga item to a players inventory.
	 * Drops the item if full.
	 * 
	 * @param sagaItem saga item
	 * @param inventory inventory to put to
	 * @param dropLoc drop location if full
	 */
	public static void giveItem(SagaItem sagaItem, Inventory inventory, Location dropLoc) {

		ItemStack item = sagaItem.createItem();
		
		ItemStack remaining = inventory.addItem(item).get(0);
		if(remaining != null) dropLoc.getWorld().dropItem(dropLoc, remaining);
		
	}
	
	
	
}
