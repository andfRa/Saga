package org.saga.messages;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.saga.buildings.Building;
import org.saga.factions.Faction;
import org.saga.settlements.Bundle;
import org.saga.settlements.Settlement;
import org.saga.utility.text.TextUtil;

public class GeneralMessages {

	
	public static ChatColor veryPositive = ChatColor.DARK_GREEN;
	
	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED;
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor announce = ChatColor.AQUA;
	
	public static ChatColor normal1 = ChatColor.GOLD;
	
	public static ChatColor normal2 = ChatColor.YELLOW;
	
	
	/**
	 * Represents space.
	 */
	public final static String SPACE_SYMBOL = "_";

	/**
	 * Represents tab in chat.
	 */
	public static String TAB = "   ";

	
	
	// Text elements:
	public static String highlight(String command){
		
		return ChatColor.UNDERLINE + command + CustomColour.RESET_FORMAT;
		
	}
	
	public static String command(String command){
	
		return ChatColor.ITALIC + command + CustomColour.RESET_FORMAT;
	
	}
	
	public static String attrAbrev(String attribute) {

		return TextUtil.firstString(attribute, 2).toUpperCase();

	}
	
	public static String columnTitle(String title) {
		return title.toUpperCase();
	}
	
	public static String tableTitle(String title) {
		return title.toUpperCase();
	}

	
	
	// Permissions:
	public static String noPermission(){
		return negative + "You don't have permission to do that.";
	}
	
	public static String noPermission(Bundle bundle){
		return negative + "You don't have permission to do that (" + bundle.getName() + " settlement).";
	}
	
	public static String noPermission(Building building){
		return negative + "You don't have permission to do that (" + building.getName() + " building).";
	}
	
	public static String noPermission(Settlement settlement){
		return negative + "You don't have permission to do that (" + settlement.getName() + " settlement).";
	}
	
	public static String noCommandPermission(Bundle bundle, String command){
		return negative + "You don't have permission use " + command + " command (" + bundle.getName() + " settlement).";
	}
	
	public static String noPermission(Faction faction){
		return negative + "You don't have permission to do that (" + faction.getColour1() + faction.getName() + negative + " faction).";
	}
	
	
	
	// Command arguments:
	public static String nameFromArg(String name) {
		return name.replaceAll(SPACE_SYMBOL, " ");
	}
	
	public static String mustBeNumber(String number) {
		return negative + "Argument " + number + " must be a number.";
	}
	
	
	
	// Material:
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

	
	
	// Other:
	public enum CustomColour{
		
		RESET_COLOR('x'),
		RESET_FORMAT('x');
		
		private char ch;
		
		private CustomColour(char ch) {
			this.ch = ch;
		}
		
		@Override
		public String toString() {
			
			if(this == RESET_FORMAT) return ChatColor.RESET.toString() + RESET_COLOR.toString();
			
			return new String(new char[]{ChatColor.COLOR_CHAR, ch});
			
		}
		
		public static String process(String message){
			
			ChatColor color = null;
			
			// Find default colour:
			char[] chmessage = message.toCharArray();
			for (int i = 1; i < chmessage.length; i++) {
				
				if(chmessage[i - 1] == ChatColor.COLOR_CHAR){
					color = ChatColor.getByChar(chmessage[i]);
					
					if(color != null && color.isColor()) break;
					else color = null;
					
				}
				
			}
			
			// Reset colours:
			if(color != null){
				message = message.replace(RESET_COLOR.toString(), color.toString());
			}else{
				message = message.replace(RESET_COLOR.toString(), "");
			}
			
			return message;
			
		}
		
		
	}

	
}
