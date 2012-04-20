package org.saga.messages;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.saga.abilities.Ability;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.ChunkGroupManager;
import org.saga.config.BalanceConfiguration;
import org.saga.config.SkillConfiguration;
import org.saga.economy.EconomyMessages;
import org.saga.factions.FactionManager;
import org.saga.factions.SagaFaction;
import org.saga.messages.PlayerMessages.ColorCircle;
import org.saga.player.GuardianRune;
import org.saga.player.PlayerLevelManager;
import org.saga.player.SagaPlayer;
import org.saga.player.SkillType;
import org.saga.utility.StringTable;
import org.saga.utility.TextUtil;

public class StatsMessages {

	
	public static ChatColor veryPositive = ChatColor.DARK_GREEN;
	
	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED;
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor anouncment = ChatColor.AQUA;
	
	public static ChatColor normal1 = ChatColor.GOLD;
	
	public static ChatColor normal2 = ChatColor.YELLOW;
	
	

	// Stats:
	public static String stats(SagaPlayer sagaPlayer, Integer page) {
		
		
		StringBuffer result = new StringBuffer();
		
		if(page > 2) page = 2;
		if(page < 0) page = 0;
		
		switch (page) {
			
			// Abilities:
			case 1:
				
				result.append(abilities(sagaPlayer).createTable());
				
				break;

			// Invites:
			case 2:
				
				result.append(invites(sagaPlayer).createTable());
				
				break;
				
			// Skills and proficiencies:
			default:
				
				// Skills:
				StringTable skills = skills(sagaPlayer);
				result.append(skills.createTable());

				result.append("\n");
				result.append("\n");

				// Proficiencies:
				result.append(proficiencies(sagaPlayer).createTable());

				result.append("\n");
				result.append("\n");

				// General:
				result.append(general(sagaPlayer).createTable());
				
				
				break;
				
		}
		
		// Add frame:
		return TextUtil.frame("player info " + (page+1) + "/" + 3, normal1, result.toString(), 57.0);
		
		
	}
	
	public static StringTable skills(SagaPlayer sagaPlayer) {

		
		PlayerLevelManager levelManager = sagaPlayer.getLevelManager();
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
		
		table.nextColumn();
		
		// Other:
		table.addLine("");
		table.addLine("current level");
		table.addLine("remaining exp");
		table.addLine("unspent skills");
		
		table.nextColumn();
		
		table.addLine("");
		table.addLine(sagaPlayer.getLevel().toString());
		table.addLine(sagaPlayer.getRemainingExp().intValue() + "");
		
		Integer remainingSkills = sagaPlayer.getRemainingSkillPoints();
		if(remainingSkills < 0){
			table.addLine(negative + remainingSkills.toString());
		}else if(remainingSkills > 0){
			table.addLine(positive + remainingSkills.toString());
		}else{
			table.addLine(remainingSkills.toString());
		}
		
		return table;
    	
		
	}

	public static StringTable proficiencies(SagaPlayer sagaPlayer) {

		
		StringTable table = new StringTable(new ColorCircle().addColor(normal1).addColor(normal2));
		
    	// Table size:
    	ArrayList<Double> widths = new ArrayList<Double>();
    	widths.add(7.75);
    	widths.add(20.25);
    	widths.add(7.75);
    	widths.add(24.25);
    	table.setCustomWidths(widths);
		
    	LinkedList<String> names = new LinkedList<String>();
    	LinkedList<String> values = new LinkedList<String>();
    	
    	// Prof:
    	names.add("prof");
    	if(sagaPlayer.getProfession() != null){
    		values.add(sagaPlayer.getProfession().getName());
    	}else{
    		values.add("none");
    	}
    	
    	// Class:
    	names.add("class");
    	if(sagaPlayer.getClazz() != null){
    		values.add(sagaPlayer.getClazz().getName());
    	}else{
    		values.add("none");
    	}

    	// Role:
    	names.add("role");
    	if(sagaPlayer.getRole() != null){
    		values.add(sagaPlayer.getRole().getName());
    	}else{
    		values.add("none");
    	}
    	
    	// Rank:
    	names.add("rank");
    	if(sagaPlayer.getRank() != null){
    		values.add(sagaPlayer.getRank().getName());
    	}else{
    		values.add("none");
    	}
    	
    	boolean first = true;
    	while (!names.isEmpty()) {
    		
    		String name = names.remove();
    		String value = values.remove();
    		
    		if(first){
    			
    			table.addLine(name, 0);
    			table.addLine(value, 1);
    			
    			first = !first;
    			
    		}else{

    			table.addLine(name, 2);
    			table.addLine(value, 3);
    			
    			first = !first;
    			
    		}
    		
		}
    	
		return table;
    	
		
	}

	public static StringTable general(SagaPlayer sagaPlayer) {

		
		StringTable table = new StringTable(new ColorCircle().addColor(normal1).addColor(normal2));
    	
    	// Table size:
    	ArrayList<Double> widths = new ArrayList<Double>();
    	widths.add(12.0);
    	widths.add(16.0);
    	widths.add(16.75);
    	widths.add(15.25);
    	table.setCustomWidths(widths);
		

    	LinkedList<String> names = new LinkedList<String>();
    	LinkedList<String> values = new LinkedList<String>();

    	// Faction:
    	names.add("faction");
    	if(sagaPlayer.getRegisteredFaction() != null){
    		values.add(sagaPlayer.getRegisteredFaction().getName());
    	}else{
    		values.add("none");
    	}

    	// Settlement:
    	names.add("settlement");
    	if(sagaPlayer.getRegisteredChunkGroup()  != null){
    		values.add(sagaPlayer.getRegisteredChunkGroup().getName());
    	}else{
    		values.add("none");
    	}
    	
    	// Wallet:
    	names.add("wallet");
    	values.add(EconomyMessages.coins(sagaPlayer.getCoins()));
    	
    	// Guard rune:
    	names.add("guard rune");
    	GuardianRune rune = sagaPlayer.getGuardianRune();
		if(!rune.isEnabled()){
			values.add("disabled");
		}else{
			
			if(rune.isCharged()){
				values.add("charged");
			}else{
				values.add("discharged");
			}
			
		}

		// Reward:
		if(sagaPlayer.getReward() > 0){
			
			int reward = sagaPlayer.getReward();
			
			names.add("reward");
			values.add(EconomyMessages.coins(BalanceConfiguration.config().getCoinReward(reward)));
			
			names.add("reward");
			values.add(BalanceConfiguration.config().getExpReward(reward) + " exp");

		}
		
    	boolean first = true;
    	while (!names.isEmpty()) {
    		
    		String name = names.remove();
    		String value = values.remove();
    		
    		if(first){
    			
    			table.addLine(name, 0);
    			table.addLine(value, 1);
    			
    			first = !first;
    			
    		}else{

    			table.addLine(name, 2);
    			table.addLine(value, 3);
    			
    			first = !first;
    			
    		}
    		
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
    	table.addLine("FACTION INVITES");
    	
    	ArrayList<SagaFaction> factions = getFactions(sagaPlayer.getFactionInvites());
    	
    	for (SagaFaction sagaFaction : factions) {
			table.addLine(sagaFaction.getName());
		}
    	
    	if(factions.size() == 0){
    		table.addLine("-");
    	}
    	
    	table.nextColumn();

    	// Chunk groups:
    	table.addLine("SETTLEMENT INVITES");
    	
    	ArrayList<ChunkGroup> chunkGroups = getSettlements(sagaPlayer.getChunkGroupInvites());
    	
    	for (ChunkGroup chunkGroup : chunkGroups) {
			table.addLine(chunkGroup.getName());
		}
    	
    	if(chunkGroups.size() == 0){
    		table.addLine("-");
    	}
    	
		return table;
    	
		
	}

	public static StringTable abilities(SagaPlayer sagaPlayer) {

		
		StringTable table = new StringTable(new ColorCircle().addColor(normal1).addColor(normal2));
		PlayerLevelManager levelManager = sagaPlayer.getLevelManager();
		HashSet<Ability> allAbilities = levelManager.getAllAbilities();
		
		// Table size:
//    	ArrayList<Double> widths = new ArrayList<Double>();
//    	widths.add(15.0);
//    	widths.add(15.0);
//    	widths.add(15.0);
//    	widths.add(20.0);
//    	table.setCustomWidths(widths);
		
    	// Names:
    	String[] line = new String[]{"ABILIY","COOLDOWN", "ACTIVE", "COST"};
    	table.addLine(line);
    	
    	if(allAbilities.size() > 0){
    		
    		for (Ability ability : allAbilities) {
    			
    			line = new String[]{ability.getName(),"ready", "-", "-"};
    			
    			if(ability.isOnCooldown()){
    				line[1] = ability.getCooldown() + "s";
    			}
    			
    			if(ability.isActive() && ability.getActive() > 0){
    				line[2] = ability.getActive() + "s";
    			}

    			Material material = ability.getDefinition().getUsedMaterial();
    			Integer amount = ability.getDefinition().getAbsoluteUsedAmount(ability.getSkillLevel());
    			if(material != Material.AIR){
    				line[3] = amount + " " + EconomyMessages.materialShort(material);
    			}
    			
        		table.addLine(line);
        		
    		}
    		
    	}else{
    		
    		table.addLine(line = new String[]{"-","-","-","-"});
    		
    	}
    	
    	table.collapse();
    	
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
	
	
	// Level:
	public static String levelup(Integer level) {
		
		return veryPositive + "Reached level " + level + ".";
		
	}
	
	

	// Abilities:
	public static String onCooldown(Ability ability) {
		
		return SagaMessages.meterCooldown(ability.getName(), ability.getCooldown(), ability.getTotalCooldown(), normal2);
		
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
	
	
	
	
	
}
