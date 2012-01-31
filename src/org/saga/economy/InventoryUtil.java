package org.saga.economy;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.saga.Saga;

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
			Saga.warning("Failed to remove " + itemStack + " from an inventory");
		}
		
	}
	
	/**
	 * Removes an item from an inventory.
	 * 
	 * @param itemStack item stack
	 * @param inventory inventory
	 */
	public static void removeItemHand2(ItemStack itemStack, Inventory inventory) {

		if(inventory instanceof PlayerInventory){
			
			PlayerInventory playerInventory = (PlayerInventory) inventory;
			
			if(!playerInventory.getItemInHand().getType().equals(itemStack.getType())){
				removeItem(itemStack, playerInventory);
				return;
			}
			
			Integer newAmount = playerInventory.getItemInHand().getAmount() - itemStack.getAmount();
			
			if(newAmount < 0){
				removeItem(new ItemStack(itemStack.getType(), newAmount * -1), playerInventory);
				newAmount = 0;
			}
			
//			playerInventory.setItemInHand(stack)
//			
//			Material inHandType = playerInventory.getItemInHand().getType();
//			Integer amountInHand = playerInventory.getItemInHand().getAmount();
//			if(inHandType.equals(itemStack.getType())){
//				Integer newAmount = amountInHand - itemStack.getAmount();
//				
//				
//			}
//			
			
		}
		
		ItemStack remaining = inventory.removeItem(itemStack).get(0);
		if(remaining != null){
			Saga.warning("Failed to remove " + itemStack + " from an inventory");
		}
		
	}
	
	
	
}
