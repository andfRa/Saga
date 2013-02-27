package org.saga.buildings;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;
import org.saga.Clock.SecondTicker;
import org.saga.SagaLogger;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.factions.SiegeManager;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.listeners.events.SagaEntityDamageEvent.PvPOverride;
import org.saga.player.SagaPlayer;
import org.saga.settlements.SagaChunk;

public class TownSquare extends Building implements SecondTicker{

	
	/**
	 * Random generator.
	 */
	transient private Random random;
	
	

	// Initialisation:
	/**
	 * Creates a building from the definition.
	 * 
	 * @param definition building definition
	 */
	public TownSquare(BuildingDefinition definition) {
		
		
		super(definition);
		
		random = new Random();
//		count = 0;
		
		
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
//		count = 0;
		
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
	
	
	// Timing:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.SecondTicker#clockSecondTick()
	 */
	@Override
	public boolean clockSecondTick() {
		
		return false;
		
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
		if(event.isFvF() && SiegeManager.manager().isSieged(getChunkBundle().getId())){
			event.addPvpOverride(PvPOverride.FACTION_CLAIMING_ALLOW);
		}
		
		
	}
	
	
}
