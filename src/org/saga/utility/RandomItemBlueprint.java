package org.saga.utility;

import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Random;
import java.util.Set;
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
	 * @param weigtedBlueprints item blueprints with weights
	 */
	public RandomItemBlueprint(Hashtable<Double, ItemBlueprint> weigtedBlueprints) {
		
		this();
		add(weigtedBlueprints);
		
	}

	
	
	// Adding blueprints:
	/**
	 * Adds an item blueprint.
	 * 
	 * @param weight item probability weight
	 * @param itemBlueprint item blueprint
	 */
	public void add(double weight, ItemBlueprint itemBlueprint) {
		
		if (weight <= 0) return;
		total += weight;
		map.put(total, itemBlueprint);
		
	}

	/**
	 * Adds all item blueprints from the table.
	 * 
	 * @param weigtBlueprints item blueprints and corresponding weights
	 * @param xVal function x value
	 */
	public void add(Hashtable<Double, ItemBlueprint> weigtedBlueprints) {
		
		Set<Entry<Double, ItemBlueprint>> entries = weigtedBlueprints.entrySet();
		
		for (Entry<Double, ItemBlueprint> entry : entries) {
			
			add(entry.getKey(), entry.getValue());
			
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
