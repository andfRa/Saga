package org.saga.abilities;

import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.saga.SagaLogger;
import org.saga.attributes.DamageType;
import org.saga.exceptions.InvalidAbilityException;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.utility.TwoPointFunction;

public class Berserk extends Ability{

	/**
	 * Hits increase per second key value key.
	 */
	transient private static String INCREASE_PER_SECOND = "increase per second";

	/**
	 * Hits decrease per second key value key.
	 */
	transient private static String DECREASE_PER_SECOND = "decrease per second";
	
	/**
	 * Damage per hit key.
	 */
	transient private static String DAMAGE_PER_HIT = "damage per hit";

	/**
	 * Maximum hits key.
	 */
	transient private static String MAX_HITS = "max hits";

	/**
	 * Time in milliseconds which the cooldown begins.
	 */
	transient private static Integer COOLDOWN_TIME = 1000;

	
	/**
	 * Time when last parry was activated.
	 */
	private Long time = null;
	
	/**
	 * Hits made.
	 */
	private Double hits = null;
	
	
	
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Berserk(AbilityDefinition definition) {
		
        super(definition);

		hits = 0.0;
		time = System.currentTimeMillis();
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#complete()
	 */
	@Override
	public boolean complete() throws InvalidAbilityException {

		
		super.complete();
	
		if (hits == null) {
			SagaLogger.nullField(this, "hits");
			hits = 0.0;
		}
		
		if (time == null) {
			SagaLogger.nullField(this, "time");
			time = System.currentTimeMillis();
		}
		
		return true;
		
	}
	

	
	// Ability usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#triggerAttack(org.saga.listeners.events.SagaEntityDamageEvent)
	 */
	@Override
	public boolean triggerAttack(SagaEntityDamageEvent event) {
		
		
		if(event.isCancelled()) return false;
		
		// Calculate hits:
		long passed = System.currentTimeMillis() - time;
		time = System.currentTimeMillis();
		if(passed <= COOLDOWN_TIME){
			hits+= getDefinition().getFunction(INCREASE_PER_SECOND).value(passed / 1000.0);
		}else{
			hits-= getDefinition().getFunction(DECREASE_PER_SECOND).value(passed / 1000.0) * (passed / 1000.0);
		}
		
		// Trim hits:
		if(hits <= 0){
			hits = 0.0;
			return false;
		}
		double maxHits = getDefinition().getFunction(MAX_HITS).value(getScore());
		if(hits > maxHits) hits = maxHits;

		// Only increase melee damage:
		if(event.type != DamageType.MELEE ) return false;
		
		// Add damage:
		TwoPointFunction damageFunction = getDefinition().getFunction(DAMAGE_PER_HIT);
		double damage = damageFunction.value(hits);
		event.modifyDamage(damage);
		
		return true;
		
		
	}

	/**
	 * Gets the degrees between a line connecting both entities and the direction the defender is facing.
	 * 
	 * @param defender defender
	 * @param attacker attacker
	 * @return direction of facing from line connecting both entities, values 0-180 degrees
	 */
	public static double getFacing(LivingEntity defender, LivingEntity attacker) {
		
		Vector defenderDirection = defender.getLocation().getDirection();
		Vector stevesVector = defender.getLocation().subtract(attacker.getLocation()).toVector().normalize();
		return defenderDirection.angle(stevesVector) / (2 * Math.PI) * 360;
		
	}
	
	
}
