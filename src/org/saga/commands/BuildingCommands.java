package org.saga.commands;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.buildings.Arena;
import org.saga.buildings.Building;
import org.saga.buildings.CrumbleArena;
import org.saga.buildings.Home;
import org.saga.buildings.TownSquare;
import org.saga.buildings.storage.StorageArea;
import org.saga.config.BuildingConfiguration;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.factions.Faction;
import org.saga.factions.FactionClaimManager;
import org.saga.messages.BuildingMessages;
import org.saga.messages.ClaimMessages;
import org.saga.messages.GeneralMessages;
import org.saga.messages.SettlementMessages;
import org.saga.messages.effects.SettlementEffectHandler;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Bundle;
import org.saga.settlements.BundleManager;
import org.saga.settlements.SagaChunk;
import org.saga.settlements.Settlement.SettlementPermission;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.SagaLocation;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;


public class BuildingCommands {

	
	// Buildings general:
	@Command(
			aliases = {"bset"},
			usage = "<building_name>",
			flags = "",
			desc = "Set a building on the chunk of land.",
			min = 1,
			max = 1
			)
		@CommandPermissions({"saga.user.building.set"})
	public static void set(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		String buildingName = null;
		Bundle selBundle = null;

		// Arguments:
		buildingName = GeneralMessages.nameFromArg(args.getString(0));
			
		// Selected chunk:
		SagaChunk selChunk = sagaPlayer.getSagaChunk();
		if(selChunk == null){
			sagaPlayer.message(BuildingMessages.buildingsOnClaimed(selBundle));
			return;
		}
			
		// Selected chunk bundle:
		selBundle = selChunk.getChunkBundle();

		// Valid building:
		if(BuildingConfiguration.config().getBuildingDefinition(buildingName) == null){
			sagaPlayer.message(BuildingMessages.invalidBuilding(buildingName));
			return;
		}
		   	
		// Building:
		Building selBuilding;
		try {
			selBuilding = BuildingConfiguration.config().createBuilding(buildingName);
		} catch (InvalidBuildingException e) {
			SagaLogger.severe(SettlementCommands.class, sagaPlayer + " tried to set a building with missing definition");
			sagaPlayer.error("definition missing for " + buildingName + " building");
			return;
		}
		if(selBuilding == null){
			sagaPlayer.message(BuildingMessages.invalidBuilding(buildingName));
			return;
		}
		
		// Permission:
		if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.SET_BUILDING)){
			sagaPlayer.message(GeneralMessages.noPermission(selBundle));
			return;
		}
		
		// Building points:
		if(!selBundle.hasBuildPointsAvailable(selBuilding)){
			sagaPlayer.message(SettlementMessages.notEnoughBuildingPoints(selBuilding));
			return;
		}

		// Existing building:
		if(selChunk.getBuilding() != null){
			sagaPlayer.message(BuildingMessages.oneBuilding(selBundle));
			return;
		}

		// Building available:
		if(!selBundle.isBuildingAvailable(buildingName)){
			sagaPlayer.message(BuildingMessages.unavailable(selBuilding));
			return;
		}
			
		// Set building:
		selChunk.setBuilding(selBuilding);

		// Inform:
		if(sagaPlayer.getBundle() == selBundle){
			sagaPlayer.message(SettlementMessages.setBuilding(selBuilding));
		}else{
			sagaPlayer.message(SettlementMessages.setBuilding(selBuilding, selBundle));
		}
			
		// Play effect:
		SettlementEffectHandler.playBuildingSet(sagaPlayer, selBuilding);

		// Statistics:
		StatisticsManager.manager().setBuildings(selBundle);
			

	}
		
	@Command(
		aliases = {"bremove"},
		usage = "",
		flags = "",
		desc = "Remove a building from the chunk of land.",
		min = 0,
		max = 0
	)
	@CommandPermissions({"saga.user.building.remove"})
	public static void remove(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		SagaChunk sagaChunk = sagaPlayer.getSagaChunk();
		
		// Bundle:
		Bundle selBundle = null;
		if(sagaChunk != null) selBundle = sagaChunk.getChunkBundle();
		
		if(selBundle == null){
			sagaPlayer.message(SettlementMessages.notMember());
			return;
		}
		
		// Selected chunk:
		SagaChunk selChunk = sagaPlayer.getSagaChunk();
	   	if(selChunk == null){
			sagaPlayer.message(BuildingMessages.buildingsOnClaimed(selBundle));
			return;
		}
		
		// Existing building:
		Building selBuilding = selChunk.getBuilding();
		if(selBuilding == null){
			sagaPlayer.message(SettlementMessages.noBuilding());
			return;
		}
		
		// Permission:
		if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.REMOVE_BUILDING)){
			sagaPlayer.message(GeneralMessages.noPermission(selBundle));
			return;
		}

		// Inform:
		if(sagaPlayer.getBundle() == selBundle){
			sagaPlayer.message(SettlementMessages.removedBuilding(selBuilding));
		}else{
			sagaPlayer.message(SettlementMessages.removedBuilding(selBuilding, selBundle));
		}

		// Play effect:
		SettlementEffectHandler.playBuildingRemove(sagaPlayer, selBuilding);
		
		// Remove building:
		selBuilding.remove();
		selChunk.removeBuilding();

		// Statistics:
		StatisticsManager.manager().setBuildings(selBundle);
			
			
	}
	

	
	// General building storage:
	@Command(
			aliases = {"baddstorage","baddstore"},
			usage = "",
			flags = "",
			desc = "Add a storage area to the building.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.user.building.storage.add"})
	public static void addStorage(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	

		// Retrieve Saga chunk:
		SagaChunk selChunk = sagaPlayer.getSagaChunk();
		if(selChunk == null){
			sagaPlayer.message(SettlementMessages.chunkNotClaimed());
			return;
		}
		
		// Retrieve building:
		Building selBuilding = selChunk.getBuilding();
		if(selBuilding == null){
			sagaPlayer.message(BuildingMessages.noBuilding());
			return;
		}

		// Permission:
		Bundle selBundle = selBuilding.getChunkBundle();
		if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.STORAGE_AREA_ADD)){
			sagaPlayer.message(GeneralMessages.noPermission(selBundle));
			return;
		}
	
		// Remaining storage areas:
		if(selBuilding.getRemainingStorageAreas() < 1){
			sagaPlayer.message(BuildingMessages.storeAreasUnavailable(selBuilding));
			return;
		}
		
		// Create storage:
		StorageArea newStoreArea = new StorageArea(sagaPlayer);
	
		// Check overlap:
		if(selBuilding.checkOverlap(newStoreArea)){
			sagaPlayer.message(BuildingMessages.storeAreaOverlap());
			return;
		}
		
		// Multiple chunks:
		ArrayList<SagaChunk> sagaChunks = newStoreArea.getSagaChunks();
		if(sagaChunks.size() > 1 || !sagaChunks.get(0).equals(selChunk)){
			sagaPlayer.message(BuildingMessages.storeAreaSingleChunk());
			return;
		}
		
		// Add:
		selBuilding.addStorageArea(newStoreArea);
		
		// Inform:
		sagaPlayer.message(BuildingMessages.storeAreaAdded(selBuilding));
		
		// Effect:
		SettlementEffectHandler.playStoreAreaCreate(sagaPlayer, newStoreArea);
		
		
	}
	
	@Command(
			aliases = {"bremovestorage","bremovestore"},
			usage = "",
			flags = "",
			desc = "Remove a storage area from the building.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.user.building.storage.remove"})
	public static void removeStorage(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	

		// Retrieve Saga chunk:
		SagaChunk selChunk = sagaPlayer.getSagaChunk();
		if(selChunk == null){
			sagaPlayer.message(SettlementMessages.chunkNotClaimed());
			return;
		}
		
		// Retrieve building:
		Building selBuilding = selChunk.getBuilding();
		if(selBuilding == null){
			sagaPlayer.message(BuildingMessages.noBuilding());
			return;
		}

		// Permission:
		Bundle selBundle = selBuilding.getChunkBundle();
		if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.STORAGE_AREA_REMOVE)){
			sagaPlayer.message(GeneralMessages.noPermission(selBundle));
			return;
		}
	
		// Retrieve storage:
		StorageArea storageArea = selBuilding.getStorageArea(sagaPlayer.getLocation());
		
		// No storage area:
		if(storageArea == null){
			sagaPlayer.message(BuildingMessages.storeAreaNotFound(selBuilding));
			return;
		}
		
		// Remove:
		selBuilding.removeStorageArea(storageArea);
		
		// Inform:
		sagaPlayer.message(BuildingMessages.storeAreaRemoved(selBuilding));
		
		// Effect:
		SettlementEffectHandler.playStoreAreaRemove(sagaPlayer, storageArea);
		
		
	}
	
	@Command(
			aliases = {"bborder","bflash"},
			usage = "",
			flags = "",
			desc = "Show storage area border outline.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.user.building.storage.showborder"})
	public static void storageBorder(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	

		// Retrieve Saga chunk:
		SagaChunk selChunk = sagaPlayer.getSagaChunk();
		if(selChunk == null){
			sagaPlayer.message(SettlementMessages.chunkNotClaimed());
			return;
		}
		
		// Retrieve building:
		Building selBuilding = selChunk.getBuilding();
		if(selBuilding == null){
			sagaPlayer.message(BuildingMessages.noBuilding());
			return;
		}

		// Permission:
		Bundle selBundle = selBuilding.getChunkBundle();
		if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.STORAGE_AREA_FLASH)){
			sagaPlayer.message(GeneralMessages.noPermission(selBundle));
			return;
		}
	
		// Retrieve storages:
		ArrayList<StorageArea> storages = selBuilding.getStorageAreas();

		// Effect:
		for (StorageArea storageArea : storages) {
			SettlementEffectHandler.playStoreAreaFashBorder(sagaPlayer, storageArea);
		}
		
		
	}
	
	
	
	// Arena:
	@Command(
			aliases = {"bpvptop"},
			usage = "[to_display]",
			flags = "",
			desc = "Show pvp arena top players.",
			min = 0,
			max = 1
	)
	@CommandPermissions({"saga.user.building.arena.top"})
	public static void pvpTop(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		Integer count = null;
		
		// Retrieve building:
		Arena selBuilding = null;
		try {
			selBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, Arena.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
	
		// Arguments:
		if (args.argsLength() == 1) {
		
			try {
				count = Integer.parseInt(args.getString(0));
			} catch (NumberFormatException e) {
				sagaPlayer.message(GeneralMessages.mustBeNumber(args.getString(0)));
				return;
			}
			
		}else{
			
			count = 10;
			
		}
		
		// Inform:
		sagaPlayer.message(BuildingMessages.arenaTop(selBuilding, count));
		
	
	}

	

	// Crumble arena:
	@Command(
			aliases = {"bsetheight","bsety"},
			usage = "<display_amount>",
			flags = "",
			desc = "Set arena height.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.user.building.crumblearena.heighy"})
	public static void setHeight(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		// Retrieve building:
		CrumbleArena selBuilding = null;
		try {
			selBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, CrumbleArena.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
	
		// Permission:
		Bundle selBundle = selBuilding.getChunkBundle();
		if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.CRUMBLE_ARENA_SETUP)){
			sagaPlayer.message(GeneralMessages.noPermission(selBundle));
			return;
		}
		
		// Set y:
		Integer y = (int)sagaPlayer.getLocation().getY();
		selBuilding.setY(y);
		
		// Inform:
		sagaPlayer.message(BuildingMessages.crumbleHeightSet(selBuilding));
		
	
	}

	@Command(
			aliases = {"bsetkickloc","bsetkick"},
			usage = "<display_amount>",
			flags = "",
			desc = "Set crumble arena kick location.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.user.building.crumblearena.kick"})
	public static void setKickLocation(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		// Retrieve building:
		CrumbleArena selBuilding = null;
		try {
			selBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, CrumbleArena.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
	
		// Permission:
		Bundle selBundle = selBuilding.getChunkBundle();
		if(!selBundle.hasPermission(sagaPlayer, SettlementPermission.CRUMBLE_ARENA_SETUP)){
			sagaPlayer.message(GeneralMessages.noPermission(selBundle));
			return;
		}
		
		Block target = sagaPlayer.getPlayer().getTargetBlock(null, 16);
		
		// Move up if not air:
		if(target.getType() != Material.AIR && target.getRelative(BlockFace.UP).getType() == Material.AIR) target = target.getRelative(BlockFace.UP);
		
		// Location on chunk:
		if(BundleManager.manager().getSagaChunk(target.getChunk()) == selBuilding.getSagaChunk()){
			sagaPlayer.message(BuildingMessages.crumbleKickMustBeOutside(selBuilding));
			return;
		}
		
		// Set kick location:
		selBuilding.setKickLocation(new SagaLocation(target.getLocation().add(0.5, 0, 0.5)));
		
		// Inform:
		sagaPlayer.message(BuildingMessages.crumbleKickLocationSet(selBuilding));
		
	
	}


	// Arena:
	@Command(
			aliases = {"bcrumbletop"},
			usage = "[to_display]",
			flags = "",
			desc = "Show crumble arena top players.",
			min = 0,
			max = 1
	)
	@CommandPermissions({"saga.user.building.crumblearena.top"})
	public static void top(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		Integer count = null;
		
		// Retrieve building:
		CrumbleArena selBuilding = null;
		try {
			selBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, CrumbleArena.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
	
		// Arguments:
		if (args.argsLength() == 1) {
		
			try {
				count = Integer.parseInt(args.getString(0));
			} catch (NumberFormatException e) {
				sagaPlayer.message(GeneralMessages.mustBeNumber(args.getString(0)));
				return;
			}
			
		}else{
			
			count = 10;
			
		}
		
		// Inform:
		sagaPlayer.message(BuildingMessages.arenaTop(selBuilding, count));
		
	
	}

	

	// Home:
	@Command(
			aliases = {"baddresident"},
			usage = "<name>",
			flags = "",
			desc = "Add a resident to a home.",
			min = 1,
			max = 1
	)
	@CommandPermissions({"saga.user.building.home.addresident"})
	public static void addResident(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		String targetName = null;
		
		// Retrieve building:
		Home selBuilding = null;
		try {
			selBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, Home.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
	
		// Arguments:
		targetName = args.getString(0);

		// Permission:
		Bundle bundle = selBuilding.getChunkBundle();
		if(!bundle.hasPermission(sagaPlayer, SettlementPermission.ADD_RESIDENT)){
			sagaPlayer.message(GeneralMessages.noPermission(bundle));
			return;
		}
		
		// Member:
		
		if(!SagaPlayer.checkExists(targetName)){
			sagaPlayer.message(SettlementMessages.nonExistantPlayer(targetName));
			return;
		}
		
		// Already a resident:
		if(selBuilding.isResident(targetName)){
			sagaPlayer.message(BuildingMessages.alreadyResident(targetName));
			return;
		}
		
		// Add:
		selBuilding.addResident(targetName);
		
		// Inform:
		sagaPlayer.message(BuildingMessages.addedResident(targetName));
		
	
	}
	
	@Command(
			aliases = {"bremoveresident"},
			usage = "<name>",
			flags = "",
			desc = "Remove a resident from a home.",
			min = 1,
			max = 1
	)
	@CommandPermissions({"saga.user.building.home.removeresident"})
	public static void removeResident(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		String targetName = null;
		
		// Retrieve building:
		Home selBuilding = null;
		try {
			selBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, Home.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
	
		// Arguments:
		targetName = args.getString(0);
		
		// Permission:
		Bundle bundle = selBuilding.getChunkBundle();
		if(!bundle.hasPermission(sagaPlayer, SettlementPermission.REMOVE_RESIDENT)){
			sagaPlayer.message(GeneralMessages.noPermission(bundle));
			return;
		}
		
		// Already a resident:
		if(!selBuilding.isResident(targetName)){
			sagaPlayer.message(BuildingMessages.notResident(targetName));
			return;
		}
		
		// Remove:
		selBuilding.removeResident(targetName);
		
		// Inform:
		sagaPlayer.message(BuildingMessages.removedResident(targetName));
		
	
	}
	
	
	
	// Town square:
	@Command(
            aliases = {"sspawn"},
            usage = "",
            flags = "",
            desc = "Spawn in a town square.",
            min = 0,
            max = 1)
	@CommandPermissions({"saga.user.building.townsquare.spawn"})
	public static void spawn(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		Bundle selChunkBundle = null;
		
		// Arguments:
		if(args.argsLength() == 1){
			
			// Chunk group:
			String groupName = GeneralMessages.nameFromArg(args.getString(0));
			selChunkBundle = BundleManager.manager().getBundle(groupName);
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.invalidBundle(groupName));
				return;
			}
			
		}else{
			
			// Chunk group:
			selChunkBundle = sagaPlayer.getBundle();
			if(selChunkBundle == null){
				sagaPlayer.message( SettlementMessages.notMember() );
				return;
			}
			
		}
		
		// Claimed by a faction:
		Faction ownerFaction = FactionClaimManager.manager().getOwningFaction(selChunkBundle.getId());
		if(ownerFaction != null && ownerFaction == sagaPlayer.getFaction()){
			
			// Being claimed:
			if(FactionClaimManager.manager().isClaiming(selChunkBundle.getId())){
				sagaPlayer.message(ClaimMessages.spawnDeny(ownerFaction));
				return;
			}
			
		}

		// Permission:
		else if(!selChunkBundle.hasPermission(sagaPlayer, SettlementPermission.SPAWN)){
			sagaPlayer.message(GeneralMessages.noPermission());
			return;
		}
		
		ArrayList<TownSquare> selBuildings = selChunkBundle.getBuildings(TownSquare.class);
		
		if(selBuildings.size() == 0){
			sagaPlayer.message(BuildingMessages.noTownSquare(selChunkBundle));
			return;
		}
		
		TownSquare selBuilding = null;
		
		for (TownSquare townSquare : selBuildings) {
			
			selBuilding = townSquare;
			break;
			
		}
		
		// Prepare chunk:
		selBuilding.getSagaChunk().loadChunk();
		
		// Location:
		Location spawnLocation = selBuilding.getSpawnLocation();
		if(spawnLocation == null){
			SagaLogger.severe(selBuilding, sagaPlayer + " player failed to respawn at " + selBuilding.getDisplayName());
			sagaPlayer.error("failed to respawn");
			return;
		}
		
		// Teleport:
		sagaPlayer.teleport(spawnLocation);
		
	
	}
	
	
}
