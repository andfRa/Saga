package org.saga.buildings;

import java.util.ArrayList;

import org.bukkit.entity.Creature;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.saga.Saga;
import org.saga.buildings.BuildingDefinition.BuildingPermission;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.listeners.events.SagaPvpEvent;
import org.saga.listeners.events.SagaPvpEvent.PvpDenyReason;
import org.saga.messages.ChunkGroupMessages;
import org.saga.messages.SagaMessages;
import org.saga.player.SagaPlayer;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;

public class Home extends Building {

	
	/**
	 * residents.
	 */
	private ArrayList<String> residents;
	
	
	// Initialization:
	/**
	 * Initializes
	 * 
	 * @param pointCost point cost
	 * @param moneyCost money cost
	 * @param proficiencies proficiencies
	 */
	private Home(String name) {
		
		super("");
		
		residents = new ArrayList<String>();
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#completeExtended()
	 */
	@Override
	public boolean completeExtended() {
		

		boolean integrity = true;
		
		if(residents == null){
			residents = new ArrayList<String>();
			Saga.severe(this, "failed to initialize residents field", "setting default");
			integrity = false;
		}
		
		return integrity;
		
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#blueprint()
	 */
	@Override
	public Building blueprint() {
		return new Home("");
	}

	
	// Residents:
	/**
	 * Checks if the player is an resident.
	 * 
	 * @param playerName player name
	 * @return true if resident
	 */
	private boolean isResident(String playerName) {

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

	
	// Events:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerDamagedByPlayer(org.bukkit.event.entity.EntityDamageByEntityEvent, org.saga.SagaPlayer, org.saga.SagaPlayer)
	 */
	@Override
	public void onPvP(SagaPvpEvent event){
			
		// Deny pvp:
		event.setDenyReason(PvpDenyReason.SAFE_AREA);
		
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
	
	
	// Messages:
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

	public static String alreadyResident(String name) {
		return ChunkGroupMessages.negative + name + " is already a resident.";
	}
	
	public static String notResident(String name) {
		return ChunkGroupMessages.negative + name + " is not a resident.";
	}

	public static String addedResident(String name) {
		return ChunkGroupMessages.positive + "Added " + name + " to the resident list.";
	}
	
	public static String removedResident(String name) {
		return ChunkGroupMessages.positive + "Removed " + name + " from the resident list.";
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
	
	
	// Commands:
	@Command(
			aliases = {"baddresident"},
			usage = "<name>",
			flags = "",
			desc = "Add a resident to a home.",
			min = 1,
			max = 1
	)
	@CommandPermissions({"saga.user.building.home.addresident"})
	public static void addResident(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		String targetName = null;
		
		// Retrieve building:
		Home selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, Home.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
	
		// Arguments:
		targetName = args.getString(0);

		// Permission:
		if(!selectedBuilding.checkBuildingPermission(sagaPlayer, BuildingPermission.FULL)){
			sagaPlayer.message(SagaMessages.noPermission(selectedBuilding));
			return;
		}
		
		// Not a member:
		ChunkGroup chunkGroup = selectedBuilding.getChunkGroup();
		if(chunkGroup == null){
			sagaPlayer.message(SagaMessages.noPermission());
			return;
		}
		
		// Member:
		
		if(!SagaPlayer.checkExistance(targetName)){
			sagaPlayer.message(ChunkGroupMessages.nonExistantPlayer(targetName));
			return;
		}
		
		// Already a resident:
		if(selectedBuilding.isResident(targetName)){
			sagaPlayer.message(alreadyResident(targetName));
			return;
		}
		
		// Add:
		selectedBuilding.addResident(targetName);
		
		// Inform:
		sagaPlayer.message(addedResident(targetName));
		
	
	}
	
	@Command(
			aliases = {"bremoveresident"},
			usage = "<name>",
			flags = "",
			desc = "Remove a resident from a home.",
			min = 1,
			max = 1
	)
	@CommandPermissions({"saga.user.building.home.removeresident"})
	public static void removeResident(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		String targetName = null;
		
		// Retrieve building:
		Home selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, Home.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
	
		// Arguments:
		targetName = args.getString(0);
		
		// Permission:
		if(!selectedBuilding.checkBuildingPermission(sagaPlayer, BuildingPermission.FULL)){
			sagaPlayer.message(SagaMessages.noPermission(selectedBuilding));
			return;
		}
		
		// Not a member:
		ChunkGroup chunkGroup = selectedBuilding.getChunkGroup();
		if(chunkGroup == null){
			sagaPlayer.message(SagaMessages.noPermission());
			return;
		}
		
//		// Member:
//		if(!chunkGroup.hasPlayer(targetName)){
//			sagaPlayer.sendMessage(ChunkGroupMessages.notChunkGroupMember(chunkGroup, targetName));
//			return;
//		}
		
		// Already a resident:
		if(!selectedBuilding.isResident(targetName)){
			sagaPlayer.message(notResident(targetName));
			return;
		}
		
		// Remove:
		selectedBuilding.removeResident(targetName);
		
		// Inform:
		sagaPlayer.message(removedResident(targetName));
		
	
	}
	
	
	
}
