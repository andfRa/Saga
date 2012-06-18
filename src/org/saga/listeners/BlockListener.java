package org.saga.listeners;


import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.saga.Saga;
import org.saga.chunkGroups.ChunkGroupManager;
import org.saga.chunkGroups.SagaChunk;
import org.saga.config.BalanceConfiguration;
import org.saga.dependencies.PermissionsManager;
import org.saga.listeners.events.SagaBlockBreakEvent;
import org.saga.listeners.events.SagaBuildEvent;
import org.saga.messages.SagaMessages;
import org.saga.metadata.UnnaturalTag;
import org.saga.player.SagaPlayer;
import org.saga.statistics.XrayIndicator;

public class BlockListener implements Listener{
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {


		Block block = event.getBlock();
		
		// Get saga chunk:
    	SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(event.getBlock().getLocation());
    	
		// Get player:
    	SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null){
    		
    		Saga.warning("Can't continue with onBlockBreak, because the saga player for "+ event.getPlayer().getName() + " isn't loaded.");
    		event.setCancelled(true);
    		
    		return;
    		
    	}

    	// Build event:
    	SagaBuildEvent eventB = new SagaBuildEvent(event, sagaPlayer, sagaChunk);
    	
    	// Claimed:
    	if(eventB.getSagaChunk() != null){
    		
    		sagaChunk.onBuild(eventB);
    		
    	}
    	
    	// Wilderness:
    	else{
    		
    		if(!PermissionsManager.hasPermission(sagaPlayer, PermissionsManager.WILDERNESS_BUILD_PERMISSION)){
    			sagaPlayer.message(SagaMessages.noPermissionWilderness());
    			eventB.cancel();
    		}
    		
    	}
    	
    	if(eventB.isCancelled()) return;
    	
    	// Saga event:
    	SagaBlockBreakEvent eventS = new SagaBlockBreakEvent(event, sagaPlayer, sagaChunk);
    	
    	// Forward to chunk:
    	if(sagaChunk != null) sagaChunk.onBlockBreak(event, sagaPlayer);
    	
    	if(event.isCancelled()) return;
    	
    	// Forward to manager:
    	sagaPlayer.getAttributeManager().onBlockBreak(eventS);
    	
    	// X-ray:
    	XrayIndicator.onBlockBreak(sagaPlayer, event);
    	
    	// Apply event:
    	eventS.apply();
    	
    	
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event) {
		
		
		Block block = event.getBlock();
		
		// Get saga chunk:
    	SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(event.getBlock().getLocation());
    	
		// Get player:
    	SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null){
    		
    		Saga.warning("Can't continue with onBlockPlace, because the saga player for "+ event.getPlayer().getName() + " isn't loaded.");
    		event.setCancelled(true);
    		
    		return;
    		
    	}

    	// Build event:
    	SagaBuildEvent eventB = new SagaBuildEvent(event, sagaPlayer, sagaChunk);
    	
    	// Claimed:
    	if(eventB.getSagaChunk() != null){
    		
    		sagaChunk.onBuild(eventB);
    		
    	}
    	
    	// Wilderness:
    	else{
    		
    		if(!PermissionsManager.hasPermission(sagaPlayer, PermissionsManager.WILDERNESS_BUILD_PERMISSION)){
    			sagaPlayer.message(SagaMessages.noPermissionWilderness());
    			eventB.cancel();
    		}
    		
    	}
    	
    	if(eventB.isCancelled()) return;
    	
    	// Forward event:
    	if(sagaChunk != null) sagaChunk.onBlockPlace(event, sagaPlayer);
    	
    	if(event.isCancelled()) return;
    	
    	// Unnatural tag:
    	if(!block.hasMetadata(UnnaturalTag.METADATA_KEY)){
    		block.setMetadata(UnnaturalTag.METADATA_KEY, UnnaturalTag.METADATA_VALUE);
    	}
    	
    	// Handle data change:
    	BalanceConfiguration.config().modifyBlockData(block);
    	
	
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
    	if(sagaChunk != null) sagaChunk.onSignChange(event, sagaPlayer);
    	
    	
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockSpread(BlockSpreadEvent event) {

		
    	// Get saga chunk:
    	Location location = event.getBlock().getLocation();
    	SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(location);
    	
    	// Forward to chunk:
    	if(sagaChunk != null) sagaChunk.getChunkGroup().onBlockSpread(event, sagaChunk);
    	
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockFromTo(BlockFromToEvent event) {


		// Get saga chunk:
    	Location location = event.getBlock().getLocation();
    	SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(location.getWorld().getChunkAt(location));
    	
    	// Forward to chunk:
    	if(sagaChunk != null) sagaChunk.getChunkGroup().onBlockFromTo(event, sagaChunk);
    	
		
	}

	
}
