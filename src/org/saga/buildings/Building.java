package org.saga.buildings;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Creature;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.saga.Saga;
import org.saga.SagaMessages;
import org.saga.buildings.BuildingDefinition.BuildingPermission;
import org.saga.buildings.signs.BindSign;
import org.saga.buildings.signs.BuildingSign;
import org.saga.buildings.signs.BuildingSign.SignException;
import org.saga.buildings.signs.LearningSign;
import org.saga.buildings.signs.SkillSign;
import org.saga.buildings.signs.ProficiencySign;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.ChunkGroupMessages;
import org.saga.chunkGroups.SagaChunk;
import org.saga.config.ChunkGroupConfiguration;
import org.saga.player.SagaEntityDamageManager.SagaPvpEvent;
import org.saga.player.SagaPlayer;
import org.saga.utility.SagaCustomSerialization;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;

public abstract class Building extends SagaCustomSerialization{


//	/**
//	 * Class name used by the loader.
//	 */
//	@SuppressWarnings("unused")
//	private final String _className;

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
	transient private BuildingDefinition buildingDefinition;
	
	/**
	 * True if the building is enabled.
	 */
	transient private boolean isEnabled = false;
	
	
	// Initialization:
	/**
	 * Used by gson.
	 * 
	 */
	protected Building() {
//		_className = getClass().getName();
	}
	
	/**
	 * Initializes.
	 * 
	 * @param name name
	 */
	public Building(String name) {
		
//		_className = getClass().getName();
//		this.name = name;
		this.level = 0;
		this.signs = new ArrayList<BuildingSign>();
		
	}
	
	/**
	 * Goes trough all the fields and makes sure everything has been set after gson load.
	 * If not, it fills the field with defaults.
	 * 
	 * @return true if everything was correct.
	 * @throws MissingBuildingDefinitionException if the building definition is missing
	 */
	public boolean complete() throws MissingBuildingDefinitionException{
		
		
		boolean integrity = true;
		
		// Fields:
//		if(name == null){
//			name = "void";
//			Saga.severe("Failed to initialize name field for " + this + " building. Setting default.");
//			integrity = false;
//		}
		if(level == null){
			level = 0;
			Saga.severe("Failed to initialize level field for " + this + " building. Setting default.");
			integrity = false;
		}
		
//		// Location chunk:
//		if(locationChunk == null){
//			Saga.severe("Failed to initialize locationChunk field for " + this + " building.");
//			integrity = false;
//		}
		
		// Definition:
		buildingDefinition = ChunkGroupConfiguration.config().getBuildingDefinition(getName());
		if(buildingDefinition == null){
			Saga.severe(this + " building failed to retrieve definition. Stoping complete.");
			integrity = false;
			throw new MissingBuildingDefinitionException(getName());
		}
		
		if(signs == null){
			signs = new ArrayList<BuildingSign>();
			Saga.severe(this, "failed to initialize signs field", "setting default");
			integrity = false;
		}
		for (int i = 0; i < signs.size(); i++) {
			
			try {
				signs.get(i).complete(this);
			} catch (SignException e) {
				Saga.severe(this, "failed to initialize signs field element: " + e.getClass().getSimpleName() + ":" + e.getMessage(), "removing element");
				signs.remove(i);
				i--;
				continue;
			}
			
		}
		
		return completeExtended() && integrity;
		

	}
	
	/**
	 * Does a complete for all extending classes.
	 * 
	 * @return true if everything was correct.
	 */
	public abstract boolean completeExtended();

	/**
	 * Uses the instance as a blueprint.
	 * 
	 * @return similar instance
	 */
	public abstract Building blueprint();
	
	/**
	 * Sets origin chunk.
	 * 
	 * @param originChunk origin chunk
	 */
	public void setOriginChunk(SagaChunk originChunk) {
		this.originChunk = originChunk;
	}
	
	/**
	 * Removes origin chunk.
	 */
	public void removeOriginChunk() {
		this.originChunk = null;
	}
	
	/**
	 * Returns the origin chunk.
	 * 
	 * @return origin chunk, null if none
	 */
	public SagaChunk getOriginChunk() {
		return originChunk;
	}
	
	/**
	 * Gets origin chunk group.
	 * 
	 * @return origin chunk group, null if not found
	 */
	public ChunkGroup getOriginChunkGroup() {
		
		if(originChunk == null){
			return null;
		}
		return originChunk.getChunkGroup();
		
	}
	
	
	// Signs:
	/**
	 * Gets the signs.
	 * 
	 * @return the signs
	 */
	public ArrayList<BuildingSign> getSigns() {
		return new ArrayList<BuildingSign>(signs);
	}
	
	/**
	 * Adds a building sign.
	 * 
	 * @param buildingSign sign
	 */
	protected void addBuildingSign(BuildingSign buildingSign) {

		
		if(signs.contains(buildingSign)){
			Saga.severe(this, "tried to add an already existing building sign", "ignoring request");
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

		
		if(!signs.contains(buildingSign)){
			Saga.severe(this, "tried to remove a non-existing building sign", "ignoring request");
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

		
		for (int i = 0; i < signs.size(); i++) {
			if(signs.get(i).isWrapped(sign)){
				return signs.get(i);
			}
		}
		return null;
		
		
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
	 * Used when a sign was removed.
	 * 
	 * @param sagaPlayer saga player
	 * @param sign sign
	 * @param event sign remove event
	 */
	protected void removedSign(SagaPlayer sagaPlayer, Sign sign, BlockBreakEvent event) {


		// Building sign:
		BuildingSign buildingSign = buildingSignAt(event.getBlock().getLocation());
		if(buildingSign == null){
			return;
		}	
		
		// Permission:
		if(!canRemoveSign(sagaPlayer, buildingSign)){
			
			sagaPlayer.message(SagaMessages.noPermission(this));
			event.setCancelled(true);
			return;
			
		}
		
		// Remove:
		deleteBuildingSign(buildingSign, event);
		
		
	}

	/**
	 * Deletes and removes a sign.
	 * 
	 * @param buildingSign building sign
	 * @param event block brake event
	 */
	protected void deleteBuildingSign(BuildingSign buildingSign, BlockBreakEvent event) {

		
		// No sign:
		if(buildingSign == null) return;
		
		// Remove sign:
		removeBuildingSign(buildingSign);

		// Disable:
		buildingSign.disable();
		
		// Delete:
		buildingSign.delete();
		
		
	}
	
	
	/**
	 * Gets the enabled signs with the given name.
	 * 
	 * @param name name
	 * @return enabled signs with the given name
	 */
	public ArrayList<BuildingSign> getEnabledSigns(String name) {
		
		ArrayList<BuildingSign> rSigns = new ArrayList<BuildingSign>();
		
		ArrayList<BuildingSign> buildingSigns = getSigns();
		
		for (BuildingSign buildingSign : buildingSigns) {
			
			if(buildingSign.getName().equals(name) && buildingSign.isEnabled()){
				rSigns.add(buildingSign);
			}
			
		}

		return rSigns;
		
	}
	
	
	// Building sign creation:
	/**
	 * Used when a sign was placed.
	 * 
	 * @param sagaPlayer saga player
	 * @param sign sign
	 * @param event sign change event
	 */
	protected void placedSign(SagaPlayer sagaPlayer, Sign sign, SignChangeEvent event) {


		// Check sign:
		if(!isBuildingSign(event.getLine(0))){
			return;
		}

		// Permission
		if(!canCreateSign(sagaPlayer, event)){
			sagaPlayer.message(SagaMessages.noPermission(this));
			return;
		}
		
		// Create:
		createBuildingSign(sign, event);
		
		
	}

	/**
	 * Check if the sign is a building sign.
	 * 
	 * @param firstLine first line
	 * @return true if a building sign
	 */
	private final boolean isBuildingSign(String firstLine) {
		
		if(isBuildingSignExtended(firstLine)) return true;
		
		if( firstLine.equalsIgnoreCase(ProficiencySign.SIGN_NAME) ||
			firstLine.equalsIgnoreCase(LearningSign.SIGN_NAME) ||
			firstLine.equalsIgnoreCase(BindSign.SIGN_NAME) ||
			firstLine.equalsIgnoreCase(SkillSign.SIGN_NAME)
		) return true;
		
		return false;
		
	}

	/**
	 * Check if the sign is a building sign.
	 * Override of custom signs.
	 * 
	 * @param firstLine first line
	 * @return true if a building sign
	 */
	protected boolean isBuildingSignExtended(String firstLine) {
		
		return false;
		
	}
	
	/**
	 * Creates and adds a sign.
	 * 
	 * @param sign sign
	 * @param event sign change event
	 */
	private final void createBuildingSign(Sign sign, SignChangeEvent event) {

		
		BuildingSign buildingSign = null;
		
		// Try extended:
		buildingSign = createBuildingSignExtended(sign, event);
		if(buildingSign != null){
			
			
		}
		
		// Training sign:
		else if(event.getLine(0).equalsIgnoreCase(ProficiencySign.SIGN_NAME)){
			
			buildingSign = ProficiencySign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
			
		}
		
		// Learning sign:
		else if(event.getLine(0).equalsIgnoreCase(LearningSign.SIGN_NAME)){
					
			buildingSign = LearningSign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
					
		}
				
		// Selection sign:
		else if(event.getLine(0).equalsIgnoreCase(BindSign.SIGN_NAME)){
					
			buildingSign = BindSign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
					
		}
		// Skill sign:
		else if(event.getLine(0).equalsIgnoreCase(SkillSign.SIGN_NAME)){
			
			buildingSign = SkillSign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
			
		}
		// Other sign:
		else{
			
//			buildingSign = BuildingSign.create(sign, event.getLine(0), event.getLine(1), event.getLine(2), event.getLine(3), this);
			
		}
		
		// Invalid sign:
		if(buildingSign == null) return;
		
		// Add sign:
		addBuildingSign(buildingSign);

		// Enable:
		buildingSign.enable();
		
		// Update event:
		event.setLine(0, sign.getLine(0));
		event.setLine(1, sign.getLine(1));
		event.setLine(2, sign.getLine(2));
		event.setLine(3, sign.getLine(3));
		
		
	}
	
	/**
	 * Creates and adds a building sign.
	 * Override for custom signs.
	 * 
	 * @param sign sign
	 * @param event event
	 * @return created sign, null if none
	 */
	protected BuildingSign createBuildingSignExtended(Sign sign, SignChangeEvent event){
		
		return null;
		
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
	 * Gets the letter for the building.
	 * 
	 * @return
	 */
	public static String getLetter() {
		return "B";
	}
	
	/**
	 * Gets the buildings letter for the map.
	 * 
	 * @return letter for the map
	 */
	public String getMapLetter() {
		
		String letter = getClass().getSimpleName().substring(0, 1).toUpperCase();
		
		// Fix letter:
		if(letter.equals("I")){
			letter = "I.";
		}
		
		return letter;
		
	}
	
	// Permissions:
	/**
	 * Checks if the player can edit a building.
	 * 
	 * @param sagaPlayer saga player
	 * @param requiredPermission required permission
	 * @return true if can use
	 */
	public boolean checkBuildingPermission(SagaPlayer sagaPlayer, BuildingPermission requiredPermission) {
		

		// No origin chunk:
		SagaChunk sagaChunk = getOriginChunk();
		if(sagaChunk == null) {
			return false;
		}
		
		// Owner:
		if(getOriginChunkGroup().isOwner(sagaPlayer.getName()) || sagaPlayer.isAdminMode()) return true;
		
		// Check profession permission:
		if(requiredPermission.equals(BuildingPermission.NONE)){
			return true;
		}
		
		return !getDefinition().getBuildingPermission(sagaPlayer).isLower(requiredPermission);
		
		
	}
	
	/**
	 * Checks if the player can promote.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can use
	 */
	public boolean canPromote(SagaPlayer sagaPlayer) {
		

		// Check if member:
		ChunkGroup chunkGroup = null;
		if(originChunk != null) {
			chunkGroup = originChunk.getChunkGroup();
		}
		
		if(chunkGroup == null) return false;
		
		if(chunkGroup.isOwner(sagaPlayer.getName()) || sagaPlayer.isAdminMode()) return true;
		
		if(!chunkGroup.isMember(sagaPlayer)) return false;

		// High permission:
		BuildingPermission permission = buildingDefinition.getBuildingPermission(sagaPlayer);
		return !permission.isLower(BuildingPermission.HIGH);
		
		
	}
	
	/**
	 * Checks if the player can build. Checks if the player has building permission.
	 * 
	 * @param sagaPlayer saga player
	 * @return
	 */
	public boolean canBuild(SagaPlayer sagaPlayer) {
		
		return checkBuildingPermission(sagaPlayer, BuildingPermission.LOW);
		
	}
	
	/**
	 * Checks if the player can create a sign.
	 * 
	 * @param sagaPlayer saga player
	 * @param event sign change event
	 * @return true if the sign can be made
	 */
	public boolean canCreateSign(SagaPlayer sagaPlayer, SignChangeEvent event) {
		
		if(isBuildingSign(event.getLine(0))) return checkBuildingPermission(sagaPlayer, BuildingPermission.LOW);
		
		return true;
		
	}
	
	/**
	 * Checks if the player can remove a sign.
	 * 
	 * @param sagaPlayer saga player
	 * @param event sign building sign
	 * @return true if the sign can be removed
	 */
	public boolean canRemoveSign(SagaPlayer sagaPlayer, BuildingSign sign) {

		if(isBuildingSign(sign.getName())) return checkBuildingPermission(sagaPlayer, BuildingPermission.LOW);
		
		return true;
		
	}
	
	/**
	 * Checks if the player can train.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if the player can train
	 */
	public boolean canTrain(SagaPlayer sagaPlayer) {
		
		
		ChunkGroup chunkGroup = getOriginChunkGroup();
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
		
		
		ChunkGroup chunkGroup = getOriginChunkGroup();
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
		
		
		ChunkGroup chunkGroup = getOriginChunkGroup();
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
	 * Gets the name. Includes chunk group name.
	 * 
	 * @param chunkGroup chunk group
	 * @return name
	 */
	public String getDisplayName(ChunkGroup chunkGroup) {
		return chunkGroup.getName() + " " + getName().replaceAll(SagaMessages.spaceSymbol, " ");
	}
	
	/**
	 * Gets the level.
	 * 
	 * @return the level
	 */
	public Short getLevel() {
		return level;
	}
	
	/**
	 * Gets the building point cost.
	 * 
	 * @return the building point cost
	 */
	public Integer getPointCost() {
		return buildingDefinition.getPointCost(getLevel());
	}
	
	/**
	 * Gets the building point cost for next level.
	 * 
	 * @return the building point cost for next level
	 */
	public Integer getNextPointCost() {
		return buildingDefinition.getPointCost((short) (getLevel() +1));
	}

	/**
	 * Gets the money cost.
	 * 
	 * @return the money cost
	 */
	public Integer getMoneyCost() {
		return buildingDefinition.getMoneyCost(getLevel()).intValue();
	}

	/**
	 * Gets the upgrade building point cost.
	 * 
	 * @return the building point cost
	 */
	public Integer getUpgradePointCost() {
		return buildingDefinition.getPointCost((short) (getLevel() + 1)) -  buildingDefinition.getPointCost(getLevel());
	}

	/**
	 * Gets the upgrade money cost.
	 * 
	 * @return the money cost
	 */
	public Integer getUpgradeMoneyCost() {
		return buildingDefinition.getMoneyCost((short) (getLevel() + 1)) -  buildingDefinition.getMoneyCost(getLevel());
	}
	
	/**
	 * Gets building definition.
	 * 
	 * @return building definition
	 */
	public BuildingDefinition getDefinition() {
		return buildingDefinition;
	}
	

	// Updates:
	/**
	 * Called by chunk groups on new day in the given world.
	 * 
	 */
	public void newDay() {

	}
	
	/**
	 * Enables the building
	 * 
	 */
	public void enable() {
		
		
		this.isEnabled = true;
		
		// Enable all signs:
		for (BuildingSign buildingSign : signs) {
			buildingSign.enable();
		}
		
		
	}
	
	/**
	 * Disables the building.
	 * 
	 */
	public void disable() {
		
		
		this.isEnabled = false;

		// Disable all signs:
		for (BuildingSign buildingSign : signs) {
			buildingSign.disable();
		}
		
		
	}

	/**
	 * True if the building is enabled.
	 * 
	 * @return true if enabled
	 */
	public boolean isEnabled() {
		return isEnabled;
	}
	
	
	// Proficiencies:

	
	// Events:
	 /**
     * Called when a player interacts with something in the building.
     * 
     * @param event event
     * @param sagaPlayer saga player
     */
    public void onPlayerInteract(PlayerInteractEvent event, SagaPlayer sagaPlayer) {

    	
    	// Clicked on invalid block:
    	if(event.getClickedBlock() == null){
    		return;
    	}
    	
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
		
		
		// Correct sign:
		if((event.getBlock().getState() instanceof Sign)){
			
			Sign sign = (Sign) event.getBlock().getState();
			placedSign(sagaPlayer, sign, event);
			
		}
		
		
	}
    
	/**
	 * Called when a block is placed in the chunk.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 */
	public void onBlockPlace(BlockPlaceEvent event, SagaPlayer sagaPlayer) {

		
		if(event.isCancelled()){
			return;
		}
		
		if(!canBuild(sagaPlayer)){
			event.setCancelled(true);
			sagaPlayer.message(SagaMessages.noPermission(this));
			return;
		}
		
		
	}

	/**
	 * Called when a block is broken in the chunk.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 */
	public void onBlockBreak(BlockBreakEvent event, SagaPlayer sagaPlayer) {

		
		if(event.isCancelled()){
			return;
		}
		
		if(!canBuild(sagaPlayer)){
			event.setCancelled(true);
			sagaPlayer.message(SagaMessages.noPermission(this));
			return;
		}

		Block targetBlock = event.getBlock();

		// Sign:
		if((targetBlock.getState() instanceof Sign)){
			
			removedSign(sagaPlayer, (Sign)targetBlock.getState(), event);
			
		}
		
		
		
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
	 * Called when a player is damaged by another player.
	 * 
	 * @param event event
	 * @param locationChunk location chunk
	 */
	public void onPlayerVersusPlayer(SagaPvpEvent event){
		
	}
	
	/**
	 * Called when a player is damaged by another player.
	 * 
	 * @param event event
	 * @param locationChunk location chunk
	 */
	public void onPlayerKillPlayer(SagaPlayer attacker, SagaPlayer defender){
		
	}

	/**
	 * Called when a player is damaged by a creature.
	 * 
	 * @param event event
	 * @param damager damager creature
	 * @param damaged damaged saga player
	 * @param locationChunk location chunk
	 */
	public void onPlayerDamagedByCreature(EntityDamageByEntityEvent event, Creature damager, SagaPlayer damaged){

	}
	
	
	/**
	 * Called when a player is damaged by another player.
	 * 
	 * @param event event
	 * @param locationChunk location chunk
	 */
	public void onPlayerVersusPlayer(SagaPvpEvent event, SagaChunk locationChunk){
		
	}
	
	/**
	 * Called when a player is damaged by another player.
	 * 
	 * @param event event
	 * @param locationChunk location chunk
	 */
	public void onPlayerKillPlayer(SagaPlayer attacker, SagaPlayer defender, SagaChunk locationChunk){
		
	}
	
	/**
	 * Called when a player is damaged by a creature.
	 * 
	 * @param event event
	 * @param damager damager creature
	 * @param damaged damaged saga player
	 * @param locationChunk location chunk
	 */
	public void onPlayerDamagedByCreature(EntityDamageByEntityEvent event, Creature damager, SagaPlayer damaged, SagaChunk locationChunk){

	}

	/**
	 * Called when a player is damages a creature.
	 * 
	 * @param event event
	 * @param damager damager saga player
	 * @param damaged damaged creature
	 */
	public void onPlayerDamagedCreature(EntityDamageByEntityEvent event, SagaPlayer damager, Creature damaged){

		
	}
	
	
	// Move events:
	/**
	 * Called when a player moves to another saga chunk.
	 * 
	 * @param sagaPlayer saga player
	 * @param fromChunk from saga chunk, null if none
	 * @param toChunk to saga chunk, null if none
	 * @param fromLocation from location, null if none
	 * @param toLocation to location, null if none
	 * @param event event
	 */
	public void onPlayerSagaChunkChange(SagaPlayer sagaPlayer, SagaChunk fromChunk, SagaChunk toChunk, Location fromLocation, Location toLocation, PlayerMoveEvent event) {
		

		if(event.isCancelled()){
			return;
		}
		
		// Enter:
		if(toChunk == getOriginChunk()){

			// Get buildings:
			Building fromBuilding = null;
			Building toBuilding = null;

			if(fromChunk != null) fromBuilding = fromChunk.getBuilding();
			if(toChunk != null) toBuilding = toChunk.getBuilding();
			
			sagaPlayer.message(ChunkGroupMessages.moved(fromBuilding, toBuilding));
			
		}
		
		// Leave:
		else if(fromChunk == getOriginChunk()){

			// Get buildings:
			Building fromBuilding = null;
			Building toBuilding = null;

			if(fromChunk != null) fromBuilding = fromChunk.getBuilding();
			if(toChunk != null) toBuilding = toChunk.getBuilding();
			
			// Only if not entering a building:
			if(toBuilding == null){
				
				sagaPlayer.message(ChunkGroupMessages.moved(fromBuilding, toBuilding));
				
			}
			
		}
		
		
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

	
	// Commands:
	@Command(
			aliases = {"binfo"},
			usage = "[building name]",
			flags = "",
			desc = "Display building information.",
			min = 0,
			max = 1
	)
	@CommandPermissions({"saga.user.building.info"})
	public static void help(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		String buildingName = null;
		
		// Arguments:
		if(args.argsLength() == 1){
			
			buildingName = args.getString(0).replaceAll(SagaMessages.spaceSymbol, " ");
			
			BuildingDefinition definition = ChunkGroupConfiguration.config().getBuildingDefinition(buildingName);
			if(definition == null){
				sagaPlayer.message(BuildingMessages.invalidName(buildingName));
				return;
			}
			
			sagaPlayer.message(BuildingMessages.info(buildingName, definition));
			
		}else{
			
			sagaPlayer.message(BuildingMessages.info());

			return;
			
		}

		
	}
	
	
	// Messages:
	
	
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
