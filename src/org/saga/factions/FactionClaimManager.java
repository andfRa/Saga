package org.saga.factions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.saga.Clock;
import org.saga.Clock.SecondTicker;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.buildings.Building;
import org.saga.buildings.TownSquare;
import org.saga.config.FactionConfiguration;
import org.saga.messages.ClaimMessages;
import org.saga.player.SagaPlayer;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.settlements.Bundle;
import org.saga.settlements.BundleManager;
import org.saga.settlements.Settlement;
import org.saga.statistics.StatisticsManager;

public class FactionClaimManager implements SecondTicker{


	/**
	 * Instance of the manager.
	 */
	transient private static FactionClaimManager instance;

	/**
	 * Gets the manager.
	 * 
	 * @return manager
	 */
	public static FactionClaimManager manager() {
		return instance;
	}

	
	
	/**
	 * Faction claims.
	 */
	private Hashtable<Integer, Integer> claims;

	/**
	 * Faction claim progress.
	 */
	private Hashtable<Integer, Double> progress;
	
	/**
	 * Claiming factions.
	 */
	private Hashtable<Integer, Integer> claiming;
	
	/**
	 * Bundle IDs that have claims active.
	 */
	transient private HashSet<Integer> claimActive;
	

	
	// Initialisation:
	/**
	 * Initialises the manager.
	 * 
	 * @param name nothing, prevents gson
	 */
	public FactionClaimManager(String name) {
		
		
		claims = new Hashtable<Integer, Integer>();
		progress = new Hashtable<Integer, Double>();
		claiming = new Hashtable<Integer, Integer>();
		claimActive = new HashSet<Integer>();

		
	}

	/**
	 * Fixes all problematic fields.
	 * 
	 */
	public void complete() {
		

		if(claims == null){
			SagaLogger.nullField(getClass(), "claims");
			claims = new Hashtable<Integer, Integer>();
		}

		if(progress == null){
			SagaLogger.nullField(getClass(), "progress");
			progress = new Hashtable<Integer, Double>();
		}
		
		if(claiming == null){
			SagaLogger.nullField(getClass(), "claiming");
			claiming = new Hashtable<Integer, Integer>();
		}
		
		// Fix factions:
		HashSet<Integer> facIds = getAllFactionIds();
		for (Integer id : facIds) {
			if(FactionManager.manager().getFaction(id) == null){
				removeFaction(id);
				SagaLogger.warning(getClass(), "faction with ID " + id + " doesn't exist");
			}
		}
		
		// Fix bundles:
		HashSet<Integer> bunIds = getAllBundleIds();
		for (Integer id : bunIds) {
			if(BundleManager.manager().getBundle(id) == null){
				removeBundle(id);
				SagaLogger.warning(getClass(), "faction with ID " + id + " doesn't exist");
			}
		}
		
		// Transient:
		claimActive = new HashSet<Integer>();
		
		
	}
	
	
	
	// Claiming:
	/**
	 * Initiates claiming.
	 * 
	 * @param bundle bundle
	 * @param faction faction
	 */
	public void initiate(Bundle bundle, Faction faction) {

		
		// Claimer:
		setClaimer(bundle.getId(), faction.getId());
		
		// Set as active:
		claimActive.add(bundle.getId());
		
		
	}
	
	/**
	 * Gets the initiating faction ID.
	 * 
	 * @param bundleId chunk bundle ID
	 * @param sagaPlayers saga player
	 * @return initiating faction ID, null if none
	 */
	public static Integer getInitFactonId(Integer bundleId, ArrayList<SagaPlayer> sagaPlayers) {

		
		Integer owningId = FactionClaimManager.manager().getOwningFactionId(bundleId);
		
		// Get faction:
		for (SagaPlayer sagaPlayer : sagaPlayers) {
			
			Faction playerFaction = sagaPlayer.getFaction();
			
			// Ignore not factions:
			if(playerFaction == null) continue;
			
			if(owningId == null) return playerFaction.getId();
			
			// Other factions:
			if(!playerFaction.getId().equals(owningId)) return playerFaction.getId();
			
		}
		
		return null;
		
		
	}

	/**
	 * Progresses the chunk bundle claim.
	 * 
	 * @param bundle chunk bundle
	 * @param amount amount claimed
	 * @return progress
	 */
	public Double progressClaim(Bundle bundle, Double amount) {

		
		Double progress = modifyProgress(bundle.getId(), amount);
		
		// Claimed:
		if(progress >= 1.0){
			
			Integer bundleId = bundle.getId();
			Faction faction = getClaimerFaction(bundleId);
			
			clearClaimer(bundleId);
			clearProgress(bundleId);
			
			if(faction == null){
				SagaLogger.severe(getClass(), "failed to retrieve faction");
				return progress;
			}
			
			Integer oldId = setOwningFactionId(bundleId, faction.getId());
			
			// Inform:
			Saga.broadcast(ClaimMessages.claimedBcast(bundle, faction));
			
			// Statistics:
			Faction oldOwner = null;
			if(oldId != null) oldOwner = FactionManager.manager().getFaction(oldId);
			if(oldOwner != null){
				StatisticsManager.manager().addBundleSeized(faction, bundle);
				StatisticsManager.manager().addBundleLost(oldOwner, bundle);
				StatisticsManager.manager().setBundlesOwned(oldOwner);
			}else{
				StatisticsManager.manager().addBundleClaimed(faction, bundle);
			}
			StatisticsManager.manager().setBundlesOwned(faction);
			
		}
		
		// In progress:
		else{
		
			// Set as active:
			claimActive.add(bundle.getId());
			
		}
		
		return progress;
		
		
	}
	
	/**
	 * Gets all faction IDs.
	 * 
	 * @param sagaPlayers all players
	 * @return all faction IDs
	 */
	public static HashSet<Integer> getAllFactionIds(ArrayList<SagaPlayer> sagaPlayers) {

		
		HashSet<Integer> allClaimers = new HashSet<Integer>();
		
		for (SagaPlayer sagaPlayer : sagaPlayers) {
			if(sagaPlayer.getFaction() != null) allClaimers.add(sagaPlayer.getFaction().getId());
		}
		
		return allClaimers; 
			
		
	}
	
	
	
	// Fields:
	/**
	 * Get owning faction ID.
	 * 
	 * @param bundleId chunk bundle ID
	 * @return owning faction ID, null if none
	 */
	public Integer getOwningFactionId(Integer bundleId) {
		return claims.get(bundleId);
	}
	
	/**
	 * Get owning faction ID.
	 * 
	 * @param bundleId chunk bundle ID
	 * @param factionId faction ID
	 * @return old owning faction ID, null if none
	 */
	public Integer setOwningFactionId(Integer bundleId, Integer factionId) {
		return claims.put(bundleId, factionId);
	}

	/**
	 * Checks if the chunk bundle has an owner.
	 * 
	 * @param bundleId chunk bundle ID
	 * @return true if has an owner
	 */
	public Boolean hasOwnerFaction(Integer bundleId) {
		return claims.get(bundleId) != null;
	}
	
	
	/**
	 * Checks if the bundle is claimed by a faction.
	 * 
	 * @param bundleId bundle ID
	 * @return true if claiming 
	 */
	public boolean isClaiming(Integer bundleId) {

		return claiming.get(bundleId) != null;
		
	}
	
	/**
	 * Gets the claimer faction ID.
	 * 
	 * @param bundleId bundle ID
	 * @return claimer faction ID, -1 if none
	 */
	public Integer getClaimerId(Integer bundleId) {

		Integer claimer = claiming.get(bundleId);
		if(claimer == null) return -1;
		
		return claimer;
		
	}

	/**
	 * Gets the contester faction ID.
	 * 
	 * @param bundleId bundle ID
	 * @param factionId faction ID
	 * @return old contester faction ID
	 */
	public Integer setClaimer(Integer bundleId, Integer factionId) {

		return claiming.put(bundleId, factionId);
		
	}
	
	/**
	 * Clears the contester faction.
	 * 
	 * @param bundleId bundle ID
	 * @return cleared contester, null if none
	 */
	public Integer clearClaimer(Integer bundleId) {

		return claiming.remove(bundleId);
		
	}
	
	
	/**
	 * Gets the progress for the given bundle.
	 * 
	 * @param bundleId bundle ID
	 * @return current progress
	 */
	public Double getProgress(Integer bundleId) {
		
		Double bundleProgess = progress.get(bundleId);
		if(bundleProgess == null) bundleProgess = 0.0;
		
		return bundleProgess;
		
	}

	/**
	 * Sets the progress for the given bundle.
	 * 
	 * @param bundleId bundle ID
	 * @param progress progress
	 */
	public void setProgress(Integer bundleId, Double progress) {
		
		this.progress.put(bundleId, progress);
		
	}
	
	
	/**
	 * Modifies claim progress
	 * 
	 * @param bundleId bundle ID
	 * @param amount amount
	 * @return progress
	 */
	public Double modifyProgress(Integer bundleId, Double amount) {

		
		Double bundleProgess = getProgress(bundleId) + amount;
		
		if(bundleProgess <= 0){
			progress.remove(bundleId);
			claiming.remove(bundleId);
			claimActive.remove(bundleId);
		}else{
			progress.put(bundleId, bundleProgess);
		}
		
		return bundleProgess;
		
		
	}
	
	/**
	 * Clears faction progress.
	 * 
	 * @param bundleId bundle ID
	 * @return cleared progress, null if none
	 */
	public Double clearProgress(Integer bundleId) {

		return progress.remove(bundleId);
		
	}
	
	
	/**
	 * Gets all faction IDs
	 * 
	 * @return all faction IDs
	 */
	public HashSet<Integer> getAllFactionIds() {

		return new HashSet<Integer>(claims.values());
		
	}

	/**
	 * Gets all bundle IDs
	 * 
	 * @return all bundle IDs
	 */
	public HashSet<Integer> getAllBundleIds() {

		return new HashSet<Integer>(claims.keySet());
		
	}
	
	
	
	// Factions and settlements:
	/**
	 * Removes a faction.
	 * 
	 * @param factionId faction ID
	 */
	public void removeFaction(Integer factionId) {

		
		Set<Integer> bundleIds;

		// Claimed:
		bundleIds = new HashSet<Integer>(claims.keySet());
		for (Integer bundleId : bundleIds) {
			if(claims.get(bundleId).equals(factionId)) claims.remove(bundleId);
		}

		// Claimers and progress:
		bundleIds = claiming.keySet();
		for (Integer bundleId : bundleIds) {
			if(claiming.get(bundleId) == factionId){
				progress.remove(bundleId);
				claiming.remove(bundleId);
			}
		}
		
		
	}
	
	/**
	 * Removes a chunk bundle.
	 * 
	 * @param bundleId chunk bundle ID
	 */
	public void removeBundle(Integer bundleId) {

		claims.remove(bundleId);
		progress.remove(bundleId);
		claiming.remove(bundleId);
		
	}
	
	
	/**
	 * Finds settlements owned by the faction.
	 * 
	 * @param factionId faction Id
	 * @return settlements owned
	 */
	public Settlement[] findSettlements(Integer factionId) {

		
		ArrayList<Settlement> settlements = new ArrayList<Settlement>();
		
		Set<Entry<Integer, Integer>> claimed = claims.entrySet();
		
		for (Entry<Integer, Integer> entry : claimed) {
			
			if(!entry.getValue().equals(factionId)) continue;
			
			Bundle bundle = BundleManager.manager().getBundle(entry.getKey());
			
			if(bundle == null || !(bundle instanceof Settlement)) continue;
			
			settlements.add(((Settlement) bundle));
			
		}
		
		return settlements.toArray(new Settlement[settlements.size()]);
	
		
	}
	

	/**
	 * Finds settlements IDs owned by the faction.
	 * 
	 * @param factionId faction Id
	 * @return settlements IDs owned
	 */
	public Integer[] findSettlementsIds(Integer factionId) {

		
		ArrayList<Integer> ids = new ArrayList<Integer>();
		
		Set<Entry<Integer, Integer>> claimed = claims.entrySet();
		
		for (Entry<Integer, Integer> entry : claimed) {
			if(!entry.getValue().equals(factionId)) continue;
			ids.add(entry.getKey());
		}
		
		return ids.toArray(new Integer[ids.size()]);
	
		
	}
	
	/**
	 * Gets levels for given settlements.
	 * 
	 * @param settlements settlements
	 * @return levels for settlements
	 */
	public static Integer[] getLevels(Settlement[] settlements) {

		
		Integer[] levels = new Integer[settlements.length];
		
		for (int i = 0; i < levels.length; i++) {
			levels[i] = settlements[i].getLevel();
		}
		
		return levels;
		
		
	}


	/**
	 * Get owning faction.
	 * 
	 * @param bundleId chunk bundle ID
	 * @return owning faction, null if none
	 */
	public Faction getOwningFaction(Integer bundleId) {
		
		Integer id = getOwningFactionId(bundleId);
		if(id == null) return null;
		
		return FactionManager.manager().getFaction(id);

	}

	/**
	 * Get contester faction.
	 * 
	 * @param bundleId chunk bundle ID
	 * @return contester faction, null if none
	 */
	public Faction getClaimerFaction(Integer bundleId) {
		
		Integer id = getClaimerId(bundleId);
		if(id == null) return null;
		
		return FactionManager.manager().getFaction(id);

	}

	/**
	 * Gets the initiating faction ID.
	 * 
	 * @param bundleId chunk bundle ID
	 * @param sagaPlayers saga player
	 * @return initiating faction ID, null if none
	 */
	public static Faction getInitFacton(Integer bundleId, ArrayList<SagaPlayer> sagaPlayers) {

		
		Integer initId = getInitFactonId(bundleId, sagaPlayers);
		if(initId == null) return null;
		
		return FactionManager.manager().getFaction(initId);
		
		
	}

	
	/**
	 * Clears the owner of the bundle.
	 * 
	 * @param bundleId bundle id
	 */
	public void clearOwner(Integer bundleId) {
		claims.remove(bundleId);
	}
	
	
	
	// Timing:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.MinuteTicker#clockMinuteTick()
	 */
	@Override
	public boolean clockSecondTick() {
		

		Set<Integer> bundleIds = new HashSet<Integer>(claiming.keySet());
		
		for (Integer bundleId : bundleIds) {
			
			// Don't count if active:
			if(claimActive.contains(bundleId)){
				claimActive.remove(bundleId);
				continue;
			}
			
			// Claiming:
			Bundle bundle = BundleManager.manager().getBundle(bundleId);
			if(bundle == null){
				SagaLogger.severe(getClass(), "failed to retrieve chunk bundle for " + bundleId);
				progress.remove(bundleId);
				claiming.remove(bundleId);
				continue;
			}
			
			// Decrease claimed:
			Integer level = 0;
			if(bundle instanceof Settlement) level = ((Settlement) bundle).getLevel();
			
			Double decrease = FactionConfiguration.config().getUnclaimSpeed(level) / 60;
			modifyProgress(bundleId, -decrease);
			
			// Inform:
			ArrayList<TownSquare> claimable = bundle.getBuildings(TownSquare.class);
			for (TownSquare townSquare : claimable) {
				townSquare.informUnclaim();
			}
			
		}
		
		return true;

			
	}
	

	
	// Checks:
	/**
	 * Checks if the bundle is available for claiming.
	 * 
	 * @param bundleId bundle ID
	 * @param sagaPlayers players on the bundle
	 * @return true if available claiming
	 */
	public boolean checkAvailable(Integer bundleId, ArrayList<SagaPlayer> sagaPlayers) {


		Integer initId = getInitFactonId(bundleId, sagaPlayers);
		
		Integer ownerId = getOwningFactionId(bundleId);
		
		// No initiator:
		if(initId == null){
			return false;
		}
		
		// No owner:
		if(ownerId == null) return true;
		
		// Already owner:
		return !initId.equals(ownerId);
		
		
	}
	
	/**
	 * Checks if claiming faction is the one claiming.
	 * 
	 * @param bundleId bundle ID
	 * @param sagaPlayers players on the bundle
	 * @return true if is claiming
	 */
	public boolean checkClaimer(Integer bundleId, ArrayList<SagaPlayer> sagaPlayers) {


		Integer claimFactionId = getClaimerId(bundleId);
		HashSet<Integer> allClaimerIds = getAllFactionIds(sagaPlayers);
		
		if(!allClaimerIds.contains(claimFactionId)) return false;
		
		return true;
		
		
	}

	/**
	 * Checks if the faction has not reached its claim limit.
	 * 
	 * @param faction faction
	 * @return true if the faction can claim
	 */
	public boolean checkClaimLimit(Faction faction) {
		return findSettlementsIds(faction.getId()).length < FactionConfiguration.config().getMaxClaims();
	}
	
	
	
	// Ranks:
	/**
	 * Gets all ranks for the given faction.
	 * 
	 * @param factionId faction ID
	 * @return faction ranks
	 */
	public Hashtable<String, Double> getRanks(Integer factionId) {

		
		Hashtable<String, Double> ranks = new Hashtable<String, Double>();
		
		Settlement[] settlements = findSettlements(factionId);
		for (int i = 0; i < settlements.length; i++) {
			
			ArrayList<Building> buildings = settlements[i].getBuildings();
			for (Building building : buildings) {
				
				Set<String> bldranks = building.getDefinition().getAllRanks();
				for (String rank : bldranks) {
					
					Double count = ranks.get(rank);
					if(count == null) count = 0.0;
					count+= building.getDefinition().getRanks(rank);
					ranks.put(rank, count);
					
				}
				
			}
			
		}
		
		return ranks;
		
		
	}
	

	
	// Load unload:
	/**
	 * Loads the manager.
	 * 
	 * @return experience configuration
	 */
	public static FactionClaimManager load(){

		
		// Inform:
		SagaLogger.info("Loading faction claims.");
		
		// New:
		if(!WriterReader.checkExists(Directory.FACTION_CLAIMS)){
			
			instance = new FactionClaimManager("");
			save();
        	
        }
		
		// Load:
		else{
			
			try {
				
				instance = WriterReader.read(Directory.FACTION_CLAIMS, FactionClaimManager.class);
				
			} catch (FileNotFoundException e) {
				
				instance = new FactionClaimManager("");
				
			} catch (IOException e) {
				
				SagaLogger.severe(FileNotFoundException.class, "failed to load");
				instance = new FactionClaimManager("");
				
			} catch (JsonParseException e) {
				
				SagaLogger.severe(FileNotFoundException.class, "failed to parse");
				SagaLogger.info("Parse message :" + e.getMessage());
				instance = new FactionClaimManager("");
				
			}
			
        }
		
		// Complete:
		instance.complete();
		
		// Clock:
		Clock.clock().enableSecondTick(instance);
		
		return instance;
		
		
	}

	/**
	 * Unloads the manager.
	 * 
	 */
	public static void unload(){

		
		// Inform:
		SagaLogger.info("Unloading faction claims.");
		
		save();
		
		instance = null;
		
		
	}
	
	/**
	 * Saves the manager.
	 * 
	 */
	public static void save(){

		
		// Inform:
		SagaLogger.info("Saving faction claims.");
		
		try {
			
			WriterReader.write(Directory.FACTION_CLAIMS, instance);
			
		} catch (IOException e) {
			
			SagaLogger.severe(FileNotFoundException.class, "write failed");
			SagaLogger.info("Write failure cause:" + e.getClass().getSimpleName() + ":" + e.getMessage());
			
		}
		
	}
	
	
}
