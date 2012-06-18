package org.saga;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.bukkit.World;
import org.saga.Clock.TimeOfDayTicker.TimeOfDay;


public class Clock implements Runnable{

	
	/**
	 * Instance.
	 */
	private static Clock instance; 
	
	/**
	 * Gets the clock.
	 * 
	 * @return clock.
	 */
	public static Clock clock() {
		return instance;
	}
	
	/**
	 * Seconds cycle.
	 */
	private short secondsCycle = 0;
	
	/**
	 * Minutes cycle.
	 */
	private short minutesCycle = 0;
	
	/**
	 * Hours cycle.
	 */
	private short hoursCycle = 0;
	
	
	/**
	 * Instances that will receive a tick every second.
	 */
	private HashSet<SecondTicker> second = new HashSet<SecondTicker>();

	/**
	 * Instances that will receive a tick every minute.
	 */
	private HashSet<MinuteTicker> minute = new HashSet<MinuteTicker>();

	/**
	 * Instances that will receive a tick every hour.
	 */
	private HashSet<HourTicker> hour = new HashSet<HourTicker>();

	/**
	 * Instances that will receive at certain parts of the day.
	 */
	private HashSet<TimeOfDayTicker> timeOfDays = new HashSet<TimeOfDayTicker>();

	private Hashtable<String, Long> previousWorldTime = new Hashtable<String, Long>();
	
	/**
	 * Initialises the clock.
	 * 
	 */
	public Clock() {

	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	synchronized public void run() {
		
		
//		String time = Saga.plugin().getServer().getWorld("world").getTime() +"";
//		System.out.println("time=" + time.substring(0,time.length() -3)+":"+time.substring(time.length() -3));
		
		// Seconds:
		HashSet<SecondTicker> removeTickers = new HashSet<Clock.SecondTicker>();
		
		synchronized (second) {
			
			secondsCycle ++;
			ArrayList<SecondTicker> second = new ArrayList<Clock.SecondTicker>(this.second);
			for (int i = 0; i < second.size(); i++) {
				
				 if(!second.get(i).clockSecondTick()){
					 removeTickers.add(second.get(i));
				 }
				 
			}
			
		}
		
		for (SecondTicker ticker : removeTickers) {
			second.remove(ticker);
		}
		
		// Minutes:
		synchronized (minute) {
			
			if(secondsCycle > 59){
				secondsCycle = 0;
				minutesCycle++;
				// Minutes:
//				Saga.info("Minute tick for " + minute.size() + " registered instances.");
				ArrayList<MinuteTicker> minute = new ArrayList<Clock.MinuteTicker>(this.minute);
				for (int i = 0; i < minute.size(); i++) {
					minute.get(i).clockMinuteTick();
				}
			}
			
		}
		
		// Hours:
		synchronized (hour) {

			if(minutesCycle > 59){
				minutesCycle = 0;
				hoursCycle ++;
				// Hours:
				ArrayList<HourTicker> hour = new ArrayList<HourTicker>(this.hour);
				Saga.info("Hour tick for " + hour.size() + " registered instances.");
				for (int i = 0; i < hour.size(); i++) {
					hour.get(i).clockHourTick();
				}
			}
			if(hoursCycle > 23){
				hoursCycle = 0;
			}
			
		}

		// Time of day:
		synchronized (timeOfDays) {
			
			sendTimeOfDayTicks();
			
		}
		

	}


	/**
	 * Registers a second ticker.
	 * 
	 * @param ticker ticker
	 */
	public void registerSecondTick(SecondTicker ticker) {

		
		synchronized (second) {
			
			if(second.contains(ticker)){
				Saga.severe("Tried to register an already registered second ticker. Ignoring request.");
				return;
			}
			second.add(ticker);
		
		}
		
		
	}

	/**
	 * Unregisters a second ticker.
	 * 
	 * @param ticker ticker
	 */
	public void unregisterSecondTick(SecondTicker ticker) {

		
		synchronized (second) {
		
			if(!second.contains(ticker)){
				Saga.severe("Tried to unregister an non-registered second ticker. Ignoring request.");
				return;
			}
			second.remove(ticker);
			
			
		}
		
		
	}
	
	
	/**
	 * Registers a hour ticker.
	 * 
	 * @param ticker ticker
	 */
	public void registerMinuteTick(MinuteTicker ticker) {

		
		synchronized (minute) {

			if(minute.contains(ticker)){
				Saga.severe("Tried to register an already registered minute ticker. Ignoring request.");
				return;
			}
			minute.add(ticker);
			
		}
		
		
	}
	

	/**
	 * Unregisters a hour ticker.
	 * 
	 * @param ticker ticker
	 */
	public void unregisterMinuteTick(MinuteTicker ticker) {

		
		synchronized (minute) {

			if(!minute.contains(ticker)){
				Saga.severe("Tried to unregister an non-registered minute ticker. Ignoring request.");
				return;
			}
			minute.remove(ticker);
			
		}
		
		
	}

	
	/**
	 * Registers a hour ticker.
	 * 
	 * @param ticker ticker
	 */
	public void registerHourTick(HourTicker ticker) {

		
		synchronized (hour) {
			
			if(minute.contains(ticker)){
				Saga.severe("Tried to register an already registered hour ticker. Ignoring request.");
				return;
			}
			hour.add(ticker);
			
		}
		
		
	}

	/**
	 * Unregisters a hour ticker.
	 * 
	 * @param ticker ticker
	 */
	public void unregisterHourTick(HourTicker ticker) {

		
		synchronized (hour) {

			if(!hour.contains(ticker)){
				Saga.severe("Tried to unregister an non-registered hour ticker. Ignoring request.");
				return;
			}
			hour.remove(ticker);
				
		}
		
		
	}

	
	/**
	 * Unregisters a time of day ticker.
	 * 
	 * @param ticker ticker
	 */
	public void registerTimeOfDayTick(TimeOfDayTicker ticker) {

		
		synchronized (timeOfDays) {

			if(timeOfDays.contains(ticker)){
				Saga.severe("Tried to register an already registered time of day ticker. Ignoring request.");
				return;
			}
			timeOfDays.add(ticker);
			
		}
		
		
	}
	
	/**
	 * Unregisters a hour ticker.
	 * 
	 * @param ticker ticker
	 */
	public void unregisterTimeOfDayTick(TimeOfDayTicker ticker) {

		
		synchronized (timeOfDays) {
		
			if(!timeOfDays.contains(ticker)){
				Saga.severe("Tried to unregister an non-registered time of day ticker. Ignoring request.");
				return;
			}
			timeOfDays.remove(ticker);
			
		}
		
		
	}
	
	/**
	 * Sends time of day ticks.
	 * 
	 */
	private void sendTimeOfDayTicks() {

		
		List<World> worlds = Saga.plugin().getServer().getWorlds();
		for (World world : worlds) {
			
			// Hardcode ignore skylands:
			if(world.getName().equals("world_the_end") || world.getName().equals("world_skylands")){
				continue;
			}
			
			String worldName = world.getName();
			Long previousTime = previousWorldTime.get(worldName);
			Long currentTime = world.getTime();
			if(previousTime == null){
				previousTime = world.getTime();
				previousWorldTime.put(worldName, previousTime);
			}
			TimeOfDay timeOfDay = null;
			
			long timePassed = 0;
			if(currentTime < previousTime){
				timePassed = Math.abs(currentTime + (24000 - previousTime));
			}else{
				timePassed = Math.abs(currentTime - previousTime);
			}
			if(timePassed > 2000){
				Saga.warning(Clock.class, "time of day tick skipped too much", "ignoring tick");
				previousWorldTime.put(worldName, currentTime);
				return;
			}
			
			// Dawn:
			if( previousTime < 12300 && currentTime >= 12300){
				timeOfDay = TimeOfDay.DAWN;
			}
			// Midnight:
			else if( previousTime < 18075 && currentTime >= 18075){
				timeOfDay = TimeOfDay.MIDNIGHT;
			}
			// Midday:
			else if( previousTime < 6225 && currentTime >= 6225){
				timeOfDay = TimeOfDay.MIDDAY;
			}
			// Sunrise:
			else if( previousTime < 23850 && currentTime >= 23850){
				timeOfDay = TimeOfDay.SUNRISE;
			}
			
			if(timeOfDay != null){
				Saga.info(timeOfDay.name() + " for " + worldName + " world.");
				ArrayList<TimeOfDayTicker> timeOfDays = new ArrayList<TimeOfDayTicker>(this.timeOfDays);
				for (TimeOfDayTicker ticker : timeOfDays) {
					
					if(ticker.checkWorld(worldName)) ticker.timeOfDayTick(timeOfDay);
					
				}
			}
			
			previousWorldTime.put(worldName, currentTime);
			
		}
		
		
	}
	
	/**
	 * Forces a time of day tick.
	 * 
	 * @param timeOfDay time of day tick
	 */
	public void forceTick(TimeOfDay timeOfDay) {

		
		for (TimeOfDayTicker ticker : timeOfDays) {
			
			List<World> worlds = Saga.plugin().getServer().getWorlds();
			for (World world : worlds) {
				
				if(ticker.checkWorld(world.getName())) ticker.timeOfDayTick(timeOfDay);
				
			}
			
			
		}

		
	}
	
	/**
	 * Loads the clock.
	 * 
	 */
	public static void load() {

		Clock clock = new Clock();
		Saga.plugin().getServer().getScheduler().scheduleAsyncRepeatingTask(Saga.plugin(),clock , 200L, 20L);
		instance = clock;
		
	}
	
	/**
	 * Unloads the clock.
	 * 
	 */
	public static void unload() {

		instance.second = null;
		instance.minute = null;
		instance.hour = null;
		instance.timeOfDays = null;
		instance = null;
		
	}
	
	
	public static interface SecondTicker{
		
		
		/**
		 * A clock tick.
		 * 
		 * @return true if continue
		 */
		public boolean clockSecondTick();
		
		
	}
	
	public static interface MinuteTicker{
		
		
		/**
		 * A clock tick.
		 * 
		 */
		public void clockMinuteTick();
		
		
	}
	
	public static interface HourTicker{
		
		
		/**
		 * A clock tick.
		 * 
		 */
		public void clockHourTick();
		
		
	}
	
	public static interface TimeOfDayTicker{
		
		
		/**
		 * A clock tick.
		 * 
		 * @param timeOfDay time of day
		 */
		public void timeOfDayTick(TimeOfDay timeOfDay);

		/**
		 * Checks if the world is correct.
		 * 
		 * @param worldName world name
		 * @return true if the world is correct
		 */
		public boolean checkWorld(String worldName);
		
		
		public static enum TimeOfDay{
			
			
			SUNRISE,
			MIDDAY,
			DAWN,
			MIDNIGHT,
			ALL;
			
			
		}
		
		
	}
	
}
