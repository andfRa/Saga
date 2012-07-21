package org.saga.buildings;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.Clock;
import org.saga.Clock.DaytimeTicker;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.buildings.signs.AbilitySign;
import org.saga.buildings.signs.AttributeSign;
import org.saga.buildings.signs.BuildingSign;
import org.saga.buildings.signs.BuildingSign.SignException;
import org.saga.buildings.storage.StorageArea;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.ChunkGroupToggleable;
import org.saga.chunkGroups.SagaChunk;
import org.saga.config.SettlementConfiguration;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.exceptions.InvalidLocationException;
import org.saga.listeners.events.SagaBuildEvent;
import org.saga.listeners.events.SagaBuildEvent.BuildOverride;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.messages.BuildingMessages;
import org.saga.messages.SagaMessages;
import org.saga.player.SagaPlayer;
import org.saga.saveload.SagaCustomSerialization;
import org.saga.settlements.Settlement.SettlementPermission;
import org.saga.utility.items.RandomRecipe;
import org.saga.utility.items.RecepieBlueprint;
import org.saga.utility.text.TextUtil;
import org.sk89q.CommandContext;

public abstract class Building extends SagaCustomSerialization implements DaytimeTicker{


	/**
	 * Name.
	 */
	private String name;
	
	/**
	 * Building level.
	 */
	private Short level;
	
	/**
	 * Building signs.
	 */
	private ArrayList<BuildingSign> signs;
	
	
	/**
	 * Storage areas.
	 */
	private ArrayList<StorageArea> storage;
	
	
	/**
	 * Origin chunk.
	 */
	transient SagaChunk originChunk;
	
	/**
	 * Building definition.
	 */
	transient private BuildingDefinition definition;
	
	
	
	// Initialisation:
	/**
	 * Creates a building from a definition.
	 * 
	 * @param definition building definition
	 */
	public Building(BuildingDefinition definition) {
		
		this.name = definition.getName();
		this.definition = definition;
		this.level = 0;
		this.signs = new ArrayList<BuildingSign>();
		this.storage = new ArrayList<StorageArea>();
		
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
		
		if(name == null){
			name = TextUtil.className(getClass());
			SagaLogger.nullField(this, "name");
			integrity = false;
		}
		
		if(level == null){
			level = 0;
			SagaLogger.nullField(this, "level");
			integrity = false;
		}
		
		definition = SettlementConfiguration.config().getBuildingDefinition(name);
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
				SagaLogger.severe(this, "failed to initialise signs field element: " + e.getClass().getSimpleName() + ":" + e.getMessage());
				signs.remove(i);
				i--;
				continue;
			}
			
		}
		
		if(storage == null){
			storage = new ArrayList<StorageArea>();
			SagaLogger.nullField(this, "storage");
			integrity = false;
		}
		if(storage.remove(null)){
			SagaLogger.nullField(this, "storage element");
			integrity = false;
		}
		for (int i = 0; i < storage.size(); i++) {
			
			try {
				storage.get(i).complete();
				storage.get(i).setSize(definition.getStorageSize());
			} catch (InvalidLocationException e) {
				SagaLogger.severe(this, "failed to initialise storage field element: " + e.getClass().getSimpleName() + ":" + e.getMessage());
				storage.remove(i);
				i--;
				continue;
			}
			
		}
		
		// Refresh signs:
		refreshSigns();
		
		// Fix className:
		if(!get_className().equals(definition.getBuildingClass())){
			SagaLogger.severe(this, "building and definition className fields don't match");
			SagaLogger.info(this, "_className=" + get_className() + ", buildingClass=" + definition.getBuildingClass());
			set_className(definition.getBuildingClass());
		}
		
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
	
	

	// General:
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public final String getName() {
		return name;
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
	 * Gets the level.
	 * 
	 * @return the level
	 */
	public Integer getLevel() {
		return 1;
//		return level.intValue();
	}
	
	/**
	 * Gets building definition.
	 * 
	 * @return building definition
	 */
	public BuildingDefinition getDefinition() {
		return definition;
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
	 * Gets all building signs signs.
	 * 
	 * @return building signs
	 */
	public ArrayList<BuildingSign> getBuildingSigns() {
		return new ArrayList<BuildingSign>(signs);
	}
	
	/**
	 * Gets the signs.
	 * 
	 * @param signClass sign class
	 * @return the signs
	 */
	public <T extends BuildingSign> ArrayList<T> getBuildingSigns(Class<T> signClass){
		
		
		ArrayList<BuildingSign> allSigns = getBuildingSigns();
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
	 * Refreshes all signs.
	 * 
	 */
	public void refreshSigns() {

		
		ArrayList<BuildingSign> signs = getBuildingSigns();
		
		for (BuildingSign buildingSign : signs) {
			buildingSign.refresh();
		}
		

	}
	
	/**
	 * Removes all signs.
	 * 
	 */
	public void removeSigns() {

		
		ArrayList<BuildingSign> signs = getBuildingSigns();
		
		for (BuildingSign buildingSign : signs) {
			removeBuildingSign(buildingSign);
		}
		

	}
	
	
	
	// Storage areas:
	/**
	 * Gets all storage areas.
	 * 
	 * @return
	 */
	public ArrayList<StorageArea> getStorageAreas() {
		return new ArrayList<StorageArea>(storage);
	}
	
	/**
	 * Adds a storage area.
	 * 
	 * @param storeArea storage area
	 */
	public void addStorageArea(StorageArea storeArea) {

		storage.add(storeArea);
		storeArea.setSize(definition.getStorageSize());
		
	}
	
	/**
	 * Removes a storage area.
	 * 
	 * @param storeArea storage area to remove
	 */
	public void removeStorageArea(StorageArea storeArea) {

		storage.remove(storeArea);

	}
	
	
	/**
	 * Gets a storage area at the given location.
	 * 
	 * @param location location
	 * @return storage area at the given location, null if none
	 */
	public StorageArea getStorageArea(Location location) {

		
		Block block = location.getBlock();
		
		ArrayList<StorageArea> allSorage = getStorageAreas();

		for (StorageArea storageArea : allSorage) {
			
			if(storageArea.checkBelongs(block)) return storageArea;
			
		}
		
		return null;
		
		
	}
	
	/**
	 * Removes a storage area from the given location.
	 * 
	 * @param location location
	 */
	public void removeStorageArea(Location location) {

		Block block = location.getBlock();
		
		ArrayList<StorageArea> allSorage = getStorageAreas();

		for (StorageArea storageArea : allSorage) {
			
			if(storageArea.checkBelongs(block)){
				storage.remove(storageArea);
			}
			
		}
	}
	
	
	/**
	 * Gets the amount of storage areas used.
	 * 
	 * @return amount of storage areas
	 */
	public Integer getUsedStorageAreas() {
		return storage.size();
	}
	
	/**
	 * Gets the amount of storage areas available.
	 * 
	 * @return amount of storage areas available
	 */
	public Integer getAvailableStorageAreas() {
		return getDefinition().getAvailableStorages(getLevel());
	}
	
	/**
	 * Gets the amount of storage areas remaining.
	 * 
	 * @return amount of storage areas remaining
	 */
	public Integer getRemainingStorageAreas() {
		return getAvailableStorageAreas() - getUsedStorageAreas();
	}
	
	
	/**
	 * Checks if the storage area overlaps with existing storage areas.
	 * 
	 * @param otherStoreArea storage area to check
	 * @return true if overlaps with others
	 */
	public boolean checkOverlap(StorageArea otherStoreArea) {

		
		ArrayList<StorageArea> allSorage = getStorageAreas();

		for (StorageArea storageArea : allSorage) {
			
			if(storageArea.checkOverlap(otherStoreArea)) return true;
			
		}
		
		return false;
		
		
	}
	
	/**
	 * Check if the block is a part of a storage area.
	 * 
	 * @param block block
	 * @return true if part of storage area.
	 */
	public boolean checkStorageArea(Block block) {

		
		ArrayList<StorageArea> allSorage = getStorageAreas();

		for (StorageArea storageArea : allSorage) {
			
			if(storageArea.checkBelongs(block)) return true;
			
		}
		
		return false;
		
		
	}

	
	/**
	 * Adds blocks to storage.
	 * 
	 * @param blocks to add
	 * @return blocks to add
	 */
	public ItemStack store(ItemStack toStore) {

		
		ArrayList<StorageArea> allSorage = getStorageAreas();

		// Blocks:
		if(toStore.getType().isBlock()){
			
			for (StorageArea storageArea : allSorage) {
				
				if(toStore.getAmount() == 0) return toStore;
				
				storageArea.storeBlock(toStore);
				
			}
			
		}
		
		// Items:
		else{
			
			for (StorageArea storageArea : allSorage) {
				
				if(toStore.getAmount() == 0) return toStore;
				
				storageArea.storeItem(toStore);
				
			}
			
		}
		
		
		
		return toStore;
		

	}
	
	/**
	 * Withdraws blocks from the storage.
	 * 
	 * @param fromStore withdrawn blocks
	 * @param amount requested amount
	 * @return withdrawn blocks
	 */
	public ItemStack withdraw(ItemStack fromStore, int amount) {

		
		System.out.println("withdrawing " + amount +" " +  fromStore + " from " + getName());
		
		ArrayList<StorageArea> allSorage = getStorageAreas();

		// Blocks:
		if(fromStore.getType().isBlock()){
			
			for (StorageArea storageArea : allSorage) {
				
				if(fromStore.getAmount() >= amount) return fromStore;
				
				storageArea.withdrawBlock(fromStore, amount);
				
			}
			
		}
		
		// Items:
		else{

			for (StorageArea storageArea : allSorage) {
				
				if(fromStore.getAmount() >= amount) return fromStore;
				
				storageArea.withdrawItem(fromStore, amount);
				
			}
			
		}
		
		return fromStore;
		

	}


	/**
	 * Counts the amount of items available.
	 * 
	 * @param item item
	 * @return amount
	 */
	public Integer countStored(ItemStack item) {

		
		int amount = 0;
		
		ArrayList<StorageArea> allSorage = getStorageAreas();

		// Blocks:
		for (StorageArea storageArea : allSorage) {
				
			amount+= storageArea.countStored(item);
				
		}
		
		return amount;
		
		
	}
	
	

	/**
	 * Withdraws blocks from the storage. Also looks in related buildings.
	 * 
	 * @param fromStore withdrawn blocks
	 * @param amount requested amount
	 * @return withdrawn blocks
	 */
	public ItemStack withdrawRelat(ItemStack fromStore, int amount) {


		// Buildings:
		ArrayList<Building> buildings = new ArrayList<Building>();
		ArrayList<String> bldgNames = getDefinition().getRelatedBuildings();
		
		for (String bldgName : bldgNames) {
			buildings.addAll(getChunkGroup().getBuildings(bldgName));
		}
		
		// Withdraw from this building:
		withdraw(fromStore, amount);
		
		// Withdraw from related buildings:
		for (Building building : buildings) {

			System.out.println("RELAT: withdrawing " + amount +" " +  fromStore + " from " + building.getName());
			
			if(fromStore.getAmount() >= amount) return fromStore;
				
			building.withdraw(fromStore, amount);
				
		}
		
		return fromStore;
		
		
	}


	/**
	 * Counts the amount of items available. Also looks in related buildings.
	 * 
	 * @param item item
	 * @return amount
	 */
	public Integer countStoredRelat(ItemStack item) {

		
		int amount = 0;
		
		// Buildings:
		ArrayList<Building> buildings = new ArrayList<Building>();
		ArrayList<String> bldgNames = getDefinition().getRelatedBuildings();
		
		for (String bldgName : bldgNames) {
			buildings.addAll(getChunkGroup().getBuildings(bldgName));
		}
		
		// Count for this building:
		amount+= countStored(item);
		
		// Count for related buildings:
		for (Building building : buildings) {

			amount+= building.countStored(item);
			
		}
		
		return amount;
		
		
	}
	
	
	/**
	 * Handles block withdraw.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 */
	public void handleStore(BlockPlaceEvent event, SagaPlayer sagaPlayer) {

		
		// Inform:
		sagaPlayer.message(BuildingMessages.stored(event.getBlock().getType(), this));
		

	}

	/**
	 * Handles block withdraw.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 */
	public void handleWithdraw(BlockBreakEvent event, SagaPlayer sagaPlayer) {

		
		// Inform:
		sagaPlayer.message(BuildingMessages.withdrew(event.getBlock().getType(), this));
		

	}
	
	/**
	 * Handles item storage open
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 */
	public void handleItemStorageOpen(PlayerInteractEvent event, SagaPlayer sagaPlayer) {
		
		
		// Permission:
		if(!getChunkGroup().hasPermission(sagaPlayer, SettlementPermission.ACCESS_STORAGE)){
			sagaPlayer.message(SagaMessages.noPermission(this));
			event.setCancelled(true);
			event.setUseInteractedBlock(Result.DENY);
			event.setUseItemInHand(Result.DENY);
			return;
		}		
		
		// Inform:
		sagaPlayer.message(BuildingMessages.openedItemStore());
		
		
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

	
	
	// Updates:
	/**
	 * Enables the building
	 * 
	 */
	public void enable() {
		
		Clock.clock().registerDaytimeTick(this);
		
	}
	
	/**
	 * Disables the building.
	 * 
	 */
	public void disable() {

		Clock.clock().unregisterDaytimeTick(this);
		
	}

	/**
	 * Performs buildings operations.
	 * 
	 */
	public void perform() {

	}

	/**
	 * Performs crafting based on recipes.
	 * 
	 */
	private void produce() {

		
		// Chunk not loaded:
		if(!getSagaChunk().isChunkLoaded()) return;
		
		// Recipes:
		RandomRecipe recipes = new RandomRecipe(getDefinition().getRecipes());
		
		// Craft:
		Integer toCraft = getDefinition().getResourceAmount(getLevel());
		
		while(recipes.size() > 0 && toCraft > 0){
			
			// Next recipe:
			RecepieBlueprint recipe = recipes.nextRecipe();
			
			// Check amounts:
			ArrayList<ItemStack> from = recipe.createFrom();
			
			boolean req = true;
			for (ItemStack fromStack : from) {
				
				if(countStoredRelat(fromStack) < fromStack.getAmount()){
					req = false;
					break;
				}
				
			}
			
			// Requirements not met:
			if(!req){
				recipes.remove(recipe);
				continue;
			}
			
			ArrayList<ItemStack> to = recipe.createTo();
			
			// Take:
			for (ItemStack fromStack : from) {
				
				int amount = fromStack.getAmount();
				fromStack.setAmount(0);
				withdrawRelat(fromStack, amount);
				
				if(fromStack.getAmount() != amount) SagaLogger.severe(this, "requested and retrieved amounts dont match for " + fromStack.getType());
				
			}
			
			// Craft:
			for (ItemStack toStack : to) {
				store(toStack);
			}
			
			toCraft--;
			
		}
		
		
	}
	

	
	// Clock:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.DaytimeTicker#daytimeTick(org.saga.Clock.DaytimeTicker.Daytime)
	 */
	@Override
	public final void daytimeTick(Daytime daytime) {

		
		// Perform:
		if(daytime == getDefinition().getPerformTime()) perform();
		
		// Handle crafting:
		if(daytime == getDefinition().getResourceTime()) produce();
		
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.DaytimeTicker#checkWorld(java.lang.String)
	 */
	@Override
	public final boolean checkWorld(String worldName) {
		return getSagaChunk().getWorldName().equals(worldName);
	}


	
	// Interact events:
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

    
    
	// Spawn events:
	/**
	 * Called when a creature spawns on the saga chunk.
	 * 
	 * @param event event
	 * @param locationChunk event location saga chunk
	 */
	public void onCreatureSpawn(CreatureSpawnEvent event, SagaChunk locationChunk) {
		
	}
	
	/**
	 * Member respawn event.
	 * 
	 * @param sagaPlayer saga player
	 * @param event event
	 */
	public void onMemberRespawn(SagaPlayer sagaPlayer, PlayerRespawnEvent event) {

		
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

		
		// Add building build override:
		if(getChunkGroup() != null && !getChunkGroup().hasPermission(event.getSagaPlayer(), SettlementPermission.BUILD_BUILDING)) event.addBuildOverride(BuildOverride.BUILDING_DENY);

		// Storage area:
		Block block = event.getBlock();
		if(block != null && checkStorageArea(block)){
			
			// Storage area deny:
			if(!getChunkGroup().hasPermission(event.getSagaPlayer(), SettlementPermission.ACCESS_STORAGE)){
				event.addBuildOverride(BuildOverride.STORAGE_AREA_DENY);
			}
			
			// Free storage area:
			if(getChunkGroup().isOptionEnabled(ChunkGroupToggleable.OPEN_STORAGE_AREAS)) event.addBuildOverride(BuildOverride.OPEN_STORAGE_AREA_ALLOW);				
			
			
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
			throw new Throwable(BuildingMessages.invalidBuilding(TextUtil.className(buildingClass), args.getCommand()));
		}
		
		// Correct building:
		T selectedBuilding;
		try {
			selectedBuilding = buildingClass.cast(building);
		} catch (ClassCastException e) {
			throw new Throwable(BuildingMessages.invalidBuilding(TextUtil.className(buildingClass), args.getCommand()));
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
