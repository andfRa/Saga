package org.saga.shape;


/**
 * Immutable point with some math operations.
 * 
 * @author andf
 *
 */
public class Point {
	
	
	private double x;
	private double z;
	
	/**
	 * Creates a point.
	 * 
	 * @param x x coordinate
	 * @param z z coordinate
	 */
	public Point(double x, double z) {
		super();
		this.x = x;
		this.z = z;
	}
	
	
	/**
	 * Finds a shifted point.
	 * 
	 * @param x amount to shift x by
	 * @param z amount to shift y by
	 * @return shifted point
	 */
	public Point shifted(double x, double z) {
		
		return new Point(this.x + x, this.z + z);
		
	}
	
	/**
	 * Finds a shifted and rotated point.
	 * 
	 * @param rot degrees to rotate by
	 * @return rotated point
	 */
	public Point rotated(double rot) {
		
		double nx = x * Math.cos(rot) - z * Math.sin(rot);
		double nz = x * Math.sin(rot) + z * Math.cos(rot);
		
		return new Point(nx, nz);
		
	}
	
	/**
	 * Finds a origin shifted, rotated and shifted point.
	 * 
	 * @param ox origin x shift
	 * @param oz origin z shift
	 * @param rot rotation degrees
	 * @param sx x shift
	 * @param sz z shift
	 * @return moved point
	 */
	public Point moved(double ox, double oz, double rot, double sx, double sz) {

		double nx = (x + ox) * Math.cos(rot) - (z + oz) * Math.sin(rot) + sx;
		double nz = (x + ox) * Math.sin(rot) + (z + oz) * Math.cos(rot) + sz;
		
		return new Point(nx, nz);
		
	}
	
	
	/**
	 * Gets the x.
	 * 
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * Gets the z.
	 * 
	 * @return the z
	 */
	public double getZ() {
		return z;
	}
	
	
	/* 
	 * Prints the point.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + x + "," + z + ")";
	}
	
	
}
