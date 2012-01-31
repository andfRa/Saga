package org.saga.attributes;

import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.saga.player.SagaPlayer;

public class FireAbsorbtionAttribute extends Attribute {

	
	/**
	 * Attribute name.
	 */
	transient private static String ATTRIBUTE_NAME = "fire absorbtion";
	
	
	/**
	 * Sets the name.
	 * 
	 */
	public FireAbsorbtionAttribute() {
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
		
		if(!((EntityDamageEvent) event).getCause().equals(DamageCause.FIRE_TICK)){
			return;
		}
		int damage = floor(((EntityDamageEvent) event).getDamage() - calculateValue(attributeLevel));
		if(damage < 0){
//			sagaPlayer.regenerateHealth(damage);
			damage = 0;
		}
		((EntityDamageEvent) event).setDamage(damage);
		System.out.println("!used "+ATTRIBUTE_NAME+" attribute!");
		
		
	}
	
	
}
