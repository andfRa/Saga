package org.saga.listeners.events;

import org.saga.chunkGroups.ChunkGroupManager;
import org.saga.chunkGroups.SagaChunk;
import org.saga.config.FactionConfiguration;
import org.saga.listeners.events.SagaPvpEvent.PvpAllowReason;
import org.saga.listeners.events.SagaPvpEvent.PvpDenyReason;
import org.saga.messages.PlayerMessages;
import org.saga.player.SagaPlayer;

public class SagaEventHandler {


	// Player versus player:
	/**
	 * Handles saga pvp event.
	 * 
	 * @param event event
	 * @return true if allowed
	 */
	public static boolean handlePvp(SagaPvpEvent event){
		
		
		
		SagaPlayer attacker = event.getAttacker();
		SagaPlayer defender = event.getDefender();
		
		// Faction versus faction only:
		if(FactionConfiguration.config().factionOnlyPvp){
			
			if(attacker.getRegisteredFaction() == null || !attacker.getRegisteredFaction().isFormed()){
				event.setDenyReason(PvpDenyReason.ATTACKER_NO_FACTION);
			}
			else if(defender.getRegisteredFaction() == null || !defender.getRegisteredFaction().isFormed()){
				event.setDenyReason(PvpDenyReason.DEFENDER_NO_FACTION);
			}
			
		}

		// Attackers faction:
		if(attacker.getRegisteredFaction() != null){
			attacker.getRegisteredFaction().onMemberAttack(event);
		}
		
		// Defenders faction:
		if(defender.getRegisteredFaction() != null){
			defender.getRegisteredFaction().onMemberDefend(event);
		}
		
		// Send event to attacker chunk:
		SagaChunk attackerChunk = ChunkGroupManager.manager().getSagaChunk(attacker.getLocation());
		if(attackerChunk != null) attackerChunk.onPvP(event);

		// Forward to defender chunk:
		SagaChunk defenderChunk = ChunkGroupManager.manager().getSagaChunk(defender.getLocation());
		if(defenderChunk != null && attackerChunk != defenderChunk) defenderChunk.onPvP(event);
		
		// Allow overrule:
		if(event.getAllowReason() != PvpAllowReason.NONE) return true;
		
		// Deny:
		if(event.getDenyReason() != PvpDenyReason.NONE){
			
			event.getWrappedEvent().setCancelled(true);
			
			// Inform:
			event.getAttacker().message(PlayerMessages.pvpDenied(event));
			
			return false;
			
		}
		
		return true;
		
		
	}
	
	
}
