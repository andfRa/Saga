/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga.statistics;

import java.util.ArrayList;

import org.bukkit.Material;
import org.saga.Saga;
import org.saga.SagaMessages;
import org.saga.chunkGroups.ChunkGroupMessages;
import org.saga.config.BalanceConfiguration;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.player.SagaPlayer;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;


/**
 *
 * @author Cory
 */
public class StatisticsCommands {


    @Command(
        aliases = {"stclasskills","killstats"},
        usage = "",
        flags = "",
        desc = "Shows class kill statistics.",
        min = 0,
        max = 0
    )
    @CommandPermissions({"saga.admin.statistics.classkills"})
    public static void classkills(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

    	// Inform:
    	sagaPlayer.message(StatisticsMessages.classKills());
    	
    	sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
    	
    }
    
	@Command(
		aliases = {"stskills"},
		usage = "",
		flags = "",
		desc = "Shows skill statistics.",
		min = 0,
		max = 0
	)
	@CommandPermissions({"saga.admin.statistics.skills"})
	public static void skills(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

    	// Inform:
    	sagaPlayer.message(StatisticsMessages.skills());
        	
    	sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
        	
	}

	@Command(
			aliases = {"stgeneral"},
			usage = "",
			flags = "",
			desc = "Shows general statistics.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.admin.statistics.skills"})
    public static void general(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		// Inform:
		sagaPlayer.message(StatisticsMessages.general());
		
		sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
		
	}
    	
	@Command(
			aliases = {"stabilities"},
			usage = "",
			flags = "",
			desc = "Shows ability statistics.",
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
			aliases = {"stxrayindication","stxrayind"},
			usage = "",
			flags = "",
			desc = "Shows all xray mod indications.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.admin.statistics.xrayindication"})
	public static void xrayIndication(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		ArrayList<String> allPlayers = StatisticsManager.manager().getXrayPlayers();
		ArrayList<String> xrayIndications = new ArrayList<String>();
		
		for (String player : allPlayers) {
			
			for (Material xrMaterial : XrayIndicator.getXrayMaterials()) {
				
				if(xrMaterial == Material.STONE) continue;
				
				// Check ratio:
				Double ratio = StatisticsManager.manager().getOreRatio(player, xrMaterial);
				if(!BalanceConfiguration.config().checkXrayIndication(xrMaterial, ratio)) continue;
				
				xrayIndications.add(player);
				
			}
			
		}
		
		// Inform:
		sagaPlayer.message(StatisticsMessages.xrayIndication(allPlayers.size(), xrayIndications));
    	
		sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
          	
		
	}
    
	@Command(
            aliases = {"stxrayindicationplayer","stxrayindp","stxray"},
            usage = "<name> [page]",
            flags = "",
            desc = "Shows x-ray indication details of a player.",
            min = 1,
            max = 2
        )
    @CommandPermissions({"saga.admin.statistics.xrayindication"})
    public static void xrayIndicationPlayer(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
    	
    	SagaPlayer targetPlayer = null;
    	Integer page = null;
    	
    	// Arguments:
    	String name = args.getString(0);
    	if(args.argsLength() == 2){
    		
    		try {
    			
        		// Force:
        		targetPlayer = Saga.plugin().forceSagaPlayer(name);

        	} catch (NonExistantSagaPlayerException e) {

        		sagaPlayer.message(SagaMessages.invalidPlayer(name));
        		return;
    		
    		}
        	
        	try {
    			page = Integer.parseInt(args.getString(1));
    		} catch (NumberFormatException e) {
    			sagaPlayer.message(ChunkGroupMessages.invalidPage(args.getString(0)));
    			return;
    		}
    		
    	}else{
    		
    		try {
    			
        		// Force:
        		targetPlayer = Saga.plugin().forceSagaPlayer(name);

        	} catch (NonExistantSagaPlayerException e) {

        		sagaPlayer.message(SagaMessages.invalidPlayer(name));
        		return;
    		
    		}
    		
    		page = 1;
        	
    	}
    	
    	// Inform:
    	sagaPlayer.message(StatisticsMessages.xray(targetPlayer, page - 1));

		sagaPlayer.message(StatisticsMessages.statisticsAge(StatisticsManager.manager().calcStatisticsAge()));
        
    	// Unforce:
    	Saga.plugin().unforceSagaPlayer(name);
    	
    	
    }
	
	@Command(
			aliases = {"stexp"},
			usage = "",
			flags = "",
			desc = "Shows experience statistics.",
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
