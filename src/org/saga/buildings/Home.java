package org.saga.buildings;

import java.util.ArrayList;

import org.bukkit.entity.Creature;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.saga.Saga;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.listeners.events.SagaEntityDamageEvent.PvPOverride;
import org.saga.player.SagaPlayer;

public class Home extends Building {

	
	/**
	 * residents.
	 */
	private ArrayList<String> residents;
	
	
	// Initialisation:
	/**
	 * Creates a building from the definition.
	 * 
	 * @param definition building definition
	 */
	public Home(BuildingDefinition definition) {
		
		super(definition);
		
		residents = new ArrayList<String>();
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#completeExtended()
	 */
	@Override
	public boolean complete() throws InvalidBuildingException {
		

		boolean integrity = super.complete();
		
		if(residents == null){
			residents = new ArrayList<String>();
			Saga.severe(this, "failed to initialize residents field", "setting default");
			integrity = false;
		}
		
		return integrity;
		
		
	}

	
	// Residents:
	/**
	 * Checks if the player is an resident.
	 * 
	 * @param playerName player name
	 * @return true if resident
	 */
	public boolean isResident(String playerName) {

		return residents.contains(playerName) || residents.contains(playerName.toLowerCase());
		
	}
	
	/**
	 * Checks if the player is an resident.
	 * 
	 * @param playerName player name
	 * @return true if resident
	 */
	public void addResident(String playerName) {


		if(isResident(playerName.toLowerCase())){
			Saga.severe(this, "tried to add an already existing resident", "ignoring request");
			return;
		}
		
		residents.add(playerName.toLowerCase());
		
		
	}
	
	/**
	 * Checks if the player is an resident.
	 * 
	 * @param playerName player name
	 * @return true if resident
	 */
	public void removeResident(String playerName) {


		if(!isResident(playerName)){
			Saga.severe(this, "tried to add an non-existing resident", "ignoring request");
			return;
		}
		
		residents.remove(playerName);
		residents.remove(playerName.toLowerCase());
		
		
	}
	
	/**
	 * Gets all residents.
	 * 
	 * @return all residents
	 */
	public ArrayList<String> getResidents() {
		return new ArrayList<String>(residents);
	}
	
	
	// Display:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		
		
		ArrayList<String> residents = getResidents();
		StringBuffer rString = new StringBuffer();
		
		for (int i = 0; i < residents.size(); i++) {
			
			if(i == 0){
				
			}else if(i == residents.size() - 1){
				rString.append(" and ");
			}else{
				rString.append(", ");
			}
			rString.append(residents.get(i) + "s");
			
		}
		
		if(residents.size() == 0){
			rString.append("unoccupied");
		}
		
		rString.append(" ");
		
		rString.append(super.getDisplayName());
		
		return rString.toString();
		
		
	}

	@Override
	public ArrayList<String> getSpecificStats() {
		
		
		ArrayList<String> residents = getResidents();
		String rString = "";
		ArrayList<String> rList = new ArrayList<String>();
		
		if(residents.size() == 0){
			rString = "Residents: none";
		}else if(residents.size() == 1){
			rString = "Resident: ";
		}else{
			rString = "Residents: ";
		}
		
		for (int i = 0; i < residents.size(); i++) {
			
			if(i != 0){
				rString += ", ";
			}
			
			rString += residents.get(i);
			
		}
		
		rList.add(rString);
		
		return rList;
		
		
	}

	
	// Events:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerDamagedByPlayer(org.bukkit.event.entity.EntityDamageByEntityEvent, org.saga.SagaPlayer, org.saga.SagaPlayer)
	 */
	@Override
	public void onPvP(SagaEntityDamageEvent event){
			
		// Deny pvp:
		event.addPvpOverride(PvPOverride.SAFE_AREA);
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerDamagedByCreature(org.bukkit.event.entity.EntityDamageByEntityEvent, org.bukkit.entity.Creature, org.saga.SagaPlayer)
	 */
	@Override
	public void onPlayerDamagedByCreature(EntityDamageByEntityEvent event, Creature damager, SagaPlayer damaged) {

		// Disable cvp:
		event.setCancelled(true);
		
	}
	
	
	// Permissions:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#canBuild(org.saga.SagaPlayer)
	 */
	@Override
	public boolean canBuild(SagaPlayer sagaPlayer) {

		
		ChunkGroup chunkGroup = getChunkGroup();
		if(chunkGroup != null && chunkGroup.isOwner(sagaPlayer.getName()) || sagaPlayer.isAdminMode()){
			return true;
		}
		
		return isResident(sagaPlayer.getName());
		
		
	}
	
	
}
