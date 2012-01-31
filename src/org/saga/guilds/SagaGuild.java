package org.saga.guilds;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

import org.saga.Clock;
import org.saga.Clock.TimeOfDayTicker;
import org.saga.Saga;
import org.saga.config.GuildConfiguration;
import org.saga.constants.IOConstants.WriteReadType;
import org.saga.player.SagaPlayer;
import org.saga.utility.SagaCustomSerialization;
import org.saga.utility.WriterReader;

import com.google.gson.JsonParseException;

public class SagaGuild extends SagaCustomSerialization implements TimeOfDayTicker{


	/**
	 * Guild name.
	 */
	private transient String name;
	
	/**
	 * Levels the player killed.
	 */
	private Hashtable<String, Integer> killedLevels;

	/**
	 * Pay for members.
	 */
	private Hashtable<String, Double> pays;
	
	/**
	 * Days passed.
	 */
	private Integer daysPassed;
	
	
	// Control:
	/**
	 * Saving is enabled if true.
	 */
	transient private Boolean savinEnabled;
	
	
	// Initialization:
	/**
	 * Creates a guild.
	 * 
	 * @param name name
	 */
	public SagaGuild(String name) {
		
		this.name = name;
		this.killedLevels = new Hashtable<String, Integer>();
		this.pays = new Hashtable<String, Double>();
		this.savinEnabled = true;
		
	}
	
	/**
	 * Completes the initialization.
	 * 
	 * @return integrity
	 */
	public boolean complete() {

		
		boolean integrity=true;
		
		if(name == null){
			Saga.severe(this, "name failed to initialize", "setting default");
			name = "unnamed";
			integrity = false;
		}
		
		if(killedLevels == null){
			Saga.severe(this, "killedLevels failed to initialize", "setting default");
			killedLevels = new Hashtable<String, Integer>();
			integrity = false;
		}
		
		if(pays == null){
			Saga.severe(this, "pay failed to initialize", "setting default");
			pays = new Hashtable<String, Double>();
			integrity = false;
		}
		
		if(daysPassed == null){
			Saga.severe(this, "daysPassed failed to initialize", "setting default");
			daysPassed = 0;
			integrity = false;
		}
		
		// Transient:
		savinEnabled = true;
		
		// Enable clock:
		Clock.clock().registerTimeOfDayTick(this);
		
		return integrity;
		
	
	}
	
	
	// Awards:
	/**
	 * Adds a kill.
	 * 
	 * @param name name
	 * @param amount amount
	 */
	private void addKill(String name, Integer amount) {

		
		Integer newAmount = killedLevels.get(name);
		if(newAmount == null) newAmount = 0;
		
		killedLevels.put(name, newAmount + amount);
		
		
	}
	
	/**
	 * Adds payed amounts.
	 * 
	 * @param name name
	 */
	protected Double putPay(String name) {

		
		// Kills:
		Integer kills = killedLevels.remove(name);
		if(kills == null) kills = 0;
		
		Double killPay = GuildConfiguration.config().getPay(kills);
		
		// Limit:
		if(killPay > GuildConfiguration.config().payLimit){
			killPay = GuildConfiguration.config().payLimit;
		}
		
		System.out.println("awarding " + name + " with " + killPay);
		
		
		// Add to pays:
		Double oldPay = pays.put(name, killPay);
		if(oldPay == null) oldPay = 0.0;
		
		return killPay - oldPay;
		
		
	}
	
	/**
	 * Gets the pay for the player.
	 * 
	 * @param name player name
	 * @return gets the pay
	 */
	public Double getPay(String name) {
		
		
		Double pay = pays.get(name);
		
		if(pay == null) return 0.0;
		
		return pay;
		
		
	}
	
	
	// Getters:
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets name
	 * 
	 * @param name name
	 */
	private void setName(String name) {
		this.name = name;
	}
	
	
	// Events:
	/**
	 * Called when a player is killed.
	 * 
	 * @param attacker attacker saga player
	 * @param defender defender saga player
	 */
	public void onKilledOposingGuildMember(SagaPlayer attacker, SagaPlayer defender) {

		
		// Add kill:
		addKill(attacker.getName(), defender.getLevel());
		
		
	}
	
	
	// Control:
	/**
	 * Disables saving.
	 * 
	 */
	private void disableSaving() {
		savinEnabled = false;
	}
	
	
	// Bonus:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.TimeOfDayTicker#timeOfDayTick(org.saga.Clock.TimeOfDayTicker.TimeOfDay)
	 */
	@Override
	public void timeOfDayTick(TimeOfDay timeOfDay) {
		
//		
//		// Proceed if enough days have passed:
//		daysPassed ++;
//		
//		System.out.println("time of day for guild. days=" + daysPassed);
//		
//		
//		if(daysPassed >= GuildConfiguration.config().payDays){
//			daysPassed = 0;
//		}else{
////			return;
//		}
//		
//		// Pay:
//		Double payed = 0.0;
//		Integer playerCount = 0;
//		
//		// Award all players:
//		Enumeration<String> names = killedLevels.keys();
//		while (names.hasMoreElements()) {
//			
//			String name = names.nextElement();
//			payed += putPay(name);
//			playerCount++;
//			
//		}
//		
//		// Inform:
//		if(payed > 0 && playerCount > 0){
//			Saga.broadcast(GuildMessages.guildPayed(this, payed, playerCount));
//		}
//		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.TimeOfDayTicker#checkWorld(java.lang.String)
	 */
	@Override
	public boolean checkWorld(String worldName) {
		return GuildConfiguration.config().baseWorld.equals(worldName);
	}
	
	
	// Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}

	
	// Save load:
	/**
	 * Loads and a guild from disc.
	 * 
	 * @param guildName guild name
	 * @return saga faction
	 */
	public static SagaGuild load(String guildName) {

		
		// Load:
		SagaGuild instance = null;
		try {
			
			instance = WriterReader.readGuild(guildName.toString());
			
		} catch (FileNotFoundException e) {
			
			Saga.severe(SagaGuild.class, "missing " + guildName + "guild", "creating new instance");
			instance = new SagaGuild(guildName);
			
		} catch (IOException e) {
			
			Saga.severe(SagaGuild.class, "loading failed for " + guildName + ": " + e.getClass().getSimpleName() +":" + e.getMessage(), "creating new instance");
			instance = new SagaGuild(guildName);
			instance.disableSaving();
			
		} catch (JsonParseException e) {
			
			Saga.severe(SagaGuild.class, "parse failed for " + guildName + ": " + e.getClass().getSimpleName() +":" + e.getMessage(), "creating new instance");
			Saga.info("Parse message :" + e.getMessage());
			instance = new SagaGuild(guildName);
			instance.disableSaving();
			
		}
		
		// Set name:
		instance.setName(guildName);
		
		// Complete:
		instance.complete();
		
		return instance;
		
		
	}
	
	/**
	 * Saves guild to disc.
	 * 
	 */
	public void save() {

		
		if(!savinEnabled){
			Saga.warning(this, "saving disabled", "ignoring request" );
			return;
		}
		
		try {
			
			WriterReader.writeGuild(name, this, WriteReadType.GUILD_NORMAL);
			
		} catch (IOException e) {
			
			Saga.severe(this, "write failed: " + e.getClass().getSimpleName() + ":" + e.getMessage(), "ignoring request");
			
		}
		
		
	}
	
	
	public static void main(String[] args) {
		
		
		StringBuffer sb = new StringBuffer();
		String ap = null;
		sb.append(ap);
		System.out.println(sb.toString().endsWith("ulld"));
		
	}
	
}
