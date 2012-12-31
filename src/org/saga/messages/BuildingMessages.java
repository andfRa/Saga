package org.saga.messages;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.saga.buildings.Arena;
import org.saga.buildings.Arena.ArenaPlayer;
import org.saga.buildings.Building;
import org.saga.buildings.CrumbleArena;
import org.saga.buildings.CrumbleArena.CrumblePlayer;
import org.saga.buildings.TownSquare;
import org.saga.config.AttributeConfiguration;
import org.saga.messages.colours.Colour;
import org.saga.messages.colours.ColourLoop;
import org.saga.settlements.Bundle;
import org.saga.utility.chat.ChatFramer;
import org.saga.utility.chat.ChatTable;
import org.saga.utility.chat.ChatUtil;

public class BuildingMessages {

	
	// Commands:
	public static String invalidBuilding(String buildingName) {
		return Colour.negative + buildingName + " isn't a valid building.";
	}
	
	public static String buildingCommandRestrict(String correctBuildingName, String command){
		return Colour.negative + "Command " + command + " can only be used from a " + correctBuildingName + ".";
	}
	
	public static String noBuildingSet(){
		return Colour.negative + "Building not set on this chunk of land.";
	}

	
	public static String buildingsOnClaimed(Bundle bundle) {
		return Colour.negative + "Buildings can only be on set on claimed land.";
	}
	
	public static String oneBuilding(Bundle bundle) {
		return Colour.negative + "A chunk of land can only have one building.";
	}

	public static String unavailable(Building building){
		
		return Colour.negative + ChatUtil.capitalize(building.getDisplayName()) + " isn't available.";
		
	}

	

	// Movement:
	public static String entered(Building building) {
		return Colour.normal1 + "" + ChatColor.ITALIC + "Entered " + building.getDisplayName() + ".";
	}
	
	public static String left(Building building) {
		return Colour.normal1 + "" + ChatColor.ITALIC + "Left " + building.getDisplayName() + ".";
	}
	
	
	
	// Storage:
	public static String storeAreaOverlap(){
		return Colour.negative + "Storage areas can't overlap.";
	}
	
	public static String storeAreaSingleChunk(){
		return Colour.negative + "Storage area must be on the same chunk of land.";
	}
	
	public static String storeAreaAdded(Building building){
		return Colour.positive + "Storage area added to " + building.getName() + ".";
	}

	public static String storeAreaRemoved(Building building){
		return Colour.positive + "Storage area removed from " + building.getName() + ".";
	}
	
	public static String storeAreaNotFound(Building building){
		return Colour.negative + "Storage area not found.";
	}

	public static String storeAreasUnavailable(Building building){
		return Colour.negative + "No storage areas available for " + building.getName() + ".";
	}
	
	public static String stored(Material material, Building building) {
		
		if(material == Material.CHEST) return Colour.positive + "Added item storage.";
		
		return Colour.positive + "Stored " + GeneralMessages.material(material) + ".";

	}
	
	public static String withdrew(Material material, Building building) {

		if(material == Material.CHEST) return Colour.positive + "Removed item storage.";
		
		return Colour.positive + "Withdrew " + GeneralMessages.material(material) + ".";

	}
	
	public static String openedItemStore() {
		return Colour.positive + "Opened item storage.";
	}
	
	
	
	// Arena:
	public static String arenaTop(Arena arena, Integer count) {
		
		
		ArrayList<ArenaPlayer> topPlayers = arena.getTop(count);
		ColourLoop messageColor = new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2);
		
		ChatTable table = new ChatTable(messageColor);
		
		
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
				kdr = ChatUtil.displayDouble(arenaPlayer.getKills().doubleValue() / arenaPlayer.getDeaths().doubleValue());
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
		
		return ChatFramer.frame("top " + count, table.createTable(), messageColor.nextColour());
		
		
	}
	
	public static String countdown(int count) {
		
		if(count == 0){
			return Colour.positive + "FIGHT!";
		}else if((count%2)==0){
			return Colour.normal1 + "" + count;
		}else{
			return Colour.normal2 + "" + count;
		}
		
	}

	
	
	// Crumble arena:
	public static String crumbleGameRunning() {
		return Colour.negative + "Crumble game already in progress.";
	}
	
	public static String crumbleCantEnterGame() {
		return Colour.negative + "Can't enter while the game is in progress.";
	}

	public static String crumbleHeightSet(CrumbleArena arena) {
		return Colour.positive + "Arena height set.";
	}

	public static String crumbleHeightNotSet(CrumbleArena arena) {
		return Colour.negative + "Arena height not set.";
	}
	
	public static String CrumbleCantSetHeightDuringGame(CrumbleArena arena) {
		return Colour.negative + "Arena height can't be set during a game.";
	}
	
	public static String crumbleHeightNotSetInfo(CrumbleArena arena) {
		return Colour.negative + "Use /bsetheight to set arena height.";
	}
	
	public static String crumbleKickLocationSet(CrumbleArena arena) {
		return Colour.positive + "Arena kick location set.";
	}

	public static String crumbleKickMustBeOutside(CrumbleArena arena) {
		return Colour.negative + "Kick location can't be on the same chunk as the " + arena.getName() + ".";
	}
	
	public static String crumbleLost(CrumbleArena arena) {
		return Colour.veryNegative + "Lost " + arena.getName() + " game.";
	}
	
	public static String crumbleSurvived(CrumbleArena arena) {
		return Colour.veryPositive + "Survived " + arena.getName() + " game!";
	}
	
	public static String arenaTop(CrumbleArena arena, Integer count) {
		
		
		ArrayList<CrumblePlayer> topPlayers = arena.getTop(count);
		ColourLoop messageColor = new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2);
		
		ChatTable table = new ChatTable(messageColor);
		
		
		Integer listLen = count;
		
		// Fix count:
		if(listLen > topPlayers.size()) listLen = topPlayers.size();
		
		// Names:
		table.addLine(new String[]{GeneralMessages.tableTitle("name"),GeneralMessages.tableTitle("wins"),GeneralMessages.tableTitle("losses"),GeneralMessages.tableTitle("WLR")});
		
		// Nobody:
		if(topPlayers.size() == 0){
			
			table.addLine(new String[]{"-","-","-","-"});
			
		}
		
		// Crumble players:
		for (CrumblePlayer arenaPlayer : topPlayers) {

			listLen --;
			if(listLen < 0) break;
			
			String wlr = "";
			if(arenaPlayer.getLosses() == 0){
				wlr = "-";
			}else{
				wlr = ChatUtil.displayDouble(arenaPlayer.getWins().doubleValue() / arenaPlayer.getLosses().doubleValue());
			}
			
			table.addLine(
				new String[]{
					arenaPlayer.getName(),
					arenaPlayer.getWins().toString(),
					arenaPlayer.getLosses().toString(),
					wlr
				});
			
			
		}
		
		table.collapse();
		
		return ChatFramer.frame("top " + count, table.createTable(), messageColor.nextColour());
		
		
	}

	public static String countdown(CrumbleArena arena, int count) {
		
		if(count == 0){
			return Colour.positive + "WATCH YOUR STEP!";
		}else if((count%2)==0){
			return Colour.normal1 + "" + count;
		}else{
			return Colour.normal2 + "" + count;
		}
		
	}

	
	
	// Home:
	public static String alreadyResident(String name) {
		return Colour.negative + name + " is already a resident.";
	}
	
	public static String notResident(String name) {
		return Colour.negative + name + " is not a resident.";
	}

	public static String addedResident(String name) {
		return Colour.positive + "Added " + name + " to the resident list.";
	}
	
	public static String removedResident(String name) {
		return Colour.positive + "Removed " + name + " from the resident list.";
	}

	public static String chestLocked() {
		return Colour.negative + "The chest is locked.";
	}

	
	
	// Attribute sign:
	public static String attributeMaxReached(String attribute) {
		return Colour.negative + "Can't increase " + attribute + " above " + AttributeConfiguration.config().maxAttributeScore + ".";
	}

	public static String attributeIncreased(String attribute, Integer score) {
		return Colour.positive + ChatUtil.capitalize(attribute) + " increased to " + score + ".";
	}

	public static String attributePointsRequired(String attribute) {
		return Colour.negative + "Not enough attribute points to increase " + attribute + ".";
	}
	
	
	
	// Reset sign:
	public static String resetAttr(String attribute, Integer score) {
		return Colour.normal1 + ChatUtil.capitalize(attribute) + " score reset to " + score + ".";
	}
	
	public static String attrAlreadyReset(String attribute) {
		return Colour.negative + ChatUtil.capitalize(attribute) + " score is already 0.";
	}
	
	public static String trainLimitReached(String attribute) {
		return Colour.negative + ChatUtil.capitalize(attribute) + " can't be trained any further.";
	}
	
	
	// Upgrading:
	public static String upgraded(Building building) {
		return Colour.positive + ChatUtil.capitalize(building.getName()) + " upgraded.";
	}
	
	public static String upgradeLimit(Building building) {
		return Colour.negative + "Upgrade limit reached for " + building.getName() + ".";
	}
	


	// Town square:
	public static String noTownSquare(Bundle bundle){
		return Colour.negative + "" + bundle.getName() + " doesn't have a " + ChatUtil.className(TownSquare.class) + ".";
	}
	
	
	// Farm:
	public static String farmAnimalsDamageDeny() {
		return Colour.negative + "Can't harm animals on this farms.";
	}
	
	
}
