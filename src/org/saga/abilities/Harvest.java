package org.saga.abilities;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.Saga;
import org.saga.shape.BlockFilter;
import org.saga.shape.RelativeShape;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.TwoPointFunction;


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

		// Drops:
		boolean reedDrop = false;
		boolean wheatDrop = false;
		
		
		// Target:
		Location location = event.getPlayer().getLocation();
		
		// Skill functions:
		Integer size = getDefinition().getPrimaryFunction().calculateRandomIntValue(getSkillLevel());
		Integer convenience = getDefinition().getSecondaryFunction().calculateRandomIntValue(getSkillLevel());
		
		// Exp reward:
		int expval = 0;
		
		// Get shape:
		RelativeShape shape = SPAHPE;
		ArrayList<Block> blocks = shape.getBlocks(location, getSagaPlayer().getOrientation(), size);
		
		// No blocks:
		if(blocks.size() == 0){
			return false;
		}
		
		// Check permissions:
		for (Block block : blocks) {

			BlockBreakEvent bbEvent = new BlockBreakEvent(block, event.getPlayer());
			Saga.plugin().getServer().getPluginManager().callEvent(bbEvent);
			
			if(bbEvent.isCancelled()){
				return false;
			}
			
		}
		
		// Break and drop:
		for (Block block : blocks) {

			
			// Convenience:
			if(RANDOM.nextDouble() <= convenience){
				
				if(block.getType().equals(Material.CROPS) && block.getData() != 7) continue;
				
				location = event.getPlayer().getLocation();
				
			}else{
				
				location = block.getLocation();
				
			}
			
			// Drop indication:
			if(block.getType() == Material.SUGAR_CANE_BLOCK){
				reedDrop = true;
			}
			else if(block.getType() == Material.CROPS){
				wheatDrop = true;
			}
			
			// Break:
			handleBreak(block, location);
			
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
		Integer awardedExp = awardExperience(expval);
		
		// Statistics:
		StatisticsManager.manager().onAbilityUse(getName(), awardedExp);
		
		return true;
		
		
	}
	
	
	// Blocks:
	/**
	 * Handles block break.
	 * 
	 * @param block block
	 * @param event event
	 */
	public void handleBreak(Block block, Location dropLocation) {

		
		Byte data = block.getData();
		
		if(block.getType() == Material.AIR) return;
		
		// Crops:
		if(block.getType() == Material.CROPS){
			
			block.setType(Material.AIR);
			
			// Not grown:
			if(data == 0){
				
				handleDrop(new ItemStack(Material.SEEDS, 1), dropLocation);
				
			}
			
			// Grown:
			else{
				
				TwoPointFunction tpf = new TwoPointFunction((short)1, 0.0, (short)7, 3.0);
				
				Integer seeds = tpf.calculateRandomIntValue(data.intValue());
				
				handleDrop(new ItemStack(Material.SEEDS, seeds), dropLocation);

				// Wheat
				if(data == 7){
					
					if(RANDOM.nextBoolean()){
						handleDrop(new ItemStack(Material.WHEAT, 1), dropLocation);
					}else{
						handleDrop(new ItemStack(Material.WHEAT, 2), dropLocation);
					}
					
				}
				
				
			}
			
			block.setType(Material.AIR);
			
		}
		
		// Sugar cane:
		else if(block.getType() == Material.SUGAR_CANE_BLOCK){
			
			handleDrop(new ItemStack(Material.SUGAR_CANE, 1), dropLocation);

			block.setType(Material.AIR);
			
		}
		
		
	}
	
	/**
	 * Handles drop.
	 * 
	 * @param item item
	 * @param location location
	 */
	public void handleDrop(ItemStack item, Location location) {

		if(item.getType() == Material.AIR || item.getAmount() == 0) return;
		
		location.getWorld().dropItemNaturally(location, item);
		
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
