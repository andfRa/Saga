package org.saga.factions;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import org.saga.Saga;
import org.saga.settlements.SettlementDefinition;
import org.saga.utility.TwoPointFunction;

public class FactionDefinition {

	
	
	
	/**
	 * Ranks that are enabled.
	 */
	private Hashtable<String, TwoPointFunction> enabledRanks;

	
	// Initialization:
	/**
	 * Used by gson.
	 * 
	 */
	private FactionDefinition() {
	}

	/**
	 * Completes.
	 * 
	 * @return integrity
	 */
	public boolean complete() {
		

		boolean integrity=true;
		
//		if(activePlayers == null){
//			Saga.severe("SettlementDefinition failed to initialize activePlayers field. Setting default.");
//			activePlayers = new TwoPointFunction(Double.MAX_VALUE, Short.MAX_VALUE, Double.MAX_VALUE);
//			integrity = false;
//		}
		
		if(enabledRanks == null){
			enabledRanks = new Hashtable<String, TwoPointFunction>();
			Saga.severe(SettlementDefinition.class, "failed to initialize enabledRanks field", "setting default");
			integrity = false;
		}
		Enumeration<String> eRanks = enabledRanks.keys();
		while (eRanks.hasMoreElements()) {
			String rank = (String) eRanks.nextElement();
			integrity = enabledRanks.get(rank).complete() && integrity;
		}
		
		return integrity;
		
		
	}
	
	
	// Ranks:
	/**
	 * Gets the total available ranks.
	 * 
	 * @param rankName rank name
	 * @param level settlement level
	 * @return amount of enabled ranks
	 */
	public Integer getTotalRanks(String rankName, Short level) {
		
		TwoPointFunction amount = enabledRanks.get(rankName);
		if(amount == null || amount.getXRequired() > level){
			return 0;
		}
		return new Double(amount.calculateValue(level)).intValue();
		
		
	}
	
	/**
	 * Gets all rank names enabled by this rank
	 * 
	 * @param level settlement level
	 * @return enabled rank names
	 */
	public HashSet<String> getAllRanks(Short level) {
		
		
		HashSet<String> ranks = new HashSet<String>();
		
		Enumeration<String> rankNames =  enabledRanks.keys();
		
		while (rankNames.hasMoreElements()) {
			String rankName = (String) rankNames.nextElement();
			if(getTotalRanks(rankName, level) > 0){
				ranks.add(rankName);
			}
		}
		
		return ranks;

		
	}
	
	
	// Other:
	/**
	 * Returns the default definition.
	 * 
	 * @return default definition
	 */
	public static FactionDefinition defaultDefinition(){
		
		
		FactionDefinition definition = new FactionDefinition();
		definition.enabledRanks = new Hashtable<String, TwoPointFunction>();
		return definition;
		
		
	}
	
	
}
