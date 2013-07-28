package org.saga.buildings;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.buildings.production.SagaItem;
import org.saga.buildings.storage.StorageArea;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.messages.GeneralMessages;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Bundle;
import org.saga.settlements.Settlement.SettlementPermission;


public class Warehouse extends Building{

	
	/**
	 * Item buffer.
	 */
	private ArrayList<SagaItem> buffer;

	
	
	// Initialisation:
	/**
	 * Creates a building from the definition.
	 * 
	 * @param definition building definition
	 */
	public Warehouse(BuildingDefinition definition) {
		
		super(definition);
		
		buffer = new ArrayList<SagaItem>();
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.production.ProductionBuilding#complete()
	 */
	@Override
	public boolean complete() throws InvalidBuildingException {
		
		super.complete();
		
		if(buffer == null){
			buffer = new ArrayList<SagaItem>();
			SagaLogger.nullField(this, "buffer");
		}
		
		return true;
		
	}

	

	// Store:
	/**
	 * Stores the item.
	 * Given item will be modified accordingly.
	 * 
	 * @param sagaItem saga item
	 */
	public void store(SagaItem sagaItem) {
		
		if(sagaItem.getType().isBlock()) storeBlock(sagaItem);
		else storeItem(sagaItem);
		
	}
	
	/**
	 * Stores blocks.
	 * 
	 * @param items saga block to store
	 */
	private void storeBlock(SagaItem sagaItem) {

		
		ArrayList<Block> storage = findLowestEmpty();
		
		// Loaded:
		if(getSagaChunk().isLoaded())
		while(sagaItem.getAmount() >= 1.0 && storage.size() > 0){
			
			// Remove block:
			int index = Saga.RANDOM.nextInt(storage.size());
			Block block = storage.get(index);
			block.setTypeIdAndData(sagaItem.getType().getId(), sagaItem.getData().byteValue(), false);
			sagaItem.modifyAmount(-1);
			storage.remove(index);
			
			// Refresh possible storage:
			if(storage.size() == 0) storage = findLowestEmpty();
			
		}
		
		// Buffer:
		for (int i = 0; i < buffer.size(); i++) {
			
			if(buffer.get(i).checkRepresents(sagaItem)){
				buffer.get(i).modifyAmount(sagaItem.getAmount());
				sagaItem.setAmount(0.0);
				break;
			}
			
			if(i == buffer.size() - 1) buffer.add(new SagaItem(buffer.get(i)));
			
		}
		
		
		
	}
	
	/**
	 * Stores items.
	 * 
	 * @param items saga item to store
	 */
	private void storeItem(SagaItem sagaItem) {

		ArrayList<Chest> possibleStorage = findChests();
		
		// Loaded:
		if(getSagaChunk().isLoaded())
		while(possibleStorage.size() > 0 && sagaItem.getAmount() >= 1.0){
			
			// Random inventory:
			int index = Saga.RANDOM.nextInt(possibleStorage.size());
			Chest chest = possibleStorage.get(index);
			Inventory inventory = chest.getInventory();

			// Item:
			ItemStack item = sagaItem.createItem();
			if(item.getAmount() == 0) break;
			sagaItem.modifyAmount(-item.getAmount());
			
			// Store:
			ItemStack remaining = inventory.addItem(item).get(0);
			
			// Full:
			if(remaining != null){
				possibleStorage.remove(index);
				sagaItem.modifyAmount(remaining.getAmount());
			}
			
			chest.update();
			
		}

		// Buffer:
		for (int i = 0; i < buffer.size(); i++) {
			
			if(buffer.get(i).checkRepresents(sagaItem)){
				buffer.get(i).modifyAmount(sagaItem.getAmount());
				sagaItem.setAmount(0.0);
				break;
			}

			if(i == buffer.size() - 1) buffer.add(new SagaItem(buffer.get(i)));
			
		}
		
		
		
	}
	
	

	// Withdraw:
	/**
	 * Withdraws an item.
	 * 
	 * @param requestedItem requested items
	 * @return items withrawed
	 */
	public SagaItem withdraw(SagaItem requestedItem) {

		if(requestedItem.getType().isBlock()) return withdrawBlock(requestedItem);
		else return withdrawItem(requestedItem);
		
	}
	
	/**
	 * Withdraws an item into another item.
	 * 
	 * @param requestedItem requested items
	 * @param collectItem item to collect into
	 */
	public void withdraw(SagaItem requestedItem, SagaItem collectItem) {
		collectItem.modifyAmount(withdraw(requestedItem).getAmount());
	}
	
	/**
	 * Withdraws a block.
	 * 
	 * @param requestedItem requested blocks
	 * @return retrieved blocks
	 */
	private SagaItem withdrawBlock(SagaItem requestedItem) {
		

		ArrayList<Block> possibleStorage = findAllStorage();
		SagaItem removedItem = new SagaItem(requestedItem);
		removedItem.setAmount(0.0);
		
		// Don't remove less than one item:
		if(requestedItem.getAmount() < 1.0) return removedItem;

		// Buffer:
		for (int i = 0; i < buffer.size(); i++) {
			
			if(buffer.get(i).checkRepresents(removedItem)){
				
				double mod = requestedItem.getAmount() - removedItem.getAmount();
				
				buffer.get(i).modifyAmount(-mod);
				removedItem.modifyAmount(mod);
				
				if(buffer.get(i).getAmount() <= 0.0) buffer.remove(i);
				
				break;
				
			}
			
		}
		
		// Loaded:
		if(getSagaChunk().isLoaded())
		for (Block block : possibleStorage) {
			
			if(!requestedItem.checkRepresents(block)) continue;
			
			if(removedItem.getAmount() >= requestedItem.getAmount()){
				return removedItem;
			}
			
			removedItem.modifyAmount(1.0);
			block.setType(Material.AIR);
				
		}
		
		return removedItem;
		
	}

	/**
	 * Withdraw items.
	 * 
	 * @param requestedItem requested items
	 * @return retrieved items
	 */
	private SagaItem withdrawItem(SagaItem requestedItem) {
		
		
		ArrayList<Chest> possibleStorage = findChests();
		SagaItem removedItem = new SagaItem(requestedItem);
		removedItem.setAmount(0.0);

		// Buffer:
		for (int i = 0; i < buffer.size(); i++) {
			
			if(buffer.get(i).checkRepresents(removedItem)){
				
				double mod = requestedItem.getAmount() - removedItem.getAmount();
				
				buffer.get(i).modifyAmount(-mod);
				removedItem.modifyAmount(mod);
				
				if(buffer.get(i).getAmount() <= 0.0) buffer.remove(i);
				
				break;
				
			}
			
		}
		
		// Don't remove less than one item:
		if(requestedItem.getAmount() < 1.0) return removedItem;
		
		// Withdraw: 
		for (Chest chest : possibleStorage) {
			
			int mod = (int)(requestedItem.getAmount() - removedItem.getAmount());
			ItemStack modStack = requestedItem.createItem();
			modStack.setAmount(mod);
			
			Inventory inventory = chest.getInventory();
			ItemStack remaining = inventory.removeItem(modStack).get(0);
			
			if(remaining != null) mod-= remaining.getAmount();
			
			removedItem.modifyAmount(mod);
			
			chest.update();
			
		}
		
		return removedItem;
		
	}
	
	
	
	// Amounts:
	/**
	 * Counts the amount of items.
	 * 
	 * @param countItem saga item to count
	 * @return item count
	 */
	public double count(SagaItem countItem) {

		if(countItem.getType().isBlock()) return countBlock(countItem);
		else return countItem(countItem);
		
	}
	
	/**
	 * Counts the amount of blocks.
	 * 
	 * @param countItem saga block item to count
	 * @return item count
	 */
	public double countBlock(SagaItem countItem) {

		
		double count = 0;
		
		ArrayList<Block> possibleStorage = findAllStorage();

		// Buffer:
		for (int i = 0; i < buffer.size(); i++) {
			
			if(buffer.get(i).checkRepresents(countItem)){
				
				count+= buffer.get(i).getAmount();
				break;
				
			}
			
		}
		
		// Loaded:
		if(getSagaChunk().isLoaded())
		for (Block block : possibleStorage) {
			
			if(!countItem.checkRepresents(block)) continue;
			
			count++;
				
		}
		
		return count;

		
	}
	
	/**
	 * Counts the amount of items.
	 * 
	 * @param countItem saga item to count
	 * @return item count
	 */
	public double countItem(SagaItem countItem) {

		
		double count = 0;
		
		ArrayList<Chest> possibleStorage = findChests();

		// Buffer:
		for (int i = 0; i < buffer.size(); i++) {
			
			if(buffer.get(i).checkRepresents(countItem)){
				
				count+= buffer.get(i).getAmount();
				break;
				
			}
			
		}
		
		// Chests:
		for (int i = 0; i < possibleStorage.size(); i++) {
			
			ItemStack[] inventory = possibleStorage.get(i).getInventory().getContents();
			
			for (int j = 0; j < inventory.length; j++) {
				
				if(!countItem.checkRepresents(inventory[j])) continue;
				count+= inventory[j].getAmount();
				
			}
			
			
		}
		
		return count;
		
		
	}
	
	
	
	// Storage blocks:
	/**
	 * Gets all lowest empty storage blocks.
	 * 
	 * @return lowest empty storage blocks
	 */
	private ArrayList<Block> findLowestEmpty() {

		ArrayList<Block> blocks = new ArrayList<Block>();
		ArrayList<StorageArea> storages = getStorageAreas();
		
		for (StorageArea storageArea : storages) {
			blocks.addAll(storageArea.getLowestEmpty());
		}
		
		return blocks;
		
	}

	/**
	 * Gets all storage blocks.
	 * 
	 * @return all storage blocks
	 */
	private ArrayList<Block> findAllStorage() {

		ArrayList<Block> blocks = new ArrayList<Block>();
		ArrayList<StorageArea> storages = getStorageAreas();
		
		for (StorageArea storageArea : storages) {
			blocks.addAll(storageArea.getAllStorage());
		}
		
		return blocks;
		
	}

	/**
	 * Filters out chests that can used for storage.
	 * 
	 * @param blocks all storage blocks
	 * @return chests for storage
	 */
	private ArrayList<Chest> findChests() {

		
		ArrayList<Chest> possible = new ArrayList<Chest>();
		ArrayList<StorageArea> storages = getStorageAreas();
		
		for (StorageArea storageArea : storages) {
			
			ArrayList<Block> blocks = storageArea.getAllStorage();
		
			for (Block block : blocks) {
				if(block.getState() instanceof Chest) possible.add((Chest) block.getState());
			}
			
		}

		return possible;
		
	}
	
	
	
	
	// Events:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent, org.saga.player.SagaPlayer)
	 */
	@Override
	public void onPlayerInteract(PlayerInteractEvent event, SagaPlayer sagaPlayer) {

	
		// Chunk group:
		Bundle bundle = getChunkBundle();
		if(bundle == null){
			
			sagaPlayer.message(GeneralMessages.noPermission(this));
			return;
			
		}
		
		Block targetBlock = event.getClickedBlock();
		if(targetBlock == null) return;
		Material targetMaterial = targetBlock.getType();
		
		// Permission:
		if(!bundle.hasPermission(sagaPlayer, SettlementPermission.ACCESS_WAREHOUSE)){

			// Chest:
			if(targetMaterial.equals(Material.CHEST) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(GeneralMessages.noPermission(this));
				
			}

			// Trapped chest:
			if(targetMaterial.equals(Material.TRAPPED_CHEST) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(GeneralMessages.noPermission(this));
				
			}
			
			// Furnace:
			else if(targetMaterial.equals(Material.FURNACE) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(GeneralMessages.noPermission(this));
				
			}

			// Burning furnace:
			else if(targetMaterial.equals(Material.BURNING_FURNACE) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(GeneralMessages.noPermission(this));
				
			}

			// Dispenser:
			else if(targetMaterial.equals(Material.DISPENSER) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(GeneralMessages.noPermission(this));
				
			}
			
			// Door:
			else if(targetMaterial.equals(Material.WOODEN_DOOR)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(GeneralMessages.noPermission(this));
				
			}
			
			// Trap door:
			else if(targetMaterial.equals(Material.TRAP_DOOR)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(GeneralMessages.noPermission(this));
				
			}
			
			// Enchantment table:
			else if(targetMaterial.equals(Material.ENCHANTMENT_TABLE) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(GeneralMessages.noPermission(this));
				
			}
			
			// Brewing stand:
			else if(targetMaterial.equals(Material.BREWING_STAND) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(GeneralMessages.noPermission(this));
				
			}

			// Brewing stand item:
			else if(targetMaterial.equals(Material.BREWING_STAND_ITEM) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(GeneralMessages.noPermission(this));
				
			}

			// Fence gate:
			else if(targetMaterial.equals(Material.FENCE_GATE)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(GeneralMessages.noPermission(this));
				
			}
			
		}
		
		
	
	}
	
	
}
