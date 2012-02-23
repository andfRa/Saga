package org.saga.buildings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.Clock;
import org.saga.Clock.TimeOfDayTicker;
import org.saga.Saga;
import org.saga.SagaMessages;
import org.saga.buildings.BuildingDefinition.BuildingPermission;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.ChunkGroupMessages;
import org.saga.chunkGroups.SagaChunk;
import org.saga.economy.EconomyCommands;
import org.saga.economy.EconomyManager;
import org.saga.economy.EconomyManager.InvalidWorldException;
import org.saga.economy.EconomyManager.TradeDealNotFoundException;
import org.saga.economy.EconomyManager.TransactionType;
import org.saga.economy.EconomyMessages;
import org.saga.economy.TradeDeal;
import org.saga.economy.TradeDeal.TradeDealException;
import org.saga.economy.TradeDeal.TradeDealType;
import org.saga.economy.TradeSign;
import org.saga.economy.TradeSign.TradeSignException;
import org.saga.economy.Trader;
import org.saga.economy.Transaction;
import org.saga.player.SagaPlayer;
import org.saga.utility.TextUtil;
import org.saga.utility.TwoPointFunction;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;


public class TradingPost extends Building implements Trader, TimeOfDayTicker{

	
	/**
	 * The currency the trade post owns.
	 */
	private Double currency;
	
	/**
	 * Stocked items.
	 */
	private Hashtable<Material, Integer> stockedItems;
	
	/**
	 * Trade signs.
	 */
	private ArrayList<TradeSign> tradeSigns;
	
	/**
	 * Transactions.
	 */
	private ArrayList<Transaction> tradeTransactions;
	
	/**
	 * Trade deals.
	 */
	private ArrayList<TradeDeal> tradeDeals;
	
	/**
	 * Amount of material to be kept.
	 */
	private Hashtable<Material, Integer> exportLimit;
	
	/**
	 * Amount of money to be kept.
	 */
	private Double importLimit;
	
	/**
	 * Trading post is automated if true.
	 */
	private Boolean automated;
	
	// Initialization:
	/**
	 * Initializes
	 * 
	 * @param pointCost point cost
	 * @param moneyCost money cost
	 * @param proficiencies proficiencies
	 */
	private TradingPost(Double currency, Hashtable<Material, Integer> stockedItems, ArrayList<TradeSign> tradeSigns, ArrayList<Transaction> tradeTransactions) {
		
		super("");
		this.currency = currency;
		this.stockedItems = stockedItems;
		this.tradeSigns = tradeSigns;
		this.tradeTransactions = tradeTransactions;
		this.tradeDeals = new ArrayList<TradeDeal>();
		this.exportLimit = new Hashtable<Material, Integer>();
		this.importLimit = 0.0;
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#completeExtended()
	 */
	@Override
	public boolean completeExtended() {
		

		boolean integrity = true;
		
		if(currency == null){
			currency = 0.0;
			Saga.severe(this, "failed to initialize currency field", "setting default");
			integrity = false;
		}
		
		if(stockedItems == null){
			stockedItems = new Hashtable<Material, Integer>();
			Saga.severe(this, "failed to initialize stockedItems field", "setting default");
			integrity = false;
		}
		
		if(tradeSigns == null){
			tradeSigns = new ArrayList<TradeSign>();
			Saga.severe(this, "failed to initialize tradeSigns field", "setting default");
			integrity = false;
		}
		for (int i = 0; i < tradeSigns.size(); i++) {
			if(tradeSigns.get(i) == null){
				Saga.severe(this, "failed to initialize tradeSigns field element", "removing element");
				tradeSigns.remove(i);
				i--;
			}
			try {
				integrity = tradeSigns.get(i).complete(this) && integrity;
				tradeSigns.get(i).refresh();
			} catch (TradeSignException e) {
				Saga.severe(this, "failed to initialize tradeSigns field element: " + e.getMessage(), "removing element");
				tradeSigns.remove(i);
				i--;
			}
		}
		
		if(tradeTransactions == null){
			tradeTransactions = new ArrayList<Transaction>();
			Saga.severe(this, "failed to initialize tradeTransactions field", "setting default");
			integrity = false;
		}
		for (int i = 0; i < tradeTransactions.size(); i++) {
			if(tradeTransactions.get(i) == null){
				Saga.severe(this, "failed to initialize tradeTransactions field element", "removing element");
				tradeTransactions.remove(i);
				i--;
				continue;
			}
			integrity = tradeTransactions.get(i).complete() && integrity;
		}
		
		if(tradeDeals == null){
			tradeDeals = new ArrayList<TradeDeal>();
			Saga.severe(this, "failed to initialize tradeDeals field", "setting default");
			integrity = false;
		}
		for (int i = 0; i < tradeDeals.size(); i++) {
			if(tradeDeals.get(i) == null){
				Saga.severe(this, "failed to initialize tradeDeals field element", "removing element");
				tradeDeals.remove(i);
				i--;
				continue;
			}
			try {
				integrity = tradeDeals.get(i).complete() && integrity;
			} catch (TradeDealException e) {
				Saga.severe(this, "failed to initialize tradeDeals field element: " + e.getMessage(), "removing element");
				tradeDeals.remove(i);
				i--;
				continue;
			}
		}
		
		if(exportLimit == null){
			exportLimit = new Hashtable<Material, Integer>();
			Saga.severe(this, "failed to initialize exportLimit field", "setting default");
			integrity = false;
		}
		
		if(importLimit == null){
			importLimit = 0.0;
			Saga.severe(this, "failed to initialize importLimit field", "setting default");
			integrity = false;
		}
		
		if(automated == null){
			automated = false;
			Saga.severe(this, "failed to initialize automated field", "setting default");
			integrity = false;
		}
		
		return integrity;
		
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#duplicate()
	 */
	@Override
	public Building blueprint() {
		
		ArrayList<Transaction> transactions = new ArrayList<Transaction>();
		for (Transaction transaction : getTransactions()) {
			transactions.add(transaction.duplicate());
		}		
		
		return new TradingPost(currency, new Hashtable<Material, Integer>(stockedItems), new ArrayList<TradeSign>(), transactions);
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#enable()
	 */
	@Override
	public void enable() {
		
		super.enable();

		// Register clock:
		Clock.clock().registerTimeOfDayTick(this);
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#disable()
	 */
	@Override
	public void disable() {

		super.disable();

		// Register clock:
		Clock.clock().unregisterTimeOfDayTick(this);
		
	}
	
	
	// Interaction:
	/**
	 * Gets the static name.
	 * 
	 * @return static name
	 */
	public String getStaticName() {
		return TradingPost.class.getSimpleName().replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2").toLowerCase();
	}
	
	/**
	 * Adds a trade sign.
	 * 
	 * @param tradeSign trade sign
	 */
	private void addTradeSign(TradeSign tradeSign) {

		
		if(tradeSigns.contains(tradeSign)){
			Saga.severe(this, "tried to add an already existing trade sign", "ignoring request");
			return;
		}
		tradeSigns.add(tradeSign);
		
		
	}
	
	/**
	 * Removes a trade sign.
	 * 
	 * @param tradeSign trade sign
	 */
	private void removeTradeSign(TradeSign tradeSign) {

		
		if(!tradeSigns.contains(tradeSign)){
			Saga.severe(this, "tried to remove a non-existing trade sign", "ignoring request");
			return;
		}
		tradeSigns.remove(tradeSign);
		
		
	}
	
	/**
	 * Gets a trade sign at given location.
	 * 
	 * @param location location
	 * @return trade sign, null if not found
	 */
	private TradeSign tradeSignAt(Location location) {

		
		for (int i = 0; i < tradeSigns.size(); i++) {
			if(tradeSigns.get(i).getLocation().equals(location)){
				return tradeSigns.get(i);
			}
		}
		return null;
		
		
	}
	
	/**
	 * Gets all trade signs.
	 * 
	 * @return trade signs
	 */
	public ArrayList<TradeSign> getTradeSigns() {
		return new ArrayList<TradeSign>(tradeSigns);
	}
	
	/**
	 * Cents the signs of the given type.
	 * 
	 * @param type type
	 * @return sign count
	 */
	public int tradeSignCount(TransactionType type) {
		
		int count = 0;
		for (int i = 0; i < tradeSigns.size(); i++) {
			if(tradeSigns.get(i).getType().equals(type)) count++;
		}
		return count;
		
	}
	
	/**
	 * Adds a transaction.
	 * 
	 * @param transaction transaction
	 */
	public void addTransaction(Transaction transaction) {
		
		// Remove previous one:
		for (int i = 0; i < tradeTransactions.size(); i++) {
			Transaction oldTransaction = tradeTransactions.get(i);
			if(oldTransaction.getMaterial().equals(transaction.getMaterial()) && oldTransaction.getType().equals(transaction.getType())){
				tradeTransactions.remove(i);
				i--;
			}
		}
		tradeTransactions.add(transaction);
		
		// Refresh signs:
		for (int i = 0; i < tradeSigns.size(); i++) {
			tradeSigns.get(i).refresh();
		}
		
	}
	
	/**
	 * Removes a transaction.
	 * 
	 * @param type transaction type
	 * @param material material
	 */
	public void removeTransaction(TransactionType type, Material material) {
		
		// Remove previous one:
		for (int i = 0; i < tradeTransactions.size(); i++) {
			Transaction oldTransaction = tradeTransactions.get(i);
			if(oldTransaction.getMaterial().equals(material) && oldTransaction.getType().equals(type)){
				tradeTransactions.remove(i);
				i--;
			}
		}
		
		// Refresh signs:
		for (int i = 0; i < tradeSigns.size(); i++) {
			tradeSigns.get(i).refresh();
		}
		
	}
	
	/**
	 * Check if there is a transaction with given parameters.
	 * 
	 * @param type type
	 * @param material material
	 * @return true if there is a transaction with given parameters
	 */
	public boolean hasTransaction(TransactionType type, Material material) {
		
		for (int i = 0; i < tradeTransactions.size(); i++) {
			if( tradeTransactions.get(i).getType().equals(type) && (tradeTransactions.get(i).getMaterial().equals(material)) ){
				return true;
			}
		}
		return false;
		
	}
	
	/**
	 * Gets the transaction for the given sign.
	 * 
	 * @param type transaction type
	 * @param material material
	 * @return transaction, null if not found
	 */
	private Transaction findTransaction(TransactionType type, Material material) {

		
		ArrayList<Transaction> transactions = getTransactions();
		for (int i = 0; i < transactions.size(); i++) {
			if( transactions.get(i).getMaterial().equals(material) && transactions.get(i).getType().equals(type)){
				return transactions.get(i);
			}
		}
		return null;
		
		
	}
	
	/**
	 * Refreshes all signs.
	 * 
	 */
	private void refreshSigns() {

		for (int i = 0; i < tradeSigns.size(); i++) {
			tradeSigns.get(i).refresh();
		}
		
	}
	
	/**
	 * Checks if the sign is available.
	 * 
	 * @param type type
	 * @param material material
	 * @return true if available
	 */
	private boolean hasSign(TransactionType type, Material material) {
		
		for (int i = 0; i < tradeSigns.size(); i++) {
			if(tradeSigns.get(i).getType().equals(type) && tradeSigns.get(i).getMaterial().equals(material)){
				return true;
			}
		}
		return false;
		
	}
	
	/**
	 * Stocked items.
	 * 
	 * @return stocked items
	 */
	public Hashtable<Material, Integer> getStockedItems() {
		return new Hashtable<Material, Integer>(stockedItems);
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.TimeOfDayTicker#timeOfDayTick(org.saga.Clock.TimeOfDayTicker.TimeOfDay)
	 */
	@Override
	public void timeOfDayTick(TimeOfDay timeOfDay) {

		
		if(!isEnabled()){
			return;
		}
		
		// Trade deals at sunrise:
		if(timeOfDay.equals(TimeOfDay.SUNRISE)){
			doTradeDeal();
		}
		
		// Automatic deals at midday:
		if(isAutomated() && timeOfDay.equals(TimeOfDay.MIDDAY)){
			automaticTradeDeals();
		}
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.TimeOfDayTicker#checkWorld(java.lang.String)
	 */
	@Override
	public boolean checkWorld(String worldName) {

		
		SagaChunk sagaChunk = getSagaChunk();		
		if(sagaChunk == null){
			return false;
		}
		
		return sagaChunk.getWorldName().equals(worldName);
		
		
	}
	
	/**
	 * Checks if the trading post is automated.
	 * 
	 * @return true if automated
	 */
	public Boolean isAutomated() {
		return automated;
	}
	
	/**
	 * Sets if the trading post is automated.
	 * 
	 * @param automated true if automated
	 */
	public void setAutomated(Boolean automated) {
		this.automated = automated;
	}
	
	// Stocked:
	/**
	 * Gets the transactions sorted, based on the value of items stored.
	 * 
	 * @param type transaction type
	 * @return transactions sorted, based on stored value
	 */
	private ArrayList<Transaction> getValueSortedTransactions(final TransactionType type) {

		
		// Custom comparator:
		Comparator<Transaction> transacactionComparator = new Comparator<Transaction>() {
			
			@Override
			public int compare(Transaction o1, Transaction o2) {
				
				Integer o1Amount= getItemCount(o1.getMaterial());
				Integer o2Amount= getItemCount(o2.getMaterial());
				
				Double o1Value = o1.getValue();
				Double o2Value = o2.getValue();
				
				if(type.equals(TransactionType.BUY)){
					return new Double(o1Amount*o1Value - o2Amount*o2Value).intValue();
				}else{
					return new Double(o2Amount*o2Value - o1Amount*o1Value).intValue();
				}
				
				

			}
			
		};
		
		// Filter transactions:
		ArrayList<Transaction> sorted = new ArrayList<Transaction>();
		for (int i = 0; i < tradeTransactions.size(); i++) {
			if(tradeTransactions.get(i).getType().equals(type)) sorted.add(tradeTransactions.get(i));
		}
		
		// Sort:
		Collections.sort(sorted, transacactionComparator);
		
		return sorted;
		
		
	}

	
	// Trade deals:
	/**
	 * Does a trade deal.
	 * 
	 */
	private void doTradeDeal() {

		
		Double currencyReport = 0.0;
		Hashtable<Material, Integer> importReport = new Hashtable<Material, Integer>();
		Hashtable<Material, Integer> exportReport = new Hashtable<Material, Integer>();
		boolean refreshSigns = false;
		
		ArrayList<TradeDeal> tradeDeals = getTradeDeals();
		
		// Trade deal transactions:
		for (int i = 0; i < tradeDeals.size(); i++) {
			
			TradeDeal tradeDeal = tradeDeals.get(i);
			
			// Next day:
			tradeDeal.nextDay();
			
			// Export:
			if(tradeDeal.getType().equals(TradeDealType.EXPORT)){
				
				if(canDoTradeDeal(tradeDeal)) {
					
					tradeDeal.doTransaction();
					removeItem(new ItemStack(tradeDeal.getMaterial(), tradeDeal.getAmount()));
					addCoins(tradeDeal.getTotalValue());
					
					Integer amount = exportReport.get(tradeDeal.getMaterial());
					if(amount == null) amount = 0;
					
					amount += tradeDeal.getAmount();
					exportReport.put(tradeDeal.getMaterial(), amount);
					currencyReport += tradeDeal.getTotalValue();
					
					refreshSigns = true;
					
				}
				
			}
			
			// Import:
			else if(tradeDeal.getType().equals(TradeDealType.IMPORT)){
				
				if(canDoTradeDeal(tradeDeal)) {
					
					tradeDeal.doTransaction();
					removeCoins(tradeDeal.getTotalValue());
					addItem(new ItemStack(tradeDeal.getMaterial(), tradeDeal.getAmount()));

					Integer amount = importReport.get(tradeDeal.getMaterial());
					if(amount == null) amount = 0;
					
					amount += tradeDeal.getAmount();
					importReport.put(tradeDeal.getMaterial(), amount);
					currencyReport -= tradeDeal.getTotalValue();
					
					refreshSigns = true;
					
				}
				
			}else{
				Saga.warning(this, "found invalid trade deal type for " + tradeDeal + " trade deal for doTradeDeal()", "ignoring request");
			}
			
			// Check for completion:
			if(tradeDeal.getTransactionsLeft() <= 0){
				try {
					removeTradeDeal(tradeDeal.getId());
					refreshSigns = true;
				} catch (TradeDealNotFoundException e) {
					Saga.severe(this, "failed to remove a non-existing trading deal with " + tradeDeal.getId(), "ignoring request");
				}
				if(originChunk != null){
					originChunk.getChunkGroup().broadcast(EconomyMessages.completedTradeDealBroadcast(tradeDeal));
				}
			}else 

			// Check for expiration:
			if(tradeDeal.getDaysLeft() <= 0){
				try {
					removeTradeDeal(tradeDeal.getId());
					refreshSigns = true;
				} catch (TradeDealNotFoundException e) {
					Saga.severe(this, "failed to remove a non-existing trading deal with " + tradeDeal.getId(), "ignoring request");
				}
				if(originChunk != null){
					originChunk.getChunkGroup().broadcast(EconomyMessages.expiredTradeDealBroadcast(tradeDeal));
				}
			}
			
			
		}
		
		// Refresh signs:
		if(refreshSigns){
			refreshSigns();
		}
		
		// Inform:
		if(originChunk != null){
			originChunk.getChunkGroup().broadcast(EconomyMessages.reportTradeDealBroadcast(getName(), currencyReport, importReport, exportReport));
		}
		
		
	}

	/**
	 * Checks if the trade deal can be done.
	 * 
	 * @param tradeDeal trade deal
	 * @return true if can be done
	 */
	private boolean canDoTradeDeal(TradeDeal tradeDeal){
		
		
		// Export:
		if(tradeDeal.getType().equals(TradeDealType.EXPORT)){
			
			// Not enough material:
			if(tradeDeal.getAmount() > getItemCount(tradeDeal.getMaterial())){
				return false;
			}
			
			// Reserved:
			if(getItemCount(tradeDeal.getMaterial()) - tradeDeal.getAmount() < getReservedAmount(tradeDeal.getMaterial())){
				return false;
			}
			
			return true;
			
		}
		

		// Import:
		if(tradeDeal.getType().equals(TradeDealType.IMPORT)){
			
			// Not enough currency:
			if(tradeDeal.getTotalValue() > getCoins()){
				return false;
			}
			
			// Reserved:
			if( getCoins() - getReservedCurrency() < tradeDeal.getTotalValue() ){
				return false;
			}
			
			return true;
			
		}
		
		return false;
		
		
	}
	
	/**
	 * Gets the maximum allowed trade deal count.
	 * 
	 * @return allowed trade deals
	 */
	public Integer getTradeDealsMaximumAmount() {
		
		TwoPointFunction tradeDeals = getDefinition().getLevelFunction();
		
		// Not high enough level:
		if(tradeDeals.getXRequired() < getLevel()){
			return 0;
		}
		
		return tradeDeals.calculateValue(getLevel()).intValue();
				
	}
	
	/**
	 * Gets the amount of trade deals.
	 * 
	 * @param type trade deal type
	 * @return amount of trade deals
	 */
	public Integer getTradeDealsAmount(TradeDealType type) {
		
		int count = 0;
		
		for (int i = 0; i < tradeDeals.size(); i++) {
			if(tradeDeals.get(i).getType().equals(type)) count++;
		}
		
		return count;
				
	}
	
	/**
	 * Does the automatic trade deals.
	 * 
	 */
	private void automaticTradeDeals() {

		
		SagaChunk sagaChunk = getSagaChunk();
		if(sagaChunk == null){
			return;
		}
		String worldName = sagaChunk.getWorldName();
		
		int newImports = new Double(Math.floor(getTradeDealsMaximumAmount()/2.0) - getTradeDealsAmount(TradeDealType.IMPORT) ).intValue();
		
		ArrayList<Transaction> sortedExport = getValueSortedTransactions(TransactionType.SELL);
		ArrayList<Transaction> sortedImport = getValueSortedTransactions(TransactionType.BUY);
		
		EconomyManager manager = null;
		
		try {
			manager = EconomyManager.manager(worldName);
		} catch (InvalidWorldException e) {
			Saga.severe(this, "failed to proceed with a new trade deal, invalid world " + worldName, "ignoring trade deal");
			return;
		}
		
		// Import:
		for (int i = 0; i < newImports; i++) {
			
			ArrayList<Transaction> sortedTransactions = sortedImport;
			TradeDealType dealType = TradeDealType.IMPORT;
			
			TradeDeal tradeDeal = null;
			
			for (Transaction transaction : sortedTransactions) {
				
				tradeDeal = manager.findTradeDeal(dealType, transaction.getMaterial());
				
				// No trade deal:
				if(tradeDeal == null) continue;
				
				// Check deal:
				if(checkDeal(tradeDeal, transaction)){
					break;
				}else{
					tradeDeal = null;
				}
				
			}
			
			// Not found:
			if(tradeDeal == null){
				continue;
			}
			
			// Form:
			try {
				tradeDeal = manager.takeTradeDeal(tradeDeal.getId());
			} catch (TradeDealNotFoundException e) {
				Saga.severe(this, "failed to form a trade deal, beacause the id wasn't found", "ignoring id");
				continue;
			}
			addTradeDeal(tradeDeal);
			
			// Inform:
			ChunkGroup chunkGroup = getChunkGroup();
			if(chunkGroup != null) chunkGroup.broadcast(EconomyMessages.broadcastTradeDealFormation(tradeDeal));
			
			
		}
		
		// Fill all remaining slots with exports:
		int newExports = getTradeDealsMaximumAmount() - getTradeDealsAmount(TradeDealType.EXPORT);
		
		// Export:
		for (int i = 0; i < newExports; i++) {
			
			ArrayList<Transaction> sortedTransactions = sortedExport;
			TradeDealType dealType = TradeDealType.EXPORT;
			
			TradeDeal tradeDeal = null;
			
			for (Transaction transaction : sortedTransactions) {
				
				tradeDeal = manager.findTradeDeal(dealType, transaction.getMaterial());
				
				// No trade deal:
				if(tradeDeal == null) continue;
				
				// Check deal:
				if(checkDeal(tradeDeal, transaction)){
					break;
				}else{
					tradeDeal = null;
				}
				
			}
			
			// Not found:
			if(tradeDeal == null){
				continue;
			}
			
			// Form:
			try {
				tradeDeal = manager.takeTradeDeal(tradeDeal.getId());
			} catch (TradeDealNotFoundException e) {
				Saga.severe(this, "failed to form a trade deal, beacause the id wasn't found", "ignoring id");
				continue;
			}
			addTradeDeal(tradeDeal);
			
			// Inform:
			ChunkGroup chunkGroup = getChunkGroup();
			if(chunkGroup != null) chunkGroup.broadcast(EconomyMessages.broadcastTradeDealFormation(tradeDeal));
			
			
		}
		
		
		
	}
	
	/**
	 * Checks if the automatic deal is good enough.
	 * 
	 * @param tradeDeal trade deal
	 * @param transaction transaction
	 * @return true if good enough
	 */
	private boolean checkDeal(TradeDeal tradeDeal, Transaction transaction) {

		
		// Export:
		if(tradeDeal.getType().equals(TradeDealType.EXPORT)){
			
			// Enough materials:
			if(tradeDeal.getAmount() + getReservedAmount(tradeDeal.getMaterial()) > getItemCount(tradeDeal.getMaterial())){
				return false;
			}
			
			// Bad deal:
			if(tradeDeal.getValue() < transaction.getValue()){
				return false;
			}
			
			return true;
			
		}else if(tradeDeal.getType().equals(TradeDealType.IMPORT)){
			
			// Enough currency:
			if(tradeDeal.getTotalValue() + getReservedCurrency() < getCoins()){
				return false;
			}

			// Bad deal:
			if(tradeDeal.getValue() > transaction.getValue()){
				return false;
			}
			
			return true;
			
		}
		
		return false;
		
	}
	
	/**
	 * Gets the amount of trade deals.
	 * 
	 * @return amount of trade deals
	 */
	public Integer getTradeDealsAmount() {
		return tradeDeals.size();
	}

	/**
	 * Adds a trade deal.
	 * 
	 * @param deal trade deal
	 */
	public void addTradeDeal(TradeDeal deal) {


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
	 * Removes a trade deal from the list.
	 * 
	 * @param id trade deal id
	 * @return removed trade deal
	 * @throws TradeDealNotFoundException if the deal with the given ID doesn't exist
	 */
	public TradeDeal removeTradeDeal(Integer id) throws TradeDealNotFoundException {

		for (int i = 0; i < tradeDeals.size(); i++) {
			
			if(tradeDeals.get(i).getId().equals(id)){
				TradeDeal tradeDeal = tradeDeals.get(i);
				tradeDeals.remove(i);
				return tradeDeal;
			}
			
		}
		throw new TradeDealNotFoundException(id);
		
	}

	
	// Reserved:
	/**
	 * Sets the amount of reserved material.
	 * 
	 * @param material material
	 * @param amount amount
	 */
	public void setReserved(Material material, Integer amount) {
		
		
		if(amount == 0){
			exportLimit.remove(material);
		}else{
			exportLimit.put(material, amount);
		}
		
		
	}
	
	/**
	 * Sets the amount of reserved currency.
	 * 
	 * @param material material
	 * @param amount amount
	 */
	public void setReserved(Double amount) {
		
		
		if(amount < 0){
			importLimit = 0.0;
		}else{
			importLimit = amount;
		}
		
		
	}
	
	/**
	 * Gets all the materials that are reserved.
	 * 
	 * @return reserved materials
	 */
	public ArrayList<Material> getReservedMaterials() {

		
		ArrayList<Material> materials = new ArrayList<Material>();
		Enumeration<Material> kMaterials = exportLimit.keys();
		while (kMaterials.hasMoreElements()) {
			Material material = (Material) kMaterials.nextElement();
			materials.add(material);
		}
		
		return materials;
		
		
	}
	
	/**
	 * Gets the reserved material amount.
	 * 
	 * @param material material
	 * @return reserved amount, 0 if not reserved
	 */
	public Integer getReservedAmount(Material material) {

		Integer amount = exportLimit.get(material);
		if(amount == null) amount = 0;
		
		return amount;
		
	}
	
	/**
	 * Gets the reserved currency.
	 * 
	 * @return reserved currency, 0 if not reserved
	 */
	public Double getReservedCurrency() {

		Double amount = importLimit;
		if(amount < 0) amount = 0.0;
		
		return amount;
		
	}
	

	// Trader:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#getTradeName()
	 */
	@Override
	public String getTradingName() {
		
		String rString = getDisplayName();
		
		SagaChunk sagaChunk = getSagaChunk();
		ChunkGroup chunkGroup = null;
		if(sagaChunk != null){
			chunkGroup = sagaChunk.getChunkGroup();
		}
		if(chunkGroup != null){
			rString = chunkGroup.getName() + " " + rString;
		}
		return rString;
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#isActive(saga.economy.EconomyManager.TransactionType, org.bukkit.Material)
	 */
	@Override
	public boolean isActive(TransactionType type, Material material) {
		
		Transaction transaction = findTransaction(type, material);
		
		if(!hasSign(type, material) || transaction == null){
			return false;
		}
		
		if(type.equals(TransactionType.SELL)){
			
			return transaction.getTotalValue() <= getCoins();
			
		}else if(type.equals(TransactionType.BUY)){
			
			return transaction.getAmount() <= getItemCount(material);
			
		}
		
		return  false;
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#addCurrency(java.lang.Double)
	 */
	@Override
	public void addCoins(Double amount) {
		
		currency += amount;
		
		refreshSigns();
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#removeCurrency(java.lang.Double)
	 */
	@Override
	public void removeCoins(Double amount) {
		
		currency -= amount;
		
		refreshSigns();
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#getCurrency()
	 */
	@Override
	public Double getCoins() {
		return currency;
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#getItemCount(org.bukkit.Material)
	 */
	@Override
	public Integer getItemCount(Material material) {
		
		Integer count = stockedItems.get(material);
		if(count == null) count = 0;
		return count;
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#addItem(org.bukkit.inventory.ItemStack)
	 */
	@Override
	public void addItem(ItemStack itemStack) {
		
		Integer count = stockedItems.get(itemStack.getType());
		if(count == null) count = 0;
		
		count += itemStack.getAmount();
		
		stockedItems.put(itemStack.getType(), count);
		
		refreshSigns();
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#removeItemStack(org.bukkit.inventory.ItemStack)
	 */
	@Override
	public void removeItem(ItemStack itemStack) {
		

		Integer count = stockedItems.get(itemStack.getType());
		if(count == null) count = 0;
		
		count -= itemStack.getAmount();
		
		if(count != 0){
			stockedItems.put(itemStack.getType(), count);
		}else{
			stockedItems.remove(itemStack.getType());
		}
		
		refreshSigns();
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#getBuyItemValues()
	 */
	@Override
	public ArrayList<Transaction> getTransactions() {
		return new ArrayList<Transaction>(tradeTransactions);
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#getTradeDeals()
	 */
	@Override
	public ArrayList<TradeDeal> getTradeDeals() {
		return new ArrayList<TradeDeal>(tradeDeals);
	}

	
	// Events:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent, org.saga.SagaPlayer)
	 */
	@Override
	public void onPlayerInteract(PlayerInteractEvent event, SagaPlayer sagaPlayer) {
		

		// Canceled:
		if(event.isCancelled()){
			return;
		}
		
		Block targetBlock = event.getClickedBlock();
    	
		// Invalid:
		if(targetBlock == null){
    		return;
    	}
		
		// Find trade sign:
		TradeSign tradeSign = tradeSignAt(targetBlock.getLocation());
		
		// Not a trade sign:
		if(tradeSign == null){
			return;
		}
//		
//		// Permission:
//		if(!canUse(sagaPlayer)){
//			sagaPlayer.sendMessage(SagaMessages.noPermission());
//			return;
//		}
//		
		
		// Forward to sign:
		tradeSign.onPlayerInteract(sagaPlayer, event);
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onSignChange(org.bukkit.event.block.SignChangeEvent, org.saga.SagaPlayer)
	 */
	@Override
	public void onSignChange(SignChangeEvent event, SagaPlayer sagaPlayer) {
		

		// Canceled:
		if(event.isCancelled()){
			return;
		}
		
		// Permission:
		if(!checkBuildingPermission(sagaPlayer, BuildingPermission.LOW)){
			sagaPlayer.message(SagaMessages.noPermission(this));
			event.setCancelled(true);
			return;
		}
		
		// Valid sign:
		TransactionType transactionType = null;
		if(TradeSign.isSellSign(event.getLine(0))){
			transactionType = TransactionType.SELL;
		}
		
		if(TradeSign.isBuySign(event.getLine(0))){
			transactionType = TransactionType.BUY;
		}
		
		if(transactionType == null){
			return;
		}
		
		Block targetBlock = event.getBlock();

		// Sign:
		if(!(targetBlock.getState() instanceof Sign)){
			return;
		}
		Sign sign = (Sign) targetBlock.getState();

		// Take control of the event:
		event.setCancelled(true);
		
		// Create and add sign:
		TradeSign tradeSign = TradeSign.create(transactionType, sign, this, event);
		addTradeSign(tradeSign);
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onBlockPlace(org.bukkit.event.block.BlockPlaceEvent, org.saga.SagaPlayer)
	 */
	@Override
	public void onBlockPlace(BlockPlaceEvent event, SagaPlayer sagaPlayer) {
		

		// Canceled:
		if(event.isCancelled()){
			return;
		}

		// Building permission:
		if(!super.canBuild(sagaPlayer)){
			event.setCancelled(true);
			sagaPlayer.message(SagaMessages.noPermission(this));
			return;
		}
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onBlockBreak(org.bukkit.event.block.BlockBreakEvent, org.saga.SagaPlayer)
	 */
	@Override
	public void onBlockBreak(BlockBreakEvent event, SagaPlayer sagaPlayer) {


		// Canceled:
		if(event.isCancelled()){
			return;
		}
		
		// Building permission:
		if(!super.canBuild(sagaPlayer)){
			event.setCancelled(true);
			sagaPlayer.message(SagaMessages.noPermission(this));
			return;
		}
		
		Block targetBlock = event.getBlock();

		// Sign:
		if(!(targetBlock.getState() instanceof Sign)){
			return;
		}
		
		// Trade sign:
		TradeSign tradeSign = tradeSignAt(targetBlock.getLocation());
		if(tradeSign == null){
			return;
		}

		// Delete and remove sign:
		tradeSign.remove();
		removeTradeSign(tradeSign);
		
		
	}
	
	
	// Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#toString()
	 */
	@Override
	public String toString() {
		return super.toString();
	}

	
	// Commands:
	@Command(
			aliases = {"bsetsell", "setsell"},
			usage = "<item> <amount> <value>",
			flags = "",
			desc = "Sets up an item for sell in a tradin post building.",
			min = 3,
			max = 3
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.setsell"})
	public static void setSell(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		// Retrieve building:
		TradingPost selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
		
		// Permission:
		if(!selectedBuilding.checkBuildingPermission(sagaPlayer, BuildingPermission.HIGH)){
			sagaPlayer.message(SagaMessages.noPermission(selectedBuilding));
			return;
		}
		
		// Material:
		Material material = Material.matchMaterial(args.getString(0));
		if(material == null){
			sagaPlayer.message( EconomyMessages.invalidMaterial(args.getString(0)) );
			return;
		}
		
		// Amount and value:
		Integer amount;
		Double value;
		try {
			amount = Integer.parseInt(args.getString(1));
		} catch (NumberFormatException e) {
			sagaPlayer.message(EconomyMessages.invalidAmount(args.getString(1)));
			return;
		}
		try {
			value = Double.parseDouble(args.getString(2));
		} catch (NumberFormatException e) {
			sagaPlayer.message(EconomyMessages.invalidAmount(args.getString(2)));
			return;
		}
		
		Transaction transaction = new Transaction(TransactionType.SELL, material, amount, value);
		
		// Add transaction:
		selectedBuilding.addTransaction(transaction);
		
		// Inform:
		SagaChunk sagaChunk = selectedBuilding.getSagaChunk();
		ChunkGroup chunkGroup = null;
		if(sagaChunk != null){
			chunkGroup = sagaChunk.getChunkGroup();
		}
		if(chunkGroup != null){
			chunkGroup.broadcast(EconomyMessages.addedTransactionBroadcast(transaction, chunkGroup, sagaPlayer));
		}
		
		
	}

	@Command(
			aliases = {"bremovesell", "removesell"},
			usage = "<item> <amount> <value>",
			flags = "",
			desc = "Removes an item for sell in a tradin post building.",
			min = 1,
			max = 1
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.removesell"})
	public static void removeSell(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		// Retrieve building:
		TradingPost selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
		
		// Permission:
		if(!selectedBuilding.checkBuildingPermission(sagaPlayer, BuildingPermission.HIGH)){
			sagaPlayer.message(SagaMessages.noPermission(selectedBuilding));
			return;
		}
		
		// Material:
		Material material = Material.matchMaterial(args.getString(0));
		if(material == null){
			sagaPlayer.message(EconomyMessages.invalidMaterial(args.getString(0)));
			return;
		}
		
		// Check if exists:
		if(!selectedBuilding.hasTransaction(TransactionType.SELL, material)){
			sagaPlayer.message(EconomyMessages.nonexistantTransaction(TransactionType.SELL, material));
			return;
		}
		
		// Remove:
		selectedBuilding.removeTransaction(TransactionType.SELL, material);
		
		// Inform:
		SagaChunk sagaChunk = selectedBuilding.getSagaChunk();
		ChunkGroup chunkGroup = null;
		if(sagaChunk != null){
			chunkGroup = sagaChunk.getChunkGroup();
		}
		if(chunkGroup != null){
			chunkGroup.broadcast(EconomyMessages.removedTransactionBroadcast(TransactionType.SELL, material, chunkGroup, sagaPlayer));
		}
		
		
	}

	@Command(
			aliases = {"bsetbuy", "setbuy"},
			usage = "<item> <amount> <value>",
			flags = "",
			desc = "Sets up an item to buy in a tradin post building.",
			min = 3,
			max = 3
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.setbuy"})
	public static void setBuy(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		// Retrieve building:
		TradingPost selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
		
		// Permission:
		if(!selectedBuilding.checkBuildingPermission(sagaPlayer, BuildingPermission.HIGH)){
			sagaPlayer.message(SagaMessages.noPermission(selectedBuilding));
			return;
		}
		
		// Material:
		Material material = Material.matchMaterial(args.getString(0));
		if(material == null){
			sagaPlayer.message( EconomyMessages.invalidMaterial(args.getString(0)) );
			return;
		}
		
		// Amount and value:
		Integer amount;
		Double value;
		try {
			amount = Integer.parseInt(args.getString(1));
		} catch (NumberFormatException e) {
			sagaPlayer.message(EconomyMessages.invalidAmount(args.getString(1)));
			return;
		}
		try {
			value = Double.parseDouble(args.getString(2));
		} catch (NumberFormatException e) {
			sagaPlayer.message(EconomyMessages.invalidAmount(args.getString(2)));
			return;
		}
		
		Transaction transaction = new Transaction(TransactionType.BUY, material, amount, value);
		
		// Add transaction:
		selectedBuilding.addTransaction(transaction);
		
		// Inform:
		SagaChunk sagaChunk = selectedBuilding.getSagaChunk();
		ChunkGroup chunkGroup = null;
		if(sagaChunk != null){
			chunkGroup = sagaChunk.getChunkGroup();
		}
		if(chunkGroup != null){
			chunkGroup.broadcast(EconomyMessages.addedTransactionBroadcast(transaction, chunkGroup, sagaPlayer));
		}
		
		
	}

	@Command(
			aliases = {"bremovebuy", "removebuy"},
			usage = "<item> <amount> <value>",
			flags = "",
			desc = "Removes an item to buy in a tradin post building.",
			min = 1,
			max = 1
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.removebuy"})
	public static void removeBuy(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		// Retrieve building:
		TradingPost selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
		
		// Permission:
		if(!selectedBuilding.checkBuildingPermission(sagaPlayer, BuildingPermission.HIGH)){
			sagaPlayer.message(SagaMessages.noPermission(selectedBuilding));
			return;
		}
		
		// Material:
		Material material = Material.matchMaterial(args.getString(0));
		if(material == null){
			sagaPlayer.message(EconomyMessages.invalidMaterial(args.getString(0)));
			return;
		}
		
		// Check if exists:
		if(!selectedBuilding.hasTransaction(TransactionType.BUY, material)){
			sagaPlayer.message(EconomyMessages.nonexistantTransaction(TransactionType.BUY, material));
			return;
		}
		
		// Remove:
		selectedBuilding.removeTransaction(TransactionType.BUY, material);
		
		// Inform:
		SagaChunk sagaChunk = selectedBuilding.getSagaChunk();
		ChunkGroup chunkGroup = null;
		if(sagaChunk != null){
			chunkGroup = sagaChunk.getChunkGroup();
		}
		if(chunkGroup != null){
			chunkGroup.broadcast(EconomyMessages.removedTransactionBroadcast(TransactionType.BUY, material, chunkGroup, sagaPlayer));
		}
		
		
	}
	
	@Command(
			aliases = {"bdonate", "donate"},
			usage = "",
			flags = "",
			desc = "Donates an item held in hand to the tading post.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.donate"})
	public static void donate(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		// Retrieve building:
		TradingPost selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
		
		// Permission:
		if(!selectedBuilding.checkBuildingPermission(sagaPlayer, BuildingPermission.LOW)){
			sagaPlayer.message(SagaMessages.noPermission(selectedBuilding));
			return;
		}
		
		// Item:
		ItemStack item = sagaPlayer.getItemInHand();
		if(item == null) return;

		// Damaged:
		if(item.getDurability() > 0){
			sagaPlayer.message(cantDonateDamaged());
			return;
		}
		
		// Enchanted:
		if(item.getEnchantments().size() > 0){
			sagaPlayer.message(cantDonateEnchanted());
			return;
		}
		
		// Remove:
		item = sagaPlayer.removeItemInHand();
		if(item.getType().equals(Material.AIR)){
			sagaPlayer.message(EconomyMessages.nothingInHand());
			return;
		}

		// Put:
		selectedBuilding.addItem(item);
		
		// Inform:
		SagaChunk sagaChunk = selectedBuilding.getSagaChunk();
		if(sagaChunk != null){
			sagaChunk.getChunkGroup().broadcast(EconomyMessages.donatedItemsBroadcast(item, selectedBuilding, sagaPlayer));
		}
		
		
	}
	
	@Command(
			aliases = {"bdonateall","donateall"},
			usage = "<item>",
			flags = "",
			desc = "Donates all items of the give type.",
			min = 1,
			max = 1
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.donate"})
	public static void donateAll(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		// Retrieve building:
		TradingPost selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
		
		// Permission:
		if(!selectedBuilding.checkBuildingPermission(sagaPlayer, BuildingPermission.LOW)){
			sagaPlayer.message(SagaMessages.noPermission(selectedBuilding));
			return;
		}
		
		// Arguments:
		Material material = Material.matchMaterial(args.getString(0));
		if(material == null || material.equals(Material.AIR)){
			sagaPlayer.message( EconomyMessages.invalidMaterial(args.getString(0)) );
			return;
		}
		
		// Add items:
		int amount = 0;
		for (int i = 0; i < sagaPlayer.getInventorySize(); i++) {
			
			ItemStack item = sagaPlayer.getInventoryItem(i);
			
			if(item.getType().equals(material) && item.getDurability() == 0 && item.getEnchantments().size() == 0){

				amount += item.getAmount();
				selectedBuilding.addItem(item);
				sagaPlayer.removeInventoryItem(i);
				
			}
			
		}
		
		// Inform:
		SagaChunk sagaChunk = selectedBuilding.getSagaChunk();
		if(sagaChunk != null){
			sagaChunk.getChunkGroup().broadcast(EconomyMessages.donatedItemsBroadcast(new ItemStack(material, amount), selectedBuilding, sagaPlayer));
		}
		
		// Refresh signs:
		selectedBuilding.refreshSigns();
		
	}
	
	@Command(
			aliases = {"bdonatecurrency", "donatec"},
			usage = "<amount>",
			flags = "",
			desc = "Donates currency to the tading post.",
			min = 1,
			max = 1
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.donate"})
	public static void donateCurrency(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		// Retrieve building:
		TradingPost selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
		
		// Permission:
		if(!selectedBuilding.checkBuildingPermission(sagaPlayer, BuildingPermission.LOW)){
			sagaPlayer.message(SagaMessages.noPermission(selectedBuilding));
			return;
		}
		
		// Amount:
		Double amount = null;
		try {
			amount = Double.parseDouble(args.getString(0));
		} catch (NumberFormatException e) {
			sagaPlayer.message(EconomyMessages.invalidAmount(args.getString(0)));
			return;
		}
		
		// Check if enough:
		if(sagaPlayer.getCoins() < amount){
			sagaPlayer.message(EconomyMessages.notEnoughCoins());
			return;
		}
		
		// Remove:
		sagaPlayer.removeCoins(amount);

		// Put:
		selectedBuilding.addCoins(amount);
		
		// Inform:
		SagaChunk sagaChunk = selectedBuilding.getSagaChunk();
		if(sagaChunk != null){
			sagaChunk.getChunkGroup().broadcast(EconomyMessages.donatedCurrencyBroadcast(amount, selectedBuilding, sagaPlayer));
		}
		
		
	}
	
	@Command(
			aliases = {"btradingpost", "tpost"},
			usage = "",
			flags = "",
			desc = "Information about the trading post building.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.user.building.tradingpost.put"})
	public static void tradingPost(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		// Retrieve building:
		TradingPost selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
		
		// Inform:
		sagaPlayer.message(EconomyMessages.tradingpost(selectedBuilding));
		
		
	}

	@Command(
			aliases = {"bnewdeal", "newdeal"},
			usage = "<trade deal ID>",
			flags = "",
			desc = "Forms a new trading deal for the settlement.",
			min = 1,
			max = 1
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.formdeal"})
	public static void formTradeDeal(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

		// Retrieve building:
		TradingPost selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
		
		// Permission:
		if(!selectedBuilding.checkBuildingPermission(sagaPlayer, BuildingPermission.MEDIUM)){
			sagaPlayer.message(SagaMessages.noPermission(selectedBuilding));
			return;
		}
		
		// Retrieve manager:
		EconomyManager manager = null;
		try {
			manager = EconomyManager.manager(sagaPlayer.getLocation());
		} catch (InvalidWorldException e1) {
			Saga.severe(EconomyCommands.class, "failed to retrieve " + EconomyManager.class.getSimpleName() + ": " + e1.getClass().getSimpleName() + ":" + e1.getMessage() + ".", "ignoring command");
			sagaPlayer.error("failed to retrieve " + EconomyManager.class.getSimpleName());
		}
		
		// Check existing trade deals:
		if(selectedBuilding.getTradeDealsAmount() >= selectedBuilding.getTradeDealsMaximumAmount()){
			sagaPlayer.message(EconomyMessages.tradeDealLimitReached(selectedBuilding));
			return;
		}
		
		// Check ID:
		Integer id;
		try {
			id = Integer.parseInt(args.getString(0));
		} catch (NumberFormatException e) {
			sagaPlayer.message(EconomyMessages.invalidId(args.getString(0)));
			return;
		}
		
		// Retrieve trade deal:
		TradeDeal tradeDeal = null;
		try {
			tradeDeal = manager.takeTradeDeal(id);
		} catch (TradeDealNotFoundException e) {
			sagaPlayer.message(EconomyMessages.invalidId(args.getString(0)));
			return;
		}
		
		// Add trade deal:
		selectedBuilding.addTradeDeal(tradeDeal);
		
		// Inform:
		ChunkGroup originChunkGroup = selectedBuilding.getChunkGroup();
		if(originChunkGroup != null){
			originChunkGroup.broadcast(EconomyMessages.formedTradeDealBroadcast(tradeDeal, sagaPlayer));
		}
		
		
	}

	@Command(
			aliases = {"bexportlimit"},
			usage = "<material> <amount>",
			flags = "",
			desc = "Sets the amount for material that will not get exported.",
			min = 2,
			max = 2
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.reserve"})
	public static void exportLimit(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		Material material = null;
		Integer amount = null;

		// Retrieve building:
		TradingPost selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
		
		// Permission:
		if(!selectedBuilding.checkBuildingPermission(sagaPlayer, BuildingPermission.MEDIUM)){
			sagaPlayer.message(SagaMessages.noPermission(selectedBuilding));
			return;
		}
		
		// Arguments:
		material = Material.matchMaterial(args.getString(0));
		if(material == null || material.equals(Material.AIR)){
			sagaPlayer.message(EconomyMessages.invalidMaterial(args.getString(0)));
			return;
		}
		try {
			amount = Integer.parseInt(args.getString(1));
		} catch (NumberFormatException e) {
			sagaPlayer.message(EconomyMessages.invalidAmount(args.getString(1)));
			return;
		}
		
		// Reserve:
		selectedBuilding.setReserved(material, amount);
		
		// Inform:
		if(selectedBuilding.getChunkGroup() != null){
			selectedBuilding.getChunkGroup().broadcast(EconomyMessages.reservedBroadcast(material, amount, sagaPlayer, selectedBuilding));
		}
		
		
	}
	
	@Command(
			aliases = {"bimportlimit"},
			usage = "<amount>",
			flags = "",
			desc = "Sets the currency that cant be used to import.",
			min = 1,
			max = 1
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.reserve"})
	public static void importLimit(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		Double amount = null;

		// Retrieve building:
		TradingPost selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
		
		// Permission:
		if(!selectedBuilding.checkBuildingPermission(sagaPlayer, BuildingPermission.MEDIUM)){
			sagaPlayer.message(SagaMessages.noPermission(selectedBuilding));
			return;
		}
		
		// Arguments:
		try {
			amount = Double.parseDouble(args.getString(0));
		} catch (NumberFormatException e) {
			sagaPlayer.message(EconomyMessages.invalidAmount(args.getString(0)));
			return;
		}
		
		// Reserve:
		selectedBuilding.setReserved(amount);
		
		// Inform:
		if(selectedBuilding.getChunkGroup() != null){
			selectedBuilding.getChunkGroup().broadcast(EconomyMessages.reservedBroadcast(amount, sagaPlayer, selectedBuilding));
		}
		
		
	}
	

	@Command(
			aliases = {"atpostautomatic"},
			usage = "",
			flags = "",
			desc = "Sets the trading post to automatic.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.admin.economy.tradingpost.automatic"})
	public static void setAutomatic(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		// Retrieve building:
		TradingPost selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
		
		// Already automatic:
		if(selectedBuilding.isAutomated()){
			sagaPlayer.message(alreadyAutomatic(selectedBuilding));
			return;
		}
		
		// Set automatic:
		selectedBuilding.setAutomated(true);
		
		// Inform:
		sagaPlayer.message(setAutomate(selectedBuilding));
		
		
	}
	
	@Command(
			aliases = {"atpostmanual"},
			usage = "",
			flags = "",
			desc = "Sets the trading post to manual.",
			min = 0,
			max = 0
	)
	@CommandPermissions({"saga.admin.economy.tradingpost.automatic"})
	public static void setManual(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		// Retrieve building:
		TradingPost selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
		
		// Already manual:
		if(!selectedBuilding.isAutomated()){
			sagaPlayer.message(alreadyAutomatic(selectedBuilding));
			return;
		}
		
		// Set manual:
		selectedBuilding.setAutomated(false);
		
		// Inform:
		sagaPlayer.message(setAutomate(selectedBuilding));
		
		
	}
	
	
	// Messages:
	public static String setAutomate(TradingPost tradingPost){
		
		if(tradingPost.isAutomated()){
			return ChunkGroupMessages.positive + "Set " + tradingPost.getDisplayName() + " to automatic.";
		}else{
			return ChunkGroupMessages.positive + "Set " + tradingPost.getDisplayName() + " to manual.";
		}
		
	}
	
	public static String alreadyAutomatic(TradingPost tradingPost){
		
		return ChunkGroupMessages.negative + "" + TextUtil.capitalize(tradingPost.getDisplayName()) + " is already automatic.";
		
	}
	
	public static String alreadyManual(TradingPost tradingPost){
		
		return ChunkGroupMessages.negative + "" + TextUtil.capitalize(tradingPost.getDisplayName()) + " is already manual.";
		
	}
	
	public static String cantDonateEnchanted(){
		
		return ChunkGroupMessages.negative + "Enchanted items can't be donated.";
		
	}
	
	public static String cantDonateDamaged(){
		
		return ChunkGroupMessages.negative + "Damaged items can't be donated.";
		
	}
	
	
}
