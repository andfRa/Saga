package org.saga.abilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.bukkit.Material;
import org.saga.SagaLogger;
import org.saga.config.AbilityConfiguration;
import org.saga.player.Proficiency;
import org.saga.player.SagaLiving;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Bundle;
import org.saga.utility.TwoPointFunction;


/**
 * Defines a profession.
 * 
 * @author andf
 *
 */
public class AbilityDefinition{
	
	
	/**
	 * Ability class.
	 */
	private String className;
	
	/**
	 * Ability name.
	 */
	private String name;
	
	
	/**
	 * Trigger item restrictions.
	 */
	private HashSet<Material> itemRestrictions;
	
	/**
	 * Activation action.
	 */
	private ActivationAction activationAction;
	
	/**
	 * Used item.
	 */
	private Material usedItem;
	
	/**
	 * Used amount.
	 */
	private TwoPointFunction usedAmount;

	
	/**
	 * Used energy.
	 */
	private TwoPointFunction usedEnergy;

	
	/**
	 * Delay ticks required between usages.
	 */
	private Integer cooldownTicks;
	
	/**
	 * Cooldown.
	 */
	private TwoPointFunction cooldown;
	
	
	/**
	 * Attribute requirements.
	 */
	private Hashtable<String, TwoPointFunction> attributeRequirements;

	/**
	 * Building requirements.
	 */
	private Hashtable<Integer, ArrayList<String>> buildingsRequired;

	/**
	 * Proficiency restrictions.
	 */
	private Hashtable<Integer, ArrayList<String>> proficienciesRequired;
	
	/**
	 * Ability functions.
	 */
	private Hashtable<String, TwoPointFunction> functions;
	
	/**
	 * Effect colour.
	 */
	private Integer colour;
	
	/**
	 * Usage description.
	 */
	private String usage;

	/**
	 * Brief description.
	 */
	private String briefDescription;

	/**
	 * Full description.
	 */
	private String fullDescription;

	
	
	// Initialisation:
	/**
	 * Used by gson.
	 * 
	 */
	@SuppressWarnings("unused")
	private AbilityDefinition() {
	}

	/**
	 * Creates definition.
	 * 
	 * @param name name
	 * @param materials materials
	 * @param type type
	 * @param abilities ability names
	 */
	public AbilityDefinition(String name, ArrayList<String> abilities) {
		this.name = name;
	}
	
	/**
	 * Completes the definition.
	 * 
	 * @return integrity.
	 */
	public void complete() {

		
		if(className == null){
			className = "invalid";
			SagaLogger.nullField(this, "className");
		}
		
		if(name == null){
			name = "invalid";
			SagaLogger.nullField(this, "name");
		}
		
		if(itemRestrictions == null){
			itemRestrictions = new HashSet<Material>();
			SagaLogger.nullField(this, "itemRestrictions");
		}
		
		if(activationAction == null){
			activationAction = ActivationAction.RIGHT_CLICK;
			SagaLogger.nullField(this, "activationAction");
		}
		
		if(usedItem == null){
			usedItem = Material.AIR;
			SagaLogger.nullField(this, "usedItem");
		}
		
		if(usedAmount == null){
			usedAmount = new TwoPointFunction(0.0);
			SagaLogger.nullField(this, "usedAmount");
		}
		usedAmount.complete();
		
		if(usedEnergy == null){
			usedEnergy = new TwoPointFunction(0.0);
			SagaLogger.nullField(this, "usedEnergy");
		}
		usedEnergy.complete();
		
		if(cooldownTicks == null){
			cooldownTicks = 0;
			SagaLogger.nullField(this, "cooldownTicks");
		}
		
		if(cooldown == null){
			cooldown = new TwoPointFunction(0.0);
			SagaLogger.nullField(this, "cooldown");
		}
		cooldown.complete();
		
		// Requirements;
		if(attributeRequirements == null){
			attributeRequirements = new Hashtable<String, TwoPointFunction>();
			SagaLogger.nullField(this, "attributeRequirements");
		}
		Collection<TwoPointFunction> reqFunctions = attributeRequirements.values();
		for (TwoPointFunction reqFunction : reqFunctions) {
			reqFunction.complete();
		}
		
		if(buildingsRequired == null){
			buildingsRequired = new Hashtable<Integer, ArrayList<String>>();
			SagaLogger.nullField(this, "buildingsRequired");
		}
		
		if(proficienciesRequired == null){
			proficienciesRequired = new Hashtable<Integer, ArrayList<String>>();
			SagaLogger.nullField(this, "proficienciesRequired");
		}
		
		if(functions == null){
			functions = new Hashtable<String, TwoPointFunction>();
			SagaLogger.nullField(this, "functions");
		}
		Collection<TwoPointFunction> functionsElements = functions.values();
		for (TwoPointFunction function : functionsElements) {
			function.complete();
		}

		if(usage == null){
			usage = "";
			SagaLogger.nullField(this, "usage");
		}

		if(briefDescription == null){
			briefDescription = "";
			SagaLogger.nullField(this, "description");
		}
		
		if(fullDescription == null){
			fullDescription = "";
			SagaLogger.nullField(this, "description");
		}
		
		if(colour == null){
			colour = 0;
			SagaLogger.nullField(this, "colour");
		}

		
	}

	
	
	// Interaction:
	/**
	 * Gets the class name.
	 * 
	 * @return class name
	 */
	public String getClassName() {
		return className;
	}
	
	/**
	 * Gets ability name.
	 * 
	 * @return ability name.
	 */
	public String getName() {
		return name;
	}
	
	
	/**
	 * Gets the trigger item restriction.
	 * 
	 * @return trigger item restrictions
	 */
	public HashSet<Material> getItemRestrictions() {
		return itemRestrictions;
	}
	
	/**
	 * Gets the activationAction.
	 * 
	 * @return the activationAction
	 */
	public ActivationAction getActivationAction() {
		return activationAction;
	}
	

	/**
	 * Gets the used item.
	 * 
	 * @return used item, AIR if none
	 */
	public Material getUsedItem() {
		return usedItem;
	}

	/**
	 * Gets the amount of used material.
	 * 
	 * @param level level
	 * @return amount of used material
	 */
	public Integer getUsedAmount(Integer level) {
		return usedAmount.randomIntValue(level);
	}
	
	/**
	 * Gets the amount of used material.
	 * 
	 * @param level level
	 * @return amount of used material
	 */
	public Integer getMaxAmount(Integer level) {
		return (int)Math.ceil(usedAmount.value(level));
	}
	
	
	/**
	 * Gets used energy.
	 * 
	 * @param score ability score
	 * @return used energy
	 */
	public int getUsedEnergy(Integer score) {
		return usedEnergy.intValue(score);
	}
	
	
	/**
	 * Gets the required delay ticks between usages.
	 * 
	 * @return required delay ticks
	 */
	public Integer getCooldownTicks() {
		return cooldownTicks;
	}
	
	/**
	 * Gets the cooldown.
	 * 
	 * @return the cooldown
	 */
	public Integer getCooldown(Integer level) {
		return cooldown.value(level).intValue();
	}
	
	/**
	 * Finds entities possible ability score.
	 * 
	 * @param sagaLiving saga entity
	 * @return ability score
	 */
	public Integer findScore(SagaLiving<?> sagaLiving) {

		
		int prevScore = 0;
		
		for (int score = 1; score <= AbilityConfiguration.config().maxAbilityScore; score++) {
			
			if(!checkRequirements(sagaLiving, score)) return prevScore;
			prevScore = score;
			
		}
		
		return prevScore;
		
		
	}
	
	/**
	 * Gets a function for the given key.
	 * 
	 * @param key key
	 * @return function for the given key, 0 if none
	 */
	public TwoPointFunction getFunction(String key) {

		TwoPointFunction function = functions.get(key);
		
		if(function == null){
			SagaLogger.severe(this, "failed to retrive function for " + key + " key");
			return new TwoPointFunction(0.0);
		}
		
		return function;

	}
	
	
	
	// Requirements:
	/**
	 * Gets attribute requirement.
	 * 
	 * @param attribute attribute
	 * @param abilityScore ability score
	 * @return ability attribute requirement
	 */
	public Integer getAttrReq(String attribute, Integer abilityScore) {

		
		TwoPointFunction function = attributeRequirements.get(attribute);
		if(function == null) return 0;
		
		return function.intValue(abilityScore);
		
		
	}

	/**
	 * Gets building requirements.
	 * 
	 * @param score ability score
	 * @return buildings requirements
	 */
	public HashSet<String> getBldgReq(Integer score) {

		HashSet<String> required = new HashSet<String>();
		for (int curScore = score; curScore >= 0; curScore--) {
			
			ArrayList<String> req = buildingsRequired.get(curScore);
			if(req != null) required.addAll(req);
			
		}
		
		return required;
		
	}

	/**
	 * Gets proficiency restrictions.
	 * 
	 * @param score ability score
	 * @return proficiency restrictions
	 */
	public HashSet<String> getProfReq(Integer score) {
		
		HashSet<String> required = new HashSet<String>();
		for (int curScore = score; curScore >= 0; curScore--) {
			
			ArrayList<String> req = proficienciesRequired.get(curScore);
			if(req != null) required.addAll(req);
			
		}
		
		return required;
		
	}
	
	
	/**
	 * Checks ability building requirements.
	 * 
	 * @param sagaLiving saga entity
	 * @param abilityScore ability score
	 * @return true if requirements are met
	 */
	public boolean checkBuildings(SagaLiving<?> sagaLiving, Integer abilityScore) {

		
		HashSet<String> required = getBldgReq(abilityScore);
		
		if(required.size() == 0) return true;
		Bundle bundle = sagaLiving.getBundle();
		if(bundle == null) return false;
		
		for (String bldgName : required) {
			
			if(bundle.getFirstBuilding(bldgName) == null) return false;
			
		}
		
		return true;


	}
	
	/**
	 * Checks ability attribute requirements.
	 * 
	 * @param sagaLiving saga entity
	 * @param abilityScore ability score
	 * @return true if requirements are met
	 */
	public boolean checkAttributes(SagaLiving<?> sagaLiving, Integer abilityScore) {
		
		Set<String> attributeNames = attributeRequirements.keySet();
		
		for (String attrName : attributeNames) {
			
			if(sagaLiving.getRawAttributeScore(attrName) < getAttrReq(attrName, abilityScore)){
				return false;
			}
			
		}
		
		return true;
		
	}
	
	/**
	 * Checks ability proficiency requirements.
	 * 
	 * @param sagaLiving saga entity
	 * @param abilityScore ability score
	 * @return true if requirements are met
	 */
	public boolean checkProficiencies(SagaLiving<?> sagaLiving, Integer abilityScore) {
		
		
		if(!(sagaLiving instanceof SagaPlayer)) return false;
		SagaPlayer sagaPlayer = (SagaPlayer) sagaLiving;
		
		HashSet<String> required = getProfReq(abilityScore);
		
		if(required.size() == 0) return true;

		Proficiency role = sagaPlayer.getRole();
		if(role != null && required.contains(role.getName())) return true;

		Proficiency rank = sagaPlayer.getRank();
		if(rank != null && required.contains(rank.getName())) return true;
		
		return false;
		
		
	}
	
	/**
	 * Checks ability requirements.
	 * 
	 * @param sagaLiving saga entity
	 * @param abilityScore ability score
	 * @return true if the requirements are met
	 */
	public boolean checkRequirements(SagaLiving<?> sagaLiving, Integer abilityScore) {
		return checkAttributes(sagaLiving, abilityScore)&&
				checkBuildings(sagaLiving, abilityScore) &&
				checkProficiencies(sagaLiving, abilityScore);
	}
	
	
	
	// Effect:
	/**
	 * Gets ability colour.
	 * 
	 * @return ability colour
	 */
	public Integer getColour() {
		return colour;
	}
	
	
	
	// Info:
	/**
	 * Gets the brief description.
	 * 
	 * @return brief description
	 */
	public String getBriefDescription() {
		return briefDescription;
	}
	
	/**
	 * Gets the full description.
	 * 
	 * @return full description
	 */
	public String getFullDescription() {
		return fullDescription;
	}

	/**
	 * Gets the usage description.
	 * 
	 * @return the usage description
	 */
	public String getUsage() {
		return usage;
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
	
	
	// Types:
	public enum ActivationAction{
		
		LEFT_CLICK,
		RIGHT_CLICK,
		NONE;
		
		public String getShortName() {

			return name().toLowerCase().replace("_", "").replace("left", "l").replace("right", "r");
			
		}
		
	}
	
}
