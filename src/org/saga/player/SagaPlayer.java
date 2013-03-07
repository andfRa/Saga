package org.saga.player;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.buildings.production.SagaItem;
import org.saga.config.AbilityConfiguration;
import org.saga.config.AttributeConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.config.ExperienceConfiguration;
import org.saga.config.FactionConfiguration;
import org.saga.dependencies.ChatDependency;
import org.saga.dependencies.EconomyDependency;
import org.saga.dependencies.PermissionsDependency;
import org.saga.dependencies.Trader;
import org.saga.factions.Faction;
import org.saga.factions.FactionManager;
import org.saga.factions.SiegeManager;
import org.saga.messages.GeneralMessages.CustomColour;
import org.saga.messages.PlayerMessages;
import org.saga.messages.StatsMessages;
import org.saga.messages.colours.Colour;
import org.saga.messages.effects.StatsEffectHandler;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.settlements.Bundle;
import org.saga.settlements.BundleManager;
import org.saga.settlements.Settlement;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.InventoryUtil;

public class SagaPlayer extends SagaLiving<Player> implements Trader{

	
	
	// Player information:
	/**
	 * Name.
	 */
	private String name;

	/**
	 * Players health.
	 */
	private Double health;

	
	/**
	 * Experience.
	 */
	private Double exp;
	
	
	/**
	 * Amount of coins the player has.
	 */
	private Double coins;
	
	
	// Faction:
	/**
	 * Player factions ID.
	 */
	private Integer factionId;

	
	// Chunk group:
	/**
	 * Player chunk group IDs.
	 */
	private Integer chunkGroupId;

	
	// Invites:
	/**
	 * Invites to chunk groups.
	 */
	private ArrayList<Integer> bundleInvites;
	
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
	transient private boolean savingEnabledFlag = true;
	
	
	
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
		
		super(name);
		
		this.factionId = -1;
		this.chunkGroupId = -1;
		
		this.name = name;
		this.health = getTotalHealth();
		
		this.exp = 0.0;
		
		this.factionInvites = new ArrayList<Integer>();
		this.bundleInvites = new ArrayList<Integer>();
		
		this.coins = EconomyConfiguration.config().playerCoins;
		
		this.guardRune = new GuardianRune(this);
		
	}
	
	/**
	 * Goes trough all the fields and makes sure everything has been set after gson load.
	 * If not, it fills the field with defaults.
	 */
	public void complete() {

		
		super.complete();
		
		// Fields:
		if(name == null){
			name = "none";
		}
		
		if(health == null){
			health = 20.0;
			SagaLogger.nullField(this, "health");
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
		
		System.out.println("COMPLETE BEF:" + chunkGroupId);
		
		if(chunkGroupId == null){
			chunkGroupId = -1;
			SagaLogger.nullField(this, "chunkGroupId");
		}
		
		System.out.println("COMPLETE AFTER:" + chunkGroupId);
		
		
		if(factionInvites == null){
			factionInvites = new ArrayList<Integer>();
			SagaLogger.nullField(this, "factionInvites");
		}
	
		if(bundleInvites == null){
			bundleInvites = new ArrayList<Integer>();
			// TODO Restore bundle invites check: SagaLogger.nullField(this, "bundleInvites");
		}
		
		if(guardRune == null){
			guardRune = new GuardianRune(this);
			SagaLogger.nullField(this, "guardRune");
		}
		guardRune.complete();
		
		
	}

	
	
	// Statistics:
	/**
	 * Updates all player statistics.
	 * 
	 */
	public void updateStatistics() {
	
		StatisticsManager.manager().setWallet(this);
    	StatisticsManager.manager().setPlayer(this);

	}
	
	
	
	// Attributes:
	/* 
	 * Adds role and rank bonuses.
	 * 
	 * @see org.saga.player.SagaLivingEntity#getAttrScoreBonus(java.lang.String)
	 */
	@Override
	public Integer getAttrScoreBonus(String attrName) {

		
		Integer bonus = super.getAttrScoreBonus(attrName);
		
		// Ask role:
		Proficiency role = getRole();
		if(role != null){
			bonus+= role.getDefinition().getAttributeBonus(attrName);
		}
		
		// Ask rank:
		Proficiency rank = getRank();
		if(rank != null){
			bonus+= rank.getDefinition().getAttributeBonus(attrName);
		}
		
		// Cap:
		Integer rawScore = getRawAttributeScore(attrName);
		Integer cap = getAttributeCap(attrName);
		if(rawScore + bonus > cap) bonus = cap - rawScore;
		
		return bonus;
		
		
	}

	/* 
	 * Compensates hearth loss.
	 * 
	 * @see org.saga.player.SagaLivingEntity#setAttributeScore(java.lang.String, java.lang.Integer)
	 */
	@Override
	public void setAttributeScore(String attribute, Integer score) {
		
		int beforeHhearts = getHalfHearts();
		
		super.setAttributeScore(attribute, score);
		
		int afterHhearts = getHalfHearts();
		
		// Compensate heart loss and synch:
		if(beforeHhearts != afterHhearts){
			health = getHealth(beforeHhearts);
		}
		synchHealth();
		
	}

	
	/**
	 * Gets the attribute cap.
	 * 
	 * @param attrName attribute name
	 * @return attribute cap
	 */
	public Integer getAttributeCap(String attrName) {
		
		
		Integer cap = AttributeConfiguration.config().getNormalAttributeCap();
		Proficiency prof = null;
		
		// Ask role:
		prof = getRole();
		if(prof != null){
			cap+= prof.getDefinition().getAttributeCapBonus(attrName);
		}
		
		// Ask rank:
		prof = getRank();
		if(prof != null){
			cap+= prof.getDefinition().getAttributeCapBonus(attrName);
		}
		
		// Normalise:
		if(cap > AttributeConfiguration.config().getMaxAttributeCap()) cap = AttributeConfiguration.config().getMaxAttributeCap();
		
		return cap;
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.player.SagaLiving#getAvailableAttributePoints()
	 */
	@Override
	public Integer getAvailableAttributePoints() {
		return ExperienceConfiguration.config().getAttributePoints(exp);
	}
	
	
	
	// Abilities:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.player.SagaLiving#getAvailableAbilityPoints()
	 */
	@Override
	public Integer getAvailableAbilityPoints() {
		
		
		// Cap:
		Integer cap = AbilityConfiguration.config().getNormalAbilityCap();
		
		Proficiency role = getRole();
		if(role != null) cap+= role.getDefinition().getAbilityCapBonus();
		
		Proficiency rank = getRank();
		if(rank != null) cap+= rank.getDefinition().getAbilityCapBonus();
		
		// Available:
		Integer available = ExperienceConfiguration.config().getAbilityPoints(exp);
		if(available > cap) available = cap;
		
		return available;
		
		
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
		
		
		super.setLivingEntity(player);
		
		// Admin mode:
		if(isAdminMode() && !PermissionsDependency.hasPermission(player, PermissionsDependency.ADMIN_MODE_PERMISSION)){
			disableAdminMode();
			SagaLogger.info(this, "no permission for admin mode");
		}
		
    	// Update chat prefix:
    	ChatDependency.updatePrefix(this);
		
		// Saving disabled:
		if(!savingEnabledFlag){
			error("player information saving disabled");
		}

		
	}
	
	/**
	 * Sets the player and changes status to offline.
	 * 
	 */
	public void removePlayer() {

		// Admin mode:
		if(isAdminMode() && !PermissionsDependency.hasPermission(livingEntity, PermissionsDependency.ADMIN_MODE_PERMISSION)){
			disableAdminMode();
			SagaLogger.info(this, "no permission for admin mode");
		}
		
    	// Update chat prefix:
    	ChatDependency.updatePrefix(this);
		
		lastSagaChunk = null;

		removeLivingEntity();
		
	}
	
	/**
	 * Convenience method to get the player.
	 * 
	 * @return player, null if not online
	 */
	public Player getPlayer() {
		return livingEntity;
	}
	
	
	
	// Health:
	/**
	 * Gets players health.
	 * 
	 * @return players health
	 */
	public Double getHealth() {
		
		if(health < 0) health = 0.0;
		
		return health;
		
	}
	
	/**
	 * Gets player health for given amount of half hearts
	 * 
	 * @param halfhearts
	 * @return
	 */
	public Double getHealth(int halfhearts) {
		
		return getTotalHealth() / 20.0 * halfhearts;
		
	}
	
	/**
	 * Damages the player.
	 * 
	 * @param amount damage amount
	 */
	public void damage(Double amount) {

		health-= amount;
		
	}
	
	/**
	 * Damages the player.
	 * 
	 * @param amount damage amount
	 */
	public void heal(Double amount) {

		health+= amount;
		if(health > getTotalHealth()) health = getTotalHealth();
		
	}
	
	/**
	 * Synchronises players health.
	 * 
	 */
	public void synchHealth() {
		
		if(livingEntity == null) return;
		
		livingEntity.setHealth(getHalfHearts());
		
	}
	
	/**
	 * Gets the health in half hearts format.
	 * 
	 * @return health in half hearts
	 */
	public int getHalfHearts() {

		double totalHealth = getTotalHealth();
		
		int hhearts = (int)(20.0 * getHealth() / totalHealth);
		
		if(hhearts == 0 && this.health > 0){
			return 1;
		}
		
		if(hhearts == 20 && this.health < totalHealth){
			return 19;
		}
		
		if(livingEntity != null && livingEntity.getHealth() == 20 && this.health < totalHealth){
			return 19;
		}
		
		if(hhearts > 20) hhearts = 20;
		
		return hhearts;
		
	}
	
	/**
	 * Gets players total health.
	 * 
	 * @return players total health
	 */
	public Double getTotalHealth() {
		
		return attributeManager.getHealthModifier() + 20.0;
		
	}
	
	/**
	 * Restores health.
	 * 
	 */
	public void restoreHealth() {
		
		health = getTotalHealth();
		
	}
	
	/**
	 * Checks if the saga player is dead.
	 * 
	 * @return true if dead
	 */
	public boolean isDead() {

		return health <= 0;
		
	}
	
	
	
	// Food level:
	/**
	 * Gets players food level.
	 * 
	 * @return players food level
	 */
	@Override
	public int getFoodLevel() {
		
		if(livingEntity == null) return 0;
		
		return livingEntity.getFoodLevel();
		
	}
	
	/**
	 * Modifies players energy.
	 * 
	 * @param amount amount to modify by
	 */
	@Override
	public void modFoodLevel(int amount) {
		
		if(livingEntity == null) return;
		livingEntity.setFoodLevel(livingEntity.getFoodLevel() + amount);
		
	}
	
	
	
	// Experience:
	/**
	 * Sets player experience.
	 * 
	 * @param exp experience
	 */
	public void setExp(Integer exp) {
		
		this.exp = exp.doubleValue();
		
		// Update managers:
		abilityManager.update();
		
	}
	
	/**
	 * Gets player experience.
	 * 
	 * @return player experience
	 */
	public Double getExp() {
		return exp;
	}
	
	/**
	 * Gets the remaining experience for attributes.
	 * 
	 * @return remaining experience
	 */
	public Double getAttributeRemainingExp() {
		
		int attrPoints = ExperienceConfiguration.config().getAttributePoints(exp);
		
		return ExperienceConfiguration.config().calcAttributeExp(attrPoints + 1) - exp;
		
	}
	
	/**
	 * Gets the remaining experience for abilities.
	 * 
	 * @return remaining experience
	 */
	public Double getAbilityRemainingExp() {
		
		int attrPoints = ExperienceConfiguration.config().getAbilityPoints(exp);
		
		return ExperienceConfiguration.config().calcAbilityExp(attrPoints + 1) - exp;
		
	}
	
	/**
	 * Awards player experience.
	 * 
	 * @param amount amount of exp
	 */
	public void awardExp(Double amount) {
		
		amount*= ExperienceConfiguration.config().getExpGainMultiplier(exp);
		amount*= getExpMult();
		
		int befAttributes = ExperienceConfiguration.config().getAttributePoints(exp);
		int befAbilites = ExperienceConfiguration.config().getAbilityPoints(exp);
		
		this.exp += amount;
		if(exp > ExperienceConfiguration.config().getMaxExp()) exp = ExperienceConfiguration.config().getMaxExp().doubleValue(); 
		
		int aftAttributes = ExperienceConfiguration.config().getAttributePoints(exp);
		int aftAbilities = ExperienceConfiguration.config().getAbilityPoints(exp);

		// Inform:
		if(befAttributes < aftAttributes){
			message(StatsMessages.gainedAttributePoints(aftAttributes - befAttributes));
			StatsEffectHandler.playLevelUp(this);
		}

		if(befAbilites < aftAbilities){
			message(StatsMessages.gainedAbilityPoints(aftAttributes - befAttributes));
			StatsEffectHandler.playLevelUp(this);
		}
		
		
	}
	
	/**
	 * Gets the experience multiplier.
	 * 
	 * @return experience multiplier
	 */
	public Double getExpMult() {


		// Triple exp:
		if(PermissionsDependency.hasPermission(this, PermissionsDependency.SPECIAL_TRIPLE_EXP_BONUS)){
			return 3.0;
		}

		// Double exp:
		if(PermissionsDependency.hasPermission(this, PermissionsDependency.SPECIAL_DOUBLE_EXP_BONUS)){
			return 2.0;
		}
		
		return 1.0;
		
		
	}
	
	
	
	// Items:
	/**
	 * Gets player item in hand.
	 * 
	 * @return player item in hand air if not online or none
	 */
	public ItemStack getItemInHand() {

		
		if(livingEntity == null) return new ItemStack(Material.AIR);
		
		return livingEntity.getItemInHand();
		
		
	}
	
	/* 
	 * Damages players tool.
	 * 
	 * @see org.saga.player.SagaLiving#damageTool()
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void damageTool() {
		
		if(livingEntity == null) return;
		
		ItemStack item = getPlayer().getItemInHand();

		if(item == null || item.getType().getMaxDurability() <= 0 ) return;
			
		item.setDurability((short) (item.getDurability() + 1));

		getPlayer().updateInventory();
		
	}
	
	
	
	// Faction:
	/**
	 * Gets the faction id.
	 * 
	 * @return factions ID, -1 if none
	 */
	public Integer getFactionId() {
		
		// Undecided members:
		if(factionId == -1 && chunkGroupId != -1 && FactionConfiguration.config().isLimitedMembershipEnabled()){
			
			Integer owningID = SiegeManager.manager().getOwningFactionID(chunkGroupId);
			if(owningID != null) return owningID;
			
		}
			
			
		return factionId;
		
	}

	/**
	 * Sets a faction ID for the player.
	 * 
	 * @param factionId faction Id
	 */
	public void setFactionId(Integer factionId) {
		this.factionId = factionId;
	}

	/**
	 * Removes a faction ID from the player.
	 */
	public void removeFactionId() {
		factionId = -1;
	}

	/**
	 * Gets the players faction.
	 * 
	 * @return players faction, null if none
	 */
	public Faction getFaction() {
		
		Integer factionId = getFactionId();
		if(factionId == -1) return null;
		
		return FactionManager.manager().getFaction(factionId);
		
	}

	
	
	// Bundle:
	/**
	 * Sets a chunk bundle ID to the player.
	 * 
	 * @param chunkBundleId chunk group ID
	 */
	public void setBundleId(Integer chunkBundleId) {
		this.chunkGroupId = chunkBundleId;
	}

	/**
	 * Removes a chunk bundle ID from the player.
	 */
	public void removeBundleId() {
		this.chunkGroupId = -1;
	}
	
	/**
	 * Gets the chunk bundle ID.
	 * 
	 * @return the chunk bundle ID
	 */
	public Integer getBundleId() {
		return chunkGroupId;
	}

	/* 
	 * Return actual chunk bundle.
	 * 
	 * @see org.saga.player.SagaLiving#getBundle()
	 */
	@Override
	public Bundle getBundle() {
		
		if(chunkGroupId == -1) return null;
		
		return BundleManager.manager().getBundle(chunkGroupId);
		
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
	
	
	
	// Bundle invites:
	/**
	 * Adds a chunk group invite.
	 * 
	 * @param groupId chunk group ID
	 */
	public void addBundleInvite(Integer groupId) {
		
		
		// Ignore invite if already exists:
		if(bundleInvites.contains(groupId)){
			return;
		}
		
		// Add invite:
		bundleInvites.add(groupId);
		
		
	}
	
	/**
	 * Removes a chunk group invite.
	 * 
	 * @param chunkGroupId chunk group ID
	 */
	public void removeBundleInvite(Integer chunkGroupId) {
		
		
		// Ignore invite if doesn't exists:
		if(!bundleInvites.contains(chunkGroupId)){
			return;
		}
		
		// Remove invite:
		bundleInvites.remove(chunkGroupId);
		
		
	}
	
	/**
	 * Gets chunk group invites.
	 * 
	 * @return chunk group invites
	 */
	public ArrayList<Integer> getBundleInvites() {
		return new ArrayList<Integer>(bundleInvites);
	}

	/**
	 * Checks if the player has an invite.
	 * 
	 * @param id ID
	 * @return true if has an invite
	 */
	public boolean hasBundleInvite(Integer id){
		return bundleInvites.contains(id);
	}

	
	/**
	 * Refreshes last saga chunk.
	 * 
	 */
	public void refreshChunk() {
		
		
		if(livingEntity == null){
			return;
		}
		
		lastSagaChunk = BundleManager.manager().getSagaChunk(livingEntity.getLocation());
		
		
	}
	
	
	
	// Rank and role:
	/**
	 * Gets player rank.
	 * 
	 * @return player rank, null if none
	 */
	public Proficiency getRank() {

		Faction faction = getFaction();
		if(faction == null) return null;
		
		return faction.getRank(getName());

	}
	
	/**
	 * Gets player role.
	 * 
	 * @return player role, null if none
	 */
	public Proficiency getRole() {

		Bundle bundle = getBundle();
		if(bundle == null) return null;
		
		if(bundle instanceof Settlement){
			return ((Settlement) bundle).getRole(getName());
		}else{
			return null;
		}
		
	}
	
	
	// Messages:
	/* 
	 * Player message.
	 * 
	 * @see org.saga.player.SagaLiving#message(java.lang.String)
	 */
	@Override
	public void message(String message) {
		
		if(message.length() == 0) return;
		
		if(livingEntity != null) livingEntity.sendMessage(CustomColour.process(message));
          
	}

	/**
	 * Sends an error message.
	 * 
	 * @param message message
	 */
	public void error(String message) {
		
		message(ChatColor.DARK_RED + message);
          
	}

	
	
	// Effects and sounds:
	/**
	 * Plays an effect.
	 * 
	 * @param effect effect
	 * @param value effect value
	 */
	public void playEffect(Effect effect, int value) {
		
		if(livingEntity == null) return;
		
		livingEntity.playEffect(getLocation(), effect, value);
		
	}
	
	/**
	 * Plays a sound.
	 * 
	 * @param sound sound
	 * @param arg2
	 * @param arg2
	 */
	public void playSound(Sound sound, float arg2, float arg3) {
		
		if(livingEntity == null) return;
		
		// TODO: Play sound
		livingEntity.playSound(getLocation(), sound, arg2, arg3);
		
		
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
	 * @see org.saga.dependencies.Trader#addCoins(java.lang.Double)
	 */
	@Override
	public boolean addCoins(Double amount) {
		coins = coins + amount;
		return true;
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.dependencies.Trader#removeCoins(java.lang.Double)
	 */
	@Override
	public boolean removeCoins(Double amount) {
		coins = coins - amount;
		return true;
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Transaction.Trader#getCurrency()
	 */
	@Override
	public Double getCoins() {
		return coins;
	}
	
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#addItem(org.bukkit.inventory.ItemStack)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void addItem(ItemStack itemStack) {
		
		if(livingEntity != null){
			
			boolean dropped = InventoryUtil.addItem(itemStack, livingEntity.getInventory(), livingEntity.getLocation());
			
			if(dropped){
				message(PlayerMessages.inventoryFullDropping());
			}
			
			livingEntity.updateInventory(); // TODO replace updateInventory()
			
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
		
		if(livingEntity != null){
			
			InventoryUtil.removeItem(itemStack, livingEntity.getInventory());
			
			livingEntity.updateInventory();
			
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
		
		if(livingEntity != null){
			
			InventoryUtil.removeItem(material, amount, false, livingEntity);
			
			livingEntity.updateInventory(); // TODO replace updateInventory()
			
		}
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#getItemCount(org.bukkit.Material)
	 */
	@Override
	public Integer getAmount(Material item) {
		
		if(livingEntity != null){
			
			return InventoryUtil.getItemCount(item, livingEntity.getInventory().getContents());
			
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

	/**
	 * Takes a saga item from the player.
	 * 
	 * @param requested requested item
	 * @return item removed
	 */
	@SuppressWarnings("deprecation")
	public SagaItem takeItem(SagaItem requested) {

		if(livingEntity == null){
			SagaItem taken = new SagaItem(requested);
			taken.setAmount(0.0);
			return taken;
		}
		
		SagaItem taken = InventoryUtil.takeItem(requested, livingEntity.getInventory());
		
		livingEntity.updateInventory();
		
		return taken;
		
	}
	
	/**
	 * Gives a Saga item to the player.
	 * Drops if inventory full.
	 * 
	 * @param sagaItem Saga item to give
	 */
	@SuppressWarnings("deprecation")
	public void giveItem(SagaItem sagaItem) {

		if(livingEntity == null){
			SagaLogger.warning(this, "can't give a item: no entity wrapped");
			return;
		}
		
		InventoryUtil.giveItem(sagaItem, livingEntity.getInventory(), livingEntity.getLocation());
		
		livingEntity.updateInventory();
		
	}
	
	
	/**
	 * Counts the available coins.
	 * Works with economy plugins.
	 * 
	 * @return amount of coins
	 */
	public Double handleCountCoins() {
		return EconomyDependency.getCoins(this);
	}

	/**
	 * Modifies the amount of coins.
	 * Works with economy plugins.
	 * 
	 * @param amount amount to modify by
	 */
	public void handleModCoins(Double amount) {
		if(amount > 0){
			EconomyDependency.addCoins(this, amount);
		}
		else if(amount < 0){
			EconomyDependency.removeCoins(this, -amount);
		}
	}
	
	/**
	 * Requests coins.
	 * Works with economy plugins
	 * 
	 * @param request amount request
	 */
	public Double requestCoins(Double request) {

		Double coins = handleCountCoins();
		Double given = request;
		if(given > coins) given = coins;
		handleModCoins(-given);
		return given;
		
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
		if(!checkExists(playerName)){
		
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

			SagaLogger.info(SagaPlayer.class, "player information file not found for " + playerName);
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
		return WriterReader.checkExists(Directory.PLAYER_DATA, name.toLowerCase());
	}

	
	
	// Control:
	/**
	 * True if the player information should be saved.
	 * 
	 * @return true if player information should be saved
	 */
	public boolean isSavingEnabled() {
            return savingEnabledFlag;
	}
	
	/**
	 * Disables or enables player information saving.
	 * 
	 * @param enabled true if enabled
	 */
	public void setSavingEnabled(boolean enabled) {
            this.savingEnabledFlag = enabled;
	}
	
	/**
	 * Called when the player instance was modified and can be released.
	 * 
	 */
	public void indicateRelease() {

		// Save if not loaded:
		if(!Saga.plugin().isSagaPlayerLoaded(name)) save();
		
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
		
		
		StringBuffer result = new StringBuffer();
		if(this.livingEntity == null){
			result.append("(offline)");
		}
		
		return getName() + result;
		
		
	}
	
	
	
	// Debuging:
	public void info(String message) {
    	
        if (livingEntity != null) livingEntity.sendMessage(Colour.normal1 + message);
        
    }

    public void warning(String message) {

        if (livingEntity != null)  this.livingEntity.sendMessage(Colour.negative + message);

    }

    
}
