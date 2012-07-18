package org.saga.utility;

import java.util.HashSet;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import org.bukkit.inventory.ItemStack;

/**
 * 
 * 
 * {@link http://stackoverflow.com/questions/6409652/random-weighted-selection-java-framework}
 *
 */
public class RandomItemBlueprint {

	
	/**
	 * Weight and blueprint map.
	 */
	private final NavigableMap<Double, ItemBlueprint> map = new TreeMap<Double, ItemBlueprint>();

	/**
	 * Random.
	 */
	private final Random random;

	/**
	 * Weight total.
	 */
	private double total = 0;

	
	
	// Initialisation:
	/**
	 * Creates the item randomiser.
	 * 
	 */
	public RandomItemBlueprint() {

		this.random = new Random();
	
	}

	/**
	 * Creates the item randomiser.
	 * 
	 * @param blueprints item blueprints
	 */
	public RandomItemBlueprint(HashSet<ItemBlueprint> blueprints) {
		
		this();
		add(blueprints);
		
	}

	
	
	// Adding blueprints:
	/**
	 * Adds an item blueprint.
	 * 
	 * @param itemBlueprint item blueprint, zero weight blueprints ignored
	 */
	public void add(ItemBlueprint itemBlueprint) {
		
		if (itemBlueprint.getWeight() <= 0) return;
		total += itemBlueprint.getWeight();
		map.put(total, itemBlueprint);
		
	}

	/**
	 * Adds all item blueprints from the table.
	 * 
	 * @param blueprints item blueprints
	 */
	public void add(HashSet<ItemBlueprint> blueprints) {
		
		for (ItemBlueprint blueprint : blueprints) {
			add(blueprint);
		}
		
	}

	
	
	// Random items:
	/**
	 * Picks a random item blueprint
	 * 
	 * @return random item blueprint, null if none
	 */
	private ItemBlueprint nextBlueprint() {
		
		if(map.size() == 0) return null;
		
		double value = random.nextDouble() * total;
		return map.ceilingEntry(value).getValue();
		
	}
	
	/**
	 * Picks a random item blueprint and creates an item.
	 * 
	 * @return random item, null if none
	 */
	public ItemStack nextItem() {

		
		ItemBlueprint itemData = nextBlueprint();
		if(itemData == null) return null;
		
		return itemData.createItem();
		
		
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
	
	
}
