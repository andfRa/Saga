package org.saga.buildings.production;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.saga.SagaLogger;

public class SagaResource extends SagaRecipe {

	
	/**
	 * Resource crafting collected amount.
	 */
	private double[] collected;
	

	/**
	 * Initialises the resource from a recipe.
	 * 
	 */
	public SagaResource(SagaRecipe recipe) {
		super(recipe);
		collected = new double[recipe.recipe.length];
	}
	
	/**
	 * Fixes all initialisation problems with the recipe.
	 * 
	 */
	public void complete(){
		
		super.complete();
		
		if(collected == null){
			SagaLogger.nullField(this, "collected");
			collected = new double[recipe.length];
		}
		if(collected.length != recipe.length){
			SagaLogger.warning(this, "completion length invalid");
			collected = new double[recipe.length];
		}
		
		
	}
	
	
	
	// Offering:
	/**
	 * Offers a item for the resource.
	 * 
	 * @param item item to offer
	 */
	public void offer(SagaItem item) {
		
		for (int i = 0; i < recipe.length; i++) {
			if(checkAccept(item, i)) progress(item, i);
		}
		
	}
	
	/**
	 * Offers items for the resource.
	 * 
	 * @param items items to offer
	 */
	public void offer(List<SagaItem> items) {
		for (SagaItem item : items) {
			offer(item);
		}
	}
	
	/**
	 * Increases all percentages for free items.
	 * Free items have {@link Material#AIR} recipes.
	 * 
	 */
	public void offerFree() {
		
		for (int i = 0; i < recipe.length; i++) {
			if(recipe[i].getType() == Material.AIR) collected[i]+= 1.0;
		}
		
	}

	/* 
	 * Additional checks. 
	 * 
	 * @see org.saga.buildings.production.SagaRecipe#checkAccept(org.saga.buildings.production.SagaItem, int)
	 */
	@Override
	public boolean checkAccept(SagaItem item, int index) throws IndexOutOfBoundsException{
		
		if(collected[index] >= recipe[index].amount) return false;
		
		return recipe[index].checkRepresents(item);
		
	}
	

	/**
	 * Advances the production progress.
	 * 
	 * @param item item to use for progression
	 * @param index recipe index
	 * @throws IndexOutOfBoundsException when index is out of bounds
	 */
	private void progress(SagaItem item, int index) throws IndexOutOfBoundsException{
		
		double mod = item.amount;
		if(mod + collected[index] > recipe[index].amount) mod = recipe[index].amount - collected[index];
		
		collected[index] = collected[index] + mod;
		item.amount-= mod;
		
	}
	
	/**
	 * Counts how many items will be accepted.
	 * 
	 * @param item item to count with
	 */
	private void countAccept(SagaItem item) {

		for (int index = 0; index < recipe.length; index++) {
			
			if(!recipe[index].checkRepresents(item)) continue;
			
			item.amount+= recipe[index].amount - collected[index];
			
		}
		
	}
	
	/**
	 * Counts how many items will be accepted.
	 * 
	 * @param countItems item list to count with
	 * @param resources resources
	 */
	public static void countAccept(ArrayList<SagaItem> countItems, ArrayList<SagaResource> resources) {

		for (SagaItem sagaItem : countItems) {
			for (int r = 0; r < resources.size(); r++) {
				resources.get(r).countAccept(sagaItem);
				sagaItem.amount = Math.ceil(sagaItem.amount);
			}
			
		}
		
	}
	
	
	// Creation:
	/**
	 * Produces the item.
	 * 
	 * @return item, null if none
	 */
	public SagaItem produceItem() {
		
		// Create item:
		double percent = findProducePercentage();
		int amount = (int)(getAmount()*percent);
		if(amount < 1) return null;
		
		SagaItem item = new SagaItem(this);
		item.setAmount(amount);
		
		// Reset progress:
		for (int i = 0; i < collected.length; i++) {
			collected[i] = collected[i] - percent * recipe[i].amount;
		}
		
		return item;
		
	}
	
	/**
	 * Finds the percentage to be produced.
	 * 
	 * @return production percentage
	 */
	public double findProducePercentage() {

		// Items without a recipe will not be produced:
		if(collected.length == 0) return 0;
		
		// Find highest amount possible:
		double percent = collected[0] / recipe[0].amount;
		
		for (int i = 0; i < collected.length; i++) {
			if(collected[i]/recipe[i].amount < percent) percent = collected[i]/recipe[i].amount;
		}
		
		return (int)(percent);
		
	}
	
	
	
	
	// Other:
	/* 
	 * Show fields.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer();
		
		result.append(super.toString());
		
		result.append("{");
		for (int i = 0; i < collected.length; i++) {
			result.append(recipe[i].getType() + " " + collected[i] + "/" + recipe[i].amount);
		}
		result.append("}");
		
		return result.toString();
		
	}
    
	
	
}
