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
import org.saga.attributes.DamageType;
import org.saga.config.VanillaConfiguration;
import org.saga.listeners.events.SagaDamageEvent;
import org.saga.listeners.events.SagaEventHandler;
import org.saga.messages.effects.StatsEffectHandler;
import org.saga.player.SagaLiving;
import org.saga.player.SagaPlayer;

public class Crush extends Ability{
	

	/**
	 * Horizontal speed key.
	 */
	transient private static String HORIZONTAL_SPEED_KEY = "horizontal speed";

	/**
	 * Vertical speed key.
	 */
	transient private static String VERTICAL_SPEED_KEY = "vertical speed";
	
	/**
	 * Duration key.
	 */
	transient private static String DURATION_KEY = "jump duration";
	
	/**
	 * Down speed key.
	 */
	transient private static String DOWN_SPEED_KEY = "down speed";
	
	/**
	 * Damage threshold key.
	 */
	transient private static String DAMAGE_THRESHOLD_KEY = "damage threshold";
	
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
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Crush(AbilityDefinition definition) {
		
        super(definition);
		
	}
	
	
	
	// Usage:
	/* 
	 * Requires jumping up.
	 * 
	 * @see org.saga.abilities.Ability#handleInteractPreTrigger(org.bukkit.event.player.PlayerInteractEvent)
	 */
	@Override
	public boolean handleInteractPreTrigger(PlayerInteractEvent event) {
		return !getSagaLiving().isFalling() && !getSagaLiving().isGrounded() && !event.getPlayer().isSprinting() && super.handlePreTrigger();
	}
	
	/* 
	 * Requires fall damage.
	 * 
	 * @see org.saga.abilities.Ability#handleDefendPreTrigger(org.saga.listeners.events.SagaEntityDamageEvent)
	 */
	@Override
	public boolean handleDefendPreTrigger(SagaDamageEvent event) {
		return event.type == DamageType.FALL;
	}
	
	/* 
	 * Starts the charge.
	 * 
	 * @see org.saga.abilities.Ability#triggerInteract(org.bukkit.event.player.PlayerInteractEvent)
	 */
	@Override
	public boolean triggerInteract(PlayerInteractEvent event) {
		
		
		final LivingEntity attacker = getSagaLiving().getWrapped();
		
		// Horizontal:
		double horSpeed = getDefinition().getFunction(HORIZONTAL_SPEED_KEY).value(getScore());
		Vector horizontal = attacker.getLocation().getDirection();
		horizontal.setY(0);
		horizontal.normalize();
		horizontal.multiply(horSpeed);

		// Vertical:
		double vertSpeed = getDefinition().getFunction(VERTICAL_SPEED_KEY).value(getScore());
		Vector vertical = new Vector(0.0, 1.0, 0.0);
		horizontal.multiply(vertSpeed);
		
		// Jump velocity:
		attacker.setVelocity(vertical.add(horizontal));
		
		// Down velocity:
		long duration = (long) (getDefinition().getFunction(DURATION_KEY).value(getScore()) * 20);
		Saga.plugin().getServer().getScheduler().scheduleSyncDelayedTask(Saga.plugin(), new Runnable() {
			
			@Override
			public void run() {
				
				double speed = getDefinition().getFunction(DOWN_SPEED_KEY).value(getScore());
				
				Vector velocity = attacker.getVelocity();
				velocity.add(new Vector(0, speed, 0));
				attacker.setVelocity(velocity);
				if(attacker instanceof Player) ((Player) attacker).setSneaking(true);
				attacker.setFallDistance(VanillaConfiguration.FALLING_MIMUM_DAMAGE_HEIGHT);
				
			}
			
		}, duration);
		
		return true;
		
		
	}
	
	@Override
	public boolean triggerDefend(SagaDamageEvent event) {
		
		
		SagaLiving sagaLiving = getSagaLiving();
		LivingEntity attacker = sagaLiving.getWrapped();
		
		// Player:
		if(event.sagaDefender instanceof SagaPlayer){
			
			Player defenderPlayer = ((SagaPlayer) event.sagaDefender).getPlayer();
			
			// Blocking:
			if(!defenderPlayer.isBlocking()) return false;
			
			// Stop crouching:
			defenderPlayer.setSneaking(false);
			
		}
		
		// Push:
		boolean trigger = false;
		double pushRadius = getDefinition().getFunction(PUSH_RADIUS_KEY).value(getScore());
		double pushSpeed = getDefinition().getFunction(PUSH_SPEED_KEY).value(getScore());
		
		List<Entity> nearby = attacker.getNearbyEntities(pushRadius, pushRadius, pushRadius);
		for (Entity entity : nearby) {
			
			if(!(entity instanceof LivingEntity)) continue;
			
			LivingEntity defender = (LivingEntity) entity;
			int damage = getDefinition().getFunction(PUSH_DAMAGE_KEY).intValue(getScore());
			
			// Damage event:
			SagaDamageEvent devent = new SagaDamageEvent(new EntityDamageEvent(attacker, DamageCause.FALLING_BLOCK, damage));
			SagaEventHandler.handleDamage(devent);
			if(devent.isCancelled()) continue;
			
			// Push:
			sagaLiving.pushAwayEntity(defender, pushSpeed);
			
			// Damage:
			defender.damage(damage, attacker);
			
			trigger = true;
			
		}
		
		// Damage threshold:
		if(event.getBaseDamage() <= getDefinition().getFunction(DAMAGE_THRESHOLD_KEY).intValue(getScore())){
			trigger = true;
			event.cancel();
		}
		
		// Effect:
		StatsEffectHandler.playCrush(sagaLiving);
		
		return trigger;
		
		
	}
	
	
}
