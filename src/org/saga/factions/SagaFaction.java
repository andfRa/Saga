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
import org.saga.Clock;
import org.saga.Clock.SecondTicker;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.config.FactionConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.ProficiencyConfiguration.InvalidProficiencyException;
import org.saga.exceptions.InvalidLocationException;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.listeners.events.SagaEntityDamageEvent.PvPOverride;
import org.saga.player.Proficiency;
import org.saga.player.SagaPlayer;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.utility.SagaLocation;

import com.google.gson.JsonParseException;

/**
 * @author andf
 *
 */
public class SagaFaction implements SecondTicker{

	
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
	private ArrayList<String> members;
	
	/**
	 * Registered players.
	 */
	transient private ArrayList<SagaPlayer> registeredMembers = new ArrayList<SagaPlayer>();

	/**
	 * Primary color.
	 */
	private ChatColor primaryColor;
	
	/**
	 * Secondary color.
	 */
	private ChatColor secondaryColor;
	
	/**
	 * Chunk group IDs.
	 */
	private ArrayList<Integer> chunkGroups;
	
	/**
	 * Chunk group invites.
	 */
	private ArrayList<Integer> chunkGroupInvites;
	
	/**
	 * Faction owner.
	 */
	private String owner;
	
	/**
	 * Faction definition.
	 */
	private FactionDefinition definition;
	
	/**
	 * Player roles.
	 */
	private Hashtable<String, Proficiency> playerRanks;

	/**
	 * True if the clock is enabled.
	 */
	transient private boolean clockEnabled;
	
	/**
	 * Enemy factions.
	 */
	private HashSet<Integer> enemies;
	
	/**
	 * Ally factions.
	 */
	private HashSet<Integer> allies;

	/**
	 * Ally invitations.
	 */
	private HashSet<Integer> allyRequests;
	
	
	// Control:
	/**
	 * If true then saving is enabled.
	 */
	transient private boolean isSavingEnabled = true;
	
	
	// Spawn:
	/**
	 * Spawn point, null if none.
	 */
	private SagaLocation spawn;
	
	
	// Initialisation:
	/**
	 * Used by gson.
	 * 
	 */
	private SagaFaction() {
	}
	
	/**
	 * Creates a faction.
	 * 
	 * @param factionId faction ID
	 * @param factionName faction name
	 */
	public SagaFaction(Integer factionId, String factionName) {
		
		
		this.id = factionId;
		this.name = factionName;
		members = new ArrayList<String>();
		registeredMembers = new ArrayList<SagaPlayer>();
		primaryColor = ChatColor.WHITE;
		secondaryColor = ChatColor.WHITE;
		chunkGroups = new ArrayList<Integer>();
		chunkGroupInvites = new ArrayList<Integer>();
		playerRanks = new Hashtable<String, Proficiency>();
		enemies = new HashSet<Integer>();
		allies = new HashSet<Integer>();
		allyRequests = new HashSet<Integer>();
		
		clockEnabled = false;
		
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
			members = new ArrayList<String>();
			integrity = false;
		}
		
		if(primaryColor == null){
			SagaLogger.nullField(this, "primaryColor");
			primaryColor = ChatColor.WHITE;
			integrity = false;
		}
		
		if(secondaryColor == null){
			SagaLogger.nullField(this, "secondaryColor");
			secondaryColor = primaryColor;
			integrity = false;
		}
		
		if(chunkGroups == null){
			SagaLogger.nullField(this, "chunkGroups");
			chunkGroups = new ArrayList<Integer>();
			integrity = false;
		}
		
		if(chunkGroupInvites == null){
			SagaLogger.nullField(this, "chunkGroupInvites");
			chunkGroupInvites = new ArrayList<Integer>();
			integrity = false;
		}
		
		for (int i = 0; i < chunkGroupInvites.size(); i++) {
			if(chunkGroupInvites.get(i) == null){
				SagaLogger.nullField(this, "chunkGroupInvites element");
				chunkGroupInvites.remove(i);
				i--;
			}
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
		
		// Transient:
		definition = FactionConfiguration.config().factionDefinition;
		clockEnabled = false;
		
		if(spawn != null){
			
			try {
				integrity = spawn.complete() && integrity;
				startClock();
			} catch (InvalidLocationException e) {
				SagaLogger.severe(this, "invalid spawn point: " + spawn);
				removeRallyPoint();
			}
			
		}
		
		if(enemies == null){
			SagaLogger.nullField(this, "enemies");
			enemies = new HashSet<Integer>();
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
		
		return integrity;
		
		
	}
	
	/**
	 * Deletes the faction.
	 * 
	 */
	public void delete() {

		
		// Log:
		SagaLogger.info("Deleting " + getId() + "(" + getName() + ") faction.");
		
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
	public static SagaFaction create(String factionName, SagaPlayer owner) {

		
		// Create:
		SagaFaction faction = new SagaFaction(FactionManager.manager().getUnusedFactoinId(), factionName);
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
		
		// Set owners rank:
		try {
			faction.setRank(owner, FactionConfiguration.config().factionOwnerRank);
		} catch (InvalidProficiencyException e) {
			SagaLogger.severe(faction, "failed to set " + FactionConfiguration.config().factionOwnerRank + " rank, because the rank name is invalid");
		}
		
		
		return faction;
		
		
	}
	
	
	// Members:
	/**
	 * Adds a member.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void addMember(SagaPlayer sagaPlayer) {
		
		
		// Check if already in this faction:
		if(members.contains(sagaPlayer.getName())){
			SagaLogger.severe(this, "tried to add an already existing member " + sagaPlayer.getName());
			return;
		}
		
		// Add member:
		members.add(sagaPlayer.getName());
		
		// Add faction:
		sagaPlayer.setFactionId(this);
		
		// Register player:
		registerMember(sagaPlayer);


		// Set default rank:
		try {
			setRank(sagaPlayer, FactionConfiguration.config().factionDefaultRank);
		} catch (InvalidProficiencyException e) {
			SagaLogger.severe(this, "failed to set " + FactionConfiguration.config().factionDefaultRank + " rank, because the rank name is invalid");
		}
		
		
	}
	
	/**
	 * Removes a member.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void removeMember(SagaPlayer sagaPlayer) {
		
		
		// Check if not in this faction:
		if(!members.contains(sagaPlayer.getName())){
			SagaLogger.severe(this, "tried to remove a non-member " + sagaPlayer.getName() + " player");
			return;
		}

		// Clear rank:
		clearRank(sagaPlayer);
		
		// Remove member:
		members.remove(sagaPlayer.getName());

		// Remove owner:
		if(isOwner(sagaPlayer.getName())){
			removeOwner();
		}
		
		// Remove faction:
		sagaPlayer.removeFactionId(getId());
		
		// Unregister player:
		unregisterMember(sagaPlayer);

		
		
		// Remove and unregister player to chunk groups:
//		for (int i = 0; i < registeredChunkGroups.size(); i++) {
//			ChunkGroupManager.manager().removeFactionPlayer(sagaPlayer, registeredChunkGroups.get(i), this);
//			ChunkGroupManager.manager().unregisterFactionPlayer(sagaPlayer, registeredChunkGroups.get(i), this);
//		}
		
		
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
		SagaPlayer factionMember;
		try {
			factionMember = Saga.plugin().forceSagaPlayer(playerName);
		} catch (NonExistantSagaPlayerException e) {
			SagaLogger.severe(this, "could not remove " + playerName + " player, because the player doesent exist");
			return;
		}
		
		// Remove:
		removeMember(factionMember);
		
		// Unforce:
		Saga.plugin().unforceSagaPlayer(playerName);
		
		
	}
	
	/**
	 * Registers a member.
	 * Will not add player permanently to the faction list.
	 * Used by SagaPlayer to create a connection with the faction.
	 * Should not be used.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void registerMember(SagaPlayer sagaPlayer) {


		// Register player:
		if(registeredMembers.contains(sagaPlayer)){
			SagaLogger.severe(this, "tried to register an already registered member " + sagaPlayer.getName());
			return;
		}
		
		// Register player:
		registeredMembers.add(sagaPlayer);
		
		// Register faction:
		sagaPlayer.registerFaction(this);

		
	}
	
	/**
	 * Unregisters a member.
	 * Will not add player permanently to the faction list.
	 * Used by SagaPlayer to create a connection with the faction.
	 * Should not be used.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void unregisterMember(SagaPlayer sagaPlayer) {


		// Unregister player:
		boolean removed = registeredMembers.remove(sagaPlayer);
		if(!removed){
			SagaLogger.severe(this, "tried to unregister an non-registered member " + sagaPlayer.getName());
		}

		// Unregister player:
		registeredMembers.remove(sagaPlayer);
		
		// Unregister faction:
		sagaPlayer.unregisterFaction(this);
		
		
		
		// Unregister for all chunk groups:
//		for (int i = 0; i < registeredChunkGroups.size(); i++) {
//			ChunkGroupManager.manager().unregisterFactionPlayer(sagaPlayer, registeredChunkGroups.get(i), this);
//		}
		
		
	}
	
	
	/**
	 * Checks if the payer is registered.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if registered
	 */
	public boolean isRegisteredMember(SagaPlayer sagaPlayer) {

		return registeredMembers.contains(sagaPlayer);
		
	}
	
	/**
	 * Checks if the payer is registered.
	 * 
	 * @param playerName player name
	 * @return true if registered
	 */
	public boolean isRegisteredMember(String playerName) {

		for (int i = 0; i < registeredMembers.size(); i++) {
			if(registeredMembers.get(i).getName().equals(playerName)) return true;
		}
		
		return false;
		
	}
	
	
	/**
	 * Checks if the player is on the member list.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if on the list
	 */
	public boolean hasMember(SagaPlayer sagaPlayer) {

		return members.contains(sagaPlayer.getName());
		
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
	 * Gets the member count.
	 * 
	 * @return amount of members
	 */
	public int getMemberCount() {
		return members.size();
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
	 * Gets the registered member count.
	 * 
	 * @return amount of registered members
	 */
	public int getRegisteredMemberCount() {
		return registeredMembers.size();
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
	
	
	
	// Messages:
	/**
	 * Sends a faction message.
	 * 
	 * @param message message
	 */
	public void message(String message) {
		
		for (int i = 0; i < registeredMembers.size(); i++) {
			registeredMembers.get(i).message(message);
		}
		
	}
	
	
	// Interaction:
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
	 * Gets the primaryColor.
	 * 
	 * @return the primaryColor
	 */
	public ChatColor getPrimaryColor() {
		return primaryColor;
	}

	/**
	 * Gets the secondaryColor.
	 * 
	 * @return the secondaryColor
	 */
	public ChatColor getSecondaryColor() {
		return secondaryColor;
	}
	
	/**
	 * Sets the primaryColor.
	 * 
	 * @param primaryColor the primaryColor to set
	 */
	public void setPrimaryColor(ChatColor primaryColor) {
		this.primaryColor = primaryColor;
	}
	

	/**
	 * Sets the secondaryColor.
	 * 
	 * @param secondaryColor the secondaryColor to set
	 */
	public void setSecondaryColor(ChatColor secondaryColor) {
		this.secondaryColor = secondaryColor;
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
	 * Gets the active members.
	 * 
	 * @return the active members
	 */
	public ArrayList<String> getActiveMembers() {
		return new ArrayList<String>(members);
	}

	
	/**
	 * Gets all registered members.
	 * 
	 * @return registered members
	 */
	public ArrayList<SagaPlayer> getRegisteredMembers() {
		return registeredMembers;
	}
	
	/**
	 * Checks if the faction is formed.
	 * 
	 * @return true if formed
	 */
	public boolean isFormed() {
		return getMemberCount() >= FactionConfiguration.config().formationAmount;
	}
	
	
	// Allies enemies:
	/**
	 * Adds a faction enemy.
	 * 
	 * @param id enemy faction ID
	 * @return true, if the id was on the list
	 */
	public boolean addEnemy(Integer id) {

		return enemies.add(id);
		
	}
	
	/**
	 * Adds a faction enemy.
	 * 
	 * @param id enemy faction ID
	 * @return true, if the id was on the list
	 */
	public boolean removeEnemy(Integer id) {

		return enemies.remove(id);
		
	}
	
	/**
	 * Checks if the faction with the given ID is an enemy.
	 * 
	 * @param id id
	 * @return true if enemy
	 */
	public boolean isEnemy(Integer id) {
		
		return enemies.contains(id);
		
	}

	/**
	 * Gets the enemies.
	 * 
	 * @return the enemies
	 */
	public HashSet<Integer> getEnemies() {
		return new HashSet<Integer>(enemies);
	}
	
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
	 * @param sagaFaction saga faction
	 * @return true if ally
	 */
	public boolean isAlly(SagaFaction sagaFaction) {
		
		return allies.contains(sagaFaction.getId());
		
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
	public Collection<SagaFaction> getAllyFactions() {
		
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
	/**
	 * Checks if the player has permission to join the faction with a settlement.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can join with a settlement
	 */
	public boolean canJoinSettlement(SagaPlayer sagaPlayer) {
		return isOwner(sagaPlayer.getName()) || sagaPlayer.isAdminMode();
	}
	
	/**
	 * Checks if the player has permission to delete the faction.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can delete
	 */
	public boolean canDelete(SagaPlayer sagaPlayer) {
		return isOwner(sagaPlayer.getName()) || sagaPlayer.isAdminMode();
	}

	/**
	 * Checks if the player has permission to invite.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can invite
	 */
	public boolean canInvite(SagaPlayer sagaPlayer) {

		// Owner:
		if(isOwner(sagaPlayer.getName()) || sagaPlayer.isAdminMode()) return true;
		
		// Check rank:
		Proficiency rank = playerRanks.get(sagaPlayer.getName());
		if(rank == null){
			return false;
		}
		
		// Check permission:
		return rank.hasFactionPermission(FactionPermission.INVITE);
		
	}

	/**
	 * Checks if the player has permission to kick.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can kick
	 */
	public boolean canKick(SagaPlayer sagaPlayer) {

		// Owner:
		if(isOwner(sagaPlayer.getName()) || sagaPlayer.isAdminMode()) return true;
		
		// Check rank:
		Proficiency role = playerRanks.get(sagaPlayer.getName());
		if(role == null){
			return false;
		}
		
		// Check permission:
		return role.hasFactionPermission(FactionPermission.KICK);
		
	}

	/**
	 * Checks if the player can quit.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can quit
	 */
	public boolean canQuit(SagaPlayer sagaPlayer) {
		return true;
	}

	/**
	 * Checks if the player has permission to set color.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can set color
	 */
	public boolean canSetColor(SagaPlayer sagaPlayer) {

		// Owner:
		if(isOwner(sagaPlayer.getName()) || sagaPlayer.isAdminMode()) return true;
		
		// Check rank:
		Proficiency role = playerRanks.get(sagaPlayer.getName());
		if(role == null){
			return false;
		}
		
		// Check permission:
		return role.hasFactionPermission(FactionPermission.SET_COLOR);
		
	}

	/**
	 * Checks if the player has permission to rename.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can set color
	 */
	public boolean canRename(SagaPlayer sagaPlayer) {

		// Owner:
		if(isOwner(sagaPlayer.getName()) || sagaPlayer.isAdminMode()) return true;
		
		// Check rank:
		Proficiency role = playerRanks.get(sagaPlayer.getName());
		if(role == null){
			return false;
		}
		
		// Check permission:
		return role.hasFactionPermission(FactionPermission.RENAME);
		
	}

	/**
	 * Checks if the player has permission to form an alliance.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can form an alliance
	 */
	public boolean canFormAlliance(SagaPlayer sagaPlayer) {

		// Owner:
		if(isOwner(sagaPlayer.getName()) || sagaPlayer.isAdminMode()) return true;
		
		// Check rank:
		Proficiency role = playerRanks.get(sagaPlayer.getName());
		if(role == null){
			return false;
		}
		
		// Check permission:
		return role.hasFactionPermission(FactionPermission.FORM_ALLIANCE);
		
	}
	
	/**
	 * Checks if the player has permission to deline an alliance.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if has permission
	 */
	public boolean canDeclineAlliance(SagaPlayer sagaPlayer) {

		// Owner:
		if(isOwner(sagaPlayer.getName()) || sagaPlayer.isAdminMode()) return true;
		
		// Check rank:
		Proficiency role = playerRanks.get(sagaPlayer.getName());
		if(role == null){
			return false;
		}
		
		// Check permission:
		return role.hasFactionPermission(FactionPermission.DECLINE_ALLIANCE);
		
	}
	
	/**
	 * Checks if the player has permission to break an alliance.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can break an alliance
	 */
	public boolean canBreakAlliance(SagaPlayer sagaPlayer) {

		// Owner:
		if(isOwner(sagaPlayer.getName()) || sagaPlayer.isAdminMode()) return true;
		
		// Check rank:
		Proficiency role = playerRanks.get(sagaPlayer.getName());
		if(role == null){
			return false;
		}
		
		// Check permission:
		return role.hasFactionPermission(FactionPermission.BREAK_ALLIANCE);
		
	}
	
	

	/**
	 * Checks if the player has permission to invite.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can invite
	 */
	public boolean canSetRank(SagaPlayer sagaPlayer) {

		// Owner:
		if(isOwner(sagaPlayer.getName()) || sagaPlayer.isAdminMode()) return true;
		
		// Check rank:
		Proficiency rank = playerRanks.get(sagaPlayer.getName());
		if(rank == null){
			return false;
		}
		
		// Check permission:
		return rank.hasFactionPermission(FactionPermission.SET_RANK);
		
	}


	/**
	 * Checks if the player can declare an owner.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can declare owner
	 */
	public boolean canDeclareOwner(SagaPlayer sagaPlayer) {

		// Owner:
		if(isOwner(sagaPlayer.getName()) || sagaPlayer.isAdminMode()) return true;
		
		// No owner:
		return !hasOwner() && (isMember(sagaPlayer.getName()) || getActiveMemberCount() == 0);
		
	}

	/**
	 * Checks if the player can set spawn.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can set spawn
	 */
	public boolean canSetSpawn(SagaPlayer sagaPlayer) {

		// Owner:
		if(isOwner(sagaPlayer.getName()) || sagaPlayer.isAdminMode()) return true;
		
		// Check rank:
		Proficiency role = playerRanks.get(sagaPlayer.getName());
		if(role == null){
			return false;
		}
		
		// Check permission:
		return role.hasFactionPermission(FactionPermission.SET_SPAWN);
		
	}

	/**
	 * Checks if the player can spawn.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can spawn
	 */
	public boolean canSpawn(SagaPlayer sagaPlayer) {

		// Owner:
		if(isOwner(sagaPlayer.getName()) || sagaPlayer.isAdminMode()) return true;
		
		// Check permission:
		return isMember(sagaPlayer.getName());
		
	}
	
	
	// Ranks:
	/**
	 * Gets the definition.
	 * 
	 * @return definition
	 */
	public FactionDefinition getDefinition() {
		return definition;
	}
	
	
	/**
	 * Gets available ranks
	 * 
	 * @return available ranks
	 */
	public HashSet<String> getAllRanks() {
		
		HashSet<String> enabledRanks = getDefinition().getAllRanks(getLevel());
		return enabledRanks;
		
	}

	/**
	 * Gets the available ranks.
	 * 
	 * @return all available ranks
	 */
	public HashSet<String> getRanks() {
		
		HashSet<String> ranks = new HashSet<String>();
		
		// Add default rank:
		ranks.add(FactionConfiguration.config().factionDefaultRank);
		
		// Add all ranks:
		ranks.addAll(getDefinition().getAllRanks(getLevel()));
		
		return ranks;
		
	}
	
	
	/**
	 * Adds a rank to the player.
	 * 
	 * @param sagaPlayer saga player
	 * @param rankName rank name
	 * @throws InvalidProficiencyException thrown if the rank name is invalid
	 */
	public void setRank(SagaPlayer sagaPlayer, String rankName) throws InvalidProficiencyException {

		
		// Void rank:
		if(rankName.equals("")){
			return;
		}
		
		// Clear previous rank:
		if( playerRanks.get(sagaPlayer.getName()) != null ){
			clearRank(sagaPlayer);
		}
		
		// Create rank:
		Proficiency rank = ProficiencyConfiguration.config().createProficiency(rankName);

		// Add to settlement:
		playerRanks.put(sagaPlayer.getName(), rank);
		
		
	}
	
	/**
	 * Clears a rank from the player.
	 * 
	 * @param sagaPlayer sga player
	 */
	public void clearRank(SagaPlayer sagaPlayer) {
	

		// Check rank:
		Proficiency rank = playerRanks.get( sagaPlayer.getName() );
		if( rank == null ){
			return;
		}

		// Remove from faction:
		playerRanks.remove(sagaPlayer.getName());
	
			
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
		Enumeration<String> players = playerRanks.keys();
		while (players.hasMoreElements()) {
			
			String player = players.nextElement();
			Proficiency rank = playerRanks.get(player);
			
			if(rankName.equals(rank.getName()))total ++;
			
		}
 		
		return total;
		
	}
	
	/**
	 * Gets the amount of ranks available
	 * 
	 * @param rankName rank name
	 * @return amont of ranks available
	 */
	public Integer getAvailableRanks(String rankName) {
		
		if(rankName.equals(FactionConfiguration.config().factionDefaultRank)){
			return getMemberCount() - getInactiveMemberCount();
		}
		
		return getDefinition().getAvailableRanks( rankName, getLevel() );
		
	}

	/**
	 * Gets the amount of ranks remaining.
	 * 
	 * @param rankName rank name
	 * @return amount of ranks remaining
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
		
		if(rankName.equals(FactionConfiguration.config().factionDefaultRank)){
			return true;
		}
		
		return getRemainingRanks(rankName) > 0;
		
	}

	
	
	// Clock:
	/**
	 * Starts the clock.
	 * 
	 */
	private void startClock() {
		
		Clock.clock().registerSecondTick(this);
		
		clockEnabled = true;
		
	}
	
	/**
	 * Stops the clock.
	 * 
	 */
	private void stopClock() {
		
		Clock.clock().unregisterSecondTick(this);
		
		clockEnabled = false;
		
	}
	
	/**
	 * Checks if the clock is enables.
	 * 
	 * @return true if enabled
	 */
	public boolean isClockEnabled() {
		return clockEnabled;
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.SecondTicker#clockSecondTick()
	 */
	@Override
	public boolean clockSecondTick() {
		
		
		boolean nextTick = false;
		
		
		// Stop clock if last tick:
		if(!nextTick){
			stopClock();
		}
		
		return true;
		
	}
	
	
	// Rally point:
	/**
	 * Removes the rally point.
	 * 
	 */
	public void removeRallyPoint() {
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
	
	
	// Settling:
	/**
	 * Gets faction level.
	 * Calculated from member count divided 0.8.
	 * 
	 */
	public Short getLevel() {
		
		Integer active = getActiveMemberCount();
		
		if(active < FactionConfiguration.config().levelsPerActivePlayers.getXMin()){
			return 0;
		}
		
		return FactionConfiguration.config().levelsPerActivePlayers.value(active).shortValue();
		
	}

	/**
	 * Sends a message to all registered players.
	 * 
	 * @param message message
	 */
	public void broadcast(String message) {

		
		for (int i = 0; i < registeredMembers.size(); i++) {
			registeredMembers.get(i).message(message);
		}
		
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
	public static SagaFaction load(String id) {

		
		// Load:
		SagaFaction config;
		try {
			
			config = WriterReader.read(Directory.FACTION_DATA, id, SagaFaction.class);
			
		} catch (FileNotFoundException e) {
			
			SagaLogger.info(SagaFaction.class, "missing data for " + id + " ID");
			config = new SagaFaction();
			
		} catch (IOException e) {
			
			SagaLogger.severe(SagaFaction.class, "failed to read data");
			config = new SagaFaction();
			config.disableSaving();
			
		} catch (JsonParseException e) {
			
			SagaLogger.severe(SagaFaction.class, "failed to parse data");
			SagaLogger.info("Parse message: " + e.getMessage());
			config = new SagaFaction();
			config.disableSaving();
			
		}
		
		// Complete:
		config.complete();
		
		return config;
		
		
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
	

    // Damage events:
	/**
	 * Called when a member damages another player.
	 * 
	 * @param event event
	 */
	public void onAttack(SagaEntityDamageEvent event){
		
		
		// Same faction:
		if(isMember(event.getDefenderPlayer().getName())){
			
			event.addPvpOverride(PvPOverride.SAME_FACTION_DENY);
			
		}

		// Ally:
		if(isAlly(event.getDefenderPlayer().getFactionId())){

			event.addPvpOverride(PvPOverride.ALLY_DENY);
			
		}

		
	}
	
	/**
	 * Called when a member is damaged by another player.
	 * 
	 * @param event event
	 */
	public void onDefend(SagaEntityDamageEvent event){
		

		// Ally:
		if(isAlly(event.getAttackerPlayer().getFactionId())){
			
			event.addPvpOverride(PvPOverride.ALLY_DENY);
			
		}

		
	}

	
	
	/**
	 * Faction permissions.
	 * 
	 * @author andf
	 *
	 */
	public enum FactionPermission{

		
		DISBAND,
		INVITE,
		KICK,
		SET_RANK,
		SET_COLOR,
		@Deprecated
		RALLY,
		SET_SPAWN,
		RENAME,
		FORM_ALLIANCE,
		DECLINE_ALLIANCE,
		BREAK_ALLIANCE;
		
		
	}
	
	

	
	
}
