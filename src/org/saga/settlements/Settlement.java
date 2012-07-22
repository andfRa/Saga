package org.saga.settlements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import org.bukkit.event.player.PlayerQuitEvent;
import org.saga.Clock;
import org.saga.Clock.MinuteTicker;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.buildings.BuildingDefinition;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.ChunkGroupToggleable;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.ProficiencyConfiguration.InvalidProficiencyException;
import org.saga.config.SettlementConfiguration;
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
	 * Player last seen dates.
	 */
	private Hashtable<String, Date> lastSeen;
	
	
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
		lastSeen = new Hashtable<String, Date>();
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
		
		if(lastSeen == null){
			SagaLogger.nullField(this, "lastSeen");
			lastSeen = new Hashtable<String, Date>();
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
		

		// Fix roles:
		try {
			
			ArrayList<String> members = getMembers();
			
			for (String member : members) {
				
				if(getRole(member) != null) continue;
				
				Proficiency role = ProficiencyConfiguration.config().createProficiency(getDefinition().defaultRole);
				playerRoles.put(member, role);
			}
			
		} catch (InvalidProficiencyException e) {
			SagaLogger.severe(this, "failed to set " + getDefinition().defaultRole + " role, because the role name is invalid");
		}
		
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
			Proficiency role = ProficiencyConfiguration.config().createProficiency(settlement.getDefinition().ownerRole);
			settlement.setRole(owner, role);
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
			Proficiency role = ProficiencyConfiguration.config().createProficiency(getDefinition().defaultRole);
			setRole(sagaPlayer, role);
		} catch (InvalidProficiencyException e) {
			SagaLogger.severe(this, "failed to set " + getDefinition().defaultRole + " role, because the role name is invalid");
		}
		
		// Last seen:
		lastSeen.put(sagaPlayer.getName(), Calendar.getInstance().getTime());
		
		
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

		// Last seen:
		lastSeen.remove(sagaPlayer.getName());
		
		
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
		return getDefinition().getExpSpeed(countActiveMembers());
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
	 * Gets the available roles.
	 * 
	 * @return all available roles
	 */
	public HashSet<String> getRoles() {
		
		HashSet<String> roles = new HashSet<String>();
		
//		// Add default role:
//		roles.add(getDefinition().defaultRole);
//		
//		// Add settlement roles:
//		roles.addAll(getDefinition().getRoles());
//		
//		// Add building roles:
//		ArrayList<SagaChunk> groupChunks = getGroupChunks();
//		for (SagaChunk sagaChunk : groupChunks) {
//			
//			Building building = sagaChunk.getBuilding();
//			if(building == null) continue;
//			
//			ArrayList<String> promotions = building.getDefinition().getRoles();
//			roles.addAll(promotions);
//			
//		}
		
		return roles;
		
	}

	/**
	 * Gets the amount of roles used.
	 * 
	 * @param hierarchy role hierarchy level
	 * @return amount of roles used
	 */
	public Integer getUsedRoles(Integer hierarchy) {
		
		Integer total = 0;
		
		Collection<Proficiency> roles = playerRoles.values();
		
		for (Proficiency role : roles) {
			
			if(role.getHierarchy() == hierarchy) total++;

		}
		
		return total;
		
	}

	/**
	 * Gets the amount of roles available.
	 * 
	 * @param hierarchy role hierarchy level
	 * @return
	 */
	public Integer getAvailableRoles(Integer hierarchy) {

		return getDefinition().getAvailableRoles(getLevel(), hierarchy);
		
	}
	
	/**
	 * Gets the amount of roles remaining.
	 * 
	 * @param hierarchy role hierarchy level
	 * @return amount of roles remaining
	 */
	public Integer getRemainingRoles(Integer hierarchy) {
		
		return getAvailableRoles(hierarchy) - getUsedRoles(hierarchy);
		
	}

	/**
	 * Checks if the given role is available.
	 * 
	 * @param hierarchy role hierarchy level
	 * @return true if available
	 */
	public boolean isRoleAvailable(Integer hierarchy) {
		
		if(hierarchy == getDefinition().getHierarchyMin()) return true;
		
		return getRemainingRoles(hierarchy) > 0;
		
	}
	
	/**
	 * Gets members with the given role.
	 * 
	 * @param roleName role name
	 * @return players with the given role
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
		
		
		return getDefinition().getBuildPoints(getLevel());
		
		
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

	
	
	// Active members:
	/**
	 * Gets player last seen date.
	 * 
	 * @param name player name
	 * @return last seen date
	 */
	public Date getLastSeen(String name) {
		
		
		// Online:
		if(hasRegisteredMember(name)) return Calendar.getInstance().getTime();
		
		// Offline:
		Date lastDate = lastSeen.get(name);
		if(lastDate == null){
			SagaLogger.severe(this, "last seen date not found for " + name + " player");
			lastDate = Calendar.getInstance().getTime();
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
		return countActiveMembers() >= getDefinition().getActivePlayers(getLevel());
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
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkGroup#onMemberQuit(org.bukkit.event.player.PlayerQuitEvent, org.saga.player.SagaPlayer)
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
	public void clockMinuteTick() {

		
		// Level progress:
		if(level < getDefinition().getMaxLevel() && checkActiveMembers()){
			
			exp += getExpSpeed();
			
			if(getRemainingExp() > 0) return;
			
			levelUp();

			// Inform:
			Saga.broadcast(ChunkGroupMessages.settleLevelBcast(this));
			
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
		ACCESS_STORAGE,
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
