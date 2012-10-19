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
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Counterattack(AbilityDefinition definition) {
		
        super(definition);
	
	}
	
	/**
	 * Used to prevent loops of death.
	 */
	private Boolean progress = null;

	
	
	// Ability usage:
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
		
		// Must be blocking:
		if(!sagaDefender.getPlayer().isBlocking()) return false;
		
		// Attacker:
		LivingEntity attacker = null;
		if(event.attackerPlayer != null) attacker = event.attackerPlayer.getPlayer();
		else if(event.attackerCreature != null) attacker = event.attackerCreature;
		else return false;
		
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
		
		if(event.attackerPlayer instanceof SagaPlayer){
			
			damage*= VanillaConfiguration.getArmourMultiplier(edbeEvent, (SagaPlayer)event.attackerPlayer);
			damage*= VanillaConfiguration.getEPFMultiplier(edbeEvent, (SagaPlayer)event.attackerPlayer);
			damage*= VanillaConfiguration.getBlockingMultiplier(edbeEvent, (SagaPlayer)event.attackerPlayer);
			
		}
		
		// Cancel and redirect:
		event.cancel();
		attacker.damage((int)damage);
		
		return true;
		
		
	}
	
	
}
