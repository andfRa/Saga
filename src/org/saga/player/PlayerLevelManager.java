package org.saga.player;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.Saga;
import org.saga.abilities.Ability;
import org.saga.abilities.AbilityDefinition.ActivationAction;
import org.saga.abilities.AbilityDefinition.ActivationType;
import org.saga.config.SkillConfiguration;

public class PlayerLevelManager{

	
	/**
	 * Skills.
	 */
	private ArrayList<Skill> damageSkills;
	
	/**
	 * Skill multipliers.
	 */
	private ArrayList<Integer> skillMultipliers;
	
	/**
	 * Saga player.
	 */
	private SagaPlayer sagaPlayer;
	
	/**
	 * Binded abilities.
	 */
	private HashSet<Ability> abilities = new HashSet<Ability>();

	
	// Initialization:
	/**
	 * Initializes.
	 * 
	 * @param sagaPlayer Saga player
	 */
	public PlayerLevelManager(SagaPlayer sagaPlayer) {
		
		
		this.sagaPlayer = sagaPlayer;
		this.damageSkills = new ArrayList<Skill>();
		this.skillMultipliers = new ArrayList<Integer>();
		
		
		
	}
	
	
	// Update:
	/**
	 * Updates the manager.
	 * 
	 */
	public void update() {

		updateSkills();
		
		updateAbilities();
		
	}
	
	/**
	 * Updates skills.
	 * 
	 */
	private void updateSkills() {

		
		damageSkills = new ArrayList<Skill>();
		skillMultipliers = new ArrayList<Integer>();
		
		Hashtable<String, Skill> allSkills = SkillConfiguration.config().getSkills();
		HashSet<String> enabledSkills = sagaPlayer.getEnabledSkills();
		
		// Add skills:
		Enumeration<String> skillNames = allSkills.keys();
		while (skillNames.hasMoreElements()) {
			
			String skillName = skillNames.nextElement();
			Integer skillMultiplier = sagaPlayer.getModifiedSkillMultiplier(skillName);
			
			// Ignore skills with zero level:
			if(skillMultiplier == null || !enabledSkills.contains(skillName)) continue;
			
			// Add:
			damageSkills.add(allSkills.get(skillName));
			skillMultipliers.add(skillMultiplier);
			
		}
		
		
	}
	
	/**
	 * Updates abilities.
	 * 
	 */
	private void updateAbilities() {

		abilities = sagaPlayer.getLearnedAbilities();
		
	}
	
	/**
	 * Gets all player abilities.
	 * 
	 * @return all player abilities
	 */
	public HashSet<Ability> getAllAbilities() {


		return new HashSet<Ability>(abilities);
		
		
	}
	
	
	// Getters:
	/**
	 * Gets the skills.
	 * 
	 * @return the skills
	 */
	public ArrayList<Skill> getDamageSkills2() {
		return new ArrayList<Skill>(damageSkills);
	}

	/**
	 * Gets the skill multiplier.
	 * 
	 * @param skillName skill name
	 * @return multiplier, 0 if none
	 */
	public Integer getSkillMultiplier(String skillName) {
		
		
		ArrayList<Skill> skills = damageSkills;
		for (int i = 0; i < skills.size(); i++) {
			
			if(skills.get(i).getName().equals(skillName)){
				return skillMultipliers.get(i);
			}
			
		}
		
		return 0;
		
		
	}
	
	/**
	 * Gets the maximum skill multiplier.
	 * 
	 * @param skillName skill name
	 * @return multiplier, 0 if none
	 */
	public Integer getMaxSkillMultiplier(String skillName) {
		
		return sagaPlayer.getSkillMaximum(skillName);
		
	}

	
	// Activation:
	/**
	 * Activates every possible ability.
	 * 
	 * @param action activation action
	 * @param material activation material
	 * @param event event
	 */
	private void activate(ActivationAction action, Material material, PlayerInteractEvent event) {

		
		// No activation:
		if(action.equals(ActivationAction.NONE)) return;
		
		// Profession:
		for (Ability selectedAbility : abilities) {
			
			String abilityName = selectedAbility.getName();
			
			// Check if binded:
			if(!sagaPlayer.isAbilityBinded(abilityName, material, action)) continue;
			
			// Type:
			ActivationType type = selectedAbility.getActivationType();
			
			// Activation:
			selectedAbility.handleActivate();
			
			// Instant:
			if(type.equals(ActivationType.INSTANT)){
				
				// Trigger:
				boolean triggered = selectedAbility.instant(event);

				// Notify usage:
				if(triggered) selectedAbility.handleAfterUse();
				
			}
			
			
		}
		
		
	}

	
	// Events:
	/**
	 * Called when the player interacts.
	 * 
	 * @param event event
	 */
	public void onPlayerInteract(PlayerInteractEvent event) {

		
		ItemStack itemInhand = event.getItem();
		if(itemInhand == null) itemInhand = new ItemStack(Material.AIR);
		
		// Get action:
		ActivationAction actAction = null;
		
		// Left click:
		if( (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))){
			actAction = ActivationAction.LEFT_CLICK;
		}

		// Right click:
		else if( (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
			actAction = ActivationAction.RIGHT_CLICK;
		}
		
		// Pressure plates:
		else if(event.getAction().equals(Action.PHYSICAL)){
			actAction = ActivationAction.NONE;
		}

		else{
			Saga.severe(this, event.getAction() + " isn't a valid action", "ignoring activation");
			return;
		}
		
		// Activate abilities:
		activate(actAction, itemInhand.getType(), event);
		
		
	}
	
	/**
	 * Called when the player interacts with an player.
	 * 
	 * @param event event
	 */
	public void onPlayerInteractPlayer(PlayerInteractEntityEvent event, SagaPlayer tragetPlayer) {


		// Trigger active abilities:
		for (Ability ability : abilities) {

			// Active:
			if(!ability.isActive()) continue;
			
			// Trigger:
			boolean triggered = ability.onPlayerInteractPlayer(event, tragetPlayer);
			
			// Notify usage:
			if(triggered) ability.handleAfterUse();
			
		}
		
		
	}
	
	/**
	 * Called when the player interacts with an creature.
	 * 
	 * @param event event
	 */
	public void onPlayerInteractCreature(PlayerInteractEntityEvent event, Creature targetCreature) {


		// Trigger active abilities:
		for (Ability ability : abilities) {

			// Active:
			if(!ability.isActive()) continue;
			
			// Trigger:
			boolean triggered = ability.onPlayerInteractCreature(event, targetCreature);
			
			// Notify usage:
			if(triggered) ability.handleAfterUse();
			
		}
		
		
	}
	
	
	/**
	 * Called when the player gets damaged by a creature.
	 * 
	 * @param event event
	 */
	public void onHitByCreature(EntityDamageByEntityEvent event, Creature creature) {
		
		
		ArrayList<Skill> skills = new ArrayList<Skill>(this.damageSkills);
		ArrayList<Integer> skillMultipliers = new ArrayList<Integer>(this.skillMultipliers);

		// Trigger skills:
		for (int i = 0; i < skills.size(); i++) {
			skills.get(i).triggerHitByCreature(sagaPlayer, skillMultipliers.get(i) ,event, creature);
		}

		// Trigger active abilities:
		for (Ability ability : abilities) {
			
			// Active:
			if(!ability.isActive()) continue;
			
			// Trigger:
			boolean triggered = ability.onHitByCreature(event, creature);
			
			// Notify usage:
			if(triggered) ability.handleAfterUse();
			
		}
		
		
	}
	
	/**
	 * Called when the player damages a creature.
	 * 
	 * @param event event
	 */
	public void onHitCreature(EntityDamageByEntityEvent event, Creature creature) {
		
		
		ArrayList<Skill> skills = new ArrayList<Skill>(this.damageSkills);
		ArrayList<Integer> skillMultipliers = new ArrayList<Integer>(this.skillMultipliers);

		// Trigger skills:
		for (int i = 0; i < skills.size(); i++) {
			skills.get(i).triggerHitCreature(sagaPlayer, skillMultipliers.get(i) ,event, creature);
		}

		// Trigger active abilities:
		for (Ability ability : abilities) {

			// Active:
			if(!ability.isActive()) continue;
			
			// Trigger:
			boolean triggered = ability.onHitCreature(event, creature);
			
			// Notify usage:
			if(triggered) ability.handleAfterUse();
			
		}
		
		
	}

	/**
	 * Called when the player gets damaged by a player.
	 * 
	 * @param event event
	 */
	public void onHitByPlayer(EntityDamageByEntityEvent event, SagaPlayer attacker) {
		
		
		ArrayList<Skill> skills = new ArrayList<Skill>(this.damageSkills);
		ArrayList<Integer> skillMultipliers = new ArrayList<Integer>(this.skillMultipliers);

		// Trigger skills:
		for (int i = 0; i < skills.size(); i++) {
			skills.get(i).triggerHitByPlayer(sagaPlayer, skillMultipliers.get(i) ,event, attacker);
		}
		
		// Trigger active abilities:
		for (Ability ability : abilities) {

			// Active:
			if(!ability.isActive()) continue;
			
			// Trigger:
			boolean triggered = ability.onHitByPlayer(event, attacker);
			
			// Notify usage:
			if(triggered) ability.handleAfterUse();
			
		}
		
		
	}
	
	/**
	 * Called when the player hits a player.
	 * 
	 * @param event event
	 */
	public void onHitPlayer(EntityDamageByEntityEvent event, SagaPlayer defender) {
		
		
		ArrayList<Skill> skills = new ArrayList<Skill>(this.damageSkills);
		ArrayList<Integer> skillMultipliers = new ArrayList<Integer>(this.skillMultipliers);

		// Trigger skills:
		for (int i = 0; i < skills.size(); i++) {
			skills.get(i).triggerHitPlayer(sagaPlayer, skillMultipliers.get(i) ,event, defender);
		}

		// Trigger active abilities:
		for (Ability ability : abilities) {
			
			// Active:
			if(!ability.isActive()) continue;
			
			// Trigger:
			boolean triggered = ability.onHitPlayer(event, defender);
			
			// Notify usage:
			if(triggered) ability.handleAfterUse();
			
		}
		
		
	}

	
	/**
	 * Called when the player gets shot by a creature.
	 * 
	 * @param event event
	 */
	public void onShotByCreature(EntityDamageByEntityEvent event, Creature creature, Projectile projectile) {
		
		
		ArrayList<Skill> skills = new ArrayList<Skill>(this.damageSkills);
		ArrayList<Integer> skillMultipliers = new ArrayList<Integer>(this.skillMultipliers);

		// Trigger skills:
		for (int i = 0; i < skills.size(); i++) {
			skills.get(i).triggerShotByCreature(sagaPlayer, skillMultipliers.get(i) ,event, creature, projectile);
		}

		// Trigger active abilities:
		for (Ability ability : abilities) {

			// Active:
			if(!ability.isActive()) continue;
			
			// Trigger:
			boolean triggered = ability.onShotByCreature(event, creature, projectile);

			// Notify usage:
			if(triggered) ability.handleAfterUse();
			
		}
		
		
	}
	
	/**
	 * Called when the player shoots a creature.
	 * 
	 * @param event event
	 */
	public void onShotCreature(EntityDamageByEntityEvent event, Creature creature, Projectile projectile) {
		
		
		ArrayList<Skill> skills = new ArrayList<Skill>(this.damageSkills);
		ArrayList<Integer> skillMultipliers = new ArrayList<Integer>(this.skillMultipliers);

		// Trigger skills:
		for (int i = 0; i < skills.size(); i++) {
			skills.get(i).triggerShotCreature(sagaPlayer, skillMultipliers.get(i) ,event, creature, projectile);
		}
		
		// Trigger active abilities:
		for (Ability ability : abilities) {

			// Active:
			if(!ability.isActive()) continue;
			
			// Trigger:
			boolean triggered = ability.onShotCreature(event, creature, projectile);

			// Notify usage:
			if(triggered) ability.handleAfterUse();
			
		}
		
		
	}

	/**
	 * Called when the player gets shot by a player.
	 * 
	 * @param event event
	 */
	public void onShotByPlayer(EntityDamageByEntityEvent event, SagaPlayer attacker, Projectile projectile) {
		
		
		ArrayList<Skill> skills = new ArrayList<Skill>(this.damageSkills);
		ArrayList<Integer> skillMultipliers = new ArrayList<Integer>(this.skillMultipliers);

		// Trigger skills:
		for (int i = 0; i < skills.size(); i++) {
			skills.get(i).triggerShotByPlayer(sagaPlayer, skillMultipliers.get(i) ,event, attacker, projectile);
		}

		// Trigger active abilities:
		for (Ability ability : abilities) {

			// Active:
			if(!ability.isActive()) continue;
			
			// Trigger:
			boolean triggered = ability.onShotByPlayer(event, attacker, projectile);

			// Notify usage:
			if(triggered) ability.handleAfterUse();
			
		}
		
		
	}
	
	/**
	 * Called when the player shoots a player.
	 * 
	 * @param event event
	 */
	public void onShotPlayer(EntityDamageByEntityEvent event, SagaPlayer defender, Projectile projectile) {
		
		
		ArrayList<Skill> skills = new ArrayList<Skill>(this.damageSkills);
		ArrayList<Integer> skillMultipliers = new ArrayList<Integer>(this.skillMultipliers);

		// Trigger skills:
		for (int i = 0; i < skills.size(); i++) {
			skills.get(i).triggerShotPlayer(sagaPlayer, skillMultipliers.get(i) ,event, defender, projectile);
		}

		// Trigger active abilities:
		for (Ability ability : abilities) {

			// Active:
			if(!ability.isActive()) continue;
			
			// Trigger:
			boolean triggered = ability.onShotPlayer(event, defender, projectile);

			// Notify usage:
			if(triggered) ability.handleAfterUse();
			
		}
		
		
	}
	

	/**
	 * Called when magic is casted by the player.
	 * 
	 * @param event event
	 */
	public void onSpelledCreature(EntityDamageByEntityEvent event, Creature defender, Projectile projectile) {

		
		ArrayList<Skill> skills = new ArrayList<Skill>(this.damageSkills);
		ArrayList<Integer> skillMultipliers = new ArrayList<Integer>(this.skillMultipliers);

		// Trigger skills:
		for (int i = 0; i < skills.size(); i++) {
			skills.get(i).triggerSpelledCreature(sagaPlayer, skillMultipliers.get(i) ,event, defender, projectile);
		}

//		// Trigger active abilities:
//		for (Ability ability : abilities) {
//
//			// Binded:
//			if(!isBinded(ability)) continue;
//			
//			// Active:
//			if(!ability.isActive()) continue;
//		
//			// Active:
//			if(!ability.isActive()) continue;
//			
//			// Trigger:
//			boolean triggered = ability.onSpelledPlayer(event, defender, projectile);
//
//			// Notify usage:
//			if(triggered) ability.onUse();
//			
//		}
		
		
		
	}
	
	/**
	 * Called when magic is casted by the player.
	 * 
	 * @param event event
	 */
	public void onSpelledPlayer(EntityDamageByEntityEvent event, SagaPlayer defender, Projectile projectile) {

		
		ArrayList<Skill> skills = new ArrayList<Skill>(this.damageSkills);
		ArrayList<Integer> skillMultipliers = new ArrayList<Integer>(this.skillMultipliers);

		// Trigger skills:
		for (int i = 0; i < skills.size(); i++) {
			skills.get(i).triggerSpelledPlayer(sagaPlayer, skillMultipliers.get(i) ,event, defender, projectile);
		}

		// Trigger active abilities:
		for (Ability ability : abilities) {

			// Active:
			if(!ability.isActive()) continue;
			
			// Trigger:
			boolean triggered = ability.onSpelledPlayer(event, defender, projectile);

			// Notify usage:
			if(triggered) ability.handleAfterUse();
			
		}
		
		
		
	}
	
	/**
	 * Called when magic is casted by the player.
	 * 
	 * @param event event
	 */
	public void onSpelledByPlayer(EntityDamageByEntityEvent event, SagaPlayer attacker, Projectile projectile) {
		
		
		ArrayList<Skill> skills = new ArrayList<Skill>(this.damageSkills);
		ArrayList<Integer> skillMultipliers = new ArrayList<Integer>(this.skillMultipliers);

		// Trigger skills:
		for (int i = 0; i < skills.size(); i++) {
			skills.get(i).triggerSpelledByPlayer(sagaPlayer, skillMultipliers.get(i) ,event, attacker, projectile);
		}

		// Trigger active abilities:
		for (Ability ability : abilities) {

			// Active:
			if(!ability.isActive()) continue;
			
			// Trigger:
			boolean triggered = ability.onSpelledByPlayer(event, attacker, projectile);

			// Notify usage:
			if(triggered) ability.handleAfterUse();
			
		}
		
		
	}
	
	
	/**
	 * Called when the player is damaged by the environment.
	 * 
	 * @param event event
	 */
	public void onDamageByEnvironment(EntityDamageEvent event) {
		
	}

	/**
	 * Called when the player breaks a block.
	 * 
	 * @param event event
	 */
	public void onBlockBrake(BlockBreakEvent event) {

		
		ArrayList<Skill> skills = new ArrayList<Skill>(this.damageSkills);
		
		// Trigger skills:
		for (int i = 0; i < skills.size(); i++) {
			skills.get(i).triggerBlockBrake(sagaPlayer, skillMultipliers.get(i) ,event);
		}

		
	}
	
	
	// Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return sagaPlayer.getName();
	}
	
	
}
