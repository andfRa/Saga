package org.saga.messages;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
	
}
