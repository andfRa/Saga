package org.saga.commands;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.buildings.Building;
import org.saga.chunks.ChunkBundle;
import org.saga.chunks.ChunkBundleManager;
import org.saga.chunks.SagaChunk;
import org.saga.config.EconomyConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.ProficiencyConfiguration.InvalidProficiencyException;
import org.saga.config.SettlementConfiguration;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.messages.BuildingMessages;
import org.saga.messages.SettlementMessages;
import org.saga.messages.EconomyMessages;
import org.saga.messages.FactionMessages;
import org.saga.messages.InfoMessages;
import org.saga.messages.SagaMessages;
import org.saga.messages.SettlementEffects;
import org.saga.player.Proficiency;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Settlement;
import org.saga.settlements.Settlement.SettlementPermission;
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
		String settlementName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");

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
		if(sagaPlayer.hasChunkBundle()){
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
		if(ChunkBundleManager.manager().getChunkBundleWithName(settlementName) != null){
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
		Saga.broadcast(SettlementMessages.settled(sagaPlayer, settlement));

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


		ChunkBundle selChunkBundle = null;

		// Arguments:
		if(args.argsLength() == 1){
			
			// Chunk bundle:
			String groupName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");
			selChunkBundle = ChunkBundleManager.manager().getChunkBundleWithName(groupName);
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.noChunkGroup(groupName));
				return;
			}
			
		}else{
			
			// Chunk bundle:
			selChunkBundle = sagaPlayer.getChunkBundle();
			if(selChunkBundle == null){
				sagaPlayer.message( SettlementMessages.notMember() );
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
	   		sagaPlayer.message(SagaMessages.noPermission());
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
		if(sagaPlayer.getChunkBundle() == selChunkBundle){
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
	   	ChunkBundle selGroup = selChunk.getChunkBundle();
	   	
	   	// Permissions:
	   	if(!selGroup.hasPermission(sagaPlayer, SettlementPermission.ABANDON)){
	   		sagaPlayer.message(SagaMessages.noPermission());
	   		return;
	   	}
	   	
		// Remove chunk from the chunk bundle:
		selGroup.removeChunk(selChunk);
		
		// Inform:
		if(sagaPlayer.getChunkBundle() == selGroup){
			sagaPlayer.message(SettlementMessages.abandoned(selChunk));
		}else{
			sagaPlayer.message(SettlementMessages.abandoned(selChunk, selGroup));
		}
		
		// Refresh:
		selChunk.refresh();
		
		// Play effect:
		SettlementEffects.playAbandon(sagaPlayer, selChunk);
		
		// Delete if none left:
		if( selGroup.getSize() == 0 ){
			selGroup.delete();
			Saga.broadcast(SettlementMessages.dissolved(sagaPlayer, selGroup));
		}
		
		
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

		
		ChunkBundle selChunkBundle = null;
		SagaPlayer targetPlayer = null;
		
		String targetName = null;
		
		// Arguments:
		switch (args.argsLength()) {
			
			case 2:
				
				// Chunk bundle:
				String groupName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");
				selChunkBundle = ChunkBundleManager.manager().getChunkBundleWithName(groupName);
				if(selChunkBundle == null){
					sagaPlayer.message(SettlementMessages.noChunkGroup(groupName));
					return;
				}
				
				// New owner:
				targetName = selChunkBundle.matchName(args.getString(1));
				if(!selChunkBundle.hasMember(targetName)){
					sagaPlayer.message(SettlementMessages.notMember(selChunkBundle, targetName));
					return;
				}
				
				break;

			default:
				
				selChunkBundle = sagaPlayer.getChunkBundle();
				if(selChunkBundle == null){
					sagaPlayer.message(SettlementMessages.notMember());
					return;
				}

				targetName = selChunkBundle.matchName(args.getString(0));
				if(!selChunkBundle.hasMember(targetName)){
					sagaPlayer.message(SettlementMessages.notMember(selChunkBundle, targetName));
					return;
				}
				
				break;
				
		}

		try {
			targetPlayer = Saga.plugin().forceSagaPlayer(targetName);
		} catch (NonExistantSagaPlayerException e) {
			sagaPlayer.message(SagaMessages.invalidPlayer(targetName));
			return;
		}
		
		// Permission:
		if(!selChunkBundle.hasPermission(sagaPlayer, SettlementPermission.RESIGN)){
			sagaPlayer.message(SagaMessages.noPermission());
			return;
		}
		
		// Already owner:
		if(selChunkBundle.isOwner(targetName)){
			
			if(targetPlayer == sagaPlayer){
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
			selSettlement.setRole(targetPlayer, role);
			
		}

		// Inform:
		selChunkBundle.broadcast(SettlementMessages.newOwnerBcast(targetName));
		
		 
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
		ChunkBundle selChunkBundle = null;

		// Arguments:
		buildingName = args.getString(0).replace(SagaMessages.spaceSymbol, " ").toLowerCase();
		
		// Selected chunk:
		SagaChunk selChunk = sagaPlayer.getSagaChunk();
	   	if(selChunk == null){
			sagaPlayer.message(BuildingMessages.notOnClaimedLand(selChunkBundle));
			return;
		}
		
	   	// Selected chunk bundle:
	   	selChunkBundle = selChunk.getChunkBundle();

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
		if(!selChunkBundle.hasPermission(sagaPlayer, SettlementPermission.SET_BUILDING)){
			sagaPlayer.message(SagaMessages.noPermission(selChunkBundle));
			return;
		}
		
		// Building points:
		if(selChunkBundle.getRemainingBuildPoints() < selBuilding.getDefinition().getBuildPoints()){
			sagaPlayer.message(SettlementMessages.notEnoughBuildingPoints(selBuilding));
			return;
		}

		// Existing building:
		if(selChunk.getBuilding() != null){
			sagaPlayer.message(BuildingMessages.oneBuildingAllowed(selChunkBundle));
			return;
		}

		// Building available:
		if(!selChunkBundle.isBuildingAvailable(buildingName)){
			sagaPlayer.message(BuildingMessages.unavailable(selBuilding));
			return;
		}
		
		// Set building:
		selChunk.setBuilding(selBuilding);

		// Inform:
		if(sagaPlayer.getChunkBundle() == selChunkBundle){
			sagaPlayer.message(SettlementMessages.setBuilding(selBuilding));
		}else{
			sagaPlayer.message(SettlementMessages.setBuilding(selBuilding, selChunkBundle));
		}
		
		// Play effect:
		SettlementEffects.playBuildingSet(sagaPlayer, selBuilding);
		

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


		ChunkBundle selChunkBundle = null;
		
		// Arguments:
		selChunkBundle = getLocationChunkBundle(sagaPlayer);
		if(selChunkBundle == null){
			sagaPlayer.message(SettlementMessages.notMember());
			return;
		}
		
		// Selected chunk:
		SagaChunk selChunk = sagaPlayer.getSagaChunk();
	   	if(selChunk == null){
			sagaPlayer.message(BuildingMessages.notOnClaimedLand(selChunkBundle));
			return;
		}
		
		// Existing building:
		Building selBuilding = selChunk.getBuilding();
		if(selBuilding == null){
			sagaPlayer.message(SettlementMessages.noBuilding());
			return;
		}
		
		// Permission:
		if(!selChunkBundle.hasPermission(sagaPlayer, SettlementPermission.REMOVE_BUILDING)){
			sagaPlayer.message(SagaMessages.noPermission(selChunkBundle));
			return;
		}

		// Inform:
		if(sagaPlayer.getChunkBundle() == selChunkBundle){
			sagaPlayer.message(SettlementMessages.removedBuilding(selBuilding));
		}else{
			sagaPlayer.message(SettlementMessages.removedBuilding(selBuilding, selChunkBundle));
		}

		// Play effect:
		SettlementEffects.playBuildingRemove(sagaPlayer, selBuilding);
		
		// Remove building:
		selChunk.removeBuilding();

		
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

		
		ChunkBundle selChunkBundle = null;
		SagaPlayer selPlayer = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			// Chunk bundle:
			String groupName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");
			selChunkBundle = ChunkBundleManager.manager().getChunkBundleWithName(groupName);
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.noChunkGroup(groupName));
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
			selChunkBundle = sagaPlayer.getChunkBundle();
			if(selChunkBundle == null){
				sagaPlayer.message( SettlementMessages.notMember() );
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
		if( !selChunkBundle.hasPermission(sagaPlayer, SettlementPermission.INVITE) ){
			sagaPlayer.message(SagaMessages.noPermission());
			// Unforce:
			Saga.plugin().unforceSagaPlayer(selPlayer.getName());
			return;
			
		}
		
		// Already a member:
		if(selChunkBundle.hasMember( selPlayer.getName()) ){
		
			sagaPlayer.message( SettlementMessages.alreadyInTheChunkBundle(selPlayer, selChunkBundle));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(selPlayer.getName());
			return;
			
		}
		
		// Already invited:
		if(selPlayer.hasChunkGroupInvite(selChunkBundle.getId())){
			
			sagaPlayer.message( SettlementMessages.alreadyInvited(selPlayer, selChunkBundle) );
			// Unforce:
			Saga.plugin().unforceSagaPlayer(selPlayer.getName());
			return;
			
		}
		
		// Add invite:
		selPlayer.addChunkGroupInvite(selChunkBundle.getId());
		
		// Inform:
		selPlayer.message(SettlementMessages.beenInvited(selPlayer, selChunkBundle));
		selChunkBundle.broadcast(SettlementMessages.invited(selPlayer, selChunkBundle));
		selPlayer.message(SettlementMessages.informAccept());
		
		// Unforce:
		Saga.plugin().unforceSagaPlayer(selPlayer.getName());
		return;
		
		
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
    	if(sagaPlayer.getChunkGroupInvites().size() == 0){
    		sagaPlayer.message(SettlementMessages.playerNoInvites(sagaPlayer));
    		return;
    	}
    	
    	// Find chunk bundle:
    	ChunkBundle selChunkBundle = null;
    	ArrayList<Integer> invitationIds = sagaPlayer.getChunkGroupInvites();
    	// No parameters:
    	if(args.argsLength() == 0 && invitationIds.size() > 0){
    		selChunkBundle = ChunkBundleManager.manager().getChunkBundle(invitationIds.get(invitationIds.size() -1 ));
    	}
    	// Chunk bundle name parameter:
    	else if(args.argsLength() == 1){
    		for (int i = 0; i < invitationIds.size(); i++) {
    			ChunkBundle group = ChunkBundleManager.manager().getChunkBundle(invitationIds.get(i));
				if( group != null && group.getName().equals(args.getString(0)) ){
					selChunkBundle = group;
					break;
				}
			}
    	}
    	
    	// Chunk bundle doesn't exist:
    	if(selChunkBundle == null && args.argsLength() == 1){
    		sagaPlayer.message(SettlementMessages.nonExistantChunkGroup(args.getString(0)));
    		return;
    	}else if(selChunkBundle == null){
    		sagaPlayer.message( SettlementMessages.nonExistantChunkGroup() );
    		return;
    	}
    	
    	// Already in a chunk bundle:
    	if(sagaPlayer.getChunkBundle() != null){
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
    	ArrayList<Integer> chunkGroupIds = sagaPlayer.getChunkGroupInvites();
    	for (int i = 0; i < chunkGroupIds.size(); i++) {
			sagaPlayer.removeChunkGroupInvite(chunkGroupIds.get(i));
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
    	ArrayList<Integer> chunkGroupIds = sagaPlayer.getChunkGroupInvites();
    	for (int i = 0; i < chunkGroupIds.size(); i++) {
			sagaPlayer.removeChunkGroupInvite(chunkGroupIds.get(i));
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
		

		ChunkBundle selChunkBundle = null;

		// Arguments:
		selChunkBundle = sagaPlayer.getChunkBundle();
		if(selChunkBundle == null){
			
			if(sagaPlayer.hasChunkBundle()){
				sagaPlayer.removeChunkBundleId(sagaPlayer.getChunkBundleId());
			}
			
			sagaPlayer.message( SettlementMessages.notMember() );
			return;
			
		}
		
//		// Permission:
//		if(!selChunkGroup.hasPermission(sagaPlayer, SettlementPermission.QUIT)){
//			sagaPlayer.message(SagaMessages.noPermission());
//			return;
//		}

		// Owner:
		if(selChunkBundle.isOwner(sagaPlayer.getName()) && selChunkBundle.getMemberCount() > 1){
			sagaPlayer.message(SettlementMessages.ownerCantQuit());
			sagaPlayer.message(SettlementMessages.ownerCantQuitInfo());
			return;
		}
		
		// Not a member:
		if(!selChunkBundle.hasMember(sagaPlayer.getName()) ){
			sagaPlayer.message(SettlementMessages.notMember(selChunkBundle));
			return;
		}
		
		// Remove:
		selChunkBundle.removeMember(sagaPlayer);

		// Inform:
		selChunkBundle.broadcast(SettlementMessages.quit(sagaPlayer, selChunkBundle));
		sagaPlayer.message(SettlementMessages.haveQuit(sagaPlayer, selChunkBundle));
		
		// Delete:
		if(selChunkBundle instanceof Settlement){
			
			Settlement selsettlement = (Settlement) selChunkBundle;
			
			if(selChunkBundle.getMemberCount() == 0){

				if(selsettlement.getLevel() < SettlementConfiguration.config().noDeleteLevel){

					// Delete:
					selsettlement.delete();
						
					// Inform:
					Saga.broadcast(SettlementMessages.dissolved(sagaPlayer, selsettlement));
					
				}else{
					
					sagaPlayer.message(SettlementMessages.informSettlementAboveLevelDelete());
					
				}
				
			}
			
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
		

		ChunkBundle selChunkBundle = null;
		SagaPlayer targetPlayer = null;
		
		String targetName = null;

		// Arguments:
		switch (args.argsLength()) {
			case 2:

				// Chunk bundle:
				String groupName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");
				selChunkBundle = ChunkBundleManager.manager().getChunkBundleWithName(groupName);
				if(selChunkBundle == null){
					sagaPlayer.message(SettlementMessages.noChunkGroup(groupName));
					return;
				}

				// Target name:
				targetName = selChunkBundle.matchName(args.getString(1));
				
				if(!selChunkBundle.hasMember(targetName)){
					sagaPlayer.message(SettlementMessages.notMember(selChunkBundle, targetName));
					return;
				}
				
				break;

			default:

				// Chunk bundle:
				selChunkBundle = sagaPlayer.getChunkBundle();
				
				if(selChunkBundle == null){
					sagaPlayer.message(SettlementMessages.notMember());
					return;
				}

				// Target name:
				targetName = selChunkBundle.matchName(args.getString(0));

				if(!selChunkBundle.hasMember(targetName)){
					sagaPlayer.message(SettlementMessages.notMember(selChunkBundle, targetName));
					return;
				}
				
				break;
				
		}

		try {
			targetPlayer = Saga.plugin().forceSagaPlayer(targetName);
		} catch (NonExistantSagaPlayerException e) {
			sagaPlayer.message(SagaMessages.invalidPlayer(targetName));
			return;
		}
		
		// Permission:
		if(!selChunkBundle.hasPermission(sagaPlayer, SettlementPermission.KICK)){
			sagaPlayer.message(SagaMessages.noPermission());
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}
		
		// Not a member:
		if(!selChunkBundle.hasMember(targetPlayer.getName())){
			
			if(sagaPlayer.getChunkBundle() == selChunkBundle){
				sagaPlayer.message(SettlementMessages.notMember(targetPlayer));
			}else{
				sagaPlayer.message(SettlementMessages.notMember(targetPlayer, selChunkBundle));
			}
			
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}

		// Kicked yourself:
		if(targetPlayer == sagaPlayer && sagaPlayer.getChunkBundleId().equals(selChunkBundle.getId())){
			sagaPlayer.message(SettlementMessages.cantKickYourself(sagaPlayer, selChunkBundle));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}

		// Kick owner:
		if(selChunkBundle.isOwner(targetName) && selChunkBundle.getMemberCount() > 1){
			sagaPlayer.message(SettlementMessages.cantKickOwner());
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}

		// Remove player:
		selChunkBundle.removeMember(targetPlayer);
		
		// Unforce:
		Saga.plugin().unforceSagaPlayer(targetName);

		// Inform:
		selChunkBundle.broadcast(SettlementMessages.kicked(targetPlayer, selChunkBundle));
		targetPlayer.message(SettlementMessages.beenKicked(targetPlayer, selChunkBundle));
		
		// Delete:
		if(selChunkBundle instanceof Settlement){
			
			Settlement selsettlement = (Settlement) selChunkBundle;
			
			if(selChunkBundle.getMemberCount() == 0){

				if(selsettlement.getLevel() < SettlementConfiguration.config().noDeleteLevel){

					// Delete:
					selsettlement.delete();
						
					// Inform:
					Saga.broadcast(SettlementMessages.dissolved(sagaPlayer, selsettlement));
					
				}else{
					
					sagaPlayer.message(SettlementMessages.informSettlementAboveLevelDelete());
					
				}
				
			}
			
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
		

		ChunkBundle selChunkBundle = null;
		String targetName = null;
		

		// Arguments:
		if(args.argsLength() == 2){
			
			// Chunk bundle:
			String groupName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");
			selChunkBundle = ChunkBundleManager.manager().getChunkBundleWithName(groupName);
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.noChunkGroup(groupName));
				return;
			}

			// Name:
			targetName = args.getString(1);
			
		}else{
			
			// Chunk bundle:
			selChunkBundle = sagaPlayer.getChunkBundle();
			if(selChunkBundle == null){
				sagaPlayer.message( SettlementMessages.notMember() );
				return;
			}

			// Name:
			targetName = args.getString(0);
			
		}
		
		// Permission:
		if(!selChunkBundle.hasPermission(sagaPlayer, SettlementPermission.DECLARE_OWNER)){
			sagaPlayer.message(SagaMessages.noPermission());
			return;
		}

		// Force player:
		SagaPlayer targetPlayer;
		try {
			targetPlayer = Saga.plugin().forceSagaPlayer(targetName);
		} catch (NonExistantSagaPlayerException e) {
			sagaPlayer.message(SagaMessages.invalidPlayer(targetName));
			return;
		}
		
		// Remove old owner:
		selChunkBundle.removeOwner();
		
		// Set new owner:
		selChunkBundle.setOwner(targetPlayer.getName());
		
		// Inform:
		selChunkBundle.broadcast(SettlementMessages.newOwnerBcast(targetName));
		
   		// Unforce:
   		Saga.plugin().unforceSagaPlayer(targetName);
		
   		
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

		
		ChunkBundle selChunkBundle = null;
		String targetName = null;
		String roleName = null;
		
		// Arguments:
		if(args.argsLength() == 3){
			
			// Chunk bundle:
			String groupName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");
			selChunkBundle = ChunkBundleManager.manager().getChunkBundleWithName(groupName);
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.noChunkGroup(groupName));
				return;
			}
			
			targetName = selChunkBundle.matchName(args.getString(1));

			roleName = args.getString(2).replace(SagaMessages.spaceSymbol, " ").toLowerCase();;
			
		}else{
			
			// Chunk bundle:
			selChunkBundle = sagaPlayer.getChunkBundle();
			if(selChunkBundle == null){
				sagaPlayer.message( SettlementMessages.notMember() );
				return;
			}

			targetName = selChunkBundle.matchName(args.getString(0));

			roleName = args.getString(1).replace(SagaMessages.spaceSymbol, " ").toLowerCase();
			
		}
		
		// Is a settlement:
		if(!(selChunkBundle instanceof Settlement)){
			sagaPlayer.message(SettlementMessages.notSettlement(selChunkBundle));
			return;
		}
		Settlement selSettlement = (Settlement) selChunkBundle;

		// Permission:
		if(!selSettlement.hasPermission(sagaPlayer, SettlementPermission.SET_ROLE)){
			sagaPlayer.message(SagaMessages.noPermission(selChunkBundle));
			return;
		}

		// Force player:
		SagaPlayer targetPlayer;
		try {
			targetPlayer = Saga.plugin().forceSagaPlayer(targetName);
		} catch (NonExistantSagaPlayerException e) {
			sagaPlayer.message(SagaMessages.invalidPlayer(targetName));
			return;
		}

		// Not chunk bundle member:
		if( !selChunkBundle.equals(targetPlayer.getChunkBundle()) ){
			sagaPlayer.message(SettlementMessages.notMember(selChunkBundle, targetPlayer.getName()));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}

		// Create role:
		Proficiency role;
		try {
			role = ProficiencyConfiguration.config().createProficiency(roleName);
		} catch (InvalidProficiencyException e) {
			sagaPlayer.message(SettlementMessages.invalidRole(roleName));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}
		
		// Not a role:
		if(role.getDefinition().getType() != ProficiencyType.ROLE){
			sagaPlayer.message(SettlementMessages.invalidRole(roleName));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}
		
		// Role available:
		if(!selSettlement.isRoleAvailable(role.getHierarchy())){
			sagaPlayer.message(SettlementMessages.roleNotAvailable(roleName));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}
		
		// Set role:
		selSettlement.setRole(targetPlayer, role);
		
		// Inform:
		selChunkBundle.broadcast(SettlementMessages.newRole(targetPlayer, selSettlement, roleName));

		// Unforce:
		Saga.plugin().unforceSagaPlayer(targetName);
		
		
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
		
		ChunkBundle selChunkBundle = null;
		String sPage = null;
		String groupName = null;
		
		// Arguments:
		switch (args.argsLength()) {
			
			case 2:
				
				// Chunk bundle:
				groupName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");
				selChunkBundle = ChunkBundleManager.manager().getChunkBundleWithName(groupName);
				if(selChunkBundle == null){
					sagaPlayer.message(SettlementMessages.noChunkGroup(groupName));
					return;
				}
				
				// Page:
				sPage = args.getString(1);
				try {
					page = Integer.parseInt(sPage);
				}
				catch (NumberFormatException e) {
					sagaPlayer.message(SettlementMessages.invalidInteger(sPage));
					return;
				}
				break;

			case 1:

				// Chunk bundle:
				selChunkBundle = sagaPlayer.getChunkBundle();
				if(selChunkBundle == null){
					sagaPlayer.message( SettlementMessages.notMember() );
					return;
				}

				// Page:
				sPage = args.getString(0);
				try {
					page = Integer.parseInt(sPage);
				}
				catch (NumberFormatException e) {
					sagaPlayer.message(SettlementMessages.invalidInteger(sPage));
					return;
				}
				
				break;

			default:

				// Chunk bundle:
				selChunkBundle = sagaPlayer.getChunkBundle();
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
		

		ChunkBundle selChunkBundle = null;

		// Arguments:
		if(args.argsLength() == 1){
			
			// Chunk bundle:
			String groupName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");
			selChunkBundle = ChunkBundleManager.manager().getChunkBundleWithName(groupName);
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.noChunkGroup(groupName));
				return;
			}
			
		}else{
			
			// Chunk bundle:
			selChunkBundle = sagaPlayer.getChunkBundle();
			if(selChunkBundle == null){
				sagaPlayer.message( SettlementMessages.notMember() );
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
		sagaPlayer.message(InfoMessages.shelp(page - 1));

		
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
	    	
		
		ChunkBundle selChunkBundle = null;
		String name = null;

		// Arguments:
		if(args.argsLength() == 2){
			
			// Chunk bundle:
			String groupName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");
			selChunkBundle = ChunkBundleManager.manager().getChunkBundleWithName(groupName);
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.noChunkGroup(groupName));
				return;
			}
			
			name = args.getString(1).replaceAll(SagaMessages.spaceSymbol, " ");
			
		}else{
			
			// Chunk bundle:
			selChunkBundle = sagaPlayer.getChunkBundle();
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.notMember());
				return;
			}
			
			name = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");

		}

		// Permission:
		if(!selChunkBundle.hasPermission(sagaPlayer, SettlementPermission.RENAME)){
			sagaPlayer.message(SagaMessages.noPermission(selChunkBundle));
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
	    if(ChunkBundleManager.manager().getChunkBundleWithName(name) != null){
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

    	sagaPlayer.message(SagaMessages.map(sagaPlayer, sagaPlayer.getLocation()));
            
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
	private static ChunkBundle getLocationChunkBundle(SagaPlayer sagaPlayer) {

		Location location = sagaPlayer.getLocation();
		if(location == null) return null;
		
		SagaChunk sagaChunk = ChunkBundleManager.manager().getSagaChunk(location);
		if(sagaChunk == null) return null;
		
		return sagaChunk.getChunkBundle();
		
	}
	
	
}
