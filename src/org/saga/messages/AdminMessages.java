package org.saga.messages;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.saga.Clock.DaytimeTicker.Daytime;
import org.saga.config.AttributeConfiguration;
import org.saga.config.GeneralConfiguration;
import org.saga.config.ExperienceConfiguration;
import org.saga.player.GuardianRune;
import org.saga.player.SagaPlayer;
import org.saga.utility.text.TextUtil;

public class AdminMessages {

	
public static ChatColor veryPositive = ChatColor.DARK_GREEN;
	

	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED;
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor announce = ChatColor.AQUA;
	
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
		return positive + "Players " + sagaPlayer.getName() + " " + attribute + " was set to " + score + ".";
	}
	
	public static String attributeInvalid(String attribute, SagaPlayer sagaPlayer){
		return negative + TextUtil.capitalize(attribute) + " isn't a valid attribute.";
	}
	
	public static String attributeOutOfRange(String score){
		return negative + "Ability score " + score + " is out of range. Allowed range: 0 - " + AttributeConfiguration.config().maxAttributeScore + ".";
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
		
		return positive + "Recharged guardian rune.";
		
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

		ChatColor nameColor = GeneralConfiguration.config().adminChatNameColor;
		ChatColor messageColor = GeneralConfiguration.config().adminChatMessageColor;
		String namedMessage = messageColor + "{" + nameColor + name + messageColor + "} " + message;
		
		return namedMessage;
		
	}
	
	public static void chatWarning(String message) {

		chatMessage("WARNING", message);
		
	}

	
	
	// Time:
	public static String nextDaytime(World world, Daytime daytime) {

		return positive + "Daytime set to " + daytime + " for world " + world.getName() + ".";
		
	}

	
}
