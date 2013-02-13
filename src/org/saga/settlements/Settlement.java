package org.saga.settlements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

import org.bukkit.event.player.PlayerQuitEvent;
import org.saga.Clock;
import org.saga.Clock.DaytimeTicker;
import org.saga.Clock.MinuteTicker;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.buildings.Building;
import org.saga.buildings.BuildingDefinition;
import org.saga.buildings.production.ProductionBuilding;
import org.saga.config.BuildingConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.config.FactionConfiguration;
import org.saga.config.GeneralConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.ProficiencyConfiguration.InvalidProficiencyException;
import org.saga.config.SettlementConfiguration;
import org.saga.dependencies.EconomyDependency;
import org.saga.factions.Faction;
import org.saga.factions.FactionClaimManager;
import org.saga.listeners.events.SagaBuildEvent;
import org.saga.listeners.events.SagaBuildEvent.BuildOverride;
import org.saga.messages.EconomyMessages;
import org.saga.messages.SettlementMessages;
import org.saga.messages.colours.Colour;
import org.saga.player.Proficiency;
import org.saga.player.ProficiencyDefinition;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;

/**
 * @author andf
 *
 */
public class Settlement extends Bundle implements MinuteTicker, DaytimeTicker{

	
	/**
	 * Settlement level.
	 */
	private Integer level; // TODO: Remove unused settlement level.

	/**
	 * Claims.
	 */
	private Double claims;
	
	/**
	 * Build points.
	 */
	private Double buildPoints;
	
	/**
	 * Coins banked.
	 */
	private Double coins;
	
	/**
	 * Player roles.
	 */
	private Hashtable<String, Proficiency> playerRoles;
	
	/**
	 * Player last seen dates.
	 */
	private Hashtable<String, Date> lastSeen;
	
	/**
	 * Work points for different roles.
	 */
	private Hashtable<String, Double> workPoints;
	
	/**
	 * Settlement wages.
	 */
	private Hashtable<String, Double> wages;

	
	
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
		buildPoints = SettlementConfiguration.config().getInitialBuildPoints().doubleValue();
		coins = 0.0;
		
		playerRoles = new Hashtable<String, Proficiency>();
		lastSeen = new Hashtable<String, Date>();
		workPoints = new Hashtable<String, Double>();
		wages = new Hashtable<String, Double>();
		
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
		
		// TODO: Remove claims conversion:
		if(claims == null && level != null){
			int maxLevel = 50;
			claims = SettlementConfiguration.config().getMaxClaims().doubleValue() * level.doubleValue() / maxLevel;
			SagaLogger.nullField(this, "claims");
		}
		
		// TODO: Remove building points conversion:
		if(buildPoints == null){
			buildPoints = SettlementConfiguration.config().getBuildPoints(getSize()).doubleValue();
		}
		
		if(claims == null){
			SagaLogger.nullField(this, "claims");
			buildPoints = 0.0;
		}
		
		if(buildPoints == null){
			SagaLogger.nullField(this, "buildPoints");
			buildPoints = 0.0;
		}
		
		
		if(coins == null){
			SagaLogger.nullField(this, "coins");
			coins = 0.0;
		}
		
		
		if(lastSeen == null){
			SagaLogger.nullField(this, "lastSeen");
			lastSeen = new Hashtable<String, Date>();
		}
		
		if(playerRoles == null){
			SagaLogger.nullField(this, "playerRoles");
			playerRoles = new Hashtable<String, Proficiency>();
		}
		
		if(workPoints == null){
			SagaLogger.nullField(this, "workPoints");
			workPoints = new Hashtable<String, Double>();
		}
		
		
		if(wages == null){
			SagaLogger.nullField(this, "wages");
			wages = new Hashtable<String, Double>();
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
		Clock.clock().enableDaytimeTicking(this);
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.settlements.Bundle#disable()
	 */
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
	
	
	
	// Work points:
	/** 
	 * Gets the work points for the given role.
	 * 
	 * @param roleName role name
	 * @return work points
	 */
	public Double getWorkPoints(String roleName) {
		
		Double points = workPoints.get(roleName);
		if(points == null) points = 0.0;
		return points;
		
	}
	
	/**
	 * Takes work points.
	 * 
	 * @param roleName role name
	 * @param requested requested amount
	 * @return work points taken
	 */
	public Double takeWorkPoints(String roleName, Double requested) {
		
		Double available = getWorkPoints(roleName);
		
		if(available - requested < 0) requested = available;
		
		if(available - requested > 0) workPoints.put(roleName, available - requested);
		else workPoints.remove(roleName);
		
		return requested;
		
	}
	
	
	
	// Wages:
	/**
	 * Handles wages.
	 * 
	 */
	private void handleWages() {
		
		if(!EconomyConfiguration.config().isEnabled()) return;
		
		// Pay online members:
		Collection<SagaPlayer> online = getOnlineMembers();
		
		for (SagaPlayer sagaPlayer : online) {
			
			Double wage = getWage(sagaPlayer.getName());
			if(wage == 0.0) continue; 
			
			// Pay:
			EconomyDependency.addCoins(sagaPlayer, wage);
			
			// Reset:
			resetWage(sagaPlayer.getName());
			
			// Inform:
			information(EconomyMessages.gotPaid(this, wage), sagaPlayer);
			
			//Statistics:
			StatisticsManager.manager().addWages(this, wage);
			
		}
		
	}
	
	/**
	 * Distributes wages.
	 * 
	 * @param paid total amount paid 
	 */
	public void distribWages(Double paid) {

		
		if(!EconomyConfiguration.config().isEnabled()) return;
		
		Collection<SagaPlayer> online = getOnlineMembers();
		
		double[] wageWeigths = new double[online.size()];
		String[] names = new String[online.size()];
		double total = 0.0;
		
		// Percentages:
		int i = 0;
		for (SagaPlayer member : online) {
			
			wageWeigths[i] = EconomyConfiguration.config().getWageWeigth(this, member);
			names[i] = member.getName();
			total+= wageWeigths[i];
			i++;
			
		}
		
		// Add wages:
		if(total != 0){
			
			for (int j = 0; j < names.length; j++) {
				
				Double wage = getWage(names[j]);
				wage+= wageWeigths[j]/total * paid;
				this.wages.put(names[j], wage);
				
			}
			
		}
		
		
	}
	
	/**
	 * Gets members wage.
	 * 
	 * @param memberName member name
	 * @return wage
	 */
	public Double getWage(String memberName) {
		
		Double wage = wages.get(memberName);
		if(wage == null) return 0.0;
		return wage;
		
	}
	
	/**
	 * Resets members wage.
	 * 
	 * @param memberName member name
	 */
	public void resetWage(String memberName) {
		wages.remove(memberName);
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
	 * Modifies the amount of claims.
	 * 
	 * @param amount amount to modify by
	 */
	public void modClaims(Double amount) {
		claims+= amount;
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
	
	
	
	// Building points:
	/**
	 * Gets the amount of buildPoints.
	 * 
	 * @return amount of buildPoints
	 */
	public Double getBuildPoints() {
		return buildPoints;
	}
	
	/**
	 * Sets the amount of buildPoints.
	 * 
	 * @param buildPoints amount of buildPoints
	 */
	public void setBuildPoints(Double buildPoints) {
		this.buildPoints = buildPoints;
	}
	
	/**
	 * Gets the amount of building points available.
	 * 
	 * @return amount building points available
	 */
	public Integer getAvailableBuildPoints() {
		return buildPoints.intValue();
	}
	
	/**
	 * Gets the amount of building points used.
	 * 
	 * @return amount of building points used
	 */
	public Integer getUsedBuildPoints() {
		
		
		Integer total = 0;
		ArrayList<Building> buildings = getBuildings();
		
		for (Building building : buildings) {
			total+= building.getDefinition().getBuildPoints();
		}
 		
		return total;
		
		
	}

	/**
	 * Gets the amount of building points remaining.
	 * 
	 * @return amount of building points remaining
	 */
	public Integer getRemainingBuildPoints() {
		
		return getAvailableBuildPoints() - getUsedBuildPoints();
		
	}

	/**
	 * Checks if there are building points are available.
	 * 
	 * @param building building
	 * @return true if building points available
	 */
	public boolean isBuildPointsAvailable(Building building) {

		if(isOptionEnabled(BundleToggleable.UNLIMITED_BUILDINGS)) return true;
		
		return getRemainingBuildPoints() >= building.getDefinition().getBuildPoints();
		
	}
	
	/**
	 * Gets the building point progress.
	 * 
	 * @return building point progress, values 0.0 - 1.0
	 */
	public Double getBuildPointsProgress() {
		return buildPoints - buildPoints.intValue();
	}
		
	
	
	// Bank:
	/**
	 * Gets the amount of coins banked.
	 * 
	 * @return coins banked
	 */
	public Double getCoins() {
		return coins;
	}
	
	/**
	 * Pays coins.
	 * 
	 * @param amount amount to pay
	 */
	public void payCoins(Double amount) {

		Double settlementShare = amount * EconomyConfiguration.config().getSettlementPercent();
		Double factionShare = amount * EconomyConfiguration.config().getSettlementFactionPercent();
		Double membersShare = amount * EconomyConfiguration.config().getSettlementMemberPercent();
		
		// Members:
		distribWages(membersShare);
		
		// Settlement:
		coins+= settlementShare;
		
		// Faction:
		Faction owningFaction = FactionClaimManager.manager().getOwningFaction(getId());
		if(owningFaction != null) owningFaction.payCoins(factionShare);
		
	}
	
	/**
	 * Requests coins.
	 * 
	 * @param request amount request
	 */
	public Double requestCoins(Double request) {

		Double given = request;
		if(given > coins) given = coins;
		coins-= given;
		return given;
		
	}
	
	/**
	 * Modifies the amount of coins.
	 * 
	 * @param amount amount to modify by
	 */
	public void modCoins(Double amount) {
		coins+= amount;
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
		
		message = Colour.normal2 + "[" + Colour.normal1 + SettlementMessages.roledPlayer(this, sagaPlayer) + Colour.normal2 + "] " + message;
		
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

	
	
	// Timed:
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

		// Work:
		handleWork();
		
		// Collect:
		handleCollect();
		
		// Produce:
		handleProduction();
		
		// If can lavel:
		if(checkActiveMembers() && SettlementConfiguration.config().checkBuildingRequirements(this)){
		
			// Increase claims:
			if(claims < SettlementConfiguration.config().getMaxClaims()){
					
				claims+= SettlementConfiguration.config().getClaimsPerMinute(online);
				
				// Statistics:
				StatisticsManager.manager().addSettlementClaims(this, SettlementConfiguration.config().getClaimsPerMinute(online));
				
			}
			
			// Increase build points:
			if(buildPoints < SettlementConfiguration.config().getMaxBuildPoints()){
				
				buildPoints+= SettlementConfiguration.config().getBuildPointsPerMinute(online);
				
				// Statistics:
				StatisticsManager.manager().addSettlementBuildPoints(this, SettlementConfiguration.config().getBuildPointsPerMinute(online));
				
			}
			
		}
		
		return true;
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.DaytimeTicker#daytimeTick(org.saga.Clock.DaytimeTicker.Daytime)
	 */
	@Override
	public boolean daytimeTick(Daytime daytime) {
		
		// Handle wages:
		if(daytime == EconomyConfiguration.config().getSettlementWagesTime()) handleWages();
		
		return true;
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.DaytimeTicker#checkWorld(java.lang.String)
	 */
	@Override
	public boolean checkWorld(String worldName) {
		return worldName.equals(GeneralConfiguration.config().getDefaultWorld());
	}
	
	
	
	// Production:
	/**
	 * Handles work points tick.
	 * 
	 */
	public void handleWork() {


		// Increase work points:
		Hashtable<String, Double> onlineRolesTotals = new Hashtable<String, Double>();
		Collection<SagaPlayer> members = getOnlineMembers();
		for (SagaPlayer sagaPlayer : members) {
			
			Proficiency role = getRole(sagaPlayer.getName());
			if(role == null) continue;
			
			workPoints.put(role.getName(), getWorkPoints(role.getName()) + 1);
			
			Double total = onlineRolesTotals.get(role.getName());
			if(total == null) total = 0.0;
			onlineRolesTotals.put(role.getName(), total+1);
			
		}
		
		// Trim work points:
		Set<String> onlineRoles = onlineRolesTotals.keySet();
		for (String role : onlineRoles) {
			if(workPoints.get(role) > onlineRolesTotals.get(role)) workPoints.put(role, onlineRolesTotals.get(role));
		}
		
		// Distribute points:
		ArrayList<ProductionBuilding> prBuildings = getBuildings(ProductionBuilding.class);
		
		for (ProductionBuilding prBuilding : prBuildings) {
			
			prBuilding.work();
			
		}
		
		
	}
	
	/**
	 * Handles resource collection.
	 * 
	 */
	public void handleCollect() {

		ArrayList<ProductionBuilding> prBuildings = getBuildings(ProductionBuilding.class);
		
		for (ProductionBuilding prBuilding : prBuildings) {
			
			prBuilding.collect();
			
		}
		
	}
	
	/**
	 * Handles resource production:
	 * 
	 */
	public void handleProduction() {

		ArrayList<ProductionBuilding> prBuildings = getBuildings(ProductionBuilding.class);
		
		for (ProductionBuilding prBuilding : prBuildings) {
			
			prBuilding.produce();
			
		}
		
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
		ADD_COINS,
		OPEN_HOME_CHESTS,
		BUILD,
		BUILD_BUILDING,
		BUILDING_UPGRADE,
		BUY_CLAIMS,
		BUY_BUILD_POINTS,
		CLAIM,
		CLAIM_SETTLEMENT,
		CRUMBLE_ARENA_SETUP,
		ABANDON,
		DECLARE_OWNER,
		DISSOLVE,
		FLASH_CHUNK,
		INVITE,
		KICK,
		SET_ROLE,
		SET_BUILDING,
		REMOVE_BUILDING,
		REMOVE_COINS,
		RENAME,
		RESIGN,
		MEMBER_COMMAND,
		SPAWN,
		STORAGE_AREA_ADD,
		STORAGE_AREA_REMOVE,
		STORAGE_AREA_FLASH,
		
		// Farm:
		HURT_FARM_ANIMALS,
		
		// Home:
		REMOVE_RESIDENT,
		ADD_RESIDENT,
		
		// Trading post:
		MANAGE_EXPORT,
		MANAGE_IMPORT,
		MANAGE_SELL,
		MANAGE_BUY;
		
		
	}
	
	
}
