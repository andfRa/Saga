package org.saga.listeners.events;

import java.util.PriorityQueue;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.chunkGroups.SagaChunk;
import org.saga.player.SagaPlayer;

public class SagaBuildEvent {

	
	/**
	 * Event.
	 */
	private Cancellable event;
	
	/**
	 * Saga player.
	 */
	private SagaPlayer sagaPlayer;
	
	/**
	 * Origin Saga chunk.
	 */
	private SagaChunk sagaChunk = null;

	/**
	 * Build override.
	 */
	private PriorityQueue<BuildOverride> buildOverride = new PriorityQueue<SagaBuildEvent.BuildOverride>();
	
	
	
	// Initialise:
	/**
	 * Sets event.
	 * 
	 * @param event event
	 */
	public SagaBuildEvent(Cancellable event, SagaPlayer sagaPlayer, SagaChunk sagaChunk) {
		
		this.event = event;
		this.sagaPlayer = sagaPlayer;
		this.sagaChunk = sagaChunk;
		
	}
	
	
	
	// Modify:
	/**
	 * Adds a build override.
	 * 
	 * @param override build override
	 */
	public void addBuildOverride(BuildOverride override) {

		buildOverride.add(override);
		
	}
	
	
	
	// Conclude:
	/**
	 * Cancel event.
	 * 
	 */
	public void cancel() {

		event.setCancelled(true);
		
		if(event instanceof BlockPlaceEvent){
			((BlockPlaceEvent) event).setBuild(false);
		}
		
		else if(event instanceof PlayerInteractEvent){
			((PlayerInteractEvent) event).setUseInteractedBlock(Result.DENY);
			((PlayerInteractEvent) event).setUseItemInHand(Result.DENY);
		}

	}


	
	// Event information:
	/**
	 * Gets the sagaPlayer.
	 * 
	 * @return the sagaPlayer
	 */
	public SagaPlayer getSagaPlayer() {
		return sagaPlayer;
	}
	
	/**
	 * Gets the sagaChunk.
	 * 
	 * @return the sagaChunk, null if none
	 */
	public SagaChunk getSagaChunk() {
		return sagaChunk;
	}
	
	/**
	 * Gets the block.
	 * 
	 * @return block, null if none
	 */
	public Block getBlock() {
		
		if(event instanceof BlockEvent) return ((BlockEvent) event).getBlock();
		
		return null;
		
	}
	
	/**
	 * Gets the top override.
	 * 
	 * @return top override, NONE if none
	 */
	public BuildOverride getbuildOverride() {

		if(buildOverride.size() == 0) return BuildOverride.NONE;
		
		return buildOverride.peek();

	}
	
	/**
	 * Checks if the event is cancelled.
	 * 
	 * @return true if cancelled
	 */
	public boolean isCancelled() {
		return event.isCancelled();
	}

	
	

	/**
	 * Build overrides.
	 * 
	 * @author andf
	 *
	 */
	public enum BuildOverride{
		
		
		ADMIN_ALLOW(true),
		ADMIN_DENY(false),
		
		OPEN_STORAGE_AREA_ALLOW(true),
		STORAGE_AREA_DENY(false),
		
		CHUNK_GROUP_DENY(false),
		
		SETTLEMENT_OWNER_ALLOW(true),
		
		HOME_RESIDENT_ALLOW(true),
		HOME_DENY(false),
		
		BUILDING_DENY(false),
		SETTLEMENT_DENY(false),
		WILDERNESS_DENY(false),
		
		NONE(true);
		
		
		/**
		 * If true, then build will be allowed.
		 */
		private boolean allow;
		
		/**
		 * Sets if build override enables build.
		 * 
		 * @param true if allows build, false if denies build
		 */
		private BuildOverride(boolean allow) {
			this.allow = allow;
		}
		
		/**
		 * If true, then build will be allowed. Denied if false.
		 * 
		 * @return true if allowed, false if denied
		 */
		public boolean isAllow() {
			return allow;
		}		
		
	}
	
	
}
