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
import org.saga.config.GeneralConfiguration;
import org.saga.listeners.events.SagaLootEvent;
import org.saga.listeners.events.SagaBuildEvent;
import org.saga.listeners.events.SagaEventHandler;
import org.saga.metadata.UnnaturalTag;
import org.saga.player.SagaPlayer;
import org.saga.settlements.BundleManager;
import org.saga.settlements.SagaChunk;
import org.saga.statistics.XrayIndicator;

public class BlockListener implements Listener{
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		
		
		// Saga disabled:
		if(GeneralConfiguration.isDisabled(event.getPlayer().getLocation().getWorld())) return;
    	
		// Cancel build on failure:
		SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null){
    		event.setCancelled(true);
    		return;
    	}
    	
    	
		// Get saga chunk:
    	SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(event.getBlock().getLocation());
    	
    	// Build event:
    	SagaBuildEvent buildEvent = new SagaBuildEvent(event, sagaPlayer, sagaChunk);
    	SagaEventHandler.handleBuild(buildEvent);
    	if(buildEvent.isCancelled()) return;
    	
    	// Saga loot event:
    	SagaLootEvent lootEvent = new SagaLootEvent(event, sagaPlayer, sagaChunk);
    	
    	// Forward to chunk:
    	if(sagaChunk != null) sagaChunk.onBlockBreak(event, sagaPlayer);
    	
    	if(event.isCancelled()) return;
    	
    	// Handle event:
    	sagaPlayer.getAttributeManager().handleBlockBreak(lootEvent);
    	
    	// X-ray:
    	XrayIndicator.onBlockBreak(sagaPlayer, event);
    	
    	// Apply loot event:
    	lootEvent.apply();
    	
    	
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		
		
		// Saga disabled:
		if(GeneralConfiguration.isDisabled(event.getPlayer().getLocation().getWorld())) return;
		
		// Cancel build on failure:
		SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null){
    		event.setCancelled(true);
    		return;
    	}
		
    	
		// Get saga chunk:
    	Block block = event.getBlock();
		SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(event.getBlock().getLocation());

    	// Build event:
    	SagaBuildEvent bldEvent = new SagaBuildEvent(event, sagaPlayer, sagaChunk);
    	SagaEventHandler.handleBuild(bldEvent);
    	if(bldEvent.isCancelled()) return;
    	
    	// Forward event:
    	if(sagaChunk != null) sagaChunk.onBlockPlace(event, sagaPlayer);
    	
    	if(event.isCancelled()) return;
    	
    	// Unnatural tag:
    	if(!block.hasMetadata(UnnaturalTag.METADATA_KEY)){
    		block.setMetadata(UnnaturalTag.METADATA_KEY, UnnaturalTag.METADATA_VALUE);
    	}
    	
    	// Handle data change:
    	GeneralConfiguration.config().modifyBlockData(block);
    	
	
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignChange(SignChangeEvent event) {
		
		// Saga disabled:
		if(GeneralConfiguration.isDisabled(event.getPlayer().getLocation().getWorld())) return;
    	
		// Saga player:
		SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null) return;
    	
    	
    	// Get saga chunk:
    	Location location = event.getBlock().getLocation();
    	SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(location.getWorld().getChunkAt(location));
    	
    	// Build event:
    	SagaBuildEvent buildEvent = new SagaBuildEvent(event, sagaPlayer, sagaChunk);
    	SagaEventHandler.handleBuild(buildEvent);
    	
    	if(buildEvent.isCancelled()) return;
    	
    	// Forward to chunk:
    	if(sagaChunk != null) sagaChunk.onSignChange(event, sagaPlayer);
    	
    	
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockSpread(BlockSpreadEvent event) {
		
		
		// Saga disabled:
		if(GeneralConfiguration.isDisabled(event.getBlock().getWorld())) return;
		
		
    	// Get saga chunk:
    	Location location = event.getBlock().getLocation();
    	SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(location);
    	
    	// Forward to chunk:
    	if(sagaChunk != null) sagaChunk.getBundle().onBlockSpread(event, sagaChunk);
    	
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockFromTo(BlockFromToEvent event) {


		// Saga disabled:
		if(GeneralConfiguration.isDisabled(event.getBlock().getWorld())) return;
    	
		
		// Get saga chunk:
    	Location location = event.getBlock().getLocation();
    	SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(location.getWorld().getChunkAt(location));
    	
    	// Forward to chunk:
    	if(sagaChunk != null) sagaChunk.getBundle().onBlockFromTo(event, sagaChunk);
    	
		
	}

	
}
