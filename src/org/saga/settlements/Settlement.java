package org.saga.settlements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.event.player.PlayerQuitEvent;
import org.saga.Clock;
import org.saga.Clock.MinuteTicker;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.buildings.Building;
import org.saga.buildings.BuildingDefinition;
import org.saga.config.BuildingConfiguration;
import org.saga.config.FactionConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.ProficiencyConfiguration.InvalidProficiencyException;
import org.saga.config.SettlementConfiguration;
import org.saga.listeners.events.SagaBuildEvent;
import org.saga.listeners.events.SagaBuildEvent.BuildOverride;
import org.saga.messages.SettlementMessages;
import org.saga.player.Proficiency;
import org.saga.player.ProficiencyDefinition;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Bundle;
import org.saga.settlements.BundleToggleable;
import org.saga.statistics.StatisticsManager;

/**
 * @author andf
 *
 */
public class Settlement extends Bundle implements MinuteTicker{

	
	/**
	 * Settlement level.
	 */
	private Integer level;

	/**
	 * Claims.
	 */
	private Double claims;
	
	
	/**
	 * Player roles.
	 */
	private Hashtable<String, Proficiency> playerRoles;
	
	/**
	 * Player last seen dates.
	 */
	private Hashtable<String, Date> lastSeen;
	
	
	/**
	 * True if the minute tick is registered.
	 */
	transient boolean isTicking = false;
	

	
	// Initialisation:
	/**
	 * Sets name.
	 * 
	 * @param name name
	 */
	public Settlement(String name) {
		
		super(name);
		level = 0;
		claims = SettlementConfiguration.config().getInitialClaims().doubleValue();
		
		playerRoles = new Hashtable<String, Proficiency>();
		lastSeen = new Hashtable<String, Date>();
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkBundle#completeExtended()
	 */
	@Override
	public void complete() {
		
		
		super.complete();
		
		if(level == null){
			SagaLogger.nullField(this, "level");
			level = 0;
		}
		
		if(claims == null){
			if(level != null){
				int maxLevel = 50;
				claims = SettlementConfiguration.config().getMaxClaims().doubleValue() * level.doubleValue() / maxLevel;
			}else{
				claims = 0.0;
			}
			SagaLogger.nullField(this, "claims");
		}
		
		if(lastSeen == null){
			SagaLogger.nullField(this, "lastSeen");
			lastSeen = new Hashtable<String, Date>();
		}
		
		if(playerRoles == null){
			SagaLogger.nullField(this, "playerRoles");
			playerRoles = new Hashtable<String, Proficiency>();
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
		

		// Fix roles:
		try {
			
			ArrayList<String> members = getMembers();
			
			for (String member : members) {
				
				if(getRole(member) != null) continue;
				
				Proficiency role = ProficiencyConfiguration.config().createProficiency(SettlementConfiguration.config().getDefaultRole());
				playerRoles.put(member, role);
			}
			
		} catch (InvalidProficiencyException e) {
			SagaLogger.severe(this, "failed to set " + SettlementConfiguration.config().getDefaultRole() + " role, because the role name is invalid");
		}
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkBundle#delete()
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
	 * @see org.saga.chunkGroups.ChunkBundle#enable()
	 */
	@Override
	public void enable() {
		
		super.enable();
		
		Clock.clock().enableMinuteTick(this);
		
	}
	
	@Override
	public void disable() {
		
		super.disable();
		
	}
	
	/**
	 * Adds a new settlement.
	 * 
	 * @param settlement settlement
	 * @param owner owner
	 */
	public static void create(Settlement settlement, SagaPlayer owner){

		// Forward:
		Bundle.create(settlement, owner);

//		// Set owners role:
//		try {
//			Proficiency role = ProficiencyConfiguration.config().createProficiency(settlement.getDefinition().ownerRole);
//			settlement.setRole(owner, role);
//		} catch (InvalidProficiencyException e) {
//			SagaLogger.severe(settlement, "failed to set " + settlement.getDefinition().ownerRole + " role, because the role name is invalid");
//		}
		
	}
	
	
	
	// Player management:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkBundle#addPlayer3(org.saga.SagaPlayer)
	 */
	@Override
	public void addMember(SagaPlayer sagaPlayer) {


		super.addMember(sagaPlayer);

		// Set default role:
		try {
			Proficiency role = ProficiencyConfiguration.config().createProficiency(SettlementConfiguration.config().getDefaultRole());
			setRole(sagaPlayer, role);
		} catch (InvalidProficiencyException e) {
			SagaLogger.severe(this, "failed to set " + SettlementConfiguration.config().getDefaultRole() + " role, because the role name is invalid");
		}
		
		// Last seen:
		lastSeen.put(sagaPlayer.getName(), Calendar.getInstance().getTime());
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkBundle#removePlayer3(org.saga.SagaPlayer)
	 */
	@Override
	public void removeMember(SagaPlayer sagaPlayer) {
		
		
		// Clear role:
		clearRole(sagaPlayer);
		
		// Forward:
		super.removeMember(sagaPlayer);

		// Last seen:
		lastSeen.remove(sagaPlayer.getName());
		
		
	}

	
	
	// Roles:
	/**
	 * Adds a role to the player.
	 * 
	 * @param sagaPlayer saga player
	 * @param role role
	 */
	public void setRole(SagaPlayer sagaPlayer, Proficiency role) {

		
		// Clear previous role:
		if(playerRoles.get(sagaPlayer.getName()) != null){
			clearRole(sagaPlayer);
		}
		
		// Add to settlement:
		playerRoles.put(sagaPlayer.getName(), role);
		
		// Update:
		sagaPlayer.update();
		
		
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

		// Update:
		sagaPlayer.update();
		
		
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
	 * Gets the amount of roles used.
	 * 
	 * @param roleName role name
	 * @return amount of roles used
	 */
	public Integer getUsedRoles(String roleName) {
		
		Integer total = 0;
		
		Collection<Proficiency> roles = playerRoles.values();
		
		for (Proficiency role : roles) {
			
			if(role.getName().equals(roleName)) total++;

		}
		
		return total;
		
	}

	/**
	 * Gets the amount of roles available.
	 * 
	 * @param roleName role name
	 * @return amount of roles available
	 */
	public Integer getAvailableRoles(String roleName) {

		Double count = 0.0;
		
		ArrayList<Building> buildings = getBuildings();
		for (Building building : buildings) {
			
			count+= building.getDefinition().getRoles(roleName);
			
		}
		
		return count.intValue();
		
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
	 * @param hierarchy role hierarchy level
	 * @return true if available
	 */
	public boolean isRoleAvailable(String roleName) {

		ProficiencyDefinition role = ProficiencyConfiguration.config().getDefinition(roleName);
		if(role == null) return false;
		
		if(role.getHierarchyLevel() == FactionConfiguration.config().getHierarchyMin()) return true;
		
		return getRemainingRoles(roleName) > 0;
		
	}
	
	
	/**
	 * Gets members with the given role.
	 * 
	 * @param roleName role name
	 * @return members with the given role
	 */
	public ArrayList<String> getMembersForRoles(String roleName) {

		ArrayList<String> filtMembers = new ArrayList<String>();
		ArrayList<String> allMembers = getMembers();
		
		for (String member : allMembers) {
			
			Proficiency role = getRole(member);
			if(role != null && role.getName().equals(roleName)) filtMembers.add(member);
			
		}
		
		return filtMembers;
		
		
	}
	
	
	// Building points:
	/**
	 * Gets the amount of building points available.
	 * 
	 * @return amount building points available
	 */
	public Integer getAvailableBuildPoints() {
		return SettlementConfiguration.config().getBuildPoints(getSize());
	}

	
	
	// Claims:
	/**
	 * Gets the amount of claims.
	 * 
	 * @return amount of claims
	 */
	public Double getClaims() {
		return claims;
	}
	
	/**
	 * Sets the amount of claims.
	 * 
	 * @param claims amount of claims
	 */
	public void setClaims(Double claims) {
		this.claims = claims;
	}
	
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
		if(claims > SettlementConfiguration.config().getMaxClaims()) return SettlementConfiguration.config().getMaxClaims();
		return claims.intValue();
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
		return getAvailableClaims() > 0 || isOptionEnabled(BundleToggleable.UNLIMITED_CLAIMS);
	}
	
	/**
	 * Gets the claim progress.
	 * 
	 * @return claim progress, values 0.0 - 1.0
	 */
	public Double getClaimProgress() {
		return claims - claims.intValue();
	}
	
	
	
	// Buildings:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkBundle#getTotalBuildings(java.lang.String)
	 */
	@Override
	public Integer getAvailableBuildings(String buildingName) {

		BuildingDefinition definition = BuildingConfiguration.config().getBuildingDefinition(buildingName);
		
		if(definition == null) return 0;
		
		return definition.getAvailableAmount(getSize());
		
	}
	

	
	// Permissions:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkBundle#hasPermisson(org.saga.player.SagaPlayer, org.saga.settlements.Settlement.SettlementPermission)
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
	
	
	
	// Messages:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunks.Bundle#chat(org.saga.player.SagaPlayer, java.lang.String)
	 */
	@Override
	public void chat(SagaPlayer sagaPlayer, String message) {
		
		message = SettlementMessages.normal2 + "[" + SettlementMessages.normal1 + SettlementMessages.roledPlayer(this, sagaPlayer) + SettlementMessages.normal2 + "] " + message;
		
		Collection<SagaPlayer> onlineMembers = getOnlineMembers();
		
		for (SagaPlayer onlineMember : onlineMembers) {
			onlineMember.message(message);
		}
			
	}
	
	
	
	// Active members:
	/**
	 * Gets player last seen date.
	 * 
	 * @param name player name
	 * @return last seen date
	 */
	public Date getLastSeen(String name) {
		
		
		// Online:
		if(Saga.plugin().isSagaPlayerLoaded(name)) return Calendar.getInstance().getTime();
		
		// Offline:
		Date lastDate = lastSeen.get(name);
		if(lastDate == null){
			SagaLogger.severe(this, "last seen date not found for " + name + " player");
			lastDate = Calendar.getInstance().getTime();
			lastSeen.put(name, lastDate);
		}
		
		return lastDate;
		
		
	}
	
	/**
	 * Checks if the member is active.
	 * 
	 * @param name player name
	 * @return true if active
	 */
	public boolean isMemberActive(String name) {

		
		Calendar inactiveCal = Calendar.getInstance();
		inactiveCal.add(Calendar.DAY_OF_MONTH, - SettlementConfiguration.config().inactiveSetDays);
		
		Date inactivate = inactiveCal.getTime();
		Date lastSeen = getLastSeen(name);
		
		return !inactivate.after(lastSeen);
		
		
	}

	/**
	 * Gets inactive member count.
	 * 
	 * @return inactive member count
	 */
	public int countInactiveMembers() {

		
		int inactive = 0;
		ArrayList<String> players = getMembers();
		
		for (String playerName : players) {
			
			if(!isMemberActive(playerName)) inactive++;
			
		}
		
		return inactive;
		
		
	}
	
	/**
	 * Gets active member count.
	 * 
	 * @return active member count
	 */
	public int countActiveMembers() {

		
		int active = 0;
		ArrayList<String> players = getMembers();
		
		for (String playerName : players) {
			
			if(isMemberActive(playerName)) active++;
			
		}
		
		return active;
		
		
	}
	
	/**
	 * Checks if the settlement has enough active members.
	 * 
	 * @return true if enough active members
	 */
	public boolean checkActiveMembers() {
		return countActiveMembers() >= SettlementConfiguration.config().getRequiredActiveMembers(getSize());
	}
	
	
	// Events:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkBundle#onBuild(org.saga.listeners.events.SagaBuildEvent)
	 */
	@Override
	public void onBuild(SagaBuildEvent event) {
		
		// Add override:
		if(!hasPermission(event.getSagaPlayer(), SettlementPermission.BUILD)) event.addBuildOverride(BuildOverride.SETTLEMENT_DENY);
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkBundle#onMemberQuit(org.bukkit.event.player.PlayerQuitEvent, org.saga.player.SagaPlayer)
	 */
	@Override
	public void onMemberQuit(PlayerQuitEvent event, SagaPlayer sagaPlayer) {

		// Update last seen:
		lastSeen.put(sagaPlayer.getName(), Calendar.getInstance().getTime());
	
	}

	
	
	// Clock:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.MinuteTicker#clockMinuteTick()
	 */
	@Override
	public boolean clockMinuteTick() {

		
		if(!isEnabled()) return false;

		int online = getOnlineMembers().size();
		
		// Statistics:
		StatisticsManager.manager().addManminutes(this, online);

		// Increase claims:
		if(claims < SettlementConfiguration.config().getMaxClaims() && checkActiveMembers()){
			claims+= SettlementConfiguration.config().getClaimsPerMinute(online);
			
			// Statistics:
			StatisticsManager.manager().addSettlementClaims(this, SettlementConfiguration.config().getClaimsPerMinute(online));
			
		}
		
		return true;
		
		
	}

	
	// Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkBundle#toString()
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
		ACCESS_STORAGE,
		OPEN_HOME_CHESTS,
		BUILD,
		BUILD_BUILDING,
		BUILDING_UPGRADE,
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
		RESIGN,
		MEMBER_COMMAND,
		SPAWN,
		
		// Farm:
		HURT_FARM_ANIMALS,
		
		// Home:
		REMOVE_RESIDENT,
		ADD_RESIDENT,
		
		// Trading post:
		MANAGE_PRICES,
		MANAGE_DEALS;
		
		
	}
	
	
	
}
