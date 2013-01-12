package org.saga.buildings.production;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.saga.SagaLogger;

/**
 * Saga item that represents a Bukkit item.
 * 
 * @author andf
 *
 */
public class SagaItem {

	
    /**
     * Item type.
     */
    protected Material type;
    
    /**
     * Item amount.
     */
    protected Double amount;
    
    /**
     * Item data.
     */
    protected Short data;
	
    
    
    // Initialise:
    /**
	 * Creates the Saga item.
	 * 
	 * @param type item type
	 * @param amount item amount
	 * @param data data
	 */
	public SagaItem(Material type, Double amount, Short data) {
		this.type = type;
		this.amount = amount.doubleValue();
		this.data = data;
	}
	
	/**
	 * Creates the Saga item.
	 * 
	 * @param type item type
	 */
	public SagaItem(Material type) {
		this.type = type;
		this.amount = 1.0;
		this.data = 0;
	}
    
    /**
     * Creates a copy item from another item.
     * 
     * @param item item to copy
     */
    public SagaItem(SagaItem item) {
    	this(item.type, item.amount, item.data);
    }
    
    /**
     * Creates a copy item from a recipe.
     * 
     * @param recipe recipe to take an item from
     */
    public SagaItem(SagaRecipe recipe) {
    	this(recipe.type, recipe.amount, recipe.data);
    }
    
	/**
	 * Fixes all initialisation problems with the item.
	 * 
	 */
	public void complete(){
		
		
		if(type == null){
			type = Material.AIR;
		}

		if(amount == null){
			amount = 1.0;
		}
		
		if(amount < 0){
			SagaLogger.severe(this, "amount negative");
		}
		
		if(data == null){
			data = 0;
		}
		
		
	}
	
	
	
	// Operation:
	/**
	 * Creates the Bukkit item represented by this Saga item.
	 * 
	 * @return bukkit item stack
	 */
	public ItemStack createItem() {

		Short data = this.data;
		
		return new ItemStack(type, amount.intValue(), data);
		
	}

	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public Material getType() {
		return type;
	}

	/**
	 * Gets the amount.
	 * 
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 * Gets the data.
	 * 
	 * @return the data
	 */
	public Short getData() {
		return data;
	}
	
	
	/**
	 * Modifies the amount.
	 * 
	 * @param mod modify amount.
	 */
	public void modifyAmount(double mod) {
		this.amount+= mod;
	}
	
	/**
	 * Sets the amount.
	 * 
	 * @param amount amount
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	
	
	// Other:
	/* 
	 * Checks fields.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return equalsItem(obj);
	}

	/* 
	 * Checks fields.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equalsItem(Object obj) {
		
		if(obj instanceof SagaItem){
			
			SagaItem item = (SagaItem) obj;
			if(!this.type.equals(item.type)) return false;
			if(!this.amount.equals(item.amount)) return false;
			if(!this.data.equals(item.data)) return false;
			return true;
			
		}
		
		return false;
		
	}

	/**
	 * Check if this item represents a Saga item.
	 * 
	 * @param sagaItem Saga item
	 * @return true if represents
	 */
	public boolean checkRepresents(SagaItem sagaItem) {

		if(!this.type.equals(sagaItem.getType())) return false;
		if(!this.data.equals((short)sagaItem.getData())) return false;
		return true;
		
	}
	
	/**
	 * Check if the item represents a given block.
	 * 
	 * @param block block
	 * @return true if represents
	 */
	public boolean checkRepresents(Block block) {

		if(!this.type.equals(block.getType())) return false;
		if(!this.data.equals((short)block.getData())) return false;
		return true;
		
	}

	/**
	 * Check if the item represents a given item stack.
	 * 
	 * @param itemStack item
	 * @return true if represents
	 */
	public boolean checkRepresents(ItemStack itemStack) {

		if(!this.type.equals(itemStack.getType())) return false;
		if(!this.data.equals(itemStack.getData())) return false;
		return true;
		
	}
	
	
	/* 
	 * Show fields.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "{type=" + type + "," + "amount=" + amount + "," + "data=" + data + "}";
	}
    
	
}
