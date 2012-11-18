package org.saga.chunks;

import java.util.ArrayList;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Settlement;
import org.saga.utility.AsciiCompass;

public class SagaMap {
	
	
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
			
			put(Biome.RIVER, ChatColor.DARK_BLUE);
			
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
	public static ArrayList<String> getMap(SagaPlayer player, Location location) {
		

		ArrayList<String> map = new ArrayList<String>();
		
		int halfHeight = 12 / 2;
		int halfWidth = 44 / 2;
		
		double inDegrees = location.getYaw();
		
		Chunk locationChunk = location.getWorld().getChunkAt(location);
		
		int topLeftX = locationChunk.getX() - halfWidth;
		int topLeftZ = locationChunk.getZ() + halfHeight;
		
		int width = halfHeight * 2 + 1;
		int height = halfWidth * 2 + 1;
		
		ChatColor prevColor = null;
		ChatColor color = ChatColor.WHITE;
		
		// Row:
		for (int dz = -width + 1; dz <= 0; dz++) {
			prevColor = null;
			color = null;
			
			// Column:
			StringBuffer row = new StringBuffer();
			for (int dx = 0; dx < height; dx++) {
				
				SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(location.getWorld().getName(), topLeftX + dx, topLeftZ + dz);

				Bundle settlement = null;
				
				if (sagaChunk != null) {
					settlement = sagaChunk.getChunkBundle();
				}
				
				String symbol = "?";
				
				// Settlement:
				if (settlement != null && settlement instanceof Settlement) {
					
					// Building:
					if (sagaChunk.getBuilding() != null) {
						symbol = sagaChunk.getBuilding().getMapChar();
						color = ChatColor.DARK_PURPLE;
					}
					
					// Border:
					else if (sagaChunk.isBorder()) {
						symbol = "|||";
						color = ChatColor.GOLD;
					}
					
					// Nothing:
					else {
						symbol = "+";
						color = ChatColor.YELLOW;
					}
					
				}
				
				// Bundle:
				else if (settlement != null) {
					symbol = "D";
					color = ChatColor.DARK_GRAY;
				}
				
				// Not claimed:
				else if (sagaChunk == null) {

					symbol = "-";
					
					// Biome:
					Biome biome = location.getWorld().getBiome((topLeftX + dx)*16 + 8, (topLeftZ + dz)*16 + 8);
					color = BIOME_COLOURS.get(biome);
					
					if(color == null) color = ChatColor.GRAY;
					
				}

				// Player location:
				if (dx == halfWidth && dz == -halfHeight) {
					color = ChatColor.DARK_RED;
					symbol = "X";
				}
				
				// Only append new colours:
				if(prevColor != color){
					
					row.append(color);
					prevColor = color;
					
				}
				
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
		
		return map;
		
		
	}
	
}
