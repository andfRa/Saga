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
import org.saga.player.SagaEntityDamageManager;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.TwoPointFunction;

public class Force extends Ability{

	
	// Initialization:
	/**
	 * Initializes using definition.
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
	public boolean instant(PlayerInteractEvent event) {
		

		// Check pre use:
		if(!handlePreUse()){
			return false;
		}

		SagaPlayer sagaPlayer = getSagaPlayer();
		
		TwoPointFunction primaryFunction = getDefinition().getPrimaryFunction();
		TwoPointFunction secondaryFunction = getDefinition().getSecondaryFunction();
		Integer skillLevel = getSkillLevel();
		
		Player player = sagaPlayer.getPlayer();
		if(player == null){
			Saga.severe(this, "cant continue with trigger because the player isn't online", "ignoring request");
			return false;
		}
		
		// Get entities:
		double radius = primaryFunction.randomIntValue(skillLevel);
		double radiusSquared = radius*radius;
		
		List<Entity> entities = player.getNearbyEntities(radius, 4, radius);
		ArrayList<LivingEntity> filteredEntities = new ArrayList<LivingEntity>();
		for (Entity entity : entities) {
			
			if(!(entity instanceof LivingEntity)) continue;
			
			// Handle pvp:
			if(entity instanceof Player){
			
				SagaPlayer targetPlayer = Saga.plugin().getSagaPlayer(((Player)entity).getName());
				if(targetPlayer == null){
					Saga.severe(this, "can't continue with trigger because the player "+ ((Player)entity).getName() + " isn't loaded", "ignoring request");
					return false;
				}
				
				EntityDamageByEntityEvent edbeevent = new EntityDamageByEntityEvent(event.getPlayer(), entity, DamageCause.ENTITY_ATTACK, 0);
				SagaEntityDamageManager.handlePvp(sagaPlayer, targetPlayer, edbeevent);
				
				if(edbeevent.isCancelled()) continue;
				
			}
			
			if(entity.getLocation().distanceSquared(player.getLocation()) > radiusSquared) continue;
			
			filteredEntities.add((LivingEntity) entity);
			
		}
		
		// Push back:
		double speed = secondaryFunction.value(skillLevel);
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
		
		// Award exp:
		Double awardedExp = awardExperience(pushed);
		
		// Statistics:
		StatisticsManager.manager().onAbilityUse(getName(), awardedExp);
		
		// Effect:
		sagaPlayer.playeSpellEffect();
		
		return true;
		
		
	}
	
	
	
}
