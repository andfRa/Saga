package org.saga.buildings;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.saga.SagaMessages;
import org.saga.buildings.Arena.ArenaPlayer;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.config.ChunkGroupConfiguration;
import org.saga.economy.EconomyMessages;
import org.saga.economy.TradeDeal;
import org.saga.economy.TradeDeal.TradeDealType;
import org.saga.player.PlayerMessages.ColorCircle;
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
	public static String newBuilding(ChunkGroup chunkGroup, Building building, SagaPlayer sagaPlayer) {
		return anouncment + sagaPlayer.getName() + " added a " + building.getDisplayName() + " to the settlement.";
	}
	
	public static String deletedBuilding(ChunkGroup chunkGroup, Building building, SagaPlayer sagaPlayer) {
		return anouncment + sagaPlayer.getName() + " removed a " + building.getDisplayName() + " from the settlement.";
	}
	
	public static String buildingsOnClaimedLand(ChunkGroup chunkGroup) {
		return negative + "Buildings can only be on located on claimed land.";
	}
	
	public static String oneBuildingPerChunk(ChunkGroup chunkGroup) {
		return negative + "A chunk of land can only have one building.";
	}
	
	public static String invalidName(String buildingName) {
		return negative + buildingName + " isn't a valid building.";
	}
	
	public static String unavailableBuilding(ChunkGroup chunkGroup, Building building){
		
		return negative + TextUtil.capitalize(building.getDisplayName()) + " isn't available.";
		
	}

	
	// Commands:
	public static String invalidBuilding(String correctBuildingName, String command){
		
		return negative + command + " can only be used from a " + correctBuildingName + ".";
		
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
	

	// Help:
	public static String info() {

		
		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook("building info", color, 10);
		
		// Buildings:
		ArrayList<Building> buildings = ChunkGroupConfiguration.config().getBuildings();
		ArrayList<String> buildingNames = new ArrayList<String>();
		
		for (Building building : buildings) {
			
			buildingNames.add(building.getName().replaceAll(" ", SagaMessages.spaceSymbol));
			
		}
		
		book.addLine("All buildings: " + TextUtil.flatten(buildingNames));

		// Requirements:
		book.addLine("/binfo <building_name> for more details.");

		return book.framed(0, 76.0);
		
		
	}
	
	public static String info(String buildingName, BuildingDefinition definition) {
		
		
		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook(buildingName + " help", color, 10);
		
		// Description:
		book.addLine(definition.getDescription());
		
		// Skills:
		book.addLine("skills: " + TextUtil.flatten(definition.getSkills()));

		// Select:
		book.addLine("classes/profs: " + TextUtil.flatten(definition.getSelectable()));
		
		// Roles:
		HashSet<String> allRoles = definition.getRoles((short)0);
		HashSet<String> roles = new HashSet<String>();
		for (String role : allRoles) {

			roles.add(definition.getTotalRoles(role, (short)0) + " " + role);
			
		}
		book.addLine("roles: " + TextUtil.flatten(roles));
		
		// Buildings:
		HashSet<String> allBuilings = definition.getBuildings((short)0);
		HashSet<String> buildings = new HashSet<String>();
		for (String building : allBuilings) {

			buildings.add(definition.getTotalBuildings(building, (short)0) + " " + building);
			
		}
		book.addLine("enabled buildings: " + TextUtil.flatten(buildings));
		
		// Building points:
		book.addLine("building point cost: " + definition.getPointCost((short)0));
		
		return book.framed(0, 76.0);
		
		
	}
	
	
	
}
