package org.saga.buildings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.Clock;
import org.saga.Clock.DaytimeTicker;
import org.saga.SagaLogger;
import org.saga.buildings.signs.BuildingSign;
import org.saga.buildings.signs.BuySign;
import org.saga.buildings.signs.SellSign;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.SagaChunk;
import org.saga.config.ChunkGroupConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.economy.EconomyManager;
import org.saga.economy.EconomyManager.InvalidWorldException;
import org.saga.economy.EconomyManager.TradeDealNotFoundException;
import org.saga.economy.EconomyManager.TransactionType;
import org.saga.economy.TradeDeal;
import org.saga.economy.TradeDeal.TradeDealType;
import org.saga.economy.Trader;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.messages.BuildingMessages;
import org.saga.player.SagaPlayer;


public class TradingPost extends Building implements Trader, DaytimeTicker{
	
	// TODO: Improve trading post
	

	/**
	 * Name for goods the sign.
	 */
	transient public static String GOODS_SIGN = "=[GOODS]=";

	/**
	 * Name for goods the sign.
	 */
	transient public static String DEALS_SIGN = "=[DEALS]=";
	
	/**
	 * Name for goods the sign.
	 */
	transient public static String REPORT_SIGN = "=[REPORT]=";

	/**
	 * Amount of materials considered at deal automation.
	 */
	transient public static Integer DEAL_AUTO_MATERIALS = 5;
	
	
	/**
	 * The currency the trade post owns.
	 */
	private Double coins;

	/**
	 * Sell prices.
	 */
	private Hashtable<Material, Double> sellPrices;

	/**
	 * Buy prices.
	 */
	private Hashtable<Material, Double> buyPrices;
	
	/**
	 * Stored items.
	 */
	private Hashtable<Material, Integer> stored;

	/**
	 * Trade deals.
	 */
	private ArrayList<TradeDeal> tradeDeals;
	
	
	/**
	 * Coins imported.
	 */
	private Double imported;
	
	/**
	 * Coins exported.
	 */
	private Double exported;

	/**
	 * New deals.
	 */
	private ArrayList<TradeDeal> newDeals;

	/**
	 * Expired deals.
	 */
	private ArrayList<TradeDeal> expiredDeals;

	/**
	 * Completed deals.
	 */
	private ArrayList<TradeDeal> completedDeals;
	
	
	// Automation:
	/**
	 * Amount of coins to be kept.
	 */
	private Double autoMinCoins;
	
	/**
	 * Trading post is automated if true.
	 */
	private Boolean automated;
	
	
	// Initialisation:
	/**
	 * Creates a building from the definition.
	 * 
	 * @param definition building definition
	 */
	public TradingPost(BuildingDefinition definition) {
		
		super(definition);
		
		this.coins = 0.0;
		this.sellPrices = new Hashtable<Material, Double>();
		this.buyPrices = new Hashtable<Material, Double>();
		this.stored = new Hashtable<Material, Integer>();
		this.tradeDeals = new ArrayList<TradeDeal>();
		
		this.imported = 0.0;
		this.exported = 0.0;
		this.newDeals = new ArrayList<TradeDeal>();
		this.expiredDeals = new ArrayList<TradeDeal>();
		this.completedDeals = new ArrayList<TradeDeal>();
		
		this.autoMinCoins = 0.0;
		this.automated = false;
		
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#completeExtended()
	 */
	@Override
	public boolean complete() throws InvalidBuildingException {
		

		boolean integrity = super.complete();
		
		if(coins == null){
			coins = 0.0;
			SagaLogger.nullField(this, "coins");
			integrity = false;
		}

		if(sellPrices == null){
			sellPrices = new Hashtable<Material, Double>();
			SagaLogger.nullField(this, "sellPrices");
			integrity = false;
		}

		if(buyPrices == null){
			buyPrices = new Hashtable<Material, Double>();
			SagaLogger.nullField(this, "buyPrices");
			integrity = false;
		}
		
		if(stored == null){
			stored = new Hashtable<Material, Integer>();
			SagaLogger.nullField(this, "stored");
			integrity = false;
		}
		
		if(tradeDeals == null){
			tradeDeals = new ArrayList<TradeDeal>();
			SagaLogger.nullField(this, "tradeDeals");
			integrity = false;
		}
		for (int i = 0; i < tradeDeals.size(); i++) {
			if(tradeDeals.get(i) == null){
				SagaLogger.nullField(this, "tradeDeals element");
				tradeDeals.remove(i);
				i--;
				continue;
			}
			integrity = tradeDeals.get(i).complete() && integrity;
		}
		
		
		if(imported == null){
			imported = 0.0;
			SagaLogger.nullField(this, "imported");
			integrity = false;
		}
		
		if(exported == null){
			exported = 0.0;
			SagaLogger.nullField(this, "exported");
			integrity = false;
		}
		
		if(newDeals == null){
			newDeals = new ArrayList<TradeDeal>();
			SagaLogger.nullField(this, "newDeals");
			integrity = false;
		}
		for (TradeDeal deal : newDeals) {
			integrity = deal.complete() && integrity;
		}
		
		if(completedDeals == null){
			completedDeals = new ArrayList<TradeDeal>();
			SagaLogger.nullField(this, "completedDeals");
			integrity = false;
		}
		for (TradeDeal deal : completedDeals) {
			integrity = deal.complete() && integrity;
		}

		if(expiredDeals == null){
			expiredDeals = new ArrayList<TradeDeal>();
			SagaLogger.nullField(this, "expiredDeals");
			integrity = false;
		}
		for (TradeDeal deal : expiredDeals) {
			integrity = deal.complete() && integrity;
		}
		
		if(autoMinCoins == null){
			autoMinCoins = 0.0;
			SagaLogger.nullField(this, "autoMinCoins");
			integrity = false;
		}
		
		if(automated == null){
			automated = false;
			SagaLogger.nullField(this, "automated");
			integrity = false;
		}
		
		return integrity;
		
		
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
		Clock.clock().registerTick(this);
		
		
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

	
	// Stored:
	/**
	 * Stored items.
	 * 
	 * @return stored items
	 */
	public Hashtable<Material, Integer> getStoredItems() {
		return new Hashtable<Material, Integer>(stored);
	}
	
	/**
	 * Gets a sell price for a item.
	 * 
	 * @param material item material
	 * @return price, null if none
	 */
	public Double getBuyPrice(Material material) {
		
		
		// Automation:
		if(isAutomated()){
			
			Double price = EconomyConfiguration.config().prices.get(material);
			if(price == null) return null;
			return price * EconomyConfiguration.config().automBuyMult;
			
		}
		
		return buyPrices.get(material);
		
		
	}
	
	/**
	 * Sets a buy price for a material.
	 * 
	 * @param material item material
	 * @param buy price price
	 */
	public void setBuyPrice(Material material, Double price) {
		buyPrices.put(material, price);
	}
	
	/**
	 * Removes a buy price for a material.
	 * 
	 * @param material item material
	 */
	public void removeBuyPrice(Material material) {
		buyPrices.remove(material);
	}

	/**
	 * Gets a sell price for a item.
	 * 
	 * @param material item material
	 * @return price, null if none
	 */
	public Double getSellPrice(Material material) {
		
		
		// Automation:
		if(isAutomated()){
			
			Double price = EconomyConfiguration.config().prices.get(material);
			if(price == null) return null;
			return price * EconomyConfiguration.config().automSellMult;
			
		}
		
		return sellPrices.get(material);
		
		
	}

	/**
	 * Sets a sell price for a material.
	 * 
	 * @param material item material
	 * @param sell price price
	 */
	public void setSellPrice(Material material, Double price) {
		sellPrices.put(material, price);
	}
	
	/**
	 * Removes a sell price for a material.
	 * 
	 * @param material item material
	 */
	public void removeSellPrice(Material material) {
		sellPrices.remove(material);
	}
	
	/**
	 * Gets a sorted list of all materials.
	 * 
	 * @return sorted list of all materials
	 */
	public ArrayList<Material> getAllMaterials() {


		// Get all materials:
		HashSet<Material> allMaterials = new HashSet<Material>();
		allMaterials.addAll(stored.keySet());
		allMaterials.addAll(sellPrices.keySet());
		allMaterials.addAll(buyPrices.keySet());
		
		// Automation:
		if(isAutomated()){
			allMaterials.addAll(EconomyConfiguration.config().getAllDealMaterials());
		}
		
		// Convert to list and sort:
		ArrayList<Material> sortedItems = new ArrayList<Material>(allMaterials);
		Collections.sort(sortedItems);

		return sortedItems;
		
		
	}
	
	
	// Time:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.Clock.TimeOfDayTicker#timeOfDayTick(org.saga.Clock.TimeOfDayTicker.TimeOfDay)
	 */
	@Override
	public void timeOfDayTick(Daytime timeOfDay) {

		
		if(!isEnabled()){
			return;
		}
		
		// Reset report before sunrise:
		if(timeOfDay.equals(Daytime.SUNRISE)){
			restReport();
		}
		
		// Trade deals at sunrise:
		if(timeOfDay.equals(Daytime.SUNRISE)){
			doDeals();
		}
		
		// Select deals:
		if(isAutomated() && timeOfDay.equals(Daytime.MIDDAY)){
			selectDeals();
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
	

	// Signs:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#isBuildingSign(java.lang.String)
	 */
	@Override
	protected boolean isBuildingSign(String firstLine) {
		
		if(firstLine.equalsIgnoreCase(SellSign.SIGN_NAME)) return true;
		
		if(firstLine.equalsIgnoreCase(BuySign.SIGN_NAME)) return true;
		
		return super.isBuildingSign(firstLine);
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#createBuildingSign2(org.bukkit.block.Sign, org.bukkit.event.block.SignChangeEvent)
	 */
	@Override
	protected BuildingSign createBuildingSign(Sign sign, SignChangeEvent event) {
		
		
		// Stone fix sign:
		if(event.getLine(0).equalsIgnoreCase(SellSign.SIGN_NAME)){
			
			return SellSign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
			
		}else if(event.getLine(0).equalsIgnoreCase(BuySign.SIGN_NAME)){
			
			return BuySign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
			
		}
		
		return super.createBuildingSign(sign, event);
		
		
	}
	
	
	// Automation:
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

	
	// Trade deals:
	/**
	 * Does a trade deal.
	 * 
	 */
	private void doDeals() {

		
		
		ArrayList<TradeDeal> tradeDeals = getDeals();
		
		// Trade deal transactions:
		for (int i = 0; i < tradeDeals.size(); i++) {
			
			TradeDeal tradeDeal = tradeDeals.get(i);
			
			// Next day:
			tradeDeal.nextDay();
			
			// Export:
			if(tradeDeal.getType().equals(TradeDealType.EXPORT)){
				
				// Do deal:
				exported += tradeDeal.doDeal(this);
				
			}
			
			// Import:
			else if(tradeDeal.getType().equals(TradeDealType.IMPORT)){
				
				// Do deal:
				imported += tradeDeal.doDeal(this);
				
			}else{
				SagaLogger.severe(this, "found invalid trade deal type for " + tradeDeal + " trade deal for doTradeDeal()");
			}
			
			// Check for completion:
			if(tradeDeal.isCompleted()){
				
				try {
					completedDeals.add(removeDeal(tradeDeal.getId()));
				} catch (TradeDealNotFoundException e) {
					SagaLogger.severe(this, "failed to remove a non-existing trading deal with " + tradeDeal.getId());
				}
				
			}else 

			// Check for expiration:
			if(tradeDeal.isExpired()){
				
				try {
					expiredDeals.add(removeDeal(tradeDeal.getId()));
				} catch (TradeDealNotFoundException e) {
					SagaLogger.severe(this, "failed to remove a non-existing trading deal with " + tradeDeal.getId());
				}

			}
			
		}
		
		// Notify transaction:
		if(imported != 0 || exported != 0) notifyTransaction();
		
		// Inform:
		if(getChunkGroup() != null) getChunkGroup().broadcast(BuildingMessages.dealsBalance(this));
		
		
	}

	/**
	 * Gets the maximum allowed trade deal count.
	 * 
	 * @return allowed trade deals
	 */
	public Integer getDealsMaxCount() {
		
		return 4;
				
	}

	/**
	 * Gets the count of trade deals.
	 * 
	 * @return count of trade deals
	 */
	public Integer getDealCount() {
		return tradeDeals.size();
	}

	/**
	 * Adds a trade deal.
	 * 
	 * @param deal trade deal
	 */
	public void addDeal(TradeDeal deal) {


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
	public TradeDeal removeDeal(Integer id) throws TradeDealNotFoundException {

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
	 * Calculates the profit for all deals.
	 * 
	 * @return profit
	 */
	private Double calcDealsProfit() {

		
		Double profit = 0.0;
		
		ArrayList<TradeDeal> deals = getDeals();
		for (TradeDeal tradeDeal : deals) {
			
			// Import:
			if(tradeDeal.getType() == TradeDealType.IMPORT){
				
				Integer amount = getAmount(tradeDeal.getMaterial());
				profit -= tradeDeal.getTotalCost(amount);
				
			}
			
			// Export:
			else if(tradeDeal.getType() == TradeDealType.EXPORT){
				
				Integer amount = getAmount(tradeDeal.getMaterial());
				profit += tradeDeal.getTotalCost(amount);
				
			}
			
		}
		
		return profit;
		
		
	}
	
	/**
	 * Gets sorted import materials. First elements have a higher priority.
	 * 
	 * @return sorted imports
	 */
	private ArrayList<Material> getSortedImports() {

		
		ArrayList<Material> materials = new ArrayList<Material>();
		ArrayList<BuySign> bSigns = getSigns(BuySign.class);
		
		for (BuySign bSign : bSigns) {
			
			if(bSign.getMaterial() == null) continue;
			
			materials.add(bSign.getMaterial());
			
		}
		
		Comparator<Material> comparator = new Comparator<Material>() {
			@Override
			public int compare(Material o1, Material o2) {

				Double price1 = getBuyPrice(o1);
				if(price1 == null) price1 = 0.0;

				Integer amount1 = getAmount(o1);
				if(amount1 == null) amount1 = 0;

				Double price2 = getBuyPrice(o2);
				if(price2 == null) price2 = 0.0;

				Integer amount2 = getAmount(o2);
				if(amount2 == null) amount1 = 2;

				return (int) (price1 * amount1 - price2 * amount2);
				
			}
		};
		
		Collections.sort(materials, comparator);

		// Limit to top deals:
		while(materials.size() > DEAL_AUTO_MATERIALS){
			materials.remove(materials.size() -1);
		}
		
		return materials;
		

	}
	
	/**
	 * Gets sorted export materials. First elements have a higher priority.
	 * 
	 * @return sorted exports
	 */
	private ArrayList<Material> getSortedExports() {

		
		ArrayList<Material> materials = new ArrayList<Material>();
		ArrayList<SellSign> bSigns = getSigns(SellSign.class);
		
		for (SellSign bSign : bSigns) {
			
			if(bSign.getMaterial() == null) continue;
			
			materials.add(bSign.getMaterial());
			
		}
		
		Comparator<Material> comparator = new Comparator<Material>() {
			@Override
			public int compare(Material o1, Material o2) {

				Double price1 = getBuyPrice(o1);
				if(price1 == null) price1 = 0.0;

				Integer amount1 = getAmount(o1);
				if(amount1 == null) amount1 = 0;

				Double price2 = getBuyPrice(o2);
				if(price2 == null) price2 = 0.0;

				Integer amount2 = getAmount(o2);
				if(amount2 == null) amount1 = 2;

				return (int) (price2 * amount2 - price1 * amount1);
				
			}
		};
		
		Collections.sort(materials, comparator);

		// Limit to top deals:
		while(materials.size() > DEAL_AUTO_MATERIALS){
			materials.remove(materials.size() -1);
		}
		
		return materials;
		

	}
	
	/**
	 * Selects deals.
	 * 
	 */
	private void selectDeals() {

		
		// Manager:
		SagaChunk sagaChunk = getSagaChunk();
		if(sagaChunk == null){
			return;
		}
		String worldName = sagaChunk.getWorldName();
		
		EconomyManager manager = null;
		try {
			manager = EconomyManager.manager(worldName);
		}
		catch (InvalidWorldException e) {
			SagaLogger.severe(this,worldName + " is not a valid world name");
			return;
		}
		
		
		// Deals:
		int newDeals = getDealsMaxCount() - getDealCount();
		TradeDeal newDeal = null;
		
		for (int i = 0; i < newDeals; i++) {
			
			newDeal = null;
			
			Double available = calcDealsProfit() + getCoins() - getMinimumCoins();
			
			// Import:
			if(available > 0){
				
				ArrayList<Material> materials = getSortedImports();
				
				ArrayList<TradeDeal> deals = manager.findGoodTradeDeal(TradeDealType.IMPORT, materials, this);
				
				for (TradeDeal tradeDeal : deals) {
					
					if(tradeDeal.getTotalCost() <= available){
						newDeal = tradeDeal;
						break;
					}
					
				}
				
			}
			
			// Export:
			if (newDeal == null) {

				ArrayList<Material> materials = getSortedExports();
				
				ArrayList<TradeDeal> deals = manager.findGoodTradeDeal(TradeDealType.EXPORT, materials, this);

				for (TradeDeal tradeDeal : deals) {
					
					if(tradeDeal.getAmount() <= getAmount(tradeDeal.getMaterial())){
						newDeal = tradeDeal;
						break;
					}
					
				}
				
				
			}
			
			// No deal found:
			if(newDeal == null) continue;
			else{
				
				try {
					
					manager.takeTradeDeal(newDeal.getId());
					addDeal(newDeal);
					
					this.newDeals.add(newDeal);
					
					// Inform:
					if(getChunkGroup() != null) getChunkGroup().broadcast(BuildingMessages.formedDeal(this, newDeal));
					
				}
				catch (TradeDealNotFoundException e) {
					SagaLogger.severe(this, "deal selection failed to retrieve a deal with id " + newDeal.getId());
					return;
				}
				
			}
			
			
		}


	}
	
	
	// Report:
	/**
	 * Resets report.
	 * 
	 */
	private void restReport() {

		imported = 0.0;
		exported = 0.0;
		newDeals = new ArrayList<TradeDeal>();
		expiredDeals = new ArrayList<TradeDeal>();
		completedDeals = new ArrayList<TradeDeal>();
		
	}
	
	/**
	 * Gets the imported.
	 * 
	 * @return the imported
	 */
	public Double getImported() {
		return imported;
	}

	/**
	 * Gets the exported.
	 * 
	 * @return the exported
	 */
	public Double getExported() {
		return exported;
	}

	/**
	 * Gets the newDeals.
	 * 
	 * @return the newDeals
	 */
	public ArrayList<TradeDeal> getNewDeals() {
		return newDeals;
	}

	/**
	 * Add a new deal.
	 * 
	 * @param deal new deal
	 */
	public void addNewDeal(TradeDeal deal) {
		newDeals.add(deal);
	}

	
	/**
	 * Gets the expiredDeals.
	 * 
	 * @return the expiredDeals
	 */
	public ArrayList<TradeDeal> getExpiredDeals() {
		return expiredDeals;
	}

	/**
	 * Gets the completedDeals.
	 * 
	 * @return the completedDeals
	 */
	public ArrayList<TradeDeal> getCompletedDeals() {
		return completedDeals;
	}

	
	// Reserved:
	/**
	 * Sets the amount of reserved coins.
	 * 
	 * @param amount amount
	 */
	public void setMinimumCoins(Double amount) {
		
		
		if(amount < 0){
			autoMinCoins = 0.0;
		}else{
			autoMinCoins = amount;
		}
		
		
	}
	
	/**
	 * Gets the kept coins.
	 * 
	 * @return kept coins, 0 if not reserved
	 */
	public Double getMinimumCoins() {

		Double amount = autoMinCoins;
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
		
//		Transaction transaction = findTransaction(type, material);
//		
//		if(!hasSign(type, material) || transaction == null){
//			return false;
//		}
		
//		if(type.equals(TransactionType.SELL)){
//			
//			return transaction.getTotalValue() <= getCoins();
//			
//		}else if(type.equals(TransactionType.BUY)){
//			
//			return transaction.getAmount() <= getAmount(material);
//			
//		}
		
		return  false;
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#addCurrency(java.lang.Double)
	 */
	@Override
	public void addCoins(Double amount) {
		
		coins += amount;
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#removeCurrency(java.lang.Double)
	 */
	@Override
	public void removeCoins(Double amount) {
		
		coins -= amount;
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#getCurrency()
	 */
	@Override
	public Double getCoins() {
		return coins;
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#getItemCount(org.bukkit.Material)
	 */
	@Override
	public Integer getAmount(Material material) {
		
		Integer count = stored.get(material);
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
		
		Integer count = stored.get(itemStack.getType());
		if(count == null) count = 0;
		
		count += itemStack.getAmount();
		
		stored.put(itemStack.getType(), count);
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#removeItemStack(org.bukkit.inventory.ItemStack)
	 */
	@Override
	public void removeItem(ItemStack itemStack) {
		

		Integer count = stored.get(itemStack.getType());
		if(count == null) count = 0;
		
		count -= itemStack.getAmount();
		
		if(count != 0){
			stored.put(itemStack.getType(), count);
		}else{
			stored.remove(itemStack.getType());
		}
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see saga.economy.Trader#getTradeDeals()
	 */
	@Override
	public ArrayList<TradeDeal> getDeals() {
		return new ArrayList<TradeDeal>(tradeDeals);
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.economy.Trader#notifyTransaction()
	 */
	@Override
	public void notifyTransaction() {
	
		
		// Sell signs:
		ArrayList<BuildingSign> sellSigns = getValidSigns(SellSign.SIGN_NAME);

		for (BuildingSign buildingSign : sellSigns) {
			buildingSign.refresh();
		}

		// Buy signs:
		ArrayList<BuildingSign> buySigns = getValidSigns(BuySign.SIGN_NAME);

		for (BuildingSign buildingSign : buySigns) {
			buildingSign.refresh();
		}
		
		
	}

	
	// Events:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onSignChange(org.bukkit.event.block.SignChangeEvent, org.saga.player.SagaPlayer)
	 */
	@Override
	public void onSignChange(SignChangeEvent event, SagaPlayer sagaPlayer) {
	
		
		// Goods sign:
		if(event.getLine(0).equalsIgnoreCase(GOODS_SIGN)){
			event.setLine(0, ChunkGroupConfiguration.config().enabledSignColor + GOODS_SIGN);
		}

		// Goods sign:
		else if(event.getLine(0).equalsIgnoreCase(DEALS_SIGN)){
			event.setLine(0, ChunkGroupConfiguration.config().enabledSignColor + DEALS_SIGN);
		}
	
		// Report sign:
		else if(event.getLine(0).equalsIgnoreCase(REPORT_SIGN)){
			event.setLine(0, ChunkGroupConfiguration.config().enabledSignColor + REPORT_SIGN);
		}
	
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent, org.saga.player.SagaPlayer)
	 */
	@Override
	public void onPlayerInteract(PlayerInteractEvent event, SagaPlayer sagaPlayer) {

		
		super.onPlayerInteract(event, sagaPlayer);

		// Canceled:
		if(event.isCancelled()){
			return;
		}
		
		Block targetBlock = event.getClickedBlock();
    	
		// Right click:
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			return;
		}
		
		// Invalid:
		if(targetBlock == null){
    		return;
    	}
    	
		// Sign:
		if(!(targetBlock.getState() instanceof Sign)){
			return;
		}
		Sign sign = (Sign) targetBlock.getState();

		// Top sign:
		if(ChatColor.stripColor(sign.getLine(0)).equals(GOODS_SIGN)){
			
			Integer page = 1;
			if(sign.getLine(1).length() > 0){
				try {
					page = Integer.parseInt(sign.getLine(1));
				}
				catch (NumberFormatException e) { }
			}
			
			sagaPlayer.message(BuildingMessages.goods(this, page - 1));
			
			// Take control:
			event.setUseInteractedBlock(Result.DENY);
			event.setUseItemInHand(Result.DENY);
			
		}
		
		// Deals sign:
		else if(ChatColor.stripColor(sign.getLine(0)).equals(DEALS_SIGN)){
			
			Integer page = 1;
			if(sign.getLine(1).length() > 0){
				try {
					page = Integer.parseInt(sign.getLine(1));
				}
				catch (NumberFormatException e) { }
			}
			
			sagaPlayer.message(BuildingMessages.deals(this, page - 1));
			
			// Take control:
			event.setUseInteractedBlock(Result.DENY);
			event.setUseItemInHand(Result.DENY);
			
		}

		// Report sign:
		else if(ChatColor.stripColor(sign.getLine(0)).equals(REPORT_SIGN)){
			
			Integer page = 1;
			if(sign.getLine(1).length() > 0){
				try {
					page = Integer.parseInt(sign.getLine(1));
				}
				catch (NumberFormatException e) { }
			}
			
			sagaPlayer.message(BuildingMessages.report(this, page - 1));
			
			// Take control:
			event.setUseInteractedBlock(Result.DENY);
			event.setUseItemInHand(Result.DENY);
			
		}
		
		
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

	
	
}
