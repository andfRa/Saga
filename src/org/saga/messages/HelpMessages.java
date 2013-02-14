package org.saga.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.saga.abilities.AbilityDefinition;
import org.saga.attributes.Attribute;
import org.saga.buildings.Academy;
import org.saga.buildings.BuildingDefinition;
import org.saga.buildings.TownSquare;
import org.saga.buildings.TradingPost;
import org.saga.buildings.TrainingCamp;
import org.saga.buildings.Warehouse;
import org.saga.buildings.signs.AttributeSign;
import org.saga.buildings.signs.BuySign;
import org.saga.buildings.signs.ExportSign;
import org.saga.buildings.signs.GuardianRuneSign;
import org.saga.buildings.signs.ImportSign;
import org.saga.buildings.signs.SellSign;
import org.saga.config.AbilityConfiguration;
import org.saga.config.AttributeConfiguration;
import org.saga.config.BuildingConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.config.FactionConfiguration;
import org.saga.config.GeneralConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.SettlementConfiguration;
import org.saga.messages.colours.Colour;
import org.saga.messages.colours.ColourLoop;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.player.ProficiencyDefinition;
import org.saga.settlements.BundleToggleable;
import org.saga.utility.chat.ChatBook;
import org.saga.utility.chat.ChatTable;
import org.saga.utility.chat.ChatUtil;
import org.saga.utility.chat.RomanNumeral;

public class HelpMessages {

	
	// Help:
	public static String ehelp(int page) {

		
		ColourLoop colours = new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2);
		ChatBook book = new ChatBook("economy help", colours);
		
		String tradingPost = tradingPost();
		String warehouse = warehouse();

		// Trading post:
		book.addLine("Each settlement can set a " + tradingPost + " by using " + GeneralMessages.command("/bset") + ". " +
			"A " + tradingPost + " can have four different signs. "
		);
		
		book.addLine(ExportSign.SIGN_NAME  + " and " + ImportSign.SIGN_NAME + " signs let players export/import certain items for predefined prices. ");
		
		book.addLine(SellSign.SIGN_NAME + " and " + BuySign.SIGN_NAME + " allow members to set their own prices. " +
			"In order for the " + SellSign.SIGN_NAME + " and " + BuySign.SIGN_NAME + " signs to work, the signs must first collect items from a " + warehouse + " and " + EconomyMessages.coins() + " from the settlements bank."
		);
		
		book.addLine("If players with certain roles are online, then the " + tradingPost + " automatically takes items from a " + warehouse + " and sells them. " +
			"Earned " + EconomyMessages.coins() + " are distributed."
		);
		
		book.nextPage();
		
		// Settlement wages:
		book.addLine("Each time someone buys from a " + tradingPost + " or when a " + tradingPost + " automatically exports items, the settlement gains " + EconomyMessages.coins() + ".");
		
		book.addLine("");
		
		// Settlement distribution table:
		ChatTable sDistTable = new ChatTable(colours);
		
		sDistTable.addLine("settlement bank", ChatUtil.displayPercent(EconomyConfiguration.config().getSettlementPercent()),0);
		
		double membPerc = EconomyConfiguration.config().getSettlementMemberPercent();
		int min = SettlementConfiguration.config().getHierarchyMin();
		int max = SettlementConfiguration.config().getHierarchyMax();
		for (int i = max; i >= min; i--) {
			String name = SettlementConfiguration.config().getHierarchyName(i);
			sDistTable.addLine(name, ChatUtil.displayPercent(membPerc*EconomyConfiguration.config().getSettlementWagePercent(i)),0);
		}
		
		sDistTable.addLine("faction", ChatUtil.displayPercent(EconomyConfiguration.config().getSettlementFactionPercent()),0);
		
		sDistTable.collapse();

		book.addTable(sDistTable);
		
		book.addLine("");
		
		// Faction wages:
		book.addLine("Faction receives " + EconomyMessages.coins() + " from owned settlements. " +
			"Earned coins are distributed between faction band and online members:"
		);
		
		book.addLine("");
		
		// Faction distribution table:
		ChatTable fDistTable = new ChatTable(colours);
		
		fDistTable.addLine("faction bank", ChatUtil.displayPercent(EconomyConfiguration.config().getFactionPercent()),0);
		
		membPerc = EconomyConfiguration.config().getFactionMemberPercent();
		min = FactionConfiguration.config().getHierarchyMin();
		max = FactionConfiguration.config().getHierarchyMax();
		for (int i = max; i >= min; i--) {
			String name = FactionConfiguration.config().getHierarchyName(i);
			fDistTable.addLine(name, ChatUtil.displayPercent(membPerc*EconomyConfiguration.config().getFactionWagePercent(i)),0);
		}
		
		fDistTable.collapse();

		book.addTable(fDistTable);
		
		book.nextPage();
		
		// Settlement buy:
		int minClaims = SettlementConfiguration.config().getInitialClaims();
		int maxClaims = SettlementConfiguration.config().getMaxClaims();
		double claimMinCost = EconomyConfiguration.config().getClaimPointCost(minClaims);
		double claimMaxCost = EconomyConfiguration.config().getClaimPointCost(maxClaims);
		double bpointMinCost = EconomyConfiguration.config().getBuildPointCost(minClaims);
		double bpointMaxCost = EconomyConfiguration.config().getBuildPointCost(maxClaims);
		
		book.addLine("Use " + GeneralMessages.command("/sdeposit") + " and " + GeneralMessages.command("/swithdraw") + " to add and remove coins from the settlements bank. " +
			"Commands " + GeneralMessages.command("/sbuyclaims") + " and " + GeneralMessages.command("/sbuybuildpoints") + " allow to buy more claim and build points. " +
			"The more claims the settlement has, the more buying new points will cost. " +
			"Claim points cost " + EconomyMessages.coins(claimMinCost) + " - " + EconomyMessages.coins(claimMaxCost) + ". " +
			"Build points cost " + EconomyMessages.coins(bpointMinCost) + " - " + EconomyMessages.coins(bpointMaxCost) + "." 
		);
		
		book.addLine("Use " + GeneralMessages.command("/fdeposit") + " and " + GeneralMessages.command("/fwithdraw") + " to add and remove coins from the factions bank. "
		);
		
		return book.framedPage(page);
		
		
	}
	
	public static String phelp(int page) {
		
		
		ColourLoop messageColor = new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2);
		ChatBook book = new ChatBook("player help", messageColor);

		// Attributes:
		String trainingCamp = trainingCamp();
		book.addLine( 
			"Players gain attribute points from killing creatures, getting crafting materials and pvp. " +
			"Attribute points can be used to increase attribute scores. " +
			"Higher attribute scores make you stronger." +
			"Attributes can be increased by interacting with " + AttributeSign.SIGN_NAME + " signs. " +
			"Training signs can only be set at a " + trainingCamp + " building. " +
			"Use " + GeneralMessages.command("/stats") + " to see your attributes."
		);
		
		book.addLine("");
		
		// Attribute table:
		ChatTable attrTable = new ChatTable(messageColor);
		attrTable.addLine(GeneralMessages.columnTitle("attribute"), GeneralMessages.columnTitle("description"), 0);
		ArrayList<Attribute> attributes = AttributeConfiguration.config().getAttributes();
		if(attributes.size() > 0){
			
			for (Attribute attribute : attributes) {
				attrTable.addLine(attribute.getName(), attribute.getDescription(), 0);
			}
			
		}else{
			attrTable.addLine("-", "-", 0);
		}
		attrTable.collapse();
		book.addTable(attrTable);
		
		book.nextPage();
		
		// Ability descriptions:
		book.addLine("Higher attribute scores unlock and upgrade abilities. " +
			"Upgraded abilities are more efficient and have lower cooldown times. " +
			"Some abilities require certain buildings and some are only available for certain roles/ranks. " +
			"Use " + GeneralMessages.command("/pabilityreq") + " to see ability attribute requirements."
		);
		
		book.addLine("");
		
		// Ability description table:
		ChatTable abilityDescTable = new ChatTable(messageColor);
		abilityDescTable.addLine(new String[]{GeneralMessages.columnTitle("ability"), GeneralMessages.columnTitle("description")});
		ArrayList<AbilityDefinition> abilities = AbilityConfiguration.config().getDefinitions();
		if(abilities.size() > 0){
			
			for (AbilityDefinition ability : abilities) {
				abilityDescTable.addLine(new String[]{ability.getName(), ability.getDescription()});
			}
			
		}else{
			abilityDescTable.addLine(new String[]{"-", "-"});
		}
		abilityDescTable.collapse();
		book.addTable(abilityDescTable);
		
		book.nextPage();
		
		// Ability activation:
		book.addLine("There are active and passive abilities. " +
			"Active abilities can be activated by clicking with a certain item. " +
			"Passive abilities are always active and are triggered by a certain action."
		);
		
		book.addLine("");
		
		// Ability table:
		ChatTable abilityUsageTable = new ChatTable(messageColor);
		abilityUsageTable.addLine(new String[]{GeneralMessages.columnTitle("ability"), GeneralMessages.columnTitle("usage")});
		if(abilities.size() > 0){
			
			for (AbilityDefinition ability : abilities) {
				abilityUsageTable.addLine(new String[]{ability.getName(), ability.getUsage()});
			}
			
		}else{
			abilityUsageTable.addLine(new String[]{"-", "-"});
		}
		abilityUsageTable.collapse();
		book.addTable(abilityUsageTable);
		
		if(GeneralConfiguration.config().isRuneEnabled()){
			
			book.nextPage();
			
			// Guardian runes:
			String rechargeCost = "";
			String academy = academy();
			if(EconomyConfiguration.config().guardianRuneRechargeCost > 0) rechargeCost = "Recharge costs " + EconomyMessages.coins(EconomyConfiguration.config().guardianRuneRechargeCost) + ". ";
			book.addLine("The guardian rune will restore all carried items after death. " +
				"The rune needs to be recharged after every use. " +
				"Recharging is done by interacting with a " + GuardianRuneSign.SIGN_NAME + " sign. " + 
				"Recharge signs can only be set at a " + academy + " building. " +
				rechargeCost + 
				"Enable or disable the rune with " + GeneralMessages.command("/grenable") + " and " + GeneralMessages.command("/grdisable") + ". "
			);
			
		}
		
		return book.framedPage(page);
		
		
	}
	
	public static String shelp(int page) {
		
		
		ColourLoop colours = new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2);
		ChatBook book = new ChatBook("settlement help", colours);

		// Cost:
		String cost = "";
		if(EconomyConfiguration.config().isEnabled() && EconomyConfiguration.config().getSettlementCreateCost() > 0){
			cost = " Creating a settlement costs " + EconomyMessages.coins(EconomyConfiguration.config().getSettlementCreateCost()) + ".";
		}
		
		// Creation and claiming:
		book.addLine("A settlement will protect your land. " +
			"Use " + GeneralMessages.command("/ssettle") + " and " + GeneralMessages.command("/sclaim") + " to create the settlement and claim more land. " +
			"Land is claimed in 16x16 chunks. " +
			"Use " + GeneralMessages.command("/sunclaim") + " to abandon land. " +
			"Use " + GeneralMessages.command("/map") + " to see what chunks have already been claimed." +
			cost
		);
		
		// Dissolve:
		book.addLine("Use " + GeneralMessages.command("/sdissolve") + " to dissolve a settlement. " +
			"Settlements are dissolved automatically when there are no members or claimed chunks left. " +
			"Settlements with at least " + SettlementConfiguration.config().getNoDeleteSize() + " claimed chunks or with the " + BundleToggleable.NO_DELETE + " option can only be dissolved by using the " + GeneralMessages.command("/sdissolve") + " command with a special flag."
		);
		
		// Claims:
		book.addLine("Available claims are gained over time. " +
			"The speed at which they are gained is determined by the number of online members. " +
			"A certain amount of members is required for the settlement to gain more claims. " +
			"Use " + GeneralMessages.command("/sstats") + " to see settlement claims, requirements and other stats."
		);
		
		// Invite:
		book.addLine("Use " + GeneralMessages.command("/sinvite") + " to invite another player to the settlement. " +
			"Settlement invitations can be accepted with " + GeneralMessages.command("/saccept") + " and declined with " + GeneralMessages.command("/sdecline") + ". " +
			"A player can only be in a single settlement. " +
			"Use " + GeneralMessages.command("/settlementquit") + " to leave a settlement. " +
			"Troublemakers can be kicked by using " + GeneralMessages.command("/skick") + ". "
		);

		List<Entry<String, Integer>> bldgReq = SettlementConfiguration.config().getSortedBuildingRequirements();
		if(bldgReq.size() > 0){
			
			// Building requirements:
			book.addLine("At some point, certain building will be required for the settlement to gain more claims.");
			
			book.addLine("");
			
			// Requirements table:
			ChatTable bldgsReqTable = new ChatTable(colours);
			bldgsReqTable.addLine(new String[]{GeneralMessages.columnTitle("required"), GeneralMessages.columnTitle("total claims")});
			for (Entry<String, Integer> req : bldgReq) {
				bldgsReqTable.addLine(new String[]{req.getKey(), req.getValue().toString()});
			}
			bldgsReqTable.collapse();
			book.addTable(bldgsReqTable);
			
		}
		
		book.nextPage();
		
		// Roles:
		book.addLine("Use " + GeneralMessages.command("/ssetrole") + " to assign a role to a member. " +
			"Each role gives certain attribute bonuses. " +
			"The amount of available roles increases when certain buildings are set. " +
			"Roles also determine which actions and commands are permitted. " +
			"Some abilities are only available for certain roles."
		);
		
		book.addLine("");

		// Role table:
		ChatTable rolesTable = new ChatTable(colours);
		ArrayList<ProficiencyDefinition> roles = ProficiencyConfiguration.config().getDefinitions(ProficiencyType.ROLE);
			
		// Titles:
		rolesTable.addLine(new String[]{GeneralMessages.columnTitle("role"), GeneralMessages.columnTitle("bonus")});

		// Values:
		if(roles.size() != 0){
			
			for (ProficiencyDefinition definition : roles) {
				
				String roleName = definition.getName();
				String bonuses = bonuses(definition);
				if(bonuses.length() == 0) bonuses = "none";
				
				rolesTable.addLine(new String[]{roleName, bonuses});
				
			}

		}else{
			
			rolesTable.addLine(new String[]{"-", "-"});

		}
		
		rolesTable.collapse();
		book.addTable(rolesTable);
		
		book.nextPage();
		
		// Buildings:
		book.addLine("Use " + GeneralMessages.command("/bset") + " to set a building on the chunk and " + GeneralMessages.command("/bremove") + " to remove it. " +
			"Each building requires a certain amount of build points. " +
			"Build points are gained over time. " +
			"More buildings become available when more land is claimed. "
		);
		
		book.addLine("");

		// Buildings table:
		ChatTable bldgsTable = new ChatTable(colours);
		ArrayList<BuildingDefinition> bldgsDefinitions = BuildingConfiguration.config().getBuildingDefinitions();
			
		// Titles:
		bldgsTable.addLine(new String[]{GeneralMessages.columnTitle("building"), GeneralMessages.columnTitle("pts."), GeneralMessages.columnTitle("description")});

		if(bldgsDefinitions.size() != 0){
			
			for (BuildingDefinition bldgDefinition : bldgsDefinitions) {
				
				String name = bldgDefinition.getName();
				String points = bldgDefinition.getBuildPoints().toString();
				String description = bldgDefinition.getDescription();
				
				bldgsTable.addLine(new String[]{name, points, description});
				
			}
			
		}else{
		
			bldgsTable.addLine(new String[]{"-", "-", "-"});

		}
		
		bldgsTable.collapse();
		book.addTable(bldgsTable);
		
		book.nextPage();

		// Storage:
		book.addLine("Some buildings can produce resources. " +
			"Resources are placed in storage areas, which can be added and removed with " + GeneralMessages.command("/baddstorage") + " and " + GeneralMessages.command("/bremovestorage") + ". " +
			"Blocks (stone, wood, ores etc) are placed directly to storage areas. " +
			"Add chests to storage areas to store items (food, swords, ingots etc). " +
			"If a storage area is full then the resources will be stored in a " + warehouse() + ". " +
			"If certain resources are required for crafting, then they are taken from a " + warehouse() + ". " +
			"Players with certain roles must be online for a building to function. " +
			"Use " + GeneralMessages.command("/bstats") + " to see production progress and requirements."
		);
		
		book.nextPage();

		// Other:
		book.addLine("To prevent griefing from settlement members, restrict building by setting homes with " + GeneralMessages.command("/bset home") + ". " +
			"Only the owner and residents can build in homes. " +
			"Residents can be added and removed with " + GeneralMessages.command("/baddresident") + " and " + GeneralMessages.command("/bremoveresident") + ". "
		);
		
		book.addLine("Use " + GeneralMessages.command("/srename") + " to rename the settlement.");
		book.addLine("Use " + GeneralMessages.command("/sresign") + " to declare someone else as the settlement owner.");
		
		return book.framedPage(page);
		
		
	}
	
	public static String fhelp(int page) {

		
		ColourLoop colours = new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2);
		ChatBook book = new ChatBook("faction help", colours);

		// Cost:
		String cost = "";
		if(EconomyConfiguration.config().isEnabled() && EconomyConfiguration.config().getFactionCreateCost() > 0){
			cost = " Creating a faction costs " + EconomyMessages.coins(EconomyConfiguration.config().getFactionCreateCost()) + ".";
		}
		
		// Claiming:
		String townSquare = townSquare();
		book.addLine("Every settlement that has a " + townSquare + " can be claimed by a faction. " +
			"To claim a settlement faction members must hold the " + townSquare + " for a certain amount of time. " +
			"When no members remain, claim progress will decrease over time. " +
			"To abandon a settlement use " + GeneralMessages.command("/funclaim") + ". " +
			"The total number of available claims increases with time." +
			cost
		);
		
		// Pvp:
		String pvp = "";
		if(FactionConfiguration.config().factionOnlyPvp){
			pvp = "Only factions can take part in pvp. ";
		}
		
		// Formation:
		String formation = "";
			if(FactionConfiguration.config().formationAmount > 1){
			formation = " A faction requires " + FactionConfiguration.config().formationAmount + " members to be formed.";
		}
		
		// Creation:
		book.addLine(pvp +
			"To create a faction use " + GeneralMessages.command("/fcreate") + ". " +
			"New members can be invited with " + GeneralMessages.command("/finvite") + ". " +
			"Use " + GeneralMessages.command("/faccept") + " and " + GeneralMessages.command("/fdecline") + " to accept or decline faction invitations. " +
			"To leave the faction use " + GeneralMessages.command("/factionquit") + ". " +
			"Troublemakers can be kicked by using " + GeneralMessages.command("/fkick") + ". " +
			formation
		);

		book.nextPage();
		
		// Ranks:
		book.addLine("Use " + GeneralMessages.command("/fsetrank") + " to assign a rank to a member. " +
			"Each rank gives certain attribute bonuses. " +
			"The amount of available ranks increases when the faction claims settlements that have certain buildings. " +
			"Ranks also determine which commands are permitted. " +
			"Some abilities are only available to certain ranks."
		);
		
		book.addLine("");

		// Rank table:
		ChatTable rolesTable = new ChatTable(colours);
		ArrayList<ProficiencyDefinition> roles = ProficiencyConfiguration.config().getDefinitions(ProficiencyType.RANK);
			
		// Titles:
		rolesTable.addLine(new String[]{GeneralMessages.columnTitle("rank"), GeneralMessages.columnTitle("bonus")});

		// Values:
		if(roles.size() != 0){
			
			for (ProficiencyDefinition definition : roles) {
				
				String roleName = definition.getName();
				String bonuses = bonuses(definition);
				if(bonuses.length() == 0) bonuses = "none";
				
				rolesTable.addLine(new String[]{roleName, bonuses});
				
			}

		}else{
			
			rolesTable.addLine(new String[]{"-", "-"});

		}
		
		rolesTable.collapse();
		book.addTable(rolesTable);
		
		book.nextPage();

		// Chat:
		book.addLine("Once the faction is formed, members gain access to faction chat " + GeneralMessages.command("/f") + ". ");

		// Colours:
		book.addLine("Faction colours can be customised with " + GeneralMessages.command("/fsetcolor1") + " and " + GeneralMessages.command("/fsetcolor2") + ".");

		// Rename:
		book.addLine("To rename the faction use " + GeneralMessages.command("/frename") + ".");

		// Spawn:
		book.addLine("Faction spawn can be set with " + GeneralMessages.command("/fsetspawn") + ". " +
			" Use " + GeneralMessages.command("/fspawn") + " to teleport to the faction spawn point."
		);
		
		book.nextPage();

		// Alliances:
		book.addLine("Factions can form alliances. " +
			"Allied factions share faction chat and can't attack each other. " +
			"An alliance request can be requested with " + GeneralMessages.command("/frequestally") + ". " +
			"Alliance requests can be accepted and declined with " + GeneralMessages.command("/facceptally") + " and " + GeneralMessages.command("/fdeclineally") + ". " +
			"An alliance can be broken with " + GeneralMessages.command("/fremoveally") + "."
		);

		book.nextPage();

		// Spawning:
		book.addLine("All faction members can use " + GeneralMessages.command("/sspawn settle_name") + " command for claimed settlements. " +
			"The command is not available when the settlement is being claimed by a rival faction."
		);
		
		return book.framedPage(page);
		
		
	}

	
	
	// Buildings:
	public static String academy(){
		
		ArrayList<BuildingDefinition> buildings = BuildingConfiguration.config().getBuildingDefinitions();
		
		for (BuildingDefinition building : buildings) {
			if(building.getBuildingClass().equals(Academy.class.getName())) return building.getName();
		}
	
		return "<missing>";
	
	}
	
	public static String townSquare(){
		
		ArrayList<BuildingDefinition> buildings = BuildingConfiguration.config().getBuildingDefinitions();
		
		for (BuildingDefinition building : buildings) {
			if(building.getBuildingClass().equals(TownSquare.class.getName())) return building.getName();
		}
		
		return "<missing>";
		
	}

	public static String tradingPost(){
	
		ArrayList<BuildingDefinition> buildings = BuildingConfiguration.config().getBuildingDefinitions();
		
		for (BuildingDefinition building : buildings) {
			if(building.getBuildingClass().equals(TradingPost.class.getName())) return building.getName();
		}
	
		return "<missing>";
	
	}
	
	public static String trainingCamp(){
		
		ArrayList<BuildingDefinition> buildings = BuildingConfiguration.config().getBuildingDefinitions();
		
		for (BuildingDefinition building : buildings) {
			if(building.getBuildingClass().equals(TrainingCamp.class.getName())) return building.getName();
		}
	
		return "<missing>";
	
	}
	
	public static String warehouse(){
		
		ArrayList<BuildingDefinition> buildings = BuildingConfiguration.config().getBuildingDefinitions();
		
		for (BuildingDefinition building : buildings) {
			if(building.getBuildingClass().equals(Warehouse.class.getName())) return building.getName();
		}
	
		return "<missing>";
	
	}

	
	
	// Abilities:
	public static String ability(AbilityDefinition definition) {

		
		ColourLoop colours = new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2);
		ChatBook book = new ChatBook(definition.getName() + " ability requirements", colours);
		
//		// Description:
//		book.addLine(TextUtil.senctence(definition.getDescription()));
//		
//		book.addLine("");
//		
//		// Usage:
//		book.addLine(GeneralMessages.columnTitle("usage:") + " " + definition.getUsage());
//		
//		book.addLine("");

		// Ability upgrade table:
		ChatTable upgrTable = new ChatTable(colours);
		Integer minScore = 1;
		Integer maxScore = AbilityConfiguration.config().maxAbilityScore;
		
		upgrTable.addLine(new String[]{GeneralMessages.columnTitle("score"), GeneralMessages.columnTitle("requirements")});
		
		if(maxScore >= minScore){
			
			for (int score = minScore; score <= maxScore; score++) {
				
				String req = StatsMessages.requirements(definition, score);
				String restr = StatsMessages.restrictions(definition);
				if(req.length() > 0 && restr.length() > 0) restr = ", " + restr;
				
				upgrTable.addLine(new String[]{definition.getName() + " " + RomanNumeral.binaryToRoman(score), req + restr});
				
			}
			
		}else{
			upgrTable.addLine(new String[]{"-","-"});
		}
		
		upgrTable.collapse();
		book.addTable(upgrTable);
		
		return book.framedPage(0);
		
		
	}
	
	
	// Utility:
	public static String bonuses(ProficiencyDefinition definition) {

		
		StringBuffer result = new StringBuffer();
		
		// Attributes:
		ArrayList<String> attributeNames = AttributeConfiguration.config().getAttributeNames();
		
		for (String attribute : attributeNames) {
			
			Integer bonus = definition.getAttributeBonus(attribute);
			if(bonus <= 0) continue;
			
			if(result.length() > 0) result.append(", ");
			
			result.append("+" + bonus + " " + GeneralMessages.attrAbrev(attribute));
			
		}

		return result.toString();
		
	
	}
	
	
}
