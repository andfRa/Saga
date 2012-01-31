package org.saga.exceptions;



public class InvalidLocationException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double x;
	
	private double y;
	
	private double z;
	
	private String world;

	public InvalidLocationException(String world, Double x, Double y, Double z) {
	
		super();
	
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		
	}
	
	@Override
	public String getMessage() {
		return "("+ world + "- "+ x + ", "+ y + ", "+ z + ")";
	}
	
	
}
