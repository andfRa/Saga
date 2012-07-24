package org.saga.commands;

import java.util.Enumeration;

import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.dependencies.PermissionsManager;
import org.saga.messages.SettlementMessages;
import org.saga.messages.InfoMessages;
import org.saga.messages.PlayerMessages;
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
    @CommandPermissions({"saga.user.stats"})
	public static void stats(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	
		
		Integer page = null;
		
		// Arguments:
		if(args.argsLength() == 1){
			
			String sPage = args.getString(0);
			
			try {
				page = Integer.parseInt(sPage);
			}
			catch (NumberFormatException e) {
				sagaPlayer.message(SettlementMessages.invalidInteger(sPage));
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
			aliases = {"disableguardianrune","grdisable"},
            usage = "",
            flags = "",
            desc = "Disables the guardian rune.",
            min = 0,
            max = 0)
	@CommandPermissions({"saga.user.guardianrune.disable"})
	public static void disableGuardianStone(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		GuardianRune stone = sagaPlayer.getGuardRune();
		
		// Already disabled:
		if(!stone.isEnabled()){
			sagaPlayer.message(PlayerMessages.alreadyDisabled(stone));
			return;
		}
		
		// Disable:
		stone.setEnabled(false);
		
		// Inform:
		sagaPlayer.message(PlayerMessages.disabled(stone));
		
		
	}
	
	@Command(
            aliases = {"enableguardianrune","grenable"},
            usage = "",
            flags = "",
            desc = "Enables the guardian rune.",
            min = 0,
            max = 0)
	@CommandPermissions({"saga.user.guardianrune.enable"})
	public static void enableGuardianStone(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		GuardianRune stone = sagaPlayer.getGuardRune();
		
		// Already enabled:
		if(stone.isEnabled()){
			sagaPlayer.message(PlayerMessages.alreadyEnabled(stone));
			return;
		}
		
		// Disable:
		stone.setEnabled(true);
		
		// Inform:
		sagaPlayer.message(PlayerMessages.enabled(stone));
		
		
	}
	
	
	
	// Special chat:
	@Command(
            aliases = {"b"},
            usage = "<message>",
            flags = "",
            desc = "Sends a message in the special chat.",
            min = 1)
	@CommandPermissions({"saga.special.chat"})
	public static void specialChat(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		// Send special message:
		String message = PlayerMessages.specialChatMessage(sagaPlayer.getName(), args.getJoinedStrings(0));
		
		chatMessage(message);
		
		
	}
	
	private static void chatMessage(String message) {


		// Send the message to all players who have the correct permission:
		Enumeration<SagaPlayer> allPlayers = Saga.plugin().getLoadedPlayers();
		
		while (allPlayers.hasMoreElements()) {
			
			SagaPlayer loadedPlayer = allPlayers.nextElement();
			
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
			usage = "[page number]",
			flags = "",
			desc = "Display player help.",
			min = 0,
			max = 1
	)
	@CommandPermissions({"saga.user.help"})
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
		sagaPlayer.message(InfoMessages.phelp(page - 1));

		
	}
	
	
	

}
