package org.saga.buildings.production;


public class SagaRecipe extends SagaItem{

	
	/**
	 * Recipe contents.
	 */
	protected SagaItem[] recipe;
	
	
	
	/**
	 * Creates a recipe.
	 * 
	 * @param item craft item
	 * @param recipe recipe
	 */
	public SagaRecipe(SagaItem item, SagaItem[] recipe){
		super(item);
		this.recipe = recipe.clone();
	}
	
	/**
	 * Creates a copy, based on another recipe.
	 * 
	 * @param recipe recipe
	 */
	protected SagaRecipe(SagaRecipe recipe) {
		super(recipe);
		this.recipe = recipe.recipe.clone();
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
			
			return true;
			
		}
		
		return false;
		
	}
	
	
}
