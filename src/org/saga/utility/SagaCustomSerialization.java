package org.saga.utility;

public class SagaCustomSerialization {

	/**
	 * Class name used by the loader.
	 */
	@SuppressWarnings("unused")
	private final String _className;

	
	/**
	 * Sets class name.
	 * 
	 */
	protected SagaCustomSerialization() {
		_className = getClass().getName();
	}
	
}
