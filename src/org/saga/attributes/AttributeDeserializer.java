/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga.attributes;

import java.lang.reflect.Type;

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
public class AttributeDeserializer implements  JsonSerializer<Attribute>, JsonDeserializer<Attribute> {

	
       public Attribute deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {

    	   
            //If this JsonElement is not an object we cannot create a profession
            if ( !je.isJsonObject() ) {
                throw new JsonParseException("ProfessionDeserializer JsonElement is not JsonObject!");
            }

            JsonObject jo = (JsonObject)je;
            JsonElement classElement = jo.get("_className");
            String className = classElement.getAsString();

            Attribute ability = null;

            //Try to get class
            try {
                type = Class.forName(className);
            } catch ( ClassNotFoundException e ) {
                throw new JsonParseException("Class " + className + " not found!");
            }

            ability = jdc.deserialize(je, type);

            return ability;

            
        }

        public JsonElement serialize(Attribute t, Type type, JsonSerializationContext jsc) {

        	
            JsonObject jo = (JsonObject)jsc.serialize(t, t.getClass());

            jo.addProperty("_className", t.getClass().getName());
            
            return jo;

            
        }

}
