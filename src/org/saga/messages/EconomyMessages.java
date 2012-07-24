package org.saga.messages;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.saga.buildings.TradingPost;
import org.saga.chunks.ChunkBundle;
import org.saga.config.EconomyConfiguration;
import org.saga.economy.EconomyManager.TransactionType;
import org.saga.economy.TradeDeal;
import org.saga.economy.TradeDeal.TradeDealType;
import org.saga.economy.Trader;
import org.saga.economy.Transaction;
import org.saga.messages.PlayerMessages.ColorCircle;
import org.saga.player.SagaPlayer;
import org.saga.utility.text.StringBook;
import org.saga.utility.text.StringTable;
import org.saga.utility.text.TextUtil;


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

		return negative + material + " isn't a valid item.";
		
	}
	
	public static String invalidItemHand(){
		
		return negative + "Item in hand is invalid.";
		
	}
	
	public static String invalidAmount(String amount) {

		return negative + amount + " isn't a valid amount.";
		
	}
	
	public static String invalidPrice(String amount) {

		return negative + amount + " isn't a valid price.";
		
	}
	
	public static String setSell(Material material, Double price) {

		return positive + TextUtil.capitalize(material(material)) + " sell price set to " +  coins(price) + ".";
		
	}

	public static String removeSell(Material material) {

		return positive + TextUtil.capitalize(material(material)) + " sell removed.";
		
	}
	
	public static String setBuy(Material material, Double price) {

		return positive + TextUtil.capitalize(material(material)) + " buy price set to " +  coins(price) + ".";
		
	}

	public static String removeBuy(Material material) {

		return positive + TextUtil.capitalize(material(material)) + " buy removed.";
		
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
	public static String addedTransactionBroadcast(Transaction transaction, ChunkBundle chunkBundle, SagaPlayer sagaPlayer) {
		
		return anouncment + sagaPlayer.getName() + " set up a new transaction: " + transaction.getType().name() + " " + transaction.getAmount() + " " + EconomyMessages.material(transaction.getMaterial()) + " for " + EconomyMessages.coins(transaction.getValue()) + " each.";
		
	}
	
	public static String removedTransactionBroadcast(TransactionType type, Material material, ChunkBundle chunkBundle, SagaPlayer sagaPlayer) {
		
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
		

//		StringBuffer rString = new StringBuffer();
//		ColorCircle messageColor = new ColorCircle().addColor(normal1).addColor(normal2);
//		ChatColor elementColor = null;
////		
////		// Signs:
////		rString.append(tradingSignsElement(tradingPost, messageColor.nextColor()));
////		
////		rString.append("\n");
////		
//		// Info:
//		elementColor = messageColor.nextColor();
//		rString.append(elementColor);
//		rString.append("Sell signs: " + tradingPost.tradeSignCount(TransactionType.SELL));
//		rString.append(" Buy signs: " + tradingPost.tradeSignCount(TransactionType.BUY));
//		int invalidSigns = tradingPost.tradeSignCount(TransactionType.INVALID);
//		if(invalidSigns > 0){
//			rString.append(veryNegative + " Invalid signs: " + invalidSigns + elementColor);
//		}
//		rString.append(" Trade deals: " + tradingPost.getTradeDealsAmount() + "/" + tradingPost.getTradeDealsMaximumAmount());
//		
//		rString.append("\n");
//		
//		// Sell buy:
//		rString.append(sellbuyElement(tradingPost, messageColor.nextColor()));
//		
//		// Trade deals:
//		ArrayList<TradeDeal> tradeDeals = tradingPost.getDeals();
//		if(tradeDeals.size() > 0){
//			
//			rString.append("\n");
//			
//			rString.append(tradeDealsElement(tradeDeals, messageColor.nextColor(), messageColor.nextColor(), positive ));
//			
//		}
//		
//		rString.append("\n");
//		
//		// Stored:
//		elementColor = messageColor.nextColor();
//		rString.append(elementColor);
//		rString.append("Stock: ");
//		rString.append(storedElement(tradingPost, elementColor));
//
//		// Reserved:
//		if(tradingPost.getReservedCurrency() > 0.0 || tradingPost.getReservedMaterials().size() > 0){
//			
//			rString.append("\n");
//			
//			rString.append(reservedElement(tradingPost, messageColor.nextColor()));
//			
//		}
//		
//		return TextUtil.frame(tradingPost.getTradingName(), rString.toString(), messageColor.nextColor());
		
		return "";
		
	}
	
	
	
	// Economy general:
	public static String notEnoughCoins() {

		return negative + "You don't have enough " + EconomyMessages.coins() + ".";
		
	}
	
	public static String notEnoughCoins(Double cost, Double available) {

		return negative + "You need aditional " + EconomyMessages.coins(cost - available) + ".";
		
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
		return negative + "Need to be within " + maximumDistance.intValue() + "blocks to pay " + coins() + "s.";
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

		return "";
		
//		return messageColor + tradeDeal.getId().toString() + "| " + tradeDeal.getType().getName() + " " + tradeDeal.getTransactionsLeft() + "x" + tradeDeal.getAmount() + " " + material(tradeDeal.getMaterial()) + " for " + coins(tradeDeal.getValue()) + " each," + " expires in " + tradeDeal.getDaysLeft() + " days" ;
		
	}
	
	private static String dealElement(TradeDeal tradeDeal) {

		return "";
		
//		return tradeDeal.getId().toString() + "| " + tradeDeal.getType().getName() + " " + tradeDeal.getTransactionsLeft() + "x" + tradeDeal.getAmount() + " " + materialShort(tradeDeal.getMaterial()) + " for " + coins(tradeDeal.getValue()) + " each, " + tradeDeal.getDaysLeft() + " days left" ;
		
	}
	
	private static String shortTradeDealElement(TradeDeal tradeDeal, ChatColor messageColor) {

//		return messageColor + tradeDeal.getId().toString() + "| " + tradeDeal.getType().getName() + " " + tradeDeal.getAmount() + " " + material(tradeDeal.getMaterial()) + " for " + coins(tradeDeal.getValue()) + " each";
		
		return "";
		
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
		
		return negative + "The trading post can't support more than " +tradingPost.getDealsMaxCount() + " trading deals.";
		
	}
	
	public static String invalidId(String id) {
		return negative + id + " isn't a valid trade deal id.";
	}
	
	public static String reservedBroadcast(Double amount, SagaPlayer sagaPlayer, TradingPost tradingPost) {
		
		return anouncment + sagaPlayer.getName() + " reserved " + tradingPost.getDisplayName() + " cois to " + coins(amount) + ".";
		
	}
	
	public static String reservedBroadcast(Material material, Integer amount, SagaPlayer sagaPlayer, TradingPost tradingPost) {
		
		return anouncment + sagaPlayer.getName() + " reserved " + tradingPost.getDisplayName() + " " + material(material) + " to " + amount + ".";
		
	}
	
	
	// Trade deals:
	public static String tradeDeals(ArrayList<TradeDeal> tradeDeals, Integer page){
		

		return "";
		
//		int pageSize = 10;
//		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
//		StringBook book = new StringBook("Trade deals", color, pageSize);
//		StringTable table = new StringTable(color);
//		
//		
//		// Names:
//		String[] titles = new String[]{"ID", "TYPE", "ITEM", "VALUE", "DAYS", "TRANSACTIONS"};
//		int titleLines = 0;
//		
//		// Create the book:
//		if(tradeDeals.size() > 0){
//			
//			for (int i = 0; i < tradeDeals.size(); i++) {
//				
//				if(titleLines == 0){
//					table.addLine(titles);
//					titleLines = pageSize - 1;
//					i--;
//					continue;
//				}else{
//					titleLines --;
//				}
//				
//				TradeDeal deal = tradeDeals.get(i);
//				
//				String[] lines = new String[]{deal.getId().toString(), deal.getType().getName(), materialShort(deal.getMaterial()), EconomyMessages.coins(deal.getValue()), deal.getDaysLeft().toString(), deal.getAmount().toString() + "x" + deal.getTransactionsLeft().toString()};
//				
//				table.addLine(lines);
//				
//			}
//			
//		}
//		
//		table.collapse();
//		
//		book.addTable(table);
//		
//		return book.framed(page);
		
		
	}
	
	public static String deals(ArrayList<TradeDeal> tradeDeals, TradeDealType type, Integer page){
		

		int pageSize = 10;
		ColorCircle colour = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook(type.getName(), colour, pageSize);
		StringTable table = new StringTable(colour);
		
		// Filter out correct deals:
		ArrayList<TradeDeal> correctDeals = new ArrayList<TradeDeal>();
		for (TradeDeal tradeDeal : tradeDeals) {
			if(tradeDeal.getType() == type) correctDeals.add(tradeDeal);
		}
		tradeDeals = correctDeals;
		
		// Names:
		String[] titles = new String[]{"ID", "ITEM", "PRICE", "DAYS", "AMOUNT"};
		int titleLines = 0;
		
		// Create the book:
		if(tradeDeals.size() > 0){
			
			for (int i = 0; i < tradeDeals.size(); i++) {
				
				// Names:
				if(titleLines == 0){
					table.addLine(titles);
					titleLines = pageSize - 1;
					i--;
					continue;
				}else{
					titleLines --;
				}
				
				TradeDeal deal = tradeDeals.get(i);
				
				String[] lines = new String[]{deal.getId().toString(), materialShort(deal.getMaterial()), EconomyMessages.coins(deal.getPrice()), deal.getDaysLeft().toString(), deal.getAmount().toString()};
				
				table.addLine(lines);
				
			}
			
		}else{
			table.addLine(new String[]{"ID", "ITEM", "PRICE", "DAYS", "AMOUNT"});
			table.addLine(new String[]{"-", "-", "-", "-", "-"});
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
	
	
	
	// Sell/buy signs:
	public static String insufItems(Material items) {
		
		return negative + "You don't have enough " + material(items) + ".";
		
	}
	
	public static String insufItems(Trader trader, Material items) {
		
		return negative + TextUtil.capitalize(trader.getTradingName()) + " doesn't have enough " + material(items) + ".";
		
	}
	
	public static String insufCoins() {
		
		return negative + "You don't have enough " + coins() + ".";
		
	}
	
	public static String insufCoins(Trader trader) {
		
		return negative + TextUtil.capitalize(trader.getTradingName()) + " doesn't have enough " + coins() + ".";
		
	}
	
	public static String sold(Material item, Integer amount, Double price) {
		
		return positive + "Sold " + amount + " " + material(item) + " for " + coins(price * amount) + ".";
		
	}
	
	public static String bought(Material item, Integer amount, Double price) {
	
	return positive + "Bought " + amount + " " + material(item) + " for " + coins(price * amount) + ".";
	
}
	
	
	// Naming:
	/**
	 * Gets the styled name for currency.
	 * 
	 * @param amount amount
	 * @return styled name
	 */
	public static String coins(Double amount) {

		return TextUtil.displayDouble(amount) + "" + coins();
		
	}
	
	/**
	 * Gets currency name
	 * 
	 * @return currency name
	 */
	public static String coins(){
		return EconomyConfiguration.config().coinName;
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
		else if(result.startsWith("cobblestone")){
			result = result.replace("cobblestone", "cobble");
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
