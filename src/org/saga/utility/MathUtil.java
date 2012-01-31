package org.saga.utility;

import java.math.BigDecimal;



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

	
}
