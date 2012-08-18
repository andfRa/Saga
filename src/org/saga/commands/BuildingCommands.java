package org.saga.commands;

import java.util.ArrayList;

import org.bukkit.Location;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.buildings.Arena;
import org.saga.buildings.Building;
import org.saga.buildings.Home;
import org.saga.buildings.TownSquare;
import org.saga.buildings.storage.StorageArea;
import org.saga.chunks.Bundle;
import org.saga.chunks.BundleManager;
import org.saga.chunks.SagaChunk;
import org.saga.factions.Faction;
import org.saga.factions.FactionClaimManager;
import org.saga.messages.BuildingMessages;
import org.saga.messages.ClaimMessages;
import org.saga.messages.EconomyMessages;
import org.saga.messages.GeneralMessages;
import org.saga.messages.SettlementEffects;
import org.saga.messages.SettlementMessages;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Settlement.SettlementPermission;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;


public class BuildingCommands {

	
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
		SettlementEffects.playStoreAreaCreate(sagaPlayer, newStoreArea);
		
		
	}
	
	@Command(
			aliases = {"bremovestorage","bremovestore"},
			usage = "",
			flags = "",
			desc = "Remove a storage area to the building.",
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
		SettlementEffects.playStoreAreaRemove(sagaPlayer, storageArea);
		
		
	}
	
	
	
	// Arena:
	@Command(
			aliases = {"btop"},
			usage = "<amount to displaye>",
			flags = "",
			desc = "Show top players for the arena.",
			min = 0,
			max = 1
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.setsell"})
	public static void top(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
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
	
	
	
	// Trading post:
	@Command(
			aliases = {"bsetsell", "setsell"},
			usage = "[item] <price>",
			flags = "",
			desc = "Sets item sell price. Item in hand if no item is given.",
			min = 1,
			max = 2
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.setsell"})
	public static void setSell(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
//		// Retrieve building:
//		TradingPost selBuilding = null;
//		try {
//			selBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
//		} catch (Throwable e) {
//			sagaPlayer.message(e.getMessage());
//			return;
//		}
//		
//		Material material = null;
//		Double price = null;
//
//		// Permission:
//		ChunkBundle chunkBundle = selBuilding.getChunkBundle();
//		if(!chunkBundle.hasPermission(sagaPlayer, SettlementPermission.MANAGE_PRICES)){
//			sagaPlayer.message(GeneralMessages.noPermission(chunkBundle));
//			return;
//		}
//		
//		// Arguments:
//		if(args.argsLength() == 2){
//			
//			String sMaterial = args.getString(0);
//			material = Material.matchMaterial(sMaterial);
//			if(material == null){
//				try {
//					material = Material.getMaterial(Integer.parseInt(sMaterial));
//				} catch (NumberFormatException e) { }
//			}
//			if(material == null){
//				sagaPlayer.message(EconomyMessages.invalidMaterial(sMaterial));
//				return;
//			}
//			
//			// Price:
//			String sValue = args.getString(1);
//			try {
//				price = Double.parseDouble(sValue);
//			} catch (NumberFormatException e) {
//				sagaPlayer.message(EconomyMessages.invalidPrice(sValue));
//				return;
//			}
//			
//		}else{
//			
//			// Material:
//			ItemStack item = sagaPlayer.getItemInHand();
//			if(item != null && item.getType() != Material.AIR) material = item.getType();
//			if(material == null){
//				sagaPlayer.message(EconomyMessages.invalidItemHand());
//				return;
//			}
//			
//			// Price:
//			String sValue = args.getString(0);
//			try {
//				price = Double.parseDouble(sValue);
//			} catch (NumberFormatException e) {
//				sagaPlayer.message(EconomyMessages.invalidPrice(sValue));
//				return;
//			}
//			
//		}
//		
//		// Add price:
//		selBuilding.setSellPrice(material, price);
//		
//		// Inform:
//		sagaPlayer.message(EconomyMessages.setSell(material, price));
//		
//		// Notify transaction:
//		selBuilding.notifyTransaction();
		
		
	}

	@Command(
			aliases = {"bremovesell", "removesell"},
			usage = "[item]",
			flags = "",
			desc = "Removes item sell price. Item in hand if no item is given.",
			min = 1,
			max = 0
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.removesell"})
	public static void removeSell(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
//		// Retrieve building:
//		TradingPost selBuilding = null;
//		try {
//			selBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
//		} catch (Throwable e) {
//			sagaPlayer.message(e.getMessage());
//			return;
//		}
//		
//		Material material = null;
//
//		// Permission:
//		ChunkBundle chunkBundle = selBuilding.getChunkBundle();
//		if(!chunkBundle.hasPermission(sagaPlayer, SettlementPermission.MANAGE_PRICES)){
//			sagaPlayer.message(GeneralMessages.noPermission(chunkBundle));
//			return;
//		}
//		
//		// Arguments:
//		if(args.argsLength() == 1){
//			
//			String sMaterial = args.getString(0);
//			material = Material.matchMaterial(sMaterial);
//			if(material == null){
//				try {
//					material = Material.getMaterial(Integer.parseInt(sMaterial));
//				} catch (NumberFormatException e) { }
//			}
//			if(material == null){
//				sagaPlayer.message(EconomyMessages.invalidMaterial(sMaterial));
//				return;
//			}
//			
//		}else{
//			
//			// Material:
//			ItemStack item = sagaPlayer.getItemInHand();
//			if(item != null && item.getType() != Material.AIR) material = item.getType();
//			if(material == null){
//				sagaPlayer.message(EconomyMessages.invalidItemHand());
//				return;
//			}
//			
//		}
//		
//		// Remove price:
//		selBuilding.removeSellPrice(material);
//		
//		// Inform:
//		sagaPlayer.message(EconomyMessages.removeSell(material));
//		
//		// Notify transaction:
//		selBuilding.notifyTransaction();
		
		
	}
	
	@Command(
			aliases = {"bsetbuy", "setbuy"},
			usage = "[item] <price>",
			flags = "",
			desc = "Sets item buy price. Item in hand if no item is given.",
			min = 1,
			max = 2
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.setsell"})
	public static void setBuy(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
//		// Retrieve building:
//		TradingPost selBuilding = null;
//		try {
//			selBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
//		} catch (Throwable e) {
//			sagaPlayer.message(e.getMessage());
//			return;
//		}
//		
//		Material material = null;
//		Double price = null;
//
//		// Permission:
//		ChunkBundle chunkBundle = selBuilding.getChunkBundle();
//		if(!chunkBundle.hasPermission(sagaPlayer, SettlementPermission.MANAGE_PRICES)){
//			sagaPlayer.message(GeneralMessages.noPermission(chunkBundle));
//			return;
//		}
//		
//		// Arguments:
//		if(args.argsLength() == 2){
//			
//			String sMaterial = args.getString(0);
//			material = Material.matchMaterial(sMaterial);
//			if(material == null){
//				try {
//					material = Material.getMaterial(Integer.parseInt(sMaterial));
//				} catch (NumberFormatException e) { }
//			}
//			if(material == null){
//				sagaPlayer.message(EconomyMessages.invalidMaterial(sMaterial));
//				return;
//			}
//			
//			// Price:
//			String sValue = args.getString(1);
//			try {
//				price = Double.parseDouble(sValue);
//			} catch (NumberFormatException e) {
//				sagaPlayer.message(EconomyMessages.invalidPrice(sValue));
//				return;
//			}
//			
//		}else{
//			
//			// Material:
//			ItemStack item = sagaPlayer.getItemInHand();
//			if(item != null && item.getType() != Material.AIR) material = item.getType();
//			if(material == null){
//				sagaPlayer.message(EconomyMessages.invalidItemHand());
//				return;
//			}
//			
//			// Price:
//			String sValue = args.getString(0);
//			try {
//				price = Double.parseDouble(sValue);
//			} catch (NumberFormatException e) {
//				sagaPlayer.message(EconomyMessages.invalidPrice(sValue));
//				return;
//			}
//			
//		}
//		
//		// Add price:
//		selBuilding.setBuyPrice(material, price);
//		
//		// Inform:
//		sagaPlayer.message(EconomyMessages.setBuy(material, price));
//		
//		// Notify transaction:
//		selBuilding.notifyTransaction();
		
		
	}

	@Command(
			aliases = {"bremovebuy", "removebuy"},
			usage = "[item]",
			flags = "",
			desc = "Removes item buy price. Item in hand if no item is given.",
			min = 1,
			max = 0
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.removesell"})
	public static void removeBuy(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
//		// Retrieve building:
//		TradingPost selBuilding = null;
//		try {
//			selBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
//		} catch (Throwable e) {
//			sagaPlayer.message(e.getMessage());
//			return;
//		}
//		
//		Material material = null;
//
//		// Permission:
//		ChunkBundle chunkBundle = selBuilding.getChunkBundle();
//		if(!chunkBundle.hasPermission(sagaPlayer, SettlementPermission.MANAGE_PRICES)){
//			sagaPlayer.message(GeneralMessages.noPermission(chunkBundle));
//			return;
//		}
//		
//		// Arguments:
//		if(args.argsLength() == 1){
//			
//			String sMaterial = args.getString(0);
//			material = Material.matchMaterial(sMaterial);
//			if(material == null){
//				try {
//					material = Material.getMaterial(Integer.parseInt(sMaterial));
//				} catch (NumberFormatException e) { }
//			}
//			if(material == null){
//				sagaPlayer.message(EconomyMessages.invalidMaterial(sMaterial));
//				return;
//			}
//			
//		}else{
//			
//			// Material:
//			ItemStack item = sagaPlayer.getItemInHand();
//			if(item != null && item.getType() != Material.AIR) material = item.getType();
//			if(material == null){
//				sagaPlayer.message(EconomyMessages.invalidItemHand());
//				return;
//			}
//			
//		}
//		
//		// Remove price:
//		selBuilding.removeBuyPrice(material);
//		
//		// Inform:
//		sagaPlayer.message(EconomyMessages.removeSell(material));
//		
//		// Notify transaction:
//		selBuilding.notifyTransaction();
		
		
	}
	
	
	
	// Town square:
	@Command(
            aliases = {"sspawn"},
            usage = "",
            flags = "",
            desc = "Spawn in your settlement town square.",
            min = 0,
            max = 1)
	@CommandPermissions({"saga.user.settlement.building.townsquare.spawn"})
	public static void spawn(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		Bundle selChunkBundle = null;
		
		// Arguments:
		if(args.argsLength() == 1){
			
			// Chunk group:
			String groupName = GeneralMessages.nameFromArg(args.getString(0));
			selChunkBundle = BundleManager.manager().getChunkBundleWithName(groupName);
			if(selChunkBundle == null){
				sagaPlayer.message(SettlementMessages.noChunkBundle(groupName));
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
			if(FactionClaimManager.manager().isFactionClaiming(selChunkBundle.getId())){
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
	
	@Command(
            aliases = {"bupgrade"},
            usage = "",
            flags = "",
            desc = "Upgrade the building.",
            min = 0,
            max = 0
    )
	@CommandPermissions({"saga.user.settlement.building.upgrade"})
	public static void upgrade(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


		// Retrieve building:
		SagaChunk selChunk = sagaPlayer.getSagaChunk();
		if(selChunk == null){
			sagaPlayer.message(BuildingMessages.noBuilding());
			return;
		}
		
		Building selBuilding = selChunk.getBuilding();
		if(selBuilding == null){
			sagaPlayer.message(BuildingMessages.noBuilding());
			return;
		}
		Bundle selChunkBundle = selBuilding.getChunkBundle();

		Integer bldgScore = selBuilding.getScore();
		
		// Permission:
		if(!selChunkBundle.hasPermission(sagaPlayer, SettlementPermission.BUILDING_UPGRADE)){
			sagaPlayer.message(GeneralMessages.noPermission());
			return;
		}
		
		// Limit:
		if(bldgScore >= selBuilding.getDefinition().getMaxScore()){
			sagaPlayer.message(BuildingMessages.upgradeLimit(selBuilding));
			return;
		}

		// Enough coins:
		Double cost = selBuilding.getDefinition().getUpgradeCost(bldgScore);
		if(sagaPlayer.getCoins() < cost){
			sagaPlayer.message(EconomyMessages.notEnoughCoins());
			return;
		}

		// Take coins:
		sagaPlayer.removeCoins(cost);
		sagaPlayer.message(EconomyMessages.coinsSpent(cost));
		
		// Upgrade:
		selChunkBundle.setBuildingScore(selBuilding.getName(), bldgScore + 1);
		
		// Inform:
		sagaPlayer.message(BuildingMessages.upgraded(selBuilding));
	
		// Play effect:
		SettlementEffects.playBuildingUpgrade(sagaPlayer, selBuilding);
		
		
	}
	
	
}
