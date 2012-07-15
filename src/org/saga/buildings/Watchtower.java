package org.saga.buildings;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.entity.Enderman;
import org.bukkit.entity.Monster;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.saga.Clock;
import org.saga.Clock.TimeOfDayTicker;
import org.saga.chunkGroups.SagaChunk;
import org.saga.exceptions.InvalidBuildingException;

public class Watchtower extends Building implements TimeOfDayTicker{

	// TODO: Improve watchtower
	
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
		
		Clock.clock().registerTimeOfDayTick(this);
		
		// Protect:
		if(getSagaChunk() == null){
			return;
		}
		ArrayList<SagaChunk> protectedChunks = originChunk.getAdjacent(getRadius());
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
		
		Clock.clock().unregisterTimeOfDayTick(this);
		
		// Unprotect:
		this.protectedChunks = new ArrayList<SagaChunk>();
		
	}
	
	
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
		
		
		// Ignore canceled events:
		if(event.isCancelled()){
			return;
		}
		
		// Check if the chunk is under protection:
		if(!protectedChunks.contains(locationChunk)){
			return;
		}
		
		if(!(event.getEntity() instanceof Enderman) && !(event.getEntity() instanceof Monster)){
			return;
		}
		
		// Take control of the event:
		event.setCancelled(true);
		
		String spottedName = event.getEntity().getClass().getSimpleName().replace("Craft", "").toLowerCase();
		Integer spottedAmount = mobReport.get(spottedName);
		if(spottedAmount == null) spottedAmount = 0;
		spottedAmount++;
		mobReport.put(spottedName, spottedAmount);
		
		
	}
	
	
	// Report:
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
			String mob = (String) spotted.nextElement();
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
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.TimeOfDayTicker#timeOfDayTick(org.saga.Clock.TimeOfDayTicker.TimeOfDay)
	 */
	@Override
	public void timeOfDayTick(TimeOfDay timeOfDay) {
		
		
		// Reset the report each morning:
		if(timeOfDay.equals(TimeOfDay.SUNRISE)){
			mobReport = new Hashtable<String, Integer>();
			return;
		}
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.TimeOfDayTicker#checkWorld(java.lang.String)
	 */
	@Override
	public boolean checkWorld(String worldName) {
		
		SagaChunk originChunk = getSagaChunk();
		
		if(originChunk == null) return false;
		
		return originChunk.getWorldName().equals(worldName);
		
	}
	
	
}
