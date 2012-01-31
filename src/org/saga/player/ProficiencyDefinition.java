package org.saga.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;

import org.bukkit.Material;
import org.saga.Saga;
import org.saga.abilities.AbilityDefinition;
import org.saga.abilities.AbilityDefinition.ActivationAction;
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
	 * Abilities.
	 */
	private HashSet<String> abilities;

	/**
	 * Ability bind materials.
	 */
	private Hashtable<String, HashSet<Material>> bindMaterials;

	/**
	 * Ability bind actions.
	 */
	private Hashtable<String, ActivationAction> bindActions;
	
	/**
	 * Skill maximum values.
	 */
	private Hashtable<String, Integer> skillMax;

	/**
	 * Settlement permissions.
	 */
	private SettlementPermission[] settlementPermissions;

	/**
	 * Faction permissions.
	 */
	private FactionPermission[] factionPermissions;

	/**
	 * Currency required to train.
	 */
	private Double currencyCost;
	
	/**
	 * Levels required to train.
	 */
	private Integer levelsCost;
	
	/**
	 * Profession properties.
	 */
	private Properties properties;

	
	// Initialization:
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
	 * @param materials materials
	 * @param type type
	 * @param abilities ability names
	 */
	public ProficiencyDefinition(String name, HashSet<String> abilities) {
		this.name = name;
		this.abilities = abilities;
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
			Saga.severe(this, "failed to initialize name field", "setting default");
			integrity=false;
		}
		if(type == null){
			type = ProficiencyType.INVALID;
			Saga.severe(this, "failed to initialize type field", "setting default");
			integrity=false;
		}
		if(hierarchyLevel == null){
			hierarchyLevel = 0;
			Saga.severe(this, "failed to initialize hierarchyLevel field", "setting default");
			integrity=false;
		}
		if(properties == null){
			properties = new Properties();
			Saga.severe(this, "failed to initialize properties field", "setting default");
			integrity=false;
		}
		
		if(settlementPermissions == null){
			settlementPermissions = new SettlementPermission[0];
			Saga.severe(this, "failed to initialize settlementPermissions field", "setting default");
			integrity=false;
		}
		
		if(factionPermissions == null){
			factionPermissions = new FactionPermission[0];
			Saga.severe(this, "failed to initialize factionPermissions field", "setting default");
			integrity=false;
		}
		
		if(abilities == null){
			abilities = new HashSet<String>();
			Saga.severe(this, "failed to initialize abilities field", "setting default");
			integrity=false;
		}
		if(abilities.remove(null)){
			Saga.severe(this, "abilities field element failed to initialize", "removing element");
			integrity=false;
		}
		
		if(bindMaterials == null){
			bindMaterials = new Hashtable<String, HashSet<Material>>();
			Saga.severe(this, "bindMaterials field failed to initialize", "setting default");
			integrity=false;
		}
		
		Collection<HashSet<Material>> bindMaterialElements = bindMaterials.values();
		for (HashSet<Material> bindElement : bindMaterialElements) {
			
			if(bindElement.remove(null)){
				Saga.severe(this, "bindElement field element failed to initialize", "removing element");
				integrity=false;
			}
			
		}
		
		if(bindActions == null){
			bindActions = new Hashtable<String, AbilityDefinition.ActivationAction>();
			Saga.severe(this, "bindActions field failed to initialize", "setting default");
			integrity=false;
		}
		
		if(skillMax == null){
			skillMax = new Hashtable<String, Integer>();
			Saga.severe(this, "skillMax field failed to initialize", "setting default");
			integrity=false;
		}
		
		if(currencyCost == null){
			currencyCost = 0.0;
			Saga.severe(this, "failed to initialize currencyCost field", "setting default");
			integrity=false;
		}
		
		if(levelsCost == null){
			levelsCost = 0;
			Saga.severe(this, "failed to initialize levelsCost field", "setting default");
			integrity=false;
		}
		
		// Transient:
		
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
	
	/**
	 * Gets the required currency for training.
	 * 
	 * @return the currency required
	 */
	public Double getCurrencyCost() {
		return currencyCost;
	}

	/**
	 * Gets the required levels for training.
	 * 
	 * @return the levels required
	 */
	public Integer getLevelsCost() {
		return levelsCost;
	}

	/**
	 * Gets a property.
	 * 
	 * @param key property key
	 * @return property. null if not found
	 */
	public String getProperty(String key) {
		
		return properties.getProperty(key);
		
	}
	

	// Skills:
	/**
	 * Gets all skills for the proficiency.
	 * 
	 * @param skillName skill name
	 * @return lskill maximum multiplier, 0 if not available
	 */
	public ArrayList<String> getSkills() {

		return new ArrayList<String>(skillMax.keySet());
		
	}

	/**
	 * Gets the skill maximum multiplier.
	 * 
	 * @param skillName skill name
	 * @return lskill maximum multiplier, 0 if not available
	 */
	public Integer getSkillMaximum(String skillName) {

		Integer skillMaximum = skillMax.get(skillName);
		if(skillMaximum == null) skillMaximum = 0;
		
		return skillMaximum;
		
	}

	
	// Abilities:
	/**
	 * Checks if the ability is allowed with that name.
	 * 
	 * @param name name
	 * @return true if allowed
	 */
	public boolean hasAbility(String name) {
		return abilities.contains(name);
	}
	
	/**
	 * Gets all enabled abilities.
	 * 
	 * @return all enabled abilities
	 */
	public HashSet<String> getAbilities() {
		return new HashSet<String>(abilities);
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

		
		if(bindActions.get(name) != action) return false;
		
		HashSet<Material> materials = bindMaterials.get(name);
		if(materials == null) return false;
		
		return materials.contains(material);
		
		
	}
	
	/**
	 * Gets ability bind action.
	 * 
	 * @param abilityName ability name
	 * @return bind action, {@link ActivationAction#NONE} if none
	 */
	public ActivationAction getBindAction(String abilityName) {
		
		ActivationAction action = bindActions.get(abilityName);
		if(action == null) action = ActivationAction.NONE;
		
		return action;
		
	}

	/**
	 * Gets ability bind materials.
	 * 
	 * @param abilityName ability name
	 * @return bind materials, empty if none
	 */
	public HashSet<Material> getBindMaterials(String abilityName) {
		
		HashSet<Material> materials = bindMaterials.get(abilityName);
		if(materials == null) materials = new HashSet<Material>();;
		
		return materials;
		
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
