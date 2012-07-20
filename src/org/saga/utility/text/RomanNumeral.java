package org.saga.utility.text;

//Program: Convert from binary (decimal) to roman numerals.
//This is the model without any user interface.
//Author : Fred Swartz
//Date   : 23 Jan 2006

public class RomanNumeral {
    
	
    private static final String[] RCODE = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
   
    private static final int[]    BVAL  = {1000, 900, 500, 400,  100,   90,  50, 40,   10,    9,   5,   4,    1};
    
    
    /**
     * Converts binary to Roman.
     * 
     * @param binary binary number
     * @return Roman number
     */
    public static String binaryToRoman(int binary) {
    	
    	if(binary == 0) return "" + 0;
    	
        if (binary <= 0 || binary >= 4000) {
            throw new NumberFormatException("Value outside roman numeral range.");
        }
        String roman = "";         // Roman notation will be accumualated here.
        
        // Loop from biggest value to smallest, successively subtracting,
        // from the binary value while adding to the roman representation.
        for (int i = 0; i < RCODE.length; i++) {
            while (binary >= BVAL[i]) {
                binary -= BVAL[i];
                roman  += RCODE[i];
            }
        }
        return roman;
        
    }  
    
    
}
