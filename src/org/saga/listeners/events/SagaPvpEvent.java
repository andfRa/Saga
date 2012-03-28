package org.saga.listeners.events;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.saga.chunkGroups.SagaChunk;
import org.saga.player.SagaPlayer;

/**
 * @author andf
 *
 */
public class SagaPvpEvent{

	
	/**
	 * Deny reason.
	 */
	private PvpDenyReason denyReason;

	/**
	 * Allow Reason.
	 */
	private PvpAllowReason allowReason;
	
	/**
	 * Saga attacker.
	 */
	private SagaPlayer attacker;
	
	/**
	 * Saga defender.
	 */
	private SagaPlayer defender;
	
	/**
	 * Saga attackers chunk, null if none.
	 */
	private SagaChunk attackerChunk;
	
	/**
	 * Saga defenders chunk, null if none.
	 */
	private SagaChunk defenderChunk;
	
	/**
	 * Wrapped event.
	 */
	private EntityDamageByEntityEvent wrapped;
	
	
	// Initialization:
	/**
	 * Creates a player versus player event.
	 * 
	 * @param attacker attacker
	 * @param defender defender
	 * @param attackerChunk attackers saga chunk, null if none
	 * @param defenderChunk defenders saga chunk, null if none
	 * @param event bukkit event
	 */
	public SagaPvpEvent(SagaPlayer attacker, SagaPlayer defender, SagaChunk attackerChunk, SagaChunk defenderChunk, EntityDamageByEntityEvent event) {

		
		this.denyReason = PvpDenyReason.NONE;
		this.allowReason = PvpAllowReason.NONE;
		this.attacker = attacker;
		this.defender = defender;
		this.attackerChunk = attackerChunk;
		this.defenderChunk = defenderChunk;
		this.wrapped = event;
		
		
	}

	
	// Getters:
	/**
	 * Gets the denyReason.
	 * 
	 * @return the denyReason
	 */
	public PvpDenyReason getDenyReason() {
	
	
		return denyReason;
	}

	/**
	 * Sets the denyReason.
	 * 
	 * @param denyReason the denyReason to set
	 */
	public void setDenyReason(PvpDenyReason denyReason) {
		this.denyReason = denyReason;
	}

	/**
	 * Gets the allowReason.
	 * 
	 * @return the allowReason
	 */
	public PvpAllowReason getAllowReason() {
		return allowReason;
	}

	/**
	 * Sets the allowReason.
	 * 
	 * @param allowReason the allowReason to set
	 */
	public void setAllowReason(PvpAllowReason allowReason) {
		this.allowReason = allowReason;
	}
	
	/**
	 * Gets the attacker.
	 * 
	 * @return the attacker
	 */
	public SagaPlayer getAttacker() {


		return attacker;
	}
	
	/**
	 * Gets the defender.
	 * 
	 * @return the defender
	 */
	public SagaPlayer getDefender() {
		return defender;
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
	 * Gets the wrapped event.
	 * 
	 * @return wrapped entity damage by entity event
	 */
	public EntityDamageByEntityEvent getWrappedEvent() {
		return wrapped;
	}

	// Types:
	/**
	 * Deny reason.
	 * 
	 * @author andf
	 *
	 */
	public static enum PvpDenyReason{
		
		NONE,
		SAFE_AREA,
		ATTACKER_NO_FACTION,
		DEFENDER_NO_FACTION,
		SAME_FACTION,
		ALLY;
		
	}
	
	/**
	 * Allow reason.
	 * 
	 * @author andf
	 *
	 */
	public static enum PvpAllowReason{
		
		NONE,
		ARENA;
		
	}
	

}
