package org.saga.saveload;

import java.io.File;

public enum Directory {

	
	ABILITY_CONFIG("plugins/Saga/config/", "abilities.json"),
	ATTRIBUTE_CONFIG("plugins/Saga/config/", "attributes.json"),
	BALANCE_CONFIG("plugins/Saga/config/", "balance.json"),
	CHUNKGROUP_CONFIG("plugins/Saga/config/", "settlements.json"),
	ECONOMY_CONFIG("plugins/Saga/config/", "economy.json"),
	EXPERIENCE_CONFIG("plugins/Saga/config/", "experience.json"),
	PROFICIENCY_CONFIG("plugins/Saga/config/", "proficiencies.json"),
	FACTION_CONFIG("plugins/Saga/config/", "factions.json"),
	
	STATISTICS("plugins/Saga/statistics/", WriterReader.NAME_SUBS + ".json"),
	TRADE_DEALS("plugins/Saga/economy/", WriterReader.NAME_SUBS + " deals.json"),

	PLAYER_DATA("plugins/Saga/players/", WriterReader.NAME_SUBS + ".json"),
	SETTLEMENT_DATA("plugins/Saga/settlements/", WriterReader.NAME_SUBS + ".json"),
	FACTION_DATA("plugins/Saga/factions/", WriterReader.NAME_SUBS + ".json");
	
	/**
	 * File extension.
	 */
	public static final String FILE_EXTENTENSION = ".json";
	
	/**
	 * String added to deleted files directories.
	 */
	public static final String DELETED_DIRECTORY_ADD = "deleted/";
	
	
	private String dir;

	private String filename;

	private Directory(String dir, String filename) {
		this.dir = dir.replace("/", File.separator);
		this.filename = filename;
	}

	public String getDirectory() {
		return dir;
	}
	
	public String getDeletedDirectory() {
		return dir + DELETED_DIRECTORY_ADD.replace("/", File.separator);
	}
	
	public String getFilename() {
		return filename;
	}
	
}
