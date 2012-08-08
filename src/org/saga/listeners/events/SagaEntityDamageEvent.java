package org.saga.listeners.events;

import java.util.PriorityQueue;
import java.util.Random;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.chunks.ChunkBundleManager;
import org.saga.chunks.SagaChunk;
import org.saga.player.SagaPlayer;
import org.saga.utility.TwoPointFunction;

public class SagaEntityDamageEvent {

	
	/**
	 * Random generator.
	 */
	private static Random RANDOM = new Random();

	
	
	
	/**
	 * Event type.
	 */
	private DamageCause type;
	
	/**
	 * Minecraft event.
	 */
	private EntityDamageEvent event;
	
	/**
	 * Projectile.
	 */
	private Projectile projectile = null;
	
	
	/**
	 * Attacker creature, null if none.
	 */
	private Creature attackerCreature = null;
	
	/**
	 * Defender creature, null if none
	 */
	private Creature defenderCreature = null;
	
	/**
	 * Attacker player, null if none.
	 */
	private SagaPlayer attackerPlayer = null;
	
	/**
	 * Defender player, null if none
	 */
	private SagaPlayer defenderPlayer = null;
	
	
	/**
	 * Attackers saga chunk.
	 */
	private SagaChunk attackerChunk = null;
	
	/**
	 * Defenders saga chunk.
	 */
	private SagaChunk defenderChunk = null;

	
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
		type = event.getCause();
		
		// Attacked by an entity:
		if(event instanceof EntityDamageByEntityEvent) attacker = ((EntityDamageByEntityEvent) event).getDamager();
		
		// Attacked by a projectile:
		if(attacker instanceof Projectile){
			
			projectile = (Projectile) attacker;
			attacker = projectile.getShooter();
			
		}else{
			
			projectile = null;
			
		}
		
		// Magic:
		if(projectile instanceof Fireball) type = DamageCause.MAGIC;
		
		// Get attacker saga player:
		if(attacker instanceof Player){

			attackerPlayer = Saga.plugin().getSagaPlayer( ((Player) attacker).getName() );

	    	// No player:
	    	if(attackerPlayer == null){
	    		SagaLogger.severe(getClass(), "failed to load saga player for "+ ((Player) attacker).getName());
	    		return;
	    	}
	    	
		}

		// Get attacker creature:
		else if(attacker instanceof Creature){
			
			attackerCreature = (Creature)attacker;
			
		}
		
		// Get defender saga player:
		if(defender instanceof Player){

			defenderPlayer = Saga.plugin().getSagaPlayer( ((Player) defender).getName() );

	    	// No player:
	    	if(defenderPlayer == null){
	    		SagaLogger.severe(getClass(), "failed to load saga player for "+ ((Player) attacker).getName());
	    		return;
	    	}
			
		}
		
		// Get defender creature:
		else if(defender instanceof Creature){
			
			defenderCreature = (Creature)defender;
			
		}
		
		// Get chunks:
		if(attacker != null) attackerChunk = ChunkBundleManager.manager().getSagaChunk(attacker.getLocation());
		if(defender != null) defenderChunk = ChunkBundleManager.manager().getSagaChunk(defender.getLocation());
		

	}
	
	
	
	// Modify:
	/**
	 * Modifies damage.
	 * 
	 * @param amount amount to modify
	 */
	public void modifyDamage(double amount) {
		modifier+=amount;
	}
	
	/**
	 * Multiplies damage.
	 * 
	 * @param amount amount to multiply by
	 */
	public void multiplyDamage(double amount) {
		multiplier+=amount;
	}
	
	/**
	 * Divides damage.
	 * 
	 * @param amount amount to divide by
	 */
	public void divideDamage(double amount) {
		multiplier/=amount;
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
	
	
	
	// Types:
	/**
	 * Checks if the attack is physical.
	 * 
	 * @return true if physical
	 */
	public boolean isPhysical() {

		return projectile == null && type == DamageCause.ENTITY_ATTACK;

	}
	
	/**
	 * Checks if the attack is ranged.
	 * 
	 * @return true if ranged
	 */
	public boolean isRanged() {

		return projectile instanceof Arrow && type == DamageCause.PROJECTILE;

	}
	
	/**
	 * Checks if the attack is magic.
	 * 
	 * @return true if magic
	 */
	public boolean isMagic() {

		return projectile instanceof Fireball && type == DamageCause.MAGIC;

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
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public DamageCause getType() {
		return type;
	}

	
	/**
	 * Checks if player attacked a player.
	 * 
	 * @return true if player versus player
	 */
	public boolean isPlayerAttackPlayer() {

		return attackerPlayer != null && defenderPlayer != null;

	}
	
	/**
	 * Checks if player attacked a creature.
	 * 
	 * @return true if player versus player
	 */
	public boolean isPlayerAttackCreature() {

		return attackerPlayer != null && defenderCreature != null;

	}
	
	/**
	 * Checks if creature attacked a creature.
	 * 
	 * @return true if player versus player
	 */
	public boolean isCreatureAttackCreature() {

		return attackerCreature != null && defenderCreature != null;

	}

	/**
	 * Checks if creature attacked a player.
	 * 
	 * @return true if player versus player
	 */
	public boolean isCreatureAttackPlayer() {

		return attackerCreature != null && defenderPlayer != null;

	}

	
	/**
	 * Gets the attackerCreature.
	 * 
	 * @return the attackerCreature
	 */
	public Creature getAttackerCreature() {
	
	
		return attackerCreature;
	}
	
	/**
	 * Gets the defenderCreature.
	 * 
	 * @return the defenderCreature
	 */
	public Creature getDefenderCreature() {
	
	
		return defenderCreature;
	}
	
	/**
	 * Gets the attackerPlayer.
	 * 
	 * @return the attackerPlayer
	 */
	public SagaPlayer getAttackerPlayer() {
	
	
		return attackerPlayer;
	}
	
	/**
	 * Gets the defenderPlayer.
	 * 
	 * @return the defenderPlayer
	 */
	public SagaPlayer getDefenderPlayer() {
	
	
		return defenderPlayer;
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
	 * Gets the attackerChunk.
	 * 
	 * @return the attackerChunk
	 */
	public SagaChunk getAttackerChunk() {
	
	
		return attackerChunk;
	}

	/**
	 * Gets the defenderChunk.
	 * 
	 * @return the defenderChunk
	 */
	public SagaChunk getDefenderChunk() {
	
	
		return defenderChunk;
	}
	
	
	/**
	 * Checks if the event is faction versus faction.
	 * 
	 * @return true if faction versus faction.
	 */
	public boolean isFactionAttackFaction() {

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
	 * Pvp overrides.
	 * 
	 * @author andf
	 *
	 */
	public enum PvPOverride{
		
		ADMIN_ALLOW(true),
		ADMIN_DENY(false),
		
		SELF_ALLOW(true),
		
		FACTION_CLAIMING_ALLOW(true),
		
		ARENA_ALLOW(true),
		
		SAFE_AREA_DENY(false),
		FACTION_ONLY_PVP_DENY(false),
		SAME_FACTION_DENY(false),
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
	
	
	// TODO: Explode on attack.
	
	
}
