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
import org.saga.SagaLogger;
import org.saga.chunks.BundleManager;
import org.saga.chunks.SagaChunk;
import org.saga.config.GeneralConfiguration;
import org.saga.listeners.events.SagaBlockBreakEvent;
import org.saga.listeners.events.SagaBuildEvent;
import org.saga.listeners.events.SagaEventHandler;
import org.saga.metadata.UnnaturalTag;
import org.saga.player.SagaPlayer;
import org.saga.statistics.XrayIndicator;

public class BlockListener implements Listener{
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {


		// Saga disabled:
		if(GeneralConfiguration.isDisabled(event.getPlayer().getLocation().getWorld())) return;
    	
		// Get saga chunk:
    	SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(event.getBlock().getLocation());
    	
		// Get player:
    	SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null){
    		
    		SagaLogger.severe(BlockListener.class, "can't continue with onBlockBreak, because the saga player for "+ event.getPlayer().getName() + " isn't loaded");
    		event.setCancelled(true);
    		
    		return;
    		
    	}

    	// Build event:
    	SagaBuildEvent eventB = new SagaBuildEvent(event, sagaPlayer, sagaChunk);
    	SagaEventHandler.onBuild(eventB);
    	if(eventB.isCancelled()) return;

    	// Saga event:
    	SagaBlockBreakEvent eventS = new SagaBlockBreakEvent(event, sagaPlayer, sagaChunk);
    	
    	// Forward to chunk:
    	if(sagaChunk != null) sagaChunk.onBlockBreak(event, sagaPlayer);
    	
    	if(event.isCancelled()) return;
    	
    	// Handle event:
    	sagaPlayer.getAttributeManager().handleBlockBreak(eventS);
    	
    	// X-ray:
    	XrayIndicator.onBlockBreak(sagaPlayer, event);
    	
    	// Apply event:
    	eventS.apply();
    	
    	
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event) {
		

		// Saga disabled:
		if(GeneralConfiguration.isDisabled(event.getPlayer().getLocation().getWorld())) return;
    	
		Block block = event.getBlock();
		
		// Get saga chunk:
    	SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(event.getBlock().getLocation());
    	
		// Get player:
    	SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null){
    		
    		SagaLogger.severe(BlockListener.class, "can't continue with onBlockPlace, because the saga player for "+ event.getPlayer().getName() + " isn't loaded.");
    		event.setCancelled(true);
    		
    		return;
    		
    	}

    	// Build event:
    	SagaBuildEvent eventB = new SagaBuildEvent(event, sagaPlayer, sagaChunk);
    	SagaEventHandler.onBuild(eventB);
    	if(eventB.isCancelled()) return;
    	
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
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent event) {


		// Saga disabled:
		if(GeneralConfiguration.isDisabled(event.getPlayer().getLocation().getWorld())) return;
    	
    	// Get saga chunk:
    	Location location = event.getBlock().getLocation();
    	SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(location.getWorld().getChunkAt(location));
    	
		// Get player:
    	SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null){
    		SagaLogger.severe(BlockListener.class, "can't continue with onSignChange, because the saga player for "+ event.getPlayer().getName() + " isn't loaded.");
    		return;
    	}
    	
    	// Build event:
    	SagaBuildEvent eventB = new SagaBuildEvent(event, sagaPlayer, sagaChunk);
    	
    	// Forward to Saga chunk:
    	if(eventB.getSagaChunk() != null){
    		
    		sagaChunk.onBuild(eventB);
    		
    	}

    	if(eventB.isCancelled()) return;
    	
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
    	if(sagaChunk != null) sagaChunk.getChunkBundle().onBlockSpread(event, sagaChunk);
    	
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockFromTo(BlockFromToEvent event) {


		// Saga disabled:
		if(GeneralConfiguration.isDisabled(event.getBlock().getWorld())) return;
    	
		// Get saga chunk:
    	Location location = event.getBlock().getLocation();
    	SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(location.getWorld().getChunkAt(location));
    	
    	// Forward to chunk:
    	if(sagaChunk != null) sagaChunk.getChunkBundle().onBlockFromTo(event, sagaChunk);
    	
		
	}

	
}
