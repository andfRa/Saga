package org.saga.chunkGroups;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.saga.Saga;
import org.saga.SagaMessages;
import org.saga.buildings.Building;
import org.saga.buildings.BuildingMessages;
import org.saga.buildings.MissingBuildingDefinitionException;
import org.saga.config.ChunkGroupConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.config.ProficiencyConfiguration.InvalidProficiencyException;
import org.saga.economy.EconomyMessages;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.factions.FactionMessages;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Settlement;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;


public class ChunkGroupCommands {

	
	public static Integer maximumNameLength = 15;
	
	public static Integer minimumNameLenght = 3;
	
	

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
		SagaChunk locationChunk = sagaPlayer.getSagaChunk();
		Location location = sagaPlayer.getLocation();
		if(location == null){
			Saga.severe(ChunkGroupCommands.class, "saga player location is null for " + sagaPlayer.getName(), "ignoring command");
			sagaPlayer.error("failed to retrieve "+ sagaPlayer +" player location");
			return;
		}
		
		// Already claimed:
		if(locationChunk != null){
			sagaPlayer.message(ChunkGroupMessages.chunkClaimed());
			return;
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
		settlement.addChunk(new SagaChunk(location));
		Settlement.create(settlement, sagaPlayer);
		
		// Inform:
		Saga.broadcast(ChunkGroupMessages.foundedChunkGroupBroadcast(sagaPlayer, settlement));
		
		
	}
	
	 @Command(
	            aliases = {"sclaim", "claim"},
	            usage = "[settlement name]",
	            flags = "",
	            desc = "Claim a chunk of land adjacent to a settlement.",
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
			selectedChunkGroup = sagaPlayer.getRegisteredChunkGroup();
			if(selectedChunkGroup == null){
				sagaPlayer.message( ChunkGroupMessages.noChunkGroup() );
				return;
			}
			
		}
		
		// Location chunk:
		SagaChunk locationChunk = sagaPlayer.getSagaChunk();
	   	Location location = sagaPlayer.getLocation();
	   	if(location == null){
	   		Saga.severe(ChunkGroupCommands.class, "saga player location is null for " + sagaPlayer.getName(), "ignoring command");
	   		sagaPlayer.error("failed to retrieve "+ sagaPlayer +" player location");
	   		return;
	   	}
	   	Chunk bukkitChunk = location.getWorld().getChunkAt(location);
	  
	   	// Already claimed:
	   	if(locationChunk != null){
			sagaPlayer.message(ChunkGroupMessages.chunkClaimed());
			return;
		}
	   	
		// Not adjacent:
	   	if(!selectedChunkGroup.isAdjacent(bukkitChunk)){
			sagaPlayer.message(ChunkGroupMessages.chunkMustBeAdjacent());
			return;
		}
	   	
	   	// Permissions:
	   	if(!selectedChunkGroup.canClaim(sagaPlayer)){
	   		sagaPlayer.message(SagaMessages.noPermission());
	   		return;
	   	}
	   	
	   	// Claim points:
	   	Settlement selectedSettlement = null;
	   	if(selectedChunkGroup instanceof Settlement){
	   		selectedSettlement = (Settlement) selectedChunkGroup;
	   	}
	   	if(selectedSettlement == null || !selectedSettlement.isClaimAvailable()){
	   		sagaPlayer.message(ChunkGroupMessages.notEnoughClaims());
	   		return;
	   	}
	   	
		// Add a new chunk to adjacent chunk group:
		SagaChunk sagaChunk = new SagaChunk(bukkitChunk);
		selectedChunkGroup.addChunk(sagaChunk);
		
		// Inform:
		sagaChunk.broadcast(ChunkGroupMessages.claimedChunkBroadcast(sagaPlayer, selectedChunkGroup));
		
		
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
		if(!selectedChunkGroup.canClaimChunkGroup(sagaPlayer)){
			sagaPlayer.message(SagaMessages.noPermission());
			return;
		}

    	// Already has chunk group:
    	if(sagaPlayer.getRegisteredChunkGroup() != null){
    		sagaPlayer.message(ChunkGroupMessages.haveCunkGroup());
    		return;
    	}
		
		// Already in chunk group:
		if(selectedChunkGroup.hasPlayer(sagaPlayer.getName())){
			sagaPlayer.message(ChunkGroupMessages.alreadyInTheChunkGroup(selectedChunkGroup));
			return;
		}
		
		// Set owner role:
		if(selectedChunkGroup instanceof Settlement){
			try {
				((Settlement) selectedChunkGroup).setRole(sagaPlayer, ChunkGroupConfiguration.config().settlementOwnerRole);
			} catch (InvalidProficiencyException e) {
				Saga.severe(ChunkGroupCommands.class, "failed to add " + ChunkGroupConfiguration.config().settlementOwnerRole + " proficiency to a chunk group, because the proficiency name is invalid", "ignoring request");
			}
		}
		
		// Add to chunk group:
		selectedChunkGroup.addPlayer(sagaPlayer);
		
		// Inform:
		sagaPlayer.message(ChunkGroupMessages.claimedChunkGroupBroadcast(sagaPlayer, selectedChunkGroup));
		
		 
	}
	 
	@Command(
	            aliases = {"sabandon", "sunclaim"},
	            usage = "",
	            flags = "",
	            desc = "Abandon the chunk of settlement land you are currently standing on. Delete if no land is left.",
	            min = 0,
	            max = 0
	        )
	@CommandPermissions({"saga.user.settlement.abandon"})
	public static void abandon(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

			
		// Location chunk:
	   	SagaChunk locationChunk = sagaPlayer.getSagaChunk();
	   	Location location = sagaPlayer.getLocation();
	   	if(location == null){
	   		Saga.severe(ChunkGroupCommands.class, "saga player location is null for " + sagaPlayer.getName(), "ignoring command");
	   		sagaPlayer.error("failed to retrieve "+ sagaPlayer +" player location");
	   		return;
	   	}
	   	
	   	// Unclaimed:
	   	if(locationChunk == null){
			sagaPlayer.message(ChunkGroupMessages.chunkNotClaimed());
			return;
		}
	   	
	   	// Chunk group:
	   	ChunkGroup chunkGroup = locationChunk.getChunkGroup();
	   	
	   	// Permissions:
	   	if(!chunkGroup.canAbandon(sagaPlayer)){
	   		sagaPlayer.message(SagaMessages.noPermission());
	   		return;
	   	}
	   	
		// Remove chunk from the chunk group:
		chunkGroup.removeChunk(locationChunk);
		
		// Inform:
		locationChunk.broadcast(ChunkGroupMessages.abandonedChunkBroadcast(sagaPlayer, chunkGroup));
		
		// Delete if none left:
		if( chunkGroup.getSize() == 0 ){
			chunkGroup.delete();
			Saga.broadcast(ChunkGroupMessages.broadcastDeleted(sagaPlayer, chunkGroup));
		}
		
		
	}
	
	@Command(
            aliases = {"sinvite"},
            usage = "[settlement name] <player name>",
            flags = "",
            desc = "Invite a player to join a settlement.",
            min = 1,
            max = 2)
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
			selectedChunkGroup = sagaPlayer.getRegisteredChunkGroup();
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
		if( !selectedChunkGroup.canInvite(sagaPlayer) ){
			sagaPlayer.message(SagaMessages.noPermission());
			return;
		}
		
		// Already a member:
		if(selectedChunkGroup.hasPlayer( selectedPlayer.getName()) ){
		
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
            max = 1)
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
    	if(sagaPlayer.getRegisteredChunkGroup() != null){
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
            max = 0)
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
            desc = "Quit settlement.",
            min = 0,
            max = 0
		)
	@CommandPermissions({"saga.user.settlement.quit"})
	public static void quit(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		ChunkGroup selectedChunkGroup = null;

		// Arguments:
		selectedChunkGroup = sagaPlayer.getRegisteredChunkGroup();
		if(selectedChunkGroup == null){
			
			if(sagaPlayer.hasChunkGroup()){
				sagaPlayer.removeChunkGroupId(sagaPlayer.getChunkGroupId());
			}
			
			sagaPlayer.message( ChunkGroupMessages.noChunkGroup() );
			return;
			
		}
		
		// Permission:
		if(!selectedChunkGroup.canQuit(sagaPlayer)){
			sagaPlayer.message(SagaMessages.noPermission());
			return;
		}

		// Not a member:
		if( !selectedChunkGroup.hasPlayer(sagaPlayer.getName()) ){
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
			
			if(selectedChunkGroup.getPlayerCount() == 0){

				if(selectedsettlement.getLevel() < ChunkGroupConfiguration.config().noDeleteLevel){

					// Delete:
					selectedsettlement.delete();
						
					// Inform:
					Saga.broadcast(ChunkGroupMessages.broadcastDeleted(sagaPlayer, selectedsettlement));
					
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
            desc = "Kick a member out of the settlement.",
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
			selectedChunkGroup = sagaPlayer.getRegisteredChunkGroup();
			if(selectedChunkGroup == null){
				sagaPlayer.message( ChunkGroupMessages.noChunkGroup() );
				return;
			}

			// Target name:
			tragetName = selectedChunkGroup.matchName(args.getString(0));
			
		}
		
		// Permission:
		if(!selectedChunkGroup.canKick(sagaPlayer)){
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
		if(!selectedChunkGroup.hasPlayer(kickedPlayer.getName())){
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
			
			if(selectedChunkGroup.getPlayerCount() == 0){

				if(selectedsettlement.getLevel() < ChunkGroupConfiguration.config().noDeleteLevel){

					// Delete:
					selectedsettlement.delete();
						
					// Inform:
					Saga.broadcast(ChunkGroupMessages.broadcastDeleted(sagaPlayer, selectedsettlement));
					
				}else{
					
					sagaPlayer.message(ChunkGroupMessages.informSettlementAboveLevelDelete());
					
				}
				
			}
			
		}
		
		
	}
	
	@Command(
            aliases = {"slist"},
            usage = "[settlement name]",
            flags = "",
            desc = "Lists all players in the settlement.",
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
			selectedChunkGroup = sagaPlayer.getRegisteredChunkGroup();
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

	@Command(
            aliases = {"sstats"},
            usage = "[settlement name]",
            flags = "",
            desc = "Lists settlement stats.",
            min = 0,
            max = 1
		)
	@CommandPermissions({"saga.user.settlement.stats"})
	public static void stats(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		Settlement selectedSettlement = null;
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
			selectedChunkGroup = sagaPlayer.getRegisteredChunkGroup();
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
		selectedSettlement = (Settlement) selectedChunkGroup;
		
		// Inform:
		sagaPlayer.message(ChunkGroupMessages.stats(sagaPlayer, selectedSettlement));
		
		
	}
	
	@Command(
            aliases = {"settlementfactioninvite", "sfinvite"},
            usage = "[settlement name] <faction name>",
            flags = "",
            desc = "Invite a faction to join your settlement.",
            min = 1,
            max = 2)
	@CommandPermissions({"saga.user.settlement.faction.invite"})
	public static void factionInvite(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
//	
//		ChunkGroup selectedChunkGroup = null;
//		SagaFaction selectedFaction = null;
//		
//
//		// Selected chunk group:
//		if(args.argsLength() == 2){
//			selectedChunkGroup = getChunkGroup(args.getString(0));
//			if(selectedChunkGroup == null){
//				sagaPlayer.sendMessage( ChunkGroupMessages.noChunkGroup() );
//				return;
//			}
//		}else{
//			selectedChunkGroup = getChunkGroup(sagaPlayer);
//			if(selectedChunkGroup == null){
//				sagaPlayer.sendMessage( ChunkGroupMessages.noChunkGroup(args.getString(0)) );
//				return;
//			}
//		}
//		
//		// Faction:
//		String factionName = null;
//		if(args.argsLength() == 2){
//			factionName = args.getString(1);
//			selectedFaction = FactionManager.getFactionManager().factionWithName(args.getString(1));
//		}else{
//			factionName = args.getString(0);
//			selectedFaction = FactionManager.getFactionManager().factionWithName(args.getString(0));
//		}
//		
//		// Non-existent faction:
//		if(selectedFaction == null){
//			sagaPlayer.sendMessage(FactionMessages.nonExistentFaction(factionName));
//			return;
//		}
//		
//		// Permission:
//		if( !selectedChunkGroup.canInvite(sagaPlayer) ){
//			sagaPlayer.sendMessage(SagaMessages.noPermission());
//			return;
//		}
//		
//		// Already a member:
//		if(selectedChunkGroup.hasFaction(selectedFaction.getId())){
//			sagaPlayer.sendMessage( ChunkGroupMessages.alreadyInTheChunkGroup(selectedFaction, selectedChunkGroup) );
//			return;
//		}
//		
//		// Already invited:
//		if(selectedFaction.hasChunkGrouInvite(selectedChunkGroup.getId())){
//			sagaPlayer.sendMessage( ChunkGroupMessages.alreadyInvited(selectedFaction, selectedChunkGroup) );
//			return;
//		}
//		
//		// Add invite:
//		selectedFaction.addChunkGroupInvitation(selectedChunkGroup.getId());
//		
//		// Inform:
//		selectedFaction.broadcast(ChunkGroupMessages.beenInvited(selectedFaction, selectedChunkGroup));
//		selectedChunkGroup.broadcast(ChunkGroupMessages.invited(selectedFaction, selectedChunkGroup));
//		
		
	}

	@Command(
            aliases = {"settlementfactionaccpet", "sfaccept"},
            usage = "<settlement name>",
            flags = "",
            desc = "Accept a settlement invitation.",
            min = 0,
            max = 1)
	@CommandPermissions({"saga.user.settlement.faction.accept"})
	public static void factionAccept(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
//		SagaFaction selectedFaction = null;
//		String chunkGroupName = null;
//		ChunkGroup selectedChunkGroup = null;
//
//		if(args.argsLength() >= 1){
//			chunkGroupName = args.getString(0);
//		}
//		
//		// Part of a faction:
//		if(sagaPlayer.getFactionCount() == 0){
//			sagaPlayer.sendMessage(FactionMessages.noFaction());
//			return;
//		}
//			
//		// Faction selection:
//		ArrayList<SagaFaction> selectedFactions = sagaPlayer.getSelectedFactions();
//		if(selectedFactions.size() != 1){
//			sagaPlayer.sendMessage( FactionMessages.mustSelectOneFaction() );
//			return;
//		}
//		selectedFaction = selectedFactions.get(0);
//
//		// Permission:
//		if( !selectedFaction.canSettlementAccept(sagaPlayer) ){
//			sagaPlayer.sendMessage(FactionMessages.noPermission(selectedFaction));
//			return;
//		}
//		
//    	// No invites:
//		ArrayList<ChunkGroup> groupsInvited = getChunkGroups(selectedFaction.getChunkGroupInvites());
//    	if(groupsInvited.size() == 0){
//    		sagaPlayer.sendMessage(ChunkGroupMessages.factionNoInvites(selectedFaction));
//    		return;
//    	}
//    	
//    	// Chunk group selection:
//    	if(chunkGroupName == null){
//    		selectedChunkGroup = groupsInvited.get( groupsInvited.size()-1 );
//    	}else 
//    	for (ChunkGroup chunkGroup : groupsInvited) {
//			if(chunkGroup.getName().equals(chunkGroupName)){
//				selectedChunkGroup = chunkGroup;
//				break;
//			}
//		}
//    	
//    	// Invalid chunk group selection:
//    	if(selectedChunkGroup == null){
//    		sagaPlayer.sendMessage(ChunkGroupMessages.factionNoInvites(selectedFaction, chunkGroupName));
//    		return;
//    	}
//    	
//    	// Inform:
//    	selectedChunkGroup.broadcast(ChunkGroupMessages.joined(selectedFaction, selectedChunkGroup));
//		selectedFaction.broadcast(ChunkGroupMessages.haveJoined(selectedFaction, selectedChunkGroup));
//    	
//    	// Add faction to chunk group:
////    	ChunkGroupManager.manager().addFaction(selectedChunkGroup, selectedFaction);
////    	ChunkGroupManager.manager().registerFaction(selectedChunkGroup, selectedFaction);
//    	
//    	// Decline every invitation:
//    	ArrayList<Integer> chunkGroupIds = selectedFaction.getChunkGroupInvites();
//    	for (int i = 0; i < chunkGroupIds.size(); i++) {
//    		selectedFaction.removeChunkGroupInvitation(chunkGroupIds.get(i));
//		}
    	
    	
	}

	@Command(
            aliases = {"settlementfactionquit","sfquit"},
            usage = "",
            flags = "",
            desc = "Quit current settlement.",
            min = 0,
            max = 1
		)
	@CommandPermissions({"saga.user.settlement.faction.quit"})
	public static void factionQuit(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
//
//		// Part of a faction:
//		if(sagaPlayer.getFactionCount() == 0){
//			sagaPlayer.sendMessage(FactionMessages.noFaction());
//			return;
//		}
//			
//		// Faction selection:
//		ArrayList<SagaFaction> selectedFactions = sagaPlayer.getSelectedFactions();
//		if(selectedFactions.size() != 1){
//			sagaPlayer.sendMessage( FactionMessages.mustSelectOneFaction() );
//			return;
//		}
//		SagaFaction selectedFaction = selectedFactions.get(0);
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		// Chunk group selection:
//		SagaChunk selectedChunk = sagaPlayer.getSagaChunk();
//		ChunkGroup selectedChunkGroup = null;
//		if(selectedChunk != null) selectedChunkGroup = selectedChunk.getChunkGroup();
//		if(selectedChunkGroup == null){
//			sagaPlayer.sendMessage( ChunkGroupMessages.notInChunkGroup() );
//			return;
//		}
//
//		// Not a member:
//		if( !selectedChunkGroup.hasRegisteredMember(sagaPlayer) ){
//			sagaPlayer.sendMessage(ChunkGroupMessages.notChunkGroupMember(selectedChunkGroup));
//			return;
//		}
//		
//		// Quit:
//		selectedChunkGroup.removePlayer(sagaPlayer);
//		
//		// Inform:
//		selectedChunkGroup.broadcast(ChunkGroupMessages.quit(sagaPlayer, selectedChunkGroup));
//		sagaPlayer.sendMessage(ChunkGroupMessages.haveQuit(sagaPlayer, selectedChunkGroup));
		
		
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
			selectedChunkGroup = sagaPlayer.getRegisteredChunkGroup();
			if(selectedChunkGroup == null){
				sagaPlayer.message( ChunkGroupMessages.noChunkGroup() );
				return;
			}

			// Name:
			targetName = args.getString(0);
			
		}
		
		// Permission:
		if(!selectedChunkGroup.canDeclareOwner(sagaPlayer)){
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
            aliases = {"ssetbuilding","setbuilding","bset"},
            usage = "<building_name>",
            flags = "",
            desc = "Sets a building on this chunk of land.",
            min = 1,
            max = 1
		)
	@CommandPermissions({"saga.user.settlement.building.set"})
	public static void setBuilding(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		String buildingName = null;
		ChunkGroup selectedChunkGroup = null;

		// Arguments:
		buildingName = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
		
		// Selected chunk:
		SagaChunk selectedChunk = sagaPlayer.getSagaChunk();
	   	if(selectedChunk == null){
			sagaPlayer.message(BuildingMessages.buildingsOnClaimedLand(selectedChunkGroup));
			return;
		}
		
	   	// Selected chunk group:
	   	selectedChunkGroup = selectedChunk.getChunkGroup();
		
		// Building:
		Building selectedBuilding;
		try {
			selectedBuilding = ChunkGroupConfiguration.config().newBuilding(buildingName);
		} catch (MissingBuildingDefinitionException e) {
			Saga.severe(ChunkGroupCommands.class, sagaPlayer + " tried to set a building with missing definition", "stopping command");
			sagaPlayer.error("definition missing for " + buildingName + " is missing");
			return;
		}
		if(selectedBuilding == null){
			sagaPlayer.message(BuildingMessages.invalidName(buildingName));
			return;
		}
		
		// Permission:
		if(!selectedChunkGroup.canSetBuilding(sagaPlayer, selectedBuilding)){
			sagaPlayer.message(SagaMessages.noPermission(selectedChunkGroup));
			return;
		}

		// Existing building:
		if(selectedChunk.getBuilding() != null){
			sagaPlayer.message(BuildingMessages.oneBuildingPerChunk(selectedChunkGroup));
			return;
		}
		
		// Building points:
		Integer cost = selectedBuilding.getPointCost();
		Integer available = selectedChunkGroup.getAvailableBuildingPoints();
		if(cost > available){
			sagaPlayer.message(ChunkGroupMessages.notEnoughBuildingPoints(selectedChunkGroup, available, cost));
			return;
		}
		
		// Building available:
		if(!selectedChunkGroup.isBuildingAvailable(buildingName)){
			sagaPlayer.message(BuildingMessages.unavailableBuilding(selectedChunkGroup, selectedBuilding));
			return;
		}
		
		// Set building:
		selectedChunk.setBuilding(selectedBuilding);
		
		// Inform:
		selectedChunkGroup.broadcast(BuildingMessages.newBuilding(selectedChunkGroup, selectedBuilding, sagaPlayer));
		

	}
	
	@Command(
            aliases = {"sremovebuilding","abanonbuilding","bremove"},
            usage = "[settlement name]",
            flags = "",
            desc = "Abandons a building on this chunk of land.",
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
			sagaPlayer.message(BuildingMessages.buildingsOnClaimedLand(selectedChunkGroup));
			return;
		}
		
		// Existing building:
		Building selectedBuilding = selectedChunk.getBuilding();
		if(selectedBuilding == null){
			sagaPlayer.message(ChunkGroupMessages.noBuilding());
			return;
		}
		
		// Permission:
		if(!selectedChunkGroup.canRemoveBuilding(sagaPlayer, selectedBuilding)){
			sagaPlayer.message(SagaMessages.noPermission(selectedChunkGroup));
			return;
		}

		// Remove building:
		selectedChunk.removeBuilding();
		
		// Inform:
		selectedChunkGroup.broadcast(BuildingMessages.deletedBuilding(selectedChunkGroup, selectedBuilding, sagaPlayer));
		
		
	}

	@Command(
            aliases = {"ssetrole"},
            usage = "<player name> <role_name>",
            flags = "",
            desc = "Set a settlement members role.",
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

			roleName = args.getString(2).replace(SagaMessages.spaceSymbol, " ");
			
		}else{
			
			// Chunk group:
			selectedChunkGroup = sagaPlayer.getRegisteredChunkGroup();
			if(selectedChunkGroup == null){
				sagaPlayer.message( ChunkGroupMessages.noChunkGroup() );
				return;
			}

			targetName = selectedChunkGroup.matchName(args.getString(0));

			roleName = args.getString(1).replace(SagaMessages.spaceSymbol, " ");
			
		}
		
		// Is a settlement:
		if(!(selectedChunkGroup instanceof Settlement)){
			sagaPlayer.message(ChunkGroupMessages.notSettlement(selectedChunkGroup));
			return;
		}
		Settlement selectedSettlement = (Settlement) selectedChunkGroup;

		// Permission:
		if(!selectedSettlement.canSetRole(sagaPlayer)){
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
		if( !selectedChunkGroup.equals(targetPlayer.getRegisteredChunkGroup()) ){
			sagaPlayer.message(ChunkGroupMessages.notChunkGroupMember(selectedChunkGroup, targetPlayer.getName()));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}
		
		// Rank available:
		if(!selectedSettlement.isRoleAvailable(roleName)){
			sagaPlayer.message(ChunkGroupMessages.roleNotAvailable(roleName));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}
		
		// Set rank:
		try {
			selectedSettlement.setRole(targetPlayer, roleName);
		} catch (InvalidProficiencyException e) {
			sagaPlayer.message(ChunkGroupMessages.invalidRole(roleName));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}
		
		// Inform:
		selectedChunkGroup.broadcast(ChunkGroupMessages.newRole(targetPlayer, selectedSettlement, roleName));

		// Unforce:
		Saga.plugin().unforceSagaPlayer(targetName);
		
		
	}
	
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
			selectedChunkGroup = sagaPlayer.getRegisteredChunkGroup();
			if(selectedChunkGroup == null){
				sagaPlayer.message(ChunkGroupMessages.noChunkGroup());
				return;
			}
			
			name = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");

		}

		// Permission:
		if(!selectedChunkGroup.canRename(sagaPlayer)){
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
		if(selectedChunkGroup.isFormed() && cost > 0){

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
	
	// Info:
	@Command(
            aliases = {"squit"},
            usage = "",
            flags = "",
            desc = "Quit settlement.",
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
		sagaPlayer.message(ChunkGroupMessages.help(page - 1));

		
	}

	
	// Other:
	@Command(
			aliases = {"bstats"},
			usage = "",
			flags = "",
			desc = "Display building stats.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.user.settlement.building.stats"})
	public static void buildingStats(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


		// Building:
		SagaChunk sagaChunk =  sagaPlayer.getSagaChunk();
		Building selectedBuilding = null;
		if(sagaChunk != null){
			selectedBuilding = sagaChunk.getBuilding();
		}
		if(selectedBuilding == null){
			sagaPlayer.message(ChunkGroupMessages.noBuilding());
			return;
		}
		
		// Inform:
		sagaPlayer.message(ChunkGroupMessages.buildingStats(selectedBuilding));

	}
	
	
	// Admin:
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
			selectedChunkGroup = sagaPlayer.getRegisteredChunkGroup();
			if(selectedChunkGroup == null){
				sagaPlayer.message( ChunkGroupMessages.noChunkGroup() );
				return;
			}
			
		}
	   	
	   	// Permissions:
	   	if(!selectedChunkGroup.canDisolve(sagaPlayer)){
	   		sagaPlayer.message(SagaMessages.noPermission(selectedChunkGroup));
	   		return;
	   	}

	   	// Level too high:
	   	if(selectedChunkGroup instanceof Settlement){
	   		
	   		Settlement selectedSettlement = (Settlement) selectedChunkGroup;
	   		
	   		if(selectedSettlement.getLevel() >= ChunkGroupConfiguration.config().noDeleteLevel){

	   			sagaPlayer.message(ChunkGroupMessages.informSettlementAboveLevelDelete());
				return;
				
			}
	   		
	   	}
	   	
		// Delete:
	   	selectedChunkGroup.delete();
			
		// Inform:
		Saga.broadcast(ChunkGroupMessages.broadcastDeleted(sagaPlayer, selectedChunkGroup));
		
	
	}
	
	@Command(
            aliases = {"assetlevel"},
            usage = "[settlement name] <level>",
            flags = "",
            desc = "Set settlement level.",
            min = 1,
            max = 2
	)
	@CommandPermissions({"saga.admin.settlement.setlevel"})
	public static void setlevel(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


		Integer level = null;
		ChunkGroup selectedChunkGroup = null;

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
				level = Integer.parseInt(args.getString(1));
			} catch (NumberFormatException e) {
				sagaPlayer.message(ChunkGroupMessages.invalidInteger(args.getString(1)));
				return;
			}
			
		}else{
			
			// Chunk group:
			selectedChunkGroup = sagaPlayer.getRegisteredChunkGroup();
			if(selectedChunkGroup == null){
				sagaPlayer.message( ChunkGroupMessages.noChunkGroup() );
				return;
			}

			try {
				level = Integer.parseInt(args.getString(0));
			} catch (NumberFormatException e) {
				sagaPlayer.message(ChunkGroupMessages.invalidInteger(args.getString(0)));
				return;
			}
			
		}
		
		// Is a settlement:
		if(!(selectedChunkGroup instanceof Settlement)){
			sagaPlayer.message(ChunkGroupMessages.notSettlement(selectedChunkGroup));
			return;
		}
		Settlement selectedSettlement = (Settlement) selectedChunkGroup;

		// Set level:
		selectedSettlement.setLevel(level);
		
		// Inform:
		selectedChunkGroup.broadcast(ChunkGroupMessages.settlementLevel(selectedSettlement));
		if(selectedChunkGroup != sagaPlayer.getRegisteredChunkGroup()){
			sagaPlayer.message(ChunkGroupMessages.setLevel(selectedSettlement));
		}
		
	}
	
	@Command(
            aliases = {"astoggleclaims"},
            usage = "[settlement name]",
            flags = "",
            desc = "Toggle unlimited claims for the settlement.",
            min = 0,
            max = 1
	)
	@CommandPermissions({"saga.admin.settlement.toggleunlimitedclaim"})
	public static void toggleUnlimitedClaim(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


		ChunkGroup selectedChunkGroup = null;
		
		// Arguments:
		if(args.argsLength() == 1){
			
			selectedChunkGroup = ChunkGroupManager.manager().getChunkGroupWithName(args.getString(0));
			if(selectedChunkGroup == null){
			sagaPlayer.message( ChunkGroupMessages.noChunkGroup(args.getString(0)) );
				return;
			}

			
		}else{
			
			selectedChunkGroup = sagaPlayer.getRegisteredChunkGroup();
			if(selectedChunkGroup == null){
				sagaPlayer.message(ChunkGroupMessages.noChunkGroup());
				return;
			}
			
		}
		
		// Toggle:
		selectedChunkGroup.toggleUnlimitedClaim();
		
		// Inform:
		sagaPlayer.message(ChunkGroupMessages.toggleUnlimitedClaim(selectedChunkGroup));
		
		
	}
	
	@Command(
            aliases = {"astogglepvp"},
            usage = "[settlement name]",
            flags = "",
            desc = "Toggle pvp protection for the settlement.",
            min = 0,
            max = 1
	)
	@CommandPermissions({"saga.admin.settlement.togglepvpprotection"})
	public static void togglePvp(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


		ChunkGroup selectedChunkGroup = null;
		
		// Arguments:
		if(args.argsLength() == 1){
			
			selectedChunkGroup = ChunkGroupManager.manager().getChunkGroupWithName(args.getString(0));
			if(selectedChunkGroup == null){
			sagaPlayer.message( ChunkGroupMessages.noChunkGroup(args.getString(0)) );
				return;
			}

			
		}else{
			
			selectedChunkGroup = sagaPlayer.getRegisteredChunkGroup();
			if(selectedChunkGroup == null){
				sagaPlayer.message(ChunkGroupMessages.noChunkGroup());
				return;
			}
			
		}
		
		// Toggle:
		selectedChunkGroup.togglePvpProtectionBonus();
		
		// Inform:
		sagaPlayer.message(ChunkGroupMessages.togglePvp(selectedChunkGroup));
		
		
	}
	
	public static boolean validateName(String str) {

         if(org.saga.utility.TextUtil.getComparisonString(str).length() < minimumNameLenght ) {
        	 return false;
         }

         if(str.length() > maximumNameLength) {
        	 return false;
         }

         for (char c : str.toCharArray()) {
                 if ( ! org.saga.utility.TextUtil.substanceChars.contains(String.valueOf(c))) {
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
