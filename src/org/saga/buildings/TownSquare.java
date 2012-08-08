package org.saga.buildings;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;
import org.saga.Clock;
import org.saga.Clock.SecondTicker;
import org.saga.SagaLogger;
import org.saga.chunks.ChunkBundle;
import org.saga.chunks.SagaChunk;
import org.saga.config.FactionConfiguration;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.factions.Faction;
import org.saga.factions.FactionClaimManager;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.listeners.events.SagaEntityDamageEvent.PvPOverride;
import org.saga.messages.ClaimMessages;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Settlement;

public class TownSquare extends Building implements SecondTicker{

	
	/**
	 * Random generator.
	 */
	transient private Random random;
	
	/**
	 * Counter for seconds.
	 */
	transient private Integer count;
	

	// Initialisation:
	/**
	 * Creates a building from the definition.
	 * 
	 * @param definition building definition
	 */
	public TownSquare(BuildingDefinition definition) {
		
		super(definition);
		
		random = new Random();
		count = 0;
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#completeExtended()
	 */
	@Override
	public boolean complete() throws InvalidBuildingException {
		

		boolean integrity = super.complete();
		
		// Transient:
		random = new Random();
		count = 0;
		
		return integrity;
		
		
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
	
	
	
	// Claiming:
	@Override
	public boolean clockSecondTick() {

		
		if(getSagaChunk() == null) return false;
		
		ArrayList<SagaPlayer> sagaPlayers = getSagaChunk().getSagaPlayers();
		ChunkBundle bundle = getChunkBundle();
		
		// No players:
		if(sagaPlayers.size() == 0) return false;
		
		Faction attackerFaction = FactionClaimManager.manager().getContesterFaction(bundle.getId());
		Faction defenderFaction = FactionClaimManager.manager().getOwningFaction(bundle.getId());
		
		// Progress:
		Double progress = FactionClaimManager.manager().getProgress(bundle.getId());
		if(progress > 0){

			if(count > 10) count = 0;
			
			// Contesting:
			if(FactionClaimManager.manager().checkContesting(bundle, sagaPlayers)){
				
				// Inform:
				if(count == 0) getSagaChunk().broadcast(ClaimMessages.contestingTownSquare(bundle, attackerFaction, defenderFaction, progress));
				count++;
				
				return true;
				
			}
			
			// Inform:
			if(count == 0){

				if(attackerFaction != null && defenderFaction != null){
					
					getSagaChunk().broadcast(ClaimMessages.claimingTownSquare(bundle, attackerFaction, defenderFaction, progress));
					
				}else if(attackerFaction != null){
					
					getSagaChunk().broadcast(ClaimMessages.claimingTownSquare(bundle, attackerFaction, progress));
					
				}
				
			}
			
			count++;
			
		}
		
		// Progress claim:
		if(FactionClaimManager.manager().checkClaiming(bundle, sagaPlayers)){
			
			Integer level = 0;
			if(bundle instanceof Settlement) level = ((Settlement) bundle).getLevel();
			
			Double claimed = FactionConfiguration.config().getClaimSpeed(level) / 60;

			FactionClaimManager.manager().progressClaim(bundle, claimed);
		
			// Inform factions:
			if(checkOverstep(progress, progress + claimed)){
				
				progress+= claimed;
				
				if(attackerFaction != null && defenderFaction != null){
					
					attackerFaction.information(ClaimMessages.claiming(bundle, attackerFaction, defenderFaction, progress));
					defenderFaction.information(ClaimMessages.loosing(bundle, defenderFaction, attackerFaction, progress));
				
				}else if(attackerFaction != null){
					
					attackerFaction.information(ClaimMessages.claiming(bundle, attackerFaction, progress));
					
				}
				
			}
			
		}
		
		// Initiate claiming:
		else if(FactionClaimManager.manager().checkInitiation(bundle, sagaPlayers)){

			Faction initFaction = FactionClaimManager.getInitFacton(bundle, sagaPlayers);
			if(initFaction == null) return true;
			
			FactionClaimManager.manager().initiate(bundle, initFaction);
			
		}
		
		return true;
		
		
	}
	
	
	
	// Utility:
	/**
	 * Gets the spawn location on this chunk.
	 * 
	 * @return spawn location, null if not found
	 */
	public Location getSpawnLocation() {

		
		SagaChunk sagaChunk = getSagaChunk();
		if(sagaChunk == null){
			return null;
		}
		
		// Displacement:
		double spreadRadius = 6;
		Double x = 2 * spreadRadius * (random.nextDouble() - 0.5);
		Double z = 2 * spreadRadius * (random.nextDouble() - 0.5);
		Vector displacement = new Vector(x.intValue(), 2, z.intValue());
		
		// Shifted location:
		Location spawnLocation = sagaChunk.getLocation(displacement);
		if(spawnLocation == null){
			return null;
		}
		
		if(spawnLocation.getY() < 10){
			SagaLogger.severe(this, spawnLocation + " is an invalid spawn location");
			return null;
		}
		
		return spawnLocation;
		
		
	}
	
	/**
	 * Prepares the chunk.
	 * 
	 */
	private void prepareChunk() {

		
		SagaChunk originChun = getSagaChunk();
		
		if(originChun == null){
			SagaLogger.severe(this, "failed to retrieve origin chunk");
			return;
		}
		
		if(!originChun.isChunkLoaded()){
			originChun.loadChunk();
		}
		
		
	}
	
	
	
	// Events:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#memberRespawnEvent(org.saga.SagaPlayer, org.bukkit.event.player.PlayerRespawnEvent)
	 */
	@Override
	public void onMemberRespawn(SagaPlayer sagaPlayer, PlayerRespawnEvent event) {

		
		// Location chunk:
		SagaChunk locationChunk = getSagaChunk();
		if(locationChunk == null){
			SagaLogger.severe(this, "can't continue with memberRespawnEvent, because the location chunk isn't set.");
			return;
		}
		
//		// Cool down;
//		if(isOnCooldown){
//			sagaPlayer.sendMessage(BuildingMessages.cooldown(this, cooldownLeft));
//			return;
//		}else{
//			startCooldown();
//		}

		// Prepare chunk:
		prepareChunk();
		
		Location spawnLocation = getSpawnLocation();
		
		if(spawnLocation == null){
			SagaLogger.severe(this, "can't continue with onMemberRespawnEvent, because the location can't be retrieved");
			sagaPlayer.error("failed to respawn at " + getDisplayName());
			return;
		}
		
		// Respawn:
		event.setRespawnLocation(spawnLocation);
		
	
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerDamagedByPlayer(org.bukkit.event.entity.EntityDamageByEntityEvent, org.saga.SagaPlayer, org.saga.SagaPlayer)
	 */
	@Override
	public void onEntityDamage(SagaEntityDamageEvent event){

		
		// Deny damage:
		if(event.isCreatureAttackPlayer()){
			event.cancel();
		}
		
		else if(event.isPlayerAttackPlayer()){
			event.addPvpOverride(PvPOverride.SAFE_AREA_DENY);
		}
		
		// Allow PvP if being claimed:
		if(event.isFactionAttackFaction() && FactionClaimManager.manager().isContested(getChunkBundle().getId())){
			event.addPvpOverride(PvPOverride.FACTION_CLAIMING_ALLOW);
		}
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerEnter(org.saga.player.SagaPlayer, org.saga.buildings.Building)
	 */
	@Override
	public void onPlayerEnter(SagaPlayer sagaPlayer, Building last) {
		
		// Enable clock:
		if(!Clock.clock().isSecondTicking(this)) Clock.clock().registerSecondTick(this);
	
	}
	
	
	
	// Utility:
	/**
	 * Checks if the progress over stepped a round value.
	 * 
	 * @param prevVal previous value
	 * @param nextVal next value
	 * @return true if over stepped
	 */
	public static boolean checkOverstep(Double prevVal, Double nextVal) {

		
		for (double i = 0; i < 1.0; i+=0.05) {
			
			if(prevVal < i && nextVal >= i){
				return true;
			}
			
		}
		
		return false;
		
		
	}
	
	
	
}
