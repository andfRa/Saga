package org.saga.attributes;

import org.bukkit.event.Event;
import org.saga.Saga;
import org.saga.SagaPlayerListener.SagaPlayerProjectileShotEvent;
import org.saga.SagaPlayerListener.SagaPlayerProjectileShotEvent.ProjectileType;
import org.saga.player.SagaPlayer;

public class ProjectileShotAttribute extends Attribute{

	
	/**
	 * Projectile type.
	 */
	private ProjectileType projectileType;
	
	
	/**
	 * Sets name and projectile type.
	 * 
	 * @param name name
	 * @param projectileType projectile type
	 */
	public ProjectileShotAttribute(String name, ProjectileType projectileType) {
		super(name, DisplayType.OFFENCE);
		this.projectileType = projectileType;
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.attributes.Attribute#completeExtended()
	 */
	@Override
	protected boolean completeExtended() {
		
		
		boolean integrity = true;

		if(projectileType==null){
			projectileType = ProjectileType.NONE;
			Saga.info("Setting default value for "+getName()+" attribute projectileType.");
			integrity = false;
		}
		
		return integrity;
		
		
	}

	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.attributes.Attribute#use(java.lang.Short, org.saga.SagaPlayer, org.bukkit.event.Event)
	 */
	@Override
	public void use(Short attributeUpgrade, SagaPlayer sagaPlayer, Event event) {
		
		
		// Ignore if invalid event:
		if(!(event instanceof SagaPlayerProjectileShotEvent)){
			return;
		}
		SagaPlayerProjectileShotEvent cEvent = (SagaPlayerProjectileShotEvent) event;
		
		// Check type:
		if(!checkProjectile(cEvent)){
			return;
		}

		// Increase speed:
		cEvent.increaseSpeed(calculateValue(attributeUpgrade));
		
		
	}

	/**
	 * Checks if the projectile type is correct.
	 * 
	 * @param event event
	 * @return true if correct
	 */
	private boolean checkProjectile(SagaPlayerProjectileShotEvent event) {

		return projectileType.equals(event.getProjectileType());
		
	}
	
	
	
}
