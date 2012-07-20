package org.saga.commands;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.ChatColor;
import org.saga.Saga;
import org.saga.config.EconomyConfiguration;
import org.saga.config.ProficiencyConfiguration.InvalidProficiencyException;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.factions.FactionManager;
import org.saga.factions.SagaFaction;
import org.saga.messages.ChunkGroupMessages;
import org.saga.messages.EconomyMessages;
import org.saga.messages.FactionMessages;
import org.saga.messages.InfoMessages;
import org.saga.messages.SagaMessages;
import org.saga.player.SagaPlayer;
import org.saga.utility.SagaLocation;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;


public class FactionCommands {

	// TODO Create faction config
	public static Integer maximumNameLength = 6;
	
	public static Integer minimumNameLenght = 3;
	
	public static Integer noDeleteMemberCount = 15;
	
	public static Integer noDeleteClaimCount = 5;
	
	
	// Normal:
	@Command(
			aliases = {"fcreate"},
			usage = "<faction name>",
			flags = "",
			desc = "Create a new faction.",
			min = 1,
			max = 1
	)
	@CommandPermissions({"saga.user.faction.create"})
	public static void create(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	    	
		
		// Arguments:
		String name = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");

		// Fix spaces:
		while(name.contains("  ")){
			name = name.replaceAll("  ", " ");
		}
		
	    	// Already in faction:
	    	if(sagaPlayer.getFaction() != null){
	    		sagaPlayer.message(FactionMessages.oneFactionAllowed());
	    		return;
	    	}

	    	// Validate name:
	    	if(!validateName(name)){
	    		sagaPlayer.message(FactionMessages.invalidName());
	    		return;
	    	}
	    	
	    	// Check name:
	    	if( FactionManager.manager().getFaction(name) != null){
	    		sagaPlayer.message(FactionMessages.inUse(name));
	    		return;
	    	}
	    	
	    	// Create faction:
	    	SagaFaction faction = SagaFaction.create(name, sagaPlayer);
	    	
	    	// Broadcast:
	    	sagaPlayer.message(FactionMessages.created(faction));
	    	
	    	
	}

	@Command(
            aliases = {"finvite"},
            usage = "[faction] <player name>",
            flags = "",
            desc = "Invite a player to join the faction.",
            min = 1,
            max = 2
	)
	@CommandPermissions({"saga.user.faction.invite"})
	public static void invite(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


		SagaFaction selectedFaction = null;
		String playerName = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			String factionName = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
			selectedFaction = FactionManager.manager().getFaction(factionName);
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(factionName));
				return;
			}
			
			playerName = selectedFaction.matchName(args.getString(1));
			
			
		}else{
			 
			selectedFaction = sagaPlayer.getFaction();
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.noFaction());
				return;
			}
			
			playerName = selectedFaction.matchName(args.getString(0));
			
		}
		
		// Permission:
		if(!selectedFaction.canInvite(sagaPlayer)){
			sagaPlayer.message(FactionMessages.noPermission(selectedFaction));
			return;
		}
		
		// Force player:
		SagaPlayer invitedPlayer;
		try {
			invitedPlayer = Saga.plugin().forceSagaPlayer(playerName);
		} catch (NonExistantSagaPlayerException e) {
			sagaPlayer.message(SagaMessages.invalidPlayer(playerName));
			return;
		}
		
		// Already in faction:
		if(invitedPlayer.getFaction() != null){
			sagaPlayer.message(FactionMessages.alreadyInFaction(invitedPlayer));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(playerName);
			return;
		}
		
		// Invite:
		invitedPlayer.addFactionInvite(selectedFaction.getId());
		
		// Inform:
		selectedFaction.broadcast(FactionMessages.invitedPlayer(invitedPlayer, selectedFaction));
		invitedPlayer.message(FactionMessages.beenInvited(invitedPlayer, selectedFaction));
		invitedPlayer.message(FactionMessages.informAccept());
		
		// Unforce:
		Saga.plugin().unforceSagaPlayer(playerName);
		
		
	}

	@Command(
            aliases = {"faccept"},
            usage = "[faction name]",
            flags = "",
            desc = "Accept a faction join invitation.",
            min = 0,
            max = 1)
	@CommandPermissions({"saga.user.faction.accept"})
	public static void accept(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		SagaFaction selectedFaction = null;
		
		// Part of a faction:
		if(sagaPlayer.getFaction() != null){
			sagaPlayer.message(FactionMessages.haveFaction());
			return;
		}
    	
    	// No invites:
    	if(sagaPlayer.getFactionInvites().size() == 0){
    		sagaPlayer.message(FactionMessages.noInvites());
    		return;
    	}
    	
		// Arguments:
		ArrayList<Integer> invitationIds = sagaPlayer.getFactionInvites();
		if(args.argsLength() == 1){
			
			for (int i = 0; i < invitationIds.size(); i++) {
				SagaFaction faction = FactionManager.manager().getFaction(invitationIds.get(i));
				if( faction != null && faction.getName().equals(args.getString(0)) ){
					selectedFaction = faction;
					break;
				}
			}
			
			if(selectedFaction == null && args.argsLength() == 1){
	    		sagaPlayer.message(FactionMessages.nonExistentFaction(args.getString(0)));
	    		return;
	    	}
			
		}else{
			
			selectedFaction = FactionManager.manager().getFaction(invitationIds.get( invitationIds.size() -1 ));
			
			if(selectedFaction == null){
	    		sagaPlayer.message( FactionMessages.nonExistantFaction() );
	    		return;
	    	}
			
		}

    	// Already in faction:
    	if(sagaPlayer.hasFaction()){
    		sagaPlayer.message(FactionMessages.haveFaction());
    		return;
    	}
		
    	// Inform:
    	selectedFaction.broadcast(FactionMessages.playerJoined(sagaPlayer, selectedFaction));
		sagaPlayer.message(FactionMessages.joinedFaction(sagaPlayer, selectedFaction));

		boolean formed = selectedFaction.isFormed();
		
    	// Add to faction:
		selectedFaction.addMember(sagaPlayer);

		// Inform formation:
		if(!formed && selectedFaction.isFormed()){
			Saga.broadcast(FactionMessages.formed(selectedFaction));
		}
		
    	// Remove all invitations:
    	for (int i = 0; i < invitationIds.size(); i++) {
			sagaPlayer.removeFactionInvite(invitationIds.get(i));
		}
    	
    	
	}

	@Command(
            aliases = {"fdeclineall"},
            usage = "",
            flags = "",
            desc = "Decline all faction join invitations.",
            min = 0,
            max = 0)
	@CommandPermissions({"saga.user.faction.decline"})
	public static void decline(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


    	// Decline every invitation:
    	ArrayList<Integer> factionIds = sagaPlayer.getFactionInvites();
    	for (int i = 0; i < factionIds.size(); i++) {
			sagaPlayer.removeFactionInvite(factionIds.get(i));
		}
    	
    	// Inform:
    	sagaPlayer.message(FactionMessages.declinedInvites());
		
		
	}
	
	@Command(
            aliases = {"fkick"},
            usage = "[faction] <player name>",
            flags = "",
            desc = "Kick a member out of the faction.",
            min = 1,
            max = 2
		)
	@CommandPermissions({"saga.user.faction.kick"})
	public static void kick(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		SagaFaction selectedFaction = sagaPlayer.getFaction();
		String playerName = null;

		// Arguments:
		if(args.argsLength() == 2){
			
			String factionName = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
			selectedFaction = FactionManager.manager().getFaction(factionName);
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(factionName));
				return;
			}
			
			playerName = selectedFaction.matchName(args.getString(1));
			
			
		}else{
			 
			selectedFaction = sagaPlayer.getFaction();
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.noFaction());
				return;
			}
			
			playerName = selectedFaction.matchName(args.getString(0));
			
		}
		
		// Permission:
		if(!selectedFaction.canKick(sagaPlayer)){
			sagaPlayer.message(FactionMessages.noPermission(selectedFaction));
			return;
		}
		
		// Force player:
		SagaPlayer selectedPlayer;
		try {
			selectedPlayer = Saga.plugin().forceSagaPlayer(playerName);
		} catch (NonExistantSagaPlayerException e) {
			sagaPlayer.message(SagaMessages.invalidPlayer(playerName));
			return;
		}
		
		// Kick owner:
		if(selectedFaction.isOwner(selectedPlayer.getName())){
			sagaPlayer.message(FactionMessages.cantKickOwner(selectedFaction));
			return;
		}
		
		// Not faction member:
		if( !selectedFaction.equals(selectedPlayer.getFaction()) ){
			sagaPlayer.message(FactionMessages.notFactionMember(selectedPlayer, selectedFaction));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(selectedPlayer.getName());
			return;
		}
		
		// Kicked yourself:
		if(selectedPlayer.equals(sagaPlayer)){
			sagaPlayer.message(FactionMessages.cantKickYourself(sagaPlayer, selectedFaction));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(selectedPlayer.getName());
			return;
		}
		
		boolean formed = selectedFaction.isFormed();
		
		// Kick:
		selectedFaction.removeMember(selectedPlayer.getName());
		
		// Inform disband:
		if(formed && !selectedFaction.isFormed()){
			Saga.broadcast(FactionMessages.disbanded(selectedFaction));
		}
		
		// Inform:
		selectedFaction.broadcast(FactionMessages.playerKicked(selectedPlayer, selectedFaction));
		selectedPlayer.message(FactionMessages.kickedFromFaction(selectedPlayer, selectedFaction));

		// Unforce:
		Saga.plugin().unforceSagaPlayer(selectedPlayer.getName());
		
		// Disband if no players left:
		if(selectedFaction.getMemberCount() <= 0){

			// Delete faction:
			selectedFaction.delete();
			
//			// Inform:
//	    	Saga.broadcast(FactionMessages.deleted(sagaPlayer, selectedFaction));
	    	
		}

		
	}

	@Command(
            aliases = {"factionquit"},
            usage = "",
            flags = "",
            desc = "Quit faction.",
            min = 0,
            max = 0
		)
	@CommandPermissions({"saga.user.faction.quit"})
	public static void quit(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		// Part of a faction:
		SagaFaction selectedFaction = sagaPlayer.getFaction();
		if(selectedFaction == null){
			
			if(sagaPlayer.hasFaction()){
				sagaPlayer.removeFactionId(sagaPlayer.getFactionId());
			}
			
			sagaPlayer.message(FactionMessages.noFaction());
			return;
			
		}
		
		// Permission:
		if(!selectedFaction.canQuit(sagaPlayer)){
			sagaPlayer.message(FactionMessages.noPermission(selectedFaction));
			return;
		}

		boolean formed = selectedFaction.isFormed();
		
		// Quit:
		selectedFaction.removeMember(sagaPlayer);

		// Inform disband:
		if(formed && !selectedFaction.isFormed()){
			Saga.broadcast(FactionMessages.disbanded(selectedFaction));
		}
		
		// Inform:
		selectedFaction.broadcast(FactionMessages.playerQuit(sagaPlayer, selectedFaction));
		sagaPlayer.message(FactionMessages.quitFaction(sagaPlayer, selectedFaction));

		// Disband if no players left:
		if(selectedFaction.getMemberCount() <= 0){

			// Delete faction:
			selectedFaction.delete();
			
//			// Broadcast:
//	    	Saga.broadcast(FactionMessages.deleted(sagaPlayer, selectedFaction));
	    	
		}
		
		
	}
	
	@Command(
            aliases = {"fstats"},
            usage = "[faction name or prefix]",
            flags = "",
            desc = "List faction stats.",
            min = 0,
            max = 1
        )
        @CommandPermissions({"saga.user.faction.stats"})
	public static void stats(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

	
		SagaFaction selectedFaction = null;

		// Arguments:
		 if(args.argsLength() == 1){
			
			String factionName = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
			selectedFaction = FactionManager.manager().getFaction(factionName);
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(factionName));
				return;
			}
			
			
		}else{
			 
			selectedFaction = sagaPlayer.getFaction();
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.noFaction());
				return;
			}
			
		}
		
		// Inform:
		sagaPlayer.message(FactionMessages.stats(selectedFaction));
		
	
	}
	
	@Command(
            aliases = {"flist"},
            usage = "[faction name or prefix]",
            flags = "",
            desc = "List faction memebers.",
            min = 0,
            max = 1
        )
        @CommandPermissions({"saga.user.faction.list"})
	public static void list(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

	
	SagaFaction selectedFaction = null;

	// Arguments:
	if(args.argsLength() == 1){
		
		String factionName = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
		selectedFaction = FactionManager.manager().getFaction(factionName);
		
		if(selectedFaction == null){
			sagaPlayer.message(FactionMessages.nonExistentFaction(factionName));
			return;
		}
		
	}else{
		 
		selectedFaction = sagaPlayer.getFaction();
		
		if(selectedFaction == null){
			sagaPlayer.message(FactionMessages.noFaction());
			return;
		}
		
	}
	
	// Inform:
	sagaPlayer.message(FactionMessages.list(selectedFaction));
	

}

	@Command(
            aliases = {"fsetrank"},
            usage = "[faction name] <player name> <rank>",
            flags = "",
            desc = "Set a faction members rank.",
            min = 2,
            max = 3
	)
	@CommandPermissions({"saga.user.faction.setrank"})
	public static void setRank(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		String targetName = null;
		String rankName = null;
		
		// Part of a faction:
		SagaFaction selectedFaction = sagaPlayer.getFaction();
		if(selectedFaction == null){
			sagaPlayer.message(FactionMessages.noFaction());
			return;
		}
		
		// Permission:
		if(!selectedFaction.canSetRank(sagaPlayer)){
			sagaPlayer.message(FactionMessages.noPermission(selectedFaction));
			return;
		}

		// Arguments:
		 if(args.argsLength() == 3){
			
			String factionName = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
			selectedFaction = FactionManager.manager().getFaction(factionName);
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(factionName));
				return;
			}
			
			targetName = selectedFaction.matchName(args.getString(1));
			rankName = args.getString(2).replace(SagaMessages.spaceSymbol, " ").toLowerCase();
			
		}else{
			 
			selectedFaction = sagaPlayer.getFaction();
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.noFaction());
				return;
			}
			
			targetName = selectedFaction.matchName(args.getString(0));
			rankName = args.getString(1).replace(SagaMessages.spaceSymbol, " ").toLowerCase();
			
		}
		
		// Force player:
		SagaPlayer targetPlayer;
		try {
			targetPlayer = Saga.plugin().forceSagaPlayer(targetName);
		} catch (NonExistantSagaPlayerException e) {
			sagaPlayer.message(SagaMessages.invalidPlayer(targetName));
			return;
		}

		// Not faction member:
		if( !selectedFaction.equals(targetPlayer.getFaction()) ){
			sagaPlayer.message(FactionMessages.notFactionMember(targetPlayer, selectedFaction));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}
		
		// Rank available:
		if(!selectedFaction.isRankAvailable(rankName)){
			sagaPlayer.message(FactionMessages.rankUnavailable(selectedFaction, rankName));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}
		
		// Set rank:
		try {
			selectedFaction.setRank(targetPlayer, rankName);
		} catch (InvalidProficiencyException e) {
			sagaPlayer.message(FactionMessages.invalidRank(selectedFaction, rankName));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}
		
		// Inform:
		selectedFaction.broadcast(FactionMessages.newRank(selectedFaction, rankName, targetPlayer));

		// Unforce:
		Saga.plugin().unforceSagaPlayer(targetName);
		
		
	}
	
	@Command(
            aliases = {"fdeclareowner"},
            usage = "[faction name] <player name>",
            flags = "",
            desc = "Declares someone as the new faction owner.",
            min = 1,
            max = 2
		)
	@CommandPermissions({"saga.user.settlement.faction.declareowner"})
	public static void declareOwner(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		SagaFaction selectedFaction = null;
		String targetName = null;

		// Arguments:
		if(args.argsLength() == 2){
			
			String factionName = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
			selectedFaction = FactionManager.manager().getFaction(factionName);
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(factionName));
				return;
			}
			
			targetName = selectedFaction.matchName(args.getString(1));
			
			
		}else{
			 
			selectedFaction = sagaPlayer.getFaction();
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.noFaction());
				return;
			}
			
			targetName = selectedFaction.matchName(args.getString(0));
			
		}
		
		// Permission:
		if(!selectedFaction.canDeclareOwner(sagaPlayer)){
			sagaPlayer.message(FactionMessages.noPermission(selectedFaction));
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
		selectedFaction.removeOwner();
		
		// Set new owner:
		selectedFaction.setOwner(targetPlayer.getName());
		
		// Inform:
		selectedFaction.broadcast(FactionMessages.newOwner(selectedFaction, targetName));
		
   		// Unforce:
   		Saga.plugin().unforceSagaPlayer(targetName);
		
   		
	}

	@Command(
			aliases = {"frename"},
			usage = "[faction name] <new faction name>",
			flags = "",
			desc = "Rename the faction.",
			min = 1,
			max = 2
	)
	@CommandPermissions({"saga.user.faction.rename"})
	public static void rename(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	    	
		
		SagaFaction selectedFaction = null;
		String name = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			String factionName = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
			selectedFaction = FactionManager.manager().getFaction(factionName);
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(factionName));
				return;
			}
			
	    	name = args.getString(1).replaceAll(SagaMessages.spaceSymbol, " ");

			
		}else{
			 
			selectedFaction = sagaPlayer.getFaction();
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.noFaction());
				return;
			}
			
	    	name = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");

		}

		// Permission:
		if(!selectedFaction.canRename(sagaPlayer)){
			sagaPlayer.message(FactionMessages.noPermission(selectedFaction));
			return;
		}
		
		// Fix spaces:
		while(name.contains("  ")){
			name = name.replaceAll("  ", " ");
		}
	    	
	    // Validate name:
	    if(!validateName(name)){
	    	sagaPlayer.message(FactionMessages.invalidName());
	    	return;
	    }
	    	
	    // Check name:
	    if(!name.equals(selectedFaction.getName()) && FactionManager.manager().getFaction(name) != null){
	    	sagaPlayer.message(FactionMessages.inUse(name));
	    	return;
	    }
	    
	    Double cost = EconomyConfiguration.config().factionRenameCost;
	    if(selectedFaction.isFormed() && cost > 0){

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
//	    String oldName = selectedFaction.getName();
//	    String oldPrefix = selectedFaction.getPrefix();
	    selectedFaction.setName(name);
	    
	    // Inform:
	    selectedFaction.broadcast(FactionMessages.renamed(selectedFaction));
	    	
	    	
	}

	
	// Alliance:
	@Command(
			aliases = {"frquestalliance","frequestally"},
			usage = "[faction name] <target_faction_name>",
			flags = "",
			desc = "Request an alliane with a faction",
			min = 1,
			max = 2
	)
	@CommandPermissions({"saga.user.faction.alliancerequest"})
	public static void allianceRequest(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	    	
		
		SagaFaction selectedFaction = null;
		SagaFaction targetFaction = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			String selectedName = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
			selectedFaction = FactionManager.manager().getFaction(selectedName);
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(selectedName));
				return;
			}
			
			String targetName = args.getString(1).replace(SagaMessages.spaceSymbol, " ");
			targetFaction = FactionManager.manager().getFaction(targetName);
			
			if(targetFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(targetName));
				return;
			}
			
		}else{
			 
			selectedFaction = sagaPlayer.getFaction();

			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.noFaction());
				return;
			}
			
			String targetName = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
			targetFaction = FactionManager.manager().getFaction(targetName);
			
			if(targetFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(targetName));
				return;
			}
			
		}

		// Formed:
		if(!selectedFaction.isFormed()){
			sagaPlayer.message(FactionMessages.notFormed(selectedFaction));
			sagaPlayer.message(FactionMessages.notFormedInfo(selectedFaction));
			return;
		}
		
		// Permission:
		if(!selectedFaction.canFormAlliance(sagaPlayer)){
			sagaPlayer.message(FactionMessages.noPermission(selectedFaction));
			return;
		}
		
		// Self invite:
		if(selectedFaction == targetFaction){
			sagaPlayer.message(FactionMessages.selfAlliance(selectedFaction));
			return;
		}
		
		// Already an ally:
		if(selectedFaction.isAlly(targetFaction.getId()) && targetFaction.isAlly(selectedFaction.getId())){
			sagaPlayer.message(FactionMessages.alreadyAlliance(selectedFaction, targetFaction));
			return;
		}
		
		// Send request:
		targetFaction.addAllianceRequest(selectedFaction.getId());
	    	
		// Inform:
		selectedFaction.broadcast(FactionMessages.sentAlliance(selectedFaction, targetFaction));
		targetFaction.broadcast(FactionMessages.recievedAlliance(targetFaction, selectedFaction));

		
	}

	@Command(
			aliases = {"facceptalliance","facceptally"},
			usage = "[faction name] [target_faction_name]",
			flags = "",
			desc = "Accept an alliane with a faction",
			min = 0,
			max = 2
	)
	@CommandPermissions({"saga.user.faction.allianceaccept"})
	public static void allianceAccept(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	    	
		
		SagaFaction selectedFaction = null;
		SagaFaction targetFaction = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			String selectedName = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
			selectedFaction = FactionManager.manager().getFaction(selectedName);
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(selectedName));
				return;
			}
			
			ArrayList<SagaFaction> targetFactions = FactionManager.manager().getFactions(selectedFaction.getAllyInvites());
		
			String targetName = args.getString(1).replace(SagaMessages.spaceSymbol, " ");
			for (SagaFaction faction : targetFactions) {
				
				if(faction.getName().equalsIgnoreCase(targetName)){
					targetFaction = faction;
					break;
				}
				
			}
			
			if(targetFaction == null){
				sagaPlayer.message(FactionMessages.noAllianceRequest(selectedFaction, targetName));
				return;
			}
			
		}
		
		if(args.argsLength() == 1){
			
			selectedFaction = sagaPlayer.getFaction();
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.noFaction());
				return;
			}
			
			ArrayList<SagaFaction> targetFactions = FactionManager.manager().getFactions(selectedFaction.getAllyInvites());
		
			String targetName = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
			for (SagaFaction faction : targetFactions) {
				
				if(faction.getName().equalsIgnoreCase(targetName)){
					targetFaction = faction;
					break;
				}
				
			}
			
			if(targetFaction == null){
				sagaPlayer.message(FactionMessages.noAllianceRequest(selectedFaction, targetName));
				return;
			}
			
		}
		
		else{

			selectedFaction = sagaPlayer.getFaction();
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.noFaction());
				return;
			}
			
			ArrayList<SagaFaction> targetFactions = FactionManager.manager().getFactions(selectedFaction.getAllyInvites());
			
			if(targetFactions.size() == 0){
				sagaPlayer.message(FactionMessages.noAllianceRequest(selectedFaction));
				return;
			}
			
			targetFaction = targetFactions.get(targetFactions.size() - 1);
			
		}

		// Formed:
		if(!selectedFaction.isFormed()){
			sagaPlayer.message(FactionMessages.notFormed(selectedFaction));
			sagaPlayer.message(FactionMessages.notFormedInfo(selectedFaction));
			return;
		}
		
		// Permission:
		if(!selectedFaction.canFormAlliance(sagaPlayer)){
			sagaPlayer.message(FactionMessages.noPermission(selectedFaction));
			return;
		}
		
		// Remove request:
		selectedFaction.removeAllianceRequest(targetFaction.getId());
		targetFaction.removeAllianceRequest(selectedFaction.getId());
		
		// Add allies:
		selectedFaction.addAlly(targetFaction.getId());
		targetFaction.addAlly(selectedFaction.getId());
		
		// Inform:
		Saga.broadcast(FactionMessages.formedAllianceBroadcast(selectedFaction, targetFaction));

		
	}

	@Command(
			aliases = {"fdeclinealliance","fdeclinetally"},
			usage = "[faction name] [target_faction_name]",
			flags = "",
			desc = "Decline an alliane with a faction",
			min = 1,
			max = 2
	)
	@CommandPermissions({"saga.user.faction.allianceaccept"})
	public static void allianceDecline(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	    	
		
		SagaFaction selectedFaction = null;
		SagaFaction targetFaction = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			String selectedName = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
			selectedFaction = FactionManager.manager().getFaction(selectedName);
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(selectedName));
				return;
			}
			
			ArrayList<SagaFaction> targetFactions = FactionManager.manager().getFactions(selectedFaction.getAllyInvites());
		
			String targetName = args.getString(1).replace(SagaMessages.spaceSymbol, " ");
			for (SagaFaction faction : targetFactions) {
				
				if(faction.getName().equalsIgnoreCase(targetName)){
					targetFaction = faction;
					break;
				}
				
			}
			
			if(targetFaction == null){
				sagaPlayer.message(FactionMessages.noAllianceRequest(selectedFaction, targetName));
				return;
			}
			
		}
		
		if(args.argsLength() == 1){
			
			selectedFaction = sagaPlayer.getFaction();
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.noFaction());
				return;
			}
			
			ArrayList<SagaFaction> targetFactions = FactionManager.manager().getFactions(selectedFaction.getAllyInvites());
		
			String targetName = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
			for (SagaFaction faction : targetFactions) {
				
				if(faction.getName().equalsIgnoreCase(targetName)){
					targetFaction = faction;
					break;
				}
				
			}
			
			if(targetFaction == null){
				sagaPlayer.message(FactionMessages.noAllianceRequest(selectedFaction, targetName));
				return;
			}
			
		}
		
		else{

			selectedFaction = sagaPlayer.getFaction();
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.noFaction());
				return;
			}
			
			ArrayList<SagaFaction> targetFactions = FactionManager.manager().getFactions(selectedFaction.getAllyInvites());
			
			if(targetFactions.size() == 0){
				sagaPlayer.message(FactionMessages.noAllianceRequest(selectedFaction));
				return;
			}
			
			targetFaction = targetFactions.get(targetFactions.size() - 1);
			
		}

		// Permission:
		if(!selectedFaction.canDeclineAlliance(sagaPlayer)){
			sagaPlayer.message(FactionMessages.noPermission(selectedFaction));
			return;
		}
		
		// Remove request:
		selectedFaction.removeAllianceRequest(targetFaction.getId());
		
		// Inform:
		selectedFaction.broadcast(FactionMessages.declinedAllianceRequest(selectedFaction, targetFaction));

		
	}

	@Command(
			aliases = {"fbreakalliance","fremoveally"},
			usage = "[faction name] <target_faction_name>",
			flags = "",
			desc = "Breaks an alliane with a faction",
			min = 1,
			max = 2
	)
	@CommandPermissions({"saga.user.faction.alliancebreak"})
	public static void breakAlliance(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	    	
		
		SagaFaction selectedFaction = null;
		SagaFaction targetFaction = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			String selectedName = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
			selectedFaction = FactionManager.manager().getFaction(selectedName);
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(selectedName));
				return;
			}
			
			String targetName = args.getString(1).replace(SagaMessages.spaceSymbol, " ");
			targetFaction = FactionManager.manager().getFaction(targetName);
			
			if(targetFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(targetName));
				return;
			}
			
		}else{
			 
			selectedFaction = sagaPlayer.getFaction();

			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.noFaction());
				return;
			}
			
			String targetName = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
			targetFaction = FactionManager.manager().getFaction(targetName);
			
			if(targetFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(targetName));
				return;
			}
			
		}

		// Permission:
		if(!selectedFaction.canBreakAlliance(sagaPlayer)){
			sagaPlayer.message(FactionMessages.noPermission(selectedFaction));
			return;
		}
		
		// No alliance:
		if(!selectedFaction.isAlly(targetFaction)){
			sagaPlayer.message(FactionMessages.noAlliance(selectedFaction, targetFaction));
			return;
		}
		
		// Remove alliance:
		targetFaction.removeAlly(selectedFaction.getId());
		selectedFaction.removeAlly(targetFaction.getId());

		// Inform:
		Saga.broadcast(FactionMessages.brokeAllianceBroadcast(selectedFaction, targetFaction));

		
	}
	
	
	// Spawn:
	@Command(
            aliases = {"fspawn"},
            usage = "[faction]",
            flags = "",
            desc = "Teleports to a faction spawn point.",
            min = 0,
            max = 1
		)
	@CommandPermissions({"saga.user.faction.spawn"})
	public static void spawn(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		// Part of a faction:
		SagaFaction selectedFaction = sagaPlayer.getFaction();
		if(selectedFaction == null){
			sagaPlayer.message(FactionMessages.noFaction());
			return;
		}

		// Arguments:
		 if(args.argsLength() == 1){
			
			selectedFaction = FactionManager.manager().getFaction(args.getString(0));
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(args.getString(0)));
				return;
			}
			
			
		}else{
			 
			selectedFaction = sagaPlayer.getFaction();
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.noFaction());
				return;
			}
			
		}
		
		// Permission:
		if(!selectedFaction.canSpawn(sagaPlayer)){
			sagaPlayer.message(FactionMessages.noPermission(selectedFaction));
			return;
		}

		// Spawn point:
		SagaLocation spawnPoint = selectedFaction.getSpawn();
		if(spawnPoint == null){
			sagaPlayer.message(FactionMessages.noSpawn(selectedFaction));
			return;
		}
		
		// Teleport:
		sagaPlayer.teleport(spawnPoint.getLocation());
		
		
	}
	
	@Command(
            aliases = {"fsetspawn"},
            usage = "[faction]",
            flags = "",
            desc = "Sets a faction spawn point.",
            min = 0,
            max = 1
		)
	@CommandPermissions({"saga.user.faction.setspawn"})
	public static void setspawn(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		// Part of a faction:
		SagaFaction selectedFaction = sagaPlayer.getFaction();
		if(selectedFaction == null){
			sagaPlayer.message(FactionMessages.noFaction());
			return;
		}

		// Arguments:
		 if(args.argsLength() == 1){
			
			selectedFaction = FactionManager.manager().getFaction(args.getString(0));
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(args.getString(0)));
				return;
			}
			
			
		}else{
			 
			selectedFaction = sagaPlayer.getFaction();
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.noFaction());
				return;
			}
			
		}
		
		// Permission:
		if(!selectedFaction.canSetSpawn(sagaPlayer)){
			sagaPlayer.message(FactionMessages.noPermission(selectedFaction));
			return;
		}

		// Set spawn point:
		selectedFaction.setSpawn(sagaPlayer.getLocation());
		
		// Inform:
		selectedFaction.broadcast(FactionMessages.newSpawn(selectedFaction));
		
		
		
	}

	
	// Messages:
	@Command(
            aliases = {"f"},
            usage = "<message>",
            flags = "",
            desc = "Sends a message to the player chat.",
            min = 1
	)
	@CommandPermissions({"saga.user.faction.list"})
	public static void chat(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

	
	SagaFaction selectedFaction = null;
	
	// Faction:
	selectedFaction = sagaPlayer.getFaction();
	
	if(selectedFaction == null){
		sagaPlayer.message(FactionMessages.noFaction());
		return;
	}
	
	// Formed:
	if(!selectedFaction.isFormed()){
		sagaPlayer.message(FactionMessages.notFormed(selectedFaction));
		sagaPlayer.message(FactionMessages.notFormedInfo(selectedFaction));
		return;
	}
	
	// Create message:
	String message = selectedFaction.getSecondaryColor() + "[" + selectedFaction.getPrimaryColor() + FactionMessages.rankedPlayer(selectedFaction, sagaPlayer) + selectedFaction.getSecondaryColor() + "] " + args.getJoinedStrings(0);
	
	// Inform:
	selectedFaction.broadcast(message);
	
	// Inform allies:
	Collection<SagaFaction> allyFactions = selectedFaction.getAllyFactions();
	for (SagaFaction allyFaction : allyFactions) {
		allyFaction.broadcast(message);
	}
	

}
	
	
	// Appearance:
	@Command(
            aliases = {"fsetprimarycolor","fsetprimary"},
            usage = "<color>",
            flags = "",
            desc = "Sets the factions primary color.",
            min = 1,
            max = 4
		)
	@CommandPermissions({"saga.user.faction.setprimarycolor"})
	public static void setPrimaryColor(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		// Part of a faction:
		SagaFaction selectedFaction = sagaPlayer.getFaction();
		if(selectedFaction == null){
			sagaPlayer.message(FactionMessages.noFaction());
			return;
		}
		
		// Permission:
		if(!selectedFaction.canSetColor(sagaPlayer)){
			sagaPlayer.message(FactionMessages.noPermission(selectedFaction));
			return;
		}
		
		// Retrieve color:
		ChatColor color = null;
		String colorName = args.getJoinedStrings(0);
		String cColorName = colorName.replace(" ", "_").toUpperCase();
		ChatColor[] colors = getAvailableColours();
		
		for (int i = 0; i < colors.length; i++) {
			if(colors[i].name().equals(cColorName)){
				color = colors[i];
				break;
			}
		}
		
		if(color == null || color == ChatColor.MAGIC){
			sagaPlayer.message(FactionMessages.invalidColor(colorName));
			sagaPlayer.message(FactionMessages.possibleColors(colors, selectedFaction));
			return;
		}
		
		// Set color:
		selectedFaction.setPrimaryColor(color);
		
		// Inform:
		selectedFaction.broadcast(FactionMessages.primaryColorSet(selectedFaction));
		
		
	}
	
	@Command(
            aliases = {"fsetsecondarycolor","fsetsecondary"},
            usage = "<color>",
            flags = "",
            desc = "Sets the faction secondary color.",
            min = 1,
            max = 4
		)
	@CommandPermissions({"saga.user.faction.setsecondarycolor"})
	public static void setSecondaryColor(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		// Part of a faction:
		SagaFaction selectedFaction = sagaPlayer.getFaction();
		if(selectedFaction == null){
			sagaPlayer.message(FactionMessages.noFaction());
			return;
		}
		
		// Permission:
		if(!selectedFaction.canSetColor(sagaPlayer)){
			sagaPlayer.message(FactionMessages.noPermission(selectedFaction));
			return;
		}
		
		// Retrieve color:
		ChatColor color = null;
		String colorName = args.getJoinedStrings(0);
		String cColorName = colorName.replace(" ", "_").toUpperCase();
		ChatColor[] colors = getAvailableColours();
		
		for (int i = 0; i < colors.length; i++) {
			if(colors[i].name().equals(cColorName)){
				color = colors[i];
				break;
			}
		}
		
		if(color == null){
			sagaPlayer.message(FactionMessages.invalidColor(colorName));
			sagaPlayer.message(FactionMessages.possibleColors(colors, selectedFaction));
			return;
		}
		
		// Set color:
		selectedFaction.setSecondaryColor(color);
		
		// Inform:
		selectedFaction.broadcast(FactionMessages.secondaryColorSet(selectedFaction));
		
		
	}
	
	private static ChatColor[] getAvailableColours(){
		
		ChatColor[] allColours = ChatColor.values();
		ArrayList<ChatColor> availableColours = new ArrayList<ChatColor>();
		
		for (int i = 0; i < allColours.length; i++) {
			
			if(allColours[i].isColor()) availableColours.add(allColours[i]);
			
		}
		
		return availableColours.toArray(new ChatColor[availableColours.size()]);
		
		
	}
	
	// Info:
	@Command(
            aliases = {"fquit"},
            usage = "",
            flags = "",
            desc = "Quit faction.",
            min = 0,
            max = 0
		)
	@CommandPermissions({"saga.user.faction.quit"})
	public static void wrongQuit(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		// Inform:
		sagaPlayer.message(FactionMessages.wrongQuit());
		
		
	}
	
	

	// Administration:
	@Command(
            aliases = {"fdisband"},
            usage = "[faction name or prefix]",
            flags = "",
            desc = "Disbands the faction.",
            min = 0,
            max = 1
        )
	@CommandPermissions({"saga.admin.faction.delete"}
	)
	public static void disband(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		SagaFaction selectedFaction = null;
		
		// Arguments:
		if(args.argsLength() == 1){
			
			selectedFaction = FactionManager.manager().getFaction(args.getString(0));
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(args.getString(0)));
				return;
			}
			
		}else{
			 
			selectedFaction = sagaPlayer.getFaction();
			
			if(selectedFaction == null){
				sagaPlayer.message(FactionMessages.noFaction());
				return;
			}
			
		}
		
		// Permission:
		if(!selectedFaction.canDelete(sagaPlayer)){
			sagaPlayer.message(FactionMessages.noPermission(selectedFaction));
			return;
		}
		
		// Delete:
		selectedFaction.delete();
		
		// Inform:
		sagaPlayer.message(FactionMessages.deleted(selectedFaction));
		
		
	}
	
	
	// Other:
	@Command(
			aliases = {"fhelp"},
			usage = "[page number]",
			flags = "",
			desc = "Display faction help.",
			min = 0,
			max = 1
	)
	@CommandPermissions({"saga.user.faction.help"})
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
		sagaPlayer.message(InfoMessages.fhelp(page));
	

	}

	public static boolean validateName(String str) {

         if(org.saga.utility.text.TextUtil.getComparisonString(str).length() < minimumNameLenght ) {
        	 return false;
         }

         if(str.length() > maximumNameLength) {
        	 return false;
         }

         for (char c : str.toCharArray()) {
                 if ( ! org.saga.utility.text.TextUtil.substanceChars.contains(String.valueOf(c))) {
                	 return false;
                 }
         }

         return true;

 }
	
	



}
