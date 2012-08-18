package org.saga.utility;

public enum MetricPrefix {

	
	NONE("",1.0),
	k("k", 1000.0),
	M("M", 1000000.0);
	
	
	
	/**
	 * Prefix name.
	 */
	private final String name;
	
	/**
	 * Prefix value.
	 */
	private final Double value;

	
	
	/**
	 * Creates a metric prefix.
	 * 
	 * @param name name
	 * @param value value
	 */
	private MetricPrefix(String name, Double value) {
		this.name = name;
		this.value = value;
	}

	
	
	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	
	
	
}
