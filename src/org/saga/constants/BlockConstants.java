package org.saga.constants;

import java.util.Hashtable;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class BlockConstants {


	public static final Material[] TRANSPARENT_MATERIALS = new Material[]{Material.AIR, Material.LONG_GRASS, Material.WATER};
	
	private static final Random RANDOM = new Random();
	
	private static Hashtable<MaterialData,  ItemStack[]> firstDrops = createFirstItemDrops();
	
	private static Hashtable<MaterialData, ItemStack[]> secondDrops = createSecondItemDrops();
	
	public static Material[] rightClickInUseMaterials = new Material[]{Material.BOW, Material.WOOD_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLD_HOE, Material.DIAMOND_HOE};
	
	// Normal:
	/**
	 * Checks if the material is transparent
	 * 
	 * @param material material
	 * @return true if transparent
	 */
	public static boolean isTransparent(Material material) {
		
		for (int i = 0; i < TRANSPARENT_MATERIALS.length; i++) {
			if(TRANSPARENT_MATERIALS[i].equals(material)){
				return true;
			}
		}
		return false;
		
	}
	
	/**
	 * Breaks a block.
	 * Drops a redefined block if the redefinition exists in the tables.
	 * Ignores the drop if the metadata is null.
	 * 
	 * @param block block
	 * @param dropMultiplier drop amount multiplier
	 */
	public static void breakBlock(Block block, short dropMultiplier) {

		
		// Don't touch bedrock:
		if(block.getType() == Material.BEDROCK){
			return;
		}
		
		// Get block information:
		Location location = block.getLocation();
		Material blockMaterial = block.getType();
		MaterialData materialData= block.getState().getData();
		
		// Remove:
		block.setType(Material.AIR);
		
		// Ignore the drop if the metadata is null:
		if(materialData == null){
			return;
		}
		
		// First drop:
		ItemStack firstDrop = randomDrop(firstDrops.get(materialData));
		
		// Second drop:
		ItemStack secondDrop = randomDrop(secondDrops.get(materialData));
		
		// Drop:
		if(firstDrop == null && secondDrop == null && !blockMaterial.equals(Material.AIR) && dropMultiplier != 0){
			block.getWorld().dropItemNaturally(location , new ItemStack(blockMaterial , dropMultiplier));
			return;
		}
			
		if(firstDrop != null && !firstDrop.getType().equals(Material.AIR) && firstDrop.getAmount() != 0){
			firstDrop.setAmount(firstDrop.getAmount() * dropMultiplier);
			block.getWorld().dropItemNaturally(location , firstDrop);
		}
		if(secondDrop != null && !secondDrop.getType().equals(Material.AIR) && secondDrop.getAmount() != 0){
			secondDrop.setAmount(secondDrop.getAmount() * dropMultiplier);
			block.getWorld().dropItemNaturally(location , secondDrop);
		}
		
		
	}

	
	// Support:
	private static Hashtable<MaterialData, ItemStack[]> createFirstItemDrops() {

		
		Hashtable<MaterialData, ItemStack[]> itemDrops = new Hashtable<MaterialData,ItemStack[]>();
		
		// Fire:
		itemDrops.put(new MaterialData(Material.FIRE), new ItemStack[]{new ItemStack(Material.FIRE, 0)});
		
		// Grass:
		itemDrops.put(new MaterialData(Material.GRASS), new ItemStack[]{new ItemStack(Material.DIRT, 1)});
		
		// Stone:
		itemDrops.put(new MaterialData(Material.STONE), new ItemStack[]{new ItemStack(Material.COBBLESTONE, 1)});
		
		// Glass:
		itemDrops.put(new MaterialData(Material.GLASS), new ItemStack[]{new ItemStack(Material.GLASS, 0)});
		
		// Crops 0x1:
		itemDrops.put(new MaterialData(Material.CROPS, (byte)0x0), new ItemStack[]{new ItemStack(Material.SEEDS, 1)});
		
		// Crops 0x1:
		itemDrops.put(new MaterialData(Material.CROPS, (byte)0x1), new ItemStack[]{new ItemStack(Material.SEEDS, 0), new ItemStack(Material.SEEDS, 1)});
		
		// Crops 0x2:
		itemDrops.put(new MaterialData(Material.CROPS, (byte)0x2), new ItemStack[]{new ItemStack(Material.SEEDS, 0), new ItemStack(Material.SEEDS, 1),new ItemStack(Material.SEEDS, 2)});
				
		// Crops 0x3:
		itemDrops.put(new MaterialData(Material.CROPS, (byte)0x3), new ItemStack[]{new ItemStack(Material.SEEDS, 0), new ItemStack(Material.SEEDS, 1), new ItemStack(Material.SEEDS, 2)});
		
		// Crops 0x4:
		itemDrops.put(new MaterialData(Material.CROPS, (byte)0x4), new ItemStack[]{new ItemStack(Material.SEEDS, 0), new ItemStack(Material.SEEDS, 1),new ItemStack(Material.SEEDS, 2)});
		
		// Crops 0x5:
		itemDrops.put(new MaterialData(Material.CROPS, (byte)0x5), new ItemStack[]{new ItemStack(Material.SEEDS, 0), new ItemStack(Material.SEEDS, 1),new ItemStack(Material.SEEDS, 2),new ItemStack(Material.SEEDS, 3)});
		
		// Crops 0x6:
		itemDrops.put(new MaterialData(Material.CROPS, (byte)0x6), new ItemStack[]{new ItemStack(Material.SEEDS, 0) ,new ItemStack(Material.SEEDS, 1),new ItemStack(Material.SEEDS, 2),new ItemStack(Material.SEEDS, 3)});
		
		// Fully grown crops:
		itemDrops.put(new MaterialData(Material.CROPS, (byte)0x7), new ItemStack[]{new ItemStack(Material.SEEDS, 1),new ItemStack(Material.SEEDS, 2),new ItemStack(Material.SEEDS, 3)});
		
		// Sign:
		itemDrops.put(new MaterialData(Material.SIGN_POST), new ItemStack[]{new ItemStack(Material.SIGN, 1)});
		
		
		return itemDrops;
		
		
	}

	private static Hashtable<MaterialData, ItemStack[]> createSecondItemDrops() {

		
		Hashtable<MaterialData, ItemStack[]> itemDrops = new Hashtable<MaterialData,ItemStack[]>();

		// Fully grown crops:
		itemDrops.put(new MaterialData(Material.CROPS, (byte)0x7), new ItemStack[]{new ItemStack(Material.WHEAT, 1), new ItemStack(Material.WHEAT, 2)});
				
		return itemDrops;
		
		
	}
	
	private static ItemStack randomDrop(ItemStack[] items) {

		
		if(items == null){
			return null;
		}
		
		int dropIndex = new Double(RANDOM.nextDouble() * items.length).intValue();
		return items[dropIndex];
		
		
	}
	
	public static Location groundLocation(Location location) {

		int highestY = location.getWorld().getHighestBlockYAt(location);
		location = location.clone();
		location.setY(highestY);
		Block currentBlock = location.getBlock();
		
		Block previousBlock = currentBlock;
		int y = highestY;
		// Loop down:
		while(y > 1){
			previousBlock = currentBlock;
			currentBlock = currentBlock.getRelative(BlockFace.DOWN);
			if( !currentBlock.getType().equals(Material.AIR) || !isTransparent(currentBlock.getType()) ){
				return previousBlock.getLocation();
			}
			y--;
		}
		return currentBlock.getLocation();
		
		
	}
	
	public static boolean checkRightClickInUse(Material material) {
		
		
		for (int i = 0; i < rightClickInUseMaterials.length; i++) {
			if(rightClickInUseMaterials[i].equals(material)){
				return true;
			}
		}
		return false;
		
		
	}
	

}
