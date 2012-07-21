package org.saga.utility.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.saga.SagaLogger;

public class ItemBlueprint {

	
	/**
	 * Material.
	 */
	private Material mat;
	
	/**
	 * Amount.
	 */
	private Integer amount = null;
	
	/**
	 * Data.
	 */
	private Byte data = null;
	
	// TODO: Add enchantments to ItemData
	
	/**
	 * Create a blueprint.
	 * 
	 * @param mat material
	 * @param amount amount
	 * @param data data
	 * @param weight probability weight
	 */
	public ItemBlueprint(Material mat, Integer amount, Byte data) {
	
		this.mat = mat;
		this.amount = amount;
		this.data = data;
	
	}
	
	/**
	 * Completes the function.
	 * 
	 * @return integrity
	 */
	public void complete() {
		
		
		if(mat == null){
			mat = Material.AIR;
			SagaLogger.nullField(getClass(), "mat");
		}
		
		// May be null:
		if(amount == null){
			amount = 1;
		}
		
		if(data == null){
			data = 0;
		}
		
		
	}

	

	/**
	 * Gets the material.
	 * 
	 * @return the material
	 */
	public Material getMat() {
		return mat;
	}



	/**
	 * Gets the data.
	 * 
	 * @return the data
	 */
	public Byte getData() {
		return data;
	}
	
	/**
	 * Creates an item from the blueprint.
	 * 
	 * @return item stack
	 */
	public ItemStack createItem() {

		return new ItemStack(mat, amount, data);
		
	}
	
	
}
