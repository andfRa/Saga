package org.saga.messages.colours;

import org.bukkit.ChatColor;

public class Colour {
	

	public static ChatColor veryPositive = ChatColor.DARK_GREEN; // DO NOT OVERUSE.

	public static ChatColor positive = ChatColor.GREEN;

	public static ChatColor negative = ChatColor.RED;

	public static ChatColor veryNegative = ChatColor.DARK_RED; // DO NOT OVERUSE.

	public static ChatColor unavailable = ChatColor.DARK_GRAY;

	public static ChatColor announce = ChatColor.AQUA;

	public static ChatColor normal1 = ChatColor.GOLD;

	public static ChatColor normal2 = ChatColor.YELLOW;

	public static ChatColor frame = ChatColor.GOLD;
	
	
	
	public enum CustomColour{
		
		
		PREVIOUS_COLOR('x'),
		PREVIOUS_FORMAT('x');
		
		private char ch;
		
		private CustomColour(char ch) {
			this.ch = ch;
		}
		
		
		/**
		 * Processes the message.
		 * 
		 * @param message message
		 * @return processed message
		 */
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
				message = message.replace(PREVIOUS_COLOR.toString(), color.toString());
			}else{
				message = message.replace(PREVIOUS_COLOR.toString(), "");
			}
			
			return message;
			
		}
		
		/**
		 * Strips all colour.
		 * 
		 * @param message
		 * @return
		 */
		public static String strip(String message){
			
			StringBuffer result = new StringBuffer();
			
			for (int i = 0; i < message.length(); i++) {
				
				char c = message.charAt(i);
				
				if(c == ChatColor.COLOR_CHAR){
					i++;
				}else{
					result.append(c);
				}
				
			}
			
			return result.toString();
			
		}
		
		
		/**
		 * Creates custom colour in string format.
		 * 
		 * @return custom colour in string format
		 */
		@Override
		public String toString() {
			
			if(this == PREVIOUS_FORMAT) return ChatColor.RESET.toString() + PREVIOUS_COLOR.toString();
			
			return new String(new char[]{ChatColor.COLOR_CHAR, ch});
			
		}
		
		
	}
	
	
}
