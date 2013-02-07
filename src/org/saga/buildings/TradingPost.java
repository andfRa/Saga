package org.saga.buildings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.saga.Clock.DaytimeTicker;
import org.saga.SagaLogger;
import org.saga.buildings.production.ProductionBuilding;
import org.saga.buildings.production.SagaItem;
import org.saga.buildings.production.SagaPricedItem;
import org.saga.buildings.signs.BuildingSign;
import org.saga.buildings.signs.BuySign;
import org.saga.buildings.signs.ImportSign;
import org.saga.config.EconomyConfiguration;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.messages.EconomyMessages;
import org.saga.settlements.Settlement;


public class TradingPost extends ProductionBuilding implements DaytimeTicker{
	
	
	/**
	 * Buy limit key.
	 */
	private static String BUY_LIMIT_KEY = "buy limit";

	
	
	/**
	 * Coins spend for buying.
	 */
	private Double buyCoins;

	/**
	 * Coins spend for selling.
	 */
	private Double sellCoins;
	
	
	/**
	 * Awaiting exports.
	 */
	private double[] collectedExports;
	
	/**
	 * Work done for exports.
	 */
	private double[] exportsWork;
	
	
	// Initialisation:
	/**
	 * Creates a building from the definition.
	 * 
	 * @param definition building definition
	 */
	public TradingPost(BuildingDefinition definition) {
		
		super(definition);
	
		buyCoins = 0.0;
		sellCoins = 0.0;
		collectedExports = new double[EconomyConfiguration.config().getTradingPostExports().length];
		exportsWork =  new double[EconomyConfiguration.config().getTradingPostExports().length];
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#completeExtended()
	 */
	@Override
	public boolean complete() throws InvalidBuildingException {
		

		boolean integrity = super.complete();

		if(buyCoins == null){
			SagaLogger.nullField(this, "buyCoins");
			buyCoins = 0.0;
		}

		if(sellCoins == null){
			SagaLogger.nullField(this, "sellCoins");
			sellCoins = 0.0;
		}
		
		if(collectedExports == null){
			SagaLogger.nullField(this, "forExport");
			collectedExports = new double[EconomyConfiguration.config().getTradingPostExports().length];
		}
		
		if(collectedExports.length != EconomyConfiguration.config().getTradingPostExports().length){
			SagaLogger.warning(this, "resetting exportAmounts");
			collectedExports = new double[EconomyConfiguration.config().getTradingPostExports().length];
		}
		
		if(exportsWork == null){
			SagaLogger.nullField(this, "collectedExports");
			exportsWork = new double[EconomyConfiguration.config().getTradingPostExports().length];
		}
		
		if(exportsWork.length != EconomyConfiguration.config().getTradingPostExports().length){
			SagaLogger.warning(this, "resetting exportsWork");
			exportsWork = new double[EconomyConfiguration.config().getTradingPostExports().length];
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

	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#disable()
	 */
	@Override
	public void disable() {

		super.disable();

	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#perform()
	 */
	@Override
	public void perform() {

		
		// Reset coins:
		buyCoins = 0.0;

		// Refresh signs:
		refreshSigns();
		
		
	}
	
	
	
	// Signs:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#isBuildingSign(java.lang.String)
	 */
	@Override
	protected boolean isBuildingSign(String firstLine) {
		
		if(firstLine.equalsIgnoreCase(BuySign.SIGN_NAME)) return true;
		if(firstLine.equalsIgnoreCase(ImportSign.SIGN_NAME)) return true;
		
		return super.isBuildingSign(firstLine);
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#createBuildingSign(org.bukkit.block.Sign, org.bukkit.event.block.SignChangeEvent)
	 */
	@Override
	protected BuildingSign createBuildingSign(Sign sign, SignChangeEvent event) {
		
		if(event.getLine(0).equalsIgnoreCase(BuySign.SIGN_NAME)) return BuySign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
		if(event.getLine(0).equalsIgnoreCase(ImportSign.SIGN_NAME)) return ImportSign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
		
		return super.createBuildingSign(sign, event);
		
	}
	
	
	
	// Updating:
	/**
	 * Called when something is bought.
	 * 
	 * @param spent amount spent
	 */
	public void notifyBuy(Double spent) {

		
		buyCoins+= spent;
		
		// Refresh signs:
		refreshSigns();
		
		
	}
	
	/**
	 * Checks if the buy limit has been reached.
	 * 
	 * @return true if buy limit has been reached
	 */
	public boolean checkOverBuyLimit() {

		return buyCoins >= getBuyLimit();
		
	}
	
	/**
	 * Get the amount of coins spent in this day.
	 * 
	 * @return amount of coins spent
	 */
	public Double getBuyCoins() {
		return buyCoins;
	}
	
	/**
	 * Gets the buy limit.
	 * 
	 * @return buy limit
	 */
	public Double getBuyLimit() {

		return getDefinition().getFunction(BUY_LIMIT_KEY).value(1);
		
	}
	
	
	
	// Production:
	/* 
	 * Distributes work among exports.
	 * 
	 * @see org.saga.buildings.production.ProductionBuilding#work()
	 */
	@Override
	public void work() {

		
		super.work();

		Settlement settlement = getSettlement();
		if(settlement == null) return;
		
		SagaPricedItem[] exports = EconomyConfiguration.config().getTradingPostExports();
		double workAvail = 0;
		
		// Find remaining work:
		double[] workRemain = new double[exportsWork.length];
		double workTotal = 0;
		for (int i = 0; i < workRemain.length; i++) {
			
			double percent = collectedExports[i] / exports[i].getAmount();
			
			workRemain[i] = exports[i].getRequiredWork()*percent - exportsWork[i];
			if(workRemain[i] < 0.0) workRemain[i] = 0.0;
			workTotal+= workRemain[i];
			
		}
		
		// Take required role points:
		Set<String> roles = getDefinition().getAllRoles();
		for (String roleName : roles) {
			
			double requested = workTotal - workAvail;
			if(requested >= 0) workAvail+= settlement.takeWorkPoints(roleName, requested);
			
		}
		
		if(workTotal == 0) return;
		
		// Distribute work:
		for (int i = 0; i < workRemain.length; i++) {
			exportsWork[i]+= workAvail * workRemain[i]/workTotal;
		}
		
		
	}
	
	/* 
	 * Collects items to export.
	 * 
	 * @see org.saga.buildings.production.ProductionBuilding#collect()
	 */
	@Override
	public void collect() {
		
		
		super.collect();
		
		ArrayList<Warehouse> warehouses = getChunkBundle().getBuildings(Warehouse.class);
		SagaPricedItem[] exports = EconomyConfiguration.config().getTradingPostExports();

		// Handle requests:
		for (int i = 0; i < exports.length; i++) {
			
			SagaItem requestItem = new SagaItem(exports[i]);
			SagaItem collectedItem = new SagaItem(requestItem);
			collectedItem.setAmount(0.0);
			requestItem.modifyAmount(-collectedExports[i]);
			
			if(requestItem.getAmount() <= 0.0) continue;
			
			for (Warehouse warehouse : warehouses) {
				warehouse.withdraw(requestItem, collectedItem);
			}
			
			collectedExports[i]+= collectedItem.getAmount();
			
		}
		
		// Only loaded:
		if(getSagaChunk().isChunkLoaded()){
			
			// Update buy signs:
			Collection<BuySign> buySigns = getBuildingSigns(BuySign.class);
			for (BuySign buySign : buySigns) {
				buySign.collect(warehouses);
			}
			
		}
		
		
	}
	
	/* 
	 * Sells everything.
	 * 
	 * @see org.saga.buildings.production.ProductionBuilding#produce()
	 */
	@Override
	public void produce() {
		
		
		super.produce();
		
		SagaPricedItem[] exports = EconomyConfiguration.config().getTradingPostExports();
		Double coins = 0.0;
		
		ArrayList<SagaItem> exported = new ArrayList<SagaItem>();
		
		// Export:
		for (int i = 0; i < exports.length; i++) {
			
			// Check work requirement:
			if(exportsWork[i] < exports[i].getRequiredWork()) continue;
			
			// Amounts and cost:
			Double cost = calcCost(i);
			double amount = collectedExports[i];
			if(amount <= 0.0) continue;
			
			// Export:
			SagaItem export = new SagaItem(exports[i]);
			export.setAmount(collectedExports[i]);
			exported.add(export);
			
			// Add coins:
			coins+= cost;
			
			// Take:
			collectedExports[i]-= amount;
			exportsWork[i] = 0.0;
			
		}
		
		// Pay coins:
		if(getChunkBundle() instanceof Settlement){
			
			Settlement settlement = (Settlement) getChunkBundle();
			if(coins >= 0.0) settlement.payCoins(coins);
			
			// Inform:
			if(exported.size() != 0) settlement.information(this, EconomyMessages.exported(exported, coins));
			
		}
		
		
	}
	
	/**
	 * Calculates the export cost for the given index.
	 * 
	 * @param index export index
	 * @return export cost
	 */
	public double calcCost(int index) {
		
		SagaPricedItem[] exports = EconomyConfiguration.config().getTradingPostExports();
		
		Double price = exports[index].getPrice();
		double amount = collectedExports[index];
		if(amount < 0.0) return 0.0;
		
		return amount * price;
		
	}
	
	/* 
	 * Sells the item.
	 * 
	 * @see org.saga.buildings.production.ProductionBuilding#offer(org.saga.buildings.production.SagaItem)
	 */
	@Override
	public void offer(SagaItem item) {

		SagaPricedItem[] exports = EconomyConfiguration.config().getTradingPostExports();
		
		for (int i = 0; i < exports.length; i++) {
			
			if(!exports[i].checkRepresents(item)) continue;
			
			double amount = item.getAmount();
			if(collectedExports[i] + amount > exports[i].getAmount()) amount = exports[i].getAmount() - collectedExports[i];
			
			item.modifyAmount(-amount);
			collectedExports[i] = collectedExports[i] + amount;
			
		}
		
	}
	
	
	
	// Exports:
	/**
	 * Gets exports amounts.
	 * 
	 * @return exports amounts
	 */
	public double[] getForExport() {
		return collectedExports;
	}
	
	/**
	 * Gets the amount of work for the given index export
	 * 
	 * @param index export index
	 * @return work done
	 * @throws IndexOutOfBoundsException when the index is out of bounds
	 */
	public double getWork(int index) throws IndexOutOfBoundsException{
		return exportsWork[index];
	}
	
	/**
	 * Gets the exports length.
	 * 
	 * @return exports length
	 */
	public int exportsLength() {
		return exportsWork.length;
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
