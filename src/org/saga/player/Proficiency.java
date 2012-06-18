package org.saga.player;


import java.util.ArrayList;
import java.util.HashSet;

import org.saga.SagaLogger;
import org.saga.abilities.Ability;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.ProficiencyConfiguration.InvalidProficiencyException;
import org.saga.exceptions.InvalidAbilityException;
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

	
	// Initialisation:
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
			SagaLogger.nullField(this, "name");
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
			SagaLogger.nullField(this, "abilities");
			integrity = false;
		}
		for (int i = 0; i < abilities.size(); i++) {
			
			if(abilities.get(i) == null){
				SagaLogger.severe(this, "abilities field element");
				abilities.remove(i);
				i--;
				continue;
			}
			
			try {
				integrity = abilities.get(i).complete() && integrity;
			} catch (InvalidAbilityException e) {
				SagaLogger.severe(this, "failed to complete abilities field element: " + e.getClass().getSimpleName() + ":" + e.getMessage());
				abilities.remove(i);
				i--;
				continue;
			}
			
		}
		if(selectedAbilities == null){
			selectedAbilities = new HashSet<String>();
			SagaLogger.nullField(this, "selectedAbilities");
			integrity = false;
		}
		
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
	 * Gets proficiency type.
	 * 
	 * @return proficiency type
	 */
	public ProficiencyType getType(){
		return definition.getType();
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
