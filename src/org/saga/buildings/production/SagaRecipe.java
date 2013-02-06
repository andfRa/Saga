package org.saga.buildings.production;

import org.saga.config.VanillaConfiguration;


public class SagaRecipe extends SagaItem{
	
	
	/**
	 * Recipe contents.
	 */
	protected SagaItem[] recipe;
	
	/**
	 * Required work points.
	 */
	private Double reqWork;
	
	
	/**
	 * Creates a recipe.
	 * 
	 * @param item saga item
	 * @param recipe recipe
	 * @param reqWork required work
	 */
	public SagaRecipe(SagaItem item, SagaItem[] recipe, Double reqWork){
		super(item);
		this.recipe = recipe.clone();
		this.reqWork = reqWork;
	}
	
	/**
	 * Creates a copy, based on another recipe.
	 * 
	 * @param recipe recipe
	 */
	protected SagaRecipe(SagaRecipe recipe) {
		super(recipe);
		this.recipe = recipe.recipe.clone();
		this.reqWork = recipe.reqWork;
	}
	
	/**
	 * Fixes all initialisation problems with the recipe.
	 * 
	 */
	public void complete(){
		
		super.complete();
		
		if(recipe == null){
			recipe = new SagaItem[0];
		}
		
		if(reqWork == null){
			reqWork = VanillaConfiguration.MINUTES_IN_MC_DAY;
		}
		
		for (int i = 0; i < recipe.length; i++) {
			recipe[i].complete();
		}
		
	}
	
	
	
	// Operation:
	/**
	 * Checks if the recipe accepts a given item.
	 * 
	 * @param item item
	 * @param index recipe index
	 * @return true if accepts
	 * @throws IndexOutOfBoundsException when the index is out of bounds
	 */
	public boolean checkAccept(SagaItem item, int index) throws IndexOutOfBoundsException{
		return item.equalsItem(recipe[index]);
	}
	
	/**
	 * Gets a recipe component.
	 * 
	 * @param index element index
	 * @return recipe component
	 * @throws IndexOutOfBoundsException when index is out of bounds
	 */
	public SagaItem getComponent(int index) throws IndexOutOfBoundsException {
		return recipe[index];
	}
	
	/**
	 * Gets the recipe length.
	 * 
	 * @return recipe length
	 */
	public int recipeLength() {
		return recipe.length;
	}
	
	/**
	 * Gets the amount of work points required.
	 * 
	 * @return work points
	 */
	public Double getRequiredWork() {
		return reqWork;
	}
	
	
	
	// Other:
	/* 
	 * Checks the recipe.
	 * 
	 * @see org.saga.buildings.production.SagaItem#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		return equalsRecipe(obj);
		
	}
	
	/**
	 * Checks if the two recipes are equal
	 * 
	 * @param obj recipe
	 * @return true if equal
	 */
	boolean equalsRecipe(Object obj) {

		if(!equalsItem(obj)) return false;
		
		if(obj instanceof SagaRecipe){
			
			SagaRecipe recipe = (SagaRecipe) obj;
			
			if(recipe.recipe.length != this.recipe.length) return false;
			
			for (int i = 0; i < this.recipe.length; i++) {
				if(!recipe.recipe[i].equalsItem(this.recipe[i])) return false;
			}
			
			if(!recipe.reqWork.equals(this.reqWork)) return false;
			
			return true;
			
		}
		
		return false;
		
	}
	
	
}
