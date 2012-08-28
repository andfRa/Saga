package org.saga.economy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException;
import org.bukkit.inventory.ItemStack;
import org.saga.Clock.DaytimeTicker;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.messages.EconomyMessages;
import org.saga.player.SagaPlayer;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;


public class EconomyManager implements DaytimeTicker{

	
	/**
	 * Instances.
	 */
	private static HashSet<EconomyManager> instances = new HashSet<EconomyManager>();
	
	/**
	 * Gets the economy manager for the given world.
	 * 
	 * @param worldName world name
	 * @return economy manager
	 * @throws InvalidWorldException when the name of the world is invalid
	 */
	public static EconomyManager manager(String worldName) throws InvalidWorldException {
		
		
		for (EconomyManager instance : instances) {
			
			if(instance.worldName.equals(worldName)) return instance;
			
		}
		
		throw new InvalidWorldException(worldName);
		
		
	}
	
	/**
	 * Gets the economy manager for the given world.
	 * 
	 * @param location location, null causes an exception
	 * @return economy manager
	 * @throws InvalidWorldException when the name of the world is invalid or location is null
	 */
	public static EconomyManager manager(Location location) throws InvalidWorldException {
		
		
		if(location == null) throw new InvalidWorldException("null location");

		String worldName = location.getWorld().getName();
		
		return manager(worldName);
		
		
	}
	
	
	/**
	 * World name.
	 */
	private String worldName;
	
	/**
	 * Trading deals.
	 */
	private ArrayList<TradeDeal> tradeDeals = new ArrayList<TradeDeal>();

	
	
	// InitialiSation:
	/**
	 * Initialises.
	 * 
	 * @param worldName world name
	 */
	public EconomyManager(String worldName) {
		this.worldName = worldName;
	}
	
	
	
	// Timed:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.TimeOfDayTicker#timeOfDayTick(org.saga.Clock.TimeOfDayTicker.TimeOfDay)
	 */
	@Override
	public boolean daytimeTick(Daytime timeOfDay) {
		return false;
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.TimeOfDayTicker#checkWorld(java.lang.String)
	 */
	@Override
	public boolean checkWorld(String worldName) {
		
		return this.worldName.equals(worldName);

	}
	
	
	
	// Interaction:
	/**
	 * Makes a transaction
	 * 
	 * @param targeter the trader that interacted
	 * @param targeted the trader that got interacted with
	 * @param transaction transaction
	 */
	public static void transaction2(Trader targeter, Trader targeted, Transaction transaction) {

		
		if(transaction == null){
			if(targeter instanceof SagaPlayer){
				((SagaPlayer) targeter).message(EconomyMessages.signNotActive());
				return;
			}
		}
		
		TransactionType type = transaction.getType();
		Material material = transaction.getMaterial();
		Integer amount = transaction.getAmount();
		Double totalValue = transaction.getTotalValue();
		
		// Enough money:
		if(type.equals(TransactionType.SELL)){

			// Enough money:
			if(targeted.getCoins() < totalValue){
				if(targeter instanceof SagaPlayer){
					((SagaPlayer) targeter).message(EconomyMessages.notEnoughStoredCoins());
				}
				return;
			}
			
			// Enough items:
			if(targeter.getAmount(material) < amount){
				if(targeter instanceof SagaPlayer){
					((SagaPlayer) targeter).message(EconomyMessages.notEnoughMaterial(material));
				}
				return;
			}

			// Do transaction:
			ItemStack itemStack = new ItemStack(material, amount);
			targeted.addItem(itemStack);
			targeted.removeCoins(totalValue);
			targeter.removeItem(itemStack);
			targeter.addCoins(totalValue);

			// Inform:
			if(targeter instanceof SagaPlayer){
				((SagaPlayer) targeter).message(EconomyMessages.targeterTrade(transaction));
			}
			
		}else if(type.equals(TransactionType.BUY)){

			// Enough money:
			if(targeter.getCoins() < totalValue){
				if(targeter instanceof SagaPlayer){
					((SagaPlayer) targeter).message(EconomyMessages.notEnoughCoins());
				}
				return;
			}
			
			// Enough items:
			if(targeted.getAmount(material) < amount){
				if(targeter instanceof SagaPlayer){
					((SagaPlayer) targeter).message(EconomyMessages.notEnoughStoredMaterial(material));
				}
				return;
			}

//			// Fix value:
//			if(value > interacter.getMoney()){
//				value = interacter.getMoney();
//				amount = transaction.getAmount(value);
//			}
			
			// Do transaction:
			ItemStack itemStack = new ItemStack(material, amount);
			targeted.removeItem(itemStack);
			targeted.addCoins(totalValue);
			targeter.addItem(itemStack);
			targeter.removeCoins(totalValue);
			
			// Inform:
			if(targeter instanceof SagaPlayer){
				((SagaPlayer) targeter).message(EconomyMessages.targeterTrade(transaction));
			}
			
		}else{
			SagaLogger.severe(EconomyManager.class, "unsupported transaction");
			if(targeter instanceof SagaPlayer){
				((SagaPlayer) targeter).error("unsupproted transaction");
			}
			return;
		}
		
		
	}
	
	
	
	// Loading unloading:
	/**
	 * Loads economy managers for all worlds.
	 * 
	 */
	public static void load() {

		// Inform:
		SagaLogger.info("Loading economy.");
		
		// Load for all worlds:
		List<World> worlds = Saga.plugin().getServer().getWorlds();
		for (World world : worlds) {
			load(world.getName());
		}
		
	}
	
	/**
	 * Loads the manager.
	 * 
	 * @param worldName world name
	 */
	public static void load(String worldName) {
		
		
		EconomyManager instance = new EconomyManager(worldName);
		instance.tradeDeals = new ArrayList<TradeDeal>();
		
		// Trading deals:
		TradeDeal[] reTradingDeals = new TradeDeal[0];
		try {
			
			 if(!WriterReader.checkExists(Directory.TRADE_DEALS, worldName)){
				 reTradingDeals = new TradeDeal[0];
			 }else{
				reTradingDeals = WriterReader.read(Directory.TRADE_DEALS, worldName, TradeDeal[].class);
			 }
			
		} catch (JsonParseException e) {
			
			SagaLogger.severe(EconomyManager.class, "failed to parse empty trading deals: " + e.getClass().getSimpleName() + ": " + e.getMessage());
			SagaLogger.info("Parse message :" + e.getMessage());
			
		} catch (IOException e) {
			
			SagaLogger.severe(EconomyManager.class, "failed to read trading deals: " + e.getClass().getSimpleName() + ": " + e.getMessage());
			reTradingDeals = new TradeDeal[0];
			
		}
		
		for (int i = 0; i < reTradingDeals.length; i++) {
			
			TradeDeal tradingDeal = reTradingDeals[i];
			tradingDeal.complete();
			instance.tradeDeals.add(tradingDeal);
			
		}
//		
//		// Enable clock:
//		Clock.clock().registerDaytimeTick(instance);
//		
		instances.add(instance);
		
		
	}

	/**
	 * Saves for all worlds.
	 * 
	 */
	public static void save() {

		// Inform:
		SagaLogger.info("Saving economy.");
		
		// Save for all worlds:
		List<World> worlds = Saga.plugin().getServer().getWorlds();
		for (World world : worlds) {
			save(world.getName());
		}
		
	}
	
	/**
	 * Saves the manager for the world.
	 * 
	 * @param worldName world name
	 */
	public static void save(String worldName) {
		
		
		// Get the correct manager:
		EconomyManager instance;
		try {
			instance = manager(worldName);
		} catch (InvalidWorldException e1) {
			SagaLogger.severe(EconomyManager.class, "failed to unload a economy manager for " + worldName + " world isn't loaded");
			return;
		}
		
		// Trading deals:
		try {
			WriterReader.write(Directory.TRADE_DEALS, worldName, instance.tradeDeals.toArray(new TradeDeal[instance.tradeDeals.size()]));
		} catch (IOException e) {
			SagaLogger.severe(EconomyManager.class, "failed to write trading deals: " + e.getClass().getSimpleName() + ": " + e.getMessage());
		}
		
		
	}

	/**
	 * Unloads economy managers for all worlds.
	 * 
	 */
	public static void unload() {
		
		
		// Inform:
		SagaLogger.info("Unloading economy.");
		
		// Unload for all worlds:
		List<World> worlds = Saga.plugin().getServer().getWorlds();
		for (World world : worlds) {
			unload(world.getName());
		}
		
		instances = null;
		
		
	}
	
	/**
	 * Unloads the manager for the world.
	 * 
	 * @param worldName world name
	 */
	public static void unload(String worldName) {
		
//		
//		// Get the correct manager:
//		EconomyManager instance;
//		try {
//			instance = manager(worldName);
//		} catch (InvalidWorldException e1) {
//			SagaLogger.severe(EconomyManager.class, "failed to unload a economy manager for " + worldName + " world isn't loaded");
//			return;
//		}
//		
		// Save:
		save(worldName);
//		
//		// Disable clock:
//		Clock.clock().unregisterDaytimeTick(instance);
//		
		
	}
	
	
	
	// Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return worldName;
	}
	

	
	// Types:
	/**
	 * Transaction type.
	 * 
	 * @author andf
	 *
	 */
	public enum TransactionType{
		
		SELL,
		BUY,
		INVALID;
		
	}
	
	public static class DuplicateTransactionException extends Exception{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		
		
	}
	
	public static class InvalidWorldException extends Exception{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public InvalidWorldException(String message) {
			super(message);
		}
		
		
	}
	
	public static class TradeDealNotFoundException extends Exception{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private int id;
		
		public TradeDealNotFoundException(int id) {
			super();
			this.id = id;
		}
		public int getId() {
			return id;
		}
		
	}
	
	
}
