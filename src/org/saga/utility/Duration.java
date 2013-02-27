package org.saga.utility;

public class Duration {

	/**
	 * Days.
	 */
	private int days;

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
		
		seconds = (int) ((milliseconds/1000)%60);
		minutes = (int) ((milliseconds/(1000*60))%60);
		hours = (int) ((milliseconds/(1000*60*60))%24);
		days = (int) ((milliseconds/ (1000*60*60*24)));
		
	}
	

	/**
	 * Gets the days.
	 * 
	 * @return the days
	 */
	public int getDays() {
		return days;
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
	 * Gets the full hours.
	 * 
	 * @return the hours
	 */
	public int getFullHours() {
		return hours + 24 * days;
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
	 * Gets the full minutes.
	 * 
	 * @return the minutes
	 */
	public int getFullMinutes() {
		return minutes + 60 * getFullHours();
	}
	
	/**
	 * Gets the seconds.
	 * 
	 * @return the seconds
	 */
	public int getSeconds() {
		return seconds;
	}

	/**
	 * Gets the full seconds.
	 * 
	 * @return the seconds
	 */
	public int getFullSeconds() {
		return seconds + 60 * getFullMinutes();
	}
	
	
}
