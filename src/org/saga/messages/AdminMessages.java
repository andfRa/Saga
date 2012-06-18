package org.saga.messages;

import java.util.ArrayList;
import java.util.Enumeration;

import org.bukkit.ChatColor;
import org.saga.Saga;
import org.saga.config.AttributeConfiguration;
import org.saga.config.BalanceConfiguration;
import org.saga.config.ExperienceConfiguration;
import org.saga.player.GuardianRune;
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
	public static String levelSet(Integer level, SagaPlayer sagaPlayer){
		return positive + "Player " + sagaPlayer.getName() + " level set to " + level + ".";
	}
	
	public static String levelSet(Integer level){
		return positive + "Level was set to " + level + ".";
	}

	public static String levelOutOfRange(String level){
		return negative + level + " is out of range. Allowed range: 0 - " + ExperienceConfiguration.config().maximumLevel + ".";
	}

	
	
	
	// Attributes:
	public static String attributeSet(String attribute, Integer score){
		return positive + TextUtil.capitalize(attribute) + " was set to " + score + ".";
	}
	
	public static String attributeSet(String attribute, Integer score, SagaPlayer sagaPlayer){
		return positive + "Player " + sagaPlayer.getName() + " " + attribute + " was set to " + score + ".";
	}
	
	public static String attributeInvalid(String attribute, SagaPlayer sagaPlayer){
		return negative + TextUtil.capitalize(attribute) + " isn't a valid attribute.";
	}
	
	public static String attributeOutOfRange(String score){
		return negative + score + " is out of range. Allowed range: 0 - " + AttributeConfiguration.config().maxAttributeScore + ".";
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
	
	
	
	
	// Guardian rune:
	public static String recharged(GuardianRune rune, SagaPlayer sagaPlayer) {
		
		return positive + "Recharged players " + sagaPlayer.getName() + " guardian rune recharged.";
		
	}
	
	public static String recharged(GuardianRune rune) {
		
		return positive + "Guardian rune recharged.";
		
	}
	

	// Saving loading:
	public static String saving() {
		return veryPositive + "Saving Saga information.";
	}
	
	public static String saved() {
		return veryPositive + "Saving complete.";
	}
	
	
	
	
	// Messages:
	public static String chatMessage(String name, String message) {

		ChatColor nameColor = BalanceConfiguration.config().adminChatNameColor;
		ChatColor messageColor = BalanceConfiguration.config().adminChatMessageColor;
		String namedMessage = messageColor + "{" + nameColor + name + messageColor + "} " + message;
		
		return namedMessage;
		
	}
	
	public static void chatnWarning(String message) {

		chatMessage("WARNING", message);
		
	}


	
}
