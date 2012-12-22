package org.saga.messages;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.saga.config.EconomyConfiguration;
import org.saga.dependencies.EconomyDependency;
import org.saga.dependencies.Trader;
import org.saga.factions.Faction;
import org.saga.player.SagaPlayer;
import org.saga.utility.text.TextUtil;


public class EconomyMessages {


	// Colours:
	public static ChatColor veryPositive = ChatColor.DARK_GREEN; // DO NOT OVERUSE.
	
	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED; // DO NOT OVERUSE.
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor announce = ChatColor.AQUA;
	
	public static ChatColor normal1 = ChatColor.GOLD;
	
	public static ChatColor normal2 = ChatColor.YELLOW;
	
	public static ChatColor frame = ChatColor.DARK_GREEN;

	
	
	// Economy general:
	public static String coinsSpent(Double amount) {
		return normal2 + "Spent " + EconomyMessages.coins(amount) + ".";
	}
	
	
	
	// Buy/sell signs:
	public static String notEnoughMaterial(Material material) {

		return negative + "You don't have enough " + GeneralMessages.material(material) + ".";
		
	}

	public static String notEnoughStoredMaterial(Material material) {

		return negative + "Not enough " + GeneralMessages.material(material) + " stored.";
		
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

		return positive + TextUtil.capitalize(GeneralMessages.material(material)) + " sell price set to " +  coins(price) + ".";
		
	}

	public static String removeSell(Material material) {

		return positive + TextUtil.capitalize(GeneralMessages.material(material)) + " sell removed.";
		
	}
	
	public static String setBuy(Material material, Double price) {

		return positive + TextUtil.capitalize(GeneralMessages.material(material)) + " buy price set to " +  coins(price) + ".";
		
	}

	public static String removeBuy(Material material) {

		return positive + TextUtil.capitalize(GeneralMessages.material(material)) + " buy removed.";
		
	}
	
	
	
	// Coins management:
	public static String spent(Double coins) {
		
		return positive + "Spent " + coins(coins) + ".";
		
	}
	
	public static String earned(Double coins) {
		
		return positive + "Earned " + coins(coins) + ".";
		
	}
	
	public static String insuficcientCoins(Double coinsRequired) {
		
		return negative + coins(coinsRequired) + " required.";
		
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
	
	public static String walletModified(SagaPlayer targetPlayer, Double amount) {
		
		if(amount >= 0) return positive + "Added " + EconomyMessages.coins(amount) + " to players " + targetPlayer.getName() + " wallet.";
		
		return positive + "Removed " + EconomyMessages.coins(amount) + " from players " + targetPlayer.getName() + " wallet.";
		
	}
	
	public static String walletModified(Double amount) {
	
		if(amount >= 0) return positive + "Added " + EconomyMessages.coins(amount) + " to wallet.";
		
		return positive + "Removed " + EconomyMessages.coins(amount) + " from wallet.";
		
	}
	
	
	
	// User:
	public static String wallet(SagaPlayer sagaPlayer) {
		return positive + "Wallet: " + EconomyMessages.coins(EconomyDependency.getCoins(sagaPlayer)) + ".";
	}
	

	
	// Sell/buy signs:
	public static String insufItems(Material items) {
		
		return negative + "You don't have enough " + GeneralMessages.material(items) + ".";
		
	}
	
	public static String insufItems(Trader trader, Material items) {
		
		return negative + TextUtil.capitalize(trader.getName()) + " doesn't have enough " + GeneralMessages.material(items) + ".";
		
	}
	
	public static String insufCoins() {
		
		return negative + "You don't have enough " + coins() + ".";
		
	}
	
	public static String insufCoins(Trader trader) {
		
		return negative + TextUtil.capitalize(trader.getName()) + " doesn't have enough " + coins() + ".";
		
	}
	
	public static String sold(Material item, Integer amount, Double price) {
		
		return positive + "Sold " + amount + " " + GeneralMessages.material(item) + " for " + coins(price * amount) + ".";
		
	}
	
	public static String bought(Material item, Integer amount, Double price) {
	
	return positive + "Bought " + amount + " " + GeneralMessages.material(item) + " for " + coins(price * amount) + ".";
	
}
	
	
	
	// Wages:
	public static String gotPaid(Faction faction, Double amount) {
		
		return faction.getColour2() + "Received " + coins(amount) + " in wages.";
		
	}
	
	public static String gotKillReward(SagaPlayer attakerPlayer, SagaPlayer defenderPlayer, Faction faction, Double amount) {
		return faction.getColour2() + attakerPlayer.getName() + " received " + coins(amount) + " for killing " + defenderPlayer.getName() + ".";
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
	 * Gets material names.
	 * 
	 * @param materials materials
	 * @return material names
	 */
	public static String materials(HashSet<Material> materials){
		
		
		ArrayList<String> names = new ArrayList<String>();
		
		for (Material material : materials) {
			names.add(GeneralMessages.material(material));
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
