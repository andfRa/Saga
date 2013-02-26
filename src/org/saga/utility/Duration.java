package org.saga.utility;

public class Duration {

	/**
	 * Hours.
	 */
	private int hours;
	
	/**
	 * Minutes.
	 */
	private int minutes;
	
	/**
	 * Seconds.
	 */
	private int seconds;
	
	
	/**
	 * Initialises duration using milliseconds.
	 * 
	 * @param milliseconds milliseconds
	 */
	public Duration(long milliseconds) {
		
		int totalSecs = (int) (milliseconds/1000);
		
		hours = totalSecs / 3600;
		minutes = (totalSecs % 3600) / 60;
		seconds = totalSecs % 60;

	}
	
	
	/**
	 * Gets the hours.
	 * 
	 * @return the hours
	 */
	public int getHours() {
		return hours;
	}
	
	/**
	 * Gets the minutes.
	 * 
	 * @return the minutes
	 */
	public int getMinutes() {
		return minutes;
	}
	
	/**
	 * Gets the seconds.
	 * 
	 * @return the seconds
	 */
	public int getSeconds() {
		return seconds;
	}
	
	
}
