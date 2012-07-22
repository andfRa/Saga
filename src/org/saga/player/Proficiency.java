package org.saga.player;


import org.saga.SagaLogger;
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
	 * Proficiency definition.
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
		
	}
	
	/**
	 * Completes.
	 * 
	 * @throws InvalidProficiencyException if the definition can not be found
	 */
	public void complete() throws InvalidProficiencyException {
		
		
		if(name == null){
			name = "null proficiency";
			SagaLogger.nullField(this, "name");
		}

		// Retrieve definition:
		definition = ProficiencyConfiguration.config().getDefinition(getName());
		if(definition == null){
			throw new InvalidProficiencyException(getName());
		}
		
		
	}

	/**
	 * Sets the player.
	 * 
	 * @param sagaPlayer saga player
	 */
	public void setPlayer(SagaPlayer sagaPlayer) {
		
		this.sagaPlayer= sagaPlayer;
		
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
		

	}
	
	/**
	 * Gets the definition.
	 * 
	 * @return definition
	 */
	public ProficiencyDefinition getDefinition() {
		return definition;
	}

	
	
	// Definition:
	/**
	 * Gets the highlight.
	 * 
	 * @return the highlight
	 */
	public Integer getHierarchy() {
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
