package org.saga.abilities;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Material;
import org.saga.Saga;
import org.saga.utility.TwoPointFunction;


/**
 * Defines a profession.
 * 
 * @author andf
 *
 */
public class AbilityDefinition{
	
	
	/**
	 * Ability class
	 */
	private String className;
	
	/**
	 * Profession name.
	 */
	private String name;
	
	/**
	 * Activation type.
	 */
	private ActivationType activationType;

	/**
	 * Used material.
	 */
	private Material usedMaterial;
	
	/**
	 * Used amount.
	 */
	private TwoPointFunction usedAmount;
	
	/**
	 * Cooldown.
	 */
	private TwoPointFunction cooldown;
	
	/**
	 * Active for.
	 */
	private TwoPointFunction active;
	
	/**
	 * Primary level function.
	 */
	private TwoPointFunction primaryFunction;
	
	/**
	 * Secondary level function.
	 */
	private TwoPointFunction secondaryFunction;
	
	/**
	 * Base skills.
	 */
	private HashSet<String> baseSkills;
	
	/**
	 * Experience reward.
	 */
	private TwoPointFunction expReward;

	/**
	 * Description.
	 */
	private String description;

	/**
	 * Primary function name.
	 */
	private String primaryStat;
	
	/**
	 * Secondary function name.
	 */
	private String secondaryStat;
	
	
	// Initialization:
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
	 * Completes the definition. Abilities need to be added.
	 * 
	 * @return integrity.
	 */
	public boolean complete() {

		
		boolean integrity=true;
		
		if(className == null){
			className = "invalid";
			Saga.severe(this, "className field failed to initialize", "setting default");
			integrity=false;
		}
		
		if(name == null){
			name = "invalid";
			Saga.severe(this, "name field failed to initialize", "setting default");
			integrity=false;
		}
		
		if(activationType == null){
			activationType = ActivationType.INSTANT;
			Saga.severe(this, "activationType field failed to initialize", "setting default");
			integrity=false;
		}
		
		if(usedMaterial == null){
			usedMaterial = Material.AIR;
			Saga.severe(this, "usedMaterial field failed to initialize", "setting default");
			integrity=false;
		}
		
		if(usedAmount == null){
			usedAmount = new TwoPointFunction(0.0, 0.0);
			Saga.severe(this, "usedAmount field failed to initialize", "setting default");
			integrity=false;
		}
		
		if(cooldown == null){
			cooldown = new TwoPointFunction(0.0, 0.0);
			Saga.severe(this, "cooldown field failed to initialize", "setting default");
			integrity=false;
		}
		
		if(active == null){
			active = new TwoPointFunction(0.0, 0.0);
			Saga.severe(this, "active field failed to initialize", "setting default");
			integrity=false;
		}
		
		if(primaryFunction == null){
			primaryFunction = new TwoPointFunction(0.0, 0.0);
			Saga.severe(this, "primaryFunction field failed to initialize", "setting default");
			integrity=false;
		}
		integrity = primaryFunction.complete() && integrity;
		
		if(secondaryFunction == null){
			secondaryFunction = new TwoPointFunction(0.0, 0.0);
			Saga.severe(this, "secondaryFunction field failed to initialize", "setting default");
			integrity=false;
		}
		integrity = secondaryFunction.complete() && integrity;
		
		if(baseSkills == null){
			baseSkills = new HashSet<String>();
			Saga.severe(this, "baseSkills field failed to initialize", "setting default");
			integrity=false;
		}
		if(baseSkills.remove(null)){
			Saga.severe(this, "baseSkills field element(s) failed to initialize", "setting default");
			integrity=false;
		}
		
		if(expReward == null){
			expReward = new TwoPointFunction(0.0, 0.0);
			Saga.severe(this, "expReward field failed to initialize", "setting default");
			integrity=false;
		}
		integrity = expReward.complete() && integrity;

		if(description == null){
			description = "<no description>";
			Saga.severe(this, "description field failed to initialize", "setting default");
			integrity = false;
		}
		
		if(primaryStat == null){
			primaryStat = "";
			Saga.severe(this, "primaryStat field failed to initialize", "setting default");
			integrity = false;
		}
		
		if(secondaryStat == null){
			secondaryStat = "";
			Saga.severe(this, "secondaryStat field failed to initialize", "setting default");
			integrity = false;
		}
		
		return integrity;
		
		
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
	 * Gets the usedMaterial.
	 * 
	 * @return the usedMaterial
	 */
	public Material getUsedMaterial() {
		return usedMaterial;
	}

	/**
	 * Gets the amount of used material.
	 * 
	 * @param level level
	 * @return amount of used material
	 */
	public Integer getUsedAmount(Integer level) {
		return usedAmount.calculateValue(level.shortValue()).intValue();
	}

	/**
	 * Gets the cooldown.
	 * 
	 * @return the cooldown
	 */
	public Integer getCooldown(Integer level) {
		return cooldown.calculateValue(level.shortValue()).intValue();
	}
	
	/**
	 * Gets the active for.
	 * 
	 * @return the active for
	 */
	public Integer getActiveFor(Integer level) {
		return active.calculateValue(level.shortValue()).intValue();
	}

	/**
	 * Gets the primaryFunction.
	 * 
	 * @return the primaryFunction
	 */
	public TwoPointFunction getPrimaryFunction() {
		return primaryFunction;
	}

	/**
	 * Gets the secondaryFunction.
	 * 
	 * @return the secondaryFunction
	 */
	public TwoPointFunction getSecondaryFunction() {
		return secondaryFunction;
	}

	/**
	 * Gets the activationType.
	 * 
	 * @return the activationType
	 */
	public ActivationType getActivationType() {
		return activationType;
	}
	
	/**
	 * Gets the base skills.
	 * 
	 * @return the base skills
	 */
	public HashSet<String> getBaseSkills() {
		return baseSkills;
	}
	
	/**
	 * Gets the expReward.
	 * 
	 * @return the expReward
	 */
	public Integer getExpReward(Integer multiplier) {
		return expReward.calculateRandomIntValue(multiplier);
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
	 * Gets the primaryStat.
	 * 
	 * @return the primaryStat
	 */
	public String getPrimaryStat() {
		return primaryStat;
	}
	
	/**
	 * Gets the secondaryStat.
	 * 
	 * @return the secondaryStat
	 */
	public String getSecondaryStat() {
		return secondaryStat;
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
	
	
	// Types
	public enum ActivationType{
		
		INSTANT,
		SINGLE_USE,
		TIMED,
		TOGGLE,
		PASSIVE;
		
	}
	
	public enum ActivationAction{
		
		LEFT_CLICK,
		RIGHT_CLICK,
		NONE;
		
		public String getShortName() {

			return name().toLowerCase().replace("_", "").replace("left", "l").replace("right", "r");
			
		}
		
	}
	
}
