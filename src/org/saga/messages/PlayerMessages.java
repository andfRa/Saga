package org.saga.messages;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.saga.abilities.Ability;
import org.saga.abilities.AbilityDefinition;
import org.saga.buildings.signs.GuardianRuneSign;
import org.saga.buildings.signs.ProficiencySign;
import org.saga.buildings.signs.RespecSign;
import org.saga.buildings.signs.SkillSign;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.ChunkGroupManager;
import org.saga.config.AbilityConfiguration;
import org.saga.config.BalanceConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.SkillConfiguration;
import org.saga.economy.EconomyMessages;
import org.saga.factions.FactionManager;
import org.saga.factions.SagaFaction;
import org.saga.listeners.events.SagaPvpEvent;
import org.saga.listeners.events.SagaPvpEvent.PvpDenyReason;
import org.saga.player.GuardianRune;
import org.saga.player.PlayerLevelManager;
import org.saga.player.Proficiency;
import org.saga.player.ProficiencyDefinition;
import org.saga.player.SagaPlayer;
import org.saga.player.SkillType;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.utility.StringBook;
import org.saga.utility.StringTable;
import org.saga.utility.TextUtil;


public class PlayerMessages {

	
	// Colors:
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
	
	
	// Help and info:
	public static String help(int page) {
		
		
		ColorCircle messageColor = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook("player help", messageColor, 10);

		// Bugs:
		if(BalanceConfiguration.config().bugReportMessage.length() > 0){
			book.addLine(veryPositive + BalanceConfiguration.config().bugReportMessage);
		}

		// Class and profession:
		book.addLine("/stats to show skills, abilities, faction, settlement and other player stats.");
		
		// Class and profession:
		book.addLine("You can select a class and a profession.");

		// Rank and role:
		book.addLine("Role is set by a settlement and rank is set by a faction.");
		
		// Skills and abilities:
		book.addLine("Each class/prof can train crtain skills.");
		
		// Selection:
		book.addLine("Rclick a " + ProficiencySign.SIGN_NAME + " sign to select a class/prof.");

		// Training:
		book.addLine("Rclick a " + SkillSign.SIGN_NAME + " sign to train a skill.");

		// Respec:
		book.addLine("Rclick a" + RespecSign.SIGN_NAME + " to reset prof, class and skills.");
		
		// Certain buildings:
		book.addLine("Profs, classes and skills are only available at certain buildings. /binfo for details.");

		// Ability bindings:
		book.addLine("To use abilities you need to lclick/rclick with a binded item.");

		// Binded items:
		book.addLine("Binded items are listed under class/prof information.");
		
		// Class info:
		book.addLine("/classinfo <class_name> [page] to show class information.");

		// Profession info:
		book.addLine("/profinfo <prof_name> [page] to show profession information.");

		// Ability info:
		book.addLine("/abilityinfo <ability_name> [page] to show ability information.");
		
		// Available classes:
		book.addLine("All classes: " + TextUtil.flatten(ProficiencyConfiguration.config().getProficiencyNames(ProficiencyType.CLASS)) + ".");

		// Available professions:
		book.addLine("All professions: " + TextUtil.flatten(ProficiencyConfiguration.config().getProficiencyNames(ProficiencyType.PROFESSION)) + ".");

		// Available abilities:
		book.addLine("All abilities: " + TextUtil.flatten(AbilityConfiguration.config().getAbilityNames()) + ".");

		// Guardian runes:
		book.addLine("The guardian rune will restore all exp and items after death.");

		// Rune charge:
		book.addLine("The rune needs to be recharged after every use.");

		// Rune status:
		book.addLine("Rune status can be seen under /stats.");
		
		// Recharge:
		book.addLine("Rclick a " + GuardianRuneSign.SIGN_NAME + " sign to recharge the rune for " + EconomyMessages.coins(EconomyConfiguration.config().guardianRuneRechargeCost) + ".");
		
		return book.framed(page);
		
		
	}
	
	public static String abilityInfo(AbilityDefinition definition, int page) {
		
		
		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook(definition.getName() + " ability info", color, 10);
		
		// Description:
		book.addLine(definition.getDescription());

		// Base skill:
		book.addLine("Base skills: " + TextUtil.flatten(definition.getBaseSkills()) + ".");

		// General info:
		StringTable table = new StringTable(color);
		
		Integer maxSkill = BalanceConfiguration.config().maximumSkillLevel;
		Integer[] levels = new Integer[]{new Double(maxSkill * 0).intValue(), new Double(maxSkill * 0.2).intValue(), new Double(maxSkill * 0.4).intValue(), new Double(maxSkill * 0.6).intValue(), new Double(maxSkill * 0.8).intValue(), new Double(maxSkill * 1).intValue()};
		String[] line = new String[levels.length + 1];
		
		// Names:
		line[0] = "skill level";
		for (int i = 1; i < line.length; i++) {
			line[i] = levels[i-1].toString();
		}
		
		table.addLine(line);
		
		// Cooldown:
		line[0] = "cooldown";
		for (int i = 1; i < line.length; i++) {
			line[i] = definition.getCooldown(levels[i-1]).toString();
		}
		
		table.addLine(line);
		
		// Primary function:
		line[0] = definition.getPrimaryStat();
		if(line[0].length() > 0){
			
			for (int i = 1; i < line.length; i++) {
				line[i] = String.format("%.2g", definition.getPrimaryFunction().value(levels[i-1]));
			}
			
			table.addLine(line);
			
		}

		// Secondary function:
		line[0] = definition.getSecondaryStat();
		if(line[0].length() > 0){
			
			for (int i = 1; i < line.length; i++) {
				line[i] = String.format("%.2g", definition.getSecondaryFunction().value(levels[i-1]));
			}
			
			table.addLine(line);
			
		}

		// Consumption function:
		if(!definition.getUsedMaterial().equals(Material.AIR)){
			
			line[0] = EconomyMessages.materialShort(definition.getUsedMaterial()) + " used";
		
			for (int i = 1; i < line.length; i++) {
				line[i] = definition.getAbsoluteUsedAmount(levels[i-1]).toString();
			}
			
			table.addLine(line);
			
		}
		
		table.collapse();
		book.addTable(table);
		
		return book.framed(page);
		
		
	}
	
	public static String proficiencyInfo(ProficiencyDefinition definition, int page) {
		
		
		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook(definition.getName() + " " + definition.getType().getName() + " info", color, 12);
		String[] line = new String[3];

		// Skills:
		ArrayList<String> skills = definition.getSkills();
		if(skills.size() > 0){
			
			StringTable skillTable = new StringTable(color);
		
			// Names:
			line = new String[]{"SKILL", "MAX"};
			
			skillTable.addLine(line);
			
			// Values:
			for (String skill : skills) {
				
				Integer maxMult = definition.getSkillMaximum(skill);
				
				if(maxMult == 0) continue;
				
				line = new String[]{skill, maxMult.toString()};
				
				skillTable.addLine(line);
				
			}
			
			skillTable.collapse();
			if(book.lines() > 0) book.addLine("");
			book.addTable(skillTable);
			
		}
		
		// Bindings:
		StringTable bindTable = new StringTable(color);
		
		// Names:
		line = new String[]{"ABILITY", "ACTION", "BINDINGS"};
		
		bindTable.addLine(line);
		
		// Abilities:
		HashSet<String> abilities = definition.getAbilities();
		for (String name : abilities) {
			
			line = new String[]{name, definition.getBindAction(name).getShortName(), EconomyMessages.materialsShort(definition.getBindMaterials(name))};
			
			bindTable.addLine(line);
			
		}
		
		bindTable.collapse();
		if(book.lines() > 0) book.addLine("");
		book.addTable(bindTable);
		
		return book.framed(page);
		
		
	}
	
	public static String trainingCost(SagaPlayer sagaPlayer) {

		
		PlayerLevelManager levelManager = sagaPlayer.getLevelManager();
		StringTable table = new StringTable(new ColorCircle().addColor(normal1).addColor(normal2));
		StringBuffer result = new StringBuffer();
		
		DecimalFormat format = new DecimalFormat("00");
		
		// Skills:
    	ArrayList<Double> widths = new ArrayList<Double>();
    	widths.add(20.5);
    	widths.add(10.5);
    	widths.add(14.0);
    	widths.add(14.5);
    	table.setCustomWidths(widths);
    	
    	// Names:
    	table.addLine("SKILL");
		ArrayList<String> skillNames = SkillConfiguration.config().getSkillNames();
		
		// Filter:
		for (int i = 0; i < skillNames.size(); i++) {
			
			if(sagaPlayer.getSkillMaximum(skillNames.get(i)) <= 0){
				skillNames.remove(i);
				i--;
				continue;
			}
			
		}
		
		// Names:
		for (String skillName : skillNames) {

			table.addLine(skillName);
			
		}
		
		table.nextColumn();
		
		// Skill names:
		table.addLine("VALUE");
		for (String skillName : skillNames) {

			String multiplier = format.format(levelManager.getSkillMultiplier(skillName));
			String multiplierMax = format.format(levelManager.getMaxSkillMultiplier(skillName));
			
			table.addLine( multiplier + "/" + multiplierMax);
			
		}
		
		table.nextColumn();
		
		// Coin costs:
		table.addLine("COST");
		for (String skillName : skillNames) {

			Integer multiplier = sagaPlayer.getSkillMultiplier(skillName);
			
			table.addLine(EconomyMessages.coins(EconomyConfiguration.config().getSkillCoinCost(multiplier)));
			
		}
		
		result.append(table.createTable());
		
		
//		result.append("\n");
		
		// Abilities:
		
		return TextUtil.frame("player info table", normal1, result.toString(), table.calcTotalWidth());
		
		
	}
	
	
	// Inventory:
	public static String inventoryFullDropping() {
		
		return negative + "Inventory full, dropped items on the ground.";
		
	}
	
	
	// Proficiencies:
	public static String proficiencySelected(Proficiency proficiency, Double coinCost) {
		
		
		if(coinCost > 0){
			return positive + "Selected " + proficiency.getName() + " " + proficiency.getType().getName() + " for " + EconomyMessages.coins(coinCost) + ".";
		}else{
			return positive + "Selected " + proficiency.getName() + " " + proficiency.getType().getName() + ".";
		}
		
		
	}
	
	public static String alreadySelected(Proficiency proficiency) {
		
		
		return negative + "A " + proficiency.getName() + " " + proficiency.getType().getName() + " is already selected.";
		
		
	}
	
	public static String proficiencyRemoved(Proficiency proficiency) {
		
		return positive + "Removed " + proficiency.getName() + " " + proficiency.getType().getName() + ".";
		
	}
	
	public static String oneProficAllowed(ProficiencyType proficType) {
		
		return negative + "Only one " + proficType.getName() + " is allowed.";
		
	}
	
	public static String oneProficAllowedInfo(ProficiencyType proficType) {
		
		return normal1 + "You need to reset your stats by using a " + RespecSign.SIGN_NAME + " sign to select a new " + proficType.getName() + ".";
		
	}
	
	public static String levelsNeeded(Integer levels, Proficiency proficiency) {
		
		return negative + levels.toString() + " level points are required to select " + proficiency.getName() + " " + proficiency.getType().getName() + ".";
		
	}
	
	public static String coinsNeeded(Double coins, Proficiency proficiency) {
		
		return negative + EconomyMessages.coins(coins) + " are needed to select " + proficiency.getName() + " " + proficiency.getType().getName() + ".";
		
	}
	
	public static String invalidProficiency(String name) {
		
		return negative + name + " isn't a valid proficiency.";
		
	}
	
	public static String removedProficiency(Proficiency profession){
		
		return positive + "Removed " + profession.getName() + " " + profession.getType().getName() + ".";
		
	}
	
	public static String noProficiency(ProficiencyType type){
		
		return negative + "No " + type.getName() + " is selected.";
		
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
	public static String pvpDenied(SagaPvpEvent event){
		
		
		PvpDenyReason cause = event.getDenyReason();
		
		switch (cause) {
		case SAME_FACTION:
			
			return negative + event.getDefender().getName() + " is from the same faction.";
			
		case ALLY:
	
		return negative + event.getDefender().getName() + " is an ally.";
	
		case ATTACKER_NO_FACTION:
			
			return negative + "You must be in a faction.";
			
		case DEFENDER_NO_FACTION:
			
			return negative + event.getDefender().getName() + " must be in a faction.";
			
		case SAFE_AREA:
			
			return negative + "Pvp not allowed in safe areas.";
			
		}
		
		return negative + "Pvp isn't allowed.";
		
	}
	
	
	// Experience:
	public static String deathExpInfo(){
		
		return normal1 + "Visit a temple to regain some of your lost experience.";
		
	}
	
	// Guardian stones:
	public static String restored(GuardianRune rune) {


		Collection<ItemStack> items = rune.getItems().values();
		int count = 0;
		for (ItemStack item : items) {
			count += item.getAmount();
		}
		
		StringBuffer rString = new StringBuffer();
		
		rString.append("Guardian rune restored " + rune.getExp() + " exp and " + count + " items.");
		
		rString.insert(0, positive);
		
		return rString.toString();
		
		
	}
	
	public static String notCharged(GuardianRune rune) {

		return positive + "Guardian rune wasn't charged. Items dropped on the ground.";
		
	}
	
	public static String notChargedInfo(GuardianRune rune) {

		return normal1 + "Guardian rune can be recharged at an academy.";
		
	}
	
	public static String notEmpty(GuardianRune rune) {

		return negative + "Guardian rune wasn't empty. Inventory won't be restored.";
		
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
	
	public static String alreadyRecharged(GuardianRune stone) {
		return negative + "The guardian rune is already recharged.";
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
	
	
	// Stats info:
	public static String trainInfo(String skillName, SagaPlayer sagaPlayer) {
		
		
		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook(skillName + " skill info", color, 10);
		
		Double maxCoinCost = EconomyConfiguration.config().getSkillCoinCost(BalanceConfiguration.config().maximumSkillLevel);
		Integer skillLevel = sagaPlayer.getSkillMultiplier(skillName);
		Double coinCost = EconomyConfiguration.config().getSkillCoinCost(skillLevel);
		
		StringTable table = new StringTable(color);
		DecimalFormat format = new DecimalFormat("00");
		
		// Skill level:
		String sSkillLevel = format.format(sagaPlayer.getLevelManager().getSkillMultiplier(skillName));
		String sSkillLevelMax = format.format(sagaPlayer.getLevelManager().getMaxSkillMultiplier(skillName));
		table.addLine(new String[]{skillName + " skill", sSkillLevel + "/" + sSkillLevelMax});
		
		// Available points:
		table.addLine(new String[]{"remaining points", sagaPlayer.getRemainingSkillPoints().toString()});
		
		if(maxCoinCost > 0.0){
			
			table.addLine(new String[]{"cost at lvl " + sagaPlayer.getLevel(), EconomyMessages.coins(coinCost)});
			
		}
		
		table.collapse();
		book.addTable(table);
		
		return book.framed(0);
		
		
	}
	
	public static String respecInfo(SagaPlayer sagaPlayer) {

		
		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook("reset info", color, 10);
		
		book.addLine(negative + "Resets profession, class and skills. Level remains unchanged.");
		
		Double maxCoinCost = EconomyConfiguration.config().getRespecCost(BalanceConfiguration.config().maximumSkillLevel);
		Double coinCost = EconomyConfiguration.config().getRespecCost(sagaPlayer.getLevel());
		if(maxCoinCost > 0){
			
			book.addLine("A reset costs " + EconomyMessages.coins(coinCost) + " at level " + + sagaPlayer.getLevel() + ".");
			
		}
		
		return book.framed(0);
		
		
	}
	
	
	/**
	 * Contains different colors.
	 * 
	 * @author andf
	 *
	 */
	public static class ColorCircle{
		
		
		ArrayList<ChatColor> colors = new ArrayList<ChatColor>();
		
		private int index = 0;
		
		
		public ColorCircle() {
		}
		
		
		public ColorCircle addColor(ChatColor color){
			colors.add(color);
			return this;
		}
		
		public ChatColor nextColor() {
			if(colors.size() == 0) return normal1;
			index++;
			if(index >= colors.size()){
				index = 0;
			}
			return colors.get(index);
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
