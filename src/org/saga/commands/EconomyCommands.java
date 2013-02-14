package org.saga.commands;

import org.bukkit.Location;
import org.saga.Saga;
import org.saga.config.EconomyConfiguration;
import org.saga.dependencies.EconomyDependency;
import org.saga.messages.EconomyMessages;
import org.saga.messages.GeneralMessages;
import org.saga.messages.HelpMessages;
import org.saga.messages.SettlementMessages;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Bundle;
import org.saga.settlements.BundleManager;
import org.saga.settlements.Settlement;
import org.saga.settlements.Settlement.SettlementPermission;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;


public class EconomyCommands {


	// Coins:
	@Command(
			aliases = {"spay", "pay"},
			usage = "<name> <amount>",
			flags = "",
			desc = "Gives money to someone.",
			min = 2,
			max = 2
	)
	@CommandPermissions({"saga.user.economy.pay"})
	public static void pay(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		// Disabled:
		if(!EconomyConfiguration.config().isEnabled()){
			sagaPlayer.message(EconomyMessages.economyDisabled());
			return;
		}
		
		// Arguments:
		SagaPlayer selPlayer = Saga.plugin().getLoadedPlayer(args.getString(0));
		if(selPlayer == null){
			sagaPlayer.message(GeneralMessages.notOnline(args.getString(0)));
			return;
		}
		
		Double amount = null;
		try {
			amount = Double.parseDouble(args.getString(1));
		} catch (NumberFormatException e) {
			sagaPlayer.message(GeneralMessages.notNumber(args.getString(1)));
			return;
		}
		
		if(amount < 0){
			amount *= -1;
		}
		
		// Enough currency:
		if(EconomyDependency.getCoins(sagaPlayer) < amount){
			sagaPlayer.message(EconomyMessages.insufficient());
			return;
		}
		
		// Not online:
		Location playerLocation = sagaPlayer.getLocation();
		Location targetLocation = selPlayer.getLocation();
		if(playerLocation == null || targetLocation == null){
			sagaPlayer.message(EconomyMessages.tooFarPay());
			return;
		}
		
		// Other world:
		if(!playerLocation.getWorld().getName().equals(targetLocation.getWorld().getName())){
			sagaPlayer.message(EconomyMessages.tooFarPay());
			return;
		}
		
		double distance = playerLocation.distance(targetLocation);
		double maxDistance = EconomyConfiguration.config().exchangeDistance;
		if(distance > maxDistance){
			sagaPlayer.message(EconomyMessages.tooFarPay(maxDistance));
			return;
		}
		
		// Pay:
		EconomyDependency.removeCoins(sagaPlayer, amount);
		EconomyDependency.addCoins(selPlayer, amount);
		
		// Inform:
		sagaPlayer.message(EconomyMessages.paid(selPlayer, amount));
		selPlayer.message(EconomyMessages.gotPaid(sagaPlayer, amount));
		
		
	}
	
	@Command(
			aliases = {"balance","wallet","bal","emoney"},
			usage = "",
			flags = "",
			desc = "See how much currency you have.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.user.economy.balance"})
	public static void balance(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		sagaPlayer.message(EconomyMessages.wallet(sagaPlayer));
		
	}
	
	
	// Settlements:
	@Command(
		aliases = {"saddcoins","sdeposit"},
		usage = "[settlement_name] <amount>",
		flags = "",
		desc = "Deposit coins to the settlements bank.",
		min = 1,
		max = 2
	)
	@CommandPermissions({"saga.user.economy.settlements.addcoins"})
	public static void addCoins(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		// Disabled:
		if(!EconomyConfiguration.config().isEnabled()){
			sagaPlayer.message(EconomyMessages.economyDisabled());
			return;
		}
		
		Bundle selBundle = null;
		Double amount = null;
		
		String strBundle = null;
		String strAmount = null;
		
		switch (args.argsLength()) {
			
			case 2:

				// Bundle:
				strBundle = GeneralMessages.nameFromArg(args.getString(0));
				selBundle = BundleManager.manager().matchBundle(strBundle);
				if(selBundle == null){
					sagaPlayer.message(GeneralMessages.invalidSettlement(strBundle));
					return;
				}
				
				// Amount:
				strAmount = args.getString(1);
				try {
					amount = Double.parseDouble(strAmount);
				}
				catch (NumberFormatException e) {
					sagaPlayer.message(GeneralMessages.notNumber(strAmount));
					return;
				}
				
				break;

			default:
				
				// Bundle:
				selBundle = sagaPlayer.getBundle();
				if(selBundle == null){
					sagaPlayer.message(SettlementMessages.notMember());
					return;
				}

				// Amount:
				strAmount = args.getString(0);
				try {
					amount = Double.parseDouble(strAmount);
				}
				catch (NumberFormatException e) {
					sagaPlayer.message(GeneralMessages.notNumber(strAmount));
					return;
				}
				
				break;
				
		}

		// Settlement:
		if(!(selBundle instanceof Settlement)){
			sagaPlayer.message(GeneralMessages.notSettlement(selBundle));
			return;
		}
		Settlement selSettlement = (Settlement) selBundle;

		// Fix amount:
		if(amount <= 0.0){
			sagaPlayer.message(GeneralMessages.mustBePositive(amount));
			return;
		}

		// Permission:
		if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.ADD_COINS)){
			sagaPlayer.message(GeneralMessages.noPermission(selBundle));
			return;
		}

		// Request coins:
		amount = sagaPlayer.requestCoins(amount);
		
		if(amount != 0.0){
			
			// Add coins:
			selSettlement.modCoins(amount);
			
			// Inform:
			sagaPlayer.message(EconomyMessages.settlementAddedCoins(amount));
		
		}else{
			
			// Inform:
			sagaPlayer.message(EconomyMessages.settlementNothingToDeposit());
			
		}
		
		
		
	}

	@Command(
		aliases = {"sremovecoins","swihdraw"},
		usage = "[settlement_name] <amount>",
		flags = "",
		desc = "Withdraw coins from the settlements bank.",
		min = 1,
		max = 2
	)
	@CommandPermissions({"saga.user.economy.settlements.removecoins"})
	public static void removeCoins(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		// Disabled:
		if(!EconomyConfiguration.config().isEnabled()){
			sagaPlayer.message(EconomyMessages.economyDisabled());
			return;
		}
		
		Bundle selBundle = null;
		Double amount = null;
		
		String strBundle = null;
		String strAmount = null;
		
		switch (args.argsLength()) {
			
			case 2:

				// Bundle:
				strBundle = GeneralMessages.nameFromArg(args.getString(0));
				selBundle = BundleManager.manager().matchBundle(strBundle);
				if(selBundle == null){
					sagaPlayer.message(GeneralMessages.invalidSettlement(strBundle));
					return;
				}
				
				// Amount:
				strAmount = args.getString(1);
				try {
					amount = Double.parseDouble(strAmount);
				}
				catch (NumberFormatException e) {
					sagaPlayer.message(GeneralMessages.notNumber(strAmount));
					return;
				}
				
				break;

			default:
				
				// Bundle:
				selBundle = sagaPlayer.getBundle();
				if(selBundle == null){
					sagaPlayer.message(SettlementMessages.notMember());
					return;
				}

				// Amount:
				strAmount = args.getString(0);
				try {
					amount = Double.parseDouble(strAmount);
				}
				catch (NumberFormatException e) {
					sagaPlayer.message(GeneralMessages.notNumber(strAmount));
					return;
				}
				
				break;
				
		}

		// Settlement:
		if(!(selBundle instanceof Settlement)){
			sagaPlayer.message(GeneralMessages.notSettlement(selBundle));
			return;
		}
		Settlement selSettlement = (Settlement) selBundle;

		// Fix amount:
		if(amount <= 0.0){
			sagaPlayer.message(GeneralMessages.mustBePositive(amount));
			return;
		}

		// Permission:
		if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.REMOVE_COINS)){
			sagaPlayer.message(GeneralMessages.noPermission(selBundle));
			return;
		}
		
		// Request coins:
		amount = selSettlement.requestCoins(amount);
		
		if(amount != 0.0){
			
			// Add coins:
			sagaPlayer.modCoins(amount);
			
			// Inform:
			sagaPlayer.message(EconomyMessages.settlementRemovedCoins(amount));
		
		}else{
			
			// Inform:
			sagaPlayer.message(EconomyMessages.settlementNothingToWithdraw());
			
		}
		
		
	}
	
	@Command(
		aliases = {"sbuyclaims","buyclaims"},
		usage = "[settlement_name] [amount]",
		flags = "",
		desc = "Buy additional claims for the settlement.",
		min = 0,
		max = 2
	)
	@CommandPermissions({"saga.user.economy.settlements.buyclaims"})
	public static void buyClaims(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		// Disabled:
		if(!EconomyConfiguration.config().isEnabled()){
			sagaPlayer.message(EconomyMessages.economyDisabled());
			return;
		}
		
		Bundle selBundle = null;
		Integer amount = null;
		
		String strBundle = null;
		String strAmount = null;
		
		switch (args.argsLength()) {
			
			case 2:

				// Bundle:
				strBundle = GeneralMessages.nameFromArg(args.getString(0));
				selBundle = BundleManager.manager().matchBundle(strBundle);
				if(selBundle == null){
					sagaPlayer.message(GeneralMessages.invalidSettlement(strBundle));
					return;
				}
				
				// Amount:
				strAmount = args.getString(1);
				try {
					amount = Integer.parseInt(strAmount);
				}
				catch (NumberFormatException e) {
					sagaPlayer.message(GeneralMessages.notNumber(strAmount));
					return;
				}
				
				break;

			case 1:
				
				// Bundle:
				selBundle = sagaPlayer.getBundle();
				if(selBundle == null){
					sagaPlayer.message(SettlementMessages.notMember());
					return;
				}

				// Amount:
				strAmount = args.getString(0);
				try {
					amount = Integer.parseInt(strAmount);
				}
				catch (NumberFormatException e) {
					sagaPlayer.message(GeneralMessages.notNumber(strAmount));
					return;
				}
				
				break;
				
			default:

				// Bundle:
				selBundle = sagaPlayer.getBundle();
				if(selBundle == null){
					sagaPlayer.message(SettlementMessages.notMember());
					return;
				}
				
				amount = 1;
				
				break;
				
		}

		// Settlement:
		if(!(selBundle instanceof Settlement)){
			sagaPlayer.message(GeneralMessages.notSettlement(selBundle));
			return;
		}
		Settlement selSettlement = (Settlement) selBundle;

//		// Requirements:
//		if(!selSettlement.checkRequirements()){
//			sagaPlayer.message(GeneralMessages.requirementsNotMet(selSettlement));
//			return;
//		}
		
		// Fix amount:
		if(amount <= 0.0){
			sagaPlayer.message(GeneralMessages.mustBePositive(amount));
			return;
		}

		// Permission:
		if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.BUY_CLAIMS)){
			sagaPlayer.message(GeneralMessages.noPermission(selBundle));
			return;
		}

		// Trim amount:
		Double coins = selSettlement.getCoins();
		Integer claims = selSettlement.getTotalClaims();
		Double cost = 0.0;
		for (int claimsMod = 0; claimsMod <= amount; claimsMod++) {
			
			Double costMod = EconomyConfiguration.config().getClaimPointCost(claims + claimsMod + 1);
			if(cost + costMod > coins){
				amount = claimsMod;
				break;
			}
			cost+= costMod;
			
		}
		
		if(amount == 0){
			sagaPlayer.message(EconomyMessages.settlementInsufficientCoins());
			return;
		}
		
		// Take coins:
		selSettlement.modCoins(-cost);
		
		// Add claims:
		selSettlement.modClaims(amount.doubleValue());
		
		// Inform:
		sagaPlayer.message(EconomyMessages.settlementBoughtClaims(amount, cost));
		
		
	}

	@Command(
		aliases = {"sbuybuildpoints","buybpoints"},
		usage = "[settlement_name] [amount]",
		flags = "",
		desc = "Buy additional build points for the settlement.",
		min = 0,
		max = 2
	)
	@CommandPermissions({"saga.user.economy.settlements.buybuildpoints"})
	public static void buyBuildPoints(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		// Disabled:
		if(!EconomyConfiguration.config().isEnabled()){
			sagaPlayer.message(EconomyMessages.economyDisabled());
			return;
		}
		
		Bundle selBundle = null;
		Integer amount = null;
		
		String strBundle = null;
		String strAmount = null;
		
		switch (args.argsLength()) {
			
			case 2:

				// Bundle:
				strBundle = GeneralMessages.nameFromArg(args.getString(0));
				selBundle = BundleManager.manager().matchBundle(strBundle);
				if(selBundle == null){
					sagaPlayer.message(GeneralMessages.invalidSettlement(strBundle));
					return;
				}
				
				// Amount:
				strAmount = args.getString(1);
				try {
					amount = Integer.parseInt(strAmount);
				}
				catch (NumberFormatException e) {
					sagaPlayer.message(GeneralMessages.notNumber(strAmount));
					return;
				}
				
				break;

			case 1:
				
				// Bundle:
				selBundle = sagaPlayer.getBundle();
				if(selBundle == null){
					sagaPlayer.message(SettlementMessages.notMember());
					return;
				}

				// Amount:
				strAmount = args.getString(0);
				try {
					amount = Integer.parseInt(strAmount);
				}
				catch (NumberFormatException e) {
					sagaPlayer.message(GeneralMessages.notNumber(strAmount));
					return;
				}
				
				break;
				
			default:

				// Bundle:
				selBundle = sagaPlayer.getBundle();
				if(selBundle == null){
					sagaPlayer.message(SettlementMessages.notMember());
					return;
				}
				
				amount = 1;
				
				break;
				
		}

		// Settlement:
		if(!(selBundle instanceof Settlement)){
			sagaPlayer.message(GeneralMessages.notSettlement(selBundle));
			return;
		}
		Settlement selSettlement = (Settlement) selBundle;
		
//		// Requirements:
//		if(!selSettlement.checkRequirements()){
//			sagaPlayer.message(GeneralMessages.requirementsNotMet(selSettlement));
//			return;
//		}
		
		// Fix amount:
		if(amount <= 0.0){
			sagaPlayer.message(GeneralMessages.mustBePositive(amount));
			return;
		}

		// Permission:
		if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.BUY_BUILD_POINTS)){
			sagaPlayer.message(GeneralMessages.noPermission(selBundle));
			return;
		}

		// Trim amount:
		Double coins = selSettlement.getCoins();
		Integer claims = selSettlement.getTotalClaims();
		Double cost = 0.0;
		for (int claimsMod = 0; claimsMod <= amount; claimsMod++) {
			
			Double costMod = EconomyConfiguration.config().getBuildPointCost(claims + claimsMod + 1);
			if(cost + costMod > coins){
				amount = claimsMod;
				break;
			}
			cost+= costMod;
			
		}
		
		if(amount == 0){
			sagaPlayer.message(EconomyMessages.settlementInsufficientCoins());
			return;
		}
		
		// Take coins:
		selSettlement.modCoins(-cost);
		
		// Add claims:
		selSettlement.modBuildPoints(amount.doubleValue());
		
		// Inform:
		sagaPlayer.message(EconomyMessages.settlementBoughtBuildPoints(amount, cost));
		
		
	}
	
	
	
	// Other:
	@Command(
		aliases = {"ehelp"},
		usage = "[page]",
		flags = "",
		desc = "Display economy help.",
		min = 0,
		max = 1
	)
	@CommandPermissions({"saga.user.help.economy"})
	public static void help(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


		// Disabled:
		if(!EconomyConfiguration.config().isEnabled()){
			sagaPlayer.message(EconomyMessages.economyDisabled());
			return;
		}
		
		Integer page = null;
		
		// Arguments:
		if(args.argsLength() == 1){
			try {
				page = Integer.parseInt(args.getString(0));
			} catch (NumberFormatException e) {
				sagaPlayer.message(GeneralMessages.notNumber(args.getString(0)));
				return;
			}
		}else{
			page = 0;
		}
		
		// Inform:
		sagaPlayer.message(HelpMessages.ehelp(page - 1));

		
	}

	
	
}
