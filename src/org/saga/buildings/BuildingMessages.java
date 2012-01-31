package org.saga.buildings;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.saga.SagaMessages;
import org.saga.buildings.Arena.ArenaPlayer;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.config.ChunkGroupConfiguration;
import org.saga.player.PlayerMessages.ColorCircle;
import org.saga.player.SagaPlayer;
import org.saga.utility.StringBook;
import org.saga.utility.TextUtil;

public class BuildingMessages {


	// Colors:
	public static ChatColor veryPositive = ChatColor.DARK_GREEN; // DO NOT OVERUSE.
	
	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED; // DO NOT OVERUSE.
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor anouncment = ChatColor.AQUA;
	
	public static ChatColor normal1 = ChatColor.GOLD;
	
	public static ChatColor normal2 = ChatColor.YELLOW;
	
	public static ChatColor frame = ChatColor.DARK_GREEN;
	

	// Buildings:
	public static String newBuilding(ChunkGroup chunkGroup, Building building, SagaPlayer sagaPlayer) {
		return anouncment + sagaPlayer.getName() + " added a " + building.getDisplayName() + " to the settlement.";
	}
	
	public static String deletedBuilding(ChunkGroup chunkGroup, Building building, SagaPlayer sagaPlayer) {
		return anouncment + sagaPlayer.getName() + " removed a " + building.getDisplayName() + " from the settlement.";
	}
	
	public static String buildingsOnClaimedLand(ChunkGroup chunkGroup) {
		return negative + "Buildings can only be on located on claimed land.";
	}
	
	public static String oneBuildingPerChunk(ChunkGroup chunkGroup) {
		return negative + "A chunk of land can only have one building.";
	}
	
	public static String invalidName(String buildingName) {
		return negative + buildingName + " isn't a valid building.";
	}
	
	public static String invalidBuilding(String correctBuildingName, String command){
		
		return negative + command + " can only be used from a " + correctBuildingName + ".";
		
	}

	public static String unavailableBuilding(ChunkGroup chunkGroup, Building building){
		
		return negative + TextUtil.capitalize(building.getDisplayName()) + " isn't available.";
		
	}

	
	// General:
	public static String cooldown(String buildingName, Integer secondsLeft) {
		return negative + TextUtil.capitalize(buildingName) + " is on cooldown for " + TextUtil.fromSeconds(secondsLeft) + ".";
	}
	
	// Arena:
	public static String arenaTop(Arena arena, Integer count) {
		
		
		StringBuffer rString = new StringBuffer();
		ArrayList<ArenaPlayer> topPlayers = arena.getTop(count);
		ColorCircle messageColor = new ColorCircle().addColor(normal1).addColor(normal2);
		
		// Fix count:
		if(count > topPlayers.size()) count = topPlayers.size();
		
		// Nobody:
		if(topPlayers.size() == 0){
			
			rString.append(messageColor.nextColor());
			
			rString.append("none");
			
		}
		
		// Arena players:
		for (ArenaPlayer arenaPlayer : topPlayers) {
			
			if(rString.length() > 0) rString.append("\n");
			
			rString.append(messageColor.nextColor());
			
			String kdr = "";
			if(arenaPlayer.getDeaths() == 0){
				kdr = "-";
			}else{
				kdr = TextUtil.displayDouble(arenaPlayer.getKills().doubleValue() / arenaPlayer.getDeaths().doubleValue());
			}
			
			rString.append(arenaPlayer.getName() + " with " + arenaPlayer.getKills() + " kills and " + kdr + " KDR.");
			
		}
		
		rString.insert(0, messageColor.nextColor());
		
		return TextUtil.frame("Top " + count, rString.toString(), messageColor.nextColor());
		
		
	}
	

	// Help:
	public static String info() {

		
		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook("building info", color, 10);
		
		// Buildings:
		ArrayList<Building> buildings = ChunkGroupConfiguration.config().getBuildings();
		ArrayList<String> buildingNames = new ArrayList<String>();
		
		for (Building building : buildings) {
			
			buildingNames.add(building.getName().replaceAll(" ", SagaMessages.spaceSymbol));
			
		}
		
		book.addLine("All buildings: " + TextUtil.flatten(buildingNames));

		// Requirements:
		book.addLine("/binfo <building_name> for more details.");

		return book.framed(0, 76.0);
		
		
	}
	
	public static String info(String buildingName, BuildingDefinition definition) {
		
		
		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook(buildingName + " help", color, 10);
		
		// Description:
		book.addLine(definition.getDescription());
		
		// Skills:
		book.addLine("skills: " + TextUtil.flatten(definition.getSkills()));

		// Select:
		book.addLine("classes/profs: " + TextUtil.flatten(definition.getSelectable()));
		
		// Roles:
		HashSet<String> allRoles = definition.getRoles((short)0);
		HashSet<String> roles = new HashSet<String>();
		for (String role : allRoles) {

			roles.add(definition.getTotalRoles(role, (short)0) + " " + role);
			
		}
		book.addLine("roles: " + TextUtil.flatten(roles));
		
		// Buildings:
		HashSet<String> allBuilings = definition.getBuildings((short)0);
		HashSet<String> buildings = new HashSet<String>();
		for (String building : allBuilings) {

			buildings.add(definition.getTotalBuildings(building, (short)0) + " " + building);
			
		}
		book.addLine("enabled buildings: " + TextUtil.flatten(buildings));
		
		// Building points:
		book.addLine("building point cost: " + definition.getPointCost((short)0));
		
		return book.framed(0, 76.0);
		
		
	}
	
	
	
}
