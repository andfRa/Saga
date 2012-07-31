package org.saga.messages;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.saga.config.BalanceConfiguration;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.listeners.events.SagaEntityDamageEvent.PvPOverride;
import org.saga.player.GuardianRune;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.player.SagaPlayer;
import org.saga.utility.text.TextUtil;


public class PlayerMessages {

	
	// Colours:
	public static ChatColor veryPositive = ChatColor.DARK_GREEN; // DO NOT OVERUSE.
	
	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED; // DO NOT OVERUSE.
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor anouncment = ChatColor.AQUA;
	
	public static ChatColor normal1 = ChatColor.GOLD;
	
	public static ChatColor normal2 = ChatColor.YELLOW;
	
	public static ChatColor frame = normal1;
	
	public static ChatColor frameTitle = normal2;
	
	
	
	// General:
	public static String coinsNeeded(Double required) {
		
		return negative + EconomyMessages.coins(required) + " coins required.";
		
	}
	
	
	
	// Inventory:
	public static String inventoryFullDropping() {
		
		return negative + "Inventory full, dropped items on the ground.";
		
	}
	

	
	// Skills:
	public static String limitReached(String skillName) {
		
		return negative + "Can't train " + skillName + " skill any farther.";
		
	}
	
	public static String trained(String skillName, Integer multiplier, Double coins) {
		
		if(coins > 0){
			return positive + "Trained " + skillName + " skill to " + multiplier + " for " + EconomyMessages.coins(coins);
		}else{
			return positive + "Trained " + skillName + " skill to " + multiplier + ".";
		}
		
		
	}
	
	public static String skillPointsNeeded() {
		
		return negative + "Not enough skill points.";
		
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
	
	
	
	// Experience:
	public static String deathExpInfo(){
		
		return normal1 + "Visit a temple to regain some of your lost experience.";
		
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

		ChatColor nameColor = BalanceConfiguration.config().specialChatNameColor;
		ChatColor messageColor = BalanceConfiguration.config().specialChatMessageColor;
		String namedMessage = messageColor + ">" + nameColor + name + messageColor + "< " + message;
		
		return namedMessage;
		
	}
	
	
	
	// Stats info:
//	public static String trainInfo(String skillName, SagaPlayer sagaPlayer) {
//		
//		
//		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
//		StringBook book = new StringBook(skillName + " skill info", color, 10);
//		
//		Double maxCoinCost = EconomyConfiguration.config().getSkillCoinCost(BalanceConfiguration.config().maximumSkillLevel);
//		Integer skillLevel = sagaPlayer.getSkillMultiplier(skillName);
//		Double coinCost = EconomyConfiguration.config().getSkillCoinCost(skillLevel);
//		
//		StringTable table = new StringTable(color);
//		DecimalFormat format = new DecimalFormat("00");
//		
//		// Skill level:
//		String sSkillLevel = format.format(sagaPlayer.getLevelManager().getSkillMultiplier(skillName));
//		String sSkillLevelMax = format.format(sagaPlayer.getLevelManager().getMaxSkillMultiplier(skillName));
//		table.addLine(new String[]{skillName + " skill", sSkillLevel + "/" + sSkillLevelMax});
//		
//		// Available points:
//		table.addLine(new String[]{"remaining points", sagaPlayer.getRemainingSkillPoints().toString()});
//		
//		if(maxCoinCost > 0.0){
//			
//			table.addLine(new String[]{"cost at lvl " + sagaPlayer.getLevel(), EconomyMessages.coins(coinCost)});
//			
//		}
//		
//		table.collapse();
//		book.addTable(table);
//		
//		return book.framed(0);
//		
//		
//	}
	
	public static String respecInfo(SagaPlayer sagaPlayer) {

		
//		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
//		StringBook book = new StringBook("reset info", color, 10);
//		
//		book.addLine(negative + "Resets profession, class and skills. Level remains unchanged.");
//		
//		Double maxCoinCost = EconomyConfiguration.config().getRespecCost(BalanceConfiguration.config().maximumSkillLevel);
//		Double coinCost = EconomyConfiguration.config().getRespecCost(sagaPlayer.getLevel());
//		if(maxCoinCost > 0){
//			
//			book.addLine("A reset costs " + EconomyMessages.coins(coinCost) + " at level " + + sagaPlayer.getLevel() + ".");
//			
//		}
//		
//		return book.framed(0);
//		
		
		return "";
	}
	
	
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
