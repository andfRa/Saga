package org.saga.player;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.saga.Clock;
import org.saga.Clock.SecondTicker;
import org.saga.SagaLogger;
import org.saga.abilities.Ability;
import org.saga.abilities.AbilityManager;
import org.saga.attributes.AttributeManager;
import org.saga.chunks.ChunkBundle;
import org.saga.chunks.ChunkBundleManager;
import org.saga.chunks.SagaChunk;
import org.saga.config.AbilityConfiguration;
import org.saga.config.AttributeConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.config.ExperienceConfiguration;
import org.saga.dependencies.PermissionsManager;
import org.saga.economy.EconomyManager.TransactionType;
import org.saga.economy.InventoryUtil;
import org.saga.economy.TradeDeal;
import org.saga.economy.Trader;
import org.saga.exceptions.InvalidAbilityException;
import org.saga.factions.Faction;
import org.saga.messages.GeneralMessages.CustomColour;
import org.saga.messages.PlayerMessages;
import org.saga.messages.StatsMessages;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.settlements.Settlement;
import org.saga.shape.RelativeShape.Orientation;

import com.google.gson.JsonParseException;

public class SagaPlayer implements SecondTicker, Trader{

	
	// Wrapped:
	/**
	 * Minecraft player.
	 */
	transient private Player player;
	
	
	// Player information:
	/**
	 * Name.
	 */
	private String name;
	
	/**
	 * Level.
	 */
	private Integer level;

	/**
	 * Experience.
	 */
	private Double exp;
	
	/**
	 * Amount of coins the player has.
	 */
	private Double coins;

	
	// Attributes:
	/**
	 * Attribute scores.
	 */
	private Hashtable<String, Integer> attributeScores;
	
	
	// Abilities:
	/**
	 * All abilities.
	 */
	private ArrayList<Ability> abilities;
	
	
	// Managers:
	/**
	 * Ability manager.
	 */
	transient private AbilityManager abilityManager;
	
	/**
	 * Attribute manager.
	 */
	transient private AttributeManager attributeManager;
	
	
	// Faction:
	/**
	 * Player factions ID.
	 */
	private Integer factionId;
	
	/**
	 * Registered factions.
	 */
	transient private Faction faction;
	
	
	// Chunk group:
	/**
	 * Player chunk group IDs.
	 */
	private Integer chunkGroupId;

	/**
	 * All registered chunk groups.
	 */
	transient private ChunkBundle chunkBundle = null;
	
	
	// Location:
	/**
	 * Last chunk the player was on.
	 */
	transient public SagaChunk lastSagaChunk = null;
	
	
	// Invites:
	/**
	 * Invites to chunk groups.
	 */
	private ArrayList<Integer> chunkGroupInvites;
	
	/**
	 * Invites to factions.
	 */
	private ArrayList<Integer> factionInvites;
	
	
	// Guardian stone:
	/**
	 * All guardian stone.
	 */
	private GuardianRune guardRune;
	
	
	// Admin mode:
	/**
	 * Administration mode.
	 */
	private Boolean adminMode;
	
	
	// Control:
	/**
	 * Disables and enables player information saving.
	 */
	transient private boolean isSavingEnabled=true;
	
	/**
	 * Forced level. If above zero, the player can't be unforced.
	 */
	transient private int forcedLevel = 0;

	/**
	 * Clock is enabled if true.
	 */
	transient private boolean clockEnabled;
	

	
	
	// Loading and initialisation:
	/**
	 * Used by gson loader.
	 */
	private SagaPlayer() {
		
		
		
	}
	
	/**
	 * Creates a saga player.
	 * 
	 * @param name name
	 */
	private SagaPlayer(String name) {
		
		this.name = name;
		this.level = 0;
		this.exp = 0.0;
		this.factionId = -1;
		this.chunkGroupId = -1;
		this.factionInvites = new ArrayList<Integer>();
		this.chunkGroupInvites = new ArrayList<Integer>();
		this.coins = EconomyConfiguration.config().playerCoins;
		this.guardRune = new GuardianRune(this);
		this.abilities = new ArrayList<Ability>();
		syncAbilities();
		this.attributeScores = new Hashtable<String, Integer>();
		this.abilityManager = new AbilityManager(this);
		this.attributeManager = new AttributeManager(this);
		
	}
	
	/**
	 * Goes trough all the fields and makes sure everything has been set after gson load.
	 * If not, it fills the field with defaults.
	 */
	public void complete() {

		
		// Fields:
		if(name == null){
			name = "none";
		}
		
		if(level == null){
			level = 0;
			SagaLogger.nullField(this, "level");
		}
		
		if(exp == null){
			exp = 0.0;
			SagaLogger.nullField(this, "level");
		}
		
		if(coins == null){
			coins = EconomyConfiguration.config().playerCoins;
			SagaLogger.nullField(this, "coins");
		}
		
		if(factionId == null){
			factionId = -1;
			SagaLogger.nullField(this, "stamina");
		}
		
		if(chunkGroupId == null){
			chunkGroupId = -1;
			SagaLogger.nullField(this, "chunkGroupId");
		}
		
		if(factionInvites == null){
			factionInvites = new ArrayList<Integer>();
			SagaLogger.nullField(this, "factionInvites");
		}
	
		if(chunkGroupInvites == null){
			chunkGroupInvites = new ArrayList<Integer>();
			SagaLogger.nullField(this, "chunkGroupInvites");
		}
		
		if(guardRune == null){
			guardRune = new GuardianRune(this);
			SagaLogger.nullField(this, "guardRune");
		}
		guardRune.complete();
		
		// Abilities:
		if(abilities == null){
			abilities = new ArrayList<Ability>();
			SagaLogger.nullField(this, "abilities");
		}
		
		for (int i = 0; i < abilities.size(); i++) {
			
			Ability ability = abilities.get(i);
			
			if(ability == null){
				SagaLogger.nullField(this, "abilities element");
				abilities.remove(i);
				i--;
				continue;
			}
			
			try {
				
				ability.setPlayer(this);
				ability.complete();
				
			} catch (InvalidAbilityException e) {
				SagaLogger.severe(this, "abilities element invalid: " + e.getMessage());
				abilities.remove(i);
				i--;
				continue;
			}
			
		}

		syncAbilities();
		
		if(attributeScores == null){
			attributeScores = new Hashtable<String, Integer>();
			SagaLogger.nullField(this, "attributeScores");
		}
		
		// Transient:
		this.clockEnabled = false;
		this.attributeManager = new AttributeManager(this);
		this.abilityManager = new AbilityManager(this);
		
		
	}

	
	// Updating:
	/**
	 * Updates everything.
	 * 
	 */
	public void update() {
		abilityManager.update();
	}
	
	
	
	// Attributes:
	/**
	 * Gets the ability manager.
	 * 
	 * @return ability manager
	 */
	public AbilityManager getAbilityManager() {
		return abilityManager;
	}
	
	/**
	 * Gets the attribute manager.
	 * 
	 * @return attribute manager
	 */
	public AttributeManager getAttributeManager() {
		return attributeManager;
	}
	
	/**
	 * Gets the score for the given attribute.
	 * 
	 * @param attrName attribute name
	 * @return attribute score
	 */
	public Integer getRawAttributeScore(String attrName) {

		Integer score = attributeScores.get(attrName);
		if(score == null) return 0;
		return score;

	}
	
	/**
	 * Gets the score for the given attribute. Includes bonuses.
	 * 
	 * @param attrName attribute name
	 * @return attribute score
	 */
	public Integer getAttributeScore(String attrName) {

		
		Integer score = attributeScores.get(attrName);
		if(score == null) score = 0;
		
		return score + getAttrScoreBonus(attrName);

		
	}

	/**
	 * Gets the bonus for the given attribute.
	 * 
	 * @param name attribute name
	 * @return attribute bonus
	 */
	public Integer getAttrScoreBonus(String attrName) {

		
		Integer bonus = 0;
		Proficiency prof = null;
		
		// Ask role:
		prof = getRole();
		if(prof != null){
			bonus+= prof.getDefinition().getAttributeBonus(attrName);
		}
		
		// Ask rank:
		prof = getRank();
		if(prof != null){
			bonus+= prof.getDefinition().getAttributeBonus(attrName);
		}
		
		return bonus;
		
		
	}
	
	
	/**
	 * Gets the used attribute points.
	 * 
	 * @return total attribute points
	 */
	public Integer getUsedAttributePoints() {

		
		Collection<Integer> attrVals = attributeScores.values();
		Integer total = 0;
		for (Integer score : attrVals) {
			total += score;
		}
		
		return total;
				
		
	}

	/**
	 * Gets the available attribute points.
	 * 
	 * @return available attribute points
	 */
	public Integer getAvailableAttributePoints() {

		return AttributeConfiguration.config().getAttributePoints(getLevel());
		
	}
	
	/**
	 * Gets the remaining attribute points.
	 * 
	 * @return remaining attribute points
	 */
	public Integer getRemainingAttributePoints() {

		return getAvailableAttributePoints() - getUsedAttributePoints();
		
	}
	
	/**
	 * Sets attribute score.
	 * 
	 * @param attribute attribute name
	 * @param score score
	 */
	public void setAttributeScore(String attribute, Integer score) {
		this.attributeScores.put(attribute, score);
		abilityManager.update();
	}

	
	
	
	// Abilities:
	/**
	 * Gets an ability with the given name.
	 * 
	 * @param name ability name
	 * @return ability, null if none
	 */
	public Ability getAbility(String name) {

		
		HashSet<Ability> abilities = getAbilities();
		
		for (Ability ability : abilities) {
			if(ability.getName().equals(name)) return ability;
		}
		
		return null;
		
		
	}
	
	/**
	 * Gets the score for the giver ability. Includes bonuses.
	 * 
	 * @param name ability name
	 * @return ability score
	 */
	public Integer getAbilityScore(String name) {

		
		Ability ability = getAbility(name);
		if(ability == null) return 0;
		
		return ability.getDefinition().getScore(this);

		
	}
	
	/**
	 * Checks if the player has an ability.
	 * 
	 * @param name ability name
	 * @return true if the player has the ability
	 */
	public boolean hasAbility(String name) {
		return getAbility(name) != null;	
	}
	
	/**
	 * Gets abilities.
	 * 
	 * @return abilities
	 */
	public HashSet<Ability> getAbilities() {
		return new HashSet<Ability>(abilities);
	}

	/**
	 * Creates and adds all missing abilities.
	 * 
	 */
	private void syncAbilities() {

		
		ArrayList<String> abilityNames = AbilityConfiguration.config().getAbilityNames();
		for (String abilityName : abilityNames) {
			if(!hasAbility(abilityName)){
				
				try {
					Ability ability = AbilityConfiguration.createAbility(abilityName);
					ability.setPlayer(this);
					abilities.add(ability);
				}
				catch (InvalidAbilityException e) {
					SagaLogger.severe(this, "failed to create ability: " + e.getClass().getSimpleName() + ":" + e.getMessage());
				}
				
			}
		}
		

	}

	
	
	// Player:
	/**
	 * Returns player name.
	 * 
	 * @return player name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the player and changes status to online.
	 * Marks the saga player an online player.
	 * 
	 * @param player player
	 */
	public void setPlayer(Player player) {
		
		this.player = player;
		
		// Admin mode:
		if(isAdminMode() && !PermissionsManager.hasPermission(player, PermissionsManager.ADMIN_MODE_PERMISSION)){
			disableAdminMode();
			SagaLogger.info(this, "no permission for admin mode");
		}
		
		// Saving disabled:
		if(!isSavingEnabled){
			error("player information saving disabled");
		}

		// Start clock:
		if(proceedClock() && !isClockEnabled()) enableClock();
		
		
	}
	
	/**
	 * Sets the player and changes status to offline.
	 * 
	 */
	public void removePlayer() {

		
		// Admin mode:
		if(isAdminMode() && !PermissionsManager.hasPermission(player, PermissionsManager.ADMIN_MODE_PERMISSION)){
			disableAdminMode();
			SagaLogger.info(this, "no permission for admin mode");
		}
		
		this.player = null;
		lastSagaChunk = null;

		
	}
	
	/**
	 * Gets the player.
	 * 
	 * @return player, null if not online
	 */
	public Player getPlayer() {
		return player;
	}
	
	
	// Level:
	/**
	 * Gets player level.
	 * 
	 * @return 0 if not online or nor a player
	 */
	public Integer getLevel() {
		
		return level;
		
	}
	
	/**
	 * Sets player level.
	 * 
	 * @param level level
	 */
	public void setLevel(int level) {
		
		this.level = level;
		
		// Update managers:
		abilityManager.update();
		
	}
	
	/**
	 * Levels up the player.
	 * 
	 */
	public void levelUp() {
		
		setLevel(level + 1);
		
		message(StatsMessages.levelup(getLevel()));
		
	}

	/**
	 * Decreases the player level.
	 * 
	 * @param amount amount to decrease
	 */
	public void decreaseLevel(Integer amount) {

		
		if(!isOnline()) return;
		
		setLevel(getLevel() - amount);
		
		
	}
	

	// Experience:
	/**
	 * Gets player experience.
	 * 
	 * @return player experience
	 */
	public Double getExp() {
		
		return exp;
		
	}
	
	/**
	 * Gets the remaining experience.
	 * 
	 * @return remaining experience
	 */
	public Double getRemainingExp() {
		
		return ExperienceConfiguration.config().getLevelExp(getLevel()).doubleValue() - getExp();
		
	}

	/**
	 * Awards player experience.
	 * 
	 * @param amount amount of exp
	 */
	public void awardExp(Double amount) {
		
		if(level > ExperienceConfiguration.config().getMaxLevel()) return;
		
		this.exp += amount;
		
		if(this.exp >= ExperienceConfiguration.config().getLevelExp(getLevel())){
			levelUp();
		}
		
	}
	
	
	
	
	// Items:
	/**
	 * Gets player armor.
	 * 
	 * @return player armor, no idea if nulls
	 */
	public ItemStack[] getArmour() {

		
		if(!isOnline()) return new ItemStack[0];
		
		return player.getInventory().getArmorContents();
		
		
	}
	
	/**
	 * Gets player item in hand.
	 * 
	 * @return player item in hand air if not online or none
	 */
	public ItemStack getItemInHand() {

		
		if(!isOnline()) return new ItemStack(Material.AIR);
		
		return player.getItemInHand();
		
		
	}
	
	/**
	 * Gets player item in hand.
	 * 
	 * @return player item in hand air if not online or none
	 */
	public int getInventorySize() {

		
		if(!isOnline()) return 0;
		
		return player.getInventory().getSize();
		
		
	}
	
	/**
	 * Gets player item.
	 * 
	 * @return player item in hand air if not online or none
	 */
	public ItemStack getInventoryItem(int index) {

		
		if(!isOnline()) return new ItemStack(Material.AIR);
		
		return player.getInventory().getItem(index);
		
		
	}
	
	/**
	 * Remove player item.
	 * 
	 */
	@SuppressWarnings("deprecation")
	public void removeInventoryItem(int index) {

		
		if(!isOnline()) return;
		
		player.getInventory().clear(index);
		
		player.updateInventory();
		
		
	}
	
	/**
	 * Removed item in hand.
	 * 
	 * @return removed item stack
	 */
	@SuppressWarnings("deprecation")
	public ItemStack removeItemInHand() {

		if(isOnline()){
			ItemStack itemInHand = player.getInventory().getItemInHand();
			player.getInventory().clear(player.getInventory().getHeldItemSlot());
			player.updateInventory();
			return itemInHand;
		}
		return new ItemStack(Material.AIR, 0);
		
		
	}

	/**
	 * Damages item in hand.
	 * 
	 */
	@SuppressWarnings("deprecation")
	public void damageTool() {

		
		if(!isOnline()) return;
		
		ItemStack item = getPlayer().getItemInHand();

		if(item == null || item.getType().getMaxDurability() <= 0 ) return;
			
		item.setDurability((short) (item.getDurability() + 1));

		getPlayer().updateInventory();
		
		
	}
	
	
	
	
	// Faction:
	/**
	 * Registers a faction.
	 * 
	 * @param saga saga faction
	 */
	public void registerFaction(Faction saga) {
		
		
		// Check if already registered:
		if(this.faction != null){
			SagaLogger.severe(this, "tried to register a second " + saga + " faction");
			return;
		}
		
		// Register:
		this.faction = saga;
		
		
	}
	
	/**
	 * Unregisters a faction.
	 * 
	 * @param faction saga faction
	 */
	public void unregisterFaction(Faction faction) {
		
		
		// Check if not registered:
		if(this.faction == null){
			SagaLogger.severe(this, "tried to unregister a non-registered " + faction + " faction");
			return;
		}

		// Check if not a correct faction:
		if(this.faction != faction){
			SagaLogger.severe(this, "tried to unregister an invalid " + faction + " faction");
			return;
		}
		
		// Remove:
		this.faction = null;
		
		
	}
	
	/**
	 * Gets the faction id.
	 * 
	 * @return factions ID, -1 if none
	 */
	public Integer getFactionId() {
		return factionId;
	}
	
	/**
	 * Gets the players faction.
	 * 
	 * @return players faction, null if none
	 */
	public Faction getFaction() {
		return faction;
	}

	/**
	 * Sets a faction ID for the player.
	 * 
	 * @param faction saga faction
	 */
	public void setFactionId(Faction faction) {
		factionId = faction.getId();
	}

	/**
	 * Removes a faction ID from the player.
	 * 
	 * @param id faction id
	 */
	public void removeFactionId(Integer id) {
		factionId = -1;
	}


	/**
	 * Checks if the player is part of the faction.
	 * 
	 * @param faction faction
	 * @return true if part of the faction
	 */
	public boolean hasFaction(Faction faction) {
		
		return faction.equals(faction.getId());
		
	}

	
	/**
	 * Checks if the player has a faction.
	 * 
	 * @return true if the player has a faction
	 */
	public boolean hasFaction() {
		return !factionId.equals(-1);
	}
	
	
	
	
	// Chunk group:
	/**
	 * Sets a chunk bundle ID to the player.
	 * 
	 * @param chunkBundleId chunk group ID
	 */
	public void setChunkBundleId(Integer chunkBundleId) {
		this.chunkGroupId = chunkBundleId;
	}

	/**
	 * Removes a chunk bundle ID from the player.
	 * 
	 * @param chunkBundleId chunk group ID
	 */
	public void removeChunkBundleId(Integer chunkBundleId) {
		this.chunkGroupId = -1;
	}
	
	/**
	 * Gets the chunk bundle ID.
	 * 
	 * @return the chunk bundle ID
	 */
	public Integer getChunkBundleId() {
		return chunkGroupId;
	}
	
	/**
	 * Registers a chunk group.
	 * Will not add faction permanently to the player.
	 * 
	 * @param chunkBundle saga chunk group
	 */
	public void registerChunkBundle(ChunkBundle chunkBundle) {
		
		
		// Check if already on the list:
		if(this.chunkBundle != null){
			SagaLogger.severe(this, "tried to register a second chunk group");
			return;
		}
		
		// Add:
		this.chunkBundle = chunkBundle;
		
		
	}
	
	/**
	 * Unregisters a chunk group.
	 * Will not remove faction permanently to the player.
	 * 
	 * @param chunkBundle saga chunk group
	 */
	public void unregisterChunkBundle(ChunkBundle chunkBundle) {
		
		
		// Check if not on the list:
		if(this.chunkBundle == null){
			SagaLogger.severe(this, "tried to unregister a non-registered chunk group");
			return;
		}
		
		// Remove:
		this.chunkBundle = null;
		
		
	}

	/**
	 * Gets the registered chunk group.
	 * 
	 * @return the registered chunk group, null if none
	 */
	public ChunkBundle getChunkBundle() {
		return chunkBundle;
	}

	/**
	 * Checks if the player has a chunk groups.
	 * 
	 * @return true if the player has chunk groups
	 */
	public boolean hasChunkBundle() {
		return !chunkGroupId.equals(-1);
	}

	
	
	// Faction invitations:
	/**
	 * Adds a chunk group invite.
	 * 
	 * @param factionId faction ID
	 */
	public void addFactionInvite(Integer factionId) {
		

		// Ignore invite if already exists:
		if(factionInvites.contains(factionId)){
			return;
		}
		
		// Add invite:
		factionInvites.add(factionId);
		
		
	}
	
	/**
	 * Removes a faction invite.
	 * 
	 * @param factionId faction ID
	 */
	public void removeFactionInvite(Integer factionId) {
		

		// Ignore invite if doesn't exists:
		if(!factionInvites.contains(factionId)){
			return;
		}

		// Remove invite:
		factionInvites.remove(factionId);
		
		
	}
	
	/**
	 * Gets faction invites.
	 * 
	 * @return faction invites
	 */
	public ArrayList<Integer> getFactionInvites() {
		return new ArrayList<Integer>(factionInvites);
	}

	/**
	 * Checks if the player has an invite.
	 * 
	 * @param id ID
	 * @return true if has an invite
	 */
	public boolean hasFactionInvite(Integer id){
		return factionInvites.contains(id);
	}
	
	
	
	
	// Chunk group invites:
	/**
	 * Adds a chunk group invite.
	 * 
	 * @param groupId chunk group ID
	 */
	public void addChunkGroupInvite(Integer groupId) {
		
		
		// Ignore invite if already exists:
		if(chunkGroupInvites.contains(groupId)){
			return;
		}
		
		// Add invite:
		chunkGroupInvites.add(groupId);
		
		
	}
	
	/**
	 * Removes a chunk group invite.
	 * 
	 * @param chunkGroupId chunk group ID
	 */
	public void removeChunkGroupInvite(Integer chunkGroupId) {
		
		
		// Ignore invite if doesn't exists:
		if(!chunkGroupInvites.contains(chunkGroupId)){
			return;
		}
		
		// Remove invite:
		chunkGroupInvites.remove(chunkGroupId);
		
		
	}
	
	/**
	 * Gets chunk group invites.
	 * 
	 * @return chunk group invites
	 */
	public ArrayList<Integer> getChunkGroupInvites() {
		return new ArrayList<Integer>(chunkGroupInvites);
	}

	/**
	 * Checks if the player has an invite.
	 * 
	 * @param id ID
	 * @return true if has an invite
	 */
	public boolean hasChunkGroupInvite(Integer id){
		return chunkGroupInvites.contains(id);
	}

	
	/**
	 * Refreshes last saga chunk.
	 * 
	 */
	public void refreshChunk() {
		
		
		if(!isOnline()){
			return;
		}
		
		lastSagaChunk = ChunkBundleManager.manager().getSagaChunk(player.getLocation());
		
		
	}
	
	
	// Rank and role:
	/**
	 * Gets player rank.
	 * 
	 * @return player rank, null if none
	 */
	public Proficiency getRank() {

		if(faction == null) return null;
		return faction.getRank(getName());

	}
	
	/**
	 * Gets player role.
	 * 
	 * @return player role, null if none
	 */
	public Proficiency getRole() {

		if(chunkBundle == null) return null;
		
		if(chunkBundle instanceof Settlement){
			return ((Settlement) chunkBundle).getRole(getName());
		}else{
			return null;
		}
		
	}
	
	
	// Messages:
	/**
	 * Sends the player a message.
	 * 
	 * @param message message
	 */
	public void message(String message) {
		
		if(message.length() == 0) return;
		
		if(player != null){
        	player.sendMessage(CustomColour.processMessage(message));
        }
          
	}

	/**
	 * Sends an error message.
	 * 
	 * @param message message
	 */
	public void error(String message) {
		
		message(ChatColor.DARK_RED + message);
          
	}

	
	
	
	// Teleport:
	/**
	 * Moves the player to the given location.
	 * Must be used when the teleport is part of an ability.
	 * 
	 * @param location location
	 */
	public void teleport(Location location) {

		if(isOnline()){
        	player.teleport(location);
        }
		
	}
	
	/**
	 * Puts a player on the given blocks centre.
	 * 
	 * @param locationBlock block the player will be placed on
	 */
	public void teleportCentered(Block locationBlock) {
		
		Location location = locationBlock.getRelative(BlockFace.UP).getLocation();
		
		teleport(location.add(0.5, 0, 0.5));
		
	}

	
	
	
	// Shapes:
	/**
	 * Gets player orientation.
	 * 
	 * @return orientation, {@link Orientation#NORTH} if not online
	 */
	public Orientation getOrientation(){
		
		
		if(!isOnline()){
			return Orientation.NORTH;
		}
		
		Location playerLocation = player.getEyeLocation();
		double yaw = playerLocation.getYaw();
		
		if( (yaw >= 315.0 && yaw <= 360) || (yaw >= 0 && yaw <= 45.0) || (yaw <= 0 && yaw >= -45.0) || (yaw <= -315 && yaw >= -360.0) ){
			
			return Orientation.WEST;
			
		}
		if( (yaw >= 45.0 && yaw <= 135.0) || (yaw >= -315.0 && yaw <= -225.0) ){
			
			return Orientation.NORTH;
			
		}
		if( (yaw >= 135.0 && yaw <= 225.0) || (yaw >= -225.0 && yaw <= -135.0) ){
			
			return Orientation.EAST;
			
		}if( (yaw >= 225.0 && yaw <= 315.0) || (yaw >= -135.0 && yaw <= -45.0) ){
			
			return Orientation.SOUTH;
			
		}
		
		return Orientation.NORTH;

		
	}

	
	
	
	// Ability usage:
	/**
	 * Shoots a fireball.
	 * 
	 * @param accuracy accuracy. Can be in the range 0-10
	 */
	public void shootFireball(Double speed) {

		
		// Ignore if the player isn't online:
		if(!isOnline()){
			return;
		}
		
		// Shooter:
		Location shootLocation = player.getEyeLocation();

		// Direction vector:
		Vector directionVector = shootLocation.getDirection().normalize();
		
		// Shoot shift vector:
		double startShift = 2;
		Vector shootShiftVector = new Vector(directionVector.getX() * startShift, directionVector.getY() * startShift, directionVector.getZ() * startShift);
		
		// Shift shoot location:
		shootLocation = shootLocation.add(shootShiftVector.getX(), shootShiftVector.getY(), shootShiftVector.getZ());
		
		// Create the fireball:
		Fireball fireballl = shootLocation.getWorld().spawn(shootLocation, Fireball.class);
		fireballl.setVelocity(directionVector.multiply(speed));
		
		// Remove fire:
		if(fireballl instanceof Fireball){
			((Fireball) fireballl).setIsIncendiary(false);
		}
		
	}
	
	/**
	 * Shoots a fireball.
	 * 
	 * @param speed speed
	 * @param shootLocation shoot location
	 */
	public void shootFireball(Double speed, Location shootLocation) {

		
		// Ignore if the player isn't online:
		if(!isOnline()){
			return;
		}
		
		// Direction vector:
		Vector directionVector = shootLocation.getDirection().normalize();
		
		// Create the fireball:
		Fireball fireball = shootLocation.getWorld().spawn(shootLocation, Fireball.class);
		fireball.setVelocity(directionVector.multiply(speed));
		
		// Set shooter:
		fireball.setShooter(player);
		
		// Remove fire:
		if(fireball instanceof Fireball){
			((Fireball) fireball).setIsIncendiary(false);
		}
		
	}
	
	/**
	 * Shoots an arrow.
	 * 
	 * @param speed arrow speed
	 */
	public void shootArrow(double speed) {

		
		// Ignore if the player isn't online:
		if(!isOnline()){
			return;
		}
		
		Arrow arrow = player.launchProjectile(Arrow.class);
		
		// Velocity vector:
		Vector velocity = arrow.getVelocity().clone();
		velocity.normalize().multiply(speed);
		
		// Set velocity:
		arrow.setVelocity(velocity);
		
		// Play effect:
		player.getLocation().getWorld().playEffect(getLocation(), Effect.BOW_FIRE, 0);
		
		
	}

	/**
	 * Pushes away an entity from the player
	 * 
	 * @param entity entity
	 * @param speed speed
	 */
	public void pushAwayEntity2(Entity entity, double speed) {

		
		// Ignore if the player isn't online:
		if(!isOnline()){
			return;
		}
		
		// Get velocity unit vector:
		Vector unitVector = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
		
		// Set speed and push entity:
		entity.setVelocity(unitVector.multiply(speed));
		
		
	}
	
	
	
	
	// Effects:
	/**
	 * Plays an effect.
	 * 
	 * @param effect effect
	 * @param value effect value
	 */
	public void playEffect(Effect effect, int value) {

		
		if(!isOnline()) return;
		
		player.playEffect(getLocation(), effect, value);
		
		
	}
	
	/**
	 * Plays an effect.
	 * 
	 * @param effect effect
	 * @param value effect value
	 */
	public void playGlobalEffect(Effect effect, int value) {

		
		if(!isOnline()) return;
		
		player.getLocation().getWorld().playEffect(getLocation(), effect, value);
		
		
	}
	
	/**
	 * Plays an effect.
	 * 
	 * @param effect effect
	 * @param value effect value
	 * @param location location
	 */
	public void playGlobalEffect(Effect effect, int value, Location location) {

		
		location.getWorld().playEffect(location, effect, value);
		
		
	}
	
	
	
	
	// Entities:
	/**
	 * Returns the player distance to a location
	 * 
	 * @param location location
	 * @return distance. 0 if player not online
	 */
	public Double getDistance(Location location) {
		
		
		// Zero loaction if the player isn't online:
		if(!isOnline()){
			return 0.0;
		}
		
		return player.getLocation().distance(location);

		
	}
	
	/**
	 * Gets nearby entities
	 * 
	 * @param x x radius
	 * @param y y radius
	 * @param z z radius
	 * @return nearby entities. emplty if player isn't online.
	 */
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		
		
		// Ignore if the player isn't online:
		if(!isOnline()){
			return new ArrayList<Entity>();
		}
		
		return player.getNearbyEntities(x, y, z);

		
	}

	
	// Saga chunk:
	/**
	 * Gets the Saga chunk the player is in.
	 * 
	 * @return Saga chunk, null if not found
	 */
	public SagaChunk getSagaChunk(){
		
		
		if(!isOnline()){
			return null;
		}
		
		Location location = player.getLocation();
		
		if(lastSagaChunk != null && lastSagaChunk.represents(location)){
			return lastSagaChunk;
		}
		
		return ChunkBundleManager.manager().getSagaChunk(location);
		
				
	}

	/**
	 * Gets the player location.
	 * 
	 * @return player location, null if not online
	 */
	public Location getLocation() {

		if(!isOnline()){
			return null;
		}
		return player.getLocation();
		
	}

	
	// Guard rune:
	/**
	 * Gets the guardRune.
	 * 
	 * @return the guardRune
	 */
	public GuardianRune getGuardRune() {
		return guardRune;
	}	
	
	
	// Trader:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#getTradingName()
	 */
	@Override
	public String getTradingName() {
		return getName();
	}


	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#isActive(saga.economy.EconomyManager.TransactionType, org.bukkit.Material)
	 */
	@Override
	public boolean isActive(TransactionType type, Material material) {
		return isOnline();
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Transaction.Trader#addCurrency(java.lang.Double)
	 */
	@Override
	public void addCoins(Double amount) {
		coins += amount;
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Transaction.Trader#removeCurrency(java.lang.Double)
	 */
	@Override
	public void removeCoins(Double amount) {
		coins -= amount;
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Transaction.Trader#getCurrency()
	 */
	@Override
	public Double getCoins() {
		return (double)coins.intValue();
	}
	
	/**
	 * Sets coins.
	 * 
	 * @param coins currency
	 */
	public void setCoins(Double coins) {
		this.coins = coins;
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#addItem(org.bukkit.inventory.ItemStack)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void addItem(ItemStack itemStack) {
		
		if(isOnline()){
			
			boolean dropped = InventoryUtil.addItem(itemStack, player.getInventory(), player.getLocation());
			
			if(dropped){
				message(PlayerMessages.inventoryFullDropping());
			}
			
			player.updateInventory(); // TODO replace updateInventory()
			
		}
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#removeItemStack(org.bukkit.inventory.ItemStack)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void removeItem(ItemStack itemStack) {
		
		if(isOnline()){
			
			InventoryUtil.removeItem(itemStack, player.getInventory());
			
			player.updateInventory(); // TODO replace updateInventory()
			
		}
		
	}
	
	/**
	 * Removes items from a player.
	 * 
	 * @param material material
	 * @param amount amount
	 */
	@SuppressWarnings("deprecation")
	public void removeItem(Material material, int amount) {
		
		if(isOnline()){
			
			InventoryUtil.removeItem(material, amount, false, player);
			
			player.updateInventory(); // TODO replace updateInventory()
			
		}
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#getItemCount(org.bukkit.Material)
	 */
	@Override
	public Integer getAmount(Material item) {
		
		if(isOnline()){
			
			return InventoryUtil.getItemCount(item, player.getInventory().getContents());
			
		}
		return 0;
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.economy.Trader#getSellPrice(org.bukkit.Material)
	 */
	@Override
	public Double getSellPrice(Material material) {
		return 100000.0;
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.economy.Trader#getBuyPrice(org.bukkit.Material)
	 */
	@Override
	public Double getBuyPrice(Material material) {
		return 100000.0;
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#getTradeDeals()
	 */
	@Override
	public ArrayList<TradeDeal> getDeals() {
		return new ArrayList<TradeDeal>();
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.economy.Trader#notifyTransaction()
	 */
	@Override
	public void notifyTransaction() {
	
	
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	// Clock:
	/**
	 * Sends a clock tick.
	 *
	 * @param tick tick number
	 */
	@Override
	public boolean clockSecondTick() {
		
		return proceedClock();
		
	}

	/**
	 * Enables the clock.
	 * 
	 */
	private void enableClock() {

		Clock.clock().registerSecondTick(this);
		
		clockEnabled = true;
		
	}
	
	/**
	 * Checks if clock is enabled.
	 * 
	 * @return true if enabled.
	 */
	public boolean isClockEnabled() {
		return clockEnabled;
	}

	/**
	 * Checks if the clock should proceed.
	 * 
	 * @return true if should proceed
	 */
	private boolean proceedClock() {

		
//		if(!guardRune.isEmpty() && player != null) return true;
		
		return false;
		
		
	}
	

	
	
	// Saving and loading:
	/**
	 * Loads a offline saga player.
	 * 
	 * @param playerName player name
	 * @param player minecraft player
	 * @param plugin plugin instance for access
	 * @param balanceInformation balance information
	 * @return saga player
	 */
	public static SagaPlayer load(String playerName){
		
		
            // Try loading:
            SagaPlayer sagaPlayer;
            
            // New players:
            if(!WriterReader.checkExists(Directory.PLAYER_DATA, playerName)){
            	
            	sagaPlayer = new SagaPlayer(playerName);
            	sagaPlayer.save();
            	return sagaPlayer;
            	
            }
            
            // Try loading:
            try {

                sagaPlayer = WriterReader.read(Directory.PLAYER_DATA, playerName.toLowerCase(), SagaPlayer.class);

                // Complete:
                sagaPlayer.complete();

                
            } catch (FileNotFoundException e) {

            	SagaLogger.info("Player information file not found for " + playerName + ". Loading default.");
                sagaPlayer = new SagaPlayer(playerName);

            } catch (IOException e) {

            	SagaLogger.severe(SagaPlayer.class, "player information file read failure for " + playerName + ":" + e.getClass().getSimpleName() + ":" + e.getMessage());
            	SagaLogger.info("disabling player information saving");
                sagaPlayer= new SagaPlayer(playerName);
                sagaPlayer.setSavingEnabled(false);

            } catch (JsonParseException e) {

            	SagaLogger.severe(SagaPlayer.class, "player information parse load failure for " + playerName + ":" + e.getClass().getSimpleName() + ":" + e.getMessage());
            	SagaLogger.info("disabling player information saving");
            	SagaLogger.info("Parse message: " + e.getMessage());
                sagaPlayer= new SagaPlayer(playerName);
                sagaPlayer.setSavingEnabled(false);

            }

            return sagaPlayer;
				

	}
	
	/**
	 * Unloads player information.
	 */
	public void unload() {
		
		save();
		
	}

	/**
	 * Saves player information.
	 */
	public void save() {
		
		
            if( isSavingEnabled() ) {

                try {
                    WriterReader.write(Directory.PLAYER_DATA, getName().toLowerCase(), this);
                } catch (Throwable e) {
                    SagaLogger.severe(this, "player information save failure:" + e.getClass().getSimpleName() + ":" + e.getMessage());
                }

            } else {
            	SagaLogger.severe(this, "player information save denied");
            }

		
	}
	
	/**
	 * Checks if the player exists.
	 * 
	 * @param name player name
	 * @return true if exists
	 */
	public static boolean checkExists(String name){
		return WriterReader.checkExists(Directory.PLAYER_DATA, name);
	}

	
	
	
	// Control:
	/**
	 * True if the player is online.
	 * 
	 * @return true if the player is online
	 */
	public boolean isOnline() {
		return player != null;
	}
	
	/**
	 * True if the player information should be saved.
	 * 
	 * @return true if player information should be saved
	 */
	public boolean isSavingEnabled() {
            return isSavingEnabled;
	}
	
	/**
	 * Disables or enables player information saving.
	 * 
	 * @param enabled true if enabled
	 */
	public void setSavingEnabled(boolean enabled) {
            this.isSavingEnabled = enabled;
	}

	/**
	 * Increases player force level.
	 * 
	 */
	public void increaseForceLevel() {

		forcedLevel++;
		
	}
	
	/**
	 * Decreases player force level.
	 * 
	 */
	public void decreaseForceLevel() {

		forcedLevel--;
		
	}
	
	/**
	 * Check if the player can be unforced.
	 * 
	 * @return true if can be unforced.
	 */
	public boolean isForced() {
		return forcedLevel > 0;
	}
	
	
	
	
	// Administration:
	/**
	 * Checks if the admin mode is active.
	 * 
	 * @return true if administration mode is active.
	 */
	public boolean isAdminMode() {
		return adminMode != null;
	}
	
	/**
	 * Enables admin mode.
	 * 
	 */
	public void enableAdminMode() {

		adminMode = true;
		
	}
	
	/**
	 * Disables admin mode.
	 * 
	 */
	public void disableAdminMode() {

		adminMode = null;
		
	}
	
	
	
	
	// Other:
	@Override
	public String toString() {
		
		
		StringBuffer rString = new StringBuffer();
		if(!isOnline()){
			rString.append("(offline)");
		}
		if(isForced()){
			rString.append("(forced)");
		}
		
		return getName() + rString;
		
		
	}
	
	
	
	
	// Debuging:
	public void info(String message) {

    	
        if ( isOnline() ) {
            this.player.sendMessage(PlayerMessages.normal1 + message);
        }

        
    }

    public void warning(String message) {

        if ( isOnline() ) {
            this.player.sendMessage(PlayerMessages.negative + message);
        }

    }

    
}
