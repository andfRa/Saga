package org.saga.abilities;

import java.util.HashSet;

import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.saga.abilities.AbilityDefinition.ActivationAction;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.player.SagaLiving;

public class AbilityManager {

	
	/**
	 * All abilities.
	 */
	private HashSet<Ability> abilities;
	
	/**
	 * Saga living entity.
	 */
	private SagaLiving<?> sagaLiving;
	
	
	
	// Initialisation:
	/**
	 * Sets entity.
	 * 
	 * @param sagaLiving saga living entity
	 */
	public AbilityManager(SagaLiving<?> sagaLiving) {

		
		this.sagaLiving = sagaLiving;
		abilities = new HashSet<Ability>();
		
		update();
		

	}
	
	/**
	 * Updates abilities.
	 * 
	 */
	public void update() {

		
		HashSet<Ability> allAbilities = sagaLiving.getAbilities();
		abilities = new HashSet<Ability>();
		
		// Only add abilities if the requirements are met:
		for (Ability ability : allAbilities) {
			
			if(ability.getScore() > 0){
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
			
			if(ability.handleInteractPreTrigger(event)){
				
				if(ability.triggerInteract(event)) ability.handleAfterTrigger();
				
			}
			
		}
		

	}
	
	/**
	 * Called when the entity attacks.
	 * 
	 * @param event event
	 */
	public void onAttack(SagaEntityDamageEvent event) {

		
		for (Ability ability : abilities) {
			
			if(ability.handleAttackPreTrigger(event)){
				
				if(ability.triggerAttack(event)) ability.handleAfterTrigger();
				
			}
			
		}
		

	}
	
	/**
	 * Called when the entity defends.
	 * 
	 * @param event event
	 */
	public void onDefend(SagaEntityDamageEvent event) {

		
		for (Ability ability : abilities) {
			
			if(ability.handleDefendPreTrigger(event)){
				
				if(ability.triggerDefend(event)) ability.handleAfterTrigger();
				
			}
			
		}
		

	}
	
	/**
	 * Called when the entity attacks.
	 * 
	 * @param event event
	 */
	public void onProjectileHit(ProjectileHitEvent event) {

		
		for (Ability ability : abilities) {
			
			if(ability.handleProjectileHitPreTrigger(event)){
				
				if(ability.triggerProjectileHit(event)) ability.handleAfterTrigger();
				
			}
			
		}
		

	}
	
	/**
	 * Called when the player shears an entity.
	 * 
	 * @param event event
	 */
	public void onShear(PlayerShearEntityEvent event) {

		for (Ability ability : abilities) {
			
			if(ability.handleShearPreTrigger(event)){
				
				if(ability.triggerShear(event)) ability.handleAfterTrigger();
				
			}
			
		}

	}

	/**
	 * Called when the player gets targeted.
	 * 
	 * @param event event
	 */
	public void onTargeted(EntityTargetEvent event) {

		for (Ability ability : abilities) {
			
			if(ability.handleTargetedPreTrigger(event)){
				
				if(ability.triggerTargeted(event)) ability.handleAfterTrigger();
				
			}
			
		}

	}
	
	/**
	 * Called when the hunger level changes.
	 * 
	 * @param event event
	 */
	public void onFoodLevelChange(FoodLevelChangeEvent event) {

		for (Ability ability : abilities) {
			
			if(ability.handleFoodLevelChangePreTrigger(event)){
				
				if(ability.triggerFoodLevelChange(event)) ability.handleAfterTrigger();
				
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
