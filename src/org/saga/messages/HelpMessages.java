package org.saga.messages;

import java.util.ArrayList;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.saga.abilities.AbilityDefinition;
import org.saga.attributes.Attribute;
import org.saga.buildings.Academy;
import org.saga.buildings.BuildingDefinition;
import org.saga.buildings.TownSquare;
import org.saga.buildings.TradingPost;
import org.saga.buildings.TrainingCamp;
import org.saga.buildings.signs.AttributeSign;
import org.saga.buildings.signs.BuySign;
import org.saga.buildings.signs.GuardianRuneSign;
import org.saga.config.AbilityConfiguration;
import org.saga.config.AttributeConfiguration;
import org.saga.config.BuildingConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.config.FactionConfiguration;
import org.saga.config.GeneralConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.SettlementConfiguration;
import org.saga.messages.PlayerMessages.ColourLoop;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.player.ProficiencyDefinition;
import org.saga.utility.text.RomanNumeral;
import org.saga.utility.text.StringBook;
import org.saga.utility.text.StringTable;

public class HelpMessages {

public static ChatColor veryPositive = ChatColor.DARK_GREEN; // DO NOT OVERUSE.
	



	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED; // DO NOT OVERUSE.
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor announce = ChatColor.AQUA;
	
	public static ChatColor normal1 = ChatColor.GOLD;
	
	public static ChatColor normal2 = ChatColor.YELLOW;
	
	public static ChatColor frame = ChatColor.DARK_GREEN;
	
	
	// Help:
	public static String ehelp(int page) {

		
		ColourLoop colours = new ColourLoop().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook("economy help", colours);
		
		// General:
		book.addLine("Faction members receive wages each " + EconomyConfiguration.config().getFactionWagesTime() + ". " +
			"Earned coins can be spent at a " + tradingPost() + ". " +
			"The amount of goods available is limited and is restocked every day. " +
			"Available coins can be seen under " + GeneralMessages.command("/stats") + "."
		);

		// Trading post:
		book.addLine("Every settlement can set a " + tradingPost() + " by using " + GeneralMessages.command("/bset") + ". " +
			"Buy signs can be created by writing " + GeneralMessages.command(BuySign.SIGN_NAME) + " on the first line and " + GeneralMessages.command("amount" + BuySign.AMOUNT_DIV_DISPLAY + "item_name") + " on the second line. " +
			"Item ID can also be used instead of " + GeneralMessages.command("item_name") + "." 
		);
		
		return book.framedPage(0);
		
		
	}
	
	public static String phelp(int page) {
		
		
		ColourLoop messageColor = new ColourLoop().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook("player help", messageColor);

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
		StringTable attrTable = new StringTable(messageColor);
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
		StringTable abilityDescTable = new StringTable(messageColor);
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
		StringTable abilityUsageTable = new StringTable(messageColor);
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
		
		
		ColourLoop colours = new ColourLoop().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook("settlement help", colours);

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
		StringTable rolesTable = new StringTable(colours);
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
			"Build points and new buildings become available when more land is claimed. "
		);
		
		book.addLine("");

		// Buildings table:
		StringTable bldgsTable = new StringTable(colours);
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
		book.addLine("Some buildings can spawn or craft resources. " +
			"Resources are taken from and stored in storage areas which can be added and removed with " + GeneralMessages.command("/baddstorage") + " and " + GeneralMessages.command("/bremovestorage") + ". " +
			"Blocks are placed directly to storage areas. " +
			"Add chests to storage areas to store items."
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

		
		ColourLoop colours = new ColourLoop().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook("faction help", colours);

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
		StringTable rolesTable = new StringTable(colours);
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
		
		// Bonuses:
		book.addLine("Members get paid daily wages, based on the amount of settlements the faction is holding. " +
			"High level settlements generate more income. " +
			"Higher ranks get paid more."
		);
		
		book.addLine("");

		// Wages table:
		StringTable wagesTable = new StringTable(colours);
		
		Integer maxSize = SettlementConfiguration.config().getMaxClaims();
		Integer halfSize = maxSize / 2;
		Integer minSize = 0;
		
		Double maxClaims = FactionConfiguration.config().getClaimPoints(maxSize);
		Double halfClaims = FactionConfiguration.config().getClaimPoints(halfSize);
		Double minClaims = FactionConfiguration.config().getClaimPoints(minSize);
		
		// Titles:
		wagesTable.addLine(new String[]{GeneralMessages.columnTitle("rank"), GeneralMessages.columnTitle("size 0"), GeneralMessages.columnTitle("size " + halfSize), GeneralMessages.columnTitle("size " + maxSize)});

		Hashtable<Integer, Double> lvl0Wages = EconomyConfiguration.config().calcHierarchyWages(EconomyConfiguration.config().calcWage(minClaims));
		Hashtable<Integer, Double> lvlHalfWages = EconomyConfiguration.config().calcHierarchyWages(EconomyConfiguration.config().calcWage(halfClaims));
		Hashtable<Integer, Double> lvlMaxWages = EconomyConfiguration.config().calcHierarchyWages(EconomyConfiguration.config().calcWage(maxClaims));
		
		int min = FactionConfiguration.config().getHierarchyMin();
		int max = FactionConfiguration.config().getHierarchyMax();
		
		if(min != max){
		
			for (int hiera = max; hiera >= min; hiera--) {
				
				String name = FactionConfiguration.config().getHierarchyName(hiera);

				Double wage0 = lvl0Wages.get(hiera);
				if(wage0 == null) wage0 = 0.0;

				Double wageHalf = lvlHalfWages.get(hiera);
				if(wageHalf == null) wageHalf = 0.0;
				
				Double wageMax = lvlMaxWages.get(hiera);
				if(wageMax == null) wageMax = 0.0;
				
				wagesTable.addLine(new String[]{name, EconomyMessages.coins(wage0), EconomyMessages.coins(wageHalf), EconomyMessages.coins(wageMax)});
				
			}
			
		}else{
			
			wagesTable.addLine(new String[]{"-", "-", "-", "-"});
			
		}
		
		wagesTable.collapse();
		book.addTable(wagesTable);
		
		
		return book.framedPage(page);
		
		
	}

	
	
	// Abilities:
	public static String ability(AbilityDefinition definition) {

		
		ColourLoop colours = new ColourLoop().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook(definition.getName() + " ability requirements", colours);
		
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
		StringTable upgrTable = new StringTable(colours);
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

	
}
