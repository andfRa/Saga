/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga.commands;

import java.util.ArrayList;

import org.bukkit.Material;
import org.saga.Saga;
import org.saga.config.GeneralConfiguration;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.messages.EconomyMessages;
import org.saga.messages.GeneralMessages;
import org.saga.messages.PlayerMessages;
import org.saga.messages.SettlementMessages;
import org.saga.messages.StatisticsMessages;
import org.saga.player.SagaPlayer;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.MetricPrefix;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;


public class StatisticsCommands {

	@Command(
			aliases = {"stgeneral"},
			usage = "",
			flags = "",
			desc = "Show general statistics.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.admin.statistics.general"})
    public static void general(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		// Inform:
		sagaPlayer.message(StatisticsMessages.general());
		
		sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
		
	}
    	
	@Command(
			aliases = {"stabilities"},
			usage = "",
			flags = "",
			desc = "Show ability statistics.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.admin.statistics.abilities"})
	public static void abilities(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		// Inform:
		sagaPlayer.message(StatisticsMessages.values("abilities used", "abilities.used", "ability", "uses sum", true, 0, 0));
            	
		sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
            	
	}
	
	@Command(
			aliases = {"stxrayhistogram","stxraytable"},
			usage = "[material]",
			flags = "",
			desc = "Show x-ray distribution histogram.",
			min = 0,
			max = 1
	)
	@CommandPermissions({"saga.admin.statistics.xraydistributions"})
	public static void xrayHistogram(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		Material material = null;

		// Arguments:
		switch (args.argsLength()) {
			
			case 1:
				
				String strMaterial = args.getString(0);
				material = Material.matchMaterial(strMaterial);
				if(material == null){
					try {
						material = Material.getMaterial(Integer.parseInt(strMaterial));
					} catch (NumberFormatException e) { }
				}
				if(material == null){
					sagaPlayer.message(EconomyMessages.invalidMaterial(strMaterial));
					return;
				}
				
				break;

			default:
				
				material = Material.DIAMOND_ORE;
				
				break;
				
		}
		
    	// Data:
    	Double[] data = StatisticsManager.manager().getVeinRatios(material);
    	
	    // Inform:
    	sagaPlayer.message(StatisticsMessages.histogram("x-ray histogram: " + GeneralMessages.material(material), data, 10, 3));
	    
    	sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
    	
    	
	}
	
	@Command(
			aliases = {"stxrayindication","stxrayind"},
			usage = "",
			flags = "",
			desc = "Show x-ray suspects.",
			min = 0,
			max = 1
	)
	@CommandPermissions({"saga.admin.statistics.xrayindication"})
	public static void xrayIndication(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		Material material = Material.DIAMOND_ORE;
		ArrayList<String> suspects = new ArrayList<String>();
		ArrayList<Double> ratios = new ArrayList<Double>();
		ArrayList<Integer> veins = new ArrayList<Integer>();
		
		ArrayList<String> allNames = StatisticsManager.manager().getVeinFoundPlayers(material);
		
		for (String name : allNames) {
			
			// Ratio:
			Double ratio = StatisticsManager.manager().getVeinRatio(name, material);
			if(ratio < GeneralConfiguration.config().getXrayDiamondRatio()) continue;
			
			// Mined stone:
			if(StatisticsManager.manager().getFoundVeins(name, Material.STONE) < GeneralConfiguration.config().getXrayMinStone()) continue;
			
			// Suspect:
			suspects.add(name);
			ratios.add(ratio);
			veins.add(StatisticsManager.manager().getFoundVeins(name, material));
			
		}
		
		sagaPlayer.message(StatisticsMessages.xrayIndication(suspects.toArray(new String[suspects.size()]), ratios.toArray(new Double[ratios.size()]), veins.toArray(new Integer[veins.size()])));
		
		
	}

	@Command(
			aliases = {"stxrayconfirm","stxrayconf"},
			usage = "<player name>",
			flags = "",
			desc = "Confirms x-ray usage.",
			min = 1,
			max = 1
	)
	@CommandPermissions({"saga.admin.statistics.xrayconfirm"})
	public static void xrayConfirm(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		String playerName = args.getString(0);
		
		// Invalid player:
		if(!Saga.plugin().isSagaPlayerExistant(playerName)){
			sagaPlayer.message(PlayerMessages.invalidPlayer(playerName));
			return;
		}
		
		// Confirm:
		Double ratio = StatisticsManager.manager().getVeinRatio(playerName, Material.DIAMOND_ORE);
		StatisticsManager.manager().confirmXray(playerName, Material.DIAMOND_ORE, ratio);
		
		// Inform:
		sagaPlayer.message(StatisticsMessages.xrayConfirmed(playerName));
		
		
	}
	
	
	@Command(
			aliases = {"stexp"},
			usage = "",
			flags = "",
			desc = "Show experience statistics.",
			min = 0,
			max = 1
	)
	@CommandPermissions({"saga.admin.statistics.exp"})
	public static void exp(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
    	// Arguments:
		Integer page = null;
    	if(args.argsLength() == 1){
    		
        	try {
    			page = Integer.parseInt(args.getString(0));
    		} catch (NumberFormatException e) {
    			sagaPlayer.message(SettlementMessages.invalidPage(args.getString(0)));
    			return;
    		}
    		
    	}else{
    		
    		page = 1;
        	
    	}
		
	    	// Inform:
	    sagaPlayer.message(StatisticsMessages.exp(page - 1));
	        	
	    sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
	      
	    
	}
	
	@Command(
			aliases = {"stlevels"},
			usage = "",
			flags = "",
			desc = "Show level distribution histogram.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.admin.statistics.levels"})
	public static void levels(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
    	// Data:
    	Integer[] data = StatisticsManager.manager().getLevels();
    	
	    // Inform:
    	sagaPlayer.message(StatisticsMessages.histogram("level histogram", data, 10, 0));
	    
    	sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
	    
	    
	}

	@Command(
		aliases = {"stattributes","stattr"},
		usage = "",
		flags = "",
		desc = "Show attribute statistics.",
		min = 0,
		max = 0
	)
	@CommandPermissions({"saga.admin.statistics.attributes"})
	public static void attributes(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		// Inform:
		sagaPlayer.message(StatisticsMessages.values("attributes trained", "attributes", "attribute", "score sum", true, 0, 0));
		
		sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
		
	}

	@Command(
			aliases = {"stbalance","stbal"},
			usage = "",
			flags = "",
			desc = "Show economy balance statistics.",
			min = 0,
			max = 1
	)
	@CommandPermissions({"saga.admin.statistics.economy"})
	public static void balance(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
    	// Arguments:
		Integer page = null;
    	if(args.argsLength() == 1){
    		
        	try {
    			page = Integer.parseInt(args.getString(0));
    		} catch (NumberFormatException e) {
    			sagaPlayer.message(SettlementMessages.invalidPage(args.getString(0)));
    			return;
    		}
    		
    	}else{
    		
    		page = 1;
        	
    	}
		
	    	// Inform:
	    sagaPlayer.message(StatisticsMessages.balance(page - 1));
	        	
	    sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
	      
	    
	}
	
	
	@Command(
			aliases = {"streset"},
			usage = "",
			flags = "",
			desc = "Resets and archives statistics.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.admin.statistics.reset"})
	public static void reset(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

	    // Archive:
		StatisticsManager.manager().archive();
		
		// Reset:
		StatisticsManager.manager().reset();
		
		// Inform:
	    sagaPlayer.message(StatisticsMessages.reset());
	        	
	}
	
	@Command(
		aliases = {"stbuildings"},
		usage = "",
		flags = "",
		desc = "Show building statistics.",
		min = 0,
		max = 0
	)
	@CommandPermissions({"saga.admin.statistics.buildings"})
	public static void buildings(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		// Inform:
		sagaPlayer.message(StatisticsMessages.values("buildings set", "buildings.set", "building", "set sum", true, 0, 0));
	           	
		sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
		
	}
		
	@Command(
		aliases = {"stwages"},
		usage = "",
		flags = "a",
		desc = "Show wages statistics. Flag 'a' to show factions.",
		min = 0,
		max = 0
		)
	@CommandPermissions({"saga.admin.statistics.wages"})
	public static void wages(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		// Inform:
		sagaPlayer.message(StatisticsMessages.values("wages", "wages.factions", "rank", "wages sum", !args.hasFlag('a'), 0, 0));
		
		sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
	            	
	}

	
	@Command(
		aliases = {"stclaimed"},
		usage = "",
		flags = "a",
		desc = "Show faction claim statistics. Flag 'a' to show factions.",
		min = 0,
		max = 0
		)
	@CommandPermissions({"saga.admin.statistics.claimed"})
	public static void claimed(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	
		// Inform:
		sagaPlayer.message(StatisticsMessages.values("faction claiming", "faction_claiming", "action", "sum", !args.hasFlag('a'), 0, 0));
		
		sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
	            	
	}

	@Command(
			aliases = {"stslevels"},
			usage = "",
			flags = "",
			desc = "Show settlement level distribution histogram.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.admin.statistics.settlementlevels"})
	public static void slevels(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
    	// Data:
    	Double[] data = StatisticsManager.manager().createHistogramData("settlements.levels");
    	
	    // Inform:
    	sagaPlayer.message(StatisticsMessages.histogram("settlement level histogram", data, 10, 0));
	    
    	sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
	    
	    
	}
	
	@Command(
			aliases = {"stflevels"},
			usage = "",
			flags = "",
			desc = "Show settlement level distribution histogram.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.admin.statistics.factionlevels"})
	public static void flevels(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
    	// Data:
    	Double[] data = StatisticsManager.manager().createHistogramData("factions.levels");
    	
	    // Inform:
    	sagaPlayer.message(StatisticsMessages.histogram("faction level histogram", data, 10, 0));
	    
    	sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
	    
	    
	}

	@Command(
			aliases = {"stwallet"},
			usage = "",
			flags = "",
			desc = "Show wallet distribution histogram.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.admin.statistics.wallet"})
	public static void wallet(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
    	// Data:
    	Double[] data = StatisticsManager.manager().createHistogramData("wallet");
    	
	    // Inform:
    	sagaPlayer.message(StatisticsMessages.histogram("wallet histogram", data, 10, 1, MetricPrefix.k));
	    
    	sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
	    
	    
	}

	
	@Command(
			aliases = {"stupdateplayers"},
			usage = "",
			flags = "",
			desc = "Update all player statistics.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.admin.statistics.playerall"})
	public static void updatePlayers(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


	    // Inform:
    	sagaPlayer.message(StatisticsMessages.updating());
	    
    	// Force and release:
		String[] players = WriterReader.getFileNames(Directory.PLAYER_DATA);
		
		for (int i = 0; i < players.length; i++) {
			
			try {
				SagaPlayer selPlayer = Saga.plugin().forceSagaPlayer(players[i].replace(Directory.FILE_EXTENTENSION, ""));
				selPlayer.updateStatistics();
				selPlayer.indicateRelease();
			}
			catch (NonExistantSagaPlayerException e) { }
			
		}
		
	    // Inform:
    	sagaPlayer.message(StatisticsMessages.updated());
	    
	    
	}

	
}
