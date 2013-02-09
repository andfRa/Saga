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
import org.saga.buildings.signs.ExportSign;
import org.saga.buildings.signs.ImportSign;
import org.saga.buildings.signs.SellSign;
import org.saga.config.EconomyConfiguration;
import org.saga.exceptions.InvalidBuildingException;
import org.saga.messages.EconomyMessages;
import org.saga.settlements.Settlement;


public class TradingPost extends ProductionBuilding implements DaytimeTicker{
	

	/**
	 * Import limit key.
	 */
	private static String IMPORT_LIMIT_KEY = "import limit";

	/**
	 * Export limit key.
	 */
	private static String EXPORT_LIMIT_KEY = "export limit";
	
	
	
	/**
	 * Coins spend for importing.
	 */
	private Double importCoins;

	/**
	 * Coins spend for exporting.
	 */
	private Double exportCoins;
	
	
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
	
		importCoins = 0.0;
		exportCoins = 0.0;
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
		
		
		if(importCoins == null){
			SagaLogger.nullField(this, "importCoins");
			importCoins = 0.0;
		}

		if(exportCoins == null){
			SagaLogger.nullField(this, "exportCoins");
			exportCoins = 0.0;
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
		
		boolean integrity = super.complete();

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
		importCoins = 0.0;
		exportCoins = 0.0;

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
		if(firstLine.equalsIgnoreCase(SellSign.SIGN_NAME)) return true;
		if(firstLine.equalsIgnoreCase(ImportSign.SIGN_NAME)) return true;
		if(firstLine.equalsIgnoreCase(ExportSign.SIGN_NAME)) return true;
		
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
		if(event.getLine(0).equalsIgnoreCase(SellSign.SIGN_NAME)) return SellSign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
		if(event.getLine(0).equalsIgnoreCase(ImportSign.SIGN_NAME)) return ImportSign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
		if(event.getLine(0).equalsIgnoreCase(ExportSign.SIGN_NAME)) return ExportSign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
		
		return super.createBuildingSign(sign, event);
		
	}
	
	
	
	// Updating:
	/**
	 * Called when something is imported.
	 * 
	 * @param spent amount spent
	 */
	public void notifyImport(Double spent) {
		
		importCoins+= spent;
		
		// Refresh signs:
		refreshSigns();
		
	}
	
	/**
	 * Called when something is exported.
	 * 
	 * @param spent amount spent
	 */
	public void notifyExport(Double spent) {
		
		exportCoins+= spent;
		
		// Refresh signs:
		refreshSigns();
		
	}

	/**
	 * Checks if the buy limit has been reached.
	 * 
	 * @return true if buy limit has been reached
	 */
	public boolean checkOverImportLimit() {
		return importCoins >= getImportLimit();
	}

	/**
	 * Checks if the buy limit has been reached.
	 * 
	 * @return true if buy limit has been reached
	 */
	public boolean checkOverExportLimit() {
		return exportCoins >= getExportLimit();
	}
	
	/**
	 * Get the amount of coins spent on imports in this day.
	 * 
	 * @return amount of coins spent on imports
	 */
	public Double getImportCoins() {
		return importCoins;
	}
	
	/**
	 * Get the amount of coins spent on exports in this day.
	 * 
	 * @return amount of coins spent on exports
	 */
	public Double getExportCoins() {
		return exportCoins;
	}

	/**
	 * Gets the import limit.
	 * 
	 * @return import limit
	 */
	public Double getImportLimit() {
		return getDefinition().getFunction(IMPORT_LIMIT_KEY).value(1);
	}

	/**
	 * Gets the export limit.
	 * 
	 * @return export limit
	 */
	public Double getExportLimit() {
		return getDefinition().getFunction(EXPORT_LIMIT_KEY).value(1);
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

			// Update sell signs:
			Collection<SellSign> sellSigns = getBuildingSigns(SellSign.class);
			for (SellSign sellSign : sellSigns) {
				sellSign.collect(warehouses);
			}

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
