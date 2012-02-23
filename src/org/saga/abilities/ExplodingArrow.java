package org.saga.abilities;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.saga.player.SagaEntityDamageManager;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;

public class ExplodingArrow extends Ability{

	
	// Initialization:
	/**
	 * Initializes using definition.
	 * 
	 * @param definition ability definition
	 */
	public ExplodingArrow(AbilityDefinition definition) {
		
        super(definition);
	
	}

	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#onShotPlayer(org.bukkit.event.entity.EntityDamageByEntityEvent, org.saga.player.SagaPlayer, org.bukkit.entity.Projectile)
	 */
	@Override
	public boolean onShotPlayer(EntityDamageByEntityEvent event, SagaPlayer defender, Projectile projectile) {

		
		// Check pre use:
		if(!handlePreUse()){
			return false;
		}
		
		// Handle pvp:
		SagaEntityDamageManager.handlePvp(getSagaPlayer(), defender, event);
		if(event.isCancelled()) return true;
		
		Location location = defender.getLocation();
		if(location == null) return false;
		
		Double strength = getDefinition().getPrimaryFunction().calculateValue(getSkillLevel());
		
		// Normalize:
		if(strength < 0.0) strength = 0.0;
		if(strength > 1.0) strength = 1.0;
		
		location.getWorld().createExplosion(location, strength.floatValue(), false);

		// Award exp:
		awardExperience();
		
		return true;
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#onShotCreature(org.bukkit.event.entity.EntityDamageByEntityEvent, org.bukkit.entity.Creature, org.bukkit.entity.Projectile)
	 */
	@Override
	public boolean onShotCreature(EntityDamageByEntityEvent event, Creature creature, Projectile projectile) {


		// Check pre use:
		if(!handlePreUse()){
			return false;
		}
		
		Location location = creature.getLocation();
		if(location == null) return false;
		
		Double fraction = getDefinition().getPrimaryFunction().calculateValue(getSkillLevel().shortValue());
		
		// Normalize:
		if(fraction < 0.0) fraction = 0.0;
		if(fraction > 1.0) fraction = 1.0;
		
		location.getWorld().createExplosion(location, fraction.floatValue(), false);

		// Award exp:
		Double awardedExp = awardExperience();
		
		// Statistics:
		StatisticsManager.manager().onAbilityUse(getName(), awardedExp);
		
		return true;
		
		
	}
	
	
}
