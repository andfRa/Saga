package org.saga.messages;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.saga.abilities.Ability;
import org.saga.utility.text.TextUtil;

public class AbilityMessages {

	
	public static ChatColor veryPositive = ChatColor.DARK_GREEN;
	
	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED;
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor announce = ChatColor.AQUA;
	
	public static ChatColor normal1 = ChatColor.GOLD;
	
	public static ChatColor normal2 = ChatColor.YELLOW;
	
	
	
	// Activation:
	public static String insufficientItems(Ability ability, Material material, Integer amount) {
		return negative + TextUtil.capitalize(ability.getName()) + " requires " + amount + " " + GeneralMessages.material(material) + ".";
	}
	
	public static String onCooldown(Ability ability) {
		return negative + TextUtil.capitalize(ability.getName()) + " is on cooldown for " + ability.getCooldown() + "s.";
	}
	
	public static String cooldownEnd(Ability ability) {
		return positive + TextUtil.capitalize(ability.getName()) + " ready.";
	}
	

	// General usage:
	public static String targetTooFar(Ability ability) {
		return negative + "Target block too far.";
	}
	
	public static String cantUseUnderground(Ability ability) {
		return negative + TextUtil.capitalize(ability.getName()) + " can't be used below ground.";
	}

	
	// Retrieval:
	public static String invalidAbility(String name) {
		return negative + "Ability " + name + " is not valid.";
	}
	
	
	// Repair:
	public static String alreadyRepaired(Material material) {
		
		return negative + TextUtil.capitalize(GeneralMessages.material(material)) + " is already repaired.";
		
	}
	
	public static String cantRepair(Material material) {
		
		return negative + TextUtil.capitalize(GeneralMessages.material(material)) + " can't be repaired.";
		
	}
	
	public static String repairLevelsRequired(Integer levels) {
		
		if(levels == 1){
			return negative + levels.toString() + " enchant level required.";
		}
		return negative + levels.toString() + " enchant levels required.";
		
	}
	
	
	// Heavy swing:
	public static String targetStone(Ability ability) {
	
		return negative + TextUtil.capitalize(ability.getName()) + " can only be used on stone.";
	
	}
	
	
	// Dig:
	public static String targetDirtSand(Ability ability) {
	
		return negative + TextUtil.capitalize(ability.getName()) + " can only be used on dirt or sand.";
	
	}
	
	
	// Remove Ladder:
	public static String ladderTooLong(Ability ability) {
		return negative + "Not strong enough to pick up the ladder.";
	}
	
	
	// Trim:
	public static String trimNotStroungEnough(Ability ability, int maximum) {
		return PlayerMessages.negative + "Not strong enough to trim more than " + maximum + " blocks.";
	}
	
	
	// Chop down:
	public static String chopDownNotTree(Ability ability) {
		return PlayerMessages.negative + TextUtil.capitalize(ability.getName()) + " ability can only be used on trees.";
	}
	
	public static String chopDownNotStroungEnough(Ability ability, int maximum) {
		return PlayerMessages.negative + "Not strong enough to chop a tree larger than " + maximum + " blocks.";
	}
	
	
}
