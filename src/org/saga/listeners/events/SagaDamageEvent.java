package org.saga.listeners.events;

import java.util.PriorityQueue;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.Saga;
import org.saga.attributes.DamageType;
import org.saga.config.AttributeConfiguration;
import org.saga.config.VanillaConfiguration;
import org.saga.dependencies.SagaMobsDependency;
import org.saga.factions.Faction;
import org.saga.player.SagaLiving;
import org.saga.player.SagaPlayer;
import org.saga.settlements.BundleManager;
import org.saga.settlements.SagaChunk;
import org.saga.utility.TwoPointFunction;

public class SagaDamageEvent {

	
	/**
	 * Damage type.
	 */
	public DamageType type;
	
	/**
	 * Projectile.
	 */
	public final Projectile projectile;
	
	/**
	 * Tool material.
	 */
	public final Material tool;
	
	
	/**
	 * Attacker creature, null if none.
	 */
	public final Creature creatureAttacker;
	
	/**
	 * Defender creature, null if none
	 */
	public final Creature creatureDefender;
	
	/**
	 * Attacker Saga entity, null if none.
	 */
	public final SagaLiving sagaAttacker;
	
	/**
	 * Defender saga entity, null if none
	 */
	public final SagaLiving sagaDefender;
	
	
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
	private double modifier = 0.0;
	
	/**
	 * Damage multiplier.
	 */
	private double multiplier = 1.0;

	/**
	 * Hit chance modifier.
	 */
	private double hitChance = 1.0;

	/**
	 * Armour penetration modifier.
	 */
	private double penetration = 0.0;

	/**
	 * Enchantment penetration modifier.
	 */
	private double disenchant = 0.0;

	/**
	 * Block multiplier.
	 */
	private double blocking = VanillaConfiguration.getBlockingMultiplier();
	
	/**
	 * Penalty multiplier.
	 * Prevents massive damage from weak attacks.
	 */
	private double penalty = 1.0;
	
	/**
	 * Tool sloppiness multiplier.
	 */
	private double sloppiness = 1.0;

	/**
	 * PvP override.
	 */
	private PriorityQueue<PvPOverride> pvpOverride = new PriorityQueue<SagaDamageEvent.PvPOverride>();
	
	
	
	// Initiate:
	/**
	 * Extracts saga entities and chunks.
	 * 
	 * @param event event
	 * @param defender defender entity
	 */
	public SagaDamageEvent(EntityDamageEvent event) {
		
		
		Entity attacker = null;
		LivingEntity defender = null;

		this.event = event;
		type = DamageType.getDamageType(event);
		if(event.getEntity() instanceof LivingEntity) defender = (LivingEntity) event.getEntity();
		
		// Damage penalty:
		// Prevents massive damage from weak attacks.
		double tresh = AttributeConfiguration.config().getPenaltyValue(type);
		if(tresh > 0 && event.getDamage() < tresh) penalty = event.getDamage() / tresh;
		
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

			sagaAttacker = Saga.plugin().getLoadedPlayer( ((Player) attacker).getName() );
			creatureAttacker = null;
			
			// Tool:
			tool = ((Player) attacker).getItemInHand().getType();
			
		}

		// Get attacker creature:
		else if(attacker instanceof Creature){
			
			creatureAttacker = (Creature)attacker;
			sagaAttacker = SagaMobsDependency.findSagaCreature(creatureAttacker);
			tool = Material.AIR;
			
		}else{
			
			creatureAttacker = null;
			sagaAttacker = null;
			tool = Material.AIR;
			
		}
		
		// Get defender saga player:
		if(defender instanceof Player){

			creatureDefender = null;
			sagaDefender = Saga.plugin().getLoadedPlayer( ((Player) defender).getName() );
			
		}
		
		// Get defender creature:
		else if(defender instanceof Creature){
			
			creatureDefender = (Creature)defender;
			sagaDefender = SagaMobsDependency.findSagaCreature(creatureDefender);
			
		}
		
		else{
			
			creatureDefender = null;
			sagaDefender = null;
			
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
	 * Modifies enchantment penetration.
	 * 
	 * @param amount amount to modify
	 */
	public void modifyEnchantPenetration(double amount) {
		disenchant+=amount;
	}
	
	/**
	 * Modifies blocking.
	 * 
	 * @param amount amount to modify
	 */
	public void modifyBlocking(double amount) {
		blocking-= amount;
	}
	
	/**
	 * Modifies tool handling.
	 * 
	 * @param amount amount to modify
	 */
	public void modifyToolHandling(double amount) {
		sloppiness-= amount;
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

		
		// Ignore cancelled:
		if(isCancelled()) return;

		// Dodge:
		if(hitChance <= Saga.RANDOM.nextDouble()) {
			event.setCancelled(true);
			return;
		}
		
		// Modify damage:
		double damage = calcDamage();
		event.setDamage((int)Math.ceil(damage));
		
		// Apply damage to player:
		if(sagaDefender != null){
			
			double harm = damage;
			
			// Armour:
			double armour = VanillaConfiguration.getArmourMultiplier(event, sagaDefender.getWrapped()) + penetration;
			if(armour < 0.0) armour = 0.0;
			if(armour > 1.0) armour = 1.0;
			harm*= armour;
		
			// Enchantments:
			double ench = VanillaConfiguration.getEPFMultiplier(event, sagaDefender.getWrapped()) + disenchant;
			if(ench < 0.0) ench = 0.0;
			if(ench > 1.0) ench = 1.0;
			harm*= ench;
			
			// Blocking:
			if(VanillaConfiguration.checkBlocking(event, sagaDefender.getWrapped())) harm*= blocking;
			
			// Apply Saga damage:
			sagaDefender.getWrapped().damage((int)Math.ceil(harm));
			
			// Take control:
			event.setDamage(0);
			
		}
		
		// Reduce tool damage:
		final int undurability;
		if(sagaAttacker != null) undurability = sagaAttacker.getHandItem().getDurability();
		else undurability = 0;
		
		// Schedule for next tick:
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Saga.plugin(), new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				
				// Tool damage reduction:
				if(sagaAttacker instanceof SagaPlayer){
					Player player = ((SagaPlayer)sagaAttacker).getPlayer();
					ItemStack item = player.getItemInHand();
					int damage = item.getDurability() - undurability;
					damage = TwoPointFunction.randomRound(sloppiness * damage).shortValue();
					int pundurability = item.getDurability();
					item.setDurability((short) (undurability + damage));
					if(item.getDurability() != pundurability) player.updateInventory();
				}
				
			}
		}, 1);
		
		
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
	 * Calculates damage.
	 * 
	 * @return damage
	 */
	public double calcDamage() {

		return (event.getDamage() + modifier) * multiplier * penalty;
		
	}
	
	/**
	 * Checks if the defender player is blocking.
	 * 
	 * @return true if blocking
	 */
	public boolean isBlocking() {
		if(sagaDefender == null) return false;
		return VanillaConfiguration.checkBlocking(event, sagaDefender.getWrapped());
	}
	
	/**
	 * Checks if player attacked a player.
	 * 
	 * @return true if player versus player
	 */
	public boolean isPvP() {

		return sagaAttacker instanceof SagaPlayer && sagaDefender instanceof SagaPlayer;

	}
	
	/**
	 * Checks if player attacked a creature.
	 * 
	 * @return true if player versus player
	 */
	public boolean isPvC() {

		return sagaAttacker instanceof SagaPlayer && creatureDefender != null;

	}
	
	/**
	 * Checks if creature attacked a creature.
	 * 
	 * @return true if player versus player
	 */
	public boolean isCvC() {

		return creatureAttacker != null && creatureDefender != null;

	}

	/**
	 * Checks if creature attacked a player.
	 * 
	 * @return true if player versus player
	 */
	public boolean isCvP() {

		return creatureAttacker != null && sagaDefender instanceof SagaPlayer;

	}

	/**
	 * Checks if the event is faction versus faction.
	 * 
	 * @return true if faction versus faction.
	 */
	public boolean isFvF() {
		
		if(!(sagaAttacker instanceof SagaPlayer) || !(sagaDefender  instanceof SagaPlayer)) return false;
		return ((SagaPlayer) sagaAttacker).getFaction() != null && ((SagaPlayer) sagaDefender).getFaction() != null;
		
	}


	/**
	 * Gets the attacker.
	 * 
	 * @return attacker, null if none
	 */
	public LivingEntity getAttacker() {
		if(sagaAttacker != null) return sagaAttacker.getWrapped();
		return creatureAttacker;
	}

	/**
	 * Gets the defender.
	 * 
	 * @return defender, null if none
	 */
	public LivingEntity getDefender() {
		if(sagaDefender != null) return sagaDefender.getWrapped();
		return creatureDefender;
	}
	

	/**
	 * Gets attacker faction.
	 * 
	 * @return attacker faction, null if none
	 */
	public Faction getAttackerFaction() {
		if(sagaAttacker instanceof SagaPlayer) return ((SagaPlayer) sagaAttacker).getFaction();
		return null;
	}

	/**
	 * Gets defender faction.
	 * 
	 * @return defender faction, null if none
	 */
	public Faction getDefenderFaction() {
		if(sagaDefender instanceof SagaPlayer) return ((SagaPlayer) sagaDefender).getFaction();
		return null;
	}
	
	
	
	/**
	 * Gets base damage.
	 * 
	 * @return base damage
	 */
	public int getBaseDamage() {
		return event.getDamage();
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
		RESPAWN_DENY(false),
		SELF_ALLOW(true),
		ARENA_ALLOW(true),
		SAME_FACTION_DENY(false),
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
