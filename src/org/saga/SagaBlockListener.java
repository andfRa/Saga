package org.saga;


import org.bukkit.Location;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.saga.chunkGroups.ChunkGroupManager;
import org.saga.chunkGroups.SagaChunk;
import org.saga.config.BalanceConfiguration;
import org.saga.player.SagaPlayer;
import org.saga.statistics.XrayIndicator;

public class SagaBlockListener extends BlockListener{
	
	
	@Override
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
		
    	// X-ray:
    	XrayIndicator.handleMine(sagaPlayer, event);

    	// Forward to level manager:
    	sagaPlayer.getLevelManager().onBlockBrake(event);
		
		
	}
	
	@Override
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
	
	@Override
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
	
	@Override
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
	
	@Override
	public void onBlockSpread(BlockSpreadEvent event) {

		
    	// Get saga chunk:
    	Location location = event.getBlock().getLocation();
    	SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(location.getWorld().getChunkAt(location));
    	
    	// Forward to chunk:
    	if(sagaChunk != null){
    		sagaChunk.getChunkGroup().onBlockSpread(event, sagaChunk);
    	}
    	
		
	}
	
	@Override
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
