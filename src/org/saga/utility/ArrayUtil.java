package org.saga.utility;

import java.util.Collection;



public class ArrayUtil {
	
	
	public static Double max(Double[] data){
		
		Double max = 0.0;
		
		for (int i = 0; i < data.length; i++) {
			if(data[i] > max) max = data[i];
		}
		
		return max;
	    
	}

	public static Double min(Double[] data){
		
		Double min = Double.MAX_VALUE;
		
		for (int i = 0; i < data.length; i++) {
			if(data[i] < min) min = data[i];
		}
		
		return min;
	    
	}
	
	
	public static Integer min(Integer[] data){
		
		Integer min = Integer.MAX_VALUE;
		
		for (int i = 0; i < data.length; i++) {
			if(data[i] < min) min = data[i];
		}
		
		return min;
	    
	}

	public static Integer max(Integer[] data){
		
		Integer max = 0;
		
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
	
	public static Double min(Collection<Double> data){
		
		Double min = Double.MAX_VALUE;
		
		for (Double val : data) {
			if(val < min) min = val;
		}
		
		return min;
	    
	}
	
	
	public static Double[] multiply(Double[] data, Double mult){
		
		for (int i = 0; i < data.length; i++) {
			data[i] = data[i] * mult;
		}
		
		return data;
	    
	}
	
	public static Integer[] intArray(Integer length){

		Integer[] array = new Integer[length];
		
		for (int i = 0; i < array.length; i++) {
			array[i] = 0;
		}
		
		return array;
	    
	}
	
}
