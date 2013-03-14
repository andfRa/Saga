package org.saga.abilities;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.saga.Saga;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.listeners.events.SagaEventHandler;
import org.saga.player.SagaLiving;

public class Charge extends Ability{
	
	
	/**
	 * Speed key.
	 */
	transient private static String SPEED_KEY = "speed";

	/**
	 * Duration key.
	 */
	transient private static String DURATION_KEY = "duration";

	/**
	 * Push radius key.
	 */
	transient private static String PUSH_RADIUS_KEY = "push radius";
	
	/**
	 * Push speed key.
	 */
	transient private static String PUSH_SPEED_KEY = "push speed";
	
	/**
	 * Push damage key.
	 */
	transient private static String PUSH_DAMAGE_KEY = "push damage";
	
	
	/**
	 * Ticks delay.
	 */
	transient private static long DELAY_TICK = 5;
	
	
	/**
	 * Time when the ability was activated.
	 */
	transient private Long time = null;
	
	
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Charge(AbilityDefinition definition) {
		
        super(definition);
		
	}
	
	
	
	// Usage:
	/* 
	 * Requires sprinting.
	 * 
	 * @see org.saga.abilities.Ability#handleInteractPreTrigger(org.bukkit.event.player.PlayerInteractEvent)
	 */
	@Override
	public boolean handleInteractPreTrigger(PlayerInteractEvent event) {
		return event.getPlayer().isSprinting() && getSagaLiving().isGrounded() && super.handlePreTrigger();
	}
	
	/* 
	 * Starts the charge.
	 * 
	 * @see org.saga.abilities.Ability#triggerInteract(org.bukkit.event.player.PlayerInteractEvent)
	 */
	@Override
	public boolean triggerInteract(PlayerInteractEvent event) {
		
		
		// Start charge:
		if(time == null){
			
			time = System.currentTimeMillis();
			handleCharging(getSagaLiving());
			
		}
		
		return true;
		
	}
	
	/**
	 * Handles charging.
	 * 
	 * @param living entity that is charging
	 */
	private void handleCharging(final SagaLiving<?> sagaLiving) {
		
		
		LivingEntity attacker = sagaLiving.getLivingEntity();
		
		double duration = getDefinition().getFunction(DURATION_KEY).value(getScore()) * 1000;
		
		// Stop charge:
		if(time != null && System.currentTimeMillis() - time >= duration){
			
			attacker.setVelocity(new Vector());
			time = null;
			if(attacker instanceof Player) ((Player) attacker).setSneaking(false);
			
			return;
			
		}
		
		// Continue charge:
		double speed = getDefinition().getFunction(SPEED_KEY).value(getScore());
		Vector velocity = attacker.getEyeLocation().getDirection();
		velocity.setY(0);
		velocity.normalize().multiply(speed);
		attacker.setVelocity(velocity);
		
		// Sneak and check push:
		boolean push = true;
		if(attacker instanceof Player){
			((Player) attacker).setSneaking(true);
			if(!((Player) attacker).isBlocking()) push = false;
		}
		
		if(push){
			
			// Push:
			double pushRadius = getDefinition().getFunction(PUSH_RADIUS_KEY).value(getScore());
			double pushSpeed = getDefinition().getFunction(PUSH_SPEED_KEY).value(getScore());
			
			List<Entity> nearby = attacker.getNearbyEntities(pushRadius, pushRadius, pushRadius);
			for (Entity entity : nearby) {
				
				if(!(entity instanceof LivingEntity)) continue;
				
				LivingEntity defender = (LivingEntity) entity;
				int damage = getDefinition().getFunction(PUSH_DAMAGE_KEY).intValue(getScore());
				
				// Damage event:
				SagaEntityDamageEvent event = new SagaEntityDamageEvent(new EntityDamageEvent(attacker, DamageCause.FALLING_BLOCK, damage), defender);
				SagaEventHandler.handleEntityDamage(event);
				if(event.isCancelled()) continue;
				
				// Push:
				sagaLiving.pushAwayEntity(defender, pushSpeed);
				
				// Damage:
				defender.damage(damage, attacker);
				
			}
			
		}
				
		Saga.plugin().getServer().getScheduler().scheduleSyncDelayedTask(Saga.plugin(), new Runnable() {
			
			@Override
			public void run() {
				handleCharging(sagaLiving);
			}
			
		}, DELAY_TICK);
		
		
	}
	
	
}
