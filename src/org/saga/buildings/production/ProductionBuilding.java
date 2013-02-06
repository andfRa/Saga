package org.saga.buildings.production;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.buildings.Building;
import org.saga.buildings.BuildingDefinition;
import org.saga.buildings.Warehouse;
import org.saga.buildings.storage.StorageArea;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.messages.BuildingMessages;
import org.saga.settlements.Settlement;

public class ProductionBuilding extends Building{

	
	/**
	 * Resources.
	 */
	private SagaResource[] resources;
	
	/**
	 * Storage buffers.
	 */
	private double[] buffers;
	
	
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
			resources = new SagaResource[0];
			SagaLogger.nullField(this, "resources");
		}
		
		for (SagaResource recource : resources) {
			recource.complete();
		}
		
		if(buffers == null){
			buffers = new double[resources.length];
			SagaLogger.nullField(this, "buffers");
		}
		
		synchResources();
		
		return true;
		
	}

	/**
	 * Synchronises resources with recipes.
	 * 
	 */
	private void synchResources() {

		SagaRecipe[] recipes = getDefinition().getProductionRecipes();
		
		if(recipes.length != resources.length || recipes.length != buffers.length){
			SagaLogger.warning(this, "resetting resources");
			resetResources();
			return;
		}
		
		for (int i = 0; i < resources.length; i++) {
			if(!resources[i].equalsRecipe(recipes[i])){
				SagaLogger.warning(this, "resetting resources");
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
		
		SagaRecipe[] recipes = getDefinition().getProductionRecipes();
		resources = new SagaResource[recipes.length];

		for (int i = 0; i < recipes.length; i++) {
			resources[i] = new SagaResource(recipes[i]);
		}
		
		buffers = new double[resources.length];
		
	}
	
	
	
	// Production:
	public void work() {

		
		Settlement settlement = getSettlement();
		if(settlement == null) return;
		
		int workAvail = 0;
		
		// Find remaining work:
		double[] workRemain = new double[resources.length];
		double workTotal = 0;
		for (int i = 0; i < workRemain.length; i++) {
			workRemain[i] = resources[i].getRemainingWork();
			workTotal+= workRemain[i];
		}
		
		// Take required role points:
		Set<String> roles = getDefinition().getAllRoles();
		for (String roleName : roles) {
			
			double requested = workTotal - workAvail;
			if(requested >= 0) workAvail+= settlement.takeWorkPoints(roleName, requested);
			
		}
		
		if(workTotal == 0) return;
		
		// Distribute work:
		for (int i = 0; i < workRemain.length; i++) {
			resources[i].work(workAvail * workRemain[i]/workTotal);
		}
		
		
	}
	
	/**
	 * Collects required items.
	 * 
	 */
	public void collect() {


		ArrayList<Warehouse> warehouses = getChunkBundle().getBuildings(Warehouse.class);
		
		// All resources:
		for (int i = 0; i < resources.length; i++) {
			
			SagaResource resource = resources[i];
			Collection<SagaItem> requests = resource.getRequests();
			
			// Handle requests:
			for (SagaItem requestItem : requests) {
				
				// Collect:
				SagaItem collectedItem = new SagaItem(requestItem);
				collectedItem.setAmount(0.0);
				
				for (Warehouse warehouse : warehouses) {
					warehouse.withdraw(requestItem, collectedItem);
				}
				
				// Offer:
				resource.offer(collectedItem);
				
			}
			
			
		}

		
	}

	/**
	 * Produces resources.
	 * 
	 */
	public void produce() {
		
		
		// Produce:
		SagaItem[] produced = new SagaItem[resources.length];
		for (int i = 0; i < produced.length; i++) {
			
			SagaItem sagaItem = resources[i].produceItem();
			if(sagaItem == null){
				sagaItem = new SagaItem(resources[i]);
				sagaItem.setAmount(0.0);
			}
			produced[i] = sagaItem;
			
		}
		
		// Inform production:
		ArrayList<SagaItem> storeable = filterStoreable(produced);
		if(storeable.size() != 0) getChunkBundle().information(this, BuildingMessages.produced(storeable));
			
		// Store here if loaded:
		if(getSagaChunk().isChunkLoaded()){
			for (int i = 0; i < produced.length; i++) {
				store(produced[i]);
			}
		}
		
		// Store remaining in a warehouse:
		ArrayList<Warehouse> warehouses = getChunkBundle().getBuildings(Warehouse.class);
		for (Warehouse warehouse : warehouses) {
			
			for (int i = 0; i < produced.length; i++) {
				warehouse.store(produced[i]);
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

	
	
	// Store:
	/**
	 * Stores item and block resource.
	 * 
	 * @param sagaItem item or block to store
	 */
	private void store(SagaItem sagaItem) {

		if(sagaItem.getType().isBlock()){
			storeBlock(sagaItem);
		}else{
			storeItem(sagaItem);
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
			
			ItemStack remaining = inventory.addItem(item).get(0);
			if(remaining != null) sagaItem.modifyAmount(remaining.getAmount());
			
			chest.update();
			
		}
		
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
	public SagaResource[] getResources() {
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
