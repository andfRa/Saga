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
import org.saga.Saga;
import org.saga.messages.PlayerMessages;
import org.saga.messages.effects.StatsEffectHandler;
import org.saga.player.SagaPlayer;
import org.saga.utility.text.TextUtil;

public class ChopDown extends Ability{

	/**
	 * Tree size key.
	 */
	private static String TREE_SIZE_KEY = "tree size";
	
	/**
	 * Amount logs a tree can have.
	 */
	private static Integer LOGS_LIMIT = 150;
	
	/**
	 * Minimum leaves to logs ratio.
	 */
	private static Integer MINIMUM_LEAVES_LOGS_RATIO = 1;

	
	// Initialisation:
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public ChopDown(AbilityDefinition definition) {
		
        super(definition);
	
	}


	
	// Usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#trigger(org.bukkit.event.player.PlayerInteractEvent)
	 */
	@Override
	public boolean triggerInteract(PlayerInteractEvent event) {

		
		ItemStack itemHand = event.getItem();
		Player player = event.getPlayer();
		
		// Drops:
		boolean triggered = false;
		
		// Pointing a log:
		Block clickedBlock = event.getClickedBlock();
		if(clickedBlock == null || !clickedBlock.getType().equals(Material.LOG)){
			return false;
		}
		
		// Get the tree:
		ArrayList<Block> blocks = new ArrayList<Block>();
		blocks.add(clickedBlock);
		ArrayList<Block> leaves = new ArrayList<Block>();
		getTree(clickedBlock, blocks, leaves);
		
		// Check ratio:
		if(blocks.size() == 0) return false;
		double ratio = new Double(leaves.size()) / new Double(blocks.size());
		
		if(ratio < MINIMUM_LEAVES_LOGS_RATIO){
			
			getSagaLiving().message(notTree());
			return false;
			
		}
		
		// Tree to big:
		Integer treeSize = getDefinition().getFunction(TREE_SIZE_KEY).value(getScore()).intValue();
		if(treeSize < blocks.size()){
			
			getSagaLiving().message(notStroungEnough(blocks.size(), treeSize));
			return false;
			
		}
		
		// Chop down:
		for (Block block : blocks) {
			
			// Send event:
			BlockBreakEvent eventB = new BlockBreakEvent(block, player);
			Saga.plugin().getServer().getPluginManager().callEvent(eventB);
			if(eventB.isCancelled()) return triggered;
			
			block.breakNaturally(itemHand);
			getSagaLiving().damageTool();
			
			triggered = true;
			
		}
		
		// Play effect:
		player.playEffect(clickedBlock.getLocation(), Effect.STEP_SOUND, Material.LOG.getId());
		
		if(getSagaLiving() instanceof SagaPlayer) StatsEffectHandler.playAnimateArm((SagaPlayer) getSagaLiving());
		
		return true;

		
	}
	
	/**
	 * Adds a tree to the given lists.
	 * 
	 * @param anchor anchor block
	 * @param logs logs
	 * @param leaves leaves
	 */
	private static void getTree(Block anchor, ArrayList<Block> logs, ArrayList<Block> leaves){
		

		// Limits:
		if(logs.size() > LOGS_LIMIT) return;
		
		Block nextAnchor = null;
		
		// North:
		nextAnchor = anchor.getRelative(BlockFace.NORTH);
		if(nextAnchor.getType().equals(Material.LOG) && !logs.contains(nextAnchor)){
			logs.add(nextAnchor);
			getTree(nextAnchor, logs, leaves);
		}
		else if(nextAnchor.getType().equals(Material.LEAVES) && !logs.contains(nextAnchor)){
			leaves.add(nextAnchor);
		}
		
		// North-east:
		nextAnchor = anchor.getRelative(BlockFace.NORTH_EAST);
		if(nextAnchor.getType().equals(Material.LOG) && !logs.contains(nextAnchor)){
			logs.add(nextAnchor);
			getTree(nextAnchor, logs, leaves);
		}
		else if(nextAnchor.getType().equals(Material.LEAVES) && !logs.contains(nextAnchor)){
			leaves.add(nextAnchor);
		}
		
		// East:
		nextAnchor = anchor.getRelative(BlockFace.EAST);
		if(nextAnchor.getType().equals(Material.LOG) && !logs.contains(nextAnchor)){
			logs.add(nextAnchor);
			getTree(nextAnchor, logs, leaves);
		}
		else if(nextAnchor.getType().equals(Material.LEAVES) && !logs.contains(nextAnchor)){
			leaves.add(nextAnchor);
		}

		// South-east:
		nextAnchor = anchor.getRelative(BlockFace.SOUTH_EAST);
		if(nextAnchor.getType().equals(Material.LOG) && !logs.contains(nextAnchor)){
			logs.add(nextAnchor);
			getTree(nextAnchor, logs, leaves);
		}
		else if(nextAnchor.getType().equals(Material.LEAVES) && !logs.contains(nextAnchor)){
			leaves.add(nextAnchor);
		}

		// South:
		nextAnchor = anchor.getRelative(BlockFace.SOUTH);
		if(nextAnchor.getType().equals(Material.LOG) && !logs.contains(nextAnchor)){
			logs.add(nextAnchor);
			getTree(nextAnchor, logs, leaves);
		}
		else if(nextAnchor.getType().equals(Material.LEAVES) && !logs.contains(nextAnchor)){
			leaves.add(nextAnchor);
		}

		// South-west:
		nextAnchor = anchor.getRelative(BlockFace.SOUTH_WEST);
		if(nextAnchor.getType().equals(Material.LOG) && !logs.contains(nextAnchor)){
			logs.add(nextAnchor);
			getTree(nextAnchor, logs, leaves);
		}
		else if(nextAnchor.getType().equals(Material.LEAVES) && !logs.contains(nextAnchor)){
			leaves.add(nextAnchor);
		}

		// West:
		nextAnchor = anchor.getRelative(BlockFace.WEST);
		if(nextAnchor.getType().equals(Material.LOG) && !logs.contains(nextAnchor)){
			logs.add(nextAnchor);
			getTree(nextAnchor, logs, leaves);
		}
		else if(nextAnchor.getType().equals(Material.LEAVES) && !logs.contains(nextAnchor)){
			leaves.add(nextAnchor);
		}

		// North-west:
		nextAnchor = anchor.getRelative(BlockFace.NORTH_WEST);
		if(nextAnchor.getType().equals(Material.LOG) && !logs.contains(nextAnchor)){
			logs.add(nextAnchor);
			getTree(nextAnchor, logs, leaves);
		}
		else if(nextAnchor.getType().equals(Material.LEAVES) && !logs.contains(nextAnchor)){
			leaves.add(nextAnchor);
		}

		// Shift anchor one up:
		anchor = anchor.getRelative(BlockFace.UP);

		// Up-north:
		nextAnchor = anchor.getRelative(BlockFace.NORTH);
		if(nextAnchor.getType().equals(Material.LOG) && !logs.contains(nextAnchor)){
			logs.add(nextAnchor);
			getTree(nextAnchor, logs, leaves);
		}
		else if(nextAnchor.getType().equals(Material.LEAVES) && !logs.contains(nextAnchor)){
			leaves.add(nextAnchor);
		}
		
		// Up-north-east:
		nextAnchor = anchor.getRelative(BlockFace.NORTH_EAST);
		if(nextAnchor.getType().equals(Material.LOG) && !logs.contains(nextAnchor)){
			logs.add(nextAnchor);
			getTree(nextAnchor, logs, leaves);
		}
		else if(nextAnchor.getType().equals(Material.LEAVES) && !logs.contains(nextAnchor)){
			leaves.add(nextAnchor);
		}
		
		// Up-east:
		nextAnchor = anchor.getRelative(BlockFace.EAST);
		if(nextAnchor.getType().equals(Material.LOG) && !logs.contains(nextAnchor)){
			logs.add(nextAnchor);
			getTree(nextAnchor, logs, leaves);
		}
		else if(nextAnchor.getType().equals(Material.LEAVES) && !logs.contains(nextAnchor)){
			leaves.add(nextAnchor);
		}

		// Up-south-east:
		nextAnchor = anchor.getRelative(BlockFace.SOUTH_EAST);
		if(nextAnchor.getType().equals(Material.LOG) && !logs.contains(nextAnchor)){
			logs.add(nextAnchor);
			getTree(nextAnchor, logs, leaves);
		}
		else if(nextAnchor.getType().equals(Material.LEAVES) && !logs.contains(nextAnchor)){
			leaves.add(nextAnchor);
		}

		// Up-south:
		nextAnchor = anchor.getRelative(BlockFace.SOUTH);
		if(nextAnchor.getType().equals(Material.LOG) && !logs.contains(nextAnchor)){
			logs.add(nextAnchor);
			getTree(nextAnchor, logs, leaves);
		}
		else if(nextAnchor.getType().equals(Material.LEAVES) && !logs.contains(nextAnchor)){
			leaves.add(nextAnchor);
		}

		// Up-south-west:
		nextAnchor = anchor.getRelative(BlockFace.SOUTH_WEST);
		if(nextAnchor.getType().equals(Material.LOG) && !logs.contains(nextAnchor)){
			logs.add(nextAnchor);
			getTree(nextAnchor, logs, leaves);
		}
		else if(nextAnchor.getType().equals(Material.LEAVES) && !logs.contains(nextAnchor)){
			leaves.add(nextAnchor);
		}

		// Up-west:
		nextAnchor = anchor.getRelative(BlockFace.WEST);
		if(nextAnchor.getType().equals(Material.LOG) && !logs.contains(nextAnchor)){
			logs.add(nextAnchor);
			getTree(nextAnchor, logs, leaves);
		}
		else if(nextAnchor.getType().equals(Material.LEAVES) && !logs.contains(nextAnchor)){
			leaves.add(nextAnchor);
		}

		// Up-north-west:
		nextAnchor = anchor.getRelative(BlockFace.NORTH_WEST);
		if(nextAnchor.getType().equals(Material.LOG) && !logs.contains(nextAnchor)){
			logs.add(nextAnchor);
			getTree(nextAnchor, logs, leaves);
		}
		else if(nextAnchor.getType().equals(Material.LEAVES) && !logs.contains(nextAnchor)){
			leaves.add(nextAnchor);
		}
		
		// Up:
		nextAnchor = anchor.getRelative(BlockFace.SELF);
		if(nextAnchor.getType().equals(Material.LOG) && !logs.contains(nextAnchor)){
			logs.add(nextAnchor);
			getTree(nextAnchor, logs, leaves);
		}
		else if(nextAnchor.getType().equals(Material.LEAVES) && !logs.contains(nextAnchor)){
			leaves.add(nextAnchor);
		}
		
		
	}
	
	
	// Messages:
	public String notTree() {
		return PlayerMessages.negative + TextUtil.capitalize(getName()) + " ability can only be used on trees.";
	}
	
	public String notStroungEnough(int blocks, int maximum) {
		return PlayerMessages.negative + "Not strong enough to " + getName() + " a tree larger than " + maximum + " blocks.";
	}
	
	
}
