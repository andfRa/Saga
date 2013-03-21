package org.saga.buildings;

import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.buildings.production.ProductionBuilding;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.messages.GeneralMessages;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Bundle;
import org.saga.settlements.Settlement.SettlementPermission;


public class Farm extends ProductionBuilding {


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
		
		
		if(!(event.sagaAttacker instanceof SagaPlayer)) return;
		
		Creature damaged = event.creatureDefender;
		SagaPlayer damager = (SagaPlayer) event.sagaAttacker;
		
		// Stop animal abuse by non members:
		if(damaged instanceof Animals && damager != null){
			
			Bundle bundle = getChunkBundle();
			if(bundle == null) return;
			
			// Permissions:
			if(!bundle.hasPermission(damager, SettlementPermission.HURT_FARM_ANIMALS)){
				damager.message(GeneralMessages.noPermission(this));
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
