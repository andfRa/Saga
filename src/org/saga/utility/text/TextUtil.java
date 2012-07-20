package org.saga.utility.text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.saga.messages.PlayerMessages.ColorCircle;
import org.saga.utility.MathUtil;

public class TextUtil {
	
	
	/**
	 * Represents tab in chat.
	 */
	public static String TAB = "   ";
	
	/**
	 * Non-special character length.
	 */
	private static Double defaultCharLength = 3.0 / 2.0;

	/**
	 * Special characters.
	 */
	private static HashMap<Character, Double> specialChars = specialCharSizeMap();
	
	/**
	 * Gap fill strings.
	 */
	private static Hashtable<String, Double> gapStringLengths = gapChars();
	
	/**
	 * Gap fill string maximum size.
	 */
	private static Double maxGapString = 1.25;
	
	
	
	public static String titleize(String str) {
            /*String line = ChatColor.GOLD + repeat("_", 58);
            String center = ".[ " + ChatColor.YELLOW + str + ChatColor.GOLD + " ].";
            int pivot = line.length() / 2;
            int eatLeft = center.length() / 2;
            int eatRight = center.length() - eatLeft;
            return line.substring(0, pivot - eatLeft) + center + line.substring(pivot + eatRight);*/
            return titleize(str,ChatColor.GOLD,ChatColor.YELLOW);
	}

	public static String titleize(String str, ChatColor color1, ChatColor color2) {
		
		
		String line = color1 + repeat("_", 58);
		String center = "=[ " + color2 + str + color1 + " ]=";
		int pivot = line.length() / 2;
		int eatLeft = center.length() / 2;
		int eatRight = center.length() - eatLeft;
		return line.substring(0, pivot - eatLeft) + center + line.substring(pivot + eatRight);
		
		
	}
	
	
	public static String repeat(String s, int times) {
	    
		
		if (times <= 0) return "";
	    else return s + repeat(s, times-1);
		
		
	}

	
	public static String getMaterialName(Material material) {
		
		
		String ret = material.toString();
		ret = ret.replace('_', ' ');
		ret = ret.toLowerCase();
		return ret.substring(0, 1).toUpperCase()+ret.substring(1);
		
		
	}

	public  static ArrayList<String> substanceChars = new ArrayList<String>(Arrays.asList(new String []{
			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", 
			"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", 
			"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", 
			"s", "t", "u", "v", "w", "x", "y", "z", " "
			}));
			
	public static String getComparisonString(String str) {
		String ret = "";
		
		for (char c : str.toCharArray()) {
			if (substanceChars.contains(String.valueOf(c))) {
				ret += c;
			}
		}
		
		return ret.toLowerCase();
	}

	
	public static String capitalize(String string) {

		if(string.length()>=1){
			return string.substring(0, 1).toUpperCase() + string.substring(1);
		}else{
			return string.toUpperCase();
		}
		
	}

	public static String firstString(String string, int chars) {

		if(string.length()>=chars){
			return string.substring(0, chars);
		}else{
			return string;
		}
		
	}


	public static String fromSeconds(Integer secondsFull) {

		
		int hours = secondsFull / 3600,
		remainder = secondsFull % 3600,
		minutes = remainder / 60,
		seconds = (remainder % 60);

		String rString = "";
		
		if(hours == 0){
			
		}else if(hours < 10){
			rString += "0" + hours + "h";
		}else{
			rString += hours + "h";
		}
		
		if(rString.length() > 0) rString += " ";
		
		if(minutes == 0){
			
		}else if(minutes < 10){
			rString += "0" + minutes + "m";
		}else{
			rString += minutes + "m";
		}
		
		if(rString.length() > 0) rString += " ";
		
		if(seconds == 0){
			rString += "0" + "s";
		}else if(seconds < 10){
			rString += "0" + seconds + "s";
		}else{
			rString += seconds + "s";
		}
		
		return rString;


	}

	public static String color(ChatColor color, ChatColor messageColor) {
		
		return color + color.name().toLowerCase().replace("_", " ") + messageColor;
		
	}

	public static String displayDouble(Double value) {

		DecimalFormat twoDec = new DecimalFormat("#.##");
		String shortString = (twoDec.format(value));
		
		return shortString;
		
	}
	
	public static String normalizeString(String str, Double requiredLength) {

		char[] chars = str.toCharArray();
		
		StringBuffer result = new StringBuffer();
		Double length = 0.0;
		
		// Cut size:
		for (int i = 0; i < chars.length; i++) {
			
			Double charLength = specialChars.get(chars[i]);
			if(charLength == null) charLength = defaultCharLength;
			
			if(length + charLength > requiredLength) break;
			
			result.append(chars[i]);
			
			if(!(chars[i] == ChatColor.COLOR_CHAR || (i > 0 && chars[i-1] == ChatColor.COLOR_CHAR)))
			length += charLength;
			
			
		}
		
		// Add spaces:
		char fillerChar = ' ';
		Double fillerLength = 1.0;
		while(true){

			Double gapLength = requiredLength - length;
			
			// Gap filled:
			if(gapLength <= 0){
				break;
			}
			
			// Filler char too big: 
			if(gapLength <= maxGapString){

				// Find a best fit:
				Enumeration<String> gapStrings = gapStringLengths.keys();
				Double bestFitLength = -1.0;
				String bestFitString = null;
				
				while (gapStrings.hasMoreElements()) {
					
					String gapString = (String) gapStrings.nextElement();
					Double gapStringLength = gapStringLengths.get(gapString);
					
					if(gapLength - gapStringLength >= 0 && gapStringLength > bestFitLength){
						bestFitLength = gapStringLength;
						bestFitString = gapString;
					}
					
				}
				
				if(bestFitString != null){
					result.append(bestFitString);
				}
				
				break;
				
			}
			
			result.append(fillerChar);
			length += fillerLength;
			
			
		}
		
		return result.toString();
		
		
	}

	public static String frame(String title, ChatColor color, String message, double width) {

		
		StringBuffer result = new StringBuffer();
		
		if(message.length() == 0){
			result.append(color);
			result.append(".-------.");
			result.append("| empty |");
			result.append("'-------'");
			return result.toString();
		}
		
		// Upper bound:
		String upperBound = "=[ " + title.toUpperCase() + " ]=";
		boolean left = true;
		while(chatLength(upperBound + "_") <= width){
			
			if(left){
				upperBound = "_" + upperBound;
			}else{
				upperBound = upperBound + "_";
			}
			left = !left;
			
		}
		
		// Lower bound:
		String lowerBound = "";
		while(chatLength(lowerBound + "_") <= width){
			
			lowerBound += "-";
			
		}
		
		// Put together:
		result.append(color);
		result.append(upperBound);
		result.append("\n");
		result.append(message);
		result.append("\n");
		result.append(color);
		result.append(lowerBound);
		
		return result.toString();
		
		
	}

	public static String frame(String title, String message, ChatColor color) {

		return frame(title, color, message, 70);
		
	}
	
	public static Double chatLength(String str) {

		
		char[] chars = str.toCharArray();
		
		Double length = 0.0;
		
		for (int i = 0; i < chars.length; i++) {
			
			Double charLength = specialChars.get(chars[i]);
			if(charLength == null) charLength = defaultCharLength;
			
			if(!(chars[i] == ChatColor.COLOR_CHAR || (i > 0 && chars[i-1] == ChatColor.COLOR_CHAR)))
			length += charLength;
			
		}
		
		return length;
		
		
	}
	
	private static HashMap<Character, Double> specialCharSizeMap() {

		HashMap<Character, Double> sizeMap = new HashMap<Character, Double>();
		
		sizeMap.put('i', 0.5);
		sizeMap.put('k', 5.0 / 4.0);
		sizeMap.put('t', 1.0);
		sizeMap.put('f', 5.0 / 4.0);
		sizeMap.put('(', 5.0 / 4.0);
		sizeMap.put(')', 5.0 / 4.0);
		sizeMap.put('<', 5.0 / 4.0);
		sizeMap.put('>', 5.0 / 4.0);
		sizeMap.put('{', 5.0 / 4.0);
		sizeMap.put('}', 5.0 / 4.0);
		sizeMap.put(',', 1.0 / 2.0);
		sizeMap.put('.', 1.0 / 2.0);
		sizeMap.put('[', 1.0);
		sizeMap.put(']', 1.0);
		sizeMap.put('I', 1.0);
		sizeMap.put('|', 1.0 / 2.0);
		sizeMap.put('*', 5.0 / 4.0);
		sizeMap.put('"', 5.0 / 4.0);
		sizeMap.put('|', 0.5);
		sizeMap.put('!', 0.5);
		sizeMap.put(':', 0.5);
		sizeMap.put('l', 3.0 / 4.0);
		sizeMap.put(' ', 1.0);
		
		return sizeMap;
		
	}
	
	private static Hashtable<String, Double> gapChars() {

		Hashtable<String, Double> sizeTable = new Hashtable<String, Double>();
		
		sizeTable.put(".", 1.0 / 2.0);
		sizeTable.put("\'", 3.0 / 4.0);
		sizeTable.put(" ", 1.0 / 1.0);
		sizeTable.put("\"", 5.0 / 4.0);
		
		
		return sizeTable;
		
	}
	
	public static String flatten(Collection<String> array) {

		
		StringBuffer result = new StringBuffer();
		
		boolean first = true;
		
		for (String string : array) {
			
			if(first){
				first = false;
			}else{
				result.append(", ");
			}
			
			result.append(string);
			
		}
		
		return result.toString();
		
		
	}
	
	public static void messageLines2(String message, Player player) {


//		String[] arrayMessage = Pattern.compile("\n").split(message);
//		
//		// Add lost colors after line brake:
//		int lastColorIndex = -1;
//		String colorString= "\u00A7";
//		String color = "";
//		
//		for (int i = 1; i < arrayMessage.length; i++) {
//			
//			lastColorIndex = arrayMessage[i-1].lastIndexOf(colorString);
//			
//			if(lastColorIndex != -1 && (arrayMessage[i-1].length() - lastColorIndex - colorString.length()) >= 1 ){
//				color = arrayMessage[i-1].substring(lastColorIndex, lastColorIndex + colorString.length()+1);
//			}
//			if(color.length() != 0){
//				arrayMessage[i] = color + arrayMessage[i];
//			}
//			
//		}
//		
//		for (int i = 0; i < arrayMessage.length; i++) {
//			player.sendMessage(arrayMessage[i]);
//		}
		
		player.sendMessage(message);
		
	}
	
	public static String histogram(Double[] data, ColorCircle colours) {

		
		StringBuffer result = new StringBuffer();
		
		Integer row = MathUtil.max(data).intValue();
		
		while (true) {
			
			colours.reset();
			
			for (int i = 0; i < data.length; i++) {
				
				if(i % 10 == 0) result.append(colours.nextColor());
				
				if(data[i] >= row){
					result.append("||");
				}else{
					result.append(" ");
				}
				
			}
			
			row --;
			
			if(row <= 0){
				break;
			}else{
				result.append("\n");
			}
			
		}
		
		return result.toString();
		
		
	}
	
	public static String className(Class<?> clazz) {
		return clazz.getSimpleName().replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2").toLowerCase();
	}
	
}


