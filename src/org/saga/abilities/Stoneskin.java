package org.saga.abilities;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.saga.SagaLogger;
import org.saga.attributes.DamageType;
import org.saga.exceptions.InvalidAbilityException;
import org.saga.listeners.events.SagaEntityDamageEvent;

public class Stoneskin extends Ability{

	
	/**
	 * Absorb total key.
	 */
	transient private static String ABSORBED_HITS_KEY = "absorbed hits";

	/**
	 * Hit regeneration per second key.
	 */
	transient private static String HITS_PER_ECOND_KEY = "hits per second";

	/**
	 * Minimum time required for regeneration.
	 */
	transient private static Integer REGENERATION_MIN_TIME = 1000;

	
	/**
	 * Amount to absorb.
	 */
	private Integer hits = 0;
	
	/**
	 * Time when last parry was activated.
	 */
	private Long time;
	
	
	
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Stoneskin(AbilityDefinition definition) {
		
        super(definition);

		hits = 0;
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
			hits = 0;
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
	 * @see org.saga.abilities.Ability#onPlayerInteractPlayer(org.bukkit.event.player.PlayerInteractEntityEvent, org.saga.player.SagaPlayer)
	 */
	@Override
	public boolean triggerDefend(SagaEntityDamageEvent event) {
		
		
		// Melee, ranged and magic:
		if(event.type != DamageType.MELEE && event.type != DamageType.RANGED && event.type != DamageType.MAGIC) return false;
		
		// Regenerate:
		long passed = System.currentTimeMillis() - time;
		time = System.currentTimeMillis();
		if(passed >= REGENERATION_MIN_TIME){
		
			int hitsRegen = (int)(passed / 1000.0 * getDefinition().getFunction(HITS_PER_ECOND_KEY).value(getScore()));
			int hitsMax = getDefinition().getFunction(ABSORBED_HITS_KEY).intValue(getScore());
			for (int i = 0; i < hitsRegen && hits < hitsMax; i++) {
				if(!checkCost()) break;
				useItems();
				hits++;
			}
			
		}
		
		if(hits <= 0) return false;
		
		// Absorb:
		hits--;
		event.cancel();
		
		if(event.defenderPlayer != null) event.defenderPlayer.playGlobalEffect(Effect.STEP_SOUND, Material.STONE.getId());
		
		return false;
		
		
	}

	
}
