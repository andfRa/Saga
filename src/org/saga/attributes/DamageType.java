package org.saga.attributes;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public enum DamageType {

	MELEE,
	RANGED,
	MAGIC,
	BURN,
	OTHER;
	
	
	
	/**
	 * Gets the damage type.
	 * 
	 * @param event event
	 * @return damage type
	 */
	public static DamageType getDamageType(EntityDamageEvent event) {

		
		// Damaged by entity:
		if(event instanceof EntityDamageByEntityEvent){
			
			EntityDamageByEntityEvent edbye = (EntityDamageByEntityEvent)event;

			// Arrow:
			if(edbye.getDamager() instanceof Arrow) return RANGED;

			// Fireball:
			if(edbye.getDamager() instanceof Fireball) return MAGIC;
			
			// Normal:
			if(edbye.getCause() == DamageCause.LIGHTNING) return OTHER;
			
			return MELEE;
			
		}
		
		// Damaged by world:
		else{
			
			if(event.getCause() == DamageCause.FIRE_TICK) return BURN;
			
		}
		
		return OTHER;
		
		
	}
	
	
}
