package org.saga.chunkGroups;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creature;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EndermanPickupEvent;
import org.bukkit.event.entity.EndermanPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.Saga;
import org.saga.SagaMessages;
import org.saga.buildings.Building;
import org.saga.buildings.MissingBuildingDefinitionException;
import org.saga.config.ChunkGroupConfiguration;
import org.saga.constants.IOConstants.WriteReadType;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.factions.SagaFaction;
import org.saga.player.SagaEntityDamageManager.SagaPvpEvent;
import org.saga.player.SagaEntityDamageManager.SagaPvpEvent.PvpDenyReason;
import org.saga.player.SagaPlayer;
import org.saga.utility.WriterReader;

import com.google.gson.JsonParseException;

public class ChunkGroup{

	
	/**
	 * Class name. Used by gson.
	 */
	@SuppressWarnings("unused")
	private String _className;
	
	/**
	 * Group name ID.
	 * -1 if none.
	 */
	private Integer id;
	
	/**
	 * Group name.
	 */
	private String name;
	
	/**
	 * Players associated with the group.
	 */
	private ArrayList<String> players;

	/**
	 * Factions associated with the group.
	 */
	private ArrayList<Integer> factions;
	
	/**
	 * Group chunks.
	 */
	private ArrayList<SagaChunk> groupChunks;
	
	/**
	 * Registered players.
	 */
	transient private ArrayList<SagaPlayer> registeredPlayers = new ArrayList<SagaPlayer>();
	
	/**
	 * Registered factions.
	 */
	transient private ArrayList<SagaFaction> registeredFactions = new ArrayList<SagaFaction>();
	
	/**
	 * Chunk group owners.
	 */
	private String owner;

	/**
	 * Player logout dates.
	 */
	private Hashtable<String, Date> lastOnlineDates;
	
	/**
	 * True if the chunk group is enabled.
	 */
	transient private boolean isEnabled = false;
	
	
	// Control:
	/**
	 * If true then saving is enabled.
	 */
	transient private Boolean isSavingEnabled;

	
	// Bonuses:
	/**
	 * Forced pvp protection.
	 */
	private Boolean pvpProtectionBonus;
	
	/**
	 * Unlimited claims.
	 */
	private Boolean unlimitedClaimBonus;
	

	// Properties:
	/**
	 * True if fire spread is enabled.
	 */
	private Boolean fireSpread;
	
	/**
	 * True if lava spread is enabled.
	 */
	private Boolean lavaSpread;
	
	
	// Initialization:
	/**
	 * Used by gson.
	 * 
	 */
	private ChunkGroup() {
	}
	
	/**
	 * Sets name and ID.
	 * 
	 * @param id ID
	 * @param name name
	 */
	public ChunkGroup(String name){
		
		this.name = name;
		this.id = ChunkGroupManager.manager().getUnusedChunkGroupId();
		players = new ArrayList<String>();
		factions = new ArrayList<Integer>();
		groupChunks = new ArrayList<SagaChunk>();
		this._className = getClass().getName();
		isSavingEnabled = true;
		owner = "";
		lastOnlineDates = new Hashtable<String, Date>();
		pvpProtectionBonus = false;
		unlimitedClaimBonus = false;
		fireSpread = false;
		lavaSpread = false;
		
	}
	
	/**
	 * Completes the initialization.
	 * 
	 * @return integrity
	 */
	public boolean complete() {

		
		boolean integrity=true;
		
		String group = id + "(" + name + ")";
		if(name == null){
			Saga.severe("ChunkGroup "+ group +" name not initialized. Setting unnamed.");
			name = "unnamed";
			integrity = false;
		}
		if(id == null){
			Saga.severe("ChunkGroup"+ group +" id not initialized. Setting -1.");
			id = -1;
			integrity = false;
		}
		if(players == null){
			Saga.severe("ChunkGroup "+ group +" players not initialized. Initializing empty list.");
			players = new ArrayList<String>();
			integrity = false;
		}
		for (int i = 0; i < players.size(); i++) {
			if(players.get(i) == null){
				Saga.severe("ChunkGroup "+ group +" players element not initialized. Removing element.");
				players.remove(i);
				i--;
				integrity = false;
			}
		}
		if(factions == null){
			Saga.severe("ChunkGroup "+ group +" factions not initialized. Initializing empty list.");
			factions = new ArrayList<Integer>();
			integrity = false;
		}
		for (int i = 0; i < factions.size(); i++) {
			if(factions.get(i) == null){
				Saga.severe("ChunkGroup "+ group +" factions element not initialized. Removing element.");
				factions.remove(i);
				i--;
				integrity = false;
			}
		}
		
		if(owner == null){
			Saga.info("ChunkGroup "+ group +" owners field not initialized. Setting default.");
			owner = "";
			integrity = false;
		}
		if(lastOnlineDates == null){
			Saga.severe("ChunkGroup "+ group +" lastOnlineDates field not initialized. Setting default.");
			lastOnlineDates = new Hashtable<String, Date>();
			integrity = false;
		}
		
		// Transient fields:
		registeredPlayers = new ArrayList<SagaPlayer>();
		registeredFactions = new ArrayList<SagaFaction>();
		isSavingEnabled = true;
	
		if(groupChunks == null){
			Saga.severe("ChunkGroup "+ group +" groupChunks not initialized. Initializing empty list.");
			groupChunks = new ArrayList<SagaChunk>();
			integrity = false;
		}
		for (int i = 0; i < groupChunks.size(); i++) {
			SagaChunk coords= groupChunks.get(i);
			if(coords == null){
			
				Saga.severe("ChunkGroup "+ group +" groupChunks element not initialized. Removing element.");
				groupChunks.remove(i);
				i--;
				continue;
				
			}
			coords.complete(this);
			// Buildings:
			if(coords.getBuilding() != null){
				
				try {
					integrity = coords.getBuilding().complete() && integrity;
				} catch (MissingBuildingDefinitionException e) {
					Saga.severe(this,"failed to complete " + coords.getBuilding().getName() + " building: "+ e.getClass().getSimpleName() + ":" + e.getMessage(), "removing element");
					disableSaving();
					coords.clearBuilding();
				}
			}
			
		}
		
		// Bonuses:
		if(pvpProtectionBonus == null){
			Saga.severe(this,"pvpProtectionBonus field not initialized","setting default");
			pvpProtectionBonus = false;
			integrity = false;
		}
		if(unlimitedClaimBonus == null){
			Saga.severe(this,"unlimitedClaimBonus field not initialized","setting default");
			unlimitedClaimBonus = false;
			integrity = false;
		}
		
		// Properties:
		if(fireSpread == null){
			Saga.severe(this,"fireSpread field not initialized", "setting default");
			fireSpread = false;
			integrity = false;
		}
		if(lavaSpread == null){
			Saga.severe(this,"lavaSpread field not initialized", "setting default");
			lavaSpread = false;
			integrity = false;
		}
		
		return integrity && completeExtended();
		
		
	}

	/**
	 * Completes extending class variable.
	 * Should be overridden.
	 * 
	 * @return integrity
	 */
	protected boolean completeExtended() {
		return true;
	}

	/**
	 * Enables the building
	 * 
	 */
	public void enable() {
		
		this.isEnabled = true;
		
		// Enable all buildings:
		ArrayList<Building> buildings = getBuildings();
		for (Building building : buildings) {
			
			if(!building.isEnabled()) building.enable();
			
		}
		
	}
	
	/**
	 * Disables the building.
	 * 
	 */
	public void disable() {
		
		this.isEnabled = false;

		// Enable all buildings:
		ArrayList<Building> buildings = getBuildings();
		for (Building building : buildings) {
			
			if(building.isEnabled()) building.disable();
			
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
	
	
	// Chunk group management:
	/**
	 * Adds a new chunk group.
	 * 
	 * @param chunkGroup chunk group.
	 */
	public final static void create(ChunkGroup chunkGroup){

		
		// Log:
		Saga.info("Creating " + chunkGroup + " chunk group.");

		// Update chunk group manager:
		ChunkGroupManager.manager().addChunkGroup(chunkGroup);
		
		// Do the first save:
		chunkGroup.save();
		
		// Refresh:
		ArrayList<SagaChunk> sagaChunks = chunkGroup.getGroupChunks();
		for (SagaChunk sagaChunk : sagaChunks) {
			sagaChunk.refresh();
		}
		
		// Enable:
		chunkGroup.enable();
		
		
	}
	
	/**
	 * Adds a new chunk group.
	 * 
	 * @param chunkGroup chunk group
	 * @param owner owner
	 */
	public static void create(ChunkGroup chunkGroup, SagaPlayer owner){
		

		// Add player:
		chunkGroup.addPlayer(owner);

		// Set owner:
		chunkGroup.setOwner(owner.getName());
		
		// Forward:
		create(chunkGroup);

		
	}

	
	/**
	 * Deletes a chunk group
	 * 
	 * @param groupName group name
	 */
	public void delete() {


		// Log:
		Saga.info("Deleting " + this + " chunk group.");
		
		// Disable:
		disable();
		
		// Remove all player:
		ArrayList<String> players = getPlayers();
		for (String player : players) {
			
			try {
				SagaPlayer sagaPlayer = Saga.plugin().forceSagaPlayer(player);
				removePlayer(sagaPlayer);
				Saga.plugin().unforceSagaPlayer(player);
			} catch (NonExistantSagaPlayerException e) {
				Saga.severe(this, "failed to remove " + player + " player", "removing player from the list");
				removePlayer(player);
			}
			
		}
		
		// Remove all saga chunks:
		ArrayList<SagaChunk> groupChunks = getGroupChunks();
		for (SagaChunk sagaChunk : groupChunks) {
			removeChunk(sagaChunk);
		}
		
		// Save one last time:
		save();
		
		// Remove from disc:
		WriterReader.deleteChunkGroup(getId().toString());
		
		// Update chunk group manager:
		ChunkGroupManager.manager().removeChunkGroup(this);
		
		
	}
	
	/**
	 * Adds a chunk.
	 * Needs to be done by chunk group manager, to update chunk shortcuts.
	 * 
	 * @param sagaChunk saga chunk
	 */
	public void addChunk(SagaChunk sagaChunk) {

		
		// Check if already on the list:
		if(groupChunks.contains(sagaChunk)){
			Saga.severe(this, "tried to add an already existing " + sagaChunk + "chunk", "ignoring request");
			return;
		}
		
		// Set chunk chunk group:
		sagaChunk.complete(this);
		
		// Add:
		groupChunks.add(sagaChunk);
		
		// Update chunk group manager:
		ChunkGroupManager.manager().addChunk(sagaChunk);
		
		// Refresh:
		sagaChunk.refresh();
		
		
	}
	
	/**
	 * Removes a chunk.
	 * Needs to be done by chunk group manager, to update chunk shortcuts.
	 * 
	 * @param sagaChunk saga chunk
	 */
	public void removeChunk(SagaChunk sagaChunk) {

		
		// Check if not in this group:
		if(!groupChunks.contains(sagaChunk)){
			Saga.severe(this, "tried to remove a non-existing " + sagaChunk + "chunk", "ignoring request");
			return;
		}
		
		// Remove member:
		groupChunks.remove(sagaChunk);

		// Update chunk group manager:
		ChunkGroupManager.manager().removeChunk(sagaChunk);

		// Refresh:
		sagaChunk.refresh();
		
		
	}

	/**
	 * Checks if the ID is on the list.
	 * 
	 * @param id ID
	 * @return true if on the list
	 */
	public boolean hasFaction(Integer id){
		return factions.contains(id);
	}

	/**
	 * Checks if the given bukkit chunk is adjacent to the chunk group.
	 * 
	 * @param bukkitChunk bukkit chunk
	 * @return true if adjacent
	 */
	public boolean isAdjacent(Chunk bukkitChunk) {

		String bWorld = bukkitChunk.getWorld().getName();
		int bX = bukkitChunk.getX();
		int bZ = bukkitChunk.getZ();
		
		for (int i = 0; i < groupChunks.size(); i++) {
			SagaChunk sChunk = groupChunks.get(i);
			// World:
			if(!sChunk.getWorldName().equals(bWorld)){
				continue;
			}
			// North from saga chunk:
			if( (sChunk.getX() == bX + 1) && (sChunk.getZ() == bZ) ){
				return true;
			}
			// East from saga chunk:
			if( (sChunk.getX() == bX) && (sChunk.getZ() == bZ + 1) ){
				return true;
			}
			// South from saga chunk:
			if( (sChunk.getX() == bX - 1) && (sChunk.getZ() == bZ) ){
				return true;
			}
			// West from saga chunk:
			if( (sChunk.getX() == bX) && (sChunk.getZ() == bZ - 1) ){
				return true;
			}
		}
		return false;
		
		
	}
	
	
	// Interaction:
	/**
	 * Sets the name
	 * 
	 * @param name name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets all chunks from the group.
	 * 
	 * @return group chunks
	 */
	public ArrayList<SagaChunk> getGroupChunks() {
		return new ArrayList<SagaChunk>(groupChunks);
	}
	
	/**
	 * Returns settlement size in chunks.
	 * 
	 * @return settlement size
	 */
	public int getSize() {
		return groupChunks.size();
	}

	/**
	 * Checks if the faction is formed.
	 * 
	 * @return true if formed
	 */
	public boolean isFormed() {
		return getActivePlayerCount() >= ChunkGroupConfiguration.config().formationAmount;
	}
	
	
	// Buildings:
	/**
	 * Gets all settlement buildings.
	 * 
	 * @return all settlement buildings
	 */
	public ArrayList<Building> getBuildings() {

		ArrayList<Building> buildings = new ArrayList<Building>();
		for (int i = 0; i < groupChunks.size(); i++) {
			Building building = groupChunks.get(i).getBuilding();
			if(building != null) buildings.add(building);
		}
		return buildings;
		
	}
	
	/**
	 * Gets  buildings count.
	 * 
	 * @return building count
	 */
	public Integer getBuildingCount() {

		return getBuildings().size();
		
	}
	
	/**
	 * Gets the total available building points.
	 * 
	 * @return total building points.
	 */
	public Integer getTotalBuildingPoints() {
		return 0;
	}
	
	/**
	 * Gets used building points.
	 * 
	 * @return
	 */
	public Integer getUsedBuildingPoints() {
		
		int used = 0;
		for (int i = 0; i < groupChunks.size(); i++) {
			Building building = groupChunks.get(i).getBuilding();
			if(building != null) used += building.getPointCost();
		}
		
		return used;
	}
	
	/**
	 * Gets the available building points
	 * 
	 * @return available building points
	 */
	public Integer getAvailableBuildingPoints() {
		return getTotalBuildingPoints() - getUsedBuildingPoints();
	}

	/**
	 * Gets the amount of enabled buildings. 
	 * 
	 * @param buildingName building name
	 * @return amount used
	 */
	public Integer getUsedBuildings(String buildingName) {


		// Total buildings:
		Integer total = 0;
		ArrayList<SagaChunk> groupChunks = getGroupChunks();
		for (SagaChunk sagaChunk : groupChunks) {
			
			Building building = sagaChunk.getBuilding();
			if(building == null) continue;
			if(!building.getName().equals(buildingName)) continue;
			
			total ++;
			
		}
		
		return total;
		
		
	}

	/**
	 * Gets the amount of total enabled buildings. 
	 * 
	 * @param buildingName building name
	 * @return total amount
	 */
	public Integer getTotalBuildings(String buildingName) {


		// Total buildings:
		Integer total = 0;
		ArrayList<SagaChunk> groupChunks = getGroupChunks();
		for (SagaChunk sagaChunk : groupChunks) {
			
			Building building = sagaChunk.getBuilding();
			if(building == null) continue;
			
			total += building.getDefinition().getTotalBuildings(buildingName, building.getLevel());
			
		}
		
		return total;
		
		
	}
	
	/**
	 * Gets the amount of available enabled buildings. 
	 * 
	 * @param buildingName building name
	 * @return amount available
	 */
	public Integer getAvailableBuildings(String buildingName) {

		return getTotalBuildings(buildingName) - getUsedBuildings(buildingName);
		
	}
	
	/**
	 * Checks if the a building is available.
	 * 
	 * @param buildingName building name
	 * @return true if available
	 */
	public boolean isBuildingAvailable(String buildingName) {
		
		return getAvailableBuildings(buildingName) > 0;
		
	}
	
	/**
	 * Gets all enabled buildings.
	 * 
	 * @return enabled buildings
	 */
	public HashSet<String> getEnabledBuildings() {

		
		HashSet<String> enabledBuildings = new HashSet<String>();
		
		ArrayList<SagaChunk> groupChunks = getGroupChunks();
		for (SagaChunk sagaChunk : groupChunks) {
			
			Building building = sagaChunk.getBuilding();
			if(building == null) continue;
			
			enabledBuildings.addAll(building.getDefinition().getBuildings(building.getLevel()));
			
		}
		
		return enabledBuildings;
		
		
	}
	
	/**
	 * Gets all buildings instance of the given class.
	 * 
	 * @param buildingClass class
	 * @return buildings that are instances of the given class
	 */
	public <T extends Building> ArrayList<T> getBuildings(Class<T> buildingClass){
		
		
		ArrayList<Building> buildings = getBuildings();
		ArrayList<T> filteredBuildings = new ArrayList<T>();
		for (Building building : buildings) {
			
			if(buildingClass.isInstance(building)){
				try {
					filteredBuildings.add(buildingClass.cast(building));
				} catch (Exception e) {
				}
			}
			
		}
		
		return filteredBuildings;
		
		
	}
	
	
	// Todo methods:
	/**
	 * Registers a faction.
	 * 
	 * @param sagaFaction saga faction
	 */
	void registerFaction(SagaFaction sagaFaction) {

		
		// Check list:
		if(registeredFactions.contains(sagaFaction)){
			Saga.severe("Tried to register an already registered faction for " + this + " chunk group. Ignoring request.");
			return;
		}
		
		// Register faction:
		registeredFactions.add(sagaFaction);

		
	}
	
	/**
	 * Unregisters a faction.
	 * Will not add player permanently to the faction list.
	 * Used by SagaPlayer to create a connection with the faction.
	 * Should not be used.
	 * 
	 * @param sagaFaction saga faction
	 */
	void unregisterFaction(SagaFaction sagaFaction) {

		
		// Check list:
		if(!registeredFactions.contains(sagaFaction)){
			Saga.severe("Tried to unregister a non-registered faction for " + this + " chunk group.");
			return;
		}

		// Unregister faction:
		registeredFactions.remove(sagaFaction);
		
		
	}
	

	// Player and faction management:
	/**
	 * Adds a player.
	 * 
	 * @param playerName player name
	 */
	private void addPlayer(String playerName) {

		
		// Check if already on the list:
		if(players.contains(playerName)){
			Saga.severe("Tried to add an already existing " + playerName + " player to "+ this +" chunk group. Ignoring request.");
			return;
		}
		
		// Add player:
		players.add(playerName);

		
	}

	/**
	 * Removes a player.
	 * 
	 * @param playerName player name
	 */
	private void removePlayer(String playerName) {
		
		
		// Check if not in this settlement:
		if(!players.contains(playerName)){
			Saga.severe("Tried to remove a non-member " + playerName + " player from " + this +  "chunk group.");
			return;
		}

		// Remove player:
		players.remove(playerName);

		// Remove ownership:
		if(isOwner(playerName)){
			removeOwner();
		}
		
		
	}

	/**
	 * Adds and registers a player.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void addPlayer(SagaPlayer sagaPlayer) {


		// Add player:
		addPlayer(sagaPlayer.getName());
		
		// Set chunk group ID:
		sagaPlayer.setChunkGroupId(getId());
		
		// Register:
		registerPlayer(sagaPlayer);

		
	}

	/**
	 * Removes and unregisters a player.
	 * 
	 * @param playerName saga player
	 */
	public void removePlayer(SagaPlayer sagaPlayer) {
		
		
		// Remove player:
		removePlayer(sagaPlayer.getName());

		// Remove chunk group ID:
		sagaPlayer.removeChunkGroupId(getId());
		
		// Unregister:
		unregisterPlayer(sagaPlayer);

		
	}
	
	/**
	 * Registers a player.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void registerPlayer(SagaPlayer sagaPlayer) {

		
		// Check list:
		if(registeredPlayers.contains(sagaPlayer)){
			Saga.severe("Tried to register an already registered " + sagaPlayer + " player for " + this + " chunk group. Ignoring request.");
			return;
		}
		
		// Register player:
		registeredPlayers.add(sagaPlayer);

		// Register chunk group:
		sagaPlayer.registerChunkGroup(this);
		
		// Saving disabled:
		if(!isSavingEnabled){
			sagaPlayer.error("saving disabled for " + getName() + " settlement");
		}
		
		
	}
	
	/**
	 * Unregisters a player.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void unregisterPlayer(SagaPlayer sagaPlayer) {

		
		// Check list:
		if(!registeredPlayers.contains(sagaPlayer)){
			Saga.severe("Tried to unregister a non-registered " + sagaPlayer + " player for " + this + " chunk group. Ignoring request.");
			return;
		}

		// Register player:
		registeredPlayers.remove(sagaPlayer);
		
		// Unregister chunk group:
		sagaPlayer.unregisterChunkGroup(this);
		
		// Add log out date:
		lastOnlineDates.put(sagaPlayer.getName(), sagaPlayer.getLastOnline());
		
		
	}

	
	// Player:
	/**
	 * Gets players associated.
	 * 
	 * @return player names
	 */
	public ArrayList<String> getPlayers() {
		return new ArrayList<String>(players);
	}
	
	/**
	 * Gets the player count
	 * 
	 * @return player count
	 */
	public int getPlayerCount() {
		return players.size();
	}
	
	/**
	 * Gets the registered players.
	 * 
	 * @return the registered players
	 */
	public ArrayList<SagaPlayer> getRegisteredPlayers() {
		return new ArrayList<SagaPlayer>(registeredPlayers);
	}
	
	/**
	 * Gets the registered player count
	 * 
	 * @return registered player count
	 */
	public int getRegisteredPlayerCount() {
		return registeredPlayers.size();
	}
	
	/**
	 * Checks if the player is on the chunk groups list.
	 * 
	 * @param playerName player name
	 * @return true if player is on the list
	 */
	public boolean hasPlayer(String playerName) {
		

		boolean registered = players.contains(playerName);
		if(registered){
			return true;
		}
		for (int i = 0; i < registeredFactions.size(); i++) {
			if(registeredFactions.get(i).isMember(playerName)) return true;
		}
		return false;
		

	}
	
	/**
	 * Checks if the player is registered.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if player is registered
	 */
	public boolean hasRegisteredPlayer(SagaPlayer sagaPlayer) {
		
		return registeredPlayers.contains(sagaPlayer);

	}
	
	/**
	 * Checks if the player is registered.
	 * 
	 * @param playerName player name
	 * @return true if player is registered
	 */
	public boolean hasRegisteredPlayer(String playerName) {
		
		for (int i = 0; i < registeredPlayers.size(); i++) {
			if(registeredPlayers.get(i).getName().equals(playerName)) return true;
		}
		
		return false;

	}
	
	/**
	 * Check if the saga player is a member.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if member
	 */
	public boolean isMember(SagaPlayer sagaPlayer) {

		
		boolean iMember = hasPlayer(sagaPlayer.getName());
		if(iMember) return true;
		
		ArrayList<SagaFaction> factions = getRegisteredFactions();
		for (SagaFaction sagaFaction : factions) {
			if(sagaFaction.isMember(sagaPlayer.getName())) return true;
		}
		return false;
		
		
	}
	
	/**
	 * Matches a name to a members name.
	 * 
	 * @param name name
	 * @return matched name, same as given if not found
	 */
	public String matchName(String name) {
		
		ArrayList<String> members = getPlayers();
		for (String memberName : members) {
			
			if(memberName.equalsIgnoreCase(name)) return memberName;

//			if(memberName.toLowerCase().contains(name)) name = memberName;
			
		}
		return name;

	}
	
	
	// Time:
	/**
	 * Gets player logout date.
	 * 
	 * @param playerName player name
	 * @return log out date, null if not found, saga players date if registered
	 */
	private Date getPlayerLogOutDate(String playerName) {
		
		
		// Online players:
		for (int i = 0; i < registeredPlayers.size(); i++) {
			if(registeredPlayers.get(i).getName().equalsIgnoreCase(playerName)) return registeredPlayers.get(i).getLastOnline();
		}
		
		// Offline players:
		Date logOutDate = lastOnlineDates.get(playerName);
		if(logOutDate != null){
			return logOutDate;
		}
		
		return null;
		
		
	}
	
	/**
	 * Check if the player is active.
	 * 
	 * @param playerName player name
	 * @return true if active
	 */
	public boolean isPlayerActive(String playerName) {

		
		Calendar inactivateCalendar = Calendar.getInstance();
		inactivateCalendar.add(Calendar.DAY_OF_MONTH, - ChunkGroupConfiguration.config().inactiveSetDays);
		Date inactivateDate = inactivateCalendar.getTime();
		Date logoutDate = getPlayerLogOutDate(playerName);
		
		if(logoutDate == null){
			return false;
		}
		
		return !inactivateDate.after(logoutDate);
		
		
	}
	
	/**
	 * Gets inactive player count.
	 * 
	 * @return inactive player count
	 */
	public int getInactivePlayerCount() {

		
		int inactivePlayers = 0;
		ArrayList<String> players = getPlayers();
		for (String playerName : players) {
			if(!isPlayerActive(playerName)){
				inactivePlayers++;
			}
		}
		return inactivePlayers;
		
		
	}
	
	/**
	 * Gets inactive player count.
	 * 
	 * @return inactive player count
	 */
	public int getActivePlayerCount() {

		
		int inactivePlayers = 0;
		ArrayList<String> players = getPlayers();
		for (String playerName : players) {
			if(isPlayerActive(playerName)){
				inactivePlayers++;
			}
		}
		return inactivePlayers;
		
		
	}
	
	/**
	 * Gets the boolean list of players that specifies if the player is inactive.
	 * 
	 * @return inactive active player list
	 */
	public ArrayList<Boolean> arePlayersActive() {

		
		Calendar inactivateCalendar = Calendar.getInstance();
		inactivateCalendar.add(Calendar.DAY_OF_MONTH, - ChunkGroupConfiguration.config().inactiveSetDays);
		Date inactivateDate = inactivateCalendar.getTime();
		ArrayList<Boolean> areActive = new ArrayList<Boolean>();
		
		ArrayList<String> players = getPlayers();
		for (String playerName : players) {
			
			Date logoutDate = getPlayerLogOutDate(playerName);
			if(logoutDate == null){
				Saga.severe(this, "failed to retrieve player log out date", "using current date");
				logoutDate = Calendar.getInstance().getTime();
			}
			
			if(inactivateDate.after(logoutDate)){
				areActive.add(false);
			}else{
				areActive.add(true);
			}
			
		}
		
		return areActive;
		
		
	}
	
	
	// Owners:
	/**
	 * Checks if the player counts as the owner of the settlement.
	 * 
	 * @param playerName player name
	 * @return true if owner
	 */
	public boolean isOwner(String playerName) {

		return owner.equalsIgnoreCase(playerName);
		
	}
	
	/**
	 * Sets an owner.
	 * 
	 * @param playerName player name
	 */
	public void setOwner(String playerName) {
		owner = playerName;
	}

	/**
	 * Removes an owner.
	 * 
	 * @param playerName player name
	 */
	public void removeOwner() {
		owner = "";
	}
	
	/**
	 * Gets the owner.
	 * 
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * Returns the owner count.
	 * 
	 * @return owner count
	 */
	public boolean hasOwner() {
		return !owner.equals("");
	}
	
	
	// Factions:
	/**
	 * Gets factions associated.
	 * 
	 * @return faction IDs
	 */
	public ArrayList<Integer> getFactions() {
		return factions;
	}

	/**
	 * Gets the registered factions.
	 * 
	 * @return the registered factions
	 */
	public ArrayList<SagaFaction> getRegisteredFactions() {
		return new ArrayList<SagaFaction>(registeredFactions);
	}

	/**
	 * Checks if the faction is registered.
	 * 
	 * @param sagaFaction saga faction
	 * @return true if registered
	 */
	public boolean isFactionRegistered(SagaFaction sagaFaction) {
		return registeredFactions.contains(sagaFaction);
	}

	/**
	 * Gets the factions count.
	 * 
	 * @return factions count
	 */
	public int getFactionCount() {
		return factions.size();
	}

	
	// Permissions:
	/**
	 * Checks if the player can build.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if the player can build
	 */
	public boolean canBuild(SagaPlayer sagaPlayer) {
		return false;
	}
	
	/**
	 * Checks if the player can claim a chunk.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if the player can claim
	 */
	public boolean canClaim(SagaPlayer sagaPlayer) {
		return false;
	}
	
	/**
	 * Checks if the player can abandon a chunk.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if the player can abandon
	 */
	public boolean canAbandon(SagaPlayer sagaPlayer) {
		return false;
	}
	
	/**
	 * Checks if the player can delete the group.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if the player can delete
	 */
	public boolean canDisolve(SagaPlayer sagaPlayer) {
		return false;
	}

	/**
	 * Checks if the player has permission to invite.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can invite
	 */
	public boolean canInvite(SagaPlayer sagaPlayer) {
		return false;
	}

	/**
	 * Checks if the player has permission to kick.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can kick
	 */
	public boolean canKick(SagaPlayer sagaPlayer) {
		return false;
	}
	
	/**
	 * Checks if the player has permission to quit.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can quit
	 */
	public boolean canQuit(SagaPlayer sagaPlayer) {
		return true;
	}

	/**
	 * Checks if the chunk group can be claimed.
	 * 
	 * @param sagaPlayer saga player.
	 * @return true if can be claimed
	 */
	public boolean canClaimChunkGroup(SagaPlayer sagaPlayer) {
		return false;
	}

	/**
	 * Checks if the player can set buildings.
	 * 
	 * @param sagaPlayer saga player.
	 * @param building building
	 * @return true if can be claimed
	 */
	public boolean canSetBuilding(SagaPlayer sagaPlayer, Building building) {
		return false;
	}
	
	/**
	 * Checks if the player can remove buildings.
	 * 
	 * @param sagaPlayer saga player.
	 * @param building building
	 * @return true if can be claimed
	 */
	public boolean canRemoveBuilding(SagaPlayer sagaPlayer, Building building) {
		return false;
	}
	
	/**
	 * Check if the player can declare owners.
	 * 
	 * @param sagaPlayer
	 * @return
	 */
	public boolean canDeclareOwner(SagaPlayer sagaPlayer) {
		return false;
	}

	/**
	 * Checks if the player has permission to rename.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can set color
	 */
	public boolean canRename(SagaPlayer sagaPlayer) {

		return false;
	}

	/**
	 * Checks if the player has permission to spawn.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can spawn
	 */
	public boolean canSpawn(SagaPlayer sagaPlayer) {
		return false;
	}

	/**
	 * Checks if the player has permission to hurt animals.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can hurt animals
	 */
	public boolean canHurtAnimals(SagaPlayer sagaPlayer) {
		return true;
	}
	
	/**
	 * Checks if the player has permission to trample crops.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can trample
	 */
	public boolean canTrample(SagaPlayer sagaPlayer) {
		return false;
	}

	/**
	 * Checks if the player has permission to use the command.
	 * 
	 * @param sagaPlayer saga player
	 * @param command comand
	 * @return true if can use command
	 */
	public boolean canUseCommand(SagaPlayer sagaPlayer, String command) {
		
		if(ChunkGroupConfiguration.config().checkMemberOnlyCommand(command) && !sagaPlayer.isAdminMode()){
			return false;
		}
		return true;
		
	}

	/**
	 * Checks if the player has permission to use potions.
	 * 
	 * @param sagaPlayer saga player
	 * @param durability durability
	 * @return true if can use potion
	 */
	public boolean canUsePotion(SagaPlayer sagaPlayer, Short durability) {
		
		return false;
		
	}
	
	
	// Bonuses:
	/**
	 * Gets the forcedPvpProtection.
	 * 
	 * @return the forcedPvpProtection
	 */
	public Boolean hasPvpProtectionBonus() {
		return pvpProtectionBonus;
	}

	/**
	 * Toggles the forcedPvpProtection.
	 */
	public void togglePvpProtectionBonus() {
		this.pvpProtectionBonus = !pvpProtectionBonus;
	}

	/**
	 * Gets the enabledUnlimitedClaim.
	 * 
	 * @return the enabledUnlimitedClaim
	 */
	public Boolean hasUnlimitedClaimBonus() {
		return unlimitedClaimBonus;
	}

	/**
	 * Toggles the enabledUnlimitedClaim.
	 */
	public void toggleUnlimitedClaim() {
		this.unlimitedClaimBonus = !unlimitedClaimBonus;
	}
	
	
	// Messages:
	/**
	 * Broadcast a message to all members.
	 * 
	 * @param message message
	 */
	public void broadcast(String message){
		
		
		for (int i = 0; i < registeredPlayers.size(); i++) {
			registeredPlayers.get(i).message(message);
		}
		for (int i = 0; i < registeredFactions.size(); i++) {
			registeredFactions.get(i).broadcast(message);
		}
		
		
	}
	

	// Getters:
	/**
	 * Gets chunk group ID.
	 * 
	 * @return ID
	 */
	public Integer getId() {
		return id;
	}
	
	/**
	 * Sets the ID.
	 * 
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	
	// Events:
	/**
	 * Called when an entity explodes on the chunk
	 * 
	 * @param event event
	 */
	void onEntityExplode(EntityExplodeEvent event, SagaChunk locationChunk) {

		
		// Cancel entity explosions:
		event.blockList().clear();

		
	}

	/**
	 * Called when a creature spawns.
	 * 
	 * @param event event
	 * @param locationChunk origin chunk.
	 */
	void onCreatureSpawn(CreatureSpawnEvent event, SagaChunk locationChunk) {
		

		if(event.isCancelled()){
			return;
		}
		
		// Forward to all buildings:
		for (int i = 0; i < groupChunks.size() && !event.isCancelled(); i++) {
			Building building = groupChunks.get(i).getBuilding();
			if(building != null) building.onCreatureSpawn(event, locationChunk);
		}
		
		
	}
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


		// Forward to all buildings:
		for (int i = 0; i < groupChunks.size() && !event.isCancelled(); i++) {
			
			SagaChunk sagaChunk = groupChunks.get(i);
			Building building = sagaChunk.getBuilding();
			
			if(building == null) continue;
			
			// Forward:
			building.onPlayerSagaChunkChange(sagaPlayer, fromChunk, toChunk, fromLocation, toLocation, event);
			
		}
		
		if(event.isCancelled()){
			return;
		}
		
		// Enter:
		if(fromChunk == null && toChunk != null){
			sagaPlayer.message(ChunkGroupMessages.entered(this));
		}
		
		// Leave:
		if(toChunk == null && fromChunk != null){
			sagaPlayer.message(ChunkGroupMessages.left(this));
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

		
		// Forward to all buildings:
		for (int i = 0; i < groupChunks.size(); i++) {
			Building building = groupChunks.get(i).getBuilding();
			if(building != null) building.onMemberRespawn(sagaPlayer, event);
		}
		
		
	}
	
	/**
	 * Member join event.
	 * 
	 * @param sagaPlayer saga player
	 * @param event event
	 */
	public void onMemberJoin(SagaPlayer sagaPlayer, PlayerJoinEvent event) {

		
		// Send to all buildings:
		for (int i = 0; i < groupChunks.size(); i++) {
			Building building = groupChunks.get(i).getBuilding();
			if(building != null) building.onMemberJoin(sagaPlayer, event);
		}
		
		
	}
	
	/**
	 * Member quit event.
	 * 
	 * @param sagaPlayer saga player
	 * @param event event
	 */
	public void onMemberQuit(SagaPlayer sagaPlayer, PlayerQuitEvent event) {

		
		// Send to all buildings:
		for (int i = 0; i < groupChunks.size(); i++) {
			Building building = groupChunks.get(i).getBuilding();
			if(building != null) building.onMemberQuit(sagaPlayer, event);
		}
		
		
	}
	
	/**
	 * Called when Enderman pickups an item.
	 * 
	 * @param event event
	 * @param sagaChunk saga chunk
	 */
	public void onEndermanPickup(EndermanPickupEvent event, SagaChunk sagaChunk) {
		
		// Effers:
		event.setCancelled(true);
		
	}
	
	/**
	 * Called when Enderman pickups an item.
	 * 
	 * @param event event
	 * @param sagaChunk saga chunk
	 */
	public void onEndermanPlace(EndermanPlaceEvent event, SagaChunk sagaChunk) {
		
		// Worse than creepers:
		event.setCancelled(true);
				
	}
	
	/**
	 * Called when a block spreads.
	 * 
	 * @param event event
	 * @param sagaChunk saga chunk
	 */
	public void onBlockSpread(BlockSpreadEvent event, SagaChunk sagaChunk) {
		
		
		// Cancel fire spread:
		if(!fireSpread){
			
			if(event.getNewState().getType().equals(Material.FIRE)){
				event.setCancelled(true);
				return;
			}
			
		}
		
		
		
	}
	
	/**
	 * Called when a block forms.
	 * 
	 * @param event event
	 * @param sagaChunk saga chunk
	 */
	public void onBlockFromTo(BlockFromToEvent event, SagaChunk sagaChunk) {
		
		
		// Cancel lava spread:
		if(!lavaSpread){
			
			if(event.getBlock().getType().equals(Material.STATIONARY_LAVA)){
				event.setCancelled(true);
				return;
			}
			if(event.getBlock().getType().equals(Material.LAVA)){
				event.setCancelled(true);
				return;
			}
			
		}
		
		
	}

    /**
     * Called when a player performs a command.
     * 
     * @param sagaPlayer saga player
     * @param event event
     * @param sagaChunk location chunk
     */
    public void onPlayerCommandPreprocess(SagaPlayer sagaPlayer, PlayerCommandPreprocessEvent event, SagaChunk sagaChunk) {

    	String command = event.getMessage().split(" ")[0].replace("/", "");
    	
    	// Permission:
    	if(!canUseCommand(sagaPlayer, command)){
    		sagaPlayer.message(SagaMessages.noCommandPermission(this, command));
    		event.setCancelled(true);
    		return;
    	}

    }
    
	
	// Interact events:
	/**
	 * Called when a player interacts with something on the chunk.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 * @param sagaChunk saga chunk
	 */
    @SuppressWarnings("deprecation")
	public void onPlayerInteract(PlayerInteractEvent event, SagaPlayer sagaPlayer, SagaChunk sagaChunk) {
    	

		// Canceled:
		if(event.isCancelled()){
			return;
		}
		
		ItemStack item = event.getPlayer().getItemInHand();
		Block block = event.getClickedBlock();
		
		// Buckets and flint steel:
		if(!canBuild(sagaPlayer) && item != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)){

			switch (item.getType()) {
				
				case LAVA_BUCKET:
					
					event.setCancelled(true);
					event.setUseItemInHand(Result.DENY);
					sagaPlayer.message(SagaMessages.noPermission(this));
					event.getPlayer().updateInventory();
					return;
				
				case FLINT_AND_STEEL:
					
					event.setCancelled(true);
					event.setUseItemInHand(Result.DENY);
					sagaPlayer.message(SagaMessages.noPermission(this));
					return;
				
				case WATER_BUCKET:
					
					event.setCancelled(true);
					event.setUseItemInHand(Result.DENY);
					sagaPlayer.message(SagaMessages.noPermission(this));
					event.getPlayer().updateInventory();
					return;
					
				case BUCKET:
	
					event.setCancelled(true);
					event.setUseItemInHand(Result.DENY);
					sagaPlayer.message(SagaMessages.noPermission(this));
					event.getPlayer().updateInventory();
					return;

				default:
					break;
				
			}
			
		}
		
		// Potions:
		if(item != null && item.getType() == Material.POTION){

			Short durability = item.getDurability();
			
			if(!canUsePotion(sagaPlayer, durability)){
				event.setUseItemInHand(Result.DENY);
				sagaPlayer.message(SagaMessages.noPermission(this));
				event.getPlayer().updateInventory();
				return;
			}
			
		}
		
		// Fire:
		if(!canBuild(sagaPlayer) && block != null && block.getRelative(BlockFace.UP) != null && block.getRelative(BlockFace.UP).getType() == Material.FIRE){
			
			event.setCancelled(true);
			event.setUseInteractedBlock(Result.DENY);
			sagaPlayer.message(SagaMessages.noPermission(this));
			event.getPlayer().updateInventory();
			return;
			
		}
		
		
    }

    /**
     * Called when a player interacts with an entity on the chunk.
     * 
     * @param event event
     * @param sagaPlayer saga player
     * @param sagaChunk saga chunk
     */
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event, SagaPlayer sagaPlayer, SagaChunk locationChunk) {
		
		
    }

    
    // Damage events:
	/**
	 * Called when a player is damaged by another player.
	 * 
	 * @param event event
	 * @param damager damager saga player
	 * @param damaged damaged saga player
	 * @param locationChunk chunk where the pvp occured
	 */
	void onPlayerVersusPlayer(SagaPvpEvent event, SagaChunk locationChunk){
		

		// Safe area:
		if(hasPvpProtectionBonus()){
			event.deny(PvpDenyReason.SAFE_AREA);
		}
		
		// Forward to all buildings:
		for (int i = 0; i < groupChunks.size(); i++) {
			Building building = groupChunks.get(i).getBuilding();
			if(building != null) building.onPlayerVersusPlayer(event, locationChunk);
		}

		
	}
	
	/**
	 * Called when a player is kiled by another player.
	 * 
	 * @param event event
	 * @param damager damager saga player
	 * @param damaged damaged saga player
	 * @param locationChunk chunk where the pvp occured
	 */
	void onPlayerKillPlayer(SagaPlayer attacker, SagaPlayer defender, SagaChunk locationChunk){
		
		
		// Forward to all buildings:
		for (int i = 0; i < groupChunks.size(); i++) {
			Building building = groupChunks.get(i).getBuilding();
			if(building != null) building.onPlayerKillPlayer(attacker, defender, locationChunk);
		}

		
	}
	
	/**
	 * Called when a player is damaged by a creature.
	 * 
	 * @param event event
	 * @param damager damager creature
	 * @param damaged damaged saga player
	 * @param locationChunk location chunk
	 */
	void onPlayerDamagedByCreature(EntityDamageByEntityEvent event, Creature damager, SagaPlayer damaged, SagaChunk locationChunk){


		if(event.isCancelled()){
			return;
		}
		
		// Forward to all buildings:
		for (int i = 0; i < groupChunks.size() && !event.isCancelled(); i++) {
			Building building = groupChunks.get(i).getBuilding();
			if(building != null) building.onPlayerDamagedByCreature(event, damager, damaged, locationChunk);
		}
		
		
	}
	
	
	// Block events:
	/**
	 * Called when a block is placed in the chunk.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 */
	void onBlockPlace(BlockPlaceEvent event, SagaPlayer sagaPlayer, SagaChunk locationChunk) {
		

		// Check permission:
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
	public void onBlockBreak(BlockBreakEvent event, SagaPlayer sagaPlayer, SagaChunk locationChunk) {
		

		// Check permission:
		if(!canBuild(sagaPlayer)){
			event.setCancelled(true);
			sagaPlayer.message(SagaMessages.noPermission(this));
			return;
		}
		
		
	}

	
    // Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getId() + "(" + getName() + ")";
	}
	
	
	// Control:
	/**
	 * Disables saving.
	 * 
	 */
	private void disableSaving() {

		Saga.warning("Disabling saving for " + this + " chunk group." );
		isSavingEnabled = false;
		
	}
	
	/**
	 * Checks if saving is enabled.
	 * 
	 * @return true if enabled
	 */
	public boolean isSavingEnabled() {
		return isSavingEnabled;
	}

	
	// Load save
	/**
	 * Loads and a faction from disc.
	 * 
	 * @param chunkGroupId faction ID in String form
	 * @return saga faction
	 */
	public static ChunkGroup load(String chunkGroupId) {

		
		// Load:
		String configName = "" + chunkGroupId + " chunk group";
		ChunkGroup config;
		try {
			config = WriterReader.readChunkGroup(chunkGroupId.toString());
		} catch (FileNotFoundException e) {
			Saga.info("Missing " + configName + ". Creating a new one.");
			config = new ChunkGroup();
		} catch (IOException e) {
			Saga.severe("Failed to load " + configName + ". Loading defaults.");
			config = new ChunkGroup();
			config.disableSaving();
		} catch (JsonParseException e) {
			Saga.severe("Failed to parse " + configName + ". Loading defaults.");
			Saga.info("Parse message :" + e.getMessage());
			config = new ChunkGroup();
			config.disableSaving();
		}
		
		// Complete:
		config.complete();
		
		// Add to manager:
		ChunkGroupManager.manager().addChunkGroup(config);
		ArrayList<SagaChunk> groupChunks = config.getGroupChunks();
		for (SagaChunk sagaChunk : groupChunks) {
			ChunkGroupManager.manager().addChunk(sagaChunk);
		}
		
		// Enable:
		config.enable();
		
		return config;
		
		
	}

	/**
	 * Saves faction to disc.
	 * 
	 */
	public void save() {

		
		String configName = "" + id + " chunk group";
		if(!isSavingEnabled){
			Saga.warning("Saving disabled for "+ configName + ". Ignoring save request." );
			return;
		}
		
		try {
			WriterReader.writeChunkGroup(id.toString(), this, WriteReadType.SETTLEMENT_NORMAL);
		} catch (IOException e) {
			Saga.severe("Failed to write "+ configName +". Ignoring write.");
		}
		
		
	}



}
