package org.saga.buildings;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.Clock;
import org.saga.Clock.SecondTicker;
import org.saga.Clock.TimeOfDayTicker;
import org.saga.Saga;
import org.saga.chunkGroups.ChunkGroupManager;
import org.saga.chunkGroups.ChunkGroupMessages;
import org.saga.chunkGroups.SagaChunk;
import org.saga.config.ChunkGroupConfiguration;
import org.saga.player.SagaEntityDamageManager.SagaPvpEvent;
import org.saga.player.SagaPlayer;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;


public class Arena extends Building implements TimeOfDayTicker, SecondTicker{


	/**
	 * Top sign.
	 */
	transient public static String TOP_SIGN = "=[top]=";
	
	/**
	 * Count down sign.
	 */
	transient public static String COUNTDOWN_SIGN = "=[count]=";
	
	/**
	 * Arena players.
	 */
	private ArrayList<ArenaPlayer> arenaPlayers;
	
	/**
	 * Count down count.
	 */
	transient private Integer count;
	
	
	// Initialization:
	/**
	 * Initializes
	 * 
	 * @param pointCost point cost
	 * @param moneyCost money cost
	 * @param proficiencies proficiencies
	 */
	private Arena(String name) {
		
		super("");
		arenaPlayers = new ArrayList<Arena.ArenaPlayer>();
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#completeExtended()
	 */
	@Override
	public boolean completeExtended() {
		

		boolean integrity = true;
		
		if(arenaPlayers == null){
			arenaPlayers = new ArrayList<Arena.ArenaPlayer>();
			Saga.severe(this, "failed to initialize arenaPlayers field", "setting default");
			integrity = false;
		}
		for (ArenaPlayer arenaPlayer : arenaPlayers) {
			integrity = arenaPlayer.complete() && integrity;
		}
		
		// Transient:
		count = 0;
		
		return integrity;
		
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#duplicate()
	 */
	@Override
	public Building blueprint() {
		
		return new Arena("");
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#enable()
	 */
	@Override
	public void enable() {
		
		super.enable();

		// Register clock:
		Clock.clock().registerTimeOfDayTick(this);
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#disable()
	 */
	@Override
	public void disable() {

		super.disable();

		// Disable time of day clock:
		Clock.clock().unregisterTimeOfDayTick(this);

		// Disable second clock:
		if(count > 0){
			Clock.clock().unregisterSecondTick(this);
		}
		
		
	}

	
	// Kill death:
	/**
	 * Adds a kill to a player.
	 * 
	 * @param name player name
	 */
	private void addKill(String name) {


		ArenaPlayer foundPlayer = null;
		
		for (ArenaPlayer arenaPlayer : arenaPlayers) {
		
			if(arenaPlayer.getName().equals(name)){
				
				foundPlayer = arenaPlayer;
				
				arenaPlayers.remove(foundPlayer);
				
				break;
				
			}
			
		}
		
		if(foundPlayer == null){
			foundPlayer = new ArenaPlayer(name, 0, 0);
		}
		
		foundPlayer.increaseKills();
		
		arenaPlayers.add(foundPlayer);
		
		
	}
	
	/**
	 * Adds a death to a player.
	 * 
	 * @param name player name
	 */
	private void addDeath(String name) {


		ArenaPlayer foundPlayer = null;
		
		for (ArenaPlayer arenaPlayer : arenaPlayers) {
		
			if(arenaPlayer.getName().equals(name)){
				
				foundPlayer = arenaPlayer;
				
				foundPlayer.increaseDeaths();
				
			}
			
		}
		
		if(foundPlayer == null){
			
			foundPlayer = new ArenaPlayer(name, 0, 0);
			foundPlayer.increaseDeaths();
			arenaPlayers.add(foundPlayer);
			
		}
		
		
		
		
		
	}

	/**
	 * Gets the top arena platers.
	 * 
	 * @param count amount of players
	 * @return top players, empty if none
	 */
	public ArrayList<ArenaPlayer> getTop(Integer count) {

		
		ArrayList<ArenaPlayer> topPlayers = new ArrayList<Arena.ArenaPlayer>();
		
		Collections.sort(arenaPlayers);
		
		for (int i = 0; i < arenaPlayers.size() && i < count; i++) {
			topPlayers.add(arenaPlayers.get(i));
		}
		
		return topPlayers;
		
		
	}
	
	
	// Count down:
	/**
	 * Initiates or refreshes count down.
	 * 
	 * @param count count
	 */
	private void intiateCountdown(int count) {

		// Enable clock if not enabled:
		if(this.count <= 0){
			Clock.clock().registerSecondTick(this);
		}
		
		this.count = count;
		
	}
	
	
	// Time:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.TimeOfDayTicker#timeOfDayTick(org.saga.Clock.TimeOfDayTicker.TimeOfDay)
	 */
	@Override
	public void timeOfDayTick(TimeOfDay timeOfDay) {

		
		if(!isEnabled()){
			return;
		}

		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.TimeOfDayTicker#checkWorld(java.lang.String)
	 */
	@Override
	public boolean checkWorld(String worldName) {

		
		SagaChunk sagaChunk = getOriginChunk();		
		if(sagaChunk == null){
			return false;
		}
		
		return sagaChunk.getWorldName().equals(worldName);
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.SecondTicker#clockSecondTick()
	 */
	@Override
	public void clockSecondTick() {
		
		
		// Disable clock:
		if(count <= 0){
			Clock.clock().unregisterSecondTick(this);
		}
		
		// Inform:
		SagaChunk sagaChunk = getOriginChunk();
		if(sagaChunk != null) sagaChunk.broadcast(countdown(count));
		
		count--;
		
	}

	
	
	// Events:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent, org.saga.SagaPlayer)
	 */
	@Override
	public void onPlayerInteract(PlayerInteractEvent event, SagaPlayer sagaPlayer) {
		

		// Canceled:
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
		if(sign.getLine(0).equals(ChunkGroupConfiguration.config().arenaTopSign)){
			
			sagaPlayer.message(BuildingMessages.arenaTop(this, 10));
			
			// Take control:
			event.setUseInteractedBlock(Result.DENY);
			event.setUseItemInHand(Result.DENY);
			
		}
		// Count down sign:
		else if(sign.getLine(0).equals(ChunkGroupConfiguration.config().arenaCountdownSign)){
			
			// Count:
			Integer count = 3;
			
			if(sign.getLine(1).length() > 0){
				try {
					count = Integer.parseInt(sign.getLine(1));
				} catch (NumberFormatException e) {
					count = 3;
				}
			}
			
			// Initiate:
			intiateCountdown(count);

			// Take control:
			event.setUseInteractedBlock(Result.DENY);
			event.setUseItemInHand(Result.DENY);
			
		}
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onSignChange(org.bukkit.event.block.SignChangeEvent, org.saga.SagaPlayer)
	 */
	@Override
	public void onSignChange(SignChangeEvent event, SagaPlayer sagaPlayer) {
		

		// Canceled:
		if(event.isCancelled()){
			return;
		}
		
//		// Permission:
//		if(!canInteract(sagaPlayer, BuildingPermission.LOW)){
//			sagaPlayer.sendMessage(ChunkGroupMessages.noPermission(this));
//			event.setCancelled(true);
//			return;
//		}
		
		// Top sign:
		if(event.getLine(0).equalsIgnoreCase(TOP_SIGN)){
			event.setLine(0, ChunkGroupConfiguration.config().arenaTopSign);
		}
		
		// Count down sign:
		if(event.getLine(0).equalsIgnoreCase(COUNTDOWN_SIGN)){
			event.setLine(0, ChunkGroupConfiguration.config().arenaCountdownSign);
		}
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onBlockPlace(org.bukkit.event.block.BlockPlaceEvent, org.saga.SagaPlayer)
	 */
	@Override
	public void onBlockPlace(BlockPlaceEvent event, SagaPlayer sagaPlayer) {
		

		// Canceled:
		if(event.isCancelled()){
			return;
		}
		
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerDamagedByPlayer(org.bukkit.event.entity.EntityDamageByEntityEvent, org.saga.SagaPlayer, org.saga.SagaPlayer)
	 */
	@Override
	public void onPlayerVersusPlayer(SagaPvpEvent event){
		
		
		// Attack from outside of the arena:
		SagaChunk attackerChunk = ChunkGroupManager.manager().getSagaChunk(event.getSagaAttacker().getLocation());
		SagaChunk defenderChunk = ChunkGroupManager.manager().getSagaChunk(event.getSagaDefender().getLocation());
		if(attackerChunk != getOriginChunk() || defenderChunk != getOriginChunk()){
			return;
		}
		
		// Force allow:
		event.forceAllow();
		
		
	}

	
	@Override
	public void onPlayerKillPlayer(SagaPlayer attacker, SagaPlayer defender) {

		
		// Add kills:
		addDeath(defender.getName());
		addKill(attacker.getName());
	
	
	}
	
	// Messages:
	public static String countdown(int count) {
		
		if(count == 0){
			return ChunkGroupMessages.positive + "Fight!";
		}else if((count%2)==0){
			return ChunkGroupMessages.normal1 + "" + count + ".";
		}else{
			return ChunkGroupMessages.normal2 + "" + count + ".";
		}
		
	}
	
	
	// Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#toString()
	 */
	@Override
	public String toString() {
		return super.toString();
	}

	
	// Commands:

	
	// Types:
	
	
	/**
	 * Death kill specific information.
	 * 
	 * @author andf
	 *
	 */
	public static class ArenaPlayer implements Comparable<ArenaPlayer>{
		
		
		/**
		 * Player name.
		 */
		private String name;
		
		/**
		 * Kills.
		 */
		private Integer kills;
		
		/**
		 * Deaths.
		 */
		private Integer deaths;
		
		
		/**
		 * Creates an arena player.
		 * 
		 * @param name name
		 * @param kills kills
		 * @param deaths deaths
		 */
		public ArenaPlayer(String name, Integer kills, Integer deaths) {

			this.name = name;
			this.kills = kills;
			this.deaths = deaths;
			
		}
		
		/* 
		 * (non-Javadoc)
		 * 
		 * @see org.saga.buildings.Building#completeExtended()
		 */
		public boolean complete() {
			

			boolean integrity = true;
			
			if(name == null){
				name = "none";
				Saga.severe(this.getClass(), "failed to initialize name field", "setting default");
				integrity = false;
			}
			
			if(kills == null){
				kills = 0;
				Saga.severe(this.getClass(), "failed to initialize kills field", "setting default");
				integrity = false;
			}
			
			if(deaths == null){
				deaths = 0;
				Saga.severe(this.getClass(), "failed to initialize deaths field", "setting default");
				integrity = false;
			}

			return integrity;
			
			
		}
		
		/**
		 * Calculates the score.
		 * 
		 * @return score
		 */
		public Double calculateScore() {
			return 2 * kills.doubleValue() - deaths;
		}
		
		/**
		 * Gets the name.
		 * 
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Gets the kills.
		 * 
		 * @return the kills
		 */
		public Integer getKills() {
			return kills;
		}

		/**
		 * Increases the kills.
		 * 
		 */
		public void increaseKills() {
			kills++;
		}

		/**
		 * Gets the deaths.
		 * 
		 * @return the deaths
		 */
		public Integer getDeaths() {
			return deaths;
		}

		/**
		 * Increases the deaths.
		 * 
		 */
		public void increaseDeaths() {
			deaths++;
		}

		
		// Other:
		/* 
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(ArenaPlayer o) {
			
			return new Double(o.calculateScore() - calculateScore()).intValue();
			
		}
		
		
		
	}
	
	

	// Commands:
	@Command(
			aliases = {"btop"},
			usage = "<amount to displaye>",
			flags = "",
			desc = "Show top players for the arena.",
			min = 0,
			max = 1
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.setsell"})
	public static void top(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		Integer count = null;
		
		// Retrieve building:
		Arena selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, Arena.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
	
		// Arguments:
		if (args.argsLength() == 1) {
		
			try {
				count = Integer.parseInt(args.getString(0));
			} catch (NumberFormatException e) {
				sagaPlayer.message(ChunkGroupMessages.invalidInteger(args.getString(0)));
				return;
			}
			
		}else{
			
			count = 10;
			
		}
		
		// Inform:
		sagaPlayer.message(BuildingMessages.arenaTop(selectedBuilding, count));
		
	
	}
	
	
	
	
}
