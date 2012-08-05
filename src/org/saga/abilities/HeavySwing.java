package org.saga.abilities;

import java.util.ArrayList;
import java.util.Random;

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
import org.saga.shape.BlockFilter;
import org.saga.shape.RelativeShape;


public class HeavySwing extends Ability{


	/**
	 * Size key.
	 */
	private static String SIZE_KEY = "size";
	
	/**
	 * Crumble key.
	 */
	private static String CRUMBLE_KEY = "crumble";
	

	/**
	 * Shape for the ability.
	 */
	private static RelativeShape SPAHPE = createShape();

	
	// Initialisation:
	/**
	 * Initialises using definition.
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
	public boolean trigger(PlayerInteractEvent event) {

		
		ItemStack itemHand = event.getItem();
		Player player = event.getPlayer();
		Integer abilityLevel = getScore();

		// Drops:
		boolean triggered = false;
		
		// Check blocks:
		if(event.getClickedBlock() == null){
			getSagaPlayer().message(AbilityMessages.targetStone(this));
			return false;
		}
		
		// Target stone:
		Location targetLocation = event.getPlayer().getLocation();
		
		// Skill functions:
		Integer size = getDefinition().getFunction(SIZE_KEY).randomIntValue(abilityLevel);
		Integer crumble = getDefinition().getFunction(CRUMBLE_KEY).randomIntValue(abilityLevel);
		
		// Get shape:
		RelativeShape shape = SPAHPE;
		ArrayList<Block> blocks = shape.getBlocks(targetLocation, getSagaPlayer().getOrientation(), size);
		
		// Check if target block is included:
		if(!blocks.contains(event.getClickedBlock())){
			getSagaPlayer().message(AbilityMessages.targetStone(this));
			return false;
		}
		
		// Swing:
		for (Block block : blocks) {

			// Send event:
			BlockBreakEvent eventB = new BlockBreakEvent(block, player);
			Saga.plugin().getServer().getPluginManager().callEvent(eventB);
			if(eventB.isCancelled()) return triggered;
			
			block.breakNaturally(itemHand);
			getSagaPlayer().damageTool();
			
			triggered = true;
			
		}
		
		Random random = new Random();
		
		// Crumble blocks:
		ArrayList<Block> crumbleBlocks = new ArrayList<Block>();
		for (Block block : blocks) {
			
			if(block.getRelative(BlockFace.UP).getType() != Material.AIR){
				crumbleBlocks.add(block.getRelative(BlockFace.UP));
			}
			
		}

		// Crumble:
		while(crumble > 0 && crumbleBlocks.size() > 0){
			
			Block crumbleBlock = crumbleBlocks.remove(random.nextInt(crumbleBlocks.size()));
			crumble--;
			
			if(canCrumble(crumbleBlock)){

				crumbleBlock.breakNaturally(itemHand);
				
			}
			
		}

		// Effect:
		getSagaPlayer().playGlobalEffect(Effect.STEP_SOUND, Material.STONE.getId());

		return true;
		
		
	}
	
	
	// Blocks:
	/**
	 * Checks if can crumble.
	 * 
	 * @param block block
	 * @return true if can crumble
	 */
	private boolean canCrumble(Block block) {

		
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
	
	/**
	 * Creates the shape for the ability.
	 * 
	 * @return shape
	 */
	private static RelativeShape createShape(){
		
		
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
	private static BlockFilter createFilter(){
		
		
		BlockFilter filter = new BlockFilter();
		
		filter.addMaterial(Material.STONE);
		filter.addMaterial(Material.COAL_ORE);
		filter.addMaterial(Material.IRON_ORE);
		filter.addMaterial(Material.GOLD_ORE);
		filter.addMaterial(Material.DIAMOND_ORE);
		filter.addMaterial(Material.REDSTONE_ORE);
		
		filter.addMaterial(Material.NETHERRACK);
		filter.addMaterial(Material.ENDER_STONE);
		
		return filter;
		
		
	}

	
	
	
}
