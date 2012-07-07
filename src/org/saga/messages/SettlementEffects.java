package org.saga.messages;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.saga.buildings.Building;
import org.saga.chunkGroups.SagaChunk;
import org.saga.player.SagaPlayer;

public class SettlementEffects {

	
	public static void playClaim(SagaPlayer sagaPlayer, SagaChunk sagaChunk) {

		
		int xmin = sagaChunk.getX() * 16;
		int zmin = sagaChunk.getZ() * 16;
		
		int xmax = xmin + 15;
		int zmax = zmin + 15;
		
		World world = sagaPlayer.getLocation().getWorld();
		
		for (int x = xmin; x <= xmax; x++) {
			
			for (int z = zmin; z <= zmax; z++) {
				
				Location location = new Location(world, x + 0.5, world.getHighestBlockYAt(x, z) + 0.5, z + 0.5);
				
				if(x != xmin && x != xmax && z != zmin && z != zmax) continue;
				
				sagaPlayer.playGlobalEffect(Effect.MOBSPAWNER_FLAMES, 0, location);
				
			}
			
		}
		
		
	}
	
	public static void playAbandon(SagaPlayer sagaPlayer, SagaChunk sagaChunk) {

		
		int xmin = sagaChunk.getX() * 16;
		int zmin = sagaChunk.getZ() * 16;
		
		int xmax = xmin + 15;
		int zmax = zmin + 15;
		
		World world = sagaPlayer.getLocation().getWorld();
		
		for (int x = xmin; x <= xmax; x++) {
			
			for (int z = zmin; z <= zmax; z++) {
				
				Location location = new Location(world, x + 0.5, world.getHighestBlockYAt(x, z) + 0.5, z + 0.5);
				
				if(x != xmin && x != xmax && z != zmin && z != zmax && (xmax - x) != (zmax - z)) continue;
				
				sagaPlayer.playGlobalEffect(Effect.MOBSPAWNER_FLAMES, 0, location);
				
			}
			
		}
		
		
	}
	
	public static void playBuildingSet(SagaPlayer sagaPlayer, Building building) {

		
		SagaChunk sagaChunk = building.getSagaChunk();
		
		int xmin = sagaChunk.getX() * 16;
		int zmin = sagaChunk.getZ() * 16;
		
		int xmax = xmin + 15;
		int zmax = zmin + 15;
		
		int shift = 4;
		
		World world = sagaPlayer.getLocation().getWorld();
		
		for (int x = xmin; x <= xmax; x++) {
			
			for (int z = zmin; z <= zmax; z++) {
				
				Location location = new Location(world, x + 0.5, world.getHighestBlockYAt(x, z) + 0.5, z + 0.5);
				
				if(x != xmin && x != xmax && z != zmin && z != zmax) continue;
				
				sagaPlayer.playGlobalEffect(Effect.MOBSPAWNER_FLAMES, 0, location);
				
			}
			
		}
		
		for (int x = xmin + shift; x <= xmax - shift; x++) {
			
			for (int z = zmin + shift; z <= zmax - shift; z++) {
				
				Location location = new Location(world, x + 0.5, world.getHighestBlockYAt(x, z) + 0.5, z + 0.5);
				
				if(x != xmin + shift && x != xmax - shift && z != zmin + shift && z != zmax - shift) continue;
				
				sagaPlayer.playGlobalEffect(Effect.MOBSPAWNER_FLAMES, 0, location);
				
			}
			
		}
		
		
	}
	
	public static void playBuildingRemove(SagaPlayer sagaPlayer, Building building) {

		
		SagaChunk sagaChunk = building.getSagaChunk();
		
		int xmin = sagaChunk.getX() * 16;
		int zmin = sagaChunk.getZ() * 16;
		
		int xmax = xmin + 15;
		int zmax = zmin + 15;
		
		int shift = 4;
		
		World world = sagaPlayer.getLocation().getWorld();
		
		for (int x = xmin; x <= xmax; x++) {
			
			for (int z = zmin; z <= zmax; z++) {
				
				Location location = new Location(world, x + 0.5, world.getHighestBlockYAt(x, z) + 0.5, z + 0.5);
				
				if(x != xmin && x != xmax && z != zmin && z != zmax && (xmax - x) != (zmax - z)) continue;
				
				sagaPlayer.playGlobalEffect(Effect.MOBSPAWNER_FLAMES, 0, location);
				
			}
			
		}
		
		for (int x = xmin + shift; x <= xmax - shift; x++) {
			
			for (int z = zmin + shift; z <= zmax - shift; z++) {
				
				Location location = new Location(world, x + 0.5, world.getHighestBlockYAt(x, z) + 0.5, z + 0.5);
				
				if(x != xmin + shift && x != xmax - shift && z != zmin + shift && z != zmax - shift) continue;
				
				sagaPlayer.playGlobalEffect(Effect.MOBSPAWNER_FLAMES, 0, location);
				
			}
			
		}
		
		
	}
	
	
}
