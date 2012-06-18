package org.saga.buildings;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.messages.SagaMessages;
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
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#completeExtended()
	 */
	@Override
	public boolean complete() throws InvalidBuildingException {
		

		boolean integrity = super.complete();
		
//		if(signs == null){
//			signs = new ArrayList<BuildingSign>();
//			Saga.severe(this, "signs field failed to initialize", "setting default");
//			integrity = false;
//		}
		
		return integrity;
		
		
	}

	
	// Events:
	@Override
	public void onPlayerDamagedCreature(EntityDamageByEntityEvent event, SagaPlayer damager, Creature damaged) {
	
		
		// Stop animal abuse:
		if(damaged instanceof Animals){
			
			ChunkGroup chunkGroup = getChunkGroup();
			
			if(chunkGroup == null) return;
			
			// Permissions:
			if(!chunkGroup.canHurtAnimals(damager)){
				damager.message(SagaMessages.noPermission(this));
				event.setCancelled(true);
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
