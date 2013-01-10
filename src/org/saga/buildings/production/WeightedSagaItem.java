package org.saga.buildings.production;

import org.bukkit.Material;
import org.saga.SagaLogger;

public class WeightedSagaItem extends SagaItem {

	
	/**
	 * Item weight.
	 */
	private Double weight;
	
	
	
    // Initialise:
    /**
	 * Creates the weighted Saga item.
	 * 
	 * @param type item type
	 * @param amount item amount
	 * @param data data
	 * @param weight
	 */
	public WeightedSagaItem(Material type, Double amount, Short data, Double weight) {
		super(type, amount, data);
		this.weight = weight;
	}

	/**
	 * Creates the weighted Saga item.
	 * 
	 * @param type item type
	 * @param amount item amount
	 * @param data data
	 * @param weight
	 */
	public WeightedSagaItem(Material type, Integer amount, Short data, Double weight) {
		super(type, amount, data);
		this.weight = weight;
	}

    /**
     * Creates a copy item from another item.
     * 
     * @param item item to copy
     * @param weight item weight
     */
    public WeightedSagaItem(SagaItem item, Double weight) {
    	this(item.type, item.amount, item.data, weight);
    }

	/* 
	 * Checks weight.
	 * 
	 * @see org.saga.buildings.production.SagaItem#complete()
	 */
	@Override
	public void complete(){
		
		super.complete();
		
		if(weight == null){
			weight = 1.0;
			SagaLogger.nullField(this, "weight");
		}
		
	}


	
	
	// Operation:
	/**
	 * Gets the weight.
	 * 
	 * @return the weight
	 */
	public Double getWeight() {
		return weight;
	}

	
	/**
	 * Sets the weight.
	 * 
	 * @param weight the weight to set
	 */
	public void setWeight(Double weight) {
		this.weight = weight;
	}

	
	
	// Other:
	/* 
	 * Show fields.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "{type=" + type + "," + "amount=" + amount + "," + "data=" + data + "," + "weight=" + weight + "}";
	}
	

}
