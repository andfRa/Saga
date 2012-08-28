/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga.config;

import java.lang.reflect.Type;

import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer;
import org.saga.player.ProficiencyDefinition;

/**
 *
 * @author Cory
 */
public class ProfessionDeserializer implements  JsonSerializer<ProficiencyDefinition>, JsonDeserializer<ProficiencyDefinition> {

	
       public ProficiencyDefinition deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {

    	   System.out.println(type);

           //If this JsonElement is not an object we cannot create a profession
           if ( !je.isJsonObject() ) {
               throw new JsonParseException("ProfessionDeserializer JsonElement is not JsonObject!");
           }

           JsonObject jo = (JsonObject)je;
           JsonElement classElement = jo.get("_className");
           String className = classElement.getAsString();

           ProficiencyDefinition ability = null;

           //Try to get class
           try {
               type = Class.forName(className);
           } catch ( ClassNotFoundException e ) {
               throw new JsonParseException("Class " + className + " not found!");
           }

           ability = jdc.deserialize(je, type);

           return ability;
            
        }

        public JsonElement serialize(ProficiencyDefinition t, Type type, JsonSerializationContext jsc) {

        	
            JsonObject jo = (JsonObject)jsc.serialize(t, t.getClass());

            jo.addProperty("_className", t.getClass().getName());
            
            return jo;

            
        }

}
