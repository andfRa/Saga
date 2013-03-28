package org.saga.abilities;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.saga.attributes.DamageType;
import org.saga.listeners.events.SagaDamageEvent;

public class Stoneskin extends Ability{

	
	/**
	 * Damage multiplier.
	 */
	transient private static String DAMAGE_MULTIPLIER_KEY = "damage multiplier";
	
	
	
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Stoneskin(AbilityDefinition definition) {
		
        super(definition);
		
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
	
	
	
	// Usage:
	/* 
	 * Enables defence.
	 * 
	 * @see org.saga.abilities.Ability#handleDefendPreTrigger(org.saga.listeners.events.SagaEntityDamageEvent)
	 */
	@Override
	public boolean handleDefendPreTrigger(SagaDamageEvent event) {
		return true;
	}
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#onPlayerInteractPlayer(org.bukkit.event.player.PlayerInteractEntityEvent, org.saga.player.SagaPlayer)
	 */
	@Override
	public boolean triggerDefend(SagaDamageEvent event) {
		
		
		// Melee, ranged and magic:
		if(event.type != DamageType.MELEE && event.type != DamageType.RANGED && event.type != DamageType.MAGIC) return false;
		
		// Multiply damage:
		double multiplier = getDefinition().getFunction(DAMAGE_MULTIPLIER_KEY).value(getScore());
		event.multiplyDamage(multiplier);
		
		// Effect:
		if(event.sagaDefender != null) event.sagaDefender.playGlobalEffect(Effect.STEP_SOUND, Material.STONE.getId());
		
		return false;
		
		
	}

	
}
