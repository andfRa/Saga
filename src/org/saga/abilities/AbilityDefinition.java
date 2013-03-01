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
	 * Used amount of food.
	 */
	private TwoPointFunction usedFood;
	
	/**
	 * Cooldown.
	 */
	private TwoPointFunction cooldown;
	
	/**
	 * Active for.
	 */
	private TwoPointFunction active;

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
	private HashSet<String> proficiencyRestrictions;
	
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
	 * Description.
	 */
	private String description;

	
	
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
		
		if(usedFood == null){
			usedFood = new TwoPointFunction(0.0);
			SagaLogger.nullField(this, "usedFood");
		}
		usedFood.complete();
		
		if(cooldown == null){
			cooldown = new TwoPointFunction(0.0);
			SagaLogger.nullField(this, "cooldown");
		}
		cooldown.complete();
		
		if(active == null){
			active = new TwoPointFunction(0.0);
			SagaLogger.nullField(this, "active");
		}
		active.complete();
		

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
		
		if(proficiencyRestrictions == null){
			proficiencyRestrictions = new HashSet<String>();
			SagaLogger.nullField(this, "proficiencyRestrictions");
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

		if(description == null){
			description = "";
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
	 * Gets used food.
	 * 
	 * @param score score
	 * @return used food
	 */
	public double getUsedFood(Integer score) {
		return usedFood.value(score);
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
	 * Gets the active for.
	 * 
	 * @return the active for
	 */
	public Integer getActiveFor(Integer level) {
		return active.value(level).intValue();
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
	
	/**
	 * Gets entities ability score.
	 * 
	 * @param sagaLiving saga entity
	 * @return ability score
	 */
	public Integer getScore(SagaLiving<?> sagaLiving) {

		
		int prevScore = 0;
		
		for (int score = 1; score <= AbilityConfiguration.config().maxAbilityScore; score++) {
			
			if(!checkRequirements(sagaLiving, score)) return prevScore;
			prevScore = score;
			
		}
		
		return prevScore;
		
		
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
	 * @return buildings requirements
	 */
	public ArrayList<String> getBldgReq(Integer score) {
		
		ArrayList<String> req = buildingsRequired.get(score);
		if(req == null) return new ArrayList<String>();
		return req;
		
	}

	/**
	 * Gets proficiency restrictions.
	 * 
	 * @return proficiency restrictions
	 */
	public HashSet<String> getProfRestr() {
		return new HashSet<String>(proficiencyRestrictions);
	}
	
	
	/**
	 * Checks ability building requirements.
	 * 
	 * @param sagaLiving saga entity
	 * @param abilityScore ability score
	 * @return true if requirements are met
	 */
	public boolean checkBuildings(SagaLiving<?> sagaLiving, Integer abilityScore) {

		
		ArrayList<String> req = getBldgReq(abilityScore);
		
		if(req.size() == 0) return true;
		Bundle bundle = sagaLiving.getBundle();
		if(bundle == null) return false;
		
		for (String building : req) {
			
			if(bundle.getFirstBuilding(building) == null) return false;
			
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
			
			if(sagaLiving.getAttributeScore(attrName) < getAttrReq(attrName, abilityScore)){
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

		
		if(proficiencyRestrictions.size() == 0) return true;

		if(sagaLiving instanceof SagaPlayer){
			Proficiency role = ((SagaPlayer) sagaLiving).getRole();
			Proficiency rank = ((SagaPlayer) sagaLiving).getRank();
			if(role != null && proficiencyRestrictions.contains(role.getName())) return true;
			if(rank != null && proficiencyRestrictions.contains(rank.getName())) return true;
		}
		
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
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
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
