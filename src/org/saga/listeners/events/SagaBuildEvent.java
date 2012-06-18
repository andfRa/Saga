package org.saga.listeners.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event.Result;
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
	
	private SagaChunk sagaChunk = null;
	
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
	 * Checks if the event is cancelled.
	 * 
	 * @return true if cancelled
	 */
	public boolean isCancelled() {
		return event.isCancelled();
	}
	
	
	
}
