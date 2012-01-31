package org.saga.guilds;

import java.util.Collection;
import java.util.Hashtable;

import org.saga.Saga;
import org.saga.config.GuildConfiguration;
import org.saga.player.Proficiency;
import org.saga.player.SagaPlayer;
import org.saga.utility.WriterReader;


public class GuildsManager {

	
	/**
	 * Instance
	 */
	private static GuildsManager instance;
	
	/**
	 * Gets manager.
	 * 
	 * @return manager
	 */
	public static GuildsManager manager() {
		return instance;
	}
	
	
	/**
	 * Guilds.
	 */
	private Hashtable<String, SagaGuild> guilds = new Hashtable<String, SagaGuild>();
	

	// Initialization:
	/**
	 * Initializes.
	 * 
	 */
	private GuildsManager() {
		
	}

	/**
	 * Adds a guild.
	 * 
	 * @param name name
	 * @param guild guild
	 */
	private void addGuild(String name, SagaGuild guild) {

		guilds.put(guild.getName(), guild);
		
	}

	
	// Events:
	/**
	 * Called when a player is killed.
	 * 
	 * @param attacker attacker saga player
	 * @param defender defender saga player
	 */
	public void onKilledPlayer(SagaPlayer attacker, SagaPlayer defender) {

		
		// Send to class attacker guild:
		Proficiency attackerClasss = attacker.getClazz();
		Proficiency defenderClasss = defender.getClazz();
		if(attackerClasss != null && defenderClasss != null){
			
			String attackerGuildName = GuildConfiguration.config().getGuild(attackerClasss.getName());
			String defenderGuildName = GuildConfiguration.config().getGuild(defenderClasss.getName());
			
			SagaGuild attackerGuild = null;
			SagaGuild defenderGuild = null;
			
			if(attackerGuildName != null) attackerGuild = guilds.get(attackerGuildName);
			
			if(defenderGuildName != null) defenderGuild = guilds.get(defenderGuildName);
			
			if(attackerGuild != null && defenderGuild != null && attackerGuild != defenderGuild){
				attackerGuild.onKilledOposingGuildMember(attacker, defender);
			}
			
		}
		
		
	}
	
	
	// Loading unloading:
	/**
	 * Loads faction manager and loads factions.
	 * 
	 */
	public static void load() {


		// Inform:
		Saga.info("Loading guilds.");
		
		GuildsManager manager = new GuildsManager();
		
		// Load factions:
		String[] names = WriterReader.getAllGuildNames();
		
		for (int i = 0; i < names.length; i++) {
			
			SagaGuild element = SagaGuild.load(names[i]);
			
			// Add to manager:
			manager.addGuild(names[i], element);
			
		}
		
		// Set instance:
		instance = manager;
		
		
	}
	
	/**
	 * Saves guild manager.
	 * 
	 */
	public static void save() {
		

		// Inform:
		Saga.info("Saving guilds.");

		// Save guilds:
		Collection<SagaGuild> guilds = manager().guilds.values();
		for (SagaGuild sagaGuild : guilds) {
			
			sagaGuild.save();
			
		}
		
		
	}
	
	/**
	 * Unloads guild manager.
	 * 
	 */
	public static void unload() {


		// Inform:
		Saga.info("Unloading guilds.");

		save();
		instance = null;
		
		
	}
	
	
}
