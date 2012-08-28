package org.saga.listeners.events;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.chunks.SagaChunk;
import org.saga.config.ExperienceConfiguration;
import org.saga.config.GeneralConfiguration;
import org.saga.messages.GeneralMessages;
import org.saga.metadata.UnnaturalTag;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.TwoPointFunction;

public class SagaBlockBreakEvent {

	
	/**
	 * Random generator.
	 */
	private static Random RANDOM = new Random();

	
	/**
	 * Minecraft event.
	 */
	private BlockBreakEvent event;
	
	/**
	 * target block.
	 */
	private Block block;

	/**
	 * Saga player.
	 */
	private SagaPlayer sagaPlayer;
	
	/**
	 * Location saga chunk.
	 */
	private SagaChunk blockSagaChunk = null;
	
	/**
	 * Drop modifier.
	 */
	private Double modifier = 0.0;

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
	public SagaBlockBreakEvent(BlockBreakEvent event, SagaPlayer sagaPlayer, SagaChunk sagaChunk) {

		
		this.event = event;
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
	
	
	

	// Conclude:
	/**
	 * Applies the event.
	 * 
	 */
	public void apply() {
		
		
		ItemStack item = event.getPlayer().getItemInHand();
		
		// Drops:
		ArrayList<ItemStack> drops = null;
		if(item == null){
			drops = new ArrayList<ItemStack>(event.getBlock().getDrops());
		}else{
			drops = new ArrayList<ItemStack>(event.getBlock().getDrops(item));
		}
		
		if(isNatural){

			// Award exp:
			if(sagaPlayer != null){
				
				Double exp = ExperienceConfiguration.config().getExp(block);
				sagaPlayer.awardExp(exp);
				
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
	 * Gets the sagaPlayer.
	 * 
	 * @return the sagaPlayer
	 */
	public SagaPlayer getSagaPlayer() {
	
	
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
