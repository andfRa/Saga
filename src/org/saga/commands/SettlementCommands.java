package org.saga.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.config.EconomyConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.ProficiencyConfiguration.InvalidProficiencyException;
import org.saga.config.SettlementConfiguration;
import org.saga.dependencies.EconomyDependency;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.messages.AdminMessages;
import org.saga.messages.EconomyMessages;
import org.saga.messages.FactionMessages;
import org.saga.messages.GeneralMessages;
import org.saga.messages.HelpMessages;
import org.saga.messages.PlayerMessages;
import org.saga.messages.SettlementMessages;
import org.saga.messages.effects.SettlementEffectHandler;
import org.saga.player.Proficiency;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Bundle;
import org.saga.settlements.BundleManager;
import org.saga.settlements.SagaChunk;
import org.saga.settlements.Settlement;
import org.saga.settlements.Settlement.SettlementPermission;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.text.TextUtil;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;


public class SettlementCommands {

	
	// Territory:
	@Command(
		aliases = {"ssettle", "settle"},
		usage = "<settlement_name>",
		flags = "",
		desc = "Create a new settlement.",
		min = 1,
		max = 1
	)
	@CommandPermissions({"saga.user.settlement.create"})
	public static void create(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	
		
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
		if(BundleManager.manager().getBundle(settlementName) != null){
			sagaPlayer.message(SettlementMessages.inUse(settlementName));
			return;
		}
		
		// Settle:
		Settlement settlement = new Settlement(settlementName);
		settlement.complete();
		selChunk = new SagaChunk(location);
		settlement.addChunk(selChunk);
		Settlement.create(settlement, sagaPlayer);
		
		// Inform:
		sagaPlayer.message(SettlementMessages.settled(sagaPlayer, settlement));

		// Play effect:
		SettlementEffectHandler.playClaim(sagaPlayer, selChunk);
		
		
	}
	
	@Command(
		aliases = {"sclaim", "claim"},
		usage = "[settlement_name]",
		flags = "",
		desc = "Claim the chunk of land.",
		min = 0,
		max = 1
	)
	@CommandPermissions({"saga.user.settlement.claim"})
	public static void claim(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


		Bundle selBundle = null;

		// Arguments:
		if(args.argsLength() == 1){
			
			// Chunk bundle:
			String groupName = GeneralMessages.nameFromArg(args.getString(0));
			selBundle = BundleManager.manager().getBundle(groupName);
			if(selBundle == null){
				sagaPlayer.message(SettlementMessages.invalidBundle(groupName));
				return;
			}
			
		}else{
			
			// Chunk bundle:
			selBundle = sagaPlayer.getBundle();
			if(selBundle == null){
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
			
			if(sagaChunk.getChunkBundle() != selBundle){
				sagaPlayer.message(SettlementMessages.claimAdjacentDeny());
				return;
			}
			
		}
	   	
		// Not adjacent:
	   	if(!selBundle.isAdjacent(bukkitChunk)){
			sagaPlayer.message(SettlementMessages.chunkMustBeAdjacent());
			return;
		}
	   	
	   	// Permissions:
	   	if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.CLAIM)){
	   		sagaPlayer.message(GeneralMessages.noPermission());
	   		return;
	   	}
	   	
	   	// Claim points:
	   	Settlement selSettlement = null;
	   	if(selBundle instanceof Settlement){
	   		selSettlement = (Settlement) selBundle;
	   	}
	   	if(selSettlement == null || !selSettlement.isClaimsAvailable()){
	   		sagaPlayer.message(SettlementMessages.notEnoughClaims());
	   		return;
	   	}
	   	
		// Add a new chunk to adjacent chunk bundle:
		SagaChunk sagaChunk = new SagaChunk(bukkitChunk);
		selBundle.addChunk(sagaChunk);
		
		// Inform:
		if(sagaPlayer.getBundle() == selBundle){
			sagaPlayer.message(SettlementMessages.claimed(sagaChunk));
		}else{
			sagaPlayer.message(SettlementMessages.claimed(sagaChunk, selBundle));
		}
		
		// Refresh:
		sagaChunk.refresh();
		
		// Play effect:
		SettlementEffectHandler.playClaim(sagaPlayer, sagaChunk);
		
		
	}

	@Command(
		aliases = {"sabandon", "sunclaim", "abandon", "unclaim"},
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
		SettlementEffectHandler.playAbandon(sagaPlayer, selChunk);
		
		// Delete if none left:
		if(selBundle.checkDelete()){
			selBundle.delete();
			sagaPlayer.message(SettlementMessages.dissolved(selBundle));
		}
		
		// Statistics:
		StatisticsManager.manager().setBuildings(selBundle);
		
	}
	
	@Command(
		aliases = {"sresign"},
		usage = "[settlement_name] <new_owner_name>",
		flags = "",
		desc = "Resign from the owner position.",
		min = 1,
		max = 2
	)
	@CommandPermissions({"saga.user.settlement.resign"})
	public static void resign(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		Bundle selBundle = null;
		SagaPlayer selPlayer = null;
		
		String targetName = null;
		
		// Arguments:
		switch (args.argsLength()) {
			
			case 2:
				
				// Chunk bundle:
				String groupName = GeneralMessages.nameFromArg(args.getString(0));
				selBundle = BundleManager.manager().getBundle(groupName);
				if(selBundle == null){
					sagaPlayer.message(SettlementMessages.invalidBundle(groupName));
					return;
				}
				
				// New owner:
				targetName = selBundle.matchName(args.getString(1));
				if(!selBundle.isMember(targetName)){
					sagaPlayer.message(SettlementMessages.notMember(selBundle, targetName));
					return;
				}
				
				break;

			default:
				
				selBundle = sagaPlayer.getBundle();
				if(selBundle == null){
					sagaPlayer.message(SettlementMessages.notMember());
					return;
				}

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
		if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.RESIGN)){
			sagaPlayer.message(GeneralMessages.noPermission());
			return;
		}
		
		// Already owner:
		if(selBundle.isOwner(targetName)){
			
			if(selPlayer == sagaPlayer){
				sagaPlayer.message(SettlementMessages.alreadyOwner());
			}else{
				sagaPlayer.message(SettlementMessages.alreadyOwner(targetName));
			}
			
			return;
		}

		// Set owner:
		selBundle.setOwner(targetName);
		
		// Inform:
		selBundle.information(SettlementMessages.newOwner(targetName));
		
		 
	}

	@Command(
			aliases = {"sdissolve"},
			usage = "[settlement_name]",
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
			selBundle = BundleManager.manager().getBundle(groupName);
			if(selBundle == null){
				sagaPlayer.message(SettlementMessages.invalidBundle(groupName));
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

	   	// Claimed too much:
	   	if(selBundle instanceof Settlement){
		   		
	   		Settlement selSettlement = (Settlement) selBundle;
		   		
	   		if(selSettlement.checkToBigToDetele()){

	   			sagaPlayer.message(SettlementMessages.informTooBigDissolve());
				return;
				
	   		}
	
	   	}
	   	
	   	// Delete:
	   	selBundle.delete();
				
		// Inform:
		sagaPlayer.message(SettlementMessages.dissolved(selBundle));

		// Statistics:
		StatisticsManager.manager().setBuildings(selBundle);
		
		
	}

	@Command(
		aliases = {"sborder","cborder","sflash"},
		usage = "[repeat times]",
		flags = "",
		desc = "Show the outline of the chunk. Repeat and wilderness only in admin mode.",
		min = 0,
		max = 1
	)
	@CommandPermissions({"saga.user.settlement.showchunkborder"})
	public static void border(CommandContext args, Saga plugin, final SagaPlayer sagaPlayer) {

		
		int maxRepeat = 60;
		long period = 20;
		
		String argsAmount;
		Integer repeat = null;

		// Arguments:
		switch (args.argsLength()) {
			
			case 1:
				
				// Repeat:
				argsAmount = args.getString(0);
				try {
					repeat = Integer.parseInt(argsAmount) - 1;
				}
				catch (NumberFormatException e) {
					sagaPlayer.message(GeneralMessages.mustBeNumber(argsAmount));
					return;
				}
				break;

			default:

				repeat = 0;
				
				break;
				
		}
		
		final Chunk chunk = sagaPlayer.getLocation().getChunk();
		SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(chunk);
		
		// Permission:
		if(sagaChunk != null){
			Bundle selBundle = sagaChunk.getChunkBundle();
			if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.FLASH_CHUNK) ){
				sagaPlayer.message(GeneralMessages.noPermission(selBundle));
				return;
			}
		}
		
		// Admin mode only:
		if(!sagaPlayer.isAdminMode()){
			
			// Wilderness:
			if(sagaChunk == null){
				sagaPlayer.message(AdminMessages.borderWildernessAdminModeOnly());
				return;
			}
			
			// Repeat:
			if(repeat > 0){
				sagaPlayer.message(AdminMessages.borderRepeatAdminModeOnly());
				repeat = 0;
			}
			
		}

		// Trim:
		if(repeat > maxRepeat) repeat = maxRepeat;
		if(repeat < 0) repeat = 0;
		
		// First flash:
		SettlementEffectHandler.playFlashBorder(sagaPlayer, chunk);
		
		// Repeat flash:
		Runnable task = new Runnable() {
			@Override
			public void run() {
				SettlementEffectHandler.playFlashBorder(sagaPlayer, chunk);
			}
		};
		
		for (int i = 1; i <= repeat; i++) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task, period*i);
		}

		
	}
	

	
	// Members:
	@Command(
		aliases = {"sinvite"},
		usage = "[settlement_name] <player_name>",
		flags = "",
		desc = "Send a settlement join invitation.",
		min = 1,
		max = 2
	)
	@CommandPermissions({"saga.user.settlement.invite"})
	public static void invite(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		Bundle selBundle = null;
		SagaPlayer selPlayer = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			// Chunk bundle:
			String groupName = GeneralMessages.nameFromArg(args.getString(0));
			selBundle = BundleManager.manager().getBundle(groupName);
			if(selBundle == null){
				sagaPlayer.message(SettlementMessages.invalidBundle(groupName));
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
			selBundle = sagaPlayer.getBundle();
			if(selBundle == null){
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
		if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.INVITE) ){
			sagaPlayer.message(GeneralMessages.noPermission());
			return;
			
		}
		
		// Already a member:
		if(selBundle.isMember( selPlayer.getName()) ){
		
			sagaPlayer.message( SettlementMessages.alreadyInTheChunkBundle(selPlayer, selBundle));
			return;
			
		}
		
		// Already invited:
		if(selPlayer.hasBundleInvite(selBundle.getId())){
			
			sagaPlayer.message( SettlementMessages.alreadyInvited(selPlayer, selBundle) );
			return;
			
		}
		
		// Add invite:
		selPlayer.addBundleInvite(selBundle.getId());
		
		// Inform:
		selPlayer.message(SettlementMessages.wasInvited(selPlayer, selBundle));
		selBundle.information(SettlementMessages.invited(selPlayer, selBundle));
		selPlayer.message(SettlementMessages.informAccept());
		
		// Release:
		selPlayer.indicateRelease();
		
		
	}
	
	@Command(
		aliases = {"saccept"},
		usage = "[settlement_name]",
		flags = "",
		desc = "Accept a settlement join invitation.",
		min = 0,
		max = 1
	)
	@CommandPermissions({"saga.user.settlement.accept"})
	public static void accept(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		String argsBundle = null;
		
		Bundle selBundle = null;
		
		// Bundle member:
		if(sagaPlayer.getBundle() != null){
			sagaPlayer.message(SettlementMessages.alreadyInBundle());
			return;
		}
    	
    	// No invites:
    	ArrayList<Bundle> inviteBundles = BundleManager.manager().getBundles(sagaPlayer.getBundleInvites());
    	if(inviteBundles.size() == 0){
    		sagaPlayer.message(SettlementMessages.noInvites());
    		return;
    	}
    	
		// Arguments:
    	switch (args.argsLength()) {
			case 1:
				
				argsBundle = args.getString(0);
				selBundle = BundleManager.manager().matchBundle(argsBundle);
				
				if(selBundle == null){
					sagaPlayer.message(SettlementMessages.invalidBundle(argsBundle));
					return;
				}
				
				if(!inviteBundles.contains(selBundle)){
					sagaPlayer.message(SettlementMessages.noInvites(argsBundle));
					return;
				}
				
				break;

			default:
				
				selBundle = inviteBundles.get(inviteBundles.size()-1);
			
				break;
				
		}
    	
    	// Inform:
    	selBundle.information(SettlementMessages.joined(sagaPlayer, selBundle));
		sagaPlayer.message(SettlementMessages.haveJoined(sagaPlayer, selBundle));

    	// Add to bundle:
		selBundle.addMember(sagaPlayer);
    	
		// Set as owner:
		if(!selBundle.hasOwner()){
			selBundle.setOwner(sagaPlayer.getName());
		}
		
    	// Decline every invitation:
    	ArrayList<Integer> inviteIds = sagaPlayer.getBundleInvites();
    	for (Integer inviteId : inviteIds) {
    		sagaPlayer.removeBundleInvite(inviteId);
		}
    	
    	
	}

	@Command(
		aliases = {"sdecline"},
		usage = "[settlement_name]",
		flags = "",
		desc = "Decline a settlement invitation.",
		min = 0,
		max = 1
	)
	@CommandPermissions({"saga.user.settlement.decline"})
	public static void decline(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		String argsBundle = null;
		
		Bundle selBundle = null;
		
		// Bundle member:
		if(sagaPlayer.getBundle() != null){
			sagaPlayer.message(SettlementMessages.alreadyInBundle());
			return;
		}
    	
    	// No invites:
    	ArrayList<Bundle> inviteBundles = BundleManager.manager().getBundles(sagaPlayer.getBundleInvites());
    	if(inviteBundles.size() == 0){
    		sagaPlayer.message(SettlementMessages.noInvites());
    		return;
    	}
    	
		// Arguments:
    	switch (args.argsLength()) {
			case 1:
				
				argsBundle = args.getString(0);
				selBundle = BundleManager.manager().matchBundle(argsBundle);
				
				if(selBundle == null){
					sagaPlayer.message(SettlementMessages.invalidBundle(argsBundle));
					return;
				}
				
				if(!inviteBundles.contains(selBundle)){
					sagaPlayer.message(SettlementMessages.noInvites(argsBundle));
					return;
				}
				
				break;

			default:
				
				selBundle = inviteBundles.get(inviteBundles.size()-1);
			
				break;
				
		}

    	// Remove invite:
    	sagaPlayer.removeBundleInvite(selBundle.getId());
    	
    	// Inform:
    	sagaPlayer.message(SettlementMessages.declinedInvite(selBundle));
		
		
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
		selBundle.information(SettlementMessages.quit(sagaPlayer, selBundle));
		sagaPlayer.message(SettlementMessages.haveQuit(sagaPlayer, selBundle));
		
		// Delete:
		if(selBundle instanceof Settlement){
			
			Settlement selSettlement = (Settlement) selBundle;
			
			if(selBundle.getMemberCount() == 0){

				if(!selSettlement.checkToBigToDetele()){

					// Delete:
					selSettlement.delete();
						
					// Inform:
					sagaPlayer.message(SettlementMessages.dissolved(selSettlement));
					
				}else{
					
					sagaPlayer.message(SettlementMessages.informTooBigDissolve());
					
				}
				
			}

			// Statistics:
			StatisticsManager.manager().setBuildings(selBundle);
			
		}
		
		
	}
	
	@Command(
		aliases = {"skick"},
		usage = "[settlement_name] <member_name>",
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
				selBundle = BundleManager.manager().getBundle(groupName);
				if(selBundle == null){
					sagaPlayer.message(SettlementMessages.invalidBundle(groupName));
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
		selBundle.information(SettlementMessages.kicked(selPlayer, selBundle));
		selPlayer.message(SettlementMessages.wasKicked(selPlayer, selBundle));
		
		// Delete:
		if(selBundle instanceof Settlement){
			
			Settlement selsettlement = (Settlement) selBundle;
			
			if(selBundle.getMemberCount() == 0){

				if(!selsettlement.checkToBigToDetele()){

					// Delete:
					selsettlement.delete();
						
					// Inform:
					sagaPlayer.message(SettlementMessages.dissolved(selsettlement));
					
				}else{
					
					sagaPlayer.message(SettlementMessages.informTooBigDissolve());
					
				}
				
			}

			// Statistics:
			StatisticsManager.manager().setBuildings(selBundle);
			
		}
		
		
	}

	@Command(
		aliases = {"ssetrole"},
		usage = "<member_name> <role_name>",
		flags = "",
		desc = "Assign a role.",
		min = 2,
		max = 3
	)
	@CommandPermissions({"saga.user.settlement.setrole"})
	public static void setRole(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		Bundle selBundle = null;
		String targetName = null;
		String roleName = null;
		
		// Arguments:
		if(args.argsLength() == 3){
			
			// Chunk bundle:
			String groupName = GeneralMessages.nameFromArg(args.getString(0));
			selBundle = BundleManager.manager().getBundle(groupName);
			if(selBundle == null){
				sagaPlayer.message(SettlementMessages.invalidBundle(groupName));
				return;
			}
			
			targetName = selBundle.matchName(args.getString(1));

			roleName = GeneralMessages.nameFromArg(args.getString(2));
			
		}else{
			
			// Chunk bundle:
			selBundle = sagaPlayer.getBundle();
			if(selBundle == null){
				sagaPlayer.message(SettlementMessages.notMember());
				return;
			}

			targetName = selBundle.matchName(args.getString(0));

			roleName = GeneralMessages.nameFromArg(args.getString(1));
			
		}
		
		// Is a settlement:
		if(!(selBundle instanceof Settlement)){
			sagaPlayer.message(SettlementMessages.notSettlement(selBundle));
			return;
		}
		Settlement selSettlement = (Settlement) selBundle;

		// Permission:
		if(!selSettlement.hasPermission(sagaPlayer, SettlementPermission.SET_ROLE)){
			sagaPlayer.message(GeneralMessages.noPermission(selBundle));
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
		if( !selBundle.equals(selPlayer.getBundle()) ){
			sagaPlayer.message(SettlementMessages.notMember(selBundle, selPlayer.getName()));
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
		if(!selSettlement.isRoleAvailable(role.getName())){
			sagaPlayer.message(SettlementMessages.roleNotAvailable(roleName));
			return;
		}
		
		// Set role:
		selSettlement.setRole(selPlayer, role);
		
		// Inform:
		selBundle.information(SettlementMessages.newRole(selPlayer, selSettlement, roleName));

		// Release:
		selPlayer.indicateRelease();
		
		
	}
	
	
	
	// Stats:
	@Command(
		aliases = {"sstats"},
		usage = "[settlement_name] [page]",
		flags = "",
		desc = "Show settlement stats.",
		min = 0,
		max = 2
	)
	@CommandPermissions({"saga.user.settlement.stats"})
	public static void stats(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		Integer page = null;
		Settlement selSettlement = null;
		
		Bundle selBundle = null;
		String argsPage = null;
		String groupName = null;
		
		// Arguments:
		switch (args.argsLength()) {
			
			case 2:
				
				// Chunk bundle:
				groupName = GeneralMessages.nameFromArg(args.getString(0));
				selBundle = BundleManager.manager().getBundle(groupName);
				if(selBundle == null){
					sagaPlayer.message(SettlementMessages.invalidBundle(groupName));
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
				selBundle = sagaPlayer.getBundle();
				if(selBundle == null){
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
				selBundle = sagaPlayer.getBundle();
				if(selBundle == null){
					sagaPlayer.message(SettlementMessages.notMember());
					return;
				}
				
				// Page:
				page = 1;
				
				break;
				
		}
		
		// Is a settlement:
		if(! (selBundle instanceof Settlement) ){
			sagaPlayer.message(SettlementMessages.notSettlement(selBundle));
			return;
		}
		selSettlement = (Settlement) selBundle;
		
		// Inform:
		sagaPlayer.message(SettlementMessages.stats(sagaPlayer, selSettlement, page -1));
		
		
	}

	@Command(
		aliases = {"slist"},
		usage = "[settlement_name]",
		flags = "",
		desc = "List settlement members.",
		min = 0,
		max = 1
		)
	@CommandPermissions({"saga.user.settlement.list"})
	public static void list(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		Bundle selBundle = null;

		// Arguments:
		if(args.argsLength() == 1){
			
			// Chunk bundle:
			String groupName = GeneralMessages.nameFromArg(args.getString(0));
			selBundle = BundleManager.manager().getBundle(groupName);
			if(selBundle == null){
				sagaPlayer.message(SettlementMessages.invalidBundle(groupName));
				return;
			}
			
		}else{
			
			// Chunk bundle:
			selBundle = sagaPlayer.getBundle();
			if(selBundle == null){
				sagaPlayer.message(SettlementMessages.notMember());
				return;
			}
			
		}
		
		// Is a settlement:
		if(! (selBundle instanceof Settlement) ){
			sagaPlayer.message(SettlementMessages.notSettlement(selBundle));
			return;
		}
		Settlement selSettlement = (Settlement) selBundle;
		
		// Inform:
		sagaPlayer.message(SettlementMessages.list(sagaPlayer, selSettlement));
		
		
	}
	
	
	
	// Info:
	@Command(
		aliases = {"shelp"},
		usage = "[page]",
		flags = "",
		desc = "Display settlement help.",
		min = 0,
		max = 1
	)
	@CommandPermissions({"saga.user.help.settlement"})
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
		usage = "[settlement_name] <new_name>",
		flags = "",
		desc = "Rename the settlement.",
		min = 1,
		max = 2
	)
	@CommandPermissions({"saga.user.settlement.rename"})
	public static void rename(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	    	
		
		Bundle selBundle = null;
		String name = null;

		// Arguments:
		if(args.argsLength() == 2){
			
			// Chunk bundle:
			String groupName = GeneralMessages.nameFromArg(args.getString(0));
			selBundle = BundleManager.manager().getBundle(groupName);
			if(selBundle == null){
				sagaPlayer.message(SettlementMessages.invalidBundle(groupName));
				return;
			}
			
			name = GeneralMessages.nameFromArg(args.getString(1));
			
		}else{
			
			// Chunk bundle:
			selBundle = sagaPlayer.getBundle();
			if(selBundle == null){
				sagaPlayer.message(SettlementMessages.notMember());
				return;
			}
			
			name = GeneralMessages.nameFromArg(args.getString(0));

		}

		// Permission:
		if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.RENAME)){
			sagaPlayer.message(GeneralMessages.noPermission(selBundle));
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
	    if(BundleManager.manager().getBundle(name) != null){
	    	sagaPlayer.message(FactionMessages.inUse(name));
	    	return;
	    }
	    
	    Double cost = EconomyConfiguration.config().chunkGroupRenameCost;
		if(cost > 0){

		    // Check coins:
		    if(EconomyDependency.getCoins(sagaPlayer) < cost){
		    	sagaPlayer.message(EconomyMessages.insuficcientCoins(cost));
		    	return;
		    }
		    
	    	// Take coins:
		    EconomyDependency.removeCoins(sagaPlayer, cost);
		    
		    // Inform:
		    sagaPlayer.message(EconomyMessages.spent(cost));
		    
	    }
	    
	    // Rename:
	    selBundle.setName(name);
	    
	    // Inform:
	    selBundle.information(SettlementMessages.renamed(selBundle));
	    	
	    	
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
	public static boolean validateName(String name) {

		
		if(TextUtil.getComparisonString(name).length() < SettlementConfiguration.config().getMinNameLength()) {
			return false;
		}

		if(name.length() > SettlementConfiguration.config().getMaxNameLength()) {
			return false;
		}

		for (char c : name.toCharArray()) {
			
			if ( ! org.saga.utility.text.TextUtil.substanceChars.contains(String.valueOf(c))) {
				return false;
			}
			
		}

		return true;

         
	}
	
	
}
