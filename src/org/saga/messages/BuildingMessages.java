package org.saga.messages;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.saga.abilities.Ability;
import org.saga.buildings.Arena;
import org.saga.buildings.Arena.ArenaPlayer;
import org.saga.buildings.Building;
import org.saga.buildings.TownSquare;
import org.saga.chunks.ChunkBundle;
import org.saga.config.AbilityConfiguration;
import org.saga.config.AttributeConfiguration;
import org.saga.messages.PlayerMessages.ColourLoop;
import org.saga.utility.text.StringTable;
import org.saga.utility.text.TextUtil;

public class BuildingMessages {


	// Colours:
	public static ChatColor veryPositive = ChatColor.DARK_GREEN; // DO NOT OVERUSE.
	
	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED; // DO NOT OVERUSE.
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor anouncment = ChatColor.AQUA;
	
	public static ChatColor normal1 = ChatColor.GOLD;
	
	public static ChatColor normal2 = ChatColor.YELLOW;
	
	public static ChatColor frame = ChatColor.DARK_GREEN;
	
	
	

	// Adding buildings:
	public static String notOnClaimedLand(ChunkBundle chunkBundle) {
		return negative + "Buildings can only be on located on claimed land.";
	}
	
	public static String oneBuildingAllowed(ChunkBundle chunkBundle) {
		return negative + "A chunk of land can only have one building.";
	}
	
	public static String invalidBuilding(String buildingName) {
		return negative + buildingName + " isn't a valid building.";
	}
	
	public static String unavailable(Building building){
		
		return negative + TextUtil.capitalize(building.getDisplayName()) + " isn't available.";
		
	}

	
	
	// General buildings:
	public static String invalidBuilding(String correctBuildingName, String command){
		
		return negative + command + " can only be used from a " + correctBuildingName + ".";
		
	}
	
	public static String noBuilding(){
		
		return negative + "No building on this chunk of land.";
		
	}
	
	
	
	// Storage:
	public static String storeAreaOverlap(){
		
		return negative + "Storage areas can't overlap.";
		
	}
	
	public static String storeAreaSingleChunk(){
		
		return negative + "Storage area must be on the same chunk of land.";
		
	}
	
	public static String storeAreaAdded(Building building){
		
		return positive + "Storage area added to " + building.getName() + ".";
		
	}

	public static String storeAreaRemoved(Building building){
		
		return positive + "Storage area removed from " + building.getName() + ".";
		
	}
	
	public static String storeAreaNotFound(Building building){
		
		return negative + "Storage area not found.";
		
	}

	public static String storeAreasUnavailable(Building building){
		return negative + "No storage areas available for " + building.getName() + ".";
	}
	
	public static String stored(Material material, Building building) {
		
		if(material == Material.CHEST) return positive + "Added item storage.";
		
		return positive + "Stored " + EconomyMessages.material(material) + ".";

	}
	
	public static String withdrew(Material material, Building building) {

		if(material == Material.CHEST) return positive + "Removed item storage.";
		
		return positive + "Withdrew " + EconomyMessages.material(material) + ".";

	}
	
	public static String openedItemStore() {

		return positive + "Opened item storage.";

	}
	
	
	
	// Arena:
	public static String countdown(int count) {
		
		if(count == 0){
			return SettlementMessages.positive + "Fight!";
		}else if((count%2)==0){
			return SettlementMessages.normal1 + "" + count + ".";
		}else{
			return SettlementMessages.normal2 + "" + count + ".";
		}
		
	}

	
	
	// Town square:
	public static String noTownSquare(ChunkBundle chunkBundle){
		
		return BuildingMessages.negative + "" + chunkBundle.getName() + " deosen't have a " + TextUtil.className(TownSquare.class) + ".";
		
	}
	
	
	
	// Movement:
	public static String entered(Building building) {
		
		return normal1 + "" + ChatColor.ITALIC + "Entered " + building.getDisplayName() + ".";
		
	}
	
	public static String left(Building building) {
		
		return normal1 + "" + ChatColor.ITALIC + "Left " + building.getDisplayName() + ".";
		
	}
	
	
	
	
	// General:
	public static String cooldown(String buildingName, Integer secondsLeft) {
		return negative + TextUtil.capitalize(buildingName) + " is on cooldown for " + TextUtil.fromSeconds(secondsLeft) + ".";
	}
	
	
	
	
	// Arena:
	public static String arenaTop(Arena arena, Integer count) {
		
		
		ArrayList<ArenaPlayer> topPlayers = arena.getTop(count);
		ColourLoop messageColor = new ColourLoop().addColor(normal1).addColor(normal2);
		
		StringTable table = new StringTable(messageColor);
		
		
		Integer listLen = count;
		
		// Fix count:
		if(listLen > topPlayers.size()) listLen = topPlayers.size();
		
		// Names:
		table.addLine(new String[]{"NAME","SCORE","KILLS","DEATHS","KDR"});
		
		// Nobody:
		if(topPlayers.size() == 0){
			
			table.addLine(new String[]{"-","-","-","-","-"});
			
		}
		
		// Arena players:
		for (ArenaPlayer arenaPlayer : topPlayers) {

			listLen --;
			if(listLen < 0) break;
			
			String kdr = "";
			if(arenaPlayer.getDeaths() == 0){
				kdr = "-";
			}else{
				kdr = TextUtil.displayDouble(arenaPlayer.getKills().doubleValue() / arenaPlayer.getDeaths().doubleValue());
			}
			
			table.addLine(
				new String[]{
					arenaPlayer.getName(),
					arenaPlayer.calculateScore().intValue() + "",
					arenaPlayer.getKills().toString(),
					arenaPlayer.getDeaths().toString(),
					kdr
				});
			
			
		}
		
		table.collapse();
		
		return TextUtil.frame("Top " + count, table.createTable(), messageColor.nextColour());
		
		
	}
	
	
	
	// Farm:
	public static String farmAnimalsDamageDeny() {
		return negative + "Can't harm animals on this farms.";
	}
	
	
	
	// Home:
	public static String alreadyResident(String name) {
		return SettlementMessages.negative + name + " is already a resident.";
	}
	
	public static String notResident(String name) {
		return SettlementMessages.negative + name + " is not a resident.";
	}

	public static String addedResident(String name) {
		return SettlementMessages.positive + "Added " + name + " to the resident list.";
	}
	
	public static String removedResident(String name) {
		return SettlementMessages.positive + "Removed " + name + " from the resident list.";
	}

	
	
	
	
	// Attribute sign:
	public static String attributeMaxReached(String attribute) {
		return negative + "Can't increase " + attribute + " above " + AttributeConfiguration.config().maxAttributeScore + ".";
	}

	public static String attributeIncreased(String attribute, Integer score) {
		return positive + TextUtil.capitalize(attribute) + " increased to " + score + ".";
	}

	public static String attributePointsRequired(String attribute) {
		return negative + "Not enough attribute points to increase " + attribute + ".";
	}
	
	
	
	
	// Ability sign:
	public static String abilityMaxReached(String ability) {
		return negative + "Can't upgrade " + ability + " above " + AbilityConfiguration.config().maxAbilityScore + ".";
	}

	public static String abilityUpgraded(String ability, Integer score) {
		
		if(score == 1){
			return positive + TextUtil.capitalize(ability) + " learned.";
		}
		return positive + "Upgraded to " + GeneralMessages.scoreAbility(ability, score) + ".";
		
	}
	
	public static String abilityReqNotMet(Ability ability, Integer score) {
		
		if(score == 1){
			return negative + StatsMessages.requirements(ability.getDefinition(), score) + " is required to learn " + ability.getName() + ".";
		}
		
		return negative + TextUtil.capitalize(GeneralMessages.scoreAbility(ability.getName(), score)) + " upgrade requires " + StatsMessages.requirements(ability.getDefinition(), score) + ".";
		
	}

	public static String abilityCost(String ability, Integer score, Double cost) {
		
		if(score == 1){
			return normal1 + "It costs " + EconomyMessages.coins(cost) + " to learn " + ability + ".";
		}
		
		return normal1 + TextUtil.capitalize(GeneralMessages.scoreAbility(ability, score)) + " upgrade costs " + EconomyMessages.coins(cost) + ".";
		
	}
		
	
	
	
	// Respec sign:
	public static String respecCost(String attribute, Integer score, Double cost) {
		
		if(score == 0){
			return normal1 + TextUtil.capitalize(attribute) + " is already 0.";
		}
		
		return normal1 + TextUtil.capitalize(attribute) +  " " + score + " reset costs " + EconomyMessages.coins(cost) + ".";
		
	}
	
	public static String respec(String attribute) {
		
		return normal1 + TextUtil.capitalize(attribute) + " reset.";
		
	}
	
	public static String alreadyRespec(String attribute) {
		
		return negative + TextUtil.capitalize(attribute) + " is already 0.";
		
	}
		
	
	
	
}
