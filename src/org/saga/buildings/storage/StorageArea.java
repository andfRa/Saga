package org.saga.buildings.storage;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.saga.SagaLogger;
import org.saga.chunks.ChunkBundleManager;
import org.saga.chunks.SagaChunk;
import org.saga.exceptions.InvalidLocationException;
import org.saga.player.SagaPlayer;
import org.saga.shape.BlockFilter;
import org.saga.shape.RelativeShape;
import org.saga.shape.RelativeShape.Orientation;
import org.saga.utility.SagaLocation;

public class StorageArea {

	
	/**
	 * Storage shape.
	 */
	private final static RelativeShape SHAPE = createShape();
	
	/**
	 * Filter for empty space.
	 */
	private final static BlockFilter EMPTY_FILTER = createEmptyFilter();
	
	/**
	 * Filter for not empty space.
	 */
	private final static BlockFilter FULL_FILTER = createFullFilter();
	
	/**
	 * Filter for chests.
	 */
	private final static BlockFilter CHEST_FILTER = createChestFilter();
	
	
	/**
	 * Anchor point.
	 */
	private SagaLocation anchor;
	
	/**
	 * Shape orientation.
	 */
	private Orientation orientation;
	
	/**
	 * Storage area size.
	 */
	transient private Integer size;
	
	
	
	// Initialisation:
	/**
	 * Creates a storage area. Sets storage anchor location and orientation.
	 * 
	 * @param sagaPlayer saga player
	 */
	public StorageArea(SagaPlayer sagaPlayer) {

		
		anchor = new SagaLocation(sagaPlayer.getLocation());
		orientation = sagaPlayer.getOrientation();
		size = 1;
		

	}
	
	/**
	 * Fixes all problematic fields.
	 * 
	 * @throws InvalidLocationException when the anchor point is invalid
	 */
	public void complete() throws InvalidLocationException {
		
		
		if (anchor == null) {
			SagaLogger.nullField(this, "anchor");
			throw new InvalidLocationException("null",null,null,null);
		}
		anchor.complete();
		
		if(orientation == null){
			SagaLogger.nullField(this, "anchor");
			orientation = Orientation.NORTH;
		}
		
		size = 1;

		
	}
	
	/**
	 * Sets storage size.
	 * 
	 * @param size size
	 */
	public void setSize(Integer size) {
		this.size = size;
	}
	
	
	// Positioning:
	/**
	 * Check if the location is in the storage area.
	 * 
	 * @param pBlock block
	 * @return true if in the storage area
	 */
	public boolean checkBelongs(Block pBlock) {

		
		ArrayList<Block> blocks = SHAPE.getBlocks(anchor.getLocation(), orientation, size);
		
		// Check:
		for (Block block : blocks) {
			
			if(block.equals(pBlock)) return true;
			
		}
		
		return false;

		
	}
	
	/**
	 * Checks if the storage areas overlap.
	 * 
	 * @param otherStoreArea other storage area.
	 * @return true if overlap
	 */
	public boolean checkOverlap(StorageArea otherStoreArea) {


		ArrayList<Block> blocks = getAllStorage();
		
		for (Block block : blocks) {
			
			if(otherStoreArea.checkBelongs(block)) return true;
			
		}
		
		return false;
		

	}
	
	/**
	 * Gets the Saga chunks the storage area is located on. 
	 * 
	 * @return saga chunks the storage area is on
	 */
	public ArrayList<SagaChunk> getSagaChunks() {

		
		ArrayList<Block> blocks = SHAPE.getBlocks(anchor.getLocation(), orientation, size);
		
		// Bukkit chunks:
		HashSet<Chunk> bukkitChunks = new HashSet<Chunk>();
		for (Block block : blocks) {
			
			bukkitChunks.add(block.getChunk());
			
		}
		
		// Saga chunks
		ArrayList<SagaChunk> sagaChunks = new ArrayList<SagaChunk>();
		for (Chunk chunk : bukkitChunks) {
			
			sagaChunks.add(ChunkBundleManager.manager().getSagaChunk(chunk));
			
		}
		
		return sagaChunks;
		
		
	}
	
	
	
	// Storage:
	/**
	 * Gets all blocks for the storage area.
	 * 
	 * @return all storage area block
	 */
	public ArrayList<Block> getAllStorage() {

		return SHAPE.getBlocks(anchor.getLocation(), orientation, size);
		
	}
	
	
	/**
	 * Adds blocks to storage.
	 * 
	 * @param blocks to add
	 * @return blocks to add
	 */
	public ItemStack storeBlock(ItemStack toStore) {

		
		// Not block:
		if(!toStore.getType().isBlock()) return toStore;
		
		// Add block:
		ArrayList<Block> blocks = SHAPE.getBlocks(anchor.getLocation(), orientation, size, EMPTY_FILTER);

		for (Block storeBlock : blocks) {
			
			if(toStore.getAmount() < 1) return toStore;
			
			storeBlock.setType(toStore.getType());
			storeBlock.setData(toStore.getData().getData());
			
			toStore.setAmount(toStore.getAmount() - 1);
			
		}
		
		return toStore;
		
		
	}
	
	/**
	 * Withdraw blocks from the storage.
	 * 
	 * @param fromStore withdrawn blocks
	 * @param amount requested amount
	 * @return withdrawn blocks
	 */
	public ItemStack withdrawBlock(ItemStack fromStore, Integer amount) {

		
		// Not block:
		if(!fromStore.getType().isBlock()) return fromStore;
		
		// Withdraw block:
		ArrayList<Block> blocks = SHAPE.getBlocks(anchor.getLocation(), orientation, size, FULL_FILTER);

		for (int i = blocks.size() - 1; i >= 0; i--) {

			Block storeBlock = blocks.get(i);

			if(storeBlock.getType() != fromStore.getType()) continue;
			
			if(fromStore.getAmount() >= amount) break;
			
			storeBlock.setType(Material.AIR);
			
			fromStore.setAmount(fromStore.getAmount() + 1);
			
		}
		
		return fromStore;
		
		
	}

	
	/**
	 * Adds blocks to storage.
	 * 
	 * @param items to add
	 * @return remaining items
	 */
	public ItemStack storeItem(ItemStack toStore) {

		
		// Not item:
		if(toStore.getType().isBlock()) return toStore;
		
		// Get chests:
		ArrayList<Block> blocks = SHAPE.getBlocks(anchor.getLocation(), orientation, size, CHEST_FILTER);
		
		ArrayList<Chest> chests = new ArrayList<Chest>();
		for (Block block : blocks) {
			if(block.getState() instanceof Chest) chests.add((Chest) block.getState());
		}

		for (Chest chest : chests) {
			
			if(toStore.getAmount() < 1) return toStore;
			
			ItemStack remaining = chest.getBlockInventory().addItem(toStore).get(0);
			
			if(remaining == null){
				toStore.setAmount(0);
			}else{
				toStore.setAmount(remaining.getAmount());
			}
			
		}
		
		return toStore;
		
		
	}

	/**
	 * Withdraw items from the storage.
	 * 
	 * @param fromStore withdrawn items
	 * @param amount requested amount
	 * @return withdrawn items
	 */
	public ItemStack withdrawItem(ItemStack fromStore, Integer amount) {

		
		// Not item:
		if(fromStore.getType().isBlock()) return fromStore;
		
		// Get chests:
		ArrayList<Block> blocks = SHAPE.getBlocks(anchor.getLocation(), orientation, size, CHEST_FILTER);
		
		ArrayList<Chest> chests = new ArrayList<Chest>();
		for (Block block : blocks) {
			if(block.getState() instanceof Chest) chests.add((Chest) block.getState());
		}

		for (Chest chest : chests) {

			if(fromStore.getAmount() >= amount) break;
			
			ItemStack toRemove = fromStore.clone();
			toRemove.setAmount(amount - toRemove.getAmount());
			
			ItemStack remaining = chest.getBlockInventory().removeItem(toRemove).get(0);
			
			if(remaining == null){
				fromStore.setAmount(amount);
			}else{
				fromStore.setAmount(amount - remaining.getAmount());
			}
			
		}
		
		return fromStore;
		
		
	}

	
	/**
	 * Counts the amount of items available.
	 * 
	 * @param item item
	 * @return amount
	 */
	public Integer countStored(ItemStack item) {

		
		int amount = 0;

		// Contents:
		ArrayList<Block> blocks = SHAPE.getBlocks(anchor.getLocation(), orientation, size, FULL_FILTER);
		
		for (Block block : blocks) {
			
			// Chest:
			if(block.getState() instanceof Chest){
				
				ItemStack[] inventory = ((Chest) block.getState()).getBlockInventory().getContents();
				for (int i = 0; i < inventory.length; i++) {

					if(inventory[i] == null) continue;
					
					// From https://github.com/Bukkit/CraftBukkit/blob/master/src/main/java/org/bukkit/craftbukkit/inventory/CraftInventory.java
					boolean equals = item.getTypeId() == inventory[i].getTypeId() && item.getDurability() == inventory[i].getDurability() && item.getEnchantments().equals(inventory[i].getEnchantments());
					
					if(!equals) continue;
					
					amount+= inventory[i].getAmount();
					
				}
				
			}
			
			// Blocks:
			else{
				
				// Material:
				if(block.getType() != item.getType()) continue;

				// Data:
				if(block.getData() != item.getData().getData()) continue;
				
				amount+= 1;
				
			}
			
			
		}
		
		return amount;
		
		
	}
	
	
	
	// Shape:
	/**
	 * Creates the shape for the storage.
	 * 
	 * @return storage shape
	 */
	private static RelativeShape createShape() {

		
		RelativeShape shape = new RelativeShape();
		
		// First layer:
		shape.addOffset(-2, 0, 0, 3);
		shape.addOffset(-1, 0, -1, 13);
		shape.addOffset(-1, 0, 1, 2);
		shape.addOffset(-1, 0, 0, 1);
		shape.addOffset(-3, 0, 0, 5);
		shape.addOffset(-2, 0, -1, 14);
		shape.addOffset(-3, 0, -1, 15);
		shape.addOffset(-4, 0, -1, 16);
		shape.addOffset(-2, 0, 1, 4);
		shape.addOffset(-3, 0, 1, 6);
		shape.addOffset(-4, 0, 0, 17);
		shape.addOffset(-4, 0, 1, 18);
		
		// Second layer:
		shape.addOffset(-1, 1, -1, 19);
		shape.addOffset(-4, 1, -1, 22);
		shape.addOffset(-1, 1, 0, 7);
		shape.addOffset(-3, 1, -1, 21);
		shape.addOffset(-2, 1, 1, 10);
		shape.addOffset(-3, 1, 0, 11);
		shape.addOffset(-2, 1, -1, 20);
		shape.addOffset(-2, 1, 0, 9);
		shape.addOffset(-3, 1, 1, 12);
		shape.addOffset(-4, 1, 0, 23);
		shape.addOffset(-1, 1, 1, 8);
		shape.addOffset(-4, 1, 1, 24);
		
		// Third layer:
		shape.addOffset(-1, 2, -1, 31);
		shape.addOffset(-1, 2, 1, 26);
		shape.addOffset(-2, 2, 1, 28);
		shape.addOffset(-3, 2, 1, 30);
		shape.addOffset(-1, 2, 0, 25);
		shape.addOffset(-3, 2, -1, 33);
		shape.addOffset(-2, 2, -1, 32);
		shape.addOffset(-4, 2, -1, 34);
		shape.addOffset(-3, 2, 0, 29);
		shape.addOffset(-4, 2, 0, 35);
		shape.addOffset(-2, 2, 0, 27);
		shape.addOffset(-4, 2, 1, 36);
		
		// Not randomised backup:
//
//		// First layer:
//		shape.addOffset(-1, 0, -1, 13);
//		shape.addOffset(-1, 0, 0, 1);
//		shape.addOffset(-1, 0, 1, 2);
//		
//		shape.addOffset(-2, 0, -1, 14);
//		shape.addOffset(-2, 0, 0, 3);
//		shape.addOffset(-2, 0, 1, 4);
//		
//		shape.addOffset(-3, 0, -1, 15);
//		shape.addOffset(-3, 0, 0, 5);
//		shape.addOffset(-3, 0, 1, 6);
//		
//		shape.addOffset(-4, 0, -1, 16);
//		shape.addOffset(-4, 0, 0, 17);
//		shape.addOffset(-4, 0, 1, 18);
//		
//		// Second layer:
//		shape.addOffset(-1, 1, -1, 19);
//		shape.addOffset(-1, 1, 0, 7);
//		shape.addOffset(-1, 1, 1, 8);
//		
//		shape.addOffset(-2, 1, -1, 20);
//		shape.addOffset(-2, 1, 0, 9);
//		shape.addOffset(-2, 1, 1, 10);
//		
//		shape.addOffset(-3, 1, -1, 21);
//		shape.addOffset(-3, 1, 0, 11);
//		shape.addOffset(-3, 1, 1, 12);
//
//		shape.addOffset(-4, 1, -1, 22);
//		shape.addOffset(-4, 1, 0, 23);
//		shape.addOffset(-4, 1, 1, 24);
//		
//		// Third layer:
//		shape.addOffset(-1, 2, -1, 31);
//		shape.addOffset(-1, 2, 0, 25);
//		shape.addOffset(-1, 2, 1, 26);
//		
//		shape.addOffset(-2, 2, -1, 32);
//		shape.addOffset(-2, 2, 0, 27);
//		shape.addOffset(-2, 2, 1, 28);
//		
//		shape.addOffset(-3, 2, -1, 33);
//		shape.addOffset(-3, 2, 0, 29);
//		shape.addOffset(-3, 2, 1, 30);
//
//		shape.addOffset(-4, 2, -1, 34);
//		shape.addOffset(-4, 2, 0, 35);
//		shape.addOffset(-4, 2, 1, 36);
		
		return shape;

		
	}
	
	/**
	 * Creates a filter for only air blocks.
	 * 
	 * @return filter for only air blocks
	 */
	private static BlockFilter createEmptyFilter() {

		BlockFilter filter = new BlockFilter();
		filter.addMaterial(Material.AIR);

		return filter;
		
	}
	
	/**
	 * Creates a filter for only not air blocks.
	 * 
	 * @return filter for only not air blocks
	 */
	private static BlockFilter createFullFilter() {

		BlockFilter filter = new BlockFilter();
		filter.addMaterial(Material.AIR);
		filter.flip();
		
		return filter;
		
	}
	
	/**
	 * Creates a filter for only chest blocks.
	 * 
	 * @return filter for only chest blocks
	 */
	private static BlockFilter createChestFilter() {

		BlockFilter filter = new BlockFilter();
		filter.addMaterial(Material.CHEST);
		
		return filter;
		
	}
	
	
	
	// Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
	
		if(obj instanceof StorageArea){
			
			return ((StorageArea) obj).anchor.getLocation().getBlock().equals(anchor.getLocation().getBlock());
			
		}
	
		return false;

	}
	
	
	
}
