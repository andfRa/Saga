package org.saga.abilities;

import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.saga.listeners.events.SagaEntityDamageEvent;

public class Backstab extends Ability{

	/**
	 * Facing half angle value key.
	 */
	transient private static String FACING_HALF_ANGLE = "facing half angle";

	/**
	 * damage multiplier key.
	 */
	transient private static String DAMAGE_MULTIPLIER = "damage multiplier";

	/**
	 * Armour penetration value key.
	 */
	transient private static String ARMOUR_PENETRATION = "armour penetration";

	/**
	 * Enchant penetration value key.
	 */
	transient private static String ENCHANT_PENETRATION = "enchant penetration";


	
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Backstab(AbilityDefinition definition) {
        super(definition);
	}
	

	
	// Ability usage:
	public boolean triggerAttack(SagaEntityDamageEvent event) {
		
		
		if(event.attackerPlayer == null) return false;
		LivingEntity defender = event.getDefender();
		LivingEntity attacker = event.attackerPlayer.getPlayer();
		
		if(defender == null || attacker == null) return false;
		
		// Facing:
		double deg = getFacing(defender, attacker);
		
		// Check degrees:
		if(Math.abs(deg) > getDefinition().getFunction(FACING_HALF_ANGLE).value(getCooldown())) return false;
	
		// Backstab:
		event.multiplyDamage(getDefinition().getFunction(DAMAGE_MULTIPLIER).value(getScore()));
		event.modifyArmourPenetration(getDefinition().getFunction(ARMOUR_PENETRATION).value(getScore()));
		event.modifyEnchantPenetration(getDefinition().getFunction(ENCHANT_PENETRATION).value(getScore()));
		
		System.out.println("BACKSTAB");
		
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
