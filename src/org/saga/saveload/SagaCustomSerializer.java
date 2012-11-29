/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga.saveload;

import java.lang.reflect.Type;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;

/**
 * 
 * @author Cory
 */
public class SagaCustomSerializer implements JsonSerializer<SagaCustomSerialization>, JsonDeserializer<SagaCustomSerialization> {

	public SagaCustomSerialization deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {


		// Check if an object:
		if (!je.isJsonObject()) {
			throw new JsonParseException("JsonElement is not JsonObject");
		}
		JsonObject jo = (JsonObject) je;
		
		// Custom class:
		JsonElement classElement = jo.get("_className");
		if (classElement == null) throw new JsonParseException("missing _className field");
		String className = classElement.getAsString();

		// Find custom class:
		try {
			type = Class.forName(className);
		}
		catch (ClassNotFoundException e) {
			throw new JsonParseException("class " + className + " not found");
		}

		// Get object:
		SagaCustomSerialization object = jdc.deserialize(je, type);
		
//		// Run complete:
//		try {
//			Method complete = object.getClass().getMethod("complete");
//			complete.invoke(object);
//		}
//		catch (Exception e) {}

		return object;

	}

	public JsonElement serialize(SagaCustomSerialization t, Type type, JsonSerializationContext jsc) {


		JsonObject jo = (JsonObject) jsc.serialize(t, t.getClass());

		jo.addProperty("_className", t.getClass().getName());

		return jo;

	}


}
