package org.saga.settlements;

import java.util.ArrayList;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.saga.player.SagaPlayer;
import org.saga.utility.AsciiCompass;

public class SagaMap {
	
	/**
	 * Building character. 'B' will be replaced with actual building charatcer.
	 */
	public static String BUILDING = "B";

	/**
	 * Wilderness character.
	 */
	public static String WILDERNESS = "-";

	/**
	 * Claimed character.
	 */
	public static String CLAIMED = "+";

	/**
	 * Border character.
	 */
	public static String BORDER = "|||";

	/**
	 * Border character.
	 */
	public static String YOUAREHERE = "X";
	
	
	
	/**
	 * Biome colours.
	 */
	public final static Hashtable<Biome, ChatColor> BIOME_COLOURS = new Hashtable<Biome, ChatColor>(){
		
		private static final long serialVersionUID = 1L;

		{
			put(Biome.BEACH, ChatColor.BLUE);
			
			put(Biome.DESERT, ChatColor.YELLOW);
			put(Biome.DESERT_HILLS, ChatColor.YELLOW);
			
			put(Biome.FOREST, ChatColor.DARK_GREEN);
			put(Biome.FOREST_HILLS, ChatColor.DARK_GREEN);
			
			put(Biome.FROZEN_OCEAN, ChatColor.DARK_BLUE);
			put(Biome.FROZEN_RIVER, ChatColor.DARK_BLUE);
			
			put(Biome.HELL, ChatColor.RED);
			
			put(Biome.ICE_MOUNTAINS, ChatColor.WHITE);
			put(Biome.ICE_PLAINS, ChatColor.WHITE);
			
			put(Biome.JUNGLE, ChatColor.GREEN);
			put(Biome.JUNGLE_HILLS, ChatColor.GREEN);
			
			put(Biome.MUSHROOM_ISLAND, ChatColor.BLACK);
			put(Biome.MUSHROOM_SHORE, ChatColor.BLACK);
			
			put(Biome.OCEAN, ChatColor.DARK_BLUE);
			
			put(Biome.PLAINS, ChatColor.GRAY);
			
			put(Biome.RIVER, ChatColor.BLUE);
			
			put(Biome.SKY, ChatColor.WHITE);
			
			put(Biome.SMALL_MOUNTAINS, ChatColor.GRAY);
			
			put(Biome.SWAMPLAND, ChatColor.DARK_GRAY);
			
			put(Biome.TAIGA, ChatColor.WHITE);
			put(Biome.TAIGA_HILLS, ChatColor.WHITE);
			
		};
	};
	
	/**
	 * Creates a chunk map.
	 */
	public static ArrayList<String> getMap(SagaPlayer sagaPlayer, Location location) {
		

		ArrayList<String> map = new ArrayList<String>();
		
		int halfHeight = 14 / 2;
		int halfWidth = 24 / 2;
		
		double inDegrees = location.getYaw();
		
		Chunk locationChunk = location.getWorld().getChunkAt(location);
		SagaChunk locationSagaChunk = sagaPlayer.getSagaChunk();

		
		int topLeftX = locationChunk.getX() - halfWidth;
		int topLeftZ = locationChunk.getZ() + halfHeight;
		
		int width = halfHeight * 2 + 1;
		int height = halfWidth * 2 + 1;
		
		ChatColor prevColor = null;
		ChatColor color = ChatColor.GRAY;
		
		// Row:
		for (int dz = -width + 1; dz <= 0; dz++) {
			prevColor = null;
			color = null;
			
			// Column:
			StringBuffer row = new StringBuffer();
			for (int dx = 0; dx < height; dx++) {
				
				SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(location.getWorld().getName(), topLeftX + dx, topLeftZ + dz);

				String symbol = "?";
				
				// Claimed:
				if (sagaChunk != null) {
					
					// Building:
					if (sagaChunk.getBuilding() != null) {
						symbol = BUILDING.replace("B", sagaChunk.getBuilding().getMapChar());
						color = ChatColor.DARK_PURPLE;
					}
					
					// Border:
					else if (sagaChunk.isBorder()) {
						symbol = BORDER;
						color = ChatColor.GOLD;
					}
					
					// Claimed:
					else {
						symbol = CLAIMED;
						color = ChatColor.YELLOW;
					}
					
				}
				
				// Not claimed:
				else {

					symbol = WILDERNESS;
					
					Biome biome = location.getWorld().getBiome((topLeftX + dx)*16 + 8, (topLeftZ + dz)*16 + 8);
					color = BIOME_COLOURS.get(biome);
					if(color == null) color = ChatColor.GRAY;
					
				}

				// Player location:
				if (dx == halfWidth && dz == -halfHeight) {
					color = ChatColor.DARK_RED;
					symbol = YOUAREHERE;
				}
				
				// Append new colour:
				if(prevColor != color){
					row.append(color);
					prevColor = color;
				}
				
				// Append symbol:
				row.append(symbol);
				
			}
			
			map.add(row.toString());
			
		}
		
		// Get the compass:
		ArrayList<String> asciiCompass = AsciiCompass.getAsciiCompass(inDegrees, ChatColor.RED, ChatColor.GOLD);
		
		// Add the compass:
		map.set(0, map.get(0) + " " + asciiCompass.get(0));
		map.set(1, map.get(1) + " " + asciiCompass.get(1));
		map.set(2, map.get(2) + " " + asciiCompass.get(2));
		
		// Add name:
		char[] locationName = null;
		
		if (locationSagaChunk != null) {
			locationName = locationSagaChunk.getChunkBundle().getName().toUpperCase().toCharArray();
		}else{
			locationName = "WILDERNESS".toCharArray();
		}
		
		for (int i = 0; i+4 < map.size() && i < locationName.length; i++) {
			map.set(i+4, map.get(i+4) + "   " + ChatColor.GOLD + locationName[i]);
		}
		
		return map;
		
		
	}

	/**
	 * Enables bonus characters.
	 * 
	 */
	public static void enableBonusCharacters(){
		
		BUILDING = "\u2502B\u2502";
		WILDERNESS = "\u2591";
		BORDER = "[]";
		CLAIMED = "| |";
		YOUAREHERE = "\u2502X\u2502";

	}
	
	
}
