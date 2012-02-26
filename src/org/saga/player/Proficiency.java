package org.saga.player;


import java.util.ArrayList;
import java.util.HashSet;

import org.saga.Saga;
import org.saga.abilities.Ability;
import org.saga.config.AbilityConfiguration.InvalidAbilityException;
import org.saga.config.BalanceConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.ProficiencyConfiguration.InvalidProficiencyException;
import org.saga.factions.SagaFaction.FactionPermission;
import org.saga.settlements.Settlement.SettlementPermission;

public class Proficiency {

	
	// General:
	/**
	 * Profession name.
	 */
	private String name;

	/**
	 * Selected abilities.
	 */
	private HashSet<String> selectedAbilities;
	
	/**
	 * Second abilities.
	 */
	private ArrayList<Ability> abilities;
	
	
	/**
	 * Contains all information needed for the proficiency.
	 */
	transient private ProficiencyDefinition definition;
	
	
	// Access:
	/**
	 * Saga player.
	 */
	transient protected SagaPlayer sagaPlayer = null;

	
	// Initialization:
	/**
	 * Used by gson.
	 * 
	 */
	@SuppressWarnings("unused")
	private Proficiency() {
	}
	
	/**
	 * Creates a proficiency based on definition.
	 * 
	 * @param definition definition
	 */
	public Proficiency(ProficiencyDefinition definition) {
		
		this.name = definition.getName();
		this.definition = definition;
		abilities = new ArrayList<Ability>();
		selectedAbilities = new HashSet<String>();
		
	}
	
	/**
	 * Completes.
	 * 
	 * @return integrity
	 * @throws InvalidProficiencyException if the definition can not be found
	 */
	public boolean complete() throws InvalidProficiencyException {
		
		
		boolean integrity = true;
		
		if(name == null){
			name = "null proficiency";
			Saga.severe(this, "failed to initialize name field", "setting default");
			integrity = false;
		}

		// Retrieve definition:
		definition = ProficiencyConfiguration.config().getDefinition(getName());
		if(definition == null){
			throw new InvalidProficiencyException(getName());
		}
		
		// First abilities:
		if(abilities == null){
			abilities = new ArrayList<Ability>();
			Saga.severe(this, "failed to initialize abilities field", "setting default");
			integrity = false;
		}
		for (int i = 0; i < abilities.size(); i++) {
			
			if(abilities.get(i) == null){
				Saga.severe(this, "failed to initialize abilities field element", "removing element");
				abilities.remove(i);
				i--;
				continue;
			}
			
			try {
				integrity = abilities.get(i).complete() && integrity;
			} catch (InvalidAbilityException e) {
				Saga.severe(this, "failed to complete abilities field element: " + e.getClass().getSimpleName() + ":" + e.getMessage(), "removing element");
				abilities.remove(i);
				i--;
				continue;
			}
			
		}
		if(selectedAbilities == null){
			selectedAbilities = new HashSet<String>();
			Saga.severe(this, "failed to initialize selectedAbilities field", "setting default");
			integrity = false;
		}
		
		// Transient:
		
		return integrity;
		
		
	}

	/**
	 * Sets the player.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void setPlayer(SagaPlayer sagaPlayer) {
		
		this.sagaPlayer= sagaPlayer;
		
		for (Ability ability : abilities) {
			ability.setPlayer(sagaPlayer);
		}
		
	}

	/**
	 * Creates a proficiency based on definition.
	 * 
	 * @param definition definition
	 */
	public static Proficiency create(ProficiencyDefinition definition){
		
		return new Proficiency(definition);
		
	}
	
	
	// Interaction:
	/**
	 * Returns the proficiency name.
	 * 
	 * @return the proficiency name
	 */
	public String getName(){
		
		return name;
		
	}

	/**
	 * Fixes the name
	 * 
	 */
	public void fixName() {
		
		if(name.equals("rouge")){
			name = "rogue";
		}
		

	}
	
	/**
	 * Gets the definition.
	 * 
	 * @return definition
	 */
	public ProficiencyDefinition getDefinition() {
		return definition;
	}

	/**
	 * Returns the level.
	 * 
	 * @return level
	 */
	public Integer getLevel() {
		
		if(sagaPlayer == null) return -1;
		return sagaPlayer.getLevel();
		
	}
	
	/**
	 * Gets proficiency type.
	 * 
	 * @return proficiency type
	 */
	public ProficiencyType getType(){
		return definition.getType();
	}

	/**
	 * Gets a property.
	 * 
	 * @param key property key
	 * @return property. null if not found
	 */
	public String getProperty(String key) {
		
		return definition.getProperty(key);
		
	}
	
	/**
	 * Gets the skill maximum multiplier.
	 * 
	 * @param skillName skill name
	 * @return skill maximum multiplier, 0 if not available
	 */
	public Integer getSkillMaximum(String skillName) {

		return getDefinition().getSkillMaximum(skillName);
		
	}
	
	
	// Abilities:
	/**
	 * Gets all selected abilities.
	 * 
	 * @return selected abilities.
	 */
	public HashSet<Ability> getSelectedAbilities() {
		
		
		HashSet<Ability> selectedAbilities = new HashSet<Ability>();
		HashSet<String> selectedAbilityNames = new HashSet<String>(this.selectedAbilities);
		Integer abilitiesLimit = BalanceConfiguration.config().abilitiesLimit;
		
		ArrayList<Ability> allAbilities = getAbilities(); 
		for (Ability ability : allAbilities) {
			
			for (String abilityName : selectedAbilityNames) {
				
				if(ability.getName().equals(abilityName)){
					selectedAbilities.add(ability);
					
					// Stop if limit reached:
					if(selectedAbilities.size() >= abilitiesLimit) return selectedAbilities;
					
					continue;
				}
				
			}
			
		}
		
		return selectedAbilities;
		
		
	}

	/**
	 * Checks if the proficiency already has the ability selected.
	 * 
	 * @param ability abilityName name
	 * @return true if selected
	 */
	public boolean isAbilitySelected(String abilityName) {

		HashSet<String> abilities = new HashSet<String>(selectedAbilities);
		for (String abilityNameC : abilities) {
			if(abilityNameC.equalsIgnoreCase(abilityName)) return true;
		}
		return false;
		
	}
	
	/**
	 * Gets all abilities.
	 * 
	 * @return all abilities
	 */
	public ArrayList<Ability> getAbilities() {
		return new ArrayList<Ability>(abilities);
	}
	
	/**
	 * Checks if the proficiency already has an ability.
	 * 
	 * @param name ability name
	 * @return true if the proficiency already has the ability
	 */
	public boolean hasAbility(String name) {

		ArrayList<Ability> abilities = getAbilities();
		for (Ability abilityi : abilities) {
			if(abilityi.getName().equalsIgnoreCase(name)) return true;
		}
		return false;
		
	}
	
	/**
	 * Checks if the ability is enabled.
	 * 
	 * @param name ability name
	 * @return true if is enabled
	 */
	public boolean hasEnabledAbility(String name) {

		return definition.hasAbility(name);
		
	}
	
	/**
	 * Adds an ability.
	 * 
	 * @param ability ability
	 */
	public void addAbility(Ability ability) {

		
		if(hasAbility(ability.getName())){
			Saga.severe(this, "tried to add an already existing ability", "ignoring request");
			return;
		}
		
		abilities.add(ability);

		// Set player:
		ability.setPlayer(sagaPlayer);
		
		
	}
	
	/**
	 * Selects an ability.
	 * 
	 * @param name ability name
	 */
	public void selectAbilty(String name) {

		
		// Limit:
		if(selectedAbilities.size() >= BalanceConfiguration.config().abilitiesLimit){
			Saga.severe(this, "ability selection limit reached: " + selectedAbilities.size() + "/" + BalanceConfiguration.config().abilitiesLimit, "ignoring request");
			return;
		}
		
		// Already selected:
		if(isAbilitySelected(name)){
			Saga.severe(this, "tried to select a selected ability: " + name, "ignoring request");
			return;
		}

		// Find ability:
		Ability newSelected = null;
		ArrayList<Ability> abilities = getAbilities();
		for (Ability ability : abilities) {
			
			if(ability.getName().equalsIgnoreCase(name)){
				newSelected = ability;
				break;
			}
			
		}
		if(newSelected == null){
			Saga.severe(this, "tried to select a non-available ability: " + name, "ignoring request");
			return;
		}
		
		// Select:
		selectedAbilities.add(name);
		
		// Update manager:
		sagaPlayer.getLevelManager().update();
		
		
	}

	/**
	 * Deselects an ability.
	 * 
	 * @param name ability name
	 */
	public void unselectAbilty(String name) {

		
		// Not selected:
		if(!isAbilitySelected(name)){
			Saga.severe(this, "tried to deselect a non selected ability: " + name, "ignoring request");
			return;
		}
		
		// Deselect:
		selectedAbilities.remove(name);
		
		// Update manager:
		sagaPlayer.getLevelManager().update();
		
		
	}
	
	/**
	 * Check if there are abilities selections available.
	 * 
	 * @return true if available
	 */
	public boolean hasAbilitySlots() {

		return selectedAbilities.size() < BalanceConfiguration.config().abilitiesLimit;
		
	}
	
	
	// Profession definition:
	/**
	 * Gets the highlight.
	 * 
	 * @return the highlight
	 */
	public Short getHierarchyLevel() {
		return definition.getHierarchyLevel();
	}

	/**
	 * Checks if the proficiency has a settlement permission.
	 * 
	 * @param permission permission
	 * @return true if permission was found
	 */
	public boolean hasSettlementPermission(SettlementPermission permission) {
		return definition.hasSettlementPermission(permission);
	}
	
	/**
	 * Checks if the proficiency has a faction permission.
	 * 
	 * @param permission permission
	 * @return true if permission was found
	 */
	public boolean hasFactionPermission(FactionPermission permission) {
		return definition.hasFactionPermission(permission);
	}

	/**
	 * Gets the proficiency definition.
	 * 
	 * @return proficiency definition.
	 */
	public ProficiencyDefinition getProfessionDefinition() {
		return definition;
	}

	
	// Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
	
	/**
	 * Profession type.
	 * 
	 * @author andf
	 *
	 */
	public enum ProficiencyType{
		
		INVALID("invalid"),
		PROFESSION("profession"),
		CLASS("class"),
		ROLE("role"),
		RANK("rank");
		
		String name;
		
		/**
		 * Sets name.
		 * 
		 * @param name name
		 */
		private ProficiencyType(String name) {
			this.name = name;
		}
		
		/**
		 * Returns the name for the type.
		 * 
		 * @return name
		 */
		public String getName() {
			return name;
		}
		
		
	}
	
	
}
