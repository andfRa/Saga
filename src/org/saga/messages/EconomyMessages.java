package org.saga.messages;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.saga.chunks.ChunkBundle;
import org.saga.config.EconomyConfiguration;
import org.saga.economy.EconomyManager.TransactionType;
import org.saga.economy.Trader;
import org.saga.economy.Transaction;
import org.saga.player.SagaPlayer;
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

	
	
	// Buy/sell signs:
	public static String notEnoughMaterial(Material material) {

		return negative + "You don't have enough " + EconomyMessages.material(material) + ".";
		
	}

	public static String notEnoughStoredMaterial(Material material) {

		return negative + "Not enough " + EconomyMessages.material(material) + " stored.";
		
	}
	
	public static String notEnoughStoredCoins() {

		return negative + "Not enough " + EconomyMessages.coins() + " stored.";
		
	}
	
	public static String signNotActive() {
		return negative + "Sign not active.";
	}
	
	
	
	// Trading post:
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
	
	
	
	// User:
	public static String wallet(SagaPlayer sagaPlayer) {
		return positive + "Wallet: " + EconomyMessages.coins(sagaPlayer.getCoins()) + ".";
	}
	

	
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
