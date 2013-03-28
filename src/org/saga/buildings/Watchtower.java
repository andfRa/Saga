package org.saga.buildings;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.entity.Monster;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.saga.Clock.DaytimeTicker;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.settlements.SagaChunk;

public class Watchtower extends Building implements DaytimeTicker{

	
	/**
	 * True if is on cool down.
	 */
	transient boolean isOnCooldown;
	
	/**
	 * Seconds left for the cool down.
	 */
	transient int cooldownLeft;
	
	/**
	 * Protected saga chunks.
	 */
	transient ArrayList<SagaChunk> protectedChunks = null;
	
	/**
	 * Spotted mob report.
	 */
	transient Hashtable<String, Integer> mobReport = null;
	

	
	// Initialisation:
	/**
	 * Creates a building from the definition.
	 * 
	 * @param definition building definition
	 */
	public Watchtower(BuildingDefinition definition) {
		
		super(definition);
		
		protectedChunks = new ArrayList<SagaChunk>();
		mobReport = new Hashtable<String, Integer>();
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#completeExtended()
	 */
	@Override
	public boolean complete() throws InvalidBuildingException {
		

		boolean integrity = super.complete();
		
		
		// Transient:
		isOnCooldown = false;
		cooldownLeft = 0;
		protectedChunks = new ArrayList<SagaChunk>();
		mobReport = new Hashtable<String, Integer>();
		
		return integrity;
		
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#enable()
	 */
	@Override
	public void enable() {
		
		super.enable();
		
		// Protect:
		if(getSagaChunk() == null){
			return;
		}
		ArrayList<SagaChunk> protectedChunks = sagaChunk.getAdjacent(getRadius());
		this.protectedChunks = protectedChunks;
		
		
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
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#perform()
	 */
	@Override
	public void perform() {
		
		
		// Reset the report:
		mobReport = new Hashtable<String, Integer>();
		
		
	}

	
	
	// Event:
	/**
	 * Gets the protection radius.
	 * 
	 * @return protection radius
	 */
	private Integer getRadius() {

		return 4;

	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onCreatureSpawn(org.bukkit.event.entity.CreatureSpawnEvent, org.saga.chunkGroups.SagaChunk)
	 */
	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event, SagaChunk locationChunk) {
		
		
		// Ignore cancelled:
		if(event.isCancelled()) return;
		
		// Only naturals:
		if(event.getSpawnReason() != SpawnReason.NATURAL) return;
		
		// Only above sea level:
		if(event.getLocation().getY() < event.getLocation().getWorld().getSeaLevel()) return;
		  
		// Check if the chunk is under protection:
		if(!protectedChunks.contains(locationChunk)) return;
		
		// Only hostiles:
		if(!(event.getEntity() instanceof Monster)) return;
		
		// Take control of the event:
		event.setCancelled(true);
		
		String spottedName = event.getEntity().getClass().getSimpleName().replace("Craft", "").toLowerCase();
		Integer spottedAmount = mobReport.get(spottedName);
		if(spottedAmount == null) spottedAmount = 0;
		spottedAmount++;
		mobReport.put(spottedName, spottedAmount);
		
		
	}
	
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#getSpecificStats()
	 */
	@Override
	public ArrayList<String> getSpecificStats() {
		
		
		ArrayList<String> rList = new ArrayList<String>();
		StringBuffer eString = new StringBuffer();
		Enumeration<String> spotted = mobReport.keys();
		
		// Radius and protected:
		rList.add("Spot radius: " + getRadius() + " Protected: " + protectedChunks.size());
		
		// Spotted:
		eString = new StringBuffer();
		boolean first = true;
		while (spotted.hasMoreElements()) {
			String mob = spotted.nextElement();
			Integer amount = mobReport.get(mob);
			
			if(!first){
				eString.append(", ");
			}else{
				eString.append("\n");
				eString.append("Spotted today: ");
				first = false;
			}
			
			eString.append(amount + " " + mob);
			if(amount > 1) eString.append("s");
			
		}
		if(eString.length() > 0){
			rList.add(eString.toString());
		}
		
		return rList;
		
		
	}
	
	
}
