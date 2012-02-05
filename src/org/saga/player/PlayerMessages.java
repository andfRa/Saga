package org.saga.player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.saga.abilities.Ability;
import org.saga.abilities.AbilityDefinition;
import org.saga.buildings.signs.ProficiencySign;
import org.saga.buildings.signs.RepairStoneSign;
import org.saga.buildings.signs.SkillSign;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.ChunkGroupManager;
import org.saga.config.AbilityConfiguration;
import org.saga.config.BalanceConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.config.ExperienceConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.SkillConfiguration;
import org.saga.economy.EconomyMessages;
import org.saga.factions.FactionManager;
import org.saga.factions.SagaFaction;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.player.SagaEntityDamageManager.SagaPvpEvent;
import org.saga.player.SagaEntityDamageManager.SagaPvpEvent.PvpDenyReason;
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
	
	
	// Stats:
	public static String stats(SagaPlayer sagaPlayer) {
		
		
		StringBuffer result = new StringBuffer();
		
		// Skills:
		StringTable skills = skills(sagaPlayer.getLevelManager());
		result.append(skills.createTable());
		
		result.append("\n");
		
		// Proficiencies:
		result.append(proficiencies(sagaPlayer).createTable());
		
		// Abilities:
		if(sagaPlayer.getLevelManager().getAllAbilities().size() > 0){
			
			result.append("\n");
			
			result.append(abilities(sagaPlayer).createTable());
			
		}
		
		result.append("\n");

		// General:
		result.append(general(sagaPlayer).createTable());
		
		// Invites:
		if(sagaPlayer.getFactionInvites().size() > 0 || sagaPlayer.getChunkGroupInvites().size() > 0){

			result.append("\n");
			
			result.append(invites(sagaPlayer).createTable());
			
		}
		
		// Add frame:
		return TextUtil.frame("player information", normal1, result.toString(), 57.0);
		
		
	}
	
	public static StringTable skills(PlayerLevelManager levelManager) {

		
		StringTable table = new StringTable(new ColorCircle().addColor(normal1).addColor(normal2));
		
		DecimalFormat format = new DecimalFormat("00");
		
    	// Table size:
    	ArrayList<Double> widths = new ArrayList<Double>();
    	widths.add(16.5);
    	widths.add(11.5);
    	widths.add(20.5);
    	widths.add(11.5);
    	table.setCustomWidths(widths);
    	
		// Offensive:
		ArrayList<String> offensive = SkillConfiguration.config().getSkillNames(SkillType.OFFENSE);
		for (String skillName : offensive) {

			table.addLine(skillName);
			
		}
		
		table.nextColumn();
		
		for (String skillName : offensive) {

			String multiplier = format.format(levelManager.getSkillMultiplier(skillName));
			String multiplierMax = format.format(levelManager.getMaxSkillMultiplier(skillName));
			
			table.addLine( multiplier + "/" + multiplierMax);
			
		}
		
		table.nextColumn();

		// Defensive:
		ArrayList<String> defensive = SkillConfiguration.config().getSkillNames(SkillType.DEFENSE);
		for (String skillName : defensive) {
			
			table.addLine(skillName);
			
		}
		
		table.nextColumn();
		
		for (String skillName : defensive) {

			String multiplier = format.format(levelManager.getSkillMultiplier(skillName));
			String multiplierMax = format.format(levelManager.getMaxSkillMultiplier(skillName));
			
			table.addLine( multiplier + "/" + multiplierMax);
			
		}
		
		table.prevoiusColumn();
		table.prevoiusColumn();
		table.prevoiusColumn();

		// Blocks:
		ArrayList<String> blocks = SkillConfiguration.config().getSkillNames(SkillType.BLOCK);
		for (String skillName : blocks) {

			table.addLine(skillName);
			
		}
		
		table.nextColumn();
		
		for (String skillName : blocks) {

			String multiplier = format.format(levelManager.getSkillMultiplier(skillName));
			String multiplierMax = format.format(levelManager.getMaxSkillMultiplier(skillName));
			
			table.addLine( multiplier + "/" + multiplierMax);
			
		}
		
		return table;
    	
		
	}

	public static StringTable proficiencies(SagaPlayer sagaPlayer) {

		
		StringTable table = new StringTable(new ColorCircle().addColor(normal1).addColor(normal2));
		
    	// Table size:
    	ArrayList<Double> widths = new ArrayList<Double>();
    	widths.add(7.75);
    	widths.add(19.25);
    	widths.add(7.75);
    	widths.add(25.25);
    	table.setCustomWidths(widths);
		
    	// Class and rank:
    	table.addLine("class ");
    	table.addLine("rank ");
    	
    	table.nextColumn();

    	if(sagaPlayer.getClazz() != null){
    		table.addLine(sagaPlayer.getClazz().getName());
    	}else{
    		table.addLine("none");
    	}

    	if(sagaPlayer.getRank() != null){
    		table.addLine(sagaPlayer.getRank().getName());
    	}else{
    		table.addLine("none");
    	}
    	
    	table.nextColumn();
    	
    	// Profession and role:
    	table.addLine("prof ");
    	table.addLine("role ");
    	
    	table.nextColumn();

    	// Profession and role:
    	if(sagaPlayer.getProfession() != null){
    		table.addLine(sagaPlayer.getProfession().getName());
    	}else{
    		table.addLine("none");
    	}
    	
    	if(sagaPlayer.getRole() != null){
    		table.addLine(sagaPlayer.getRole().getName());
    	}else{
    		table.addLine("none");
    	}
    	
		return table;
    	
		
	}

	public static StringTable general(SagaPlayer sagaPlayer) {

		
		StringTable table = new StringTable(new ColorCircle().addColor(normal1).addColor(normal2));
		String[] line = new String[]{"","","",""};
    	
    	// Table size:
    	ArrayList<Double> widths = new ArrayList<Double>();
    	widths.add(11.0);
    	widths.add(14.0);
    	widths.add(18.75);
    	widths.add(16.25);
    	table.setCustomWidths(widths);
		
    	// Faction and settlement:
    	line = new String[]{"faction","none","settlement","none"};
    	
    	if(sagaPlayer.getRegisteredFaction() != null){
    		line[1] = sagaPlayer.getRegisteredFaction().getName();
    	}
    	
    	if(sagaPlayer.getRegisteredChunkGroup()  != null){
    		line[3] = sagaPlayer.getRegisteredChunkGroup().getName();
    	}
    	
    	table.addLine(line);
    	
    	// Wallet value and guardian rune:
    	line = new String[]{"wallet","","guard rune",""};
    	
    	line[1] = EconomyMessages.coins(sagaPlayer.getCoins());
    	
    	GuardianRune stone = sagaPlayer.getGuardianRune();
		if(!stone.isEnabled()){
			line[3] = "disabled";
		}else{
			
			if(stone.isCharged()){
				line[3] = "charged";
			}else{
				line[3] = "discharged";
			}
			
		}
    	table.addLine(line);
		
		// Reward:
		if(sagaPlayer.getReward() > 0){
			
			int reward = sagaPlayer.getReward();
			table.addLine(new String[]{"reward", EconomyMessages.coins(BalanceConfiguration.config().getCoinReward(reward))});
			table.addLine(new String[]{"", BalanceConfiguration.config().getExpReward(reward) + " exp"});
			
		}
		
		return table;
    	
		
	}
	
	public static StringTable invites(SagaPlayer sagaPlayer) {

		
		StringTable table = new StringTable(new ColorCircle().addColor(normal1).addColor(normal2));
		
		// Table size:
    	ArrayList<Double> widths = new ArrayList<Double>();
    	widths.add(28.5);
    	widths.add(28.5);
    	table.setCustomWidths(widths);
		
    	// Factions:
    	table.addLine("faction invites:");
    	
    	ArrayList<SagaFaction> factions = getFactions(sagaPlayer.getFactionInvites());
    	
    	for (SagaFaction sagaFaction : factions) {
			table.addLine(sagaFaction.getName());
		}
    	
    	if(factions.size() == 0){
    		table.addLine("none");
    	}
    	
    	table.nextColumn();

    	// Chunk groups:
    	table.addLine("settlement invites:");
    	
    	ArrayList<ChunkGroup> chunkGroups = getSettlements(sagaPlayer.getChunkGroupInvites());
    	
    	for (ChunkGroup chunkGroup : chunkGroups) {
			table.addLine(chunkGroup.getName());
		}
    	
    	if(chunkGroups.size() == 0){
    		table.addLine("none");
    	}
    	
		return table;
    	
		
	}

	public static StringTable abilities(SagaPlayer sagaPlayer) {

		
		StringTable table = new StringTable(new ColorCircle().addColor(normal1).addColor(normal2));
		PlayerLevelManager levelManager = sagaPlayer.getLevelManager();
		HashSet<Ability> allAbilities = levelManager.getAllAbilities();
		
		// Table size:
    	ArrayList<Double> widths = new ArrayList<Double>();
    	widths.add(23.0);
    	widths.add(25.0);
    	widths.add(12.0);
    	table.setCustomWidths(widths);
		
    	// Names:
    	for (Ability ability : allAbilities) {
			
    		table.addLine(ability.getName());
    		
		}
    	
//    	table.nextColumn();
//    	
//    	// Bindings:
//    	for (Ability ability : allAbilities) {
//			
//    		ActivationAction action = levelManager.getAbilityAction2(ability.getName());
//    		Material material = levelManager.getAbilityMaterial2(ability.getName());
//    		
//    		if(action == null || material == null){
//    			table.addLine("not binded");
//    		}else{
//    			table.addLine(action.getShortName() + " " + EconomyMessages.material(material));
//    		}
//    		
//		}
    	
    	table.nextColumn();
    	
    	// Cooldown:
    	for (Ability ability : allAbilities) {
			
    		if(!ability.isOnCooldown()){
    			table.addLine("ready");
    		}else{
    			table.addLine(ability.getCooldown() + " s");
    		}
    		
		}
    	
    	
    	// Factions:
    	
		return table;
    	
		
	}
	
	private static ArrayList<SagaFaction> getFactions(ArrayList<Integer> ids) {


		// Faction invites:
		ArrayList<SagaFaction> factions = new ArrayList<SagaFaction>();
		if(ids.size() > 0){
			
			for (int i = 0; i < ids.size(); i++) {
			
				SagaFaction faction = FactionManager.manager().getFaction(ids.get(i));
				if( faction != null ){
					factions.add(faction);
				}else{
					ids.remove(i);
					i--;
				}
				
			}
		}
		
		return factions;
		
		
	}
	
	private static ArrayList<ChunkGroup> getSettlements(ArrayList<Integer> ids) {


		// Faction invites:
		ArrayList<ChunkGroup> chunkGroups = new ArrayList<ChunkGroup>();
		if(ids.size() > 0){
			
			for (int i = 0; i < ids.size(); i++) {
			
				ChunkGroup faction = ChunkGroupManager.manager().getChunkGroup(ids.get(i));
				if( faction != null ){
					chunkGroups.add(faction);
				}else{
					ids.remove(i);
					i--;
				}
				
			}
		}
		
		return chunkGroups;
		
		
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
		book.addLine("Each class/prof enables skill training and provides abilities.");
		
		// Selection:
		book.addLine("Rclick a " + ProficiencySign.SIGN_NAME + " sign to select a class/prof.");

		// Training:
		book.addLine("Rclick a " + SkillSign.SIGN_NAME + " sign to train a skill.");

		// Training cost:
		book.addLine("/trainingcost to list skill training costs.");
		
		// Remove:
		book.addLine("/removeclass or /removeprofession to remove a prof/class.");
		
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
		book.addLine("Rclick a " + RepairStoneSign.SIGN_NAME + " sign at an academy to recharge the rune for " + EconomyMessages.coins(EconomyConfiguration.config().guardianRuneRechargeCost) + ".");
		
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
				line[i] = String.format("%.2g", definition.getPrimaryFunction().calculateValue(levels[i-1]));
			}
			
			table.addLine(line);
			
		}

		// Secondary function:
		line[0] = definition.getSecondaryStat();
		if(line[0].length() > 0){
			
			for (int i = 1; i < line.length; i++) {
				line[i] = String.format("%.2g", definition.getSecondaryFunction().calculateValue(levels[i-1]));
			}
			
			table.addLine(line);
			
		}

		// Consumption function:
		if(!definition.getUsedMaterial().equals(Material.AIR)){
			
			line[0] = EconomyMessages.materialShort(definition.getUsedMaterial()) + " used";
		
			for (int i = 1; i < line.length; i++) {
				line[i] = definition.getUsedAmount(levels[i-1]).toString();
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
		table.addLine(" TRAINING");
		for (String skillName : skillNames) {

			Integer multiplier = sagaPlayer.getSkillMultiplier(skillName);
			
			table.addLine(EconomyMessages.coins(EconomyConfiguration.config().getSkillCoinCost(multiplier)));
			
		}
		
		table.nextColumn();
		
		// Level costs:
		table.addLine("COST");
		for (String skillName : skillNames) {

			Integer multiplier = sagaPlayer.getSkillMultiplier(skillName);
			
			table.addLine(ExperienceConfiguration.config().getSkillLevelCost(multiplier) + " lvls");
			
		}
		
		result.append(table.createTable());
		
		
//		result.append("\n");
		
		// Abilities:
		
		return TextUtil.frame("player info table", normal1, result.toString(), table.calcTotalWidth());
		
		
	}
	
	
	// Abilities:
	public static String onCooldown(Ability ability) {
		
		return meterCooldown(ability.getName(), ability.getCooldown(), ability.getTotalCooldown(), normal2);
		
	}
	
	public static String cooldownEnd(Ability ability) {
		
		return positive + TextUtil.capitalize(ability.getName()) + " ready.";
		
	}
	
	public static String alreadyActive(Ability ability) {
		
		return negative + TextUtil.capitalize(ability.getName()) + " is already active.";
		
	}
	
	public static String insufficientMaterials(Ability ability, Material material, Integer amount) {
		return negative + "" + amount + " " + EconomyMessages.material(material) + " required to use " + ability.getName() + " ability.";
	}
	
	public static String invalidAbility(String name) {
		
		return negative + name + " isn't a valid ability.";
		
	}
	
	public static String used(Ability ability) {
		return positive + "Used " + ability.getName() + " ability.";
	}

	public static String activated(Ability ability) {
		return positive + "Activated " + ability.getName() + " ability.";
	}
	
	public static String deactivated(Ability ability) {
		return positive + "Deactivated " + ability.getName() + " ability.";
	}
	
	
	// Inventory:
	public static String inventoryFullDropping() {
		
		return negative + "Inventory full, dropped items on the ground.";
		
	}
	
	
	// Proficiencies:
	public static String proficiencySelected(Proficiency proficiency, Double currency, Integer levels) {
		
		
		StringBuffer cost = new StringBuffer();
		
		if(!levels.equals(0)){
			cost.append(levels + " level points");
		}
		
		if(!currency.equals(0.0)){
			
			if(cost.length() > 0){
				cost.append(" and ");
			}
			
			cost.append(EconomyMessages.coins(currency));
			
		}
		
		if(cost.length() > 0){
			return positive + "Selected " + proficiency.getName() + " " + proficiency.getType().getName() + " for " + cost.toString() + ".";
		}else{
			return positive + "Selected " + proficiency.getName() + " " + proficiency.getType().getName() + ".";
		}
		
		
	}
	
	public static String alreadySelected(Proficiency proficiency) {
		
		
		return negative + TextUtil.capitalize(proficiency.getName()) + " " + proficiency.getType().getName() + " is already selected.";
		
		
	}
	
	public static String proficiencyRemoved(Proficiency proficiency) {
		
		return positive + "Removed " + proficiency.getName() + " " + proficiency.getType().getName() + ".";
		
	}
	
	public static String oneProficiencyAllowed2(Proficiency proficiency) {
		
		return negative + "Only one " + proficiency.getType().getName() + " is allowed.";
		
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
	public static String levelsNeeded(String skillName, Integer required) {
		
		return negative + "Training a " + skillName + " skill requires " + required + " level points.";
		
	}
	
	public static String coinsNeeded(String skillName, Double required) {
		
		return negative + "Training a " + skillName + " skill requires " + EconomyMessages.coins(required) + ".";
		
	}
	
	public static String limitReached(String skillName) {
		
		return negative + "Can't train " + skillName + " skill any farther.";
		
	}
	
	public static String trained(String skillName, Integer multiplier, Integer levels, Double coins) {
		
		return positive + "Trained " + skillName + " skill to " + multiplier + " for " + levels + " levels and " + EconomyMessages.coins(coins);
		
	}
	
	
	// Player versus player:
	public static String pvpDeny(SagaPvpEvent event){
		
		
		PvpDenyReason cause = event.getReason();
		
		switch (cause) {
		case SAME_FACTION:
			
			return negative + event.getSagaDefender().getName() + " is from the same faction.";
			
		case ALLY:
	
		return negative + event.getSagaDefender().getName() + " is an ally.";
	
		case ATTACKER_NO_FACTION:
			
			return negative + "You must be in a faction.";
			
		case DEFENDER_NO_FACTION:
			
			return negative + event.getSagaDefender().getName() + " must be in a faction.";
			
		case SAFE_AREA:
			
			return negative + "Pvp not allowed in safe areas.";
			
		}
		
		return negative + "Pvp isn't allowed.";
		
	}
	
	
	// Experience:
	public static String deathExpInfo(){
		
		return normal1 + "Visit a temple to regain some of your lost experience.";
		
	}
	
	public static String meter(String name, Integer value, Integer maxValue, String unit, ChatColor messageColor, ChatColor barColor, ChatColor enclosingColor) {

		
		// Tnx Heroes RPG for the good idea.
		
		Integer barLength = 20;
		StringBuffer rString = new StringBuffer();
		
		// Normalize:
		if(value > maxValue){
			value = maxValue;
		}
		
		Integer normalizedValue = new Double(barLength.doubleValue() * value/maxValue).intValue();

		// Add bar name:
		rString.append(messageColor);
		rString.append(TextUtil.capitalize(name));
		rString.append(": ");
		
		// Add enclosing element:
		rString.append(enclosingColor);
		rString.append("{]");
		
		// Add bar:
		rString.append(barColor);
		rString.append(TextUtil.repeat("||", normalizedValue));
		rString.append(TextUtil.repeat(" ", barLength - normalizedValue));
		
		// Add enclosing element:
		rString.append(enclosingColor);
		rString.append("[}");
		
		// Add value:
		rString.append(messageColor);
		rString.append(" - " + value + "" + unit);
		
		return rString.toString();
		
		
	}
	
	public static String meterCooldown(String cooldownName, int value, int maxValue, ChatColor messageColor) {
		return meter(cooldownName + " cooldown", value, maxValue, "s", messageColor, ChatColor.RED, ChatColor.DARK_GRAY);
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
		
		
		
	}
	
	
}
