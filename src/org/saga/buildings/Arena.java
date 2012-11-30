package org.saga.buildings;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.Clock;
import org.saga.Clock.SecondTicker;
import org.saga.SagaLogger;
import org.saga.config.SettlementConfiguration;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.listeners.events.SagaEntityDamageEvent.PvPOverride;
import org.saga.messages.BuildingMessages;
import org.saga.player.SagaPlayer;
import org.saga.settlements.SagaChunk;


public class Arena extends Building implements SecondTicker{


	/**
	 * Top sign.
	 */
	transient public static String TOP_SIGN = "=[TOP]=";
	
	/**
	 * Count down sign.
	 */
	transient public static String COUNTDOWN_SIGN = "=[COUNT]=";
	
	/**
	 * KDR multiplier.
	 */
	transient public static Double KDR_MULTIPLIER = 0.4;

	/**
	 * Attribute point multiplier.
	 */
	transient public static Double ATTRIBUTE_MULTIPLIER = 0.075;
	
	
	/**
	 * Arena players.
	 */
	private ArrayList<ArenaPlayer> arenaPlayers;
	
	/**
	 * Count down count.
	 */
	transient private Integer count;
	
	
	// Initialisation:
	/**
	 * Creates a building from the definition.
	 * 
	 * @param definition building definition
	 */
	public Arena(BuildingDefinition definition) {
		
		
		super(definition);

		arenaPlayers = new ArrayList<Arena.ArenaPlayer>();
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#completeExtended()
	 */
	@Override
	public boolean complete() throws InvalidBuildingException {
		

		boolean integrity = super.complete();
		
		if(arenaPlayers == null){
			arenaPlayers = new ArrayList<Arena.ArenaPlayer>();
			SagaLogger.nullField(this, "arenaPlayers");
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
				break;
			}
			
		}
		
		if(foundPlayer == null){
			
			foundPlayer = new ArenaPlayer(name, 0, 0, 0.0);
			arenaPlayers.add(foundPlayer);
			
		}
		
		foundPlayer.increaseKills();
		
		
		
	}
	
	/**
	 * Adds points to a player.
	 * 
	 * @param name player name
	 * @param sagaPlayer saga player
	 */
	private void addPoints(String name, Double points) {

		
		ArenaPlayer foundPlayer = null;
		
		for (ArenaPlayer arenaPlayer : arenaPlayers) {
		
			if(arenaPlayer.getName().equals(name)){
				foundPlayer = arenaPlayer;
				break;
			}
			
		}
		
		if(foundPlayer == null){
			
			foundPlayer = new ArenaPlayer(name, 0, 0, 0.0);
			arenaPlayers.add(foundPlayer);
			
		}
		
		foundPlayer.increasePoints(points);
		
		
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
				break;
			}
			
		}
		
		if(foundPlayer == null){
			
			foundPlayer = new ArenaPlayer(name, 0, 0, 0.0);
			arenaPlayers.add(foundPlayer);
			
		}
		
		foundPlayer.increaseDeaths();
			
		
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
			Clock.clock().enableSecondTick(this);
		}
		
		this.count = count;
		
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
		
		// Disable clock:
		if(count <= 0) return false;
		
		// Inform:
		SagaChunk sagaChunk = getSagaChunk();
		if(sagaChunk != null) sagaChunk.broadcast(BuildingMessages.countdown(count));
		
		count--;
		
		return true;
		
		
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
		if(ChatColor.stripColor(sign.getLine(0)).equals(TOP_SIGN)){
			
			sagaPlayer.message(BuildingMessages.arenaTop(this, 10));
			
			// Take control:
			event.setUseInteractedBlock(Result.DENY);
			event.setUseItemInHand(Result.DENY);
			
		}
		// Count down sign:
		else if(ChatColor.stripColor(sign.getLine(0)).equals(COUNTDOWN_SIGN)){
			
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
			event.setLine(0, SettlementConfiguration.config().enabledSignColor + TOP_SIGN);
		}
		
		// Count down sign:
		if(event.getLine(0).equalsIgnoreCase(COUNTDOWN_SIGN)){
			event.setLine(0, SettlementConfiguration.config().enabledSignColor + COUNTDOWN_SIGN);
		}
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerDamagedByPlayer(org.bukkit.event.entity.EntityDamageByEntityEvent, org.saga.SagaPlayer, org.saga.SagaPlayer)
	 */
	@Override
	public void onEntityDamage(SagaEntityDamageEvent event){
		
		
		// Attack from outside of the arena:
		SagaChunk attackerChunk = event.attackerChunk;
		SagaChunk defenderChunk = event.defenderChunk;
		if(attackerChunk == null || defenderChunk == null || attackerChunk != getSagaChunk() || defenderChunk != getSagaChunk()){
			return;
		}
		
		// Allow pvp:
		event.addPvpOverride(PvPOverride.ARENA_ALLOW);
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerKillPlayer(org.saga.player.SagaPlayer, org.saga.player.SagaPlayer)
	 */
	@Override
	public void onPvPKill(SagaPlayer attacker, SagaPlayer defender) {

		
		// Add kills:
		addKill(attacker.getName());
		addDeath(defender.getName());
		
		// Points:
		addPoints(attacker.getName(), defender.getUsedAttributePoints().doubleValue() * ATTRIBUTE_MULTIPLIER);
		
	
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
		 * Points.
		 */
		private Double points;
		
		
		/**
		 * Creates an arena player.
		 * 
		 * @param name name
		 * @param kills kills
		 * @param deaths deaths
		 * @param points points
		 */
		public ArenaPlayer(String name, Integer kills, Integer deaths, Double points) {

			this.name = name;
			this.kills = kills;
			this.deaths = deaths;
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
			
			if(kills == null){
				kills = 0;
				SagaLogger.nullField(this, "kills");
				integrity = false;
			}
			
			if(deaths == null){
				deaths = 0;
				SagaLogger.nullField(this, "deaths");
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
		public Double calculateScore() {
			
			Integer kills = this.kills;
			Integer deaths = this.deaths;
			
			if(kills == 0) kills = 1;
			if(deaths == 0) deaths = 1;
			
			return points * (1 + KDR_MULTIPLIER * (kills / deaths));
			
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
		 * Gets kills.
		 * 
		 * @return kills
		 */
		public Integer getKills() {
			return kills;
		}

		/**
		 * Increases kills.
		 * 
		 */
		public void increaseKills() {
			kills++;
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
	
	
}
