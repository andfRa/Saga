package org.saga.utility;

import org.bukkit.Location;
import org.bukkit.World;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.exceptions.InvalidLocationException;

/**
 * Rally location.
 * 
 * @author andf
 * 
 */
public class SagaLocation {

	
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
	 * Yaw coordinate.
	 */
	private Float yaw;
	
	/**
	 * Pitch coordinate.
	 */
	private Float pitch;

	
	/**
	 * Location.
	 */
	transient Location location;

	
	// Initialization:
	/**
	 * Sets location and time remaining.
	 * 
	 * @param location location
	 */
	public SagaLocation(Location location) {


		this.location = location;

		this.worldn = location.getWorld().getName();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.yaw = location.getYaw();
		this.pitch = location.getPitch();
		
		
	}

	/**
	 * Goes trough all the fields and makes sure everything has been set after
	 * gson load. If not, it fills the field with defaults.
	 * 
	 * @return true if everything was correct.
	 */
	public boolean complete() throws InvalidLocationException {


		boolean integrity = true;

		if (worldn == null) {
			SagaLogger.severe(this, "world field invalid");
			throw new InvalidLocationException(worldn, x, y, z);
		}

		if (x == null) {
			SagaLogger.severe(this, "x field invalid");
			throw new InvalidLocationException(worldn, x, y, z);
		}

		if (y == null) {
			SagaLogger.severe(this, "y field invalid");
			throw new InvalidLocationException(worldn, x, y, z);
		}

		if (z == null) {
			SagaLogger.severe(this, "z field invalid");
			throw new InvalidLocationException(worldn, x, y, z);
		}
		
		if (yaw == null) {
			SagaLogger.severe(this, "yaw field invalid");
			throw new InvalidLocationException(worldn, x, y, z);
		}
		
		if (pitch == null) {
			SagaLogger.severe(this, "pitch field invalid");
			throw new InvalidLocationException(worldn, x, y, z);
		}

		World world = Saga.plugin().getServer().getWorld(worldn);
		if (world == null) {
			SagaLogger.severe(this, "world field invalid");
			throw new InvalidLocationException(worldn, x, y, z);
		}

		// Transient:
		location = new Location(world,x,y,z,yaw,pitch);

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
	

}
