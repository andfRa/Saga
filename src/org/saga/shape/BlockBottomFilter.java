package org.saga.shape;


import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;



public class BlockBottomFilter extends BlockFilter{

	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.shape.BlockFilter#checkBlock(org.bukkit.block.Block)
	 */
	@Override
	public boolean checkBlock(Block block) {
		
		
		if(super.checkBlock(block.getRelative(BlockFace.DOWN))) return false;
		
		return super.checkBlock(block);
		
	}
	
	
}
