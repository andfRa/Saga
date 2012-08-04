/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga.commands;

import java.util.Enumeration;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.saga.Clock;
import org.saga.Clock.DaytimeTicker.Daytime;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.chunks.ChunkBundle;
import org.saga.chunks.ChunkBundleManager;
import org.saga.chunks.ChunkBundleToggleable;
import org.saga.config.AttributeConfiguration;
import org.saga.config.ExperienceConfiguration;
import org.saga.dependencies.PermissionsManager;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.exceptions.SagaPlayerNotLoadedException;
import org.saga.messages.AdminMessages;
import org.saga.messages.GeneralMessages;
import org.saga.messages.PlayerMessages;
import org.saga.messages.SettlementMessages;
import org.saga.messages.StatsMessages;
import org.saga.player.GuardianRune;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Settlement;
import org.saga.utility.text.TextUtil;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;


public class AdminCommands {
	
	
	// Stats:
	@Command(
		aliases = {"astatsother","astatso"},
		usage = "<name> [page]",
		flags = "",
		desc = "Show other player stats.",
		min = 1,
		max = 2
	)
	@CommandPermissions({"saga.admin.statsother"})
	public static void statsOther(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		String name = null;
		Integer page = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			name = args.getString(0);
			
			String argsPage = args.getString(1);
			
			try {
				page = Integer.parseInt(argsPage);
			}
			catch (NumberFormatException e) {
				sagaPlayer.message(GeneralMessages.mustBeNumber(argsPage));
				return;
			}
			
		}else{
			
			name = args.getString(0);
			
			page = 1;
			
		}
		
		// Match:
		SagaPlayer targetPlayer = null;
		try {
			targetPlayer = Saga.plugin().matchPlayer(name);
		} catch (SagaPlayerNotLoadedException e) {
			sagaPlayer.message(PlayerMessages.notOnline(name));
			return;
		}
		
		// Inform:
		sagaPlayer.message(AdminMessages.statsTargetName(targetPlayer));
		sagaPlayer.message(StatsMessages.stats(targetPlayer, page-1));
		
		
	}
	
	@Command(
		aliases = {"astatsotheroffline","astatsof"},
		usage = "<name> [page]",
		flags = "",
		desc = "Shows other player stats.",
		min = 1,
		max = 2
	)
	@CommandPermissions({"saga.admin.statsother.offline"})
	public static void statsOffline(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		String name = null;
		Integer page = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			name = args.getString(0);
			
			String argsPage = args.getString(1);
			
			try {
				page = Integer.parseInt(argsPage);
			}
			catch (NumberFormatException e) {
				sagaPlayer.message(GeneralMessages.mustBeNumber(argsPage));
				return;
			}
			
		}else{
			
			name = args.getString(0);
			
			page = 1;
			
		}
		
		// Find player:
		SagaPlayer targetPlayer = null;
		try {
		
			// Force:
			targetPlayer = Saga.plugin().forceSagaPlayer(name);
		
		} catch (NonExistantSagaPlayerException e) {

			sagaPlayer.message(PlayerMessages.invalidPlayer(name));
			return;
		
		}
		
		// Inform:
		sagaPlayer.message(StatsMessages.stats(targetPlayer, page-1));
		
		// Unforce:
		Saga.plugin().unforceSagaPlayer(name);
		
		
	}



	// Attributes and levels:
	@Command(
		aliases = {"asetlevel"},
		usage = "[player name] <level>",
		flags = "",
		desc = "Set players level.",
		min = 1,
		max = 2)
	@CommandPermissions({"saga.admin.setlevel"})
	public static void setLevel(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		SagaPlayer selectedPlayer = null;
		Integer level = null;
		
		String playerName = null;
		String argsLevel = null;

		// Arguments:
		switch (args.argsLength()) {
			
			case 1:
				
				playerName = sagaPlayer.getName();
				
				argsLevel = args.getString(0);
				try {
					level = Integer.parseInt(argsLevel);
				} catch (NumberFormatException e) {
					sagaPlayer.message(GeneralMessages.mustBeNumber(argsLevel));
				}
				
				break;

			default:

				playerName = args.getString(0);
				
				argsLevel = args.getString(1);
				try {
					level = Integer.parseInt(argsLevel);
				} catch (NumberFormatException e) {
					sagaPlayer.message(GeneralMessages.mustBeNumber(argsLevel));
				}
				
				break;
				
		}
		
		// Derived arguments:
		try {
			
			// Force:
			selectedPlayer = Saga.plugin().forceSagaPlayer(playerName);
			
		} catch (NonExistantSagaPlayerException e) {
			
			sagaPlayer.message(SettlementMessages.nonExistantPlayer(playerName));
			return;
			
		}
		
		// Invalid level:
		if(level < 0 || level > ExperienceConfiguration.config().maximumLevel){
			
			sagaPlayer.message(AdminMessages.levelOutOfRange(level + ""));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(playerName);
			return;
			
		}
		
		// Set level:
		selectedPlayer.setLevel(level);
		
		// Inform:
		selectedPlayer.message(AdminMessages.levelSet(level));
		if(selectedPlayer != sagaPlayer){
			sagaPlayer.message(AdminMessages.levelSet(level, selectedPlayer));
		}
		
		// Unforce:
		Saga.plugin().unforceSagaPlayer(playerName);
		return;	
			
	}
	
	@Command(
		aliases = {"asetattribute","asetattr"},
		usage = "[player name] <attribute> <score>",
		flags = "",
		desc = "Set players attribute score.",
		min = 2,
		max = 3)
	@CommandPermissions({"saga.admin.setattribute"})
	public static void setAttribute(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		SagaPlayer selectedPlayer = null;
		String attrName = null;
		Integer score = null;
		
		String playerName = null;
		
		// Arguments:
		switch (args.argsLength()) {
			case 2:
				
				playerName = sagaPlayer.getName();
				
				attrName = GeneralMessages.nameFromArg(args.getString(0));
				
				try {
					score = Integer.parseInt(args.getString(1));
				} catch (NumberFormatException e) {
					sagaPlayer.message(GeneralMessages.mustBeNumber(args.getString(1)));
				}
				
				break;

			default:

				playerName = args.getString(0);
				
				attrName = GeneralMessages.nameFromArg(args.getString(1));
				
				try {
					score = Integer.parseInt(args.getString(2));
				} catch (NumberFormatException e) {
					sagaPlayer.message(GeneralMessages.mustBeNumber(args.getString(1)));
				}
				
				break;
				
		}
		
		try {
			
			// Force:
			selectedPlayer = Saga.plugin().forceSagaPlayer(playerName);
			
		} catch (NonExistantSagaPlayerException e) {
			
			sagaPlayer.message(SettlementMessages.nonExistantPlayer(playerName));
			return;
			
		}

		// Invalid attribute:
		if(!AttributeConfiguration.config().getAttributeNames().contains(attrName)){
			sagaPlayer.message(AdminMessages.attributeInvalid(attrName, sagaPlayer));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(playerName);
			return;
		}
		
		// Invalid score:
		if(score < 0 || score > AttributeConfiguration.config().maxAttributeScore){
			
			sagaPlayer.message(AdminMessages.attributeOutOfRange(score + ""));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(playerName);
			return;
			
		}
		
		// Set attribute:
		selectedPlayer.setAttributeScore(attrName, score);
		
		// Inform:
		selectedPlayer.message(AdminMessages.attributeSet(attrName, score));
		if(selectedPlayer != sagaPlayer){
			sagaPlayer.message(AdminMessages.attributeSet(attrName, score, selectedPlayer));
		}
		
		// Unforce:
		Saga.plugin().unforceSagaPlayer(playerName);
		return;	
			
	}

	

	// Buildings general:
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
		ChunkBundle selectedChunkBundle = null;

		// Arguments:
		if(args.argsLength() == 2){
			
			// Chunk group:
			String bundleName = GeneralMessages.nameFromArg(args.getString(0));
			selectedChunkBundle = ChunkBundleManager.manager().getChunkBundleWithName(bundleName);
			if(selectedChunkBundle == null){
				sagaPlayer.message(SettlementMessages.noChunkBundle(bundleName));
				return;
			}

			try {
				level = Integer.parseInt(args.getString(1));
			} catch (NumberFormatException e) {
				sagaPlayer.message(GeneralMessages.mustBeNumber(args.getString(1)));
				return;
			}
			
		}else{
			
			// Chunk group:
			selectedChunkBundle = sagaPlayer.getChunkBundle();
			if(selectedChunkBundle == null){
				sagaPlayer.message( SettlementMessages.notMember() );
				return;
			}

			try {
				level = Integer.parseInt(args.getString(0));
			} catch (NumberFormatException e) {
				sagaPlayer.message(GeneralMessages.mustBeNumber(args.getString(0)));
				return;
			}
			
		}
		
		// Is a settlement:
		if(!(selectedChunkBundle instanceof Settlement)){
			sagaPlayer.message(SettlementMessages.notSettlement(selectedChunkBundle));
			return;
		}
		Settlement selectedSettlement = (Settlement) selectedChunkBundle;

		// Set level:
		selectedSettlement.setLevel(level);
		
		// Inform:
		selectedChunkBundle.broadcast(SettlementMessages.settleLevelBcast(selectedSettlement));
		if(selectedChunkBundle != sagaPlayer.getChunkBundle()){
			sagaPlayer.message(SettlementMessages.setLevel(selectedSettlement));
		}
		
	}
	
	@Command(
		aliases = {"asenableoption", "asenableopt", "aenableopt"},
		usage = "[settlement name] <option>",
		flags = "",
		desc = "Enable settlement option.",
		min = 1,
		max = 2
	)
	@CommandPermissions({"saga.admin.settlement.options.set.all"})
	public static void enableOption(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


		ChunkBundle selChunkBundle = null;
		ChunkBundleToggleable option = null;

		String aOption = null;
		
		// Arguments:
		switch (args.argsLength()) {
			case 2:
				
				// Chunk bundle:
				String bundleName = GeneralMessages.nameFromArg(args.getString(0));
				selChunkBundle = ChunkBundleManager.manager().getChunkBundleWithName(bundleName);
				if(selChunkBundle == null){
					sagaPlayer.message( SettlementMessages.noChunkBundle(bundleName));
					return;
				}
				
				// Option:
				aOption = args.getString(1);
				option = ChunkBundleToggleable.match(aOption);
				if(option == null){
					sagaPlayer.message(SettlementMessages.optionInvalid(args.getString(1)));
					sagaPlayer.message(SettlementMessages.optionInvalidInfo());
					return;
				}
				
				break;

			default:
			
				// Chunk group:
				selChunkBundle = sagaPlayer.getChunkBundle();
				if(selChunkBundle == null){
					sagaPlayer.message(SettlementMessages.notMember());
					return;
				}
				
				// Option:
				aOption = args.getString(0);
				option = ChunkBundleToggleable.match(aOption);
				if(option == null){
					sagaPlayer.message(SettlementMessages.optionInvalid(aOption));
					sagaPlayer.message(SettlementMessages.optionInvalidInfo());
					return;
				}
				
				break;
				
		}
		
		// Already enabled:
		if(selChunkBundle.isOptionEnabled(option)){
			sagaPlayer.message(SettlementMessages.optionAlreadyEnabled(selChunkBundle, option));
			return;
		}
		
		// Enable:
		selChunkBundle.enableOption(option);
		
		// Inform:
		sagaPlayer.message(SettlementMessages.optionToggle(selChunkBundle, option));
		
		
	}
	
	@Command(
		aliases = {"asdisableoption", "asdisableopt", "adisableopt"},
		usage = "[settlement name] <option>",
		flags = "",
		desc = "Disable settlement option.",
		min = 1,
		max = 2
	)
	@CommandPermissions({"saga.admin.settlement.options.set.all"})
	public static void disableOption(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


		ChunkBundle selChunkBundle = null;
		ChunkBundleToggleable option = null;
		
		String aOption = null;
		
		// Arguments:
		switch (args.argsLength()) {
			case 2:
				
				// Chunk bundle:
				String bundleName = GeneralMessages.nameFromArg(args.getString(0));
				selChunkBundle = ChunkBundleManager.manager().getChunkBundleWithName(bundleName);
				if(selChunkBundle == null){
					sagaPlayer.message( SettlementMessages.noChunkBundle(bundleName));
					return;
				}
				
				// Option:
				aOption = args.getString(1);
				option = ChunkBundleToggleable.match(aOption);
				if(option == null){
					sagaPlayer.message(SettlementMessages.optionInvalid(args.getString(1)));
					sagaPlayer.message(SettlementMessages.optionInvalidInfo());
					return;
				}
				
				break;

			default:
			
				// Chunk group:
				selChunkBundle = sagaPlayer.getChunkBundle();
				if(selChunkBundle == null){
					sagaPlayer.message(SettlementMessages.notMember());
					return;
				}
				
				// Option:
				aOption = args.getString(0);
				option = ChunkBundleToggleable.match(aOption);
				if(option == null){
					sagaPlayer.message(SettlementMessages.optionInvalid(aOption));
					sagaPlayer.message(SettlementMessages.optionInvalidInfo());
					return;
				}
				
				break;
				
		}
		
		// Already disabled:
		if(!selChunkBundle.isOptionEnabled(option)){
			sagaPlayer.message(SettlementMessages.optionAlreadyDisabled(selChunkBundle, option));
			return;
		}
		
		// Disabled:
		selChunkBundle.disableOption(option);
		
		// Inform:
		sagaPlayer.message(SettlementMessages.optionToggle(selChunkBundle, option));
		
		
	}
	
	
	
	// Guard rune:
	@Command(
		aliases = {"agrecharge","agrrech"},
		usage = "[player name]",
		flags = "",
		desc = "Recharges the guard rune.",
		min = 0,
		max = 1)
	@CommandPermissions({"saga.admin.guardrune"})
	public static void rechargeGuardDune(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
	
		SagaPlayer selectedPlayer = null;
		
		String playerName = null;
		

		// Arguments:
		switch (args.argsLength()) {
			case 0:
				
				playerName = sagaPlayer.getName();
				
				break;

			default:

				playerName = args.getString(0);
				
				break;
				
		}
		
		// Derived arguments:
		try {
			
			// Force:
			selectedPlayer = Saga.plugin().forceSagaPlayer(playerName);
			
		} catch (NonExistantSagaPlayerException e) {
			
			sagaPlayer.message(SettlementMessages.nonExistantPlayer(playerName));
			return;
			
		}
		
		// Already charged:
		GuardianRune rune = selectedPlayer.getGuardRune();
		if(rune.isCharged()){
			sagaPlayer.message(PlayerMessages.alreadyRecharged(rune));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(playerName);
			return;
		}
		
		// Recharge:
		rune.recharge();
		
		// Inform:
		if(selectedPlayer != sagaPlayer){
			selectedPlayer.message(AdminMessages.recharged(rune,sagaPlayer));
			sagaPlayer.message(AdminMessages.recharged(rune));
		}else{
			sagaPlayer.message(AdminMessages.recharged(rune));
		}
		
		// Unforce:
		Saga.plugin().unforceSagaPlayer(playerName);
		return;	
		
	}
	
	
	
	// Chat:
	@Command(
		aliases = {"a"},
		usage = "<message>",
		flags = "",
		desc = "Send an admin chat message.",
		min = 1)
	@CommandPermissions({"saga.admin.chat"})
	public static void adminChat(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		// Send admin message:
		String message = AdminMessages.chatMessage(sagaPlayer.getName(), args.getJoinedStrings(0));
		
		chatMessage(message);
		
		
	}
	
	public static void chatWarning(String message){
		

		// Send admin message:
		AdminMessages.chatMessage("WARNING", message);
		
		
	}
	
	public static void chatServer(String message){
		

		// Send admin message:
		AdminMessages.chatMessage("SERVER", message);
		
		
	}
	
	private static void chatMessage(String message) {


		// Send the message to all players who have the correct permission:
		Enumeration<SagaPlayer> allPlayers = Saga.plugin().getLoadedPlayers();
		
		while (allPlayers.hasMoreElements()) {
			
			SagaPlayer loadedPlayer = allPlayers.nextElement();
			
			if(PermissionsManager.hasPermission(loadedPlayer, PermissionsManager.ADMIN_CHAT_PERMISSION)){
				loadedPlayer.message(message);
			}
			
		}
		
		// Log:
		SagaLogger.message(message);

		
	}
	
	
	
	// Debug:
	@Command(
		aliases = {"debuginfo"},
		usage = "",
		flags = "",
		desc = "Debug assist command.",
		min = 0,
		max = 0
	)
	@CommandPermissions({"saga.admin.dinfo"})
	public static void debugInfo(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		Player player = sagaPlayer.getPlayer();
		
		sagaPlayer.message(ChatColor.GREEN + "level=" + player.getLevel() + " exp=" + player.getExp() + " totexp=" + player.getTotalExperience() + " exhaust=" + player.getExhaustion() + ".");
		
		
	}
	
	@Command(
		aliases = {"debugcommand","dc"},
		usage = "",
		flags = "",
		desc = "Debug assist command.",
		min = 0
	)
	@CommandPermissions({"saga.admin.dcommand"})
	public static void debugCommand(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		sagaPlayer.message(TextUtil.repeat(" ", args.getInteger(0))+"|");
		
	}
	
	@Command(
		aliases = {"damagetool","dt"},
		usage = "",
		flags = "",
		desc = "Debug assist command.",
		min = 0,
		max = 1000
	)
	@CommandPermissions({"saga.admin.dcommand"})
	public static void damageTool(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		ItemStack item = sagaPlayer.getPlayer().getItemInHand();
		
		if(item != null)
		item.setDurability((short) (item.getType().getMaxDurability() - 1));
				
		
	}
	
	
	
	// Time of day:
	@Command(
		aliases = {"anextdaytime","anexttime"},
		usage = "",
		flags = "",
		desc = "Force next daytime.",
		min = 0,
		max = 0
	)
	@CommandPermissions({"saga.admin.forcenexdaytime"})
	public static void forceNextDaytime(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


		// Next daytime:
		World world = sagaPlayer.getLocation().getWorld();
		Daytime daytime = Clock.clock().forceNextDaytime(world);
		
		// Inform:
		sagaPlayer.message(AdminMessages.nextDaytime(world, daytime));
		
		
	}
	
	
	
	// Administration mode:
	@Command(
		aliases = {"enableadmin","aenable"},
		usage = "",
		flags = "",
		desc = "Enable Saga admin mode.",
		min = 0,
		max = 0)
	@CommandPermissions({"saga.admin.adminmode"})
	public static void enableAdminmode(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		// Already enabled:
		if(sagaPlayer.isAdminMode()){
			sagaPlayer.message(AdminMessages.adminModeAlreadyEnabled());
			return;
		}
		
		// Enable:
		sagaPlayer.enableAdminMode();
		
		// Inform:
		sagaPlayer.message(AdminMessages.adminMode(sagaPlayer));
		
		
	}
	
	@Command(
		aliases = {"disableadmin","adisable"},
		usage = "",
		flags = "",
		desc = "Disable Saga admin mode.",
		min = 0,
		max = 0)
	@CommandPermissions({"saga.admin.adminmode"})
	public static void disableAdminmode(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		// Already disabled:
		if(!sagaPlayer.isAdminMode()){
			sagaPlayer.message(AdminMessages.adminModeAlreadyDisabled());
			return;
		}
		
		// Enable:
		sagaPlayer.disableAdminMode();
		
		// Inform:
		sagaPlayer.message(AdminMessages.adminMode(sagaPlayer));
		
		
	}
	
	
	
	// Saving:
	@Command(
		aliases = {"asave"},
		usage = "",
		flags = "",
		desc = "Saves Saga information.",
		min = 0,
		max = 0
	)
	@CommandPermissions({"saga.admin.save"})
	public static void save(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

			// Inform:
			sagaPlayer.message(AdminMessages.saving());
			
			// Save:
			Saga.plugin().save();
			
			// Inform:
			sagaPlayer.message(AdminMessages.saved());

	}
	
	
}
