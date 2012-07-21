package org.saga.utility.items;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.TreeMap;

/**
 * 
 * 
 * {@link http://stackoverflow.com/questions/6409652/random-weighted-selection-java-framework}
 *
 */
public class RandomRecipe {

	
	/**
	 * Weight and blueprint map.
	 */
	private TreeMap<Double, RecepieBlueprint> map = new TreeMap<Double, RecepieBlueprint>();

	/**
	 * Random.
	 */
	private Random random;

	/**
	 * Weight total.
	 */
	private double total = 0;

	
	
	// Initialisation:
	/**
	 * Creates the item randomiser.
	 * 
	 */
	public RandomRecipe() {

		this.random = new Random();
	
	}

	/**
	 * Creates the item randomiser.
	 * 
	 * @param recipes recipes
	 */
	public RandomRecipe(HashSet<RecepieBlueprint> recipes) {
		
		this();
		add(recipes);
		
	}

	
	
	// Adding blueprints:
	/**
	 * Adds an recipe.
	 * 
	 * @param recipe recipe, zero weight blueprints ignored
	 */
	public void add(RecepieBlueprint recipe) {
		
		if (recipe.getWeight() <= 0) return;
		total += recipe.getWeight();
		map.put(total, recipe);
		
	}

	/**
	 * Adds all recipes from the table.
	 * 
	 * @param recipies recipes
	 */
	public void add(HashSet<RecepieBlueprint> recipies) {
		
		for (RecepieBlueprint blueprint : recipies) {
			add(blueprint);
		}
		
	}

	/**
	 * Removes a recipe.
	 * 
	 * @param recipe recipe to remove
	 */
	public void remove(RecepieBlueprint recipe) {
		
		
		TreeMap<Double, RecepieBlueprint> oldMap = this.map;
		map = new TreeMap<Double, RecepieBlueprint>();
		
		// Add all values again:
		Collection<RecepieBlueprint> values = oldMap.values();
		for (RecepieBlueprint recepie : values) {
			
			if(recepie == recipe) continue;
			add(recepie);
			
		}
		
		
	}
	
	
	// Random items:
	/**
	 * Picks a random recipe
	 * 
	 * @return random recipe, null if none
	 */
	public RecepieBlueprint nextRecipe() {
		
		if(map.size() == 0) return null;
		
		double value = random.nextDouble() * total;
		return map.ceilingEntry(value).getValue();
		
	}
	

	
	// Other:
	/**
	 * Gets the amount of possible items.
	 * 
	 * @return amount of items
	 */
	public int size() {
		return map.size();
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected RandomRecipe clone() throws CloneNotSupportedException {

		RandomRecipe result = new RandomRecipe();
		result.map = new TreeMap<Double, RecepieBlueprint>(this.map);
		result.random = this.random;
		result.total = this.total;
		
		return result;
	
	}
	
	
}
