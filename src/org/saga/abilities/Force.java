package org.saga.abilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.messages.effects.StatsEffectHandler;
import org.saga.player.SagaLiving;
import org.saga.player.SagaPlayer;

public class Force extends Ability{

	/**
	 * Radius key.
	 */
	private static String RADIUS_KEY = "radius";
	
	/**
	 * Power key.
	 */
	private static String POWER_KEY = "power";
	
	
	
	// Initialisation:
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Force(AbilityDefinition definition) {
		
        super(definition);
	
	}


	
	// Usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#trigger()
	 */
	@Override
	public boolean triggerInteract(PlayerInteractEvent event) {
		

		SagaLiving<?> sagaLiving = getSagaLiving();
		Integer abilityLevel = getScore();
		
		LivingEntity player = sagaLiving.getLivingEntity();
		if(player == null){
			SagaLogger.severe(this, "can't continue with trigger because the player isn't online");
			return false;
		}
		
		// Get entities:
		double radius = getDefinition().getFunction(RADIUS_KEY).randomIntValue(getScore());
		double radiusSquared = radius*radius;
		
		List<Entity> entities = player.getNearbyEntities(radius, 4, radius);
		ArrayList<LivingEntity> filteredEntities = new ArrayList<LivingEntity>();
		for (Entity entity : entities) {
			
			if(!(entity instanceof LivingEntity)) continue;
			
			// Handle pvp:
			if(entity instanceof Player){
			
				SagaPlayer targetPlayer = Saga.plugin().getLoadedPlayer(((Player)entity).getName());
				if(targetPlayer == null){
					SagaLogger.severe(this, "can't continue with trigger because the player "+ ((Player)entity).getName() + " isn't loaded");
					return false;
				}
				
				// Pvp event:
				EntityDamageByEntityEvent edbeEvent = new EntityDamageByEntityEvent(event.getPlayer(), entity, DamageCause.ENTITY_ATTACK, 0);
				Saga.plugin().getServer().getPluginManager().callEvent(edbeEvent);
				if(edbeEvent.isCancelled()) continue;
				
			}
			
			if(entity.getLocation().distanceSquared(player.getLocation()) > radiusSquared) continue;
			
			filteredEntities.add((LivingEntity) entity);
			
		}
		
		// Push back:
		double speed = getDefinition().getFunction(POWER_KEY).value(abilityLevel);
		Integer pushed = 0;
		
		for (LivingEntity entity : filteredEntities) {
			
			Vector velocity = entity.getLocation().subtract(player.getLocation()).toVector();
			velocity.setY(0);
			velocity = velocity.normalize();
			velocity.setY(0.2);
			velocity = velocity.multiply(speed);

			// Limit velocity at the edges:
			double distanceSquared = entity.getLocation().distanceSquared(player.getLocation());
			if(distanceSquared / radiusSquared > 0.86){
				velocity = velocity.multiply(0.5);
			}
			
			pushed++;
			entity.setVelocity(velocity);
			
		}
		
		// Effect:
		StatsEffectHandler.playSpellCast(sagaLiving);
		
		return true;
		
		
	}
	
}
