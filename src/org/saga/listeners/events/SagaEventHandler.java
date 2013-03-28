package org.saga.listeners.events;

import org.bukkit.block.Block;
import org.saga.config.FactionConfiguration;
import org.saga.dependencies.PermissionsDependency;
import org.saga.listeners.events.SagaBuildEvent.BuildOverride;
import org.saga.listeners.events.SagaEntityDamageEvent.PvPOverride;
import org.saga.messages.PlayerMessages;
import org.saga.messages.SettlementMessages;
import org.saga.player.SagaPlayer;
import org.saga.settlements.SagaChunk;

public class SagaEventHandler {

	
	public static void handleBuild(SagaBuildEvent event) {

		
    	SagaChunk sagaChunk = event.getSagaChunk();
    	SagaPlayer sagaPlayer = event.getSagaPlayer();
    	
    	// Forward to Saga chunk:
		if(sagaChunk != null) sagaChunk.onBuild(event);
    	
    	// Wilderness:
    	else if(!PermissionsDependency.hasPermission(sagaPlayer, PermissionsDependency.WILDERNESS_BUILD_PERMISSION)){
    		
    		event.addBuildOverride(BuildOverride.WILDERNESS_DENY);
    		
    		Block block = event.getBlock();
    		if(PermissionsDependency.hasPermission(sagaPlayer, PermissionsDependency.WILDERNESS_BUILD_PERMISSION + "." + block.getTypeId())) event.addBuildOverride(BuildOverride.WILDERNESS_SPECIFIC_BLOCK_ALLOW);
    		
    	}
		
		// Conclude and inform:
		if(!event.getbuildOverride().isAllow()){
			
			event.cancel();
			sagaPlayer.message(SettlementMessages.buildOverride(event.getbuildOverride()));
			
			return;
			
		}
    	
    	
	}
	
	public static void handlePvP(SagaEntityDamageEvent event) {


		// Forward to Saga chunks:
		SagaChunk attackerChunk = event.attackerChunk;
		SagaChunk defenderChunk = event.defenderChunk;
		if(attackerChunk != null) attackerChunk.onEntityDamage(event);
		if(defenderChunk != null && attackerChunk != defenderChunk) defenderChunk.onEntityDamage(event);
		
		// PvP event:
		if(event.isPvP()){
			
			// Damaged self:
			if(event.sagaDefender == event.sagaAttacker) event.addPvpOverride(PvPOverride.SELF_ALLOW);

			// Only faction versus faction:
			if(FactionConfiguration.config().factionOnlyPvp && !event.isFvF()) event.addPvpOverride(PvPOverride.FACTION_ONLY_PVP_DENY);
			
			// Forward to Saga factions:
			if(event.getAttackerFaction() != null) event.getAttackerFaction().onPvPAttack(event);
			if(event.getDefenderFaction() != null) event.getDefenderFaction().onPvPDefend(event);

			// PvP override:
			if(!event.getOverride().isAllow()){
				event.sagaAttacker.message(PlayerMessages.pvpOverride(event));
				event.cancel();
				return;
			}
			
		}


	}
	
	
}
