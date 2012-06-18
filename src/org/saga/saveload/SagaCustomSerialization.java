package org.saga.saveload;

public class SagaCustomSerialization {

	/**
	 * Class name used for serialisation.
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
