package org.saga.settlements;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import org.saga.Clock;
import org.saga.Clock.MinuteTicker;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.buildings.Building;
import org.saga.buildings.BuildingDefinition;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.ChunkGroupToggleable;
import org.saga.chunkGroups.SagaChunk;
import org.saga.config.SettlementConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.ProficiencyConfiguration.InvalidProficiencyException;
import org.saga.listeners.events.SagaBuildEvent;
import org.saga.listeners.events.SagaBuildEvent.BuildOverride;
import org.saga.messages.ChunkGroupMessages;
import org.saga.player.Proficiency;
import org.saga.player.SagaPlayer;

/**
 * @author andf
 *
 */
public class Settlement extends ChunkGroup implements MinuteTicker{

	
	/**
	 * Settlement level.
	 */
	private Integer level;
	
	/**
	 * Player roles.
	 */
	private Hashtable<String, Proficiency> playerRoles;
	
	/**
	 * Experience.
	 */
	private Double exp;
	
	/**
	 * True if the minute tick is registered.
	 */
	transient boolean isTicking = false;
	
	/**
	 * Settlement definition.
	 */
	transient private SettlementDefinition definition;
	
	
	// Initialisation:
	/**
	 * Sets name.
	 * 
	 * @param name name
	 */
	public Settlement(String name) {
		
		super(name);
		level = 0;
		exp = 0.0;
		playerRoles = new Hashtable<String, Proficiency>();
		definition = SettlementConfiguration.config().getSettlementDefinition();
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkGroup#completeExtended()
	 */
	@Override
	public boolean complete() {
		
		
		boolean integrity = super.complete();
		
		if(level == null){
			SagaLogger.nullField(this, "level");
			level = 0;
			integrity = false;
		}
		
		if(exp == null){
			SagaLogger.nullField(this, "exp "+ this +" levelProgress");
			exp = 0.0;
			integrity = false;
		}
		
		if(playerRoles == null){
			SagaLogger.nullField(this, "playerRoles");
			playerRoles = new Hashtable<String, Proficiency>();
			integrity = false;
		}
		
		Enumeration<String> playerNames = playerRoles.keys();
		while(playerNames.hasMoreElements()){
			String playerName = null;
			Proficiency proficiency = null;
			playerName = playerNames.nextElement();
			try {
				proficiency = playerRoles.get(playerName);
				proficiency.complete();
			} catch (InvalidProficiencyException e) {
				SagaLogger.severe(this, "tried to add an invalid " + proficiency + " role:" + e.getMessage());
				playerRoles.remove(playerName);
			}
			catch (NullPointerException e) {
				SagaLogger.severe(this, "tried to add an null proficiency");
				playerRoles.remove(playerName);
			}
		}
		
		// Definition:
		definition = SettlementConfiguration.config().getSettlementDefinition();
		
		return integrity;
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkGroup#delete()
	 */
	@Override
	public void delete() {
		
		super.delete();
		
		// Clear roles:
		playerRoles.clear();
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkGroup#enable()
	 */
	@Override
	public void enable() {
		
		super.enable();
		
		Clock.clock().registerMinuteTick(this);
		
	}
	
	@Override
	public void disable() {
		
		super.disable();
		
		Clock.clock().unregisterMinuteTick(this);
		
	}
	
	/**
	 * Adds a new settlement.
	 * 
	 * @param settlement settlement
	 * @param owner owner
	 */
	public static void create(Settlement settlement, SagaPlayer owner){

		
		// Forward:
		ChunkGroup.create(settlement, owner);

		// Set owners role:
		try {
			settlement.setRole(owner, settlement.getDefinition().ownerRole);
		} catch (InvalidProficiencyException e) {
			SagaLogger.severe(settlement, "failed to set " + settlement.getDefinition().ownerRole + " role, because the role name is invalid");
		}
		
		
	}
	
	
	
	// Player management:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkGroup#addPlayer3(org.saga.SagaPlayer)
	 */
	@Override
	public void addPlayer(SagaPlayer sagaPlayer) {


		super.addPlayer(sagaPlayer);

		// Set default role:
		try {
			setRole(sagaPlayer, getDefinition().defaultRole);
		} catch (InvalidProficiencyException e) {
			SagaLogger.severe(this, "failed to set " + getDefinition().defaultRole + " role, because the role name is invalid");
		}
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkGroup#removePlayer3(org.saga.SagaPlayer)
	 */
	@Override
	public void removePlayer(SagaPlayer sagaPlayer) {
		
		
		// Clear role:
		clearRole(sagaPlayer);
		
		// Forward:
		super.removePlayer(sagaPlayer);
		
		
	}
	
	
	// Leveling:
	/**
	 * Gets settlement level.
	 * 
	 */
	public Integer getLevel() {
		return level;
	}
	
	/**
	 * Sets the level of the settlement.
	 * 
	 * @param level level
	 */
	public void setLevel(Integer level) {
		
		// Set related fields:
		this.level = level;
		
	}
	
	/**
	 * Levels up the settlement.
	 * 
	 */
	public void levelUp() {
		setLevel(getLevel() + 1);
	}
	
	/**
	 * Gets experience.
	 * 
	 * @return experience
	 */
	public Double getExp() {
		return exp;
	}

	/**
	 * Gets remaining experience.
	 * 
	 * @return remaining experience
	 */
	public Double getRemainingExp() {
		return getDefinition().getLevelUpExp(getLevel()) - exp;
	}

	/**
	 * Gets experience gain speed.
	 * 
	 * @return experience speed
	 */
	public Double getExpSpeed() {
		return getDefinition().getExpSpeed(getActivePlayerCount());
	}
	
	
	
	// Roles:
	/**
	 * Adds a role to the player.
	 * 
	 * @param sagaPlayer saga player
	 * @param roleName role name
	 * @throws InvalidProficiencyException thrown if the role name is invalid
	 */
	public void setRole(SagaPlayer sagaPlayer, String roleName) throws InvalidProficiencyException {

		
		// Void role:
		if(roleName.equals("")){
			return;
		}
		
		// Clear previous role:
		if( playerRoles.get(sagaPlayer.getName()) != null ){
			clearRole(sagaPlayer);
		}
		
		// Create role:
		Proficiency role = ProficiencyConfiguration.config().createProficiency(roleName);

		// Add to settlement:
		playerRoles.put(sagaPlayer.getName(), role);
		
		
	}
	
	/**
	 * Clears a role from the player.
	 * 
	 * @param sagaPlayer sga player
	 */
	public void clearRole(SagaPlayer sagaPlayer) {
	

		// Check role:
		Proficiency role = playerRoles.get( sagaPlayer.getName() );
		if( role == null ){
			return;
		}

		// Remove from settlement:
		playerRoles.remove(sagaPlayer.getName());
		
			
	}

	/**
	 * Gets a player role.
	 * 
	 * @param playerName player name
	 * @return player role. null if none
	 */
	public Proficiency getRole(String playerName) {
		
		return playerRoles.get(playerName);
		
	}

	/**
	 * Gets the available roles.
	 * 
	 * @return all available roles
	 */
	public HashSet<String> getRoles() {
		
		HashSet<String> roles = new HashSet<String>();
		
		// Add default role:
		roles.add(getDefinition().defaultRole);
		
		// Add settlement roles:
		roles.addAll(getDefinition().getRoles());
		
		// Add building roles:
		ArrayList<SagaChunk> groupChunks = getGroupChunks();
		for (SagaChunk sagaChunk : groupChunks) {
			
			Building building = sagaChunk.getBuilding();
			if(building == null) continue;
			
			ArrayList<String> promotions = building.getDefinition().getRoles();
			roles.addAll(promotions);
			
		}
		
		return roles;
		
	}

	/**
	 * Gets the amount of roles used.
	 * 
	 * @param roleName role name
	 * @return amount of roles used
	 */
	public Integer getUsedRoles(String roleName) {
		
		Integer total = 0;
		Enumeration<String> players = playerRoles.keys();
		while (players.hasMoreElements()) {
			
			String player = players.nextElement();
			Proficiency role = playerRoles.get(player);
			
			if(roleName.equals(role.getName()))total ++;
			
		}
 		
		return total;
		
	}

	/**
	 * Gets the amount of roles available.
	 * 
	 * @param roleName role name
	 * @return amount roles available
	 */
	public Integer getAvailableRoles(String roleName) {
		
		
		if(roleName.equals(getDefinition().defaultRole)){
			return getPlayerCount() - getInactivePlayerCount();
		}
		
		Integer total = 0;
		
		// Settlement roles:
		total += getDefinition().getAvailableRoles(roleName, getLevel());
		
		// Buildings roles:
		ArrayList<SagaChunk> groupChunks = getGroupChunks();
		for (SagaChunk sagaChunk : groupChunks) {
			
			Building building = sagaChunk.getBuilding();
			if(building == null) continue;
			total += building.getDefinition().getAvailableRoles(roleName, building.getLevel());
			
		}
		
		return total;
		
	}
	
	/**
	 * Gets the amount of roles remaining.
	 * 
	 * @param roleName role name
	 * @return amount of roles remaining
	 */
	public Integer getRemainingRoles(String roleName) {
		
		return getAvailableRoles(roleName) - getUsedRoles(roleName);
		
	}

	/**
	 * Checks if the given role is available.
	 * 
	 * @param roleName role name
	 * @return true if available
	 */
	public boolean isRoleAvailable(String roleName) {
		
		if(roleName.equals(getDefinition().defaultRole)){
			return true;
		}
		
		return getRemainingRoles(roleName) > 0;
		
	}
	
	

	
	// Claiming:
	/**
	 * Gets used claims.
	 * 
	 * @return used claims
	 */
	public Integer getUsedClaimed() {
		return new Integer(getGroupChunks().size());
	}
	
	/**
	 * Gets total claims.
	 * 
	 * @return total claims.
	 */
	public Integer getTotalClaims() {
		return getDefinition().getClaims(getLevel());
	}
	
	/**
	 * Gets available claims.
	 * 
	 * @return available claims
	 */
	public Integer getAvailableClaims() {
		return getTotalClaims() - getUsedClaimed();
	}

	/**
	 * Check if there is a claim available.
	 * 
	 * @return true if available
	 */
	public boolean isClaimsAvailable() {
		return getAvailableClaims() > 0 || isOptionEnabled(ChunkGroupToggleable.UNLIMITED_CLAIMS);
	}
	
	
	
	// Buildings:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkGroup#getTotalBuildings(java.lang.String)
	 */
	@Override
	public Integer getAvailableBuildings(String buildingName) {


		BuildingDefinition definition = SettlementConfiguration.config().getBuildingDefinition(buildingName);
		
		if(definition == null) return 0;
		
		return definition.getAvailableAmount(getLevel());
		
		
	}
	

	
	// Permissions:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkGroup#hasPermisson(org.saga.player.SagaPlayer, org.saga.settlements.Settlement.SettlementPermission)
	 */
	public boolean hasPermission(SagaPlayer sagaPlayer, SettlementPermission permission) {

		
		// Owner or admin:
		if(isOwner(sagaPlayer.getName()) || sagaPlayer.isAdminMode()) return true;
		
		// Check role:
		Proficiency role = playerRoles.get(sagaPlayer.getName());
		if(role == null){
			return false;
		}
		
		// Check permission:
		return role.hasSettlementPermission(permission);
		
		
	}
	
	
	
	// Information:
	/**
	 * Gets the definition for the settlement.
	 * 
	 * @return definition
	 */
	public SettlementDefinition getDefinition() {
		return definition;
	}

	
	
	// Events:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkGroup#onBuild(org.saga.listeners.events.SagaBuildEvent)
	 */
	@Override
	public void onBuild(SagaBuildEvent event) {
		
		// Add override:
		if(!hasPermission(event.getSagaPlayer(), SettlementPermission.BUILD)) event.addBuildOverride(BuildOverride.SETTLEMENT_DENY);
		
	}
	

	
	// Clock:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.MinuteTicker#clockMinuteTick()
	 */
	@Override
	public void clockMinuteTick() {

		
		// Level progress:
		if(level < getDefinition().getMaxLevel() && definition.canLevelUp(this)){
			
			exp += getExpSpeed();
			
			if(getRemainingExp() > 0) return;
			
			levelUp();

			// Inform:
			Saga.broadcast(ChunkGroupMessages.settlementLevel(this));
			
		}
		
		
	}

	
	// Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkGroup#toString()
	 */
	@Override
	public String toString() {
		return getId() + " (" + getName() + ")";
	}
	
	
	
	// Types:
	/**
	 * Settlement permissions.
	 * 
	 * @author andf
	 *
	 */
	public enum SettlementPermission{
		
		
		ACCESS_WAREHOUSE,
		BUILD,
		BUILD_BUILDING,
		CLAIM,
		CLAIM_SETTLEMENT,
		ABANDON,
		DECLARE_OWNER,
		DISSOLVE,
		INVITE,
		KICK,
		SET_ROLE,
		SET_BUILDING,
		REMOVE_BUILDING,
		RENAME,
		MEMBER_COMMAND,
		SPAWN,
		
		// Home:
		REMOVE_RESIDENT,
		ADD_RESIDENT,
		
		// Trading post:
		MANAGE_PRICES,
		MANAGE_DEALS;
		
		
	}
	
	
	
}
