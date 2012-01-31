package org.saga.abilities;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.Saga;
import org.saga.exceptions.InvalidLocationException;
import org.saga.factions.FactionMessages;
import org.saga.factions.SagaFaction;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;

public class Mobilize extends Ability{

	
	// Initialization:
	/**
	 * Initializes using definition.
	 * 
	 * @param definition ability definition
	 */
	public Mobilize(AbilityDefinition definition) {
		
        super(definition);
	
	}

	
	// Usage:
	@Override
	public boolean instant(PlayerInteractEvent event) {
		

		// Check pre use:
		if(!handlePreUse()){
			return false;
		}
		
		SagaPlayer sagaPlayer = getSagaPlayer();
		SagaFaction faction = sagaPlayer.getRegisteredFaction();
		Location location = sagaPlayer.getLocation();
		
		// Check faction:
		if(faction == null){
			sagaPlayer.message(FactionMessages.noFaction());
			return false;
		}
		
		// Check location:
		if(location == null){
			sagaPlayer.error("failed to retrieve location");
			Saga.severe(this, "failed to retrieve location", "ignoring request");
			return false;
		}
		
		// Set mobilization point:
		faction.setRallyPoint(location, getDefinition().getPrimaryFunction().calculateValue(getSkillLevel()).intValue());

		// Inform:
		faction.broadcast(FactionMessages.mobilizationDeclare(faction, sagaPlayer));
		
		// Info:
		faction.broadcast(FactionMessages.mobilizationDeclareInfo(faction));

		// Award exp:
		Integer awardedExp = awardExperience();
		
		// Statistics:
		StatisticsManager.manager().onAbilityUse(getName(), awardedExp);
		
		return true;
		
		
	}
	
	
	// Types:
	/**
	 * Rally location.
	 * 
	 * @author andf
	 *
	 */
	public static class RallyPoint{

		
		/**
		 * World name.
		 */
		private String worldn;
		
		/**
		 * X coordinate.
		 */
		private Double x;
		
		/**
		 * Y coordinate.
		 */
		private Double y;
		
		/**
		 * Z coordinate.
		 */
		private Double z;
		
		/**
		 * The time the rally point has remaining.
		 */
		private Integer remaining;
		
		/**
		 * Location.
		 */
		transient Location location;
		
		
		// Initialization:
		/**
		 * Sets location and time remaining.
		 * 
		 * @param location location
		 * @param timeRemaining time remaining
		 */
		public RallyPoint(Location location, Integer timeRemaining) {

			this.location = location;
			
			this.worldn = location.getWorld().getName();
			this.x= location.getX();
			this.y= location.getY();
			this.z= location.getZ();
			
			this.remaining = timeRemaining;
			
		}
		
		/**
		 * Goes trough all the fields and makes sure everything has been set after gson load.
		 * If not, it fills the field with defaults.
		 * 
		 * @return true if everything was correct.
		 */
		public boolean complete() throws InvalidLocationException{
			
			
			boolean integrity = true;
			
			if (worldn == null) {
				Saga.severe(this, "world field failed to initialize", "stopping complete");
				throw new InvalidLocationException(worldn, x, y, z);
			}
			
			if (x == null) {
				Saga.severe(this, "x field failed to initialize", "stopping complete");
				throw new InvalidLocationException(worldn, x, y, z);
			}
			
			if (y == null) {
				Saga.severe(this, "y field failed to initialize", "stopping complete");
				throw new InvalidLocationException(worldn, x, y, z);
			}
			
			if (z == null) {
				Saga.severe(this, "z field failed to initialize", "stopping complete");
				throw new InvalidLocationException(worldn, x, y, z);
			}
			
			World world = Saga.plugin().getServer().getWorld(worldn);
			if(world == null){
				Saga.severe(this, "world name is invalid", "stopping complete");
				throw new InvalidLocationException(worldn, x, y, z);
			}
			
			if (remaining == null) {
				Saga.severe(this, "remaining field failed to initialize", "setting default");
				remaining = 0;
				integrity = false;
			}
			
			// Transient:
			location= new Location(world, x, y, z);
			
			return integrity;
			

		}
		
		
		// Interaction:
		/**
		 * Gets the location.
		 * 
		 * @return location
		 */
		public Location getLocation() {
			return location;
		}
		
		/**
		 * Gets the time remaining.
		 * 
		 * @return time remaining
		 */
		public Integer getTimeRemaining() {
			return remaining;
		}
		
		
		
		/**
		 * Decreases the time remaining.
		 * 
		 * @param amount amount to decrease by
		 */
		public void decreaseTime(int amount) {
			remaining -= amount;
		}
		
		/**
		 * Decreases the time remaining.
		 */
		public void decreaseTime() {
			decreaseTime(1);
		}
		
		
	}
	
}
