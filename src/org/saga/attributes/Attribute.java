package org.saga.attributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.saga.SagaLogger;
import org.saga.utility.TwoPointFunction;

public class Attribute {


	/**
	 * Name.
	 */
	private String name;
	
	/**
	 * Attack scores.
	 */
	private Hashtable<AttributeParameter, TwoPointFunction> attack;

	/**
	 * Defence scores.
	 */
	private Hashtable<AttributeParameter, TwoPointFunction> defend;

	/**
	 * Passive scores.
	 */
	private Hashtable<AttributeParameter, TwoPointFunction> passive;

	/**
	 * Tool handling scores.
	 */
	private Hashtable<Material, TwoPointFunction> handling;
	
	
	/**
	 * Description.
	 */
	private String description;
	
	
	/**
	 * Initialises the attribute.
	 * 
	 * @param name name
	 */
	public Attribute(String name) {
		
		this.name = name;
		attack = new Hashtable<AttributeParameter, TwoPointFunction>();
		defend = new Hashtable<AttributeParameter, TwoPointFunction>();
		passive = new Hashtable<AttributeParameter, TwoPointFunction>();
		handling = new Hashtable<Material, TwoPointFunction>();
		description = "";
		
	}
	
	/**
	 * Completes the attribute.
	 * 
	 * @return integrity
	 */
	public void complete() {

		
		if(name == null){
			name = "none";
			SagaLogger.nullField(this, "name");
		}
		
		if(attack == null){
			attack = new Hashtable<AttributeParameter, TwoPointFunction>();
			SagaLogger.nullField(this, "attack");
		}
		Collection<TwoPointFunction> scores = attack.values();
		for (TwoPointFunction twoPointFunction : scores) {
			twoPointFunction.complete();
		}
		
		if(defend == null){
			defend = new Hashtable<AttributeParameter, TwoPointFunction>();
			SagaLogger.nullField(this, "defend");
		}
		scores = defend.values();
		for (TwoPointFunction twoPointFunction : scores) {
			twoPointFunction.complete();
		}

		if(passive == null){
			passive = new Hashtable<AttributeParameter, TwoPointFunction>();
			SagaLogger.nullField(this, "passive");
		}
		scores = passive.values();
		for (TwoPointFunction twoPointFunction : scores) {
			twoPointFunction.complete();
		}

		if(handling == null){
			handling = new Hashtable<Material, TwoPointFunction>();
			SagaLogger.nullField(this, "handling");
		}
		scores = handling.values();
		for (TwoPointFunction twoPointFunction : scores) {
			twoPointFunction.complete();
		}
		
		if(description == null){
			description = "";
			SagaLogger.nullField(this, "description");
		}
		
		
	}
	
	
	
	// Modifiers:
	/**
	 * Get the attack modifier for the given parameter.
	 * 
	 * @param parameter parameter
	 * @param score attribute score
	 * @return parameter modifier, 0 if none
	 */
	public double getAttackModifier(AttributeParameter parameter, Integer score) {

		TwoPointFunction function = attack.get(parameter);
		if(function == null) return 0.0;
		
		return function.value(score);
		
	}
	
	/**
	 * Get the attack modifier for the given parameter.
	 * 
	 * @param parameter parameter
	 * @param score attribute score
	 * @return parameter modifier, 0 if none
	 */
	public double getDefendModifier(AttributeParameter parameter, Integer score) {

		TwoPointFunction function = defend.get(parameter);
		if(function == null) return 0.0;
		
		return function.value(score);
		
	}
	
	/**
	 * Get the passive modifier for the given parameter.
	 * 
	 * @param parameter parameter
	 * @param score attribute score
	 * @return parameter modifier, 0 if none
	 */
	public double getPassiveModifier(AttributeParameter parameter, Integer score) {

		TwoPointFunction function = passive.get(parameter);
		if(function == null) return 0.0;
		
		return function.value(score);
		
	}

	/**
	 * Get the tool handling modifier for the given parameter.
	 * 
	 * @param material tool material
	 * @param score attribute score
	 * @return tool handling modifier, 0 if none
	 */
	public double getToolHandlingModifier(Material material, Integer score) {

		TwoPointFunction function = handling.get(material);
		if(function == null) return 0.0;
		
		return function.value(score);
		
	}
	
	
	
	// Getters:
	/**
	 * Gets attribute name.
	 * 
	 * @return attribute name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets attack entry set.
	 * 
	 * @return attack entry set
	 */
	public ArrayList<Entry<AttributeParameter, TwoPointFunction>> getAllEntries() {
		
		ArrayList<Entry<AttributeParameter, TwoPointFunction>> entries = new ArrayList<Entry<AttributeParameter,TwoPointFunction>>();
		
		entries.addAll(attack.entrySet());
		entries.addAll(defend.entrySet());
		entries.addAll(passive.entrySet());
		
		return entries;
		
	}
	
	
	
	// Other:
	@Override
	public String toString() {
		return getName();
	}
	
	


}
