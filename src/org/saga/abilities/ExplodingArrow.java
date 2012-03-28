package org.saga.abilities;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.saga.Saga;
import org.saga.listeners.events.SagaEventHandler;
import org.saga.listeners.events.SagaPvpEvent;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;

public class ExplodingArrow extends Ability{

	
	// Initialization:
	/**
	 * Initializes using definition.
	 * 
	 * @param definition ability definition
	 */
	public ExplodingArrow(AbilityDefinition definition) {
		
        super(definition);
	
	}

	@Override
	public boolean onProjectileHit(ProjectileHitEvent event) {
		

		// Check pre use:
		if(!handlePreUse()){
			return false;
		}

		Double strength = getDefinition().getPrimaryFunction().value(getSkillLevel());
		Double radius = getDefinition().getSecondaryFunction().value(getSkillLevel());
		
		Double radiusSqr = radius * radius;
		SagaPlayer sagaPlayer = getSagaPlayer();
		
		Location location = event.getEntity().getLocation();
		List<Entity> entities = event.getEntity().getNearbyEntities(radius, radius, radius);
	
		// Call events:
		for (Entity entity : entities) {
			
			if(entity.getLocation().distanceSquared(location) <= radiusSqr) continue;
			
			if(entity instanceof Player){
				
				SagaPlayer targetPlayer = Saga.plugin().getSagaPlayer(((Player) entity).getName());
				
				if(targetPlayer == sagaPlayer || targetPlayer == null) continue;
				
				EntityDamageByEntityEvent edbeEvent = new EntityDamageByEntityEvent(sagaPlayer.getPlayer(), targetPlayer.getPlayer(), DamageCause.PROJECTILE, 1);
				SagaEventHandler.handlePvp(new SagaPvpEvent(getSagaPlayer(), targetPlayer, getSagaPlayer().getSagaChunk(), targetPlayer.getSagaChunk(), edbeEvent));
				if(edbeEvent.isCancelled()) return false;
				
			}
			
		}

		// Create explosion:
		location.getWorld().createExplosion(location, strength.floatValue(), false);

		// Award exp:
		Double exp = awardExperience();

		// Statistics:
		StatisticsManager.manager().onAbilityUse(getName(), exp);
		
		return true;
		
	}
	
	
	
}
