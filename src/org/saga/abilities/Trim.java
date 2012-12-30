package org.saga.abilities;

import java.util.ArrayList;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.Saga;
import org.saga.messages.AbilityMessages;
import org.saga.messages.effects.StatsEffectHandler;
import org.saga.player.SagaPlayer;
import org.saga.utility.TwoPointFunction;

public class Trim extends Ability{

	
	/**
	 * Greens size key.
	 */
	private static String GREENS_SIZE_KEY = "greens size";

	/**
	 * Tool damage multiplier key.
	 */
	private static String TOOL_DAMAGE_MULTIPLIER_KEY = "tool damage";
	
	
	/**
	 * Maximum amount greens.
	 */
	private static Integer GREENS_LIMIT = 500;
	

	
	// Initialisation:
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Trim(AbilityDefinition definition) {
		
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

		Block clickedBlock = event.getClickedBlock();
		if(clickedBlock == null || !isGreen(clickedBlock)) return false;
		
		return handlePreTrigger();
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#trigger(org.bukkit.event.player.PlayerInteractEvent)
	 */
	@Override
	public boolean triggerInteract(PlayerInteractEvent event) {

		
		Block clickedBlock = event.getClickedBlock();
		ItemStack tool = event.getItem();
		Player player = event.getPlayer();
		
		// Tool damage:
		double toolDam = getDefinition().getFunction(TOOL_DAMAGE_MULTIPLIER_KEY).value(getScore());
		double acumToolDam = 0;
		
		// Drops:
		boolean triggered = false;
		
		// Get greens:
		ArrayList<Block> blocks = new ArrayList<Block>();
		blocks.add(clickedBlock);
		getGreens(clickedBlock, blocks);
		
		// Too much greens:
		Integer treeSize = getDefinition().getFunction(GREENS_SIZE_KEY).value(getScore()).intValue();
		if(blocks.size() > treeSize){
			getSagaLiving().message(AbilityMessages.trimNotStroungEnough(this, treeSize));
			return false;
		}
		
		// Trim:
		for (Block block : blocks) {
			
			// Send event:
			BlockBreakEvent bevent = new BlockBreakEvent(block, player);
			Saga.plugin().getServer().getPluginManager().callEvent(bevent);
			if(bevent.isCancelled()) return triggered;
			
			// Always drop something:
			if(block.getDrops(tool).size() == 0){
				Location loc = block.getLocation();
				block.getLocation().getWorld().dropItemNaturally(loc, new ItemStack(block.getType(), 1, block.getData()));
			}
			
			// Break:
			block.breakNaturally(tool);
			
			// Tool damage:
			acumToolDam+= toolDam;
			while (acumToolDam >= 1){
				getSagaLiving().damageTool();
				acumToolDam--;
			}
			
			triggered = true;
			
		}
		
		// Damage remaining:
		if(TwoPointFunction.randomRound(acumToolDam) > 0){
			getSagaLiving().damageTool();
		}
		
		// Play effect:
		player.playEffect(clickedBlock.getLocation(), Effect.STEP_SOUND, Material.LEAVES.getId());
		
		if(getSagaLiving() instanceof SagaPlayer) StatsEffectHandler.playAnimateArm((SagaPlayer) getSagaLiving());
		
		return true;

		
	}
	
	/**
	 * Adds all greens to the given list.
	 * 
	 * @param anchor anchor block
	 * @param greens leaves and grass
	 */
	private static void getGreens(Block anchor, ArrayList<Block> greens){
		

		// Limits:
		if(greens.size() > GREENS_LIMIT) return;
		
		Block nextAnchor = null;
		
		// North:
		nextAnchor = anchor.getRelative(BlockFace.NORTH);
		if(nextAnchor.getType().equals(Material.LEAVES) && !greens.contains(nextAnchor)){
			greens.add(nextAnchor);
			getGreens(nextAnchor, greens);
		}
		
		// North-east:
		nextAnchor = anchor.getRelative(BlockFace.NORTH_EAST);
		if(isGreen(nextAnchor) && !greens.contains(nextAnchor)){
			greens.add(nextAnchor);
			getGreens(nextAnchor, greens);
		}
		
		// East:
		nextAnchor = anchor.getRelative(BlockFace.EAST);
		if(isGreen(nextAnchor) && !greens.contains(nextAnchor)){
			greens.add(nextAnchor);
			getGreens(nextAnchor, greens);
		}

		// South-east:
		nextAnchor = anchor.getRelative(BlockFace.SOUTH_EAST);
		if(isGreen(nextAnchor) && !greens.contains(nextAnchor)){
			greens.add(nextAnchor);
			getGreens(nextAnchor, greens);
		}

		// South:
		nextAnchor = anchor.getRelative(BlockFace.SOUTH);
		if(isGreen(nextAnchor) && !greens.contains(nextAnchor)){
			greens.add(nextAnchor);
			getGreens(nextAnchor, greens);
		}

		// South-west:
		nextAnchor = anchor.getRelative(BlockFace.SOUTH_WEST);
		if(isGreen(nextAnchor) && !greens.contains(nextAnchor)){
			greens.add(nextAnchor);
			getGreens(nextAnchor, greens);
		}

		// West:
		nextAnchor = anchor.getRelative(BlockFace.WEST);
		if(isGreen(nextAnchor) && !greens.contains(nextAnchor)){
			greens.add(nextAnchor);
			getGreens(nextAnchor, greens);
		}

		// North-west:
		nextAnchor = anchor.getRelative(BlockFace.NORTH_WEST);
		if(isGreen(nextAnchor) && !greens.contains(nextAnchor)){
			greens.add(nextAnchor);
			getGreens(nextAnchor, greens);
		}
		
		// Up:
		nextAnchor = anchor.getRelative(BlockFace.UP);
		if(isGreen(nextAnchor) && !greens.contains(nextAnchor)){
			greens.add(nextAnchor);
			getGreens(nextAnchor, greens);
		}
		
		// Down:
		nextAnchor = anchor.getRelative(BlockFace.DOWN);
		if(isGreen(nextAnchor) && !greens.contains(nextAnchor)){
			greens.add(nextAnchor);
			getGreens(nextAnchor, greens);
		}

		
	}
	
	/**
	 * Checks if the ability can be applied to the given block.
	 * 
	 * @param block block
	 * @return true if can be applied
	 */
	private static boolean isGreen(Block block) {
		
		if(block.getType() == Material.LEAVES) return true;
		if(block.getType() == Material.LONG_GRASS) return true;
		if(block.getType() == Material.RED_ROSE) return true;
		if(block.getType() == Material.YELLOW_FLOWER) return true;
		
		return false;
		
	}
	
	
}
