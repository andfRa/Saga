package org.saga.messages;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.saga.buildings.Building;
import org.saga.chunks.ChunkBundle;
import org.saga.chunks.SagaMap;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Settlement;
import org.saga.utility.text.TextUtil;


public class SagaMessages {

	
	// Colors:
	public static ChatColor veryPositive = ChatColor.DARK_GREEN; // DO NOT OVERUSE.
	
	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED; // DO NOT OVERUSE.
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor anouncment = ChatColor.AQUA;
	
	public static ChatColor normal1 = ChatColor.GOLD;
	
	public static ChatColor normal2 = ChatColor.YELLOW;
	
	public static ChatColor frame = normal1;
	
	public static ChatColor frameTitle = normal2;
	
	
	// Strings:
	public static String spaceSymbol = "_";
	
	
	// Player retrieval:
	public static String invalidPlayer(String playerName){
		return negative + "Player " + playerName +" doesn't exist.";
	}

	public static String notOnline(String name) {
		return negative + "Player " + name + " isn't online.";
	}
	

	
	// Permission:
	public static String noPermission(){
		return negative + "You don't have permission to do that.";
	}
	
	public static String noPermission(ChunkBundle chunkBundle){
		return negative + "You don't have permission to do that (" + chunkBundle.getName() + " settlement).";
	}
	
	public static String noPermission(Building building){
		return negative + "You don't have permission to do that (" + building.getName() + " building).";
	}
	
	public static String noPermission(Settlement settlement){
		return negative + "You don't have permission to do that (" + settlement.getName() + " settlement).";
	}
	
	public static String noCommandPermission(ChunkBundle chunkBundle, String command){
		return negative + "You don't have permission use " + command + " command (" + chunkBundle.getName() + " settlement).";
	}
	
	
	
	// Map:
	public static String map(SagaPlayer sagaPlayer, Location location){
		
		
		ArrayList<String> map = SagaMap.getMap(sagaPlayer, sagaPlayer.getLocation());
		StringBuffer result = new StringBuffer();
		
		// Add borders:
		result.append(" ");
		map.add(0, " ");
		for (int i = 0; i < map.size(); i++) {
			
			if(i != 0) result.append("\n");
			
			result.append("  " + map.get(i) + "  ");
			
		}
		result.append(" ");
		
		Chunk locationChunk = location.getWorld().getChunkAt(location);
		String title = locationChunk.getWorld().getName() + " map " + "(" + locationChunk.getX() + ", " + locationChunk.getZ() + ")";
		
		return TextUtil.frame(title, result.toString(), ChatColor.GOLD);
		
		
	}
	

	
	
	// Meter:
	public static String meter(String name, Integer value, Integer maxValue, String unit, ChatColor messageColor, ChatColor barColor, ChatColor enclosingColor) {

		
		// Tnx Heroes RPG for the good idea.
		
		Integer barLength = 20;
		StringBuffer rString = new StringBuffer();
		
		// Normalize:
		if(value > maxValue){
			value = maxValue;
		}
		
		Integer normValue = new Double(barLength.doubleValue() * value/maxValue).intValue();

		// Add bar name:
		rString.append(messageColor);
		rString.append(TextUtil.capitalize(name));
		rString.append(": ");
		
		// Add enclosing element:
		rString.append(enclosingColor);
		rString.append("{]");
		
		// Add bar:
		rString.append(barColor);
		rString.append(TextUtil.repeat("||", normValue));
		rString.append(TextUtil.repeat(" ", barLength - normValue));
		
		// Add enclosing element:
		rString.append(enclosingColor);
		rString.append("[}");
		
		// Add value:
		rString.append(messageColor);
		rString.append(" - " + value + "" + unit);
		
		return rString.toString();
		
		
	}
	
	public static String meterCooldown(String cooldownName, int value, int maxValue, ChatColor messageColor) {
		return meter(cooldownName + " cooldown", value, maxValue, "s", messageColor, ChatColor.RED, ChatColor.DARK_GRAY);
	}
	
	
	
}
