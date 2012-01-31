package org.saga.shape;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;



public class RelativeShape {
	
	
	/**
	 * X offsets.
	 */
	private ArrayList<Integer> xoffsets = new ArrayList<Integer>();

	/**
	 * Y offsets.
	 */
	private ArrayList<Integer> yoffsets = new ArrayList<Integer>();

	/**
	 * Z offsets.
	 */
	private ArrayList<Integer> zoffsets = new ArrayList<Integer>();

	/**
	 * Required levels.
	 */
	private ArrayList<Integer> reqLevels = new ArrayList<Integer>();
	
	/**
	 * Shape filter.
	 */
	private ShapeFilter filter = null;
	
	// Initialization:
	/**
	 * Initializes.
	 * 
	 */
	public RelativeShape() {

	
	}
	
	/**
	 * Sets filter.
	 * 
	 * @param filter shape filter
	 */
	public RelativeShape(ShapeFilter filter) {

		this.filter = filter;
		
	}
	
	
	// Elements:
	/**
	 * Adds an offset.
	 * 
	 * @param x x offset
	 * @param y y offset
	 * @param z z offset
	 */
	public void addOffset(int x, int y, int z) {
		
		this.xoffsets.add(x);
		this.yoffsets.add(y);
		this.zoffsets.add(z);
		this.reqLevels.add(-1);
		
	}
	
	/**
	 * Adds an offset.
	 * 
	 * @param x x offset
	 * @param y y offset
	 * @param z z offset
	 * @param reqLevel required level
	 */
	public void addOffset(int x, int y, int z, int reqLevel) {
		
		this.xoffsets.add(x);
		this.yoffsets.add(y);
		this.zoffsets.add(z);
		this.reqLevels.add(reqLevel);
		
	}
	
	/**
	 * Adds a relative offset.
	 * 
	 * @param x x offset
	 * @param y y offset
	 * @param z z offset
	 */
	/**
	 * 
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param level
	 */
	public void addRelOffset(Integer x, Integer y, Integer z, Integer level) {

		
		// Add last element:
		if(xoffsets.size() > 0){
			x += xoffsets.get(xoffsets.size()-1);
			y += yoffsets.get(yoffsets.size()-1);
			z += zoffsets.get(zoffsets.size()-1);
		}
		
		// Add offsets:
		this.xoffsets.add(x);
		this.yoffsets.add(y);
		this.zoffsets.add(z);
		this.reqLevels.add(-1);
		
		
	}
	
	
	// Retrieval:
	/**
	 * Gets the blocks of the shape.
	 * 
	 * @param anchor anchor location
	 * @param orientation orientation
	 * @param level level
	 * @return shape
	 */
	public ArrayList<Block> getBlocks(Location anchor, Orientation orientation, Integer level) {
		
		
		ArrayList<Block> blocks = new ArrayList<Block>();
		
		for (int i = 0; i < xoffsets.size(); i++) {
			
			Block block = anchor.getBlock().getRelative(getXOffset(orientation, i), getYOffset(orientation, i), getZOffset(orientation, i));
			
			if(filter != null && !filter.checkBlock(block)) continue;
			
			Integer reqLevel = reqLevels.get(i);
			if(reqLevel != -1 && level < reqLevel) continue;
			
			blocks.add(block);
			
		}
		
		return blocks;
		
		
	}

	/**
	 * Gets x offset.
	 * 
	 * @param orientation orientation
	 * @param index shape element index
	 * @return x offset
	 * @throws IndexOutOfBoundsException when the index is invalid
	 */
	public final int getXOffset(Orientation orientation, int index) throws IndexOutOfBoundsException{
		
		
		if(orientation == Orientation.NORTH){
			
			return xoffsets.get(index);
			
		}else if(orientation == Orientation.EAST){
			
			return -zoffsets.get(index);
			
		}else if(orientation == Orientation.SOUTH){
			
			return -xoffsets.get(index);
			
		}else{
			
			return zoffsets.get(index);
			
		}
		
		
	}
	
	/**
	 * Gets y offset.
	 * 
	 * @param orientation orientation
	 * @param index shape element index
	 * @return y offset
	 * @throws IndexOutOfBoundsException when the index is invalid
	 */
	public final int getYOffset(Orientation orientation, int index) throws IndexOutOfBoundsException{
		
		return yoffsets.get(index);
		
	}
	
	/**
	 * Gets x offset.
	 * 
	 * @param orientation orientation
	 * @param index shape element index
	 * @return x offset
	 * @throws IndexOutOfBoundsException when the index is invalid
	 */
	public final int getZOffset(Orientation orientation, int index) throws IndexOutOfBoundsException{
		
		
		if(orientation == Orientation.NORTH){
			
			return zoffsets.get(index);
			
		}else if(orientation == Orientation.EAST){
			
			return xoffsets.get(index);
			
		}else if(orientation == Orientation.SOUTH){
			
			return -zoffsets.get(index);
			
		}else{
			
			return -xoffsets.get(index);
			
		}
		
		
	}
	
	
	/**
	 * Shape orientation.
	 * 
	 * @author andf
	 *
	 */
	public enum Orientation{
		
		NORTH,
		EAST,
		SOUTH,
		WEST;
		
	}
	
	
}
