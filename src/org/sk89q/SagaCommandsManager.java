package org.sk89q;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Modification to sk98q CommandsManager.
 * 
 * @author andf
 *
 */
public abstract class SagaCommandsManager<T> extends CommandsManager<T>{

	
	/**
     * Gets all command methods.
     * 
     * @return command methods
     */
    public HashSet<Method> getCommandMethods() {

    	
    	HashSet<Method> methods = new HashSet<Method>();
    	
    	Set<Entry<Method, Map<String, Method>>> methodEntries = commands.entrySet();
    	for (Entry<Method, Map<String, Method>> entry : methodEntries) {
			
    		methods.add(entry.getKey());
    		if(entry.getValue() != null) methods.addAll(entry.getValue().values());
    		
		}
    	
    	methods.remove(null);
    	
    	return methods;
    	
    	
	}
	
    
}
