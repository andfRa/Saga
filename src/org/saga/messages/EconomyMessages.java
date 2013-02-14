package org.saga.messages;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.saga.buildings.production.SagaItem;
import org.saga.config.EconomyConfiguration;
import org.saga.dependencies.EconomyDependency;
import org.saga.factions.Faction;
import org.saga.messages.colours.Colour;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Settlement;
import org.saga.utility.chat.ChatUtil;


public class EconomyMessages {


	// Commands:
	public static String invalidMaterial(String material) {
		return Colour.negative + "Item " + material + " is invalid.";
	}
	
	public static String economyDisabled() {
		return Colour.negative + "Economy disabled.";
	}
	
	
	// Coins:
	public static String spent(Double coins) {
		
		return Colour.positive + "Spent " + coins(coins) + ".";
		
	}
	
	public static String earned(Double coins) {
		
		return Colour.positive + "Earned " + coins(coins) + ".";
		
	}
	
	public static String insufficient(Double coinsRequired) {
		
		return Colour.negative + coins(coinsRequired) + " required.";
		
	}

	public static String insufficient() {

		return Colour.negative + "Insufficient " + EconomyMessages.coins() + ".";
		
	}
	
	
	
	// Settlement:
	public static String settlementInsufficientCoins() {
		return Colour.negative + "The settlement doesn't have enough " + EconomyMessages.coins() + ".";
	}
	
	public static String settlementBoughtClaims(Integer amount, Double cost) {
		
		if(amount == 1){
			return Colour.positive + "Bought a claim for " + EconomyMessages.coins(cost) + ".";
		}else{
			return Colour.positive + "Bought "+ amount +" claims for " + EconomyMessages.coins(cost) + ".";
		}
		
	}
	
	public static String settlementBoughtBuildPoints(Integer amount, Double cost) {
		
		if(amount == 1){
			return Colour.positive + "The settlement bought a build point for " + EconomyMessages.coins(cost) + ".";
		}else{
			return Colour.positive + "The settlement bought "+ amount +" build points for " + EconomyMessages.coins(cost) + ".";
		}
		
	}
	
	public static String settlementAddedCoins(Double amount) {
		return Colour.positive + "Deposited " + EconomyMessages.coins(amount) + " to the settlements bank.";
	}
	
	public static String settlementRemovedCoins(Double amount) {
		return Colour.positive + "Withdrew " + EconomyMessages.coins(amount) + " from the settlements bank.";
	}
	
	public static String settlementNothingToWithdraw(){
		return Colour.negative + "Nothing to withdraw.";
	}
	
	public static String settlementNothingToDeposit(){
		return Colour.negative + "Nothing to deposit.";
	}
	
	
	
	// Pay:
	public static String paid(SagaPlayer paidPlayer, Double amount) {
		return Colour.positive + "Paid " + coins(amount) + "s to " + paidPlayer.getName() + ".";
	}
	
	public static String gotPaid(SagaPlayer payerPlayer, Double amount) {
		return Colour.positive + "Got paid " + coins(amount) + "s by " + payerPlayer.getName() + ".";
	}
	
	public static String tooFarPay(Double maximumDistance) {
		return Colour.negative + "Need to be within " + maximumDistance.intValue() + "blocks to pay.";
	}
	
	public static String tooFarPay() {
		return Colour.negative + "Too far to pay.";
	}
	
	public static String walletModified(SagaPlayer targetPlayer, Double amount) {
		
		if(amount >= 0) return Colour.positive + "Added " + EconomyMessages.coins(amount) + " to players " + targetPlayer.getName() + " wallet.";
		
		return Colour.positive + "Removed " + EconomyMessages.coins(amount) + " from players " + targetPlayer.getName() + " wallet.";
		
	}
	
	public static String walletModified(Double amount) {
	
		if(amount >= 0) return Colour.positive + "Added " + EconomyMessages.coins(amount) + " to wallet.";
		
		return Colour.positive + "Removed " + EconomyMessages.coins(amount) + " from wallet.";
		
	}
	
	
	
	// Sell/buy signs:
	public static String insufItems(Material items) {
		
		return Colour.negative + "Not enough " + GeneralMessages.material(items) + ".";
		
	}
	
	public static String insufCoins() {
		
		return Colour.negative + "Not enough " + coins() + ".";
		
	}
	
	public static String sold(Material item, Integer amount, Double price) {
		
		return Colour.positive + "Sold " + amount + " " + GeneralMessages.material(item) + " for " + coins(price * amount) + ".";
		
	}
	
	public static String bought(Material item, Integer amount, Double price) {
	
	return Colour.positive + "Bought " + amount + " " + GeneralMessages.material(item) + " for " + coins(price * amount) + ".";
	
}
	
	
	
	// Wages:
	public static String gotPaid(Faction faction, Double amount) {
		return faction.getColour2() + "Received " + coins(amount) + " in wages.";
	}
	
	public static String gotPaid(Settlement faction, Double amount) {
		return Colour.normal2 + "Received " + coins(amount) + " in wages.";
	}
	
	public static String gotKillReward(SagaPlayer attakerPlayer, SagaPlayer defenderPlayer, Faction faction, Double amount) {
		return faction.getColour2() + attakerPlayer.getName() + " received " + coins(amount) + " for killing " + defenderPlayer.getName() + ".";
	}
	
	
	
	// Trading post:
	public static String exported(ArrayList<SagaItem> exports, Double coins) {
		
		StringBuffer result = new StringBuffer();
		ChatColor primCol = Colour.normal2;
		ChatColor secCol = Colour.normal1;
		
		if(exports.size() == 0) return primCol + "Exported: -.";
		
		result.append(primCol + "Exported: ");
		
		for (int i = 0; i < exports.size(); i++) {
			
			SagaItem item = exports.get(i);
			
			// Duplicate:
			boolean duplic = false;
			if(i != 0 && exports.get(i-1).getType() == item.getType()) duplic = true; 
			if(i != exports.size() - 1 && exports.get(i+1).getType() == item.getType()) duplic = true; 

			if(i != 0) result.append(primCol + ", ");
			result.append(secCol);
			
			// Item:
			if(item.getAmount() > 1) result.append(item.getAmount().intValue() + " ");
			result.append(GeneralMessages.material(item.getType()));
			if(duplic) result.append(":" + item.getData());
			
		}
		
		result.append(primCol + " for " + EconomyMessages.coins(coins) + ".");
		
		return result.toString();
		
	}
	
	
	
	// Naming:
	public static String coins(Double amount) {
		return ChatUtil.displayDouble(amount) + "" + coins();
	}
	
	public static String coins(){
		return EconomyConfiguration.config().coinName;
	}

	

	// User:
	public static String wallet(SagaPlayer sagaPlayer) {
		return Colour.positive + "Wallet: " + EconomyMessages.coins(EconomyDependency.getCoins(sagaPlayer)) + ".";
	}
	
	
}
