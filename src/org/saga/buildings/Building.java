package org.saga.buildings;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.buildings.signs.AbilitySign;
import org.saga.buildings.signs.AttributeSign;
import org.saga.buildings.signs.BuildingSign;
import org.saga.buildings.signs.BuildingSign.SignException;
import org.saga.buildings.signs.BuildingSign.SignStatus;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.SagaChunk;
import org.saga.config.ChunkGroupConfiguration;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.listeners.events.SagaBuildEvent;
import org.saga.listeners.events.SagaBuildEvent.BuildOverride;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.messages.BuildingMessages;
import org.saga.messages.SagaMessages;
import org.saga.player.SagaPlayer;
import org.saga.saveload.SagaCustomSerialization;
import org.saga.settlements.Settlement.SettlementPermission;
import org.sk89q.CommandContext;

public abstract class Building extends SagaCustomSerialization{


	/**
	 * Building level.
	 */
	private Short level;
	
	/**
	 * Building signs.
	 */
	private ArrayList<BuildingSign> signs;
	
	/**
	 * Origin chunk.
	 */
	transient SagaChunk originChunk;
	
	/**
	 * Building definition.
	 */
	transient private BuildingDefinition definition;
	
	/**
	 * True if the building is enabled.
	 */
	transient private boolean isEnabled = false;
	
	
	
	// Initialisation:
	/**
	 * Creates a building from a definition.
	 * 
	 * @param definition building definition
	 */
	public Building(BuildingDefinition definition) {
		
		this.definition = definition;
		this.level = 0;
		this.signs = new ArrayList<BuildingSign>();
		
	}
	
	/**
	 * Goes trough all the fields and makes sure everything has been set after gson load.
	 * If not, it fills the field with defaults.
	 * 
	 * @return true if everything was correct.
	 * @throws InvalidBuildingException if the building definition is missing
	 */
	public boolean complete() throws InvalidBuildingException{
		
		
		boolean integrity = true;
		
		if(level == null){
			level = 0;
			SagaLogger.severe("level field for " + this + " building. Setting default.");
			integrity = false;
		}
		
		// Definition:
		definition = ChunkGroupConfiguration.config().getBuildingDefinition(getName());
		if(definition == null){
			SagaLogger.severe(this, "missing definition");
			integrity = false;
			throw new InvalidBuildingException(getName());
		}
		
		if(signs == null){
			signs = new ArrayList<BuildingSign>();
			SagaLogger.nullField(this, "signs");
			integrity = false;
		}
		for (int i = 0; i < signs.size(); i++) {
			
			try {
				signs.get(i).complete(this);
			} catch (SignException e) {
				SagaLogger.nullField(this, "failed to initialise signs field element: " + e.getClass().getSimpleName() + ":" + e.getMessage());
				signs.remove(i);
				i--;
				continue;
			}
			
		}
		
		// Refresh signs:
		refreshSigns();
		
		return integrity;
		

	}

	/**
	 * Sets origin chunk.
	 * 
	 * @param originChunk origin chunk
	 */
	public void setSagaChunk(SagaChunk originChunk) {
		this.originChunk = originChunk;
	}
	
	/**
	 * Removes saga chunk.
	 */
	public void removeSagaChunk() {
		this.originChunk = null;
	}
	
	/**
	 * Returns the saga chunk.
	 * 
	 * @return origin chunk, null if none
	 */
	public SagaChunk getSagaChunk() {
		return originChunk;
	}
	
	/**
	 * Gets origin chunk group.
	 * 
	 * @return origin chunk group, null if not found
	 */
	public ChunkGroup getChunkGroup() {
		
		if(originChunk == null){
			return null;
		}
		return originChunk.getChunkGroup();
		
	}
	
	
	
	// Building signs:
	/**
	 * Handles sign placement.
	 * 
	 * @param sagaPlayer saga player
	 * @param sign sign
	 * @param event sign change event
	 */
	public final void handleSignPlace(SagaPlayer sagaPlayer, Sign sign, SignChangeEvent event) {


		// Check sign:
		if(!isBuildingSign(event.getLine(0))){
			return;
		}

		// Permission
		if(!getChunkGroup().hasPermission(sagaPlayer, SettlementPermission.BUILD_BUILDING)){
			sagaPlayer.message(SagaMessages.noPermission(this));
			return;
		}
		
		// Create:
		BuildingSign buildingSign = createBuildingSign(sign, event);
		
		// Invalid sign:
		if(buildingSign == null) return;

		// Add sign:
		addBuildingSign(buildingSign);

		// Enable:
		buildingSign.refresh();
		
		// Set lines:
		buildingSign.setLines(sign);
		
		// Update event:
		event.setLine(0, sign.getLine(0));
		event.setLine(1, sign.getLine(1));
		event.setLine(2, sign.getLine(2));
		event.setLine(3, sign.getLine(3));
		
		
	}

	/**
	 * Handles sign remove.
	 * 
	 * @param sagaPlayer saga player
	 * @param sign sign
	 * @param event sign remove event
	 */
	public final void handleSignRemove(SagaPlayer sagaPlayer, Sign sign, BlockBreakEvent event) {


		// Building sign:
		BuildingSign buildingSign = buildingSignAt(event.getBlock().getLocation());
		if(buildingSign == null) return;

		// Permission
		if(!getChunkGroup().hasPermission(sagaPlayer, SettlementPermission.BUILD_BUILDING)){
			sagaPlayer.message(SagaMessages.noPermission(this));
			return;
		}
		
		// Remove:
		removeBuildingSign(buildingSign);
		
		
	}
	
	/**
	 * Check if the sign is a building sign.
	 * 
	 * @param firstLine first line
	 * @return true if a building sign
	 */
	protected boolean isBuildingSign(String firstLine) {
		

		if( firstLine.equalsIgnoreCase(AttributeSign.SIGN_NAME) ||
			firstLine.equalsIgnoreCase(AbilitySign.SIGN_NAME)
		) return true;
		
		return false;
		
		
	}
	
	/**
	 * Creates a building sign.
	 * 
	 * @param sign sign
	 * @param event sign change event
	 * @return building sign, null if none
	 */
	protected BuildingSign createBuildingSign(Sign sign, SignChangeEvent event) {

		
		BuildingSign buildingSign = null;
		
		// Attribute sign:
		if(event.getLine(0).equalsIgnoreCase(AttributeSign.SIGN_NAME)){
			
			buildingSign = AttributeSign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
			
		}

		// Attribute sign:
		else if(event.getLine(0).equalsIgnoreCase(AbilitySign.SIGN_NAME)){
			
			buildingSign = AbilitySign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
			
		}

		return buildingSign;
		
		
	}

	/**
	 * Gets the signs.
	 * 
	 * @return the signs
	 */
	public ArrayList<BuildingSign> getSigns() {
		return new ArrayList<BuildingSign>(signs);
	}
	
	/**
	 * Gets the signs.
	 * 
	 * @param signClass sign class
	 * @return the signs
	 */
	public <T extends BuildingSign> ArrayList<T> getSigns(Class<T> signClass){
		
		
		ArrayList<BuildingSign> allSigns = getSigns();
		ArrayList<T> rSigns = new ArrayList<T>();
		
		for (BuildingSign buildingSign : allSigns) {

			// Correct sign:
			T rSign;
			if(signClass.isInstance(buildingSign)){
				try {
					rSign = signClass.cast(buildingSign);
					rSigns.add(rSign);
				}catch (Throwable e) {}
			}
			
			
		}
		
		return rSigns;
		
		
	}
	
	/**
	 * Adds a building sign.
	 * 
	 * @param buildingSign sign
	 */
	protected void addBuildingSign(BuildingSign buildingSign) {

		
		if(signs.contains(buildingSign)){
			SagaLogger.severe(this, "tried to add an already existing building sign");
			return;
		}
		signs.add(buildingSign);


	}

	/**
	 * Removes a building sign.
	 * 
	 * @param buildingSign sign
	 */
	protected void removeBuildingSign(BuildingSign buildingSign) {

		
		// No sign:
		if(buildingSign == null) return;

		// Delete:
		buildingSign.remove();

		// Non-existent:
		if(!signs.contains(buildingSign)){
			SagaLogger.severe(this, "tried to remove a non-existing building sign");
			return;
		}
		
		// Remove:
		signs.remove(buildingSign);
		
		
	}
	
	/**
	 * Gets a building sign.
	 * 
	 * @param sign sign
	 * @return building sign, null if not found
	 */
	protected BuildingSign buildingSignFor(Sign sign) {

		
		return buildingSignAt(sign.getBlock().getLocation());
		
//		for (int i = 0; i < signs.size(); i++) {
//			if(signs.get(i).isWrapped(sign)){
//				return signs.get(i);
//			}
//		}
//		return null;
		
	}

	/**
	 * Gets a building sign at given location.
	 * 
	 * @param location location
	 * @return building sign, null if not found
	 */
	protected BuildingSign buildingSignAt(Location location) {

		
		for (int i = 0; i < signs.size(); i++) {
			if(signs.get(i).getLocation().equals(location)){
				return signs.get(i);
			}
		}
		return null;
		
		
	}

	/**
	 * Gets the enabled signs with the given name.
	 * 
	 * @param name sign name
	 * @return enabled signs with the given name
	 */
	public ArrayList<BuildingSign> getEnabledSigns(String name) {
		
		
		ArrayList<BuildingSign> enabledSigns = new ArrayList<BuildingSign>();
		
		ArrayList<BuildingSign> buildingSigns = getSigns();
		
		for (BuildingSign buildingSign : buildingSigns) {
			
			if(buildingSign.getName().equals(name) && buildingSign.getStatus() == SignStatus.ENABLED){
				enabledSigns.add(buildingSign);
			}
			
		}

		return enabledSigns;
		
		
	}
	

	/**
	 * Gets the valid signs with the given name.
	 * 
	 * @param name sign name
	 * @return enabled signs with the given name
	 */
	public ArrayList<BuildingSign> getValidSigns(String name) {
		
		
		ArrayList<BuildingSign> validSigns = new ArrayList<BuildingSign>();
		
		ArrayList<BuildingSign> buildingSigns = getSigns();
		
		for (BuildingSign buildingSign : buildingSigns) {
			
			if(buildingSign.getName().equals(name) && buildingSign.getStatus() != SignStatus.INVALIDATED){
				validSigns.add(buildingSign);
			}
			
		}

		return validSigns;
		
		
	}

	
	/**
	 * Refreshes all signs.
	 * 
	 */
	public void refreshSigns() {

		
		ArrayList<BuildingSign> signs = getSigns();
		
		for (BuildingSign buildingSign : signs) {
			buildingSign.refresh();
		}
		

	}
	
	/**
	 * Removes all signs.
	 * 
	 */
	public void removeSigns() {

		
		ArrayList<BuildingSign> signs = getSigns();
		
		for (BuildingSign buildingSign : signs) {
			removeBuildingSign(buildingSign);
		}
		

	}
	
	
	
	// Stats and description:
	/**
	 * Gets the building specific stats.
	 * Each group as a different element.
	 * 
	 * @return building specific stats, empty string if none
	 */
	public ArrayList<String> getSpecificStats() {
		return new ArrayList<String>();
	}

	/**
	 * Gets the buildings character for the map.
	 * 
	 * @return character for the map
	 */
	public String getMapChar() {
		
		String letter = getClass().getSimpleName().substring(0, 1).toUpperCase();
		
		// Fix letter:
		if(letter.equals("I")){
			letter = "I.";
		}
		
		return letter;
		
	}

	
	/**
	 * Checks if the player can create a sign.
	 * 
	 * @param sagaPlayer saga player
	 * @param event sign change event
	 * @return true if the sign can be made
	 */
	public boolean canCreateSign2(SagaPlayer sagaPlayer, SignChangeEvent event) {
		
//		if(isBuildingSign(event.getLine(0))) return checkBuildingPermission(sagaPlayer, BuildingPermission.LOW);
		
		return true;
		
	}
	
	/**
	 * Checks if the player can remove a sign.
	 * 
	 * @param sagaPlayer saga player
	 * @param event sign building sign
	 * @return true if the sign can be removed
	 */
	public boolean canRemoveSign2(SagaPlayer sagaPlayer, BuildingSign sign) {

//		if(isBuildingSign(sign.getName())) return checkBuildingPermission(sagaPlayer, BuildingPermission.LOW);
		
		return true;
		
	}
	
	/**
	 * Checks if the player can train.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if the player can train
	 */
	public boolean canTrain(SagaPlayer sagaPlayer) {
		
		
		ChunkGroup chunkGroup = getChunkGroup();
		if(chunkGroup == null) return false;
		
		// Is member:
//		return chunkGroup.isMember(sagaPlayer);
		return true;
		
	}
	
	/**
	 * Checks if the player can respec.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if the player can recpec
	 */
	public boolean canRespec(SagaPlayer sagaPlayer) {
		
		
		ChunkGroup chunkGroup = getChunkGroup();
		if(chunkGroup == null) return false;
		
		// Is member:
//		return chunkGroup.isMember(sagaPlayer);
		return true;
		
	}
	
	/**
	 * Checks if the player can select abilities.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if the player can select abilities
	 */
	public boolean canSelect(SagaPlayer sagaPlayer) {
		
		
		ChunkGroup chunkGroup = getChunkGroup();
		if(chunkGroup == null) return false;
		
		// Is member:
//		return chunkGroup.isMember(sagaPlayer);
		return true;
		
	}
	
	/**
	 * Checks if the player can learn an ability.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if the player can learn an ability
	 */
	public boolean canLearn(SagaPlayer sagaPlayer) {
		
		
		ChunkGroup chunkGroup = getChunkGroup();
		if(chunkGroup == null) return false;
		
		// Is member:
//		return chunkGroup.isMember(sagaPlayer);
		return true;
		
	}
	
	
	
	// Interaction:
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public final String getName() {
		return getName(getClass());
	}
	
	/**
	 * Gets the display name for this building.
	 * 
	 * @return
	 */
	public String getDisplayName() {
		return getName();
	}
	
	/**
	 * Gets the name.
	 * 
	 * @param buildingClass building class
	 * @return the name
	 */
	public static String getName(Class<? extends Building> buildingClass) {
		return buildingClass.getSimpleName().replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2").toLowerCase();
	}

	/**
	 * Gets the level.
	 * 
	 * @return the level
	 */
	public Integer getLevel() {
		return level.intValue();
	}
	
	/**
	 * Gets building definition.
	 * 
	 * @return building definition
	 */
	public BuildingDefinition getDefinition() {
		return definition;
	}
	

	
	// Updates:
	/**
	 * Enables the building
	 * 
	 */
	public void enable() {
		
		this.isEnabled = true;
		
	}
	
	/**
	 * Disables the building.
	 * 
	 */
	public void disable() {
		
		this.isEnabled = false;
		
	}

	/**
	 * True if the building is enabled.
	 * 
	 * @return true if enabled
	 */
	public boolean isEnabled() {
		return isEnabled;
	}
	
	
	
	// Events:
	 /**
     * Called when a player interacts with something in the building.
     * 
     * @param event event
     * @param sagaPlayer saga player
     */
    public void onPlayerInteract(PlayerInteractEvent event, SagaPlayer sagaPlayer) {

    	
    	// Clicked on invalid block:
    	if(event.getClickedBlock() == null) return;
    	
    	// Left click:
    	if(event.getClickedBlock().getState() instanceof Sign){
    		
    		BuildingSign sign = buildingSignAt(event.getClickedBlock().getLocation());
    		if(sign != null) sign.onPlayerInteract(sagaPlayer, event);
    		
    	}
    	
    	
    	
    }
	
    /**
	 * Called when a sign changes
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 */
	public void onSignChange(SignChangeEvent event, SagaPlayer sagaPlayer) {
		
		
	}
    
	/**
	 * Called when a creature spawns on the saga chunk.
	 * 
	 * @param event event
	 * @param locationChunk event location saga chunk
	 */
	public void onCreatureSpawn(CreatureSpawnEvent event, SagaChunk locationChunk) {
		
	}
	
	
	
	// Entity damage events:
	/**
	 * Called when a a entity takes damage.
	 * 
	 * @param event event
	 */
	public void onEntityDamage(SagaEntityDamageEvent event){
		
	}

	/**
	 * Called when a player is killed by another player.
	 * 
	 * @param event event
	 * @param locationChunk location chunk
	 */
	public void onPvPKill(SagaPlayer attacker, SagaPlayer defender){
		
	}

	
	
	// Move events:
	/**
	 * Called when a player enters the building.
	 * 
	 * @param sagaPlayer saga player
	 * @param last last building, null if none
	 */
	public void onPlayerEnter(SagaPlayer sagaPlayer, Building last) {


	}
	
	/**
	 * Called when a player enters the building.
	 * 
	 * @param sagaPlayer saga player
	 * @param next next building, null if none
	 */
	public void onPlayerLeave(SagaPlayer sagaPlayer, Building next) {


	}

	
	
	// Block events:
	/**
	 * Called when a player builds on the chunk.
	 * 
	 * @param event event
	 */
	public void onBuild(SagaBuildEvent event) {

		// Add override:
		if(getChunkGroup() != null && !getChunkGroup().hasPermission(event.getSagaPlayer(), SettlementPermission.BUILD_BUILDING)) event.addBuildOverride(BuildOverride.BUILDING_DENY);
		
	}
	

	// Member events:
	/**
	 * Member respawn event.
	 * 
	 * @param sagaPlayer saga player
	 * @param event event
	 */
	public void onMemberRespawn(SagaPlayer sagaPlayer, PlayerRespawnEvent event) {

		
	}
	
	/**
	 * Member join event.
	 * 
	 * @param sagaPlayer saga player
	 * @param event event
	 */
	public void onMemberJoin(SagaPlayer sagaPlayer, PlayerJoinEvent event) {

		
	}
	
	/**
	 * Member respawn event.
	 * 
	 * @param sagaPlayer saga player
	 * @param event event
	 */
	public void onMemberQuit(SagaPlayer sagaPlayer, PlayerQuitEvent event) {

		
	}
	
	
	// Utility:
	/**
	 * Retrieves a building the player is standing on.
	 * 
	 * @param args args
	 * @param plugin plugin 
	 * @param sagaPlayer saga player
	 * @param buildingClass building class
	 * @return building
	 * @throws Throwable thrown if validation fails, message is in the form that can be sent to saga player
	 */
	public static <T extends Building> T retrieveBuilding(CommandContext args, Saga plugin, SagaPlayer sagaPlayer, Class<T> buildingClass) throws Throwable {

		
		// Building:
		SagaChunk sagaChunk =  sagaPlayer.getSagaChunk();
		Building building = null;
		if(sagaChunk != null){
			building = sagaChunk.getBuilding();
		}
		if(building == null){
			throw new Throwable(BuildingMessages.invalidBuilding(getName(buildingClass), args.getCommand()));
		}
		
		// Correct building:
		T selectedBuilding;
		try {
			selectedBuilding = buildingClass.cast(building);
		} catch (ClassCastException e) {
			throw new Throwable(BuildingMessages.invalidBuilding(getName(buildingClass), args.getCommand()));
		} 
		
		return selectedBuilding;
		
		
	}
	
	
	
	// Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
	
	
}
