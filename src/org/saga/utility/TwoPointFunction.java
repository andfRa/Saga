package org.saga.utility;

import org.saga.Saga;
import org.saga.player.SagaEntityDamageManager;

public class TwoPointFunction {

	
	/**
	 * Maximum level.
	 */
	transient public static final short MAXIMUM_LEVEL = 50;
	
	/**
	 * Multiplier function x1.
	 */
	private Short x1;
	
	/**
	 * Multiplier function y1.
	 */
	private Double y1;
	
	/**
	 * Multiplier function x2.
	 */
	private Short x2;
	
	/**
	 * Multiplier function y2
	 */
	private Double y2;
	
	
	/**
	 * Used by gson.
	 * 
	 */
	@SuppressWarnings("unused")
	private TwoPointFunction() {
	}
	
	/**
	 * Sets constant value.
	 * 
	 * @param value value
	 */
	public TwoPointFunction(Double value){

		this.x1 = 0;
		this.y1 = value;
		this.x2 = 1;
		this.y2 = value;
		
	}

	/**
	 * Sets function y values at min and max points.
	 * 
	 * 
	 * @param x1 x1
	 * @param y1 y1
	 * @param x2 x2
	 * @param y2 y2
	 */
	public TwoPointFunction(Short x1, Double y1, Short x2, Double y2){

		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		
	}
	
	/**
	 * Completes the function.
	 * 
	 * @return integrity
	 */
	public boolean complete() {
		
		
		boolean integrity = true;
		// Fields:
		if(x1 == null){
			x1 = 0;
			Saga.severe("Failed to initialize " + this + " level function x1. Setting default.");
			integrity = false;
		}
		if(y1 == null){
			y1 = 1.0;
			Saga.severe("Failed to initialize " + this + " level function y1. Setting default.");
			integrity = false;
		}
		if(x2 == null){
			x2 = MAXIMUM_LEVEL;
			Saga.severe("Failed to initialize " + this + " level function x2. Setting default.");
			integrity = false;
		}
		if(y2 == null){
			y2 = 1.0;
			Saga.severe("Failed to initialize " + this + " level function y2. Setting default.");
			integrity = false;
		}

		return integrity;
		
		
	}


	/**
	 * Calculates the value for the given y value.
	 * 
	 * @param x x value
	 */
	public Double value(Short x) {
		
		
		if(x > x2){
			x = x2;
		}
		
		if(x < x1){
			x = x1;
		}
		
		if(x2 - x1 == 0){
			Saga.severe(getClass(), "infinite slope", "using 0.0 value");
			return 0.0;
		}
		
		double k= (y2 - y1)/(x2-x1);
		double b= y1 - k * x1;
		return new Double(k * x + b);
		
		
	}
	
	/**
	 * Calculates the value for the given y value.
	 * 
	 * @param x x value
	 */
	public Double value(Integer x) {
		
		return value(x.shortValue());
		
	}
	
	/**
	 * Calculates the random integer value for the given y value.
	 * 
	 * @param x x value
	 */
	public Integer randomIntValue(Integer x) {
		
		return SagaEntityDamageManager.randomRound(value(x.intValue()));
		
	}
	
	/**
	 * Gets the minimum x value.
	 * 
	 * @return x requirement
	 */
	public Short getXMin() {
		return x1;
	}
	
	/**
	 * Gets the maximum x value.
	 * 
	 * @return maximum value
	 */
	public Short getXMax() {
		return x2;
	}
	
	
	@Override
	public String toString() {
		return "(" + x1 +"," + y1 + ");(" + x2 +"," + y2 + ")";
	}
	
	
}
