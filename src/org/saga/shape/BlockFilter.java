package org.saga.shape;


import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.block.Block;



public class BlockFilter implements ShapeFilter{

	
	/**
	 * Materials.
	 */
	private HashSet<Material> materials = new HashSet<Material>();

	
	// Initialization:
	/**
	 * Initializes.
	 * 
	 */
	public BlockFilter() {
	}
	
	/**
	 * Adds a material.
	 * 
	 * @param material material
	 */
	public void addMaterial(Material material) {

		materials.add(material);
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.shape.ShapeFilter#checkBlock(org.bukkit.block.Block)
	 */
	@Override
	public boolean checkBlock(Block block) {
		
		
		return materials.contains(block.getType());

		
	}
	
	
}
