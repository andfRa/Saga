package org.saga.buildings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
import org.saga.Clock;
import org.saga.Clock.SecondTicker;
import org.saga.SagaLogger;
import org.saga.config.SettlementConfiguration;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.exceptions.InvalidLocationException;
import org.saga.listeners.events.SagaBuildEvent;
import org.saga.listeners.events.SagaBuildEvent.BuildOverride;
import org.saga.messages.BuildingMessages;
import org.saga.player.SagaPlayer;
import org.saga.utility.SagaLocation;


public class CrumbleArena extends Building implements SecondTicker{


	/**
	 * Random.
	 */
	transient public static Random RANDOM = new Random();
	
	
	/**
	 * Top sign.
	 */
	transient public static String TOP_SIGN = "=[TOP]=";
	
	/**
	 * Start sign.
	 */
	transient public static String START_SIGN = "=[START]=";

	/**
	 * Allowed y uncertainty.
	 */
	transient public static Integer ALLOWED_Y_MOD = 2;
	
	/**
	 * Stable block for the arena.
	 */
	transient public static MaterialData STABLE_BLOCK = new MaterialData(Material.SMOOTH_BRICK, (byte)3);

	/**
	 * Damaged block for the arena.
	 */
	transient public static MaterialData DAMAGED_BLOCK = new MaterialData(Material.SMOOTH_BRICK, (byte)2);

	/**
	 * Key for number of hotspots.
	 */
	transient public static String HOTSPOTS_KEY = "hotspots per round";
	
	/**
	 * Damaged blocks per round key.
	 */
	transient public static String DAMAGE_BLOCKS_KEY = "damaged per round";
	
	/**
	 * Round duration key.
	 */
	transient public static String TURN_DURATION_KEY = "round duration";
	
	
	/**
	 * Arena players.
	 */
	private ArrayList<CrumblePlayer> crumblePlayers;
	
	/**
	 * Rounds left.
	 */
	private Integer rounds;

	/**
	 * Seconds remaining for given round.
	 */
	private Integer remaining;

	/**
	 * Countdown.
	 */
	private Integer countdown;
	
	/**
	 * Players.
	 */
	private HashSet<String> players;
	
	/**
	 * Y position for the floor.
	 */
	private Integer y = null;
	
	/**
	 * Location the players get kicked to.
	 */
	private SagaLocation kickLocation = null;
	
	
	
	// Initialisation:
	/**
	 * Creates a building from the definition.
	 * 
	 * @param definition building definition
	 */
	public CrumbleArena(BuildingDefinition definition) {
		
		super(definition);

		crumblePlayers = new ArrayList<CrumbleArena.CrumblePlayer>();
		
		rounds = 0;
		remaining = 0;
		countdown = -1;
		players = new HashSet<String>();
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#completeExtended()
	 */
	@Override
	public boolean complete() throws InvalidBuildingException {
		
		
		super.complete();

		if(crumblePlayers == null){
			crumblePlayers = new ArrayList<CrumbleArena.CrumblePlayer>();
			SagaLogger.nullField(this, "crublePlayers");
		}
		for (CrumblePlayer crumblePlayer : crumblePlayers) {
			crumblePlayer.complete();
		}
		
		if(rounds == null){
			rounds = 0;
			SagaLogger.nullField(this, "rounds");
		}
		
		if(remaining == null){
			remaining = 0;
			SagaLogger.nullField(this, "remaining");
		}
		
		if(countdown == null){
			countdown = 0;
			SagaLogger.nullField(this, "countdown");
		}
		
		if(players == null){
			players = new HashSet<String>();
			SagaLogger.nullField(this, "players");
		}
		
		// Clock:
		if(rounds > 0) Clock.clock().enableSecondTick(this);
		
		if(kickLocation != null)
			try {
				kickLocation.complete();
			}
			catch (InvalidLocationException e) {
				SagaLogger.severe(this, "invalid kick location: " + kickLocation);
			}
		
		return true;
		
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#enable()
	 */
	@Override
	public void enable() {
		
		super.enable();

	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#disable()
	 */
	@Override
	public void disable() {

		super.disable();
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#remove()
	 */
	@Override
	public void remove() {
		removeArena();
	}
	
	
	
	// Win loss:
	/**
	 * Adds a win to a player.
	 * 
	 * @param name player name
	 */
	private void addWin(String name) {


		CrumblePlayer foundPlayer = null;
		
		for (CrumblePlayer crumblePlayer : crumblePlayers) {
		
			if(crumblePlayer.getName().equals(name)){
				foundPlayer = crumblePlayer;
				break;
			}
			
		}
		
		if(foundPlayer == null){
			
			foundPlayer = new CrumblePlayer(name, 0, 0, 0.0);
			crumblePlayers.add(foundPlayer);
			
		}
		
		foundPlayer.increaseWins();
		
		
		
	}
	
	/**
	 * Adds a loss to a player.
	 * 
	 * @param name player name
	 */
	private void addLoss(String name) {


		CrumblePlayer foundPlayer = null;
		
		for (CrumblePlayer crumblePlayer : crumblePlayers) {
		
			if(crumblePlayer.getName().equals(name)){
				foundPlayer = crumblePlayer;
				break;
			}
			
		}
		
		if(foundPlayer == null){
			
			foundPlayer = new CrumblePlayer(name, 0, 0, 0.0);
			crumblePlayers.add(foundPlayer);
			
		}
		
		foundPlayer.increaseLosses();
			
		
	}

	/**
	 * Gets the top arena platers.
	 * 
	 * @param count amount of players
	 * @return top players, empty if none
	 */
	public ArrayList<CrumblePlayer> getTop(Integer count) {


		ArrayList<CrumblePlayer> topPlayers = new ArrayList<CrumbleArena.CrumblePlayer>();
		
		Collections.sort(crumblePlayers);
		
		for (int i = 0; i < crumblePlayers.size() && i < count; i++) {
			topPlayers.add(crumblePlayers.get(i));
		}
		
		return topPlayers;
		
		
	}
	
	
	
	// Game:
	/**
	 * Starts the game.
	 * 
	 */
	private void start() {

		
		if(y == null) return;
		
		rounds = getMaxRounds();
		remaining = 0;
		countdown = 5;
		
		// Add players:
		players = new HashSet<String>();
		ArrayList<SagaPlayer> sagaPlayers = getSagaChunk().getSagaPlayers();
		for (SagaPlayer sagaPlayer : sagaPlayers) {
			players.add(sagaPlayer.getName());
		}
		
		// Create arena:
		ArrayList<Block> blocks = getAllArenaBlocks();
		createArena(blocks);
		
		// Start clock:
		Clock.clock().enableSecondTick(this);

		
	}
	
	/**
	 * Starts the game.
	 * 
	 */
	private void end() {

		
		// Winners:
		ArrayList<SagaPlayer> sagaPlayers = getSagaChunk().getSagaPlayers();
		for (SagaPlayer sagaPlayer : sagaPlayers) {
			
			String name = sagaPlayer.getName();
			if(players.contains(name)){
				
				// Add win:
				addWin(name);
				
				// Inform:
				sagaPlayer.message(BuildingMessages.crumbleSurvived(this));
				
			}
			
		}
		players.clear();
		
		// Fix arena:
		createArena();
		
	}
	
	/**
	 * Checks if the game is running.
	 * 
	 * @return true if running
	 */
	private boolean isGameRunning() {
		return rounds > 0;
	}
	
	/**
	 * Gets the maximum amount of rounds.
	 * 
	 * @return maximum rounds
	 */
	private Integer getMaxRounds() {
		return getDefinition().getFunction(DAMAGE_BLOCKS_KEY).getXMax();
	}
	
	/**
	 * Gets the round duration in seconds.
	 * 
	 * @return round duration
	 */
	private Integer getRoundDuration() {
		return getDefinition().getFunction(TURN_DURATION_KEY).intValue(getMaxRounds()-rounds);
	}
	
	/**
	 * Handles player kicking.
	 * 
	 */
	private void handleKickPlayers() {

		
		ArrayList<SagaPlayer> sagaPlayers = getSagaChunk().getSagaPlayers();
		if(kickLocation != null){
			
			Iterator<SagaPlayer> it = sagaPlayers.iterator();
			while(it.hasNext())	{
				
				SagaPlayer sagaPlayer = it.next();
				
				if(!players.contains(sagaPlayer.getName()) && !sagaPlayer.isAdminMode()){
					sagaPlayer.teleport(kickLocation.getLocation());
					sagaPlayer.message(BuildingMessages.crumbleCantEnterGame());
					it.remove();
					
				}
				
			}
			
		}
		
		// Disqualify:
		for (SagaPlayer sagaPlayer : sagaPlayers) {
			
			// Not a player:
			if(!players.contains(sagaPlayer.getName())) continue;
			
			// Losers:
			if(Math.abs(sagaPlayer.getLocation().getY() - (y + 1.0)) > ALLOWED_Y_MOD){
				
				// Remove player:
				players.remove(sagaPlayer.getName());

				// Add loss:
				addLoss(sagaPlayer.getName());
				
				// Inform:
				sagaPlayer.message(BuildingMessages.crumbleLost(this));
				
				// Kick:
				if(kickLocation != null) sagaPlayer.teleport(kickLocation.getLocation());
				
			}
			
		}
		
		
	}
	
	
	
	// Blocks:
	/**
	 * Gets the all arena blocks.
	 * 
	 * @return all arena blocks
	 * 
	 */
	private ArrayList<Block> getAllArenaBlocks() {

		
		ArrayList<Block> blocks = new ArrayList<Block>();
		if(y == null) return blocks;
		
		Chunk chunk = getSagaChunk().getBukkitChunk();
		if(chunk == null) return blocks;
		
		for (int Dx = 0; Dx <= 15; Dx++) {
			for (int Dz = 0; Dz <= 15; Dz++) {
				
				// Only arena block:
				Block block = chunk.getBlock(Dx, y, Dz);
				if(block.getType() != Material.AIR && (block.getType() != STABLE_BLOCK.getItemType() && block.getData() != STABLE_BLOCK.getData() || block.getType() != DAMAGED_BLOCK.getItemType() && block.getData() != DAMAGED_BLOCK.getData())) continue;
				
				// Add:
				blocks.add(block);
				
			}
		}
		
		return blocks;
		
		
	}
	
	/**
	 * Damages a block.
	 * 
	 * @param block block
	 */
	private void damageBlock(Block block) {

		if(block.getType() == STABLE_BLOCK.getItemType() && block.getData() == STABLE_BLOCK.getData()){
			block.setTypeIdAndData(DAMAGED_BLOCK.getItemTypeId(), DAMAGED_BLOCK.getData(), false);
		}
		else if(block.getType() == DAMAGED_BLOCK.getItemType() && block.getData() == DAMAGED_BLOCK.getData()){
			block.setTypeIdAndData(Material.AIR.getId(), (byte)0, false);
		}
		
	}
	
	/**
	 * Filters the stable blocks.
	 * 
	 * @param blocks arena blocks
	 * @return stable blocks from arena blocks
	 */
	private ArrayList<Block> filterStableBlocks(ArrayList<Block> blocks) {

		
		ArrayList<Block> filtered = new ArrayList<Block>();
		for (Block block : blocks) {
			if(block.getType() == STABLE_BLOCK.getItemType() && block.getData() == STABLE_BLOCK.getData()) filtered.add(block);
		}
		
		return filtered;
		
		
	}
	
	/**
	 * Filters the stable blocks.
	 * 
	 * @param blocks arena blocks
	 * @return stable blocks from arena blocks
	 */
	private ArrayList<Block> filterStableAvailableBlocks(ArrayList<Block> blocks) {

		
		ArrayList<Block> filtered = new ArrayList<Block>();
		for (Block block : blocks) {
			
			if(block.getType() != STABLE_BLOCK.getItemType() && block.getData() != STABLE_BLOCK.getData()) continue;
			
			if(	getAdjacent(block,BlockFace.NORTH).getType() != Material.AIR &&
//				getAdjacent(block,BlockFace.NORTH_EAST).getType() != Material.AIR &&
				getAdjacent(block,BlockFace.EAST).getType() != Material.AIR &&
//				getAdjacent(block,BlockFace.SOUTH_EAST).getType() != Material.AIR &&
				getAdjacent(block,BlockFace.SOUTH).getType() != Material.AIR &&
//				getAdjacent(block,BlockFace.SOUTH_WEST).getType() != Material.AIR &&
				getAdjacent(block,BlockFace.WEST).getType() != Material.AIR
//				getAdjacent(block,BlockFace.NORTH_WEST).getType() != Material.AIR
			) continue;
			
			filtered.add(block);
			
		}
		
		return filtered;
		
		
	}

	/**
	 * Gets the adjacent block on the given chunk.
	 * 
	 * @param anchor anchor block
	 * @param face block face
	 * @return adjacent block
	 */
	private static Block getAdjacent(Block anchor, BlockFace face) {

		
		Chunk anchorChunk = anchor.getChunk();
		Block relative = anchor.getRelative(face);
		Chunk relativeChunk = relative.getChunk();
		
		if(!relativeChunk.equals(anchorChunk)){
			
			switch (face) {
				case NORTH:
					
					return anchor.getRelative(0, 0, 15);
					
//				case NORTH_EAST:
//					
//					return anchor.getRelative(0, 0, 0);
//					
				case EAST:
					
					return anchor.getRelative(-15, 0, 0);
					
//				case SOUTH_EAST:
//					
//					return anchor.getRelative(0, 0, 0);
//					
				case SOUTH:
					
					return anchor.getRelative(0, 0, -15);
					
//				case SOUTH_WEST:
//					
//					return anchor.getRelative(10, 0, -10);
//					
				case WEST:
					
					return anchor.getRelative(15, 0, 0);
					
//				case NORTH_WEST:
//					
//					return anchor.getRelative(10, 0, 10);
					
				default:
					return anchor;
					
			}
			
		}
		
		return anchor.getRelative(face);
		
		
	}
	
	/**
	 * Filters the damaged blocks.
	 * 
	 * @param blocks arena blocks
	 * @return damaged blocks from arena blocks
	 */
	private ArrayList<Block> filterDamagedBlocks(ArrayList<Block> blocks) {

		
		ArrayList<Block> filtered = new ArrayList<Block>();
		for (Block block : blocks) {
			if(block.getType() == DAMAGED_BLOCK.getItemType() && block.getData() == DAMAGED_BLOCK.getData()) filtered.add(block);
		}
		
		return filtered;
		
		
	}

	/**
	 * Creates hotspots by removing stable blocks.
	 * 
	 * @param blocks arena blocks
	 */
	private void createHotsports(ArrayList<Block> blocks) {

		
		Integer hotspots = getDefinition().getFunction(HOTSPOTS_KEY).intValue(getMaxRounds() - rounds);
		
		// Hotspot players:
		ArrayList<SagaPlayer> sagaPlayers = getSagaChunk().getSagaPlayers();
		while(hotspots > 0 && sagaPlayers.size() > 0){
			
			
			int r = RANDOM.nextInt(sagaPlayers.size());
			SagaPlayer sagaPlayer = sagaPlayers.get(r);
			sagaPlayers.remove(r);
			
			if(!players.contains(sagaPlayer.getName())) continue;
			
			damageAreaBelow(sagaPlayer.getLocation());
			
			hotspots--;
			
		}
		
		// Hotspot random:
		ArrayList<Block> filtered = filterStableBlocks(blocks);
		while(hotspots > 0 && filtered.size() > 0){
			
			int r = RANDOM.nextInt(filtered.size());
			Location loc = filtered.get(r).getLocation();
			
			damageAreaBelow(loc);
			
			filtered.remove(r);
			hotspots--;
			
		}
		
		
	}
	
	/**
	 * Damages blocks below the location.
	 * 
	 * @param location location
	 */
	private void damageAreaBelow(Location location) {

		
		Location arenaLoc = location;
		arenaLoc.setY(y);
		
		Block anchor = arenaLoc.getBlock();
		
		Block relative = anchor.getRelative(BlockFace.SELF);
		if(relative.getType() != Material.AIR) damageBlock(relative); 

		relative = anchor.getRelative(BlockFace.NORTH);
		if(relative.getType() != Material.AIR) damageBlock(relative); 

		relative = anchor.getRelative(BlockFace.EAST);
		if(relative.getType() != Material.AIR) damageBlock(relative); 

		relative = anchor.getRelative(BlockFace.SOUTH);
		if(relative.getType() != Material.AIR) damageBlock(relative); 

		relative = anchor.getRelative(BlockFace.WEST);
		if(relative.getType() != Material.AIR) damageBlock(relative); 


		relative = anchor.getRelative(BlockFace.NORTH_EAST);
		if(relative.getType() != Material.AIR) damageBlock(relative); 

		relative = anchor.getRelative(BlockFace.NORTH_WEST);
		if(relative.getType() != Material.AIR) damageBlock(relative); 

		relative = anchor.getRelative(BlockFace.SOUTH_EAST);
		if(relative.getType() != Material.AIR) damageBlock(relative); 

		relative = anchor.getRelative(BlockFace.SOUTH_WEST);
		if(relative.getType() != Material.AIR) damageBlock(relative); 

		// Effect:
		location.getWorld().playEffect(anchor.getLocation().add(0.5,0.5,0.5), Effect.STEP_SOUND, STABLE_BLOCK.getItemTypeId());
		
		
	}
	
	/**
	 * Sets up the blocks for the arena.
	 * 
	 * @param arenaBlocks arena blocks
	 */
	private void createArena(ArrayList<Block> arenaBlocks) {

		if(!getSagaChunk().isChunkLoaded()) getSagaChunk().loadChunk();
		
		for (Block block : arenaBlocks) {
			if(block.getType() != Material.AIR && block.getType() != DAMAGED_BLOCK.getItemType() && block.getData() != DAMAGED_BLOCK.getData()) continue;
			block.setTypeIdAndData(STABLE_BLOCK.getItemTypeId(), STABLE_BLOCK.getData(), false);
		}
		
	}
	
	/**
	 * Creates the arena.
	 * 
	 */
	public void createArena() {
		createArena(getAllArenaBlocks());
	}
	
	/**
	 * Removes the arena.
	 * 
	 */
	public void removeArena() {
		
		if(!getSagaChunk().isChunkLoaded()) getSagaChunk().loadChunk();
		
		ArrayList<Block> blocks = getAllArenaBlocks();
		for (Block block : blocks) {
			block.setType(Material.AIR);
		}
		
	}
	
	
	
	// Configuration:
	/**
	 * Sets arena height.
	 * 
	 * @param y arena height
	 */
	public void setY(Integer y) {
		
		if(y != null) removeArena();
		this.y = y;
		createArena();
		
	}
	
	/**
	 * Sets kick location.
	 * 
	 * @param loc arena kick location
	 */
	public void setKickLocation(SagaLocation loc) {
		this.kickLocation = loc;
	}
	
	
	
	// Time:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.SecondTicker#clockSecondTick()
	 */
	@Override
	public boolean clockSecondTick() {
		

		if(!isEnabled()) return false;
		
		// Kick out:
		handleKickPlayers();
		
		// Countdown:
		if(countdown > 0){
			getSagaChunk().broadcast(BuildingMessages.countdown(this, countdown));
			countdown--;
			return true;
		}else if(countdown == 0){
			getSagaChunk().broadcast(BuildingMessages.countdown(this, countdown));
			countdown--;
		}
		
		// Rounds:
		remaining--;
		if(remaining <= 0) remaining = getRoundDuration();
		else return rounds > 0;
		rounds--;
		
		// Blocks:
		ArrayList<Block> blocks = getAllArenaBlocks();
		Integer damage = getDefinition().getFunction(DAMAGE_BLOCKS_KEY).intValue(getMaxRounds() - rounds);
		
		// Damage damaged:
		ArrayList<Block> damaged = filterDamagedBlocks(blocks);
		while(damaged.size() > 0 && damage > 0){
			
			int r = RANDOM.nextInt(damaged.size());
			damageBlock(damaged.get(r));
			damaged.remove(r);
			damage--;
			
		}
		
		// Damage stable:
		ArrayList<Block> stable = filterStableAvailableBlocks(blocks);
		while(stable.size() > 0 && damage > 0){
			
			int r = RANDOM.nextInt(stable.size());
			damageBlock(stable.get(r));
			stable.remove(r);
			damage--;
			
		}
		
		// End:
		if(rounds <= 0){
			end();
			return false;
		}
		
		// New hotspots:
		createHotsports(blocks);
		
		return true;
		
		
	}

	
	
	// Events:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onSignChange(org.bukkit.event.block.SignChangeEvent, org.saga.SagaPlayer)
	 */
	@Override
	public void onSignChange(SignChangeEvent event, SagaPlayer sagaPlayer) {
		

		// Cancelled:
		if(event.isCancelled()){
			return;
		}
		
		// Top sign:
		if(event.getLine(0).equalsIgnoreCase(TOP_SIGN)){
			event.setLine(0, SettlementConfiguration.config().enabledSignColor + TOP_SIGN);
		}
		
		// Start sign:
		if(event.getLine(0).equalsIgnoreCase(START_SIGN)){
			event.setLine(0, SettlementConfiguration.config().enabledSignColor + START_SIGN);
		}
		
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent, org.saga.SagaPlayer)
	 */
	@Override
	public void onPlayerInteract(PlayerInteractEvent event, SagaPlayer sagaPlayer) {
		

		// Cancelled:
		if(event.isCancelled()){
			return;
		}
		
		Block targetBlock = event.getClickedBlock();
    	
		// Right click:
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			return;
		}
		
		// Invalid:
		if(targetBlock == null){
    		return;
    	}
    	
		// Sign:
		if(!(targetBlock.getState() instanceof Sign)){
			return;
		}
		Sign sign = (Sign) targetBlock.getState();

		// Top sign:
		if(ChatColor.stripColor(sign.getLine(0)).equals(TOP_SIGN)){
			
			Integer display = 10;
			if(sign.getLine(1).length() > 0){
				try {
					display = Integer.parseInt(sign.getLine(1));
				}
				catch (NumberFormatException e) {}
			}
			if(display < 1) display = 1;
			if(display > 100) display = 100;
			
			sagaPlayer.message(BuildingMessages.arenaTop(this, display));
			
			// Take control:
			event.setUseInteractedBlock(Result.DENY);
			event.setUseItemInHand(Result.DENY);
			
		}
		// Start sign:
		else if(ChatColor.stripColor(sign.getLine(0)).equals(START_SIGN)){
			
			// Already running:
			if(isGameRunning()){
				sagaPlayer.message(BuildingMessages.crumbleGameRunning());
				return;
			}
			
			// Height not set:
			if(y == null){
				sagaPlayer.message(BuildingMessages.crumbleHeightNotSet(this));
				sagaPlayer.message(BuildingMessages.crumbleHeightNotSetInfo(this));
				return;
			}
			
			start();
			
		}
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onBuild(org.saga.listeners.events.SagaBuildEvent)
	 */
	@Override
	public void onBuild(SagaBuildEvent event) {
		
		// No building in arena:
		if(event.getBlock() != null && event.getBlock().getY() == y && !event.getSagaPlayer().isAdminMode()){
			event.addBuildOverride(BuildOverride.CRUMBLE_ARENA_DENY);
		}
		
	}
	
	
	
	// Types:
	/**
	 * Win/lose specific information.
	 * 
	 * @author andf
	 *
	 */
	public static class CrumblePlayer implements Comparable<CrumblePlayer>{
		
		
		/**
		 * Player name.
		 */
		private String name;
		
		/**
		 * Wins.
		 */
		private Integer wins;
		
		/**
		 * Losss.
		 */
		private Integer losses;

		/**
		 * Points.
		 */
		private Double points;
		
		
		/**
		 * Creates an arena player.
		 * 
		 * @param name name
		 * @param wins wins
		 * @param losses losses
		 * @param points points
		 */
		public CrumblePlayer(String name, Integer wins, Integer losses, Double points) {

			this.name = name;
			this.wins = wins;
			this.losses = losses;
			this.points = points;
			
		}
		
		/* 
		 * (non-Javadoc)
		 * 
		 * @see org.saga.buildings.Building#complete()
		 */
		public boolean complete() {
			

			boolean integrity = true;
			
			if(name == null){
				name = "none";
				SagaLogger.nullField(this, "name");
				integrity = false;
			}
			
			if(wins == null){
				wins = 0;
				SagaLogger.nullField(this, "wins");
				integrity = false;
			}
			
			if(losses == null){
				losses = 0;
				SagaLogger.nullField(this, "losses");
				integrity = false;
			}
			
			if(points == null){
				points = 0.0;
				SagaLogger.nullField(this, "points");
				integrity = false;
			}

			return integrity;
			
			
		}
		
		/**
		 * Calculates the score.
		 * 
		 * @return score
		 */
		public Double getScore() {
			
			return 0.0;
			
		}
		
		/**
		 * Gets name.
		 * 
		 * @return name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Gets wins.
		 * 
		 * @return wins
		 */
		public Integer getWins() {
			return wins;
		}

		/**
		 * Increases wins.
		 * 
		 */
		public void increaseWins() {
			wins++;
		}

		/**
		 * Increases points.
		 * 
		 */
		public void increasePoints(Double points) {
			
			if(points < 1) points = 1.0;
			
			this.points += points;
			
		}

		/**
		 * Gets the losses.
		 * 
		 * @return the losses
		 */
		public Integer getLosses() {
			return losses;
		}

		/**
		 * Increases the losses.
		 * 
		 */
		public void increaseLosses() {
			losses++;
		}

		
		// Other:
		/* 
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(CrumblePlayer o) {
			
			return new Double((o.getWins() - o.getLosses()) - (getWins() - getLosses())).intValue();
			
		}
		
		
		
	}
	
	
}
