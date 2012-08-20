package org.saga.commands;

import java.util.Collection;

import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.dependencies.PermissionsManager;
import org.saga.messages.GeneralMessages;
import org.saga.messages.HelpMessages;
import org.saga.messages.PlayerMessages;
import org.saga.messages.SettlementMessages;
import org.saga.messages.StatsMessages;
import org.saga.player.GuardianRune;
import org.saga.player.SagaPlayer;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;

public class PlayerCommands {
	
	
	// Info:
	@Command(
			aliases = {"stats"},
			usage = "[page]",
			flags = "",
			desc = "Shows player stats.",
			min = 0,
			max = 1
	)
    @CommandPermissions({"saga.user.player.stats"})
	public static void stats(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	
		
		Integer page = null;
		
		// Arguments:
		if(args.argsLength() == 1){
			
			String argsPage = args.getString(0);
			
			try {
				page = Integer.parseInt(argsPage);
			}
			catch (NumberFormatException e) {
				sagaPlayer.message(GeneralMessages.mustBeNumber(argsPage));
				return;
			}
			
		}else{
			page = 1;
		}
		
		// Inform:
		sagaPlayer.message(StatsMessages.stats(sagaPlayer, page-1));
    	
		
	}
	

	
	// Guardian stone:
	@Command(
			aliases = {"grdisable"},
            usage = "",
            flags = "",
            desc = "Disable guardian rune.",
            min = 0,
            max = 0)
	@CommandPermissions({"saga.user.player.guardrune.disable"})
	public static void disableGuardianStone(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		GuardianRune rune = sagaPlayer.getGuardRune();
		
		// Already disabled:
		if(!rune.isEnabled()){
			sagaPlayer.message(PlayerMessages.alreadyDisabled(rune));
			return;
		}
		
		// Disable:
		rune.setEnabled(false);
		
		// Inform:
		sagaPlayer.message(PlayerMessages.disabled(rune));
		
		
	}
	
	@Command(
            aliases = {"grenable"},
            usage = "",
            flags = "",
            desc = "Enable guardian rune.",
            min = 0,
            max = 0)
	@CommandPermissions({"saga.user.player.guardrune.enable"})
	public static void enableGuardianStone(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		GuardianRune rune = sagaPlayer.getGuardRune();
		
		// Already enabled:
		if(rune.isEnabled()){
			sagaPlayer.message(PlayerMessages.alreadyEnabled(rune));
			return;
		}
		
		// Disable:
		rune.setEnabled(true);
		
		// Inform:
		sagaPlayer.message(PlayerMessages.enabled(rune));
		
		
	}
	
	
	
	// Special chat:
	@Command(
            aliases = {"b"},
            usage = "<message>",
            flags = "",
            desc = "Sends a message in the special chat.",
            min = 1)
	@CommandPermissions({"saga.special.player.chat"})
	public static void specialChat(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		// Send special message:
		String message = PlayerMessages.specialChatMessage(sagaPlayer.getName(), args.getJoinedStrings(0));
		
		chatMessage(message);
		
		
	}
	
	private static void chatMessage(String message) {


		// Send the message to all players who have the correct permission:
		Collection<SagaPlayer> allPlayers = Saga.plugin().getLoadedPlayers();
		
		for (SagaPlayer loadedPlayer : allPlayers) {
			
			if(PermissionsManager.hasPermission(loadedPlayer, PermissionsManager.SPECIAL_CHAT_PERMISSION)){
				loadedPlayer.message(message);
			}
			
		}
		
		// Log:
		SagaLogger.message(message);

		
	}
	
	
	
	// Info:
	@Command(
			aliases = {"phelp"},
			usage = "[page]",
			flags = "",
			desc = "Display player help.",
			min = 0,
			max = 1
	)
	@CommandPermissions({"saga.user.help.player"})
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
		sagaPlayer.message(HelpMessages.phelp(page - 1));

		
	}
	
	
	

}
