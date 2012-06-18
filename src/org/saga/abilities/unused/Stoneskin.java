package org.saga.abilities.unused;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.saga.Saga;
import org.saga.abilities.Ability;
import org.saga.messages.PlayerMessages;
import org.saga.player.SagaEntityDamageManager;
import org.saga.player.SagaPlayer;
import org.saga.player.Skill.ArmourType;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.TextUtil;
import org.saga.utility.TwoPointFunction;

public class Stoneskin extends Ability{

//	
//	/**
//	 * The amount of uses the ability has.
//	 */
//	private Integer uses = null;
//	
//	
//	// Initialisation:
//	/**
//	 * Initialises using definition.
//	 * 
//	 * @param definition ability definition
//	 */
//	public Stoneskin(AbilityDefinition definition) {
//		
//		super(definition);
//		this.uses = 0;
//       
//	}
//
//	// Uses:
//	/**
//	 * Called when the ability was used.
//	 */
//	private void use() {
//
//		if(this.uses == null) this.uses = 0;
//		
//		this.uses++;
//		
//	}
//	
//	/**
//	 * Clears the uses.
//	 * 
//	 * @return uses
//	 */
//	private int clearUses() {
//
//		if(this.uses == null) return 0;
//		
//		Integer uses = this.uses;
//		
//		this.uses = null;
//		
//		return uses;
//		
//	}
//	
//	// Events:
//	/* 
//	 * (non-Javadoc)
//	 * @see org.saga.abilities.Ability#onActivate()
//	 */
//	@Override
//	public boolean onPreActivate() {
//
//
//		// Heavy armour restriction:
//		if(getSagaPlayer().getArmor(ArmourType.HEAVY) > 0){
//			getSagaPlayer().message(invalidArmour(ArmourType.HEAVY));
//			return false;
//		}
//
//		// Exotic armour restriction:
//		if(getSagaPlayer().getArmor(ArmourType.EXOTIC) > 0){
//			getSagaPlayer().message(invalidArmour(ArmourType.EXOTIC));
//			return false;
//		}
//
//		// Play effect:
//		playeEffect(getSagaPlayer());
//	
//		// Reset uses:
//		this.uses = null;
//		
//		return true;
//		
//		
//	}
//	
//	/* 
//	 * (non-Javadoc)
//	 * @see org.saga.abilities.Ability#onPreDeactivate()
//	 */
//	@Override
//	public boolean onPreDeactivate() {
//		
//		
//		// Award exp:
//		Double awardedExp = awardExperience(clearUses());
//		
//		// Statistics:
//		StatisticsManager.manager().onAbilityUse(getName(), awardedExp);
//		
//		return true;
//		
//		
//	}
//	
//	/* 
//	 * (non-Javadoc)
//	 * @see org.saga.abilities.Ability#onHitByPlayer(org.bukkit.event.entity.EntityDamageByEntityEvent, org.saga.player.SagaPlayer)
//	 */
//	@Override
//	public boolean onHitByPlayer(EntityDamageByEntityEvent event, SagaPlayer attacker) {
//
//	
//		// Check pre use:
//		if(!handlePreUse()){
//			return false;
//		}
//
//		return handleAbility(event);
//		
//	
//	}
//	
//	/* 
//	 * (non-Javadoc)
//	 * @see org.saga.abilities.Ability#onHitByCreature(org.bukkit.event.entity.EntityDamageByEntityEvent, org.bukkit.entity.Creature)
//	 */
//	@Override
//	public boolean onHitByCreature(EntityDamageByEntityEvent event, Creature creature) {
//
//		
//		// Check pre use:
//		if(!handlePreUse()){
//			return false;
//		}
//
//		return handleAbility(event);
//		
//	
//	}
//	
//	/* 
//	 * (non-Javadoc)
//	 * @see org.saga.abilities.Ability#onShotByPlayer(org.bukkit.event.entity.EntityDamageByEntityEvent, org.saga.player.SagaPlayer, org.bukkit.entity.Projectile)
//	 */
//	@Override
//	public boolean onShotByPlayer(EntityDamageByEntityEvent event, SagaPlayer attacker, Projectile projectile) {
//
//		
//		// Check pre use:
//		if(!handlePreUse()){
//			return false;
//		}
//
//		return handleAbility(event);
//		
//	
//	}
//	
//	/* 
//	 * (non-Javadoc)
//	 * @see org.saga.abilities.Ability#onShotCreature(org.bukkit.event.entity.EntityDamageByEntityEvent, org.bukkit.entity.Creature, org.bukkit.entity.Projectile)
//	 */
//	@Override
//	public boolean onShotByCreature(EntityDamageByEntityEvent event, Creature creature, Projectile projectile) {
//
//		
//		// Check pre use:
//		if(!handlePreUse()){
//			return false;
//		}
//
//		return handleAbility(event);
//		
//	
//	}
//	
//	/* 
//	 * (non-Javadoc)
//	 * @see org.saga.abilities.Ability#onSpelledByPlayer(org.bukkit.event.entity.EntityDamageByEntityEvent, org.saga.player.SagaPlayer, org.bukkit.entity.Projectile)
//	 */
//	@Override
//	public boolean onSpelledByPlayer(EntityDamageByEntityEvent event, SagaPlayer defender, Projectile projectile) {
//
//		
//		// Check pre use:
//		if(!handlePreUse()){
//			return false;
//		}
//
//		return handleAbility(event);
//
//		
//	}
//	
//	/**
//	 * Handles ability.
//	 * 
//	 * @param event event
//	 * @return true if successful
//	 */
//	private boolean handleAbility(EntityDamageByEntityEvent event) {
//
//
//		// Heavy armour restriction:
//		if(getSagaPlayer().getArmor(ArmourType.HEAVY) > 0){
//			getSagaPlayer().message(invalidArmour(ArmourType.HEAVY));
//			return false;
//		}
//
//		// Exotic armour restriction:
//		if(getSagaPlayer().getArmor(ArmourType.EXOTIC) > 0){
//			getSagaPlayer().message(invalidArmour(ArmourType.EXOTIC));
//			return false;
//		}
//
//		Double damageMult = 1 - getDefinition().getPrimaryFunction().value(getSkillLevel());
//
//		// Normalize:
//		if(damageMult < 0){
//			Saga.severe(this, "damage multiplier negative", "using 0.0");
//			damageMult = 0.0;
//		}
//		else if(damageMult > 1){
//			Saga.severe(this, "damage multiplier over 1", "using 1.0");
//			damageMult = 1.0;
//		}
//		
//		// Reduce damage;
//		event.setDamage(TwoPointFunction.randomRound(event.getDamage() * damageMult));
//
//		// Play effect:
//		playeEffect(getSagaPlayer());
//		
//		// Use:
//		use();
//		
//		return true;
//		
//		
//	}
//	
//	/**
//	 * Plays ability effect.
//	 * 
//	 * @param sagaPlayer saga player
//	 */
//	private static void playeEffect(SagaPlayer sagaPlayer) {
//
//		Location feetLoc = sagaPlayer.getLocation();
//		
//		if(feetLoc == null) return;
//		
//		Location headLoc = sagaPlayer.getLocation();
//		headLoc.add(0, 1, 0);
//		
//		// Play effect:
//		sagaPlayer.playGlobalEffect(Effect.STEP_SOUND, 1, feetLoc);
//		sagaPlayer.playGlobalEffect(Effect.STEP_SOUND, 1, headLoc);
//		
//		
//	}
//	
//	public String invalidArmour(ArmourType type){
//		
//		switch (type) {
//		case HEAVY:
//			
//			return PlayerMessages.negative + TextUtil.capitalize(getName()) + " can't be used with heavy armour.";
//
//		case EXOTIC:
//	
//			return PlayerMessages.negative + TextUtil.capitalize(getName()) + " can't be used with exotic armour.";
//
//		default:
//			return PlayerMessages.negative + "Invalid armour.";
//		}
//		
//	}
//	
	
}
