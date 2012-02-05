package org.saga.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.saga.Saga;
import org.saga.constants.IOConstants.WriteReadType;
import org.saga.player.Skill;
import org.saga.player.SkillType;
import org.saga.utility.WriterReader;

import com.google.gson.JsonParseException;

public class SkillConfiguration {

	
	/**
	 * Instance of the configuration.
	 */
	transient private static SkillConfiguration instance;
	
	/**
	 * Gets the instance.
	 * 
	 * @return instance
	 */
	public static SkillConfiguration config() {
		return instance;
	}
	

	/**
	 * Skills.
	 */
	private Hashtable<String, Skill> damageSkills; 
	
	
	// Initialization:
	/**
	 * Used by gson.
	 * 
	 */
	public SkillConfiguration() {
	}
	
	/**
	 * Completes construction.
	 * 
	 */
	public boolean complete() {
		
		
		boolean integrity = true;
		
		if(damageSkills == null){
			Saga.severe(getClass(), "damageSkills field failed to intialize", "setting default");
			damageSkills = new Hashtable<String, Skill>();
			integrity = false;
		}
		Enumeration<String> skills = damageSkills.keys();
		while (skills.hasMoreElements()) {
			String skill = (String) skills.nextElement();
			integrity = damageSkills.get(skill).complete() && integrity;
		}
		
		return integrity;
		
		
	}
	
	/**
	 * Gets the skills.
	 * 
	 * @return skills
	 */
	public Hashtable<String, Skill> getSkills() {
		return new Hashtable<String, Skill>(damageSkills);
	}

	
	
	/**
	 * Gets a skill.
	 * 
	 * @param name skill name
	 * @return skill, null if none
	 */
	public Skill getSkill(String name) {
		return damageSkills.get(name);
	}


	/**
	 * Gets the skill names.
	 * 
	 * @return skill names
	 */
	public ArrayList<String> getSkillNames() {

		
		ArrayList<String> skills = new ArrayList<String>();
	
		Hashtable<String, Skill> allSkills = getSkills();
		Enumeration<String> allSkillNames = allSkills.keys();
		
		while (allSkillNames.hasMoreElements()) {
			
			String skillName = allSkillNames.nextElement();
			
			skills.add(skillName);
			
		}
		
		return skills;
		
		
	}
	
	/**
	 * Gets the skill names.
	 * 
	 * @param type skill type
	 * @return skill names
	 */
	public ArrayList<String> getSkillNames(SkillType type) {

		
		ArrayList<String> skills = new ArrayList<String>();
	
		Hashtable<String, Skill> allSkills = getSkills();
		Enumeration<String> allSkillNames = allSkills.keys();
		
		while (allSkillNames.hasMoreElements()) {
			
			String skillName = allSkillNames.nextElement();
			
			if(allSkills.get(skillName).getType().equals(type)){
				skills.add(skillName);
			}
			
		}
		
		return skills;
		
		
	}


	// Load unload:
	/**
	 * Loads the configuration.
	 * 
	 * @return experience configuration
	 */
	public static SkillConfiguration load(){
		
		
		boolean integrityCheck = true;
		
		// Load:
		String configName = "skill configuration";
		SkillConfiguration config;
		try {
			config = WriterReader.readSkillConfig();
		} catch (FileNotFoundException e) {
			Saga.severe("Missing " + configName + ". Loading defaults.");
			config = new SkillConfiguration();
			integrityCheck = false;
		} catch (IOException e) {
			Saga.severe("Failed to load " + configName + ". Loading defaults.");
			config = new SkillConfiguration();
			integrityCheck = false;
		} catch (JsonParseException e) {
			Saga.severe("Failed to parse " + configName + ". Loading defaults.");
			Saga.info("Parse message :" + e.getMessage());
			config = new SkillConfiguration();
			integrityCheck = false;
		}
		
		// Integrity check and complete:
		integrityCheck = config.complete() && integrityCheck;
		
		// Write default if integrity check failed:
		if (!integrityCheck) {
			Saga.severe("Integrity check failed for " + configName);
			Saga.info("Writing " + configName + " with fixed default values. Edit and rename to use it.");
			try {
				WriterReader.writeSkillConfig(config, WriteReadType.CONFIG_DEFAULTS);
			} catch (IOException e) {
				Saga.severe("Profession information write failure. Ignoring write.");
			}
		}
		
		// Set instance:
		instance = config;
		
		return config;
		
		
	}
	
	/**
	 * Unloads the instance.
	 * 
	 */
	public static void unload(){
		instance = null;
	}
	
	
}
