package org.saga.chunkGroups;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Settlement;
import org.saga.utility.AsciiCompass;

public class SagaMap {
	
	
	/**
	 * The map is relative to a coord and a faction north is in the direction of
	 * decreasing x east is in the direction of decreasing z
	 */
	public static ArrayList<String> getMap(SagaPlayer player, Location location) {
		
		ArrayList<String> map = new ArrayList<String>();
		
		int halfWidth = 38 / 2;
		int halfHeight = 12 / 2;
		
		double inDegrees = location.getYaw();
		
		Chunk locationChunk = location.getWorld().getChunkAt(location);
		
		int topLeftX = locationChunk.getX() - halfHeight;
		int topLeftZ = locationChunk.getZ() + halfWidth;
		
		int width = halfWidth * 2 + 1;
		int height = halfHeight * 2 + 1;
		
		ChatColor prevColor = null;
		ChatColor color = ChatColor.WHITE;
		
		// For each row
		for (int dx = 0; dx < height; dx++) {
			
			prevColor = null;
			color = null;
			
			// Draw and add row:
			StringBuffer row = new StringBuffer();
			for (int dz = 0; dz > -width; dz--) {
				
				SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(location.getWorld().getName(), topLeftX + dx, topLeftZ + dz);

				ChunkGroup settlement = null;
				
				if (sagaChunk != null) {
					settlement = sagaChunk.getChunkGroup();
				}
				
				String symbol = "?";
				
				// Settlement:
				if (settlement != null && settlement instanceof Settlement) {
					
					// Building:
					if (sagaChunk.getBuilding() != null) {
						symbol = sagaChunk.getBuilding().getMapLetter();
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
				
				// Chunk group:
				else if (settlement != null) {
					symbol = "D";
					color = ChatColor.DARK_GRAY;
				}
				
				// Not claimed:
				else if (sagaChunk == null) {
					
					symbol = "-";
					color = ChatColor.WHITE;
					
				}

				// Player location:
				if (dx == halfHeight && dz == -halfWidth) {
					color = ChatColor.DARK_RED;
					symbol = "X";
				}
				
				// Only append new colors:
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
		map.set(1, asciiCompass.get(0) + map.get(1).substring(0, 2) + map.get(1).substring(5));
		map.set(2, asciiCompass.get(1) + map.get(2).substring(0, 2) + map.get(2).substring(5));
		map.set(3, asciiCompass.get(2) + map.get(3).substring(0, 2) + map.get(3).substring(5));
		
		return map;
		
		
	}
	
}
