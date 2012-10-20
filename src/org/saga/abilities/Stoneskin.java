package org.saga.abilities;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.SagaLogger;
import org.saga.exceptions.InvalidAbilityException;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.messages.effects.StatsEffectHandler;
import org.saga.player.SagaPlayer;

public class Stoneskin extends Ability{

	/**
	 * Absorb total key.
	 */
	transient private static String ABSORB_TOTAL_KEY = "absorb total";

	/**
	 * The amount reduced per second.
	 */
	transient private static String DECAY_PER_SECOND_KEY = "decay per second";

	
	/**
	 * Amount to absorb.
	 */
	private Double absorb = 0.0;

	
	
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Stoneskin(AbilityDefinition definition) {
		
        super(definition);

		absorb = 0.0;
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#complete()
	 */
	@Override
	public boolean complete() throws InvalidAbilityException {

		super.complete();
	
		if (absorb == null) {
			SagaLogger.nullField(this, "absorb");
			absorb = 0.0;
		}
		
		updateClock();
		
		return true;
		
	}
	
	/* 
	 * Trigger indication.
	 * 
	 * @see org.saga.abilities.Ability#hasAttackPreTrigger()
	 */
	@Override
	public boolean hasInteractPreTrigger() {
		return true;
	}
	
	
	
	// Ability usage:
	@Override
	public boolean triggerInteract(PlayerInteractEvent event) {
		
		double absorb = getDefinition().getFunction(ABSORB_TOTAL_KEY).value(getScore());

		if(this.absorb >= absorb) return false;

		this.absorb = absorb;

		updateClock();
		
		// Effect:
		Location loc = event.getPlayer().getLocation();
		loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.STONE.getId());
		
		if(getSagaLiving() instanceof SagaPlayer) StatsEffectHandler.playAnimateArm((SagaPlayer) getSagaLiving());
		
		return true;
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#onPlayerInteractPlayer(org.bukkit.event.player.PlayerInteractEntityEvent, org.saga.player.SagaPlayer)
	 */
	@Override
	public boolean triggerDefend(SagaEntityDamageEvent event) {
		
		if(absorb == 0) return false;
		
		double damage = event.calcDamage();
		
		if(absorb < damage){
			event.multiplyDamage(-(1 - absorb/damage));
			absorb = 0.0;
			return false;
		}
		
		absorb-= damage;
		
		event.cancel();
		
		if(event.defenderPlayer != null) event.defenderPlayer.playGlobalEffect(Effect.STEP_SOUND, Material.STONE.getId());
		
		updateClock();
		
		return false;
		
	}

	
	
	// Clock:
	/* 
	 * Include absorb.
	 * 
	 * @see org.saga.abilities.Ability#clockSecondTick()
	 */
	@Override
	public boolean clockSecondTick() {
		
		if(absorb > 0) absorb-= getDefinition().getFunction(DECAY_PER_SECOND_KEY).value(getScore());
		
		return super.clockSecondTick();
		
	}
	
	/* 
	 * Include absorb.
	 * 
	 * @see org.saga.abilities.Ability#checkClock()
	 */
	@Override
	public boolean checkClock() {
		return super.checkClock() || absorb > 0;
	}
	
}
