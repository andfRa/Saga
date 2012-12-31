package org.saga.messages;

import org.bukkit.ChatColor;
import org.saga.factions.Faction;
import org.saga.messages.colours.Colour;
import org.saga.settlements.Bundle;
import org.saga.utility.text.TextUtil;

public class ClaimMessages {

	
	// Faction notifications:
	public static String loosing(Bundle bundle, Faction defenderFaction, Faction attackerFaction, Double progress){
		return defenderFaction.getColour2() + "Loosing " + bundle.getName() + " to " + FactionMessages.faction(attackerFaction, defenderFaction.getColour2())+ "." + " " + TextUtil.round(progress * 100, 1) + "% claimed.";
	}

	public static String claiming(Bundle bundle, Faction attackerFaction, Faction defenderFaction, Double progress){
		return attackerFaction.getColour2() + "Seizing " + bundle.getName() + " from " + FactionMessages.faction(defenderFaction, attackerFaction.getColour2())+ "." + " " + TextUtil.round(progress * 100, 1) + "% claimed.";
	}
	
	public static String claiming(Bundle bundle, Faction attackerFaction, Double progress){
		return attackerFaction.getColour2() + "Claiming " + bundle.getName() + "." + " " + TextUtil.round(progress * 100, 1) + "% claimed.";
	}

	
	
	// Town square notifications:
	public static String claimingTownSquare(Bundle bundle, Faction faction, Double progress){
		
		String claimed = "";
		if(progress > 0){
			claimed = " " + TextUtil.round(progress * 100, 1) + "% claimed.";
		}
		
		return Colour.normal1 + "[" + "->" + FactionMessages.faction(faction, Colour.normal1) + "]" + claimed;
	
	}

	public static String claimingTownSquare(Bundle bundle, Faction attackerFaction, Faction defenderFaction, Double progress){

		String claimed = "";
		if(progress > 0){
			claimed = " " + TextUtil.round(progress * 100, 1) + "% claimed.";
		}
		
		return Colour.normal1 + "[" + FactionMessages.faction(defenderFaction, Colour.normal1) + "->" + FactionMessages.faction(attackerFaction, Colour.normal1) + "]" + claimed;

	}
	
	public static String unclaimingTownSquare(Bundle bundle, Faction faction, Double progress){
		
		String claimed = "";
		if(progress > 0){
			claimed = " " + TextUtil.round(progress * 100, 1) + "% claimed.";
		}
		
		return Colour.normal1 + "[" + "<-" + FactionMessages.faction(faction, Colour.normal1) + "]" + claimed;
	
	}

	public static String unclaimingTownSquare(Bundle bundle, Faction attackerFaction, Faction defenderFaction, Double progress){

		String claimed = "";
		if(progress > 0){
			claimed = " " + TextUtil.round(progress * 100, 1) + "% claimed.";
		}
		
		return Colour.normal1 + "[" + FactionMessages.faction(defenderFaction, Colour.normal1) + "<-" + FactionMessages.faction(attackerFaction, Colour.normal1) + "]" + claimed;

	}

	
	
	// Spawning:
	public static String spawnDeny(Faction faction){
		return Colour.negative + "Can't spawn in settlements that are being claimed.";
	}

	
	// Broadcast:
	public static String claimedBcast(Bundle bundle, Faction faction){
		return Colour.announce + "" + "Settlement " + bundle.getName() + " was claimed by " + FactionMessages.faction(ChatColor.UNDERLINE, faction, Colour.announce)+".";
	}
	
	
}
