package org.saga.buildings;

import java.util.HashSet;
import java.util.Hashtable;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;
import org.saga.Clock.SecondTicker;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.exceptions.InvalidLocationException;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.listeners.events.SagaEntityDamageEvent.PvPOverride;
import org.saga.player.SagaPlayer;
import org.saga.settlements.SagaChunk;
import org.saga.utility.SagaLocation;

public class TownSquare extends Building implements SecondTicker{
	
	
	/**
	 * Maximum number of spawn blocks.
	 */
	transient private final int SPAWN_BLOCK_LIMIT = 64;
	
	/**
	 * Damage immunity after respawn.
	 */
	transient private final String RESPAWN_DAMAGE_IMMUNITY_TIME = "respawn immunity";
	
	
	/**
	 * Immunity times.
	 */
	transient private Hashtable<String, Long> protectionTimes;
	
	
	/**
	 * Spawn location.
	 */
	private SagaLocation spawn = null;
	
	
	
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
		
		super.complete();
		
		if(spawn != null)
			try {
				spawn.complete();
			}
			catch (InvalidLocationException e) {
				SagaLogger.severe(this, "invalid spawn location: " + spawn);
				spawn = null;
			}
		
		// Transient:
		protectionTimes = new Hashtable<String, Long>();
		
		return true;
		
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
		
		protectionTimes.put(sagaPlayer.getName(), current + addition);
		
	}
	
	
	/**
	 * Gets the spawn location.
	 * 
	 * @return the spawnLocation, null if none
	 */
	public SagaLocation getSpawn() {
		return spawn;
	}

	
	/**
	 * Sets the spawn location.
	 * 
	 * @param spawn the spawnLocation to set, null if none
	 */
	public void setSpawn(SagaLocation spawn) {
		this.spawn = spawn;
	}
	
	
	/**
	 * Finds the spawn location.
	 * 
	 * @return finds the spawn location
	 */
	public Location findSpawnLocation() {

		Location location = findLeveledSpawnLocation();
		if(location != null) return location;
		
		location = findTopSpawnLocation();
		return location;
		
	}
	

	/**
	 * Finds the top spawn location on this chunk.
	 * 
	 * @return top spawn location, null if not found
	 */
	public Location findTopSpawnLocation() {

		
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
	 * Finds the levelled spawn location on this chunk.
	 * 
	 * @return levelled spawn location, null if not found or not possible
	 */
	public Location findLeveledSpawnLocation() {
		
		if(spawn == null) return null;
		
		SagaChunk sagaChunk = getSagaChunk();
		if(sagaChunk == null) return null;
		
		// Possible spawn blocks:
		HashSet<Block> blocks = new HashSet<Block>();
		fillAdjecentValid(spawn.getLocation().getBlock(), blocks);
		if(blocks.size() == 0) return null;
		
		int next = Saga.RANDOM.nextInt(blocks.size());
		for (Block block : blocks) {
			if(next == 0) return block.getLocation().add(0.5, 0.5, 0.5);
			next--;
		}
		
		return null;
		
	}
	
	/**
	 * Fills the blocks set with all possible spawn location blocks.
	 * 
	 * @param anchor anchor block
	 * @param blocks all blocks
	 * @return true if the anchor block was valid
	 */
	public boolean fillAdjecentValid(Block anchor, HashSet<Block> blocks) {
		
		Block up = anchor.getRelative(BlockFace.UP);
		Block same = anchor.getRelative(BlockFace.SELF);
		Block down = anchor.getRelative(BlockFace.DOWN);
		
		// Limit:
		if(blocks.size() > SPAWN_BLOCK_LIMIT) return false;
		
		// Add block:
		if(blocks.contains(anchor)) return false;
		if(isValidSpawnBlock(anchor)){
			blocks.add(anchor);
		}else{
			return false;
		}
		
		// Self:
		if(same.getRelative(BlockFace.SELF).getChunk() == anchor.getChunk())
			if(!fillAdjecentValid(same.getRelative(BlockFace.SELF), blocks))
				if(!fillAdjecentValid(down.getRelative(BlockFace.SELF), blocks))
					fillAdjecentValid(up.getRelative(BlockFace.SELF), blocks);
		
		// North:
		if(same.getRelative(BlockFace.NORTH).getChunk() == anchor.getChunk())
			if(!fillAdjecentValid(same.getRelative(BlockFace.NORTH), blocks))
				if(!fillAdjecentValid(down.getRelative(BlockFace.NORTH), blocks))
					fillAdjecentValid(up.getRelative(BlockFace.NORTH), blocks);
		
		// East:
		if(same.getRelative(BlockFace.EAST).getChunk() == anchor.getChunk())
			if(!fillAdjecentValid(same.getRelative(BlockFace.EAST), blocks))
				if(!fillAdjecentValid(down.getRelative(BlockFace.EAST), blocks))
					fillAdjecentValid(up.getRelative(BlockFace.EAST), blocks);
		
		// South:
		if(same.getRelative(BlockFace.SOUTH).getChunk() == anchor.getChunk())
			if(!fillAdjecentValid(same.getRelative(BlockFace.SOUTH), blocks))
				if(!fillAdjecentValid(down.getRelative(BlockFace.SOUTH), blocks))
					fillAdjecentValid(up.getRelative(BlockFace.SOUTH), blocks);
		
		// West:
		if(same.getRelative(BlockFace.WEST).getChunk() == anchor.getChunk())
			if(!fillAdjecentValid(same.getRelative(BlockFace.WEST), blocks))
				if(!fillAdjecentValid(down.getRelative(BlockFace.WEST), blocks))
					fillAdjecentValid(up.getRelative(BlockFace.WEST), blocks);
		
		return true;
		
	}

	
	/**
	 * Checks if the block is valid for spawning.
	 * 
	 * @param block block
	 * @return true if valid
	 */
	public boolean isValidSpawnBlock(Block block) {
		
		if(!block.getRelative(BlockFace.DOWN).getType().isSolid()) return false;
		
		if(block.getType().isSolid()) return false;
		if(block.getRelative(BlockFace.UP).getType().isSolid()) return false;
		if(block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType().isSolid()) return false;
		
		return true;
		
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
		Location spawnLocation = findSpawnLocation();
		
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
		
		
		SagaPlayer attackerPlayer = null;
		SagaPlayer defenderPlayer = null;
		
		if(event.sagaAttacker instanceof SagaPlayer) attackerPlayer = (SagaPlayer)event.sagaAttacker;
		if(event.sagaDefender instanceof SagaPlayer) defenderPlayer = (SagaPlayer)event.sagaDefender;
		
		
		if(event.sagaAttacker != null && event.sagaDefender != null){
			
			Long immunity = protectionTimes.get(attackerPlayer);
			if(immunity != null && immunity >= System.currentTimeMillis()){
				event.addPvpOverride(PvPOverride.RESPAWN_DENY);
				return;
			}
			
		}
		
		if(event.sagaDefender != null){

			Long immunity = protectionTimes.get(defenderPlayer);
			if(immunity != null && immunity >= System.currentTimeMillis()){
				event.addPvpOverride(PvPOverride.RESPAWN_DENY);
				return;
			}
			
		}
		
		
	}
	
	
}
