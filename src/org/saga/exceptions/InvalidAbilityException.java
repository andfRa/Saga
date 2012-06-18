package org.saga.exceptions;

public class InvalidAbilityException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Sets a ability name.
	 * 
	 * @param name name
	 */
	public InvalidAbilityException(String name) {
		super("ability name="+name);
	}
	
	/**
	 * Sets a ability name and cause.
	 * 
	 * @param name name
	 * @param cause cause
	 */
	public InvalidAbilityException(String name, String cause) {
		super("ability name=" + name + ", cause=" + cause);
	}
	
	
}
