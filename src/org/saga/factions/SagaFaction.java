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
import org.saga.abilities.Mobilize.RallyPoint;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.config.ChunkGroupConfiguration;
import org.saga.config.FactionConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.ProficiencyConfiguration.InvalidProficiencyException;
import org.saga.constants.IOConstants.WriteReadType;
import org.saga.exceptions.InvalidLocationException;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.player.Proficiency;
import org.saga.player.SagaEntityDamageManager.SagaPvpEvent;
import org.saga.player.SagaEntityDamageManager.SagaPvpEvent.PvpDenyReason;
import org.saga.player.SagaPlayer;
import org.saga.utility.WriterReader;

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
	 * Faction registered chunk groups.
	 */
	transient private ArrayList<ChunkGroup> registeredChunkGroups = new ArrayList<ChunkGroup>();
	
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
	
	
	// Rally:
	/**
	 * Mobilization point, null if none.
	 */
	private RallyPoint rallyPoint;
	
	
	// Initialization:
	/**
	 * Used by gson.
	 * 
	 */
	private SagaFaction() {
	}
	
	/**
	 * Initializes.
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
		registeredChunkGroups = new ArrayList<ChunkGroup>();
		chunkGroupInvites = new ArrayList<Integer>();
		playerRanks = new Hashtable<String, Proficiency>();
		enemies = new HashSet<Integer>();
		allies = new HashSet<Integer>();
		allyRequests = new HashSet<Integer>();
		
		clockEnabled = false;
		
		removeOwner();
		
		
	}
	
	/**
	 * Completes the initialization.
	 * 
	 * @return integrity
	 */
	public boolean complete() {

		
		boolean integrity=true;
		
		String faction = id + "(" + name + ")";
		if(name == null){
			Saga.severe("Faction "+ faction +" name not initialized. Setting unnamed.");
			name= "unnamed";
			integrity = false;
		}
		
		if(id == null){
			Saga.severe("Faction "+ faction +" id not initialized. Setting -1.");
			id = -1;
			integrity = false;
		}
		
		if(members == null){
			Saga.severe("Faction "+ faction +" memberNames not initialized. Initializing empty list.");
			members = new ArrayList<String>();
			integrity = false;
		}
		
		if(primaryColor == null){
			Saga.severe("Faction "+ faction +" primaryColor not initialized. Setting white.");
			primaryColor = ChatColor.WHITE;
			integrity = false;
		}
		
		if(secondaryColor == null){
			Saga.severe("Faction "+ faction +" secondaryColor not initialized. Setting primaryColor.");
			secondaryColor = primaryColor;
			integrity = false;
		}
		
		if(chunkGroups == null){
			Saga.severe("Faction "+ faction +" chunkGroups not initialized. Setting empty list.");
			chunkGroups = new ArrayList<Integer>();
			integrity = false;
		}
		
		if(chunkGroupInvites == null){
			Saga.severe("Faction "+ faction +" chunkGroupInvites not initialized. Setting empty list.");
			chunkGroupInvites = new ArrayList<Integer>();
			integrity = false;
		}
		
		for (int i = 0; i < chunkGroupInvites.size(); i++) {
			if(chunkGroupInvites.get(i) == null){
				Saga.severe("Faction "+ faction +" chunkGroupInvites element not initialized. Removing element.");
				chunkGroupInvites.remove(i);
				i--;
			}
		}
		
		if(owner == null){
			Saga.severe(this, "failed to initialize owner field", "setting default");
			owner = "";
			integrity = false;
		}
		
		if(playerRanks == null){
			Saga.severe(this, "failed to initialize playerRanks field", "setting default");
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
				Saga.severe(this, "tried to add an invalid " + proficiency + " rank:" + e.getMessage(), "removing proficiency");
				playerRanks.remove(playerName);
			}
			catch (NullPointerException e) {
				Saga.severe(this, "tried to add a unitialized " + proficiency + " rank", "removing proficiency");
				playerRanks.remove(playerName);
			}
		}
		
		// Transient:
		definition = FactionConfiguration.config().factionDefinition;
		clockEnabled = false;
		
		// Rally:
		if(rallyPoint != null){
			
			try {
				integrity = rallyPoint.complete() && integrity;
				startClock();
			} catch (InvalidLocationException e) {
				Saga.severe(this, "invalid rally point " + rallyPoint, "removing mobilization point");
				removeRallyPoint();
			}
			
		}
		
		if(enemies == null){
			Saga.severe(this, "enemies field failed to initialize", "setting default");
			enemies = new HashSet<Integer>();
			integrity = false;
		}
		
		if(allies == null){
			Saga.severe(this, "allies field failed to initialize", "setting default");
			allies = new HashSet<Integer>();
			integrity = false;
		}
		
		if(allyRequests == null){
			Saga.severe(this, "allyRequests field failed to initialize", "setting default");
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
		Saga.info("Deleting " + getId() + "(" + getName() + ") faction.");
		
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
		WriterReader.deleteFaction(getId().toString());
		
		
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
		Saga.info("Creating " + faction + " faction.");
		
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
			Saga.severe(faction, "failed to set " + FactionConfiguration.config().factionOwnerRank + " rank, because the rank name is invalid", "ignoring request");
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
			Saga.severe(this, "tried to add an already existing member " + sagaPlayer.getName(), "ignoring request");
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
			Saga.severe(this, "failed to set " + FactionConfiguration.config().factionDefaultRank + " rank, because the rank name is invalid", "ignoring request");
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
			Saga.severe("Tried to remove a non-member " + sagaPlayer.getName() + " player from " + this +  "faction.");
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
			Saga.severe("Tried to remove a non-member " + playerName + " player from " + this +  "faction.");
			return;
		}
		
		// Force member:
		SagaPlayer factionMember;
		try {
			factionMember = Saga.plugin().forceSagaPlayer(playerName);
		} catch (NonExistantSagaPlayerException e) {
			Saga.severe(this, "could not remove " + playerName + " player, because the player doesent exist", "ignoring request");
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


		// Register rank:
		Proficiency rank = getRank(sagaPlayer.getName());
		if(rank != null) sagaPlayer.registerRank(rank);
		
		// Register player:
		if(registeredMembers.contains(sagaPlayer)){
			Saga.severe(this, "tried to register an already registered member " + sagaPlayer.getName(), "ignoring request");
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


		// Unregister rank:
		Proficiency rank = getRank(sagaPlayer.getName());
		if(rank != null) sagaPlayer.unregisterRank();
		
		// Unregister player:
		boolean removed = registeredMembers.remove(sagaPlayer);
		if(!removed){
			Saga.severe(this, "tried to unregister an non-registered member " + sagaPlayer.getName(), "ignoring request");
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
	
	
	// Chunk groups:
	/**
	 * Registers a chunk group.
	 * Will not add faction permanently to the player.
	 * 
	 * @param sagaSettlement saga faction
	 */
	public void registerChunkGroup2(ChunkGroup sagaSettlement) {
		
		
		// Check if already on the list:
		if(registeredChunkGroups.contains(sagaSettlement)){
			Saga.severe("Tried to register an already registered " + sagaSettlement.getId() + "(" + sagaSettlement.getName() + ") settlement" + ". Ignoring request.");
			return;
		}
		
		// Add:
		registeredChunkGroups.add(sagaSettlement);
		
		
	}
	
	/**
	 * Unregisters a chunk group.
	 * Will not remove faction permanently to the player.
	 * 
	 * @param sagaSettlement saga faction
	 */
	public void unregisterChunkGroup2(ChunkGroup sagaSettlement) {
		
		
		// Check if not on the list:
		if(!registeredChunkGroups.contains(sagaSettlement)){
			Saga.severe("Tried to unregister an non-registered " + sagaSettlement.getId() + "(" + sagaSettlement.getName() + ") settlement" + ". Ignoring request.");
			return;
		}
		
		// Remove:
		registeredChunkGroups.remove(sagaSettlement);
		
		
	}


	/**
	 * Gets the number of chunk groups.
	 * 
	 * @return chunk group count.
	 */
	public int getChunkGroupCount2() {
		return chunkGroups.size();
	}
	
	/**
	 * Gets the chunk group IDs.
	 * 
	 * @return the chunk group IDs
	 */
	public ArrayList<Integer> getChunkGroupIds2() {
		return chunkGroups;
	}

	/**
	 * Gets registered chunk groups.
	 * 
	 * @return the registered chunk groups
	 */
	public ArrayList<ChunkGroup> getRegisteredChunkGroups2() {
		return new ArrayList<ChunkGroup>(registeredChunkGroups);
	}

	
	// Invites:
	/**
	 * Adds an invitation to a chunk group.
	 * 
	 * @param id chunk group ID
	 */
	public void addChunkGroupInvitation2(Integer id) {

		
		if(chunkGroupInvites.contains(id)){
			Saga.severe("Tried to add an already existing " + id +" chunk group invite to "+ this +" faction. Ignoring request.");
			return;
		}
		chunkGroupInvites.add(id);
		
		
	}
	
	/**
	 * Removes an invitation to a chunk group.
	 * 
	 * @param id chunk group ID
	 */
	public void removeChunkGroupInvitation2(Integer id) {

		
		if(!chunkGroupInvites.contains(id)){
			Saga.severe("Tried to remove an non-existing " + id +" chunk group invite from "+ this +" faction. Ignoring request.");
			return;
		}
		chunkGroupInvites.remove(id);
		
		
	}
	
	/**
	 * Gets the chunkGroupInvites.
	 * 
	 * @return the chunkGroupInvites
	 */
	public ArrayList<Integer> getChunkGroupInvites2() {
		return chunkGroupInvites;
	}

	/**
	 * Checks if the faction has an invite to a chunk group.
	 * 
	 * @param id chunk group ID
	 * @return
	 */
	public boolean hasChunkGrouInvite2(Integer id) {
		return chunkGroupInvites.contains(id);
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
		Proficiency role = playerRanks.get(sagaPlayer.getName());
		if(role == null){
			return false;
		}
		
		// Check permission:
		return role.hasFactionPermission(FactionPermission.SET_RANK);
		
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
	 * Checks if the player can rally.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if can rally
	 */
	public boolean canRally(SagaPlayer sagaPlayer) {

		// Owner:
		if(isOwner(sagaPlayer.getName()) || sagaPlayer.isAdminMode()) return true;
		
		// Check rank:
		Proficiency role = playerRanks.get(sagaPlayer.getName());
		if(role == null){
			return false;
		}
		
		// Check permission:
		return role.hasFactionPermission(FactionPermission.RALLY);
		
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
	 * Gets the total amount of ranks with the given name
	 * 
	 * @param rankName rank name
	 * @return
	 */
	public Integer getTotalRanks(String rankName) {
		
		if(rankName.equals(FactionConfiguration.config().factionDefaultRank)){
			return getMemberCount() - getInactiveMemberCount();
		}
		
		return getDefinition().getTotalRanks( rankName, getLevel() );
		
	}

	/**
	 * Gets the amount of used ranks.
	 * 
	 * @param rankName rank name
	 * @return amount of used ranks
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
	 * Gets the amount of used ranks.
	 * 
	 * @param rankName rank name
	 * @return amount of used available ranks
	 */
	public Integer getAvailableRanks(String rankName) {
		
		return getTotalRanks(rankName) - getUsedRanks(rankName);
		
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
	 * Adds a rank to the player.
	 * 
	 * @param sagaPlayer saga player
	 * @param rankName rank name
	 * @throws InvalidProficiencyException thrown if the rank name is invalid
	 */
	public void setRank(SagaPlayer sagaPlayer, String rankName) throws InvalidProficiencyException {

		
		// Void rank:
		if(rankName.equals(ChunkGroupConfiguration.VOID_PROFICIENCY)){
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
		
		// Register:
		sagaPlayer.setRank(rank);
		
		
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
	
		// Unregister:
		sagaPlayer.clearRank();
		
			
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
		
		return getAvailableRanks(rankName) > 0;
		
	}
	
	/**
	 * Gets a player rank.
	 * 
	 * @param playerName player name
	 * @return player rank. null if none
	 */
	public Proficiency getRank(String playerName) {
		
		return playerRanks.get(playerName);
		
	}

	/**
	 * Gets the available ranks.
	 * 
	 * @return all available ranks
	 */
	public HashSet<String> getRanks() {
		
		HashSet<String> ranks = new HashSet<String>();
		
		// Add default role:
		ranks.add(FactionConfiguration.config().factionDefaultRank);
		
		// Add all roles:
		ranks.addAll(getDefinition().getAllRanks(getLevel()));
		
		return ranks;
		
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
	public void clockSecondTick() {
		
		
		boolean nextTick = false;
		
		// Mobilization point:
		if(rallyPoint != null && rallyPoint.getTimeRemaining() > 0){
			
			rallyPoint.decreaseTime();
			
			Integer time = rallyPoint.getTimeRemaining();
			
			// Expired inform:
			if(time == 0){
				broadcast(FactionMessages.mobilizationExpired(this));
			}
			// General inform:
			else if(time % 30 == 0){
			    broadcast(FactionMessages.mobilizationRemind(this, time));
			}
			// Last seconds inform:
			else if(time < 10 && (time == 5 || time == 3 || time == 2 || time == 1) ){
				broadcast(FactionMessages.mobilizationRemind(this, time));
			}
			
			if(rallyPoint.getTimeRemaining() > 0){
				nextTick = true;
			}else{
				removeRallyPoint();
			}
			
		}
		
		// Stop clock if last tick:
		if(!nextTick){
			stopClock();
		}
		
		
	}
	
	
	// Rally point:
	/**
	 * Removes the rally point.
	 * 
	 */
	public void removeRallyPoint() {
		rallyPoint = null;
	}
	
	/**
	 * Sets the rally point.
	 * 
	 * @param location location
	 * @param time time
	 */
	public void setRallyPoint(Location location, Integer time) {
		
		rallyPoint = new RallyPoint(location, time);
		
		// Start clock:
		if(!isClockEnabled()) startClock();
		
	}
	
	/**
	 * Gets the rally point.
	 * 
	 * @return rally point, null if none
	 */
	public RallyPoint getRallyPoint() {
		return rallyPoint;
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
		
		return FactionConfiguration.config().levelsPerActivePlayers.value(active.shortValue()).shortValue();
		
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
	 * @param factionId faction ID in String form
	 * @return saga faction
	 */
	public static SagaFaction load(String factionId) {

		
		// Load:
		String configName = "" + factionId + " faction";
		SagaFaction config;
		try {
			config = WriterReader.readFaction(factionId.toString());
		} catch (FileNotFoundException e) {
			Saga.info("Missing " + configName + ". Creating a new one.");
			config = new SagaFaction();
		} catch (IOException e) {
			Saga.severe("Failed to load " + configName + ". Loading defaults.");
			config = new SagaFaction();
			config.disableSaving();
		} catch (JsonParseException e) {
			Saga.severe("Failed to parse " + configName + ". Loading defaults.");
			Saga.info("Parse message :" + e.getMessage());
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
			Saga.warning("Saving disabled for "+ id + " (" +name + ") faction. Ignoring save request." );
			return;
		}
		
		String configName = "" + id + " faction";
		try {
			WriterReader.writeFaction(id.toString(), this, WriteReadType.FACTION_NORMAL);
		} catch (IOException e) {
			Saga.severe("Failed to write "+ configName +". Ignoring write.");
		}
		
		
	}
	
	
	// Control:
	/**
	 * Disables saving.
	 * 
	 */
	private void disableSaving() {

		Saga.warning("Disabling saving for "+ id + " (" +name + ") faction." );
		isSavingEnabled = false;
		// TODO Add notify for faction saving disabled.
	}
	

    // Damage events:
	/**
	 * Called when a member damages another player.
	 * 
	 * @param event event
	 */
	public void onMemberAttack(SagaPvpEvent event){
		
		
		// From faction:
		if(isMember(event.getSagaDefender().getName())){
			
			event.deny(PvpDenyReason.SAME_FACTION);
			
		}

		// Ally:
		if(isAlly(event.getSagaDefender().getFactionId())){

			event.deny(PvpDenyReason.ALLY);
			
		}

		
	}
	
	/**
	 * Called when a member is damaged by another player.
	 * 
	 * @param event event
	 */
	public void onMemberDefend(SagaPvpEvent event){
		

		// Ally:
		if(isAlly(event.getSagaAttacker().getFactionId())){
			
			event.deny(PvpDenyReason.ALLY);
			
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
		RALLY,
		RENAME,
		FORM_ALLIANCE,
		DECLINE_ALLIANCE,
		BREAK_ALLIANCE;
		
		
	}
	
	

	
	
}
