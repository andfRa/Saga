package org.saga.abilities;

import java.util.Random;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;

public class Dodge extends Ability{

	
	// Initialization:
	/**
	 * Initializes using definition.
	 * 
	 * @param definition ability definition
	 */
	public Dodge(AbilityDefinition definition) {
		
        super(definition);
	
	}

	
	// Usage:
	/* 
	 * (non-Javadoc)
	 * @see org.saga.abilities.Ability#onHitByPlayer(org.bukkit.event.entity.EntityDamageByEntityEvent, org.saga.player.SagaPlayer)
	 */
	@Override
	public boolean onHitByPlayer(EntityDamageByEntityEvent event, SagaPlayer attacker) {

	
		// Check pre use:
		if(!handlePreUse()){
			return false;
		}

		return handleAbility(event);
		
	
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.saga.abilities.Ability#onHitByCreature(org.bukkit.event.entity.EntityDamageByEntityEvent, org.bukkit.entity.Creature)
	 */
	@Override
	public boolean onHitByCreature(EntityDamageByEntityEvent event, Creature creature) {

		
		// Check pre use:
		if(!handlePreUse()){
			return false;
		}

		return handleAbility(event);
		
	
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.saga.abilities.Ability#onShotByPlayer(org.bukkit.event.entity.EntityDamageByEntityEvent, org.saga.player.SagaPlayer, org.bukkit.entity.Projectile)
	 */
	@Override
	public boolean onShotByPlayer(EntityDamageByEntityEvent event, SagaPlayer attacker, Projectile projectile) {

		
		// Check pre use:
		if(!handlePreUse()){
			return false;
		}

		return handleAbility(event);
		
	
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.saga.abilities.Ability#onShotCreature(org.bukkit.event.entity.EntityDamageByEntityEvent, org.bukkit.entity.Creature, org.bukkit.entity.Projectile)
	 */
	@Override
	public boolean onShotByCreature(EntityDamageByEntityEvent event, Creature creature, Projectile projectile) {

		
		// Check pre use:
		if(!handlePreUse()){
			return false;
		}

		return handleAbility(event);
		
	
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.saga.abilities.Ability#onSpelledByPlayer(org.bukkit.event.entity.EntityDamageByEntityEvent, org.saga.player.SagaPlayer, org.bukkit.entity.Projectile)
	 */
	@Override
	public boolean onSpelledByPlayer(EntityDamageByEntityEvent event, SagaPlayer defender, Projectile projectile) {

		
		// Check pre use:
		if(!handlePreUse()){
			return false;
		}

		return handleAbility(event);
		
	
	}
	
	/**
	 * Handles ability.
	 * 
	 * @param event event
	 * @return true if successful
	 */
	private boolean handleAbility(EntityDamageByEntityEvent event) {


		Double probability = getDefinition().getPrimaryFunction().calculateValue(getSkillLevel());
		
		Random random = new Random();
		
		// Check dodge:
		if(random.nextDouble() > probability){
			return false;
		}
		
		// Play effect:
		Vector jump = event.getEntity().getLocation().subtract(event.getDamager().getLocation()).toVector();
		jump.setY(0);
		jump.normalize();
		jump.setY(0.20);
		jump.multiply(0.6);
		event.getEntity().setVelocity(jump);
		
		// Dodge:
		event.setDamage(0);
		
		// Award exp:
		Double awardedExp = awardExperience();
		
		// Statistics:
		StatisticsManager.manager().onAbilityUse(getName(), awardedExp);
		
		return true;
		
		
	}
	
	
}
