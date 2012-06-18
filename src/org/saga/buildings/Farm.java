package org.saga.buildings;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.messages.BuildingMessages;
import org.saga.player.SagaPlayer;


public class Farm extends Building{


	// Initialisation:
	/**
	 * Creates a building from the definition.
	 * 
	 * @param definition building definition
	 */
	public Farm(BuildingDefinition definition) {
		
		super(definition);
		
	}

	
	// Events:
	@Override
	public void onEntityDamage(SagaEntityDamageEvent event) {
	
		
		Creature damaged = event.getDefenderCreature();
		SagaPlayer damager = event.getAttackerPlayer();
		
		// Stop animal abuse by non members:
		if(damaged instanceof Animals && damager != null){
			
			ChunkGroup chunkGroup = getChunkGroup();
			if(chunkGroup == null) return;
			
			// Permissions:
			if(!chunkGroup.isMember(damager)){
				damager.message(BuildingMessages.farmAnimalsDamageDeny());
				event.cancel();
			}
			
		}
	
	
	}
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent event, SagaPlayer sagaPlayer) {
		
		
		super.onPlayerInteract(event, sagaPlayer);
		
		// Stop trampling:
		if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL){
			
				event.setCancelled(true);
						
			
		}
		
		
	}
	
}
