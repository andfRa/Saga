/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga.saveload;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.TypeAdapter;
import org.bukkit.craftbukkit.libs.com.google.gson.TypeAdapterFactory;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonReader;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonToken;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;
import org.saga.SagaLogger;

/**
 * Serialisation for enums. If enum is not found, first value is returned.
 * 
 * http://google-gson.googlecode.com/svn/trunk/gson/docs/javadocs/com/google/gson/TypeAdapterFactory.html
 *
 */
public class SagaEnumSerializer implements TypeAdapterFactory {

	
	@SuppressWarnings("unchecked")
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		
		
		Class<T> rawType = (Class<T>) type.getRawType();
		if (!rawType.isEnum()) return null;
		
		// Create a map of all enums:
		final Map<String, T> constantMap = new HashMap<String, T>();
	    boolean first = true;
		for (T constant : rawType.getEnumConstants()) {
	    	
			// Add enum:
			String name = ((Enum<?>) constant).name();
	    	constantMap.put(name.toLowerCase(), constant);
	    	
	    	// Replacement for invalid:
	    	if(first){
	    		constantMap.put(null, constant);
	    		first = false;
	    	}
	    	
	    }
	    
		// Create type adapter:
	    return new TypeAdapter<T>() {
	    	
	    	// Writing:
			public void write(JsonWriter out, T value) throws IOException {
				
				if (value == null) {
					
					out.nullValue();
					
				} else {
					
					String name = ((Enum<?>) value).name();
					out.value(name);
					
				}
				
			}
			
			// Reading:
			public T read(JsonReader reader) throws IOException {
				
				if (reader.peek() == JsonToken.NULL) {
					
					reader.nextNull();
					return null;
					
				} else {
					
					// Find constant:
					String name = reader.nextString();
					T value = constantMap.get(name.toLowerCase());
					
					// Invalid name:
					if(value == null){
						value = constantMap.get(null);
						SagaLogger.severe("Constant not found for " + name + ", using " + value + ".");
					}
					
					return value;
					
				}
			}
		
	    };    
		    
	}
	
	
}
