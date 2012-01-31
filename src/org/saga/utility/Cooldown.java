package org.saga.utility;

public interface Cooldown {


	/**
	 * Checks if the building is on cooldown.
	 * 
	 * @return true if on cooldown
	 */
	 public boolean isOnCooldown();
	
	/**
	 * Starts the cooldown.
	 * 
	 */
	public void startCooldown();
	
	/**
	 * Gets the cooldown.
	 * 
	 */
	public int getCooldown();
	
	
	
}
