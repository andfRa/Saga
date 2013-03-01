package org.saga.abilities;

import org.saga.SagaLogger;
import org.saga.attributes.DamageType;
import org.saga.exceptions.InvalidAbilityException;
import org.saga.listeners.events.SagaEntityDamageEvent;

public class Berserk extends Ability{

	/**
	 * Hits required value key.
	 */
	transient private static String HITS_REQUIRED = "hits required";

	/**
	 * Damage multiplier value key.
	 */
	transient private static String DAMAGE_MULTIPLIER = "damage multiplier";
	
	
	/**
	 * Hits made.
	 */
	private Integer hits;
	
	
	
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Berserk(AbilityDefinition definition) {
		
        super(definition);

		hits = 0;
		
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
			hits = 0;
		}
		
		return true;
		
	}

	
	

	
	// Usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#handleAttackPreTrigger(org.saga.listeners.events.SagaEntityDamageEvent)
	 */
	@Override
	public boolean handleAttackPreTrigger(SagaEntityDamageEvent event) {
		return handlePreTrigger();
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#useSilentPreTrigger()
	 */
	@Override
	public boolean useSilentPreTrigger() {
		return true;
	}
	
	@Override
	public boolean handleDefendPreTrigger(SagaEntityDamageEvent event) {
		return hits > 0;
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#triggerAttack(org.saga.listeners.events.SagaEntityDamageEvent)
	 */
	@Override
	public boolean triggerAttack(SagaEntityDamageEvent event) {
		
		if(event.isCancelled()) return false;
		
		// Increase hits:
		hits++;
		
		// Only melee:
		if(event.type != DamageType.MELEE ) return false;
		
		// Enough hits:
		if(hits < getDefinition().getFunction(HITS_REQUIRED).value(getScore())) return false;
		
		// Increase damage:
		double mult = getDefinition().getFunction(DAMAGE_MULTIPLIER).value(getScore());
		event.multiplyDamage(mult);
		
		// Reset hits:
		hits = 0;
		
		return true;
		
	}
	
	/* 
	 * Resets hits.
	 * 
	 * @see org.saga.abilities.Ability#triggerDefend(org.saga.listeners.events.SagaEntityDamageEvent)
	 */
	@Override
	public boolean triggerDefend(SagaEntityDamageEvent event) {
		
		hits = 0;
		return false;
	
	}
	
	
}
