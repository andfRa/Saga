package org.saga.buildings;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.SagaChunk;
import org.saga.listeners.events.SagaPvpEvent;
import org.saga.listeners.events.SagaPvpEvent.PvpDenyReason;
import org.saga.messages.ChunkGroupMessages;
import org.saga.player.SagaPlayer;

public class TownHall extends Building{

	/**
	 * Blacklist sign.
	 */
	transient public static String BLACKLIST_SIGN = "=[blacklist]=";
	
	
	// Initialization:
	/**
	 * Initializes
	 * 
	 * @param pointCost point cost
	 * @param moneyCost money cost
	 * @param proficiencies proficiencies
	 */
	private TownHall(String name) {
		
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
		
		return integrity;
		
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#blueprint()
	 */
	@Override
	public Building blueprint() {
		return new TownHall("");
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#enable()
	 */
	@Override
	public void enable() {
		
		super.enable();
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#disable()
	 */
	@Override
	public void disable() {

		super.disable();

	}

	
	// Events:
	@Override
	public void onPlayerSagaChunkChange(SagaPlayer sagaPlayer, SagaChunk fromChunk, SagaChunk toChunk, Location fromLocation, Location toLocation, PlayerMoveEvent event) {

//		
//		// Deny entrance:
//		if(fromChunk == null && toChunk != null){
//			
//			if(isBlacklisted(sagaPlayer.getName())){
//				
//				// Cancel move:
//				event.setCancelled(true);
//				
//				// Inform:
//				if(!blackListMessageCooldown.isOnCooldown(sagaPlayer.getName())){
//					sagaPlayer.sendMessage(notAllowed(toChunk.getChunkGroup()));
//					blackListMessageCooldown.addCooldown(sagaPlayer.getName(), 10);
//				}
//				
//				
//			}
//			
//		}
		
		super.onPlayerSagaChunkChange(sagaPlayer, fromChunk, toChunk, fromLocation, toLocation, event);
		
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerDamagedByPlayer(org.bukkit.event.entity.EntityDamageByEntityEvent, org.saga.SagaPlayer, org.saga.SagaPlayer)
	 */
	@Override
	public void onPvP(SagaPvpEvent event){
		
		// Deny pvp:
		event.setDenyReason(PvpDenyReason.SAFE_AREA);
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerDamagedByCreature(org.bukkit.event.entity.EntityDamageByEntityEvent, org.bukkit.entity.Creature, org.saga.SagaPlayer)
	 */
	@Override
	public void onPlayerDamagedByCreature(EntityDamageByEntityEvent event, Creature damager, SagaPlayer damaged) {

		// Disable cvp:
		event.setCancelled(true);
		
	}

	// Messages:
	

	public static String alreadyBlacklisted(String name) {
		return ChunkGroupMessages.negative + name + " is already blacklisted.";
	}
	
	public static String alreadyBlacklisted(ArrayList<String> names){
		
		
		String rString = "";
		
		for (int i = 0; i < names.size(); i++) {
			
			if(i != 0) rString += ", ";
			
			rString += names.get(i);
			
		}
		
		if(names.size() == 0){
			return ChunkGroupMessages.negative + "Player is already blacklisted.";
		}else if(names.size() == 1){
			return ChunkGroupMessages.negative + rString + " is already blacklisted.";
		}else{
			return ChunkGroupMessages.negative + rString + "  are already blacklisted.";
		}
		
		
	}
	
	public static String notAllowed(ChunkGroup chunkGroup) {
		return ChunkGroupMessages.negative + "You are not allowed in " + chunkGroup.getName() + ".";
	}


	// Messages:
	
	
	
	
}
