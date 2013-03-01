/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga.commands;

import java.util.ArrayList;

import org.saga.Saga;
import org.saga.config.EconomyConfiguration;
import org.saga.config.FactionConfiguration;
import org.saga.factions.Faction;
import org.saga.factions.Faction.FactionPermission;
import org.saga.factions.FactionManager;
import org.saga.factions.SiegeManager;
import org.saga.factions.WarManager;
import org.saga.messages.EconomyMessages;
import org.saga.messages.FactionMessages;
import org.saga.messages.GeneralMessages;
import org.saga.messages.SettlementMessages;
import org.saga.messages.WarMessages;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Bundle;
import org.saga.settlements.BundleManager;
import org.saga.settlements.Settlement.SettlementPermission;
import org.saga.utility.Duration;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;


public class WarCommands {


	// Siege:
	@Command(
            aliases = {"fsiege"},
            usage = "[faction_name] <settlement_name>",
            flags = "",
            desc = "Declare a siege.",
            min = 1,
            max = 2
		)
	@CommandPermissions({"saga.user.war.siege"})
	public static void siege(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		Faction selFaction = null;
		Bundle selBundle = null;
		
		String bundleName = null;

		// Arguments:
		switch (args.argsLength()) {
			case 2:
				
				// Faction:
				String factionName = GeneralMessages.nameFromArg(args.getString(0));
				selFaction = FactionManager.manager().matchFaction(factionName);
				
				if(selFaction == null){
					sagaPlayer.message(GeneralMessages.invalidFaction(factionName));
					return;
				}

				// Bundle:
				bundleName = GeneralMessages.nameFromArg(args.getString(1));
				selBundle = BundleManager.manager().matchBundle(bundleName);
				
				if(selBundle == null){
					sagaPlayer.message(GeneralMessages.invalidSettlement(bundleName));
					return;
				}
				
				break;

			default:
				
				// Faction:
				selFaction = sagaPlayer.getFaction();
				
				if(selFaction == null){
					sagaPlayer.message(FactionMessages.notMember());
					return;
				}

				// Bundle:
				bundleName = GeneralMessages.nameFromArg(args.getString(0));
				selBundle = BundleManager.manager().matchBundle(bundleName);
				
				if(selBundle == null){
					sagaPlayer.message(GeneralMessages.invalidSettlement(bundleName));
					return;
				}
				
				break;
				
		}
		
		// Permission:
		if(!selFaction.hasPermission(sagaPlayer, FactionPermission.DECLARE_SIEGE)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}

		// Already owned:
		Integer bundleID = selBundle.getId();
		Faction owningFaction = SiegeManager.manager().getOwningFaction(bundleID);
		if(owningFaction == selFaction){
			sagaPlayer.message(WarMessages.alreadyOwned(selFaction, selBundle));
			return;
		}
		
		// Taking over from another:
		if(owningFaction != null){
			
			// Ally:
			if(WarManager.manager().isAlly(selFaction.getId(), owningFaction.getId())){
				selFaction.information(FactionMessages.isAllyDeny(selFaction, owningFaction), sagaPlayer);
				return;
			}
			
			// Not at war:
			if(FactionConfiguration.config().isSiegeWarRequired()){
				
				if(!WarManager.manager().isAtWar(selFaction.getId(), owningFaction.getId())){
					selFaction.information(WarMessages.peaceDeny(selFaction, owningFaction), sagaPlayer);
					return;
				}
				
			}
			
		}
		
		// Not owned:
		else{
			
			// Affiliated settlement:
			Integer affiliationID = SiegeManager.manager().getAffiliationID(selBundle.getId());
			
			if(affiliationID != null && affiliationID.equals(selFaction.getId())){
				
				SiegeManager.manager().handleSiegeAffiliated(selBundle, selFaction);
				return;
				
			}
			
		}
		
		// Already sieged:
		Faction attackingFaction = SiegeManager.manager().getAttackingFaction(bundleID);
		if(attackingFaction != null){
			selFaction.information(WarMessages.siegeAlreadyDeclared(selFaction, selBundle), sagaPlayer);
			return;
		}
		
		// Cost:
		ArrayList<Integer> owned = SiegeManager.manager().getOwnedBundleIDs(selFaction.getId());
		Double cost = EconomyConfiguration.config().getSiegeCost(owned.size());
		if(EconomyConfiguration.config().isEnabled() && cost > 0){
			
			// Check cost:
			if(selFaction.getCoins() < cost){
				selFaction.information(EconomyMessages.insufficient(selFaction), sagaPlayer);
				return;
			}
			
			// Take coins:
			selFaction.modCoins(-cost);
			
			// Inform:
			selFaction.information(EconomyMessages.spent(selFaction, cost), sagaPlayer);
			
		}
		
		// Declare:
		SiegeManager.manager().handleDeclaration(selFaction.getId(), selBundle.getId());
		
		// Inform:
		selFaction.information(WarMessages.siegeDeclared(selFaction, selBundle));
		if(owningFaction != null) owningFaction.information(WarMessages.siegeWasDeclared(owningFaction, selBundle));
		
	}

	@Command(
            aliases = {"fsiegeunclaim"},
            usage = "[faction_name] <settlement_name>",
            flags = "",
            desc = "Unclaims a claimed settlement.",
            min = 1,
            max = 2
		)
	@CommandPermissions({"saga.user.war.siegeunclaim"})
	public static void unsiege(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		Faction selFaction = null;
		Bundle selBundle = null;
		
		String bundleName = null;

		// Arguments:
		switch (args.argsLength()) {
			case 2:
				
				// Faction:
				String factionName = GeneralMessages.nameFromArg(args.getString(0));
				selFaction = FactionManager.manager().matchFaction(factionName);
				
				if(selFaction == null){
					sagaPlayer.message(GeneralMessages.invalidFaction(factionName));
					return;
				}

				// Bundle:
				bundleName = GeneralMessages.nameFromArg(args.getString(1));
				selBundle = BundleManager.manager().matchBundle(bundleName);
				
				if(selBundle == null){
					sagaPlayer.message(GeneralMessages.invalidSettlement(bundleName));
					return;
				}
				
				break;

			default:
				
				// Faction:
				selFaction = sagaPlayer.getFaction();
				
				if(selFaction == null){
					sagaPlayer.message(FactionMessages.notMember());
					return;
				}

				// Bundle:
				bundleName = GeneralMessages.nameFromArg(args.getString(0));
				selBundle = BundleManager.manager().matchBundle(bundleName);
				
				if(selBundle == null){
					sagaPlayer.message(GeneralMessages.invalidSettlement(bundleName));
					return;
				}
				
				break;
				
		}
		
		// Permission:
		if(!selFaction.hasPermission(sagaPlayer, FactionPermission.UNCLAIM)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}

		Integer bundleId = selBundle.getId();
		
		// Not claimed:
		if(!SiegeManager.manager().getOwningFactionID(bundleId).equals(selFaction.getId())){
			sagaPlayer.message(FactionMessages.notClaimed(selFaction, selBundle));
			return;
		}
		
		// Remove owner:
		SiegeManager.manager().handleRemoveOwnerFaction(bundleId);
		
		// Inform:
		selFaction.information(FactionMessages.unclaimed(selFaction, selBundle));
		if(!selFaction.isMember(sagaPlayer.getName())){
			selFaction.information(FactionMessages.unclaimed(selFaction, selBundle), sagaPlayer);
		}
		
		
	}
	
	
	
	// War:
	@Command(
		aliases = {"fdeclarewar"},
		usage = "[faction_name] <target_faction_name>",
		flags = "",
		desc = "Declare war.",
		min = 1,
		max = 2
	)
	@CommandPermissions({"saga.user.war.declarewar"})
	public static void declareWar(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		Faction selFaction = null;
		Faction targetFaction = null;
		
		String targetFactionName = null;

		// Arguments:
		switch (args.argsLength()) {
			case 2:
				
				// Faction:
				String factionName = GeneralMessages.nameFromArg(args.getString(0));
				selFaction = FactionManager.manager().matchFaction(factionName);
				
				if(selFaction == null){
					sagaPlayer.message(GeneralMessages.invalidFaction(factionName));
					return;
				}

				// Target faction:
				targetFactionName = GeneralMessages.nameFromArg(args.getString(1));
				targetFaction = FactionManager.manager().matchFaction(targetFactionName);
				
				if(targetFaction == null){
					sagaPlayer.message(GeneralMessages.invalidFaction(targetFactionName));
					return;
				}
				
				break;

			default:
				
				// Faction:
				selFaction = sagaPlayer.getFaction();
				
				if(selFaction == null){
					sagaPlayer.message(FactionMessages.notMember());
					return;
				}

				// Target faction:
				targetFactionName = GeneralMessages.nameFromArg(args.getString(0));
				targetFaction = FactionManager.manager().matchFaction(targetFactionName);
				
				if(targetFaction == null){
					sagaPlayer.message(GeneralMessages.invalidFaction(targetFactionName));
					return;
				}
				
				break;
				
		}
		
		// IDs:
		Integer selFactionID = selFaction.getId();
		Integer targetFactionID = targetFaction.getId();
		
		// Permission:
		if(!selFaction.hasPermission(sagaPlayer, FactionPermission.START_WAR)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}
		
		// Declaring on self:
		if(selFaction == targetFaction){
			selFaction.information(WarMessages.warCantBeDeclaredOnSelf(selFaction), sagaPlayer);
			return;
		}
		
		// Already at war:
		if(WarManager.manager().isAtWar(selFactionID, targetFactionID)){
			selFaction.information(WarMessages.warAlreadyDeclared(selFaction, targetFaction), sagaPlayer);
			return;
		}
		
		// Ally:
		if(WarManager.manager().isAlly(selFactionID, targetFactionID)){
			selFaction.information(FactionMessages.isAllyDeny(selFaction, targetFaction), sagaPlayer);
			return;
		}
		
		// Declared too soon:
		Long peaceTime = WarManager.manager().getPeaceDeclarationTime(selFactionID, targetFactionID);
		if(peaceTime != null){
			
			long requiredTime = FactionConfiguration.config().getWarDeclareAfterPeaceMinutes() * 60000L;
			long currenTime = System.currentTimeMillis();
			
			if(currenTime - peaceTime < requiredTime){
				Duration duration = new Duration(requiredTime - (currenTime - peaceTime));
				selFaction.information(WarMessages.warDeclareWait(selFaction, targetFaction, duration), sagaPlayer);
				return;
			}
			
		}
		
		// Cost:
		if(EconomyConfiguration.config().isEnabled()){
			Integer ownedSettles = SiegeManager.manager().getOwnedBundleCount(selFactionID);
			Double cost = EconomyConfiguration.config().getWarStartCost(ownedSettles);
			
			if(selFaction.getCoins() < cost){
				selFaction.information(EconomyMessages.insufficient(selFaction), sagaPlayer);
				return;
			}
			
			// Take coins:
			selFaction.modCoins(-cost);
			
			// Inform:
			selFaction.information(EconomyMessages.spent(selFaction, cost), sagaPlayer);
			
		}

		// Declare:
		WarManager.manager().handleDeclareWar(selFactionID, targetFactionID);
		
		// Inform:
		selFaction.information(WarMessages.warDeclaredOn(selFaction, targetFaction));
		targetFaction.information(WarMessages.warDeclaredBy(targetFaction, selFaction));
		
	}
	
	@Command(
		aliases = {"fdeclarepeace"},
		usage = "[faction_name] <target_faction_name>",
		flags = "",
		desc = "Declare peace.",
		min = 1,
		max = 2
	)
	@CommandPermissions({"saga.user.war.declarepeace"})
	public static void declarePeace(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		Faction selFaction = null;
		Faction targetFaction = null;
		
		String targetFactionName = null;

		// Arguments:
		switch (args.argsLength()) {
			case 2:
				
				// Faction:
				String factionName = GeneralMessages.nameFromArg(args.getString(0));
				selFaction = FactionManager.manager().matchFaction(factionName);
				
				if(selFaction == null){
					sagaPlayer.message(GeneralMessages.invalidFaction(factionName));
					return;
				}

				// Target faction:
				targetFactionName = GeneralMessages.nameFromArg(args.getString(1));
				targetFaction = FactionManager.manager().matchFaction(targetFactionName);
				
				if(targetFaction == null){
					sagaPlayer.message(GeneralMessages.invalidFaction(targetFactionName));
					return;
				}
				
				break;

			default:
				
				// Faction:
				selFaction = sagaPlayer.getFaction();
				
				if(selFaction == null){
					sagaPlayer.message(FactionMessages.notMember());
					return;
				}

				// Target faction:
				targetFactionName = GeneralMessages.nameFromArg(args.getString(0));
				targetFaction = FactionManager.manager().matchFaction(targetFactionName);
				
				if(targetFaction == null){
					sagaPlayer.message(GeneralMessages.invalidFaction(targetFactionName));
					return;
				}
				
				break;
				
		}
		
		// IDs:
		Integer selFactionID = selFaction.getId();
		Integer targetFactionID = targetFaction.getId();
		
		// Permission:
		if(!selFaction.hasPermission(sagaPlayer, FactionPermission.END_WAR)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}
		
		// Declaring on self:
		if(selFaction == targetFaction){
			selFaction.information(WarMessages.peaceCantBeDeclaredOnSelf(selFaction), sagaPlayer);
			return;
		}
		
		// Already at peace:
		if(!WarManager.manager().isAtWar(selFactionID, targetFactionID)){
			selFaction.information(WarMessages.peaceDeny(selFaction, targetFaction), sagaPlayer);
			return;
		}
		
		// Cost:
		if(EconomyConfiguration.config().isEnabled()){
			
			Integer ownedSettles = SiegeManager.manager().getOwnedBundleCount(selFactionID);
			Double cost = EconomyConfiguration.config().getWarEndCost(ownedSettles);
			
			if(selFaction.getCoins() < cost){
				selFaction.information(EconomyMessages.insufficient(selFaction), sagaPlayer);
				return;
			}
			
			// Take coins:
			selFaction.modCoins(-cost);
			
			// Inform:
			selFaction.information(EconomyMessages.spent(selFaction, cost), sagaPlayer);
			
		}

		// Declare:
		WarManager.manager().handleDeclarePeace(selFactionID, targetFactionID);
		
		// Inform:
		selFaction.information(WarMessages.peaceDeclaredOn(selFaction, targetFaction));
		targetFaction.information(WarMessages.peaceDeclaredBy(targetFaction, selFaction));
		
	}
	
	
	
	// Alliance:
	@Command(
			aliases = {"frequestally"},
			usage = "[faction_name] <other_faction_name>",
			flags = "",
			desc = "Request an alliance.",
			min = 1,
			max = 2
	)
	@CommandPermissions({"saga.user.war.alliance.request"})
	public static void allianceRequest(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	    	
		
		Faction selFaction = null;
		Faction targetFaction = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			String selName = GeneralMessages.nameFromArg(args.getString(0));
			selFaction = FactionManager.manager().matchFaction(selName);
			
			if(selFaction == null){
				sagaPlayer.message(GeneralMessages.invalidFaction(selName));
				return;
			}
			
			String targetName = GeneralMessages.nameFromArg(args.getString(1));
			targetFaction = FactionManager.manager().matchFaction(targetName);
			
			if(targetFaction == null){
				sagaPlayer.message(GeneralMessages.invalidFaction(targetName));
				return;
			}
			
		}else{
			 
			selFaction = sagaPlayer.getFaction();

			if(selFaction == null){
				sagaPlayer.message(FactionMessages.notMember());
				return;
			}
			
			String targetName = GeneralMessages.nameFromArg(args.getString(0));
			targetFaction = FactionManager.manager().matchFaction(targetName);
			
			if(targetFaction == null){
				sagaPlayer.message(GeneralMessages.invalidFaction(targetName));
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
		if(!selFaction.hasPermission(sagaPlayer, FactionPermission.FORM_ALLIANCE)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}

		// At war:
		if(WarManager.manager().isAtWar(selFaction.getId(), targetFaction.getId())){
			selFaction.information(WarMessages.isAtWarDeny(selFaction, targetFaction), sagaPlayer);
			return;
		}
		
		// Self invite:
		if(selFaction == targetFaction){
			sagaPlayer.message(FactionMessages.selfAlliance(selFaction));
			return;
		}
		
		// Already an ally:
		if(WarManager.manager().isAlly(selFaction.getId(), targetFaction.getId())){
			sagaPlayer.message(FactionMessages.alreadyAlliance(selFaction, targetFaction));
			return;
		}
		
		// Send request:
		targetFaction.addAllianceRequest(selFaction.getId());
	    	
		// Inform:
		selFaction.information(FactionMessages.sentAlliance(selFaction, targetFaction));
		targetFaction.information(FactionMessages.recievedAlliance(targetFaction, selFaction));

		
	}

	@Command(
			aliases = {"facceptally"},
			usage = "[faction_name] [other_faction_name]",
			flags = "",
			desc = "Accept an alliance.",
			min = 0,
			max = 2
	)
	@CommandPermissions({"saga.user.war.alliance.accept"})
	public static void allianceAccept(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	    	
		
		Faction selFaction = null;
		Faction targetFaction = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			String selName = GeneralMessages.nameFromArg(args.getString(0));
			selFaction = FactionManager.manager().matchFaction(selName);
			
			if(selFaction == null){
				sagaPlayer.message(GeneralMessages.invalidFaction(selName));
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
		if(!selFaction.hasPermission(sagaPlayer, FactionPermission.FORM_ALLIANCE)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}

		// At war:
		if(WarManager.manager().isAtWar(selFaction.getId(), targetFaction.getId())){
			selFaction.information(WarMessages.isAtWarDeny(selFaction, targetFaction), sagaPlayer);
			return;
		}
		
		// Remove request:
		selFaction.removeAllianceRequest(targetFaction.getId());
		targetFaction.removeAllianceRequest(selFaction.getId());
		
		// Add allies:
		WarManager.manager().setAlliance(selFaction.getId(), targetFaction.getId());
		
		// Inform:
		selFaction.information(FactionMessages.formedAlliance(selFaction, targetFaction));
		targetFaction.information(FactionMessages.formedAlliance(targetFaction, selFaction));

		
	}

	@Command(
			aliases = {"fdeclineally"},
			usage = "[faction_name] [other_faction_name]",
			flags = "",
			desc = "Decline an alliance.",
			min = 0,
			max = 2
	)
	@CommandPermissions({"saga.user.war.alliance.decline"})
	public static void allianceDecline(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	    	
		
		Faction selFaction = null;
		Faction targetFaction = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			String selName = GeneralMessages.nameFromArg(args.getString(0));
			selFaction = FactionManager.manager().matchFaction(selName);
			
			if(selFaction == null){
				sagaPlayer.message(GeneralMessages.invalidFaction(selName));
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
		if(!selFaction.hasPermission(sagaPlayer, FactionPermission.DECLINE_ALLIANCE)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}
		
		// Remove request:
		selFaction.removeAllianceRequest(targetFaction.getId());
		
		// Inform:
		selFaction.information(FactionMessages.declinedAllianceRequest(selFaction, targetFaction));

		
	}

	@Command(
			aliases = {"fremoveally"},
			usage = "[faction_name] <other_faction_name>",
			flags = "",
			desc = "Break an alliance.",
			min = 1,
			max = 2
	)
	@CommandPermissions({"saga.user.war.alliance.remove"})
	public static void breakAlliance(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	    	
		
		Faction selFaction = null;
		Faction targetFaction = null;
		
		// Arguments:
		if(args.argsLength() == 2){
			
			String selName = GeneralMessages.nameFromArg(args.getString(0));
			selFaction = FactionManager.manager().matchFaction(selName);
			
			if(selFaction == null){
				sagaPlayer.message(GeneralMessages.invalidFaction(selName));
				return;
			}
			
			String targetName = GeneralMessages.nameFromArg(args.getString(1));
			targetFaction = FactionManager.manager().matchFaction(targetName);
			
			if(targetFaction == null){
				sagaPlayer.message(GeneralMessages.invalidFaction(targetName));
				return;
			}
			
		}else{
			 
			selFaction = sagaPlayer.getFaction();

			if(selFaction == null){
				sagaPlayer.message(FactionMessages.notMember());
				return;
			}
			
			String targetName = GeneralMessages.nameFromArg(args.getString(0));
			targetFaction = FactionManager.manager().matchFaction(targetName);
			
			if(targetFaction == null){
				sagaPlayer.message(GeneralMessages.invalidFaction(targetName));
				return;
			}
			
		}

		// Permission:
		if(!selFaction.hasPermission(sagaPlayer, FactionPermission.BREAK_ALLIANCE)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}
		
		// No alliance:
		if(!WarManager.manager().isAlly(selFaction.getId(), targetFaction.getId())){
			sagaPlayer.message(FactionMessages.noAlliance(selFaction, targetFaction));
			return;
		}
		
		// Remove alliance:
		WarManager.manager().removeAlliance(selFaction.getId(), targetFaction.getId());
		
		// Inform:
		selFaction.information(FactionMessages.brokeAlliance(selFaction, targetFaction));
		targetFaction.information(FactionMessages.brokeAlliance(targetFaction, selFaction));

		
	}
	
	
	// Settlement affiliation:
	@Command(
		aliases = {"ssetaffiliation"},
		usage = "[settlement_name] <faction_name>",
		flags = "",
		desc = "Set settlement affiliation.",
		min = 0,
		max = 2
	)
	@CommandPermissions({"saga.user.war.setaffiliation"})
	public static void setAffiliation(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		Bundle selBundle = null;
		Faction selFaction = null;
		
		String strFaction = null;
		String strBundle = null;

		// Arguments:
		switch (args.argsLength()) {
			case 2:
				
				// Bundle:
				strBundle = GeneralMessages.nameFromArg(args.getString(0));
				selBundle = BundleManager.manager().matchBundle(strBundle);
				
				if(selBundle == null){
					sagaPlayer.message(GeneralMessages.invalidSettlement(strBundle));
					return;
				}

				// Faction:
				strFaction = GeneralMessages.nameFromArg(args.getString(1));
				selFaction = FactionManager.manager().matchFaction(strFaction);

				if(selFaction == null){
					sagaPlayer.message(GeneralMessages.invalidFaction(strFaction));
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
				
				// Faction:
				strFaction = GeneralMessages.nameFromArg(args.getString(0));
				selFaction = FactionManager.manager().matchFaction(strFaction);

				if(selFaction == null){
					sagaPlayer.message(GeneralMessages.invalidFaction(strFaction));
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
				
				// Faction:
				selFaction = sagaPlayer.getFaction();
				
				if(selFaction == null){
					sagaPlayer.message(FactionMessages.notMember());
					return;
				}
				
				break;
				
		}
		
		// Permission:
		if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.SET_AFFILIATION)){
			sagaPlayer.message(GeneralMessages.noPermission());
			return;
		}
		
		// Faction and bundle IDs:
		Integer bundleID = selBundle.getId();
		Integer factionID = selFaction.getId();
		
		// Already affiliated:
		Integer affiliationID = SiegeManager.manager().getAffiliationID(bundleID);
		if(affiliationID != null && affiliationID.equals(factionID)){
			sagaPlayer.message(WarMessages.affiliationAlreadySet(selBundle, selFaction));
			return;
		}
		
		// Set affiliation:
		SiegeManager.manager().setAffiliation(bundleID, factionID);
		
		// Inform:
		selBundle.information(WarMessages.affiliationSet(selBundle, selFaction));
		
		
	}

	@Command(
		aliases = {"sremoveaffiliation"},
		usage = "[settlement_name]",
		flags = "",
		desc = "Remove settlement affiliation.",
		min = 0,
		max = 1
	)
	@CommandPermissions({"saga.user.war.removeaffiliation"})
	public static void removeAffiliation(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		Bundle selBundle = null;
		
		String strBundle = null;

		// Arguments:
		switch (args.argsLength()) {
			case 1:
				
				// Bundle:
				strBundle = GeneralMessages.nameFromArg(args.getString(0));
				selBundle = BundleManager.manager().matchBundle(strBundle);
				
				if(selBundle == null){
					sagaPlayer.message(GeneralMessages.invalidSettlement(strBundle));
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
				
				break;
				
		}
		
		// Permission:
		if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.REMOVE_AFFILIATION)){
			sagaPlayer.message(GeneralMessages.noPermission());
			return;
		}
		
		// Bundle ID:
		Integer bundleID = selBundle.getId();
		
		// Not affiliated:
		Integer affiliationID = SiegeManager.manager().getAffiliationID(bundleID);
		if(affiliationID == null){
			sagaPlayer.message(WarMessages.affiliationNotSet(selBundle));
			return;
		}
		
		// Remove affiliation:
		SiegeManager.manager().removeAffiliation(bundleID);
		
		// Inform:
		selBundle.information(WarMessages.affiliationRemoved(selBundle));
		
		
	}
	
	

	// Spawn:
	@Command(
		aliases = {"fsetcapital"},
		usage = "[faction_name] <settlement_name>",
		flags = "",
		desc = "Set faction capital settlement.",
		min = 0,
		max = 2
	)
	@CommandPermissions({"saga.user.war.setcapital"})
	public static void setCapital(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		Bundle selBundle = null;
		Faction selFaction = null;
		
		String strFaction = null;
		String strBundle = null;

		// Arguments:
		switch (args.argsLength()) {
			case 2:

				// Faction:
				strFaction = GeneralMessages.nameFromArg(args.getString(0));
				selFaction = FactionManager.manager().matchFaction(strFaction);

				if(selFaction == null){
					sagaPlayer.message(GeneralMessages.invalidFaction(strFaction));
					return;
				}
				
				// Bundle:
				strBundle = GeneralMessages.nameFromArg(args.getString(1));
				selBundle = BundleManager.manager().matchBundle(strBundle);
				
				if(selBundle == null){
					sagaPlayer.message(GeneralMessages.invalidSettlement(strBundle));
					return;
				}
				
				break;

			case 1:

				// Faction:
				selFaction = sagaPlayer.getFaction();
				
				if(selFaction == null){
					sagaPlayer.message(FactionMessages.notMember());
					return;
				}

				// Bundle:
				strBundle = GeneralMessages.nameFromArg(args.getString(0));
				selBundle = BundleManager.manager().matchBundle(strBundle);
				
				if(selBundle == null){
					sagaPlayer.message(GeneralMessages.invalidSettlement(strBundle));
					return;
				}
				
				break;
				
			default:

				// Faction:
				selFaction = sagaPlayer.getFaction();
				
				if(selFaction == null){
					sagaPlayer.message(FactionMessages.notMember());
					return;
				}

				// Bundle:
				selBundle = sagaPlayer.getBundle();
				
				if(selBundle == null){
					sagaPlayer.message(SettlementMessages.notMember());
					return;
				}
				
				break;
				
		}
		
		// Permission:
		if(!selFaction.hasPermission(sagaPlayer, FactionPermission.SET_CAPITAL)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}
		
		// Owned:
		Integer ownerID = SiegeManager.manager().getOwningFactionID(selBundle.getId());
		if(ownerID == null || !selFaction.getId().equals(ownerID)){
			sagaPlayer.message(WarMessages.notOwned(selFaction, selBundle));
			return;
		}
		
		// Already set:
		Integer capitalID = SiegeManager.manager().getCapitalID(selFaction.getId());
		if(capitalID != null && capitalID.equals(selBundle.getId())){
			sagaPlayer.message(WarMessages.capitalAlreadySet(selFaction, selBundle));
			return;
		}
		
		// Cost:
		Double cost = EconomyConfiguration.config().getCapitalSetCost();
		if(EconomyConfiguration.config().isEnabled() && cost > 0){
			
			// Check cost:
			if(selFaction.getCoins() < cost){
				selFaction.information(EconomyMessages.insufficient(selFaction), sagaPlayer);
				return;
			}
			
			// Take coins:
			selFaction.modCoins(-cost);
			
			// Inform:
			selFaction.information(EconomyMessages.spent(selFaction, cost), sagaPlayer);
			
		}
		
		// Set capital:
		SiegeManager.manager().setCapitalID(selFaction.getId(), selBundle.getId());
		
		// Inform:
		selFaction.information(WarMessages.capitalSet(selFaction, selBundle));
		
		
	}
	
	@Command(
		aliases = {"fremovecapital"},
		usage = "[faction_name] <settlement_name>",
		flags = "",
		desc = "Remove faction capital settlement.",
		min = 0,
		max = 2
	)
	@CommandPermissions({"saga.user.war.removecapital"})
	public static void removeCapital(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		Bundle selBundle = null;
		Faction selFaction = null;
		
		String strFaction = null;
		String strBundle = null;

		// Arguments:
		switch (args.argsLength()) {
			case 2:

				// Faction:
				strFaction = GeneralMessages.nameFromArg(args.getString(0));
				selFaction = FactionManager.manager().matchFaction(strFaction);

				if(selFaction == null){
					sagaPlayer.message(GeneralMessages.invalidFaction(strFaction));
					return;
				}
				
				// Bundle:
				strBundle = GeneralMessages.nameFromArg(args.getString(1));
				selBundle = BundleManager.manager().matchBundle(strBundle);
				
				if(selBundle == null){
					sagaPlayer.message(GeneralMessages.invalidSettlement(strBundle));
					return;
				}
				
				break;

			case 1:

				// Faction:
				selFaction = sagaPlayer.getFaction();
				
				if(selFaction == null){
					sagaPlayer.message(FactionMessages.notMember());
					return;
				}

				// Bundle:
				strBundle = GeneralMessages.nameFromArg(args.getString(0));
				selBundle = BundleManager.manager().matchBundle(strBundle);
				
				if(selBundle == null){
					sagaPlayer.message(GeneralMessages.invalidSettlement(strBundle));
					return;
				}
				
				break;
				
			default:

				// Faction:
				selFaction = sagaPlayer.getFaction();
				
				if(selFaction == null){
					sagaPlayer.message(FactionMessages.notMember());
					return;
				}

				// Bundle:
				selBundle = sagaPlayer.getBundle();
				
				if(selBundle == null){
					sagaPlayer.message(SettlementMessages.notMember());
					return;
				}
				
				break;
				
		}
		
		// Permission:
		if(!selFaction.hasPermission(sagaPlayer, FactionPermission.REMOVE_CAPITAL)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}
		
//		// Owned:
//		Integer ownerID = SiegeManager.manager().getOwningFactionID(selBundle.getId());
//		if(ownerID == null || !selFaction.getId().equals(ownerID)){
//			sagaPlayer.message(WarMessages.notOwned(selFaction, selBundle));
//			return;
//		}

		// Not set:
		Integer capitalID = SiegeManager.manager().getCapitalID(selFaction.getId());
		if(capitalID == null){
			sagaPlayer.message(WarMessages.capitalNotSet(selFaction));
			return;
		}
		
		// Remove capital:
		SiegeManager.manager().removeCapitalID(selFaction.getId());
		
		// Inform:
		selFaction.information(WarMessages.capitalRemoved(selFaction));
		
		
	}


	
	
	
	@Command(
		aliases = {"fspawn"},
		usage = "[faction_name]",
		flags = "",
		desc = "Teleport to the faction capital spawn point.",
		min = 0,
		max = 1
	)
	@CommandPermissions({"saga.user.war.factionspawn"})
	public static void spawn(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		// Part of a faction:
		Faction selFaction = sagaPlayer.getFaction();
		if(selFaction == null){
			sagaPlayer.message(FactionMessages.notMember());
			return;
		}

		// Arguments:
		 if(args.argsLength() == 1){
			
			selFaction = FactionManager.manager().matchFaction(args.getString(0));
			
			if(selFaction == null){
				sagaPlayer.message(GeneralMessages.invalidFaction(args.getString(0)));
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
		if(!selFaction.hasPermission(sagaPlayer, FactionPermission.SPAWN)){
			sagaPlayer.message(GeneralMessages.noPermission(selFaction));
			return;
		}
		
		// Capital:
		Bundle capital = SiegeManager.manager().getCapital(selFaction.getId());
		
		if(capital == null){
			
			sagaPlayer.message(WarMessages.capitalNotSet(selFaction));
			return;
			
		}
		
		// Handle spawn:
		BuildingCommands.handleSpawn(sagaPlayer, capital);
		
		
	}
	
	
	
}
