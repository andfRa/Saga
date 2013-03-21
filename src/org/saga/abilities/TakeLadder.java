package org.saga.abilities;

import java.util.ArrayList;

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
import org.saga.messages.AbilityMessages;
import org.saga.utility.InventoryUtil;

public class TakeLadder extends Ability{

	
	/**
	 * Ladder height key.
	 */
	transient private static String LADDER_HEIGHT_KEY = "ladder height";

	/**
	 * Ladder maximum distance.
	 */
	transient private static Integer MAX_DISTANCE = 3;

	
	
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public TakeLadder(AbilityDefinition definition) {
        super(definition);
	}

	
	
	// Usage:
	@Override
	public boolean handleInteractPreTrigger(PlayerInteractEvent event) {

		if(event.isCancelled()) return false;
		
		// Check placement:
		Block targetBlock = event.getPlayer().getTargetBlock(null, MAX_DISTANCE);
		if(targetBlock == null || targetBlock.getType() != Material.LADDER) return false;
		
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
		
		// Check placement:
		Block targetBlock = event.getPlayer().getTargetBlock(null, MAX_DISTANCE);
		if(targetBlock == null || targetBlock.getType() != Material.LADDER) return false;
		
		// Height:
		int height = getDefinition().getFunction(LADDER_HEIGHT_KEY).intValue(getScore());
		
		// Down:
		ArrayList<Block> ladder = new ArrayList<Block>();
		byte data = targetBlock.getData();
		for (int i = 0; i < height + 1; i++) {
			
			Block block = targetBlock.getRelative(BlockFace.UP, i);
			if(block.getType() == Material.LADDER && block.getData() == data){
				ladder.add(block);
			}
			else break;
			
		}
		
		// Up:
		for (int i = 1; i <= height + 1; i++) {
			
			Block block = targetBlock.getRelative(BlockFace.DOWN, i);
			if(block.getType() == Material.LADDER && block.getData() == data){
				ladder.add(block);
			}
			else break;
			
		}
		
		// Ladder too long:
		if(height < ladder.size()){
			getSagaLiving().message(AbilityMessages.ladderTooLong(this));
			return false;
		}

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
		for (Block ladderBlock : ladder) {

			// Event:
			BlockBreakEvent bevent = new BlockBreakEvent(ladderBlock, player);
			Saga.plugin().getServer().getPluginManager().callEvent(bevent);
			if(bevent.isCancelled()) return triggered;
			
			// Remove:
			ladderBlock.setType(Material.AIR);
			triggered = true;
			
			// Add ladder:
			InventoryUtil.addItem(new ItemStack(Material.LADDER, 1), inventory, player.getLocation());
			
		}
		
		// Take control:
		event.setCancelled(true);
		
		// Effect:
		if(triggered){
			player.updateInventory();
			getSagaLiving().playGlobalEffect(Effect.STEP_SOUND, Material.LADDER.getId(), targetBlock.getLocation());
		}
	
		return triggered;
		
		
	}
	
	
}
