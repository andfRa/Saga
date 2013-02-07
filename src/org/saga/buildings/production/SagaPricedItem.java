package org.saga.buildings.production;

import org.saga.SagaLogger;
import org.saga.config.VanillaConfiguration;


public class SagaPricedItem extends SagaItem{

	
	/**
	 * Price contents.
	 */
	protected Double price;

	/**
	 * Required work points.
	 */
	private Double reqWork;
	
	

	// Initialisation:
	/**
	 * Creates a priced item.
	 * 
	 * @param item saga item
	 * @param priced item price
	 * @param reqWork required work
	 */
	public SagaPricedItem(SagaItem item, Double price, Double reqWork){
		super(item);
		this.price = price;
		this.reqWork = reqWork;
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

		
		if(type != null && amount == null){
			amount = (double)type.getMaxStackSize();
		}
		
		super.complete();
		
		if(price == null){
			price = 0.0;
			SagaLogger.nullField(this, "price");
		}
		
		if(reqWork == null){
			reqWork = VanillaConfiguration.MINUTES_IN_MC_DAY;
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
	
	/**
	 * Gets the amount of work required.
	 * 
	 * @return amount of work required
	 */
	public Double getRequiredWork() {
		return reqWork;
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
