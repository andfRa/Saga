package org.saga.buildings;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.SagaMessages;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.player.SagaPlayer;


public class Farm extends Building{

	
	// Initialization:
	/**
	 * Initializes
	 * 
	 * @param pointCost point cost
	 * @param moneyCost money cost
	 * @param proficiencies proficiencies
	 */
	private Farm(String name) {
		
		super("");
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#completeExtended()
	 */
	@Override
	public boolean completeExtended() {
		

		boolean integrity = true;
		
//		if(signs == null){
//			signs = new ArrayList<BuildingSign>();
//			Saga.severe(this, "signs field failed to initialize", "setting default");
//			integrity = false;
//		}
		
		return integrity;
		
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#blueprint()
	 */
	@Override
	public Building blueprint() {
		return new Farm("");
	}
	
	
	// Events:
	@Override
	public void onPlayerDamagedCreature(EntityDamageByEntityEvent event, SagaPlayer damager, Creature damaged) {
	
		
		// Stop animal abuse:
		if(damaged instanceof Animals){
			
			ChunkGroup chunkGroup = getOriginChunkGroup();
			
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
			
			ChunkGroup chunkGroup = getOriginChunkGroup();
			
			if(chunkGroup == null) return;
			
			// Permissions:
			if(!chunkGroup.canTrample(sagaPlayer)){
				sagaPlayer.message(SagaMessages.noPermission(this));
				event.setCancelled(true);
			}
						
			
		}
		
		
	}
	
}
