package org.saga.attributes;

import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.saga.player.SagaPlayer;

public class FireResistanceAttribute extends Attribute {

	
	/**
	 * Attribute name.
	 */
	transient private static String ATTRIBUTE_NAME = "fire resistance";
	
	
	/**
	 * Sets the name.
	 * 
	 */
	public FireResistanceAttribute() {
		super(ATTRIBUTE_NAME, DisplayType.DEFENSE);
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.attributes.Attribute#completeExtended()
	 */
	@Override
	protected boolean completeExtended() {
		return true;
	}

	
	/**
	 * Uses the attribute.
	 * 
	 */
	public void use(Short attributeLevel, SagaPlayer sagaPlayer, Event event) {
		
		
		// Ignore if not a damage event:
		if(!(event instanceof EntityDamageEvent)){
			return;
		}
		
		if(!((EntityDamageEvent) event).getCause().equals(DamageCause.FIRE)){
			return;
		}
		int damage = ceiling(((EntityDamageEvent) event).getDamage()*calculateValue(attributeLevel));
		if(damage < 0){
		}
		((EntityDamageEvent) event).setDamage(damage);
		System.out.println("!used "+ATTRIBUTE_NAME+" attribute!");
		
		
	}
	
	
}
