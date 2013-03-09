package org.saga.messages;

import org.bukkit.ChatColor;
import org.saga.config.GeneralConfiguration;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.listeners.events.SagaEntityDamageEvent.PvPOverride;
import org.saga.messages.colours.Colour;
import org.saga.player.GuardianRune;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.utility.chat.ChatUtil;


public class PlayerMessages {

	
	// Reset:
	public static String respec(Boolean proffRespec, Boolean classRespec, Boolean skillRespec, Double coinCost) {
		
		
		if(!proffRespec && !classRespec && !skillRespec){
			return Colour.negative + "Nothing to reset.";
		}
		
		StringBuffer rString = new StringBuffer();
		
		if(proffRespec){
			rString.append(ProficiencyType.PROFESSION.getName());
		}
		
		if(classRespec){
			
			if(rString.length() > 0) rString.append(", ");
			
			rString.append(ProficiencyType.CLASS.getName());
			
		}
		
		if(skillRespec){
			
			if(rString.length() > 0) rString.append(", ");
			
			rString.append("skills");
			
		}
		
		if(coinCost > 0){
			rString.append(" reset for " + EconomyMessages.coins(coinCost) + ".");
		}else{
			rString.append(" reset.");
		}
		
		return Colour.positive + ChatUtil.capitalize(rString.toString());
		
		
	}
	
	

	// Player versus player:
	public static String pvpOverride(SagaEntityDamageEvent event){
		
		
		PvPOverride cause = event.getOverride();
		
		switch (cause) {
		case SAME_FACTION_DENY:
			
			return Colour.negative + "Can't attack faction members.";
			
		case ALLY_DENY:
	
		return Colour.negative + "Can't attack allies.";
	
		case FACTION_ONLY_PVP_DENY:
			
			return Colour.negative + "Only factions can participate in pvp.";
			
		case SAFE_AREA_DENY:
			
			return Colour.negative + "Can't attack players in safe areas.";
		
		case RESPAWN_DENY:
	
				return Colour.negative + "Can't attack players after respawning.";
	
		default:
			
			break;
			
		}
		
		return Colour.negative + "Can't attack player.";
		
		
	}
	
	
	
	// Guardian rune:
	public static String restored(GuardianRune rune) {

		int count = GuardianRune.countItems(rune.getItems()) + GuardianRune.countItems(rune.getArmour());
		
		StringBuffer rString = new StringBuffer();
		
		rString.append("Guardian rune restored " + count + " items.");
		
		rString.insert(0, Colour.positive);
		
		return rString.toString();
		
		
	}
	
	public static String notCharged(GuardianRune rune) {

		return Colour.positive + "Guardian rune wasn't charged. No items were absorbed.";
		
	}
	
	public static String notChargedInfo(GuardianRune rune) {

		return Colour.normal1 + "Guardian rune can be recharged at an academy.";
		
	}
	
	public static String notEmpty(GuardianRune rune) {

		return Colour.negative + "Guardian rune wasn't empty.";
		
	}
	
	public static String disabled(GuardianRune rune) {
		return Colour.positive + "Disabled guardian rune.";
	}

	public static String enabled(GuardianRune rune) {
		return Colour.positive + "Enabled guardian rune.";
	}
	
	public static String alreadyEnabled(GuardianRune stone) {
		return Colour.negative + "The guardian rune is already enabled.";
	}
	
	public static String alreadyDisabled(GuardianRune stone) {
		return Colour.negative + "The guardian rune is already disabled.";
	}
	
	public static String recharged(GuardianRune rune, Double price) {
		
		
		StringBuffer rString = new StringBuffer();
		
		if(price > 0.0){
			rString.append("Recharged the guardian rune for " + EconomyMessages.coins(price) + ".");
		}else{
			rString.append("Recharged the guardian rune.");
		}
		
		rString.insert(0, Colour.positive);
		
		return rString.toString();
		
		
	}
	
	public static String alreadyRecharged(GuardianRune stone) {
		return Colour.negative + "The guardian rune is already recharged.";
	}
	
	

	// Inventory:
	public static String inventoryFullDropping() {
		
		return Colour.negative + "Inventory full, dropped items on the ground.";
		
	}
	
	
	// Special:
	public static String specialChatMessage(String name, String message) {

		ChatColor nameColor = GeneralConfiguration.config().specialChatNameColor;
		ChatColor messageColor = GeneralConfiguration.config().specialChatMessageColor;
		String namedMessage = messageColor + ">" + nameColor + name + messageColor + "< " + message;
		
		return namedMessage;
		
	}
	
	
}
