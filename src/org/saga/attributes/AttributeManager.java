package org.saga.attributes;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.saga.config.AttributeConfiguration;
import org.saga.listeners.events.SagaBlockBreakEvent;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.player.SagaLiving;

public class AttributeManager {

	
	/**
	 * Random generator.
	 */
	private static Random RANDOM = new Random();
	
	
	/**
	 * Saga living entity.
	 */
	private SagaLiving<?> sagaLiving;
	
	/**
	 * Attributes.
	 */
	private ArrayList<Attribute> attributes;
	
	
	/**
	 * Sets saga entity and initialises.
	 * 
	 * @param sagaLiving Saga living entity
	 */
	public AttributeManager(SagaLiving<?> sagaLiving) {

		this.sagaLiving = sagaLiving;
		this.attributes = AttributeConfiguration.config().getAttributes();

	}

	
	
	// Modification:
	/**
	 * Handles attack.
	 * 
	 * @param event event
	 */
	public void handleAttack(SagaEntityDamageEvent event) {

		
		AttributeManager attackerManager = sagaLiving.getAttributeManager();
		
		// Damage:
		switch (event.type) {
			
			case MELEE:

				// Modifier:
				event.modifyDamage(attackerManager.getAttackModifier(AttributeParameter.MELEE_DAMAGE_MODIFIER));

				// Multiplier:
				event.multiplyDamage(attackerManager.getAttackModifier(AttributeParameter.MELEE_DAMAGE_MULTIPLIER));
				
				// Hit chance:
				event.modifyHitChance(attackerManager.getAttackModifier(AttributeParameter.MELEE_HIT_CHANCE_MODIFIER));

				// Armour penetration:
				event.modifyArmourPenetration(attackerManager.getAttackModifier(AttributeParameter.MELEE_ARMOUR_PENETRATION));

				// Enchant penetration:
				event.modifyEnchantPenetration(attackerManager.getAttackModifier(AttributeParameter.MELEE_ENCHANT_PENETRATION));
				
				break;

			case RANGED:

				// Modifier:
				event.modifyDamage(attackerManager.getAttackModifier(AttributeParameter.RANGED_DAMAGE_MODIFIER));

				// Multiplier:
				event.multiplyDamage(attackerManager.getAttackModifier(AttributeParameter.RANGED_DAMAGE_MULTIPLIER));
				
				// Hit chance:
				event.modifyHitChance(attackerManager.getAttackModifier(AttributeParameter.RANGED_HIT_CHANCE_MODIFIER));

				// Armour penetration:
				event.modifyArmourPenetration(attackerManager.getAttackModifier(AttributeParameter.RANGED_ARMOUR_PENETRATION));

				// Enchant penetration:
				event.modifyEnchantPenetration(attackerManager.getAttackModifier(AttributeParameter.RANGED_ENCHANT_PENETRATION));
				
				break;

			case MAGIC:

				// Modifier:
				event.modifyDamage(attackerManager.getAttackModifier(AttributeParameter.MAGIC_DAMAGE_MODIFIER));

				// Multiplier:
				event.multiplyDamage(attackerManager.getAttackModifier(AttributeParameter.MAGIC_DAMAGE_MULTIPLIER));
				
				// Hit chance:
				event.modifyHitChance(attackerManager.getAttackModifier(AttributeParameter.MAGIC_HIT_CHANCE_MODIFIER));

				// Armour penetration:
				event.modifyArmourPenetration(attackerManager.getAttackModifier(AttributeParameter.MAGIC_ARMOUR_PENETRATION));

				// Enchant penetration:
				event.modifyEnchantPenetration(attackerManager.getAttackModifier(AttributeParameter.MAGIC_ENCHANT_PENETRATION));
				
				break;
				
			default:
				
				break;
				
		}
		
		// Tool:
		event.modifyToolHandling(getToolHandlingModifier(event.tool));
		
		
	}
	
	/**
	 * Handles defend.
	 * 
	 * @param event event
	 */
	public void handleDefend(SagaEntityDamageEvent event) {

		
		AttributeManager defenderManager = sagaLiving.getAttributeManager();
		
		// Damage:
		switch (event.type) {
			
			case MELEE:

				// Modifier:
				event.modifyDamage(defenderManager.getDefendModifier(AttributeParameter.MELEE_DAMAGE_MODIFIER));
				
				// Multiplier:
				event.multiplyDamage(defenderManager.getDefendModifier(AttributeParameter.MELEE_DAMAGE_MULTIPLIER));
				
				// Hit chance:
				event.modifyHitChance(defenderManager.getDefendModifier(AttributeParameter.MELEE_HIT_CHANCE_MODIFIER));

				// Armour penetration:
				event.modifyArmourPenetration(defenderManager.getDefendModifier(AttributeParameter.MELEE_ARMOUR_PENETRATION));

				// Enchant penetration:
				event.modifyEnchantPenetration(defenderManager.getDefendModifier(AttributeParameter.MELEE_ENCHANT_PENETRATION));
				
				break;

			case RANGED:

				// Modifier:
				event.modifyDamage(defenderManager.getDefendModifier(AttributeParameter.RANGED_DAMAGE_MODIFIER));

				// Multiplier:
				event.multiplyDamage(defenderManager.getDefendModifier(AttributeParameter.RANGED_DAMAGE_MULTIPLIER));
				
				// Hit chance:
				event.modifyHitChance(defenderManager.getDefendModifier(AttributeParameter.RANGED_HIT_CHANCE_MODIFIER));

				// Armour penetration:
				event.modifyArmourPenetration(defenderManager.getDefendModifier(AttributeParameter.RANGED_ARMOUR_PENETRATION));

				// Enchant penetration:
				event.modifyEnchantPenetration(defenderManager.getDefendModifier(AttributeParameter.RANGED_ENCHANT_PENETRATION));
				
				break;

			case MAGIC:

				// Modifier:
				event.modifyDamage(defenderManager.getDefendModifier(AttributeParameter.MAGIC_DAMAGE_MODIFIER));

				// Multiplier:
				event.multiplyDamage(defenderManager.getDefendModifier(AttributeParameter.MAGIC_DAMAGE_MULTIPLIER));
				
				// Hit chance:
				event.modifyHitChance(defenderManager.getDefendModifier(AttributeParameter.MAGIC_HIT_CHANCE_MODIFIER));

				// Armour penetration:
				event.modifyArmourPenetration(defenderManager.getDefendModifier(AttributeParameter.MAGIC_ARMOUR_PENETRATION));

				// Enchant penetration:
				event.modifyEnchantPenetration(defenderManager.getDefendModifier(AttributeParameter.MAGIC_ENCHANT_PENETRATION));
				
				break;
			
			case BURN:
				
				if(randomBoolean(defenderManager.getDefendModifier(AttributeParameter.BURN_RESIST))){
					event.cancel();
					event.defenderPlayer.playGlobalEffect(Effect.EXTINGUISH, 0);
				}

				break;
				
			default:
				
				break;
				
		}
		
		
	}
	
	/**
	 * Handles block break.
	 * 
	 * @param event event
	 */
	public void handleBlockBreak(SagaBlockBreakEvent event) {

		
		AttributeManager manager = (event.sagaLiving != null) ? event.sagaLiving.getAttributeManager() : null;
		if(manager == null) return;

		// Drops:
		event.modifyExtraDrop(manager.getPassiveModifier(AttributeParameter.EXTRA_DROP_CHANCE_MODIFIER));

		// Tool:
		event.modifyToolHandling(getToolHandlingModifier(event.tool));
		
		
	}
	
	
	
	// Modifiers:
	/**
	 * Sums all attack modifiers.
	 * 
	 * @param parameter parameter
	 * @return sum of attack modifiers
	 */
	private double getAttackModifier(AttributeParameter parameter) {

		double modifier = 0.0;
		
		for (Attribute attribute : attributes) {
			modifier+= attribute.getAttackModifier(parameter, sagaLiving.getAttributeScore(attribute.getName()));
		}
		
		return modifier;
		
	}

	/**
	 * Sums all defend modifiers.
	 * 
	 * @param parameter parameter
	 * @return sum of attack modifiers
	 */
	private double getDefendModifier(AttributeParameter parameter) {

		double modifier = 0.0;
		
		for (Attribute attribute : attributes) {
			modifier+= attribute.getDefendModifier(parameter, sagaLiving.getAttributeScore(attribute.getName()));
		}
		
		return modifier;
		
	}
	
	/**
	 * Sums all passive modifiers.
	 * 
	 * @param parameter parameter
	 * @return sum of attack modifiers
	 */
	private double getPassiveModifier(AttributeParameter parameter) {

		double modifier = 0.0;
		
		for (Attribute attribute : attributes) {
			modifier+= attribute.getPassiveModifier(parameter, sagaLiving.getAttributeScore(attribute.getName()));
		}
		
		return modifier;
		
	}
	
	/**
	 * Sums all tool handling modifiers.
	 * 
	 * @param material tool material
	 * @return sum of tool handling modifiers
	 */
	private double getToolHandlingModifier(Material material) {

		double modifier = 0.0;
		
		for (Attribute attribute : attributes) {
			modifier+= attribute.getToolHandlingModifier(material, sagaLiving.getAttributeScore(attribute.getName()));
		}
		
		return modifier;
		
	}
	
	/**
	 * Gets a random boolean from a double
	 * 
	 * @param x
	 * @return true if x > random double
	 */
	private boolean randomBoolean(double x) {

		return x > RANDOM.nextDouble();
		
	}
	
	/**
	 * Gets the health modifier.
	 * 
	 * @return health modifier
	 */
	public int getHealthModifier() {

		
		double modifier = 0.0;
		
		for (Attribute attribute : attributes) {
			modifier+= attribute.getPassiveModifier(AttributeParameter.HEALTH_MODIFIER, sagaLiving.getAttributeScore(attribute.getName()));
		}
		
		return (int)modifier;
		
		
	}
	
	
}
