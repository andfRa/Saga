package org.saga.shape;

import java.util.ArrayList;

public class TrapezoidGrid {
	
	
	// Grid:
	/**
	 * Grid of defined points, null elements for undefined.
	 */
	private Point[][] grid;
	
	
	// Creation:
	/**
	 * Creates a grid.
	 * 
	 * @param distance distance between points
	 * @param width grid width
	 * @param height grid height
	 * @param bottom grid bottom
	 * @param step grid step
	 */
	public TrapezoidGrid(double distance, int width, int height, int bottom, double step) {
		
		
		grid = new Point[height][width];
		
		double radius = 0.5 * bottom;
		
		for (int h = 0; h < grid.length; h++) {
			
			radius = 0.5 * (bottom - 1) * distance  +  step * h * distance;
			
			for (int w = 0; w < grid[h].length; w++) {
				
				double x = h * distance;
				double z = (w - 0.5*(width - 1)) * distance;
				
				if(Math.abs(z) > radius) continue;
				
				grid[h][w] = new Point(x, z);
				
			}
			
			
			
		}
		
		
	}
	
	/**
	 * Creates rotated and shifted trapezoid.
	 * 
	 * @param shift origin shift
	 * @param x x coordinate
	 * @param z z coordinate
	 * @param rot rotation
	 * @return shifted and rotated trapezoid
	 */
	public ArrayList<ArrayList<Point>> create(double shift, double x, double z, double rot) {

		ArrayList<ArrayList<Point>> points = new ArrayList<ArrayList<Point>>();
		
		for (int h = 0; h < grid.length; h++) {
			
			ArrayList<Point> row = new ArrayList<Point>();
			points.add(row);
			
			for (int w = 0; w < grid[h].length; w++) {
				
				if(grid[h][w] != null) row.add(grid[h][w].moved(shift, 0, rot, x, z));
				
			}
			
		}
		
		return points;
		
	}
	
	
	
	// Other:
	/* 
	 * Prints the grid.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer();
		
		for (int h = 0; h < grid.length; h++) {

			if(h != 0) result.append("\n");
			
			for (int w = 0; w < grid[h].length; w++) {
				
				if(w != 0) result.append(" ");
				result.append(grid[h][w]);
				
			}
			
		}
		
		return result.toString();
		
	}
	
	
	public static void main(String[] args) {
		
		
	}
	
	
}
