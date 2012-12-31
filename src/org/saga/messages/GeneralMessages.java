package org.saga.messages;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.saga.buildings.Building;
import org.saga.factions.Faction;
import org.saga.messages.colours.Colour;
import org.saga.settlements.Bundle;
import org.saga.settlements.Settlement;
import org.saga.utility.chat.ChatUtil;

public class GeneralMessages {

	
	/**
	 * Represents space.
	 */
	public final static String SPACE_SYMBOL = "_";

	/**
	 * Represents tab in chat.
	 */
	public static String TAB = "   ";

	
	
	// Command arguments:
	public static String notNumber(String number) {
		return Colour.negative + number + " is not a number.";
	}
	
	public static String notOnline(String name) {
		return Colour.negative + "Player " + name + " isn't online.";
	}
	
	public static String invalidPlayer(String playerName){
		return Colour.negative + "Player " + playerName +" doesn't exist.";
	}

	public static String nameFromArg(String name) {
		return name.replaceAll(SPACE_SYMBOL, " ");
	}
	
	
	public static String invalidSettlement(String name){
		return Colour.negative + "Settlement " + name + " doesn't exist.";
	}
	
	public static String notSettlement(Bundle bundle){
		return Colour.negative + "Chunk bundle " + bundle.getName() + " isn't a settlement.";
	}
	
	public static String invalidFaction(String factionName) {
		return Colour.negative + "Faction " + factionName + " doesn't exist.";
	}
	

	
	// Material:
	public static String material(Material material){
		return material.toString().toLowerCase().replace("_", " ");
	}
	
	public static String materialAbrev(Material material){
		
		String result = material(material);
		
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
		else if(result.startsWith("cobblestone ")){
			result = result.replace("cobblestone ", "cb.");
		}
		
		return result;
		
	}
	
	
	
	// Text elements:
	public static String highlight(String command){
		
		return ChatColor.UNDERLINE + command + CustomColour.RESET_FORMAT;
		
	}
	
	public static String command(String command){
	
		return ChatColor.ITALIC + command + CustomColour.RESET_FORMAT;
	
	}
	
	public static String attrAbrev(String attribute) {

		return ChatUtil.firstChars(attribute, 2).toUpperCase();

	}
	
	public static String columnTitle(String title) {
		return title.toUpperCase();
	}
	
	public static String tableTitle(String title) {
		return title.toUpperCase();
	}

	public static String page(Integer page, Integer lastPage){
		
		return "page " + (page+1) + " of " + (lastPage+1);
		
	}

	
	
	// Permissions:
	public static String noPermission(){
		return Colour.negative + "You don't have permission to do that.";
	}
	
	public static String noPermission(Bundle bundle){
		return Colour.negative + "You don't have permission to do that (" + bundle.getName() + " settlement).";
	}
	
	public static String noPermission(Building building){
		return Colour.negative + "You don't have permission to do that (" + building.getName() + " building).";
	}
	
	public static String noPermission(Settlement settlement){
		return Colour.negative + "You don't have permission to do that (" + settlement.getName() + " settlement).";
	}

	public static String noPermission(Faction faction){
		return Colour.negative + "You don't have permission to do that (" + faction.getColour1() + faction.getName() + Colour.negative + " faction).";
	}

	public static String noCommandPermission(Bundle bundle, String command){
		return Colour.negative + "You don't have permission use " + command + " command (" + bundle.getName() + " settlement).";
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
