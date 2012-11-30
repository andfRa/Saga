package org.saga.factions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.saga.Clock;
import org.saga.Clock.DaytimeTicker;
import org.saga.Clock.MinuteTicker;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.config.EconomyConfiguration;
import org.saga.config.FactionConfiguration;
import org.saga.config.GeneralConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.ProficiencyConfiguration.InvalidProficiencyException;
import org.saga.dependencies.ChatDependency;
import org.saga.dependencies.EconomyDependency;
import org.saga.exceptions.InvalidLocationException;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.listeners.events.SagaEntityDamageEvent.PvPOverride;
import org.saga.messages.EconomyMessages;
import org.saga.messages.FactionMessages;
import org.saga.player.Proficiency;
import org.saga.player.ProficiencyDefinition;
import org.saga.player.SagaPlayer;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.settlements.Settlement;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.SagaLocation;

/**
 * @author andf
 *
 */
public class Faction implements MinuteTicker, DaytimeTicker{

	
	/**
	 * Faction ID.
	 * -1 if none.
	 */
	private Integer id;
	
	/**
	 * Faction name.
	 */
	private String name;

	/**
	 * Faction members.
	 */
	private HashSet<String> members;

	
	/**
	 * Settlement level.
	 */
	private Integer level;

	/**
	 * Available claims.
	 */
	private Double claims;
	
	
	/**
	 * Primary colour.
	 */
	private ChatColor colour1;
	
	/**
	 * Secondary colour.
	 */
	private ChatColor colour2;

	
	/**
	 * Faction owner.
	 */
	private String owner;
	
	
	/**
	 * Player roles.
	 */
	private Hashtable<String, Proficiency> playerRanks;


	/**
	 * Ally factions.
	 */
	private HashSet<Integer> allies;

	/**
	 * Ally invitations.
	 */
	private HashSet<Integer> allyRequests;
	
	
	/**
	 * Spawn point, null if none.
	 */
	private SagaLocation spawn;
	
	
	/**
	 * Daily kills.
	 */
	private HashSet<String> dailyKills;


	
	// Control:
	/**
	 * If true then saving is enabled.
	 */
	transient private boolean isSavingEnabled = true;

	/**
	 * True if enabled.
	 */
	transient private boolean enabled = false;
	
	
	
	// Initialisation:
	/**
	 * Used by gson.
	 * 
	 */
	private Faction() {
	}
	
	/**
	 * Creates a faction.
	 * 
	 * @param factionId faction ID
	 * @param factionName faction name
	 */
	public Faction(Integer factionId, String factionName) {
		
		
		this.id = factionId;
		this.name = factionName;
		members = new HashSet<String>();
		claims = FactionConfiguration.config().getInitialClaims().doubleValue();
		colour1 = ChatColor.WHITE;
		colour2 = ChatColor.WHITE;
		playerRanks = new Hashtable<String, Proficiency>();
		dailyKills = new HashSet<String>();
		allies = new HashSet<Integer>();
		allyRequests = new HashSet<Integer>();
		
		removeOwner();
		
		
	}
	
	/**
	 * Completes the initialisation.
	 * 
	 * @return integrity
	 */
	public boolean complete() {

		
		boolean integrity=true;
		
		if(name == null){
			SagaLogger.nullField(this, "name");
			name= "unnamed";
			integrity = false;
		}
		
		if(id == null){
			SagaLogger.nullField(this, "id");
			id = -1;
			integrity = false;
		}
		
		if(members == null){
			SagaLogger.nullField(this, "memberNames");
			members = new HashSet<String>();
			integrity = false;
		}

		if(claims == null){
			SagaLogger.nullField(this, "claims");
			double maxLevel = 50;
			if(level != null){
				claims = level.doubleValue() / maxLevel * FactionConfiguration.config().getMaxClaims();
			}else{
				claims = 0.0;
			}
			integrity = false;
		}
		
		if(colour1 == null){
			SagaLogger.nullField(this, "colour1");
			colour1 = ChatColor.WHITE;
			integrity = false;
		}
		
		if(colour2 == null){
			SagaLogger.nullField(this, "colour2");
			colour2 = colour1;
			integrity = false;
		}
		
		if(owner == null){
			SagaLogger.nullField(this, "owner");
			owner = "";
			integrity = false;
		}
		
		if(playerRanks == null){
			SagaLogger.nullField(this, "playerRanks");
			playerRanks = new Hashtable<String, Proficiency>();
			integrity = false;
		}
		
		Enumeration<String> playerNames = playerRanks.keys();
		while(playerNames.hasMoreElements()){
			String playerName = null;
			Proficiency proficiency = null;
			playerName = playerNames.nextElement();
			try {
				proficiency = playerRanks.get(playerName);
				proficiency.complete();
			} catch (InvalidProficiencyException e) {
				SagaLogger.severe(this, "tried to add an invalid " + proficiency + " rank:" + e.getMessage());
				playerRanks.remove(playerName);
			}
			catch (NullPointerException e) {
				SagaLogger.severe(this, "tried to add a unitialized " + proficiency + " rank");
				playerRanks.remove(playerName);
			}
		}
		
		if(spawn != null){
			
			try {
				integrity = spawn.complete() && integrity;
			} catch (InvalidLocationException e) {
				SagaLogger.severe(this, "invalid spawn point: " + spawn);
				removeSpawnPoint();
			}
			
		}
		
		if(dailyKills == null){
			SagaLogger.nullField(this, "dailyKills");
			dailyKills = new HashSet<String>();
			integrity = false;
		}
		
		if(allies == null){
			SagaLogger.nullField(this, "allies");
			allies = new HashSet<Integer>();
			integrity = false;
		}
		
		if(allyRequests == null){
			SagaLogger.nullField(this, "allyRequests");
			allyRequests = new HashSet<Integer>();
			integrity = false;
		}

		//Statistics:
		StatisticsManager.manager().setClaims(this);
		
		return integrity;
		
		
	}
	
	/**
	 * Deletes the faction.
	 * 
	 */
	public void delete() {

		
		// Log:
		SagaLogger.info("Deleting " + getId() + "(" + getName() + ") faction.");

		// Disable:
		disable();
		
		// Remove all members:
		ArrayList<String> playerNames = getMembers();
		for (int i = 0; i < playerNames.size(); i++) {
			removeMember(playerNames.get(i));
		}
		
		// Update faction manager:
		FactionManager.manager().removeFaction(this);
		
		// Save last time:
		save();
		
		// Remove from disc:
		WriterReader.delete(Directory.FACTION_DATA, getId().toString());
		
		
	}
	
	/**
	 * Creates the faction.
	 * 
	 * @param factionName name
	 * @param factionPrefix prefix
	 * @param owner owner
	 * @return
	 */
	public static Faction create(String factionName, SagaPlayer owner) {

		
		// Create:
		Faction faction = new Faction(FactionManager.manager().getUnusedFactoinId(), factionName);
		faction.complete();
		
		// Log:
		SagaLogger.info("Creating " + faction + " faction.");
		
		// Add the first member:
		faction.addMember(owner);
		
		// Update faction manager
		FactionManager.manager().addFaction(faction);
		
		// Set owner:
		faction.setOwner(owner.getName());
		
		// Save:
		faction.save();

//		// Set owner rank:
//		try {
//			Proficiency rank = ProficiencyConfiguration.config().createProficiency(FactionConfiguration.config().getOwnerRank());
//			faction.setRank(owner, rank);
//		} catch (InvalidProficiencyException e) {
//			SagaLogger.severe(faction, "failed to set " + FactionConfiguration.config().getOwnerRank() + " rank, because the rank name is invalid");
//		}
		
		// Enable:
		faction.enable();
		
		return faction;
		
		
	}
	

	/**
	 * Enables the faction.
	 * 
	 */
	public void enable() {

		
		// Clock:
		Clock.clock().enableMinuteTick(this);
		Clock.clock().enableDaytimeTicking(this);
		
		enabled = true;
		
		
	}

	/**
	 * Disables the faction.
	 * 
	 */
	public void disable() {
		
		enabled = false;
		
	}

	/**
	 * Checks if the faction is enabled.
	 * 
	 * @return true if enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	
	
	// Members:
	/**
	 * Adds a member.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void addMember(SagaPlayer sagaPlayer) {
		
		
		boolean formed = isFormed();
		
		// Check if already in this faction:
		if(members.contains(sagaPlayer.getName())) SagaLogger.severe(this, "tried to add an already existing member " + sagaPlayer.getName());
		
		
		// Add member:
		members.add(sagaPlayer.getName());
		
		// Set Id:
		sagaPlayer.setFactionId(getId());
		
		// Set default rank:
		try {
			Proficiency rank = ProficiencyConfiguration.config().createProficiency(FactionConfiguration.config().getDefaultRank());
			setRank(sagaPlayer, rank);
		} catch (InvalidProficiencyException e) {
			SagaLogger.severe(this, "failed to set " + FactionConfiguration.config().getDefaultRank() + " rank, because the rank name is invalid");
		}

    	// Update chat prefix:
    	if(isFormed() == formed){
    		ChatDependency.updatePrefix(sagaPlayer, this);
    	}else{
    		updatePrefix();
    	}
		
    	
	}
	
	/**
	 * Removes a member.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void removeMember(SagaPlayer sagaPlayer) {
		

		// Clear rank:
		clearRank(sagaPlayer);
		
		// Remove member:
		members.remove(sagaPlayer.getName());

		// Remove owner:
		if(isOwner(sagaPlayer.getName())){
			removeOwner();
		}
		
		// Remove faction:
		sagaPlayer.removeFactionId();

    	// Update chat prefix:
    	ChatDependency.updatePrefix(sagaPlayer, this);
    	
		
	}
	
	/**
	 * Removes a member.
	 * 
	 * @param playerName player name
	 */
	public final void removeMember(String playerName) {
		
		
		// Check if not in this faction:
		if(!members.contains(playerName)){
			SagaLogger.severe("Tried to remove a non-member " + playerName + " player");
			return;
		}
		
		// Force member:
		SagaPlayer selPlayer;
		try {
			selPlayer = Saga.plugin().forceSagaPlayer(playerName);
		} catch (NonExistantSagaPlayerException e) {
			SagaLogger.severe(this, "could not remove " + playerName + " player, because the player doesent exist");
			return;
		}
		
		// Remove:
		removeMember(selPlayer);
		
		// Release:
		selPlayer.indicateRelease();

		
	}
	
	/**
	 * Checks if the player is on the member list.
	 * 
	 * @param playerName player name
	 * @return true if on the list
	 */
	public boolean isMember(String playerName) {

		return members.contains(playerName);
		
	}
	

	/**
	 * Gets the members.
	 * 
	 * @return the members
	 */
	public ArrayList<String> getMembers() {
		return new ArrayList<String>(members);
	}
	
	/**
	 * Gets the member count.
	 * 
	 * @return amount of members
	 */
	public int getMemberCount() {
		return members.size();
	}


	/**
	 * Gets the active members.
	 * 
	 * @return the active members
	 */
	public ArrayList<String> getActiveMembers() {
		return new ArrayList<String>(members);
	}

	/**
	 * Gets the active member count.
	 * 
	 * @return amount of active members
	 */
	public int getActiveMemberCount() {
		return members.size();
	}
	
	/**
	 * Gets the inactive member count.
	 * 
	 * @return amount of inactive members
	 */
	public int getInactiveMemberCount() {
		return 0;
	}

	/**
	 * Check if the member is active.
	 * 
	 * @param name member name
	 * @return true if active
	 */
	public boolean isMemberActive(String name) {
		return true;
	}
	
	
	/**
	 * Gets the online members.
	 * 
	 * @return online members
	 */
	public Collection<SagaPlayer> getOnlineMembers() {
		
		
		Collection<SagaPlayer> onlinePlayers = Saga.plugin().getLoadedPlayers();
		Collection<SagaPlayer> onlineMembers = new HashSet<SagaPlayer>();
		
		for (SagaPlayer onlinePlayer : onlinePlayers) {
			
			if(isMember(onlinePlayer.getName())) onlineMembers.add(onlinePlayer);
			
		}
		
		return onlineMembers;
		
		
	}
	
	/**
	 * Checks if the member is registered.
	 * 
	 * @param playerName player name
	 * @return true if member is registered
	 */
	public boolean isMemberOnline(String playerName) {
		
		return isMember(playerName) && Saga.plugin().isSagaPlayerLoaded(playerName);

	}
	
	
	/**
	 * Checks if the faction is formed.
	 * 
	 * @return true if formed
	 */
	public boolean isFormed() {
		return getMemberCount() >= FactionConfiguration.config().formationAmount;
	}
	
	/**
	 * Matches a name to a members name.
	 * 
	 * @param name name
	 * @return matched name, same as given if not found
	 */
	public String matchName(String name) {
		
		ArrayList<String> members = getMembers();
		for (String memberName : members) {
			
			if(memberName.equalsIgnoreCase(name)) return memberName;

//			if(memberName.toLowerCase().contains(name)) name = memberName;
			
		}
		return name;

	}
	
	
	
	// Owner:
	/**
	 * Checks if the player counts as the owner of the settlement.
	 * 
	 * @param playerName player name
	 * @return true if owner
	 */
	public boolean isOwner(String playerName) {

		return owner.equalsIgnoreCase(playerName);
		
	}
	
	/**
	 * Sets an owner.
	 * 
	 * @param playerName player name
	 */
	public void setOwner(String playerName) {
		owner = playerName;
	}

	/**
	 * Removes an owner.
	 * 
	 * @param playerName player name
	 */
	public void removeOwner() {
		owner = "";
	}
	
	/**
	 * Gets the owner.
	 * 
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * Returns the owner count.
	 * 
	 * @return owner count
	 */
	public boolean hasOwner() {
		return !owner.equals("");
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
	 * Gets the amount of available claims.
	 * 
	 * @return amount of available claims
	 */
	public Integer getAvailableClaims() {
		return claims.intValue();
	}
	
	/**
	 * Gets the claim progress.
	 * 
	 * @return claim progress, values 0.0 - 1.0
	 */
	public Double getClaimProgress() {
		return claims - claims.intValue();
	}
	
	/**
	 * Gets the claimed settlement count.
	 * 
	 * @return claimed settlement count
	 */
	public int countClaimedSettles() {

		Settlement[] settlemenents = FactionClaimManager.manager().findSettlements(getId());
		return settlemenents.length;
		
		
	}
	
	/**
	 * Gets factions claim points.
	 * 
	 * @return factions claim points
	 */
	public Double calcClaimPoints() {


		Settlement[] claimedSettles = FactionClaimManager.manager().findSettlements(getId());
		Integer[] claimedSizes = FactionClaimManager.getSizes(claimedSettles);
		
		Double claimPts = 0.0;
		for (int i = 0; i < claimedSizes.length; i++) {
			
			claimPts+= FactionConfiguration.config().getClaimPoints(claimedSizes[i]); 
			
		}
		
		return claimPts;
		
		
	}
	
	
	
	// Naming:
	/**
	 * Gets the factionName.
	 * 
	 * @return the factionName
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the factionName.
	 * 
	 * @param factionName the factionName to set
	 */
	public void setName(String factionName) {
		
		this.name = factionName;
		
		updatePrefix();
		
	}

	/**
	 * Gets the factionId.
	 * 
	 * @return the factionId
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the factionId.
	 * 
	 * @param factionId the factionId to set
	 */
	void setId(Integer factionId) {
		this.id = factionId;
	}

	/**
	 * Updates all prefixes:
	 * 
	 */
	private void updatePrefix() {

		Collection<SagaPlayer> online = getOnlineMembers();
		
		for (SagaPlayer sagaPlayer : online) {
			ChatDependency.updatePrefix(sagaPlayer, this);
		}
		
	}
	
	
	
	// Colours:
	/**
	 * Gets the colour 1.
	 * 
	 * @return colour 1
	 */
	public ChatColor getColour1() {
		return colour1;
	}

	/**
	 * Gets the colour 2.
	 * 
	 * @return colour 2
	 */
	public ChatColor getColour2() {
		return colour2;
	}
	
	/**
	 * Sets the first colour.
	 * 
	 * @param colour1 colour
	 */
	public void setColor1(ChatColor colour1) {
		this.colour1 = colour1;
	}

	/**
	 * Sets the second colour.
	 * 
	 * @param colour1 colour
	 */
	public void setColor2(ChatColor colour2) {
		this.colour2 = colour2;
	}

	
	
	// Allies:
	/**
	 * Adds a faction ally.
	 * 
	 * @param id ally faction ID
	 * @return true, if the id was on the list
	 */
	public boolean addAlly(Integer id) {

		return allies.add(id);
		
	}
	
	/**
	 * Adds a faction ally.
	 * 
	 * @param id ally faction ID
	 * @return true, if the id was on the list
	 */
	public boolean removeAlly(Integer id) {

		return allies.remove(id);
		
	}
	
	/**
	 * Checks if the faction with the given ID is an ally.
	 * 
	 * @param id id
	 * @return true if ally
	 */
	public boolean isAlly(Integer id) {
		
		return allies.contains(id);
		
	}

	/**
	 * Checks if the faction is an ally.
	 * 
	 * @param faction saga faction
	 * @return true if ally
	 */
	public boolean isAlly(Faction faction) {
		
		return allies.contains(faction.getId());
		
	}
	
	/**
	 * Gets the allies.
	 * 
	 * @return the allies
	 */
	public HashSet<Integer> getAllies() {
		return new HashSet<Integer>(allies);
	}
	
	/**
	 * Gets the ally factions.
	 * 
	 * @return the ally factions
	 */
	public Collection<Faction> getAllyFactions() {
		
		return FactionManager.manager().getFactions(getAllies());
		
	}
	
	/**
	 * Gets ally invites.
	 * 
	 * @return ally invites
	 */
	public HashSet<Integer> getAllyInvites() {
		return new HashSet<Integer>(allyRequests);
	}
	
	/**
	 * Adds a faction alliance request.
	 * 
	 * @param id request faction ID
	 * @return true, if the request didn't have given request
	 */
	public boolean addAllianceRequest(Integer id) {

		return allyRequests.add(id);
		
	}
	
	/**
	 * Adds a faction alliance request.
	 * 
	 * @param id request faction ID
	 * @return true, if the request id was on the list
	 */
	public boolean removeAllianceRequest(Integer id) {

		return allyRequests.remove(id);
		
	}
	
	
	
	// Permissions:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.chunkGroups.ChunkBundle#hasPermisson(org.saga.player.SagaPlayer, org.saga.settlements.Settlement.SettlementPermission)
	 */
	public boolean hasPermission(SagaPlayer sagaPlayer, FactionPermission permission) {

		
		// Owner or admin:
		if(isOwner(sagaPlayer.getName()) || sagaPlayer.isAdminMode()) return true;
		
		// Check role:
		Proficiency rank = playerRanks.get(sagaPlayer.getName());
		if(rank == null){
			return false;
		}
		
		// Check permission:
		return rank.hasFactionPermission(permission);
		
		
	}
	
	
	
	// Ranks:
	/**
	 * Adds a rank to the player.
	 * 
	 * @param sagaPlayer saga player
	 * @param rank rank
	 */
	public void setRank(SagaPlayer sagaPlayer, Proficiency rank){

		
		// Clear previous rank:
		if( playerRanks.get(sagaPlayer.getName()) != null ){
			clearRank(sagaPlayer);
		}
		
		// Add to settlement:
		playerRanks.put(sagaPlayer.getName(), rank);
		
		// Update:
		sagaPlayer.update();
		
		
	}
	
	/**
	 * Clears a rank from the player.
	 * 
	 * @param sagaPlayer Saga player
	 */
	public void clearRank(SagaPlayer sagaPlayer) {
	

		// Check rank:
		Proficiency rank = playerRanks.get( sagaPlayer.getName() );
		if( rank == null ){
			return;
		}

		// Remove from faction:
		playerRanks.remove(sagaPlayer.getName());

		// Update:
		sagaPlayer.update();
		
			
	}
	
	/**
	 * Gets a player rank.
	 * 
	 * @param name player name
	 * @return player rank. null if none
	 */
	public Proficiency getRank(String name) {
		
		return playerRanks.get(name);
		
	}
	

	/**
	 * Gets the amount of ranks used.
	 * 
	 * @param rankName rank name
	 * @return amount of ranks used
	 */
	public Integer getUsedRanks(String rankName) {

		Integer total = 0;
		
		Collection<Proficiency> ranks = playerRanks.values();
		
		for (Proficiency rank : ranks) {
			
			if(rank.getName().equals(rankName)) total++;

		}
		
		return total;
		
	}
	
	/**
	 * Gets the amount of ranks available.
	 * 
	 * @param rankName rank name
	 * @return amount of ranks available
	 */
	public Integer getAvailableRanks(String rankName) {
		
		Hashtable<String, Double> ranks = FactionClaimManager.manager().getRanks(id);
		Double amount = ranks.get(rankName);
		if(amount == null) return 0;
		
		return amount.intValue();
		
	}

	/**
	 * Gets the amount of ranks remaining.
	 * 
	 * @param rankName rank name
	 * @return amount of remaining ranks
	 */
	public Integer getRemainingRanks(String rankName) {
		
		return getAvailableRanks(rankName) - getUsedRanks(rankName);
		
	}
	
	/**
	 * Checks if the given rank is available.
	 * 
	 * @param rankName rank name
	 * @return true if available
	 */
	public boolean isRankAvailable(String rankName) {
		
		ProficiencyDefinition rank = ProficiencyConfiguration.config().getDefinition(rankName);
		if(rank == null) return false;
		
		if(rank.getHierarchyLevel() == FactionConfiguration.config().getHierarchyMin()) return true;
		
		return getRemainingRanks(rankName) > 0;
		
	}
	
	
	/**
	 * Gets members with the given rank.
	 * 
	 * @param rankName rank name
	 * @return members with the given rank
	 */
	public ArrayList<String> getMembersForRanks(String rankName) {

		ArrayList<String> filtMembers = new ArrayList<String>();
		ArrayList<String> allMembers = getMembers();
		
		for (String member : allMembers) {
			
			Proficiency rank = getRank(member);
			if(rank != null && rank.getName().equals(rankName)) filtMembers.add(member);
			
		}
		
		return filtMembers;
		
		
	}
	
	
	
	// Clock:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.SecondTicker#clockSecondTick()
	 */
	@Override
	public boolean clockMinuteTick() {

		if(!isEnabled()) return false;
		
		int online = getOnlineMembers().size();
		
		// Statistics:
		StatisticsManager.manager().addManminutes(this, online);

		// Increase claims:
		if(claims < FactionConfiguration.config().getMaxClaims()){
			claims+= FactionConfiguration.config().getClaimsPerMinute(online);

			// Statistics:
			StatisticsManager.manager().addFactionClaims(this, FactionConfiguration.config().getClaimsPerMinute(online));
			
		}
		
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
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.DaytimeTicker#daytimeTick(org.saga.Clock.DaytimeTicker.Daytime)
	 */
	@Override
	public boolean daytimeTick(Daytime daytime) {
		
		if(!isEnabled()) return false;
		
		// Formed:
		if(!isFormed()) return true;
		
		// Reset kills:
		if(daytime == Daytime.SUNRISE) dailyKills = new HashSet<String>();
		
		// Wages:
		if(daytime == EconomyConfiguration.config().getFactionWagesTime()) handleWages();
		
		return true;
		
		
	}

	
	
	// Spawn:
	/**
	 * Removes the spawn point.
	 * 
	 */
	public void removeSpawnPoint() {
		spawn = null;
	}
	
	/**
	 * Sets the faction spawn.
	 * 
	 * @param location location
	 */
	public void setSpawn(Location location) {
		
		spawn = new SagaLocation(location);
		
	}
	
	/**
	 * Gets the rally point.
	 * 
	 * @return rally point, null if none
	 */
	public SagaLocation getSpawn() {
		return spawn;
	}
	
	
	
	// Wages:
	/**
	 * Pays all wages.
	 * 
	 */
	private void handleWages() {

		
		Hashtable<Integer, Double> wages = calcWages();
		Collection<SagaPlayer> onlineMembers = getOnlineMembers();
		
		// Play wages:
		for (SagaPlayer sagaPlayer : onlineMembers) {
			
			Proficiency rank = getRank(sagaPlayer.getName());
			if(rank == null) continue;
			
			Double wage = wages.get(rank.getHierarchy());
			if(wage == null || wage == 0) continue;
			
			EconomyDependency.addCoins(sagaPlayer, wage);
			
			information(EconomyMessages.gotPaid(this, wage), sagaPlayer);
			
			//Statistics:
			StatisticsManager.manager().addWages(this, rank, wage);
			
		}
		
		
	}
	
	/**
	 * Gets the wages for hierarchy levels.
	 * 
	 * @return wages hierarchy levels
	 */
	public Hashtable<Integer, Double> calcWages() {

		Double rawWage = EconomyConfiguration.config().calcWage(calcClaimPoints());
		return EconomyConfiguration.config().calcHierarchyWages(rawWage);
		
	}
	

	
	// Messages:
	/**
	 * Sends a chat message to all registered players.
	 * 
	 * @param sagaPlayer sender Saga player
	 * @param message message
	 */
	public void chat(SagaPlayer sagaPlayer, String message) {

		message = getColour2() + "[" + getColour1() + FactionMessages.rankedPlayer(this, sagaPlayer) + getColour2() + "] " + message;
		
		Collection<SagaPlayer> onlineMembers = getOnlineMembers();
		
		for (SagaPlayer onelineMember : onlineMembers) {
			onelineMember.message(message);
		}
		
	}
	
	/**
	 * Sends a faction message.
	 * 
	 * @param message message
	 */
	public void information(String message) {

		Collection<SagaPlayer> onlineMembers = getOnlineMembers();
		
		for (SagaPlayer onlineMember : onlineMembers) {
			information(message, onlineMember);
		}
		
	}
	
	/**
	 * Sends a faction message to a member.
	 * 
	 * @param message message
	 * @param target message target
	 */
	public void information(String message, SagaPlayer target) {
		
		message = getColour2() + "[" + getColour1() + "info" + getColour2() + "] " + message;

		target.message(message);
		
	}
	
	
	
	// Other:
	@Override
	public String toString() {
		return getId() + "(" + getName() + ")";
	}
	
	
	
	// Load save
	/**
	 * Loads and a faction from disc.
	 * 
	 * @param id faction ID in String form
	 * @return saga faction
	 */
	public static Faction load(String id) {

		
		// Load:
		Faction faction;
		try {
			
			faction = WriterReader.read(Directory.FACTION_DATA, id, Faction.class);
			
		} catch (FileNotFoundException e) {
			
			SagaLogger.info(Faction.class, "missing data for " + id + " ID");
			faction = new Faction();
			
		} catch (IOException e) {
			
			SagaLogger.severe(Faction.class, "failed to read data");
			faction = new Faction();
			faction.disableSaving();
			
		} catch (JsonParseException e) {
			
			SagaLogger.severe(Faction.class, "failed to parse data");
			SagaLogger.info("Parse message: " + e.getMessage());
			faction = new Faction();
			faction.disableSaving();
			
		}
		
		// Complete:
		faction.complete();

		// Enable:
		faction.enable();
		
		return faction;
		
		
	}
	
	/**
	 * Saves faction to disc.
	 * 
	 */
	public void save() {

		
		if(!isSavingEnabled){
			SagaLogger.warning(this, "saving disabled");
			return;
		}
		
		try {
			WriterReader.write(Directory.FACTION_DATA, id.toString(), this);
		} catch (IOException e) {
			SagaLogger.severe(this, "write failed: " + e.getClass().getSimpleName() + ":" + e.getMessage());
		}
		
		
	}
	
	
	
	// Control:
	/**
	 * Disables saving.
	 * 
	 */
	private void disableSaving() {

		SagaLogger.warning(this, "disabling saving");
		isSavingEnabled = false;
		// TODO Add notify for faction saving disabled.
	}
	

	
    // Events:
	/**
	 * Called when a member damages another player.
	 * 
	 * @param event event
	 */
	public void onPvPAttack(SagaEntityDamageEvent event){
		
		
		// Same faction:
		if(isMember(event.defenderPlayer.getName())){
			
			event.addPvpOverride(PvPOverride.SAME_FACTION_DENY);
			
		}

		// Ally:
		if(isAlly(event.defenderPlayer.getFactionId())){

			event.addPvpOverride(PvPOverride.ALLY_DENY);
			
		}

		
	}
	
	/**
	 * Called when a member is damaged by another player.
	 * 
	 * @param event event
	 */
	public void onPvPDefend(SagaEntityDamageEvent event){
		

		// Ally:
		if(isAlly(event.attackerPlayer.getFactionId())){
			
			event.addPvpOverride(PvPOverride.ALLY_DENY);
			
		}

		
	}

	/**
	 * Called when a player is killed by another player.
	 * 
	 * @param attacker player
	 * @param defender defender player
	 */
	public void onPvpKill(SagaPlayer attacker, SagaPlayer defender){

		
		// Formed:
		if(!isFormed()) return;
		
		// Only when other faction member is killed:
		Faction attackerFaction = attacker.getFaction();
		Faction defenderFaction = defender.getFaction();
		if(!(attackerFaction != null && attackerFaction == this && defenderFaction != attackerFaction)) return;
		
		// Daily kills:
		if(attackerFaction == this && defenderFaction != this){
			
			if(dailyKills.add(attacker.getName())){
				Double reward = EconomyConfiguration.config().getFactionKillReward(defender, defenderFaction);
				EconomyDependency.addCoins(attacker, reward);
				information(EconomyMessages.gotKillReward(attacker, defender, attackerFaction, reward));
				
				//Statistics:
				StatisticsManager.manager().addWages(this, reward);
				
				
			}
		
		}
		
		
	}
	

	
	// Types:
	/**
	 * Faction permissions.
	 * 
	 * @author andf
	 *
	 */
	public enum FactionPermission{

		
		DISBAND,
		DELETE,
		INVITE,
		KICK,
		QUIT,
		SET_RANK,
		SET_COLOR,
		SET_SPAWN,
		SPAWN,
		RENAME,
		FORM_ALLIANCE,
		DECLINE_ALLIANCE,
		BREAK_ALLIANCE,
		RESIGN,
		UNCLAIM;
		
		
	}
	
	
}
