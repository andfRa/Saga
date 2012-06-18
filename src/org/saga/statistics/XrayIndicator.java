package org.saga.statistics;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.saga.player.SagaPlayer;


public class XrayIndicator {
	
	
	/**
	 * X-ray blocks.
	 */
	private static HashSet<Material> XRAY_BLOCKS = getXrayBlocks();
	
	/**
	 * Transparent materials.
	 */
	private static HashSet<Material> TRANSPARENT = getTransparent();
	
	
	/**
	 * Called when a block is broken.
	 * 
	 * @param sagaPlayer saga player
	 * @param event event
	 */
	public static void onBlockBreak(SagaPlayer sagaPlayer, BlockBreakEvent event) {


		// Trigger by stone, dirt and gravel:
		if(event.getBlock() == null || (
				event.getBlock().getType() != Material.STONE &&
				event.getBlock().getType() != Material.DIRT &&
				event.getBlock().getType() != Material.GRAVEL
		)) return;

		// Add stone as a vein:
		if(event.getBlock().getType() == Material.STONE) StatisticsManager.manager().addFoundVein(sagaPlayer.getName(), Material.STONE);
		
		// Get vein:
		Block vein = getVein(event.getBlock());
		if(vein == null) return;
		
		// Get relative:
		HashSet<Block> targets = new HashSet<Block>();
		HashSet<Block> others = new HashSet<Block>();
		getRelative(vein, targets, others, 30);
		
		
		// Check for transparent blocks:
		for (Block block : others) {
			if(TRANSPARENT.contains(block.getType())) return;
		}
		
		// Add vein:
		HashSet<Material> veinMaterials = new HashSet<Material>();
		for (Block block : targets) {
			
			if(veinMaterials.contains(block.getType())) continue;
			
			veinMaterials.add(block.getType());
			
			StatisticsManager.manager().addFoundVein(sagaPlayer.getName(), block.getType());
			
		}
		
		
	}
	
	/**
	 * Gets relative vein block.
	 * 
	 * @param anchor anchor block
	 * @return vein block, null if none
	 */
	private static Block getVein(Block anchor) {

		Block relative = null;

		relative = anchor.getRelative(BlockFace.NORTH);
		if(XRAY_BLOCKS.contains(relative.getType())){
			return relative;
		}

		relative = anchor.getRelative(BlockFace.EAST);
		if(XRAY_BLOCKS.contains(relative.getType())){
			return relative;
		}

		relative = anchor.getRelative(BlockFace.SOUTH);
		if(XRAY_BLOCKS.contains(relative.getType())){
			return relative;
		}

		relative = anchor.getRelative(BlockFace.WEST);
		if(XRAY_BLOCKS.contains(relative.getType())){
			return relative;
		}

		relative = anchor.getRelative(BlockFace.UP);
		if(XRAY_BLOCKS.contains(relative.getType())){
			return relative;
		}

		relative = anchor.getRelative(BlockFace.DOWN);
		if(XRAY_BLOCKS.contains(relative.getType())){
			return relative;
		}

		return null;
		
		
	}
	
	/**
	 * Gets all relative ores.
	 * 
	 * @param anchor anchor block
	 * @param targetBlocks target block
	 * @param otherBlocks other blocks
	 * @param depthremain depth
	 */
	private static void getRelative(Block anchor, HashSet<Block> targetBlocks, HashSet<Block> otherBlocks, int depthremain) {

			
		if(targetBlocks.contains(anchor) || otherBlocks.contains(anchor)) return;
		if(depthremain < 1) return;
		
		targetBlocks.add(anchor);
		
		Block relative = null;

		relative = anchor.getRelative(BlockFace.NORTH);
		if(XRAY_BLOCKS.contains(relative.getType())){
			getRelative(relative, targetBlocks, otherBlocks, depthremain-1);
		}else{
			otherBlocks.add(relative);
		}

		relative = anchor.getRelative(BlockFace.EAST);
		if(XRAY_BLOCKS.contains(relative.getType())){
			getRelative(relative, targetBlocks, otherBlocks, depthremain-1);
		}else{
			otherBlocks.add(relative);
		}

		relative = anchor.getRelative(BlockFace.SOUTH);
		if(XRAY_BLOCKS.contains(relative.getType())){
			getRelative(relative, targetBlocks, otherBlocks, depthremain-1);
		}else{
			otherBlocks.add(relative);
		}

		relative = anchor.getRelative(BlockFace.WEST);
		if(XRAY_BLOCKS.contains(relative.getType())){
			getRelative(relative, targetBlocks, otherBlocks, depthremain-1);
		}else{
			otherBlocks.add(relative);
		}

		relative = anchor.getRelative(BlockFace.UP);
		if(XRAY_BLOCKS.contains(relative.getType())){
			getRelative(relative, targetBlocks, otherBlocks, depthremain-1);
		}else{
			otherBlocks.add(relative);
		}

		relative = anchor.getRelative(BlockFace.DOWN);
		if(XRAY_BLOCKS.contains(relative.getType())){
			getRelative(relative, targetBlocks, otherBlocks, depthremain-1);
		}else{
			otherBlocks.add(relative);
		}
		

	}
	
	
	// Materials:
	/**
	 * Gets all xray targets.
	 * 
	 * @return xray targets
	 */
	public static HashSet<Material> getXrayBlocks(){
		
		
		HashSet<Material> materials = new HashSet<Material>();
		
		materials.add(Material.COAL_ORE);
		materials.add(Material.IRON_ORE);
		materials.add(Material.GOLD_ORE);
		materials.add(Material.LAPIS_ORE);
		materials.add(Material.DIAMOND_ORE);
		materials.add(Material.MOSSY_COBBLESTONE);
		
		return materials;
		
		
	}
	
	/**
	 * Gets all transparent materials.
	 * 
	 * @return transparent materials
	 */
	public static HashSet<Material> getTransparent(){
		
		
		HashSet<Material> materials = new HashSet<Material>();
		
		materials.add(Material.AIR);
		materials.add(Material.WATER);
		materials.add(Material.STATIONARY_WATER);
		
		return materials;
		
		
	}
	
	
}
