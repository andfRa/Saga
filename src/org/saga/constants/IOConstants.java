/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga.constants;

import java.io.File;


/**
 *
 * @author Cory
 */
public class IOConstants {

    

    
	public static final String FILE_EXTENTENSION = ".json";
	
	public static final String FILE_SPACE = "-";

    
	public enum WriteReadType{
		
		
		PLUGIN_DIRECTORY("plugins" + File.separator + "Saga" + File.separator),
		BACKUP(PLUGIN_DIRECTORY.getDirectory() + "backup" + File.separator),
		DEFAULTS(PLUGIN_DIRECTORY.getDirectory() + "defaults"+ File.separator),
		OTHER(PLUGIN_DIRECTORY.getDirectory() + "other" + File.separator),
		
		CONFIG_NORMAL(PLUGIN_DIRECTORY.getDirectory() + "config" + File.separator),
		CONFIG_DEFAULTS(DEFAULTS.getDirectory() + File.separator + "config" + File.separator),
		CONFIG_BACKUP(BACKUP.getDirectory() + "config" + File.separator),
		
		ABILITY_NORMAL(CONFIG_NORMAL.getDirectory() + "abilities" + File.separator),
		ABILITY_DEFAULTS(CONFIG_DEFAULTS.getDirectory() + "abilities" + File.separator),

		PLAYER_NORMAL(PLUGIN_DIRECTORY.getDirectory() + "players" + File.separator),
		PLAYER_BACKUP(CONFIG_BACKUP.getDirectory() + "players" + File.separator),
		
		TRADE_AGREEMENTS_NORMAL(PLUGIN_DIRECTORY.getDirectory() + "economy" + File.separator),
		TRADE_AGREEMENTS_BACKUP(CONFIG_BACKUP.getDirectory() + "economy" + File.separator),
		
		FACTION_NORMAL(PLUGIN_DIRECTORY.getDirectory() + "factions" + File.separator),
		FACTION_BACKUP(BACKUP.getDirectory() + "factions" + File.separator),
		FACTION_DELETED(BACKUP.getDirectory() + "deleted factions" + File.separator),
		
		SETTLEMENT_NORMAL(PLUGIN_DIRECTORY.getDirectory() + "settlements" + File.separator),
		SETTLEMENT_BACKUP(BACKUP.getDirectory() + "settlements" + File.separator),
		SETTLEMENT_DELETED(BACKUP.getDirectory() + "deleted settlements" + File.separator),
		
		GUILD_NORMAL(PLUGIN_DIRECTORY.getDirectory() + "guilds" + File.separator),
		
		STATISTICS_NORMAL(PLUGIN_DIRECTORY.getDirectory() + "statistics" + File.separator);
		
		
		
		private String directory;

		
		private WriteReadType(String fileName) {
			this.directory = fileName;
		}
		
		
		public String getDirectory() {
			return directory;
		}
		
		
	}
	
	public enum ConfigType{
		
		
		EXPERIENCE("experience" + FILE_EXTENTENSION),
		SKILL("skills" + FILE_EXTENTENSION),
		PROFICIENCIES("proficiencies"+ FILE_EXTENTENSION),
		BALANCE("balance" + FILE_EXTENTENSION),
		FACTIONS("factions" + FILE_EXTENTENSION),
		CHUNK_GROUP("chunkgroup" + FILE_EXTENTENSION),
		ABILITY("abilities" + FILE_EXTENTENSION),
		ECONOMY("economy" + FILE_EXTENTENSION),
		GUILDS("guilds" + FILE_EXTENTENSION),
		STATISTICS("statistics" + FILE_EXTENTENSION),
		
		TRADE_AGREEMENTS("tradeagreements" + FILE_EXTENTENSION);
		
		
		private String fileName;
		
		
		private ConfigType(String fileName) {
			this.fileName = fileName;
		}
		
		
		public String getFileName() {
			return fileName;
		}
		
		
	}
	
    
    
    
    
}
