package org.saga.buildings;

import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.saga.Clock.DaytimeTicker;
import org.saga.SagaLogger;
import org.saga.buildings.signs.BuildingSign;
import org.saga.buildings.signs.BuySign;
import org.saga.exceptions.InvalidBuildingException;


public class TradingPost extends Building implements DaytimeTicker{
	
	
	/**
	 * Buy limit key.
	 */
	private static String BUY_LIMIT = "buy limit";
	
	
	
	/**
	 * Coins spend for buying.
	 */
	private Double buyCoins;
	
	
	
	// Initialisation:
	/**
	 * Creates a building from the definition.
	 * 
	 * @param definition building definition
	 */
	public TradingPost(BuildingDefinition definition) {
		
		
		super(definition);
	
		buyCoins = 0.0;
		
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#completeExtended()
	 */
	@Override
	public boolean complete() throws InvalidBuildingException {
		

		boolean integrity = super.complete();
		
		if(buyCoins== null){
			SagaLogger.nullField(this, "buyCoins");
			buyCoins = 0.0;
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
		if(event.getLine(0).equalsIgnoreCase(BuySign.SIGN_NAME)){
			
			return BuySign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
			
		}
		
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

		return getDefinition().getFunction(BUY_LIMIT).value(getScore());
		
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
