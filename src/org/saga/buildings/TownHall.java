package org.saga.buildings;

import org.bukkit.entity.Creature;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.listeners.events.SagaEntityDamageEvent.PvPFlag;
import org.saga.player.SagaPlayer;

public class TownHall extends Building{

	/**
	 * Blacklist sign.
	 */
	transient public static String BLACKLIST_SIGN = "=[blacklist]=";
	

	// Initialisation:
	/**
	 * Creates a building from the definition.
	 * 
	 * @param definition building definition
	 */
	public TownHall(BuildingDefinition definition) {
		
		super(definition);
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#enable()
	 */
	@Override
	public void enable() {
		
		super.enable();
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#disable()
	 */
	@Override
	public void disable() {

		super.disable();

	}

	
	// Events:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerDamagedByPlayer(org.bukkit.event.entity.EntityDamageByEntityEvent, org.saga.SagaPlayer, org.saga.SagaPlayer)
	 */
	@Override
	public void onPvP(SagaEntityDamageEvent event){
		
		// Deny pvp:
		event.addFlag(PvPFlag.SAFE_AREA);
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerDamagedByCreature(org.bukkit.event.entity.EntityDamageByEntityEvent, org.bukkit.entity.Creature, org.saga.SagaPlayer)
	 */
	@Override
	public void onPlayerDamagedByCreature(EntityDamageByEntityEvent event, Creature damager, SagaPlayer damaged) {

		// Disable cvp:
		event.setCancelled(true);
		
	}

	
	
	
}
