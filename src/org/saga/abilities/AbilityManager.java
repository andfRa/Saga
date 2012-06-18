package org.saga.abilities;

import java.util.HashSet;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.abilities.AbilityDefinition.ActivationAction;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.player.SagaPlayer;

public class AbilityManager {

	
	/**
	 * All abilities.
	 */
	private HashSet<Ability> abilities;
	
	/**
	 * Saga player.
	 */
	private SagaPlayer sagaPlayer;
	
	
	// Initialisation:
	/**
	 * Sets player.
	 * 
	 * @param sagaPlayer saga player
	 */
	public AbilityManager(SagaPlayer sagaPlayer) {

		
		this.sagaPlayer = sagaPlayer;
		abilities = new HashSet<Ability>();
		
		update();
		

	}
	
	/**
	 * Updates abilities.
	 * 
	 */
	public void update() {

		
		HashSet<Ability> allAbilities = sagaPlayer.getAbilities();
		abilities = new HashSet<Ability>();
		
		// Only add abilities if the requirements are met:
		for (Ability ability : allAbilities) {
			
			if(ability.getEffectiveScore() > 0){
				abilities.add(ability);
			}
			
		}
		
		
		

	}

	/**
	 * Called when a player interacts with a block.
	 * 
	 * @param event event
	 */
	public void onInteract(PlayerInteractEvent event) {

		
		ActivationAction action = null;
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
			
			action = ActivationAction.LEFT_CLICK;
			
		}else if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
			
			action = ActivationAction.RIGHT_CLICK;
			
		}
		if(action == null) return;
		
		for (Ability ability : abilities) {
			
			if(ability.getDefinition().getActivationAction() != action) continue;
			
			if(ability.handlePreTrigger()){
				
				if(ability.trigger(event)) ability.handleAfterTrigger();
				
			}
			
		}
		

	}
	
	/**
	 * Called when the player attacks.
	 * 
	 * @param event event
	 */
	public void onAttack(SagaEntityDamageEvent event) {

		
		for (Ability ability : abilities) {
			
			if(ability.handlePreTrigger()){
				
				if(ability.triggerAttack(event)) ability.handleAfterTrigger();
				
			}
			
		}
		

	}
	
	/**
	 * Called when the player defends.
	 * 
	 * @param event event
	 */
	public void onDefend(SagaEntityDamageEvent event) {

		
		for (Ability ability : abilities) {
			
			if(ability.handlePreTrigger()){
				
				if(ability.triggerDefend(event)) ability.handleAfterTrigger();
				
			}
			
		}
		

	}
	
	
	// Getters:
	/**
	 * Gets all abilities.
	 * 
	 * @return all abilities
	 */
	public HashSet<Ability> getAbilities() {
		return new HashSet<Ability>(abilities);
	}
	
}
