package org.saga.messages;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.saga.abilities.Ability;
import org.saga.abilities.AbilityDefinition;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.ChunkGroupManager;
import org.saga.config.AttributeConfiguration;
import org.saga.config.ExperienceConfiguration;
import org.saga.factions.FactionManager;
import org.saga.factions.Faction;
import org.saga.messages.PlayerMessages.ColorCircle;
import org.saga.player.GuardianRune;
import org.saga.player.SagaPlayer;
import org.saga.utility.text.StringTable;
import org.saga.utility.text.TextUtil;

public class StatsMessages {

	
	public static ChatColor veryPositive = ChatColor.DARK_GREEN;
	
	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED;
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor anouncment = ChatColor.AQUA;
	
	public static ChatColor normal1 = ChatColor.GOLD;
	
	public static ChatColor normal2 = ChatColor.YELLOW;
	
	
	
	
	// Stats command:
	public static String stats(SagaPlayer sagaPlayer, Integer page) {

		
		if(page > 2) page = 2;
		if(page < 0) page = 0;
		
		StringBuffer result = new StringBuffer();
		
		switch (page) {
			case 2:
				
				result.append(invites(sagaPlayer).createTable());
				
				break;
				
			case 1:
				
				result.append(abilities(sagaPlayer).createTable());
				
				break;

			default:
				
				// Attributes and levels:
				result.append(attributesLevels(sagaPlayer).createTable());
				
				result.append("\n");
				result.append("\n");
				
				// Faction and settlement:
				result.append(factionSettlement(sagaPlayer).createTable());
				
				result.append("\n");
				result.append("\n");
				
				// General:
				result.append(general(sagaPlayer).createTable());
				
				break;
				
		}
		
		
		return TextUtil.frame("player stats " + (page+1) + "/" + 3, normal1, result.toString(), 57.0);
		
		
	}
	
	
	private static StringTable attributesLevels(SagaPlayer sagaPlayer) {

		
		StringTable table = new StringTable(new ColorCircle().addColor(normal1).addColor(normal2));
		DecimalFormat format = new DecimalFormat("00");
		
		// Attributes:
		ArrayList<String> attrNames = AttributeConfiguration.config().getAttributeNames();
		for (String attrName : attrNames) {
			
			Integer attrBonus = sagaPlayer.getAttrScoreBonus(attrName);
			Integer attrScore = sagaPlayer.getRawAttributeScore(attrName);
			
			String scoreCurr = format.format(attrScore + attrBonus);
			String scoreMax = format.format(AttributeConfiguration.config().maxAttributeScore + attrBonus);
			
			String score = scoreCurr + "/" + scoreMax;
			
			// Colours:
			if(attrBonus > 0){
				score = positive + score;
			}
			else if(attrBonus < 0){
				score = negative + score;
			}
			
			table.addLine(attrName, score, 0);
			
		}
		
		// Levels:
		table.addLine("Level", sagaPlayer.getLevel() + "/" + ExperienceConfiguration.config().maximumLevel, 2);
		table.addLine("Next EXP", sagaPlayer.getRemainingExp().intValue() + "", 2);
		
		String attrPoints = sagaPlayer.getUsedAttributePoints() + "/" + sagaPlayer.getAvailableAttributePoints();
		if(sagaPlayer.getRemainingAttributePoints() < 0){
			attrPoints = ChatColor.DARK_RED + attrPoints;
		}
		else if (sagaPlayer.getRemainingAttributePoints() > 0) {
			attrPoints = ChatColor.DARK_GREEN + attrPoints;
		}
		table.addLine("Attributes", attrPoints, 2);
		
		// Style:
		table.collapse();
		
		return table;
		

	}
	
	private static StringTable factionSettlement(SagaPlayer sagaPlayer) {

		
		StringTable table = new StringTable(new ColorCircle().addColor(normal1).addColor(normal2));

		
		// Faction and settlement:
		String faction = "none";
		if(sagaPlayer.getFaction() != null) faction = sagaPlayer.getFaction().getName();

		String settlement = "none";
		if(sagaPlayer.getChunkGroup() != null) settlement = sagaPlayer.getChunkGroup().getName();
		
		table.addLine("faction", faction, 0);
		table.addLine("settlement", settlement, 2);
		
		// Rank and role:
		String rank = "none";
		if(sagaPlayer.getRank() != null) rank = sagaPlayer.getRank().getName();

		String role = "none";
		if(sagaPlayer.getRole() != null) role = sagaPlayer.getRole().getName();

		table.addLine("rank", rank, 0);
		table.addLine("role", role, 2);
		
		// Style:
		table.collapse();
		
		return table;
		

	}
	
	private static StringTable general(SagaPlayer sagaPlayer) {

		StringTable table = new StringTable(new ColorCircle().addColor(normal1).addColor(normal2));

		// Wallet:
		table.addLine("Wallet", EconomyMessages.coins(sagaPlayer.getCoins()), 0);
		
		// Guard rune:
		GuardianRune guardRune = sagaPlayer.getGuardRune();
		String rune = "";
		if(!guardRune.isEnabled()){
			rune = "disabled";
		}else{

			if(guardRune.isCharged()){
				rune= "charged";
			}else{
				rune= "discharged";
			}

		}
		table.addLine("Guard rune", rune, 2);
		
		table.collapse();
		
		return table;
		

	}

	
	private static StringTable abilities(SagaPlayer sagaPlayer) {

		
		StringTable table = new StringTable(new ColorCircle().addColor(normal1).addColor(normal2));
		HashSet<Ability> allAbilities = sagaPlayer.getAbilities();
		
    	// Add abilities:
    	if(allAbilities.size() > 0){
    		
    		for (Ability ability : allAbilities) {
    			
    			String name = GeneralMessages.scoreAbility(ability);
    			String status = "";
    			
    			if(ability.getScore() <= 0){
    				name = unavailable + name;
    				status = unavailable + "(" + requirements(ability.getDefinition(), 1) + ")";
    			}
    			
    			else{
    				
    				if(ability.getCooldown() <= 0){
    					status = "ready";
    				}else{
    					status = ability.getCooldown() + "s";
    				}
    				
    			}
    			
    			table.addLine(name, status, 0);
    			
    		}
    		
    	}
    	
    	// No abilities:
    	else{
    		table.addLine("-");
    	}
    	
    	table.collapse();
    	
		return table;
    	
		
	}
	
	public static String requirements(AbilityDefinition ability, Integer abilityScore) {

		
		StringBuffer result = new StringBuffer();
		
		ArrayList<String> attributeNames = AttributeConfiguration.config().getAttributeNames();
		
		for (String attribute : attributeNames) {
			
			Integer reqScore = ability.getAttrReq(attribute, abilityScore);
			if(reqScore <= 0) continue;
			
			if(result.length() > 0) result.append(", ");
			
			result.append(GeneralMessages.attrAbrev(attribute) + " " + reqScore);
			
		}
		
		return result.toString();
		
		
	}
	

	public static StringTable invites(SagaPlayer sagaPlayer) {

		
		StringTable table = new StringTable(new ColorCircle().addColor(normal1).addColor(normal2));
		
		// Table size:
    	ArrayList<Double> widths = new ArrayList<Double>();
    	widths.add(28.5);
    	widths.add(28.5);
    	table.setCustomWidths(widths);
		
    	// Factions:
    	table.addLine(GeneralMessages.columnTitle("faction invites"));
    	
    	ArrayList<Faction> factions = getFactions(sagaPlayer.getFactionInvites());
    	
    	for (Faction faction : factions) {
			table.addLine(faction.getName());
		}
    	
    	if(factions.size() == 0){
    		table.addLine("-");
    	}
    	
    	table.nextColumn();

    	// Chunk groups:
    	table.addLine(GeneralMessages.columnTitle("settlement invites"));
    	
    	ArrayList<ChunkGroup> chunkGroups = getSettlements(sagaPlayer.getChunkGroupInvites());
    	
    	for (ChunkGroup chunkGroup : chunkGroups) {
			table.addLine(chunkGroup.getName());
		}
    	
    	if(chunkGroups.size() == 0){
    		table.addLine("-");
    	}
    	
		return table;
    	
		
	}
	
	private static ArrayList<Faction> getFactions(ArrayList<Integer> ids) {


		// Faction invites:
		ArrayList<Faction> factions = new ArrayList<Faction>();
		if(ids.size() > 0){
			
			for (int i = 0; i < ids.size(); i++) {
			
				Faction faction = FactionManager.manager().getFaction(ids.get(i));
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
	
	
	
	
}
