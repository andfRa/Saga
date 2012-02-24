package org.saga.abilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.Saga;
import org.saga.shape.BlockFilter;
import org.saga.shape.RelativeShape;
import org.saga.statistics.StatisticsManager;


public class Harvest extends Ability{

	
	/**
	 * Random.
	 */
	private static Random RANDOM = new Random();

	/**
	 * Shape for the ability.
	 */
	private static RelativeShape SPAHPE = createShape();

	
	// Initialization:
	/**
	 * Initializes using definition.
	 * 
	 * @param definition ability definition
	 */
	public Harvest(AbilityDefinition definition) {
		
        super(definition);
	
	}

	
	// Ability usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#instant(org.bukkit.event.player.PlayerInteractEvent)
	 */
	@Override
	public boolean instant(PlayerInteractEvent event) {

		
		// Check preuse:
		if(!handlePreUse()){
			return false;
		}

		Player player = event.getPlayer();
		
		// Drops:
		boolean reedDrop = false;
		boolean wheatDrop = false;
		
		
		// Target:
		Location dropLocation = event.getPlayer().getLocation();
		
		// Skill functions:
		Integer size = getDefinition().getPrimaryFunction().randomIntValue(getSkillLevel());
		Integer convenience = getDefinition().getSecondaryFunction().randomIntValue(getSkillLevel());
		
		// Exp reward:
		int expval = 0;
		
		// Get shape:
		RelativeShape shape = SPAHPE;
		ArrayList<Block> blocks = shape.getBlocks(dropLocation, getSagaPlayer().getOrientation(), size);
		
		// No blocks:
		if(blocks.size() == 0){
			return false;
		}
		
		// Break and drop:
		for (Block block : blocks) {

			// Convenience:
			if(RANDOM.nextDouble() <= convenience){
				
				if(block.getType().equals(Material.CROPS) && block.getData() != 7) continue;
				dropLocation = event.getPlayer().getLocation();
				
			}else{
				dropLocation = block.getLocation();
			}
			
			// Drop indication:
			if(block.getType() == Material.SUGAR_CANE_BLOCK){
				reedDrop = true;
			}
			else if(block.getType() == Material.CROPS){
				wheatDrop = true;
			}
			
			// Event:
			if(!handleEvent(block, player)) return false;
			
			// Break:
			Collection<ItemStack> drops = handleBreak(block, player);

			for (ItemStack drop : drops) {
				
				handleDrop(drop, dropLocation);
				
			}
			
			expval += 1;
			
		}
		
		
		// Effect:
		if(reedDrop){
			getSagaPlayer().playGlobalEffect(Effect.STEP_SOUND, Material.SUGAR_CANE_BLOCK.getId());
		}
		
		if(wheatDrop){
			getSagaPlayer().playGlobalEffect(Effect.STEP_SOUND, Material.CROPS.getId());
		}
		
		// Award exp:
		Double awardedExp = awardExperience(expval);
		
		// Statistics:
		StatisticsManager.manager().onAbilityUse(getName(), awardedExp);
		
		return true;
		
		
	}

	
	// Blocks:
	/**
	 * Handles block break event.
	 * 
	 * @param block block
	 * @param player player
	 * @return true if canceled
	 */
	private boolean handleEvent(Block block, Player player) {


		// Call event:
		BlockBreakEvent bbEvent = new BlockBreakEvent(block, player);
		Saga.plugin().getServer().getPluginManager().callEvent(bbEvent);
		if(bbEvent.isCancelled()) return false;

		return true;
		
		
	}
	
	/**
	 * Handles block break.
	 * 
	 * @param block block
	 * @param player player
	 */
	private Collection<ItemStack> handleBreak(Block block, Player player) {

		
		// Air:
		if(block.getType() == Material.AIR) return new ArrayList<ItemStack>();

		// Call event:
		BlockBreakEvent bbEvent = new BlockBreakEvent(block, player);
		Saga.plugin().getServer().getPluginManager().callEvent(bbEvent);
		if(bbEvent.isCancelled()) return new ArrayList<ItemStack>();
		
		Collection<ItemStack> drops = block.getDrops();

		// Break:
		block.setType(Material.AIR);
		
		// Damage tool:
		getSagaPlayer().damageTool();
		
		return drops;
		
		
	}
	
	/**
	 * Handles drop.
	 * 
	 * @param drop item
	 * @param location location
	 */
	private void handleDrop(ItemStack drop, Location location) {

		if(drop.getType() == Material.AIR) return;
		
		location.getWorld().dropItemNaturally(location, drop);
		
	}
	
	
	
	/**
	 * Creates the shape for the ability.
	 * 
	 * @return shape
	 */
	public static RelativeShape createShape(){
		
		
		RelativeShape shape = new RelativeShape(createFilter());

		// Top second layer:
		shape.addOffset(0, 2, 0, 0);
		
		shape.addOffset(0, 2, 1, 1);
		shape.addOffset(0, 2, -1, 2);
		
		shape.addOffset(-1, 2, 0, 3);
		
		shape.addOffset(-1, 2, 1, 4);
		shape.addOffset(-1, 2, -1, 5);
		
		shape.addOffset(0, 2, 2, 6);
		shape.addOffset(0, 2, -2, 7);
		
		shape.addOffset(-2, 2, 0, 8);
		
		shape.addOffset(-2, 2, 1, 9);
		shape.addOffset(-2, 2, -1, 10);
		
		shape.addOffset(-1, 2, 2, 11);
		shape.addOffset(-1, 2, -2, 12);
		
		shape.addOffset(0, 2, 2, 13);
		shape.addOffset(0, 2, -2, 14);
		
		shape.addOffset(-3, 2, 0, 15);
		
		shape.addOffset(-3, 2, 1, 16);
		shape.addOffset(-3, 2, -1, 17);
		
		shape.addOffset(-2, 2, 2, 18);
		shape.addOffset(-2, 2, -2, 19);

		shape.addOffset(-1, 2, 3, 20);
		shape.addOffset(-1, 2, -3, 21);
		
		shape.addOffset(0, 2, 4, 22);
		shape.addOffset(0, 2, -4, 23);
		
		shape.addOffset(-4, 2, 0, 24);
		
		shape.addOffset(-4, 2, 1, 25);
		shape.addOffset(-4, 2, -1, 26);
		
		shape.addOffset(-3, 2, 2, 27);
		shape.addOffset(-3, 2, -2, 28);
		
		shape.addOffset(-2, 2, 3, 29);
		shape.addOffset(-2, 2, -3, 30);

		shape.addOffset(-1, 2, 4, 31);
		shape.addOffset(-1, 2, -4, 32);
		
		shape.addOffset(-5, 2, 0, 33);
		
		shape.addOffset(-5, 2, 1, 34);
		shape.addOffset(-5, 2, -1, 35);
		
		shape.addOffset(-4, 2, 2, 36);
		shape.addOffset(-4, 2, -2, 37);
		
		shape.addOffset(-3, 2, 3, 38);
		shape.addOffset(-3, 2, -3, 39);
		
		shape.addOffset(-2, 2, 4, 40);
		shape.addOffset(-2, 2, -4, 41);

		// Top layer:
		shape.addOffset(0, 1, 0, 0);
		
		shape.addOffset(0, 1, 1, 1);
		shape.addOffset(0, 1, -1, 2);
		
		shape.addOffset(-1, 1, 0, 3);
		
		shape.addOffset(-1, 1, 1, 4);
		shape.addOffset(-1, 1, -1, 5);
		
		shape.addOffset(0, 1, 2, 6);
		shape.addOffset(0, 1, -2, 7);
		
		shape.addOffset(-2, 1, 0, 8);
		
		shape.addOffset(-2, 1, 1, 9);
		shape.addOffset(-2, 1, -1, 10);
		
		shape.addOffset(-1, 1, 2, 11);
		shape.addOffset(-1, 1, -2, 12);
		
		shape.addOffset(0, 1, 2, 13);
		shape.addOffset(0, 1, -2, 14);
		
		shape.addOffset(-3, 1, 0, 15);
		
		shape.addOffset(-3, 1, 1, 16);
		shape.addOffset(-3, 1, -1, 17);
		
		shape.addOffset(-2, 1, 2, 18);
		shape.addOffset(-2, 1, -2, 19);

		shape.addOffset(-1, 1, 3, 20);
		shape.addOffset(-1, 1, -3, 21);
		
		shape.addOffset(0, 1, 4, 22);
		shape.addOffset(0, 1, -4, 23);
		
		shape.addOffset(-4, 1, 0, 24);
		
		shape.addOffset(-4, 1, 1, 25);
		shape.addOffset(-4, 1, -1, 26);
		
		shape.addOffset(-3, 1, 2, 27);
		shape.addOffset(-3, 1, -2, 28);
		
		shape.addOffset(-2, 1, 3, 29);
		shape.addOffset(-2, 1, -3, 30);

		shape.addOffset(-1, 1, 4, 31);
		shape.addOffset(-1, 1, -4, 32);
		
		shape.addOffset(-5, 1, 0, 33);
		
		shape.addOffset(-5, 1, 1, 34);
		shape.addOffset(-5, 1, -1, 35);
		
		shape.addOffset(-4, 1, 2, 36);
		shape.addOffset(-4, 1, -2, 37);
		
		shape.addOffset(-3, 1, 3, 38);
		shape.addOffset(-3, 1, -3, 39);
		
		shape.addOffset(-2, 1, 4, 40);
		shape.addOffset(-2, 1, -4, 41);
		
		// Middle layer:
		shape.addOffset(0, 0, 0, 0);
		
		shape.addOffset(0, 0, 1, 1);
		shape.addOffset(0, 0, -1, 2);
		
		shape.addOffset(-1, 0, 0, 3);
		
		shape.addOffset(-1, 0, 1, 4);
		shape.addOffset(-1, 0, -1, 5);
		
		shape.addOffset(0, 0, 2, 6);
		shape.addOffset(0, 0, -2, 7);
		
		shape.addOffset(-2, 0, 0, 8);
		
		shape.addOffset(-2, 0, 1, 9);
		shape.addOffset(-2, 0, -1, 10);
		
		shape.addOffset(-1, 0, 2, 11);
		shape.addOffset(-1, 0, -2, 12);
		
		shape.addOffset(0, 0, 2, 13);
		shape.addOffset(0, 0, -2, 14);
		
		shape.addOffset(-3, 0, 0, 15);
		
		shape.addOffset(-3, 0, 1, 16);
		shape.addOffset(-3, 0, -1, 17);
		
		shape.addOffset(-2, 0, 2, 18);
		shape.addOffset(-2, 0, -2, 19);

		shape.addOffset(-1, 0, 3, 20);
		shape.addOffset(-1, 0, -3, 21);
		
		shape.addOffset(0, 0, 4, 22);
		shape.addOffset(0, 0, -4, 23);
		
		shape.addOffset(-4, 0, 0, 24);
		
		shape.addOffset(-4, 0, 1, 25);
		shape.addOffset(-4, 0, -1, 26);
		
		shape.addOffset(-3, 0, 2, 27);
		shape.addOffset(-3, 0, -2, 28);
		
		shape.addOffset(-2, 0, 3, 29);
		shape.addOffset(-2, 0, -3, 30);

		shape.addOffset(-1, 0, 4, 31);
		shape.addOffset(-1, 0, -4, 32);
		
		shape.addOffset(-5, 0, 0, 33);
		
		shape.addOffset(-5, 0, 1, 34);
		shape.addOffset(-5, 0, -1, 35);
		
		shape.addOffset(-4, 0, 2, 36);
		shape.addOffset(-4, 0, -2, 37);
		
		shape.addOffset(-3, 0, 3, 38);
		shape.addOffset(-3, 0, -3, 39);
		
		shape.addOffset(-2, 0, 4, 40);
		shape.addOffset(-2, 0, -4, 41);

		// Bottom layer:
		shape.addOffset(0, -1, 0, 0);
		
		shape.addOffset(0, -1, 1, 1);
		shape.addOffset(0, -1, -1, 2);
		
		shape.addOffset(-1, -1, 0, 3);
		
		shape.addOffset(-1, -1, 1, 4);
		shape.addOffset(-1, -1, -1, 5);
		
		shape.addOffset(0, -1, 2, 6);
		shape.addOffset(0, -1, -2, 7);
		
		shape.addOffset(-2, -1, 0, 8);
		
		shape.addOffset(-2, -1, 1, 9);
		shape.addOffset(-2, -1, -1, 10);
		
		shape.addOffset(-1, -1, 2, 11);
		shape.addOffset(-1, -1, -2, 12);
		
		shape.addOffset(0, -1, 2, 13);
		shape.addOffset(0, -1, -2, 14);
		
		shape.addOffset(-3, -1, 0, 15);
		
		shape.addOffset(-3, -1, 1, 16);
		shape.addOffset(-3, -1, -1, 17);
		
		shape.addOffset(-2, -1, 2, 18);
		shape.addOffset(-2, -1, -2, 19);

		shape.addOffset(-1, -1, 3, 20);
		shape.addOffset(-1, -1, -3, 21);
		
		shape.addOffset(0, -1, 4, 22);
		shape.addOffset(0, -1, -4, 23);
		
		shape.addOffset(-4, -1, 0, 24);
		
		shape.addOffset(-4, -1, 1, 25);
		shape.addOffset(-4, -1, -1, 26);
		
		shape.addOffset(-3, -1, 2, 27);
		shape.addOffset(-3, -1, -2, 28);
		
		shape.addOffset(-2, -1, 3, 29);
		shape.addOffset(-2, -1, -3, 30);

		shape.addOffset(-1, -1, 4, 31);
		shape.addOffset(-1, -1, -4, 32);
		
		shape.addOffset(-5, -1, 0, 33);
		
		shape.addOffset(-5, -1, 1, 34);
		shape.addOffset(-5, -1, -1, 35);
		
		shape.addOffset(-4, -1, 2, 36);
		shape.addOffset(-4, -1, -2, 37);
		
		shape.addOffset(-3, -1, 3, 38);
		shape.addOffset(-3, -1, -3, 39);
		
		shape.addOffset(-2, -1, 4, 40);
		shape.addOffset(-2, -1, -4, 41);

//
//		// Bottom layer:
//		shape.addOffset(0, -a, 0, 0);
//		
//		shape.addOffset(0, -a, 1, 1);
//		shape.addOffset(0, -a, -1, 2);
//		
//		shape.addOffset(-1, -a, 0, 3);
//		
//		shape.addOffset(-1, -a, 1, 4);
//		shape.addOffset(-1, -a, -1, 5);
//		
//		shape.addOffset(0, -a, 2, 6);
//		shape.addOffset(0, -a, -2, 7);
//		
//		shape.addOffset(-2, -a, 0, 8);
//		
//		shape.addOffset(-2, -a, 1, 9);
//		shape.addOffset(-2, -a, -1, 10);
//		
//		shape.addOffset(-1, -a, 2, 11);
//		shape.addOffset(-1, -a, -2, 12);
//		
//		shape.addOffset(0, -a, 2, 13);
//		shape.addOffset(0, -a, -2, 14);
//		
//		shape.addOffset(-3, -a, 0, 15);
//		
//		shape.addOffset(-3, -a, 1, 16);
//		shape.addOffset(-3, -a, -1, 17);
//		
//		shape.addOffset(-2, -a, 2, 18);
//		shape.addOffset(-2, -a, -2, 19);
//
//		shape.addOffset(-1, -a, 3, 20);
//		shape.addOffset(-1, -a, -3, 21);
//		
//		shape.addOffset(0, -a, 4, 22);
//		shape.addOffset(0, -a, -4, 23);
//		
//		shape.addOffset(-4, -a, 0, 24);
//		
//		shape.addOffset(-4, -a, 1, 25);
//		shape.addOffset(-4, -a, -1, 26);
//		
//		shape.addOffset(-3, -a, 2, 27);
//		shape.addOffset(-3, -a, -2, 28);
//		
//		shape.addOffset(-2, -a, 3, 29);
//		shape.addOffset(-2, -a, -3, 30);
//
//		shape.addOffset(-1, -a, 4, 31);
//		shape.addOffset(-1, -a, -4, 32);
//		
//		shape.addOffset(-5, -a, 0, 33);
//		
//		shape.addOffset(-5, -a, 1, 34);
//		shape.addOffset(-5, -a, -1, 35);
//		
//		shape.addOffset(-4, -a, 2, 36);
//		shape.addOffset(-4, -a, -2, 37);
//		
//		shape.addOffset(-3, -a, 3, 38);
//		shape.addOffset(-3, -a, -3, 39);
//		
//		shape.addOffset(-2, -a, 4, 40);
//		shape.addOffset(-2, -a, -4, 41);
//		
		
		return shape;
		
		
	}

	/**
	 * Creates the filter for the ability.
	 * 
	 * @return filter
	 */
	public static BlockFilter createFilter(){
		
		
		BlockFilter filter = new BlockFilter();
		
		filter.addMaterial(Material.CROPS);
		filter.addMaterial(Material.SUGAR_CANE_BLOCK);
		
		return filter;
		
		
	}

	
	
	
}
