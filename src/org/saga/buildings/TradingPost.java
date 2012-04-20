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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.Clock;
import org.saga.Clock.TimeOfDayTicker;
import org.saga.Saga;
import org.saga.buildings.BuildingDefinition.BuildingPermission;
import org.saga.buildings.signs.BuildingSign;
import org.saga.buildings.signs.BuySign;
import org.saga.buildings.signs.SellSign;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.SagaChunk;
import org.saga.config.ChunkGroupConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.economy.EconomyCommands;
import org.saga.economy.EconomyManager;
import org.saga.economy.EconomyManager.InvalidWorldException;
import org.saga.economy.EconomyManager.TradeDealNotFoundException;
import org.saga.economy.EconomyManager.TransactionType;
import org.saga.economy.EconomyMessages;
import org.saga.economy.TradeDeal;
import org.saga.economy.TradeDeal.TradeDealType;
import org.saga.economy.Trader;
import org.saga.messages.BuildingMessages;
import org.saga.messages.ChunkGroupMessages;
import org.saga.messages.SagaMessages;
import org.saga.player.SagaPlayer;
import org.saga.utility.TwoPointFunction;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;


public class TradingPost extends Building implements Trader, TimeOfDayTicker{


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
	
	
	// Initialization:
	/**
	 * Initializes
	 * 
	 * @param pointCost point cost
	 * @param moneyCost money cost
	 * @param proficiencies proficiencies
	 */
	private TradingPost(Double coins, Hashtable<Material, Integer> storedItems) {
		
		
		super("");
		this.coins = coins;
		this.sellPrices = new Hashtable<Material, Double>();
		this.buyPrices = new Hashtable<Material, Double>();
		this.stored = storedItems;
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
	public boolean completeExtended() {
		

		boolean integrity = true;
		
		if(coins == null){
			coins = 0.0;
			Saga.severe(this, "coins field failed to initialize", "setting default");
			integrity = false;
		}

		if(sellPrices == null){
			sellPrices = new Hashtable<Material, Double>();
			Saga.severe(this, "sellPrices field failed to initialize", "setting default");
			integrity = false;
		}

		if(buyPrices == null){
			buyPrices = new Hashtable<Material, Double>();
			Saga.severe(this, "buyPrices field failed to initialize", "setting default");
			integrity = false;
		}
		
		if(stored == null){
			stored = new Hashtable<Material, Integer>();
			Saga.severe(this, "stored field failed to initialize", "setting default");
			integrity = false;
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
			integrity = tradeDeals.get(i).complete() && integrity;
		}
		
		
		if(imported == null){
			imported = 0.0;
			Saga.severe(this, "imported field failed to initialize", "setting default");
			integrity = false;
		}
		
		if(exported == null){
			exported = 0.0;
			Saga.severe(this, "exported field failed to initialize", "setting default");
			integrity = false;
		}
		
		if(newDeals == null){
			newDeals = new ArrayList<TradeDeal>();
			Saga.severe(this, "newDeals field failed to initialize", "setting default");
			integrity = false;
		}
		for (TradeDeal deal : newDeals) {
			integrity = deal.complete() && integrity;
		}
		
		if(completedDeals == null){
			completedDeals = new ArrayList<TradeDeal>();
			Saga.severe(this, "completedDeals field failed to initialize", "setting default");
			integrity = false;
		}
		for (TradeDeal deal : completedDeals) {
			integrity = deal.complete() && integrity;
		}

		if(expiredDeals == null){
			expiredDeals = new ArrayList<TradeDeal>();
			Saga.severe(this, "expiredDeals field failed to initialize", "setting default");
			integrity = false;
		}
		for (TradeDeal deal : expiredDeals) {
			integrity = deal.complete() && integrity;
		}
		
		if(autoMinCoins == null){
			autoMinCoins = 0.0;
			Saga.severe(this, "autoMinCoins field failed to initialize", "setting default");
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
		
		return new TradingPost(coins, new Hashtable<Material, Integer>(stored));
		
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
	public void timeOfDayTick(TimeOfDay timeOfDay) {

		
		if(!isEnabled()){
			return;
		}
		
		// Reset report before sunrise:
		if(timeOfDay.equals(TimeOfDay.SUNRISE)){
			restReport();
		}
		
		// Trade deals at sunrise:
		if(timeOfDay.equals(TimeOfDay.SUNRISE)){
			doDeals();
		}
		
		// Select deals:
		if(isAutomated() && timeOfDay.equals(TimeOfDay.MIDDAY)){
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
				Saga.warning(this, "found invalid trade deal type for " + tradeDeal + " trade deal for doTradeDeal()", "ignoring request");
			}
			
			// Check for completion:
			if(tradeDeal.isCompleted()){
				
				try {
					completedDeals.add(removeDeal(tradeDeal.getId()));
				} catch (TradeDealNotFoundException e) {
					Saga.severe(this, "failed to remove a non-existing trading deal with " + tradeDeal.getId(), "ignoring request");
				}
				
			}else 

			// Check for expiration:
			if(tradeDeal.isExpired()){
				
				try {
					expiredDeals.add(removeDeal(tradeDeal.getId()));
				} catch (TradeDealNotFoundException e) {
					Saga.severe(this, "failed to remove a non-existing trading deal with " + tradeDeal.getId(), "ignoring request");
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
		
		TwoPointFunction tradeDeals = getDefinition().getLevelFunction();
		
		// Not high enough level:
		if(tradeDeals.getXMin() < getLevel()){
			return 0;
		}
		
		return tradeDeals.value(getLevel()).intValue();
				
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
			Saga.severe(this,worldName + " is not a valid world name", "ignoring deal selection");
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
					Saga.severe(this, "deal selection failed to retrieve a deal with id " + newDeal.getId(), "stopping deal selection");
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
//		
//		// Trade sign:
//		TradeSign tradeSign = tradeSignAt(targetBlock.getLocation());
//		if(tradeSign == null){
//			return;
//		}
//
//		// Delete and remove sign:
//		tradeSign.remove();
//		removeTradeSign(tradeSign);
//		
//		
	}
	
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

	
	// Commands:
	@Command(
			aliases = {"bsetsell", "setsell"},
			usage = "[item] <price>",
			flags = "",
			desc = "Sets item sell price. Item in hand if no item is given.",
			min = 1,
			max = 2
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
		
		Material material = null;
		Double price = null;
		
		// Permission:
		if(!selectedBuilding.checkBuildingPermission(sagaPlayer, BuildingPermission.HIGH)){
			sagaPlayer.message(SagaMessages.noPermission(selectedBuilding));
			return;
		}
		
		// Arguments:
		if(args.argsLength() == 2){
			
			String sMaterial = args.getString(0);
			material = Material.matchMaterial(sMaterial);
			if(material == null){
				try {
					material = Material.getMaterial(Integer.parseInt(sMaterial));
				} catch (NumberFormatException e) { }
			}
			if(material == null){
				sagaPlayer.message(EconomyMessages.invalidMaterial(sMaterial));
				return;
			}
			
			// Price:
			String sValue = args.getString(1);
			try {
				price = Double.parseDouble(sValue);
			} catch (NumberFormatException e) {
				sagaPlayer.message(EconomyMessages.invalidPrice(sValue));
				return;
			}
			
		}else{
			
			// Material:
			ItemStack item = sagaPlayer.getItemInHand();
			if(item != null && item.getType() != Material.AIR) material = item.getType();
			if(material == null){
				sagaPlayer.message(EconomyMessages.invalidItemHand());
				return;
			}
			
			// Price:
			String sValue = args.getString(0);
			try {
				price = Double.parseDouble(sValue);
			} catch (NumberFormatException e) {
				sagaPlayer.message(EconomyMessages.invalidPrice(sValue));
				return;
			}
			
		}
		
		// Add price:
		selectedBuilding.setSellPrice(material, price);
		
		// Inform:
		sagaPlayer.message(EconomyMessages.setSell(material, price));
		
		// Notify transaction:
		selectedBuilding.notifyTransaction();
		
		
	}

	@Command(
			aliases = {"bremovesell", "removesell"},
			usage = "[item]",
			flags = "",
			desc = "Removes item sell price. Item in hand if no item is given.",
			min = 1,
			max = 0
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
		
		Material material = null;
		
		// Permission:
		if(!selectedBuilding.checkBuildingPermission(sagaPlayer, BuildingPermission.HIGH)){
			sagaPlayer.message(SagaMessages.noPermission(selectedBuilding));
			return;
		}
		
		// Arguments:
		if(args.argsLength() == 1){
			
			String sMaterial = args.getString(0);
			material = Material.matchMaterial(sMaterial);
			if(material == null){
				try {
					material = Material.getMaterial(Integer.parseInt(sMaterial));
				} catch (NumberFormatException e) { }
			}
			if(material == null){
				sagaPlayer.message(EconomyMessages.invalidMaterial(sMaterial));
				return;
			}
			
		}else{
			
			// Material:
			ItemStack item = sagaPlayer.getItemInHand();
			if(item != null && item.getType() != Material.AIR) material = item.getType();
			if(material == null){
				sagaPlayer.message(EconomyMessages.invalidItemHand());
				return;
			}
			
		}
		
		// Remove price:
		selectedBuilding.removeSellPrice(material);
		
		// Inform:
		sagaPlayer.message(EconomyMessages.removeSell(material));
		
		// Notify transaction:
		selectedBuilding.notifyTransaction();
		
		
	}
	
	@Command(
			aliases = {"bsetbuy", "setbuy"},
			usage = "[item] <price>",
			flags = "",
			desc = "Sets item buy price. Item in hand if no item is given.",
			min = 1,
			max = 2
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.setsell"})
	public static void setBuy(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
		// Retrieve building:
		TradingPost selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
		
		Material material = null;
		Double price = null;
		
		// Permission:
		if(!selectedBuilding.checkBuildingPermission(sagaPlayer, BuildingPermission.HIGH)){
			sagaPlayer.message(SagaMessages.noPermission(selectedBuilding));
			return;
		}
		
		// Arguments:
		if(args.argsLength() == 2){
			
			String sMaterial = args.getString(0);
			material = Material.matchMaterial(sMaterial);
			if(material == null){
				try {
					material = Material.getMaterial(Integer.parseInt(sMaterial));
				} catch (NumberFormatException e) { }
			}
			if(material == null){
				sagaPlayer.message(EconomyMessages.invalidMaterial(sMaterial));
				return;
			}
			
			// Price:
			String sValue = args.getString(1);
			try {
				price = Double.parseDouble(sValue);
			} catch (NumberFormatException e) {
				sagaPlayer.message(EconomyMessages.invalidPrice(sValue));
				return;
			}
			
		}else{
			
			// Material:
			ItemStack item = sagaPlayer.getItemInHand();
			if(item != null && item.getType() != Material.AIR) material = item.getType();
			if(material == null){
				sagaPlayer.message(EconomyMessages.invalidItemHand());
				return;
			}
			
			// Price:
			String sValue = args.getString(0);
			try {
				price = Double.parseDouble(sValue);
			} catch (NumberFormatException e) {
				sagaPlayer.message(EconomyMessages.invalidPrice(sValue));
				return;
			}
			
		}
		
		// Add price:
		selectedBuilding.setBuyPrice(material, price);
		
		// Inform:
		sagaPlayer.message(EconomyMessages.setBuy(material, price));
		
		// Notify transaction:
		selectedBuilding.notifyTransaction();
		
		
	}

	@Command(
			aliases = {"bremovebuy", "removebuy"},
			usage = "[item]",
			flags = "",
			desc = "Removes item buy price. Item in hand if no item is given.",
			min = 1,
			max = 0
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.removesell"})
	public static void removeBuy(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		// Retrieve building:
		TradingPost selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
		
		Material material = null;
		
		// Permission:
		if(!selectedBuilding.checkBuildingPermission(sagaPlayer, BuildingPermission.HIGH)){
			sagaPlayer.message(SagaMessages.noPermission(selectedBuilding));
			return;
		}
		
		// Arguments:
		if(args.argsLength() == 1){
			
			String sMaterial = args.getString(0);
			material = Material.matchMaterial(sMaterial);
			if(material == null){
				try {
					material = Material.getMaterial(Integer.parseInt(sMaterial));
				} catch (NumberFormatException e) { }
			}
			if(material == null){
				sagaPlayer.message(EconomyMessages.invalidMaterial(sMaterial));
				return;
			}
			
		}else{
			
			// Material:
			ItemStack item = sagaPlayer.getItemInHand();
			if(item != null && item.getType() != Material.AIR) material = item.getType();
			if(material == null){
				sagaPlayer.message(EconomyMessages.invalidItemHand());
				return;
			}
			
		}
		
		// Remove price:
		selectedBuilding.removeBuyPrice(material);
		
		// Inform:
		sagaPlayer.message(EconomyMessages.removeSell(material));
		
		// Notify transaction:
		selectedBuilding.notifyTransaction();
		
		
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
			sagaPlayer.message(BuildingMessages.cantDonateDamaged());
			return;
		}
		
		// Enchanted:
		if(item.getEnchantments().size() > 0){
			sagaPlayer.message(BuildingMessages.cantDonateEnchanted());
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
		
		// Notify transaction:
		selectedBuilding.notifyTransaction();
		
		
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
			
			if(item != null && item.getType().equals(material) && item.getDurability() == 0 && item.getEnchantments().size() == 0){

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
		
		// Notify transactions:
		selectedBuilding.notifyTransaction();
		
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
	public static void donateCoins(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

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

		// Notify transaction:
		selectedBuilding.notifyTransaction();
		
		
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
			aliases = {"bnewdeal","bnewimport","bnewexport", "newdeal","newimport","newexport"},
			usage = "<trade deal ID>",
			flags = "",
			desc = "Form a new trading deal.",
			min = 1,
			max = 1
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.formdeal"})
	public static void newdeal(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		

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
		if(selectedBuilding.getDealCount() >= selectedBuilding.getDealsMaxCount()){
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
		selectedBuilding.addDeal(tradeDeal);
		
		// Inform:
		if(selectedBuilding.getChunkGroup() != null){
			selectedBuilding.getChunkGroup().broadcast(BuildingMessages.formedDealBrdc(tradeDeal, sagaPlayer));
		}
		
		// Report:
		selectedBuilding.newDeals.add(tradeDeal);
		
		
	}

	@Command(
			aliases = {"bsetminimumcoins","bsetminc"},
			usage = "<amount>",
			flags = "",
			desc = "Sets the coins that can't be used for imports.",
			min = 1,
			max = 1
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.reserve"})
	public static void setMinCoins(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		
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
		selectedBuilding.setMinimumCoins(amount);
		
		// Inform:
		if(selectedBuilding.getChunkGroup() != null){
			selectedBuilding.getChunkGroup().broadcast(EconomyMessages.reservedBroadcast(amount, sagaPlayer, selectedBuilding));
		}
		
		
	}
	

	@Command(
			aliases = {"atpostautomatic","asetautomatic"},
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
			sagaPlayer.message(BuildingMessages.alreadyAutomatic(selectedBuilding));
			return;
		}
		
		// Set automatic:
		selectedBuilding.setAutomated(true);
		
		// Inform:
		sagaPlayer.message(BuildingMessages.setAutomate(selectedBuilding));
		
		
	}
	
	@Command(
			aliases = {"atpostmanual","asetmanual"},
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
			sagaPlayer.message(BuildingMessages.alreadyAutomatic(selectedBuilding));
			return;
		}
		
		// Set manual:
		selectedBuilding.setAutomated(false);
		
		// Inform:
		sagaPlayer.message(BuildingMessages.setAutomate(selectedBuilding));
		
		
	}
	
	
	@Command(
			aliases = {"bgoods","goods"},
			usage = "",
			flags = "",
			desc = "Shows trading post goods.",
			min = 0,
			max = 1
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.goods"})
	public static void levels(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


		// Retrieve building:
		TradingPost selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
		
    	// Arguments:
		Integer page = null;
    	if(args.argsLength() == 1){
    		
        	try {
    			page = Integer.parseInt(args.getString(0));
    		} catch (NumberFormatException e) {
    			sagaPlayer.message(ChunkGroupMessages.invalidPage(args.getString(0)));
    			return;
    		}
    		
    	}else{
    		
    		page = 1;
        	
    	}
		
	    // Inform:
	    sagaPlayer.message(BuildingMessages.goods(selectedBuilding, page - 1));
	      
	    
	}
	
	@Command(
			aliases = {"breport","report"},
			usage = "",
			flags = "",
			desc = "Shows trading post report.",
			min = 0,
			max = 1
	)
	@CommandPermissions({"saga.user.building.tradingpost.economy.report"})
	public static void report(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


		// Retrieve building:
		TradingPost selectedBuilding = null;
		try {
			selectedBuilding = Building.retrieveBuilding(args, plugin, sagaPlayer, TradingPost.class);
		} catch (Throwable e) {
			sagaPlayer.message(e.getMessage());
			return;
		}
		
    	// Arguments:
		Integer page = null;
    	if(args.argsLength() == 1){
    		
        	try {
    			page = Integer.parseInt(args.getString(0));
    		} catch (NumberFormatException e) {
    			sagaPlayer.message(ChunkGroupMessages.invalidPage(args.getString(0)));
    			return;
    		}
    		
    	}else{
    		
    		page = 1;
        	
    	}
		
	    // Inform:
	    sagaPlayer.message(BuildingMessages.report(selectedBuilding, page - 1));
	      
	    
	}
	
	
	
}
