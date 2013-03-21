package org.saga.abilities;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.saga.Saga;
import org.saga.player.SagaLiving;
import org.saga.shape.RelativeShape.Orientation;
import org.saga.utility.InventoryUtil;

public class PlaceLadder extends Ability{

	
	/**
	 * Ladder height key.
	 */
	transient private static String LADDER_HEIGHT_KEY = "ladder height";

	
	
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public PlaceLadder(AbilityDefinition definition) {
        super(definition);
	}

	
	
	// Usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#handleInteractPreTrigger(org.bukkit.event.player.PlayerInteractEvent)
	 */
	@Override
	public boolean handleInteractPreTrigger(PlayerInteractEvent event) {
		
		if(event.isCancelled()) return false;
		
		// Check placement:
		Block clickedBlock = event.getClickedBlock();
		if(clickedBlock == null || event.getBlockFace() != BlockFace.UP) return false;
		byte data = getData(clickedBlock.getRelative(BlockFace.UP), null);
		if(!canSupport(clickedBlock.getRelative(BlockFace.UP), data)) return false;
		
		// Normal trigger:
		return handlePreTrigger();
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#triggerInteract(org.bukkit.event.player.PlayerInteractEvent)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean triggerInteract(PlayerInteractEvent event) {
		
		
		boolean triggered = false;
		
		Block clickedBlock = event.getClickedBlock();
		
		// Check placement:
		if(clickedBlock == null || event.getBlockFace() != BlockFace.UP) return false;
		
		SagaLiving<?> sagaLiving = getSagaLiving();
		
		// Data:
		byte data = getData(clickedBlock.getRelative(BlockFace.UP), sagaLiving);
		if(data == -1) return false;

		// Height:
		int height = getDefinition().getFunction(LADDER_HEIGHT_KEY).intValue(getScore());
		
		// Inventory:
		PlayerInventory inventory = null;
		Player player = null;
		if(getSagaLiving().getWrapped() instanceof Player){
			player = (Player) getSagaLiving().getWrapped();
			inventory = player.getInventory();
		}else{
			return false;
		}
		
		// Place ladder:
		for (int i = 1; i <= height; i++) {
			
			// Ladder block:
			Block ladderBlock = clickedBlock.getRelative(BlockFace.UP, i);

			// Has ladder:
			if(!inventory.contains(Material.LADDER, 1)) break;
			
			// Event:
			BlockBreakEvent bevent = new BlockBreakEvent(ladderBlock, player);
			Saga.plugin().getServer().getPluginManager().callEvent(bevent);
			if(bevent.isCancelled()) return triggered;
			
			// Remove ladder:
			InventoryUtil.removeItem(new ItemStack(Material.LADDER, 1), inventory);
			triggered = true;
			
			// Set ladder:
			if(canSupport(ladderBlock, data)){
				ladderBlock.setTypeIdAndData(Material.LADDER.getId(), data, false);
			}else{
				break;
			}
			
		}
		
		// Update inventory:
		if(triggered){
			player.updateInventory();
		}

		// Take control:
		event.setCancelled(true);
		
		// Effect:
		sagaLiving.playGlobalEffect(Effect.STEP_SOUND, Material.LADDER.getId(), clickedBlock.getLocation());
		
		return true;
	
		
	}
	
	/**
	 * Gets ladder data.
	 * 
	 * @param target target block
	 * @param sagaLiving saga entity that placed the ladder, null if ignore
	 * @return block data, -1 if can't attach
	 */
	private static byte getData(Block target, SagaLiving<?> sagaLiving) {

		
		// Try orientation:
		if(sagaLiving != null){
			Orientation orientation = sagaLiving.getOrientation();
			if(orientation == Orientation.NORTH && canAttach(target.getRelative(BlockFace.NORTH))) return 3;
			if(orientation == Orientation.EAST && canAttach(target.getRelative(BlockFace.EAST))) return 4;
			if(orientation == Orientation.SOUTH && canAttach(target.getRelative(BlockFace.SOUTH))) return 2;
			if(orientation == Orientation.WEST && canAttach(target.getRelative(BlockFace.WEST))) return 5;
		}
		
		// Try adjacent:
		if(canAttach(target.getRelative(BlockFace.NORTH))) return 3;
		if(canAttach(target.getRelative(BlockFace.EAST))) return 4;
		if(canAttach(target.getRelative(BlockFace.SOUTH))) return 2;
		if(canAttach(target.getRelative(BlockFace.WEST))) return 5;
		
		return -1;
		
		
	}
	
	/**
	 * Checks if the block can attach a ladder. Not the same block where the ladder is located.
	 * 
	 * @param block block to attach the ladder to
	 * @return true if can attack
	 */
	private static boolean canAttach(Block block) {
		
		return block.getType().isOccluding() && block.getType().isSolid();
		
	}
	
	/**
	 * Checks if the given block can support a ladder.
	 * 
	 * @param block block
	 * @param data ladder orientation data
	 * @return true if can support
	 */
	static boolean canSupport(Block block, byte data) {
		
		if(block.getType() != Material.AIR) return false;
		
		if(data == 3) return canAttach(block.getRelative(BlockFace.NORTH));
		if(data == 4) return canAttach(block.getRelative(BlockFace.EAST));
		if(data == 2) return canAttach(block.getRelative(BlockFace.SOUTH));
		if(data == 5) return canAttach(block.getRelative(BlockFace.WEST));
		
		return false;
		
	}
	
	
}
