package org.saga.attributes;

import java.util.Collection;
import java.util.Hashtable;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.listeners.events.SagaBlockBreakEvent;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.player.SagaPlayer;
import org.saga.utility.TwoPointFunction;

public class Attribute {


	/**
	 * Name.
	 */
	private String name;
	
	/**
	 * Attack scores.
	 */
	private Hashtable<DamageModifiers, TwoPointFunction> attack;

	/**
	 * Defence scores.
	 */
	private Hashtable<DamageModifiers, TwoPointFunction> defend;

	/**
	 * Block break scores.
	 */
	private Hashtable<BlockModifiers, TwoPointFunction> blockBreak;
	
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
		attack = new Hashtable<DamageModifiers, TwoPointFunction>();
		defend = new Hashtable<DamageModifiers, TwoPointFunction>();
		blockBreak = new Hashtable<BlockModifiers, TwoPointFunction>();
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
			attack = new Hashtable<DamageModifiers, TwoPointFunction>();
			SagaLogger.nullField(this, "attack");
		}
		Collection<TwoPointFunction> scores = attack.values();
		for (TwoPointFunction twoPointFunction : scores) {
			twoPointFunction.complete();
		}
		
		if(defend == null){
			defend = new Hashtable<DamageModifiers, TwoPointFunction>();
			SagaLogger.nullField(this, "defend");
		}
		scores = defend.values();
		for (TwoPointFunction twoPointFunction : scores) {
			twoPointFunction.complete();
		}
		
		if(blockBreak == null){
			blockBreak = new Hashtable<BlockModifiers, TwoPointFunction>();
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
			function = attack.get(DamageModifiers.MELEE_MODIFIER);
			if(function != null){
				event.modifyDamage(function.value(score));
			}

			// Physical multiplier:
			function = attack.get(DamageModifiers.MELEE_MULTIPLIER);
			if(function != null){
				event.multiplyDamage(function.value(score));
			}
			
			// Hit chance:
			function = attack.get(DamageModifiers.MELEE_HIT_CHANCE);
			if(function != null){
				event.modifyHitChance(function.value(score));
			}

			// Armour penetration:
			function = attack.get(DamageModifiers.MELEE_ARMOUR_PENETRATION);
			if(function != null){
				event.modifyArmourPenetration(function.value(score));
			}
			
		}

		// Ranged:
		else if(event.isRanged()){

			// Ranged damage:
			function = attack.get(DamageModifiers.RANGED_MODIFIER);
			if(function != null){
				event.modifyDamage(function.value(score));
			}

			// Ranged multiplier:
			function = attack.get(DamageModifiers.RANGED_MULTIPLIER);
			if(function != null){
				event.multiplyDamage(function.value(score));
			}
			
			// Hit chance:
			function = attack.get(DamageModifiers.RANGED_HIT_CHANCE);
			if(function != null){
				event.modifyHitChance(function.value(score));
			}

			// Armour penetration:
			function = attack.get(DamageModifiers.RANGED_ARMOUR_PENETRATION);
			if(function != null){
				event.modifyArmourPenetration(function.value(score));
			}
			
		}
		
		// Magic:
		if(event.isMagic()){

			// Magic damage:
			function = attack.get(DamageModifiers.MAGIC_MODIFIER);
			if(function != null){
				event.modifyDamage(function.value(score));
			}

			// Magic multiplier:
			function = attack.get(DamageModifiers.MAGIC_MULTIPLIER);
			if(function != null){
				event.multiplyDamage(function.value(score));
			}
			
			// Hit chance:
			function = attack.get(DamageModifiers.MAGIC_HIT_CHANCE);
			if(function != null){
				event.modifyHitChance(function.value(score));
			}

			// Armour penetration:
			function = attack.get(DamageModifiers.MAGIC_ARMOUR_PENETRATION);
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
			function = defend.get(DamageModifiers.MELEE_MODIFIER);
			if(function != null){
				event.modifyDamage(function.value(score));
			}

			// Physical multiplier:
			function = defend.get(DamageModifiers.MELEE_MULTIPLIER);
			if(function != null){
				event.multiplyDamage(function.value(score));
			}

			// Hit chance:
			function = defend.get(DamageModifiers.MELEE_HIT_CHANCE);
			if(function != null){
				event.modifyHitChance(function.value(score));
			}

			// Armour penetration:
			function = defend.get(DamageModifiers.MELEE_ARMOUR_PENETRATION);
			if(function != null){
				event.modifyArmourPenetration(function.value(score));
			}
			
		}

		// Ranged:
		else if(event.isRanged()){

			// Ranged damage:
			function = defend.get(DamageModifiers.RANGED_MODIFIER);
			if(function != null){
				event.modifyDamage(function.value(score));
			}

			// Ranged multiplier:
			function = defend.get(DamageModifiers.RANGED_MULTIPLIER);
			if(function != null){
				event.multiplyDamage(function.value(score));
			}

			// Hit chance:
			function = defend.get(DamageModifiers.RANGED_HIT_CHANCE);
			if(function != null){
				event.modifyHitChance(function.value(score));
			}

			// Armour penetration:
			function = defend.get(DamageModifiers.RANGED_ARMOUR_PENETRATION);
			if(function != null){
				event.modifyArmourPenetration(function.value(score));
			}
			
		}
		
		// Magic:
		if(event.isMagic()){

			// Magic damage:
			function = defend.get(DamageModifiers.MAGIC_MODIFIER);
			if(function != null){
				event.modifyDamage(function.value(score));
			}

			// Magic multiplier:
			function = defend.get(DamageModifiers.MAGIC_MULTIPLIER);
			if(function != null){
				event.multiplyDamage(function.value(score));
			}

			// Hit chance:
			function = defend.get(DamageModifiers.MAGIC_HIT_CHANCE);
			if(function != null){
				event.modifyHitChance(function.value(score));
			}

			// Armour penetration:
			function = defend.get(DamageModifiers.MAGIC_ARMOUR_PENETRATION);
			if(function != null){
				event.modifyArmourPenetration(function.value(score));
			}
			
		}
		
		// Burn resist:
		if(event.getType() == DamageCause.FIRE_TICK){
			
			function = defend.get(DamageModifiers.BURN_IGNORE_CHANCE);
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
		function = blockBreak.get(BlockModifiers.DROP_MODIFIER);
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

	
	
	
	// Other:
	@Override
	public String toString() {
		return getName();
	}
	
	


}
