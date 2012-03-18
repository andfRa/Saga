package org.saga;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.chunkGroups.ChunkGroupManager;
import org.saga.chunkGroups.SagaChunk;
import org.saga.config.BalanceConfiguration;
import org.saga.player.SagaPlayer;
import org.saga.statistics.XrayIndicator;

public class SagaBlockListener implements Listener{
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {

		
    	// Get saga chunk:
    	Location location = event.getBlock().getLocation();
    	SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(location.getWorld().getChunkAt(location));
    	
		// Get player:
    	SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null){
    		Saga.warning("Can't continue with onBlockBreak, because the saga player for "+ event.getPlayer().getName() + " isn't loaded.");
    		if(sagaChunk != null){
    			Saga.info("Found saga chunk. Canceling event.");
    			event.setCancelled(true);
    		}
    		return;
    	}
    	
    	// Forward to chunk:
    	if(sagaChunk != null){
    		sagaChunk.onBlockBreak(event, sagaPlayer);
    	}

    	if(event.isCancelled()){
    		return;
    	}
    	
    	// X-ray:
    	XrayIndicator.handleMine(sagaPlayer, event);
    	
    	// Forward to level manager:
    	sagaPlayer.getLevelManager().onBlockBreak(event);
		
    	// Experience:
    	sagaPlayer.onBlockExp(event);
    	
    	// TODO: Bug workaround(hoe takes no damage):
    	ItemStack item = sagaPlayer.getItemInHand();
    	if(item.getType() == Material.WOOD_HOE || item.getType() == Material.STONE_HOE || item.getType() == Material.IRON_HOE || item.getType() == Material.GOLD_HOE || item.getType() == Material.DIAMOND_HOE){
    		sagaPlayer.damageTool();
    	}
    	
    	
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockDamage(BlockDamageEvent event) {
		
		
    	// Get saga chunk:
    	Location location = event.getBlock().getLocation();
    	SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(location.getWorld().getChunkAt(location));
    	
		// Get player:
    	SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null){
    		Saga.warning("Can't continue with onBlockDamage, because the saga player for "+ event.getPlayer().getName() + " isn't loaded.");
    		return;
    	}
    	
    	// Forward to chunk:
    	if(sagaChunk != null){
    		sagaChunk.onBlockDamage(event, sagaPlayer);
    	}
    	
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event) {


		// Get saga chunk:
    	Location location = event.getBlock().getLocation();
    	SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(location.getWorld().getChunkAt(location));
    	
		// Get player:
    	SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null){
    		Saga.warning("Can't continue with onBlockPlace, because the saga player for "+ event.getPlayer().getName() + " isn't loaded.");
    		if(sagaChunk != null){
    			Saga.info("Found saga chunk. Canceling event.");
    			event.setCancelled(true);
    		}
    		return;
    	}
    	
    	// Forward to chunk:
    	if(sagaChunk != null){
    		sagaChunk.onBlockPlace(event, sagaPlayer);
    	}
    	
    	// Handle data change:
    	BalanceConfiguration.config().handleDataChange(event.getBlock());
    	
	
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent event) {

		
    	// Get saga chunk:
    	Location location = event.getBlock().getLocation();
    	SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(location.getWorld().getChunkAt(location));
    	
		// Get player:
    	SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null){
    		Saga.warning("Can't continue with onSignChange, because the saga player for "+ event.getPlayer().getName() + " isn't loaded.");
    		return;
    	}
    	
    	
    	// Forward to chunk:
    	if(sagaChunk != null){
    		sagaChunk.onSignChange(event, sagaPlayer);
    	}
    	
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockSpread(BlockSpreadEvent event) {

		
    	// Get saga chunk:
    	Location location = event.getBlock().getLocation();
    	SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(location);
    	
    	// Forward to chunk:
    	if(sagaChunk != null){
    		sagaChunk.getChunkGroup().onBlockSpread(event, sagaChunk);
    	}
    	
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockFromTo(BlockFromToEvent event) {


		// Get saga chunk:
    	Location location = event.getBlock().getLocation();
    	SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(location.getWorld().getChunkAt(location));
    	
    	// Forward to chunk:
    	if(sagaChunk != null){
    		sagaChunk.getChunkGroup().onBlockFromTo(event, sagaChunk);
    	}
    	
		
	}

	
}
