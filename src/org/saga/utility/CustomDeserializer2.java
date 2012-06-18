/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga.utility;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * 
 * @author Cory
 */
public class CustomDeserializer2 implements JsonSerializer<Object>, JsonDeserializer<Object> {
	
	
	public Object deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
	
		// If this JsonElement is not an object:
		if (!je.isJsonObject()) {
			throw new JsonParseException("ProfessionDeserializer JsonElement is not JsonObject!");
		}
		
		JsonObject jo = (JsonObject) je;
		JsonElement classElement = jo.get("_className");
		
		// No _className element:
		if (classElement == null) {
			Gson gson = new Gson();
			return gson.fromJson(je, type);
		}
		
		// Try to get class:
		String className = classElement.getAsString();
		Object instance = null;
		Type newType = null;
		try {
			newType = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new JsonParseException("Class " + className + " not found!");
		}
		
		// Type didn't change:
		if (type.equals(newType)) {
			Gson gson = new Gson();
			return gson.fromJson(je, type);
		}
		
		instance = jdc.deserialize(je, newType);
		
		return instance;
		
	}
	
	public JsonElement serialize(Object t, Type type, JsonSerializationContext jsc) {
	
		JsonObject jo = (JsonObject) jsc.serialize(t, t.getClass());
		
		return jo;
		
	}
	
}
