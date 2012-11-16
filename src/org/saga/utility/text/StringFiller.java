package org.saga.utility.text;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.saga.messages.GeneralMessages.CustomColour;

public class StringFiller {


	/**
	 * Default character length.
	 */
	private static Double defaultLength = 3.0 / 2.0;

	/**
	 * Gap fill string maximum size.
	 */
	private static Double maxGap = 1.25;
	
	
	/**
	 * Size map.
	 */
	private static HashMap<Character, Double> sizeMap = new HashMap<Character, Double>(){
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		{
			put('i', 0.5);
			put('k', 5.0 / 4.0);
			put('t', 1.0);
			put('f', 5.0 / 4.0);
			put('(', 5.0 / 4.0);
			put(')', 5.0 / 4.0);
			put('<', 5.0 / 4.0);
			put('>', 5.0 / 4.0);
			put('{', 5.0 / 4.0);
			put('}', 5.0 / 4.0);
			put(',', 1.0 / 2.0);
			put('.', 1.0 / 2.0);
			put('[', 1.0);
			put(']', 1.0);
			put('I', 1.0);
			put('|', 1.0 / 2.0);
			put('*', 5.0 / 4.0);
			put('"', 5.0 / 4.0);
			put('|', 0.5);
			put('!', 0.5);
			put(':', 0.5);
			put('l', 3.0 / 4.0);
			put(' ', 1.0);
			put('.', 1.0 / 2.0);
			put('\'', 3.0 / 4.0);
			put(' ', 1.0 / 1.0);
			put('\"', 5.0 / 4.0);
			put('`', 0.5);
			put('\u2500', 5.0/4.0);
			put('\u2502', 1.0/4.0);
			put('\u250C', 3.0/4.0);
			put('\u2510', 3.0/4.0);
			put('\u2514', 3.0/4.0);
			put('\u2518', 3.0/4.0);
			
		}
	};
	
	/**
	 * Gap fill chars.
	 */
	private static HashSet<Character> fillChars = new HashSet<Character>(){
		
		/**
		 * Serial version UID.
		 */
		private static final long serialVersionUID = 1L;
		
		{
			add('`');
			add('\'');
			add(' ');
			add('\"');
		}
		
	};

	
	
	/**
	 * Fills a string.
	 * 
	 * @param str string to fill
	 * @param reqLength required length
	 * @return string with the given length 
	 */
	public static String fillString(String str, Double reqLength) {

		
		char[] chars = str.toCharArray();
		
		StringBuffer result = new StringBuffer();
		Double length = 0.0;
		
		// Cut size:
		for (int i = 0; i < chars.length; i++) {
			
			Double charLength = sizeMap.get(chars[i]);
			if(charLength == null) charLength = defaultLength;
			
			if(length + charLength > reqLength) break;
			
			result.append(chars[i]);
			
			if(!(chars[i] == ChatColor.COLOR_CHAR || (i > 0 && chars[i-1] == ChatColor.COLOR_CHAR)))
			length += charLength;
			
		}
		
		// Add spaces:
		Character fillChar = ' ';
		Double fillLength = 1.0;
		while(true){

			Double gapLength = reqLength - length;
			
			// Gap filled:
			if(gapLength <= 0) break;
			
			// Add custom fillers: 
			if(gapLength <= maxGap){

				fillChar = findCustom(gapLength, reqLength);
				if(fillChar != null){
					result.append(fillChar);
					fillLength = sizeMap.get(fillChar);
				}
				
				break;
				
			}
			
			result.append(fillChar);
			length += fillLength;
			
		}
		
		return result.toString();
		
		
	}

	/**
	 * Finds a custom character with the best fit.
	 * 
	 * @param gapLen gap length
	 * @param reqLength required length
	 * @return char that best fits the gap, null if none
	 */
	private static Character findCustom(Double gapLen, Double reqLength) {
		

		Set<Character> gapStrs = new HashSet<Character>(fillChars);
		Double bestFitLen = -1.0;
		Character bestFitStr = null;
		
		for (Character gapStr : gapStrs) {
			
			Double gapStrLen = sizeMap.get(gapStr);
			
			if(gapLen - gapStrLen >= 0 && gapStrLen > bestFitLen){
				bestFitLen = gapStrLen;
				bestFitStr = gapStr;
			}
			
		}
		
		return bestFitStr;
		
		
	}
	
	/**
	 * Calculates the length of a string.
	 * 
	 * @param str string
	 * @return string length
	 */
	public static Double calcLength(String str) {

		
		char[] chars = str.toCharArray();
		
		Double length = 0.0;
		
		for (int i = 0; i < chars.length; i++) {
			
			Double charLength = sizeMap.get(chars[i]);
			if(charLength == null) charLength = defaultLength;
			
			if(!(chars[i] == ChatColor.COLOR_CHAR || (i > 0 && chars[i-1] == ChatColor.COLOR_CHAR)))
			length += charLength;
			
		}
		
		return length;
		
		
	}
	
	/**
	 * Adjusts filler characters.
	 * 
	 * @param str string
	 * @return adjusted string
	 */
	public static String adjustFillers(String str) {

		
		str = str.replace("\"", ChatColor.BOLD + " " + CustomColour.RESET_FORMAT);
		str = str.replace("\'", ChatColor.BOLD + "`" + CustomColour.RESET_FORMAT);
		
		return CustomColour.process(str);
		
		
	}
	
	
}
