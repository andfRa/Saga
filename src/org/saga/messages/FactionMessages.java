package org.saga.messages;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.saga.commands.FactionCommands;
import org.saga.config.FactionConfiguration;
import org.saga.factions.FactionManager;
import org.saga.factions.SagaFaction;
import org.saga.player.Proficiency;
import org.saga.player.SagaPlayer;
import org.saga.utility.text.TextUtil;


public class FactionMessages {

public static ChatColor positiveHighlightColor = ChatColor.GREEN;
	


	// Colors:
	public static ChatColor veryPositive = ChatColor.DARK_GREEN; // DO NOT OVERUSE.

	public static ChatColor positive = ChatColor.GREEN;

	public static ChatColor negative = ChatColor.RED;

	public static ChatColor veryNegative = ChatColor.DARK_RED; // DO NOT OVERUSE.

	public static ChatColor unavailable = ChatColor.DARK_GRAY;

	public static ChatColor anouncment = ChatColor.AQUA;

	public static ChatColor normal1 = ChatColor.GOLD;

	public static ChatColor normal2 = ChatColor.YELLOW;

	public static ChatColor frame = ChatColor.DARK_GREEN;


	// General restriction:
	public static String noPermission(SagaFaction faction){
		return faction.getSecondaryColor() + "You dont have permission from " + faction(faction, faction.getSecondaryColor()) + " to do that.";
	}

	public static String noFaction() {
		return negative + "You don't have a faction.";
	}
	
	public static String noFaction(String factionName) {
		return negative + factionName + " doesn't exist.";
	}
	
	public static String notFormed(SagaFaction faction) {

		return negative + "The faction isnt formed yet.";
		
	}
	
	public static String notFormedInfo(SagaFaction faction) {

		return normal1 + "The faction requires " + (FactionConfiguration.config().formationAmount-faction.getMemberCount()) + " more members.";
		
	}

	
	// Faction restriction:
	public static String mustSelectOneFaction() {
		return "You must have one faction selected.";
	}

	public static String oneFactionAllowed() {

		return negative + "You can only have one faction.";
		
	}
	
	public static String nonExistentFaction(String factionName) {
		return negative + factionName + " faction doesn't exist.";
	}
	
	public static String nonExistantFaction() {
		return negative + "Faction doesn't exist.";
	}

	public static String alreadyInFaction(SagaPlayer sagaPlayer) {
		return negative + sagaPlayer.getName() + " is already in a faction.";
	}
	
	public static String haveFaction() {
		return negative + "You are already in a faction.";
	}
	
	
	// Specific stats:
	public static String primaryColorSet(SagaFaction faction) {

		return faction.getSecondaryColor() + "Factions primary color set to " + TextUtil.color(faction.getPrimaryColor(), faction.getSecondaryColor());
		
	}
	
	public static String secondaryColorSet(SagaFaction faction) {

		return faction.getSecondaryColor() + "Factions secondary color set to " + TextUtil.color(faction.getSecondaryColor(), faction.getSecondaryColor());
		
	}
	

	// Create delete:
	public static String created(SagaFaction faction) {

		return faction.getSecondaryColor() + "Created " + faction(faction, faction.getSecondaryColor()) + " faction.";
		
	}
	
	public static String deleted(SagaFaction faction) {

		return faction.getSecondaryColor() + "Deleted " + faction(faction, faction.getSecondaryColor()) + " faction.";
		
	}
	
	public static String formed(SagaFaction faction) {

		
		StringBuffer rString = new StringBuffer();
		ArrayList<String> members = faction.getActiveMembers();
		
		for (int i = 0; i < members.size(); i++) {
			
			if(i != 0) rString.append(", ");
			
			rString.append(members.get(i));
			
		}
		
		if(members.size() == 0){
			rString.append("nobody formed");
		}else if(members.size() == 1){
			rString.append(" formed");
		}else{
			rString.append(" formed");
		}
		
		rString.append(" "+faction(faction, anouncment) + " faction.");
		
		
		return anouncment + rString.toString();
		
		
	}

	public static String disbanded(SagaFaction faction) {

		return anouncment + faction(faction, anouncment) + " faction was disbanded.";
		
	}
	

	// Invite join leave messages:
	public static String beenInvited(SagaPlayer sagaPlayer, SagaFaction faction) {
		return faction.getSecondaryColor() + "You have been invited to " + faction.getPrimaryColor() + faction.getName() + faction.getSecondaryColor() + " faction.";
	}
	
	public static String invitedPlayer(SagaPlayer sagaPlayer, SagaFaction faction) {
		return faction.getSecondaryColor() + "" + sagaPlayer.getName() + " was invited to the faction.";
	}
	
	
	public static String joinedFaction(SagaPlayer sagaPlayer, SagaFaction faction) {
		return faction.getSecondaryColor() + "You joined " + faction.getPrimaryColor() + faction.getName() + faction.getSecondaryColor() + " faction.";
	}
	
	public static String playerJoined(SagaPlayer sagaPlayer, SagaFaction faction) {
		return faction.getSecondaryColor() + sagaPlayer.getName() + " has joined the faction.";
	}
	
	
	public static String quitFaction(SagaPlayer sagaPlayer, SagaFaction sagaFaction) {
		return sagaFaction.getSecondaryColor() + "You have quit your faction.";
	}
	
	public static String playerQuit(SagaPlayer sagaPlayer, SagaFaction sagaFaction) {
		return sagaFaction.getSecondaryColor() + sagaPlayer.getName() + " has quit the faction.";
	}

	
	public static String kickedFromFaction(SagaPlayer sagaPlayer, SagaFaction sagaFaction) {
		return sagaFaction.getSecondaryColor() + "You have been kicked out of your faction.";
	}
	
	public static String playerKicked(SagaPlayer sagaPlayer, SagaFaction sagaFaction) {
		return sagaFaction.getSecondaryColor() + sagaPlayer.getName() + " has been kicked from the faction.";
	}
	
	
	public static String cantKickYourself(SagaPlayer sagaPlayer, SagaFaction faction) {
		return negative + "Can't kick yourself from the faction.";
	}
	
	public static String cantKickOwner(SagaFaction faction) {
		return negative + "Can't kick the owner.";
	}
	
	public static String notFactionMember(SagaPlayer sagaPlayer, SagaFaction faction) {
		return negative + sagaPlayer.getName() + " isn't part of the faction.";
	}
	
	public static String notFactionMember(SagaFaction faction) {
		return negative + "You aren't part of the " + faction(faction, negative) + "faction.";
	}
	
	
	// Invite join  leave restrictions:
	public static String noInvites() {
		return negative + "You don't have a faction invitation.";
	}
	
	public static String noInvites(String factionName) {
		return negative + "You don't have an invitation to " + factionName + " faction.";
	}
	
	public static String cantAcceptInvitations() {

		return negative + "You can't accept faction invitations.";
		
	}
	
	public static String declinedInvites() {
		return normal1 + "Declined all faction invitations.";
	}

	public static String pendingInvitations(SagaPlayer sagaPlayer, ArrayList<SagaFaction> factions) {
		
		
		StringBuffer rString = new StringBuffer();
		ChatColor messageColor = positiveHighlightColor;
		
		if(factions.size() == 0){
			return messageColor + "You don't have a pending faction invitation.";
		}
		
		rString.append(messageColor);
		
		rString.append("You have");
		
		if(factions.size() == 1){
			rString.append(" a pending invitation from ");
		}else{
			rString.append(" pending invitations from ");
		}
		
		for (int i = 0; i < factions.size(); i++) {
			if( i != 0 ) rString.append(", ");
			rString.append(factions.get(i).getPrimaryColor() + factions.get(i).getName() + messageColor);
		}
		
		if(factions.size() == 1){
			rString.append(" faction.");
		}else{
			rString.append(" factions.");
		}
		
		return rString.toString();
		
		
	}
	
	public static String cantInviteYourself(SagaPlayer sagaPlayer, SagaFaction faction) {
		return negative + "Yo dawg. I herd you like " + faction.getName() + ". So we invited you to your own faction, so you can be in your faction, while you are in your faction.";
	}

	public static String alreadyOwner() {
		return negative + "You already own the faction.";
	}
	
	public static String newOwner(SagaFaction faction, String name) {
		return faction.getSecondaryColor() + name + " is the new owner of the faction.";
	}
	
	
	// Inform:
	public static String informAccept() {
		return normal1 + "Use /faccept to accept a faction invitation.";
	}
	
	
	// Other:
	public static String invalidName() {
		
		return negative + "Name must be " + FactionCommands.minimumNameLenght + "-" + FactionCommands.maximumNameLength + ". Letters and numbers only.";
		
	}

	public static String inUse(String name) {
		return negative + name + " is already in use.";
	}
	
	public static String invalidColor(String colorName) {
		
		return negative + colorName +" isn't a valid color.";
		
	}
	
	public static String possibleColors(ChatColor[] colors, SagaFaction faction) {
		
		
		StringBuffer rString = new StringBuffer();
		
		if(colors.length == 0){
			rString.append("Possible colors: none");
		}else if(colors.length == 1){
			rString.append("Possible color: ");
		}else{
			rString.append("Possible colors: ");
		}
		
		for (int i = 0; i < colors.length; i++) {
			
			if( i!= 0) rString.append(", ");
			
			rString.append(colors[i].name().toLowerCase().replace("_", " "));
			
		}
		
		
		rString.insert(0, faction.getSecondaryColor());
		
		return rString.toString();
		
		
	}
	
	public static String pvpFactionOnly() {
		
		return negative + FactionConfiguration.config().pvpFactionOnlyMessage;
		
	}
	
	
	// Stats:
	public static String stats(SagaFaction faction) {
		
		
		StringBuffer rString = new StringBuffer();
		ChatColor messageColor = faction.getSecondaryColor();
		
		// Owner:
		ChatColor elementColor = messageColor;
		if(faction.hasOwner()){
			rString.append(elementColor);
			rString.append("Owner: " + faction.getOwner());
		}else{
			rString.append("Owner: "+ veryNegative +"none" + elementColor);
		}
		
		rString.append("\n");
		
		// Colors:
		rString.append("Primary color: " + TextUtil.color(faction.getPrimaryColor(), messageColor));
		rString.append(" ");
		rString.append("Secondary color: " + TextUtil.color(faction.getSecondaryColor(), messageColor));
		
		rString.append("\n");
		
		// Ranks:
		rString.append(ranks(faction, messageColor));
		
		rString.append("\n");
		
		// Allies:
		ArrayList<String> allies = FactionManager.manager().getFactionNames(faction.getAllies());
		rString.append("Allies: ");
		if(allies.size() > 0){
			rString.append(TextUtil.flatten(allies));
		}else{
			rString.append("none");
		}

		// Ally invites:
		ArrayList<String> allyInvites = FactionManager.manager().getFactionNames(faction.getAllyInvites());
		if(allyInvites.size() > 0){
			
			rString.append(" Ally invites: " + TextUtil.flatten(allyInvites));
			
		}
		
		return TextUtil.frame(faction(faction, messageColor) + " stats", rString.toString(), messageColor);
		
		
	}
	
	private static String ranks(SagaFaction faction, ChatColor messageColor) {
		
		
		StringBuffer rString = new StringBuffer();
		HashSet<String> ranks = faction.getRanks();
		
		if(ranks.size() == 0){
			rString.append("Ranks: none");
		}else if(ranks.size() == 0){
			rString.append("Rank: ");
		}else{
			rString.append("Ranks: ");
		}
		
		boolean firstElement = true;
		for (String role : ranks) {
			
			if(!firstElement){
				rString.append(", ");
			}
			firstElement = false;
			
			rString.append(role + " " + faction.getUsedRanks(role) + "/" + faction.getAvailableRanks(role));
			
		}
		
		rString.insert(0, messageColor);
		
		return rString.toString();
		
		
	}
	
	
	// Rename:
	public static String renamedAnnounce(String oldName, String oldPrefix, SagaFaction faction) {

		return anouncment + oldName + "(" + oldPrefix + ")" + " faction was renamed to " + faction(faction, anouncment) + ".";
		
	}
	
	public static String renamed(SagaFaction faction) {

		return faction.getSecondaryColor() + "Faction was renamed to " + faction(faction, faction.getSecondaryColor()) + ".";
		
	}
	
	
	// Spawn:
	public static String noSpawn(SagaFaction faction) {
		
		return negative + "The faction spawn point hasn't been set.";
		
	}
	
	public static String newSpawn(SagaFaction faction) {
		
		return faction.getSecondaryColor() + "New faction spawn point has been set.";
		
	}

	
	// Ally:
	public static String sentAlliance(SagaFaction faction, SagaFaction target) {
		
		return faction.getSecondaryColor() + "An alliance request was sent to " + faction(target, faction.getSecondaryColor()) + " faction.";
		
	}
	
	public static String recievedAlliance(SagaFaction faction, SagaFaction source) {
		
		return faction.getSecondaryColor() + "Recieved an alliance request from " + faction(source, faction.getSecondaryColor()) + " faction.";
		
	}
	
	public static String recievedAllianceInfo(SagaFaction faction, SagaFaction target) {
		
		return normal1 + "Use /fallyaccept to accept and /fallydecline to decline the alliance request.";
		
	}

	public static String declinedAllianceRequest(SagaFaction faction, SagaFaction target) {
		
		return faction.getSecondaryColor() + "Alliance request from " + faction(target, faction.getSecondaryColor()) + " faction was declined.";
		
	}
	
	public static String formedAllianceBroadcast(SagaFaction faction, SagaFaction target) {
		
		return anouncment + "An alliance was formed between " + faction(faction, anouncment) + " and " + faction(target, anouncment) + " factions.";
		
	}
	
	public static String brokeAllianceBroadcast(SagaFaction faction, SagaFaction target) {
		
		return anouncment + "An alliance was broken between " + faction(faction, anouncment) + " and " + faction(target, anouncment) + " factions.";
		
	}

	public static String selfAlliance(SagaFaction faction) {
		
		return negative + "Can't request an alliance from your own faction.";
		
	}
	
	public static String alreadyAlliance(SagaFaction faction, SagaFaction targetFaction) {
		
		return negative + "An alliance with " + faction(targetFaction, negative) + " is already formed.";
		
	}

	public static String noAllianceRequest(SagaFaction faction, String souceFaction) {
		
		return negative + "The faction doesn't have an alliance request from " + souceFaction + " faction.";
		
	}
	
	public static String noAlliance(SagaFaction faction, SagaFaction targetFaction) {
		
		return negative + "No alliance formed with " + faction(targetFaction, negative) + " faction.";
		
	}
	
	public static String noAllianceRequest(SagaFaction faction) {
		
		return negative + "The faction doesn't have alliance requests.";
		
	}

	
	// List:
	public static String list(SagaFaction faction) {
		
		
		StringBuffer rString = new StringBuffer();
		
		// Online total inactive:
		if(faction.getMemberCount() > 0){
			
			if(rString.length() > 0) rString.append("\n");
			
			rString.append(faction.getSecondaryColor() + "Online: " + faction.getRegisteredMemberCount() + "/" + faction.getMemberCount());

			int inactiveCount = faction.getInactiveMemberCount();
			if(inactiveCount != 0){
				rString.append(" Inactive: " + inactiveCount);
			}
			
		}
		
		// Players:
		if(faction.getMemberCount() > 0){
			
			if(rString.length() > 0) rString.append("\n");
			
			rString.append( listPlayersElement(faction, faction.getSecondaryColor()) );

		}
		
		return TextUtil.frame(faction(faction, faction.getSecondaryColor()) + " members", rString.toString(), faction.getSecondaryColor());
		
		
	}
	
	private static String listPlayersElement(SagaFaction faction, ChatColor messageColor){
		

		// Fill roles table:
		Integer maxHierarchyLevel = 0;
		Hashtable<Integer, Hashtable<String, ArrayList<String>>> roleTable = new Hashtable<Integer, Hashtable<String,ArrayList<String>>>();
		ArrayList<String> zeroHighlMembers = new ArrayList<String>(faction.getMembers());
		for (int i = 0; i < zeroHighlMembers.size(); i++) {
			String name = zeroHighlMembers.get(i);
			Proficiency role = faction.getRank(name);
			if(role == null) continue;
			zeroHighlMembers.remove(i);
			i--;
			String roleName = role.getName();
			Integer hierarchyLevel = role.getHierarchy();
			
			// Hierarchy:
			if(hierarchyLevel > maxHierarchyLevel){
				maxHierarchyLevel = hierarchyLevel;
			}
			
			// Hierarchy level:
			Hashtable<String, ArrayList<String>> hierLevelRoles = roleTable.get(hierarchyLevel);
			if(hierLevelRoles == null){
				hierLevelRoles = new Hashtable<String, ArrayList<String>>();
				roleTable.put(hierarchyLevel, hierLevelRoles);
			}

			// Player names:
			ArrayList<String> playerNames = hierLevelRoles.get(roleName);
			if(playerNames == null){
				playerNames = new ArrayList<String>();
				hierLevelRoles.put(roleName, playerNames);
			}
			
			// Add:
			playerNames.add(name);
			
		}
		
		StringBuffer rString = new StringBuffer();
		
		// Add above zero highlight players:
		for (Integer i = maxHierarchyLevel ; i >= 0 ; i--) {
			
			// All roles for hierarchy level:
			Hashtable<String, ArrayList<String>> highlRoles = roleTable.get(i);
			if(highlRoles == null) continue;
			
			if(rString.length() != 0){
				rString.append("\n");
			}
			
			// All roles:
			Enumeration<String> roleNames = highlRoles.keys();
			StringBuffer eString = new StringBuffer();
			while(roleNames.hasMoreElements()){
				
				String roleName = roleNames.nextElement();
				ArrayList<String> playerNames = highlRoles.get(roleName);
				
				// Title:
				if(eString.length() != 0){
					eString.append(" ");
				}
				if(playerNames.size() == 1){
					eString.append(TextUtil.capitalize(roleName) + ": ");
				}else{
					eString.append(TextUtil.capitalize(roleName + "s") + ": ");
				}
				
				// Names:
				for (int j = 0; j < playerNames.size(); j++) {
					if( j != 0 ) eString.append(", ");
					eString.append( playerNameElement(playerNames.get(j), faction, messageColor, faction.isMemberActive(playerNames.get(j))) );
				}
				
			}
			rString.append(eString);
			
		}
		
		// Add zero highlight players:
		if(zeroHighlMembers.size() > 0){
			
			if(rString.length() > 0){
				rString.append("\n");
			}
			
			for (int i = 0; i < zeroHighlMembers.size(); i++) {
				if(i != 0) rString.append(", ");
				rString.append(playerNameElement(zeroHighlMembers.get(i), faction, messageColor, faction.isMemberActive(zeroHighlMembers.get(i))) );
			}
			
		}
		
		return messageColor + rString.toString();
		
		
	}

	private static String playerNameElement(String playerName, SagaFaction faction, ChatColor messageColor, boolean isActive){
		
		
		StringBuffer rString = new StringBuffer();
		ChatColor offlineColor = ChatColor.GRAY;
		ChatColor inactiveColor = ChatColor.DARK_GRAY;
		
		// Name:
		if(!isActive){
			
			rString.append(inactiveColor + playerName + messageColor);
			
		}
		else if(!faction.isRegisteredMember(playerName)){
			
			rString.append(offlineColor + playerName + messageColor);
			
		}
		else{
		
			rString.append(faction.getPrimaryColor() + playerName + messageColor);
			
		}
		
		return rString.toString();
		
		
	}

	
	// Info:
	public static String wrongQuit() {
		
		return negative + "Because /squit and /fquit are similar, this command isn't used. Please use /factionquit instead.";
		
	}
	
	
	// Rank:
	public static String newRank(SagaFaction faction, String rankName, SagaPlayer targetPlayer) {
		
		return faction.getSecondaryColor() + targetPlayer.getName() + " is now a " + rankName + ".";
		
	}
	
	public static String invalidRank(SagaFaction faction, String rankName) {
		
		return negative + rankName + " isn't a valid rank.";
		
	}
	
	public static String rankUnavailable(SagaFaction faction, String rankName) {
		
		return negative + rankName + " rank isn't available.";
		
	}
	
	
	// Utility:
	public static String faction(SagaFaction faction, ChatColor messageColor) {
		
		return faction.getPrimaryColor() + faction.getName() + messageColor;
		
	}
	
	public static String rankedPlayer(SagaFaction faction, SagaPlayer sagaPlayer) {

		Proficiency rank = faction.getRank(sagaPlayer.getName());
		
		if(rank == null){
			return sagaPlayer.getName();
		}else{
			return rank.getName() + " " + sagaPlayer.getName();
		}
		
	}
	
}
