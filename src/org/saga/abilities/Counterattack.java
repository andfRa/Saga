package org.saga.abilities;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.saga.Saga;
import org.saga.attributes.DamageType;
import org.saga.config.VanillaConfiguration;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.messages.effects.StatsEffectHandler;
import org.saga.player.SagaPlayer;

public class Counterattack extends Ability{
	
	
	/**
	 * Facing half angle value key.
	 */
	transient private static String FACING_HALF_ANGLE = "facing half angle";
	
	
	/**
	 * Used to prevent loops of death.
	 */
	transient private Boolean progress = null;
	
	
	
	// Initialisation:
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Counterattack(AbilityDefinition definition) {
		
        super(definition);
	
	}
	
	/* 
	 * Cancels trigger spam.
	 * 
	 * @see org.saga.abilities.Ability#useSilentPreTrigger()
	 */
	@Override
	public boolean useSilentPreTrigger() {
		return true;
	}
	
	
	// Ability usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#handleDefendPreTrigger(org.saga.listeners.events.SagaEntityDamageEvent)
	 */
	@Override
	public boolean handleDefendPreTrigger(SagaEntityDamageEvent event) {
		return handlePreTrigger();
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#onPlayerInteractPlayer(org.bukkit.event.player.PlayerInteractEntityEvent, org.saga.player.SagaPlayer)
	 */
	@Override
	public boolean triggerDefend(SagaEntityDamageEvent event) {

		
		// Prevent loops:
		if(progress != null) return false;
		
		// Only physical:
		if(event.type != DamageType.MELEE) return false;
		
		// Defender must be a player:
		if(!(getSagaLiving() instanceof SagaPlayer)) return false;
		SagaPlayer sagaDefender = (SagaPlayer) getSagaLiving();
		Player defender = sagaDefender.getPlayer();
		
		// Blocking:
		if(!sagaDefender.getPlayer().isBlocking()) return false;
		
		// Attacker:
		LivingEntity attacker = null;
		if(event.sagaAttacker != null) attacker = event.sagaAttacker.getWrapped();
		else if(event.creatureAttacker != null) attacker = event.creatureAttacker;
		else return false;
		
		// Check degrees:
		double deg = Parry.getFacing(defender, attacker);
		if(Math.abs(deg -180) > getDefinition().getFunction(FACING_HALF_ANGLE).value(getCooldown())) return false;
		
		// Animation:
		StatsEffectHandler.playAnimateArm(sagaDefender);
		
		// Defenders weapon:
		Material weapon = defender.getItemInHand().getType();
		
		// Call event:
		progress = true;
		EntityDamageByEntityEvent edbeEvent = new EntityDamageByEntityEvent(defender, attacker, DamageCause.ENTITY_ATTACK, VanillaConfiguration.getBaseDamage(weapon));
		Saga.plugin().getServer().getPluginManager().callEvent(edbeEvent);
		if(edbeEvent.isCancelled()) return false;
		progress = null;
		
		// Calculate actual damage:
		double damage = edbeEvent.getDamage();
		
		if(event.sagaAttacker instanceof SagaPlayer){
			
			damage*= VanillaConfiguration.getArmourMultiplier(edbeEvent, event.sagaAttacker.getWrapped());
			damage*= VanillaConfiguration.getEPFMultiplier(edbeEvent, event.sagaAttacker.getWrapped());
			if(VanillaConfiguration.checkBlocking(edbeEvent, event.sagaAttacker.getWrapped()))
				damage*= VanillaConfiguration.getBlockingMultiplier();
			
		}
		
		// Cancel and redirect:
		event.cancel();
		attacker.damage((int)damage, defender);
		
		// Animation:
		StatsEffectHandler.playAnimateArm(sagaDefender);
		
		// Effect:
		StatsEffectHandler.playParry(sagaDefender);
		
		return true;
		
		
	}
	
	
}
