package org.saga.buildings;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.saga.Clock;
import org.saga.Clock.SecondTicker;
import org.saga.buildings.signs.BuildingSign;
import org.saga.buildings.signs.RespecSign;
import org.saga.chunkGroups.SagaChunk;
import org.saga.player.SagaPlayer;

public class Temple extends Building implements SecondTicker{

	
	/**
	 * Clock is enabled if true.
	 */
	transient private boolean clockEnabled;

	
	// Initialisation:
	/**
	 * Creates a building from the definition.
	 * 
	 * @param definition building definition
	 */
	public Temple(BuildingDefinition definition) {
		
		super(definition);
		
		clockEnabled = false;
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#enable()
	 */
	@Override
	public void enable() {
		
		
		super.enable();
		
		// Enable clock:
		if(countPlayers() > 0){
			enableClock();
		}
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#disable()
	 */
	public void disable() {
		
		
		super.disable();
		
		// Disable clock if enabled:
		if (isClockEnabled()) {
			disableClock();
		}
		
		
	}
	
	
	// Signs:
	@Override
	protected boolean isBuildingSign(String firstLine) {
	
		if(firstLine.equalsIgnoreCase(RespecSign.SIGN_NAME)) return true;
		
		return super.isBuildingSign(firstLine);
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#createBuildingSign(org.bukkit.block.Sign, org.bukkit.event.block.SignChangeEvent)
	 */
	@Override
	protected BuildingSign createBuildingSign(Sign sign, SignChangeEvent event) {
	

		// Respec sign:
		if(event.getLine(0).equalsIgnoreCase(RespecSign.SIGN_NAME)){
			
			return RespecSign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
			
		}
		
		
		return super.createBuildingSign(sign, event);
		
	
	}
	
	
	// Clock:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.SecondTicker#clockSecondTick()
	 */
	@Override
	public boolean clockSecondTick() {

		
		// Disable clock if no players:
		if(countPlayers() == 0) disableClock();
		
		// Level too low:
		if(getDefinition().getLevelFunction().getXMin() > getLevel()){
			return true;
		}
		
		// Get saga players:
		SagaChunk sagaChunk = getSagaChunk();
		if(sagaChunk == null) return true;
		ArrayList<SagaPlayer> sagaPlayers = sagaChunk.getSagaPlayers();
		
		return true;
		
		
	}
	
	/**
	 * Enables the clock.
	 * 
	 */
	private void enableClock() {

		Clock.clock().registerSecondTick(this);
		
		clockEnabled = true;
		
	}
	
	/**
	 * Checks if clock is enabled.
	 * 
	 * @return true if enabled.
	 */
	public boolean isClockEnabled() {
		return clockEnabled;
	}
	
	/**
	 * Disable the clock.
	 * 
	 */
	private void disableClock() {

		Clock.clock().unregisterSecondTick(this);
		
		clockEnabled = false;
		
	}
	
	
	// Experience restore:
	/**
	 * Counts the players.
	 * 
	 * @return player count
	 */
	private int countPlayers() {

		
		SagaChunk sagaChunk = getSagaChunk();
		
		if(sagaChunk == null){
			return 0;
		}
		
		return sagaChunk.countPlayers();
		
		
	}
	
	
	// Events:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerSagaChunkChange(org.saga.player.SagaPlayer, org.saga.chunkGroups.SagaChunk, org.saga.chunkGroups.SagaChunk, org.bukkit.Location, org.bukkit.Location, org.bukkit.event.player.PlayerMoveEvent)
	 */
	@Override
	public void onPlayerEnter(SagaPlayer sagaPlayer, Building last) {
	
		
		// Enable clock:
		if(!isClockEnabled()) enableClock();
	
	
	}
	
	
}
