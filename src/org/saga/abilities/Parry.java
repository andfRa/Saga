package org.saga.abilities;

import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.saga.SagaLogger;
import org.saga.exceptions.InvalidAbilityException;
import org.saga.listeners.events.SagaEntityDamageEvent;

public class Parry extends Ability{

	/**
	 * Facing half angle value key.
	 */
	transient private static String FACING_HALF_ANGLE = "facing half angle";

	/**
	 * Active time value key.
	 */
	transient private static String ACTIVE_TIME = "active time";

	/**
	 * Hit chance modifier value key.
	 */
	transient private static String HIT_CHANCE_MODIFIER = "hit chance";

	
	/**
	 * Amount to absorb.
	 */
	private Double absorb = 0.0;

	/**
	 * Time when last parry was activated.
	 */
	private Long time = null;
	
	
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Parry(AbilityDefinition definition) {
		
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
	

	
	// Ability usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#onPlayerInteractPlayer(org.bukkit.event.player.PlayerInteractEntityEvent, org.saga.player.SagaPlayer)
	 */
	@Override
	public boolean triggerDefend(SagaEntityDamageEvent event) {
		
		
		if(event.defenderPlayer == null) return false;
		LivingEntity defender = event.defenderPlayer.getLivingEntity();
		LivingEntity attacker = event.getAttacker();
		
		if(defender == null || attacker == null) return false;
		
		// Damage immunity:
		if(time != null){
			
			if(getDefinition().getFunction(ACTIVE_TIME).value(getScore()) * 1000.0 >= System.currentTimeMillis() - time){
				event.modifyHitChance(getDefinition().getFunction(HIT_CHANCE_MODIFIER).value(getScore()));
			}else{
				time = null;
			}
			
		}
		
		// Blocking:
		if(!event.isBlocking()) return false;
		double deg = getFacing(defender, attacker);
		
		// Check degrees:
		if(Math.abs(deg -180) > getDefinition().getFunction(FACING_HALF_ANGLE).value(getCooldown())) return false;
	
		// Parry:
		event.cancel();
		time = System.currentTimeMillis();
		
//		// Push:
//		if(event.type == DamageType.MELEE){
//			double speed = getDefinition().getFunction(PUSHBACK).value(getCooldown());
//			double distance = Math.abs(attacker.getLocation().distance(defender.getLocation()));
//			if(distance < 1.0) distance = 1.0;
//			speed/= distance;
//			Vector velocity = event.defenderPlayer.pushAwayEntity(attacker, speed);
//			defender.setVelocity(velocity);
//		}
		
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
