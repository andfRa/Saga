package org.saga.buildings;

import java.util.Hashtable;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;
import org.saga.Clock.SecondTicker;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.listeners.events.SagaEntityDamageEvent.PvPOverride;
import org.saga.player.SagaPlayer;
import org.saga.settlements.SagaChunk;

public class TownSquare extends Building implements SecondTicker{
	
	
	/**
	 * Damage immunity after respawn.
	 */
	transient private final String RESPAWN_DAMAGE_IMMUNITY_TIME = "respawn immunity";
	
	
	/**
	 * Immunity times.
	 */
	transient private Hashtable<String, Long> protectionTimes;
	
	
	
	// Initialisation:
	/**
	 * Creates a building from the definition.
	 * 
	 * @param definition building definition
	 */
	public TownSquare(BuildingDefinition definition) {
		
		super(definition);
		
		protectionTimes = new Hashtable<String, Long>();
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#completeExtended()
	 */
	@Override
	public boolean complete() throws InvalidBuildingException {
		
		boolean integrity = super.complete();
		
		protectionTimes = new Hashtable<String, Long>();
		
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
	
	
	
	// Spawning:
	/**
	 * Updates spawn camping protection.
	 * 
	 * @param sagaPlayer Saga player
	 */
	public void updateSpawningProtect(SagaPlayer sagaPlayer) {
		
		long current = System.currentTimeMillis();
		
		// Previous time:
		Long previous = protectionTimes.get(sagaPlayer.getName());
		if(previous == null) previous = current - getDefinition().getFunction(RESPAWN_DAMAGE_IMMUNITY_TIME).getXMax();
		
		// Still active:
		if(previous > current) return;
		
		long passed = current - previous;
		long addition = getDefinition().getFunction(RESPAWN_DAMAGE_IMMUNITY_TIME).longValue(passed / 1000.0) * 1000;
		
		System.out.println("passed=" + passed/1000.0);
		System.out.println("addition=" + addition/1000.0);
		
		protectionTimes.put(sagaPlayer.getName(), current + addition);
		
	}
	
	
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
		Double x = 2 * spreadRadius * (Saga.RANDOM.nextDouble() - 0.5);
		Double z = 2 * spreadRadius * (Saga.RANDOM.nextDouble() - 0.5);
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
		
		// Update spawning protection:
		updateSpawningProtect(sagaPlayer);
		
		// Prepare chunk:
		prepareChunk();
		
		// Respawn:
		Location spawnLocation = getSpawnLocation();
		
		if(spawnLocation == null){
			SagaLogger.severe(this, "can't continue with onMemberRespawnEvent, because the location can't be retrieved");
			sagaPlayer.error("failed to respawn at " + getDisplayName());
			return;
		}
		
		event.setRespawnLocation(spawnLocation);
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerDamagedByPlayer(org.bukkit.event.entity.EntityDamageByEntityEvent, org.saga.SagaPlayer, org.saga.SagaPlayer)
	 */
	@Override
	public void onEntityDamage(SagaEntityDamageEvent event){
		
		
		if(event.attackerPlayer != null && event.defenderPlayer != null){
			
			Long immunity = protectionTimes.get(event.attackerPlayer.getName());
			if(immunity != null && immunity >= System.currentTimeMillis()){
				event.addPvpOverride(PvPOverride.RESPAWN_DENY);
				return;
			}
			
		}
		
		if(event.defenderPlayer != null){

			Long immunity = protectionTimes.get(event.defenderPlayer.getName());
			if(immunity != null && immunity >= System.currentTimeMillis()){
				event.addPvpOverride(PvPOverride.RESPAWN_DENY);
				return;
			}
			
		}
		
		
	}
	
	
}
