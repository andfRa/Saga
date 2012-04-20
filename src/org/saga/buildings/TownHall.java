package org.saga.buildings;

import org.bukkit.entity.Creature;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.saga.listeners.events.SagaPvpEvent;
import org.saga.listeners.events.SagaPvpEvent.PvpDenyReason;
import org.saga.player.SagaPlayer;

public class TownHall extends Building{

	/**
	 * Blacklist sign.
	 */
	transient public static String BLACKLIST_SIGN = "=[blacklist]=";
	
	
	// Initialization:
	/**
	 * Initializes
	 * 
	 * @param pointCost point cost
	 * @param moneyCost money cost
	 * @param proficiencies proficiencies
	 */
	private TownHall(String name) {
		
		super("");
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#completeExtended()
	 */
	@Override
	public boolean completeExtended() {
		

		boolean integrity = true;
		
		return integrity;
		
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#blueprint()
	 */
	@Override
	public Building blueprint() {
		return new TownHall("");
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
	public void onPvP(SagaPvpEvent event){
		
		// Deny pvp:
		event.setDenyReason(PvpDenyReason.SAFE_AREA);
		
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
