package org.saga.utility.text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.saga.messages.colours.ColourLoop;
import org.saga.utility.ArrayUtil;

public class TextUtil {
	
	
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
	
	public static String senctence(String string) {

		if(!string.endsWith(".")) string+= ".";
		return capitalize(string);
		
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

	public static String colour(ChatColor color) {
		
		return color + color.name().toLowerCase().replace("_", " ");
		
	}

	public static String displayDouble(Double value) {

		DecimalFormat twoDec = new DecimalFormat("#.##");
		String shortString = (twoDec.format(value));
		
		return shortString;
		
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

	public static String histogram(Double[] data, ColourLoop colours) {

		
		StringBuffer result = new StringBuffer();
		
		Integer row = ArrayUtil.max(data).intValue();
		
		while (true) {
			
			colours.reset();
			
			for (int i = 0; i < data.length; i++) {
				
				if(i % 10 == 0) result.append(colours.nextColour());
				
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
	
	public static String round(Double doub, int decimals){

		
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(decimals);
		df.setMinimumFractionDigits(decimals);
		df.setGroupingUsed(false);
		
		return df.format(doub);
		
		
	}
	
}


