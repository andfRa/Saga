package org.saga.messages.colours;

import java.util.Stack;

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
	
	
	
	public enum CustomColour {
		
		
		PREVIOUS_COLOR('\u2193'),
		NORMAL_FORMAT('\u21D3');
		
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
			
			StringBuffer result = new StringBuffer(message.length());
			Stack<ChatColor> cols = new Stack<ChatColor>();
			
			for (int i = 0; i < message.length(); i++) {
				
				char c = message.charAt(i);
				
				// Last colour:
				if(c == ChatColor.COLOR_CHAR && i + 1 < message.length()){
					
					ChatColor nextCol = ChatColor.getByChar(message.charAt(i+1));
					if(nextCol != null && nextCol.isColor()) cols.push(nextCol);
					
				}
				
				// Previous colour:
				if(c == PREVIOUS_COLOR.ch){
					
					cols.pop();
					if(!cols.isEmpty()) result.append(cols.pop());
					continue;
					
				}
				
				// Previous format:
				if(c == NORMAL_FORMAT.ch){
					
					result.append(ChatColor.RESET);
					if(!cols.isEmpty()) result.append(cols.peek());
					continue;
					
				}
				
				// Text:
				result.append(c);
				
			}
			
			return result.toString();
			
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
				
				// Custom colours:
				if(c == PREVIOUS_COLOR.ch) continue;
				if(c == NORMAL_FORMAT.ch) continue;
				
				// Normal colours:
				if(c == ChatColor.COLOR_CHAR){
					i++;
					continue;
				}
				
				// Text:
				result.append(c);
				
			}
			
			return result.toString();
			
		}
		
		
		/**
		 * Gets the associated character.
		 * 
		 * @return character
		 */
		public char getChar() {
			return ch;
		}
		
		/**
		 * Creates custom colour in string format.
		 * 
		 * @return custom colour in string format
		 */
		@Override
		public String toString() {
			return new String(new char[]{ch});
		}
		
		
	}
	
	
}
