package org.saga.player;

import java.util.Random;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.saga.chunkGroups.ChunkGroupManager;
import org.saga.chunkGroups.SagaChunk;
import org.saga.config.FactionConfiguration;
import org.saga.player.SagaEntityDamageManager.SagaPvpEvent.PvpDenyReason;

public class SagaEntityDamageManager {

	/**
	 * Random generator.
	 */
	private static Random random = new Random();
	
	
	/**
	 * Handles magic damage.
	 * 
	 * @param event event
	 * @param attacker attacker
	 * @param defender defender
	 */
	public static void handleMagicDamage2(EntityDamageByEntityEvent event, SagaPlayer attacker, SagaPlayer defender) {

		
		if(event.isCancelled()) return;

		// Only living:
		if(!(event.getEntity() instanceof LivingEntity)) return;

		LivingEntity entity = (LivingEntity) event.getEntity();

		// Damage trough armor:
		entity.damage(event.getDamage(), event.getDamager());
		
		// Take control of the event:
		event.setCancelled(true);
		
		
	}
	
	/**
	 * Handles magic damage.
	 * 
	 * @param event event
	 * @param attacker attacker
	 * @param defender defender
	 */
	public static void handleMagicDamage2(EntityDamageByEntityEvent event, SagaPlayer attacker, Creature defender) {

		
		if(event.isCancelled()) return;

		// Only living:
		if(!(event.getEntity() instanceof LivingEntity)) return;

		LivingEntity entity = (LivingEntity) event.getEntity();

		// Damage trough armor:
		entity.damage(event.getDamage(), event.getDamager());
		
		// Take control of the event:
		event.setCancelled(true);
		
		
	}
	
	/**
	 * Handles pvp.
	 * 
	 * @param attacker attacker saga player
	 * @param defender defender saga player
	 * @param event event
	 */
	public static void handlePvp(SagaPlayer attacker, SagaPlayer defender, EntityDamageByEntityEvent event) {

		
		SagaPvpEvent pvpEvent = new SagaPvpEvent(attacker, defender, event);

		
		// Faction versus faction only:
		if(FactionConfiguration.config().factionOnlyPvp){
			
			if(attacker.getRegisteredFaction() == null || !attacker.getRegisteredFaction().isFormed()){
				pvpEvent.deny(PvpDenyReason.ATTACKER_NO_FACTION);
			}
			else if(defender.getRegisteredFaction() == null || !defender.getRegisteredFaction().isFormed()){
				pvpEvent.deny(PvpDenyReason.DEFENDER_NO_FACTION);
			}
			
		}

		// Forward to attackers group:
		SagaChunk attackerChunk = ChunkGroupManager.manager().getSagaChunk(attacker.getLocation());
		if(attackerChunk != null){
			attackerChunk.onPlayerVersusPlayer(pvpEvent);
		}

		// Forward to defenders group:
		SagaChunk defenderChunk = ChunkGroupManager.manager().getSagaChunk(defender.getLocation());
		if(defenderChunk != null){
			defenderChunk.onPlayerVersusPlayer(pvpEvent);
		}
		
		// Attackers faction:
		if(attacker.getRegisteredFaction() != null){
			attacker.getRegisteredFaction().onMemberAttack(pvpEvent);
		}
		
		// Defenders faction:
		if(defender.getRegisteredFaction() != null){
			defender.getRegisteredFaction().onMemberDefend(pvpEvent);
		}
		
		// Not allowed:
		if(!pvpEvent.isAllowed()){
			
			event.setCancelled(true);
			
			// Inform:
			attacker.message(PlayerMessages.pvpDeny(pvpEvent));
			
		}
		
	}
	
	
	/**
	 * Generates rounds a integer and adds random damage of one.
	 * 
	 * @param value value
	 * @return integer with random additional damage
	 */
	public static int randomRound(Double value) {

		
		
		if (value >= 0){
		
			double decimal = value - Math.floor(value);
			
			if(random.nextDouble() < decimal){
				return value.intValue() + 1;
			}else{
				return value.intValue();
			}
			
		}else{
			
			double decimal = -value + Math.ceil(value);
			
			if(random.nextDouble() < decimal){
				return value.intValue() - 1;
			}else{
				return value.intValue();
			}
			
		}
			
			
			
		
	}

	/**
	 * @author andf
	 *
	 */
	public static class SagaPvpEvent extends EntityDamageByEntityEvent{

		
		/**
		 * Pvp cancel reason.
		 */
		private PvpDenyReason reason;
		
		/**
		 * Saga attacker.
		 */
		private SagaPlayer sagaAttacker;
		
		/**
		 * Saga defender.
		 */
		private SagaPlayer sagaDefender;
		
		/**
		 * The event will be allowed no matter what if true.
		 */
		private boolean forceAllow = false;
		
		
		// Initialization:
		/**
		 * Creates the event.
		 * 
		 * @param attacker attacker
		 * @param defender defender
		 */
		public SagaPvpEvent(SagaPlayer attacker, SagaPlayer defender, EntityDamageByEntityEvent event) {

			
			super(event.getDamager(), event.getEntity(), event.getCause(), event.getDamage());
			
			reason = PvpDenyReason.NONE;
			this.sagaAttacker = attacker;
			this.sagaDefender = defender;
			
		}

		
		// Interaction:
		/**
		 * Denies the event.
		 * 
		 * @param reason reason
		 */
		public void deny(PvpDenyReason reason) {

			this.reason = reason;
			
		}
		
		/**
		 * Force allows the event.
		 * 
		 */
		public void forceAllow() {

			this.forceAllow = true;
			
		}
		
		/**
		 * Checks if pvp is allowed.
		 * 
		 * @return true if allowed
		 */
		public boolean isAllowed() {

			// Force allowed.
			if(forceAllow) return true;
			
			return reason.equals(PvpDenyReason.NONE);
			
		}
		
		/**
		 * Gets the reason the event was canceled.
		 * 
		 * @return cancel reason
		 */
		public PvpDenyReason getReason() {
			return reason;
		}
		
		
		// Getters:
		/**
		 * Gets the saga attacker.
		 * 
		 * @return the saga attacker
		 */
		public SagaPlayer getSagaAttacker() {
			return sagaAttacker;
		}

		/**
		 * Gets the saga defender.
		 * 
		 * @return the saga defender
		 */
		public SagaPlayer getSagaDefender() {
			return sagaDefender;
		}




		public static enum PvpDenyReason{
			
			NONE,
			SAFE_AREA,
			ATTACKER_NO_FACTION,
			DEFENDER_NO_FACTION,
			SAME_FACTION,
			ALLY;
			
		}
		
	}
	
	
}
