package org.saga.listeners.events;

import org.saga.chunks.SagaChunk;
import org.saga.config.FactionConfiguration;
import org.saga.dependencies.PermissionsManager;
import org.saga.listeners.events.SagaBuildEvent.BuildOverride;
import org.saga.listeners.events.SagaEntityDamageEvent.PvPOverride;
import org.saga.messages.SettlementMessages;
import org.saga.messages.PlayerMessages;
import org.saga.player.SagaPlayer;

public class SagaEventHandler {

	
	public static void onBuild(SagaBuildEvent event) {

		
    	SagaChunk sagaChunk = event.getSagaChunk();
    	SagaPlayer sagaPlayer = event.getSagaPlayer();
    	
    	// Forward to Saga chunk:
		if(sagaChunk != null){
			
			sagaChunk.onBuild(event);
			
		}
    	
    	// Wilderness:
    	else if(!PermissionsManager.hasPermission(sagaPlayer, PermissionsManager.WILDERNESS_BUILD_PERMISSION)){
    		
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
		if(event.isPlayerAttackPlayer()){
			
			// Forward to Saga chunks:
			SagaChunk attackerChunk = event.getAttackerChunk();
			SagaChunk defenderChunk = event.getDefenderChunk();
			if(attackerChunk != null) attackerChunk.onEntityDamage(event);
			if(defenderChunk != null && attackerChunk != defenderChunk) defenderChunk.onEntityDamage(event);
			
			// Only faction versus faction:
			if(FactionConfiguration.config().factionOnlyPvp && !event.isFactionAttacksFaction()) event.addPvpOverride(PvPOverride.FACTION_ONLY_PVP_DENY);
			
			// Forward to Saga factions:
			if(event.getAttackerPlayer().getFaction() != null) event.getAttackerPlayer().getFaction().onAttack(event);
			if(event.getDefenderPlayer().getFaction() != null) event.getDefenderPlayer().getFaction().onDefend(event);
			
			if(!event.getOverride().isAllow()){
				
				event.getAttackerPlayer().message(PlayerMessages.pvpOverride(event));
				event.cancel();
				return;
				
			}
			
		}
		

	}
	
	
}
