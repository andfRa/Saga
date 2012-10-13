package org.saga.listeners.events;

import java.util.PriorityQueue;
import java.util.Random;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.attributes.DamageType;
import org.saga.chunks.BundleManager;
import org.saga.chunks.SagaChunk;
import org.saga.player.SagaPlayer;
import org.saga.utility.TwoPointFunction;

public class SagaEntityDamageEvent {

	
	/**
	 * Random generator.
	 */
	private static Random RANDOM = new Random();

	
	/**
	 * Damage type.
	 */
	public DamageType type;
	
	/**
	 * Projectile.
	 */
	public final Projectile projectile;
	
	
	/**
	 * Attacker creature, null if none.
	 */
	public final Creature attackerCreature;
	
	/**
	 * Defender creature, null if none
	 */
	public final Creature defenderCreature;
	
	/**
	 * Attacker player, null if none.
	 */
	public final SagaPlayer attackerPlayer;
	
	/**
	 * Defender player, null if none
	 */
	public final SagaPlayer defenderPlayer;
	
	
	/**
	 * Attackers saga chunk, null if none.
	 */
	public final SagaChunk attackerChunk;
	
	/**
	 * Defenders saga chunk, null if none.
	 */
	public final SagaChunk defenderChunk;

	
	/**
	 * Minecraft event.
	 */
	private final EntityDamageEvent event;
	
	/**
	 * Damage modifier.
	 */
	private double modifier = 0;
	
	/**
	 * Damage multiplier.
	 */
	private double multiplier = 1;

	/**
	 * Hit chance modifier.
	 */
	private double hitChance = 1;

	/**
	 * Armour penetration modifier.
	 */
	private double penetration = 0;
	
	/**
	 * PvP override.
	 */
	private PriorityQueue<PvPOverride> pvpOverride = new PriorityQueue<SagaEntityDamageEvent.PvPOverride>();
	
	
	
	// Initialise:
	/**
	 * Sets entity damage event.
	 * 
	 * @param event event
	 * @param defender defender entity
	 */
	public SagaEntityDamageEvent(EntityDamageEvent event, LivingEntity defender) {

		
		Entity attacker = null;

		this.event = event;
		type = DamageType.getDamageType(event);
		
		// Attacked by an entity:
		if(event instanceof EntityDamageByEntityEvent) attacker = ((EntityDamageByEntityEvent) event).getDamager();
		
		// Attacked by a projectile:
		if(attacker instanceof Projectile){
			
			projectile = (Projectile) attacker;
			attacker = projectile.getShooter();
			
		}else{
			
			projectile = null;
			
		}
		
		// Get attacker saga player:
		if(attacker instanceof Player){

			attackerPlayer = Saga.plugin().getLoadedPlayer( ((Player) attacker).getName() );
			attackerCreature = null;
			
	    	// No player:
	    	if(attackerPlayer == null) SagaLogger.severe(getClass(), "failed to load saga player for "+ ((Player) attacker).getName());
	    	
		}

		// Get attacker creature:
		else if(attacker instanceof Creature){
			
			attackerCreature = (Creature)attacker;
			attackerPlayer = null;
			
		}else{
			
			attackerCreature = null;
			attackerPlayer = null;
			
		}
		
		// Get defender saga player:
		if(defender instanceof Player){

			defenderPlayer = Saga.plugin().getLoadedPlayer( ((Player) defender).getName() );
			defenderCreature = null;
			
	    	// No player:
	    	if(defenderPlayer == null) SagaLogger.severe(getClass(), "failed to load saga player for "+ ((Player) attacker).getName());
			
		}
		
		// Get defender creature:
		else if(defender instanceof Creature){
			
			defenderPlayer = null;
			defenderCreature = (Creature)defender;
			
		}
		
		else{
			
			defenderPlayer = null;
			defenderCreature = null;
			
		}
		
		// Get chunks:
		attackerChunk = (attacker != null) ? BundleManager.manager().getSagaChunk(attacker.getLocation()) : null;
		defenderChunk = (defender != null) ? BundleManager.manager().getSagaChunk(defender.getLocation()) : null;
		

	}
	
	
	
	// Modify:
	/**
	 * Modifies damage.
	 * 
	 * @param amount amount to modify
	 */
	public void modifyDamage(double amount) {
		modifier+= amount;
	}
	
	/**
	 * Multiplies damage.
	 * 
	 * @param amount amount to multiply by
	 */
	public void multiplyDamage(double amount) {
		multiplier+= amount;
	}
	
	/**
	 * Divides damage.
	 * 
	 * @param amount amount to divide by
	 */
	public void divideDamage(double amount) {
		multiplier/= amount;
	}
	
	/**
	 * Modifies hit chance.
	 * 
	 * @param amount amount to modify
	 */
	public void modifyHitChance(double amount) {
		hitChance+=amount;
	}
	
	/**
	 * Modifies armour penetration.
	 * 
	 * @param amount amount to modify
	 */
	public void modifyArmourPenetration(double amount) {
		penetration+=amount;
	}
	
	/**
	 * Gets the projectile.
	 * 
	 * @return projectile, null if none
	 */
	public Projectile getProjectile() {
		return projectile;
	}
	
	
	
	// Conclude:
	/**
	 * Applies the event.
	 * 
	 */
	public void apply() {


		// Dodge:
		if(hitChance <= RANDOM.nextDouble()) {
//			if(defenderPlayer != null) defenderPlayer.message(ChatColor.GREEN + "dodge!");
//			if(attackerPlayer != null) attackerPlayer.message(ChatColor.GREEN + "dodge!");
			event.setCancelled(true);
			return;
		}
		
//		if(attackerPlayer != null) attackerPlayer.message(ChatColor.GREEN + "dam=" + event.getDamage() + " mod=" + modifier +  " pen=" + penetration + " hit=" + hitChance + " mult=" + multiplier);
//		if(defenderPlayer != null) defenderPlayer.message(ChatColor.GREEN + "dam=" + event.getDamage() + " mod=" + modifier +  " pen=" + penetration + " hit=" + hitChance + " mult=" + multiplier);

		// Normalise multiplier:
		if(multiplier < 0) multiplier = 0;
		
		// Multiply and modify damage:
		double damage = (event.getDamage() + modifier) * multiplier;
		
		// Penetrating damage:
		if(penetration > 1) penetration = 1;
		if(penetration < 0) penetration = 0;
		double penetrating = damage * penetration;
		
		// Normal damage:
		double normal = damage - penetrating;
		
		// Apply damage:
		event.setDamage(TwoPointFunction.randomRound(normal));
		if(event.getEntity() instanceof LivingEntity) ((LivingEntity) event.getEntity()).damage(TwoPointFunction.randomRound(penetrating));
		
		// Player killed player:
		if(attackerPlayer != null && defenderPlayer != null){
			
		}
		
		
	}

	/**
	 * Cancels the event.
	 * 
	 */
	public void cancel() {

		event.setCancelled(true);

	}
	
	
	
	// Event information:
	/**
	 * Checks if player attacked a player.
	 * 
	 * @return true if player versus player
	 */
	public boolean isPvP() {

		return attackerPlayer != null && defenderPlayer != null;

	}
	
	/**
	 * Checks if player attacked a creature.
	 * 
	 * @return true if player versus player
	 */
	public boolean isPvC() {

		return attackerPlayer != null && defenderCreature != null;

	}
	
	/**
	 * Checks if creature attacked a creature.
	 * 
	 * @return true if player versus player
	 */
	public boolean isCvC() {

		return attackerCreature != null && defenderCreature != null;

	}

	/**
	 * Checks if creature attacked a player.
	 * 
	 * @return true if player versus player
	 */
	public boolean isCvP() {

		return attackerCreature != null && defenderPlayer != null;

	}

	/**
	 * Checks if the event is faction versus faction.
	 * 
	 * @return true if faction versus faction.
	 */
	public boolean isFvF() {

		if(attackerPlayer == null || defenderPlayer == null) return false;
		return attackerPlayer.getFaction() != null && defenderPlayer.getFaction() != null;

	}

	
	/**
	 * Checks if the event is cancelled.
	 * 
	 * @return true if cancelled
	 */
	public boolean isCancelled() {

		return event.isCancelled();

	}

	
	/**
	 * Adds a pvp override.
	 * 
	 * @param override pvp override
	 */
	public void addPvpOverride(PvPOverride override) {

		pvpOverride.add(override);
		
	}
	
	/**
	 * Gets the top override.
	 * 
	 * @return top override, NONE if none
	 */
	public PvPOverride getOverride() {

		if(pvpOverride.size() == 0) return PvPOverride.NONE;
		
		return pvpOverride.peek();

	}


	
	/**
	 * Pvp overrides.
	 * 
	 * @author andf
	 *
	 */
	public enum PvPOverride{
		
		
		ADMIN_ALLOW(true),
		ADMIN_DENY(false),
		
		SELF_ALLOW(true),
		
		ARENA_ALLOW(true),
		
		SAME_FACTION_DENY(false),
		FACTION_CLAIMING_ALLOW(true),
		
		SAFE_AREA_DENY(false),
		FACTION_ONLY_PVP_DENY(false),
		ALLY_DENY(false),
		
		NONE(true);
		
		
		/**
		 * If true, then pvp will be allowed.
		 */
		private boolean allow;
		
		/**
		 * Sets if pvp override enables PvP.
		 * 
		 * @param true if allows pvp, false if denies pvp
		 */
		private PvPOverride(boolean allow) {
			this.allow = allow;
		}
		
		/**
		 * If true, then pvp will be allowed. Denied if false.
		 * 
		 * @return true if allowed, false if denied
		 */
		public boolean isAllow() {
			return allow;
		}		
		
		
	}
	
	
}
