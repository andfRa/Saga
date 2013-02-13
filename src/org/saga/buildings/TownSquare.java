package org.saga.buildings;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;
import org.saga.Clock;
import org.saga.Clock.SecondTicker;
import org.saga.SagaLogger;
import org.saga.config.FactionConfiguration;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.factions.Faction;
import org.saga.factions.FactionClaimManager;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.listeners.events.SagaEntityDamageEvent.PvPOverride;
import org.saga.messages.ClaimMessages;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Bundle;
import org.saga.settlements.BundleToggleable;
import org.saga.settlements.SagaChunk;
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

		
		if(!isEnabled()) return false;
		
		// Counting:
		count++;
		if(count > 9){
			count = 0;
		}
		
		// Players:
		ArrayList<SagaPlayer> sagaPlayers = getSagaChunk().getSagaPlayers();
		if(sagaPlayers.size() == 0) return false;

		// Bundles and factions:
		if(getSagaChunk() == null) return false;
		Bundle bundle = getChunkBundle();
		Integer bundleId = bundle.getId();
		
		Faction claimerFaction = FactionClaimManager.manager().getClaimerFaction(bundleId);
		Faction owningFaction = FactionClaimManager.manager().getOwningFaction(bundleId);
		
		// Unclaimable:
		if(bundle.isOptionEnabled(BundleToggleable.UNCLAIMABLE)) return true;
		
		// Progress:
		Double progress = FactionClaimManager.manager().getProgress(bundleId);
		Double nextProgress = progress;
		
		// Messaging:
		boolean message = false;
		if(count == 5) message = true;
		
		// Claiming:
		if(FactionClaimManager.manager().isClaiming(bundleId)){

			// Check claiming faction:
			if(!FactionClaimManager.manager().checkClaimer(bundleId, sagaPlayers)) return true;

			// Claim speed:
			Integer size = 0;
			if(bundle instanceof Settlement) size = ((Settlement) bundle).getSize();
			Double claimed = FactionConfiguration.config().getClaimSpeed(size) / 60;
			claimed*= FactionConfiguration.config().getMemberMultiplier(countMembers(sagaPlayers, claimerFaction));

			// Claim:
			nextProgress = progress + claimed;
			
			// Inform town square:
			if(message && nextProgress > 0){
				
				if(claimerFaction != null && owningFaction != null){
					
					getSagaChunk().broadcast(ClaimMessages.claimingTownSquare(bundle, claimerFaction, owningFaction, progress));
					
				}else if(claimerFaction != null){
					
					getSagaChunk().broadcast(ClaimMessages.claimingTownSquare(bundle, claimerFaction, progress));
					
				}
				
			}
			
			// Inform factions:
			if(checkOverstep(progress, nextProgress) && claimed > 0){
				
				if(claimerFaction != null && owningFaction != null){
					
					claimerFaction.information(ClaimMessages.claiming(bundle, claimerFaction, owningFaction, nextProgress));
					owningFaction.information(ClaimMessages.loosing(bundle, owningFaction, claimerFaction, nextProgress));
				
				}else if(claimerFaction != null){
					
					claimerFaction.information(ClaimMessages.claiming(bundle, claimerFaction, nextProgress));
					
				}
				
			}
			
			FactionClaimManager.manager().progressClaim(bundle, claimed);
			
		}
		
		// Initiating:
		else{
			
			// Claiming players:
			sagaPlayers = getSagaChunk().getSagaPlayers();
			
			// Available:
			if(!FactionClaimManager.manager().checkAvailable(bundleId, sagaPlayers)) return true;

			// Initiating faction:
			Faction initFaction = FactionClaimManager.getInitFacton(bundleId, sagaPlayers);
			if(initFaction == null) return true;
			
			// Claim limit:
			if(!initFaction.areClaimsAvailable()) return true;
			
			// Initiate:
			FactionClaimManager.manager().initiate(bundle, initFaction);
			
		}
		
		return (nextProgress < 1.0) && (nextProgress >= 0.0);
		
		
	}
	
	/**
	 * Informs that the chunk bundle is being unclaimed.
	 * 
	 */
	public void informUnclaim() {
		
		
		if(count != 5) return;
		if(getSagaChunk() == null) return;
		
		Bundle bundle = getChunkBundle();
		Integer bundleId = bundle.getId();
		
		Faction claimerFaction = FactionClaimManager.manager().getClaimerFaction(bundleId);
		Faction owningFaction = FactionClaimManager.manager().getOwningFaction(bundleId);
		
		Double progress = FactionClaimManager.manager().getProgress(bundleId);
		
		if(progress <= 0) return;
		
		if(claimerFaction != null && owningFaction != null){
			
			getSagaChunk().broadcast(ClaimMessages.unclaimingTownSquare(bundle, claimerFaction, owningFaction, progress));
			
		}else if(claimerFaction != null){
			
			getSagaChunk().broadcast(ClaimMessages.unclaimingTownSquare(bundle, claimerFaction, progress));
			
		}
		
		
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
		
		if(!originChun.isLoaded()){
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
		if(event.isCvP()){
			event.cancel();
		}
		
		else if(event.isPvP()){
			event.addPvpOverride(PvPOverride.SAFE_AREA_DENY);
		}
		
		// Allow PvP if being claimed:
		if(event.isFvF() && FactionClaimManager.manager().isClaiming(getChunkBundle().getId())){
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
		if(!Clock.clock().isSecondTicking(this)) Clock.clock().enableSecondTick(this);
	
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
	
	/**
	 * Counts the number of faction members.
	 * 
	 * @param sagaPlayers saga players
	 * @param faction faction
	 * @return number of members
	 */
	private static Integer countMembers(ArrayList<SagaPlayer> sagaPlayers, Faction faction) {

		
		Integer count = 0;
		
		for (SagaPlayer sagaPlayer : sagaPlayers) {
			if(!faction.isMember(sagaPlayer.getName())) count++;
		}
		
		return count;
		
		
	}
	
	
}
