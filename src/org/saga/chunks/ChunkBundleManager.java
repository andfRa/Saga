package org.saga.chunks;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.saga.SagaLogger;
import org.saga.chunks.SagaChunk.ChunkSide;
import org.saga.factions.Faction;
import org.saga.player.SagaPlayer;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;

public class ChunkBundleManager {


	/**
	 * Instance
	 */
	private static ChunkBundleManager instance;
	
	/**
	 * Gets manager.
	 * 
	 * @return manager
	 */
	public static ChunkBundleManager manager() {
		return instance;
	}
	
	
	/**
	 * Registered groups.
	 */
	transient  Hashtable<Integer, ChunkBundle> registeredGroups = new Hashtable<Integer, ChunkBundle>();
	
	/**
	 * All saga chunks.
	 */
	transient private Hashtable<String, Hashtable<Integer, Hashtable<Integer, SagaChunk>>> sagaChunks = new Hashtable<String, Hashtable<Integer,Hashtable<Integer,SagaChunk>>>();

	
	// Player load unload:
	/**
	 * Registers a faction.
	 * 
	 * @param faction saga faction
	 */
	public void factionLoaded(Faction faction) {

////		
////		// Get all chunk group IDs:
////		ArrayList<Integer> chunkGroupIds = sagaFaction.getChunkGroupIds();
////		
//		// Register all chunk groups:
//		for (Integer groupId : chunkGroupIds) {
//			
//			// Retrieve the chunk groups:
//			ChunkBundle chunkGroup = registeredGroups.get(groupId);
//			
//			// No longer exists:
//			if(chunkGroup == null){
//				Saga.severe(this, a,"ChunkGroupManager could not register " + groupId + " chunk group for " + sagaFaction + " faction, because the chunk group was not found. Ignoring request.");
//				continue;
//			}
//			
//			if(!chunkGroup.hasFaction(sagaFaction.getId())){
//				Saga.severe(this, a,"ChunkGroupManager could not register " + groupId + " chunk group for " + sagaFaction + " faction, because the chunk group doesn't have the faction on its list. Ignoring request.");
//				continue;
//			}
//			
//			// Register faction:
////			chunkGroup.(chunkGroup, sagaFaction);
//
//		}
//		
		
	}
	
	/**
	 * Unregisters a faction.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void factionUnloaded(Faction faction) {

//
//		// Get all chunk group IDs:
//		ArrayList<Integer> chunkGroupIds = sagaFaction.getChunkGroupIds();
//		
//		// Register all chunk groups:
//		for (Integer groupId : chunkGroupIds) {
//			
//			// Retrieve the chunk groups:
//			ChunkBundle chunkGroup = registeredGroups.get(groupId);
//			
//			// No longer exists:
//			if(chunkGroup == null){
//				Saga.severe(this, a,"ChunkGroupManager could not unregister " + groupId + " chunk group for " + sagaFaction + " faction, because the chunk group was not found. Ignoring request.");
//				continue;
//			}
//			
//			if(!chunkGroup.hasFaction(sagaFaction.getId())){
//				Saga.severe(this, a,"ChunkGroupManager could not unregister " + groupId + " chunk group for " + sagaFaction + " faction, because the chunk group doesn't have the faction on its list. Ignoring request.");
//				continue;
//			}
//			
//			// Unregister faction:
////			unregisterFaction(chunkGroup, sagaFaction);
//			
//		}
//		
		
	}
	
	/**
	 * Registers a player.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void playerLoaded(SagaPlayer sagaPlayer) {


		// Get all chunk group IDs:
		Integer groupId = sagaPlayer.getChunkGroupId();
		
		// Stop if invalid chunk group:
		if(groupId <= 0){
			return;
		}

		// Retrieve the chunk group:
		ChunkBundle chunkBundle = registeredGroups.get(groupId);
		
		// No longer exists:
		if(chunkBundle == null){
			SagaLogger.severe(getClass(), "failed to register " + groupId + " chunk group for " + sagaPlayer + " player, because the chunk group was not found");
			return;
		}
		
		// Not on the list:
		if(!chunkBundle.hasMember(sagaPlayer.getName())){
			SagaLogger.severe(getClass(), "chunkGroupManager could not register " + groupId + " chunk group for " + sagaPlayer + " player, because the chunk group doesn't have the player on its list");
			sagaPlayer.removeChunkGroupId(sagaPlayer.getChunkGroupId());
			return;
		}
		
		// Register player:
		chunkBundle.registerPlayer(sagaPlayer);
		
		
	}
	
	/**
	 * Unregisters a player.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void playerUnloaded(SagaPlayer sagaPlayer) {


		// Get all chunk group IDs:
		Integer groupId = sagaPlayer.getChunkGroupId();

		// Stop if invalid chunk group:
		if(groupId <= 0){
			return;
		}
		
		// Retrieve the chunk group:
		ChunkBundle chunkBundle = registeredGroups.get(groupId);
		
		// No longer exists:
		if(chunkBundle == null){
			SagaLogger.severe(this, "could not unregister " + groupId + " chunk group for " + sagaPlayer + " player, because the chunk group was not found");
			return;
		}
		
		if(!chunkBundle.hasMember(sagaPlayer.getName())){
			SagaLogger.severe(this, "could not unregister " + groupId + " chunk group for " + sagaPlayer + " player, because the chunk group doesn't have the player on its list");
			return;
		}
		
		// Unregister player:
		chunkBundle.unregisterPlayer(sagaPlayer);

		
	}


	// Chunk group interaction:
	/**
	 * Gets the chunk groups associated with the player.
	 * 
	 * @param chunkGroupId group ID
	 * @return chunk group. null if not found
	 */
	public ChunkBundle getChunkBundle(Integer chunkGroupId) {

		
		return registeredGroups.get(chunkGroupId);
		
		
	}

	/**
	 * Gets a saga chunk.
	 * 
	 * @param location location
	 * @return saga chunk. null if not found
	 */
	public SagaChunk getSagaChunk(Location location) {

		return getSagaChunk(location.getWorld().getChunkAt(location));
		
	}
	
	/**
	 * Gets a saga chunk.
	 * 
	 * @param chunk location chunk
	 * @return saga chunk. null if not found
	 */
	public SagaChunk getSagaChunk(Chunk chunk) {

		
		Hashtable<Integer, Hashtable<Integer, SagaChunk>> world = sagaChunks.get(chunk.getWorld().getName());
		if(world == null){
			return null;
		}
		
		Hashtable<Integer, SagaChunk> x = world.get(chunk.getX());
		if(x == null){
			return null;
		}
		
		return x.get(chunk.getZ());
		
		
	}
	
	/**
	 * Gets a saga chunk.
	 * 
	 * @param worldName world name
	 * @param x chunk x
	 * @param z chunk z
	 * @return saga chunk. null if not found
	 */
	public SagaChunk getSagaChunk(String worldName, int x, int z) {

		
		Hashtable<Integer, Hashtable<Integer, SagaChunk>> tworld = sagaChunks.get(worldName);
		if(tworld == null){
			return null;
		}
		
		Hashtable<Integer, SagaChunk> tX = tworld.get(x);
		if(tX == null){
			return null;
		}
		
		return tX.get(z);
		
		
	}
	
	/**
	 * Gets the adjacent chunk.
	 * 
	 * @param chunkSide chunk side
	 * @param bukkitChunk bukkit chunk
	 * @return saga chunk. null if not found
	 */
	public SagaChunk getAdjacentSagaChunk(ChunkSide chunkSide, Chunk bukkitChunk) {

		
		String worldName = bukkitChunk.getWorld().getName();
	   	int x = bukkitChunk.getX();
	   	int z = bukkitChunk.getZ();
		
		switch (chunkSide) {
		case FRONT: 
			return ChunkBundleManager.manager().getSagaChunk(worldName, x - 1, z);
		case LEFT:
			return ChunkBundleManager.manager().getSagaChunk(worldName, x, z + 1);
		case BACK:
			return ChunkBundleManager.manager().getSagaChunk(worldName, x +1 , z);
		case RIGHT:
			return ChunkBundleManager.manager().getSagaChunk(worldName, x, z - 1);	
		default:
			return null;
		}
		
		
	}
	
	/**
	 * Gets chunk group with the given name
	 * 
	 * @param name name
	 * @return chunk group. null if not found
	 */
	public ChunkBundle getChunkBundleWithName(String name) {

		
        Iterator<Integer> i = registeredGroups.keySet().iterator();

        while ( i.hasNext() ) {

            Integer id = i.next();
            ChunkBundle settlement = registeredGroups.get(id);
            if ( settlement.getName().equalsIgnoreCase(name) ) {
                return settlement;
            }

        }

        return null;
        
        
    }
	
	
	// Chunk updates for SagaChunkGroup:
	/**
	 * Removes a chunk.
	 * 
	 * @param sagaChunk saga chunk
	 */
	void removeChunk(SagaChunk sagaChunk) {

		
		Hashtable<Integer, Hashtable<Integer, SagaChunk>> world = sagaChunks.get(sagaChunk.getWorldName());
		
		if(world == null){
			SagaLogger.severe(this, "tried to remove a non-existan " + sagaChunk + " chunk to group manager chunk shortcut");
			return;
		}
		
		Hashtable<Integer, SagaChunk> x = world.get(sagaChunk.getX());
		
		if(x == null){
			SagaLogger.severe(this, "tried to remove a non-existan " + sagaChunk + " chunk to group manager chunk shortcut");
			return;
		}
		
		SagaChunk z = x.get(sagaChunk.getZ());
		
		if(z == null){
			SagaLogger.severe(this, "tried to remove a non-existan " + sagaChunk + " chunk to group manager chunk shortcut");
			return;
		}
		
		// Remove:
		x.remove(sagaChunk.getZ());
		
		// Clean up:
		if(x.isEmpty()){
			world.remove(sagaChunk.getX());
		}
		if(world.isEmpty()){
			sagaChunks.remove(sagaChunk.getWorldName());
		}

		
	}
	
	/**
	 * Adds a chunk.
	 * 
	 * @param sagaChunk saga chunk
	 */
	void addChunk(SagaChunk sagaChunk) {

		
		Hashtable<Integer, Hashtable<Integer, SagaChunk>> world = sagaChunks.get(sagaChunk.getWorldName());
		
		if(world == null){
			world = new Hashtable<Integer, Hashtable<Integer,SagaChunk>>();
			sagaChunks.put(sagaChunk.getWorldName(), world);
		}
		
		Hashtable<Integer, SagaChunk> x = world.get(sagaChunk.getX());
		
		if(x == null){
			x = new Hashtable<Integer, SagaChunk>();
			world.put(sagaChunk.getX(), x);
		}
		
		SagaChunk z = x.get(sagaChunk.getZ());
		
		if(z != null){
			SagaLogger.severe(getClass(), "overlap between " + z + " and " + sagaChunk + " Saga chunks");
			return;
		}
		
		// Add:
		x.put(sagaChunk.getZ(), sagaChunk);
		
		
	}
	
	/**
	 * Removes a chunk group.
	 * 
	 * @param chunkBundle chunk group
	 */
	void removeChunkBundle(ChunkBundle chunkBundle) {

		
		if(!registeredGroups.containsKey(chunkBundle.getId())){
			SagaLogger.severe(getClass(), "tried to remove a non-existing " + chunkBundle + " chunk group");
			return;
		}
		
		registeredGroups.remove(chunkBundle.getId());
		// Unregister chunk group manager:
//		chunkGroup.unregisterChunkGroupManager();
//
//		// Remove chunk shortcuts:
//		ArrayList<SagaChunk> groupChunks = chunkGroup.getGroupChunks();
//		for (int i = 0; i < groupChunks.size(); i++) {
//			removeChunk(groupChunks.get(i));
//		}
//		
		
	}
	
	/**
	 * Adds a chunk group.
	 * 
	 * @param chunkBundle chunk group
	 */
	void addChunkBundle(ChunkBundle chunkBundle) {

		
		if(registeredGroups.containsKey(chunkBundle.getId())){
			SagaLogger.severe(getClass(), "tried to add an already existing " + chunkBundle + " chunk group");
			return;
		}
		
		registeredGroups.put(chunkBundle.getId(), chunkBundle);
		
//
//		// Add chunk shortcuts:
//		ArrayList<SagaChunk> groupChunks = chunkGroup.getGroupChunks();
//		for (int i = 0; i < groupChunks.size(); i++) {
//			addChunk(groupChunks.get(i));
//		}
		
		
	}

	/**
	 * Gets an unused chunk group ID.
	 * 
	 * @return unused chunk group ID. from 0(exclusive)
	 */
	int getUnusedChunkGroupId() {

		
        Random random = new Random();

        int newId = random.nextInt(Integer.MAX_VALUE);

        while ( newId == 0 || registeredGroups.get(new Integer(newId)) != null ) {
            //Get another random id until we find one that isn't used
            // We also skip 0 because that is a special value that means no faction
            newId = random.nextInt();
        }

        return newId;

        
    }
	
	
	// Load unload:
	/**
	 * Loads the map.
	 * 
	 */
	public static void load(){
		

		// Inform:
		SagaLogger.info("Loading chunk groups.");
		
		ChunkBundleManager manager = new ChunkBundleManager();
		
		// Set instance:
		instance = manager;
		
		// Load:
		String[] ids = WriterReader.getAllIds(Directory.SETTLEMENT_DATA);
		for (int i = 0; i < ids.length; i++) {
			
			ChunkBundle element = ChunkBundle.load(ids[i]);
			
			// Ignore all invalid IDs:
			if(element.getId() < 0){
				SagaLogger.severe(ChunkBundleManager.class, "can't load " + element + " chunk group, because it has an invalid ID");
				continue;
			}
			
		}
		
		
	}
	
	/**
	 * Saves the manager.
	 * 
	 */
	public static void save() {


		// Inform:
		SagaLogger.info("Saving chunk groups.");

		// Save:
		Collection<ChunkBundle> elements = manager().registeredGroups.values();
		for (ChunkBundle element : elements) {
			element.save();
		}
		
		
	}
	
	/**
	 * Unloads the map.
	 * 
	 */
	public static void unload() {


		// Inform:
		SagaLogger.info("Unloading chunk groups.");
		
		save();
		instance = null;
		
		
	}

	
}
