package org.saga.utility.items;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.inventory.ItemStack;
import org.saga.SagaLogger;

public class RecepieBlueprint {

	
	/**
	 * Recipe items.
	 */
	private HashSet<ItemBlueprint> from;
	
	/**
	 * Crafted items.
	 */
	private HashSet<ItemBlueprint> to;

	/**
	 * Probability weight.
	 */
	private Double weight;
	
	
	
	/**
	 * Fixes all fields.
	 * 
	 */
	public void complete() {
		

		if(from == null){
			from = new HashSet<ItemBlueprint>();
		}
		for (ItemBlueprint blueprint : from) {
			blueprint.complete();
		}

		if(to == null){
			to = new HashSet<ItemBlueprint>();
		}
		for (ItemBlueprint blueprint : to) {
			blueprint.complete();
		}
		
		if(weight == null){
			SagaLogger.nullField(this, "weight");
			weight = 0.0;
		}
		
	
	}
	
	
	
	/**
	 * Creates a list for the required items.
	 * 
	 * @return list of required items
	 */
	public ArrayList<ItemStack> createFrom() {

		
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		
		for (ItemBlueprint blueprint : from) {
			items.add(blueprint.createItem());
		}
		
		return items;
		
		
	}
	
	/**
	 * Creates a list for the resulting items.
	 * 
	 * @return list of resulting items
	 */
	public ArrayList<ItemStack> createTo() {

		
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		
		for (ItemBlueprint blueprint : to) {
			items.add(blueprint.createItem());
		}
		
		return items;
		
		
	}
	
	/**
	 * Gets probability weight.
	 * 
	 * @return weight
	 */
	public Double getWeight() {
		return weight;
	}

	
}
