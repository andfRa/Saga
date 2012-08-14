package org.saga.commands;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.ChatColor;
import org.saga.Saga;
import org.saga.config.EconomyConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.ProficiencyConfiguration.InvalidProficiencyException;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.factions.Faction;
import org.saga.factions.FactionManager;
import org.saga.messages.EconomyMessages;
import org.saga.messages.FactionMessages;
import org.saga.messages.GeneralMessages;
import org.saga.messages.InfoMessages;
import org.saga.messages.PlayerMessages;
import org.saga.messages.SettlementMessages;
import org.saga.player.Proficiency;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.player.SagaPlayer;
import org.saga.utility.SagaLocation;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;


public class FactionCommands {


	public static Integer maximumNameLength = 6;
	
	public static Integer minimumNameLenght = 3;
	
	public static Integer noDeleteMemberCount = 15;
	
	public static Integer noDeleteClaimCount = 5;
	
	
	
	// Members:
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
		String name = GeneralMessages.nameFromArg(args.getString(0));

		// Fix spaces:
		while(name.contains("  ")){
			name = name.replaceAll("  ", " ");
		}
		
	    	// Already in faction:
	    	if(sagaPlayer.getFaction() != null){
	    		sagaPlayer.message(FactionMessages.alreadyInFaction());
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
	    	Faction faction = Faction.create(name, sagaPlayer);
	    	
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


		Faction selFaction = null;
		String playerName = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			String factionName = GeneralMessages.nameFromArg(args.getString(0));
			selFaction = FactionManager.manager().getFaction(factionName);
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(factionName));
				return;
			}
			
			playerName = selFaction.matchName(args.getString(1));
			
			
		}else{
			 
			selFaction = sagaPlayer.getFaction();
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.notMember());
				return;
			}
			
			playerName = selFaction.matchName(args.getString(0));
			
		}
		
		// Permission:
		if(!selFaction.canInvite(sagaPlayer)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}
		
		// Force player:
		SagaPlayer invitedPlayer;
		try {
			invitedPlayer = Saga.plugin().forceSagaPlayer(playerName);
		} catch (NonExistantSagaPlayerException e) {
			sagaPlayer.message(PlayerMessages.invalidPlayer(playerName));
			return;
		}
		
		// Already in a faction:
		if(invitedPlayer.getFaction() != null){
			sagaPlayer.message(FactionMessages.alreadyInFaction(invitedPlayer));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(playerName);
			return;
		}
		
		// Invite:
		invitedPlayer.addFactionInvite(selFaction.getId());
		
		// Inform:
		selFaction.broadcast(FactionMessages.invited(invitedPlayer, selFaction));
		invitedPlayer.message(FactionMessages.beenInvited(invitedPlayer, selFaction));
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

		
		Faction selFaction = null;
		
		// Part of a faction:
		if(sagaPlayer.getFaction() != null){
			sagaPlayer.message(FactionMessages.alreadyInFaction());
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
				Faction faction = FactionManager.manager().getFaction(invitationIds.get(i));
				if( faction != null && faction.getName().equals(args.getString(0)) ){
					selFaction = faction;
					break;
				}
			}
			
			if(selFaction == null && args.argsLength() == 1){
	    		sagaPlayer.message(FactionMessages.nonExistentFaction(args.getString(0)));
	    		return;
	    	}
			
		}else{
			
			selFaction = FactionManager.manager().getFaction(invitationIds.get( invitationIds.size() -1 ));
			
			if(selFaction == null){
	    		sagaPlayer.message( FactionMessages.nonExistantFaction() );
	    		return;
	    	}
			
		}

    	// Already in a faction:
    	if(sagaPlayer.getFaction() != null){
    		sagaPlayer.message(FactionMessages.alreadyInFaction());
    		return;
    	}
		
    	// Inform:
    	selFaction.broadcast(FactionMessages.joined(sagaPlayer, selFaction));
		sagaPlayer.message(FactionMessages.haveJoined(sagaPlayer, selFaction));

		boolean formed = selFaction.isFormed();
		
    	// Add to faction:
		selFaction.addMember(sagaPlayer);

		// Inform formation:
		if(!formed && selFaction.isFormed()){
			Saga.broadcast(FactionMessages.formedBcast(selFaction));
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
		
		
		Faction selFaction = sagaPlayer.getFaction();
		String playerName = null;

		// Arguments:
		if(args.argsLength() == 2){
			
			String factionName = GeneralMessages.nameFromArg(args.getString(0));
			selFaction = FactionManager.manager().getFaction(factionName);
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(factionName));
				return;
			}
			
			playerName = selFaction.matchName(args.getString(1));
			
			
		}else{
			 
			selFaction = sagaPlayer.getFaction();
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.notMember());
				return;
			}
			
			playerName = selFaction.matchName(args.getString(0));
			
		}
		
		// Permission:
		if(!selFaction.canKick(sagaPlayer)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}
		
		// Force player:
		SagaPlayer selPlayer;
		try {
			selPlayer = Saga.plugin().forceSagaPlayer(playerName);
		} catch (NonExistantSagaPlayerException e) {
			sagaPlayer.message(PlayerMessages.invalidPlayer(playerName));
			return;
		}
		
		// Kick owner:
		if(selFaction.isOwner(selPlayer.getName())){
			sagaPlayer.message(FactionMessages.cantKickOwner(selFaction));
			return;
		}
		
		// Not faction member:
		if( !selFaction.equals(selPlayer.getFaction()) ){
			sagaPlayer.message(FactionMessages.notFactionMember(selPlayer, selFaction));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(selPlayer.getName());
			return;
		}
		
		// Kicked yourself:
		if(selPlayer.equals(sagaPlayer)){
			sagaPlayer.message(FactionMessages.cantKickYourself(sagaPlayer, selFaction));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(selPlayer.getName());
			return;
		}
		
		boolean formed = selFaction.isFormed();
		
		// Kick:
		selFaction.removeMember(selPlayer.getName());
		
		// Inform disband:
		if(formed && !selFaction.isFormed()){
			Saga.broadcast(FactionMessages.disbanded(selFaction));
		}
		
		// Inform:
		selFaction.broadcast(FactionMessages.playerKicked(selPlayer, selFaction));
		selPlayer.message(FactionMessages.beenKicked(selPlayer, selFaction));

		// Unforce:
		Saga.plugin().unforceSagaPlayer(selPlayer.getName());
		
		// Disband if no players left:
		if(selFaction.getMemberCount() <= 0){

			// Delete faction:
			selFaction.delete();
			
//			// Inform:
//	    	Saga.broadcast(FactionMessages.deleted(sagaPlayer, selFaction));
	    	
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
		Faction selFaction = sagaPlayer.getFaction();
		if(selFaction == null){
			
			sagaPlayer.removeFactionId();
			
			sagaPlayer.message(FactionMessages.notMember());
			return;
			
		}
		
		// Permission:
		if(!selFaction.canQuit(sagaPlayer)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}

		boolean formed = selFaction.isFormed();
		
		// Quit:
		selFaction.removeMember(sagaPlayer);

		// Inform disband:
		if(formed && !selFaction.isFormed()){
			Saga.broadcast(FactionMessages.disbanded(selFaction));
		}
		
		// Inform:
		selFaction.broadcast(FactionMessages.quit(sagaPlayer, selFaction));
		sagaPlayer.message(FactionMessages.haveQuit(sagaPlayer, selFaction));

		// Disband if no players left:
		if(selFaction.getMemberCount() <= 0){

			// Delete faction:
			selFaction.delete();
			
//			// Broadcast:
//	    	Saga.broadcast(FactionMessages.deleted(sagaPlayer, selFaction));
	    	
		}
		
		
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
		Faction selFaction = sagaPlayer.getFaction();
		if(selFaction == null){
			sagaPlayer.message(FactionMessages.notMember());
			return;
		}
		
		// Permission:
		if(!selFaction.canSetRank(sagaPlayer)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}

		// Arguments:
		 if(args.argsLength() == 3){
			
			String factionName = GeneralMessages.nameFromArg(args.getString(0));
			selFaction = FactionManager.manager().getFaction(factionName);
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(factionName));
				return;
			}
			
			targetName = selFaction.matchName(args.getString(1));
			rankName = GeneralMessages.nameFromArg(args.getString(2));
			
		}else{
			 
			selFaction = sagaPlayer.getFaction();
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.notMember());
				return;
			}
			
			targetName = selFaction.matchName(args.getString(0));
			rankName = GeneralMessages.nameFromArg(args.getString(1)).toLowerCase();
			
		}
		
		// Force player:
		SagaPlayer targetPlayer;
		try {
			targetPlayer = Saga.plugin().forceSagaPlayer(targetName);
		} catch (NonExistantSagaPlayerException e) {
			sagaPlayer.message(PlayerMessages.invalidPlayer(targetName));
			return;
		}

		// Not faction member:
		if( !selFaction.equals(targetPlayer.getFaction()) ){
			sagaPlayer.message(FactionMessages.notFactionMember(targetPlayer, selFaction));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}

		// Create rank:
		Proficiency rank;
		try {
			rank = ProficiencyConfiguration.config().createProficiency(rankName);
		} catch (InvalidProficiencyException e) {
			sagaPlayer.message(FactionMessages.invalidRank(rankName));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}

		// Not a rank:
		if(rank.getDefinition().getType() != ProficiencyType.RANK){
			sagaPlayer.message(FactionMessages.invalidRank(rankName));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}
		
		// Rank available:
		if(!selFaction.isRankAvailable(rank.getHierarchy())){
			sagaPlayer.message(FactionMessages.rankUnavailable(selFaction, rankName));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(targetName);
			return;
		}
		
		// Set rank:
		selFaction.setRank(targetPlayer, rank);
		
		// Inform:
		selFaction.broadcast(FactionMessages.newRank(selFaction, rankName, targetPlayer));

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
		

		Faction selFaction = null;
		String targetName = null;

		// Arguments:
		if(args.argsLength() == 2){
			
			String factionName = GeneralMessages.nameFromArg(args.getString(0));
			selFaction = FactionManager.manager().getFaction(factionName);
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(factionName));
				return;
			}
			
			targetName = selFaction.matchName(args.getString(1));
			
			
		}else{
			 
			selFaction = sagaPlayer.getFaction();
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.notMember());
				return;
			}
			
			targetName = selFaction.matchName(args.getString(0));
			
		}
		
		// Permission:
		if(!selFaction.canDeclareOwner(sagaPlayer)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}

		// Force player:
		SagaPlayer targetPlayer;
		try {
			targetPlayer = Saga.plugin().forceSagaPlayer(targetName);
		} catch (NonExistantSagaPlayerException e) {
			sagaPlayer.message(PlayerMessages.invalidPlayer(targetName));
			return;
		}
		
		// Remove old owner:
		selFaction.removeOwner();
		
		// Set new owner:
		selFaction.setOwner(targetPlayer.getName());
		
		// Inform:
		selFaction.broadcast(FactionMessages.newOwner(selFaction, targetName));
		
   		// Unforce:
   		Saga.plugin().unforceSagaPlayer(targetName);
		
   		
	}

	
	
	// Stats:
	@Command(
            aliases = {"fstats"},
            usage = "[faction name] [page]",
            flags = "",
            desc = "List faction stats.",
            min = 0,
            max = 2
        )
        @CommandPermissions({"saga.user.faction.stats"})
	public static void stats(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		Integer page = null;
		Faction selFaction = null;
		
		String argsPage = null;
		String factionName = null;
		
		// Arguments:
		switch (args.argsLength()) {
			
			case 2:
				
				// Faction:
				factionName = GeneralMessages.nameFromArg(args.getString(0));
				selFaction = FactionManager.manager().getFaction(factionName);
				if(selFaction == null){
					sagaPlayer.message(FactionMessages.noFaction(factionName));
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

				// Chunk group:
				selFaction = sagaPlayer.getFaction();
				if(selFaction == null){
					sagaPlayer.message(FactionMessages.notMember());
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

				// Chunk group:
				selFaction = sagaPlayer.getFaction();
				if(selFaction == null){
					sagaPlayer.message(FactionMessages.notMember());
					return;
				}
				
				// Page:
				page = 1;
				
				break;
				
		}
		
		// Inform:
		sagaPlayer.message(FactionMessages.stats(selFaction, page -1));
		
		
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

	
	Faction selFaction = null;

	// Arguments:
	if(args.argsLength() == 1){
		
		String factionName = GeneralMessages.nameFromArg(args.getString(0));
		selFaction = FactionManager.manager().getFaction(factionName);
		
		if(selFaction == null){
			sagaPlayer.message(FactionMessages.nonExistentFaction(factionName));
			return;
		}
		
	}else{
		 
		selFaction = sagaPlayer.getFaction();
		
		if(selFaction == null){
			sagaPlayer.message(FactionMessages.notMember());
			return;
		}
		
	}
	
	// Inform:
	sagaPlayer.message(FactionMessages.list(selFaction));
	

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
	    	
		
		Faction selFaction = null;
		Faction targetFaction = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			String selName = GeneralMessages.nameFromArg(args.getString(0));
			selFaction = FactionManager.manager().getFaction(selName);
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(selName));
				return;
			}
			
			String targetName = GeneralMessages.nameFromArg(args.getString(1));
			targetFaction = FactionManager.manager().getFaction(targetName);
			
			if(targetFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(targetName));
				return;
			}
			
		}else{
			 
			selFaction = sagaPlayer.getFaction();

			if(selFaction == null){
				sagaPlayer.message(FactionMessages.notMember());
				return;
			}
			
			String targetName = GeneralMessages.nameFromArg(args.getString(0));
			targetFaction = FactionManager.manager().getFaction(targetName);
			
			if(targetFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(targetName));
				return;
			}
			
		}

		// Formed:
		if(!selFaction.isFormed()){
			sagaPlayer.message(FactionMessages.notFormed(selFaction));
			sagaPlayer.message(FactionMessages.notFormedInfo(selFaction));
			return;
		}
		
		// Permission:
		if(!selFaction.canFormAlliance(sagaPlayer)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}
		
		// Self invite:
		if(selFaction == targetFaction){
			sagaPlayer.message(FactionMessages.selfAlliance(selFaction));
			return;
		}
		
		// Already an ally:
		if(selFaction.isAlly(targetFaction.getId()) && targetFaction.isAlly(selFaction.getId())){
			sagaPlayer.message(FactionMessages.alreadyAlliance(selFaction, targetFaction));
			return;
		}
		
		// Send request:
		targetFaction.addAllianceRequest(selFaction.getId());
	    	
		// Inform:
		selFaction.broadcast(FactionMessages.sentAlliance(selFaction, targetFaction));
		targetFaction.broadcast(FactionMessages.recievedAlliance(targetFaction, selFaction));

		
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
	    	
		
		Faction selFaction = null;
		Faction targetFaction = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			String selName = GeneralMessages.nameFromArg(args.getString(0));
			selFaction = FactionManager.manager().getFaction(selName);
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(selName));
				return;
			}
			
			ArrayList<Faction> targetFactions = FactionManager.manager().getFactions(selFaction.getAllyInvites());
		
			String targetName = GeneralMessages.nameFromArg(args.getString(1));
			for (Faction faction : targetFactions) {
				
				if(faction.getName().equalsIgnoreCase(targetName)){
					targetFaction = faction;
					break;
				}
				
			}
			
			if(targetFaction == null){
				sagaPlayer.message(FactionMessages.noAllianceRequest(selFaction, targetName));
				return;
			}
			
		}
		
		if(args.argsLength() == 1){
			
			selFaction = sagaPlayer.getFaction();
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.notMember());
				return;
			}
			
			ArrayList<Faction> targetFactions = FactionManager.manager().getFactions(selFaction.getAllyInvites());
		
			String targetName = GeneralMessages.nameFromArg(args.getString(0));
			for (Faction faction : targetFactions) {
				
				if(faction.getName().equalsIgnoreCase(targetName)){
					targetFaction = faction;
					break;
				}
				
			}
			
			if(targetFaction == null){
				sagaPlayer.message(FactionMessages.noAllianceRequest(selFaction, targetName));
				return;
			}
			
		}
		
		else{

			selFaction = sagaPlayer.getFaction();
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.notMember());
				return;
			}
			
			ArrayList<Faction> targetFactions = FactionManager.manager().getFactions(selFaction.getAllyInvites());
			
			if(targetFactions.size() == 0){
				sagaPlayer.message(FactionMessages.noAllianceRequest(selFaction));
				return;
			}
			
			targetFaction = targetFactions.get(targetFactions.size() - 1);
			
		}

		// Formed:
		if(!selFaction.isFormed()){
			sagaPlayer.message(FactionMessages.notFormed(selFaction));
			sagaPlayer.message(FactionMessages.notFormedInfo(selFaction));
			return;
		}
		
		// Permission:
		if(!selFaction.canFormAlliance(sagaPlayer)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}
		
		// Remove request:
		selFaction.removeAllianceRequest(targetFaction.getId());
		targetFaction.removeAllianceRequest(selFaction.getId());
		
		// Add allies:
		selFaction.addAlly(targetFaction.getId());
		targetFaction.addAlly(selFaction.getId());
		
		// Inform:
		Saga.broadcast(FactionMessages.formedAllianceBroadcast(selFaction, targetFaction));

		
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
	    	
		
		Faction selFaction = null;
		Faction targetFaction = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			String selName = GeneralMessages.nameFromArg(args.getString(0));
			selFaction = FactionManager.manager().getFaction(selName);
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(selName));
				return;
			}
			
			ArrayList<Faction> targetFactions = FactionManager.manager().getFactions(selFaction.getAllyInvites());
		
			String targetName = GeneralMessages.nameFromArg(args.getString(1));
			for (Faction faction : targetFactions) {
				
				if(faction.getName().equalsIgnoreCase(targetName)){
					targetFaction = faction;
					break;
				}
				
			}
			
			if(targetFaction == null){
				sagaPlayer.message(FactionMessages.noAllianceRequest(selFaction, targetName));
				return;
			}
			
		}
		
		if(args.argsLength() == 1){
			
			selFaction = sagaPlayer.getFaction();
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.notMember());
				return;
			}
			
			ArrayList<Faction> targetFactions = FactionManager.manager().getFactions(selFaction.getAllyInvites());
		
			String targetName = GeneralMessages.nameFromArg(args.getString(0));
			for (Faction faction : targetFactions) {
				
				if(faction.getName().equalsIgnoreCase(targetName)){
					targetFaction = faction;
					break;
				}
				
			}
			
			if(targetFaction == null){
				sagaPlayer.message(FactionMessages.noAllianceRequest(selFaction, targetName));
				return;
			}
			
		}
		
		else{

			selFaction = sagaPlayer.getFaction();
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.notMember());
				return;
			}
			
			ArrayList<Faction> targetFactions = FactionManager.manager().getFactions(selFaction.getAllyInvites());
			
			if(targetFactions.size() == 0){
				sagaPlayer.message(FactionMessages.noAllianceRequest(selFaction));
				return;
			}
			
			targetFaction = targetFactions.get(targetFactions.size() - 1);
			
		}

		// Permission:
		if(!selFaction.canDeclineAlliance(sagaPlayer)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}
		
		// Remove request:
		selFaction.removeAllianceRequest(targetFaction.getId());
		
		// Inform:
		selFaction.broadcast(FactionMessages.declinedAllianceRequest(selFaction, targetFaction));

		
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
	    	
		
		Faction selFaction = null;
		Faction targetFaction = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			String selName = GeneralMessages.nameFromArg(args.getString(0));
			selFaction = FactionManager.manager().getFaction(selName);
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(selName));
				return;
			}
			
			String targetName = GeneralMessages.nameFromArg(args.getString(1));
			targetFaction = FactionManager.manager().getFaction(targetName);
			
			if(targetFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(targetName));
				return;
			}
			
		}else{
			 
			selFaction = sagaPlayer.getFaction();

			if(selFaction == null){
				sagaPlayer.message(FactionMessages.notMember());
				return;
			}
			
			String targetName = GeneralMessages.nameFromArg(args.getString(0));
			targetFaction = FactionManager.manager().getFaction(targetName);
			
			if(targetFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(targetName));
				return;
			}
			
		}

		// Permission:
		if(!selFaction.canBreakAlliance(sagaPlayer)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}
		
		// No alliance:
		if(!selFaction.isAlly(targetFaction)){
			sagaPlayer.message(FactionMessages.noAlliance(selFaction, targetFaction));
			return;
		}
		
		// Remove alliance:
		targetFaction.removeAlly(selFaction.getId());
		selFaction.removeAlly(targetFaction.getId());

		// Inform:
		Saga.broadcast(FactionMessages.brokeAllianceBroadcast(selFaction, targetFaction));

		
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
		Faction selFaction = sagaPlayer.getFaction();
		if(selFaction == null){
			sagaPlayer.message(FactionMessages.notMember());
			return;
		}

		// Arguments:
		 if(args.argsLength() == 1){
			
			selFaction = FactionManager.manager().getFaction(args.getString(0));
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(args.getString(0)));
				return;
			}
			
			
		}else{
			 
			selFaction = sagaPlayer.getFaction();
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.notMember());
				return;
			}
			
		}
		
		// Permission:
		if(!selFaction.canSpawn(sagaPlayer)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}

		// Spawn point:
		SagaLocation spawnPoint = selFaction.getSpawn();
		if(spawnPoint == null){
			sagaPlayer.message(FactionMessages.noSpawn(selFaction));
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
		Faction selFaction = sagaPlayer.getFaction();
		if(selFaction == null){
			sagaPlayer.message(FactionMessages.notMember());
			return;
		}

		// Arguments:
		 if(args.argsLength() == 1){
			
			selFaction = FactionManager.manager().getFaction(args.getString(0));
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(args.getString(0)));
				return;
			}
			
			
		}else{
			 
			selFaction = sagaPlayer.getFaction();
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.notMember());
				return;
			}
			
		}
		
		// Permission:
		if(!selFaction.canSetSpawn(sagaPlayer)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}

		// Set spawn point:
		selFaction.setSpawn(sagaPlayer.getLocation());
		
		// Inform:
		selFaction.broadcast(FactionMessages.newSpawn(selFaction));
		
		
		
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

	
	Faction selFaction = null;
	
	// Faction:
	selFaction = sagaPlayer.getFaction();
	
	if(selFaction == null){
		sagaPlayer.message(FactionMessages.notMember());
		return;
	}
	
	// Formed:
	if(!selFaction.isFormed()){
		sagaPlayer.message(FactionMessages.notFormed(selFaction));
		sagaPlayer.message(FactionMessages.notFormedInfo(selFaction));
		return;
	}
	
	// Create message:
	String message = selFaction.getColour2() + "[" + selFaction.getColour1() + FactionMessages.rankedPlayer(selFaction, sagaPlayer) + selFaction.getColour2() + "] " + args.getJoinedStrings(0);
	
	// Inform:
	selFaction.broadcast(message);
	
	// Inform allies:
	Collection<Faction> allyFactions = selFaction.getAllyFactions();
	for (Faction allyFaction : allyFactions) {
		allyFaction.broadcast(message);
	}
	

}
	
	
	
	// Appearance:
	@Command(
		aliases = {"fsetcolour1","fsetcolor1","fsetprimary"},
		usage = "<colour>",
		flags = "",
		desc = "Sets the factions primary colour.",
		min = 1,
		max = 4
	)
	@CommandPermissions({"saga.user.faction.setcolour1"})
	public static void setColour1(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		// Part of a faction:
		Faction selFaction = sagaPlayer.getFaction();
		if(selFaction == null){
			sagaPlayer.message(FactionMessages.notMember());
			return;
		}
		
		// Permission:
		if(!selFaction.canSetColor(sagaPlayer)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}
		
		// Retrieve colour:
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
			sagaPlayer.message(FactionMessages.possibleColors(colors, selFaction));
			return;
		}
		
		// Set color:
		selFaction.setColor1(color);
		
		// Inform:
		selFaction.broadcast(FactionMessages.colour1Set(selFaction));
		
		
	}
	
	@Command(
			aliases = {"fsetcolour2","fsetcolor2","fsetsecondary"},
			usage = "<colour>",
			flags = "",
			desc = "Sets the factions primary colour.",
			min = 1,
			max = 4
	)
	@CommandPermissions({"saga.user.faction.setcolour2"})
	public static void setSecondaryColor(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		// Part of a faction:
		Faction selFaction = sagaPlayer.getFaction();
		if(selFaction == null){
			sagaPlayer.message(FactionMessages.notMember());
			return;
		}
		
		// Permission:
		if(!selFaction.canSetColor(sagaPlayer)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
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
			sagaPlayer.message(FactionMessages.possibleColors(colors, selFaction));
			return;
		}
		
		// Set color:
		selFaction.setColor2(color);
		
		// Inform:
		selFaction.broadcast(FactionMessages.colour2Set(selFaction));
		
		
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
		

		Faction selFaction = null;
		
		// Arguments:
		if(args.argsLength() == 1){
			
			selFaction = FactionManager.manager().getFaction(args.getString(0));
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(args.getString(0)));
				return;
			}
			
		}else{
			 
			selFaction = sagaPlayer.getFaction();
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.notMember());
				return;
			}
			
		}
		
		// Permission:
		if(!selFaction.canDelete(sagaPlayer)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}
		
		// Delete:
		selFaction.delete();
		
		// Inform:
		sagaPlayer.message(FactionMessages.deleted(selFaction));
		
		
	}
	
	
	
	// Other:
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
	    	
		
		Faction selFaction = null;
		String newName = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			String factionName = GeneralMessages.nameFromArg(args.getString(0));
			selFaction = FactionManager.manager().getFaction(factionName);
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.nonExistentFaction(factionName));
				return;
			}
			
	    	newName = GeneralMessages.nameFromArg(args.getString(1));

			
		}else{
			 
			selFaction = sagaPlayer.getFaction();
			
			if(selFaction == null){
				sagaPlayer.message(FactionMessages.notMember());
				return;
			}
			
	    	newName = GeneralMessages.nameFromArg(args.getString(0));

		}

		// Permission:
		if(!selFaction.canRename(sagaPlayer)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}
		
		// Fix spaces:
		while(newName.contains("  ")){
			newName = newName.replaceAll("  ", " ");
		}
	    	
	    // Validate name:
	    if(!validateName(newName)){
	    	sagaPlayer.message(FactionMessages.invalidName());
	    	return;
	    }
	    	
	    // Check name:
	    if(!newName.equals(selFaction.getName()) && FactionManager.manager().getFaction(newName) != null){
	    	sagaPlayer.message(FactionMessages.inUse(newName));
	    	return;
	    }
	    
	    Double cost = EconomyConfiguration.config().factionRenameCost;
	    if(selFaction.isFormed() && cost > 0){

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
	    
	    selFaction.setName(newName);
	    
	    // Inform:
	    selFaction.broadcast(FactionMessages.renamed(selFaction));
	    	
	    	
	}
	
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
				sagaPlayer.message(SettlementMessages.invalidPage(args.getString(0)));
				return;
			}
		}else{
			page = 0;
		}
		
		// Inform:
		sagaPlayer.message(InfoMessages.fhelp(page - 1));
	

	}

	
	
	// Until:
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
