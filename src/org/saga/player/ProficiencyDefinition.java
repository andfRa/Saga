package org.saga.player;

import org.saga.SagaLogger;
import org.saga.factions.SagaFaction.FactionPermission;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.settlements.Settlement.SettlementPermission;


/**
 * Defines a profession.
 * 
 * @author andf
 *
 */
public class ProficiencyDefinition{
	

	/**
	 * Profession name.
	 */
	private String name;

	/**
	 * Proficiency type.
	 */
	private ProficiencyType type;
	
	/**
	 * Profession hierarchy level.
	 */
	private Short hierarchyLevel;

	/**
	 * Settlement permissions.
	 */
	private SettlementPermission[] settlementPermissions;

	/**
	 * Faction permissions.
	 */
	private FactionPermission[] factionPermissions;

	
	
	// Initialisation:
	/**
	 * Used by gson.
	 * 
	 */
	@SuppressWarnings("unused")
	private ProficiencyDefinition() {
	}

	/**
	 * Creates definition.
	 * 
	 * @param name name
	 */
	public ProficiencyDefinition(String name) {
		this.name = name;
	}
	
	/**
	 * Completes the definition. Abilities need to be added.
	 * 
	 * @return integrity.
	 */
	public boolean complete() {

		
		boolean integrity=true;
		
		if(name == null){
			name = "";
			SagaLogger.nullField(this, "name");
			integrity=false;
		}
		if(type == null){
			type = ProficiencyType.INVALID;
			SagaLogger.nullField(this, "type");
			integrity=false;
		}
		if(hierarchyLevel == null){
			hierarchyLevel = 0;
			SagaLogger.nullField(this, "hierarchyLevel");
			integrity=false;
		}
		
		if(settlementPermissions == null){
			settlementPermissions = new SettlementPermission[0];
			SagaLogger.nullField(this, "settlementPermissions");
			integrity=false;
		}
		
		if(factionPermissions == null){
			factionPermissions = new FactionPermission[0];
			SagaLogger.nullField(this, "factionPermissions");
			integrity=false;
		}
		
		return integrity;
		
		
	}

	
	
	// Interaction:
	/**
	 * Gets ability name.
	 * 
	 * @return ability name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets profession type.
	 * 
	 * @return profession type
	 */
	public ProficiencyType getType() {
		return type;
	}

	/**
	 * Gets the highlight.
	 * 
	 * @return the highlight
	 */
	public Short getHierarchyLevel() {
		return hierarchyLevel;
	}

	
	
	// Permissions:
	/**
	 * Checks if the definition has a settlement permissions.
	 * 
	 * @param permission permission
	 * @return true if permission was found
	 */
	public boolean hasSettlementPermission(SettlementPermission permission) {


		for (int i = 0; i < settlementPermissions.length; i++) {
			if(settlementPermissions[i].equals(permission)) return true;
		}
		return false;
		
		
	}

	/**
	 * Checks if the definition has a faction permissions.
	 * 
	 * @param permission permission
	 * @return true if permission was found
	 */
	public boolean hasFactionPermission(FactionPermission permission) {


		for (int i = 0; i < factionPermissions.length; i++) {
			if(factionPermissions[i].equals(permission)) return true;
		}
		return false;
		
		
	}
	
	
	
	// Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
	
	
}
