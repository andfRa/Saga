package org.saga.commands;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.buildings.Building;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.ChunkGroupManager;
import org.saga.chunkGroups.SagaChunk;
import org.saga.config.EconomyConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.ProficiencyConfiguration.InvalidProficiencyException;
import org.saga.config.SettlementConfiguration;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.messages.BuildingMessages;
import org.saga.messages.ChunkGroupMessages;
import org.saga.messages.EconomyMessages;
import org.saga.messages.FactionMessages;
import org.saga.messages.InfoMessages;
import org.saga.messages.SagaMessages;
import org.saga.messages.SettlementEffects;
import org.saga.player.Proficiency;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Settlement;
import org.saga.settlements.Settlement.SettlementPermission;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;


public class ChunkGroupCommands {

	
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
		SagaChunk selectedChunk = sagaPlayer.getSagaChunk();
		Location location = sagaPlayer.getLocation();
		if(location == null){
			SagaLogger.severe(ChunkGroupCommands.class, "saga player location is null for " + sagaPlayer.getName());
			sagaPlayer.error("failed to retrieve "+ sagaPlayer +" player location");
			return;
		}
		
		// Already claimed:
		if(selectedChunk != null){
			sagaPlayer.message(ChunkGroupMessages.chunkClaimed());
			return;
		}

		// Adjacent:
		ArrayList<SagaChunk> adjacent = SagaChunk.getAllAdjacent(location.getChunk());
		for (SagaChunk sagaChunk : adjacent) {
			
			if(sagaChunk.getChunkGroup() != null){
				sagaPlayer.message(ChunkGroupMessages.claimAdjacentDeny());
				return;
			}
			
		}
		
		// Settles:
		if(sagaPlayer.hasChunkGroup()){
			sagaPlayer.message(ChunkGroupMessages.oneChunkGroupAllowed());
			return;
		}
		
		// Fix spaces:
		while(settlementName.contains("  ")){
			settlementName = settlementName.replaceAll("  ", " ");
		}
		
		// Validate name:
		if(!validateName(settlementName)){
			sagaPlayer.message(ChunkGroupMessages.invalidName());
			return;
		}
		
		// Check name:
		if( ChunkGroupManager.manager().getChunkGroupWithName(settlementName) != null ){
			sagaPlayer.message(FactionMessages.inUse(settlementName));
			return;
		}
		
		// Settle:
		Settlement settlement = new Settlement(settlementName);
		settlement.complete();
		selectedChunk = new SagaChunk(location);
		settlement.addChunk(selectedChunk);
		Settlement.create(settlement, sagaPlayer);
		
		// Inform:
		Saga.broadcast(ChunkGroupMessages.settled(sagaPlayer, settlement));

		// Play effect:
		SettlementEffects.playClaim(sagaPlayer, selectedChunk);
		
		
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


		ChunkGroup selectedChunkGroup = null;

		// Arguments:
		if(args.argsLength() == 1){
			
			// Chunk group:
			String groupName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");
			selectedChunkGroup = ChunkGroupManager.manager().getChunkGroupWithName(groupName);
			if(selectedChunkGroup == null){
				sagaPlayer.message(ChunkGroupMessages.noChunkGroup(groupName));
				return;
			}
			
		}else{
			
			// Chunk group:
			selectedChunkGroup = sagaPlayer.getChunkGroup();
			if(selectedChunkGroup == null){
				sagaPlayer.message( ChunkGroupMessages.noChunkGroup() );
				return;
			}
			
		}
		
		// Location chunk:
		SagaChunk locationChunk = sagaPlayer.getSagaChunk();
	   	Location location = sagaPlayer.getLocation();
	   	if(location == null){
	   		SagaLogger.severe(ChunkGroupCommands.class, "saga player location is null for " + sagaPlayer.getName());
	   		sagaPlayer.error("failed to retrieve "+ sagaPlayer +" player location");
	   		return;
	   	}
	   	Chunk bukkitChunk = location.getWorld().getChunkAt(location);
	  
	   	// Already claimed:
	   	if(locationChunk != null){
			sagaPlayer.message(ChunkGroupMessages.chunkClaimed());
			return;
		}
	   	
		// Adjacent:
		ArrayList<SagaChunk> adjacent = SagaChunk.getAllAdjacent(location.getChunk());
		for (SagaChunk sagaChunk : adjacent) {
			
			if(sagaChunk.getChunkGroup() != selectedChunkGroup){
				sagaPlayer.message(ChunkGroupMessages.claimAdjacentDeny());
				return;
			}
			
		}
	   	
		// Not adjacent:
	   	if(!selectedChunkGroup.isAdjacent(bukkitChunk)){
			sagaPlayer.message(ChunkGroupMessages.chunkMustBeAdjacent());
			return;
		}
	   	
	   	// Permissions:
	   	if(!selectedChunkGroup.hasPermission(sagaPlayer, SettlementPermission.CLAIM)){
	   		sagaPlayer.message(SagaMessages.noPermission());
	   		return;
	   	}
	   	
	   	// Claim points:
	   	Settlement selectedSettlement = null;
	   	if(selectedChunkGroup instanceof Settlement){
	   		selectedSettlement = (Settlement) selectedChunkGroup;
	   	}
	   	if(selectedSettlement == null || !selectedSettlement.isClaimsAvailable()){
	   		sagaPlayer.message(ChunkGroupMessages.notEnoughClaims());
	   		return;
	   	}
	   	
		// Add a new chunk to adjacent chunk group:
		SagaChunk sagaChunk = new SagaChunk(bukkitChunk);
		selectedChunkGroup.addChunk(sagaChunk);
		
		// Inform:
		if(sagaPlayer.getChunkGroup() == selectedChunkGroup){
			sagaPlayer.message(ChunkGroupMessages.claimed(sagaChunk));
		}else{
			sagaPlayer.message(ChunkGroupMessages.claimed(sagaChunk, selectedChunkGroup));
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
	   	SagaChunk selectedChunk = sagaPlayer.getSagaChunk();
	   	Location location = sagaPlayer.getLocation();
	   	if(location == null){
	   		SagaLogger.severe(ChunkGroupCommands.class, "saga player location is null for " + sagaPlayer.getName());
	   		sagaPlayer.error("failed to retrieve "+ sagaPlayer +" player location");
	   		return;
	   	}
	   	
	   	// Unclaimed:
	   	if(selectedChunk == null){
			sagaPlayer.message(ChunkGroupMessages.chunkNotClaimed());
			return;
		}
	   	
	   	// Chunk group:
	   	ChunkGroup selectedGroup = selectedChunk.getChunkGroup();
	   	
	   	// Permissions:
	   	if(!selectedGroup.hasPermission(sagaPlayer, SettlementPermission.ABANDON)){
	   		sagaPlayer.message(SagaMessages.noPermission());
	   		return;
	   	}
	   	
		// Remove chunk from the chunk group:
		selectedGroup.removeChunk(selectedChunk);
		
		// Inform:
		if(sagaPlayer.getChunkGroup() == selectedGroup){
			sagaPlayer.message(ChunkGroupMessages.abandoned(selectedChunk));
		}else{
			sagaPlayer.message(ChunkGroupMessages.abandoned(selectedChunk, selectedGroup));
		}
		
		// Refresh:
		selectedChunk.refresh();
		
		// Play effect:
		SettlementEffects.playAbandon(sagaPlayer, selectedChunk);
		
		// Delete if none left:
		if( selectedGroup.getSize() == 0 ){
			selectedGroup.delete();
			Saga.broadcast(ChunkGroupMessages.dissolved(sagaPlayer, selectedGroup));
		}
		
		
	}
	
	@Command(
		aliases = {"sclaimsettlement"},
		usage = "[settlement name]",
		flags = "",
		desc = "Claim an empty settlement.",
		min = 0,
		max = 1
	)
	@CommandPermissions({"saga.admin.settlement.claim"})
	public static void claimChunkGroup(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		ChunkGroup selectedChunkGroup = null;
		
		// Selected chunk group:
		if(args.argsLength() == 1){
			
			// Chunk group:
			String groupName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");
			selectedChunkGroup = ChunkGroupManager.manager().getChunkGroupWithName(groupName);
			if(selectedChunkGroup == null){
				sagaPlayer.message(ChunkGroupMessages.noChunkGroup(groupName));
				return;
			}
			
		}else{
			
			SagaChunk sagaChunk = sagaPlayer.getSagaChunk();
			if(sagaChunk != null) selectedChunkGroup = sagaChunk.getChunkGroup();
			if(selectedChunkGroup == null){
				sagaPlayer.message( ChunkGroupMessages.noChunkGroup() );
				return;
			}
			
		}

		// Permission:
		if(!selectedChunkGroup.hasPermission(sagaPlayer, SettlementPermission.CLAIM_SETTLEMENT)){
			sagaPlayer.message(SagaMessages.noPermission());
			return;
		}

    	// Already has chunk group:
    	if(sagaPlayer.getChunkGroup() != null){
    		sagaPlayer.message(ChunkGroupMessages.haveCunkGroup());
    		return;
    	}
		
		// Already in chunk group:
		if(selectedChunkGroup.hasMember(sagaPlayer.getName())){
			sagaPlayer.message(ChunkGroupMessages.alreadyInTheChunkGroup(selectedChunkGroup));
			return;
		}
		
		// Add to chunk group:
		selectedChunkGroup.addPlayer(sagaPlayer);
		
		// Set owner role:
		if(selectedChunkGroup instanceof Settlement){

			Settlement selectedSettlement = (Settlement) selectedChunkGroup;
			String roleName = selectedSettlement.getDefinition().ownerRole;
			
			// Get role:
			Proficiency role;
			try {
				role = ProficiencyConfiguration.config().createProficiency(roleName);
			} catch (InvalidProficiencyException e) {
				sagaPlayer.message(ChunkGroupMessages.invalidRole(roleName));
				return;
			}
			
			// Set role:
			selectedSettlement.setRole(sagaPlayer, role);
			
		}

		// Inform:
		sagaPlayer.message(ChunkGroupMessages.claimedChunkGroupBroadcast(sagaPlayer, selectedChunkGroup));
		
		 
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
		ChunkGroup selectedChunkGroup = null;

		// Arguments:
		buildingName = args.getString(0).replace(SagaMessages.spaceSymbol, " ").toLowerCase();
		
		// Selected chunk:
		SagaChunk selectedChunk = sagaPlayer.getSagaChunk();
	   	if(selectedChunk == null){
			sagaPlayer.message(BuildingMessages.notOnClaimedLand(selectedChunkGroup));
			return;
		}
		
	   	// Selected chunk group:
	   	selectedChunkGroup = selectedChunk.getChunkGroup();

	   	// Valid building:
	   	if(SettlementConfiguration.config().getBuildingDefinition(buildingName) == null){
	   		sagaPlayer.message(BuildingMessages.invalidBuilding(buildingName));
	   		return;
	   	}
	   	
		// Building:
		Building selectedBuilding;
		try {
			selectedBuilding = SettlementConfiguration.config().createBuilding(buildingName);
		} catch (InvalidBuildingException e) {
			SagaLogger.severe(ChunkGroupCommands.class, sagaPlayer + " tried to set a building with missing definition");
			sagaPlayer.error("definition missing for " + buildingName + " building");
			return;
		}
		if(selectedBuilding == null){
			sagaPlayer.message(BuildingMessages.invalidBuilding(buildingName));
			return;
		}
		
		// Permission:
		if(!selectedChunkGroup.hasPermission(sagaPlayer, SettlementPermission.SET_BUILDING)){
			sagaPlayer.message(SagaMessages.noPermission(selectedChunkGroup));
			return;
		}
		
		// Building points:
		if(selectedChunkGroup.getRemainingBuildPoints() < selectedBuilding.getDefinition().getBuildPoints()){
			sagaPlayer.message(ChunkGroupMessages.notEnoughBuildingPoints(selectedBuilding));
			return;
		}

		// Existing building:
		if(selectedChunk.getBuilding() != null){
			sagaPlayer.message(BuildingMessages.oneBuildingAllowed(selectedChunkGroup));
			return;
		}

		// Building available:
		if(!selectedChunkGroup.isBuildingAvailable(buildingName)){
			sagaPlayer.message(BuildingMessages.unavailable(selectedBuilding));
			return;
		}
		
		// Set building:
		selectedChunk.setBuilding(selectedBuilding);

		// Inform:
		if(sagaPlayer.getChunkGroup() == selectedChunkGroup){
			sagaPlayer.message(ChunkGroupMessages.setBuilding(selectedBuilding));
		}else{
			sagaPlayer.message(ChunkGroupMessages.setBuilding(selectedBuilding, selectedChunkGroup));
		}
		
		// Play effect:
		SettlementEffects.playBuildingSet(sagaPlayer, selectedBuilding);
		

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


		ChunkGroup selectedChunkGroup = null;
		
		// Arguments:
		selectedChunkGroup = getLocationChunkGroup(sagaPlayer);
		if(selectedChunkGroup == null){
			sagaPlayer.message(ChunkGroupMessages.noChunkGroup());
			return;
		}
		
		// Selected chunk:
		SagaChunk selectedChunk = sagaPlayer.getSagaChunk();
	   	if(selectedChunk == null){
			sagaPlayer.message(BuildingMessages.notOnClaimedLand(selectedChunkGroup));
			return;
		}
		
		// Existing building:
		Building selectedBuilding = selectedChunk.getBuilding();
		if(selectedBuilding == null){
			sagaPlayer.message(ChunkGroupMessages.noBuilding());
			return;
		}
		
		// Permission:
		if(!selectedChunkGroup.hasPermission(sagaPlayer, SettlementPermission.REMOVE_BUILDING)){
			sagaPlayer.message(SagaMessages.noPermission(selectedChunkGroup));
			return;
		}

		// Inform:
		if(sagaPlayer.getChunkGroup() == selectedChunkGroup){
			sagaPlayer.message(ChunkGroupMessages.removedBuilding(selectedBuilding));
		}else{
			sagaPlayer.message(ChunkGroupMessages.removedBuilding(selectedBuilding, selectedChunkGroup));
		}

		// Play effect:
		SettlementEffects.playBuildingRemove(sagaPlayer, selectedBuilding);
		
		// Remove building:
		selectedChunk.removeBuilding();

		
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

		
		ChunkGroup selectedChunkGroup = null;
		SagaPlayer selectedPlayer = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			// Chunk group:
			String groupName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");
			selectedChunkGroup = ChunkGroupManager.manager().getChunkGroupWithName(groupName);
			if(selectedChunkGroup == null){
				sagaPlayer.message(ChunkGroupMessages.noChunkGroup(groupName));
				return;
			}
			
			try {
				// Force:
				selectedPlayer = Saga.plugin().forceSagaPlayer(args.getString(1));
			} catch (NonExistantSagaPlayerException e) {
				sagaPlayer.message(ChunkGroupMessages.nonExistantPlayer(args.getString(1)));
				return;
			}
			
		}else{
			
			// Chunk group:
			selectedChunkGroup = sagaPlayer.getChunkGroup();
			if(selectedChunkGroup == null){
				sagaPlayer.message( ChunkGroupMessages.noChunkGroup() );
				return;
			}
			
			try {
				// Force:
				selectedPlayer = Saga.plugin().forceSagaPlayer(args.getString(0));
			} catch (NonExistantSagaPlayerException e) {
				sagaPlayer.message(ChunkGroupMessages.nonExistantPlayer(args.getString(0)));
				return;
			}
			
		}
		
		
		// Permission:
		if( !selectedChunkGroup.hasPermission(sagaPlayer, SettlementPermission.INVITE) ){
			sagaPlayer.message(SagaMessages.noPermission());
			return;
		}
		
		// Already a member:
		if(selectedChunkGroup.hasMember( selectedPlayer.getName()) ){
		
			sagaPlayer.message( ChunkGroupMessages.alreadyInTheChunkGroup(selectedPlayer, selectedChunkGroup) );
			// Unforce:
			Saga.plugin().unforceSagaPlayer(selectedPlayer.getName());
			return;
			
		}
		
		// Already invited:
		if(selectedPlayer.hasChunkGroupInvite(selectedChunkGroup.getId())){
			
			sagaPlayer.message( ChunkGroupMessages.alreadyInvited(selectedPlayer, selectedChunkGroup) );
			// Unforce:
			Saga.plugin().unforceSagaPlayer(selectedPlayer.getName());
			return;
			
		}
		
		// Add invite:
		selectedPlayer.addChunkGroupInvite(selectedChunkGroup.getId());
		
		// Inform:
		selectedPlayer.message(ChunkGroupMessages.beenInvited(selectedPlayer, selectedChunkGroup));
		selectedChunkGroup.broadcast(ChunkGroupMessages.invited(selectedPlayer, selectedChunkGroup));
		selectedPlayer.message(ChunkGroupMessages.informAccept());
		
		// Unforce:
		Saga.plugin().unforceSagaPlayer(selectedPlayer.getName());
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
    		sagaPlayer.message(ChunkGroupMessages.playerNoInvites(sagaPlayer));
    		return;
    	}
    	
    	// Find chunk group:
    	ChunkGroup selectedChunkGroup = null;
    	ArrayList<Integer> invitationIds = sagaPlayer.getChunkGroupInvites();
    	// No parameters:
    	if(args.argsLength() == 0 && invitationIds.size() > 0){
    		selectedChunkGroup = ChunkGroupManager.manager().getChunkGroup(invitationIds.get(invitationIds.size() -1 ));
    	}
    	// Chunk group name parameter:
    	else if(args.argsLength() == 1){
    		for (int i = 0; i < invitationIds.size(); i++) {
    			ChunkGroup group = ChunkGroupManager.manager().getChunkGroup(invitationIds.get(i));
				if( group != null && group.getName().equals(args.getString(0)) ){
					selectedChunkGroup = group;
					break;
				}
			}
    	}
    	
    	// Chunk group doesn't exist:
    	if(selectedChunkGroup == null && args.argsLength() == 1){
    		sagaPlayer.message(ChunkGroupMessages.nonExistantChunkGroup(args.getString(0)));
    		return;
    	}else if(selectedChunkGroup == null){
    		sagaPlayer.message( ChunkGroupMessages.nonExistantChunkGroup() );
    		return;
    	}
    	
    	// Already in a chunk group:
    	if(sagaPlayer.getChunkGroup() != null){
    		sagaPlayer.message(ChunkGroupMessages.haveCunkGroup());
    		return;
    	}
    	
    	// Inform:
    	selectedChunkGroup.broadcast(ChunkGroupMessages.joined(sagaPlayer, selectedChunkGroup));
		sagaPlayer.message(ChunkGroupMessages.haveJoined(sagaPlayer, selectedChunkGroup));

    	// Add to chunk group:
		selectedChunkGroup.addPlayer(sagaPlayer);
    	
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
    	sagaPlayer.message(ChunkGroupMessages.declinedInvites());
		
		
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
		

		ChunkGroup selectedChunkGroup = null;

		// Arguments:
		selectedChunkGroup = sagaPlayer.getChunkGroup();
		if(selectedChunkGroup == null){
			
			if(sagaPlayer.hasChunkGroup()){
				sagaPlayer.removeChunkGroupId(sagaPlayer.getChunkGroupId());
			}
			
			sagaPlayer.message( ChunkGroupMessages.noChunkGroup() );
			return;
			
		}
		
//		// Permission:
//		if(!selectedChunkGroup.hasPermission(sagaPlayer, SettlementPermission.QUIT)){
//			sagaPlayer.message(SagaMessages.noPermission());
//			return;
//		}

		// Not a member:
		if( !selectedChunkGroup.hasMember(sagaPlayer.getName()) ){
			sagaPlayer.message(ChunkGroupMessages.notChunkGroupMember(selectedChunkGroup));
			return;
		}
		
		// Remove:
		selectedChunkGroup.removePlayer(sagaPlayer);

		// Inform:
		selectedChunkGroup.broadcast(ChunkGroupMessages.quit(sagaPlayer, selectedChunkGroup));
		sagaPlayer.message(ChunkGroupMessages.haveQuit(sagaPlayer, selectedChunkGroup));
		
		// Delete:
		if(selectedChunkGroup instanceof Settlement){
			
			Settlement selectedsettlement = (Settlement) selectedChunkGroup;
			
			if(selectedChunkGroup.getMemberCount() == 0){

				if(selectedsettlement.getLevel() < SettlementConfiguration.config().noDeleteLevel){

					// Delete:
					selectedsettlement.delete();
						
					// Inform:
					Saga.broadcast(ChunkGroupMessages.dissolved(sagaPlayer, selectedsettlement));
					
				}else{
					
					sagaPlayer.message(ChunkGroupMessages.informSettlementAboveLevelDelete());
					
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
		

		ChunkGroup selectedChunkGroup = null;
		String tragetName = null;

		// Arguments:
		if(args.argsLength() == 2){
			
			// Chunk group:
			String groupName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");
			selectedChunkGroup = ChunkGroupManager.manager().getChunkGroupWithName(groupName);
			if(selectedChunkGroup == null){
				sagaPlayer.message(ChunkGroupMessages.noChunkGroup(groupName));
				return;
			}

			// Target name:
			tragetName = selectedChunkGroup.matchName(args.getString(1));
			
		}else{
			
			// Chunk group:
			selectedChunkGroup = sagaPlayer.getChunkGroup();
			if(selectedChunkGroup == null){
				sagaPlayer.message( ChunkGroupMessages.noChunkGroup() );
				return;
			}

			// Target name:
			tragetName = selectedChunkGroup.matchName(args.getString(0));
			
		}
		
		// Permission:
		if(!selectedChunkGroup.hasPermission(sagaPlayer, SettlementPermission.KICK)){
			sagaPlayer.message(SagaMessages.noPermission());
			return;
		}

		// Force player:
		SagaPlayer kickedPlayer;
		try {
			kickedPlayer = Saga.plugin().forceSagaPlayer(tragetName);
		} catch (NonExistantSagaPlayerException e) {
			sagaPlayer.message(SagaMessages.invalidPlayer(tragetName));
			return;
		}
		
		// Target in the faction:
		if(!selectedChunkGroup.hasMember(kickedPlayer.getName())){
			sagaPlayer.message(ChunkGroupMessages.playerNotChunkGroupMember(kickedPlayer, selectedChunkGroup));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(tragetName);
			return;
		}
		
		// Kicked yourself:
		if(kickedPlayer.equals(sagaPlayer)){
			sagaPlayer.message(ChunkGroupMessages.cantKickYourself(sagaPlayer, selectedChunkGroup));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(tragetName);
			return;
		}
		
		// Remove player:
		selectedChunkGroup.removePlayer(kickedPlayer);
		
		// Unforce:
		Saga.plugin().unforceSagaPlayer(tragetName);

		// Inform:
		selectedChunkGroup.broadcast(ChunkGroupMessages.kicked(kickedPlayer, selectedChunkGroup));
		kickedPlayer.message(ChunkGroupMessages.beenKicked(kickedPlayer, selectedChunkGroup));
		
		// Delete:
		if(selectedChunkGroup instanceof Settlement){
			
			Settlement selectedsettlement = (Settlement) selectedChunkGroup;
			
			if(selectedChunkGroup.getMemberCount() == 0){

				if(selectedsettlement.getLevel() < SettlementConfiguration.config().noDeleteLevel){

					// Delete:
					selectedsettlement.delete();
						
					// Inform:
					Saga.broadcast(ChunkGroupMessages.dissolved(sagaPlayer, selectedsettlement));
					
				}else{
					
					sagaPlayer.message(ChunkGroupMessages.informSettlementAboveLevelDelete());
					
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
		

		ChunkGroup selectedChunkGroup = null;
		String targetName = null;
		

		// Arguments:
		if(args.argsLength() == 2){
			
			// Chunk group:
			String groupName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");
			selectedChunkGroup = ChunkGroupManager.manager().getChunkGroupWithName(groupName);
			if(selectedChunkGroup == null){
				sagaPlayer.message(ChunkGroupMessages.noChunkGroup(groupName));
				return;
			}

			// Name:
			targetName = args.getString(1);
			
		}else{
			
			// Chunk group:
			selectedChunkGroup = sagaPlayer.getChunkGroup();
			if(selectedChunkGroup == null){
				sagaPlayer.message( ChunkGroupMessages.noChunkGroup() );
				return;
			}

			// Name:
			targetName = args.getString(0);
			
		}
		
		// Permission:
		if(!selectedChunkGroup.hasPermission(sagaPlayer, SettlementPermission.DECLARE_OWNER)){
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
		selectedChunkGroup.removeOwner();
		
		// Set new owner:
		selectedChunkGroup.setOwner(targetPlayer.getName());
		
		// Inform:
		selectedChunkGroup.broadcast(ChunkGroupMessages.newOwner(targetName));
		
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

		
		ChunkGroup selectedChunkGroup = null;
		String targetName = null;
		String roleName = null;
		
		// Arguments:
		if(args.argsLength() == 3){
			
			// Chunk group:
			String groupName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");
			selectedChunkGroup = ChunkGroupManager.manager().getChunkGroupWithName(groupName);
			if(selectedChunkGroup == null){
				sagaPlayer.message(ChunkGroupMessages.noChunkGroup(groupName));
				return;
			}
			
			targetName = selectedChunkGroup.matchName(args.getString(1));

			roleName = args.getString(2).replace(SagaMessages.spaceSymbol, " ").toLowerCase();;
			
		}else{
			
			// Chunk group:
			selectedChunkGroup = sagaPlayer.getChunkGroup();
			if(selectedChunkGroup == null){
				sagaPlayer.message( ChunkGroupMessages.noChunkGroup() );
				return;
			}

			targetName = selectedChunkGroup.matchName(args.getString(0));

			roleName = args.getString(1).replace(SagaMessages.spaceSymbol, " ").toLowerCase();
			
		}
		
		// Is a settlement:
		if(!(selectedChunkGroup instanceof Settlement)){
			sagaPlayer.message(ChunkGroupMessages.notSettlement(selectedChunkGroup));
			return;
		}
		Settlement selectedSettlement = (Settlement) selectedChunkGroup;

		// Permission:
		if(!selectedSettlement.hasPermission(sagaPlayer, SettlementPermission.SET_ROLE)){
			sagaPlayer.message(SagaMessages.noPermission(selectedChunkGroup));
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

		// Not chunk group member:
		if( !selectedChunkGroup.equals(targetPlayer.getChunkGroup()) ){
			sagaPlayer.message(ChunkGroupMessages.notChunkGroupMember(selectedChunkGroup, targetPlayer.getName()));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}

		// Get role:
		Proficiency role;
		try {
			role = ProficiencyConfiguration.config().createProficiency(roleName);
		} catch (InvalidProficiencyException e) {
			sagaPlayer.message(ChunkGroupMessages.invalidRole(roleName));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}
		
		// Role available:
		if(!selectedSettlement.isRoleAvailable(role.getHierarchy())){
			sagaPlayer.message(ChunkGroupMessages.roleNotAvailable(roleName));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}
		
		// Set role:
		selectedSettlement.setRole(targetPlayer, role);
		
		// Inform:
		selectedChunkGroup.broadcast(ChunkGroupMessages.newRole(targetPlayer, selectedSettlement, roleName));

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
		Settlement selectedSettlement = null;
		
		ChunkGroup selectedChunkGroup = null;
		String sPage = null;
		String groupName = null;
		
		// Arguments:
		switch (args.argsLength()) {
			
			case 2:
				
				// Chunk group:
				groupName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");
				selectedChunkGroup = ChunkGroupManager.manager().getChunkGroupWithName(groupName);
				if(selectedChunkGroup == null){
					sagaPlayer.message(ChunkGroupMessages.noChunkGroup(groupName));
					return;
				}
				
				// Page:
				sPage = args.getString(1);
				try {
					page = Integer.parseInt(sPage);
				}
				catch (NumberFormatException e) {
					sagaPlayer.message(ChunkGroupMessages.invalidInteger(sPage));
					return;
				}
				break;

			case 1:

				// Chunk group:
				selectedChunkGroup = sagaPlayer.getChunkGroup();
				if(selectedChunkGroup == null){
					sagaPlayer.message( ChunkGroupMessages.noChunkGroup() );
					return;
				}

				// Page:
				sPage = args.getString(0);
				try {
					page = Integer.parseInt(sPage);
				}
				catch (NumberFormatException e) {
					sagaPlayer.message(ChunkGroupMessages.invalidInteger(sPage));
					return;
				}
				
				break;

			default:

				// Chunk group:
				selectedChunkGroup = sagaPlayer.getChunkGroup();
				if(selectedChunkGroup == null){
					sagaPlayer.message(ChunkGroupMessages.noChunkGroup());
					return;
				}
				
				// Page:
				page = 1;
				
				break;
				
		}
		
		// Is a settlement:
		if(! (selectedChunkGroup instanceof Settlement) ){
			sagaPlayer.message(ChunkGroupMessages.notSettlement(selectedChunkGroup));
			return;
		}
		selectedSettlement = (Settlement) selectedChunkGroup;
		
		// Inform:
		sagaPlayer.message(ChunkGroupMessages.stats(sagaPlayer, selectedSettlement, page -1));
		
		
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
		

		ChunkGroup selectedChunkGroup = null;

		// Arguments:
		if(args.argsLength() == 1){
			
			// Chunk group:
			String groupName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");
			selectedChunkGroup = ChunkGroupManager.manager().getChunkGroupWithName(groupName);
			if(selectedChunkGroup == null){
				sagaPlayer.message(ChunkGroupMessages.noChunkGroup(groupName));
				return;
			}
			
		}else{
			
			// Chunk group:
			selectedChunkGroup = sagaPlayer.getChunkGroup();
			if(selectedChunkGroup == null){
				sagaPlayer.message( ChunkGroupMessages.noChunkGroup() );
				return;
			}
			
		}
		
		// Is a settlement:
		if(! (selectedChunkGroup instanceof Settlement) ){
			sagaPlayer.message(ChunkGroupMessages.notSettlement(selectedChunkGroup));
			return;
		}
		Settlement selectedSettlement = (Settlement) selectedChunkGroup;
		
		// Inform:
		sagaPlayer.message(ChunkGroupMessages.list(sagaPlayer, selectedSettlement));
		
		
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
		sagaPlayer.message(ChunkGroupMessages.wrongQuit());
		
		
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
				sagaPlayer.message(ChunkGroupMessages.invalidPage(args.getString(0)));
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
	    	
		
		ChunkGroup selectedChunkGroup = null;
		String name = null;

		// Arguments:
		if(args.argsLength() == 2){
			
			// Chunk group:
			String groupName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");
			selectedChunkGroup = ChunkGroupManager.manager().getChunkGroupWithName(groupName);
			if(selectedChunkGroup == null){
				sagaPlayer.message(ChunkGroupMessages.noChunkGroup(groupName));
				return;
			}
			
			name = args.getString(1).replaceAll(SagaMessages.spaceSymbol, " ");
			
		}else{
			
			// Chunk group:
			selectedChunkGroup = sagaPlayer.getChunkGroup();
			if(selectedChunkGroup == null){
				sagaPlayer.message(ChunkGroupMessages.noChunkGroup());
				return;
			}
			
			name = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");

		}

		// Permission:
		if(!selectedChunkGroup.hasPermission(sagaPlayer, SettlementPermission.RENAME)){
			sagaPlayer.message(SagaMessages.noPermission(selectedChunkGroup));
			return;
		}

		// Fix spaces:
		while(name.contains("  ")){
			name = name.replaceAll("  ", " ");
		}
	    	
	    // Validate name:
	    if(!validateName(name)){
	    	sagaPlayer.message(ChunkGroupMessages.invalidName());
	    	return;
	    }
	    	
	    // Check name:
	    if(ChunkGroupManager.manager().getChunkGroupWithName(name) != null){
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
	    selectedChunkGroup.setName(name);
	    
	    // Inform:
	    selectedChunkGroup.broadcast(ChunkGroupMessages.renamed(selectedChunkGroup));
	    	
	    	
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
	 * Gets the chunk group the player is standing on.
	 * 
	 * @param sagaPlayer saga player
	 * @return chunk group, null if none
	 */
	private static ChunkGroup getLocationChunkGroup(SagaPlayer sagaPlayer) {

		Location location = sagaPlayer.getLocation();
		if(location == null) return null;
		
		SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(location);
		if(sagaChunk == null) return null;
		
		return sagaChunk.getChunkGroup();
		
	}
	
	
}
