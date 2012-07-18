package org.saga.messages;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.saga.abilities.Ability;
import org.saga.buildings.Arena;
import org.saga.buildings.Arena.ArenaPlayer;
import org.saga.buildings.Building;
import org.saga.buildings.TownSquare;
import org.saga.buildings.TradingPost;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.config.AbilityConfiguration;
import org.saga.config.AttributeConfiguration;
import org.saga.economy.TradeDeal;
import org.saga.economy.TradeDeal.TradeDealType;
import org.saga.messages.PlayerMessages.ColorCircle;
import org.saga.player.SagaPlayer;
import org.saga.utility.StringBook;
import org.saga.utility.StringTable;
import org.saga.utility.TextUtil;

public class BuildingMessages {


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
	
	
	

	// Adding buildings:
	public static String notOnClaimedLand(ChunkGroup chunkGroup) {
		return negative + "Buildings can only be on located on claimed land.";
	}
	
	public static String oneBuildingAllowed(ChunkGroup chunkGroup) {
		return negative + "A chunk of land can only have one building.";
	}
	
	public static String invalidBuilding(String buildingName) {
		return negative + buildingName + " isn't a valid building.";
	}
	
	public static String unavailable(Building building){
		
		return negative + TextUtil.capitalize(building.getDisplayName()) + " isn't available.";
		
	}

	
	
	// General buildings:
	public static String invalidBuilding(String correctBuildingName, String command){
		
		return negative + command + " can only be used from a " + correctBuildingName + ".";
		
	}
	
	public static String noBuilding(){
		
		return negative + "No building on this chunk of land.";
		
	}
	
	
	
	// Storage:
	public static String storeAreaOverlap(){
		
		return negative + "Storage areas can't overlap.";
		
	}
	
	public static String storeAreaSingleChunk(){
		
		return negative + "Storage area must be on the same chunk of land.";
		
	}
	
	public static String storeAreaAdded(Building building){
		
		return positive + "Storage area added to " + building.getName() + ".";
		
	}

	public static String storeAreaRemoved(Building building){
		
		return positive + "Storage area removed from " + building.getName() + ".";
		
	}
	
	public static String storeAreaNotFound(Building building){
		
		return negative + "Storage area not found.";
		
	}

	public static String storeAreasUnavailable(Building building){
		return negative + "No storage areas available for " + building.getName() + ".";
	}
	
	public static String stored(Material material, Building building) {

		return positive + "Stored " + EconomyMessages.material(material) + ".";

	}
	
	public static String withdrew(Material material, Building building) {

		return positive + "Withdrew " + EconomyMessages.material(material) + ".";

	}
	
	
	
	// Arena:
	public static String countdown(int count) {
		
		if(count == 0){
			return ChunkGroupMessages.positive + "Fight!";
		}else if((count%2)==0){
			return ChunkGroupMessages.normal1 + "" + count + ".";
		}else{
			return ChunkGroupMessages.normal2 + "" + count + ".";
		}
		
	}

	

	
	// Trading post:
	public static String setAutomate(TradingPost tradingPost){
		
		if(tradingPost.isAutomated()){
			return positive + "Set " + tradingPost.getDisplayName() + " to automatic.";
		}else{
			return positive + "Set " + tradingPost.getDisplayName() + " to manual.";
		}
		
	}
	
	public static String alreadyAutomatic(TradingPost tradingPost){
		
		return negative + "" + TextUtil.capitalize(tradingPost.getDisplayName()) + " is already automatic.";
		
	}
	
	public static String alreadyManual(TradingPost tradingPost){
		
		return negative + "" + TextUtil.capitalize(tradingPost.getDisplayName()) + " is already manual.";
		
	}
	
	public static String cantDonateEnchanted(){
		
		return negative + "Enchanted items can't be donated.";
		
	}
	
	public static String cantDonateDamaged(){
		
		return negative + "Damaged items can't be donated.";
		
	}

	public static String goods(TradingPost tpost, Integer page){
		
		
		StringBook book = new StringBook(tpost.getName() + " goods", new ColorCircle().addColor(normal1).addColor(normal2), 10);
		StringTable table = new StringTable(new ColorCircle().addColor(normal1).addColor(normal2));
		
		ArrayList<Material> materials = tpost.getAllMaterials();
		
		// Names:
		table.addLine(new String[]{"ITEM","BUY FOR","SELL FOR","STORED"});
		
		// Coins:
		book.addLine("availabe coins " + EconomyMessages.coins(tpost.getCoins()));
		
		book.addLine("\n");
		
		// Materials:
		if(materials.size() > 0){
			
			for (Material material : materials) {
				
				String buy = "-";
				if(tpost.getBuyPrice(material) != null){
					buy = EconomyMessages.coins(tpost.getBuyPrice(material));
				}

				String sell = "-";
				if(tpost.getSellPrice(material) != null){
					sell = EconomyMessages.coins(tpost.getSellPrice(material));
				}

				String stored = "-";
				if(tpost.getAmount(material) != null){
					stored = tpost.getAmount(material).toString();
				}
				
				table.addLine(new String[]{EconomyMessages.material(material),buy,sell,stored});
				
			}
			
		}else{
			table.addLine(new String[]{"-","-","-","-"});
		}
			
		table.collapse();
		book.addTable(table);
		
		return book.framed(page);
		
		
	}

	public static String deals(TradingPost tpost, Integer page){
		
		
		StringBook book = new StringBook(tpost.getName() + " deals", new ColorCircle().addColor(normal1).addColor(normal2), 10);
		ArrayList<TradeDeal> deals = tpost.getDeals();
		
		// Imports:
		book.addLine("IMPORTS");
		book.addTable(dealsTable(deals, TradeDealType.IMPORT));
		
		book.addLine("\n");
		
		// Exports:
		book.addLine("EXPORTS");
		book.addTable(dealsTable(deals, TradeDealType.EXPORT));
		
		return book.framed(page);
		
		
	}
	
	public static StringTable dealsTable(ArrayList<TradeDeal> tradeDeals, TradeDealType type){
		

		ColorCircle colour = new ColorCircle().addColor(normal1).addColor(normal2);
		StringTable table = new StringTable(colour);
		
		// Filter out correct deals:
		ArrayList<TradeDeal> correctDeals = new ArrayList<TradeDeal>();
		for (TradeDeal tradeDeal : tradeDeals) {
			if(tradeDeal.getType() == type) correctDeals.add(tradeDeal);
		}
		tradeDeals = correctDeals;
		
		// Names:
		String[] names = new String[]{"ID", "ITEM", "PRICE", "DAYS", "AMOUNT"};
		table.addLine(names);
		
		// Create the book:
		if(tradeDeals.size() > 0){
			
			for (int i = 0; i < tradeDeals.size(); i++) {
				
				TradeDeal deal = tradeDeals.get(i);
				
				String[] lines = new String[]{deal.getId().toString(), EconomyMessages.materialShort(deal.getMaterial()), EconomyMessages.coins(deal.getPrice()), deal.getDaysLeft().toString(), deal.getAmount().toString()};
				
				table.addLine(lines);
				
			}
			
		}else{
			table.addLine(new String[]{"-", "-", "-", "-", "-"});
		}
		
		table.collapse();
		
		return table;
		
		
	}

	public static String report(TradingPost tpost){
		
		
//		ColorCircle colour = new ColorCircle().addColor(normal1).addColor(normal2);
//		
//		StringTable table = new StringTable(colour);
//		table.setCustomWidths(new Double[]{18.0, 11.0, 16.0, 10.0, 16.0});
//		
//		String sExports = "-";
//		String sImports = "-";
//		
//		if(exported > 0){
//			sExports = exported.toString();
//		}
//		if(imported > 0){
//			sImports = imported.toString();
//		}
//		
//		table.addLine(new String[]{"=[DEALS]=","exports",sExports,"imports",sImports});
//		
//		return table.createTable();
		return "";
		
	}

	public static String formedDeal(TradingPost tpost, TradeDeal deal){
		
		return normal2 + "Formend a new trade deal: " + deal.getType().getName() + " " + EconomyMessages.material(deal.getMaterial()) + " for " + EconomyMessages.coins(deal.getPrice()) + ".";
		
	}

	public static String report(TradingPost tpost, Integer page){
		
		
		ColorCircle colour = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook(tpost.getDisplayName() + " report",colour , 10);
		
		// Import and export:
		StringTable expimptable = new StringTable(colour);
		expimptable.setCustomWidths(new Double[]{17.25,17.25,17.25,17.25});
		expimptable.addLine(new String[]{
				"export",
				EconomyMessages.coins(tpost.getExported()),
				"import",
				EconomyMessages.coins(tpost.getImported()),
		});
		expimptable.addLine(new String[]{
				"balance",
				EconomyMessages.coins(tpost.getExported()-tpost.getImported()),
				"total",
				EconomyMessages.coins(tpost.getCoins()),
		});
		book.addTable(expimptable);
		
		book.addLine("");
		
		// New deals:
		ArrayList<TradeDeal> newDeals = tpost.getNewDeals();
		book.addLine("NEW DEALS");
		book.addTable(shortDeals(newDeals));

		book.nextPage();
		
		// Expired deals:
		ArrayList<TradeDeal> completedDeals = tpost.getCompletedDeals();
		book.addLine("COMPLETED DEALS");
		book.addTable(shortDeals(completedDeals));

		book.addLine("");
		
		// Expired deals:
		ArrayList<TradeDeal> expiredDeals = tpost.getExpiredDeals();
		book.addLine("EXPIRED DEALS");
		book.addTable(shortDeals(expiredDeals));
		
		return book.framed(page);
		
		
	}
	
	public static StringTable shortDeals(ArrayList<TradeDeal> deals){
		
		
		StringTable table = new StringTable(new ColorCircle().addColor(normal1).addColor(normal2));
		
		if(table.size() > 0) table.addLine(new String[]{"", "", ""});
		
		// Names:
		table.addLine(new String[]{"ITEM", "TYPE", "PRICE"});
		
		if(deals.size() > 0){
			
			for (TradeDeal deal : deals) {
				
				table.addLine(new String[]{EconomyMessages.materialShort(deal.getMaterial()), deal.getType().getName(), EconomyMessages.coins(deal.getPrice())});
				
			}
			
		}else{
			table.addLine(new String[]{"-", "-", "-"});
		}
		
		table.collapse();
		
		return table;
		
		
	}
	
	public static String dealsBalance(TradingPost tpost){
		
		return normal2 +"Exported goods for " + EconomyMessages.coins(tpost.getExported()) + " and imported for " + EconomyMessages.coins(tpost.getImported()) + ".";
		
	}
	
	public static String formedDealBrdc(TradeDeal deal, SagaPlayer sagaPlayer) {

		return anouncment + "New deal was formed by " + sagaPlayer.getName() + ": " + deal.getType().getName() + " " + EconomyMessages.material(deal.getMaterial()) + " for " + EconomyMessages.coins(deal.getPrice()) + ".";
		
	}
	
	
	
	// Town square:
	public static String noTownSquare(ChunkGroup chunkGroup){
		
		return BuildingMessages.negative + "" + chunkGroup.getName() + " deosen't have a " + TextUtil.className(TownSquare.class) + ".";
		
	}
	
	
	
	// Movement:
	public static String entered(Building building) {
		
		return normal1 + "" + ChatColor.ITALIC + "Entered " + building.getDisplayName() + ".";
		
	}
	
	public static String left(Building building) {
		
		return normal1 + "" + ChatColor.ITALIC + "Left " + building.getDisplayName() + ".";
		
	}
	
	
	
	
	// General:
	public static String cooldown(String buildingName, Integer secondsLeft) {
		return negative + TextUtil.capitalize(buildingName) + " is on cooldown for " + TextUtil.fromSeconds(secondsLeft) + ".";
	}
	
	
	
	
	// Arena:
	public static String arenaTop(Arena arena, Integer count) {
		
		
		ArrayList<ArenaPlayer> topPlayers = arena.getTop(count);
		ColorCircle messageColor = new ColorCircle().addColor(normal1).addColor(normal2);
		
		StringTable table = new StringTable(messageColor);
		
		
		Integer listLen = count;
		
		// Fix count:
		if(listLen > topPlayers.size()) listLen = topPlayers.size();
		
		// Names:
		table.addLine(new String[]{"NAME","SCORE","KILLS","DEATHS","KDR"});
		
		// Nobody:
		if(topPlayers.size() == 0){
			
			table.addLine(new String[]{"-","-","-","-","-"});
			
		}
		
		// Arena players:
		for (ArenaPlayer arenaPlayer : topPlayers) {

			listLen --;
			if(listLen < 0) break;
			
			String kdr = "";
			if(arenaPlayer.getDeaths() == 0){
				kdr = "-";
			}else{
				kdr = TextUtil.displayDouble(arenaPlayer.getKills().doubleValue() / arenaPlayer.getDeaths().doubleValue());
			}
			
			table.addLine(
				new String[]{
					arenaPlayer.getName(),
					arenaPlayer.calculateScore().intValue() + "",
					arenaPlayer.getKills().toString(),
					arenaPlayer.getDeaths().toString(),
					kdr
				});
			
			
		}
		
		table.collapse();
		
		return TextUtil.frame("Top " + count, table.createTable(), messageColor.nextColor());
		
		
	}
	
	
	
	// Farm:
	public static String farmAnimalsDamageDeny() {
		return negative + "Can't harm animals on this farms.";
	}
	
	
	
	// Home:
	public static String alreadyResident(String name) {
		return ChunkGroupMessages.negative + name + " is already a resident.";
	}
	
	public static String notResident(String name) {
		return ChunkGroupMessages.negative + name + " is not a resident.";
	}

	public static String addedResident(String name) {
		return ChunkGroupMessages.positive + "Added " + name + " to the resident list.";
	}
	
	public static String removedResident(String name) {
		return ChunkGroupMessages.positive + "Removed " + name + " from the resident list.";
	}

	
	
	
	
	// Attribute sign:
	public static String attributeMaxReached(String attribute) {
		return negative + "Can't increase " + attribute + " above " + AttributeConfiguration.config().maxAttributeScore + ".";
	}

	public static String attributeIncreased(String attribute, Integer score) {
		return positive + TextUtil.capitalize(attribute) + " increased to " + score + ".";
	}

	public static String attributePointsRequired(String attribute) {
		return negative + "Not enough attribute points to increase " + attribute + ".";
	}
	
	
	
	
	// Ability sign:
	public static String abilityMaxReached(String ability) {
		return negative + "Can't upgrade " + ability + " above " + AbilityConfiguration.config().maxAbilityScore + ".";
	}

	public static String abilityUpgraded(String ability, Integer score) {
		
		if(score == 1){
			return positive + TextUtil.capitalize(ability) + " learned.";
		}
		return positive + "Upgraded to " + GeneralMessages.scoreAbility(ability, score) + ".";
		
	}
	
	public static String abilityReqNotMet(Ability ability, Integer score) {
		
		if(score == 1){
			return negative + StatsMessages.requirements(ability.getDefinition(), score) + " is required to learn " + ability.getName() + ".";
		}
		
		return negative + TextUtil.capitalize(GeneralMessages.scoreAbility(ability.getName(), score)) + " upgrade requires " + StatsMessages.requirements(ability.getDefinition(), score) + ".";
		
	}

	public static String abilityCost(String ability, Integer score, Double cost) {
		
		if(score == 1){
			return normal1 + "It costs " + EconomyMessages.coins(cost) + " to learn " + ability + ".";
		}
		
		return normal1 + TextUtil.capitalize(GeneralMessages.scoreAbility(ability, score)) + " upgrade costs " + EconomyMessages.coins(cost) + ".";
		
	}
		
	
	
	
	// Respec sign:
	public static String respecCost(String attribute, Integer score, Double cost) {
		
		if(score == 0){
			return normal1 + TextUtil.capitalize(attribute) + " is already 0.";
		}
		
		return normal1 + TextUtil.capitalize(attribute) +  " " + score + " reset costs " + EconomyMessages.coins(cost) + ".";
		
	}
	
	public static String respec(String attribute) {
		
		return normal1 + TextUtil.capitalize(attribute) + " reset.";
		
	}
	
	public static String alreadyRespec(String attribute) {
		
		return negative + TextUtil.capitalize(attribute) + " is already 0.";
		
	}
		
	
	
	
}
