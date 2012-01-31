package org.saga.buildings;

import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.saga.Clock;
import org.saga.Clock.SecondTicker;
import org.saga.Saga;
import org.saga.buildings.signs.BreakStoneSign;
import org.saga.buildings.signs.BuildingSign;
import org.saga.buildings.signs.RepairStoneSign;
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
	
	// Initialization:
	/**
	 * Initializes
	 * 
	 * @param pointCost point cost
	 * @param moneyCost money cost
	 * @param proficiencies proficiencies
	 */
	private Academy(String name) {
		
		
		super("");
		
		cooldown = 0;
		clockActive = false;
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#completeExtended()
	 */
	@Override
	public boolean completeExtended() {
		

		boolean integrity = true;
		
		if(cooldown == null){
			cooldown = 0;
			Saga.severe(this, "cooldown field failed to initialize", "setting default");
			integrity = false;
		}
//		for (int i = 0; i < signs.size(); i++) {
//			
//			try {
//				signs.get(i).complete(this);
//			} catch (SignException e) {
//				Saga.severe(this, "failed to initialize signs field element: " + e.getClass().getSimpleName() + ":" + e.getMessage(), "removing element");
//				signs.remove(i);
//				i--;
//				continue;
//			}
//			
//		}
		
		
		
		return integrity;
		
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#blueprint()
	 */
	@Override
	public Building blueprint() {
		return new Academy("");
	}

	
	// Cooldown:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.SecondTicker#clockSecondTick()
	 */
	@Override
	public void clockSecondTick() {
		
		
		cooldown--;
		
		if(cooldown <= 0){
			stopClock();
		}
		
		
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
		
		this.cooldown = getDefinition().getLevelFunction().calculateValue(getLevel()).intValue();
		
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
	public boolean isBuildingSignExtended(String firstLine) {
		
		if(firstLine.equalsIgnoreCase(RepairStoneSign.SIGN_NAME)) return true;
		
		if(firstLine.equalsIgnoreCase(BreakStoneSign.SIGN_NAME)) return true;
		
		return false;
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#createBuildingSignExtended(org.bukkit.block.Sign, org.bukkit.event.block.SignChangeEvent)
	 */
	@Override
	protected BuildingSign createBuildingSignExtended(Sign sign, SignChangeEvent event) {
		
		
		// Stone fix sign:
		if(event.getLine(0).equalsIgnoreCase(RepairStoneSign.SIGN_NAME)){
			
			return RepairStoneSign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
			
		}
		
		// Stone break sign:
		if(event.getLine(0).equalsIgnoreCase(BreakStoneSign.SIGN_NAME)){
			
			return BreakStoneSign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
			
		}
		
		return null;
		
		
	}
	
	
}
