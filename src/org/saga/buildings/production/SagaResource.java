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
	 * Work progress.
	 */
	private Double work;

	
	
	/**
	 * Initialises the resource from a recipe.
	 * 
	 */
	public SagaResource(SagaRecipe recipe) {
		super(recipe);
		collected = new double[recipe.recipe.length];
		work = 0.0;
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
		
		if(work == null){
			SagaLogger.nullField(this, "progress");
			work = 0.0;
		}
		
	}
	
	
	
	// Requests:
	/**
	 * Gets the requested item amounts.
	 * 
	 * @return requested items
	 */
	public ArrayList<SagaItem> getRequests() {
		
		ArrayList<SagaItem> requests = new ArrayList<SagaItem>();
		
		for (int i = 0; i < this.recipe.length; i++) {
			
			double amount = recipe[i].getAmount() - collected[i];
			if(amount <= 0.0) continue;
			
			SagaItem request = new SagaItem(this.recipe[i]);
			request.setAmount(amount);
			requests.add(request);
			
		}
		
		return requests;
		
	}
	
	
	
	// Offering:
	/**
	 * Offers a item for the resource.
	 * 
	 * @param item item to offer
	 */
	public void offer(SagaItem item) {
		
		for (int i = 0; i < recipe.length; i++) {
			if(checkAccept(item, i)) collect(item, i);
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
			if(recipe[i].getType() == Material.AIR ) collected[i]+= 1.0;
			if(collected[i] > recipe[i].amount) collected[i] = recipe[i].amount;
		}
		
	}

	/* 
	 * Additional checks. 
	 * 
	 * @see org.saga.buildings.production.SagaRecipe#checkAccept(org.saga.buildings.production.SagaItem, int)
	 */
	@Override
	public boolean checkAccept(SagaItem item, int index) throws IndexOutOfBoundsException{
		
		if(collected[index] >= recipe[index].getAmount()) return false;
		
		return recipe[index].checkRepresents(item);
		
	}

	
	
	// Collecting:
	/**
	 * Advances the production collection progress.
	 * 
	 * @param item item to use for progression
	 * @param index recipe index
	 * @throws IndexOutOfBoundsException when index is out of bounds
	 */
	private void collect(SagaItem item, int index) throws IndexOutOfBoundsException{
		
		double mod = item.getAmount();
		if(mod + collected[index] > recipe[index].getAmount()) mod = recipe[index].getAmount() - collected[index];
		
		collected[index] = collected[index] + mod;
		item.modifyAmount(-mod);
		
	}
	
	/**
	 * Gets collected items.
	 * 
	 * @param index index
	 * @return amount collected
	 * @throws IndexOutOfBoundsException when index is out of bounds
	 */
	public double getCollected(int index) throws IndexOutOfBoundsException {
		return collected[index];
	}
	
	/**
	 * Gets the percentage for the given recipe component.
	 * 
	 * @param index index
	 * @return percent collected
	 * @throws IndexOutOfBoundsException when index is out of bounds
	 */
	public double getPercentage(int index) throws IndexOutOfBoundsException {
		return collected[index] / recipe[index].getAmount();
	}
	
	/**
	 * Gets the sum percentage.
	 * 
	 * @return sum percentage
	 */
	public double getSumPercentage() {

		if(recipe.length == 0) return 0.0;
		
		double sum = 0;
		
		for (int i = 0; i < recipeLength(); i++) {
			sum+= getPercentage(i);
		}
		
		return sum / recipe.length;
		
	}
	
	
	
	// Work:
	/**
	 * Progresses work on the resource.
	 * 
	 * @param workPoints work points
	 */
	public void work(double workPoints) {
		work+= workPoints;
	}
	
	/**
	 * Gets the amount of work points.
	 * 
	 * @return work points
	 */
	public Double getWork() {
		return work;
	}
	
	/**
	 * Gets the amount of working points remaining.
	 * 
	 * @return remaining working points
	 */
	public double getRemainingWork() {
		return (getRequiredWork() * getSumPercentage() - work);
	}
	
	
	
	// Creation:
	/**
	 * Produces the item.
	 * 
	 * @return item, null if none
	 */
	public SagaItem produceItem() {
		
		// Check work:
		if(work < getRequiredWork()) return null;
		
		// Check amount:
		double percent = findProducePercentage();
		double amount = (int)(getAmount()*percent);
		if(amount < 1) return null;
		
		// Create item:
		SagaItem item = new SagaItem(this);
		item.setAmount(amount);
		
		// Reset work:
		work = 0.0;
		
		// Reset collected:
		for (int i = 0; i < collected.length; i++) {
			collected[i] = collected[i] - percent * recipe[i].getAmount();
		}
		
		return item;
		
	}
	
	/**
	 * Finds the percentage to be produced.
	 * 
	 * @return production percentage
	 */
	public double findProducePercentage() {

		// Items without a recipe can alwasy be produced:
		if(collected.length == 0) return 1.0;
		
		// Find highest amount possible:
		double percent = collected[0] / recipe[0].getAmount();
		
		for (int i = 0; i < collected.length; i++) {
			if(collected[i]/recipe[i].getAmount() < percent) percent = collected[i]/recipe[i].getAmount();
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
			result.append(recipe[i].getType() + " " + collected[i] + "/" + recipe[i].getAmount());
		}
		result.append("}");
		
		return result.toString();
		
	}
	
	
}
