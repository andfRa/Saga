package org.saga.saveload;

public class SagaCustomSerialization {

	
	/**
	 * Class name used for serialisation.
	 */
	private String _className;

	
	
	/**
	 * Sets class name.
	 * 
	 */
	protected SagaCustomSerialization() {
		_className = getClass().getName();
	}
	
	
	
	/**
	 * Gets the _className used during serialisation.
	 * 
	 * @return _className
	 */
	public String get_className() {
		return _className;
	}
	
	/**
	 * Sets _className.
	 * 
	 * @param _className new _className
	 */
	public void set_className(String _className) {
		this._className = _className;
	}
	
	
}
