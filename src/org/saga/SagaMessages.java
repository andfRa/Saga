package org.saga;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.saga.buildings.Building;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.SagaMap;
import org.saga.config.BalanceConfiguration;
import org.saga.economy.EconomyMessages;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Settlement;
import org.saga.utility.TextUtil;


public class SagaMessages {

	
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
	
	
	// Strings:
	public static String spaceSymbol = "_";
	
	
	// Player retrieval:
	public static String invalidPlayer(String playerName){
		return "" + playerName +" player doesn't exist.";
	}

	public static String notOnline(String name) {
		return negative + name + " isn't online.";
	}
	

	// Levels:
	public static String levelSet(SagaPlayer sagaPlayer){
		return positive + sagaPlayer.getName() + " level set to " + sagaPlayer.getLevel() + ".";
	}
	
	public static String levelSetTo(SagaPlayer sagaPlayer){
		return positive + "Level was set to " + sagaPlayer.getLevel() + ".";
	}

	public static String levelOutOfRange(String level){
		return negative + level + " is out of range. Level must be a in the range of 0 - " + BalanceConfiguration.config().maximumLevel + ".";
	}

	
	// Skills:
	public static String skillSet(SagaPlayer sagaPlayer, String skill){
		return positive + sagaPlayer.getName() + " " + skill + " skill set to " + sagaPlayer.getSkillMultiplier(skill) + ".";
	}
	
	public static String skillSetTo(SagaPlayer sagaPlayer, String skill){
		return positive + TextUtil.capitalize(skill) + " skill was set to " + sagaPlayer.getSkillMultiplier(skill) + ".";
	}

	public static String skillOutOfRange(String skill){
		return negative + skill + " is out of range. Skill must be a in the range of 0 - " + BalanceConfiguration.config().maximumSkillLevel + ".";
	}
	
	public static String invalidSkill(String skill){
		return negative + skill + " isn't a valid skill.";
	}
	
	
	// Administrator mode:
	public static String adminMode(SagaPlayer sagaPlayer) {
		
		
		if(sagaPlayer.isAdminMode()){
			return positive + "Admin mode enabled.";
		}else{
			return positive + "Admin mode disabled.";
		}
		
		
	}
	
	public static String adminModeAlreadyEnabled() {
		
		return negative + "Admin mode already enabled.";
		
	}
	
	public static String adminModeAlreadyDisabled() {
		
		return negative + "Admin mode already disabled.";
		
	}

	
	// Permission:
	public static String noPermission(){
		return negative + "You don't have permission to do that.";
	}
	
	public static String noPermission(ChunkGroup chunkGroup){
		return negative + "You don't have permission to do that (" + chunkGroup.getName() + " settlement).";
	}
	
	public static String noPermission(Building building){
		return negative + "You don't have permission to do that (" + building.getName() + " building).";
	}
	
	public static String noPermission(Settlement settlement){
		return negative + "You don't have permission to do that (" + settlement.getName() + " settlement).";
	}
	
	public static String noCommandPermission(ChunkGroup chunkGroup, String command){
		return negative + "You don't have permission use " + command + " command (" + chunkGroup.getName() + " settlement).";
	}
	
	
	// Map:
	public static String map(SagaPlayer sagaPlayer, Location location){
		
		
		ArrayList<String> map = SagaMap.getMap(sagaPlayer, sagaPlayer.getLocation());
		StringBuffer result = new StringBuffer();
		
		// Add borders:
		result.append(" ");
		map.add(0, " ");
		for (int i = 0; i < map.size(); i++) {
			
			if(i != 0) result.append("\n");
			
			result.append("  " + map.get(i) + "  ");
			
		}
		result.append(" ");
		
		Chunk locationChunk = location.getWorld().getChunkAt(location);
		String title = locationChunk.getWorld().getName() + " map " + "(" + locationChunk.getX() + ", " + locationChunk.getZ() + ")";
		
		return TextUtil.frame(title, result.toString(), ChatColor.GOLD);
		
		
	}
	
	
	// Reward:
	public static String rewarded(ArrayList<String> players) {

		return positive + "Rewarded " + players.size() + " players.";
		
	}
	
	public static String playersNotFound(ArrayList<String> players) {

		return negative + "Failed to find players: " + TextUtil.flatten(players) + ".";
		
	}
	
	public static String noReward() {

		return negative + "No reward available.";
		
	}
	
	public static String collectedReward(Integer exp, Double coins) {

		return positive + "Recieved " + exp + " exp and " + EconomyMessages.coins(coins) + ".";
		
	}


	// Saving loading:
	public static String saving() {
		return veryPositive + "Saving Saga information.";
	}
	
	public static String saved() {
		return veryPositive + "Saving complete.";
	}
	
	
	// Additional info:
	public static String statsTargetName(SagaPlayer sagaPlayer) {
		return positive + "Stats for " + sagaPlayer.getName() + ".";
	}
	
	
}
