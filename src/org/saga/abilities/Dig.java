package org.saga.abilities;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.Saga;
import org.saga.messages.AbilityMessages;
import org.saga.messages.effects.StatsEffectHandler;
import org.saga.player.SagaPlayer;


public class Dig extends Ability{

	
	/**
	 * Radius key.
	 */
	private static String RADIUS_KEY = "radius";
	
	/**
	 * Randomness key.
	 */
	private static String RANDOMNESS_AMOUNT_KEY = "random block";

	
	
	// Initialisation:
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Dig(AbilityDefinition definition) {
		
        super(definition);
	
	}

	/* 
	 * Trigger indication.
	 * 
	 * @see org.saga.abilities.Ability#hasAttackPreTrigger()
	 */
	@Override
	public boolean hasInteractPreTrigger() {
		return true;
	}
	
	
	
	// Ability usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#instant(org.bukkit.event.player.PlayerInteractEvent)
	 */
	@Override
	public boolean triggerInteract(PlayerInteractEvent event) {

		
		ItemStack itemHand = event.getItem();
		Player player = event.getPlayer();
		Integer abilityLevel = getScore();

		// Drops:
		boolean triggered = false;

		// Check blocks:
		if(event.getClickedBlock() == null || !checkBlock(event.getClickedBlock())){
			getSagaLiving().message(AbilityMessages.targetDirtSand(this));
			return false;
		}
		
		// Get blocks:
		double radius = getDefinition().getFunction(RADIUS_KEY).intValue(abilityLevel);
		double shapeRadius2 = radius*radius;
		double randRadius2 = (radius + 1)*(radius + 1);
		int boxRadius = (int)Math.ceil(radius + 1);
		
		ArrayList<Block> blocks = new ArrayList<Block>();
		ArrayList<Block> randoms = new ArrayList<Block>();
		for (int modx = -boxRadius; modx <= boxRadius; modx++) {
			for (int mody = -boxRadius; mody <= boxRadius; mody++) {
				for (int modz = -boxRadius; modz <= boxRadius; modz++) {
					
					Block relative = event.getClickedBlock().getRelative(modx, mody, modz);
					
					if(!checkBlock(relative)) continue;
					
					if(modx*modx + mody*mody + modz*modz <= shapeRadius2){
						blocks.add(relative);
					}else if(modx*modx + mody*mody + modz*modz <= randRadius2){
						randoms.add(relative);
					}
					
				}
			}
		}
		
		// Randomisation:
		Integer randomAmount = getDefinition().getFunction(RANDOMNESS_AMOUNT_KEY).randomIntValue(abilityLevel);
		
		for (Block block : blocks) {
			
			// Send event:
			BlockBreakEvent eventB = new BlockBreakEvent(block, player);
			Saga.plugin().getServer().getPluginManager().callEvent(eventB);
			if(eventB.isCancelled()) return triggered;
			
			block.breakNaturally(itemHand);
			getSagaLiving().damageTool();
			
			
		}
		
		// Trigger:
		if(breakBlock(player, blocks, randoms, randomAmount)){

			// Effect:
			getSagaLiving().playGlobalEffect(Effect.STEP_SOUND, Material.STONE.getId());
			
			if(getSagaLiving() instanceof SagaPlayer) StatsEffectHandler.playAnimateArm((SagaPlayer) getSagaLiving());
			
			return true;
			
		}else{
			return false;
		}
		
		
	}
	
	
	
	// Block operations:
	/**
	 * Checks if the ability can be used on the given block.
	 * 
	 * @param block block
	 * @return true if can be used
	 */
	private boolean checkBlock(Block block) {

		
		if(block.getType() == Material.DIRT) return true;
		
		if(block.getType() == Material.GRASS) return true;
		
		if(block.getType() == Material.SOIL) return true;
		
		if(block.getType() == Material.SNOW_BLOCK) return true;
		
		if(block.getType() == Material.SAND) return true;
		
		if(block.getType() == Material.GRAVEL) return true;
		
		if(block.getType() == Material.SOUL_SAND) return true;
		
		return false;
		
		
	}
	
	
	/**
	 * Breaks blocks in a spherical shape.
	 * 
	 * @param player player
	 * @param blocks blocks to remove
	 * @param randoms random block to remove
	 * @param randomness amount of random blocks to remove
	 * @return true if block were removd
	 */
	private boolean breakBlock(Player player, ArrayList<Block> blocks, ArrayList<Block> randoms, Integer randomness) {

		
		Random random = new Random();
		boolean triggered = false;
		ItemStack itemHand = player.getItemInHand();
		
		for (Block block : blocks) {

			// Send event:
			BlockBreakEvent eventB = new BlockBreakEvent(block, player);
			Saga.plugin().getServer().getPluginManager().callEvent(eventB);
			if(eventB.isCancelled()) return triggered;
			
			// Break:
			block.breakNaturally(itemHand);
			getSagaLiving().damageTool();
			
			triggered = true;
			
		}
		
		while (randomness > 0 && randoms.size() > 0) {

			int i = random.nextInt(randoms.size());
			Block block = randoms.remove(i);
			randomness--;

			// Send event:
			BlockBreakEvent eventB = new BlockBreakEvent(block, player);
			Saga.plugin().getServer().getPluginManager().callEvent(eventB);
			if(eventB.isCancelled()) return triggered;
			
			// Break:
			block.breakNaturally(itemHand);
			getSagaLiving().damageTool();
			
			triggered = true;
			
		}
		
		return triggered;
		
		
	}
	
	
}
