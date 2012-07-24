package org.saga.utility;

import org.saga.utility.text.TextUtil;

public class Histogram {

	
	private Double[] data;

	private Double max;
	
	private Double min;
	
	
	
	public Histogram(Double[] data) {
		
		
		this.data = data;
		
		max = ArrayUtil.max(data);
		min = ArrayUtil.min(data);
		
		for (int i = 0; i < data.length; i++) {
			data[i]-= min;
		}
		
		
	} 
	
	public Integer[] createHistogram(Integer width) {

		
		Double max = ArrayUtil.max(data);
		Integer[] histogram = ArrayUtil.intArray(width);
		Double step = (width.doubleValue() - 1.0) / max;

		for (int i = 0; i < data.length; i++) {

			if(data[i] == 0) continue;
			
			Integer index = new Double(data[i] * step).intValue();
			histogram[index] = histogram[index] + 1;
			
		}
		
		return histogram;

		
	}

	public Integer[] createHistogram(Integer width, Integer height) {

		
		Integer[] histogram = createHistogram(width);
		
		Integer max = ArrayUtil.max(histogram);
		
		if(max == 0) return histogram;
		
		for (int i = 0; i < histogram.length; i++) {
			histogram[i] = new Double(histogram[i] * height / max).intValue();
		}
		
		return histogram;

		
	}
	
	public Double[] createValues(Integer width) {

		
		Double step = (max - min) / (width.doubleValue());
		
		Double[] vals = new Double[width + 1]; 
		
		for (int i = 0; i < vals.length; i++) {
			vals[i] = i* step + min;
		}

		return vals;
		
		
	}
	
	public String[] createValues(Integer width, Integer decimals) {
		
		
		Double[] values = createValues(width);
		
		String[] strVals = new String[values.length];
		
		for (int i = 0; i < values.length; i++) {
			strVals[i] = TextUtil.round(values[i], decimals);
		}
		
		return strVals;
		
		
	}

	
}
