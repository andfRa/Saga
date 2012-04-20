package org.saga.utility;

import org.bukkit.Location;
import org.bukkit.World;
import org.saga.Saga;
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
		
		if (yaw == null) {
			Saga.severe(this, "yaw field failed to initialize", "stopping complete");
			throw new InvalidLocationException(worldn, x, y, z);
		}
		
		if (pitch == null) {
			Saga.severe(this, "pitch field failed to initialize", "stopping complete");
			throw new InvalidLocationException(worldn, x, y, z);
		}

		World world = Saga.plugin().getServer().getWorld(worldn);
		if (world == null) {
			Saga.severe(this, "world name is invalid", "stopping complete");
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
