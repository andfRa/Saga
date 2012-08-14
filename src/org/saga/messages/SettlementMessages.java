package org.saga.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.saga.buildings.Building;
import org.saga.buildings.BuildingDefinition;
import org.saga.chunks.ChunkBundle;
import org.saga.chunks.ChunkBundleToggleable;
import org.saga.chunks.SagaChunk;
import org.saga.chunks.SagaMap;
import org.saga.commands.SettlementCommands;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.SettlementConfiguration;
import org.saga.factions.Faction;
import org.saga.factions.FactionClaimManager;
import org.saga.listeners.events.SagaBuildEvent.BuildOverride;
import org.saga.messages.PlayerMessages.ColourLoop;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.player.ProficiencyDefinition;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Settlement;
import org.saga.settlements.SettlementDefinition;
import org.saga.utility.text.StringTable;
import org.saga.utility.text.TextUtil;


public class SettlementMessages {
	

	// Colours:
	public static ChatColor veryPositive = ChatColor.DARK_GREEN; // DO NOT OVERUSE.
	
	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED; // DO NOT OVERUSE.
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor announce = ChatColor.AQUA;
	
	public static ChatColor normal1 = ChatColor.GOLD;
	
	public static ChatColor normal2 = ChatColor.YELLOW;
	
	public static ChatColor frame = ChatColor.DARK_GREEN;
	
	
	
	// General restriction:
	public static String missingDefinition(String buildingName){
		return veryNegative + "" + buildingName + " building isn't fully defined.";
	}
	
	public static String noChunkBundle(String name){
		return negative + "Settlement " + name + " doesn't exist.";
	}
	
	public static String notSettlement(ChunkBundle bundle){
		return negative + "Chunk bundle " + bundle.getName() + " isn't a settlement.";
	}
	

	
	// Membership:
	public static String notMember(){
		return negative + "You aren't a settlement member.";
	}
	
	public static String notMember(ChunkBundle chunkBundle){
		return negative + "You aren't a member of " + chunkBundle.getName() + " settlement.";
	}
	
	public static String notMember(ChunkBundle chunkBundle, String name){
		return negative + "Player " + name + " isn't a member of the settlement.";
	}

	
	
	// Arguments:
	public static String invalidPage(String amount) {
		return negative + amount + " isn't a valid page number.";
	}
	
	
	
	// Owner:
	public static String newOwnerBcast(String name) {
		return announce + "Player " + name + " is the new owner of the settlement.";
	}
	
	public static String alreadyOwner() {
		return negative + "You are already the settlement owner.";
	}
	
	public static String alreadyOwner(String name) {
		return negative + "Player " + name + " is already the settlement owner.";
	}
	
	public static String ownerCantQuit() {
		return negative + "Settlement owner can't quit the settlement.";
	}
	
	public static String ownerCantQuitInfo() {
		return normal1 + "Use /sresign to declare someone else as the owner.";
	}

	public static String cantKickOwner() {
		return negative + "Can't kick settlement owner.";
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
	
	
	
	// Settling and dissolving:
	public static String settledBcast(SagaPlayer sagaPlayer, ChunkBundle settlement) {
		return announce + settlement.getName() + " settlement was founded by " + sagaPlayer.getName() + ".";
	}

	public static String dissolved(SagaPlayer sagaPlayer, ChunkBundle settlement) {
		return announce + settlement.getName() + " settlement was dissolved by " + sagaPlayer.getName() + ".";
	}

	public static String informDissolveLevel() {
		return normal1 + "Settlement with level " + SettlementConfiguration.config().noDeleteLevel + " and above can only be dissolved by unclaiming everything.";
	}

	
	
	// Claiming:
	public static String claimed(SagaChunk sagaChunk) {
		return normal1 +  "Claimed chunk.";
	}
	
	public static String claimed(SagaChunk sagaChunk, ChunkBundle chunkBundle) {
		return normal1 +  "Claimed chunk for " + chunkBundle.getName() + " settlement.";
	}
	
	public static String abandoned(SagaChunk sagaChunk) {
		return normal1 +  "Abandoned chunk.";
	}
	
	public static String abandoned(SagaChunk sagaChunk, ChunkBundle chunkBundle) {
		return normal1 +  "Abandoned chunk from " + chunkBundle.getName() + " settlement.";
	}
	
	
	
	// Buildings:
	public static String setBuilding(Building building) {
		return normal1 +  "Set " +  building.getName() + " building.";
	}
	
	public static String setBuilding(Building building, ChunkBundle chunkBundle) {
		return normal1 +  "Set " +  building.getName() + " building for " + chunkBundle.getName() + " settlement.";
	}
	
	public static String removedBuilding(Building building) {
		return normal1 +  "Removed " +  building.getName() + " building.";
	}
	
	public static String removedBuilding(Building building, ChunkBundle chunkBundle) {
		return normal1 +  "Removed " +  building.getName() + " building from " + chunkBundle.getName() + " settlement.";
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

	public static String claimAdjacentDeny() {
		return negative + "Can't claim land adjacent to other settlements.";
	}

	
	
	// Invite join leave messages:
	public static String beenInvited(SagaPlayer sagaPlayer, ChunkBundle settlement) {
		return normal1 + "You have been invited to " + settlement.getName() + " settlement.";
	}
	
	public static String invited(SagaPlayer sagaPlayer, ChunkBundle settlement) {
		return announce + sagaPlayer.getName() + " was invited to the settlement.";
	}
	

	public static String informAccept() {
		return normal1 + "Use /saccept to accept a settlement invitation.";
	}
	
	
	public static String haveJoined(SagaPlayer sagaPlayer, ChunkBundle settlement) {
		return normal1 + "You joined " +settlement.getName() + " settlement.";
	}
	
	public static String joined(SagaPlayer sagaPlayer, ChunkBundle settlement) {
		return announce + sagaPlayer.getName() + " has joined the settlement.";
	}
	
	
	public static String haveQuit(SagaPlayer sagaPlayer, ChunkBundle settlement) {
		return normal1 + "You left from " + settlement.getName() + " settlement.";
	}
	
	public static String quit(SagaPlayer sagaPlayer, ChunkBundle settlement) {
		return announce + sagaPlayer.getName() + " has left the settlement.";
	}

	
	public static String beenKicked(SagaPlayer sagaPlayer, ChunkBundle settlement) {
		return normal1 + "You have been kicked out of " + settlement.getName() + " settlement.";
	}
	
	public static String kicked(SagaPlayer sagaPlayer, ChunkBundle settlement) {
		return announce + sagaPlayer.getName() + " has been kicked from the settlement.";
	}

	
	public static String declinedInvites() {
		return normal1 + "Declined all settlement invitations.";
	}

	public static String pendingInvitations(SagaPlayer sagaPlayer, ArrayList<ChunkBundle> groups) {
		
		
		StringBuffer rString = new StringBuffer();
		ChatColor messageColor = announce;
		
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
	public static String playerNoInvites(SagaPlayer sagaPlayer) {
		return negative + "You don't have a settlement invitation.";
	}
	
	public static String playerNoInvites(SagaPlayer sagaPlayer, String name) {
		return negative + "You don't have an invitation to " + name + " settlement.";
	}
	
	public static String factionNoInvites(Faction faction) {
		return negative + "The faction doesn't have a settlement invitation.";
	}
	
	public static String factionNoInvites(Faction faction, String name) {
		return negative + "The faction doesn't have an invitation to " + name + " settlement.";
	}
	
	public static String cantAcceptInvitations() {

		return negative + "You can't accept settlement invitations.";
		
	}

	public static String cantInviteYourself(SagaPlayer sagaPlayer, ChunkBundle chunkBundle) {
		return negative + "You can't invite yourself.";
	}

	public static String cantKickYourself(SagaPlayer sagaPlayer, ChunkBundle settlement) {
		return negative + "You can't kick yourself from the settlement.";
	}

	public static String nonExistantChunkBundle(String bundleName) {
		return negative + bundleName + " settlement doesn't exist.";
	}
	
	public static String nonExistantChunkBundle() {
		return negative + "Settlement doesn't exist.";
	}
	
	public static String nonExistantPlayer(String playerName) {
		return negative + playerName + " doesn't exist.";
	}

	public static String alreadyInTheChunkBundle(SagaPlayer sagaPlayer, ChunkBundle group) {
		return negative + sagaPlayer.getName() + " is already a part of the settlement.";
	}
	
	public static String alreadyInTheChunkBundle(ChunkBundle group) {
		return negative + "You already are a part of the settlement.";
	}

	public static String alreadyInvited(SagaPlayer sagaPlayer, ChunkBundle group) {
		return negative + sagaPlayer.getName() + " is already a invited to the settlement.";
	}
	
	public static String notMember(SagaPlayer sagaPlayer, ChunkBundle chunkBundle) {
		return negative + "Player " + sagaPlayer.getName() + " isn't a member of the settlement.";
	}
	
	public static String notMember(SagaPlayer sagaPlayer) {
		return negative + "Player " + sagaPlayer.getName() + " isn't a member of the settlement.";
	}
	
	public static String haveCunkGroup() {
		return negative + "You are already in a settlement.";
	}
	
	
	
	// Stats:
	public static String stats(SagaPlayer sagaPlayer, Settlement settlement, Integer page) {
		
		
		StringBuffer result = new StringBuffer();
		
		switch (page) {
			
			// Buildings:
			case 1:
				
				result.append(buildings(settlement).createTable());
				
				break;

			// Roles:	
			case 2:
				
				result.append(listMembers(settlement));
				
				break;
				
			// Main stats:
			default:
				
				page = 0;
				
				// Levels and claims:
				result.append(main(settlement).createTable());

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
	
	private static StringTable main(Settlement settlement){
		
		
		ColourLoop colours = new ColourLoop().addColor(normal1).addColor(normal2);
		StringTable table = new StringTable(colours);
		
		// Claims:
		table.addLine("claims", settlement.getUsedClaimed() + "/" + settlement.getTotalClaims(), 0);
		
		// Building points:
		table.addLine("build points", settlement.getUsedBuildPoints() + "/" + settlement.getAvailableBuildPoints(), 0);
		
		// Owner:
		if(settlement.hasOwner()){
			table.addLine("owner", settlement.getOwner(), 0);
		}else{
			table.addLine("owner", veryNegative + "none", 0);
		}
		
		// Level:
		table.addLine("level", settlement.getLevel() + "/" + settlement.getDefinition().getMaxLevel(), 2);

		// Next exp:
		table.addLine("next EXP", settlement.getRemainingExp().intValue() + "", 2);

		// Exp per minute:
		table.addLine("EXP/minute", settlement.getExpSpeed().toString(), 2);
		
		table.collapse();
		
		return table;
		
		
	}
	
	private static StringTable requirements (Settlement settlement){
		
		
		ColourLoop colours = new ColourLoop().addColor(normal1).addColor(normal2);
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
		
		
		ColourLoop colours = new ColourLoop().addColor(normal1).addColor(normal2);
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
		table.addLine(new String[]{GeneralMessages.columnTitle("building"), GeneralMessages.columnTitle("effect")});
		
		// Column values:
		if(definitions.length != 0){
			
			for (int j = 0; j < definitions.length; j++) {
				
				// Values:
				String name = definitions[j].getName();
				String status = "";
				
				Integer score = settlement.getBuildingLevel(name);
				
				// Requirements met:
				if(definitions[j].checkRequirements(settlement, 1)){
					
					// Multiple buildings:
					Integer totalBuildings = settlement.getAvailableBuildings(name);
					Integer usedBuildings = settlement.getTotalBuildings(name);
					
					// Set:
					if(usedBuildings > 0){
						
						// Status:
						status = definitions[j].getEffect(score);
						if(status.length() == 0) status = "set";
						
						// Colours:
						name = positive + name;
						status = positive + status;
						
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
	
	private static String requirements(BuildingDefinition definition, Integer buildingLevel){
		
		
		StringBuffer result = new StringBuffer();
		
		// Level:
		Integer reqLevel = definition.getRequiredLevel();
		if(reqLevel > 0) result.append("lvl " + reqLevel);
		
		return result.toString();
		
		
	}
	
	public static String list(SagaPlayer sagaPlayer, Settlement settlement) {
		
		
		StringBuffer result = new StringBuffer();
		ColourLoop colours = new ColourLoop().addColor(normal1).addColor(normal2);
		
		result.append(listMembers(settlement));
		
		return TextUtil.frame(settlement.getName() + " members", result.toString(), colours.nextColour());
		
		
	}
	
	private static String listMembers(Settlement settlement){
		
		
		StringBuffer result = new StringBuffer();
		
		ChatColor general = normal1;
		ChatColor normal = normal2;
		
		int hMin = settlement.getDefinition().getHierarchyMin();
		int hMax = settlement.getDefinition().getHierarchyMax();
		
		// Hierarchy levels:
		for (int hierarchy = hMax; hierarchy >= hMin; hierarchy--) {
			
			if(result.length() > 0){
				result.append("\n");
				result.append("\n");
			}
			
			// Group name:
			String groupName = settlement.getDefinition().getHierarchyName(hierarchy);
			if(groupName.length() == 0) groupName = "-";
			result.append(GeneralMessages.tableTitle(general + groupName));
			
			// Role amounts:
			if(hierarchy != settlement.getDefinition().getHierarchyMin()){
				
				String amounts = settlement.getUsedRoles(hierarchy) + "/" + settlement.getAvailableRoles(hierarchy);
				
				if(settlement.isRoleAvailable(hierarchy)){
					amounts = positive + amounts;
				}else{
					amounts = negative + amounts;
				}
				
				result.append(" " + amounts);
				
			}else{
				
				String amounts = settlement.getUsedRoles(hierarchy) + "/-";
				result.append(" " + amounts);
				
			}
			
			// All roles:
			StringBuffer resultRoles = new StringBuffer();
			
			ArrayList<ProficiencyDefinition> roles = ProficiencyConfiguration.config().getDefinitions(ProficiencyType.ROLE, hierarchy);
			
			for (ProficiencyDefinition definition : roles) {
				
				// Members:
				if(resultRoles.length() > 0) resultRoles.append("\n");
				
				String roleName = definition.getName();
				ArrayList<String> members = settlement.getMembersForRoles(roleName);
				
				// Colour members:
				colourMembers(members, settlement);
				
				// Add members:
				resultRoles.append(normal);
				
				resultRoles.append(roleName + ": ");
				
				if(members.size() != 0){
					resultRoles.append(TextUtil.flatten(members));
				}else{
					resultRoles.append("none");
				}
				
			}
			
			result.append("\n");
			
			// Add roles:
			result.append(resultRoles);
			
		}
		
		return result.toString();
		
		
	}

	private static void colourMembers(ArrayList<String> members, Settlement settlement){
		
		for (int i = 0; i < members.size(); i++) {
			members.set(i, member(members.get(i), settlement));
		}
		
	}
	
	private static String member(String name, Settlement settlement){
		
		
		// Active:
		if(!settlement.isMemberActive(name)){
			return unavailable + "" + ChatColor.STRIKETHROUGH + name + normal1;
		}
		
		// Offline:
		else if(!settlement.isMemberOnline(name)){
			return unavailable + name + normal1;
		}
		
		// Normal:
		else{
			return normal1 + name;
		}
		
		
	}

	
	
	// Roles:
	public static String invalidRole(String roleName){
		return negative + "Role " + roleName + " is invalid.";
	}
	
	public static String newRole(SagaPlayer sagaPlayer, ChunkBundle settlement, String roleName) {
		
		return announce + sagaPlayer.getName() + " is now a " + roleName + ".";
		
	}

	public static String roleNotAvailable(String roleName) {

		return negative + "No " + roleName + " roles are available.";
		
	}
	
	
	
	// Levels and building points:
	public static String settleLevelBcast(Settlement settlement) {
		return announce + "Settlement " + settlement.getName() + " is now level " + settlement.getLevel() + ".";
	}
	
	public static String notEnoughBuildingPoints(Building building) {
		return negative + "Not enough build points.";
	}

	public static String noBuilding() {
		return negative + "There is no building on this chunk of land.";
	}

	
	
	// Rename:
	public static String renamed(ChunkBundle chunkBundle) {

		return announce + "Settlement was renamed to " + chunkBundle.getName() + ".";
		
	}
	
	
	
	// Options:
	public static String optionToggle(ChunkBundle chunkBundle, ChunkBundleToggleable option) {
		
		if(chunkBundle.isOptionEnabled(option)){
			return positive + "Enabled " + option + " for " + chunkBundle.getName() + ".";
		}else{
			return positive + "Disabled " + option + " for " + chunkBundle.getName() + ".";
		}

	}
	
	public static String optionAlreadyEnabled(ChunkBundle chunkBundle, ChunkBundleToggleable option){
		
		return negative + "Option " + option.toString() + " is already enabled for " + chunkBundle.getName() + " settlement.";
		
	}
	
	public static String optionAlreadyDisabled(ChunkBundle chunkBundle, ChunkBundleToggleable option){
		
		return negative + "Option " + option.toString() + " is already disabled for " + chunkBundle.getName() + " settlement.";
		
	}
	
	public static String optionInvalid(String option){
		
		return negative + "Option " + option + " is not valid.";
		
	}
	
	public static String optionInvalidInfo(){
		
		
		ChunkBundleToggleable[] options = ChunkBundleToggleable.values();
		ArrayList<String> validOptions = new ArrayList<String>();
		for (int i = 0; i < options.length; i++) {
			
			validOptions.add(options[i].toString().replace(" ", GeneralMessages.SPACE_SYMBOL));
			
		}
		
		return normal1 + "Valid options: " + TextUtil.flatten(validOptions) + ".";

		
	}
	
	
	
	// Move:
	public static String entered(ChunkBundle chunkBundle) {
		
		// Claimed:
		if(FactionClaimManager.manager().hasOwnerFaction(chunkBundle.getId())){
			Faction faction = FactionClaimManager.manager().getOwningFaction(chunkBundle.getId());
			return normal1 + "[" + FactionMessages.faction(faction, normal1) + "]" + ChatColor.ITALIC + " Entered " + chunkBundle.getName() + " settlement.";
		}
		
		return normal1 + "" + ChatColor.ITALIC + "Entered " + chunkBundle.getName() + " settlement.";
		
	}
	
	public static String left(ChunkBundle chunkBundle) {

		// Claimed:
		if(FactionClaimManager.manager().hasOwnerFaction(chunkBundle.getId())){
			Faction faction = FactionClaimManager.manager().getOwningFaction(chunkBundle.getId());
			return normal1 + "[" + FactionMessages.faction(faction, normal1) + "]" + ChatColor.ITALIC + " Left " + chunkBundle.getName() + " settlement.";
		}
		
		return normal1 + "" + ChatColor.ITALIC + "Left " + chunkBundle.getName() + " settlement.";
		
	}
	
	
	
	// Info:
	public static String wrongQuit() {
		
		return negative + "Because /squit and /fquit are similar, this command isn't used. Please use /settlementquit instead.";
		
	}


	
	// Map:
	public static String map(SagaPlayer sagaPlayer, Location location){
		
		
		ArrayList<String> map = SagaMap.getMap(sagaPlayer, sagaPlayer.getLocation());
		StringBuffer result = new StringBuffer();
		
		// Add borders:
		result.append(" ");
		map.add(0, " ");
		for (int i = 0; i < map.size(); i++) {
			
			if(i != 0) result.append("\n");
			
			result.append("  " + map.get(i) + "  ");
			
		}
		result.append(" ");
		
		Chunk locationChunk = location.getWorld().getChunkAt(location);
		String title = locationChunk.getWorld().getName() + " map " + "(" + locationChunk.getX() + ", " + locationChunk.getZ() + ")";
		
		return TextUtil.frame(title, result.toString(), ChatColor.GOLD);
		
		
	}
	
	
	
	// Admin:
	public static String setLevel(Settlement settlement){
		
		return positive + TextUtil.capitalize(settlement.getName()) + " level set to " +settlement.getLevel() + "."  ;
		
	}
	
	
	
	// Creating:
	public static String invalidName() {
		
		return negative + "Name must be " + SettlementCommands.minimumNameLenght + "-" + SettlementCommands.maximumNameLength + ". Letters and numbers only.";
		
	}
	
	
}
