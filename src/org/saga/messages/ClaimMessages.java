package org.saga.messages;

import org.bukkit.ChatColor;
import org.saga.chunks.Bundle;
import org.saga.factions.Faction;
import org.saga.utility.text.TextUtil;

public class ClaimMessages {

	
	public static ChatColor veryPositive = ChatColor.DARK_GREEN;
	
	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED;
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor announce = ChatColor.AQUA;
	
	public static ChatColor normal1 = ChatColor.GOLD;
	
	public static ChatColor normal2 = ChatColor.YELLOW;
	
	
	
	// All:
	public static String claimedBcast(Bundle bundle, Faction faction){
		
		return announce + "" + "Settlement " + bundle.getName() + " was claimed by " + FactionMessages.faction(ChatColor.UNDERLINE, faction, announce)+".";
	
	}
	
	
	
	// Factions:
	public static String loosing(Bundle bundle, Faction defenderFaction, Faction attackerFaction, Double progress){
		
		return defenderFaction.getColour2() + "Loosing " + bundle.getName() + " to " + FactionMessages.faction(attackerFaction, defenderFaction.getColour2())+ "." + " " + TextUtil.round(progress * 100, 1) + "% claimed.";
	
	}

	public static String claiming(Bundle bundle, Faction attackerFaction, Faction defenderFaction, Double progress){
		
		return attackerFaction.getColour2() + "Seizing " + bundle.getName() + " from " + FactionMessages.faction(defenderFaction, attackerFaction.getColour2())+ "." + " " + TextUtil.round(progress * 100, 1) + "% claimed.";
	
	}
	
	public static String claiming(Bundle bundle, Faction attackerFaction, Double progress){
		
		return attackerFaction.getColour2() + "Claiming " + bundle.getName() + "." + " " + TextUtil.round(progress * 100, 1) + "% claimed.";
	
	}

	
	
	// Town square:
	public static String claimingTownSquare(Bundle bundle, Faction faction, Double progress){
		
		String claimed = "";
		if(progress > 0){
			claimed = " " + TextUtil.round(progress * 100, 1) + "% claimed.";
		}
		
		return normal1 + "[" + "->" + FactionMessages.faction(faction, normal1) + "]" + claimed;
	
	}

	public static String claimingTownSquare(Bundle bundle, Faction attackerFaction, Faction defenderFaction, Double progress){

		String claimed = "";
		if(progress > 0){
			claimed = " " + TextUtil.round(progress * 100, 1) + "% claimed.";
		}
		
		return normal1 + "[" + FactionMessages.faction(defenderFaction, normal1) + "->" + FactionMessages.faction(attackerFaction, normal1) + "]" + claimed;

	}
	
	public static String unclaimingTownSquare(Bundle bundle, Faction faction, Double progress){
		
		String claimed = "";
		if(progress > 0){
			claimed = " " + TextUtil.round(progress * 100, 1) + "% claimed.";
		}
		
		return normal1 + "[" + "<-" + FactionMessages.faction(faction, normal1) + "]" + claimed;
	
	}

	public static String unclaimingTownSquare(Bundle bundle, Faction attackerFaction, Faction defenderFaction, Double progress){

		String claimed = "";
		if(progress > 0){
			claimed = " " + TextUtil.round(progress * 100, 1) + "% claimed.";
		}
		
		return normal1 + "[" + FactionMessages.faction(defenderFaction, normal1) + "<-" + FactionMessages.faction(attackerFaction, normal1) + "]" + claimed;

	}

	
	
	// Spawning:
	public static String spawnDeny(Faction faction){
		
		return negative + "Can't spawn in settlements that are being claimed.";

	}

	
	
}
