package org.saga.utility;

import java.math.BigDecimal;
import java.util.Collection;



public class MathUtil {
	
	
	public static double round(double unrounded, int precision, int roundingMode){
		
		BigDecimal bd = new BigDecimal(unrounded);
		BigDecimal rounded = bd.setScale(precision, roundingMode);
		return rounded.doubleValue();
	    
	}
	
	public static Double round(double unrounded){
		
		BigDecimal bd = new BigDecimal(unrounded);
		BigDecimal rounded = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		return rounded.doubleValue();
	    
	}
	
	public static Double max(Double[] data){
		
		Double max = 0.0;
		
		for (int i = 0; i < data.length; i++) {
			if(data[i] > max) max = data[i];
		}
		
		return max;
	    
	}
	
	public static Double max(Collection<Double> data){
		
		Double max = 0.0;
		
		for (Double val : data) {
			if(val > max) max = val;
		}
		
		return max;
	    
	}
	
	public static Double[] multiply(Double[] data, Double mult){
		
		for (int i = 0; i < data.length; i++) {
			data[i] = data[i] * mult;
		}
		
		return data;
	    
	}
	
	

	
}
