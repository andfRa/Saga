package org.saga.messages;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.saga.abilities.AbilityDefinition;
import org.saga.attributes.Attribute;
import org.saga.buildings.TradingPost;
import org.saga.buildings.signs.AttributeSign;
import org.saga.buildings.signs.GuardianRuneSign;
import org.saga.buildings.signs.SellSign;
import org.saga.config.AbilityConfiguration;
import org.saga.config.AttributeConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.config.FactionConfiguration;
import org.saga.messages.PlayerMessages.ColorCircle;
import org.saga.utility.text.RomanNumeral;
import org.saga.utility.text.StringBook;
import org.saga.utility.text.StringTable;

public class InfoMessages {

public static ChatColor veryPositive = ChatColor.DARK_GREEN; // DO NOT OVERUSE.
	



	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED; // DO NOT OVERUSE.
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor anouncment = ChatColor.AQUA;
	
	public static ChatColor normal1 = ChatColor.GOLD;
	
	public static ChatColor normal2 = ChatColor.YELLOW;
	
	public static ChatColor frame = ChatColor.DARK_GREEN;
	
	
	// Help:
	public static String ehelp(int page) {

		
		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook("building help", color, 9);
		
		// General:
		book.addLine("Items can be bought and sold at a trading post building. See /bhelp for details.");

		// Balance:
		book.addLine("/stats to see how much coins you have in your wallet.");

		// Limited items:
		book.addLine("A trading post doesn't have unlimited coins/items. Everything is gained from players buying/selling and exporting/importing.");
		
		// Building info:
		book.addLine("/tpost to see all buyable items, sellable items, exports, imports, available coins and available items.");
		
		// Set sell:
		book.addLine("/bsetsell <item> <amount> <value> to set the minimum amount and value of a sold item");

		// Set buy:
		book.addLine("/bsetbuy <item> <amount> <value> to set the minimum amount and value of a bought item");
		
		// Signs:
		book.addLine("Place \"=[sell]= | amount" + SellSign.MATERIAL_VALUE_DIV + "item\" and \"=[buy]= | amount" + SellSign.MATERIAL_VALUE_DIV + "item\" signs to sell and buy items.");

		// Donate:
		book.addLine("To get the trading post running, you will need to donate items or coins.");
		
		// Donate:
		book.addLine("/donate, /donatec <amount> or /donateall <item> to donate item in hand, coins or all items of the given type.");
		
		// Export import:
		book.addLine("A deal needs to be formed to export or import items.");

		// Imports:
		book.addLine("/eimports and /eexports to list all deals.");

		// Expiration:
		book.addLine("/bnewdeal <ID> to form a deal. Deal will expire after certain amout of items or time.");

		// Timing:
		book.addLine("After a deal is formed, the goods will get exported/imported each sunrise.");

		// Goods sign:
		book.addLine("" + TradingPost.GOODS_SIGN + " sign displays goods list.");

		// Deals sign:
		book.addLine("" + TradingPost.DEALS_SIGN + " sign displays deals list.");

		return book.framed(page);
		
		
	}
	
	public static String phelp(int page) {
		
		
		ColorCircle messageColor = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook("player help", messageColor, 13);

		int maxAttr = AttributeConfiguration.config().findMaxAttrPoints();
		int minAttr = AttributeConfiguration.config().findMinAttrPoints();
		String attrGain = minAttr + "-" + maxAttr;
		if(minAttr == maxAttr) attrGain = minAttr + "";
		
		// Attributes:
		book.addLine( 
			"Player levels are gained from killing creatures, getting crafting materials and pvp. " +
			"Each level gives " + attrGain + " attribute points that can be used to increase attribute scores. " +
			"Higher attribute scores make you stronger and unlock new abilities. " +
			"Attributes can be increased by interacting with " + AttributeSign.SIGN_NAME + " signs. " +
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
		
		book.addLine("");
		book.nextPage();
		
		// Abilities:
		book.addLine("There are active and passive abilities. " +
			"Active abilities can be activated by clicking with a certain item. " +
			"Passive abilities are always active. "
		);
		
		book.addLine("");
		
		// Ability table:
		StringTable abilityTable = new StringTable(messageColor);
		abilityTable.addLine(new String[]{GeneralMessages.columnTitle("ability"), GeneralMessages.columnTitle("description"), GeneralMessages.columnTitle("usage")});
		ArrayList<AbilityDefinition> abilities = AbilityConfiguration.config().getDefinitions();
		if(abilities.size() > 0){
			
			for (AbilityDefinition ability : abilities) {
				abilityTable.addLine(new String[]{ability.getName(), ability.getDescription(), ability.getUsage()});
			}
			
		}else{
			abilityTable.addLine(new String[]{"-", "-", "-"});
		}
		abilityTable.collapse();
		book.addTable(abilityTable);
		
		book.addLine("");
		book.nextPage();
		
		// Ability upgrades:
		book.addLine(
			"Abilities can be upgraded by increasing attributes. " +
			"Upgraded abilities are more efficient and have lower cooldown times. " +
			"Some abilities require certain buildings."
		);
		
		book.addLine("");
		
		// Ability upgrade table:
		StringTable upgrTable = new StringTable(messageColor);
		Integer score1 = 1;
		Integer score3 = AbilityConfiguration.config().maxAbilityScore;
		Integer score2 = new Double((score1.doubleValue() + score3.doubleValue())/2.0).intValue();
		
		upgrTable.addLine(new String[]{GeneralMessages.columnTitle("ability"),
				GeneralMessages.columnTitle("required " + RomanNumeral.binaryToRoman(score1)),
				GeneralMessages.columnTitle("required " + RomanNumeral.binaryToRoman(score2)),
				GeneralMessages.columnTitle("required " + RomanNumeral.binaryToRoman(score3))
		});
		
		if(abilities.size() > 0){
			
			for (AbilityDefinition ability : abilities) {
				upgrTable.addLine(new String[]{ability.getName(),
					StatsMessages.requirements(ability, score1),
					StatsMessages.requirements(ability, score2),
					StatsMessages.requirements(ability, score3)
				});
			}
			
		}else{
			upgrTable.addLine(new String[]{"-", "-", "-", "-"});
		}
		upgrTable.collapse();
		book.addTable(upgrTable);
		
		book.addLine("");
		book.nextPage();
		
		// Guardian runes:
		String rechargeCost = "";
		if(EconomyConfiguration.config().guardianRuneRechargeCost > 0) rechargeCost = "Recharge costs " + EconomyMessages.coins(EconomyConfiguration.config().guardianRuneRechargeCost) + ". ";
		book.addLine("The guardian rune will restore all carried items after death. " +
			"The rune needs to be recharged after every use. " +
			"Recharging is done by interacting with a " + GuardianRuneSign.SIGN_NAME + " sign. " + 
			rechargeCost + 
			"Enable or disable the rune with " + GeneralMessages.command("/grenable") + " and " + GeneralMessages.command("/grdisable") + ". "
		);
		
		return book.framed(page);
		
		
	}
	
	public static String shelp(int page) {
		
		
		ColorCircle messageColor = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook("settlement help", messageColor, 10);

		// Pvp enabled:
		if(FactionConfiguration.config().factionOnlyPvp){
			book.addLine(veryNegative + "Settlements don't get pvp protection. Build walls or don't join a faction to protect yourself.");
		}else{
			book.addLine(veryNegative + "Settlements don't get pvp protection. Build walls to protect yourself.");
		}
		
		// Create:
		book.addLine("/ssettle <name> to create a settlement.");

		// Claim:
		book.addLine("/sclaim to claim more land.");
		
		// Abandon:
		book.addLine("/sabandon to unclaim land.");

		// Map:
		book.addLine("/map to see all claimed land.");
		
		// Invite:
		book.addLine("/sinvite <name> to invite someone to the settlement.");

		// Accept:
		book.addLine("/saccept to accept a settlement invitation.");
		
		// Decline:
		book.addLine("/sdeclineall to decline all settlement invitations.");

		// Stats:
		book.addLine("/sstats to see the level, claims, buildings, roles and other settlement stats.");

		// Leveling bonus:
		book.addLine("Higher level gives more land to claim and more buildings.");
		
		// List:
		book.addLine("/slist to see all settlement members.");

		// Quit:
		book.addLine("/settlementquit to quit the settlement.");

		// Kick:
		book.addLine("/skick <name> to kick someone from the settlement.");

		// Set role:
		book.addLine("/ssetrole <name> <role_name> to assign a role to someone.");

		// Roles:
		book.addLine("Available roles can be found under /sstats.");
		
		// Declare owner:
		book.addLine("/sdeclareowner <name> to declare someone as the new owner.");
		
		// Homes:
		book.addLine("/bset home to set a home building.");

		// New residents:
		book.addLine("/baddresident to add a resident to a home.");

		// Remove residents:
		book.addLine("/bremoveresident to remove a resident from a home.");

		// List residents:
		book.addLine("Home residents are listed under /bstats.");
		
		// Protection:
		book.addLine(positive + "Set homes to limit griefing damage by members!");
		
		// Leveling speed:
		book.addLine("The settlement gains exp each second, based on the number of members online.");

		// Set building:
		book.addLine("/bset <building_name> to set a building.");

		// Remove building:
		book.addLine("/bremove to remove a building.");
		
		// Buildings:
		book.addLine("Available buildings can be found under /sstats.");

		// New buildings:
		book.addLine("More buildings become available as the settlement gains levels or diferent building are added.");

		// Set building:
		book.addLine("/binfo for information about a particular building.");
		
		// Rename:
		book.addLine("/srename <name> to rename the settlement. Costs " + EconomyMessages.coins(EconomyConfiguration.config().chunkGroupRenameCost) + ".");
		
		return book.framed(page);
		
		
	}

	public static String fhelp(int page) {
		
		
		ColorCircle messageColor = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook("Faction help", messageColor, 10);
		
		// Pvp:
		if(FactionConfiguration.config().factionOnlyPvp){
			book.addLine(veryNegative + "Only factions can take part in pvp.");
		}
		
		// Create:
		book.addLine("/fcreate <name> creates a new faction.");
		
		// Formation:
		if(FactionConfiguration.config().formationAmount > 1){
			book.addLine("A faction will not be formed until it has " + FactionConfiguration.config().formationAmount + " members.");
		}
		
		// Invite:
		book.addLine("/finvite <name> to invite someone to the faction.");
		
		// Accept:
		book.addLine("/faccept to accept a faction invitation.");
		
		// Decline
		book.addLine("/fdeclineall to decline all faction invitations.");

		// Kick:
		book.addLine("/fkick <name> to kick someone out from the faction.");
		
		// Quit:
		book.addLine("/factionquit to quit a faction.");

		// List:
		book.addLine("/flist to list all faction members.");

		// Primary color:
		book.addLine("/fsetprimarycolor <color> to set the factions primary color.");

		// Secondary color:
		book.addLine("/fsetsecondarycolor <color> to set the factions secondary color.");

		// Chat:
		book.addLine("/f <message> to send a message in the faction chat.");

		// Stats:
		book.addLine("/fstats to see available ranks and other stats.");

		// Set rank:
		book.addLine("/fsetrank <name> <rank> to assign a rank to somebody.");

		// Rename:
		book.addLine("/frename <name> to rename the faction. Costs " + EconomyMessages.coins(EconomyConfiguration.config().factionRenameCost) + ".");
		
		// Request alliance:
		book.addLine("/frequestally <faction_name> to request an alliance.");

		// Accept alliance:
		book.addLine("/facceptally <faction_name> to accept an alliance.");

		// Decline alliance:
		book.addLine("/fdeclinetally <faction_name> to deline an alliance.");
		
		// Decline alliance:
		book.addLine("/fremoveally <faction_name> to break an alliance.");
		
		return book.framed(page);
		
		
	}


	



	
}
