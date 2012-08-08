/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga.commands;

import org.bukkit.Material;
import org.saga.Saga;
import org.saga.messages.EconomyMessages;
import org.saga.messages.GeneralMessages;
import org.saga.messages.SettlementMessages;
import org.saga.messages.StatisticsMessages;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;
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
			aliases = {"stlevelhistogram", "stlevels"},
			usage = "",
			flags = "",
			desc = "Show level distribution histogram.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.admin.statistics.level"})
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
		desc = "Show wages statistics. Add 'a' tag to show all factions.",
		min = 0,
		max = 0
		)
	@CommandPermissions({"saga.admin.statistics.wages"})
	public static void wages(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		// Inform:
		sagaPlayer.message(StatisticsMessages.values("wages", "wages.factions", "rank", "wages sum", !args.hasFlag('a'), 0, 0));
		
		sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
	            	
	}
		
	
}
