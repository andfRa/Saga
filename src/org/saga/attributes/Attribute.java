package org.saga.attributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map.Entry;

import org.bukkit.Effect;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.saga.SagaLogger;
import org.saga.listeners.events.SagaBlockBreakEvent;
import org.saga.listeners.events.SagaEntityDamageEvent;
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
	 * Block break scores.
	 */
	private Hashtable<AttributeParameter, TwoPointFunction> blockBreak;
	
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
		blockBreak = new Hashtable<AttributeParameter, TwoPointFunction>();
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
		
		if(blockBreak == null){
			blockBreak = new Hashtable<AttributeParameter, TwoPointFunction>();
			SagaLogger.nullField(this, "blockBreak");
		}
		
		scores = blockBreak.values();
		for (TwoPointFunction twoPointFunction : scores) {
			twoPointFunction.complete();
		}
		
		if(description == null){
			description = "";
			SagaLogger.nullField(this, "description");
		}
		
		
	}
	
	
	// Attribute triggers:
	/**
	 * Triggered when the player attacks.
	 * 
	 * @param event entity damage event
	 * @param score attribute score
	 */
	public void triggerAttack(SagaEntityDamageEvent event, Integer score) {

		
		TwoPointFunction function = null;
		
		
		// Physical:
		if(event.isPhysical()){
			
			// Physical damage:
			function = attack.get(AttributeParameter.MELEE_MODIFIER);
			if(function != null){
				event.modifyDamage(function.value(score));
			}

			// Physical multiplier:
			function = attack.get(AttributeParameter.MELEE_MULTIPLIER);
			if(function != null){
				event.multiplyDamage(function.value(score));
			}
			
			// Hit chance:
			function = attack.get(AttributeParameter.MELEE_HIT_CHANCE);
			if(function != null){
				event.modifyHitChance(function.value(score));
			}

			// Armour penetration:
			function = attack.get(AttributeParameter.MELEE_ARMOUR_PENETRATION);
			if(function != null){
				event.modifyArmourPenetration(function.value(score));
			}
			
		}

		// Ranged:
		else if(event.isRanged()){

			// Ranged damage:
			function = attack.get(AttributeParameter.RANGED_MODIFIER);
			if(function != null){
				event.modifyDamage(function.value(score));
			}

			// Ranged multiplier:
			function = attack.get(AttributeParameter.RANGED_MULTIPLIER);
			if(function != null){
				event.multiplyDamage(function.value(score));
			}
			
			// Hit chance:
			function = attack.get(AttributeParameter.RANGED_HIT_CHANCE);
			if(function != null){
				event.modifyHitChance(function.value(score));
			}

			// Armour penetration:
			function = attack.get(AttributeParameter.RANGED_ARMOUR_PENETRATION);
			if(function != null){
				event.modifyArmourPenetration(function.value(score));
			}
			
		}
		
		// Magic:
		if(event.isMagic()){

			// Magic damage:
			function = attack.get(AttributeParameter.MAGIC_MODIFIER);
			if(function != null){
				event.modifyDamage(function.value(score));
			}

			// Magic multiplier:
			function = attack.get(AttributeParameter.MAGIC_MULTIPLIER);
			if(function != null){
				event.multiplyDamage(function.value(score));
			}
			
			// Hit chance:
			function = attack.get(AttributeParameter.MAGIC_HIT_CHANCE);
			if(function != null){
				event.modifyHitChance(function.value(score));
			}

			// Armour penetration:
			function = attack.get(AttributeParameter.MAGIC_ARMOUR_PENETRATION);
			if(function != null){
				event.modifyArmourPenetration(function.value(score));
			}
			
		}
		

	}
	
	/**
	 * Triggered when the player defends.
	 * 
	 * @param event entity damage event
	 * @param score attribute score
	 */
	public void triggerDefence(SagaEntityDamageEvent event, Integer score) {

		
		TwoPointFunction function = null;
		

		// Physical:
		if(event.isPhysical()){

			// Physical damage:
			function = defend.get(AttributeParameter.MELEE_MODIFIER);
			if(function != null){
				event.modifyDamage(function.value(score));
			}

			// Physical multiplier:
			function = defend.get(AttributeParameter.MELEE_MULTIPLIER);
			if(function != null){
				event.multiplyDamage(function.value(score));
			}

			// Hit chance:
			function = defend.get(AttributeParameter.MELEE_HIT_CHANCE);
			if(function != null){
				event.modifyHitChance(function.value(score));
			}

			// Armour penetration:
			function = defend.get(AttributeParameter.MELEE_ARMOUR_PENETRATION);
			if(function != null){
				event.modifyArmourPenetration(function.value(score));
			}
			
		}

		// Ranged:
		else if(event.isRanged()){

			// Ranged damage:
			function = defend.get(AttributeParameter.RANGED_MODIFIER);
			if(function != null){
				event.modifyDamage(function.value(score));
			}

			// Ranged multiplier:
			function = defend.get(AttributeParameter.RANGED_MULTIPLIER);
			if(function != null){
				event.multiplyDamage(function.value(score));
			}

			// Hit chance:
			function = defend.get(AttributeParameter.RANGED_HIT_CHANCE);
			if(function != null){
				event.modifyHitChance(function.value(score));
			}

			// Armour penetration:
			function = defend.get(AttributeParameter.RANGED_ARMOUR_PENETRATION);
			if(function != null){
				event.modifyArmourPenetration(function.value(score));
			}
			
		}
		
		// Magic:
		if(event.isMagic()){

			// Magic damage:
			function = defend.get(AttributeParameter.MAGIC_MODIFIER);
			if(function != null){
				event.modifyDamage(function.value(score));
			}

			// Magic multiplier:
			function = defend.get(AttributeParameter.MAGIC_MULTIPLIER);
			if(function != null){
				event.multiplyDamage(function.value(score));
			}

			// Hit chance:
			function = defend.get(AttributeParameter.MAGIC_HIT_CHANCE);
			if(function != null){
				event.modifyHitChance(function.value(score));
			}

			// Armour penetration:
			function = defend.get(AttributeParameter.MAGIC_ARMOUR_PENETRATION);
			if(function != null){
				event.modifyArmourPenetration(function.value(score));
			}
			
		}
		
		// Burn resist:
		if(event.getType() == DamageCause.FIRE_TICK){
			
			function = defend.get(AttributeParameter.BURN_RESIST);
			if(function != null && function.randomBooleanValue(score)){
				event.cancel();
				event.getDefenderPlayer().playGlobalEffect(Effect.EXTINGUISH, 0);
				return;
			}
			
		}
		

	}

	/**
	 * Triggers when a block is broken
	 * 
	 * @param event event
	 * @param score attribute score
	 */
	public void triggerBreak(SagaBlockBreakEvent event, Integer score) {


		TwoPointFunction function = null;
		
		// Drop modifier:
		function = blockBreak.get(AttributeParameter.DROP_MODIFIER);
		if(function != null){
			event.modifyDrops(function.value(score));
		}


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
		entries.addAll(blockBreak.entrySet());
		
		return entries;
		
	}
	
	
	
	// Other:
	@Override
	public String toString() {
		return getName();
	}
	
	


}
