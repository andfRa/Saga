package org.saga.messages;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.saga.SagaLogger;
import org.saga.abilities.Ability;
import org.saga.abilities.AbilityDefinition;
import org.saga.buildings.Building;
import org.saga.buildings.BuildingDefinition;
import org.saga.buildings.TownSquare;
import org.saga.buildings.TradingPost;
import org.saga.buildings.production.ProductionBuilding;
import org.saga.buildings.production.SagaItem;
import org.saga.buildings.production.SagaPricedItem;
import org.saga.buildings.production.SagaResource;
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
import org.saga.factions.FactionManager;
import org.saga.factions.SiegeManager;
import org.saga.factions.WarManager;
import org.saga.messages.colours.Colour;
import org.saga.messages.colours.ColourLoop;
import org.saga.player.GuardianRune;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.player.ProficiencyDefinition;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Bundle;
import org.saga.settlements.BundleManager;
import org.saga.settlements.Settlement;
import org.saga.utility.Duration;
import org.saga.utility.chat.ChatBook;
import org.saga.utility.chat.ChatFiller;
import org.saga.utility.chat.ChatFramer;
import org.saga.utility.chat.ChatTable;
import org.saga.utility.chat.ChatUtil;
import org.saga.utility.chat.RomanNumeral;

public class StatsMessages {

	
	// Player stats:
	public static String stats(SagaPlayer sagaPlayer, Integer page) {

		
		ChatBook book = new ChatBook("stats", new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2));
		
		// Attributes and levels:
		book.addTable(physical(sagaPlayer));
		book.addLine("");
		book.addTable(info(sagaPlayer));

		book.nextPage();
		
		// Abilities:
		book.addTable(abilities(sagaPlayer));

		book.nextPage();
		
		// Invites:
		book.addTable(invites(sagaPlayer));
		
		return book.framedPage(page);

		
	}
	
	
	private static ChatTable physical(SagaPlayer sagaPlayer) {

		
		ChatTable table = new ChatTable(new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2));
		DecimalFormat format = new DecimalFormat("00");

		// Health:
		table.addLine("health", ChatUtil.round((double)sagaPlayer.getHealth(), 0) + "/" + ChatUtil.round((double)sagaPlayer.getTotalHealth(), 0), 0);

		// Stamina:
		table.addLine("energy", ChatUtil.round(100.0 * sagaPlayer.getEnergy() / sagaPlayer.calcMaxEnergy(), 0) + "%", 0);

		// Attribute exp:
		table.addLine("next attr.", (int)(100.0 - 100.0 * sagaPlayer.getAttributeRemainingExp() / ExperienceConfiguration.config().getAttributePointCost()) + "%", 0);
		
		// Ability exp:
		table.addLine("next abil.", (int)(100.0 - 100.0 * sagaPlayer.getAbilityRemainingExp() / ExperienceConfiguration.config().getAbilityPointCost()) + "%", 0);
		
		table.addLine("","", 0);
		
		// Attributes:
		String score = " ";
		ArrayList<String> attrNames = AttributeConfiguration.config().getAttributeNames();
		for (String attrName : attrNames) {
			
			Integer attrBonus = sagaPlayer.getAttrScoreBonus(attrName);
			Integer attrScore = sagaPlayer.getRawAttributeScore(attrName);
			
			String scoreCurr = format.format(attrScore + attrBonus);
			String scoreMax = format.format(sagaPlayer.getAttributeCap(attrName));
			
			score = scoreCurr + "/" + scoreMax;
			
			// Colours:
			if(attrBonus > 0){
				score = Colour.positive + score;
			}
			else if(attrBonus < 0){
				score = Colour.negative + score;
			}
			
			table.addLine(attrName, score, 0);
			
		}
		
		// Available attributes:
		String attrPoints = "" + sagaPlayer.getRemainingAttributePoints() + "";
		String filler = ChatFiller.fillString(attrPoints, ChatFiller.calcLength(score));
		filler = filler.replace(attrPoints, "");
		
		if(sagaPlayer.getRemainingAttributePoints() < 0){
			attrPoints = ChatColor.DARK_RED + attrPoints;
		}
		else if (sagaPlayer.getRemainingAttributePoints() > 0) {
			attrPoints = ChatColor.DARK_GREEN + attrPoints;
		}else{
			attrPoints = Colour.unavailable + attrPoints;
		}
		
		attrPoints = filler + attrPoints;
		
		table.addLine("", attrPoints, 0);
		
		// Abilities:
		ArrayList<AbilityDefinition> definitions = AbilityConfiguration.config().getDefinitions();
		
		for (AbilityDefinition definition : definitions) {
			
			if(!definition.checkProficiencies(sagaPlayer)) continue;
			
			String name = definition.getName();
			Integer actScore = sagaPlayer.getAbilityScore(definition.getName());
			score = actScore + "/" + AbilityConfiguration.config().maxAbilityScore;
			
			// Colours:
			Integer rawScore = sagaPlayer.getRawAbilityScore(definition.getName());
			if(actScore > rawScore){
				score = Colour.positive + score;
			}
			else if(actScore < rawScore){
				score = Colour.negative + score;
			}
			
			table.addLine(name, score, 2);
			
		}
		
		// Available abilities:
		String abilPoints = "" + sagaPlayer.getRemainingAbilityPoints();
		filler = ChatFiller.fillString(abilPoints, ChatFiller.calcLength(score));
		filler = filler.replace(abilPoints, "");
		
		if(sagaPlayer.getRemainingAbilityPoints() < 0){
			abilPoints = ChatColor.DARK_RED + abilPoints;
		}
		else if (sagaPlayer.getRemainingAbilityPoints() > 0) {
			abilPoints = ChatColor.DARK_GREEN + abilPoints;
		}else{
			abilPoints = Colour.unavailable + abilPoints;
		}
		
		abilPoints = filler + abilPoints;
		
		table.addLine("", abilPoints, 2);
		
		table.collapse();
		
		return table;
		

	}
	
	private static ChatTable info(SagaPlayer sagaPlayer) {
		
		
		ChatTable table = new ChatTable(new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2));
		
		// Wallet:
		table.addLine("wallet", EconomyMessages.coins(EconomyDependency.getCoins(sagaPlayer)), 0);
		
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
			table.addLine("guard rune", rune, 2);
		}
		
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
    			
    			Integer nextScore = sagaPlayer.getRawAbilityScore(ability.getName()) + 1;
    			if(!ability.getDefinition().checkRequirements(sagaPlayer, nextScore)){
    				name = Colour.unavailable + name;
    				required = Colour.unavailable + required;
    			}
    			
    			if(ability.getScore() < AbilityConfiguration.config().maxAbilityScore){
    				
    				String requirements = requirements(ability.getDefinition(), nextScore);
    				String restrictions = restrictions(ability.getDefinition());
    				
    				if(restrictions.length() > 0){
    					if(requirements.length() > 0) requirements+= ", ";
    					requirements+= restrictions;
    				}
    				
    				required+= requirements;
    				
    			}else{
    				required = "-";
    			}
    			
    			table.addLine(new String[]{name, required});
    			
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
	public static String stats(Settlement settlement, Integer page) {
		
		
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

		// Claims:
		table.addLine("claim points", settlement.getUsedClaimed() + "/" + settlement.getTotalClaims(), 0);

		// Building points:
		table.addLine("build points", settlement.getUsedBuildPoints() + "/" + settlement.getAvailableBuildPoints(), 0);

		// Next claim:
		double claimProgress = settlement.getClaimProgress();
		table.addLine("next cpoint", (int)(claimProgress*100) + "%", 0);
		
		// Next building point:
		double buildPointProgress = settlement.getBuildPointsProgress();
		table.addLine("next bpoint", (int)(buildPointProgress*100) + "%", 0);

		// Owner:
		if(settlement.hasOwner()){
			table.addLine("owner", settlement.getOwner(), 0);
		}else{
			table.addLine("owner", Colour.veryNegative + "none", 0);
		}

		// Owner:
		String ownerStr = "none";
		Faction owner = SiegeManager.manager().getOwningFaction(settlement.getId());
		if(owner != null) ownerStr = FactionMessages.faction(owner, Colour.normal1);
		table.addLine("owner faction" ,ownerStr , 2);
		
		// Affiliation:
		String affilStr = "none";
		Faction affiliation = SiegeManager.manager().getAffiliationFaction(settlement.getId());
		if(affiliation != null) affilStr = FactionMessages.faction(affiliation, Colour.normal1);
		table.addLine("affiliation" ,affilStr , 2);
		
		// Economy:
		if(EconomyConfiguration.config().isEnabled()){
			
			Integer claims = settlement.getTotalClaims();

			// Claim cost:
			table.addLine("claim cost", EconomyMessages.coins(EconomyConfiguration.config().getClaimPointCost(claims)) , 2);

			// Build point cost:
			table.addLine("build point cost", EconomyMessages.coins(EconomyConfiguration.config().getBuildPointCost(claims)) , 2);

			// Banked:
			table.addLine("banked", EconomyMessages.coins(settlement.getCoins()), 2);
			
		}
		
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
				return arg0.getRequiredClaimed() - arg1.getRequiredClaimed();
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
		Integer reqSize = definition.getRequiredClaimed();
		if(reqSize > 0) result.append("claimed " + reqSize);
		
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
		
		// Info:
		book.addTable(info(faction));

		book.addLine("");
		
		// Allies, enemies and sieges:
		book.addTable(alliesEnemiesSieges(faction));
		
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

		// Settlements:
		String strCapital = "none";
		Bundle capital = SiegeManager.manager().getCapital(faction.getId());
		if(capital != null) strCapital = capital.getName();
		table.addLine("capital", strCapital, 0);

		// Economy:
		if(EconomyConfiguration.config().isEnabled()){

			// Siege cost:
			ArrayList<Integer> owned = SiegeManager.manager().getOwnedBundleIDs(faction.getId());
			Integer size = owned.size();
			table.addLine("siege cost", EconomyMessages.coins(EconomyConfiguration.config().getSiegeCost(size)), 2);

			// War start cost:
			table.addLine("war start cost", EconomyMessages.coins(EconomyConfiguration.config().getWarStartCost(size)), 2);

			// War start cost:
			table.addLine("war end cost", EconomyMessages.coins(EconomyConfiguration.config().getWarEndCost(size)), 2);

			// Banked:
			table.addLine("banked", EconomyMessages.coins(faction.getCoins()), 2);
			
		}

		table.collapse();
		
		return table;
		
		
	}
	
	private static ChatTable alliesEnemiesSieges(Faction faction){
		
		
		ColourLoop colours = new ColourLoop().addColor(faction.getColour2());
		ChatTable table = new ChatTable(colours);

		ArrayList<Faction> allies = WarManager.manager().getAllyDeclarations(faction.getId());
		ArrayList<Faction> allyInvites = FactionManager.manager().getFactions(faction.getAllyInvites());
		ArrayList<Faction> enemies = WarManager.manager().getWarDeclarations(faction.getId());
		
		ArrayList<Bundle> attack = SiegeManager.manager().getDeclaredSiegesAttack(faction.getId());
		ArrayList<Bundle> defend = SiegeManager.manager().getDeclaredSiegesDefend(faction.getId());
		
		// Names:
		String separator = "  ";
		table.addLine(new String[]{GeneralMessages.columnTitle("faction"), GeneralMessages.columnTitle("relation"), separator, GeneralMessages.columnTitle("settlement"), GeneralMessages.columnTitle("siege")});
		
		// Left:
		if(enemies.size() != 0 || allies.size() != 0 || allyInvites.size() != 0){

			// Enemies:
			for (Faction enemy : enemies) {
				String name = FactionMessages.faction(enemy, faction.getColour2());
				String relation = "at war";
				table.addLine(name, relation, 0);
			}
			
			// Allies:
			for (Faction ally : allies) {
				String name = FactionMessages.faction(ally, faction.getColour2());
				String relation = "ally";
				table.addLine(name, relation, 0);
			}
			
			// Ally invites:
			for (Faction enemy : allyInvites) {
				String name = FactionMessages.faction(enemy, faction.getColour2());
				String relation = "ally invite";
				table.addLine(name, relation, 0);
			}

		}else{
			table.addLine("-", "-", 0);
		}
		
		// Right:
		if(attack.size() != 0 || defend.size() != 0){

			// Siege attack:
			for (Bundle bundle : attack) {
				table.addLine(bundle.getName(), siegeAttack(faction, bundle), 3);
			}
			
			// Siege defend:
			for (Bundle bundle : defend) {
				table.addLine(bundle.getName(), siegeDefend(faction, bundle), 3);
			}
			
		}else{
			table.addLine("-", "-", 3);
		}
		
		table.collapse();
		
		return table;
		
		
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
			
			Hashtable<String, Double> allAvailRanks = SiegeManager.manager().getRanks(faction.getId());
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
		
		// Limited members:
		if(FactionConfiguration.config().isLimitedMembershipEnabled()){
			
			result.append("\n");
			result.append("\n");
			
			Collection<SagaPlayer> onlineLimted = faction.getLimitedOnlineMembers();
			
			// Naming:
			result.append(general + GeneralMessages.tableTitle("limited membership"));
			
			result.append("\n");
			
			if(onlineLimted.size() > 0){
				
				Collection<String> limitedNames = new ArrayList<String>();
				
				for (SagaPlayer sagaPlayer : onlineLimted) {
					limitedNames.add(sagaPlayer.getName());
				}
				
				result.append(normal);
				result.append(ChatUtil.flatten(limitedNames));
				
			}else{
				
				result.append(normal + "none online");
				
			}
			
		}
		
		return result.toString();
		
		
	}

	private static ChatTable claimed(Faction faction){
		
		
		ArrayList<Settlement> settlements = SiegeManager.manager().getOwnedSettlements(faction.getId());
		
		ColourLoop colours = new ColourLoop().addColor(faction.getColour2());
		ChatTable table = new ChatTable(colours);

		
		// Titles:
		table.addLine(new String[]{GeneralMessages.columnTitle("settlement"), GeneralMessages.columnTitle("location"), GeneralMessages.columnTitle("closest"), GeneralMessages.columnTitle("distance")});
		
		if(settlements.size() > 0){
			
			for (int i = 0; i < settlements.size(); i++) {
				
				Settlement settlement = settlements.get(i);
				
				String name = settlement.getName();
				Location location = retTownSquareLoc(settlement);
				
				String locationStr = "no " + HelpMessages.townSquare();
				if(location != null) locationStr = location(location);
				
				String closestName = "none";
				Location closestLocation = null;

				Settlement closestSettle = closest(settlement, settlements);
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

	private static Settlement closest(Settlement settlement, ArrayList<Settlement> otherSettles){
		
		
		Double minDistSuared = Double.MAX_VALUE;
		Settlement closestSettlement = null;
				
		// Location:
		Location location = retTownSquareLoc(settlement);
		if(location == null) return null;
				
		for (int i = 0; i < otherSettles.size(); i++) {
			
			Settlement otherSettlement = otherSettles.get(i);
			
			// Same settlement:
			if(settlement == otherSettlement) continue;
			
			// Town square:
			Location otherLocation = retTownSquareLoc(otherSettlement);
			if(otherLocation == null) continue;
			
			// Other world:
			if(!otherLocation.getWorld().getName().equalsIgnoreCase(location.getWorld().getName())) continue;
			
			Double distSqared = otherLocation.distanceSquared(location);
			if(distSqared < minDistSuared){
				minDistSuared = distSqared;
				closestSettlement = otherSettlement;
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
	
	
	private static String siegeAttack(Faction faction, Bundle bundle) {
		
		
		// Time:
		Integer minutes = SiegeManager.manager().getSiegeRemainingMinutes(bundle.getId());
		if(minutes == null){
			SagaLogger.severe(StatsMessages.class, "siege remaining minutes not defined for faction " + faction);
			minutes = 0;
		}
		long millis = minutes*60*1000;
		
		// Siege not started:
		if(millis > 0){
			Duration durationHM = new Duration(millis);
			return "attack in " + GeneralMessages.durationDHM(durationHM);
		}
		
		// Siege started:
		else{
			
			// Counts:
			int attackers = SiegeManager.manager().getAttackerCount(bundle.getId());
			int defenders = SiegeManager.manager().getDefenderCount(bundle.getId());
			double side = SiegeManager.manager().getSiegePtsPerSecond(bundle.getId(), attackers-defenders);
			
			// Colours:
			ChatColor colRight = Colour.normal1;
			ChatColor colLeft = faction.getColour1();
			ChatColor colGeneral = faction.getColour2();
			Faction owningFaction = SiegeManager.manager().getOwningFaction(bundle.getId());
			if(owningFaction != null) colRight = owningFaction.getColour1();
			
			// Progress:
			Double progress = -SiegeManager.manager().getSiegeProgress(bundle.getId());
			
			// Bar:
			if(side < 0){
				return attackers + "" + GeneralMessages.tugBarLeft(colLeft, colRight, colGeneral, progress) + colGeneral + "" + defenders;
			}
			
			else if(side > 0){
				return attackers + "" + GeneralMessages.tugBarRight(colLeft, colRight, colGeneral, progress) + colGeneral + "" + defenders;
			}
			
			else{
				return attackers + "" + GeneralMessages.tugBarStop(colLeft, colRight, colGeneral, progress) + colGeneral + "" + defenders;
			}
			
		}
		
	}

	private static String siegeDefend(Faction faction, Bundle bundle) {

		
		// Time:
		Integer minutes = SiegeManager.manager().getSiegeRemainingMinutes(bundle.getId());
		if(minutes == null){
			SagaLogger.severe(StatsMessages.class, "siege remaining minutes not defined for faction " + faction);
			minutes = 0;
		}
		long millis = minutes*60*1000;
		
		// Siege not started:
		if(millis > 0){
			Duration durationHM = new Duration(millis);
			return "defend in " + GeneralMessages.durationDHM(durationHM);
		}
		
		// Siege started:
		else{
			
			// Counts:
			int attackers = SiegeManager.manager().getAttackerCount(bundle.getId());
			int defenders = SiegeManager.manager().getDefenderCount(bundle.getId());
			double side = SiegeManager.manager().getSiegePtsPerSecond(bundle.getId(), attackers-defenders);
			
			// Colours:
			ChatColor colRight = Colour.normal1;
			ChatColor colLeft = faction.getColour1();
			ChatColor colGeneral = faction.getColour2();
			Faction owningFaction = SiegeManager.manager().getAttackingFaction(bundle.getId());
			if(owningFaction != null) colRight = owningFaction.getColour1();
			
			// Progress:
			Double progress = SiegeManager.manager().getSiegeProgress(bundle.getId());
			
			// Bar:
			if(side < 0){
				return defenders + " " + GeneralMessages.tugBarRight(colLeft, colRight, colGeneral, progress) + colGeneral + " " + attackers;
			}
			
			else if(side > 0){
				return defenders + " " + GeneralMessages.tugBarLeft(colLeft, colRight, colGeneral, progress) + colGeneral + " " + attackers;
			}
			
			else{
				return defenders + " " + GeneralMessages.tugBarStop(colLeft, colRight, colGeneral, progress) + colGeneral + " " + attackers;
			}
			
		}
		
	}
	
	
	
	// Building stats:
	public static String stats(Building building) {
		
		ChatBook book = new ChatBook(building.getName() + " stats", new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2));
		ChatTable table = new ChatTable(new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2));
		
		if(building instanceof ProductionBuilding){
			
			ProductionBuilding prBuilding = (ProductionBuilding) building;
			
			// Names:
			table.addLine(new String[]{GeneralMessages.columnTitle("progress"), GeneralMessages.columnTitle("resource"), GeneralMessages.columnTitle("materials")});

			// Production:
			if(((ProductionBuilding) building).resourcesLength() > 0){
				
				addProgress(table, prBuilding);
				table.collapse();
				book.addTable(table);
				
			}
			
			// Export:
			if(building instanceof TradingPost){
				
				TradingPost tpost = (TradingPost) building;
				addProgress(table, tpost);
				table.collapse();
				book.addTable(table);
				
			}
			
			if(book.lines() <= 1) table.addLine(new String[]{"-","-","-"});
			
			return book.framedPage(0);
			
		}
		
		else return Colour.negative + " Stats not available.";
		
	}
	
	private static ChatTable addProgress(ChatTable table, ProductionBuilding building) {
		
		
		SagaResource[] resources = building.getResources();
		
		// Values:
		if(resources.length != 0){
			
			// Recipes:
			for (int r = 0; r < resources.length; r++) {
				
				SagaResource resource = resources[r];
				
				// Duplicates:
				boolean duplic = false;
				if(r != 0 && resources[r-1].getType() == resource.getType()) duplic = true; 
				if(r != resources.length - 1 && resources[r+1].getType() == resource.getType()) duplic = true; 
				
				// Progress:
				String progress = (int)(resource.getWork().doubleValue() / resource.getRequiredWork() * 100) + "%";
				table.addLine(progress, 0);
				
				// Name:
				StringBuffer recipeName = new StringBuffer();
				if(resource.getAmount() > 1) recipeName.append(resource.getAmount().intValue() + " ");
				recipeName.append(GeneralMessages.material(resource.getType()));
				if(duplic) recipeName.append(":" + resource.getData());
				table.addLine(recipeName.toString(), 1);
				
				StringBuffer requirements = new StringBuffer();
				
				// Requirements:
				if(resource.recipeLength() != 0){
					
					for (int i = 0; i < resource.recipeLength(); i++) {
						
						SagaItem component = resource.getComponent(i);
						if(requirements.length() > 0) requirements.append(", ");
						requirements.append(GeneralMessages.material(component.getType()));
						if(duplic) requirements.append(":" + component.getData());
						
						requirements.append(" " + (int)resource.getCollected(i) + "/" + component.getAmount().intValue());
						
					}
					
				}else{
					requirements.append("-");
				}
				
				table.addLine(requirements.toString(), 2);
				
			}
			
		}
		
		table.collapse();
		
		return table;
		
		
	}

	private static ChatTable addProgress(ChatTable table, TradingPost building) {
		
		SagaPricedItem[] exports = EconomyConfiguration.config().getTradingPostExports();
		
		// Values:
		if(exports.length != 0){
			
			// Recipes:
			for (int r = 0; r < exports.length; r++) {
				
				SagaPricedItem export = exports[r];
				
				// Duplicates:
				boolean duplic = false;
				if(r != 0 && exports[r-1].getType() == export.getType()) duplic = true; 
				if(r != exports.length - 1 && exports[r+1].getType() == export.getType()) duplic = true; 
				
				// Progress:
				String progress = (int)(building.getWork(r) / export.getRequiredWork() * 100) + "%";
				table.addLine(progress, 0);
				
				// Name:
				table.addLine(EconomyMessages.coins(building.calcCost(r)), 1);
				
				StringBuffer requirements = new StringBuffer();
				
				// Requirement:
				requirements.append(GeneralMessages.material(export.getType()));
				if(duplic) requirements.append(":" + export.getData());
				requirements.append(" " + (int)building.getForExport()[r] + "/" + export.getAmount().intValue());
								
				table.addLine(requirements.toString(), 2);
				
			}
			
		}else{
			table.addLine(new String[]{"-","-","-"});
		}
		
		return table;
		
		
	}

	
	
	// Attribute and ability points:
	public static String gainedAttributePoints(Integer amount) {
		return Colour.veryPositive + "Gained " + amount + " attribute points.";
	}
	
	public static String gainedAbilityPoints(Integer amount) {
		return Colour.veryPositive + "Gained " + amount + " ability points.";
	}
	
	
}
