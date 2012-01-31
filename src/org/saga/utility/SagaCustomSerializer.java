/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga.utility;

import java.lang.reflect.Type;

import org.saga.Saga;

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
public class SagaCustomSerializer implements  JsonSerializer<SagaCustomSerialization>, JsonDeserializer<SagaCustomSerialization> {

	
       public SagaCustomSerialization deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {

    	   
            //If this JsonElement is not an object we cannot create a profession
            if ( !je.isJsonObject() ) {
                throw new JsonParseException("JsonElement is not JsonObject");
            }

            JsonObject jo = (JsonObject)je;
            JsonElement classElement = jo.get("_className");
            
            if(classElement == null){
            	throw new JsonParseException("missing _className field");
            }
            
            String className = classElement.getAsString();
            
            // Fix:
            className = fixName(className);
            
            //Try to get class:
            try {
                type = Class.forName(className);
            } catch ( ClassNotFoundException e ) {
                throw new JsonParseException("class " + className + " not found");
            }

            return jdc.deserialize(je, type);

        }

        public JsonElement serialize(SagaCustomSerialization t, Type type, JsonSerializationContext jsc) {

            JsonObject jo = (JsonObject)jsc.serialize(t, t.getClass());

            jo.addProperty("_className", t.getClass().getName());
            
            return jo;

        }

        /**
         * Fixes the class name.
         * 
         * @param name name
         * @return fixed name
         */
        private String fixName(String name) {

        	
        	// Barracks:
        	if(name.contains("Baracks")){

        		Saga.warning(getClass(), "found invalid name " + name, "fixing name");
        		
        		return name.replace("Baracks", "Barracks");
        		
        	}
        	
        	return name;
        	
        	
		}
        
}
