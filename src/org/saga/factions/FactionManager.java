package org.saga.factions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import org.saga.Saga;
import org.saga.chunkGroups.ChunkGroupManager;
import org.saga.player.SagaPlayer;
import org.saga.utility.WriterReader;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;


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
	private Hashtable<Integer, SagaFaction> factions = new Hashtable<Integer, SagaFaction>();
	

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
		SagaFaction faction = factions.get(factionId);
		
		// No longer exists:
		if(faction == null){
			Saga.severe(getClass(), "failed to retrieve faction for " + sagaPlayer + "player with " + factionId + " ID", "ignoring request");
			return;
		}
		
		// Not on the list:
		if(!faction.isMember(sagaPlayer.getName())){
			Saga.severe(getClass(), "failed to register faction for " + sagaPlayer + "player with " + factionId + " ID, because the player is not on its member list", "removing ID");
			sagaPlayer.removeFactionId(sagaPlayer.getFactionId());
			return;
		}
		
		// Register player:
		faction.registerMember(sagaPlayer);
		
		
	}
	
	/**
	 * Unregisters a player.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void playerUnloaded(SagaPlayer sagaPlayer) {


		// Retrieve the faction:
		SagaFaction faction = sagaPlayer.getRegisteredFaction();

		// No longer exists:
		if(faction == null){
			return;
		}

		// Unregister player:
		faction.unregisterMember(sagaPlayer);
		
		
	}

	
	// List:
	/**
	 * Gets a saga faction from the list.
	 * 
	 * @param factionId faction ID
	 * @return saga faction, null if not found
	 */
	public SagaFaction getFaction(Integer factionId) {

		return factions.get(factionId);
		
	}
	
	/**
	 * Gets saga factions with the given ids.
	 * 
	 * @param ids faction IDs
	 * @return saga factions, empty if none
	 */
	public ArrayList<SagaFaction> getFactions(Collection<Integer> ids) {

		
		ArrayList<SagaFaction> factions = new ArrayList<SagaFaction>();
		
		for (Integer id : ids) {
			
			SagaFaction faction = getFaction(id);
			
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
    public SagaFaction getFaction(String name) {

    	
        Iterator<Integer> ids = factions.keySet().iterator();

        while( ids.hasNext() ) {

            Integer id = ids.next();

            SagaFaction faction = factions.get(id);

            if ( faction.getName().equals(name) ) {
                return faction;
            }

        }

        return null;

        
    }
	
	
	// SagaFaction methods:
	/**
	 * Adds a faction.
	 * 
	 * @param faction
	 */
	void addFaction(SagaFaction faction) {
	
		
		// Add:
		SagaFaction oldFaction = factions.put(faction.getId(), faction);
		if(oldFaction != null){
			Saga.severe("Added an already existing faction " + oldFaction.getName() + "(" + oldFaction.getId() + ") to the faction list.");
		}
		
		// Register faction:
		ChunkGroupManager.manager().factionLoaded(faction);
		
		
	}
	
	/**
	 * Removes a faction.
	 * 
	 * @param faction
	 */
	void removeFaction(SagaFaction faction) {
		
		
		// Remove:
		if(factions.remove(faction.getId()) == null){
			Saga.severe("Tried to remove a non-existing " + faction.getName() + "(" + faction.getId() + ") faction from the list.");
			return;
		}

		// Unregister faction:
		ChunkGroupManager.manager().factionUnloaded(faction);
		
		

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
			
			SagaFaction faction = getFaction(id);
			
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
		Saga.info("Loading factions.");

		FactionManager manager = new FactionManager();
		
		// Load factions:
		String[] ids = WriterReader.getAllFactionIds();
		for (int i = 0; i < ids.length; i++) {
			SagaFaction element = SagaFaction.load(ids[i]);
			// Ignore all invalid IDs:
			if(element.getId() < 0){
				Saga.severe("Can't load " + element + " faction, because it has an invalid ID. Ignoring request.");
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
		Saga.info("Saving factions.");

		// Save factions:
		Collection<SagaFaction> factions = manager().factions.values();
		for (SagaFaction sagaFaction : factions) {
			sagaFaction.save();
		}
		
		
	}
	
	/**
	 * Unloads faction manager and saves factions.
	 * 
	 */
	public static void unload() {

		// Inform:
		Saga.info("Unloading factions.");
		
		save();
		instance = null;
		
	}
	
	
}
