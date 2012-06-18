package org.saga.player;

import java.util.Random;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class SagaEntityDamageManager {

	/**
	 * Random generator.
	 */
	private static Random random = new Random();
	
	
	/**
	 * Handles magic damage.
	 * 
	 * @param event event
	 * @param attacker attacker
	 * @param defender defender
	 */
	public static void handleMagicDamage2(EntityDamageByEntityEvent event, SagaPlayer attacker, SagaPlayer defender) {

		
		if(event.isCancelled()) return;

		// Only living:
		if(!(event.getEntity() instanceof LivingEntity)) return;

		LivingEntity entity = (LivingEntity) event.getEntity();

		// Damage trough armor:
		entity.damage(event.getDamage(), event.getDamager());
		
		// Take control of the event:
		event.setCancelled(true);
		
		
	}
	
	/**
	 * Handles magic damage.
	 * 
	 * @param event event
	 * @param attacker attacker
	 * @param defender defender
	 */
	public static void handleMagicDamage2(EntityDamageByEntityEvent event, SagaPlayer attacker, Creature defender) {

		
		if(event.isCancelled()) return;

		// Only living:
		if(!(event.getEntity() instanceof LivingEntity)) return;

		LivingEntity entity = (LivingEntity) event.getEntity();

		// Damage trough armor:
		entity.damage(event.getDamage(), event.getDamager());
		
		// Take control of the event:
		event.setCancelled(true);
		
		
	}
	
	

	
	
}
