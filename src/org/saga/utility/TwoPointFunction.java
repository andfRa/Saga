package org.saga.utility;

import java.util.Random;

import org.saga.SagaLogger;

public class TwoPointFunction {

	
	/**
	 * Random generator.
	 */
	private static Random RANDOM = new Random();
	
	
	
	/**
	 * Multiplier function x1.
	 */
	private Double x1;
	
	/**
	 * Multiplier function y1.
	 */
	private Double y1;
	
	/**
	 * Multiplier function x2.
	 */
	private Double x2;
	
	/**
	 * Multiplier function y2
	 */
	private Double y2;
	
	
	
	// Initialisation:
	/**
	 * Sets constant value.
	 * 
	 * @param value value
	 */
	public TwoPointFunction(Double value){

		this.x1 = 0.0;
		this.y1 = value;
		this.x2 = 1.0;
		this.y2 = value;
		
	}

	/**
	 * Sets function values at min and max points.
	 * 
	 * 
	 * @param x1 x1
	 * @param y1 y1
	 * @param x2 x2
	 * @param y2 y2
	 */
	public TwoPointFunction(Double x1, Double y1, Double x2, Double y2){

		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		
		if(x1 == x2){
			x2++;
			SagaLogger.severe(getClass(), "infinite slope in constructor");
		}
		
	}
	
	/**
	 * Completes the function.
	 * 
	 * @return integrity
	 */
	public boolean complete() {
		
		
		boolean integrity = true;
		
		if(x1 == null){
			x1 = 0.0;
			SagaLogger.nullField(getClass(), "x1");
			integrity = false;
		}
		if(y1 == null){
			y1 = 1.0;
			SagaLogger.nullField(getClass(), "y1");
			integrity = false;
		}
		if(x2 == null){
			x2 = x1 + 1.0;
		}
		if(y2 == null){
			y2 = y1;
		}
		
		if(x1 == x2){
			x2++;
			SagaLogger.severe(getClass(), "infinite slope in complete");
		}

		return integrity;
		
		
	}

	
	// X values:
	/**
	 * Gets the x value for the given y.
	 * 
	 * @param y y value
	 * @return x value, x2, if undefined
	 */
	public Double xValue(Double y) {

		double k= (y2 - y1)/(x2-x1);
		double b= y1 - k * x1;
		
		if(k == 0) return x2;
		
		return new Double((y - b) / k);
		
	}
	
	/**
	 * Gets the x value for the given y.
	 * 
	 * @param y y value
	 * @return x value, x2, if undefined
	 */
	public Double xValue(Integer y) {
		return xValue(y.doubleValue());
	}
	
	/**
	 * Gets the x value for the given y.
	 * 
	 * @param y y value
	 * @return x value, x2, if undefined
	 */
	public Integer intxValue(Double y) {
		return xValue(y).intValue();
	}
	
	/**
	 * Gets the x value for the given y.
	 * 
	 * @param y y value
	 * @return x value, x2, if undefined
	 */
	public Integer intxValue(Integer y) {
		return xValue(y.doubleValue()).intValue();
	}

	
	
	// Values:
	/**
	 * Calculates the value for the given y value.
	 * 
	 * @param x x value
	 */
	public Double value(Double x) {
		
		
		if(x > x2){
			x = x2;
		}
		
		if(x < x1){
			x = x1;
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
		
		return value(x.doubleValue());
		
	}
	
	/**
	 * Calculates the value for the given y value.
	 * 
	 * @param x x value
	 */
	public Integer intValue(Integer x) {
		
		return value(x.doubleValue()).intValue();
		
	}
	
	/**
	 * Calculates the value for the given y value.
	 * 
	 * @param x x value
	 */
	public Integer intValue(Double x) {
		
		return value(x).intValue();
		
	}
	
	/**
	 * Calculates the value for the given y value.
	 * 
	 * @param x x value
	 */
	public Integer intValueCeil(Integer x) {
		
		return (int)Math.ceil(value(x.doubleValue()));
		
	}
	
	
	/**
	 * Calculates the random integer value for the given x value.
	 * 
	 * @param x x value
	 */
	public Integer randomIntValue(Integer x) {
		
		return randomRound(value(x.intValue()));
		
	}
	
	/**
	 * Calculates the random short value for the given x value.
	 * 
	 * @param x x value
	 */
	public Short randomShortValue(Integer x) {
		
		return randomIntValue(x).shortValue();
		
	}

	/**
	 * Calculates the random boolean value for the given x value.
	 * 
	 * @param x x value
	 */
	public boolean randomBooleanValue(Double x) {
		
		return value(x) > RANDOM.nextDouble();
		
	}
	
	/**
	 * Calculates the random boolean value for the given x value.
	 * 
	 * @param x x value
	 */
	public boolean randomBooleanValue(Integer x) {
		
		return randomBooleanValue(x.doubleValue());
		
	}
	
	
	/**
	 * Gets the minimum x value.
	 * 
	 * @return x requirement
	 */
	public Integer getXMin() {
		return x1.intValue();
	}
	
	/**
	 * Gets the maximum x value.
	 * 
	 * @return maximum value
	 */
	public Integer getXMax() {
		return x2.intValue();
	}
	
	
	
	// Utility:
	/**
	 * Generates rounds a integer and adds random damage of one.
	 * 
	 * @param value value
	 * @return integer with random additional damage
	 */
	public static Integer randomRound(Double value) {

		
		
		if (value >= 0){
		
			double decimal = value - Math.floor(value);
			
			if(RANDOM.nextDouble() < decimal){
				return value.intValue() + 1;
			}else{
				return value.intValue();
			}
			
		}else{
			
			double decimal = -value + Math.ceil(value);
			
			if(RANDOM.nextDouble() < decimal){
				return value.intValue() - 1;
			}else{
				return value.intValue();
			}
			
		}
			
			
			
		
	}
	
	

	// Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + x1 +"," + y1 + ");(" + x2 +"," + y2 + ")";
	}
	
	
	public static void main(String[] args) {

		System.out.println(Math.ceil(4.55));
		
	}
	
}
