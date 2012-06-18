package org.saga.buildings;

import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.saga.Clock;
import org.saga.Clock.SecondTicker;
import org.saga.SagaLogger;
import org.saga.buildings.signs.BuildingSign;
import org.saga.buildings.signs.GuardianRuneSign;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.utility.Cooldown;


public class Academy extends Building implements SecondTicker, Cooldown{

	
	/**
	 * Building cooldown.
	 */
	private Integer cooldown;
	
	/**
	 * True if clock active.
	 */
	transient private boolean clockActive; 
	
	
	// Initialisation:
	/**
	 * Creates a building from the definition.
	 * 
	 * @param definition building definition
	 */
	public Academy(BuildingDefinition definition) {
		
		
		super(definition);

		cooldown = 0;
		clockActive = false;
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#completeExtended()
	 */
	@Override
	public boolean complete() throws InvalidBuildingException {
		

		boolean integrity = super.complete();
		
		if(cooldown == null){
			cooldown = 0;
			SagaLogger.nullField(this, "cooldown");
			integrity = false;
		}
		
		return integrity;
		
		
	}

	
	// Cooldown:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.SecondTicker#clockSecondTick()
	 */
	@Override
	public boolean clockSecondTick() {
		
		
		cooldown--;
		
		if(cooldown <= 0){
			stopClock();
		}

		return true;
		
		
	}
	
	/**
	 * Starts the clock:
	 * 
	 */
	private void startClock() {
		Clock.clock().registerSecondTick(this);
		clockActive = true;
	}
	
	/**
	 * Stops the clock:
	 * 
	 */
	private void stopClock() {
		Clock.clock().unregisterSecondTick(this);
		clockActive = false;
	}
	
	/**
	 * Checks if the clock is active.
	 * 
	 * @return
	 */
	public boolean isClockActive() {
		return clockActive;
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.utility.Cooldown#isOnCooldown()
	 */
	public boolean isOnCooldown() {
		return cooldown > 0;
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.utility.Cooldown#startCooldown()
	 */
	public void startCooldown() {
		
		this.cooldown = getDefinition().getLevelFunction().value(getLevel()).intValue();
		
		// Start clock:
		if(!isClockActive()){
			startClock();
		}
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.utility.Cooldown#getCooldown()
	 */
	public int getCooldown() {
		return cooldown;
	}
	
	
	// Signs:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#isBuildingSign(java.lang.String)
	 */
	@Override
	public boolean isBuildingSign(String firstLine) {
		
		if(firstLine.equalsIgnoreCase(GuardianRuneSign.SIGN_NAME)) return true;
		
		return super.isBuildingSign(firstLine);
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#createBuildingSign2(org.bukkit.block.Sign, org.bukkit.event.block.SignChangeEvent)
	 */
	@Override
	protected BuildingSign createBuildingSign(Sign sign, SignChangeEvent event) {
		
		
		// Stone fix sign:
		if(event.getLine(0).equalsIgnoreCase(GuardianRuneSign.SIGN_NAME)){
			
			return GuardianRuneSign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
			
		}else{
			
		}
		
		return super.createBuildingSign(sign, event);
		
		
	}
	
	
}
