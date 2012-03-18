package org.saga.chunkGroups;


import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.saga.Saga;
import org.saga.SagaMessages;
import org.saga.buildings.Building;
import org.saga.player.SagaEntityDamageManager.SagaPvpEvent;
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
     * Bukkit chunk.
     */
    transient private Chunk bukkitChunk = null;
    
    /**
     * Chunk group.
     */
    transient private ChunkGroup chunkGroup = null;
    
    
    // Initialization:
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
	 * Completes the initialization.
	 * 
	 * @param chunkGroup origin chunk group
	 * @return integrity
	 */
	public boolean complete(ChunkGroup chunkGroup) {

		
		boolean integrity=true;
		if(x == null){
			Saga.severe(this + " saga chunk x field not initialized. Setting default.");
			x= 0;
			integrity = false;
		}
		if(z == null){
			Saga.severe(this + " saga chunk z field not initialized. Setting default.");
			z= 0;
			integrity = false;
		}
		if(world == null){
			Saga.severe(this + " saga chunk world field not initialized. Setting default.");
			world = "nullworld";
			integrity = false;
		}
		
		// Set access for building:
		if(bld != null){
			bld.setSagaChunk(this);
		}
		
		// Check chunk group:
		this.chunkGroup = chunkGroup;
//		if(chunkGroup == null){
//			Saga.severe(this + " saga chunk chunkGroup field not initialized.");
//			integrity = false;
//		}
		
		// Properties:
		
		return integrity;
		
		
	}
	
	/**
	 * Sets the chunk group.
	 * 
	 * @param chunkGroup chunk group
	 */
	void s2etChunkGroup(ChunkGroup chunkGroup) {
		this.chunkGroup = chunkGroup;
	}
	
	
	// Interaction:
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

	/**
	 * Gets chunk group associated with this saga chunk.
	 * 
	 * @return chunk group.
	 */
	public ChunkGroup getChunkGroup() {
		return chunkGroup;
	}
	
	/**
	 * Gets the chunk world.
	 * 
	 * @return chunk world, null if none
	 */
	public World getWorld() {
		

		if(bukkitChunk != null){
			return bukkitChunk.getWorld();
		}
		
		return Saga.plugin().getServer().getWorld(getWorldName());
		
		
	}
	
	/**
	 * Gets a bukkit chunk associated with the saga chunk.
	 * 
	 * @return bukkit chunk, null if not found
	 */
	public Chunk getBukkitChunk() {
		
		
		if(bukkitChunk != null){
			return bukkitChunk;
		}
		
		World world = getWorld();
		
		if(world == null){
			return null;
		}
		
		Chunk bukkitChunk = world.getChunkAt(x, z);
		
		this.bukkitChunk = bukkitChunk;
		
        return bukkitChunk;
        
        
    }
	
	/**
	 * Loads the bukkit chunk.
	 * 
	 */
	public void loadChunk() {

		
		World world = getWorld();
		
		if(world == null){
			Saga.severe(this, "failed to retrieve world", "ignoring bukkit chunk load");
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
			Saga.severe(this, "failed to retrieve world", "returning false for loaded check");
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
	 * Sends a refresh for all entities on the chunk.
	 * 
	 */
	public void refresh() {
		
		
		Entity[] entities = getBukkitChunk().getEntities();
		ArrayList<SagaPlayer> sagaPlayers = new ArrayList<SagaPlayer>();
		for (int i = 0; i < entities.length; i++) {
			if(!(entities[i] instanceof Player)){
				continue;
			}
			SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(((Player) entities[i]).getName());
			if(sagaPlayer != null){
				sagaPlayers.add(sagaPlayer);
			}
		}
		for (SagaPlayer sagaPlayer : sagaPlayers) {
			sagaPlayer.refreshLocation();
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
				
				SagaChunk adjChunk = ChunkGroupManager.manager().getSagaChunk(getWorldName(), x + dx, z + dz);
				
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
			
			SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(player.getName());
			
			if(sagaPlayer == null){
				Saga.severe(this, "failed to fetch saga player for " + player.getName() + ", because the player isnt loaded", "ignoring request");
				continue;
			}
			
			sagaPlayers.add(sagaPlayer);
			
		}
		
		return sagaPlayers;
		
		
	}
	
	
	// Events:
	/**
	 * Called when a block is damaged in the chunk.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 */
	public void onBlockDamage(BlockDamageEvent event, SagaPlayer sagaPlayer) {
		
		
		
		
	}
	
	/**
	 * Called when a sign changes
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 */
	public void onSignChange(SignChangeEvent event, SagaPlayer sagaPlayer) {

		
		// Canceled:
		if(event.isCancelled()){
			return;
		}
		
		// Ask chunk group:
		ChunkGroup chunkGroup = getChunkGroup();
		boolean isCanceled = true;
		if(chunkGroup != null){
			isCanceled = !chunkGroup.canBuild(sagaPlayer);
		}
		event.setCancelled(isCanceled);

		// Inform:
		if(isCanceled){
			sagaPlayer.message(SagaMessages.noPermission());
		}

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
	
    /**
     * Called when a player interacts with something on the chunk.
     * 
     * @param event event
     * @param sagaPlayer saga player
     */
    public void onPlayerInteract(PlayerInteractEvent event, SagaPlayer sagaPlayer) {
    	

		// Forward to chunk group:
		getChunkGroup().onPlayerInteract(event, sagaPlayer, this);
		
		// Forward to building:
		if(bld != null) bld.onPlayerInteract(event, sagaPlayer);
    	
    	
    }
	
	/**
	 * Called when an entity explodes on the chunk
	 * 
	 * @param event event
	 */
	public void onEntityExplode(EntityExplodeEvent event) {

		
		// Forward to chunk group:
		ChunkGroup chunkGroup = getChunkGroup();
		if(chunkGroup == null){
			return;
		}
		chunkGroup.onEntityExplode(event, this);
		
		
	}
	
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
		ChunkGroup chunkGroup = getChunkGroup();
		if(chunkGroup == null){
			return;
		}
		chunkGroup.onCreatureSpawn(event, this);
		
		
	}

	/**
	 * Called when an entity forms blocks.
	 * 
	 * @param event event
	 */
	public void onEntityBlockForm(EntityBlockFormEvent event) {
		
		// Forward to chunk group:
		getChunkGroup().onEntityBlockForm(event, this);
		
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
	public  void onPlayerVersusPlayer(SagaPvpEvent event){
		

		// Forward to chunk group:
		getChunkGroup().onPlayerVersusPlayer(event, this);
		
		// Forward to building:
		if(bld != null) bld.onPlayerVersusPlayer(event);

		
	}
	
	/**
	 * Called when a player is kiled by another player.
	 * 
	 * @param event event
	 * @param damager damager saga player
	 * @param damaged damaged saga player
	 * @param locationChunk chunk where the pvp occured
	 */
	public void onPlayerKillPlayer(SagaPlayer attacker, SagaPlayer defender){
		

		// Forward to chunk group:
		getChunkGroup().onPlayerKillPlayer(attacker, defender, this);
		
		// Forward to building:
		if(bld != null) bld.onPlayerKillPlayer(attacker, defender);

		
	}
	

	/**
	 * Called when a player is damaged by a creature.
	 * 
	 * @param event event
	 * @param damager damager creature
	 * @param damaged damaged saga player
	 */
	public void onPlayerDamagedByCreature(EntityDamageByEntityEvent event, Creature damager, SagaPlayer damaged){

		
		if(event.isCancelled()){
			return;
		}

		// Forward to chunk group:
		getChunkGroup().onPlayerDamagedByCreature(event, damager, damaged, this);
		
		// Forward to building:
		if(bld != null) bld.onPlayerDamagedByCreature(event, damager, damaged);

		
	}
	
	/**
	 * Called when a player is damages a creature.
	 * 
	 * @param event event
	 * @param damager damager saga player
	 * @param damaged damaged creature
	 */
	public void onPlayerDamagedCreature(EntityDamageByEntityEvent event, SagaPlayer damager, Creature damaged){

		
		if(event.isCancelled()){
			return;
		}
//
//		// Forward to chunk group:
//		getChunkGroup().onPlayerDamagedCreature(event, damager, damaged, this);
//		
		// Forward to building:
		if(bld != null) bld.onPlayerDamagedCreature(event, damager, damaged);

		
	}
	
	
	// Block events:
	/**
	 * Called when a block is placed in the chunk.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 */
	public void onBlockPlace(BlockPlaceEvent event, SagaPlayer sagaPlayer) {


		// Canceled:
		if(event.isCancelled()) return;

		// Forward to chunk group:
		getChunkGroup().onBlockPlace(event, sagaPlayer, this);
		
		// Canceled:
		if(event.isCancelled()) return;
		
		// Forward to building:
		if(bld != null) bld.onBlockPlace(event, sagaPlayer);

		
	}
	
	/**
	 * Called when a block is broken in the chunk.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 */
	public void onBlockBreak(BlockBreakEvent event, SagaPlayer sagaPlayer) {



		// Canceled:
		if(event.isCancelled()) return;

		// Forward to chunk group:
		getChunkGroup().onBlockBreak(event, sagaPlayer, this);

		// Canceled:
		if(event.isCancelled()) return;

		// Forward to building:
		if(bld != null){
			
			bld.onBlockBreak(event, sagaPlayer);
			
			// Sign:
			Block targetBlock = event.getBlock();
			if((targetBlock.getState() instanceof Sign)){
						
				bld.handleSignRemove(sagaPlayer, (Sign)targetBlock.getState(), event);
						
			}
			
		}
    	

		
		
	}

	
	// Interact:
	/**
     * Called when a player interacts with an entity on the chunk.
     * 
     * @param event event
     * @param sagaPlayer saga player
     * @param sagaChunk saga chunk
     */
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event, SagaPlayer sagaPlayer) {
    	

		// Canceled:
		if(event.isCancelled()){
			return;
		}

		// Forward to chunk group:
		getChunkGroup().onPlayerInteractEntity(event, sagaPlayer, this);

		// Canceled:
		if(event.isCancelled()) return;

		
    }
	
    
    // Commands:
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
		getChunkGroup().onPlayerCommandPreprocess(sagaPlayer, event, this);


    }
	
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
			return "" + world + " (" + x + ", " + z + ")" + " " + getBuilding();
		}else{
			return  "" + world + " (" + x + ", " + z + ")";
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
			return ChunkGroupManager.manager().getSagaChunk(world, x - 1, z);
		case LEFT:
			return ChunkGroupManager.manager().getSagaChunk(world, x, z + 1);
		case BACK:
			return ChunkGroupManager.manager().getSagaChunk(world, x +1 , z);
		case RIGHT:
			return ChunkGroupManager.manager().getSagaChunk(world, x, z - 1);	
		default:
			return null;
		}
		
		
	}

	/**
	 * Checks if the chunk is a chunk group border.
	 * 
	 * @return true if border
	 */
	public boolean isBorder() {

		
		SagaChunk adjacent = null;
		
		adjacent = getAdjacent(ChunkSide.BACK);
		if( adjacent == null || !adjacent.getChunkGroup().getId().equals(getChunkGroup().getId()) ) return true;
		
		adjacent = getAdjacent(ChunkSide.FRONT);
		if( adjacent == null || !adjacent.getChunkGroup().getId().equals(getChunkGroup().getId()) ) return true;
		
		adjacent = getAdjacent(ChunkSide.LEFT);
		if( adjacent == null || !adjacent.getChunkGroup().getId().equals(getChunkGroup().getId()) ) return true;
		
		adjacent = getAdjacent(ChunkSide.RIGHT);
		if( adjacent == null || !adjacent.getChunkGroup().getId().equals(getChunkGroup().getId()) ) return true;
		
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
