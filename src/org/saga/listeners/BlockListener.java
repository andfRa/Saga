package org.saga.listeners;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.saga.Saga;
import org.saga.config.GeneralConfiguration;
import org.saga.config.SettlementConfiguration;
import org.saga.listeners.events.SagaBuildEvent;
import org.saga.listeners.events.SagaEventHandler;
import org.saga.listeners.events.SagaLootEvent;
import org.saga.metadata.UnnaturalTag;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Bundle;
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
		
		if(!canSpread(event.getSource().getType(), event.getSource().getLocation(), event.getBlock().getLocation())) event.setCancelled(true);
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockFromTo(BlockFromToEvent event) {

		// Saga disabled:
		if(GeneralConfiguration.isDisabled(event.getBlock().getWorld())) return;
		
		
		if(!canSpread(event.getBlock().getType(), event.getBlock().getLocation(), event.getToBlock().getLocation())) event.setCancelled(true);
		
	}
	

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBurn(BlockBurnEvent event) {
		event.setCancelled(true);
	}

	
	// Util:
	/**
	 * Checks if the material can spread.
	 * 
	 * @param mat material
	 * @param from from location
	 * @param to to location
	 * @return true if can spread
	 */
	private static boolean canSpread(Material mat, Location from, Location to) {

		if(mat != Material.FIRE && mat != Material.LAVA && mat != Material.STATIONARY_LAVA && mat != Material.WATER && mat != Material.STATIONARY_WATER) return true;
		
		SagaChunk fromChunk = BundleManager.manager().getSagaChunk(from);
    	SagaChunk toChunk = BundleManager.manager().getSagaChunk(to);
    	Bundle fromBundle = null;
    	Bundle toBundle = null;
    	if(fromChunk != null) fromBundle = fromChunk.getBundle();
    	if(toChunk != null) toBundle = toChunk.getBundle();
    	
    	if(toBundle != null){
    		
    		// Inward spread:
    		if(SettlementConfiguration.config().getInwardSpreadProtection() && fromBundle != toBundle) return false;
    		
    		// Fire spread:
    		if(mat == Material.FIRE && toBundle == fromBundle) return false;
    		
    	}
		
		return true;
		
	}
	
}
