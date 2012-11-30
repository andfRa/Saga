package org.saga.settlements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.saga.SagaLogger;
import org.saga.factions.FactionClaimManager;
import org.saga.player.SagaPlayer;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.settlements.SagaChunk.ChunkSide;

public class BundleManager {


	/**
	 * Instance
	 */
	private static BundleManager instance;
	
	/**
	 * Gets manager.
	 * 
	 * @return manager
	 */
	public static BundleManager manager() {
		return instance;
	}
	
	
	/**
	 * Registered groups.
	 */
	transient  Hashtable<Integer, Bundle> registeredGroups = new Hashtable<Integer, Bundle>();
	
	/**
	 * All saga chunks.
	 */
	transient private Hashtable<String, Hashtable<Integer, Hashtable<Integer, SagaChunk>>> sagaChunks = new Hashtable<String, Hashtable<Integer,Hashtable<Integer,SagaChunk>>>();

	
	// Synchronisation:
	/**
	 * Synchronises players bundle.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void syncBundle(SagaPlayer sagaPlayer) {


		// No bundle:
		Integer bundleId = sagaPlayer.getBundleId();
		if(bundleId == -1) return;

		// No longer exists:
		Bundle bundle = registeredGroups.get(bundleId);
		if(bundle == null){
			SagaLogger.severe(getClass(), "bundle " + bundleId + "doesn't exist for player " + sagaPlayer.getName());
			sagaPlayer.removeBundleId();
			return;
		}
		
		// Not on the list:
		if(!bundle.isMember(sagaPlayer.getName())){
			SagaLogger.severe(getClass(), "player " + sagaPlayer.getName() + " is not on the member list for bundle " + bundle);
			sagaPlayer.removeBundleId();
			return;
		}
		
		
	}

	
	
	// Chunk bundles:
	/**
	 * Gets the chunk bundle for the given ID.
	 * 
	 * @param bundleId bundle ID
	 * @return bundle, null if not found
	 */
	public Bundle getBundle(Integer bundleId) {
		
		return registeredGroups.get(bundleId);
		
	}

	/**
	 * Gets the chunk bundles for the given IDs.
	 * 
	 * @param bundleIds bundle IDs
	 * @return bundles
	 */
	public ArrayList<Bundle> getBundles(ArrayList<Integer> bundleIds) {
		
		
		ArrayList<Bundle> bundles = new ArrayList<Bundle>();
		
		for (Integer bundleId : bundleIds) {
			
			Bundle bundle = getBundle(bundleId);
			if(bundle != null) bundles.add(bundle);
			
		}
		
		return bundles;
		
		
	}

	/**
	 * Gets the bundle with the given name.
	 * 
	 * @param name bundle name
	 * @return bundle, null if not found
	 */
	public Bundle getBundle(String name) {

		
		Collection<Bundle> bundles = registeredGroups.values();

		for (Bundle bundle : bundles) {
			if(bundle.getName().equalsIgnoreCase(name)) return bundle;
		}

        return null;
        
        
    }

	
	/**
     * Matches a bundle with the given name.
     * 
     * @param name bundle name
     * @return bundle, null if not found
     */
    public Bundle matchBundle(String name) {

    	
    	// Try complete match:
    	Bundle bundle = getBundle(name);
    	if(bundle != null) return bundle;
    	
    	Collection<Bundle> factions = this.registeredGroups.values();
    	for (Bundle matchBundle : factions) {
			
    		if(matchBundle.getName().toLowerCase().startsWith(name.toLowerCase())) return matchBundle;
    		
		}
    	
    	return null;
    	
    	
	}
	
	
	// Saga chunks:
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
			return BundleManager.manager().getSagaChunk(worldName, x - 1, z);
		case LEFT:
			return BundleManager.manager().getSagaChunk(worldName, x, z + 1);
		case BACK:
			return BundleManager.manager().getSagaChunk(worldName, x +1 , z);
		case RIGHT:
			return BundleManager.manager().getSagaChunk(worldName, x, z - 1);	
		default:
			return null;
		}
		
		
	}

	
	
	// Updates:
	/**
	 * Removes a saga chunk.
	 * 
	 * @param sagaChunk saga chunk
	 */
	void removeSagaChunk(SagaChunk sagaChunk) {

		
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
	 * Adds a saga chunk.
	 * 
	 * @param sagaChunk saga chunk
	 */
	void addSagaChunk(SagaChunk sagaChunk) {

		
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
	 * @param bundle chunk group
	 */
	void removeBundle(Bundle bundle) {

		
		if(!registeredGroups.containsKey(bundle.getId())){
			SagaLogger.severe(getClass(), "tried to remove a non-existing " + bundle + " chunk group");
			return;
		}
		
		registeredGroups.remove(bundle.getId());
		
		// Remove from claim manager:
		FactionClaimManager.manager().removeBundle(bundle.getId());
		
		
	}
	
	/**
	 * Adds a bundle.
	 * 
	 * @param bundle chunk group
	 */
	void addBundle(Bundle bundle) {

		
		if(registeredGroups.containsKey(bundle.getId())){
			SagaLogger.severe(getClass(), "tried to add an already existing " + bundle + " chunk group");
			return;
		}
		
		registeredGroups.put(bundle.getId(), bundle);
		
		
	}

	/**
	 * Gets an unused chunk group ID.
	 * 
	 * @return unused chunk group ID. from 0(exclusive)
	 */
	int getUnusedId() {

		
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
	 * Loads the manager.
	 * 
	 */
	public static void load(){
		

		// Inform:
		SagaLogger.info("Loading bundles.");
		
		BundleManager manager = new BundleManager();
		
		// Set instance:
		instance = manager;
		
		// Load:
		String[] ids = WriterReader.getAllIds(Directory.SETTLEMENT_DATA);
		for (int i = 0; i < ids.length; i++) {
			
			Bundle element = Bundle.load(ids[i]);
			
			// Ignore all invalid IDs:
			if(element.getId() < 0){
				SagaLogger.severe(BundleManager.class, "can't load " + element + " chunk group, because it has an invalid ID");
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
		Collection<Bundle> elements = manager().registeredGroups.values();
		for (Bundle element : elements) {
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
