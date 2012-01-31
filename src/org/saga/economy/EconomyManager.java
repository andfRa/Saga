package org.saga.economy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.saga.Clock;
import org.saga.Clock.TimeOfDayTicker;
import org.saga.Saga;
import org.saga.config.EconomyConfiguration;
import org.saga.constants.IOConstants.WriteReadType;
import org.saga.economy.TradeDeal.TradeDealException;
import org.saga.economy.TradeDeal.TradeDealType;
import org.saga.player.SagaPlayer;
import org.saga.utility.WriterReader;


import com.google.gson.JsonParseException;


public class EconomyManager implements TimeOfDayTicker{

	
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
			if(instance.worldName.equals(worldName)){
				return instance;
			}
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
		
		if(location == null){
			throw new InvalidWorldException("null location");
		}
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

	
	// Initialization:
	/**
	 * Initializes.
	 * 
	 * @param worldName world name
	 */
	public EconomyManager(String worldName) {
		this.worldName = worldName;
	}
	
	
	// Clock tick:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.TimeOfDayTicker#timeOfDayTick(org.saga.Clock.TimeOfDayTicker.TimeOfDay)
	 */
	@Override
	public void timeOfDayTick(TimeOfDay timeOfDay) {
		
		
//		if(!timeOfDay.equals(TimeOfDay.SUNRISE)){
//			return;
//		}
		
		// Update and remove expired deals:
		for (int i = 0; i < tradeDeals.size(); i++) {
			tradeDeals.get(i).nextDay();
			if(tradeDeals.get(i).getDaysLeft() <= 0){
				tradeDeals.remove(i);
				i--;
				continue;
			}
		}
		
		// Player count:
		World world = Saga.plugin().getServer().getWorld(worldName);
		if(world == null){
			Saga.severe(EconomyManager.class, "failed to get a world with " + worldName + " name", "ignoring trade deal amount refresh");
			return;
		}
		int playerCount = world.getPlayers().size();
		
		// Deal amount:
		Integer dealsAmount = EconomyConfiguration.config().calculateDealsPerPlayer(playerCount);
		
		
		// Check if new deals are needed:
		if(tradeDeals.size() >= dealsAmount){
			return;
		}
		
		// Calculate how much to add:
		int newDeals = EconomyConfiguration.config().calculateDealsGainPerPlayer(playerCount);
		if(newDeals < 0){
			Saga.severe(getClass(), "negative new deals amount", "ignoring tick");
			return;
		}
		if(tradeDeals.size() + newDeals > dealsAmount){
			newDeals = dealsAmount - tradeDeals.size();
		}
		
		// Add deals:
		for (int i = 1; i <= newDeals; i++) {
			addTradeDeal(EconomyConfiguration.config().nextTradeDeal().createRandomTradeDeal());
		}

		
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
	public static void transaction(Trader targeter, Trader targeted, Transaction transaction) {

		
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
					((SagaPlayer) targeter).message(EconomyMessages.notEnoughStoredMoney());
				}
				return;
			}
			
			// Enough items:
			if(targeter.getItemCount(material) < amount){
				if(targeter instanceof SagaPlayer){
					((SagaPlayer) targeter).message(EconomyMessages.notEnoughMaterial(material));
				}
				return;
			}

//			// Fix amount:
//			if(amount > interacted.getItemCount(material)){
//				amount = interacted.getItemCount(material);
//				value = transaction.getValue(amount);
//			}
			
			// Don't trade damaged items:
			
			
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
			if(targeted.getItemCount(material) < amount){
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
			Saga.severe(EconomyManager.class, "unsupported transaction", "ignoring request");
			if(targeter instanceof SagaPlayer){
				((SagaPlayer) targeter).error("unsupproted transaction");
			}
			return;
		}
		
		
	}

	/**
	 * Adds a trade deal.
	 * 
	 * @param deal trade deal
	 */
	private void addTradeDeal(TradeDeal deal) {


		// Find the smallest available id:
		Integer availableID = -1;
		boolean idFound = false;
		if(tradeDeals.size() == 0){
			idFound = true;
			availableID = 0;
		}
		while(!idFound){
			
			availableID++;
			for (int i = 0; i < tradeDeals.size(); i++) {
				if(tradeDeals.get(i).getId().equals(availableID)) break;
				if(i == tradeDeals.size() -1) idFound = true;
			}
			
		}
		
		// Add:
		deal.setId(availableID);
		tradeDeals.add(deal);
		
		
	}
	
	/**
	 * Takes a trade deal from the list.
	 * 
	 * @param id trade deal id
	 * @return trade deal
	 * @throws TradeDealNotFoundException if the deal with the given ID doesn't exist
	 */
	public TradeDeal takeTradeDeal(Integer id) throws TradeDealNotFoundException {

		for (int i = 0; i < tradeDeals.size(); i++) {
			
			if(tradeDeals.get(i).getId().equals(id)){
				TradeDeal tradeDeal = tradeDeals.get(i);
				tradeDeals.remove(i);
				return tradeDeal;
			}
			
		}
		throw new TradeDealNotFoundException(id);
		
	}
	
	/**
	 * Gets all trade deals.
	 * 
	 * @return all trade deals
	 */
	public ArrayList<TradeDeal> getTradingDeals() {
		return new ArrayList<TradeDeal>(tradeDeals);
	}
	
	/**
	 * Finds a trade deal.
	 * 
	 * @param type type
	 * @param material material
	 * @return trade deal, null if not found
	 */
	public TradeDeal findTradeDeal(TradeDealType type, Material material) {

		
		ArrayList<TradeDeal> tDeals = getTradingDeals();
		for (TradeDeal tradeDeal : tDeals) {
			
			if(!tradeDeal.getType().equals(type)) continue;
			
			if(!tradeDeal.getMaterial().equals(material)) continue;
			
			return tradeDeal;
			
		}
		
		return null;
		
		
	}
	
	// Load unload:
	/**
	 * Loads economy managers for all worlds.
	 * 
	 */
	public static void load() {

		// Inform:
		Saga.info("Loading economy.");
		
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
			
			reTradingDeals = WriterReader.readTradeDeals(worldName);
			
		} catch (JsonParseException e) {
			
			Saga.severe(EconomyManager.class, "failed to parse empty trading deals: " + e.getClass().getSimpleName() + ": " + e.getMessage(), "using empty list");
			Saga.info("Parse message :" + e.getMessage());
			
		} catch (FileNotFoundException e) {
			
			try {
				WriterReader.writeTradeDeals(worldName, new TradeDeal[0], WriteReadType.TRADE_AGREEMENTS_NORMAL);
			} catch (IOException e1) {
				Saga.severe(EconomyManager.class, "failed to write empty trading deals: " + e1.getClass().getSimpleName() + ": " + e.getMessage(), "ignoring write");
			}
			
		}catch (IOException e) {
			
			Saga.severe(EconomyManager.class, "failed to read trading deals: " + e.getClass().getSimpleName() + ": " + e.getMessage(), "using empty list");
			reTradingDeals = new TradeDeal[0];
			
		}
		for (int i = 0; i < reTradingDeals.length; i++) {
			
			TradeDeal tradingDeal;
			try {
				tradingDeal = reTradingDeals[i];
				tradingDeal.complete();
			} catch (TradeDealException e) {
				Saga.severe(EconomyManager.class, "failed to complete trading deals element: " + e.getClass().getSimpleName() + ": " + e.getMessage(), "ignoring element");
				continue;
			}
			instance.tradeDeals.add(tradingDeal);
			
		}
		
		// Enable clock:
		Clock.clock().registerTimeOfDayTick(instance);
		
		
		instances.add(instance);
		
		
	}

	/**
	 * Saves for all worlds.
	 * 
	 */
	public static void save() {

		// Inform:
		Saga.info("Saving economy.");
		
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
			Saga.severe(EconomyManager.class, "failed to unload a economy manager for " + worldName + " world isn't loaded", "ignoring unload");
			return;
		}
		
		// Trading deals:
		try {
			WriterReader.writeTradeDeals(worldName, instance.tradeDeals.toArray(new TradeDeal[instance.tradeDeals.size()]), WriteReadType.TRADE_AGREEMENTS_NORMAL);
		} catch (IOException e) {
			Saga.severe(EconomyManager.class, "failed to write trading deals: " + e.getClass().getSimpleName() + ": " + e.getMessage(), "ignoring write");
		}
		
		
	}

	/**
	 * Unloads economy managers for all worlds.
	 * 
	 */
	public static void unload() {
		
		
		// Inform:
		Saga.info("Unloading economy.");
		
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
		
		
		// Get the correct manager:
		EconomyManager instance;
		try {
			instance = manager(worldName);
		} catch (InvalidWorldException e1) {
			Saga.severe(EconomyManager.class, "failed to unload a economy manager for " + worldName + " world isn't loaded", "ignoring unload");
			return;
		}
		
		// Save:
		save(worldName);
		
		// Disable clock:
		Clock.clock().unregisterTimeOfDayTick(instance);
		
		
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
