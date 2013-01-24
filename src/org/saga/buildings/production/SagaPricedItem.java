package org.saga.buildings.production;

import org.saga.SagaLogger;


public class SagaPricedItem extends SagaItem{

	
	/**
	 * Price contents.
	 */
	protected Double price;
	
	

	// Initialisation:
	/**
	 * Creates a priced item.
	 * 
	 * @param item saga item
	 * @param priced item price
	 */
	public SagaPricedItem(SagaItem item, Double price){
		super(item);
		this.price = price;
	}
	
	/**
	 * Creates a copy, based on another priced item.
	 * 
	 * @param priced item priced item
	 */
	protected SagaPricedItem(SagaPricedItem pricedItem) {
		super(pricedItem);
		this.price = pricedItem.price;
	}
	
	/**
	 * Fixes all initialisation problems with the priced item.
	 * 
	 */
	public void complete(){
		
		super.complete();
		
		if(price == null){
			price = 0.0;
			SagaLogger.nullField(this, "price");
		}
		
	}
	
	
	// Getters:
	/**
	 * Gets item price.
	 * 
	 * @return item price
	 */
	public Double getPrice() {
		return price;
	}
	
	
	
	// Other:
	/* 
	 * Checks the priced item.
	 * 
	 * @see org.saga.buildings.production.SagaItem#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		return equalsPricedItem(obj);
		
	}
	
	/**
	 * Checks if the two priced items are equal.
	 * 
	 * @param obj priced items
	 * @return true if equal
	 */
	boolean equalsPricedItem(Object obj) {

		if(!equalsItem(obj)) return false;
		
		if(obj instanceof SagaPricedItem){
			
			SagaPricedItem pricedItem = (SagaPricedItem) obj;
			
			return this.price.equals(pricedItem.price);
			
		}
		
		return false;
		
	}
	
	
}
