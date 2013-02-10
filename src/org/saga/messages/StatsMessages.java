package org.saga.messages;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.saga.abilities.Ability;
import org.saga.abilities.AbilityDefinition;
import org.saga.buildings.BuildingDefinition;
import org.saga.buildings.TownSquare;
import org.saga.config.AbilityConfiguration;
import org.saga.config.AttributeConfiguration;
import org.saga.config.BuildingConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.config.ExperienceConfiguration;
import org.saga.config.FactionConfiguration;
import org.saga.config.GeneralConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.SettlementConfiguration;
import org.saga.dependencies.EconomyDependency;
import org.saga.factions.Faction;
import org.saga.factions.FactionClaimManager;
import org.saga.factions.FactionManager;
import org.saga.messages.colours.Colour;
import org.saga.messages.colours.ColourLoop;
import org.saga.player.GuardianRune;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.player.ProficiencyDefinition;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Bundle;
import org.saga.settlements.BundleManager;
import org.saga.settlements.Settlement;
import org.saga.utility.chat.ChatBook;
import org.saga.utility.chat.ChatFramer;
import org.saga.utility.chat.ChatTable;
import org.saga.utility.chat.ChatUtil;
import org.saga.utility.chat.RomanNumeral;

public class StatsMessages {

	
	// Player stats:
	public static String stats(SagaPlayer sagaPlayer, Integer page) {

		
		ChatBook book = new ChatBook("stats", new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2));
		
		// Attributes and levels:
		book.addTable(info(sagaPlayer));
		book.addLine("");
		book.addTable(factionSettlement(sagaPlayer));

		book.nextPage();
		
		// Abilities:
		book.addTable(abilities(sagaPlayer));

		book.nextPage();
		
		// Invites:
		book.addTable(invites(sagaPlayer));
		
		return book.framedPage(page);

		
	}
	
	
	private static ChatTable info(SagaPlayer sagaPlayer) {

		
		ChatTable table = new ChatTable(new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2));
		DecimalFormat format = new DecimalFormat("00");
		
		// Attributes:
		ArrayList<String> attrNames = AttributeConfiguration.config().getAttributeNames();
		for (String attrName : attrNames) {
			
			Integer attrBonus = sagaPlayer.getAttrScoreBonus(attrName);
			Integer attrScore = sagaPlayer.getRawAttributeScore(attrName);
			
			String scoreCurr = format.format(attrScore + attrBonus);
			String scoreMax = format.format(AttributeConfiguration.config().maxAttributeScore + attrBonus);
			
			String score = scoreCurr + "/" + scoreMax;
			
			// Colours:
			if(attrBonus > 0){
				score = Colour.positive + score;
			}
			else if(attrBonus < 0){
				score = Colour.negative + score;
			}
			
			table.addLine(attrName, score, 0);
			
		}
		
		// Health:
		table.addLine("Health", ChatUtil.round((double)sagaPlayer.getHealth(), 0) + "/" + ChatUtil.round((double)sagaPlayer.getTotalHealth(), 0), 2);
		
		String attrPoints = sagaPlayer.getUsedAttributePoints() + "/" + sagaPlayer.getAvailableAttributePoints();
		if(sagaPlayer.getRemainingAttributePoints() < 0){
			attrPoints = ChatColor.DARK_RED + attrPoints;
		}
		else if (sagaPlayer.getRemainingAttributePoints() > 0) {
			attrPoints = ChatColor.DARK_GREEN + attrPoints;
		}
		table.addLine("Attributes", attrPoints, 2);

		// Exp:
		table.addLine("Progress", (int)(100.0 - 100.0 * sagaPlayer.getRemainingExp() / ExperienceConfiguration.config().getAttributePointCost()) + "%", 2);

		// Wallet:
		table.addLine("Wallet", EconomyMessages.coins(EconomyDependency.getCoins(sagaPlayer)), 2);
		
		// Guard rune:
		if(GeneralConfiguration.config().isRuneEnabled()){
			GuardianRune guardRune = sagaPlayer.getGuardRune();
			String rune = "";
			if(!guardRune.isEnabled()){
				rune = "disabled";
			}else{
				if(guardRune.isCharged()){
					rune= "charged";
				}else{
					rune= "discharged";
				}
			}
			table.addLine("Guard rune", rune, 2);
		}
		
		table.collapse();
		
		return table;
		

	}
	
	private static ChatTable factionSettlement(SagaPlayer sagaPlayer) {

		
		ChatTable table = new ChatTable(new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2));

		
		// Faction and settlement:
		String faction = "none";
		if(sagaPlayer.getFaction() != null) faction = sagaPlayer.getFaction().getName();

		String settlement = "none";
		if(sagaPlayer.getBundle() != null) settlement = sagaPlayer.getBundle().getName();
		
		table.addLine("faction", faction, 0);
		table.addLine("settlement", settlement, 2);
		
		// Rank and role:
		String rank = "none";
		if(sagaPlayer.getRank() != null) rank = sagaPlayer.getRank().getName();

		String role = "none";
		if(sagaPlayer.getRole() != null) role = sagaPlayer.getRole().getName();

		table.addLine("rank", rank, 0);
		table.addLine("role", role, 2);
		
		// Style:
		table.collapse();
		
		return table;
		

	}
	
	private static ChatTable abilities(SagaPlayer sagaPlayer) {

		
		ChatTable table = new ChatTable(new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2));
		HashSet<Ability> allAbilities = sagaPlayer.getAbilities();
		
    	// Add abilities:
    	if(allAbilities.size() > 0){
    		
    		for (Ability ability : allAbilities) {
    			
    			String name = ability.getName() + " " + RomanNumeral.binaryToRoman(ability.getScore());
    			String required = "";
    			String status = "";
    			
    			if(ability.getScore() == 0){
    				name = Colour.unavailable + name;
    				required = Colour.unavailable + required;
    				status = Colour.unavailable + status;
    			}
    			
    			if(ability.getScore() < AbilityConfiguration.config().maxAbilityScore){
    				
    				String requirements = requirements(ability.getDefinition(), ability.getScore() + 1);
    				String restrictions = restrictions(ability.getDefinition());
    				
    				
    				if(restrictions.length() > 0){
    					if(requirements.length() > 0) requirements+= ", ";
    					requirements+= restrictions;
    				}
    				
    				required+= requirements;
    				
    			}else{
    				required = "-";
    			}
    			
    			if(ability.getScore() == 0){
    				status+= "-";
    			}
    			else if(ability.getCooldown() <= 0){
					status+= "ready";
				}else{
					status = ability.getCooldown() + "s";
				}
    			
    			table.addLine(new String[]{name, required, status});
    			
    		}
    		
    	}
    	
    	// No abilities:
    	else{
    		table.addLine("-");
    	}
    	
    	table.collapse();
    	
		return table;
    	
		
	}

	private static ChatTable invites(SagaPlayer sagaPlayer) {

		
		ChatTable table = new ChatTable(new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2));
		
		// Table size:
    	ArrayList<Double> widths = new ArrayList<Double>();
    	widths.add(28.5);
    	widths.add(28.5);
    	table.setCustomWidths(widths);
		
    	// Factions:
    	table.addLine(GeneralMessages.columnTitle("faction invites"), 0);
    	
    	ArrayList<Faction> factions = getFactions(sagaPlayer.getFactionInvites());
    	
    	for (Faction faction : factions) {
			table.addLine(faction.getName(), 0);
		}
    	
    	if(factions.size() == 0){
    		table.addLine("-", 0);
    	}
    	
    	// Chunk groups:
    	table.addLine(GeneralMessages.columnTitle("settlement invites"), 1);
    	
    	ArrayList<Bundle> bundles = getSettlements(sagaPlayer.getBundleInvites());
    	
    	for (Bundle bundle : bundles) {
			table.addLine(bundle.getName(), 1);
		}
    	
    	if(bundles.size() == 0){
    		table.addLine("-", 1);
    	}
    	
		return table;
    	
		
	}
	
	private static ArrayList<Faction> getFactions(ArrayList<Integer> ids) {


		// Faction invites:
		ArrayList<Faction> factions = new ArrayList<Faction>();
		if(ids.size() > 0){
			
			for (int i = 0; i < ids.size(); i++) {
			
				Faction faction = FactionManager.manager().getFaction(ids.get(i));
				if( faction != null ){
					factions.add(faction);
				}else{
					ids.remove(i);
					i--;
				}
				
			}
		}
		
		return factions;
		
		
	}
	
	private static ArrayList<Bundle> getSettlements(ArrayList<Integer> ids) {


		// Faction invites:
		ArrayList<Bundle> bundles = new ArrayList<Bundle>();
		if(ids.size() > 0){
			
			for (int i = 0; i < ids.size(); i++) {
			
				Bundle faction = BundleManager.manager().getBundle(ids.get(i));
				if( faction != null ){
					bundles.add(faction);
				}else{
					ids.remove(i);
					i--;
				}
				
			}
		}
		
		return bundles;
		
		
	}
	

	public static String requirements(AbilityDefinition definition, Integer score) {

		
		StringBuffer result = new StringBuffer();
		
		// Attributes:
		ArrayList<String> attributeNames = AttributeConfiguration.config().getAttributeNames();
		
		for (String attribute : attributeNames) {
			
			Integer reqScore = definition.getAttrReq(attribute, score);
			if(reqScore <= 0) continue;
			
			if(result.length() > 0) result.append(", ");
			
			result.append(GeneralMessages.attrAbrev(attribute) + " " + reqScore);
			
		}

		// Buildings:
		List<String> buildings = definition.getBldgReq(score);
		if(buildings.size() > 0){
			if(result.length() > 0) result.append(", ");
			result.append(ChatUtil.flatten(buildings));
		}
		
		return result.toString();
		
		
	}
	
	public static String restrictions(AbilityDefinition definition) {

		StringBuffer result = new StringBuffer();
		
		// Proficiencies:
		HashSet<String> proficiencies = definition.getProfRestr();
		if(proficiencies.size() > 0) result.append(ChatUtil.flatten(proficiencies));
		
		return result.toString().replace(", ", "/");
		
	}
	
	
	
	// Settlement stats:
	public static String stats(SagaPlayer sagaPlayer, Settlement settlement, Integer page) {
		
		
		ChatBook book = new ChatBook(settlement.getName() + " stats", new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2));
		
		// Claims and active members:
		book.addTable(info(settlement));
		book.addLine("");
		book.addLine(GeneralMessages.tableTitle("required"));
		book.addTable(requirements(settlement));
		
		book.nextPage();

		// Buildings:
		book.addTable(buildings(settlement));

		book.nextPage();
		
		// Members:
		book.addLine(listMembers(settlement));
		
		return book.framedPage(page);

		
	}
	
	public static String list(SagaPlayer sagaPlayer, Settlement settlement) {
		
		
		StringBuffer result = new StringBuffer();
		ColourLoop colours = new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2);
		
		result.append(listMembers(settlement));
		
		return ChatFramer.frame(settlement.getName() + " members", result.toString(), colours.nextColour());
		
		
	}
	
	
	private static ChatTable info(Settlement settlement){
		
		
		ColourLoop colours = new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2);
		ChatTable table = new ChatTable(colours);
		
		// Building points:
		table.addLine("build points", settlement.getUsedBuildPoints() + "/" + settlement.getAvailableBuildPoints(), 0);
		
		// Owner:
		if(settlement.hasOwner()){
			table.addLine("owner", settlement.getOwner(), 0);
		}else{
			table.addLine("owner", Colour.veryNegative + "none", 0);
		}

		// Banked:
		if(EconomyConfiguration.config().isEnabled()){
			table.addLine("banked", EconomyMessages.coins(settlement.getCoins()), 0);
		}
		
		// Claims:
		double progress = settlement.getClaimProgress();
		table.addLine("size", settlement.getUsedClaimed() + "/" + settlement.getTotalClaims(), 2);

		// Next claim:
		table.addLine("next claim", (int)(progress*100) + "%", 2);

		table.collapse();
		
		return table;
		
		
	}
	
	private static ChatTable requirements(Settlement settlement){
		
		
		ColourLoop colours = new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2);
		ChatTable table = new ChatTable(colours);
		
		// Active players:
		Integer active = settlement.countActiveMembers();
		if(settlement.checkActiveMembers()){
			table.addLine(Colour.positive + "members", Colour.positive + active.toString() + "/" + SettlementConfiguration.config().getRequiredActiveMembers(settlement.getSize()), 0);
		}else{
			table.addLine(Colour.negative + "members", Colour.negative + active.toString() + "/" + SettlementConfiguration.config().getRequiredActiveMembers(settlement.getSize()), 0);
		}
		
		// Buildings:
		ArrayList<String> required = SettlementConfiguration.config().getSortedRequiredBuildings(settlement);
		for (String reqBldgName : required) {
			if(settlement.getFirstBuilding(reqBldgName) != null){
				table.addLine(Colour.positive + reqBldgName);
			}else{
				table.addLine(Colour.negative + reqBldgName);
			}
		}
		
		table.collapse();
		
		return table;
		
		
	}
	
	private static ChatTable buildings(Settlement settlement){
		
		
		ColourLoop colours = new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2);
		ChatTable table = new ChatTable(colours);
		
		// Retrieve buildings:
		BuildingDefinition[] definitions = BuildingConfiguration.config().getBuildingDefinitions().toArray(new BuildingDefinition[0]);
		
		// Sort required by size:
		Comparator<BuildingDefinition> comparator = new Comparator<BuildingDefinition>() {
			@Override
			public int compare(BuildingDefinition arg0, BuildingDefinition arg1) {
				return arg0.getRequiredSize() - arg1.getRequiredSize();
			}
		};
		Arrays.sort(definitions, comparator);
		
		// Column names:
		table.addLine(new String[]{GeneralMessages.columnTitle("building"), GeneralMessages.columnTitle("pts."), GeneralMessages.columnTitle("effect")});
		
		// Column values:
		if(definitions.length != 0){
			
			for (int j = 0; j < definitions.length; j++) {
				
				// Values:
				String name = definitions[j].getName();
				String points = definitions[j].getBuildPoints() + "";
				String effect = "";
				
				// Requirements met:
				if(definitions[j].checkRequirements(settlement, 1)){
					
					// Multiple buildings:
					Integer totalBuildings = settlement.getAvailableBuildings(name);
					Integer usedBuildings = settlement.getTotalBuildings(name);
					
					// Set:
					if(usedBuildings > 0){
						
						// Status:
						effect = definitions[j].getEffect();
						if(effect.length() == 0) effect = "set";
						
						// Colours:
						name = Colour.positive + name;
						effect = Colour.positive + effect;
						
						if(totalBuildings != 1){
							name = name + " " + usedBuildings + "/" + totalBuildings;
						}
					
					}
					
					// Available:
					else{
						effect = "not set";
					}
					
					
				}
				
				// Requirements not met:
				else{
					name = Colour.unavailable + name;
					effect = Colour.unavailable + "(" + requirements(definitions[j], 1) + ")";
				}
					
				table.addLine(new String[]{name, points, effect});
			
			}
			
		}else{
			table.addLine(new String[]{"-", "-", "-"});
		}
		
		table.collapse();
		
		return table;
		
		
	}
	
	private static String requirements(BuildingDefinition definition, Integer buildingLevel){
		
		
		StringBuffer result = new StringBuffer();
		
		// Level:
		Integer reqSize = definition.getRequiredSize();
		if(reqSize > 0) result.append("size " + reqSize);
		
		return result.toString();
		
		
	}
	
	private static String listMembers(Settlement settlement){
		
		
		StringBuffer result = new StringBuffer();
		
		ChatColor general = Colour.normal1;
		ChatColor normal = Colour.normal2;
		
		int hMin = SettlementConfiguration.config().getHierarchyMin();
		int hMax = SettlementConfiguration.config().getHierarchyMax();
		
		// Hierarchy levels:
		for (int hierarchy = hMax; hierarchy >= hMin; hierarchy--) {
			
			if(result.length() > 0){
				result.append("\n");
				result.append("\n");
			}
			
			// Group name:
			String groupName = SettlementConfiguration.config().getHierarchyName(hierarchy);
			if(groupName.length() == 0) groupName = "-";
			result.append(GeneralMessages.tableTitle(general + groupName));
			
			// All roles:
			StringBuffer resultRoles = new StringBuffer();
			
			ArrayList<ProficiencyDefinition> roles = ProficiencyConfiguration.config().getDefinitions(ProficiencyType.ROLE, hierarchy);
			
			for (ProficiencyDefinition roleDefinition : roles) {

				// Members:
				if(resultRoles.length() > 0) resultRoles.append("\n");
				
				String roleName = roleDefinition.getName();
				ArrayList<String> members = settlement.getMembersForRoles(roleName);
				
				// Colour members:
				colourMembers(members, settlement);
				
				// Add members:
				resultRoles.append(normal);
				resultRoles.append(roleName);
				
				// Amounts:
				Integer usedRoles = settlement.getUsedRoles(roleName);
				Integer availRoles = settlement.getAvailableRoles(roleName);
				
				if(roleDefinition.getHierarchyLevel() > FactionConfiguration.config().getHierarchyMin()){
					resultRoles.append(" " + usedRoles + "/" + availRoles.intValue());
				}
				
				resultRoles.append(": ");
				
				if(members.size() != 0){
					resultRoles.append(ChatUtil.flatten(members));
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
			return Colour.unavailable + "" + ChatColor.STRIKETHROUGH + name + Colour.normal1;
		}
		
		// Offline:
		else if(!settlement.isMemberOnline(name)){
			return Colour.unavailable + name + Colour.normal1;
		}
		
		// Normal:
		else{
			return Colour.normal1 + name;
		}
		
		
	}


	
	// Faction stats:
	public static String stats(Faction faction, Integer page) {

		
		ChatBook book = new ChatBook(faction.getName() + " stats", new ColourLoop().addColor(faction.getColour2()));
		
		// Levels, claims and allies:
		book.addTable(info(faction));
		book.addLine("");
		book.addLine(allies(faction));
		
		book.nextPage();
		
		// Members:
		book.addLine(listMembers(faction));
		
		book.nextPage();
		
		// Claimed:
		book.addTable(claimed(faction));
		
		return book.framedPage(page);

		
	}
	
	public static String list(Faction faction) {
		
		StringBuffer result = new StringBuffer();
		
		result.append(listMembers(faction));
		
		return ChatFramer.frame(faction.getName() + " members", result.toString(), Colour.normal1);
		
	}
	
	
	private static ChatTable info(Faction faction){
		
		
		ColourLoop colours = new ColourLoop().addColor(faction.getColour2());
		ChatTable table = new ChatTable(colours);
		
		// Colours:
		table.addLine("colour I", faction.getColour1() + ChatUtil.colour(faction.getColour1()), 0);
		
		// Building points:
		table.addLine("colour II", faction.getColour2() + ChatUtil.colour(faction.getColour2()), 0);
		
		// Owner:
		if(faction.hasOwner()){
			table.addLine("owner", faction.getOwner(), 0);
		}else{
			table.addLine("owner", Colour.veryNegative + "none", 0);
		}
		
		int claimed = FactionClaimManager.manager().findSettlementsIds(faction.getId()).length;
		int totalClaims = faction.getTotalClaims();
		double progress = faction.getClaimProgress();
		
		// Claimed:
		table.addLine("settlements", claimed + "/" + totalClaims, 2);

		// Next claim:
		table.addLine("next claim", (int)(progress*100) + "%", 2);

		table.collapse();
		
		return table;
		
		
	}
	
	private static String allies(Faction faction){
		
		
		StringBuffer result = new StringBuffer();

		ArrayList<String> allies = FactionManager.manager().getFactionNames(faction.getAllies());
		ArrayList<String> allyInvites = FactionManager.manager().getFactionNames(faction.getAllyInvites());
		
		// Allies:
		result.append("allies: ");
		if(allies.size() > 0){
			result.append(ChatUtil.flatten(allies));
		}else{
			result.append("none");
		}

		// Ally invites:
		if(allyInvites.size() > 0){
			
			result.append("\n");
			result.append("ally invites: " + ChatUtil.flatten(allyInvites));
			
		}
		
		return result.toString();
		
		
	}
	
	private static String listMembers(Faction faction){
		
		
		StringBuffer result = new StringBuffer();
		
		ChatColor general = faction.getColour1();
		ChatColor normal = faction.getColour2();
		
		int hMin = FactionConfiguration.config().getHierarchyMin();
		int hMax = FactionConfiguration.config().getHierarchyMax();
		
		// Hierarchy levels:
		for (int hierarchy = hMax; hierarchy >= hMin; hierarchy--) {
			
			if(result.length() > 0){
				result.append("\n");
				result.append("\n");
			}
			
			// Group name:
			String groupName = FactionConfiguration.config().getHierarchyName(hierarchy);
			if(groupName.length() == 0) groupName = "-";
			result.append(GeneralMessages.tableTitle(general + groupName));
			
			// All ranks:
			StringBuffer resultRanks = new StringBuffer();
			
			Hashtable<String, Double> allAvailRanks = FactionClaimManager.manager().getRanks(faction.getId());
			ArrayList<ProficiencyDefinition> allRanks = ProficiencyConfiguration.config().getDefinitions(ProficiencyType.RANK, hierarchy);
			
			for (ProficiencyDefinition definition : allRanks) {
				
				// Members:
				if(resultRanks.length() > 0) resultRanks.append("\n");
				
				String roleName = definition.getName();
				ArrayList<String> members = faction.getMembersForRanks(roleName);
				
				// Colour members:
				colourMembers(members, faction);
				
				// Add members:
				resultRanks.append(normal);
				resultRanks.append(roleName);
				
				// Amounts:
				Integer usedRanks = faction.getUsedRanks(roleName);
				Double availRanks = allAvailRanks.get(roleName);
				if(availRanks == null) availRanks = 0.0;
				
				if(definition.getHierarchyLevel() > FactionConfiguration.config().getHierarchyMin()){
					resultRanks.append(" " + usedRanks + "/" + availRanks.intValue());
				}
				
				resultRanks.append(": ");
				
				if(members.size() != 0){
					resultRanks.append(ChatUtil.flatten(members));
				}else{
					resultRanks.append("none");
				}
				
			}
			
			result.append("\n");
			
			// Add roles:
			result.append(resultRanks);
			
		}
		
		return result.toString();
		
		
	}

	private static ChatTable claimed(Faction faction){
		
		
		Settlement[] settlements = FactionClaimManager.manager().findSettlements(faction.getId());
		
		ColourLoop colours = new ColourLoop().addColor(faction.getColour2());
		ChatTable table = new ChatTable(colours);

		
		// Titles:
		table.addLine(new String[]{GeneralMessages.columnTitle("settlement"), GeneralMessages.columnTitle("location"), GeneralMessages.columnTitle("closest"), GeneralMessages.columnTitle("distance")});
		
		if(settlements.length > 0){
			
			for (int i = 0; i < settlements.length; i++) {
				
				String name = settlements[i].getName();
				Location location = retTownSquareLoc(settlements[i]);
				
				String locationStr = "no " + HelpMessages.townSquare();
				if(location != null) locationStr = location(location);
				
				String closestName = "none";
				Location closestLocation = null;

				Settlement closestSettle = closest(settlements[i], settlements);
				if(closestSettle != null){
					closestLocation = retTownSquareLoc(closestSettle);
					closestName = closestSettle.getName();
				}
				
				String distance = "-";
				if(location != null && closestLocation != null) distance = (int)location.distance(closestLocation) + "";
				
				table.addLine(new String[]{name, locationStr, closestName, distance});
			
			}
			
		}else{
			
			table.addLine(new String[]{"-", "-", "-", "-"});
			
		}
		
		table.collapse();
		
		return table;
		
		
	}
	
	
	private static void colourMembers(ArrayList<String> members, Faction faction){
		
		for (int i = 0; i < members.size(); i++) {
			members.set(i, member(members.get(i), faction));
		}
		
	}
	
	private static String member(String name, Faction faction){
		
		
		// Active:
		if(!faction.isMemberActive(name)){
			return Colour.unavailable + "" + ChatColor.STRIKETHROUGH + name + Colour.normal1;
		}
		
		// Offline:
		else if(!faction.isMemberOnline(name)){
			return Colour.unavailable + name + faction.getColour2();
		}
		
		// Normal:
		else{
			return faction.getColour2() + name;
		}
		
		
	}

	private static Settlement closest(Settlement settlement, Settlement[] otherSettles){
		
		
		Double minDistSuared = Double.MAX_VALUE;
		Settlement closestSettlement = null;
				
		// Location:
		Location location = retTownSquareLoc(settlement);
		if(location == null) return null;
				
		for (int i = 0; i < otherSettles.length; i++) {
			
			// Same settlement:
			if(settlement == otherSettles[i]) continue;
			
			// Town square:
			Location otherLocation = retTownSquareLoc(otherSettles[i]);
			if(otherLocation == null) continue;
			
			// Other world:
			if(!otherLocation.getWorld().getName().equalsIgnoreCase(location.getWorld().getName())) continue;
			
			Double distSqared = otherLocation.distanceSquared(location);
			if(distSqared < minDistSuared){
				minDistSuared = distSqared;
				closestSettlement = otherSettles[i];
			}
			
		}
		
		return closestSettlement;
		
		
	}

	private static String location(Location location){
		
		
		StringBuffer result = new StringBuffer();
		
		if(location != null){
			result.append(location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()); 

			if(!location.getWorld().getName().equals(GeneralConfiguration.config().getDefaultWorld())) result.insert(0, location.getWorld().getName() + " ");
			
		}
		
		return result.toString();
		
		
	}
	
	private static Location retTownSquareLoc(Settlement settlement) {

		ArrayList<TownSquare> townSquares = settlement.getBuildings(TownSquare.class);
		if(townSquares.size() == 0) return null;
		return townSquares.get(0).getSagaChunk().getCenterLocation();
		
	}
	
	
	
	// Attribute points:
	public static String gaineAttributePoints(Integer amount) {
		
		return Colour.veryPositive + "Gained " + amount + " attribute points.";
		
	}
	
	
}
