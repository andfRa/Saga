package org.saga.listeners.events;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.Saga;
import org.saga.chunks.SagaChunk;
import org.saga.config.ExperienceConfiguration;
import org.saga.config.GeneralConfiguration;
import org.saga.messages.GeneralMessages;
import org.saga.metadata.UnnaturalTag;
import org.saga.player.SagaLiving;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.TwoPointFunction;

public class SagaBlockBreakEvent {

	
	/**
	 * Random generator.
	 */
	private static Random RANDOM = new Random();


	/**
	 * target block.
	 */
	public final Block block;

	/**
	 * Tool material.
	 */
	public final Material tool;
	
	/**
	 * Saga living.
	 */
	public final SagaLiving<?> sagaPlayer;
	
	/**
	 * Location saga chunk.
	 */
	public final SagaChunk blockSagaChunk;
	

	/**
	 * Minecraft event.
	 */
	private BlockBreakEvent event;
	
	/**
	 * Drop modifier.
	 */
	private Double modifier = 0.0;

	/**
	 * Tool sloppiness multiplier.
	 */
	private double sloppiness = 1.0;
	
	/**
	 * True if the block is considered player placed.
	 */
	private Boolean isNatural = true;
	
	
	
	// Initialise:
	/**
	 * Sets event and player.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 * @param sagaChunk saga chunk
	 */
	public SagaBlockBreakEvent(BlockBreakEvent event, SagaLiving<?> sagaPlayer, SagaChunk sagaChunk) {

		
		this.event = event;
		this.tool = event.getPlayer().getItemInHand().getType();
		this.sagaPlayer = sagaPlayer;
		this.block = event.getBlock();
    	this.blockSagaChunk = sagaChunk;
    	
    	// Set player placed:
    	if(block.hasMetadata(UnnaturalTag.METADATA_KEY)){
    		
    		isNatural = false;
    		
	    	// Compensate for growth:
	    	Byte newData = GeneralConfiguration.config().getNewBlockData(block);
	    	
	    	if(!newData.equals((byte)-1) && !newData.equals(block.getData())){
	    		isNatural = true;
	    	}
    		
		}
		

	}
	
	
	
	// Modify:
	/**
	 * Modifies drops.
	 * 
	 * @param amount amount to add
	 */
	public void modifyDrops(Double amount) {
		modifier += amount;
	}

	/**
	 * Modifies tool handling.
	 * 
	 * @param amount amount to modify
	 */
	public void modifyToolHandling(double amount) {
		sloppiness-= amount;
	}
	
	

	// Conclude:
	/**
	 * Applies the event.
	 * 
	 */
	public void apply() {
		
		
		ItemStack item = event.getPlayer().getItemInHand();
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>(event.getBlock().getDrops(item));

		// Reduce tool damage:
		final int undurability = item.getDurability();
		
		// Natural break:
		if(isNatural){

			// Award exp:
			if(sagaPlayer != null && sagaPlayer instanceof SagaPlayer){
				
				Double exp = ExperienceConfiguration.config().getExp(block);
				((SagaPlayer)sagaPlayer).awardExp(exp);
				
				// Statistics:
				StatisticsManager.manager().addExp("block", GeneralMessages.material(block.getType()), exp);
				
			}
			
			// Select and drop:
			ArrayList<ItemStack> selectedDrops = selectDrops(drops);
			Location location = block.getLocation();
			for (ItemStack drop : selectedDrops) {
				location.getWorld().dropItemNaturally(location, drop);
			}
			
		}
		
		// Schedule for next tick:
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Saga.plugin(), new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				
				// Tool damage reduction:
				Player player = event.getPlayer();
				ItemStack item = player.getItemInHand();
				int damage = item.getDurability() - undurability;
				damage = TwoPointFunction.randomRound(sloppiness * damage).shortValue();
				int pundurability = item.getDurability();
				item.setDurability((short) (undurability + damage));
				if(item.getDurability() != pundurability) player.updateInventory();
				
			}
		}, 1);
		
		
		
		
	}
	
	/**
	 * Selects random drops from given drops
	 * 
	 * @param drops drops
	 * @return selected drops
	 */
	private ArrayList<ItemStack> selectDrops(ArrayList<ItemStack> drops) {


		Integer newDrops = TwoPointFunction.randomRound(modifier);
		ArrayList<ItemStack> selectedDrops = new ArrayList<ItemStack>();
		
		if(drops.size() > 0){
			
			for (int i = 0; i < newDrops; i++) {
			
				ItemStack newDrop = drops.get(RANDOM.nextInt(drops.size()));
				selectedDrops.add(newDrop.clone());
				
				newDrops--;
			
			}
		}
		
		return selectedDrops;
		

	}

	/**
	 * Cancels the event.
	 * 
	 */
	public void cancel() {

		event.setCancelled(true);

	}

	
	
	// Event information:
	/**
	 * Gets the block.
	 * 
	 * @return the block
	 */
	public Block getBlock() {
	
	
		return block;
	}

	/**
	 * Gets the Saga living entity.
	 * 
	 * @return the Saga living entity
	 */
	public SagaLiving<?> getSagaPlayer() {
		return sagaPlayer;
	}

	/**
	 * Gets the blockSagaChunk.
	 * 
	 * @return the blockSagaChunk
	 */
	public SagaChunk getBlockSagaChunk() {
		return blockSagaChunk;
	}

	/**
	 * Gets the isPlayerPlaced.
	 * 
	 * @return the isPlayerPlaced
	 */
	public Boolean getIsPlayerPlaced() {
		return !isNatural;
	}
	
	
	
	
}
