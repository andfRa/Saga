package org.saga.buildings;

import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.listeners.events.SagaEntityDamageEvent.PvPOverride;

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
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerDamagedByPlayer(org.bukkit.event.entity.EntityDamageByEntityEvent, org.saga.SagaPlayer, org.saga.SagaPlayer)
	 */
	@Override
	public void onEntityDamage(SagaEntityDamageEvent event){

		
		if(event.isPvP()){
			event.addPvpOverride(PvPOverride.SAFE_AREA_DENY);
		}
		
		
	}

	
	
	
}
