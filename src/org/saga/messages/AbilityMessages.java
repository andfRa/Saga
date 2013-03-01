package org.saga.messages;

import org.bukkit.Material;
import org.saga.abilities.Ability;
import org.saga.messages.colours.Colour;
import org.saga.utility.chat.ChatUtil;

public class AbilityMessages {

	
	// Usage:
	public static String insufficientItems(Ability ability, Material material, Integer amount) {
		return Colour.negative + ChatUtil.capitalize(ability.getName()) + " requires " + amount + " " + GeneralMessages.material(material) + ".";
	}
	
	public static String onCooldown(Ability ability) {
		return Colour.negative + ChatUtil.capitalize(ability.getName()) + " is on cooldown for " + ability.getCooldown() + "s.";
	}
	
	public static String cooldownEnd(Ability ability) {
		return Colour.positive + ChatUtil.capitalize(ability.getName()) + " ready.";
	}
	

	public static String targetTooFar(Ability ability) {
		return Colour.negative + "Target block too far.";
	}
	
	public static String cantUseUnderground(Ability ability) {
		return Colour.negative + ChatUtil.capitalize(ability.getName()) + " can't be used below ground.";
	}
	
	public static String invalidAbility(String name) {
		return Colour.negative + "Ability " + name + " is not valid.";
	}
	
	
	public static String insuficientFood(String name) {
		return Colour.negative + "Too tired to use " + name + " ability.";
	}
	
	
	
	// Repair (not used):
	public static String repairAlreadyRepaired(Material material) {
		
		return Colour.negative + ChatUtil.capitalize(GeneralMessages.material(material)) + " is already repaired.";
		
	}
	
	public static String repairCantRepair(Material material) {
		
		return Colour.negative + ChatUtil.capitalize(GeneralMessages.material(material)) + " can't be repaired.";
		
	}
	
	public static String repairLevelsRequired(Integer levels) {
		
		if(levels == 1){
			return Colour.negative + levels.toString() + " enchant level required.";
		}
		return Colour.negative + levels.toString() + " enchant levels required.";
		
	}
	
	
	// Heavy swing:
	public static String hevySwingTargetStone(Ability ability) {
	
		return Colour.negative + ChatUtil.capitalize(ability.getName()) + " can only be used on stone.";
	
	}
	
	
	// Dig:
	public static String digTargetDirtSand(Ability ability) {
	
		return Colour.negative + ChatUtil.capitalize(ability.getName()) + " can only be used on dirt or sand.";
	
	}
	
	
	// Remove Ladder:
	public static String ladderTooLong(Ability ability) {
		return Colour.negative + "Not strong enough to pick up the ladder.";
	}
	
	
	// Trim:
	public static String trimNotStroungEnough(Ability ability, int maximum) {
		return Colour.negative + "Not strong enough to trim more than " + maximum + " blocks.";
	}
	
	
	// Chop down:
	public static String chopDownNotTree(Ability ability) {
		return Colour.negative + ChatUtil.capitalize(ability.getName()) + " ability can only be used on trees.";
	}
	
	public static String chopDownNotStroungEnough(Ability ability, int maximum) {
		return Colour.negative + "Not strong enough to chop a tree larger than " + maximum + " blocks.";
	}
	
	
}
