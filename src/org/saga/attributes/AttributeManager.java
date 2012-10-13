package org.saga.attributes;

import java.util.ArrayList;

import org.saga.config.AttributeConfiguration;
import org.saga.listeners.events.SagaBlockBreakEvent;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.player.SagaPlayer;

public class AttributeManager {

	/**
	 * Saga player.
	 */
	private SagaPlayer sagaPlayer;
	
	/**
	 * Attributes.
	 */
	private ArrayList<Attribute> attributes;
	
	
	/**
	 * Sets saga player and initialises.
	 * 
	 * @param sagaPlayer
	 */
	public AttributeManager(SagaPlayer sagaPlayer) {

		this.sagaPlayer = sagaPlayer;
		this.attributes = AttributeConfiguration.config().getAttributes();

	}

	
	
	// Modification:
	/**
	 * Handles attack.
	 * 
	 * @param event event
	 */
	public void handleAttack(SagaEntityDamageEvent event) {

		
		AttributeManager attackerManager = sagaPlayer.getAttributeManager();
		
		switch (event.type) {
			
			case MELEE:

				// Modifier:
				event.modifyDamage(attackerManager.getAttackModifier(AttributeParameter.MELEE_MODIFIER));
				
				// Hit chance:
				event.modifyHitChance(attackerManager.getAttackModifier(AttributeParameter.MELEE_MODIFIER));

				// Hit chance:
				event.modifyArmourPenetration(attackerManager.getAttackModifier(AttributeParameter.MELEE_ARMOUR_PENETRATION));
				
				break;

			case RANGED:

				// Modifier:
				event.modifyDamage(attackerManager.getAttackModifier(AttributeParameter.RANGED_MODIFIER));
				
				// Hit chance:
				event.modifyHitChance(attackerManager.getAttackModifier(AttributeParameter.RANGED_MODIFIER));

				// Hit chance:
				event.modifyArmourPenetration(attackerManager.getAttackModifier(AttributeParameter.RANGED_ARMOUR_PENETRATION));
				
				break;

			case MAGIC:

				// Modifier:
				event.modifyDamage(attackerManager.getAttackModifier(AttributeParameter.MAGIC_MODIFIER));
				
				// Hit chance:
				event.modifyHitChance(attackerManager.getAttackModifier(AttributeParameter.MAGIC_MODIFIER));

				// Hit chance:
				event.modifyArmourPenetration(attackerManager.getAttackModifier(AttributeParameter.MAGIC_ARMOUR_PENETRATION));
				
				break;
				
			default:
				
				break;
				
		}
		
		
	}
	
	/**
	 * Handles defend.
	 * 
	 * @param event event
	 */
	public void handleDefend(SagaEntityDamageEvent event) {

		
		AttributeManager defenderManager = sagaPlayer.getAttributeManager();
		
		switch (event.type) {
			
			case MELEE:

				// Modifier:
				event.modifyDamage(defenderManager.getDefendModifier(AttributeParameter.MELEE_MODIFIER));
				
				// Hit chance:
				event.modifyHitChance(defenderManager.getDefendModifier(AttributeParameter.MELEE_MODIFIER));

				// Hit chance:
				event.modifyArmourPenetration(defenderManager.getDefendModifier(AttributeParameter.MELEE_ARMOUR_PENETRATION));
				
				break;

			case RANGED:

				// Modifier:
				event.modifyDamage(defenderManager.getDefendModifier(AttributeParameter.RANGED_MODIFIER));
				
				// Hit chance:
				event.modifyHitChance(defenderManager.getDefendModifier(AttributeParameter.RANGED_MODIFIER));

				// Hit chance:
				event.modifyArmourPenetration(defenderManager.getDefendModifier(AttributeParameter.RANGED_ARMOUR_PENETRATION));
				
				break;

			case MAGIC:

				// Modifier:
				event.modifyDamage(defenderManager.getDefendModifier(AttributeParameter.MAGIC_MODIFIER));
				
				// Hit chance:
				event.modifyHitChance(defenderManager.getDefendModifier(AttributeParameter.MAGIC_MODIFIER));

				// Hit chance:
				event.modifyArmourPenetration(defenderManager.getDefendModifier(AttributeParameter.MAGIC_ARMOUR_PENETRATION));
				
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

		
		AttributeManager manager = (event.sagaPlayer != null) ? event.sagaPlayer.getAttributeManager() : null;
		if(manager == null) return;

		// Modifier:
		event.modifyDrops(manager.getPassiveModifier(AttributeParameter.DROP_MODIFIER));
		
		
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
			modifier+= attribute.getAttackModifier(parameter, sagaPlayer.getAttributeScore(attribute.getName()));
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
			modifier+= attribute.getDefendModifier(parameter, sagaPlayer.getAttributeScore(attribute.getName()));
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
			modifier+= attribute.getPassiveModifier(parameter, sagaPlayer.getAttributeScore(attribute.getName()));
		}
		
		return modifier;
		
	}
	
	
}
