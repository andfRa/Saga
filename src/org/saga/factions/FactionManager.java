package org.saga.factions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import org.saga.SagaLogger;
import org.saga.chunks.ChunkBundleManager;
import org.saga.player.SagaPlayer;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;


public class FactionManager {

	
	/**
	 * Instance
	 */
	private static FactionManager instance;
	
	/**
	 * Gets manager.
	 * 
	 * @return manager
	 */
	public static FactionManager manager() {
		return instance;
	}
	
	
	/**
	 * Factions.
	 */
	private Hashtable<Integer, Faction> factions = new Hashtable<Integer, Faction>();
	

	// Initialization:
	/**
	 * Initializes.
	 * 
	 */
	private FactionManager() {
	}

	

	// Player load unload:
	/**
	 * Registers a player.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void playerLoaded(SagaPlayer sagaPlayer) {


		// Get faction ID:
		Integer factionId = sagaPlayer.getFactionId();
		
		// Stop if invalid faction ID:
		if(factionId <= 0){
			return;
		}

		// Retrieve the faction:
		Faction faction = factions.get(factionId);
		
		// No longer exists:
		if(faction == null){
			SagaLogger.severe(getClass(), "failed to retrieve faction for " + sagaPlayer + "player with " + factionId + " ID");
			return;
		}
		
		// Not on the list:
		if(!faction.isMember(sagaPlayer.getName())){
			SagaLogger.severe(getClass(), "failed to register faction for " + sagaPlayer + "player with " + factionId + " ID, because the player is not on its member list");
			sagaPlayer.removeFactionId();
			return;
		}
		
		
	}
	
	/**
	 * Unregisters a player.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void playerUnloaded(SagaPlayer sagaPlayer) {


		// Retrieve the faction:
		Faction faction = sagaPlayer.getFaction();

		// No longer exists:
		if(faction == null){
			return;
		}

		
	}

	
	// List:
	/**
	 * Gets a saga faction from the list.
	 * 
	 * @param factionId faction ID
	 * @return saga faction, null if not found
	 */
	public Faction getFaction(Integer factionId) {

		return factions.get(factionId);
		
	}
	
	/**
	 * Gets saga factions with the given ids.
	 * 
	 * @param ids faction IDs
	 * @return saga factions, empty if none
	 */
	public ArrayList<Faction> getFactions(Collection<Integer> ids) {

		
		ArrayList<Faction> factions = new ArrayList<Faction>();
		
		for (Integer id : ids) {
			
			Faction faction = getFaction(id);
			
			if(faction != null) factions.add(faction);
			
		}
		
		return factions;
		
	}

    /**
     * Finds a saga faction with the given name.
     * 
     * @param name name
     * @return saga faction. null if not found
     */
    public Faction getFaction(String name) {

    	
        Iterator<Integer> ids = factions.keySet().iterator();

        while( ids.hasNext() ) {

            Integer id = ids.next();

            Faction faction = factions.get(id);

            if ( faction.getName().equals(name) ) {
                return faction;
            }

        }

        return null;

        
    }
	
	
	// Faction methods:
	/**
	 * Adds a faction.
	 * 
	 * @param faction
	 */
	void addFaction(Faction faction) {
	
		
		// Add:
		Faction oldFaction = factions.put(faction.getId(), faction);
		if(oldFaction != null){
			SagaLogger.severe(getClass(), "added an already existing faction " + oldFaction + " to the faction list");
		}
		
		// Register faction:
		ChunkBundleManager.manager().factionLoaded(faction);
		
		
	}
	
	/**
	 * Removes a faction.
	 * 
	 * @param faction
	 */
	void removeFaction(Faction faction) {
		
		
		// Remove:
		if(factions.remove(faction.getId()) == null){
			SagaLogger.severe(getClass(), "tried to remove a non-existing " + faction + " faction from the list");
			return;
		}

		// Unregister faction:
		ChunkBundleManager.manager().factionUnloaded(faction);
		
		// Remove from claim manager:
		FactionClaimManager.manager().removeFaction(faction.getId());

		
	}

	/**
	 * Gets an unused faction ID.
	 * 
	 * @return unused faction ID. from 0(exclusive)
	 */
	int getUnusedFactoinId() {

        Random random = new Random();

        int newId = random.nextInt(Integer.MAX_VALUE);

        while ( newId == 0 || factions.get(new Integer(newId)) != null ) {
            //Get another random id until we find one that isn't used
            // We also skip 0 because that is a special value that means no faction
            newId = random.nextInt();
        }

        return newId;

    }
	
	
	// Other:
	/**
	 * Gets saga faction names with the given IDs.
	 * 
	 * @param ids faction IDs
	 * @return saga faction names, empty if none
	 */
	public ArrayList<String> getFactionNames(Collection<Integer> ids) {

		
		ArrayList<String> factions = new ArrayList<String>();
		
		for (Integer id : ids) {
			
			Faction faction = getFaction(id);
			
			if(faction != null) factions.add(faction.getName());
			
		}
		
		return factions;
		
	}

	
	// Loading unloading:
	/**
	 * Loads faction manager and loads factions.
	 * 
	 */
	public static void load() {

		
		// Inform:
		SagaLogger.info("Loading factions.");

		FactionManager manager = new FactionManager();
		
		// Load factions:
		String[] ids = WriterReader.getAllIds(Directory.FACTION_DATA);
		for (int i = 0; i < ids.length; i++) {
			Faction element = Faction.load(ids[i]);
			// Ignore all invalid IDs:
			if(element.getId() < 0){
				SagaLogger.severe(FactionManager.class, "can't load " + element + " faction, because it has an invalid ID");
				continue;
			}
			// Add to manager:
			manager.addFaction(element);
		}
		
		// Set instance:
		instance = manager;
		
		
	}
	
	/**
	 * Saves faction manager.
	 * 
	 */
	public static void save() {

		// Inform:
		SagaLogger.info("Saving factions.");

		// Save factions:
		Collection<Faction> factions = manager().factions.values();
		for (Faction faction : factions) {
			faction.save();
		}
		
		
	}
	
	/**
	 * Unloads faction manager and saves factions.
	 * 
	 */
	public static void unload() {

		// Inform:
		SagaLogger.info("Unloading factions.");
		
		save();
		instance = null;
		
	}
	
	
}
