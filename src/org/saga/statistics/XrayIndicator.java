package org.saga.statistics;

import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.saga.config.BalanceConfiguration;
import org.saga.player.SagaPlayer;



public class XrayIndicator {
	
	
	/**
	 * Xray materials.
	 */
	private static HashSet<Material> XRAY_MATERIALS = getXrayMaterials();
	
	/**
	 * Handles mining.
	 * 
	 * @param sagaPlayer player
	 * @param event event
	 */
	public static void handleMine(SagaPlayer sagaPlayer, BlockBreakEvent event) {

		
		if(event.isCancelled()) return;
		
		// Select blocks:
		Material material = event.getBlock().getType();
		
		if(!XRAY_MATERIALS.contains(material)) return;
		
		// Add blocks:
		Integer amount = sagaPlayer.addMinedBlocks(material, 1);
		
		// Flush:
		if(material.equals(Material.STONE) && amount >= BalanceConfiguration.config().xrayUpdateInterval){
			
			String name = sagaPlayer.getName();
			HashSet<Material> xrayMaterials = getXrayMaterials();
			
			for (Material xrayMat : xrayMaterials) {
				
				StatisticsManager.manager().onXrayStatisticsUpdate(name, xrayMat, sagaPlayer.getMinedBlocks(xrayMat));
				sagaPlayer.clearMinedBlocks(xrayMat);
				
			}
			
			
		}
		
		
	}
	
	/**
	 * Gets all xray materials.
	 * 
	 * @return xray materials
	 */
	public static HashSet<Material> getXrayMaterials(){
		
		
		HashSet<Material> materials = new HashSet<Material>();
		
		materials.add(Material.STONE);
		materials.add(Material.COAL_ORE);
		materials.add(Material.IRON_ORE);
		materials.add(Material.GOLD_ORE);
		materials.add(Material.LAPIS_ORE);
		materials.add(Material.DIAMOND_ORE);
		materials.add(Material.MOSSY_COBBLESTONE);
		
		return materials;
		
		
	}
	
	
}
