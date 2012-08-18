package org.saga.commands;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.buildings.Building;
import org.saga.chunks.Bundle;
import org.saga.chunks.BundleManager;
import org.saga.chunks.SagaChunk;
import org.saga.config.EconomyConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.ProficiencyConfiguration.InvalidProficiencyException;
import org.saga.config.SettlementConfiguration;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.messages.BuildingMessages;
import org.saga.messages.EconomyMessages;
import org.saga.messages.FactionMessages;
import org.saga.messages.GeneralMessages;
import org.saga.messages.HelpMessages;
import org.saga.messages.PlayerMessages;
import org.saga.messages.SettlementEffects;
import org.saga.messages.SettlementMessages;
import org.saga.player.Proficiency;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Settlement;
import org.saga.settlements.Settlement.SettlementPermission;
import org.saga.statistics.StatisticsManager;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;


public class SettlementCommands {

	
	public static Integer maximumNameLength = 15;
	
	public static Integer minimumNameLenght = 3;
	
	
	
	// Territory:
	@Command(
		aliases = {"ssettle", "settle"},
		usage = "<settlement name>",
		flags = "",
		desc = "Create a new settlement.",
		min = 1,
		max = 1
	)
	@CommandPermissions({"saga.user.settlement.create"})
	public static void settle(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	
		
		// Arguments:
		String settlementName = GeneralMessages.nameFromArg(args.getString(0));

		// Location chunk:
		SagaChunk selChunk = sagaPlayer.getSagaChunk();
		Location location = sagaPlayer.getLocation();
		if(location == null){
			SagaLogger.severe(SettlementCommands.class, "saga player location is null for " + sagaPlayer.getName());
			sagaPlayer.error("failed to retrieve "+ sagaPlayer +" player location");
			return;
		}
		
		// Already claimed:
		if(selChunk != null){
			sagaPlayer.message(SettlementMessages.chunkClaimed());
			return;
		}

		// Adjacent:
		ArrayList<SagaChunk> adjacent = SagaChunk.getAllAdjacent(location.getChunk());
		for (SagaChunk sagaChunk : adjacent) {
			
			if(sagaChunk.getChunkBundle() != null){
				sagaPlayer.message(SettlementMessages.claimAdjacentDeny());
				return;
			}
			
		}
		
		// Settles:
		if(sagaPlayer.getBundle() != null){
			sagaPlayer.message(SettlementMessages.oneChunkGroupAllowed());
			return;
		}
		
		// Fix spaces:
		while(settlementName.contains("  ")){
			settlementName = settlementName.replaceAll("  ", " ");
		}
		
		// Validate name:
		if(!validateName(settlementName)){
			sagaPlayer.message(SettlementMessages.invalidName());
			return;
		}
		
		// Check name:
		if(BundleManager.manager().getChunkBundleWithName(settlementName) != null){
			sagaPlayer.message(FactionMessages.inUse(settlementName));
			return;
		}
		
		// Settle:
		Settlement settlement = new Settlement(settlementName);
		settlement.complete();
		selChunk = new SagaChunk(location);
		settlement.addChunk(selChunk);
		Settlement.create(settlement, sagaPlayer);
		
		// Inform:
		Saga.broadcast(SettlementMessages.settledBcast(sagaPlayer, settlement));

		// Play effect:
		SettlementEffects.playClaim(sagaPlayer, selChunk);
		
		
	}
	
	@Command(
		aliases = {"sclaim", "claim"},
		usage = "[settlement name]",
		flags = "",
		desc = "Claim the chunk of land.",
		min = 0,
		max = 1
	)
	@CommandPermissions({"saga.user.settlement.claim"})
	public static void claim(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


		Bundle selChunkBundle = null;

		// Arguments:
		if(args.argsLength() == 1){
			
			// Chunk bundle:
			String groupName = GeneralMessages.nameFromArg(args.getString(0));
			selChunkBundle = BundleManager.manager().getChunkBundleWithName(groupName);
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.noChunkBundle(groupName));
				return;
			}
			
		}else{
			
			// Chunk bundle:
			selChunkBundle = sagaPlayer.getBundle();
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.notMember());
				return;
			}
			
		}
		
		// Location chunk:
		SagaChunk locationChunk = sagaPlayer.getSagaChunk();
	   	Location location = sagaPlayer.getLocation();
	   	if(location == null){
	   		SagaLogger.severe(SettlementCommands.class, "saga player location is null for " + sagaPlayer.getName());
	   		sagaPlayer.error("failed to retrieve "+ sagaPlayer +" player location");
	   		return;
	   	}
	   	Chunk bukkitChunk = location.getWorld().getChunkAt(location);
	  
	   	// Already claimed:
	   	if(locationChunk != null){
			sagaPlayer.message(SettlementMessages.chunkClaimed());
			return;
		}
	   	
		// Adjacent:
		ArrayList<SagaChunk> adjacent = SagaChunk.getAllAdjacent(location.getChunk());
		for (SagaChunk sagaChunk : adjacent) {
			
			if(sagaChunk.getChunkBundle() != selChunkBundle){
				sagaPlayer.message(SettlementMessages.claimAdjacentDeny());
				return;
			}
			
		}
	   	
		// Not adjacent:
	   	if(!selChunkBundle.isAdjacent(bukkitChunk)){
			sagaPlayer.message(SettlementMessages.chunkMustBeAdjacent());
			return;
		}
	   	
	   	// Permissions:
	   	if(!selChunkBundle.hasPermission(sagaPlayer, SettlementPermission.CLAIM)){
	   		sagaPlayer.message(GeneralMessages.noPermission());
	   		return;
	   	}
	   	
	   	// Claim points:
	   	Settlement selSettlement = null;
	   	if(selChunkBundle instanceof Settlement){
	   		selSettlement = (Settlement) selChunkBundle;
	   	}
	   	if(selSettlement == null || !selSettlement.isClaimsAvailable()){
	   		sagaPlayer.message(SettlementMessages.notEnoughClaims());
	   		return;
	   	}
	   	
		// Add a new chunk to adjacent chunk bundle:
		SagaChunk sagaChunk = new SagaChunk(bukkitChunk);
		selChunkBundle.addChunk(sagaChunk);
		
		// Inform:
		if(sagaPlayer.getBundle() == selChunkBundle){
			sagaPlayer.message(SettlementMessages.claimed(sagaChunk));
		}else{
			sagaPlayer.message(SettlementMessages.claimed(sagaChunk, selChunkBundle));
		}
		
		// Refresh:
		sagaChunk.refresh();
		
		// Play effect:
		SettlementEffects.playClaim(sagaPlayer, sagaChunk);
		
		
	}

	@Command(
		aliases = {"abandon", "unclaim", "sabandon", "sunclaim"},
		usage = "",
		flags = "",
		desc = "Abandon the chunk of land. Delete the settlement if no land is left.",
		min = 0,
		max = 0
	)
	@CommandPermissions({"saga.user.settlement.abandon"})
	public static void abandon(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

			
		// Location chunk:
	   	SagaChunk selChunk = sagaPlayer.getSagaChunk();
	   	Location location = sagaPlayer.getLocation();
	   	if(location == null){
	   		SagaLogger.severe(SettlementCommands.class, "saga player location is null for " + sagaPlayer.getName());
	   		sagaPlayer.error("failed to retrieve "+ sagaPlayer +" player location");
	   		return;
	   	}
	   	
	   	// Unclaimed:
	   	if(selChunk == null){
			sagaPlayer.message(SettlementMessages.chunkNotClaimed());
			return;
		}
	   	
	   	// Chunk bundle:
	   	Bundle selBundle = selChunk.getChunkBundle();
	   	
	   	// Permissions:
	   	if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.ABANDON)){
	   		sagaPlayer.message(GeneralMessages.noPermission());
	   		return;
	   	}
	   	
		// Remove chunk from the chunk bundle:
		selBundle.removeChunk(selChunk);
		
		// Inform:
		if(sagaPlayer.getBundle() == selBundle){
			sagaPlayer.message(SettlementMessages.abandoned(selChunk));
		}else{
			sagaPlayer.message(SettlementMessages.abandoned(selChunk, selBundle));
		}
		
		// Refresh:
		selChunk.refresh();
		
		// Play effect:
		SettlementEffects.playAbandon(sagaPlayer, selChunk);
		
		// Delete if none left:
		if( selBundle.getSize() == 0 ){
			selBundle.delete();
			Saga.broadcast(SettlementMessages.dissolved(sagaPlayer, selBundle));
		}
		
		// Statistics:
		StatisticsManager.manager().setBuildings(selBundle);
		
	}
	
	@Command(
		aliases = {"sresign"},
		usage = "[settlement name] <member name>",
		flags = "",
		desc = "Resign from the owner role.",
		min = 1,
		max = 2
	)
	@CommandPermissions({"saga.user.settlement.resign"})
	public static void resign(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		Bundle selChunkBundle = null;
		SagaPlayer selPlayer = null;
		
		String targetName = null;
		
		// Arguments:
		switch (args.argsLength()) {
			
			case 2:
				
				// Chunk bundle:
				String groupName = GeneralMessages.nameFromArg(args.getString(0));
				selChunkBundle = BundleManager.manager().getChunkBundleWithName(groupName);
				if(selChunkBundle == null){
					sagaPlayer.message(SettlementMessages.noChunkBundle(groupName));
					return;
				}
				
				// New owner:
				targetName = selChunkBundle.matchName(args.getString(1));
				if(!selChunkBundle.isMember(targetName)){
					sagaPlayer.message(SettlementMessages.notMember(selChunkBundle, targetName));
					return;
				}
				
				break;

			default:
				
				selChunkBundle = sagaPlayer.getBundle();
				if(selChunkBundle == null){
					sagaPlayer.message(SettlementMessages.notMember());
					return;
				}

				targetName = selChunkBundle.matchName(args.getString(0));
				if(!selChunkBundle.isMember(targetName)){
					sagaPlayer.message(SettlementMessages.notMember(selChunkBundle, targetName));
					return;
				}
				
				break;
				
		}

		try {
			selPlayer = Saga.plugin().forceSagaPlayer(targetName);
		} catch (NonExistantSagaPlayerException e) {
			sagaPlayer.message(PlayerMessages.invalidPlayer(targetName));
			return;
		}
		
		// Permission:
		if(!selChunkBundle.hasPermission(sagaPlayer, SettlementPermission.RESIGN)){
			sagaPlayer.message(GeneralMessages.noPermission());
			return;
		}
		
		// Already owner:
		if(selChunkBundle.isOwner(targetName)){
			
			if(selPlayer == sagaPlayer){
				sagaPlayer.message(SettlementMessages.alreadyOwner());
			}else{
				sagaPlayer.message(SettlementMessages.alreadyOwner(targetName));
			}
			
			return;
		}

		// Set owner:
		selChunkBundle.setOwner(targetName);
		
		// Set owner role:
		if(selChunkBundle instanceof Settlement){

			Settlement selSettlement = (Settlement) selChunkBundle;
			String roleName = selSettlement.getDefinition().ownerRole;
			
			// Get role:
			Proficiency role;
			try {
				role = ProficiencyConfiguration.config().createProficiency(roleName);
			} catch (InvalidProficiencyException e) {
				sagaPlayer.message(SettlementMessages.invalidRole(roleName));
				return;
			}
			
			// Set role:
			selSettlement.setRole(selPlayer, role);
			
		}

		// Inform:
		selChunkBundle.broadcast(SettlementMessages.newOwnerBcast(targetName));
		
		 
	}

	@Command(
			aliases = {"sdissolve"},
			usage = "[settlement name]",
			flags = "",
			desc = "Dissolve the settlement.",
			min = 0,
			max = 1
	)
	@CommandPermissions({"saga.user.settlement.delete"})
	public static void disolve(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

			
		Bundle selBundle = null;

		// Arguments:
		if(args.argsLength() == 1){
				
			// Chunk group:
			String groupName = GeneralMessages.nameFromArg(args.getString(0));
			selBundle = BundleManager.manager().getChunkBundleWithName(groupName);
			if(selBundle == null){
				sagaPlayer.message(SettlementMessages.noChunkBundle(groupName));
				return;
			}
			
		}else{
			
			// Chunk group:
			selBundle = sagaPlayer.getBundle();
			if(selBundle == null){
				sagaPlayer.message(SettlementMessages.notMember());
				return;
			}
			
		}
	   	
	   	// Permissions:
	   	if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.DISSOLVE)){
	   		sagaPlayer.message(GeneralMessages.noPermission(selBundle));
	   		return;
	   	}

	   	// Level too high:
	   	if(selBundle instanceof Settlement){
		   		
	   		Settlement selectedSettlement = (Settlement) selBundle;
		   		
	   		if(selectedSettlement.getLevel() >= SettlementConfiguration.config().noDeleteLevel){

	   			sagaPlayer.message(SettlementMessages.informDissolveLevel());
				return;
				
	   		}
	
	   	}
	   	
	   	// Delete:
	   	selBundle.delete();
				
		// Inform:
		Saga.broadcast(SettlementMessages.dissolved(sagaPlayer, selBundle));

		// Statistics:
		StatisticsManager.manager().setBuildings(selBundle);
		
		
	}
	
	
	// Buildings:
	@Command(
		aliases = {"ssetbuilding","setbuilding","bset"},
		usage = "<building_name>",
		flags = "",
		desc = "Sets a building on the chunk of land.",
		min = 1,
		max = 1
		)
	@CommandPermissions({"saga.user.settlement.building.set"})
	public static void setBuilding(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		String buildingName = null;
		Bundle selBundle = null;

		// Arguments:
		buildingName = GeneralMessages.nameFromArg(args.getString(0));
		
		// Selected chunk:
		SagaChunk selChunk = sagaPlayer.getSagaChunk();
	   	if(selChunk == null){
			sagaPlayer.message(BuildingMessages.buildingsOnClaimed(selBundle));
			return;
		}
		
	   	// Selected chunk bundle:
	   	selBundle = selChunk.getChunkBundle();

	   	// Valid building:
	   	if(SettlementConfiguration.config().getBuildingDefinition(buildingName) == null){
	   		sagaPlayer.message(BuildingMessages.invalidBuilding(buildingName));
	   		return;
	   	}
	   	
		// Building:
		Building selBuilding;
		try {
			selBuilding = SettlementConfiguration.config().createBuilding(buildingName);
		} catch (InvalidBuildingException e) {
			SagaLogger.severe(SettlementCommands.class, sagaPlayer + " tried to set a building with missing definition");
			sagaPlayer.error("definition missing for " + buildingName + " building");
			return;
		}
		if(selBuilding == null){
			sagaPlayer.message(BuildingMessages.invalidBuilding(buildingName));
			return;
		}
		
		// Permission:
		if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.SET_BUILDING)){
			sagaPlayer.message(GeneralMessages.noPermission(selBundle));
			return;
		}
		
		// Building points:
		if(!selBundle.hasBuildPointsAvailable(selBuilding)){
			sagaPlayer.message(SettlementMessages.notEnoughBuildingPoints(selBuilding));
			return;
		}

		// Existing building:
		if(selChunk.getBuilding() != null){
			sagaPlayer.message(BuildingMessages.oneBuilding(selBundle));
			return;
		}

		// Building available:
		if(!selBundle.isBuildingAvailable(buildingName)){
			sagaPlayer.message(BuildingMessages.unavailable(selBuilding));
			return;
		}
		
		// Set building:
		selChunk.setBuilding(selBuilding);

		// Inform:
		if(sagaPlayer.getBundle() == selBundle){
			sagaPlayer.message(SettlementMessages.setBuilding(selBuilding));
		}else{
			sagaPlayer.message(SettlementMessages.setBuilding(selBuilding, selBundle));
		}
		
		// Play effect:
		SettlementEffects.playBuildingSet(sagaPlayer, selBuilding);

		// Statistics:
		StatisticsManager.manager().setBuildings(selBundle);
		

	}
	
	@Command(
		aliases = {"sremovebuilding","abandonbuilding","bremove"},
		usage = "[settlement name]",
		flags = "",
		desc = "Abandons a building on the chunk of land.",
		min = 0,
		max = 0
		)
	@CommandPermissions({"saga.user.settlement.building.remove"})
	public static void removeBuilding(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


		Bundle selBundle = null;
		
		// Arguments:
		selBundle = getLocationChunkBundle(sagaPlayer);
		if(selBundle == null){
			sagaPlayer.message(SettlementMessages.notMember());
			return;
		}
		
		// Selected chunk:
		SagaChunk selChunk = sagaPlayer.getSagaChunk();
	   	if(selChunk == null){
			sagaPlayer.message(BuildingMessages.buildingsOnClaimed(selBundle));
			return;
		}
		
		// Existing building:
		Building selBuilding = selChunk.getBuilding();
		if(selBuilding == null){
			sagaPlayer.message(SettlementMessages.noBuilding());
			return;
		}
		
		// Permission:
		if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.REMOVE_BUILDING)){
			sagaPlayer.message(GeneralMessages.noPermission(selBundle));
			return;
		}

		// Inform:
		if(sagaPlayer.getBundle() == selBundle){
			sagaPlayer.message(SettlementMessages.removedBuilding(selBuilding));
		}else{
			sagaPlayer.message(SettlementMessages.removedBuilding(selBuilding, selBundle));
		}

		// Play effect:
		SettlementEffects.playBuildingRemove(sagaPlayer, selBuilding);
		
		// Remove building:
		selChunk.removeBuilding();

		// Statistics:
		StatisticsManager.manager().setBuildings(selBundle);
		
		
	}

	
	
	// Members:
	@Command(
		aliases = {"sinvite"},
		usage = "[settlement name] <player name>",
		flags = "",
		desc = "Invite a player to join the settlement.",
		min = 1,
		max = 2
	)
	@CommandPermissions({"saga.user.settlement.invite"})
	public static void invite(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		Bundle selChunkBundle = null;
		SagaPlayer selPlayer = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			// Chunk bundle:
			String groupName = GeneralMessages.nameFromArg(args.getString(0));
			selChunkBundle = BundleManager.manager().getChunkBundleWithName(groupName);
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.noChunkBundle(groupName));
				return;
			}
			
			try {
				// Force:
				selPlayer = Saga.plugin().forceSagaPlayer(args.getString(1));
			} catch (NonExistantSagaPlayerException e) {
				sagaPlayer.message(SettlementMessages.nonExistantPlayer(args.getString(1)));
				return;
			}
			
		}else{
			
			// Chunk bundle:
			selChunkBundle = sagaPlayer.getBundle();
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.notMember());
				return;
			}
			
			try {
				// Force:
				selPlayer = Saga.plugin().forceSagaPlayer(args.getString(0));
			} catch (NonExistantSagaPlayerException e) {
				sagaPlayer.message(SettlementMessages.nonExistantPlayer(args.getString(0)));
				return;
			}
			
		}
		
		
		// Permission:
		if(!selChunkBundle.hasPermission(sagaPlayer, SettlementPermission.INVITE) ){
			sagaPlayer.message(GeneralMessages.noPermission());
			return;
			
		}
		
		// Already a member:
		if(selChunkBundle.isMember( selPlayer.getName()) ){
		
			sagaPlayer.message( SettlementMessages.alreadyInTheChunkBundle(selPlayer, selChunkBundle));
			return;
			
		}
		
		// Already invited:
		if(selPlayer.hasBundleInvite(selChunkBundle.getId())){
			
			sagaPlayer.message( SettlementMessages.alreadyInvited(selPlayer, selChunkBundle) );
			return;
			
		}
		
		// Add invite:
		selPlayer.addBundleInvite(selChunkBundle.getId());
		
		// Inform:
		selPlayer.message(SettlementMessages.beenInvited(selPlayer, selChunkBundle));
		selChunkBundle.broadcast(SettlementMessages.invited(selPlayer, selChunkBundle));
		selPlayer.message(SettlementMessages.informAccept());
		
		// Release:
		selPlayer.indicateRelease();
		
		
	}
	
	@Command(
		aliases = {"saccept"},
		usage = "<settlement name>",
		flags = "",
		desc = "Accept a settlement invitation.",
		min = 0,
		max = 1
	)
	@CommandPermissions({"saga.user.settlement.accept"})
	public static void accept(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


    	// No invites:
    	if(sagaPlayer.getBundleInvites().size() == 0){
    		sagaPlayer.message(SettlementMessages.playerNoInvites(sagaPlayer));
    		return;
    	}
    	
    	// Find chunk bundle:
    	Bundle selChunkBundle = null;
    	ArrayList<Integer> invitationIds = sagaPlayer.getBundleInvites();
    	// No parameters:
    	if(args.argsLength() == 0 && invitationIds.size() > 0){
    		selChunkBundle = BundleManager.manager().getChunkBundle(invitationIds.get(invitationIds.size() -1 ));
    	}
    	// Chunk bundle name parameter:
    	else if(args.argsLength() == 1){
    		for (int i = 0; i < invitationIds.size(); i++) {
    			Bundle group = BundleManager.manager().getChunkBundle(invitationIds.get(i));
				if( group != null && group.getName().equals(args.getString(0)) ){
					selChunkBundle = group;
					break;
				}
			}
    	}
    	
    	// Chunk bundle doesn't exist:
    	if(selChunkBundle == null && args.argsLength() == 1){
    		sagaPlayer.message(SettlementMessages.nonExistantChunkBundle(args.getString(0)));
    		return;
    	}else if(selChunkBundle == null){
    		sagaPlayer.message( SettlementMessages.nonExistantChunkBundle() );
    		return;
    	}
    	
    	// Already in a chunk bundle:
    	if(sagaPlayer.getBundle() != null){
    		sagaPlayer.message(SettlementMessages.haveCunkGroup());
    		return;
    	}
    	
    	// Inform:
    	selChunkBundle.broadcast(SettlementMessages.joined(sagaPlayer, selChunkBundle));
		sagaPlayer.message(SettlementMessages.haveJoined(sagaPlayer, selChunkBundle));

    	// Add to chunk bundle:
		selChunkBundle.addMember(sagaPlayer);
    	
		// Set owner:
		if(!selChunkBundle.hasOwner()){
			selChunkBundle.setOwner(sagaPlayer.getName());
		}
		
    	// Decline every invitation:
    	ArrayList<Integer> chunkGroupIds = sagaPlayer.getBundleInvites();
    	for (int i = 0; i < chunkGroupIds.size(); i++) {
			sagaPlayer.removeBundleInvite(chunkGroupIds.get(i));
		}
    	
    	
	}

	@Command(
		aliases = {"sdeclineall", "sdecline"},
		usage = "",
		flags = "",
		desc = "Decline all settlement join invitations.",
		min = 0,
		max = 0
	)
	@CommandPermissions({"saga.user.settlement.decline"})
	public static void decline(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


    	// Decline every invitation:
    	ArrayList<Integer> chunkGroupIds = sagaPlayer.getBundleInvites();
    	for (int i = 0; i < chunkGroupIds.size(); i++) {
			sagaPlayer.removeBundleInvite(chunkGroupIds.get(i));
		}
    	
    	// Inform:
    	sagaPlayer.message(SettlementMessages.declinedInvites());
		
		
	}

	@Command(
		aliases = {"settlementquit"},
		usage = "",
		flags = "",
		desc = "Quit the settlement.",
		min = 0,
		max = 0
	)
	@CommandPermissions({"saga.user.settlement.quit"})
	public static void quit(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		Bundle selBundle = null;

		// Arguments:
		selBundle = sagaPlayer.getBundle();
		if(selBundle == null){
			
			sagaPlayer.removeBundleId();
			
			sagaPlayer.message(SettlementMessages.notMember());
			return;
			
		}
		
		// Owner:
		if(selBundle.isOwner(sagaPlayer.getName()) && selBundle.getMemberCount() > 1){
			sagaPlayer.message(SettlementMessages.ownerCantQuit());
			sagaPlayer.message(SettlementMessages.ownerCantQuitInfo());
			return;
		}
		
		// Not a member:
		if(!selBundle.isMember(sagaPlayer.getName()) ){
			sagaPlayer.message(SettlementMessages.notMember(selBundle));
			return;
		}
		
		// Remove:
		selBundle.removeMember(sagaPlayer);

		// Inform:
		selBundle.broadcast(SettlementMessages.quit(sagaPlayer, selBundle));
		sagaPlayer.message(SettlementMessages.haveQuit(sagaPlayer, selBundle));
		
		// Delete:
		if(selBundle instanceof Settlement){
			
			Settlement selsettlement = (Settlement) selBundle;
			
			if(selBundle.getMemberCount() == 0){

				if(selsettlement.getLevel() < SettlementConfiguration.config().noDeleteLevel){

					// Delete:
					selsettlement.delete();
						
					// Inform:
					Saga.broadcast(SettlementMessages.dissolved(sagaPlayer, selsettlement));
					
				}else{
					
					sagaPlayer.message(SettlementMessages.informDissolveLevel());
					
				}
				
			}

			// Statistics:
			StatisticsManager.manager().setBuildings(selBundle);
			
		}
		
		
	}
	
	@Command(
		aliases = {"skick"},
		usage = "[settlement name] <player name>",
		flags = "",
		desc = "Kick a member from the settlement.",
		min = 1,
		max = 2
	)
	@CommandPermissions({"saga.user.settlement.kick"})
	public static void kick(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		Bundle selBundle = null;
		SagaPlayer selPlayer = null;
		
		String targetName = null;

		// Arguments:
		switch (args.argsLength()) {
			case 2:

				// Chunk bundle:
				String groupName = GeneralMessages.nameFromArg(args.getString(0));
				selBundle = BundleManager.manager().getChunkBundleWithName(groupName);
				if(selBundle == null){
					sagaPlayer.message(SettlementMessages.noChunkBundle(groupName));
					return;
				}

				// Target name:
				targetName = selBundle.matchName(args.getString(1));
				
				if(!selBundle.isMember(targetName)){
					sagaPlayer.message(SettlementMessages.notMember(selBundle, targetName));
					return;
				}
				
				break;

			default:

				// Chunk bundle:
				selBundle = sagaPlayer.getBundle();
				
				if(selBundle == null){
					sagaPlayer.message(SettlementMessages.notMember());
					return;
				}

				// Target name:
				targetName = selBundle.matchName(args.getString(0));

				if(!selBundle.isMember(targetName)){
					sagaPlayer.message(SettlementMessages.notMember(selBundle, targetName));
					return;
				}
				
				break;
				
		}

		try {
			selPlayer = Saga.plugin().forceSagaPlayer(targetName);
		} catch (NonExistantSagaPlayerException e) {
			sagaPlayer.message(PlayerMessages.invalidPlayer(targetName));
			return;
		}
		
		// Permission:
		if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.KICK)){
			sagaPlayer.message(GeneralMessages.noPermission());
			return;
		}
		
		// Not a member:
		if(!selBundle.isMember(selPlayer.getName())){
			
			if(sagaPlayer.getBundle() == selBundle){
				sagaPlayer.message(SettlementMessages.notMember(selPlayer));
			}else{
				sagaPlayer.message(SettlementMessages.notMember(selPlayer, selBundle));
			}
			return;
		}

		// Kicked yourself:
		if(selPlayer == sagaPlayer && sagaPlayer.getBundleId().equals(selBundle.getId())){
			sagaPlayer.message(SettlementMessages.cantKickYourself(sagaPlayer, selBundle));
			return;
		}

		// Kick owner:
		if(selBundle.isOwner(targetName) && selBundle.getMemberCount() > 1){
			sagaPlayer.message(SettlementMessages.cantKickOwner());
			return;
		}

		// Remove player:
		selBundle.removeMember(selPlayer);
		
		// Release:
		selPlayer.indicateRelease();

		// Inform:
		selBundle.broadcast(SettlementMessages.kicked(selPlayer, selBundle));
		selPlayer.message(SettlementMessages.beenKicked(selPlayer, selBundle));
		
		// Delete:
		if(selBundle instanceof Settlement){
			
			Settlement selsettlement = (Settlement) selBundle;
			
			if(selBundle.getMemberCount() == 0){

				if(selsettlement.getLevel() < SettlementConfiguration.config().noDeleteLevel){

					// Delete:
					selsettlement.delete();
						
					// Inform:
					Saga.broadcast(SettlementMessages.dissolved(sagaPlayer, selsettlement));
					
				}else{
					
					sagaPlayer.message(SettlementMessages.informDissolveLevel());
					
				}
				
			}

			// Statistics:
			StatisticsManager.manager().setBuildings(selBundle);
			
		}
		
		
	}

	@Command(
		aliases = {"sdeclareowner"},
		usage = "[settlement name] <player name>",
		flags = "",
		desc = "Declares someone as the new settlement owner.",
		min = 1,
		max = 2
		)
	@CommandPermissions({"saga.user.settlement.declareowner"})
	public static void declareOwner(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		Bundle selChunkBundle = null;
		String targetName = null;
		

		// Arguments:
		if(args.argsLength() == 2){
			
			// Chunk bundle:
			String groupName = GeneralMessages.nameFromArg(args.getString(0));
			selChunkBundle = BundleManager.manager().getChunkBundleWithName(groupName);
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.noChunkBundle(groupName));
				return;
			}

			// Name:
			targetName = args.getString(1);
			
		}else{
			
			// Chunk bundle:
			selChunkBundle = sagaPlayer.getBundle();
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.notMember());
				return;
			}

			// Name:
			targetName = args.getString(0);
			
		}
		
		// Permission:
		if(!selChunkBundle.hasPermission(sagaPlayer, SettlementPermission.DECLARE_OWNER)){
			sagaPlayer.message(GeneralMessages.noPermission());
			return;
		}

		// Force player:
		SagaPlayer selPlayer;
		try {
			selPlayer = Saga.plugin().forceSagaPlayer(targetName);
		} catch (NonExistantSagaPlayerException e) {
			sagaPlayer.message(PlayerMessages.invalidPlayer(targetName));
			return;
		}
		
		// Remove old owner:
		selChunkBundle.removeOwner();
		
		// Set new owner:
		selChunkBundle.setOwner(selPlayer.getName());
		
		// Inform:
		selChunkBundle.broadcast(SettlementMessages.newOwnerBcast(targetName));

		// Release:
		selPlayer.indicateRelease();

   		
	}

	@Command(
		aliases = {"ssetrole"},
		usage = "<player name> <role_name>",
		flags = "",
		desc = "Set a role for the settlement member.",
		min = 2,
		max = 3
	)
	@CommandPermissions({"saga.user.settlement.setrole"})
	public static void setRole(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		Bundle selChunkBundle = null;
		String targetName = null;
		String roleName = null;
		
		// Arguments:
		if(args.argsLength() == 3){
			
			// Chunk bundle:
			String groupName = GeneralMessages.nameFromArg(args.getString(0));
			selChunkBundle = BundleManager.manager().getChunkBundleWithName(groupName);
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.noChunkBundle(groupName));
				return;
			}
			
			targetName = selChunkBundle.matchName(args.getString(1));

			roleName = GeneralMessages.nameFromArg(args.getString(2));
			
		}else{
			
			// Chunk bundle:
			selChunkBundle = sagaPlayer.getBundle();
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.notMember());
				return;
			}

			targetName = selChunkBundle.matchName(args.getString(0));

			roleName = GeneralMessages.nameFromArg(args.getString(1));
			
		}
		
		// Is a settlement:
		if(!(selChunkBundle instanceof Settlement)){
			sagaPlayer.message(SettlementMessages.notSettlement(selChunkBundle));
			return;
		}
		Settlement selSettlement = (Settlement) selChunkBundle;

		// Permission:
		if(!selSettlement.hasPermission(sagaPlayer, SettlementPermission.SET_ROLE)){
			sagaPlayer.message(GeneralMessages.noPermission(selChunkBundle));
			return;
		}

		// Force player:
		SagaPlayer selPlayer;
		try {
			selPlayer = Saga.plugin().forceSagaPlayer(targetName);
		} catch (NonExistantSagaPlayerException e) {
			sagaPlayer.message(PlayerMessages.invalidPlayer(targetName));
			return;
		}

		// Not chunk bundle member:
		if( !selChunkBundle.equals(selPlayer.getBundle()) ){
			sagaPlayer.message(SettlementMessages.notMember(selChunkBundle, selPlayer.getName()));
			return;
		}

		// Create role:
		Proficiency role;
		try {
			role = ProficiencyConfiguration.config().createProficiency(roleName);
		} catch (InvalidProficiencyException e) {
			sagaPlayer.message(SettlementMessages.invalidRole(roleName));
			return;
		}
		
		// Not a role:
		if(role.getDefinition().getType() != ProficiencyType.ROLE){
			sagaPlayer.message(SettlementMessages.invalidRole(roleName));
			return;
		}
		
		// Role available:
		if(!selSettlement.isRoleAvailable(role.getHierarchy())){
			sagaPlayer.message(SettlementMessages.roleNotAvailable(roleName));
			return;
		}
		
		// Set role:
		selSettlement.setRole(selPlayer, role);
		
		// Inform:
		selChunkBundle.broadcast(SettlementMessages.newRole(selPlayer, selSettlement, roleName));

		// Release:
		selPlayer.indicateRelease();
		
		
	}
	
	
	
	// Stats:
	@Command(
		aliases = {"sstats"},
		usage = "[settlement name] [page]",
		flags = "",
		desc = "Show settlement stats.",
		min = 0,
		max = 2
	)
	@CommandPermissions({"saga.user.settlement.stats"})
	public static void stats(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		Integer page = null;
		Settlement selSettlement = null;
		
		Bundle selChunkBundle = null;
		String argsPage = null;
		String groupName = null;
		
		// Arguments:
		switch (args.argsLength()) {
			
			case 2:
				
				// Chunk bundle:
				groupName = GeneralMessages.nameFromArg(args.getString(0));
				selChunkBundle = BundleManager.manager().getChunkBundleWithName(groupName);
				if(selChunkBundle == null){
					sagaPlayer.message(SettlementMessages.noChunkBundle(groupName));
					return;
				}
				
				// Page:
				argsPage = args.getString(1);
				try {
					page = Integer.parseInt(argsPage);
				}
				catch (NumberFormatException e) {
					sagaPlayer.message(GeneralMessages.mustBeNumber(argsPage));
					return;
				}
				break;

			case 1:

				// Chunk bundle:
				selChunkBundle = sagaPlayer.getBundle();
				if(selChunkBundle == null){
					sagaPlayer.message(SettlementMessages.notMember());
					return;
				}

				// Page:
				argsPage = args.getString(0);
				try {
					page = Integer.parseInt(argsPage);
				}
				catch (NumberFormatException e) {
					sagaPlayer.message(GeneralMessages.mustBeNumber(argsPage));
					return;
				}
				
				break;

			default:

				// Chunk bundle:
				selChunkBundle = sagaPlayer.getBundle();
				if(selChunkBundle == null){
					sagaPlayer.message(SettlementMessages.notMember());
					return;
				}
				
				// Page:
				page = 1;
				
				break;
				
		}
		
		// Is a settlement:
		if(! (selChunkBundle instanceof Settlement) ){
			sagaPlayer.message(SettlementMessages.notSettlement(selChunkBundle));
			return;
		}
		selSettlement = (Settlement) selChunkBundle;
		
		// Inform:
		sagaPlayer.message(SettlementMessages.stats(sagaPlayer, selSettlement, page -1));
		
		
	}

	@Command(
		aliases = {"slist"},
		usage = "[settlement name]",
		flags = "",
		desc = "Lists settlement members.",
		min = 0,
		max = 1
		)
	@CommandPermissions({"saga.user.settlement.list"})
	public static void list(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		Bundle selChunkBundle = null;

		// Arguments:
		if(args.argsLength() == 1){
			
			// Chunk bundle:
			String groupName = GeneralMessages.nameFromArg(args.getString(0));
			selChunkBundle = BundleManager.manager().getChunkBundleWithName(groupName);
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.noChunkBundle(groupName));
				return;
			}
			
		}else{
			
			// Chunk bundle:
			selChunkBundle = sagaPlayer.getBundle();
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.notMember());
				return;
			}
			
		}
		
		// Is a settlement:
		if(! (selChunkBundle instanceof Settlement) ){
			sagaPlayer.message(SettlementMessages.notSettlement(selChunkBundle));
			return;
		}
		Settlement selSettlement = (Settlement) selChunkBundle;
		
		// Inform:
		sagaPlayer.message(SettlementMessages.list(sagaPlayer, selSettlement));
		
		
	}
	
	
	
	// Info:
	@Command(
		aliases = {"squit"},
		usage = "",
		flags = "",
		desc = "Wrong command to quit the settlement.",
		min = 0,
		max = 0
		)
	@CommandPermissions({"saga.user.faction.quit"})
	public static void wrongQuit(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		// Inform:
		sagaPlayer.message(SettlementMessages.wrongQuit());
		
		
	}

	@Command(
		aliases = {"shelp"},
		usage = "[page number]",
		flags = "",
		desc = "Display settlement help.",
		min = 0,
		max = 1
	)
	@CommandPermissions({"saga.user.settlement.help"})
	public static void help(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		Integer page = null;
		
		// Arguments:
		if(args.argsLength() == 1){
			try {
				page = Integer.parseInt(args.getString(0));
			} catch (NumberFormatException e) {
				sagaPlayer.message(SettlementMessages.invalidPage(args.getString(0)));
				return;
			}
		}else{
			page = 0;
		}
		
		// Inform:
		sagaPlayer.message(HelpMessages.shelp(page - 1));

		
	}

	
	
	// Other:
	@Command(
		aliases = {"srename"},
		usage = "[settlement name] <new settlement name>",
		flags = "",
		desc = "Rename the settlement.",
		min = 1,
		max = 2
	)
	@CommandPermissions({"saga.user.settlement.rename"})
	public static void rename(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	    	
		
		Bundle selChunkBundle = null;
		String name = null;

		// Arguments:
		if(args.argsLength() == 2){
			
			// Chunk bundle:
			String groupName = GeneralMessages.nameFromArg(args.getString(0));
			selChunkBundle = BundleManager.manager().getChunkBundleWithName(groupName);
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.noChunkBundle(groupName));
				return;
			}
			
			name = GeneralMessages.nameFromArg(args.getString(1));
			
		}else{
			
			// Chunk bundle:
			selChunkBundle = sagaPlayer.getBundle();
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.notMember());
				return;
			}
			
			name = GeneralMessages.nameFromArg(args.getString(0));

		}

		// Permission:
		if(!selChunkBundle.hasPermission(sagaPlayer, SettlementPermission.RENAME)){
			sagaPlayer.message(GeneralMessages.noPermission(selChunkBundle));
			return;
		}

		// Fix spaces:
		while(name.contains("  ")){
			name = name.replaceAll("  ", " ");
		}
	    	
	    // Validate name:
	    if(!validateName(name)){
	    	sagaPlayer.message(SettlementMessages.invalidName());
	    	return;
	    }
	    	
	    // Check name:
	    if(BundleManager.manager().getChunkBundleWithName(name) != null){
	    	sagaPlayer.message(FactionMessages.inUse(name));
	    	return;
	    }
	    
	    Double cost = EconomyConfiguration.config().chunkGroupRenameCost;
		if(cost > 0){

		    // Check coins:
		    if(sagaPlayer.getCoins() < cost){
		    	sagaPlayer.message(EconomyMessages.insuficcientCoins(cost));
		    	return;
		    }
		    
	    	// Take coins:
		    sagaPlayer.removeCoins(cost);
		    
		    // Inform:
		    sagaPlayer.message(EconomyMessages.spent(cost));
		    
	    }
	    
	    // Rename:
	    selChunkBundle.setName(name);
	    
	    // Inform:
	    selChunkBundle.broadcast(SettlementMessages.renamed(selChunkBundle));
	    	
	    	
	}
	
	@Command(
		aliases = {"map"},
		usage = "",
		flags = "",
		desc = "Show a map of all claimed land.",
		min = 0,
		max = 0
	)
	@CommandPermissions({"saga.user.map"})
	public static void map(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

    	sagaPlayer.message(SettlementMessages.map(sagaPlayer, sagaPlayer.getLocation()));
            
	}
	
	
	
	// Utility:
	/**
	 * Validates building name.
	 * 
	 * @param name building name
	 * @return true if valid
	 */
	public static boolean validateName(String name) {

		
		if(org.saga.utility.text.TextUtil.getComparisonString(name).length() < minimumNameLenght ) {
			return false;
		}

		if(name.length() > maximumNameLength) {
			return false;
		}

		for (char c : name.toCharArray()) {
			
			if ( ! org.saga.utility.text.TextUtil.substanceChars.contains(String.valueOf(c))) {
				return false;
			}
			
		}

		return true;

         
	}
	
	/**
	 * Gets the chunk bundle the player is standing on.
	 * 
	 * @param sagaPlayer saga player
	 * @return chunk bundle, null if none
	 */
	private static Bundle getLocationChunkBundle(SagaPlayer sagaPlayer) {

		Location location = sagaPlayer.getLocation();
		if(location == null) return null;
		
		SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(location);
		if(sagaChunk == null) return null;
		
		return sagaChunk.getChunkBundle();
		
	}
	
	
}
