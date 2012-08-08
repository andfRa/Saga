package org.saga.factions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

import org.saga.Clock;
import org.saga.Clock.MinuteTicker;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.chunks.ChunkBundle;
import org.saga.chunks.ChunkBundleManager;
import org.saga.config.FactionConfiguration;
import org.saga.messages.ClaimMessages;
import org.saga.player.SagaPlayer;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.settlements.Settlement;

import com.google.gson.JsonParseException;

public class FactionClaimManager implements MinuteTicker{


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
	 * Faction claim contesters.
	 */
	private Hashtable<Integer, Integer> contesters;
	
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
		contesters = new Hashtable<Integer, Integer>();
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
		
		if(contesters == null){
			SagaLogger.nullField(getClass(), "contesters");
			contesters = new Hashtable<Integer, Integer>();
		}
		
		
		// Transient:
		claimActive = new HashSet<Integer>();
		
		
	}
	
	
	
	// Initiation:
	/**
	 * Checks if claim initiation is possible.
	 * 
	 * @param faction faction
	 * @return true if claim initiation is possible
	 */
	public boolean checkInitiation(ChunkBundle bundle, ArrayList<SagaPlayer> sagaPlayers) {


		Faction initFaction = getInitFacton(bundle, sagaPlayers);
		Faction owningFaction = getOwningFaction(bundle.getId());
		
		// Initiating faction:
		if(initFaction == null) return false;
		
		// Already claimed:
		if(initFaction == owningFaction) return false;
		
		// Already contested:
		if(isContested(bundle.getId())) return false;
		
		// Check members for seizing:
		if(owningFaction != null){

			if(initFaction.getRegisteredMemberCount() < FactionConfiguration.config().getToClaimMembers()) return false;
		
			if(owningFaction.getRegisteredMemberCount() < FactionConfiguration.config().getToClaimMembers()) return false;
			
		}
		
		return true;
	
		
	}
	
	/**
	 * Initiates claiming.
	 * 
	 * @param bundle bundle
	 * @param faction faction
	 */
	public void initiate(ChunkBundle bundle, Faction faction) {

		setContester(bundle.getId(), faction.getId());
		
	}
	
	/**
	 * Gets the initiating faction.
	 * 
	 * @param bundle chunk bundle
	 * @param sagaPlayers saga player
	 * @return initiating faction, null if none
	 */
	public static Faction getInitFacton(ChunkBundle bundle, ArrayList<SagaPlayer> sagaPlayers) {

		
		Faction owningFaction = FactionClaimManager.manager().getOwningFaction(bundle.getId());
		
		// Get faction:
		for (SagaPlayer sagaPlayer : sagaPlayers) {
			
			// Ignore members:
			if(bundle.isMember(sagaPlayer.getName())) continue;
			
			Faction playerFaction = sagaPlayer.getFaction();
			
			// Ignore not factions:
			if(playerFaction == null) continue;
			
			// Other factions:
			if(playerFaction != owningFaction) return playerFaction;
			
		}
		
		return null;
		
		
	}
	
	
	
	// Claiming:
	/**
	 * Checks if the bundle can is being claimed.
	 * 
	 * @param bundle bundle
	 * @param sagaPlayers players on the bundle
	 * @return true if being claimed
	 */
	public boolean checkClaiming(ChunkBundle bundle, ArrayList<SagaPlayer> sagaPlayers) {


		Faction contFaction = getContesterFaction(bundle.getId());

		// Multiple factions:
		HashSet<Faction> allContesters = getAllContesters(sagaPlayers);
		
		// Contested:
		if(!isContested(bundle.getId())) return false;
		
		// Contesting faction not present:
		if(!allContesters.contains(contFaction)) return false;
		
		return true;
		
		
	}

	/**
	 * Checks if the bundle is being contested.
	 * 
	 * @param bundle chunk bundle
	 * @param sagaPlayers saga players
	 * @return true if contested
	 */
	public boolean checkContesting(ChunkBundle bundle, ArrayList<SagaPlayer> sagaPlayers) {

		
		Faction owningFaction = getOwningFaction(bundle.getId());

		// Multiple factions:
		HashSet<Faction> allContesters = getAllContesters(sagaPlayers);
		
		// Owning faction present:
		if(allContesters.contains(owningFaction)) return true;
		
		return false;
		
		
	}
	
	/**
	 * Progresses the chunk bundle claim.
	 * 
	 * @param bundle chunk bundle
	 * @param amount amount claimed
	 */
	public void progressClaim(ChunkBundle bundle, Double amount) {

		
		Double progress = modifyProgress(bundle.getId(), amount);
		
		// Claimed:
		if(progress >= 1.0){
			
			Integer bundleId = bundle.getId();
			Faction faction = getContesterFaction(bundleId);
			
			clearContester(bundleId);
			clearProgress(bundleId);
			
			if(faction == null){
				SagaLogger.severe(getClass(), "failed to retrieve faction");
				return;
			}
			
			setOwningId(bundleId, faction.getId());
			
			// Inform:
			Saga.broadcast(ClaimMessages.claimedBcast(bundle, faction));
			
		}
		
		// In progress:
		else{
		
			// Set as active:
			claimActive.add(bundle.getId());
			
		}
		
		
		
	}
	
	/**
	 * Gets all contesting factions.
	 * 
	 * @param sagaPlayers all players
	 * @return all contesting factions
	 */
	public static HashSet<Faction> getAllContesters(ArrayList<SagaPlayer> sagaPlayers) {

		
		HashSet<Faction> allContesters = new HashSet<Faction>();
		
		for (SagaPlayer sagaPlayer : sagaPlayers) {
			if(sagaPlayer.getFaction() != null) allContesters.add(sagaPlayer.getFaction());
		}
		
		return allContesters; 
			
		
	}
	
	
	
	// Fields:
	/**
	 * Get owning faction ID.
	 * 
	 * @param bundleId chunk bundle ID
	 * @return owning faction ID, null if none
	 */
	public Integer getOwningId(Integer bundleId) {
		return claims.get(bundleId);
	}
	
	/**
	 * Get owning faction ID.
	 * 
	 * @param bundleId chunk bundle ID
	 * @return owning faction ID, null if none
	 * @param factionId faction ID
	 */
	public void setOwningId(Integer bundleId, Integer factionId) {
		claims.put(bundleId, factionId);
	}
	
	/**
	 * Get owning faction.
	 * 
	 * @param bundleId chunk bundle ID
	 * @return owning faction, null if none
	 */
	public Faction getOwningFaction(Integer bundleId) {
		
		Integer id = getOwningId(bundleId);
		if(id == null) return null;
		
		return FactionManager.manager().getFaction(id);

	}
	
	/**
	 * Checks if the chunk bundle has an owner.
	 * 
	 * @param bundle chunk bundle
	 * @return true if has an owner
	 */
	public Boolean hasOwner(ChunkBundle bundle) {
		return claims.get(bundle.getId()) != null;
	}
	
	
	/**
	 * Checks if the bundle is claimed by a faction.
	 * 
	 * @param bundleId bundle ID
	 * @return true if contested 
	 */
	public boolean isContested(Integer bundleId) {

		return contesters.get(bundleId) != null;
		
	}
	
	/**
	 * Gets the contester faction ID.
	 * 
	 * @param bundleId bundle ID
	 * @return contester faction ID
	 */
	public Integer getContester(Integer bundleId) {

		Integer contester = contesters.get(bundleId);
		if(contester == null) contester = -1;
		
		return contester;
		
	}

	/**
	 * Get contester faction.
	 * 
	 * @param bundleId chunk bundle ID
	 * @return contester faction, null if none
	 */
	public Faction getContesterFaction(Integer bundleId) {
		
		Integer id = getContester(bundleId);
		if(id == null) return null;
		
		return FactionManager.manager().getFaction(id);

	}
	
	/**
	 * Gets the contester faction ID.
	 * 
	 * @param bundleId bundle ID
	 * @param factionId faction ID
	 * @return old contester faction ID
	 */
	public Integer setContester(Integer bundleId, Integer factionId) {

		return contesters.put(bundleId, factionId);
		
	}
	
	/**
	 * Clears the contester faction.
	 * 
	 * @param bundleId bundle ID
	 * @return cleared contester, null if none
	 */
	public Integer clearContester(Integer bundleId) {

		return contesters.remove(bundleId);
		
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
			contesters.remove(bundleId);
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
	
	
	
	// Factions and settlements:
	/**
	 * Removes a faction.
	 * 
	 * @param factionId faction ID
	 */
	public void removeFaction(Integer factionId) {

		Set<Integer> bundleIds;

		// Claimed:
		bundleIds = claims.keySet();
		for (Integer bundleId : bundleIds) {
			if(claims.get(bundleId) == factionId) claims.remove(bundleId);
		}

		// Contesters and progress:
		bundleIds = contesters.keySet();
		for (Integer bundleId : bundleIds) {
			if(contesters.get(bundleId) == factionId){
				progress.remove(bundleId);
				contesters.remove(bundleId);
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
		contesters.remove(bundleId);
		
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
			
			ChunkBundle bundle = ChunkBundleManager.manager().getChunkBundle(entry.getKey());
			
			if(bundle == null || !(bundle instanceof Settlement)) continue;
			
			settlements.add(((Settlement) bundle));
			
		}
		
		return settlements.toArray(new Settlement[settlements.size()]);
	
		
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

	
	
	// Timing:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.MinuteTicker#clockMinuteTick()
	 */
	@Override
	public boolean clockMinuteTick() {
		
		
		Set<Integer> bundleIds = contesters.keySet();
		
		for (Integer bundleId : bundleIds) {
			
			// Not count if active:
			if(claimActive.contains(bundleId)){
				claimActive.remove(bundleId);
				continue;
			}
			
			// Contested:
			ChunkBundle bundle = ChunkBundleManager.manager().getChunkBundle(bundleId);
			if(bundle == null){
				SagaLogger.severe(getClass(), "failed to retrieve chunk bundle for " + bundleId);
				continue;
			}
			
			// Decrease claimed:
			Integer level = 0;
			if(bundle instanceof Settlement) level = ((Settlement) bundle).getLevel();
			
			Double decrease = FactionConfiguration.config().getUnclaimSpeed(level);
			modifyProgress(bundleId, -decrease);
			
			
		}
		
		return true;

			
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
		Clock.clock().registerMinuteTick(instance);
		
		return instance;
		
		
	}

	/**
	 * Unloads the manager.
	 * 
	 */
	public static void unload(){

		
		// Inform:
		SagaLogger.info("Unloading faction claims.");
		
		// Clock:
		Clock.clock().unregisterMinuteTick(instance);
		
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
