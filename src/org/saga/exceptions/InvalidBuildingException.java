package org.saga.exceptions;

public class InvalidBuildingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Sets a building name.
	 * 
	 * @param name name
	 */
	public InvalidBuildingException(String name) {
		super("building name="+name);
	}
	
	/**
	 * Sets a building name and cause.
	 * 
	 * @param name name
	 * @param cause cause
	 */
	public InvalidBuildingException(String name, String cause) {
		super("building name=" + name + ", cause=" + cause);
	}
	
	
	
}
