package org.saga.abilities.unused;

import org.saga.abilities.Ability;

public class ExplodingArrow extends Ability{

//	/**
//	 * Power key.
//	 */
//	private static String POWER_KEY = "power";
//	
//	
//	// Initialization:
//	/**
//	 * Initializes using definition.
//	 * 
//	 * @param definition ability definition
//	 */
//	public ExplodingArrow(AbilityDefinition definition) {
//		
//        super(definition);
//	
//	}
//
//	@Override
//	public boolean trigger(SagaEntityDamageEvent event) {
//		
//
//		Double strength = getDefinition().getPrimaryFunction().value(getSkillLevel());
//		Double radius = getDefinition().getSecondaryFunction().value(getSkillLevel());
//		
//		Double radiusSqr = radius * radius;
//		SagaPlayer sagaPlayer = getSagaPlayer();
//		
//		Location location = event.getEntity().getLocation();
//		List<Entity> entities = event.getEntity().getNearbyEntities(radius, radius, radius);
//	
//		// Call events:
//		for (Entity entity : entities) {
//			
//			if(entity.getLocation().distanceSquared(location) <= radiusSqr) continue;
//			
//			if(entity instanceof Player){
//				
//				SagaPlayer targetPlayer = Saga.plugin().getSagaPlayer(((Player) entity).getName());
//				
//				if(targetPlayer == sagaPlayer || targetPlayer == null) continue;
//				
//				// Pvp event:
//				EntityDamageByEntityEvent edbeEvent = new EntityDamageByEntityEvent(sagaPlayer.getPlayer(), targetPlayer.getPlayer(), DamageCause.PROJECTILE, 1);
//				Saga.plugin().getServer().getPluginManager().callEvent(edbeEvent);
//				if(edbeEvent.isCancelled()) return false;
//				
//			}
//			
//		}
//
//		// Create explosion:
//		location.getWorld().createExplosion(location, strength.floatValue(), false);
//
//		// Award exp:
//		Double exp = awardExperience();
//
//		// Statistics:
//		StatisticsManager.manager().onAbilityUse(getName(), exp);
//		
//		return true;
//		
//	}
	
	
	
}
