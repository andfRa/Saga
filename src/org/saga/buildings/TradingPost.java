package org.saga.buildings;

import java.util.ArrayList;

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
	 * Export amounts.
	 */
	private double[] exportAmounts;
	
	
	
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
		exportAmounts = new double[EconomyConfiguration.config().getTradingPostExports().length];
		
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
		
		if(exportAmounts == null){
			SagaLogger.nullField(this, "exportAmounts");
			exportAmounts = new double[EconomyConfiguration.config().getTradingPostExports().length];
		}
		
		if(exportAmounts.length != EconomyConfiguration.config().getTradingPostExports().length){
			SagaLogger.warning(this, "resetting exportAmounts");
			exportAmounts = new double[EconomyConfiguration.config().getTradingPostExports().length];
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
	 * Sells everything.
	 * 
	 * @see org.saga.buildings.production.ProductionBuilding#produce()
	 */
	@Override
	public void produce() {
		
		
		SagaPricedItem[] exports = EconomyConfiguration.config().getTradingPostExports();
		Double coins = 0.0;
		
		ArrayList<SagaItem> exported = new ArrayList<SagaItem>();
		
		// Export:
		for (int i = 0; i < exports.length; i++) {
			
			Double price = exports[i].getPrice();
			double amount = exportAmounts[i];
			if(amount <= 0.0) continue;
			
			SagaItem export = new SagaItem(exports[i]);
			export.setAmount(exportAmounts[i]);
			exported.add(export);
			
			coins+= amount * price;
			exportAmounts[i]-= amount;
			
		}
		
		// Pay coins:
		if(getChunkBundle() instanceof Settlement){
			
			Settlement settlement = (Settlement) getChunkBundle();
			if(coins >= 0.0) settlement.payCoins(coins);
			
			// Inform:
			if(exported.size() != 0) settlement.information(this, EconomyMessages.exported(exported, coins));
			
		}
		
		
		
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
			if(exportAmounts[i] + amount > exports[i].getAmount()) amount = exports[i].getAmount() - exportAmounts[i];
			
			item.modifyAmount(-amount);
			exportAmounts[i] = exportAmounts[i] + amount;
			
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
