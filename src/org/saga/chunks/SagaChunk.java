package org.saga.chunks;


import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.buildings.Building;
import org.saga.listeners.events.SagaBuildEvent;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.messages.BuildingMessages;
import org.saga.player.SagaPlayer;

public class SagaChunk {

	
	/**
	 * Chunk x.
	 */
	private Integer x;
	
	/**
	 * Chunk z.
	 */
	private Integer z;
    
	/**
	 * World name.
	 */
	private String world;

	/**
	 * Building.
     */
	private Building bld;
	
	
	/**
	 * Bundle.
	 */
	transient private Bundle bundle = null;
	
	
	
	// Initialisation:
	/**
	 * Creates a saga chunk from a bukkit chunk.
	 * 
	 * @param chunk bukkit chunk
	 */
	public SagaChunk(Chunk chunk){
		
		this.world = chunk.getWorld().getName();
		this.x = chunk.getX();
		this.z = chunk.getZ();
		
	}
	
	/**
	 * Creates a saga chunk from a location.
	 * 
	 * @param location location
	 */
	public SagaChunk(Location location){
		
		this(location.getWorld().getChunkAt(location));
		
	}
	
	/**
	 * Completes the initialisation.
	 * 
	 * @param bundle origin chunk group
	 * @return integrity
	 */
	public boolean complete(Bundle bundle) {

		
		boolean integrity=true;
		if(x == null){
			SagaLogger.nullField(this,"x");
			x= 0;
			integrity = false;
		}
		if(z == null){
			SagaLogger.nullField(this, "z");
			z= 0;
			integrity = false;
		}
		if(world == null){
			SagaLogger.nullField(this, "world");
			world = "nullworld";
			integrity = false;
		}
		
		// Set access for building:
		if(bld != null){
			bld.setSagaChunk(this);
		}
		
		// Check chunk group:
		this.bundle = bundle;
		
		// Properties:
		
		return integrity;
		
		
	}
	
	
	
	// Coordinates:
	/**
	 * Gets the x.
	 * 
	 * @return the x
	 */
	public Integer getX() {
		return x;
	}
	
	/**
	 * Gets the z.
	 * 
	 * @return the z
	 */
	public Integer getZ() {
		return z;
	}
	
	/**
	 * Gets the worldName.
	 * 
	 * @return the worldName
	 */
	public String getWorldName() {
		return world;
	}

	
	
	// Bukkit chunk:
	/**
	 * Gets chunk group associated with this saga chunk.
	 * 
	 * @return chunk group.
	 */
	public Bundle getChunkBundle() {
		return bundle;
	}
	
	
	/**
	 * Gets the chunk world.
	 * 
	 * @return chunk world, null if none
	 */
	public World getWorld() {
		
		return Saga.plugin().getServer().getWorld(getWorldName());
		
	}
	
	/**
	 * Gets a bukkit chunk associated with the saga chunk.
	 * 
	 * @return bukkit chunk, null if not loaded
	 */
	public Chunk getBukkitChunk() {
		
		
		World world = getWorld();
		
		if(world == null) return null;
		
		if(!world.isChunkLoaded(x, z)) return null;
		
		return world.getChunkAt(x, z);
		
		
	}
	
	
	/**
	 * Loads the bukkit chunk.
	 * 
	 */
	public void loadChunk() {

		
		World world = getWorld();
		
		if(world == null){
			SagaLogger.severe(this, "failed to retrieve world");
			return;
		}
		
		world.loadChunk(this.x, this.z, false);
		
		
	}
	
	/**
	 * Checks if the chunk is loaded.
	 * 
	 * @return true if loaded
	 */
	public boolean isChunkLoaded() {

		
		World world = getWorld();
		
		if(world == null){
			SagaLogger.severe(this, "failed to retrieve world");
			return false;
		}
		
		return world.isChunkLoaded(this.x, this.z);
		
		
	}
	
	
	/**
	 * Gets the saga chunk location. The location is the highest in the center of the chunk.
	 * 
	 * @return saga chunk location, null if not found
	 */
	public Location getCenterLocation() {
		
		
		World world = getWorld();
		if(world == null) return null;
		
		Double x = 16 * this.x + 7.5;
		Double z = 16 * this.z + 7.5;
		
		return new Location(world, x, world.getHighestBlockYAt(x.intValue(), z.intValue()), z);

		
	}
	
	/**
	 * Gets the top position that is shifted by a certain amount from the center.
	 * 
	 * @param displacement displacement
	 * @return shifted location, null if not found
	 */
	public Location getLocation(Vector displacement) {


		World world = getWorld();
		if(world == null) return null;
		
		Double x = 16 * this.x + 7.5 + displacement.getX();
		Double z = 16 * this.z + 7.5 + displacement.getZ();
		Double dy = displacement.getY();

		return new Location(world, x, world.getHighestBlockYAt(x.intValue(), z.intValue()) + dy, z);
		
		
	}
	
	
	/**
	 * Broadcasts a message to all entities on the chunk.
	 * 
	 * @param message message
	 */
	public void broadcast(String message) {

		Entity[] entities = getBukkitChunk().getEntities();

		for ( int i = 0; i < entities.length; i++ ) {

			if ( entities[i] instanceof Player ) {

				Player player = (Player)entities[i];
				player.sendMessage(message);

			}

		}


	}
	
	
	/**
	 * Checks if the bukkit chunk represents a saga chunk.
	 * 
	 * @param bukkitChunk bukkit chunk
	 * @return true if the bukkit chunk represents the saga chunk
	 */
	public boolean represents(Chunk bukkitChunk) {

		return bukkitChunk.getX() == x && bukkitChunk.getZ() == z && bukkitChunk.getWorld().getName().equals(world);
		
	}
	
	/**
	 * Checks if the location is on the saga chunk.
	 * 
	 * @param loaction location
	 * @return true if the location is on the saga chunk
	 */
	public boolean represents(Location location) {

		return represents(location.getWorld().getChunkAt(location));
		
	}
	
	
	/**
	 * Sends a chunk refresh to all players on the chunk.
	 * 
	 */
	public void refresh() {
		
		
		Entity[] entities = getBukkitChunk().getEntities();
		ArrayList<SagaPlayer> sagaPlayers = new ArrayList<SagaPlayer>();
		
		for (int i = 0; i < entities.length; i++) {

			if(!(entities[i] instanceof Player)) continue;
			
			SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(((Player) entities[i]).getName());
			if(sagaPlayer != null) sagaPlayers.add(sagaPlayer);
			
		}
		
		for (SagaPlayer sagaPlayer : sagaPlayers) {
			sagaPlayer.refreshChunk();
		}
		
		
	}

	
	
	// Building:
	/**
	 * Gets the building on the saga chunk.
	 * 
	 * @return structure, null if none
	 */
	public Building getBuilding() {
		return bld;
	}
	
	/**
	 * Sets the building on the saga chunk.
	 * 
	 * @param building the building on the saga chunk
	 */
	public void setBuilding(Building building) {
		
		if(bld!= null) removeBuilding();
		
		this.bld = building;
		this.bld.setSagaChunk(this);
		getChunkBundle().setBuildingScore(building.getName(), 1);
		this.bld.enable();
		
	}
	
	/**
	 * Removes the building on the saga chunk.
	 * 
	 */
	public void removeBuilding() {
		
		if(bld != null){
			bld.removeSagaChunk();
			bld.disable();
			bld.removeSigns();
		}
		
		this.bld = null;
		
	}

	/**
	 * Clears the building without disabling it on the saga chunk.
	 * 
	 */
	public void clearBuilding() {
		
		if(bld != null){
			bld.removeSagaChunk();
		}
		
		this.bld = null;
		
	}

	
	
	// Adjacent:
	/**
	 * Gets all the adjacent saga chunks with the given radius
	 * 
	 * @param radius radius
	 * @return adjacent saga chunks
	 */
	public ArrayList<SagaChunk> getAdjacent(int radius) {

		
		ArrayList<SagaChunk> adjacent = new ArrayList<SagaChunk>();
		int radiusSquared = radius*radius;
		
		for (int dx = -radius; dx <= radius; dx++) {
			
			for (int dz = -radius; dz <= radius; dz++) {
				
				if((dx*dx + dz*dz) > radiusSquared) continue;
				
				SagaChunk adjChunk = BundleManager.manager().getSagaChunk(getWorldName(), x + dx, z + dz);
				
				if(adjChunk!= null) adjacent.add(adjChunk);
				
			}
			
		}
		
		return adjacent;
		
		
	}
	
	
	
	// Entities:
	/**
	 * Counts players on the chunk.
	 * 
	 * @return player count
	 */
	public int countPlayers() {
		
		
		Chunk bukkitChunk = getBukkitChunk();
		
		if(bukkitChunk == null) return 0;
		
		int count = 0;
		
		Entity[] entities = bukkitChunk.getEntities();
		for (int i = 0; i < entities.length; i++) {
			
			if(entities[i] instanceof Player) count ++;
			
		}
		
		return count;

		
	}
	
	/**
	 * Gets players on the chunk.
	 * 
	 * @return player on the chunk
	 */
	public ArrayList<Player> getPlayers() {
		
		
		ArrayList<Player> players = new ArrayList<Player>();
		
		Chunk bukkitChunk = getBukkitChunk();
		
		if(bukkitChunk == null) return players;
		
		// Add players:
		Entity[] entities = bukkitChunk.getEntities();
		for (int i = 0; i < entities.length; i++) {
			
			if(entities[i] instanceof Player){
				players.add((Player) entities[i]);
			}
			
		}
		
		return players;

		
	}
	
	/**
	 * Gets all saga players on the chunk.
	 * 
	 * @return all saga players on the chunk
	 */
	public ArrayList<SagaPlayer> getSagaPlayers() {

		
		ArrayList<Player> players = getPlayers();
		ArrayList<SagaPlayer> sagaPlayers = new ArrayList<SagaPlayer>();
		
		for (Player player : players) {
			
			SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(player.getName());
			
			if(sagaPlayer == null){
//				SagaLogger.severe(this, "failed to retrieve saga player for " + player.getName() + ", because the player isnt loaded");
				continue;
			}
			
			sagaPlayers.add(sagaPlayer);
			
		}
		
		return sagaPlayers;
		
		
	}
	
	
	
	// Damage events:
	/**
	 * Called when a player is damaged by another player.
	 * 
	 * @param event event
	 */
	public void onEntityDamage(SagaEntityDamageEvent event){
		

		// Forward to chunk group:
		getChunkBundle().onEntityDamage(event, this);
		
		// Forward to building:
		if(bld != null) bld.onEntityDamage(event);

		
	}
	
	/**
	 * Called when a player is killed by another player.
	 * 
	 * @param event event
	 * @param damager damager saga player
	 * @param damaged damaged saga player
	 * @param locationChunk chunk where the pvp occured
	 */
	public void onPvpKill(SagaPlayer attacker, SagaPlayer defender){
		

		// Forward to chunk group:
		getChunkBundle().onPvpKill(attacker, defender, this);
		
		// Forward to building:
		if(bld != null) bld.onPvPKill(attacker, defender);

		
	}
	
	
	// Block events:
	/**
	 * Called when a block is placed in the chunk.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 */
	public void onBlockPlace(BlockPlaceEvent event, SagaPlayer sagaPlayer) {

		if(bld != null){
			
			Block targetBlock = event.getBlock();
			
			// Storage area:
			if(bld.checkStorageArea(targetBlock)){
				bld.handleStore(event, sagaPlayer);
			}
			
		}

	}
	
	/**
	 * Called when a block is broken in the chunk.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 */
	public void onBlockBreak(BlockBreakEvent event, SagaPlayer sagaPlayer) {
		

		if(bld != null){
			
			Block targetBlock = event.getBlock();
			
			// Storage area:
			if(bld.checkStorageArea(targetBlock)){
				bld.handleWithdraw(event, sagaPlayer);
			}
			
			// Sign:
			if((targetBlock.getState() instanceof Sign)){
						
				bld.handleSignRemove(sagaPlayer, (Sign)targetBlock.getState(), event);
						
			}
			
		}

		
	}

	/**
	 * Called when a player builds on the chunk.
	 * 
	 * @param event event
	 */
	public void onBuild(SagaBuildEvent event) {

		
		// Forward to building:
		if(bld != null) bld.onBuild(event);
		
		// Forward to chunk group:
		if(bundle != null) bundle.onBuild(event);
		
		
	}


	// Sign events:
	/**
	 * Called when a sign changes
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 */
	public void onSignChange(SignChangeEvent event, SagaPlayer sagaPlayer) {

		
		// Forward to building:
		Building building = getBuilding();
		if(building != null){
			
			building.onSignChange(event, sagaPlayer);

			// Sign place:
			if((event.getBlock().getState() instanceof Sign)){
				
				Sign sign = (Sign) event.getBlock().getState();
				bld.handleSignPlace(sagaPlayer, sign, event);
				
			}
			
		}
		
		
	}
	
	
	// Interact events::
	/**
	 * Called when a player interacts with something on the chunk.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 */
	public void onPlayerInteract(PlayerInteractEvent event, SagaPlayer sagaPlayer) {
		

		// Forward to chunk group:
		getChunkBundle().onPlayerInteract(event, sagaPlayer, this);
		
		if(bld != null){
			
			// Forward to building:
			bld.onPlayerInteract(event, sagaPlayer);
			
			// Storage area:
			Block block = event.getClickedBlock();
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null && block.getType() == Material.CHEST && bld.checkStorageArea(block)){
				
				bld.handleItemStorageOpen(event, sagaPlayer);
				
			}
			
		}
		
		
	}

	
	// Mob events:
	/**
	 * Called when a creature spawns on the saga chunk.
	 * 
	 * @param event event
	 */
	public void onCreatureSpawn(CreatureSpawnEvent event) {


		// Canceled:
		if(event.isCancelled()){
			return;
		}
		
		// Forward to chunk group:
		Bundle bundle = getChunkBundle();
		if(bundle == null){
			return;
		}
		bundle.onCreatureSpawn(event, this);
		
		
	}

	
	// World change events:
	/**
	 * Called when an entity explodes on the chunk
	 * 
	 * @param event event
	 */
	public void onEntityExplode(EntityExplodeEvent event) {

		
		// Forward to chunk group:
		Bundle bundle = getChunkBundle();
		if(bundle == null){
			return;
		}
		bundle.onEntityExplode(event, this);
		
		
	}

	/**
	 * Called when an entity forms blocks.
	 * 
	 * @param event event
	 */
	public void onEntityBlockForm(EntityBlockFormEvent event) {
		
		// Forward to chunk group:
		getChunkBundle().onEntityBlockForm(event, this);
		
	}
	
	
	// Interact events:
	/**
	 * Called when a player interacts with an entity on the chunk.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 * @param sagaChunk saga chunk
	 */
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event, SagaPlayer sagaPlayer) {
		
	}
	
	
	// Move events:
	/**
	 * Called when a player enters the chunk.
	 * 
	 * @param sagaPlayer saga player
	 * @param last last chunk, null if none
	 */
	public void onPlayerEnter(SagaPlayer sagaPlayer, SagaChunk last) {

		
		Bundle lastChunkBundle = null;
		Bundle thisChunkBundle = getChunkBundle();
		Building lastBuilding = null;
		Building thisBuilding = bld;
		if(last != null){
			lastChunkBundle = last.getChunkBundle();
			lastBuilding = last.bld;
		}
		
		// Forward to chunk group:
		if(lastChunkBundle != thisChunkBundle) getChunkBundle().onPlayerEnter(sagaPlayer, lastChunkBundle);
		
		// Forward to building:
		if(bld != null && lastBuilding != thisBuilding){
			
			bld.onPlayerEnter(sagaPlayer, lastBuilding);
			
			if(lastBuilding == null || !lastBuilding.getName().equalsIgnoreCase(thisBuilding.getName())){
				sagaPlayer.message(BuildingMessages.entered(thisBuilding));
			}
			
		}

		
	}
	
	/**
	 * Called when a player enters the chunk.
	 * 
	 * @param sagaPlayer saga player
	 * @param next next chunk, null if none
	 */
	public void onPlayerLeave(SagaPlayer sagaPlayer, SagaChunk next) {

		
		Bundle nextChunkBundle = null;
		Bundle thisChunkBundle = getChunkBundle();
		Building nextBuilding = null;
		Building thisBuilding = bld;
		if(next != null){
			nextChunkBundle = next.getChunkBundle();
			nextBuilding = next.bld;
		}
		
		// Forward to chunk group:
		if(nextChunkBundle != thisChunkBundle) getChunkBundle().onPlayerLeave(sagaPlayer, nextChunkBundle);
		
		// Forward to building:
		if(bld != null && nextBuilding != thisBuilding){
			
			bld.onPlayerLeave(sagaPlayer, nextBuilding);
			
			if(nextBuilding == null){
				sagaPlayer.message(BuildingMessages.left(thisBuilding));
			}
			
		}

		
	}
	
	
	// World events:
	/**
	 * Called when the chunk loads.
	 */
	public void onChunkLoad() {

		
		// Refresh all signs:
		if(bld != null){
			
			bld.refreshSigns();
			
		}

		
	}

	
	// Command events:
	/**
	 * Called when a player performs a command.
	 * 
	 * @param sagaPlayer saga player
	 * @param event event
	 */
	public void onPlayerCommandPreprocess(SagaPlayer sagaPlayer, PlayerCommandPreprocessEvent event) {

		
		// Canceled:
		if(event.isCancelled()){
			return;
		}

		// Forward to chunk group:
		getChunkBundle().onPlayerCommandPreprocess(sagaPlayer, event, this);


	}
	
	
	
	// Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		
		if(!(obj instanceof SagaChunk)){
			return false;
		}
		
		if(!world.equals(((SagaChunk)obj).world)){
			return false;
		}
		
		if(x != ((SagaChunk)obj).x){
			return false;
		}
		
		if(z != ((SagaChunk)obj).z){
			return false;
		}
		
		return true;
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		
		if(getBuilding() != null){
			return "(" + world + ", " + x + ", " + z + ")" + "_{" + getBuilding().getName() + ", " + getChunkBundle().getName() + "}";
		}else{
			return  "(" + world + ", " + x + ", " + z + ")" + "_{" + getChunkBundle().getName() + "}";
		}
		
		
	}
	
	/**
	 * Gets the adjacent chunk.
	 * 
	 * @param chunkSide chunk side
	 * @return saga chunk. null if not found
	 */
	public SagaChunk getAdjacent(ChunkSide chunkSide) {

		
		switch (chunkSide) {
		case FRONT: 
			return BundleManager.manager().getSagaChunk(world, x - 1, z);
		case LEFT:
			return BundleManager.manager().getSagaChunk(world, x, z + 1);
		case BACK:
			return BundleManager.manager().getSagaChunk(world, x +1 , z);
		case RIGHT:
			return BundleManager.manager().getSagaChunk(world, x, z - 1);	
		default:
			return null;
		}
		
		
	}

	/**
	 * Gets all adjacent Saga chunks.
	 * 
	 * @param bukkitChunk bukkit chunk
	 * @return all adjacent saga chunks
	 */
	public static ArrayList<SagaChunk> getAllAdjacent(Chunk bukkitChunk) {

		
		SagaChunk sagaChunk = null;
		ArrayList<SagaChunk> sagaChunks = new ArrayList<SagaChunk>();
		
		int x = bukkitChunk.getX();
		int z = bukkitChunk.getZ();
		String world = bukkitChunk.getWorld().getName();
		
		// x + 1:
		sagaChunk = BundleManager.manager().getSagaChunk(world, x+1, z-1);
		if(sagaChunk != null) sagaChunks.add(sagaChunk);
		
		sagaChunk = BundleManager.manager().getSagaChunk(world, x+1, z);
		if(sagaChunk != null) sagaChunks.add(sagaChunk);
		
		sagaChunk = BundleManager.manager().getSagaChunk(world, x+1, z+1);
		if(sagaChunk != null) sagaChunks.add(sagaChunk);

		// x:
		sagaChunk = BundleManager.manager().getSagaChunk(world, x, z-1);
		if(sagaChunk != null) sagaChunks.add(sagaChunk);
		
		sagaChunk = BundleManager.manager().getSagaChunk(world, x, z);
		if(sagaChunk != null) sagaChunks.add(sagaChunk);
		
		sagaChunk = BundleManager.manager().getSagaChunk(world, x, z+1);
		if(sagaChunk != null) sagaChunks.add(sagaChunk);
		
		// x - 1:
		sagaChunk = BundleManager.manager().getSagaChunk(world, x-1, z-1);
		if(sagaChunk != null) sagaChunks.add(sagaChunk);
		
		sagaChunk = BundleManager.manager().getSagaChunk(world, x-1, z);
		if(sagaChunk != null) sagaChunks.add(sagaChunk);
		
		sagaChunk = BundleManager.manager().getSagaChunk(world, x-1, z+1);
		if(sagaChunk != null) sagaChunks.add(sagaChunk);
		
		return sagaChunks;
		
		
	}
	
	

	/**
	 * Checks if the chunk is a chunk group border.
	 * 
	 * @return true if border
	 */
	public boolean isBorder() {

		
		SagaChunk adjacent = null;
		
		adjacent = getAdjacent(ChunkSide.BACK);
		if( adjacent == null || !adjacent.getChunkBundle().getId().equals(getChunkBundle().getId()) ) return true;
		
		adjacent = getAdjacent(ChunkSide.FRONT);
		if( adjacent == null || !adjacent.getChunkBundle().getId().equals(getChunkBundle().getId()) ) return true;
		
		adjacent = getAdjacent(ChunkSide.LEFT);
		if( adjacent == null || !adjacent.getChunkBundle().getId().equals(getChunkBundle().getId()) ) return true;
		
		adjacent = getAdjacent(ChunkSide.RIGHT);
		if( adjacent == null || !adjacent.getChunkBundle().getId().equals(getChunkBundle().getId()) ) return true;
		
		return false;
		
		
	}
	
	public enum ChunkSide{
		
		FRONT,
		LEFT,
		BACK,
		RIGHT,
		
	}
	
	public enum ChunkVisual{
		
		
		NORMAL,
		WATER,
		MOUNTAIN;
		
		
		public static ChunkVisual visualise(Chunk chunk){
			
			
			World world = chunk.getWorld();
			
			// Water:
			int seaLevel = world.getSeaLevel();
			if(chunk.getBlock(0, seaLevel, 0).getType() == Material.STATIONARY_WATER &&
					chunk.getBlock(15, seaLevel, 0).getType() == Material.STATIONARY_WATER &&	
					chunk.getBlock(15, seaLevel, 15).getType() == Material.STATIONARY_WATER &&
					chunk.getBlock(0, seaLevel, 15).getType() == Material.STATIONARY_WATER &&
					chunk.getBlock(7, seaLevel, 7).getType() == Material.STATIONARY_WATER
					){
				return WATER;
			}
			
			// Mountain:
			
			return NORMAL;
			
			
		}
		
		
		
		
	}
	

	
	
}
