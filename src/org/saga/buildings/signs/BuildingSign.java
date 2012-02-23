package org.saga.buildings.signs;



import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.Saga;
import org.saga.buildings.Building;
import org.saga.config.ChunkGroupConfiguration;
import org.saga.player.SagaPlayer;
import org.saga.utility.SagaCustomSerialization;


public abstract class BuildingSign extends SagaCustomSerialization{

	
	/**
	 * Sign name.
	 */
	private String name;
	
	/**
	 * Second line.
	 */
	private String secondLine;
	
	/**
	 * Third line.
	 */
	private String thirdLine;
	
	/**
	 * Fourth line.
	 */
	private String fourthLine;
	
	/**
	 * True if enabled.
	 */
	private Boolean enabled;
	
	/**
	 * World.
	 */
	private String world;
	
	/**
	 * X coordinate.
	 */
	private Integer x;
	
	/**
	 * Y coordinate.
	 */
	private Integer y;
	
	/**
	 * Z coordinate.
	 */
	private Integer z;
	
	/**
	 * Location.
	 */
	transient private Location location;

	/**
	 * Sign.
	 */
	transient private Sign sign;
	
	/**
	 * Building.
	 */
	transient private Building building;

	/**
	 * True if the sign is invalidated.
	 */
	transient boolean invalidated;

	// Initialization:
	/**
	 * Creates a economy sign.
	 * 
	 * @param type transaction type
	 * @param sign sign
	 * @param firstLine first line
	 * @param secondLine second line
	 * @param thirdLine third line
	 * @param fourthLine fourth line
	 * @param event event that created the sign
	 * @param building building
	 */
	protected BuildingSign(Sign sign, String firstLine, String secondLine, String thirdLine, String fourthLine, Building building){

		super();
		
		this.name = firstLine;
		this.enabled = false;
		this.location = sign.getBlock().getLocation();
		this.world = location.getWorld().getName();
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
		this.sign = sign;
		this.secondLine = secondLine;
		this.thirdLine = thirdLine;
		this.fourthLine = fourthLine;
		this.building = building;
		this.invalidated = false;
		
	}

	/**
	 * Completes.
	 * 
	 * @param building building
	 * @return integrity
	 * @throws SignException if the sign isnt found
	 */
	public boolean complete(Building building) throws SignException{
		
		
		boolean integrity = true;

		if(name== null){
			Saga.severe(this, "failed to initialize name field", "stopping complete");
			throw new SignException("name null");
		}
		
		if(enabled== null){
			Saga.severe(this, "failed to initialize enabled field", "setting default");
			enabled = false;
		}
		
		if(world== null){
			Saga.severe(this, "failed to initialize world field", "stopping complete");
			throw new SignException("world null");
		}
		
		if(x== null){
			Saga.severe(this, "failed to initialize x field", "stopping complete");
			throw new SignException("x null");
		}
		
		if(y== null){
			Saga.severe(this, "failed to initialize y field", "stopping complete");
			throw new SignException("y null");
		}
		
		if(z== null){
			Saga.severe(this, "failed to initialize z field", "stopping complete");
			throw new SignException("z null");
		}
		
		World serverWorld = Saga.plugin().getServer().getWorld(this.world);
		if(serverWorld == null){
			Saga.severe(this, "failed to retrieve world", "stopping complete");
			throw new SignException("invalid world");
		}
		
		location = new Location(serverWorld, x, y, z);
		try {
			sign = (Sign) location.getBlock().getState();
		} catch (Exception e) {
			throw new SignException(e.getClass().getSimpleName() + ":" + e.getMessage() + " for sign retrieve");
		}
		
		if(secondLine== null){
			Saga.severe(this, "failed to initialize secondLine field", "setting default");
			secondLine = "";
		}
		
		if(thirdLine== null){
			Saga.severe(this, "failed to initialize thirdLine field", "setting default");
			thirdLine = "";
		}
		
		if(fourthLine== null){
			Saga.severe(this, "failed to initialize fourthLine field", "setting default");
			fourthLine = "";
		}

		this.building = building;
		
		// Transient:
		invalidated = false;
		
		return integrity;
		
		
	}	
	
	/**
	 * Deletes the sign.
	 * 
	 */
	public void remove(){

		
		// Empty sign:
		sign.setLine(0, "");
		sign.setLine(1, "");
		sign.setLine(2, "");
		sign.setLine(3, "");

		
	}
	
	/**
	 * Enables the sign.
	 * 
	 */
	public void enable() {
		
		
		sign.setLine(0, ChunkGroupConfiguration.config().enabledSignColor + getName());
		sign.setLine(1, secondLine);
		sign.setLine(2, thirdLine);
		sign.setLine(3, fourthLine);
		
		sign.update();
		
		this.enabled = true;
		
		
	}
	
	/**
	 * Disables the sign.
	 * 
	 */
	public void disable() {


		sign.setLine(0, ChunkGroupConfiguration.config().disabledSignColor + getName());
		sign.setLine(1, secondLine);
		sign.setLine(2, thirdLine);
		sign.setLine(3, fourthLine);
		
		sign.update();
		
		this.enabled = false;
		
	}
	
	/**
	 * Invalidates the sign.
	 * 
	 */
	public void invalidate() {

		sign.setLine(0, ChunkGroupConfiguration.config().invalidSignColor + getName());
		sign.setLine(1, secondLine);
		sign.setLine(2, thirdLine);
		sign.setLine(3, fourthLine);
		
		sign.update();

		this.enabled = false;
		this.invalidated = false;
		
	}
	
	/**
	 * Toggles sign enable.
	 * 
	 */
	public void toggleEnabled() {
		this.enabled = !enabled;
	}

	
	// Interaction:
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public abstract String getName();
	
	/**
	 * Gets the enabled.
	 * 
	 * @return the enabled
	 */
	public Boolean isEnabled() {
		return enabled;
	}

	/**
	 * Gets the sign location.
	 * 
	 * @return sign location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Gets the wrapped sign.
	 * 
	 * @return sign
	 */
	public Sign getSign() {
		return sign;
	}

	/**
	 * Gets the building.
	 * 
	 * @return the building
	 */
	public Building getBuilding() {
		return building;
	}
	
	/**
	 * Called when the parameters changed and the sign needs a refresh.
	 * 
	 */
	public void refresh() {

	}
	
	
	// Parameters:
	/**
	 * Gets the parameters on the sign. First line is ignored.
	 * 
	 * @return parameters of the sign
	 */
	public ArrayList<String> getParameters() {

		ArrayList<String> list = new ArrayList<String>();
		
		if(!secondLine.equals("")) list.add(secondLine);
		if(!thirdLine.equals("")) list.add(thirdLine);
		if(!fourthLine.equals("")) list.add(fourthLine);
		
		return list;
		
	}
	
	/**
	 * Checks if a string is listed on the sign. First line is not checked.
	 * 
	 * @param string string
	 * @return true if listed
	 */
	public boolean isParameter(String string) {
		
		if(secondLine.equals(string)) return true;
		if(thirdLine.equals(string)) return true;
		if(fourthLine.equals(string)) return true;
		return false;
		
	}
	
	/**
	 * Gets the amount of not empty parameters.
	 * 
	 * @return non-empty parameters count
	 */
	public int getParametersSize() {

		int count = 0;
		
		if(!secondLine.equals("")) count++;
		if(!thirdLine.equals("")) count++;
		if(!fourthLine.equals("")) count++;
		
		return count;
		
	}

	/**
	 * Gets the first parameter.
	 * 
	 * @return first parameter
	 */
	public String getFirstParameter() {
		return secondLine;
	}
	
	/**
	 * Gets the second parameter.
	 * 
	 * @return second parameter
	 */
	public String getSecondParameter() {
		return thirdLine;
	}
	
	/**
	 * Gets the third parameter.
	 * 
	 * @return third parameter
	 */
	public String getThirdParameter() {
		return fourthLine;
	}
	
	/**
	 * Sets the first parameter.
	 * 
	 * @param parameter parameter
	 */
	public void setFirstParameter(String parameter) {
		this.secondLine = parameter;
	}

	/**
	 * Sets the second parameter.
	 * 
	 * @param parameter parameter
	 */
	public void setSecondParameter(String parameter) {
		this.thirdLine = parameter;
	}
	
	/**
	 * Sets the third parameter.
	 * 
	 * @param parameter parameter
	 */
	public void setThirdParameter(String parameter) {
		this.fourthLine = parameter;
	}


	// Interact events:
	/**
	 * Called when the player left clicks the sign.
	 * 
	 * @param sagaPlayer saga player
	 */
	protected void onLeftClick(SagaPlayer sagaPlayer) {

		
		
	}
	
	/**
	 * Called when the player right clicks the sign.
	 * 
	 * @param sagaPlayer saga player
	 */
	protected void onRightClick(SagaPlayer sagaPlayer) {

		
		
	}
	
	// Events:
	/**
	 * Called when the player interacts with the sign.
	 * 
	 * @param sagaPlayer saga player
	 * @param event event
	 */
	public final void onPlayerInteract(SagaPlayer sagaPlayer, PlayerInteractEvent event) {

		
		if(!isEnabled()){
			return;
		}
		
		// Left click:
		if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
			
			onLeftClick(sagaPlayer);
			
		}
		
		// Right click:
		else if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			
			onRightClick(sagaPlayer);
			
		}

		// Take control:
		event.setUseItemInHand(Result.DENY);
		
		
	}

	
	// Other:
	/**
	 * Checks if the sign is a duplicate.
	 * 
	 * @param sign sign
	 * @return true if duplicate
	 */
	public boolean isDuplicateSign(BuildingSign sign) {
		return this.sign.equals(sign);
	}
	
	/**
	 * Checks if the wrapped signs match.
	 * 
	 * @param bukkitSign bukkit sign
	 * @return true if the wrapped and the given signs are the same
	 */
	public boolean isWrapped(Sign bukkitSign) {
		return this.sign == bukkitSign;
	}
	
	
	public class SignException extends Exception{

		public SignException(String message) {
			super(message);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		
		
	}
	
}
