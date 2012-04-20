/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga;

import java.util.ArrayList;
import java.util.Enumeration;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.saga.Clock.TimeOfDayTicker.TimeOfDay;
import org.saga.config.BalanceConfiguration;
import org.saga.config.SkillConfiguration;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.exceptions.SagaPlayerNotLoadedException;
import org.saga.messages.AdminMessages;
import org.saga.messages.ChunkGroupMessages;
import org.saga.messages.SagaMessages;
import org.saga.messages.StatsMessages;
import org.saga.player.SagaPlayer;
import org.saga.player.Skill;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;


/**
 *
 * @author Cory
 */
public class SagaCommands {

	
	// Help and info:
    @Command(
        aliases = {"saga"},
        usage = "",
        flags = "",
        desc = "TODO: ",
        min = 0,
        max = 0
    )
    @CommandPermissions({"saga.common.admin"})
    public static void saga(CommandContext args, Saga plugin, SagaPlayer player) {


    }

    @Command(
            aliases = {"astatsother","astatso"},
            usage = "<name> [page]",
            flags = "",
            desc = "Shows other player stats.",
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
			
			String sPage = args.getString(1);
			
			try {
				page = Integer.parseInt(sPage);
			}
			catch (NumberFormatException e) {
				sagaPlayer.message(ChunkGroupMessages.invalidInteger(sPage));
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
			sagaPlayer.message(SagaMessages.notOnline(name));
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
			
			String sPage = args.getString(1);
			
			try {
				page = Integer.parseInt(sPage);
			}
			catch (NumberFormatException e) {
				sagaPlayer.message(ChunkGroupMessages.invalidInteger(sPage));
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

    		sagaPlayer.message(SagaMessages.invalidPlayer(name));
    		return;
		
		}
    	
    	// Inform:
    	sagaPlayer.message(StatsMessages.stats(targetPlayer, page-1));
    	
    	// Unforce:
    	Saga.plugin().unforceSagaPlayer(name);
    	
    	
    }

    @Command(
        aliases = {"map"},
        usage = "",
        flags = "",
        desc = "Shows a map of claimed land.",
        min = 0,
        max = 0
    )
    @CommandPermissions({"saga.user.map"})
    public static void map(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

    	sagaPlayer.message(SagaMessages.map(sagaPlayer, sagaPlayer.getLocation()));
        
    }


    // Levels:
	@Command(
            aliases = {"asetlevel"},
            usage = "<player name> <level>",
            flags = "",
            desc = "Set players level.",
            min = 2,
            max = 2)
	@CommandPermissions({"saga.admin.setlevel"})
	public static void setLevel(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		SagaPlayer selectedPlayer = null;
		Integer level = null;
		
		// Arguments:
		String playerName = args.getString(0);
		try {
			
			// Force:
			selectedPlayer = Saga.plugin().forceSagaPlayer(playerName);
			
		} catch (NonExistantSagaPlayerException e) {
			
			sagaPlayer.message(ChunkGroupMessages.nonExistantPlayer(playerName));
			return;
			
		}
		
		try {
			
			level = Integer.parseInt(args.getString(1));
			
		} catch (NumberFormatException e) {
			
			sagaPlayer.message(ChunkGroupMessages.invalidInteger(args.getString(1)));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(playerName);
			return;
			
		}
		
		// Invalid level:
		if(level < 0 || level > BalanceConfiguration.config().maximumLevel){
			
			sagaPlayer.message(AdminMessages.levelOutOfRange(level + ""));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(playerName);
			return;
			
		}
		
		// Set level:
		selectedPlayer.setLevel(level);
		
		// Inform:
		selectedPlayer.message(AdminMessages.levelSetTo(selectedPlayer));
		if(selectedPlayer != sagaPlayer){
			sagaPlayer.message(AdminMessages.levelSet(selectedPlayer));
		}
		
		// Unforce:
		Saga.plugin().unforceSagaPlayer(playerName);
		return;	
			
	}
	
	@Command(
            aliases = {"asetskill"},
            usage = "<player name> <skill_name> <multiplier>",
            flags = "",
            desc = "Set players skill.",
            min = 3,
            max = 3)
	@CommandPermissions({"saga.admin.setskill"})
	public static void setSkill(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		SagaPlayer selectedPlayer = null;
		String skillName = null;
		Integer multiplier = null;
		
		// Arguments:
		String playerName = args.getString(0);
		try {
			
			// Force:
			selectedPlayer = Saga.plugin().forceSagaPlayer(playerName);
			
		} catch (NonExistantSagaPlayerException e) {
			
			sagaPlayer.message(ChunkGroupMessages.nonExistantPlayer(playerName));
			return;
			
		}
		
		skillName = args.getString(1).replaceAll(SagaMessages.spaceSymbol, " ");
		Skill skill = SkillConfiguration.config().getSkill(skillName);
		if(skill == null){
			
			sagaPlayer.message(AdminMessages.invalidSkill(skillName));
			
			// Unforce:
			Saga.plugin().unforceSagaPlayer(playerName);
			return;
			
		}
		
		try {
			
			multiplier = Integer.parseInt(args.getString(2));
			
		} catch (NumberFormatException e) {
			
			sagaPlayer.message(ChunkGroupMessages.invalidInteger(args.getString(2)));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(playerName);
			return;
			
		}
		
		// Invalid level:
		if(multiplier < 0 || multiplier > BalanceConfiguration.config().maximumSkillLevel){
			
			sagaPlayer.message(AdminMessages.skillOutOfRange(multiplier + ""));
			// Unforce:
			Saga.plugin().unforceSagaPlayer(playerName);
			return;
			
		}
		
		// Set skill:
		selectedPlayer.setSkillMultiplier(skillName, multiplier);
		
		// Inform:
		selectedPlayer.message(AdminMessages.skillSetTo(sagaPlayer, skillName));
		if(selectedPlayer != sagaPlayer){
			sagaPlayer.message(AdminMessages.skillSet(sagaPlayer, skillName));
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
            desc = "Sends a message in the admin chat.",
            min = 1)
	@CommandPermissions({"saga.admin.chat"})
	public static void adminChat(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		// Send admin message:
		sendAdminMessage(sagaPlayer.getName(), args.getJoinedStrings(0));
		
		
	}

    
    // Reward:
    @Command(
        aliases = {"areward"},
        usage = "<name> [name] [name] ... [name]",
        flags = "",
        desc = "Reward a player.",
        min = 1
    )
    @CommandPermissions({"saga.admin.reward"})
    public static void reward(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

    	
    	ArrayList<String> names = new ArrayList<String>();
    	for (int i = 0; i < args.argsLength(); i++) {
			names.add(args.getString(i));
		}
    	
    	// Reward:
    	ArrayList<String> remaining = reward(names);
    	
    	// Inform:
    	sagaPlayer.message(AdminMessages.rewarded(names));
    	if(remaining.size() > 0) sagaPlayer.message(AdminMessages.playersNotFound(remaining));
    	
    	
    }
    
	@Command(
            aliases = {"claimreward","collectreward"},
            usage = "",
            flags = "",
            desc = "Collect a reward.",
            min = 0
        )
	@CommandPermissions({"saga.user.claimreward"})
    public static void claimReward(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

        	
    		// Check reward:
    		if(sagaPlayer.getReward() <= 0){
    			sagaPlayer.message(AdminMessages.noReward());
    			return;
    		}
    	
    		// Reward:
    		Double exp = BalanceConfiguration.config().getExpReward(sagaPlayer.getReward());
    		Double coins = BalanceConfiguration.config().getCoinReward(sagaPlayer.getReward());
    		sagaPlayer.giveExperience(exp);
    		sagaPlayer.addCoins(coins);
    		sagaPlayer.clearReward();
    		
    		// Inform:
    		sagaPlayer.message(AdminMessages.collectedReward(exp, coins));
    		
    		
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
            min = 0,
            max = 1000
        )
    @CommandPermissions({"saga.admin.dcommand"})
    public static void debugCommand(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

    	
    	try {
			
    		Integer data = args.getInteger(0);
    		
    		sagaPlayer.playEffect(Effect.GHAST_SHOOT, data);
    		
    		
		}
		catch (Exception e) {
			System.out.println("e:" + e);
		}
    	
    	
    }

    
    // Time of day:
    @Command(
            aliases = {"asunt","asunrisetick"},
            usage = "",
            flags = "",
            desc = "Forces a sunrise tick.",
            min = 0,
            max = 0
        )
    @CommandPermissions({"saga.admin.forcetick"})
    public static void forceSunriseTick(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

    	Clock.clock().forceTick(TimeOfDay.SUNRISE);
    	
    }

    
    // Admin mode:
    @Command(
            aliases = {"enableadmin","aenable"},
            usage = "",
            flags = "",
            desc = "Enables Saga admin mode.",
            min = 0,
            max = 0)
	@CommandPermissions({"saga.admin.adminmode.enable"})
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
            desc = "Disables Saga admin mode.",
            min = 0,
            max = 0)
	@CommandPermissions({"saga.admin.adminmode.enable"})
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
            desc = "Saves saga information.",
            min = 0,
            max = 0
	)
	@CommandPermissions({"saga.admin.save"})
    public static void admin(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

    		// Inform:
    		sagaPlayer.message(AdminMessages.saving());
    		
    		// Save:
    		Saga.plugin().save();
    		
    		// Inform:
    		sagaPlayer.message(AdminMessages.saved());

        }

    
    // Utility:
    public static ArrayList<String> reward(ArrayList<String> names){
    	
    	
    	ArrayList<String> remaining = new ArrayList<String>();
    	
    	for (String name : names) {
			
    		SagaPlayer sagaPlayer = null;
    		
    		// Force:
    		try {
    			sagaPlayer = Saga.plugin().forceSagaPlayer(name);
			} catch (NonExistantSagaPlayerException e) {
				remaining.add(name);
				continue;
			}
    		
    		// Reward:
    		sagaPlayer.reward(1);
    		
    		// Unforce:
    		Saga.plugin().unforceSagaPlayer(name);
    		
		}
    	
    	return remaining;
    	
    	
    }

	public static void sendAdminMessage(String name, String message) {

		
		// Message:
		ChatColor nameColor = BalanceConfiguration.config().adminChatNameColor;
		ChatColor messageColor = BalanceConfiguration.config().adminChatMessageColor;
		String namedMessage = messageColor + "{" + nameColor + name + messageColor + "} " + message;
		
		// Send the message to all players who have the correct permission:
		Enumeration<SagaPlayer> allPlayers = Saga.plugin().getLoadedPlayers();
		
		while (allPlayers.hasMoreElements()) {
			
			SagaPlayer loadedPlayer = allPlayers.nextElement();
			
			
			if(Saga.plugin().hasPermission(loadedPlayer, "saga.admin.chat")){
				loadedPlayer.message(namedMessage);
			}
			
		}
		
		// Log:
		Saga.info(namedMessage);
		
		
	}
	
	public static void sendAdminWarning(String message) {

		sendAdminMessage("WARNING", message);
		
	}
	
	
}
