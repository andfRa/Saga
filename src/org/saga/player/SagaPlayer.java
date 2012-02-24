package org.saga.player;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftFireball;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import org.saga.Clock;
import org.saga.Clock.SecondTicker;
import org.saga.Saga;
import org.saga.SagaPlayerListener.SagaPlayerProjectileShotEvent;
import org.saga.SagaPlayerListener.SagaPlayerProjectileShotEvent.ProjectileType;
import org.saga.abilities.Ability;
import org.saga.abilities.AbilityDefinition;
import org.saga.abilities.AbilityDefinition.ActivationAction;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.ChunkGroupManager;
import org.saga.chunkGroups.SagaChunk;
import org.saga.config.AbilityConfiguration;
import org.saga.config.AbilityConfiguration.InvalidAbilityException;
import org.saga.config.BalanceConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.config.ExperienceConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.ProficiencyConfiguration.InvalidProficiencyException;
import org.saga.config.SkillConfiguration;
import org.saga.constants.BlockConstants;
import org.saga.constants.PlayerDefaults;
import org.saga.economy.EconomyManager.TransactionType;
import org.saga.economy.InventoryUtil;
import org.saga.economy.TradeDeal;
import org.saga.economy.Trader;
import org.saga.economy.Transaction;
import org.saga.factions.SagaFaction;
import org.saga.player.GuardianRune.GuardianRuneStatus;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.player.Skill.ArmourType;
import org.saga.shape.RelativeShape.Orientation;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.TextUtil;
import org.saga.utility.WriterReader;

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
	 * Amount of currency the player has.
	 */
	private Double coins;

	
	// Proficiencies:
	/**
	 * Players profession.
	 */
	private Proficiency profession;
	
	/**
	 * Players class.
	 */
	private Proficiency classs;
	
	/**
	 * Players role.
	 */
	transient private Proficiency role;
	
	/**
	 * Players role.
	 */
	transient private Proficiency rank;
	
	
	// Skills:
	/**
	 * Skills.
	 */
	private Hashtable<String, Integer> skills;

	
	// Abilities:
	/**
	 * Ability activation map.
	 */
	private Hashtable<String, ActivationAction> abilityActions;
	
	/**
	 * Ability material map.
	 */
	private Hashtable<String, Material> abilityMaterials;
	
	/**
	 * All abilities.
	 */
	private ArrayList<Ability> abilities;
	
	
	// Managers:
	/**
	 * Level manager.
	 */
	transient private PlayerLevelManager levelManager;
	

	// Experience:
	/**
	 * Experience to be regenerated.
	 */
	private Integer expRegen;
	
	
	// Faction:
	/**
	 * Player factions ID.
	 */
	private Integer factionId;
	
	/**
	 * Registered factions.
	 */
	transient private SagaFaction registeredFaction;
	
	
	// Chunk group:
	/**
	 * Player chunk group IDs.
	 */
	private Integer chunkGroupId;

	/**
	 * All registered chunk groups.
	 */
	transient private ChunkGroup registeredChunkGroup = null;
	
	
	// Location:
	/**
	 * Last chunk the player was on.
	 */
	transient private SagaChunk lastSagaChunk = null;
	
	/**
	 * Last player location.
	 */
	transient private Location lastLocation = null;

	
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
	private GuardianRune guardianStone;
	
	
	// Reward:
	/**
	 * Reward.
	 */
	private Integer reward; 
	
	
	// Mining Statistics:
	/**
	 * Mining statistics:
	 */
	private Hashtable<Material, Integer> miningStatistics;
	
	
	// Date:
	/**
	 * Lost online date.
	 */
	private Date lastOnline;

	
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
	

	// Loading and initialization:
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
		this.coins = EconomyConfiguration.config().playerInitialCurrency;
		this.guardianStone = GuardianRune.newStone();
		this.lastOnline = Calendar.getInstance().getTime();
		this.levelManager = new PlayerLevelManager(this);
		this.expRegen = 0;
		this.abilities = new ArrayList<Ability>();
		this.abilityActions = new Hashtable<String, AbilityDefinition.ActivationAction>();
		this.abilityMaterials = new Hashtable<String, Material>();
		this.reward = 0;
		this.skills = new Hashtable<String, Integer>();
		this.miningStatistics = new Hashtable<Material, Integer>();
		
	}
	
	/**
	 * Goes trough all the fields and makes sure everything has been set after gson load.
	 * If not, it fills the field with defaults.
	 */
	public void complete() {

		
		// Fields:
		if(name == null){
			name = PlayerDefaults.name;
		}
		
		if(level == null){
			level = 0;
			Saga.severe(this, "level field not initialized", "setting default");
		}
		
		if(exp == null){
			exp = 0.0;
			Saga.severe(this, "level field not initialized", "setting default");
		}
		
		if(coins == null){
			coins = EconomyConfiguration.config().playerInitialCurrency;
			Saga.severe(this, "coins field not initialized", "setting default");
		}
		
		if(factionId == null){
			factionId = -1;
			Saga.severe(this, "stamina field not initialized", "setting default");
		}
		
		if(chunkGroupId == null){
			chunkGroupId = -1;
			Saga.severe(this, "chunkGroupId field not initialized", "setting default");
		}
		
		if(factionInvites == null){
			factionInvites = new ArrayList<Integer>();
			Saga.severe(this, "factionInvites field not initialized", "setting default");
		}
	
		if(chunkGroupInvites == null){
			chunkGroupInvites = new ArrayList<Integer>();
			Saga.severe(this, "chunkGroupInvites field not initialized", "setting default");
		}
		
		if(guardianStone == null){
			guardianStone = GuardianRune.newStone();
			Saga.severe(this, "guardianStone field not initialized", "setting default");
		}
		guardianStone.complete();
		
		if(lastOnline == null){
			lastOnline = Calendar.getInstance().getTime();
			Saga.severe(this, "failed to initialize lastOnline field", "setting default");
		}
		
		if(profession != null){
			
			try {
				profession.complete();
				profession.setPlayer(this);
			} catch (InvalidProficiencyException e) {
				Saga.severe(this, "failed to complete profession field", "removing field");
				profession = null;
			}
			
		}
		
		if(classs != null){
			
			try {
				
				classs.fixName();
				
				classs.complete();
				classs.setPlayer(this);
				
			} catch (InvalidProficiencyException e) {
				Saga.severe(this, "failed to complete classs field to initialize", "removing field");
				classs = null;
			}
			
		}

		// Abilities:
		if(abilityActions == null){
			abilityActions = new Hashtable<String, AbilityDefinition.ActivationAction>();
			Saga.severe(this, "abilityActions field failed to initialize", "setting default");
		}
		
		if(abilityMaterials == null){
			abilityMaterials = new Hashtable<String, Material>();
			Saga.severe(this, "abilityMaterials field failed to initialize", "setting default");
		}
		
		if(abilities == null){
			abilities = new ArrayList<Ability>();
			Saga.severe(this, "abilities field failed", "setting default");
		}
		
		for (int i = 0; i < abilities.size(); i++) {
			
			Ability ability = abilities.get(i);
			
			if(ability == null){
				Saga.severe(this, "abilities element field failed to initialize", "setting default");
				abilities.remove(i);
				i--;
				continue;
			}
			
			try {
				
				ability.complete();
				
				ability.setPlayer(this);
				
				ability.setProficiency(matchProficiency(ability));
				
			} catch (InvalidAbilityException e) {
				Saga.severe(this, "abilities element invalid: " + e.getMessage(), "removing element");
				abilities.remove(i);
				i--;
				continue;
			}
			
		}
		
		if(expRegen == null){
			expRegen = 0;
			Saga.severe(this, "expRegen field failed", "setting default");
		}
		
		// Limit experience regeneration:
		if(expRegen > BalanceConfiguration.config().expRegenLimit){
			expRegen = BalanceConfiguration.config().expRegenLimit;
		}
		
		if(skills == null){
			skills = new Hashtable<String, Integer>();
			Saga.severe(this, "skills field failed to initialize", "setting default");
		}
		
		if(reward == null){
			reward = 0;
			Saga.severe(this, "reward field failed to initialize", "setting default");
		}
		
		if(miningStatistics == null){
			miningStatistics = new Hashtable<Material, Integer>();
			Saga.severe(this, "miningStatistics field failed to initialize", "setting default");
		}
		
		// Transient:
		this.clockEnabled = false;
		this.levelManager = new PlayerLevelManager(this);
	
		
	}

	
	// Proficiencies:
	/**
	 * Gets all proficiencies.
	 * 
	 * @return all proficiencies
	 */
	public ArrayList<Proficiency> getAllProficiencies() {

		
		ArrayList<Proficiency> allProficiencies = new ArrayList<Proficiency>();
		
		if(profession != null){
			allProficiencies.add(profession);
		}
		
		if(classs != null){
			allProficiencies.add(classs);
		}
		
		if(role != null){
			allProficiencies.add(role);
		}
		
		if(rank != null){
			allProficiencies.add(rank);
		}
		
		return allProficiencies;
		
		
	}
	
	/**
	 * Gets the profession.
	 * 
	 * @return profession, null if none
	 */
	public Proficiency getProfession() {
		return profession;
	}
	
	/**
	 * Sets the profession.
	 * 
	 * @param profession profession
	 */
	public void setProfession(Proficiency profession) {
		
		if(this.profession != null){
			Saga.severe(this, "tried to set a second profession", "continuing with request");
		}

		// Set player:
		profession.setPlayer(this);
		
		this.profession = profession;

		// Update managers:
		levelManager.update();
//
//		// Correct bindings:
//		correctBindings();
//		
		
	}
	
	/**
	 * Clears the profession.
	 * 
	 */
	public void clearProfession() {
		
		if(this.profession == null){
			Saga.severe(this, "tried clear a non-existant profession", "continuing with request");
		}
		
		this.profession = null;

		// Update managers:
		levelManager.update();

	}


	/**
	 * Gets the class.
	 * 
	 * @return class, null if none
	 */
	public Proficiency getClazz() {
		return classs;
	}
	
	/**
	 * Sets the class.
	 * 
	 * @param class class
	 */
	public void setClass(Proficiency classs) {
		
		if(this.classs != null){
			Saga.severe(this, "tried to set a second class", "continuing with request");
		}
		
		// Set player:
		classs.setPlayer(this);
		
		this.classs = classs;
		
		// Update managers:
		levelManager.update();
//
//		// Correct bindings:
//		correctBindings();
//		
		
		
	}
	
	/**
	 * Clears the class.
	 * 
	 */
	public void clearClass() {
		
		if(this.classs == null){
			Saga.severe(this, "tried clear a non-existant class", "continuing with request");
		}
		
		this.classs = null;

		// Update managers:
		levelManager.update();
		
	}


	/**
	 * Gets the role.
	 * 
	 * @return role, null if none
	 */
	public Proficiency getRole() {
		return role;
	}
	
	/**
	 * Sets the role.
	 * 
	 * @param role role
	 */
	public void setRole(Proficiency role) {
		
		// Register:
		registerRole(role);
		
	}
	
	/**
	 * Clears the role.
	 * 
	 */
	public void clearRole() {

		
		// Unregister:
		unregisterRole();

	}
	

	/**
	 * Gets the rank.
	 * 
	 * @return rank, null if none
	 */
	public Proficiency getRank() {
		return rank;
	}
	
	/**
	 * Sets the rank.
	 * 
	 * @param rank rank
	 */
	public void setRank(Proficiency rank) {
		
		// Register:
		registerRank(rank);
		
	}
	
	/**
	 * Clears the rank.
	 * 
	 */
	public void clearRank() {
		
		// Unregister:
		unregisterRank();
		
	}


	/**
	 * Registers the role.
	 * 
	 * @param role role
	 */
	public void registerRole(Proficiency role) {
		
		
		if(this.role != null){
			Saga.severe(this, "tried to set a second role", "continuing with request");
		}

		// Set player:
		role.setPlayer(this);
		
		this.role = role;

		// Update managers:
		levelManager.update();

		
	}
	
	/**
	 * Unregisters the role.
	 * 
	 */
	public void unregisterRole() {
		
		
		if(this.role == null){
			Saga.severe(this, "tried clear a non-existant role", "continuing with request");
		}
		
		this.role = null;
		
		// Update managers:
		levelManager.update();

	
	}

	/**
	 * Registers the rank.
	 * 
	 * @param rank rank
	 */
	public void registerRank(Proficiency rank) {
		
		if(this.rank != null){
			Saga.severe(this, "tried to set a second rank", "continuing with request");
		}

		// Set player:
		rank.setPlayer(this);
		
		this.rank = rank;

		// Update managers:
		levelManager.update();
		
		
	}
	
	/**
	 * Unregisters the rank.
	 * 
	 */
	public void unregisterRank() {
		
		if(this.rank == null){
			Saga.severe(this, "tried clear a non-existant rank", "continuing with request");
		}
		
		this.rank = null;

		// Update managers:
		levelManager.update();

		
	}
	
	
	// Attributes:
	/**
	 * Gets the level manager.
	 * 
	 * @return level manager
	 */
	public PlayerLevelManager getLevelManager() {
		return levelManager;
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
	 * Checks if the player has an ability.
	 * 
	 * @param name ability name
	 * @return true if the player has the ability
	 */
	public boolean hasAbility(String name) {
		return getAbility(name) != null;	
	}
	
	/**
	 * Gets learned abilities.
	 * 
	 * @return learned abilities
	 */
	public HashSet<Ability> getLearnedAbilities() {

		
		HashSet<Ability> learnedAbilities = new HashSet<Ability>();
		HashSet<String> learnableAbilities = getLearnableAbilities();
		
		for (String abilityName : learnableAbilities) {
			
			Ability ability = getAbility(abilityName);
			
			// Correct abilities:
			if(ability == null){
				try {
					
					ability = AbilityConfiguration.createAbility(abilityName);
					
					this.abilities.add(ability);
					
					ability.setPlayer(this);
					
					ability.setProficiency(matchProficiency(ability));
					
				} catch (InvalidAbilityException e) {
					
					Saga.severe(this, "failed to retrieve " + abilityName + " ability:" + expRegen.getClass().getSimpleName() + ":" + e.getMessage(), "ignoring ability");
					continue;
					
				}
			}
			
			learnedAbilities.add(ability);
			
		}
		
		return learnedAbilities;
		
		
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
	 * Checks if the ability is binded to the given material and action.
	 * 
	 * @param name ability name
	 * @param material material
	 * @param action action
	 * @return true if binded
	 */
	public boolean isAbilityBinded(String name, Material material, ActivationAction action) {

		
		if(profession != null && profession.getDefinition().isAbilityBinded(name, material, action)) return true;

		if(classs != null && classs.getDefinition().isAbilityBinded(name, material, action)) return true;

		if(role != null && role.getDefinition().isAbilityBinded(name, material, action)) return true;

		if(rank != null && rank.getDefinition().isAbilityBinded(name, material, action)) return true;
		
		return false;
		
		
	}

	/**
	 * Checks if the ability can be learned.
	 * 
	 * @param name ability name
	 * @return true if can be learned
	 */
	public boolean canLearnAbility(String name) {

		
		// Profession:
		if(profession != null && profession.getDefinition().hasAbility(name)) return true;
		
		// Class:
		if(classs != null && classs.getDefinition().hasAbility(name)) return true;
		
		// Role:
		if(role != null && role.getDefinition().hasAbility(name)) return true;
		
		// Rank:
		if(rank != null && rank.getDefinition().hasAbility(name)) return true;
		
		return false;
		
		
	}
	
	/**
	 * Gets all learnable abilities.
	 * 
	 * @return learnable abilities
	 */
	public HashSet<String> getLearnableAbilities() {

		
		HashSet<String> abilities = new HashSet<String>();

		// Profession:
		if(profession != null){
			abilities.addAll(profession.getDefinition().getAbilities());
		}

		// Class:
		if(classs != null){
			abilities.addAll(classs.getDefinition().getAbilities());
		}

		// Role:
		if(role != null){
			abilities.addAll(role.getDefinition().getAbilities());
		}

		// Rank:
		if(rank != null){
			abilities.addAll(rank.getDefinition().getAbilities());
		}
		
		return abilities;
		
		
	}

	/**
	 * Matches an proficiency to the ability.
	 * 
	 * @param ability ability
	 * @return proficiency, null if none
	 */
	private Proficiency matchProficiency(Ability ability) {


		// Profession:
		if(profession != null && profession.getDefinition().hasAbility(ability.getName())){
			return profession;
		}
		
		// Class:
		if(classs != null && classs.getDefinition().hasAbility(ability.getName())){
			return classs;
		}
		
		return null;

	}
	
	
	// Skills:
	/**
	 * Gets skills.
	 * 
	 * @return skills
	 */
	public Hashtable<String, Integer> getSkills() {
		return new Hashtable<String, Integer>(skills);
	}
	
	/**
	 * Gets enabled skills.
	 * 
	 * @return enabled skills
	 */
	public HashSet<String> getEnabledSkills() {
		
		
		HashSet<String> skills = new HashSet<String>();

		if(profession != null){
			skills.addAll(profession.getDefinition().getSkills());
		}
		
		if(classs != null){
			skills.addAll(classs.getDefinition().getSkills());
		}
		
		if(role != null){
			skills.addAll(role.getDefinition().getSkills());
		}
		
		if(rank != null){
			skills.addAll(rank.getDefinition().getSkills());
		}
		
		return skills;
		
		
	}
	
	/**
	 * Gets skill multiplier.
	 * 
	 * @param name skill name
	 * @return multiplier, 0 if none
	 */
	public Integer getSkillMultiplier(String name) {
		
		Integer value = skills.get(name);
		if(value == null) value = 0;
		
		return value;
		
	}

	/**
	 * Gets modified skill multiplier.
	 * 
	 * @param name skill name
	 * @return modified multiplier, 0 if none
	 */
	public Integer getModifiedSkillMultiplier(String name) {
		
		Integer value = skills.get(name);
		if(value == null) value = 0;
		
		
		Integer maxValue = getSkillMaximum(name);
		if(value > maxValue) value = maxValue;
		
		return value;
		
	}

	/**
	 * Gets maximum modified skill multiplier.
	 * 
	 * @param name skill name
	 * @return modified multiplier, 0 if none
	 */
	public Integer getModifiedSkillMultiplier(HashSet<String> skillNames) {
		
		Integer value = 0;
		for (String skillName : skillNames) {
			Integer nextValue = getModifiedSkillMultiplier(skillName);
			if(nextValue > value) value = nextValue;
		}
		
		return value;
		
	}
	
	/**
	 * Increases a skill.
	 * 
	 * @param name name
	 * @param amount amount to increase
	 * 
	 * @return new skill value
	 */
	public Integer increaseSkill(String name, Integer amount) {
		
		
		Integer value = skills.get(name);
		if(value == null) value = 0;
		
		value += amount;
		
		if(value == 0){
			skills.remove(name);
			return value;
		}
		
		skills.put(name, value);

		// Update level manager:
		levelManager.update();
		
		return value;
		
		
	}
	
	/**
	 * Decreases a skill.
	 * 
	 * @param name name
	 * @param amount amount to decrease
	 * 
	 * @return new skill value
	 */
	public Integer decreaseSkill(String name, Integer amount) {
		
		
		Integer value = skills.get(name);
		if(value == null) value = 0;
		
		value -= amount;
		
		if(value == 0){
			skills.remove(name);
			return value;
		}
		
		skills.put(name, value);

		// Update level manager:
		levelManager.update();
		
		return value;
		
		
	}
	
	/**
	 * Clears all skills.
	 * 
	 */
	public void clearSkills() {
		
		
		skills = new Hashtable<String, Integer>();

		// Update level manager:
		levelManager.update();
		
		
	}
	
	/**
	 * Gets the skill maximum multiplier.
	 * 
	 * @param name skill name
	 * @return skill maximum multiplier, 0 if not available
	 */
	public Integer getSkillMaximum(String name) {

		
		Integer maximum = 0;
		
		ArrayList<Proficiency> proficiencies = getAllProficiencies();
		for (Proficiency proficiency : proficiencies) {
			
			Integer profMax = proficiency.getSkillMaximum(name);
			
			if(profMax > maximum) maximum = profMax;
			
		}
		
		return maximum;
	
		
	}

	/**
	 * Sets skill multiplier.
	 * 
	 * @param name skill name
	 * @param multiplier multiplier
	 */
	public void setSkillMultiplier(String name, Integer multiplier) {

		
		// Check if valid skill:
		if(SkillConfiguration.config().getSkill(name) == null){
			Saga.severe(this, "tried to set an invalid skill " + name, "ignoring request");
			return;
		}
		
		skills.put(name, multiplier);

		// Update level manager:
		levelManager.update();
		
		
	}
	
	
	// Skill points:
	/**
	 * Gets spent skill points.
	 * 
	 * @return spent skill points
	 */
	public Integer getSkillPoints() {
		
		
		Integer skillsPoints = 0;
		
		Hashtable<String, Integer> skills = getSkills();
		
		Collection<Integer> multipliers = skills.values();
		
		for (Integer multiplier : multipliers) {
			skillsPoints += multiplier;
		}
		
		return skillsPoints;

		
	}
	
	/**
	 * Gets remaining skill points.
	 * 
	 * @return spent skill points
	 */
	public Integer getRemainingSkillPoints() {
		
		return ExperienceConfiguration.config().getSkillPoints(getLevel()) - getSkillPoints();

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
		this.lastLocation = player.getLocation();
		this.lastSagaChunk = ChunkGroupManager.manager().getSagaChunk(lastLocation);
		this.lastOnline = Calendar.getInstance().getTime();
		
		// Update managers:
		levelManager.update();

		// Admin mode:
		if(isAdminMode() && !Saga.plugin().canAdminMode(this)){
			disableAdminMode();
			Saga.info(this, "no permission for admin mode", "disabling admin mode");
		}
		
		// Saving disabled:
		if(!isSavingEnabled){
			error("player information saving disabled");
		}

		
	}
	
	/**
	 * Sets the player and changes status to offline.
	 * 
	 */
	public void removePlayer() {

		
		// Admin mode:
		if(isAdminMode() && !Saga.plugin().canAdminMode(this)){
			disableAdminMode();
			Saga.info(this, "no permission for admin mode", "disabling admin mode");
		}
		
		this.player = null;
		this.lastLocation = null;
		lastSagaChunk = null;
		this.lastOnline = Calendar.getInstance().getTime();

		
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
		levelManager.update();
		
	}
	
	/**
	 * Levels up the player.
	 * 
	 */
	public void levelUp() {
		
		setLevel(level + 1);
		
		message(PlayerMessages.levelup(getLevel()));
		
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
	 * Gets player total experience.
	 * 
	 * @return player total experience
	 */
	public int getTotalExperience() {
		
		if(!isOnline()) return 0;
		
		int experience = ExperienceConfiguration.config().getTotalExperience(player);

		return experience;
		
	}

	/**
	 * Sets player experience.
	 * 
	 * @param experience experience
	 */
	public void setTotalExperience2(int experience) {
		
		
		
	}
	
	/**
	 * Gives player experience.
	 * 
	 * @param expAmount experience
	 */
	public void giveExperience(Double expAmount) {
		
		if(level >= ExperienceConfiguration.config().getMaxLevel()) return;
		
		this.exp += expAmount;
		
		if(this.exp >= ExperienceConfiguration.config().getLevelExp(getLevel())){
			expAmount = 0.0;
			levelUp();
			
		}
		
	}
	
	
	// Physical:
	/**
	 * Returns player health.
	 * 
	 * @return player health, -1 if offline
	 */
	public Integer getHealth() {
		
		if(isOnline()){
			return player.getHealth();
		}else{
			return -1;
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
	 * Gets the armor.
	 * 
	 * @param type type
	 * @return armor percentage
	 */
	public double getArmor(ArmourType type) {

		
		if(!isOnline()) return 0.0;
		
		double armour = 0;
		PlayerInventory inventory = getPlayer().getInventory();
		
		switch (type) {
		case EXOTIC:
			
			if(inventory.getHelmet() != null && inventory.getHelmet().getType().equals(Material.DIAMOND_HELMET)){
				armour += 0.15;
			}
			
			if(inventory.getChestplate() != null && inventory.getChestplate().getType().equals(Material.DIAMOND_CHESTPLATE)){
				armour += 0.4;
			}
			
			if(inventory.getLeggings() != null && inventory.getLeggings().getType().equals(Material.DIAMOND_LEGGINGS)){
				armour += 0.3;
			}
			
			if(inventory.getBoots() != null && inventory.getBoots().getType().equals(Material.DIAMOND_BOOTS)){
				armour += 0.15;
			}
			
			break;
			
		case HEAVY:
			
			if(inventory.getHelmet() != null && inventory.getHelmet().getType().equals(Material.IRON_HELMET)){
				armour += 0.15;
			}
			
			if(inventory.getChestplate() != null && inventory.getChestplate().getType().equals(Material.IRON_CHESTPLATE)){
				armour += 0.4;
			}
			
			if(inventory.getLeggings() != null && inventory.getLeggings().getType().equals(Material.IRON_LEGGINGS)){
				armour += 0.3;
			}
			
			if(inventory.getBoots() != null && inventory.getBoots().getType().equals(Material.IRON_BOOTS)){
				armour += 0.15;
			}
			
			break;

		case LIGHT:
			
			if(inventory.getHelmet() != null && inventory.getHelmet().getType().equals(Material.LEATHER_HELMET)){
				armour += 0.15;
			}
			
			if(inventory.getChestplate() != null && inventory.getChestplate().getType().equals(Material.LEATHER_CHESTPLATE)){
				armour += 0.4;
			}
			
			if(inventory.getLeggings() != null && inventory.getLeggings().getType().equals(Material.LEATHER_LEGGINGS)){
				armour += 0.3;
			}
			
			if(inventory.getBoots() != null && inventory.getBoots().getType().equals(Material.LEATHER_BOOTS)){
				armour += 0.15;
			}
			
			break;
		
		case UNARMOURED:
			
			if(inventory.getHelmet() == null || inventory.getHelmet().getType().equals(Material.AIR)){
				armour += 0.15;
			}
			
			if(inventory.getChestplate() == null || inventory.getChestplate().getType().equals(Material.AIR)){
				armour += 0.4;
			}
			
			if(inventory.getLeggings() == null || inventory.getLeggings().getType().equals(Material.AIR)){
				armour += 0.3;
			}
			
			if(inventory.getBoots() == null || inventory.getBoots().getType().equals(Material.AIR)){
				armour += 0.15;
			}
			
			break;
		
		case ALL:
		
			armour = 1.0;
			
		default:
			break;
		}
	
		return armour;
		
		
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
	 * @param sagaFaction saga faction
	 */
	public void registerFaction(SagaFaction sagaFaction) {
		
		
		// Check if already registered:
		if(registeredFaction != null){
			Saga.severe(this, "tried to register a second " + sagaFaction + " faction", "ignoring request");
			return;
		}
		
		// Register:
		registeredFaction = sagaFaction;
		
		
	}
	
	/**
	 * Unregisters a faction.
	 * 
	 * @param sagaFaction saga faction
	 */
	public void unregisterFaction(SagaFaction sagaFaction) {
		
		
		// Check if not registered:
		if(registeredFaction == null){
			Saga.severe(this, "tried to unregister a non-registered " + sagaFaction + " faction", "ignoring request");
			return;
		}

		// Check if not a correct faction:
		if(registeredFaction != sagaFaction){
			Saga.severe(this, "tried to unregister an invalid " + sagaFaction + " faction", "ignoring request");
			return;
		}
		
		// Remove:
		registeredFaction = null;
		
		
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
	 * Gets the registered faction.
	 * 
	 * @return registered faction, null if none
	 */
	public SagaFaction getRegisteredFaction() {
		return registeredFaction;
	}

	/**
	 * Sets a faction ID for the player.
	 * 
	 * @param sagaFaction saga faction
	 */
	public void setFactionId(SagaFaction sagaFaction) {

		
		// Check if already on the list:
		if(factionId.equals(sagaFaction.getId())){
			Saga.severe(this, "tried to set a second " + sagaFaction + " faction ID", "continuing with the request");
		}
		
		// Add:
		factionId = sagaFaction.getId();
		
		
	}

	/**
	 * Removes a faction ID from the player.
	 * 
	 * @param id faction id
	 */
	public void removeFactionId(Integer id) {


		// Check if not set:
		if(factionId == -1){
			Saga.severe(this, "tried to remove a non-existing " + id + " faction ID", "continuing with the request");
		}

		// Check if not a correct faction:
		if(!factionId.equals(id)){
			Saga.severe(this, "tried to remove an invalid " + id + " faction ID", "continuing with the request");
		}
		
		// Add:
		factionId = -1;
		
		
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
	 * Sets a chunk group ID to the player.
	 * 
	 * @param chunkGroupId chunk group ID
	 */
	public void setChunkGroupId(Integer chunkGroupId) {


		// Check if already set:
		if(!this.chunkGroupId.equals(-1)){
			Saga.severe(this, "tried set a second " + chunkGroupId + " chunk group ID", "continuing with request");
		}
		
		// Set:
		this.chunkGroupId = chunkGroupId;
		
		
	}

	/**
	 * Removes a chunk group ID from the player.
	 * 
	 * @param chunkGroupId chunk group ID
	 */
	public void removeChunkGroupId(Integer chunkGroupId) {


		// Check if not set:
		if(this.chunkGroupId.equals(-1)){
			Saga.severe(this, "tried to remove a non-existant chunk group ID", "continuing with request");
		}

		// Check if not set:
		if(!this.chunkGroupId.equals(chunkGroupId)){
			Saga.severe(this, "tried to remove an invalid chunk group ID", "continuing with request");
		}
		
		// Remove:
		this.chunkGroupId = -1;
		
		
	}
	
	/**
	 * Gets the chunk group ID.
	 * 
	 * @return the chunk group ID
	 */
	public Integer getChunkGroupId() {
		return chunkGroupId;
	}
	
	/**
	 * Registers a chunk group.
	 * Will not add faction permanently to the player.
	 * 
	 * @param chunkGroup saga chunk group
	 */
	public void registerChunkGroup(ChunkGroup chunkGroup) {
		
		
		// Check if already on the list:
		if(registeredChunkGroup != null){
			Saga.severe(this, "tried to register a second chunk group", "ignoring request");
			return;
		}
		
		// Add:
		registeredChunkGroup = chunkGroup;
		
		
	}
	
	/**
	 * Unregisters a chunk group.
	 * Will not remove faction permanently to the player.
	 * 
	 * @param chunkGroup saga chunk group
	 */
	public void unregisterChunkGroup(ChunkGroup chunkGroup) {
		
		
		// Check if not on the list:
		if(registeredChunkGroup == null){
			Saga.severe(this, "tried to unregister a non-registered chunk group", "ignoring request");
			return;
		}
		
		// Remove:
		registeredChunkGroup = null;
		
		
	}
	
	/**
	 * Check if a chunk group is registered.
	 * 
	 * @param chunkGroup saga chunk group
	 * @return true if registered
	 */
	public boolean isChunkGroupRegistered(ChunkGroup chunkGroup) {

		return registeredChunkGroup != null;
		
	}

	/**
	 * Gets the registered chunk group.
	 * 
	 * @return the registered chunk group, null if none
	 */
	public ChunkGroup getRegisteredChunkGroup() {
		return registeredChunkGroup;
	}

	/**
	 * Checks if the player is part of the chunk group.
	 * 
	 * @param chunkGroup chunk group
	 * @return true if part of a chunk group
	 */
	public boolean hasChunkGroup(ChunkGroup chunkGroup) {
		
		return chunkGroup.equals(chunkGroup.getId());
		
	}

	/**
	 * Checks if the player has a chunk groups.
	 * 
	 * @return true if the player has chunk groups
	 */
	public boolean hasChunkGroup() {
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

	
	// Messages:
	/**
	 * Sends the player a message.
	 * 
	 * @param message message
	 */
	public void message(String message) {
		
		if(message.length() == 0) return;
		
		if(isOnline()){
        	TextUtil.messageLines(message, player);
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

	
	// Last location:
	/**
	 * Gets the last saga chunk.
	 * 
	 * @return last saga chunk, null if none
	 */
	public SagaChunk getLastSagaChunk() {
		return lastSagaChunk;
	}
	
	/**
	 * Sets last saga chunk.
	 * 
	 * @param lastSagaChunk last saga chunk, null if none
	 */
	public void setLastSagaChunk(SagaChunk lastSagaChunk) {
		this.lastSagaChunk = lastSagaChunk;
	}
	
	/**
	 * Gets the last location of the player.
	 * 
	 * @return player last location, null if none
	 */
	public Location getLastLocation() {
		return lastLocation;
	}
	
	/**
	 * Sets the last location.
	 * 
	 * @param lastLocation last location, null if none
	 */
	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}
	
	/**
	 * Forces a refresh on the last location.
	 * 
	 */
	public void refreshLocation() {
		
		
		if(!isOnline()){
			return;
		}
		
		lastLocation = player.getLocation();
		lastSagaChunk = ChunkGroupManager.manager().getSagaChunk(lastLocation);
		
		
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
	 * Puts a player on the given blocks center.
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
	public void shootFireball() {

		
		// Ignore if the player isn't online:
		if(!isOnline()){
			return;
		}
		
		// Shooter:
		EntityLiving shooter = ((CraftPlayer)player).getHandle();
		WorldServer serverWorld = ((CraftWorld) player.getWorld()).getHandle();
		Location shootLocation = player.getEyeLocation();

		// Direction vector:
		Vector directionVector = shootLocation.getDirection().normalize();
		
		// Shoot shift vector:
		double startShift = 2;
		Vector shootShiftVector = new Vector(directionVector.getX() * startShift, directionVector.getY() * startShift, directionVector.getZ() * startShift);
		
		// Empirical Accuracy:
		double accuracy = 4;
		
		// Shift shoot location:
		shootLocation = shootLocation.add(shootShiftVector.getX(), shootShiftVector.getY(), shootShiftVector.getZ());
		
		// Create the fireball:
		EntityFireball fireball = new EntityFireball(serverWorld, shooter, directionVector.getX() * accuracy, directionVector.getY() * accuracy, directionVector.getZ() * accuracy);
		fireball.getBukkitEntity().teleport(shootLocation);
		serverWorld.addEntity(fireball);

		// Remove fire:
		((CraftFireball)fireball.getBukkitEntity()).setIsIncendiary(false);
		
		// Send the event:
		SagaPlayerProjectileShotEvent event = new SagaPlayerProjectileShotEvent(this, ProjectileType.FIREBALL, 0);
//		Saga.playerListener().onSagaPlayerProjectileShot(event);
		
		// Cancel the event if needed:
		if(event.isCancelled()){
			fireball.getBukkitEntity().remove();
			return;
		}
		
		// Velocity vector:
		Vector velocityVector = directionVector.clone().multiply(1.5);
		
		// Set speed:
//		fireball.getBukkitEntity().setVelocity(shootShiftVector.multiply(speed));
		
		// Increase speed:
		fireball.getBukkitEntity().setVelocity(velocityVector);
		
		
	}
	
	/**
	 * Shoots lightning at the target.
	 * 
	 * @param targetLocation target
	 * @param selfDamage if true, the shooter may also be damaged
	 */
	public void shootLightning(Location targetLocation, boolean selfDamage) {

		
		// Ignore if the player isn't online:
		if(!isOnline()){
			return;
		}
		Entity damager = player;
		
		// Ground location:
		targetLocation = BlockConstants.groundLocation(targetLocation);
		
		// Get the craftworld.
		CraftWorld craftWorld = (CraftWorld) player.getWorld();
		
		// Strike lightning effect and get the resulting entity:
		Entity craftLightning = craftWorld.strikeLightningEffect(targetLocation);
		
		// Send damage events:
		java.util.List<Entity> damagedEntities = craftLightning.getNearbyEntities(1, 5, 1);
		ArrayList<EntityDamageByEntityEvent> damageEvents = new ArrayList<EntityDamageByEntityEvent>();
		for (int i = 0; i < damagedEntities.size(); i++) {
			EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, damagedEntities.get(i), DamageCause.LIGHTNING, BalanceConfiguration.config().baseLightningDamage);
			// Don't add self if damage self is false:
			if(selfDamage || !damagedEntities.get(i).equals(damager)){
				// Reset damage ticks:
				if(damagedEntities.get(i) instanceof LivingEntity) ((LivingEntity) damagedEntities.get(i)).setNoDamageTicks(0);
				// Call event:
				Bukkit.getServer().getPluginManager().callEvent(event);
				damageEvents.add(event);
			}
		}
		
		// Damage the puny eldar if the event is not canceled:
		for (EntityDamageByEntityEvent entityDamageByEntityEvent : damageEvents) {
			if(!entityDamageByEntityEvent.isCancelled()){
				Entity damaged = entityDamageByEntityEvent.getEntity();
				if(damaged instanceof LivingEntity){
					((LivingEntity) damaged).damage(entityDamageByEntityEvent.getDamage());
				}
			}
		}
		
		
	}

	/**
	 * Shoots lightning relative to the player.
	 * 
	 * @param relativeLocation location relative to the player
	 * @param selfDamage if true, the shooter may also be damaged
	 */
	public void shootLightning(Vector relativeLocation, boolean selfDamage) {

		
		// Ignore if the player isn't online:
		if(!isOnline()){
			return;
		}
		Entity damager = player;
		
		Location target = damager.getLocation().clone().add(relativeLocation.getX(), 0, relativeLocation.getZ());
		
		shootLightning(target, selfDamage);
		
		
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
		
		Arrow arrow = player.shootArrow();
		
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

	/**
	 * Plays a spell cast effect.
	 * 
	 */
	public void playeSpellEffect() {
		
		
		if(!isOnline()) return;
		
		// Smoke:
		for (int i = 5; i <= 12; i++) {
			playGlobalEffect(Effect.SMOKE, i);
		}
		
		// Sound:
		playGlobalEffect(Effect.BLAZE_SHOOT, 0);
		
		
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
	 * Gets the saga chunk the player is standing on.
	 * 
	 * @return saga chunk. null if not found
	 */
	public SagaChunk getSagaChunk(){
		
		
		if(!isOnline()){
			return null;
		}
		
		Location location = player.getLocation();
		
		if(lastSagaChunk != null && lastSagaChunk.represents(location)){
			return lastSagaChunk;
		}
		
		return ChunkGroupManager.manager().getSagaChunk(location);
		
				
	}

	/**
	 * Gets the player location.
	 * 
	 * @return player location. null if no location
	 */
	public Location getLocation() {

		
		if(!isOnline()){
			return null;
		}
		return player.getLocation();
		
		
	}

	
	// Reward:
	/**
	 * Gets the reward.
	 * 
	 * @return the reward
	 */
	public Integer getReward() {
		return reward;
	}
	
	/**
	 * Rewards the player.
	 * 
	 * @param reward reward
	 */
	public void reward(int reward) {
		this.reward += reward;
	}
	
	/**
	 * Clears the reward.
	 */
	public void clearReward() {
		reward = 0;
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
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#getItemCount(org.bukkit.Material)
	 */
	@Override
	public Integer getItemCount(Material material) {
		
		if(isOnline()){
			
			return InventoryUtil.getItemCount(material, player.getInventory().getContents());
			
		}
		return 0;
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#getBuyItemValues()
	 */
	@Override
	public ArrayList<Transaction> getTransactions() {
		return new ArrayList<Transaction>();
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#getTradeDeals()
	 */
	@Override
	public ArrayList<TradeDeal> getTradeDeals() {
		return new ArrayList<TradeDeal>();
	}
	
	
	// Events:
	/**
	 * Player join event.
	 * 
	 * @param event event
	 */
	public void playerJoinEvent2(PlayerJoinEvent event) {

		// Forward to chunk group:
		if(registeredChunkGroup != null) registeredChunkGroup.onMemberJoin(this, event);
		// TODO: redo chunk group registering from events
		
		
		
	}
	
	/**
	 * Player quit event.
	 * 
	 * @param event event
	 */
	public void playerQuitEvent(PlayerQuitEvent event) {

		// Forward to chunk group:
		if(registeredChunkGroup != null) registeredChunkGroup.onMemberQuit(this, event);
		
	}
	
	/**
	 * Called when the guardian rune absorbed items.
	 * 
	 */
	public void onGuardRuneAbsorption() {

		if(!isClockEnabled()) enableClock();
		
	}

	/**
	 * Called when the saga player is created.
	 * 
	 */
	public void onSagaPlayerCreation() {

		
		// Random profession:
		if(ProficiencyConfiguration.config().initialProfession){
			
			ArrayList<String> professions =  ProficiencyConfiguration.config().getProficiencyNames(ProficiencyType.PROFESSION);
			
			if(professions.size() != 0 && profession == null){
				
				String name = professions.get(new Random().nextInt(professions.size()));
				
				try {
					
					Proficiency profession = ProficiencyConfiguration.config().createProficiency(name);
					setProfession(profession);
					
				} catch (InvalidProficiencyException e) {
					Saga.severe(this, "failed to create proficiency: " + e.getClass().getSimpleName() + ": " + e.getMessage(), "ignoring request");
				}
				
			}
			
		}
		
		// Random class:
		if(ProficiencyConfiguration.config().initialClass){
			
			ArrayList<String> clazzes =  ProficiencyConfiguration.config().getProficiencyNames(ProficiencyType.CLASS);
			
			if(clazzes.size() != 0 && classs == null){
				
				String name = clazzes.get(new Random().nextInt(clazzes.size()));
				
				try {
					
					Proficiency profession = ProficiencyConfiguration.config().createProficiency(name);
					setClass(profession);
					
				} catch (InvalidProficiencyException e) {
					Saga.severe(this, "failed to create proficiency: " + e.getClass().getSimpleName() + ": " + e.getMessage(), "ignoring request");
				}
				
			}
			
		}
		
		
	}
	
	
	// Clock:
	/**
	 * Sends a clock tick.
	 *
	 * @param tick tick number
	 */
	@Override
	public void clockSecondTick() {
		
		
		if(!getGuardianRune().isEmpty() && isOnline()){
			
			if(!getPlayer().isDead()){
				GuardianRune.handleRestore(this);
			}
			
		}
		
		// Disable clock:
		if(!proceedClock()){
			disableClock();
		}
		
		
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
	 * Disable the clock.
	 * 
	 */
	private void disableClock() {

		Clock.clock().unregisterSecondTick(this);
		
		clockEnabled = false;
		
	}
	
	/**
	 * Checks if the clock should proceed.
	 * 
	 * @return true if should proceed
	 */
	private boolean proceedClock() {

		
		if(!getGuardianRune().isEmpty()) return true;
		
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
            
            // Try loading:
            try {

                sagaPlayer = WriterReader.readPlayerInformation(playerName.toLowerCase());

                // Complete:
                sagaPlayer.complete();

                
            } catch (FileNotFoundException e) {

                Saga.info("Player information file not found for " + playerName + ". Loading default.");
                sagaPlayer = new SagaPlayer(playerName);

                // Create event:
                sagaPlayer.onSagaPlayerCreation();
                
            } catch (IOException e) {

                Saga.severe(SagaPlayer.class, "player information file read failure for " + playerName + ":" + e.getClass().getSimpleName() + ":" + e.getMessage() ,"setting defaults");
                Saga.info("disabling player information saving");
                sagaPlayer= new SagaPlayer(playerName);
                sagaPlayer.setSavingEnabled(false);

            } catch (JsonParseException e) {

            	Saga.severe(SagaPlayer.class, "player information parse load failure for " + playerName + ":" + e.getClass().getSimpleName() + ":" + e.getMessage() ,"setting defaults");
                Saga.info("disabling player information saving");
                Saga.info("Parse message: " + e.getMessage());
                sagaPlayer= new SagaPlayer(playerName);
                sagaPlayer.setSavingEnabled(false);

            }

    		// Enable clock:
    		if(sagaPlayer.proceedClock()){
    			sagaPlayer.enableClock();
    		}
            
            return sagaPlayer;
				

	}
	
	/**
	 * Unloads player information. Will not save if {@link #isSavingEnabled()} is false.
	 */
	public void unload() {
		

		// Save:
		save();
		
		// Disable clock:
		if(isClockEnabled()) disableClock();
		
	}

	/**
	 * Saves player information. Will not save if {@link #isSavingEnabled()} is false.
	 */
	public void save() {
		
		
            if( isSavingEnabled() ) {

                try {
                    WriterReader.writePlayerInformation(getName().toLowerCase(), this);
                } catch (Exception e) {
                    Saga.severe(this, "player information save failure:" + e.getClass().getSimpleName() + ":" + e.getMessage(), "ignoring save");
                }

            } else {
            	Saga.severe(this, "player information save denied", "ignoring save");
            }

		
	}

	
	/**
	 * Checks if the player exists.
	 * 
	 * @param name
	 * @return
	 */
	public static boolean checkExistance(String name){
		return WriterReader.playerExists(name.toLowerCase());
	}
	
	
	// Guardian rune:
	/**
	 * Gets the guardian rune.
	 * 
	 * @return guardian rune
	 */
	public GuardianRune getGuardianRune() {
		return guardianStone;
	}

	/**
	 * Makes the guardian rune absorb items.
	 * 
	 */
	public void guardianRuneAbsorb() {

		
		if(!isOnline()) return;
		
		// Empty:
		if(!guardianStone.isEmpty()){
			Saga.severe(this, "tried to absorb with a non-empty guardian rune", "ignoring request");
			return;
		}
		
		// Absorb:
		guardianStone.absorb(getPlayer());
		
		
	}

	/**
	 * Makes the guardian rune absorb items.
	 * 
	 */
	public void guardianRuneDischarge() {

		if(!isOnline()) return;
		
		// Not charged:
		if(!guardianStone.getStatus().equals(GuardianRuneStatus.CHARGED)){
			Saga.severe(this, "tried to discharge a non-charged guardian rune", "ignoring request");
			return;
		}
		
		// Discharge:
		guardianStone.discharge();
		
		
	}
	
	/**
	 * Restores items from the guardian rune.
	 * 
	 */
	public void guardianRuneRestore() {

		
		if(!isOnline()) return;

		// Empty:
		if(guardianStone.isEmpty()){
			Saga.severe(this, "tried to restore from an empty guardian rune", "ignoring request");
			return;
		}
		
		// Restore:
		guardianStone.restore(getPlayer());
		
		
	}

	/**
	 * Checks if there is anything to absorb.
	 * 
	 * @return true if can be absorbed
	 */
	public boolean checkGuardRuneAbsorb() {

		
		if(!isOnline()) return false;

		Player player = getPlayer();
		
		// Level:
		if(getLevel() > 0) return true;
		
		// Items:
		ItemStack[] items = player.getInventory().getContents();
		for (int i = 0; i < items.length; i++) {
			
			if(items[i] != null && !items[i].getType().equals(Material.AIR)) return true;
			
		}

		// Armour:
		ItemStack[] armour = player.getInventory().getArmorContents();
		for (int i = 0; i < armour.length; i++) {
			
			if(armour[i] != null && !armour[i].getType().equals(Material.AIR)) return true;
			
		}
		
		return false;
		
		
	}
	
	
	// Time:
	/**
	 * Gets last online date.
	 * 
	 * @return last online date, current time if online
	 */
	public Date getLastOnline() {
		
		
		if(isOnline()) return Calendar.getInstance().getTime();
		
		return lastOnline;
		
		
	}
	
	
	// Experience:
	/**
	 * Adds experience to regeneration.
	 * 
	 * @param amount amount of experience.
	 */
	public void addRegenExp(int amount) {
		
		expRegen += amount;
		
		// Normalize:
		if(expRegen > BalanceConfiguration.config().expRegenLimit){
			expRegen = BalanceConfiguration.config().expRegenLimit;
		}
		
	}
	
	/**
	 * Regenerates experience.
	 * 
	 * @return amount regenerated, 0 if none
	 */
	public int regenExp() {
		
		
		if(isOnline()){
			
			int regenAmount = SagaEntityDamageManager.randomRound(BalanceConfiguration.config().expRegenSpeed);
			
			// Normalize:
			if(regenAmount > expRegen){
				regenAmount = expRegen;
			}
			if(regenAmount <= 0) return 0;
			
			// Limit level:
			if(getLevel() >= BalanceConfiguration.config().maximumLevel){
				return 0;
			}
			
			// Subtract:
			expRegen -= regenAmount;
			
			// Give exp:
			player.giveExp(regenAmount);
			
			return regenAmount;
			
		}else{
			
			return 0;
			
		}

		
	}
	
	/**
	 * Gets the amount of experience regeneration.
	 * 
	 * @return amount of experience regeneration
	 */
	public Integer getExpRegen() {
		return expRegen;
	}


	// Experience events:
	/**
	 * Called when a block is broken by the player.
	 * 
	 * @param event event
	 */
	public void onBlockExp(BlockBreakEvent event) {

		
		// Profession:
		if(profession != null){
			Double expAmount = profession.getDefinition().calcExp(event.getBlock());
			
			if(expAmount != 0){
				
				giveExperience(expAmount);

				// Statistics:
				StatisticsManager.manager().onExp(this, profession.getName(), "block", expAmount);
				
			}
			
		}

		// Class:
		if(classs != null){
			Double expAmount = classs.getDefinition().calcExp(event.getBlock());
			
			if(expAmount != 0){
				
				giveExperience(expAmount);

				// Statistics:
				StatisticsManager.manager().onExp(this, profession.getName(), "block", expAmount);
				
			}
			
		}
		
		
	}
	
	/**
	 * Called when a creature is killed.
	 * 
	 * @param event event
	 * @param creature creature
	 */
	public void onCreatureExp(EntityDeathEvent event, Creature creature) {

		
		// Profession:
		if(profession != null){
			Double expAmount = profession.getDefinition().calcExp(creature);
			
			if(expAmount != 0){
				
				giveExperience(expAmount);

				// Statistics:
				StatisticsManager.manager().onExp(this, profession.getName(), "creature", expAmount);
				
			}
			
		}

		// Class:
		if(classs != null){
			Double expAmount = classs.getDefinition().calcExp(creature);
			
			if(expAmount != 0){
				
				giveExperience(expAmount);

				// Statistics:
				StatisticsManager.manager().onExp(this, profession.getName(), "creature", expAmount);
				
			}
			
		}
		
		
	}
	
	/**
	 * Called when a creature is killed.
	 * 
	 * @param event event
	 * @param killedPlayer killed saga player
	 */
	public void onPlayerExp(EntityDeathEvent event, SagaPlayer killedPlayer) {

		
		// Profession:
		if(profession != null){
			Double expAmount = profession.getDefinition().calcExp(killedPlayer);
			
			if(expAmount != 0){
				
				giveExperience(expAmount);

				// Statistics:
				StatisticsManager.manager().onExp(this, profession.getName(), "player", expAmount);
				
			}
			
		}

		// Class:
		if(classs != null){
			Double expAmount = classs.getDefinition().calcExp(killedPlayer);
			
			if(expAmount != 0){
				
				giveExperience(expAmount);

				// Statistics:
				StatisticsManager.manager().onExp(this, profession.getName(), "player", expAmount);
				
			}
			
		}
		
		
	}
	
	/**
	 * Called when an ability is used.
	 * 
	 * @param ability ability
	 * @param proficiency proficiency, null if none
	 * @param expAmount experience
	 */
	public void onAbilityExp(Ability ability, Proficiency proficiency, Double expAmount) {

		
		if(expAmount == 0) return;
		
		giveExperience(expAmount);

		// Statistics:
		if(profession != null){
			StatisticsManager.manager().onExp(this, profession.getName(), ability.getName(), expAmount);
		}else{
			StatisticsManager.manager().onExp(this, "-", ability.getName(), expAmount);
		}
		
		
	}
	
	
	// Mining history:
	/**
	 * Gets the amount of block mined.
	 * 
	 * @param material block material
	 * @return amount mined
	 */
	public Integer getMinedBlocks(Material material) {
		
		Integer mined = miningStatistics.get(material);
		if(mined == null) mined = 0;
		
		return mined;
		
	}
	
	/**
	 * Adds mined blocks.
	 * 
	 * @param material material
	 * @param amount amount mined
	 * @return new amount
	 */
	public Integer addMinedBlocks(Material material, Integer amount) {
		
		Integer newAmount = getMinedBlocks(material) + amount;
		
		miningStatistics.put(material, newAmount);
		
		return newAmount;
		
	}
	
	/**
	 * Clears mining amount for the given material.
	 * 
	 * @param material material
	 */
	public void clearMinedBlocks(Material material) {

		miningStatistics.remove(material);
		
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
	 * @param savingDisabled true if player information should be disabled 
	 */
	public void setSavingEnabled(boolean savingDisabled) {
            this.isSavingEnabled = savingDisabled;
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
