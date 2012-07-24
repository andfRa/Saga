package org.saga.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.saga.chunks.ChunkBundleManager;
import org.saga.chunks.SagaChunk;

public class WorldListener implements Listener{

	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onChunkLoadEvent(ChunkLoadEvent event) {

		
		SagaChunk sagaChunk = ChunkBundleManager.manager().getSagaChunk(event.getChunk());

		// Forward event:
		if(sagaChunk != null) sagaChunk.onChunkLoad();
		
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onChunkUnloadEvent(ChunkUnloadEvent event) {
		
	}
	
	
}
