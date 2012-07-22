package org.saga.messages;

import org.bukkit.ChatColor;
import org.saga.abilities.Ability;
import org.saga.utility.text.RomanNumeral;
import org.saga.utility.text.TextUtil;

public class GeneralMessages {

	
	public static ChatColor veryPositive = ChatColor.DARK_GREEN;
	
	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED;
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor anouncment = ChatColor.AQUA;
	
	public static ChatColor normal1 = ChatColor.GOLD;
	
	public static ChatColor normal2 = ChatColor.YELLOW;
	
	
	
	
	public static String highlight(String command){
		
		return ChatColor.UNDERLINE + command + CustomColour.RESET_FORMAT;
		
	}
	
	public static String command(String command){
	
		return ChatColor.ITALIC + command + CustomColour.RESET_FORMAT;
	
	}
	
	public static String attrAbrev(String attribute) {

		return TextUtil.firstString(attribute, 2).toUpperCase();

	}
	
	public static String scoreAbility(Ability ability) {

		if(ability.getScore() <= 0) return ability.getName();
		
		return scoreAbility(ability.getName(), ability.getScore());

	}
	
	public static String scoreAbility(String ability, Integer score) {

		if(score <= 0) return ability;
		
		return ability + " " + RomanNumeral.binaryToRoman(score);

	}

	public static String columnTitle(String title) {
		return title.toUpperCase();
	}
	
	public static String tableTitle(String title) {
		return title.toUpperCase();
	}

	public static String coinsSpent(Double amount) {
		return normal2 + "Spent " + EconomyMessages.coins(amount) + ".";
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
		
		public static String processMessage(String message){
			
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
	
	public static void main(String[] args) {

		System.out.println(CustomColour.processMessage("dfd<" + ChatColor.ITALIC + ChatColor.RED + ">dfds<" + ChatColor.GRAY + ">dfddd<" + CustomColour.RESET_FORMAT + ">fdsfd"));;
		
		System.out.println(CustomColour.RESET_FORMAT);
		
	}
	
	
	
}
