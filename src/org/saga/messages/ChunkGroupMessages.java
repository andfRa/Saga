package org.saga.messages;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.saga.buildings.Building;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.SagaChunk;
import org.saga.commands.ChunkGroupCommands;
import org.saga.config.ChunkGroupConfiguration;
import org.saga.factions.SagaFaction;
import org.saga.listeners.events.SagaBuildEvent.BuildOverride;
import org.saga.messages.PlayerMessages.ColorCircle;
import org.saga.player.Proficiency;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Settlement;
import org.saga.utility.TextUtil;


public class ChunkGroupMessages {

	

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
	public static String missingDefinition(String buildingName){
		return veryNegative + "" + buildingName + " building isn't fully defined.";
	}
	
	public static String savingDisabledError(ChunkGroup chunkGroup){
		return veryNegative + "Saving is disabled for " + chunkGroup.getName() + " settlement.";
	}
	
	public static String proficiencyNotAvailable2(String proficiencyName){
		return negative + "No " + proficiencyName + " roles are available.";
	}

	public static String noChunkGroup(){
		return negative + "You aren't a part of settlement.";
	}
	
	public static String noChunkGroup(String name){
		return negative + name + " settlement doesen't exist.";
	}
	
	public static String notSettlement(ChunkGroup group){
		return negative + group.getName() + " isn't a settlement.";
	}
	
	public static String notChunkGroupMember(ChunkGroup chunkGroup){
		return negative + "You aren't a part of " + chunkGroup.getName() + " settlement.";
	}
	
	public static String isChunkGroupMember(ArrayList<String> names){
		
		
		String rString = "";
		
		for (int i = 0; i < names.size(); i++) {
			
			if(i != 0) rString += ", ";
			
			rString += names.get(i);
			
		}
		
		if(names.size() == 0){
			return negative + "Player is part of the settlement.";
		}else if(names.size() == 1){
			return negative + rString + " is part of the settlement.";
		}else{
			return negative + rString + " are part of the settlement.";
		}
		
		
	}
	
	public static String notChunkGroupMember(ChunkGroup chunkGroup, String playerName){
		return negative + playerName + " isn't part of the settlement.";
	}

	public static String oneOwner() {
		return negative + "There can only be one owner.";
	}
	
	public static String alreadyOwner() {
		return negative + "You already own the settlement.";
	}
	
	public static String alreadyOwner(SagaPlayer sagaPlayer) {
		return negative + sagaPlayer.getName() + " already owns the settlement.";
	}
	
	public static String newOwner(String name) {
		return anouncment + name + " is the new owner of the settlement.";
	}
	
	public static String claimedChunkGroupBroadcast(SagaPlayer sagaPlayer, ChunkGroup chunkGroup){
		return anouncment + sagaPlayer.getName() + " has claimed " + chunkGroup.getName() + " settlement.";
	}

	public static String invalidInteger(String amount) {
		return negative + amount + " isn't a valid integer.";
	}
	
	public static String invalidPage(String amount) {
		return negative + amount + " isn't a valid page number.";
	}
	
	
	
	// Build restriction:
	public static String buildOverride(BuildOverride override){
		
		
		switch (override) {
			case BUILDING_DENY:
				
				return negative + "Can't build in this building.";

			case SETTLEMENT_DENY:
				
				return negative + "Can't build in this settlement.";

			case CHUNK_GROUP_DENY:
				
				return negative + "Can't build in this chunk group.";
				
			case HOME_DENY:
				
				return negative + "Can't build in this home.";

			case WILDERNESS_DENY:
				
				return negative + "Can't build in the wilderness.";

			default:
				return negative + "Can't build here.";
				
		}
		
		
	}
	
	
	
	// Settle and claim messages:
	public static String settlesRemaining(Short settles) {
		

		ChatColor settlesColor = positive;
		if(settles == 0){
			settlesColor = negative;
		}
		return normal1 + "You have " + settlesColor + settles + normal1 + " settlement points remaining.";
		
		
	}
	
	public static String claimsRemaining(Short claims) {
		
		
		ChatColor claimsColor = positive;
		if(claims == 0){
			claimsColor = negative;
		}
		return normal1 + "You have " + claimsColor + claims + normal1 + " claim points remaining.";
		
		
	}
	
	
	// Found delete claim abandon messages:
	public static String foundedChunkGroupBroadcast(SagaPlayer sagaPlayer, ChunkGroup settlement) {
		return anouncment + sagaPlayer.getName() + " has founded the new settlement " + settlement.getName() + ".";
	}

	public static String broadcastDeleted(SagaPlayer sagaPlayer, ChunkGroup settlement) {
		return anouncment + settlement.getName() + " settlement was disolved by " + sagaPlayer.getName();
	}

	public static String abandonedChunkBroadcast(SagaPlayer sagaPlayer, ChunkGroup settlement) {
		return anouncment + "This chunk has been abandoned from " + settlement.getName() + " settlement.";
	}
	
	
	
	// Claiming:
	public static String claimed(SagaChunk sagaChunk) {
		return normal1 +  "Claimed chunk.";
	}
	
	public static String claimed(SagaChunk sagaChunk, ChunkGroup chunkGroup) {
		return normal1 +  "Claimed chunk for " + chunkGroup.getName() + " settlement.";
	}
	
	

	// Found delete claim abandon restrictions:
	public static String notEnoughFactionSettles() {
		return negative + "The faction doesn't have any settlement points.";
	}
	
	public static String oneChunkGroupAllowed() {
		return negative + "You can only be in one settlement.";
	}
	
	public static String notEnoughClaims() {
		return negative + "Settlement doesn't have any claim points.";
	}

	public static String chunkClaimed(){
		return negative + "This chunk of land has already been claimed.";
	}
	
	public static String chunkNotClaimed(){
		return negative + "This chunk of land isn't claimed.";
	}

	public static String chunkMustBeAdjacent(){
		return negative + "You can only claim chunks adjacent to an existing settlement.";
	}
	
	public static String levelTooHighDelete() {
		return negative + "Settlements above level " + ChunkGroupConfiguration.config().noDeleteLevel + " can't be deleted.";
	}

	// Invite join leave messages:
	public static String beenInvited(SagaPlayer sagaPlayer, ChunkGroup settlement) {
		return anouncment + "You have been invited to " + settlement.getName() + " settlement.";
	}
	
	public static String invited(SagaPlayer sagaPlayer, ChunkGroup settlement) {
		return anouncment + sagaPlayer.getName() + " was invited to the settlement.";
	}
	
	
	public static String beenInvited(SagaFaction sagaFaction, ChunkGroup settlement) {
		return sagaFaction.getSecondaryColor() + "The faction was invited to " + settlement.getName() + " settlement.";
	}
	
	public static String invited(SagaFaction sagaFaction, ChunkGroup settlement) {
		return anouncment + "" + name(sagaFaction, anouncment) + " was invited to the settlement.";
	}
	
	
	public static String haveJoined(SagaPlayer sagaPlayer, ChunkGroup settlement) {
		return anouncment + "You joined " +settlement.getName() + " settlement.";
	}
	
	public static String joined(SagaPlayer sagaPlayer, ChunkGroup settlement) {
		return anouncment + sagaPlayer.getName() + " has joined the settlement.";
	}
	
	
	public static String haveJoined(SagaFaction sagaFaction, ChunkGroup settlement) {
		return sagaFaction.getSecondaryColor() + "The faction joined " +settlement.getName() + " settlement.";
	}
	
	public static String joined(SagaFaction sagaFaction, ChunkGroup settlement) {
		return anouncment + "" + name(sagaFaction, anouncment) + " faction has joined the settlement.";
	}
	
	
	public static String haveQuit(SagaPlayer sagaPlayer, ChunkGroup settlement) {
		return anouncment + "You left from " + settlement.getName() + " settlement.";
	}
	
	public static String quit(SagaPlayer sagaPlayer, ChunkGroup settlement) {
		return anouncment + sagaPlayer.getName() + " has left the settlement.";
	}

	
	public static String beenKicked(SagaPlayer sagaPlayer, ChunkGroup settlement) {
		return anouncment + "You have been kicked out of " + settlement.getName() + " settlement.";
	}
	
	public static String kicked(SagaPlayer sagaPlayer, ChunkGroup settlement) {
		return anouncment + sagaPlayer.getName() + " has been kicked from the settlement.";
	}

	
	public static String declinedInvites() {
		return normal1 + "Declined all settlement invitations.";
	}

	public static String pendingInvitations(SagaPlayer sagaPlayer, ArrayList<ChunkGroup> groups) {
		
		
		StringBuffer rString = new StringBuffer();
		ChatColor messageColor = anouncment;
		
		if(groups.size() == 0){
			return messageColor + "You don't have a pending settlement invitation.";
		}
		
		rString.append(messageColor);
		
		rString.append("You have");
		
		if(groups.size() == 1){
			rString.append(" a pending invitation from ");
		}else{
			rString.append(" pending invitations from ");
		}
		
		for (int i = 0; i < groups.size(); i++) {
			if( i != 0 ) rString.append(", ");
			rString.append(groups.get(i).getName());
		}
		
		if(groups.size() == 1){
			rString.append(" settlement.");
		}else{
			rString.append(" settlements.");
		}
		
		return rString.toString();
		
		
	}

	
	// Invite join  leave restrictions:
	public static String noFactionInvites() {
		return negative + "The faction doesn't have a settlement invitation.";
	}
	
	public static String noFactionInvites(String factionName) {
		return negative + "The factions doesn't have an invitation to " + factionName + " settlement.";
	}
	
	public static String playerNoInvites(SagaPlayer sagaPlayer) {
		return negative + "You don't have a settlement invitation.";
	}
	
	public static String playerNoInvites(SagaPlayer sagaPlayer, String name) {
		return negative + "You don't have an invitation to " + name + " settlement.";
	}
	
	public static String factionNoInvites(SagaFaction sagaFaction) {
		return negative + "The faction doesn't have a settlement invitation.";
	}
	
	public static String factionNoInvites(SagaFaction sagaFaction, String name) {
		return negative + "The faction doesn't have an invitation to " + name + " settlement.";
	}
	
	public static String cantAcceptInvitations() {

		return negative + "You can't accept settlement invitations.";
		
	}

	public static String cantInviteYourself(SagaPlayer sagaPlayer, ChunkGroup chunkGroup) {
		return negative + "You can't invite yourself.";
	}

	public static String cantKickYourself(SagaPlayer sagaPlayer, ChunkGroup settlement) {
		return negative + "You can't kick yourself from the settlement.";
	}

	public static String nonExistantChunkGroup(String groupName) {
		return negative + groupName + " settlement doesn't exist.";
	}
	
	public static String nonExistantChunkGroup() {
		return negative + "Settlement doesn't exist.";
	}
	
	public static String nonExistantPlayer(String playerName) {
		return negative + playerName + " doesn't exist.";
	}
	
	public static String nonExistantFaction(String factionName) {
		return negative + factionName + " faction doesn't exist.";
	}
	
	public static String alreadyInTheChunkGroup(SagaFaction sagaFaction, ChunkGroup group) {
		return negative + name(sagaFaction, negative) + " faction is already a part of the settlement.";
	}
	
	public static String alreadyInTheChunkGroup(SagaPlayer sagaPlayer, ChunkGroup group) {
		return negative + name(sagaPlayer, negative) + " is already a part of the settlement.";
	}
	
	public static String alreadyInTheChunkGroup(ChunkGroup group) {
		return negative + "You already are a part of the settlement.";
	}

	public static String alreadyInvited(SagaFaction sagaFaction, ChunkGroup group) {
		return negative + name(sagaFaction, negative) + " is already a invited to the settlement.";
	}
	
	public static String alreadyInvited(SagaPlayer sagaPlayer, ChunkGroup group) {
		return negative + name(sagaPlayer, negative) + " is already a invited to the settlement.";
	}
	
	public static String playerNotChunkGroupMember(SagaPlayer sagaPlayer, ChunkGroup chunkGroup) {
		return negative + name(sagaPlayer, negative) + " isn't part of the settlement.";
	}
	
	public static String haveCunkGroup() {
		return negative + "You are already in a settlement.";
	}
	
	
	// Stats:
	public static String stats(SagaPlayer sagaPlayer, Settlement settlement) {
		
		
		StringBuffer rString = new StringBuffer();
		ColorCircle messageColor = new ColorCircle().addColor(normal1).addColor(normal2);
		
		// Saving disabled:
		if(!settlement.isSavingEnabled()){
			
			rString.append(veryNegative + "Settlement information will not be saved!");
			
			rString.append("\n");
			
		}
		
		// Level:
		rString.append(messageColor.nextColor());
		rString.append("Level: " + settlement.getLevel());
		if(settlement.getLevel() < settlement.getDefinition().getMaxLevel()){
			rString.append(" Remaining exp: " + settlement.getRemainingExp().intValue());
		}else{
			rString.append(" Remaining exp: -");
		}
		rString.append(" Exp speed: " + settlement.getExpSpeed());
		
		rString.append("\n");
		
		// Claim and building points:
		rString.append(messageColor.nextColor());
		rString.append("Claims: " + settlement.getTotalClaimed() + "/" + settlement.getTotalClaims());
		rString.append(" Building points: " + settlement.getUsedBuildingPoints() + "/" + settlement.getTotalBuildingPoints());
		
		rString.append("\n");
		
		// Owner:
		ChatColor elementColor = messageColor.nextColor();
		if(settlement.hasOwner()){
			rString.append(elementColor);
			rString.append("Owner: " + settlement.getOwner());
		}else{
			rString.append("Owner: "+ veryNegative +"none" + elementColor);
		}
		
		rString.append("\n");
		
		// Level requirements:
		rString.append(levelupRequirementsElement(settlement, messageColor.nextColor()));
		
		// Buildings:
		rString.append("\n");
		rString.append(buildingsElement(settlement, messageColor.nextColor()));

		// Roles:
		if(settlement.getRoles().size() != 0){
			
			rString.append("\n");
			
			rString.append(rolesElement(settlement, messageColor.nextColor()));
			
		}
		
		// Bonuses:
		ArrayList<String> bonuses = bonusesElement(settlement);
		if(bonuses.size() > 0){
			
			rString.append("\n");
			rString.append(messageColor.nextColor());
			
			rString.append("Bonuses: ");
			
			for (int i = 0; i < bonuses.size(); i++) {
				
				if(i != 0) rString.append(", ");
				
				rString.append(bonuses.get(i));
				
			}
			
		}
		
		return TextUtil.frame(settlement.getName() + " stats", rString.toString(), messageColor.nextColor());
		
		
	}
	
	private static String levelupRequirementsElement(Settlement settlement, ChatColor messageColor){
		
		
		StringBuffer rString = new StringBuffer();
		ChatColor notEnough = ChatColor.DARK_GRAY;
		
		// Active players:
		Integer activePlayers = settlement.getActivePlayerCount();
		Integer requiredActive = settlement.getDefinition().getActivePlayers(settlement.getLevel());
		if(requiredActive == 0){
			
		}	
		else if(requiredActive > activePlayers){
			rString.append("" + notEnough + activePlayers + "/" + requiredActive + messageColor + " active players");
		}else{
			rString.append("" + activePlayers + "/" + requiredActive + " active players");
		}
		
		// No requirements:
		if(rString.length() == 0){
			rString.append("none");
		}
		
		// Tite:
		rString.insert(0, "Requirements: ");
		
		rString.insert(0, messageColor);
		
		return rString.toString();
		
		
	}

	private static String buildingsElement(Settlement settlement, ChatColor messageColor) {

		
		StringBuffer rString = new StringBuffer();
		
		HashSet<String> buildings = settlement.getEnabledBuildings();
		
		if(buildings.size() == 0){
			rString.append("Buildings: none");
		}else if(buildings.size() == 1){
			rString.append("Building: ");
		}else{
			rString.append("Buildings: ");
		}
		
		boolean firstElement = true;
		for (String buildingName : buildings) {
			
			if(!firstElement) rString.append(", ");
			firstElement = false;
			
			rString.append(buildingName + " " + settlement.getUsedBuildings(buildingName) + "/" + settlement.getTotalBuildings(buildingName) );
			
		}
		
		rString.insert(0, messageColor);
		
		return rString.toString();
		
		
	}
	
	private static String rolesElement(Settlement settlement, ChatColor messageColor) {
		
		
		StringBuffer rString = new StringBuffer();
		HashSet<String> roles = settlement.getRoles();
		
		if(roles.size() == 0){
			rString.append("Roles: none");
		}else if(roles.size() == 0){
			rString.append("Role: ");
		}else{
			rString.append("Roles: ");
		}
		
		boolean firstElement = true;
		for (String role : roles) {
			
			if(!firstElement){
				rString.append(", ");
			}
			firstElement = false;
			
			rString.append(role + " " + settlement.getUsedRoles(role) + "/" + settlement.getTotalRoles(role));
			
		}
		
		rString.insert(0, messageColor);
		
		return rString.toString();
		
		
	}
	
	public static String list(SagaPlayer sagaPlayer, Settlement settlement) {
		
		
		StringBuffer rString = new StringBuffer();
		ColorCircle normalColor = new ColorCircle().addColor(normal1).addColor(normal2);
		
		// Online total inactive:
		if(settlement.getPlayerCount() > 0){
			
			if(rString.length() > 0) rString.append("\n");
			
			rString.append(normalColor.nextColor() + "Online: " + settlement.getRegisteredPlayerCount() + "/" + settlement.getPlayerCount());

			int inactiveCount = settlement.getInactivePlayerCount();
			if(inactiveCount != 0){
				rString.append(" Inactive: " + inactiveCount);
			}
			
		}
		
		// Players:
		if(settlement.getPlayerCount() > 0){
			
			if(rString.length() > 0) rString.append("\n");
			
			rString.append( listPlayersElement(sagaPlayer, settlement, normalColor.nextColor()) );

		}
		
		// Factions:
		if(settlement.getFactions().size() > 0){
			
			if(rString.length() > 0) rString.append("\n");
			
			rString.append( listFactionsElement(settlement, normalColor.nextColor()) );
			
		}
		
		return TextUtil.frame(settlement.getName() + " members", rString.toString(), normalColor.nextColor());
		
		
	}
	
	private static String listPlayersElement(SagaPlayer sagaPlayer, Settlement settlement, ChatColor messageColor){
		

		// Fill roles table:
		Short maxHierarchyLevel = 0;
		Hashtable<Short, Hashtable<String, ArrayList<String>>> roleTable = new Hashtable<Short, Hashtable<String,ArrayList<String>>>();
		ArrayList<String> zeroHighlMembers = new ArrayList<String>(settlement.getPlayers());
		for (int i = 0; i < zeroHighlMembers.size(); i++) {
			String name = zeroHighlMembers.get(i);
			Proficiency role = settlement.getRole(name);
			if(role == null) continue;
			zeroHighlMembers.remove(i);
			i--;
			String roleName = role.getName();
			Short hierarchyLevel = role.getHierarchyLevel();
			
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
		for (Short i = maxHierarchyLevel ; i >= 0 ; i--) {
			
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
					eString.append(capitalize(roleName) + ": ");
				}else{
					eString.append(capitalize(roleName + "s") + ": ");
				}
				
				// Names:
				for (int j = 0; j < playerNames.size(); j++) {
					if( j != 0 ) eString.append(", ");
					eString.append( playerNameElement(playerNames.get(j), settlement, messageColor, settlement.isPlayerActive(playerNames.get(j))) );
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
				rString.append(playerNameElement(zeroHighlMembers.get(i), settlement, messageColor, settlement.isPlayerActive(zeroHighlMembers.get(i))) );
			}
			
		}
		
		return messageColor + rString.toString();
		
		
	}

	private static String listFactionsElement(Settlement settlement, ChatColor messageColor){
		
		
		StringBuffer rString = new StringBuffer();
		
		ArrayList<SagaFaction> factions = settlement.getRegisteredFactions();
		
		if(factions.size() == 0){
			rString.append("Factions: none");
		}else if(factions.size() == 1){
			rString.append("Faction: ");
		}else{
			rString.append("Faction: ");
			rString.append("\n");
		}
		
		for (int i = 0; i < factions.size(); i++) {
			// Faction:
			if(i != 0 ) rString.append(", ");
			rString.append(name(factions.get(i), messageColor));
			// Faction players:
			rString.append("(");
			ArrayList<String> factionPlayers = factions.get(i).getMembers();
			for (int j = 0; j < factionPlayers.size(); j++) {
				if(j != 0 ) rString.append(", ");
				rString.append(playerNameElement(factionPlayers.get(j), factions.get(i), messageColor));
			}
			rString.append(")");
		}
		
		return messageColor + rString.toString();
		
		
	}
	
	private static String playerNameElement(String playerName, Settlement settlement, ChatColor messageColor, boolean isActive){
		
		
		StringBuffer rString = new StringBuffer();
		ChatColor offlineColor = ChatColor.GRAY;
		ChatColor inactiveColor = ChatColor.DARK_GRAY;
		
		// Name:
		if(!isActive){
			
			rString.append(inactiveColor + playerName + messageColor);
			
		}
		else if(!settlement.hasRegisteredPlayer(playerName)){
			
			rString.append(offlineColor + playerName + messageColor);
			
		}
		else{
		
			rString.append(messageColor + playerName + messageColor);
			
		}
		
//		// Faction:
//		SagaFaction playerFaction = settlement.getPlayerFaction(playerName);
//		if(playerFaction != null){
//			rString.append(":" + firstLetter(playerFaction.getName()) + "");
//		}
		
		return rString.toString();
		
		
	}

	private static String playerNameElement(String playerName, SagaFaction faction, ChatColor messageColor){
		
		
		StringBuffer rString = new StringBuffer();
		ChatColor offlineColor = ChatColor.DARK_GRAY;
		
		// Name:
		if(!faction.isRegisteredMember(playerName)){
			
			rString.append(offlineColor + playerName + messageColor);
			
		}else{
		
			rString.append(messageColor + playerName + messageColor);
			
		}
		
		return rString.toString();
		
		
	}

	private static ArrayList<String> bonusesElement(ChunkGroup cunkGroup){
		
		ArrayList<String> rList = new ArrayList<String>();
		
		// Pvp:
		if(cunkGroup.hasPvpProtectionBonus()){
			
			rList.add("pvp protection");
			
		}
		
		// Unlimited claims:
		if(cunkGroup.hasUnlimitedClaimBonus()){
			
			rList.add("unlimited claims");
			
		}
		
		return rList;
		
		
	}
	
	
	// Roles:
	public static String invalidRole(String roleName){
		return negative + roleName + " is an invalid role.";
	}
	
	public static String alreadyHasRole(String targetName){
		return negative + targetName + " already has a role.";
	}
	
	public static String cantPromote(String roleName, Building building){
		return negative + TextUtil.capitalize(building.getDisplayName()) + " can't promote a " + roleName + ".";
	}
	
	public static String cantDemote(String roleName, Building building){
		return negative + TextUtil.capitalize(building.getDisplayName()) + " can't demote a " + roleName + ".";
	}

	public static String cantPromoteTo(String roleName, Building building){
		return negative + TextUtil.capitalize(building.getDisplayName()) + " can't promote to " + roleName + ".";
	}
	
	public static String cantDemoteTo(String roleName, Building building){
		return negative + TextUtil.capitalize(building.getDisplayName()) + " can't demote to " + roleName + ".";
	}
	
	
	public static String finalRole(String targetName, Building building){
		return negative + TextUtil.capitalize(building.getDisplayName()) + " can't be used to promote " + targetName + " any further.";
	}
	
	public static String noRole(SagaPlayer targetPlayer, Settlement settlement){
		return negative + targetPlayer.getName() + " doesn't have a role.";
	}
	
	public static String newRole(SagaPlayer sagaPlayer, ChunkGroup settlement, String roleName) {
		
		return anouncment + sagaPlayer.getName() + " is now a " + roleName + ".";
		
	}

	public static String roleNotAvailable(String roleName) {

		return negative + "No " + roleName + " roles are available.";
		
	}
	
	public static String promotedToRoleBroadcast(SagaPlayer sagaPlayer, SagaPlayer promotedSagaPlayer, ChunkGroup settlement, String roleName) {
		
		if(sagaPlayer == promotedSagaPlayer){
			return anouncment + sagaPlayer.getName() + " promoted himself to " + roleName + ".";
		}
		
		return anouncment + sagaPlayer.getName() + " promoted " + promotedSagaPlayer.getName() + " to " + roleName + ".";
		
	}
	
	public static String demotedToRoleBroadcast(SagaPlayer sagaPlayer, SagaPlayer promotedSagaPlayer, ChunkGroup settlement, String roleName) {
		
		if(sagaPlayer == promotedSagaPlayer){
			return anouncment + sagaPlayer.getName() + " demoted himself to " + roleName + ".";
		}
		
		return anouncment + sagaPlayer.getName() + " demoted " + promotedSagaPlayer.getName() + " to " + roleName + ".";
		
	}
	
	public static String canSetRoleTo(ArrayList<String> promote) {

		
		StringBuffer rString = new StringBuffer();
		for (String profession : promote) {
			if(rString.length() != 0 ) rString.append(", ");
			rString.append(profession);
		}
		
		if(promote.size() == 0){
			return negative + "You can't set roles.";
		}
		
		if(promote.size() == 1){
			return negative + "You only can only set " + rString.toString() + " role.";
		}
		
		return negative + "You only can only set " + rString.toString() + " roles.";
		
		
	}
	
	public static String canSetRoleFrom(ArrayList<String> promote) {

		
		StringBuffer rString = new StringBuffer();
		for (String profession : promote) {
			if(rString.length() != 0 ) rString.append(", ");
			rString.append(profession);
		}
		
		if(promote.size() == 0){
			return negative + "You can't set roles.";
		}
		
		if(promote.size() == 1){
			return negative + "Target needs to have " + rString.toString() + " role.";
		}
		
		return negative + "Target needs to have one of " + rString.toString() + " roles.";
		
		
	}
	
	
	public static String noRoles(Building building) {
		return negative + "" + TextUtil.capitalize(building.getDisplayName()) + " doesen't offer any roles.";
	}
	
	public static String roleMustBeLower() {
		return negative + "Only lower roles can be promoted.";
	}
	
	
	// Leveling:
	public static String settlementLevel(Settlement settlement) {
		return anouncment + settlement.getName() + " settlement is now level " + settlement.getLevel() + ".";
	}
	
	public static String notEnoughBuildingPoints(ChunkGroup chunkGroup, Integer available, Integer cost) {
		return negative + "Not enough building points. You need " + (cost - available) + " aditional points.";
	}

	public static String noBuilding() {
		return negative + "There is no building on this chunk of land.";
	}

	
	public static String buildingStats(Building building){
		
		
		StringBuffer rString = new StringBuffer();
		ColorCircle messageColor = new ColorCircle().addColor(ChunkGroupMessages.normal1).addColor(ChunkGroupMessages.normal2);
		ChatColor elementColor;
		
		// Chunk group:
		SagaChunk sagaChunk = building.getSagaChunk();
		elementColor = messageColor.nextColor();
		rString.append(elementColor);
		if(sagaChunk == null){
			
			rString.append("Settlement: " + veryNegative + "none" + elementColor);
			
		}else{
			
			rString.append("Settlement: " + sagaChunk.getChunkGroup().getName());
			
		}
		
		// Enabled buildings:
		if(building.getDefinition().getBuildings(building.getLevel()).size() > 0){
			
			rString.append("\n");
			
			rString.append(enabledBuildingsElement(building, messageColor.nextColor()));
			
		}
		
		// Enabled roles:
		if(building.getDefinition().getRoles(building.getLevel()).size() > 0){
			
			rString.append("\n");
			
			rString.append(enabledRolesElement(building, messageColor.nextColor()));
			
		}
		
		
		// Specific:
		ArrayList<String> specific = building.getSpecificStats();
		if(specific.size() > 0){
			
			for (String sElement : specific) {
				
				rString.append("\n");
				
				rString.append(messageColor.nextColor());
				
				rString.append(sElement);
				
			}
			
		}
		
		rString.append(messageColor.nextColor());
		
		return TextUtil.frame(building.getDisplayName() + " stats", rString.toString(), messageColor.nextColor());
		
	}
	
	private static String enabledBuildingsElement(Building building, ChatColor messageColor) {
		

		StringBuffer rString = new StringBuffer();
		
		HashSet<String> buildings = building.getDefinition().getBuildings(building.getLevel());
		
		if(buildings.size() == 0){
			rString.append("Enabled buildings: none");
		}else if(buildings.size() == 1){
			rString.append("Enabled building: ");
		}else{
			rString.append("Enabled buildings: ");
		}
		
		boolean firstElement = true;
		for (String buildingName : buildings) {
			
			if(!firstElement) rString.append(", ");
			firstElement = false;
			
			String element = buildingName + "(" + building.getDefinition().getTotalBuildings(buildingName, building.getLevel()) + ")";
			
			rString.append(element);
			
		}
		
		rString.insert(0, messageColor);
		
		return rString.toString();
		
		
		// TODO Auto-generated method stub

	}
	
	private static String enabledRolesElement(Building building, ChatColor messageColor) {
		

		StringBuffer rString = new StringBuffer();
		
		HashSet<String> roles = building.getDefinition().getRoles(building.getLevel());
		
		if(roles.size() == 0){
			rString.append("Enabled roles: none");
		}else if(roles.size() == 1){
			rString.append("Enabled roles: ");
		}else{
			rString.append("Enabled roles: ");
		}
		
		boolean firstElement = true;
		for (String buildingName : roles) {
			
			if(!firstElement) rString.append(", ");
			firstElement = false;
			
			String element = buildingName + "(" + building.getDefinition().getTotalRoles(buildingName, building.getLevel()) + ")";
			
			rString.append(element);
			
		}
		
		rString.insert(0, messageColor);
		
		return rString.toString();
		

	}
	
	
	// Inform:
	public static String informSettlementAboveLevelDelete() {
		return normal1 + "Settlement with level " + ChunkGroupConfiguration.config().noDeleteLevel + " and above can only be deleted by unclaiming everything.";
	}
	
	public static String informAccept() {
		return normal1 + "Use /saccept to accept a settlement invitation.";
	}
	
	// Pvp:
	public static String safeArea() {
		return negative + "Pvp allowed in a safe area.";
	}
	
	
	// Rename:
	public static String renamed(ChunkGroup chunkGroup) {

		return anouncment + "Settlement was renamed to " + chunkGroup.getName() + ".";
		
	}
	
	// Bonuses:
	public static String togglePvp(ChunkGroup chunkGroup) {
		
		if(chunkGroup.hasPvpProtectionBonus()){
			return positive + "Enabled pvp protection for " + chunkGroup.getName() + ".";
		}else{
			return positive + "Disabled pvp protection for " + chunkGroup.getName() + ".";
		}

	}
	
	public static String toggleUnlimitedClaim(ChunkGroup chunkGroup) {
		
		if(chunkGroup.hasUnlimitedClaimBonus()){
			return positive + "Enabled unlimited claim points for " + chunkGroup.getName() + ".";
		}else{
			return positive + "Disabled unlimited claim points for " + chunkGroup.getName() + ".";
		}

	}
	
	
	// Move:
	public static String entered(ChunkGroup chunkGroup) {
		
		return normal1 + "" + ChatColor.ITALIC + "Entered " + chunkGroup.getName() + " settlement.";
		
	}
	
	public static String left(ChunkGroup chunkGroup) {
		
		return normal1 + "" + ChatColor.ITALIC + "Left " + chunkGroup.getName() + " settlement.";
		
	}
	
	
	// Info:
	public static String wrongQuit() {
		
		return negative + "Because /squit and /fquit are similar, this command isn't used. Please use /settlementquit instead.";
		
	}


	
	
	// Admin:
	public static String setLevel(Settlement settlement){
		
		return positive + TextUtil.capitalize(settlement.getName()) + " level set to " +settlement.getLevel() + "."  ;
		
	}
	
	
	// Creating:
	public static String invalidName() {
		
		return negative + "Name must be " + ChunkGroupCommands.minimumNameLenght + "-" + ChunkGroupCommands.maximumNameLength + ". Letters and numbers only.";
		
	}
	
	// Utility:
	private static String capitalize(String string) {

		if(string.length()>=1){
			return string.substring(0, 1).toUpperCase() + string.substring(1);
		}else{
			return string.toUpperCase();
		}
		
	}
	
	private static String name(SagaFaction sagaFaction, ChatColor messageColor){
		
		return sagaFaction.getPrimaryColor() + sagaFaction.getName() + messageColor;
		
	}
	
	private static String name(SagaPlayer sagaPlayer, ChatColor messageColor){
		
		return messageColor + sagaPlayer.getName() + messageColor;
		
	}
	

	
}
