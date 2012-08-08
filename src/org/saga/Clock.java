package org.saga;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.bukkit.World;
import org.saga.Clock.DaytimeTicker.Daytime;


public class Clock implements Runnable{

	
	/**
	 * Lag delay compensation.
	 */
	private final static Integer LAG_COMPENSATION = 120;

	
	
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
	 * Second tick instances.
	 */
	private HashSet<SecondTicker> seconds = new HashSet<SecondTicker>();

	/**
	 * Minute tick instances.
	 */
	private HashSet<MinuteTicker> minutes = new HashSet<MinuteTicker>();

	/**
	 * Hour tick instances.
	 */
	private HashSet<HourTicker> hours = new HashSet<HourTicker>();

	/**
	 * Daytime tick instances.
	 */
	private HashSet<DaytimeTicker> daytimes = new HashSet<DaytimeTicker>();

	/**
	 * Previous daytime.
	 */
	private Hashtable<String, Daytime> prevDaytimes = new Hashtable<String, Clock.DaytimeTicker.Daytime>();
	
	
	
	// Initialisation:
	/**
	 * Creates a clock.
	 * 
	 */
	public Clock() {

	}
	
	
	
	// Ticking:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	synchronized public void run() {
		
		
//		String time = Saga.plugin().getServer().getWorld("world").getTime() +"";
//		System.out.println("time=" + time);
		
		HashSet<SecondTicker> removeSecond = new HashSet<Clock.SecondTicker>();
		HashSet<MinuteTicker> removeMinute = new HashSet<Clock.MinuteTicker>();
		
		// Seconds:
		synchronized (seconds) {
			
			secondsCycle ++;
			
			ArrayList<SecondTicker> second = new ArrayList<Clock.SecondTicker>(this.seconds);
			for (int i = 0; i < second.size(); i++) {
				
				 if(!second.get(i).clockSecondTick()){
					 removeSecond.add(second.get(i));
				 }
				 
			}
			
		}
		
		for (SecondTicker ticker : removeSecond) {
			seconds.remove(ticker);
		}
		
		// Minutes:
		synchronized (minutes) {
			
			if(secondsCycle > 59){
				
				secondsCycle = 0;
				minutesCycle++;
				
				ArrayList<MinuteTicker> minute = new ArrayList<Clock.MinuteTicker>(this.minutes);
				for (int i = 0; i < minute.size(); i++) {
					
					if(!minute.get(i).clockMinuteTick()){
						removeMinute.add(minute.get(i));
					}
					
				}
				
			}
			
		}
		
		for (MinuteTicker ticker : removeMinute) {
			minutes.remove(ticker);
		}
		
		// Hours:
		synchronized (hours) {

			if(minutesCycle > 59){
				minutesCycle = 0;
				hoursCycle ++;

				ArrayList<HourTicker> hour = new ArrayList<HourTicker>(this.hours);
				SagaLogger.info("Hour tick for " + hour.size() + " registered instances.");
				for (int i = 0; i < hour.size(); i++) {
					
					hour.get(i).clockHourTick();
					
				}
			}
			
			if(hoursCycle > 23){
				hoursCycle = 0;
			}
			
		}

		// Time of day:
		synchronized (daytimes) {
			
			sendDaytimeTicks();
			
		}
		

	}

	/**
	 * Sends daytime ticks.
	 * 
	 */
	private void sendDaytimeTicks() {

		
		List<World> worlds = Saga.plugin().getServer().getWorlds();
		
		for (World world : worlds) {
			
			String worldName = world.getName();
			
			Daytime prevDaytime = prevDaytimes.get(worldName);
			Daytime currDaytime = Daytime.getDaytime(world.getTime());
			
			// Tick only if daytime changed:
			if(currDaytime != Daytime.NONE && prevDaytime != currDaytime){
				
				ArrayList<DaytimeTicker> daytimes = new ArrayList<DaytimeTicker>(this.daytimes);
				for (DaytimeTicker ticker : daytimes) {
					
					if(ticker.checkWorld(worldName)) ticker.daytimeTick(currDaytime);
					
				}
				
			}
			
			// Update previous:
			prevDaytimes.put(worldName, currDaytime);
			
			
		}
		
		
	}
	
	/**
	 * Forces next daytime.
	 * 
	 * @param world world
	 * @return next daytime
	 */
	public Daytime forceNextDaytime(World world) {

		
		Daytime nextDaytime = Daytime.getNextDaytime(world.getTime());
		
		world.setTime(nextDaytime.getTime());
		
		return nextDaytime;
		
		
	}

	

	// Registering:
	/**
	 * Registers ticking.
	 * 
	 * @param ticker ticker
	 */
	public void registerSecondTick(SecondTicker ticker) {

		
		synchronized (seconds) {
			
			if(seconds.contains(ticker)){
				SagaLogger.warning(getClass(), ticker.getClass().getSimpleName() + "{" + ticker + "}" + " second ticker already registered");
				return;
			}
			seconds.add(ticker);
		
		}
		
		
	}

	/**
	 * Unregisters ticking.
	 * 
	 * @param ticker ticker
	 */
	public void unregisterSecondTick(SecondTicker ticker) {

		
		synchronized (seconds) {
		
			if(!seconds.contains(ticker)){
				SagaLogger.warning(getClass(), ticker.getClass().getSimpleName() + "{" + ticker + "}" + " second ticker not registered");
				return;
			}
			seconds.remove(ticker);
			
			
		}
		
		
	}
	
	/**
	 * Checks if the second clock is ticking.
	 * 
	 * @param ticker ticker
	 * @return true if ticking
	 */
	public boolean isSecondTicking(SecondTicker ticker) {
		return seconds.contains(ticker);
	}
	
	
	/**
	 * Registers ticking.
	 * 
	 * @param ticker ticker
	 */
	public void registerMinuteTick(MinuteTicker ticker) {

		
		synchronized (minutes) {

			if(minutes.contains(ticker)){
				SagaLogger.warning(getClass(), ticker.getClass().getSimpleName() + "{" + ticker + "}" + " minute ticker already registered");
				return;
			}
			minutes.add(ticker);
			
		}
		
		
	}

	/**
	 * Unregisters ticking.
	 * 
	 * @param ticker ticker
	 */
	public void unregisterMinuteTick(MinuteTicker ticker) {

		
		synchronized (minutes) {

			if(!minutes.contains(ticker)){
				SagaLogger.warning(getClass(), ticker.getClass().getSimpleName() + "{" + ticker + "}" + " minute ticker not registered");
				return;
			}
			minutes.remove(ticker);
			
		}
		
		
	}

	/**
	 * Checks if the minute clock is ticking.
	 * 
	 * @param ticker ticker
	 * @return true if ticking
	 */
	public boolean isMinuteTicking(MinuteTicker ticker) {
		return minutes.contains(ticker);
	}
	
	
	/**
	 * Registers ticking.
	 * 
	 * @param ticker ticker
	 */
	public void registerHourTick(HourTicker ticker) {

		
		synchronized (hours) {
			
			if(minutes.contains(ticker)){
				SagaLogger.warning(getClass(), ticker.getClass().getSimpleName() + "{" + ticker + "}" + " hour ticker already registered");
				return;
			}
			hours.add(ticker);
			
		}
		
		
	}

	/**
	 * Unregisters ticking.
	 * 
	 * @param ticker ticker
	 */
	public void unregisterHourTick(HourTicker ticker) {

		
		synchronized (hours) {

			if(!hours.contains(ticker)){
				SagaLogger.warning(getClass(), ticker.getClass().getSimpleName() + "{" + ticker + "}" + " hour ticker not registered");
				return;
			}
			hours.remove(ticker);
				
		}
		
		
	}

	
	/**
	 * Registers ticking.
	 * 
	 * @param ticker ticker
	 */
	public void registerDaytimeTick(DaytimeTicker ticker) {

		
		synchronized (daytimes) {

			if(daytimes.contains(ticker)){
				SagaLogger.warning(getClass(), ticker.getClass().getSimpleName() + "{" + ticker + "}" + " daytime ticker already registered");
				return;
			}
			daytimes.add(ticker);
			
		}
		
		
	}
	
	/**
	 * Unregisters ticking.
	 * 
	 * @param ticker ticker
	 */
	public void unregisterDaytimeTick(DaytimeTicker ticker) {

		
		synchronized (daytimes) {
		
			if(!daytimes.contains(ticker)){
				SagaLogger.warning(getClass(), ticker.getClass().getSimpleName() + "{" + ticker + "}" + " daytime ticker not registered");
				return;
			}
			daytimes.remove(ticker);
			
		}
		
		
	}
	
	
	
	// Loading and unloading:
	/**
	 * Loads the clock.
	 * 
	 */
	public static void load() {

		
		// Initialisation:
		Clock clock = new Clock();
		Saga.plugin().getServer().getScheduler().scheduleAsyncRepeatingTask(Saga.plugin(),clock , 200L, 20L);
		instance = clock;
		
		// Initial daytimes:
		List<World> worlds = Saga.plugin().getServer().getWorlds();
		for (World world : worlds) {
			clock.prevDaytimes.put(world.getName(), Daytime.getDaytime(world.getTime()));
		}
		
		
	}
	
	/**
	 * Unloads the clock.
	 * 
	 */
	public static void unload() {

		
		instance.seconds = null;
		instance.minutes = null;
		instance.hours = null;
		instance.daytimes = null;
		instance.prevDaytimes = null;
		instance = null;
		
		
	}
	
	
	
	// Types:
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
		 * @return true if continue
		 */
		public boolean clockMinuteTick();
		
		
	}
	
	public static interface HourTicker{
		
		
		/**
		 * A clock tick.
		 * 
		 */
		public void clockHourTick();
		
		
	}
	
	public static interface DaytimeTicker{
		
		
		/**
		 * A clock tick.
		 * 
		 * @param daytime daytime
		 */
		public void daytimeTick(Daytime daytime);

		/**
		 * Checks if the world is correct.
		 * 
		 * @param worldName world name
		 * @return true if the world is correct
		 */
		public boolean checkWorld(String worldName);
		
		
		/**
		 * Represents daytime.
		 * 
		 * @author andf
		 *
		 */
		public static enum Daytime{
			
			
			MIDDAY(6225L),
			SUNSET(12300L),
			MIDNIGHT(18075L),
			SUNRISE(23850L),
			NONE(0L);
			

			
			/**
			 * Time.
			 */
			private Long time;
			
			
			
			/**
			 * Sets constants.
			 * 
			 * @param time time
			 * @param next next daytime
			 */
			private Daytime(Long time) {

				this.time = time;

			}
			
			
			
			/**
			 * Gets the time.
			 * 
			 * @return the time
			 */
			public Long getTime() {
			
			
				return time;
			}

			/**
			 * Gets the daytime.
			 * 
			 * @param time world time
			 * @return corresponding daytime, {@link Daytime#NONE} if none
			 */
			public static Daytime getDaytime(Long time) {

				// Midday:
				if(time > MIDDAY.getTime() && time < MIDDAY.getTime() + LAG_COMPENSATION){
					return MIDDAY; 
				}

				// Sunset:
				if(time > SUNSET.getTime() && time < SUNSET.getTime() + LAG_COMPENSATION){
					return SUNSET; 
				}

				// Midnight:
				if(time > MIDNIGHT.getTime() && time < MIDNIGHT.getTime() + LAG_COMPENSATION){
					return MIDNIGHT; 
				}

				if(time < SUNRISE.getTime() - 24000 + LAG_COMPENSATION) time+= 24000;
				
				// Sunrise:
				if(time > SUNRISE.getTime() && time < SUNRISE.getTime() + LAG_COMPENSATION){
					return SUNRISE; 
				}
				
				return NONE;
				

			}
			
			/**
			 * Gets next daytime.
			 * 
			 * @param time time
			 * @return next daytime
			 */
			public static Daytime getNextDaytime(Long time) {

				
				Daytime[] values = values();
				
				for (int i = 0; i < values.length; i++) {
					if(values[i].getTime() > time) return values[i];
				}
				
				return values[0];
				
				
			}
			
			
			
			/* 
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Enum#toString()
			 */
			@Override
			public String toString() {
				
				return super.toString().replace("_", " ").toLowerCase();

			}

		}
		
		
	}
	
	
}
