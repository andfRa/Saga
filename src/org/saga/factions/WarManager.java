package org.saga.factions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.saga.Clock;
import org.saga.Clock.SecondTicker;
import org.saga.SagaLogger;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;

public class WarManager implements SecondTicker{


	/**
	 * Instance of the manager.
	 */
	transient private static WarManager instance;

	/**
	 * Gets the manager.
	 * 
	 * @return manager
	 */
	public static WarManager manager() {
		return instance;
	}

	
	/**
	 * Wars declared.
	 */
	private Hashtable<Integer, HashSet<Integer>> warsDeclared;

	/**
	 * Alliances declared.
	 */
	private Hashtable<Integer, HashSet<Integer>> alliancesDeclared;
	
	/**
	 * Times since when the faction has no settlements.
	 */
	private Hashtable<Integer, Long> defeatTimes;

	/**
	 * Times since when the faction declared peace.
	 */
	private Hashtable<Integer, Hashtable<Integer, Long>> peaceTimes;
	
	
	
	// Initialisation:
	/**
	 * Initialises the manager.
	 * 
	 * @param name nothing, prevents gson
	 */
	public WarManager(String name) {
		
		warsDeclared = new Hashtable<Integer, HashSet<Integer>>();
		alliancesDeclared = new Hashtable<Integer, HashSet<Integer>>();
		defeatTimes = new Hashtable<Integer, Long>();
		peaceTimes = new Hashtable<Integer, Hashtable<Integer,Long>>();
		
	}

	/**
	 * Fixes all problematic fields.
	 * 
	 */
	public void complete() {
		

		if(warsDeclared == null){
			SagaLogger.nullField(getClass(), "warsDeclared");
			warsDeclared = new Hashtable<Integer, HashSet<Integer>>();
		}
		
		if(alliancesDeclared == null){
			SagaLogger.nullField(getClass(), "alliancesDeclared");
			alliancesDeclared = new Hashtable<Integer, HashSet<Integer>>();
		}
		
		if(defeatTimes == null){
			SagaLogger.nullField(getClass(), "defeatTimes");
			defeatTimes = new Hashtable<Integer, Long>();
		}
		
		if(peaceTimes == null){
			SagaLogger.nullField(getClass(), "peaceTimes");
			peaceTimes = new Hashtable<Integer, Hashtable<Integer,Long>>();
		}
		
		
	}
	
	
	
	// War declaration:
	/**
	 * Adds a war declaration.
	 * 
	 * @param attacker faction attacker
	 * @param defender faction defender
	 */
	private void addWarDeclaration(Integer attacker, Integer defender) {
		
		HashSet<Integer> declared = warsDeclared.get(attacker);
		if(declared == null){
			declared = new HashSet<Integer>();
			warsDeclared.put(attacker, declared);
		}
		
		declared.add(defender);
		
	}
	
	/**
	 * Removes a war declaration.
	 * 
	 * @param attackerID attacker faction ID
	 * @param defenderID defender faction ID
	 */
	private void removeWarDeclaration(Integer attackerID, Integer defenderID) {
		
		HashSet<Integer> declared = warsDeclared.get(attackerID);
		if(declared == null) return;
		
		declared.remove(defenderID);
		if(declared.size() == 0) warsDeclared.remove(attackerID);
		
	}
	
	/**
	 * Gets war declarations.
	 * 
	 * @param attackerID attacker faction ID
	 * @return war declarations
	 */
	public HashSet<Integer> getWarDeclarationIDs(Integer attackerID) {
		
		HashSet<Integer> declared = warsDeclared.get(attackerID);
		if(declared == null) return new HashSet<Integer>();
		return declared;
		
	}
	
	/**
	 * Gets war declarations.
	 * 
	 * @param factionID faction ID
	 * @return war declarations
	 */
	public ArrayList<Faction> getWarDeclarations(Integer factionID) {
		return FactionManager.manager().getFactions(getWarDeclarationIDs(factionID));
	}
	
	
	/**
	 * Sets war between the factions.
	 * 
	 * @param faction1ID first faction ID
	 * @param faction2ID second faction ID
	 */
	private void setWar(Integer faction1ID, Integer faction2ID) {
		
		addWarDeclaration(faction1ID, faction2ID);
		addWarDeclaration(faction2ID, faction1ID);
		
	}
	
	/**
	 * Removes war between the factions.
	 * 
	 * @param faction1ID first faction ID
	 * @param faction2ID second faction ID
	 */
	private void removeWar(Integer faction1ID, Integer faction2ID) {
		
		removeWarDeclaration(faction1ID, faction2ID);
		removeWarDeclaration(faction2ID, faction1ID);
		
	}
	
	
	/**
	 * Handles war declaration.
	 * 
	 * @param faction1ID first faction ID
	 * @param faction2ID second faction ID
	 */
	public void handleDeclareWar(Integer faction1ID, Integer faction2ID) {

		setWar(faction1ID, faction2ID);
		
	}
	
	/**
	 * Handles peace declaration.
	 * 
	 * @param faction1ID first faction ID
	 * @param faction2ID second faction ID
	 */
	public void handleDeclarePeace(Integer faction1ID, Integer faction2ID) {

		removeWar(faction1ID, faction2ID);
		updatePeaceDeclareTime(faction1ID, faction2ID);
		
	}


	/**
	 * Checks if there is a war between factions.
	 * 
	 * @param faction1ID faction ID 1
	 * @param faction2ID faction ID 2
	 * @return true if there is a war
	 */
	public boolean isAtWar(Integer faction1ID, Integer faction2ID) {
		return getWarDeclarationIDs(faction1ID).contains(faction2ID);
	}
	
	
	/**
	 * Updates peace declaration time.
	 * 
	 * @param faction1ID first faction ID
	 * @param faction2ID second faction ID
	 */
	public void updatePeaceDeclareTime(Integer faction1ID, Integer faction2ID) {
		
		Hashtable<Integer, Long> peaceTimes1 = peaceTimes.get(faction1ID);
		if(peaceTimes1 == null){
			peaceTimes1 = new Hashtable<Integer, Long>();
			peaceTimes.put(faction1ID, peaceTimes1);
		}

		Hashtable<Integer, Long> peaceTimes2 = peaceTimes.get(faction2ID);
		if(peaceTimes2 == null){
			peaceTimes2 = new Hashtable<Integer, Long>();
			peaceTimes.put(faction2ID, peaceTimes2);
		}
		
		Long time = System.currentTimeMillis();
		peaceTimes1.put(faction2ID, time);
		peaceTimes2.put(faction1ID, time);
		
	}
	
	/**
	 * Gets the last time peace was declared.
	 * 
	 * @param faction1ID faction ID 1
	 * @param faction2ID faction ID 2
	 * @return time in milliseconds peace was declared, null if never
	 */
	public Long getPeaceDeclarationTime(Integer faction1ID, Integer faction2ID) {
		
		Hashtable<Integer, Long> peaceTimes1 = peaceTimes.get(faction1ID);
		if(peaceTimes1 == null) return null;
		
		return peaceTimes1.get(faction2ID);
		
	}
	
	
	
	// Alliance declaration:
	/**
	 * Adds a ally declaration.
	 * 
	 * @param attacker faction attacker
	 * @param defender faction defender
	 */
	private void addAllyDeclaration(Integer attacker, Integer defender) {
		
		HashSet<Integer> declared = alliancesDeclared.get(attacker);
		if(declared == null){
			declared = new HashSet<Integer>();
			alliancesDeclared.put(attacker, declared);
		}
		
		declared.add(defender);
		
	}
	
	/**
	 * Removes a ally declaration.
	 * 
	 * @param attackerID attacker faction ID
	 * @param defenderID defender faction ID
	 */
	private void removeAllyDeclaration(Integer attackerID, Integer defenderID) {
		
		HashSet<Integer> declared = alliancesDeclared.get(attackerID);
		if(declared == null) return;
		
		declared.remove(defenderID);
		if(declared.size() == 0) alliancesDeclared.remove(attackerID);
		
	}
	
	/**
	 * Gets ally declarations.
	 * 
	 * @param attackerID attacker faction ID
	 * @return ally declarations
	 */
	public HashSet<Integer> getAllyDeclarationIDs(Integer attackerID) {
		
		HashSet<Integer> declared = alliancesDeclared.get(attackerID);
		if(declared == null) return new HashSet<Integer>();
		return declared;
		
	}
	
	/**
	 * Gets ally declarations.
	 * 
	 * @param factionID faction ID
	 * @return ally declarations
	 */
	public ArrayList<Faction> getAllyDeclarations(Integer factionID) {
		return FactionManager.manager().getFactions(getAllyDeclarationIDs(factionID));
	}
	
	
	/**
	 * Sets alliance between the factions.
	 * 
	 * @param faction1ID first faction ID
	 * @param faction2ID second faction ID
	 */
	public void setAlliance(Integer faction1ID, Integer faction2ID) {
		
		addAllyDeclaration(faction1ID, faction2ID);
		addAllyDeclaration(faction2ID, faction1ID);
		
	}
	
	/**
	 * Removes alliance between the factions.
	 * 
	 * @param faction1ID first faction ID
	 * @param faction2ID second faction ID
	 */
	public void removeAlliance(Integer faction1ID, Integer faction2ID) {
		
		removeAllyDeclaration(faction1ID, faction2ID);
		removeAllyDeclaration(faction2ID, faction1ID);
		
	}

	/**
	 * Checks if there is a ally between factions.
	 * 
	 * @param faction1ID faction ID 1
	 * @param faction2ID faction ID 2
	 * @return true if there is a ally
	 */
	public boolean isAlly(Integer faction1ID, Integer faction2ID) {
		return getAllyDeclarationIDs(faction1ID).contains(faction2ID);
	}
	
	
	
	// Timing:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.MinuteTicker#clockMinuteTick()
	 */
	@Override
	public boolean clockSecondTick() {
		
		
		
		
		
		return true;
		
			
	}
	
	
	
	// Load unload:
	/**
	 * Loads the manager.
	 * 
	 * @return experience configuration
	 */
	public static WarManager load(){

		
		// Inform:
		SagaLogger.info("Loading wars.");
		
		// New:
		if(!WriterReader.checkExists(Directory.WARS)){
			
			instance = new WarManager("");
			save();
        	
        }
		
		// Load:
		else{
			
			try {
				
				instance = WriterReader.read(Directory.WARS, WarManager.class);
				
			} catch (FileNotFoundException e) {
				
				instance = new WarManager("");
				
			} catch (IOException e) {
				
				SagaLogger.severe(WarManager.class, "failed to load");
				instance = new WarManager("");
				
			} catch (JsonParseException e) {
				
				SagaLogger.severe(WarManager.class, "failed to parse");
				SagaLogger.info("Parse message :" + e.getMessage());
				instance = new WarManager("");
				
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
		SagaLogger.info("Unloading wars.");
		
		save();
		
		instance = null;
		
		
	}
	
	/**
	 * Saves the manager.
	 * 
	 */
	public static void save(){

		
		// Inform:
		SagaLogger.info("Saving wars.");
		
		try {
			
			WriterReader.write(Directory.WARS, instance);
			
		} catch (IOException e) {
			
			SagaLogger.severe(WarManager.class, "write failed");
			SagaLogger.info("Write failure cause:" + e.getClass().getSimpleName() + ":" + e.getMessage());
			
		}
		
	}
	
	
}
