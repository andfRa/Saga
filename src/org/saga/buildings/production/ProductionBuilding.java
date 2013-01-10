package org.saga.buildings.production;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.buildings.Building;
import org.saga.buildings.BuildingDefinition;
import org.saga.buildings.storage.StorageArea;
import org.saga.exceptions.InvalidBuildingException;

public class ProductionBuilding extends Building{

	
	/**
	 * Resources.
	 */
	private ArrayList<SagaResource> resources;
	
	
	
	/**
	 * Initialises using definition.
	 * 
	 * @param definition building definition.
	 */
	public ProductionBuilding(BuildingDefinition definition) {
		
		super(definition);
		
		resetResources(); 
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#complete()
	 */
	@Override
	public boolean complete() throws InvalidBuildingException {
		
		super.complete();
		
		if(resources == null){
			resources = new ArrayList<SagaResource>();
			SagaLogger.nullField(this, "resources");
		}
		
		for (SagaResource recource : resources) {
			recource.complete();
		}
		
		synchResources();
		
		return true;
		
	}

	/**
	 * Synchronises resources with recipes.
	 * 
	 */
	private void synchResources() {

		ArrayList<SagaRecipe> recipes = getDefinition().getProductionRecipes();
		
		if(recipes.size() != resources.size()){
			SagaLogger.info(this, "resetting resources");
			resetResources();
			return;
		}
		
		for (int i = 0; i < resources.size(); i++) {
			if(!resources.get(i).equalsRecipe(recipes.get(i))){
				SagaLogger.info(this, "resetting resources");
				resetResources();
				return;
			}
		}
		
	}
	
	/**
	 * Resets all resources.
	 * 
	 */
	private void resetResources() {
		
		resources = new ArrayList<SagaResource>();
		ArrayList<SagaRecipe> recipes = getDefinition().getProductionRecipes();
		for (SagaRecipe sagaRecipe : recipes) {
			resources.add(new SagaResource(sagaRecipe));
		}
		
	}
	
	
	
	// Production and offer:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#produce()
	 */
	@Override
	public void produce() {
		
		
		ArrayList<SagaItem> allItems = new ArrayList<SagaItem>();
		
		// Offer free:
		for (SagaResource resource : resources) {
			resource.offerFree();
		}
		
		// All recipes:
		for (SagaResource resource : resources) {
			
			// Produce:
			SagaItem sagaItem = resource.produceItem();
			if(sagaItem == null) continue;
			
			allItems.add(sagaItem);
			
		}
		
		// Buildings:
		if(allItems.size() > 0){
			
			List<String> buildingNames = getDefinition().getOfferBuildings();
			
			// Different building types:
			for (String buildingName : buildingNames) {
				
				ArrayList<Building> offerBuildings = getChunkBundle().getBuildings(buildingName);
				
				// Different buildings:
				for (Building building : offerBuildings) {
					
					if(!(building instanceof ProductionBuilding)) continue;
					ProductionBuilding prBuilding = ((ProductionBuilding) building);
					
					// Offer:
					prBuilding.offer(allItems);
					
					// Only if loaded:
					if(getSagaChunk().isChunkLoaded()){

						// Count accepted:
						ArrayList<SagaItem> countItems = new ArrayList<SagaItem>();
						for (SagaItem sagaItem : resources) {
							SagaItem newItem = new SagaItem(sagaItem);
							newItem.setAmount(0);
							countItems.add(newItem);
						}
						prBuilding.countAccepted(countItems);
						
						// Withdraw:
						ArrayList<SagaItem> withdraw = new ArrayList<SagaItem>();
						withdraw.addAll(withdraw(countItems));
						
						// Offer:
						prBuilding.offer(withdraw);
						
					}
					
				}
				
				
			}
			
		}
		
		// Store remaining:
		if(getSagaChunk().isChunkLoaded()){
			store(allItems);
		}
		
		
	}
	
	/**
	 * Offers items for production.
	 * 
	 * @param items items to offer
	 */
	public void offer(List<SagaItem> items) {

		Iterator<SagaItem> it = items.iterator();
		while (it.hasNext()) {
			
			SagaItem item = it.next();
			offer(item);
			
			if(item.amount <= 0) it.remove();
			
		}
		
	}
	
	/**
	 * Offers an item for production.
	 * 
	 * @param item item to offer
	 */
	public void offer(SagaItem item) {
		for (SagaResource resource : resources) resource.offer(item);
	}
	
	/**
	 * Counts how many items will be accepted.
	 * 
	 * @param items items to count with
	 */
	private void countAccepted(ArrayList<SagaItem> countItems) {
		SagaResource.countAccept(countItems, resources);
	}
	
	
	// Store:
	/**
	 * Stores items
	 * 
	 * @param blocksItems items or block to store
	 */
	public void store(ArrayList<SagaItem> blocksItems) {

		
		ArrayList<SagaItem> blocks = new ArrayList<SagaItem>();
		ArrayList<SagaItem> items = new ArrayList<SagaItem>();
		
		for (SagaItem sagaItem : blocksItems) {
			if(sagaItem.getType().isBlock()) blocks.add(sagaItem);
			else items.add(sagaItem);
		}
		
		// Store:
		storeBlocks(blocks);
		storeItems(items);
		
		
	}
	
	/**
	 * Stores blocks.
	 * 
	 * @param items saga block item
	 */
	public void storeBlocks(ArrayList<SagaItem> items) {

		
		ArrayList<Block> possibleStorage = findLowestEmpty();
		
		for (SagaItem sagaItem : items) {
			
			while(sagaItem.getAmount() > 0 && possibleStorage.size() > 0){
				
				// Remove block:
				int index = Saga.getRandom().nextInt(possibleStorage.size());
				Block block = possibleStorage.get(index);
				block.setTypeIdAndData(sagaItem.getType().getId(), sagaItem.getData().byteValue(), false);
				sagaItem.modifyAmount(-1);
				possibleStorage.remove(index);
				
				// Refresh possible storage:
				if(possibleStorage.size() == 0) possibleStorage = findLowestEmpty();
				
			}
			
		}
		
		
	}
	
	/**
	 * Stores blocks.
	 * 
	 * @param items saga block item
	 */
	public void storeItems(ArrayList<SagaItem> items) {

		
		ArrayList<Chest> possibleStorage = findChests();
		
		while(possibleStorage.size() > 0){
			
			// Inventory:
			int index = Saga.getRandom().nextInt(possibleStorage.size());
			Chest chest = possibleStorage.get(index);
			Inventory inventory = chest.getInventory();
			possibleStorage.remove(index);
			
			// Add all items:
			for (SagaItem sagaItem : items) {
				
				ItemStack item = sagaItem.createItem();
				if(item.getAmount() == 0) continue;
				
				inventory.addItem(item).get(0);
				
			}
			
			chest.update();
			
			
		}
		
		
		
		
	}
	
	

	// Withdraw:
	/**
	 * Withdraw blocks.
	 * 
	 * @param items saga block item types to withdraw
	 * @return items that were withdrawn
	 */
	public ArrayList<SagaItem> withdraw(ArrayList<SagaItem> resources) {

		
		ArrayList<SagaItem> blocks = new ArrayList<SagaItem>();
		ArrayList<SagaItem> items = new ArrayList<SagaItem>();

		ArrayList<SagaItem> witdraw = new ArrayList<SagaItem>();
		
		// Clone resources:
		for (SagaItem sagaItem : resources) {
			if(sagaItem.getType().isBlock()) blocks.add(sagaItem);
			else items.add(sagaItem);
		}
		
		// Witdraw:
		witdraw.addAll(withdrawBlocks(blocks));
		witdraw.addAll(withdrawItems(items));
		
		return witdraw;
		
		
	}

	
	/**
	 * Withdraw blocks.
	 * 
	 * @param items saga block item types to withdraw
	 * @return block that were removed
	 */
	public ArrayList<SagaItem> withdrawBlocks(ArrayList<SagaItem> resources) {

		
		ArrayList<Block> possibleStorage = findHighestFull();

		// Clone resources:
		ArrayList<SagaItem> withdraw = new ArrayList<SagaItem>();
		for (SagaItem sagaItem : resources) {
			SagaItem newItem = new SagaItem(sagaItem);
			newItem.setAmount(0);
			withdraw.add(newItem);
		}
		
		// Withdraw: 
		for (Block block : possibleStorage) {
			
			for (int j = 0; j < withdraw.size(); j++) {
				
				SagaItem sagaItem = withdraw.get(j);
				SagaItem resource = resources.get(j);
				
				if(!sagaItem.checkRepresents(block)) continue;
				if(sagaItem.getAmount() >= resource.getAmount()) continue;
				
				sagaItem.modifyAmount(1);
				block.setType(Material.AIR);
				
				if(possibleStorage.size() == 0) possibleStorage = findHighestFull();
				
				break;
				
			}
			
		}

		// Filter 0 amount:
		ListIterator<SagaItem> it = withdraw.listIterator();
		while (it.hasNext()) {
			SagaItem item = it.next();
			if(item.getAmount() == 0) it.remove();
		}
		
		return withdraw;
		
		
	}

	/**
	 * Withdraw blocks.
	 * 
	 * @param items saga block item types to withdraw
	 * @param resources items that were removed
	 */
	public ArrayList<SagaItem> withdrawItems(ArrayList<SagaItem> resources) {

		
		ArrayList<Chest> possibleStorage = findChests();
		
		// Clone resources:
		ArrayList<SagaItem> withdraw = new ArrayList<SagaItem>();
		for (SagaItem sagaItem : resources) {
			SagaItem newItem = new SagaItem(sagaItem);
			newItem.setAmount(0);
			withdraw.add(newItem);
		}
		
		// Withdraw: 
		for (Chest chest : possibleStorage) {
			
			for (int j = 0; j < withdraw.size(); j++) {
				
				ItemStack removeItem = resources.get(j).createItem();
				if(removeItem.getAmount() == 0) continue;
				
				Inventory inventory = chest.getInventory();
				ItemStack remaining = inventory.removeItem(removeItem).get(0);
				
				int mod = resources.get(j).getAmount();
				if(remaining != null) mod-= remaining.getAmount();
				
				withdraw.get(j).modifyAmount(mod);
				
			}
			
			chest.update();
			
		}
		
		// Filter 0 amount:
		ListIterator<SagaItem> it = withdraw.listIterator();
		while (it.hasNext()) {
			SagaItem item = it.next();
			if(item.getAmount() == 0) it.remove();
		}
		
		return withdraw;
		
		
	}
	
	
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
	 * Gets all lowest empty storage blocks.
	 * 
	 * @return lowest empty storage blocks
	 */
	private ArrayList<Block> findHighestFull() {

		ArrayList<Block> blocks = new ArrayList<Block>();
		ArrayList<StorageArea> storages = getStorageAreas();
		
		for (StorageArea storageArea : storages) {
			blocks.addAll(storageArea.getHighestFull());
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
	
	
}
