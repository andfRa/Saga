package org.saga.buildings.signs;



import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.buildings.Building;
import org.saga.config.SettlementConfiguration;
import org.saga.player.SagaPlayer;
import org.saga.saveload.SagaCustomSerialization;


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
	 * Building.
	 */
	transient private Building building;


	// Initialisation:
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
		this.location = sign.getBlock().getLocation();
		this.world = location.getWorld().getName();
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
		this.secondLine = secondLine;
		this.thirdLine = thirdLine;
		this.fourthLine = fourthLine;
		this.building = building;
		
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
			SagaLogger.nullField(this, "name");
			throw new SignException("name null");
		}
		
		if(world== null){
			SagaLogger.nullField(this, "world");
			throw new SignException("world null");
		}
		
		if(x== null){
			SagaLogger.nullField(this, "x");
			throw new SignException("x null");
		}
		
		if(y== null){
			SagaLogger.nullField(this, "y");
			throw new SignException("y null");
		}
		
		if(z== null){
			SagaLogger.nullField(this, "z");
			throw new SignException("z null");
		}
		
		World serverWorld = Saga.plugin().getServer().getWorld(this.world);
		if(serverWorld == null){
			SagaLogger.severe(this, "failed to retrieve world");
			throw new SignException("invalid world");
		}
		
		location = new Location(serverWorld, x, y, z);
		
		if(secondLine== null){
			SagaLogger.nullField(this, "secondLine");
			secondLine = "";
		}
		
		if(thirdLine== null){
			SagaLogger.nullField(this, "thirdLine");
			thirdLine = "";
		}
		
		if(fourthLine== null){
			SagaLogger.nullField(this, "fourthLine");
			fourthLine = "";
		}

		this.building = building;
		
		return integrity;
		
		
	}	
	
	/**
	 * Refreshes the sign.
	 * 
	 */
	public void refresh() {
		
		
		Sign sign = getSign();
		if(sign == null) return;
		
		setLines(sign);
		
		
	}
	
	/**
	 * Sets bukkit sign lines.
	 * 
	 * @param sign bukkit sign
	 */
	public void setLines(Sign sign) {

		
		SignStatus status = getStatus();
		
		// First line:
		switch (status) {
			case ENABLED:
				
				sign.setLine(0, SettlementConfiguration.config().enabledSignColor + getName());
				break;

			case DISABLED:
				
				sign.setLine(0, SettlementConfiguration.config().disabledSignColor + getName());
				break;

			case INVALIDATED:

				sign.setLine(0, SettlementConfiguration.config().invalidSignColor + getName());
				break;

			default:
				break;
		}
		
		// Remaining lines:
		sign.setLine(1, getLine(1, status));
		sign.setLine(2, getLine(2, status));
		sign.setLine(3, getLine(3, status));
		
		sign.update();
		
		
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public abstract String getName();
	
	/**
	 * Gets sign status.
	 * 
	 * @return sign status
	 */
	public abstract SignStatus getStatus();

	/**
	 * Gets sign line.
	 * 
	 * @param index line index
	 * @param status sign status
	 * @return sign line
	 */
	public abstract String getLine(int index, SignStatus status);

	/**
	 * Deletes the sign.
	 * 
	 */
	public void remove(){


		Sign sign = getSign();
		if(sign == null) return;
		
		// Empty sign:
		sign.setLine(0, "");
		sign.setLine(1, "");
		sign.setLine(2, "");
		sign.setLine(3, "");
		
		sign.update();

		
	}

	
	// Interaction:
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
	 * @return sign null if not loaded or doesn't exist
	 */
	public Sign getSign() {
		
		
		// Only loaded:
		if(!location.getChunk().isLoaded()) return null;
		
		// Get sign:
		try {
			return (Sign) location.getBlock().getState();
		} catch (Exception e) {
			return null;
		}

		
	}

	/**
	 * Gets the building.
	 * 
	 * @return the building
	 */
	public Building getBuilding() {
		return building;
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


		if(getStatus() != SignStatus.ENABLED) return;
		
		// Left click:
		if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
			
			onLeftClick(sagaPlayer);
			
		}
		
		// Right click:
		else if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			
			onRightClick(sagaPlayer);
			
		}else{
			
			return;
			
		}

		// Take control:
		if(event.hasBlock() && event.getAction() == Action.RIGHT_CLICK_BLOCK){
			event.setCancelled(true);
			event.setUseItemInHand(Result.DENY);
		}
		
		
	}

	
	// Other:
	public class SignException extends Exception{

		public SignException(String message) {
			super(message);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		
		
	}
	
	public enum SignStatus{
		
		ENABLED,
		DISABLED,
		INVALIDATED;
		
	}
	
}
