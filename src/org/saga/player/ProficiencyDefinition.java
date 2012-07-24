package org.saga.player;

import java.util.Hashtable;

import org.saga.SagaLogger;
import org.saga.factions.Faction.FactionPermission;
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
	private Integer hierarchyLevel;

	/**
	 * Attribute bonuses.
	 */
	private Hashtable<String, Integer> attributeBonuses;
	
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
	 * Fixes all problematic fields.
	 * 
	 */
	public void complete() {

		
		if(name == null){
			name = "";
			SagaLogger.nullField(this, "name");
		}
		
		if(type == null){
			type = ProficiencyType.INVALID;
			SagaLogger.nullField(this, "type");
		}
		
		if(hierarchyLevel == null){
			hierarchyLevel = 0;
			SagaLogger.nullField(this, "hierarchyLevel");
		}
		
		if(attributeBonuses == null){
			attributeBonuses = new Hashtable<String, Integer>();
			SagaLogger.nullField(this, "attributeBonuses");
		}
		
		if(settlementPermissions == null){
			settlementPermissions = new SettlementPermission[0];
			SagaLogger.nullField(this, "settlementPermissions");
		}
		
		if(factionPermissions == null){
			factionPermissions = new FactionPermission[0];
			SagaLogger.nullField(this, "factionPermissions");
		}
		
		
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
	public Integer getHierarchyLevel() {
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
	
	
	
	// Bonuses:
	/**
	 * Gets attribute bonus.
	 * 
	 * @param attrName attribute name
	 * @return attribute bonus
	 */
	public Integer getAttributeBonus(String attrName) {
		
		Integer bonus = attributeBonuses.get(attrName);
		if(bonus == null) return 0;
		
		return bonus;
		
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
