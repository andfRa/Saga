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
	
	/**
	 * Called when the player attacks.
	 * 
	 * @param event event
	 */
	public void onAttack(SagaEntityDamageEvent event) {

		for (Attribute attribute : attributes) {
			attribute.triggerAttack(event, sagaPlayer.getAttributeScore(attribute.getName()));
		}

	}
	
	/**
	 * Called when the player defends.
	 * 
	 * @param event event
	 */
	public void onDefend(SagaEntityDamageEvent event) {

		for (Attribute attribute : attributes) {
			attribute.triggerDefence(event, sagaPlayer.getAttributeScore(attribute.getName()));
		}

	}

	/**
	 * Called when the player breaks a block.
	 * 
	 * @param event event
	 */
	public void onBlockBreak(SagaBlockBreakEvent event) {

		for (Attribute attribute : attributes) {
			attribute.triggerBreak(event, sagaPlayer.getAttributeScore(attribute.getName()));
		}

	}
	
	
}
