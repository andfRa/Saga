package org.saga.abilities;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.Saga;
import org.saga.economy.EconomyMessages;
import org.saga.player.PlayerMessages;
import org.saga.shape.BlockFilter;
import org.saga.shape.RelativeShape;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.TextUtil;


public class HeavySwing extends Ability{

	
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
	public HeavySwing(AbilityDefinition definition) {
		
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

		// Check blocks:
		if(event.getClickedBlock() == null) return false;
		
		// Target stone:
		Location targetLocation = event.getPlayer().getLocation();
		
		// Skill functions:
		Integer size = getDefinition().getPrimaryFunction().calculateRandomIntValue(getSkillLevel());
		Integer crumble = getDefinition().getSecondaryFunction().calculateRandomIntValue(getSkillLevel());
		
		// Cobble stacking:
		int cobbleStack = 5;
		int cobbleCount = 0;
		Location dropLocation = targetLocation;
		
		// Exp reward:
		int expval = 0;
		
		// Get shape:
		RelativeShape shape = SPAHPE;
		ArrayList<Block> blocks = shape.getBlocks(targetLocation, getSagaPlayer().getOrientation(), size);
		
		// Check if target block is included:
		if(!blocks.contains(event.getClickedBlock())){
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

			
			ItemStack item = handleBreak(block);
			dropLocation = block.getLocation();
				
			// Drop normal items:
			if(item.getType() != Material.COBBLESTONE){
				
				handleDrop(item, dropLocation);
				
			}
			
			// Stack up cobble:
			else{
				
				cobbleCount ++;
				
				if(cobbleCount >= cobbleStack){
					
					handleDrop(new ItemStack(Material.COBBLESTONE, cobbleCount), dropLocation);
					
					cobbleCount = 0;
					
				}
				
			}

			// Experience value:
			if(item.getType() == Material.COAL){
				expval += 1;
			}
			else if(item.getType() == Material.IRON_ORE){
				expval += 3;
			}
			else if(item.getType() == Material.GOLD_ORE){
				expval += 6;
			}
			else if(item.getType() == Material.DIAMOND){
				expval += 12;
			}
			
			
		}
		
		Random random = new Random();
		
		// Crumble blocks:
		ArrayList<Block> crumbleBlocks = new ArrayList<Block>();
		for (Block block : blocks) {
			
			if(block.getRelative(BlockFace.UP).getType() != Material.AIR){
				crumbleBlocks.add(block.getRelative(BlockFace.UP));
			}
			
		}

		// Check permissions:
		for (Block block : crumbleBlocks) {

			BlockBreakEvent bbEvent = new BlockBreakEvent(block, event.getPlayer());
			Saga.plugin().getServer().getPluginManager().callEvent(bbEvent);

			if(bbEvent.isCancelled()){
				crumble = 0;
				break;
			}
			
		}
		
		// Crumble:
		while(crumble > 0 && crumbleBlocks.size() > 0){
			
			Block crumbleBlock = crumbleBlocks.remove(random.nextInt(crumbleBlocks.size()));
			crumble--;
			
			if(canCrumble(crumbleBlock)){
				

				ItemStack item = handleBreak(crumbleBlock);
				dropLocation = crumbleBlock.getLocation();
					
				// Drop normal items:
				if(item.getType() != Material.COBBLESTONE){
					
					handleDrop(item, dropLocation);
					
				}
				
				// Stack up cobble:
				else{
					
					cobbleCount ++;
					
					if(cobbleCount >= cobbleStack){
						
						handleDrop(new ItemStack(Material.COBBLESTONE, cobbleCount), dropLocation);
						
						cobbleCount = 0;
						
					}
					
				}
				
			}
			
		}
		
		// Drop remaining cobble:
		if(cobbleCount > 0){
			handleDrop(new ItemStack(Material.COBBLESTONE, cobbleCount), dropLocation);
		}
		
		// Effect:
		getSagaPlayer().playGlobalEffect(Effect.STEP_SOUND, Material.STONE.getId());

		// Award exp:
		Double awardedExp = awardExperience(expval);
		
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
	public ItemStack handleBreak(Block block) {

		
		if(block.getType() == Material.AIR) return new ItemStack(Material.AIR);
		
		ItemStack item = new ItemStack(block.getType(), 1);
		
		// Stone:
		if(block.getType() == Material.STONE){
			
			item.setType(Material.COBBLESTONE);
			
		}
		
		// Coal:
		else if(block.getType() == Material.COAL_ORE){
			
			item.setType(Material.COAL);
			
		}
		
		// Diamond:
		else if(block.getType() == Material.DIAMOND_ORE){
			
			item.setType(Material.DIAMOND);
			
		}
		
		// Redstone:
		else if(block.getType() == Material.REDSTONE_ORE){
			
			item.setType(Material.REDSTONE);
			
			if(RANDOM.nextBoolean()){
				item.setAmount(4);
			}else{
				item.setAmount(5);
			}
			
			
		}

		// Break:
		block.setType(Material.AIR);
		
		return item;
		
		
	}
	
	/**
	 * Handles drop.
	 * 
	 * @param item item
	 * @param location location
	 */
	public void handleDrop(ItemStack item, Location location) {

		if(item.getType() == Material.AIR) return;
		
		location.getWorld().dropItemNaturally(location, item);
		
	}
	
	/**
	 * Checks if can crumble.
	 * 
	 * @param block block
	 * @return true if can crumble
	 */
	public boolean canCrumble(Block block) {

		
		if(block.getType() == Material.STONE) return true;
		
		if(block.getType() == Material.DIRT) return true;
		
		if(block.getType() == Material.GRAVEL) return true;

		if(block.getType() == Material.COAL_ORE) return true;

		if(block.getType() == Material.IRON_ORE) return true;

		if(block.getType() == Material.GOLD_ORE) return true;

		if(block.getType() == Material.DIAMOND_ORE) return true;

		if(block.getType() == Material.REDSTONE_ORE) return true;
		
		return false;
		
		
	}
	
	
	// Messages:
	public String stuck(Material material) {
		return PlayerMessages.negative + TextUtil.capitalize(getName()) + " got stuck in " + EconomyMessages.material(material) + ".";
	}
	
	public String mustHitStone() {
		return PlayerMessages.negative + TextUtil.capitalize(getName()) + " ability can only be used on stone and ores.";
	}
	
	
	/**
	 * Creates the shape for the ability.
	 * 
	 * @return shape
	 */
	public static RelativeShape createShape(){
		
		
//		RelativeShape shape = new RelativeShape(new MiningFilter());
		RelativeShape shape = new RelativeShape(createFilter());
		
		
		// Back layer:
		shape.addOffset(0, 1, 0, 1);
		
		shape.addOffset(0, 0, 0, 2);
		
		shape.addOffset(0, 1, 1, 5);
		shape.addOffset(0, 1, -1, 7);

		shape.addOffset(0, 0, 1, 9);
		shape.addOffset(0, 0, -1, 11);

		shape.addOffset(0, 0, 2, 13);
		shape.addOffset(0, 0, -2, 15);

		shape.addOffset(0, 1, 2, 17);
		shape.addOffset(0, 1, -2, 19);

		shape.addOffset(0, 2, 0, 21);

		shape.addOffset(0, 2, 1, 23);
		shape.addOffset(0, 2, -1, 25);

		shape.addOffset(0, 2, 2, 27);
		shape.addOffset(0, 2, -2, 29);

		shape.addOffset(0, 1, 3, 31);
		shape.addOffset(0, 1, -3, 33);

		shape.addOffset(0, 3, 0, 35);
		
		shape.addOffset(0, 3, 1, 37);
		shape.addOffset(0, 3, -1, 39);

		shape.addOffset(0, 3, 2, 41);
		shape.addOffset(0, 3, -2, 43);

		shape.addOffset(0, 2, 3, 45);
		shape.addOffset(0, 2, -3, 47);
		
		shape.addOffset(0, 4, 0, 49);
		
		shape.addOffset(0, 4, 1, 51);
		shape.addOffset(0, 4, -1, 53);

		// First layer:
		shape.addOffset(-1, 1, 0, 0);
		
		shape.addOffset(-1, 0, 0, 2);
		
		shape.addOffset(-1, 1, 1, 4);
		shape.addOffset(-1, 1, -1, 6);

		shape.addOffset(-1, 0, 1, 8);
		shape.addOffset(-1, 0, -1, 10);

		shape.addOffset(-1, 0, 2, 12);
		shape.addOffset(-1, 0, -2, 14);

		shape.addOffset(-1, 1, 2, 16);
		shape.addOffset(-1, 1, -2, 18);

		shape.addOffset(-1, 2, 0, 20);

		shape.addOffset(-1, 2, 1, 22);
		shape.addOffset(-1, 2, -1, 24);

		shape.addOffset(-1, 2, 2, 26);
		shape.addOffset(-1, 2, -2, 28);

		shape.addOffset(-1, 1, 3, 30);
		shape.addOffset(-1, 1, -3, 32);

		shape.addOffset(-1, 3, 0, 34);
		
		shape.addOffset(-1, 3, 1, 36);
		shape.addOffset(-1, 3, -1, 38);

		shape.addOffset(-1, 3, 2, 40);
		shape.addOffset(-1, 3, -2, 42);

		shape.addOffset(-1, 2, 3, 44);
		shape.addOffset(-1, 2, -3, 46);
		
		shape.addOffset(-1, 4, 0, 48);
		
		shape.addOffset(-1, 4, 1, 50);
		shape.addOffset(-1, 4, -1, 52);

		
		// Second layer:
		shape.addOffset(-2, 1, 0, 5);
		
		shape.addOffset(-2, 1, 1, 9);
		shape.addOffset(-2, 1, -1, 11);

		shape.addOffset(-2, 0, 0, 7);
		
		shape.addOffset(-2, 0, 1, 13);
		shape.addOffset(-2, 0, -1, 15);
		
		shape.addOffset(-2, 2, 0, 25);
		
		shape.addOffset(-2, 2, 1, 27);
		shape.addOffset(-2, 2, -1, 29);

		shape.addOffset(-2, 1, 2, 21);
		shape.addOffset(-2, 1, -2, 23);

		shape.addOffset(-2, 2, 2, 31);
		shape.addOffset(-2, 2, -2, 33);
	
		shape.addOffset(-2, 3, 0, 39);
		
		shape.addOffset(-2, 3, 1, 41);
		shape.addOffset(-2, 3, -1, 43);
		
		// Third layer:
		shape.addOffset(-3, 1, 0, 5);

		shape.addOffset(-3, 1, 1, 15);
		shape.addOffset(-3, 1, -1, 17);
		
		shape.addOffset(-3, 2, 0, 5);

		shape.addOffset(-3, 2, 1, 33);
		shape.addOffset(-3, 2, -1, 35);
		
		
		return shape;
		
		
	}


	/**
	 * Creates the filter for the ability.
	 * 
	 * @return filter
	 */
	public static BlockFilter createFilter(){
		
		
		BlockFilter filter = new BlockFilter();
		
		filter.addMaterial(Material.STONE);
		filter.addMaterial(Material.COAL_ORE);
		filter.addMaterial(Material.IRON_ORE);
		filter.addMaterial(Material.GOLD_ORE);
		filter.addMaterial(Material.DIAMOND_ORE);
		filter.addMaterial(Material.REDSTONE_ORE);
		
		return filter;
		
		
	}

	
	
	
}
