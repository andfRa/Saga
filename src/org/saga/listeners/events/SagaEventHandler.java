package org.saga.listeners.events;

import org.saga.config.FactionConfiguration;
import org.saga.dependencies.PermissionsDependency;
import org.saga.listeners.events.SagaBuildEvent.BuildOverride;
import org.saga.listeners.events.SagaEntityDamageEvent.PvPOverride;
import org.saga.messages.PlayerMessages;
import org.saga.messages.SettlementMessages;
import org.saga.player.SagaPlayer;
import org.saga.settlements.SagaChunk;

public class SagaEventHandler {

	
	public static void onBuild(SagaBuildEvent event) {

		
    	SagaChunk sagaChunk = event.getSagaChunk();
    	SagaPlayer sagaPlayer = event.getSagaPlayer();
    	
    	// Forward to Saga chunk:
		if(sagaChunk != null){
			
			sagaChunk.onBuild(event);
			
		}
    	
    	// Wilderness:
    	else if(!PermissionsDependency.hasPermission(sagaPlayer, PermissionsDependency.WILDERNESS_BUILD_PERMISSION)){
    		
    		event.addBuildOverride(BuildOverride.WILDERNESS_DENY);
    		
    	}
		
		if(!event.getbuildOverride().isAllow()){
			
			event.cancel();
			sagaPlayer.message(SettlementMessages.buildOverride(event.getbuildOverride()));
			return;
			
		}
    	
    	
	}
	
	public static void onEntityDamage(SagaEntityDamageEvent event) {


		// PvP event:
		if(event.isPvP()){
			
			// Damaged self:
			if(event.defenderPlayer == event.attackerPlayer) event.addPvpOverride(PvPOverride.SELF_ALLOW);

			// Only faction versus faction:
			if(FactionConfiguration.config().factionOnlyPvp && !event.isFvF()) event.addPvpOverride(PvPOverride.FACTION_ONLY_PVP_DENY);
			
			// Forward to Saga factions:
			if(event.attackerPlayer.getFaction() != null) event.attackerPlayer.getFaction().onPvPAttack(event);
			if(event.defenderPlayer.getFaction() != null) event.defenderPlayer.getFaction().onPvPDefend(event);
			
			if(!event.getOverride().isAllow()){
				
				event.attackerPlayer.message(PlayerMessages.pvpOverride(event));
				event.cancel();
				return;
				
			}
			
		}

		// Forward to Saga chunks:
		SagaChunk attackerChunk = event.attackerChunk;
		SagaChunk defenderChunk = event.defenderChunk;
		if(attackerChunk != null) attackerChunk.onEntityDamage(event);
		if(defenderChunk != null && attackerChunk != defenderChunk) defenderChunk.onEntityDamage(event);
		

	}
	
	
}
