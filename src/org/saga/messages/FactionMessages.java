package org.saga.messages;

import org.bukkit.ChatColor;
import org.saga.config.FactionConfiguration;
import org.saga.factions.Faction;
import org.saga.factions.SiegeManager;
import org.saga.messages.colours.Colour;
import org.saga.player.Proficiency;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Bundle;
import org.saga.utility.Duration;
import org.saga.utility.chat.ChatUtil;


public class FactionMessages {

	
	
	// Create/delete:
	public static String created(Faction faction) {

		return Colour.normal1 + "Created " + faction(faction, faction.getColour2()) + " faction.";
		
	}

	public static String formed(Faction faction) {

		return faction.getColour2() + "The faction is now fully formed.";
		
	}

	public static String unformed(Faction faction) {
		return faction.getColour2() + "The faction is no longer formed.";
	}

	public static String disbanded(Faction faction) {
		return faction.getColour2() + "The faction was disbanded.";
	}
	
	public static String disbandedOther(Faction faction) {
		return faction.getColour2() + "Disbanded " + faction(faction, faction.getColour2()) + " faction.";
	}

	
	
	// Naming:
	public static String invalidName() {
		return Colour.negative + "Name must be " + FactionConfiguration.config().getMinNameLength() + "-" + FactionConfiguration.config().getMaxNameLength() + " characters long and only contain letters and numbers.";
	}

	public static String inUse(String name) {
		return Colour.negative + "Faction name " + name + " is already in use.";
	}
	
	public static String renamed(Faction faction) {

		return faction.getColour2() + "Faction was renamed to " + faction(faction, faction.getColour2()) + ".";
		
	}

	

	// Members:
	public static String notMember() {
		return Colour.negative + "You aren't a faction member.";
	}
	
	public static String notMember(Faction faction, String selName){
		return Colour.negative + "Player " + selName + " isn't a member of the faction.";
	}
	
	public static String notMember(SagaPlayer selPlayer) {
		return Colour.negative + "Player " + selPlayer.getName() + " isn't a member of the faction.";
	}

	public static String alreadyInFaction() {
		return Colour.negative + "You are already in a faction.";
	}
	
	public static String alreadyInFaction(SagaPlayer selPlayer) {
		return Colour.negative + "Player " + selPlayer.getName() + " is already in a faction.";
	}

	
	
	// Invite join leave:
	public static String noInvites() {
		return Colour.negative + "You don't have any faction invitations.";
	}
	
	public static String noInvite(String factionName) {
		return Colour.negative + "You don't have an invitation to " + factionName + " faction.";
	}
	
	public static String declinedInvite(Faction faction) {
		return Colour.normal1 + "Declined a join invitation from " + faction(faction, Colour.normal1) + " faction.";
	}
	
	public static String declinedInvites() {
		return Colour.normal1 + "Declined all faction join invitations.";
	}

	public static String informInvited() {
		return Colour.normal1 + "Use /faccept to accept the faction join invitation.";
	}

	public static String cantKickYourself(SagaPlayer sagaPlayer, Faction faction) {
		return Colour.negative + "Can't kick yourself.";
	}

	
	
	// Invite join leave broadcasts:
	public static String wasInvited(SagaPlayer sagaPlayer, Faction faction) {
		return faction.getColour2() + "You were invited to " + faction.getColour1() + faction.getName() + faction.getColour2() + " faction.";
	}
	
	public static String invited(SagaPlayer sagaPlayer, Faction faction) {
		return faction.getColour2() + "" + sagaPlayer.getName() + " was invited to the faction.";
	}
	
	
	public static String haveJoined(SagaPlayer sagaPlayer, Faction faction) {
		return faction.getColour2() + "Joined " + faction.getColour1() + faction.getName() + faction.getColour2() + " faction.";
	}
	
	public static String joined(SagaPlayer sagaPlayer, Faction faction) {
		return faction.getColour2() + "" + sagaPlayer.getName() + " joined the faction.";
	}
	
	
	public static String haveQuit(SagaPlayer sagaPlayer, Faction faction) {
		return faction.getColour2() + "Quit " + faction(faction, faction.getColour2()) + " faction.";
	}
	
	public static String quit(SagaPlayer sagaPlayer, Faction faction) {
		return faction.getColour2() + "" + sagaPlayer.getName() + " quit the faction.";
	}

	
	public static String wasKicked(SagaPlayer sagaPlayer, Faction faction) {
		return faction.getColour2() + "You were kicked from the faction.";
	}
	
	public static String playerKicked(SagaPlayer sagaPlayer, Faction faction) {
		return faction.getColour2() + "" + sagaPlayer.getName() + " was kicked from the faction.";
	}
	
	
	
	// Owner:
	public static String newOwner(Faction faction, String name) {
		return faction.getColour2() + "Player " + name + " is the new owner of the faction.";
	}

	public static String ownerCantQuit() {
		return Colour.negative + "Faction owner can't quit the faction.";
	}
	
	public static String ownerCantQuitInfo() {
		return Colour.normal1 + "Use /fresign to declare someone else as the owner.";
	}

	public static String cantKickOwner(Faction faction) {
		return Colour.negative + "Can't kick the owner.";
	}
	
	
	
	// Formation
	public static String notFormed(Faction faction) {
		return Colour.negative + "The faction isn't formed yet.";
	}
	
	public static String notFormedInfo(Faction faction) {

		return Colour.normal1 + "The faction requires " + (FactionConfiguration.config().formationAmount-faction.getMemberCount()) + " more members.";
		
	}

	
	
	// Colours:
	public static String colour1Set(Faction faction) {

		return faction.getColour2() + "Factions colour I set to " + faction.getColour1() + ChatUtil.colour(faction.getColour1()) + faction.getColour2() + ".";
		
	}
	
	public static String colour2Set(Faction faction) {

		return faction.getColour2() + "Factions colour II set to " + faction.getColour2() + ChatUtil.colour(faction.getColour2()) + faction.getColour2() + ".";
		
	}
	
	public static String invalidColor(String colourName) {
		return Colour.negative + "Colour " + colourName + " isn't a valid colour.";
	}
	
	public static String possibleColors(ChatColor[] colours, Faction faction) {
		
		StringBuffer rString = new StringBuffer();
		
		if(colours.length == 0){
			rString.append("Possible colours: none");
		}else if(colours.length == 1){
			rString.append("Possible colour: ");
		}else{
			rString.append("Possible colours: ");
		}
		
		for (int i = 0; i < colours.length; i++) {
			
			if( i!= 0) rString.append(", ");
			
			rString.append(colours[i].name().toLowerCase().replace("_", " "));
			
		}
		
		rString.insert(0, faction.getColour2());
		
		return rString.toString();
		
	}
	

	
	// Spawn:
	public static String noSpawn(Faction faction) {
		
		return Colour.negative + "Faction spawn point isn't set.";
		
	}
	
	public static String newSpawn(Faction faction) {
		
		return faction.getColour2() + "New faction spawn point was set.";
		
	}

	
	
	// Ally:
	public static String sentAlliance(Faction faction, Faction target) {
		
		return faction.getColour2() + "An alliance request was sent to " + faction(target, faction.getColour2()) + " faction.";
		
	}
	
	public static String recievedAlliance(Faction faction, Faction source) {
		
		return faction.getColour2() + "Recieved an alliance request from " + faction(source, faction.getColour2()) + " faction.";
		
	}
	
	public static String recievedAllianceInfo(Faction faction, Faction target) {
		
		return Colour.normal1 + "Use /fallyaccept to accept and /fallydecline to decline the alliance request.";
		
	}

	public static String declinedAllianceRequest(Faction faction, Faction target) {
		
		return faction.getColour2() + "Alliance request from " + faction(target, faction.getColour2()) + " faction was declined.";
		
	}
	
	public static String formedAlliance(Faction faction, Faction target) {
		
		return faction.getColour2() + "An alliance was formed with " + faction(target, faction.getColour2()) + " faction.";
		
	}
	
	public static String brokeAlliance(Faction faction, Faction target) {
		
		return faction.getColour2() + "The alliance with " + faction(target, faction.getColour2()) + " faction was broken.";
		
	}

	public static String selfAlliance(Faction faction) {
		
		return Colour.negative + "Can't request an alliance from your own faction.";
		
	}
	
	public static String alreadyAlliance(Faction faction, Faction targetFaction) {
		
		return Colour.negative + "An alliance with " + faction(targetFaction, Colour.negative) + " is already formed.";
		
	}

	public static String noAllianceRequest(Faction faction, String souceFaction) {
		
		return Colour.negative + "The faction doesn't have an alliance request from " + souceFaction + " faction.";
		
	}
	
	public static String noAlliance(Faction faction, Faction targetFaction) {
		
		return Colour.negative + "No alliance formed with " + faction(targetFaction, Colour.negative) + " faction.";
		
	}
	
	public static String noAllianceRequest(Faction faction) {
		
		return Colour.negative + "The faction doesn't have alliance requests.";
		
	}

	
	
	// Rank:
	public static String newRank(Faction faction, String rankName, SagaPlayer targetPlayer) {
		
		return faction.getColour2() + "Rank " + rankName + " was assigned to " + targetPlayer.getName() + ".";
		
	}
	
	public static String invalidRank(String rankName) {
		
		return Colour.negative + "Rank " + rankName + " is invalid.";
		
	}
	
	public static String rankUnavailable(Faction faction, String rankName) {
		
		return Colour.negative + "Rank " + rankName + " isn't available.";
		
	}
	
	
	
	// Claiming:
	public static String notClaimed(Faction faction, Bundle bundle) {
		return Colour.negative + "Settlement " + bundle.getName() + " is not claimed by the faction.";
	}
	
	public static String unclaimed(Faction faction, Bundle bundle) {
		return faction.getColour2() + "Settlement " + bundle.getName() + " was unclaimed.";
	}
	
	

	// Info:
	public static String wrongQuit() {
		
		return Colour.negative + "Because /squit and /fquit are similar, this command isn't used. Please use /factionquit instead.";
		
	}
	
	
	
	// Sieges:
	public static String siegeDeclared(Faction faction, Bundle bundle) {
		return faction.getColour2() + "Siege declared on " + bundle.getName() + ".";
	}
	
	public static String siegeWasDeclared(Faction faction, Bundle bundle) {
		return faction.getColour2() + "Siege was declared on " + bundle.getName() + ".";
	}
	
	public static String siegeAlreadyDeclared(Faction faction, Bundle bundle) {
		return Colour.negative + "Siege already declared on " + bundle.getName() + ".";
	}
	
	
	public static String siegeAttackSuccess(Faction faction, Bundle bundle) {
		return faction.getColour2() + "Successfully sieged " + bundle.getName() + " settlement.";
	}
	
	public static String siegeAttackFailure(Faction faction, Bundle bundle) {
		return faction.getColour2() + "Failed to siege " + bundle.getName() + " settlement.";
	}
	
	public static String siegeDefendSuccess(Faction defender, Bundle bundle) {
		return defender.getColour2() + "Successfully defended " + bundle.getName() + " settlement.";
	}
	
	public static String siegeDefendFailure(Faction defender, Bundle bundle) {
		return defender.getColour2() + "Failed to defend " + bundle.getName() + " settlement.";
	}
	
	
	public static String alreadyOwned(Faction faction, Bundle bundle) {
		return Colour.negative + "The faction already owns " + bundle.getName() + ".";
	}
	
	
	
	// Siege reminders:
	public static String siegeAttackReminder(Faction faction, Bundle bundle) {
		
		Integer minutes = SiegeManager.manager().getSiegeRemainingMinutes(bundle.getId());
		
		if(minutes > 0){
			
			Duration durationHM = new Duration(minutes*60000);
			return faction.getColour2() + "Siege on " + bundle.getName() + " settlement starts in " + GeneralMessages.durationHM(durationHM) + ".";
		
		}else{
			
			return faction.getColour2() + "Attack " + bundle.getName() + " settlement!";
			
		}
		
	}
	
	public static String siegeDefendReminder(Faction faction, Bundle bundle) {
		
		Integer minutes = SiegeManager.manager().getSiegeRemainingMinutes(bundle.getId());
		
		if(minutes > 0){
			
			Duration durationHM = new Duration(minutes*60000);
			return faction.getColour2() + "Defence of " + bundle.getName() + " settlement starts in " + GeneralMessages.durationHM(durationHM) + ".";
		
		}else{
			
			return faction.getColour2() + "Defend " + bundle.getName() + " settlement!";
			
		}
		
	}
	
	
	
	
	// Utility:
	public static String faction(Faction faction, ChatColor colour) {
		
		return faction.getColour1() + faction.getName() + colour;
		
	}
	
	public static String faction(ChatColor style, Faction faction, ChatColor colour) {
		
		return faction.getColour1().toString() + style + faction.getName() + colour + style;
		
	}

	public static String rankedPlayer(Faction faction, SagaPlayer sagaPlayer) {

		Proficiency rank = faction.getRank(sagaPlayer.getName());
		
		if(rank == null){
			return sagaPlayer.getName();
		}else{
			return rank.getName() + " " + sagaPlayer.getName();
		}
		
	}
	
	
}
