package org.saga.buildings.production;

import java.util.ArrayList;
import java.util.List;

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
import org.saga.messages.BuildingMessages;

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
		

		// Offer free:
		for (SagaResource resource : resources) {
			resource.offerFree();
		}
		
		// Buildings:
		List<String> buildingNames = getDefinition().getOfferBuildings();
		ArrayList<ProductionBuilding> buildings = new ArrayList<ProductionBuilding>();
		
		for (String buildingName : buildingNames) {
			
			ArrayList<ProductionBuilding> offerBuildings = getChunkBundle().getBuildings(ProductionBuilding.class, buildingName);
			buildings.addAll(offerBuildings);
			
		}
		
		// Produce:
		SagaItem[] allItems = new SagaItem[resources.size()];
		for (int i = 0; i < allItems.length; i++) {
			
			SagaItem sagaItem = resources.get(i).produceItem();
			if(sagaItem == null){
				sagaItem = new SagaItem(resources.get(i));
				sagaItem.setAmount(0.0);
			}
			allItems[i] = sagaItem;
			
		}
		
		// Requests:
		double[][] requests = findRequests(resources, buildings);
		double[] reqTotal = findTotals(requests);
		
		// Take offers from produced items:
		SagaItem[] offerItems = new SagaItem[resources.size()];
		for (int i = 0; i < resources.size(); i++) {
			
			SagaItem offer = new SagaItem(resources.get(i));
			offer.setAmount(0.0);
			SagaItem item = allItems[i];
			offerItems[i] = offer;
			
			double reqAmount = reqTotal[i];
			if(reqAmount > item.getAmount()) reqAmount = item.getAmount();
			offer.modifyAmount(reqAmount);
			item.modifyAmount(-reqAmount);

		}
		
		// Only when loaded:
		if(getSagaChunk().isChunkLoaded()){
			
			// Inform store:
			ArrayList<SagaItem> storeable = filterStoreable(allItems);
			if(storeable.size() != 0) getChunkBundle().information(this, BuildingMessages.produced(storeable));
			
			// Store remaining:
			store(allItems);
			
			// Prepare missing items:
			SagaItem[] missingItems = new SagaItem[resources.size()];
			for (int i = 0; i < resources.size(); i++) {
				
				double reqAmount = reqTotal[i];
				
				SagaItem newItem = new SagaItem(resources.get(i));
				newItem.setAmount(reqAmount - offerItems[i].getAmount());
				missingItems[i] = newItem;
				
			}
			
			// Withdraw missing:
			SagaItem[] withdrawItems = withdraw(missingItems);
			for (int i = 0; i < resources.size(); i++) {
				offerItems[i].modifyAmount(withdrawItems[i].getAmount());
			}
			
		}
		
		// Find weights:
		double[][] weights = new double[resources.size()][buildings.size()];
		for (int i = 0; i < resources.size(); i++) {
			
			for (int j = 0; j < buildings.size(); j++) {
				if(reqTotal[i] == 0) weights[i][j] = 0.0;
				else weights[i][j] = requests[i][j] / reqTotal[i];
			}
			
		}
		
		// Offer items:
		for (int i = 0; i < offerItems.length; i++) {
			
			for (int j = 0; j < buildings.size(); j++) {
				
				double weight = weights[i][j];
				ProductionBuilding prBuilding = buildings.get(j);
				
				SagaItem offerItem = new SagaItem(offerItems[i]);
				offerItem.setAmount(offerItem.getAmount() * weight);
				
				prBuilding.offer(offerItem);
				
			}
			
		}
		
		
	}
	
	
	
	// Offer:
	/**
	 * Offers items for production.
	 * 
	 * @param items items to offer
	 */
	public void offer(SagaItem[] items) {

		for (int i = 0; i < items.length; i++) {
			offer(items[i]);
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
	 * Finds the amount of items requested.
	 * 
	 * @param items items
	 * @return requested amount
	 */
	private double[] findRequests(ArrayList<SagaResource> items) {

		double[] requests = new double[items.size()];
		
		for (int i = 0; i < items.size(); i++) {
			
			SagaItem item = items.get(i);
			
			for (SagaResource resource : this.resources) {
				requests[i] = requests[i] + resource.countRequired(item);
			}
			
		}
		
		return requests;
		
	}

	/**
	 * Finds the amount of items requested.
	 * 
	 * @param items items
	 * @param buildings buildings
	 * @return requested amount
	 */
	private static double[][] findRequests(ArrayList<SagaResource> items, ArrayList<ProductionBuilding> buildings) {

		double[][] requests = new double[items.size()][buildings.size()];
		
		for (int b = 0; b < buildings.size(); b++) {

			double[] bldgRequested = buildings.get(b).findRequests(items);
			
			for (int i = 0; i < bldgRequested.length; i++) {
				requests[i][b] = bldgRequested[i];
			}
			
		}
		
		return requests;
		
	}

	/**
	 * Finds the total amount of items requested.
	 * 
	 * @param items items
	 * @param buildings buildings
	 * @return requested amount
	 */
	private static double[] findTotals(double[][] requests) {

		double[] totals = new double[requests.length];
		
		for (int i = 0; i < requests.length; i++) {

			for (int b = 0; b < requests[i].length; b++) {
				totals[i] = totals[i] + requests[i][b];
			}
			
		}
		
		return totals;
		
	}
	
	
	
	// Store:
	/**
	 * Stores items and blocks.
	 * 
	 * @param blocksItems items or blocks to store
	 */
	public void store(SagaItem[] blocksItems) {
		
		for (int i = 0; i < blocksItems.length; i++) {
			
			SagaItem sagaItem = blocksItems[i];
			
			if(sagaItem.getType().isBlock()){
				storeBlock(sagaItem);
			}else{
				storeItem(sagaItem);
			}
			
		}
		
	}
	
	/**
	 * Stores blocks.
	 * 
	 * @param items saga block to store
	 */
	public void storeBlock(SagaItem sagaItem) {

		
		ArrayList<Block> possibleStorage = findLowestEmpty();
		
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
	
	/**
	 * Stores items.
	 * 
	 * @param items saga item to store
	 */
	public void storeItem(SagaItem sagaItem) {

		ArrayList<Chest> possibleStorage = findChests();
		
		while(possibleStorage.size() > 0 && sagaItem.getAmount() >= 1.0){
			
			// Inventory:
			int index = Saga.getRandom().nextInt(possibleStorage.size());
			Chest chest = possibleStorage.get(index);
			Inventory inventory = chest.getInventory();

			ItemStack item = sagaItem.createItem();
			if(item.getAmount() == 0) break;
			sagaItem.modifyAmount(-item.getAmount());
			
			inventory.addItem(item);
			
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
	public SagaItem[] withdraw(SagaItem[] requested) {

		
		SagaItem[] withdraw = new SagaItem[requested.length];
		
		// Clone resources:
		for (int i = 0; i < requested.length; i++) {
			
			SagaItem requestedItem = requested[i];
			
			if(requestedItem.getType().isBlock()){
				withdraw[i] = withdrawBlock(requested[i]);
			}else{
				withdraw[i] = withdrawItem(requested[i]);
			}
			
		}
		
		return withdraw;
		
	}
	
	/**
	 * Withdraw blocks.
	 * 
	 * @param requestedItem requested blocks
	 * @return retrieved blocks
	 */
	public SagaItem withdrawBlock(SagaItem requestedItem) {

		
		ArrayList<Block> possibleStorage = findAllStorage();
		SagaItem removedItem = new SagaItem(requestedItem);
		removedItem.setAmount(0.0);
		
		// Don't remove less than one item:
		if(requestedItem.getAmount() < 1.0) return removedItem;
		
		// Withdraw: 
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
	 * Withdraw blocks.
	 * 
	 * @param requestedItem requested items
	 * @return retrieved items
	 */
	public SagaItem withdrawItem(SagaItem requestedItem) {
		
		ArrayList<Chest> possibleStorage = findChests();
		SagaItem removedItem = new SagaItem(requestedItem);
		removedItem.setAmount(0.0);

		// Don't remove less than one item:
		if(removedItem.getAmount() < 1.0) return removedItem;
		
		
		// Withdraw: 
		for (Chest chest : possibleStorage) {
			
			Inventory inventory = chest.getInventory();
			ItemStack removeStack = removedItem.createItem();
			ItemStack remaining = inventory.removeItem(removeStack).get(0);
			
			int mod = removeStack.getAmount();
			if(remaining != null) mod-= remaining.getAmount();
			
			removedItem.modifyAmount(mod);
			
			chest.update();
			
		}
		
		return removedItem;
		
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
	
	
	
	// Resources:
	/**
	 * Gets all resources.
	 * 
	 * @return resources
	 */
	public ArrayList<SagaResource> getResources() {
		return resources;
	}
	
	/**
	 * Filters items that can be stored.
	 * 
	 * @param items items
	 * @return items that can be stored
	 */
	private ArrayList<SagaItem> filterStoreable(SagaItem[] items) {

		ArrayList<SagaItem> results = new ArrayList<SagaItem>();
		
		for (int i = 0; i < items.length; i++) {
			if(items[i].getAmount() >= 1) results.add(items[i]);
		}

		return results;
		
	}
	
	
}
