package org.saga.buildings;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.chunks.ChunkBundle;
import org.saga.messages.SagaMessages;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Settlement.SettlementPermission;


public class Warehouse extends Building{


	// Initialisation:
	/**
	 * Creates a building from the definition.
	 * 
	 * @param definition building definition
	 */
	public Warehouse(BuildingDefinition definition) {
		
		super(definition);
		
		
	}
	
	
	// Events:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent, org.saga.player.SagaPlayer)
	 */
	@Override
	public void onPlayerInteract(PlayerInteractEvent event, SagaPlayer sagaPlayer) {

	
		// Chunk group:
		ChunkBundle chunkBundle = getChunkBundle();
		if(chunkBundle == null){
			
			sagaPlayer.message(SagaMessages.noPermission(this));
			return;
			
		}
		
		Block targetBlock = event.getClickedBlock();
		if(targetBlock == null) return;
		Material targetMaterial = targetBlock.getType();
		
		// Permission:
		if(!chunkBundle.hasPermission(sagaPlayer, SettlementPermission.ACCESS_WAREHOUSE)){
			
			// Chest:
			if(targetMaterial.equals(Material.CHEST) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(SagaMessages.noPermission(this));
				
			}
			
			// Furnace:
			else if(targetMaterial.equals(Material.FURNACE) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(SagaMessages.noPermission(this));
				
			}

			// Burning furnace:
			else if(targetMaterial.equals(Material.BURNING_FURNACE) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(SagaMessages.noPermission(this));
				
			}

			// Dispenser:
			else if(targetMaterial.equals(Material.DISPENSER) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(SagaMessages.noPermission(this));
				
			}
			
			// Door:
			else if(targetMaterial.equals(Material.WOODEN_DOOR)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(SagaMessages.noPermission(this));
				
			}
			
			// Trap door:
			else if(targetMaterial.equals(Material.TRAP_DOOR)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(SagaMessages.noPermission(this));
				
			}
			
			// Enchantment table:
			else if(targetMaterial.equals(Material.ENCHANTMENT_TABLE) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(SagaMessages.noPermission(this));
				
			}
			
			// Brewing stand:
			else if(targetMaterial.equals(Material.BREWING_STAND) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(SagaMessages.noPermission(this));
				
			}

			// Brewing stand item:
			else if(targetMaterial.equals(Material.BREWING_STAND_ITEM) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(SagaMessages.noPermission(this));
				
			}

			// Fence gate:
			else if(targetMaterial.equals(Material.FENCE_GATE)){
				
				event.setUseItemInHand(Result.DENY);
				event.setUseInteractedBlock(Result.DENY);
				
				sagaPlayer.message(SagaMessages.noPermission(this));
				
			}
			
		}
		
		
	
	}
	
	
}
