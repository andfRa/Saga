package org.saga.messages;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.saga.config.GeneralConfiguration;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.listeners.events.SagaEntityDamageEvent.PvPOverride;
import org.saga.player.GuardianRune;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.utility.text.TextUtil;


public class PlayerMessages {

	
	// Colours:
	public static ChatColor veryPositive = ChatColor.DARK_GREEN; // DO NOT OVERUSE.
	
	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED; // DO NOT OVERUSE.
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor announce = ChatColor.AQUA;
	
	public static ChatColor normal1 = ChatColor.GOLD;
	
	public static ChatColor normal2 = ChatColor.YELLOW;
	
	public static ChatColor frame = normal1;
	
	public static ChatColor frameTitle = normal2;
	
	
	
	// Availability:
	public static String invalidPlayer(String playerName){
		return negative + "Player " + playerName +" doesn't exist.";
	}

	public static String notOnline(String name) {
		return negative + "Player " + name + " isn't online.";
	}
	
	
	
	// Inventory:
	public static String inventoryFullDropping() {
		
		return negative + "Inventory full, dropped items on the ground.";
		
	}
	

	
	// Respec:
	public static String respec(Boolean proffRespec, Boolean classRespec, Boolean skillRespec, Double coinCost) {
		
		
		if(!proffRespec && !classRespec && !skillRespec){
			return negative + "Nothing to reset.";
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
		
		return positive + TextUtil.capitalize(rString.toString());
		
		
	}
	
	

	// Player versus player:
	public static String pvpOverride(SagaEntityDamageEvent event){
		
		
		PvPOverride cause = event.getOverride();
		
		switch (cause) {
		case SAME_FACTION_DENY:
			
			return negative + "Can't attack faction members.";
			
		case ALLY_DENY:
	
		return negative + "Can't attack allies.";
	
		case FACTION_ONLY_PVP_DENY:
			
			return negative + "Only factions can participate in pvp.";
			
		case SAFE_AREA_DENY:
			
			return negative + "Can't attack players in safe areas.";
			
		default:
			
			break;
			
		}
		
		return negative + "Can't attack player.";
		
		
	}
	
	
	
	// Guardian rune:
	public static String restored(GuardianRune rune) {

		int count = GuardianRune.countItems(rune.getItems()) + GuardianRune.countItems(rune.getArmour());
		
		StringBuffer rString = new StringBuffer();
		
		rString.append("Guardian rune restored " + count + " items.");
		
		rString.insert(0, positive);
		
		return rString.toString();
		
		
	}
	
	public static String notCharged(GuardianRune rune) {

		return positive + "Guardian rune wasn't charged. No items were absorbed.";
		
	}
	
	public static String notChargedInfo(GuardianRune rune) {

		return normal1 + "Guardian rune can be recharged at an academy.";
		
	}
	
	public static String notEmpty(GuardianRune rune) {

		return negative + "Guardian rune wasn't empty.";
		
	}
	
	public static String disabled(GuardianRune rune) {
		return positive + "Disabled guardian rune.";
	}

	public static String enabled(GuardianRune rune) {
		return positive + "Enabled guardian rune.";
	}
	
	public static String alreadyEnabled(GuardianRune stone) {
		return negative + "The guardian rune is already enabled.";
	}
	
	public static String alreadyDisabled(GuardianRune stone) {
		return negative + "The guardian rune is already disabled.";
	}
	
	public static String recharged(GuardianRune rune, Double price) {
		
		
		StringBuffer rString = new StringBuffer();
		
		if(price > 0.0){
			rString.append("Recharged the guardian rune for " + EconomyMessages.coins(price) + ".");
		}else{
			rString.append("Recharged the guardian rune.");
		}
		
		rString.insert(0, positive);
		
		return rString.toString();
		
		
	}
	
	public static String alreadyRecharged(GuardianRune stone) {
		return negative + "The guardian rune is already recharged.";
	}
	
	
	
	// Special:
	public static String specialChatMessage(String name, String message) {

		ChatColor nameColor = GeneralConfiguration.config().specialChatNameColor;
		ChatColor messageColor = GeneralConfiguration.config().specialChatMessageColor;
		String namedMessage = messageColor + ">" + nameColor + name + messageColor + "< " + message;
		
		return namedMessage;
		
	}
	
	
	
	// Types:
	/**
	 * Contains different colours.
	 * 
	 * @author andf
	 *
	 */
	public static class ColourLoop{
		
		
		/**
		 * All colours.
		 */
		ArrayList<ChatColor> colours = new ArrayList<ChatColor>();
		
		/**
		 * Current index.
		 */
		private int index = 0;
		
		
		
		/**
		 * Initialises the colour loop.
		 * 
		 */
		public ColourLoop() {
		}
		
		
		
		/**
		 * Adds a colour to the circle.
		 * 
		 * @param colour colour
		 * @return instance
		 */
		public ColourLoop addColor(ChatColor colour){
			colours.add(colour);
			return this;
		}
		
		/**
		 * Gets the next colour in the loop.
		 * 
		 * @return next colour, {@link PlayerMessages#normal1} if none
		 */
		public ChatColor nextColour() {
			
			
			if(colours.size() == 0) return normal1;
			
			index++;
			
			if(index >= colours.size()){
				index = 0;
			}
			
			return colours.get(index);
			
			
		}
		
		/**
		 * Gets the first colour in the loop.
		 * 
		 * @return first colour, {@link PlayerMessages#normal1} if none
		 */
		public ChatColor firstColour() {
			
			
			if(colours.size() == 0) return normal1;
			
			return colours.get(0);
			
			
		}
		
		/**
		 * Resets the loop.
		 * 
		 */
		public void reset() {

			index = 0;

		}
		
		
	}
	
	
}
