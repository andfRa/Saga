/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga.statistics;

import org.saga.Saga;
import org.saga.messages.ChunkGroupMessages;
import org.saga.messages.StatisticsMessages;
import org.saga.player.SagaPlayer;
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
		sagaPlayer.message(StatisticsMessages.abilities());
            	
		sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
            	
	}
	
	@Command(
			aliases = {"stxraydistr","stxraytable"},
			usage = "",
			flags = "",
			desc = "Show x-ray distribution statistics.",
			min = 0,
			max = 1
	)
	@CommandPermissions({"saga.admin.statistics.xraydistributions"})
	public static void xrayDistr(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
    	// Arguments:
		Integer page = null;
    	if(args.argsLength() == 1){
    		
        	try {
    			page = Integer.parseInt(args.getString(0));
    		} catch (NumberFormatException e) {
    			sagaPlayer.message(ChunkGroupMessages.invalidPage(args.getString(0)));
    			return;
    		}
    		
    	}else{
    		
    		page = 1;
        	
    	}
		
	    	// Inform:
	    sagaPlayer.message(StatisticsMessages.xrayDistrib(page - 1));
	        	
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
    			sagaPlayer.message(ChunkGroupMessages.invalidPage(args.getString(0)));
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
			desc = "Show level statistics.",
			min = 0,
			max = 1
	)
	@CommandPermissions({"saga.admin.statistics.level"})
	public static void levels(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
    	// Arguments:
		Integer page = null;
    	if(args.argsLength() == 1){
    		
        	try {
    			page = Integer.parseInt(args.getString(0));
    		} catch (NumberFormatException e) {
    			sagaPlayer.message(ChunkGroupMessages.invalidPage(args.getString(0)));
    			return;
    		}
    		
    	}else{
    		
    		page = 1;
        	
    	}
		
	    	// Inform:
	    sagaPlayer.message(StatisticsMessages.levels(page - 1));
	        	
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
	public static void skills(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

	    	// Inform:
	    	sagaPlayer.message(StatisticsMessages.attributes());
	        	
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
    			sagaPlayer.message(ChunkGroupMessages.invalidPage(args.getString(0)));
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
	
	
}
