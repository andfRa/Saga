package org.saga.messages;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.saga.config.BalanceConfiguration;
import org.saga.economy.EconomyMessages;
import org.saga.player.SagaPlayer;
import org.saga.utility.TextUtil;

public class AdminMessages {

	
public static ChatColor veryPositive = ChatColor.DARK_GREEN;
	

	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED;
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor anouncment = ChatColor.AQUA;
	
	public static ChatColor normal1 = ChatColor.GOLD;
	
	public static ChatColor normal2 = ChatColor.YELLOW;


	// Levels:
	public static String levelSet(SagaPlayer sagaPlayer){
		return positive + "Player " + sagaPlayer.getName() + " level set to " + sagaPlayer.getLevel() + ".";
	}
	
	public static String levelSetTo(SagaPlayer sagaPlayer){
		return positive + "Level was set to " + sagaPlayer.getLevel() + ".";
	}

	public static String levelOutOfRange(String level){
		return negative + level + " is out of range. Allowed range: 0 - " + BalanceConfiguration.config().maximumLevel + ".";
	}

	
	// Skills:
	public static String skillSet(SagaPlayer sagaPlayer, String skill){
		return positive + "Player " +  sagaPlayer.getName() + " " + skill + " skill set to " + sagaPlayer.getSkillMultiplier(skill) + ".";
	}
	
	public static String skillSetTo(SagaPlayer sagaPlayer, String skill){
		return positive + TextUtil.capitalize(skill) + " skill was set to " + sagaPlayer.getSkillMultiplier(skill) + ".";
	}

	public static String skillOutOfRange(String skill){
		return negative + skill + " is out of range. Allowed range: 0 - " + BalanceConfiguration.config().maximumSkillLevel + ".";
	}
	
	public static String invalidSkill(String skill){
		return negative + "Skill " + skill + " is not valid.";
	}
	
	
	// Administrator mode:
	public static String adminMode(SagaPlayer sagaPlayer) {
		
		
		if(sagaPlayer.isAdminMode()){
			return positive + "Admin mode enabled.";
		}else{
			return positive + "Admin mode disabled.";
		}
		
		
	}
	
	public static String adminModeAlreadyEnabled() {
		
		return negative + "Admin mode already enabled.";
		
	}
	
	public static String adminModeAlreadyDisabled() {
		
		return negative + "Admin mode already disabled.";
		
	}
	

	// Additional info:
	public static String statsTargetName(SagaPlayer sagaPlayer) {
		return positive + "Stats for " + sagaPlayer.getName() + ".";
	}
	

	// Saving loading:
	public static String saving() {
		return veryPositive + "Saving Saga information.";
	}
	
	public static String saved() {
		return veryPositive + "Saving complete.";
	}
	

	// Reward:
	public static String rewarded(ArrayList<String> players) {

		return positive + "Rewarded " + players.size() + " players.";
		
	}
	
	public static String playersNotFound(ArrayList<String> players) {

		return negative + "Failed to find players: " + TextUtil.flatten(players) + ".";
		
	}
	
	public static String noReward() {

		return negative + "No reward available.";
		
	}
	
	public static String collectedReward(Double exp, Double coins) {

		return positive + "Collected " + exp.intValue() + " exp and " + EconomyMessages.coins(coins) + ".";
		
	}

	
}
