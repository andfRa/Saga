package org.saga.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.saga.Clock.DaytimeTicker.Daytime;
import org.saga.buildings.Building;
import org.saga.buildings.BuildingDefinition;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.ChunkGroupToggleable;
import org.saga.chunkGroups.SagaChunk;
import org.saga.commands.ChunkGroupCommands;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.SettlementConfiguration;
import org.saga.factions.SagaFaction;
import org.saga.listeners.events.SagaBuildEvent.BuildOverride;
import org.saga.messages.PlayerMessages.ColorCircle;
import org.saga.player.Proficiency;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.player.ProficiencyDefinition;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Settlement;
import org.saga.settlements.SettlementDefinition;
import org.saga.utility.StringTable;
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
	
	
	
	// Settling and dissolving:
	public static String settled(SagaPlayer sagaPlayer, ChunkGroup settlement) {
		return anouncment + settlement.getName() + " settlement was founded  by " + sagaPlayer.getName() + ".";
	}

	public static String dissolved(SagaPlayer sagaPlayer, ChunkGroup settlement) {
		return anouncment + settlement.getName() + " settlement was dissolved by " + sagaPlayer.getName() + ".";
	}

	
	
	// Claiming:
	public static String claimed(SagaChunk sagaChunk) {
		return normal1 +  "Claimed chunk.";
	}
	
	public static String claimed(SagaChunk sagaChunk, ChunkGroup chunkGroup) {
		return normal1 +  "Claimed chunk for " + chunkGroup.getName() + " settlement.";
	}
	
	public static String abandoned(SagaChunk sagaChunk) {
		return normal1 +  "Abandoned chunk.";
	}
	
	public static String abandoned(SagaChunk sagaChunk, ChunkGroup chunkGroup) {
		return normal1 +  "Abandoned chunk from " + chunkGroup.getName() + " settlement.";
	}
	
	
	
	// Buildings:
	public static String setBuilding(Building building) {
		return normal1 +  "Set " +  building.getName() + " building.";
	}
	
	public static String setBuilding(Building building, ChunkGroup chunkGroup) {
		return normal1 +  "Set " +  building.getName() + " building for " + chunkGroup.getName() + " settlement.";
	}
	
	public static String removedBuilding(Building building) {
		return normal1 +  "Removed " +  building.getName() + " building.";
	}
	
	public static String removedBuilding(Building building, ChunkGroup chunkGroup) {
		return normal1 +  "Removed " +  building.getName() + " building from " + chunkGroup.getName() + " settlement.";
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
		return negative + "Settlements above level " + SettlementConfiguration.config().noDeleteLevel + " can't be deleted.";
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
	public static String stats(SagaPlayer sagaPlayer, Settlement settlement, Integer page) {
		
		// TODO: bonuses
		StringBuffer result = new StringBuffer();
		
		switch (page) {
			
			// Buildings:
			case 1:
				
				result.append(buildings(settlement).createTable());
				
				break;

			// Roles:	
			case 2:
				
				result.append(roles(settlement).createTable());
				
				break;
				
			// Main stats:
			default:
				
				page = 0;
				
				// Levels and claims:
				result.append(levelClaims(settlement).createTable());

				result.append("\n");
				result.append("\n");
				
				// Active members:
				result.append(GeneralMessages.tableTitle("required"));
				result.append("\n");
				result.append(requirements(settlement).createTable());
				
				break;
				
		}
		
		return TextUtil.frame(settlement.getName() + " stats " + (page + 1) + "/" + 3, result.toString(), normal1);

		
	}
	
	private static StringTable levelClaims(Settlement settlement){
		
		
		ColorCircle colours = new ColorCircle().addColor(normal1).addColor(normal2);
		StringTable table = new StringTable(colours);
		
		// Claims:
		table.addLine("claims", settlement.getUsedClaimed() + "/" + settlement.getTotalClaims(), 0);
		
		// Upgrades:
		table.addLine("upgrades", 0 + "/" + 0, 0);
		
		// Owner:
		if(settlement.hasOwner()){
			table.addLine("owner", settlement.getOwner(), 0);
		}else{
			table.addLine("owner", veryNegative + "none", 0);
		}
		
		// Level:
		table.addLine("level", settlement.getLevel() + "/" + settlement.getDefinition().getMaxLevel(), 2);

		// Next exp:
		table.addLine("next EXP", settlement.getRemainingExp().toString(), 2);

		// Exp per minute:
		table.addLine("EXP/minute", settlement.getExpSpeed().toString(), 2);
		
		table.collapse();
		
		return table;
		
		
	}
	
	private static StringTable requirements (Settlement settlement){
		
		
		ColorCircle colours = new ColorCircle().addColor(normal1).addColor(normal2);
		StringTable table = new StringTable(colours);
		
		SettlementDefinition definition = settlement.getDefinition();
		Integer level = settlement.getLevel();
		
		// Active players:
		Integer active = settlement.countActiveMembers();
		if(settlement.checkActiveMembers()){
			table.addLine(positive + "members", positive + active.toString() + "/" + definition.getActivePlayers(level).toString(), 0);
		}else{
			table.addLine(negative + "members", negative + active.toString() + "/" + definition.getActivePlayers(level).toString(), 0);
		}
		table.collapse();
		
		return table;
		
		
	}
	
	private static StringTable buildings(Settlement settlement){
		
		
		ColorCircle colours = new ColorCircle().addColor(normal1).addColor(normal2);
		StringTable table = new StringTable(colours);
		
		// Retrieve buildings:
		BuildingDefinition[] definitions = SettlementConfiguration.config().getBuildingDefinitions().toArray(new BuildingDefinition[0]);
		
		// Sort required by level:
		Comparator<BuildingDefinition> comparator = new Comparator<BuildingDefinition>() {
			@Override
			public int compare(BuildingDefinition arg0, BuildingDefinition arg1) {
				return arg0.getRequiredLevel() - arg1.getRequiredLevel();
			}
		};
		Arrays.sort(definitions, comparator);
		
		// Column names:
		table.addLine(new String[]{GeneralMessages.columnTitle("building"), GeneralMessages.columnTitle("status")});
		
		// Column values:
		if(definitions.length != 0){
			
			for (int j = 0; j < definitions.length; j++) {
				
				// Values:
				String name = definitions[j].getName();
				String status = "";
				
				Integer level = settlement.getBuildingLevel(name);
				
				// Requirements met:
				if(definitions[j].checkRequirements(settlement, 1)){
					
					// Multiple buildings:
					Integer totalBuildings = settlement.getAvailableBuildings(name);
					Integer usedBuildings = settlement.getTotalBuildings(name);
					
					// Set:
					if(usedBuildings > 0){
						
						// Status:
						status = resources(definitions[j], level);
						if(status.length() == 0) status = "set";
						
						// Colours:
						name = veryPositive + name;
						status = veryPositive + status;
						
						if(totalBuildings != 1){
							name = name + " " + usedBuildings + "/" + totalBuildings;
						}
					
					}
					
					// Available:
					else{
						status = "not set";
					}
					
					
				}
				
				// Requirements not met:
				else{
					name = unavailable + name;
					status = unavailable + "(" + requirements(definitions[j], 1) + ")";
				}
					
				table.addLine(new String[]{name, status});
			
			}
			
		}else{
			table.addLine(new String[]{"-", "-"});
		}
		
		table.collapse();
		
		return table;
		
		
	}
	
	private static String resources(BuildingDefinition definition, Integer level){
		
		
		// Amount:
		Integer amount = definition.getResourceAmount(level);
		if(amount < 1) return "";
		
		// Daytime:
		Daytime daytime = definition.getResourceTime();
		if(daytime == Daytime.NONE) return "";
		
		// Resource:
		String resource = definition.getResource();
		if(resource.length() == 0) resource = "items";
		
		return amount + " " + resource + "/" + daytime;
		
				
	}
	
	private static String requirements(BuildingDefinition definition, Integer buildingLevel){
		
		
		StringBuffer result = new StringBuffer();
		
		// Level:
		Integer reqLevel = definition.getRequiredLevel();
		if(reqLevel > 0) result.append("lvl " + reqLevel);
		
		return result.toString();
		
		
	}
	
	private static StringTable roles(Settlement settlement){
		
		
		ColorCircle colours = new ColorCircle().addColor(normal1).addColor(normal2);
		StringTable table = new StringTable(colours);
		
		// Definitions:
		ArrayList<String> names = ProficiencyConfiguration.config().getProficiencyNames(ProficiencyType.ROLE);
		ArrayList<ProficiencyDefinition> definitions = new ArrayList<ProficiencyDefinition>();
		for (String name : names) {
			definitions.add(ProficiencyConfiguration.config().getDefinition(name));
		}
		
		// Sort by hierarchy:
		Comparator<ProficiencyDefinition> comparator = new Comparator<ProficiencyDefinition>() {
			@Override
			public int compare(ProficiencyDefinition o1, ProficiencyDefinition o2) {
				return o2.getHierarchyLevel() - o1.getHierarchyLevel();
			}
		};
		Collections.sort(definitions, comparator);
		
		// Column names:
		table.addLine(new String[]{GeneralMessages.columnTitle("role"), GeneralMessages.columnTitle("assigned")});
		
		// Column values:
		if(definitions.size() != 0){
			
			for (ProficiencyDefinition definition : definitions) {
				
				String name = definition.getName();
				String available = settlement.getUsedRoles(name) + "/" + settlement.getAvailableRoles(name);
				
				if(settlement.getRemainingRoles(name) > 0){
					available = positive + available;
				}else if(settlement.getRemainingRoles(name) < 0){
					available = negative + available;
				}
				
				table.addLine(new String[]{name, available});
				
			}
			
			
		}else{
			table.addLine(new String[]{"-", "-"});
		}
		
		table.collapse();
		
		return table;
		
		
	}

	
	public static String list(SagaPlayer sagaPlayer, Settlement settlement) {
		
		
		StringBuffer rString = new StringBuffer();
		ColorCircle normalColor = new ColorCircle().addColor(normal1).addColor(normal2);
		
		// Online total inactive:
		if(settlement.getPlayerCount() > 0){
			
			if(rString.length() > 0) rString.append("\n");
			
			rString.append(normalColor.nextColor() + "Online: " + settlement.getRegisteredMemberCount() + "/" + settlement.getPlayerCount());

			int inactiveCount = settlement.countInactiveMembers();
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
					eString.append( playerNameElement(playerNames.get(j), settlement, messageColor, settlement.isMemberActive(playerNames.get(j))) );
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
				rString.append(playerNameElement(zeroHighlMembers.get(i), settlement, messageColor, settlement.isMemberActive(zeroHighlMembers.get(i))) );
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
		else if(!settlement.hasRegisteredMember(playerName)){
			
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

	
	
	// Inform:
	public static String informSettlementAboveLevelDelete() {
		return normal1 + "Settlement with level " + SettlementConfiguration.config().noDeleteLevel + " and above can only be deleted by unclaiming everything.";
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
	
	
	
	// Options:
	public static String optionToggle(ChunkGroup chunkGroup, ChunkGroupToggleable option) {
		
		if(chunkGroup.isOptionEnabled(option)){
			return positive + "Enabled " + option + " for " + chunkGroup.getName() + ".";
		}else{
			return positive + "Disabled " + option + " for " + chunkGroup.getName() + ".";
		}

	}
	
	public static String optionAlreadyEnabled(ChunkGroup chunkGroup, ChunkGroupToggleable option){
		
		return negative + "Option " + option.toString() + " is already enabled for " + chunkGroup.getName() + " settlement.";
		
	}
	
	public static String optionAlreadyDisabled(ChunkGroup chunkGroup, ChunkGroupToggleable option){
		
		return negative + "Option " + option.toString() + " is already disabled for " + chunkGroup.getName() + " settlement.";
		
	}
	
	public static String optionInvalid(String option){
		
		return negative + "Option " + option + " is not valid.";
		
	}
	
	public static String optionInvalidInfo(){
		
		
		ChunkGroupToggleable[] options = ChunkGroupToggleable.values();
		ArrayList<String> validOptions = new ArrayList<String>();
		for (int i = 0; i < options.length; i++) {
			
			validOptions.add(options[i].toString().replace(" ", SagaMessages.spaceSymbol));
			
		}
		
		return normal1 + "Valid options: " + TextUtil.flatten(validOptions) + ".";

		
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
