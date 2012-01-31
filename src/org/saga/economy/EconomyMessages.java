package org.saga.economy;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.saga.SagaMessages;
import org.saga.buildings.Building;
import org.saga.buildings.TradingPost;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.config.ChunkGroupConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.economy.EconomyManager.TransactionType;
import org.saga.player.PlayerMessages.ColorCircle;
import org.saga.player.SagaPlayer;
import org.saga.utility.StringBook;
import org.saga.utility.StringTable;
import org.saga.utility.TextUtil;


public class EconomyMessages {


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
	

	public static String transaction2(SagaPlayer sagaPlayer, TradingPost building, Transaction transaction) {

		return positive + "Traded " + transaction.getTotalValue() + " " + transaction.getMaterial() + " for " + transaction.getAmount() + " " + transaction.getMaterial() + ".";
		
	}
	
	public static String notEnoughMaterial(Material material) {

		return negative + "You don't have enough " + EconomyMessages.material(material) + ".";
		
	}

	public static String notEnoughStoredMaterial(Material material) {

		return negative + "Not enough " + EconomyMessages.material(material) + " stored.";
		
	}
	
	public static String notEnoughStoredMoney() {

		return negative + "Not enough " + EconomyMessages.coins() + " stored.";
		
	}
	
	public static String signNotActive() {
		return negative + "Sign not active.";
	}
	
	public static String invalidMaterial(String material) {

		return negative + material + " isn't a valid material.";
		
	}
	
	public static String invalidAmount(String amount) {

		return negative + amount + " isn't a valid amount.";
		
	}
	
	public static String invalidValue(String value) {

		return negative + value + " isn't a valid value.";
		
	}
	
	public static String nonexistantTransaction(TransactionType transactionType, Material material) {

		return negative + transactionType.name() + " transaction for " + EconomyMessages.material(material) + " doesen't exist.";
		
	}
	
	
	// General:
	public static String spent(Double coins) {
		
		return positive + "Spent " + coins(coins) + ".";
		
	}
	
	public static String earned(Double coins) {
		
		return positive + "Earned " + coins(coins) + ".";
		
	}
	
	public static String insuficcientCoins(Double coinsRequired) {
		
		return negative + coins(coinsRequired) + " required.";
		
	}
	
	// Transactions:
	public static String addedTransactionBroadcast(Transaction transaction, ChunkGroup chunkGroup, SagaPlayer sagaPlayer) {
		
		return anouncment + sagaPlayer.getName() + " set up a new transaction: " + transaction.getType().name() + " " + transaction.getAmount() + " " + EconomyMessages.material(transaction.getMaterial()) + " for " + EconomyMessages.coins(transaction.getValue()) + " each.";
		
	}
	
	public static String removedTransactionBroadcast(TransactionType type, Material material, ChunkGroup chunkGroup, SagaPlayer sagaPlayer) {
		
		return anouncment + sagaPlayer.getName() + " removed a transaction: " + type.name() + " " + EconomyMessages.material(material) + ".";
		
	}
	
	public static String targeterTrade(Transaction transaction) {
		
		if(transaction.getType().equals(TransactionType.SELL)){
			
			return positive + "Sold " + transaction.getAmount() + " " + material(transaction.getMaterial()) + " for " + coins(transaction.getTotalValue()) + ".";
			
		}else if(transaction.getType().equals(TransactionType.BUY)){
			
			return positive + "Bought " + transaction.getAmount() + " " + material(transaction.getMaterial()) + " for " + coins(transaction.getTotalValue()) + ".";
			
		}else{
			
			return veryNegative + " Invalid transaction type: " +transaction.getType() + ", " + transaction.getAmount() + ", " + material(transaction.getMaterial()) + ", " + coins(transaction.getTotalValue());
			
		}
		
		
	}
	
	
	// Trading post:
	public static String nothingInHand(){
		
		return negative + "You don't have any items in hand.";
		
	}
	
	public static String putItems(ItemStack itemStack, TradingPost tradingPost){
		
		return positive + "You added " + itemStack.getAmount() + " " + material(itemStack.getType()) + " to the trading post.";
		
	}
	
	public static String donatedItemsBroadcast(ItemStack itemStack, TradingPost tradingPost, SagaPlayer sagaPlayer){
		
		return anouncment + sagaPlayer.getName() + " donated " + itemStack.getAmount() + " " + material(itemStack.getType()) + " to the trading post.";
		
	}
	
	public static String donatedCurrencyBroadcast(Double currency, TradingPost tradingPost, SagaPlayer sagaPlayer){
		
		return anouncment + sagaPlayer.getName() + " donated " + coins(currency) + " to the trading post.";
		
	}
	
	public static String tradingpost(TradingPost tradingPost){
		

		StringBuffer rString = new StringBuffer();
		ColorCircle messageColor = new ColorCircle().addColor(normal1).addColor(normal2);
		ChatColor elementColor = null;
//		
//		// Signs:
//		rString.append(tradingSignsElement(tradingPost, messageColor.nextColor()));
//		
//		rString.append("\n");
//		
		// Info:
		elementColor = messageColor.nextColor();
		rString.append(elementColor);
		rString.append("Sell signs: " + tradingPost.tradeSignCount(TransactionType.SELL));
		rString.append(" Buy signs: " + tradingPost.tradeSignCount(TransactionType.BUY));
		int invalidSigns = tradingPost.tradeSignCount(TransactionType.INVALID);
		if(invalidSigns > 0){
			rString.append(veryNegative + " Invalid signs: " + invalidSigns + elementColor);
		}
		rString.append(" Trade deals: " + tradingPost.getTradeDealsAmount() + "/" + tradingPost.getTradeDealsMaximumAmount());
		
		rString.append("\n");
		
		// Sell buy:
		rString.append(sellbuyElement(tradingPost, messageColor.nextColor()));
		
		// Trade deals:
		ArrayList<TradeDeal> tradeDeals = tradingPost.getTradeDeals();
		if(tradeDeals.size() > 0){
			
			rString.append("\n");
			
			rString.append(tradeDealsElement(tradeDeals, messageColor.nextColor(), messageColor.nextColor(), positive ));
			
		}
		
		rString.append("\n");
		
		// Stored:
		elementColor = messageColor.nextColor();
		rString.append(elementColor);
		rString.append("Stock: ");
		rString.append(storedElement(tradingPost, elementColor));

		// Reserved:
		if(tradingPost.getReservedCurrency() > 0.0 || tradingPost.getReservedMaterials().size() > 0){
			
			rString.append("\n");
			
			rString.append(reservedElement(tradingPost, messageColor.nextColor()));
			
		}
		
		return TextUtil.frame(tradingPost.getTradingName(), rString.toString(), messageColor.nextColor());
		
		
	}
	
	private static String storedElement(TradingPost tradingPost, ChatColor messageColor){
		
		
		StringBuffer rString = new StringBuffer();
		Hashtable<Material, Integer> stock = tradingPost.getStockedItems();
		
		Enumeration<Material> materials = stock.keys();
		
		// Currency:
		rString.append(coins(tradingPost.getCoins()));
		
		// Resources:
		while (materials.hasMoreElements()) {
			
			Material material = (Material) materials.nextElement();
			
			rString.append(", ");
			
			rString.append( stock.get(material) + " " + material(material));
			
		}
		
		rString.insert(0, messageColor);
		
		return rString.toString();
		
		
	}
	
	
	// Economy general:
	public static String sellbuyElement(Trader trader, ChatColor messageColor){
		
		
		StringBuffer rString = new StringBuffer();
		
		ArrayList<Transaction> transactions = trader.getTransactions();
		ArrayList<Transaction> sellTransactons = new ArrayList<Transaction>();
		ArrayList<Transaction> buyTransactons = new ArrayList<Transaction>();
		
		for (Transaction transaction : transactions) {
			if(transaction.getType().equals(TransactionType.SELL)){
				sellTransactons.add(transaction);
			}else if(transaction.getType().equals(TransactionType.BUY)){
				buyTransactons.add(transaction);
			}
		}
		
			
		// Sell:
		rString.append("Sell: ");
		if(sellTransactons.size() > 0){
			
			for (int i = 0; i < sellTransactons.size(); i++) {
				
				if(i != 0) rString.append(", ");
				
				Transaction transaction = sellTransactons.get(i);
				
				String element = transaction.getAmount() + " " +material(transaction.getMaterial()) + " for " + coins(transaction.getValue()) + " each"; 
				
				if( !trader.isActive(transaction.getType(), transaction.getMaterial()) ){
					rString.append( unavailable + element + messageColor);
				}else{
					rString.append(element);
				}
				
			}
			
		}else{
			rString.append("nothing");
		}
		
		rString.append("\n");
		
		// Buy:
		rString.append("Buy: ");
		if(buyTransactons.size() > 0){
			
			if(rString.length() > 0){
				
			}
			
			for (int i = 0; i < buyTransactons.size(); i++) {
				
				if(i != 0) rString.append(", ");
				
				Transaction transaction = buyTransactons.get(i);
				
				String element = transaction.getAmount() + " " +material(transaction.getMaterial()) + " for " + transaction.getValue() + " each"; 
				
				if( !trader.isActive(transaction.getType(), transaction.getMaterial()) ){
					rString.append( unavailable + element + messageColor);
				}else{
					rString.append(element);
				}
				
			}
			
		}else{
			rString.append("nothing");
		}
		
		rString.insert(0, messageColor);
		
		return rString.toString();
		
		
	}

	public static String notEnoughCoins() {

		return negative + "You don't have enough " + EconomyMessages.coins() + ".";
		
	}
	
	public static String notNumber(String number) {
		return negative + number + " is not a number.";
	}
	
	
	// Exchange:
	public static String paid(SagaPlayer paidPlayer, Double amount) {
		return positive + "Gave " + coins(amount) + "s to " + paidPlayer.getName() + ".";
	}
	
	public static String gotPaid(SagaPlayer payerPlayer, Double amount) {
		return positive + "Received " + coins(amount) + "s from " + payerPlayer.getName() + ".";
	}
	
	public static String notOnline(String name) {
		return negative + name + " isnt online.";
	}
	
	public static String tooFarPay(Double maximumDistance) {
		return negative + "Need to be within " + maximumDistance.intValue() + "m to exchange " + coins() + "s.";
	}
	
	public static String tooFarPay() {
		return negative + "Too far to exchange " + coins() + ".";
	}
	
	public static String setWallet(SagaPlayer targetPlayer, Double amount) {
		return positive + "Set "  + targetPlayer.getName() + " wallet to " + coins(amount) + "s.";
	}
	
	public static String walletWasSet(Double amount) {
		return positive + "Wallet was set to " + coins(amount) + "s.";
	}
	
	
	// Trade deals:
	private static String tradeDealElement(TradeDeal tradeDeal, ChatColor messageColor) {

		return messageColor + tradeDeal.getId().toString() + "| " + tradeDeal.getType().getName() + " " + tradeDeal.getTransactionsLeft() + "x" + tradeDeal.getAmount() + " " + material(tradeDeal.getMaterial()) + " for " + coins(tradeDeal.getValue()) + " each," + " expires in " + tradeDeal.getDaysLeft() + " days" ;
		
	}
	
	private static String dealElement(TradeDeal tradeDeal) {

		return tradeDeal.getId().toString() + "| " + tradeDeal.getType().getName() + " " + tradeDeal.getTransactionsLeft() + "x" + tradeDeal.getAmount() + " " + materialShort(tradeDeal.getMaterial()) + " for " + coins(tradeDeal.getValue()) + " each, " + tradeDeal.getDaysLeft() + " days left" ;
		
	}
	
	private static String shortTradeDealElement(TradeDeal tradeDeal, ChatColor messageColor) {

		return messageColor + tradeDeal.getId().toString() + "| " + tradeDeal.getType().getName() + " " + tradeDeal.getAmount() + " " + material(tradeDeal.getMaterial()) + " for " + coins(tradeDeal.getValue()) + " each";
		
	}

	private static String reservedElement(TradingPost tradingPost, ChatColor messageColor) {

		
		StringBuffer rString = new StringBuffer();
		
		ArrayList<Material> reserved = tradingPost.getReservedMaterials();
		
		if(reserved.size() == 0 && tradingPost.getReservedCurrency() <= 0){
			rString.append("Reserved: none");
		}else{
			rString.append("Reserved: ");
		}
		
		boolean first = true;
		
		// Currency:
		if(tradingPost.getReservedCurrency() > 0){
			
			rString.append(coins(tradingPost.getReservedCurrency()));
			
			first = false;
			
		}
		
		// Materials:
		for (int i = 0; i < reserved.size(); i++) {
			
			if(!first){
				rString.append(", ");
			}else{
				first = false;
			}
			
			rString.append(tradingPost.getReservedAmount(reserved.get(i)) + " " + material(reserved.get(i)));
			
		}
		
		rString.insert(0, messageColor);
		
		return rString.toString();
		
		
	}
	
	public static String tradeDealsElement(ArrayList<TradeDeal> tradeDeals, ChatColor messageColor1, ChatColor messageColor2, ChatColor highlightColor){
		
		
		StringBuffer rString = new StringBuffer();
		ColorCircle messageColor = new ColorCircle().addColor(messageColor1).addColor(messageColor2);
		
		
		// All:
		if(tradeDeals.size() > 0){
			
			for (int i = 0; i < tradeDeals.size(); i++) {
				
				if(i != 0) rString.append("\n");
				
				rString.append(tradeDealElement(tradeDeals.get(i), messageColor.nextColor()));
				
			}
			
		}
		
		rString.insert(0, messageColor.nextColor());
		
		return rString.toString();
		
		
	}
	
	public static String formedTradeDealBroadcast(TradeDeal tradeDeal, SagaPlayer sagaPlayer) {

		return anouncment + sagaPlayer.getName() + " formed a deal: " + shortTradeDealElement(tradeDeal, anouncment) + ".";
		
	}
	
	public static String broadcastTradeDealFormation(TradeDeal tradeDeal) {

		return anouncment + "Formed a deal: " + shortTradeDealElement(tradeDeal, anouncment) + ".";
		
	}
	
	public static String removedTradeDealBroadcast(TradeDeal tradeDeal, SagaPlayer sagaPlayer) {

		return anouncment + sagaPlayer.getName() + " broke a deal: " + shortTradeDealElement(tradeDeal, anouncment) + ".";
		
	}
	
	public static String expiredTradeDealBroadcast(TradeDeal tradeDeal) {

		return anouncment + "Deal expired: " + shortTradeDealElement(tradeDeal, anouncment) + ".";
		
	}
	
	public static String completedTradeDealBroadcast(TradeDeal tradeDeal) {

		return anouncment + "Deal complete: " + shortTradeDealElement(tradeDeal, anouncment) + ".";
		
	}

	public static String reportTradeDealBroadcast(String name, Double currency, Hashtable<Material, Integer> imported, Hashtable<Material, Integer> exported) {

		
		StringBuffer rString = new StringBuffer();
		
		// Name:
		rString.append(TextUtil.capitalize(name) + "s report:");
		
		// Currency:
		if(currency > 0){
			rString.append(" Earned " + coins(currency));
			rString.append(".");
		}else if(currency < 0){
			currency *= -1;
			rString.append(" Spent " + coins(currency));
			rString.append(".");
		}
		
		// Import:
		if(imported.size() > 0){
				
			rString.append(" Imported ");
		
			boolean first = false;
			Enumeration<Material> materials = imported.keys();
			while (materials.hasMoreElements()) {
				
				Material material = materials.nextElement();
				
				if(!first){
					rString.append(", ");
				}
				first = true;
				
				rString.append(imported.get(material) + " " + material(material));
				
			}
			
			rString.append(".");
		
		}

			
		// Export:
		if(exported.size() > 0){
			
			
			rString.append(" Exported ");
		
			boolean first = true;
			Enumeration<Material> materials = exported.keys();
			while (materials.hasMoreElements()) {
				
				Material material = materials.nextElement();
				
				if(!first){
					rString.append(", ");
				}
				first = false;
				
				rString.append(exported.get(material) + " " + material(material));
				
			}
			
			rString.append(".");
		
		}
		
		// No transactions:
		if(currency == 0 && imported.size() == 0 && exported.size() == 0){
			rString.append(" No exports and imports today.");
		}
		
		rString.insert(0, anouncment);
		
		return rString.toString();
		
		
	}
	
	public static String listDeals(ArrayList<TradeDeal> tradeDeals, Integer page){
		
		
		ColorCircle messageColor = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook("Trade deals", messageColor, 10);
		
		
		// Create the book:
		if(tradeDeals.size() > 0){
			
			for (int i = 0; i < tradeDeals.size(); i++) {
				
				book.addLine( dealElement(tradeDeals.get(i)) );
				
			}
			
		}
		
//		return book.framed(page);
		return tradeDeals(tradeDeals, page);
		
	}
	
	public static String tradeDealLimitReached(TradingPost tradingPost){
		
		return negative + "The trading post can't support more than " +tradingPost.getTradeDealsMaximumAmount() + " trading deals.";
		
	}
	
	public static String invalidId(String id) {
		return negative + id + " isn't a valid trade deal id.";
	}
	
	public static String reservedBroadcast(Double amount, SagaPlayer sagaPlayer, TradingPost tradingPost) {
		
		return anouncment + sagaPlayer.getName() + " reserved " + tradingPost.getDisplayName() + " currency to " + coins(amount) + ".";
		
	}
	
	public static String reservedBroadcast(Material material, Integer amount, SagaPlayer sagaPlayer, TradingPost tradingPost) {
		
		return anouncment + sagaPlayer.getName() + " reserved " + tradingPost.getDisplayName() + " " + material(material) + " to " + amount + ".";
		
	}
	
	
	// Trade deals:
	public static String tradeDeals(ArrayList<TradeDeal> tradeDeals, Integer page){
		

		int pageSize = 10;
		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook("Trade deals", color, pageSize);
		StringTable table = new StringTable(color);
		
		
		// Names:
		String[] titles = new String[]{"ID", "TYPE", "ITEM", "VALUE", "DAYS", "TRANSACTIONS"};
		int titleLines = 0;
		
		// Create the book:
		if(tradeDeals.size() > 0){
			
			for (int i = 0; i < tradeDeals.size(); i++) {
				
				if(titleLines == 0){
					table.addLine(titles);
					titleLines = pageSize - 1;
					i--;
					continue;
				}else{
					titleLines --;
				}
				
				TradeDeal deal = tradeDeals.get(i);
				
				String[] lines = new String[]{deal.getId().toString(), deal.getType().getName(), materialShort(deal.getMaterial()), EconomyMessages.coins(deal.getValue()), deal.getDaysLeft().toString(), deal.getAmount().toString() + "x" + deal.getTransactionsLeft().toString()};
				
				table.addLine(lines);
				
			}
			
		}
		
		table.collapse();
		
		book.addTable(table);
		
		return book.framed(page);
		
		
	}
	
	// User:
	public static String wallet(SagaPlayer sagaPlayer) {
		return positive + "Wallet: " + EconomyMessages.coins(sagaPlayer.getCoins()) + ".";
	}
	

	// Help:
	public static String help(int page) {

		
		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook("building help", color, 9);
		
		// Buildings:
		ArrayList<Building> buildings = ChunkGroupConfiguration.config().getBuildings();
		ArrayList<String> buildingNames = new ArrayList<String>();
		
		for (Building building : buildings) {
			
			buildingNames.add(building.getName().replaceAll(" ", SagaMessages.spaceSymbol));
			
		}
		
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
		book.addLine("Place \"=[sell]= | item\" and \"=[buy]= | item\" signs to sell and buy items.");

		// Donate:
		book.addLine("To get the trading post running, you will need to donate items or coins.");
		
		// Donate:
		book.addLine("/donate, /donatec <amount> or /donateall <item> to donate item in hand, coins or all items of the given type.");
		
		// Export import:
		book.addLine("A deal needs to be formed to export or import items.");

		// Deals:
		book.addLine("/edeals to list all deals.");

		// Expiration:
		book.addLine("/bnewdeal <ID> to form a deal. Deal will expire after certain amout of items or time.");

		// Timing:
		book.addLine("After a deal is formed, the goods will get exported/imported each sunrise.");

		
		return book.framed(page, 76.0);
		
		
	}
	
	
	
	// Naming:
	/**
	 * Gets the styled name for currency.
	 * 
	 * @param amount amount
	 * @return styled name
	 */
	public static String coins(Double amount) {

		return TextUtil.displayDouble(amount) + "" + EconomyConfiguration.config().currencyName;
		
	}
	
	/**
	 * Gets currency name
	 * 
	 * @return currency name
	 */
	public static String coins(){
		return EconomyConfiguration.config().currencyName;
	}
	
	/**
	 * Gets material short name,
	 * 
	 * @param material material
	 * @return material name
	 */
	public static String materialShort(Material material){
		
		
		String result = material.toString().toLowerCase().replace("_", " ");
		
		if(result.startsWith("wood ")){
			result = result.replace("wood ", "wd. ");
		}
		else if(result.startsWith("stone ")){
			result = result.replace("stone ", "st. ");
		}
		else if(result.startsWith("iron ")){
			result = result.replace("iron ", "ir. ");
		}
		else if(result.startsWith("gold ")){
			result = result.replace("gold ", "gl. ");
		}
		else if(result.startsWith("diamond ")){
			result = result.replace("diamond ", "di. ");
		}
		
		return result;
		
		
	}
	

	/**
	 * Gets material name.
	 * 
	 * @param material material
	 * @return material name
	 */
	public static String material(Material material){
		
		String result = material.toString().toLowerCase().replace("_", " ");
		
		return result;
		
	}

	/**
	 * Gets material names.
	 * 
	 * @param materials materials
	 * @return material names
	 */
	public static String materials(HashSet<Material> materials){
		
		
		ArrayList<String> names = new ArrayList<String>();
		
		for (Material material : materials) {
			names.add(material(material));
		}
		
		return TextUtil.flatten(names);
		
	}


	/**
	 * Gets short material names.
	 * 
	 * @param materials materials
	 * @return material names
	 */
	public static String materialsShort(HashSet<Material> materials){
		
		
		ArrayList<String> names = new ArrayList<String>();
		
		for (Material material : materials) {
			names.add(materialShort(material));
		}
		
		return TextUtil.flatten(names);
		
	}

	
}
