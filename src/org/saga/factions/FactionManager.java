package org.saga.factions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;

import org.saga.SagaLogger;
import org.saga.player.SagaPlayer;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.statistics.StatisticsManager;


public class FactionManager {

	
	/**
	 * Instance
	 */
	private static FactionManager instance;
	
	/**
	 * Gets the manager instance.
	 * 
	 * @return manager
	 */
	public static FactionManager manager() {
		return instance;
	}
	
	
	/**
	 * Factions.
	 */
	private Hashtable<Integer, Faction> loadedFactions = new Hashtable<Integer, Faction>();
	
	

	// Synchronisation:
	/**
	 * Synchronises players faction.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void syncFaction(SagaPlayer sagaPlayer) {


		// No faction:
		Integer factionId = sagaPlayer.getFactionId();
		if(factionId == -1) return;

		// No longer exists:
		Faction faction = loadedFactions.get(factionId);
		if(faction == null){
			SagaLogger.severe(getClass(), "faction " + factionId + "doesn't exist for player " + sagaPlayer.getName());
			sagaPlayer.removeFactionId();
			return;
		}
		
		// Not on the list:
		if(!faction.isMember(sagaPlayer.getName())){
			SagaLogger.severe(getClass(), "player " + sagaPlayer.getName() + " is not on the member list for faction " + faction);
			sagaPlayer.removeFactionId();
			return;
		}
		
		
	}
	

	
	// Faction:
	/**
	 * Gets a faction from the list.
	 * 
	 * @param factionId faction ID
	 * @return faction, null if not found
	 */
	public Faction getFaction(Integer factionId) {

		return loadedFactions.get(factionId);
		
	}
	
	/**
	 * Gets factions with the given IDs.
	 * 
	 * @param factionIds faction IDs
	 * @return factions
	 */
	public ArrayList<Faction> getFactions(ArrayList<Integer> factionIds) {

		
		ArrayList<Faction> factions = new ArrayList<Faction>();
		
		for (Integer id : factionIds) {
			
			Faction faction = getFaction(id);
			
			if(faction != null) factions.add(faction);
			
		}
		
		return factions;
		
	}
	
	/**
	 * Gets factions with the given IDs.
	 * 
	 * @param factionIds faction IDs
	 * @return factions
	 */
	public ArrayList<Faction> getFactions(Set<Integer> factionIds) {
		return getFactions(new ArrayList<Integer>(factionIds));
	}


    /**
     * Finds a faction with the given name.
     * 
     * @param name name
     * @return faction. null if not found
     */
    public Faction getFaction(String name) {

    	Collection<Faction> allFactions = loadedFactions.values();
    	for (Faction faction : allFactions) {
    		if(faction.getName().toLowerCase().equalsIgnoreCase(name)) return faction;
		}
    	
        return null;
        
    }
    
    /**
     * Matches a faction with the given name.
     * 
     * @param name faction name
     * @return faction, null if not found
     */
    public Faction matchFaction(String name) {

    	Faction faction = getFaction(name);
    	if(faction != null) return faction;
    	
    	Collection<Faction> factions = this.loadedFactions.values();
    	for (Faction matchFaction : factions) {
			
    		if(matchFaction.getName().toLowerCase().startsWith(name.toLowerCase())) return matchFaction;
    		
		}
    	
    	return null;
    	
	}
	
    
	
	// Updating:
	/**
	 * Adds a faction.
	 * 
	 * @param faction
	 */
	void addFaction(Faction faction) {
	
		
		// Add:
		Faction oldFaction = loadedFactions.put(faction.getId(), faction);
		if(oldFaction != null){
			SagaLogger.severe(getClass(), "added an already existing faction " + oldFaction + " to the faction list");
		}
		
		
	}
	
	/**
	 * Removes a faction.
	 * 
	 * @param faction
	 */
	void removeFaction(Faction faction) {
		
		
		// Remove:
		if(loadedFactions.remove(faction.getId()) == null){
			SagaLogger.severe(getClass(), "tried to remove a non-existing " + faction + " faction from the list");
			return;
		}

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

        while ( newId == 0 || loadedFactions.get(new Integer(newId)) != null ) {
            //Get another random id until we find one that isn't used
            // We also skip 0 because that is a special value that means no faction
            newId = random.nextInt();
        }

        return newId;

    }
	
	
	
	// Other:
	/**
	 * Gets faction names with the given IDs.
	 * 
	 * @param ids faction IDs
	 * @return faction names, empty if none
	 */
	public ArrayList<String> getFactionNames(Collection<Integer> ids) {

		
		ArrayList<String> factions = new ArrayList<String>();
		
		for (Integer id : ids) {
			
			Faction faction = getFaction(id);
			
			if(faction != null) factions.add(faction.getName());
			
		}
		
		return factions;
		
	}

	/**
	 * Updates faction statistics.
	 * 
	 */
	public void updateStatistics() {


		Collection<Faction> factions = this.loadedFactions.values();
		for (Faction faction : factions) {
			StatisticsManager.manager().setClaims(faction);
			StatisticsManager.manager().setRanks(faction);
		}
		
		
	}
	
	
	
	// Load unload:
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
		Collection<Faction> factions = manager().loadedFactions.values();
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
