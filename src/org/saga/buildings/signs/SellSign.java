package org.saga.buildings.signs;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.saga.SagaLogger;
import org.saga.buildings.Building;
import org.saga.buildings.Warehouse;
import org.saga.buildings.production.SagaItem;
import org.saga.config.EconomyConfiguration;
import org.saga.config.SettlementConfiguration;
import org.saga.dependencies.EconomyDependency;
import org.saga.messages.EconomyMessages;
import org.saga.messages.GeneralMessages;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Settlement;
import org.saga.statistics.StatisticsManager;


public class SellSign extends BuildingSign {


	/**
	 * Name for the sign.
	 */
	public static String SIGN_NAME = "=[SELL]=";

	/**
	 * Amount division string.
	 */
	public static String DATA_DIV = ":";
	
	/**
	 * Amount division string.
	 */
	public static String AMOUNT_DIV = "\\*";
	
	/**
	 * Displayed amount division string.
	 */
	public static String AMOUNT_DIV_DISPLAY = "*";
	

	/**
	 * Amount of coins stored.
	 */
	private Double coins;

	/**
	 * Maximum amount of coins stored.
	 */
	private Double maxCoins;

	/**
	 * Amount of goods pending.
	 */
	private Double pending;

	
	/**
	 * Saga item.
	 */
	transient SagaItem item = null;

	/**
	 * Amount.
	 */
	transient private Double price = null;


	
	// Initialisation:
	/**
	 * Creates a learning 
	 * 
	 * @param sign sign
	 * @param secondLine second line
	 * @param thirdLine third line
	 * @param fourthLine fourth line
	 * @param event event that created the sign
	 * @param building building
	 */
	protected SellSign(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building){
	
		super(sign, SIGN_NAME, secondLine, thirdLine, fourthLine, building);
		
		coins = 0.0;
		pending = 0.0;
		
		initialiseFields();
		
	}
	
	/**
	 * Creates the buy sign. 
	 * 
	 * @param sign bukkit sign
	 * @param firstLine first line
	 * @param secondLine second line
	 * @param thirdLine third line
	 * @param fourthLine fourth line
	 * @param building building
	 * @return training sign
	 */
	public static SellSign create(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building) {
		return new SellSign(sign, secondLine, thirdLine, fourthLine, building);
	}

	@Override
	public boolean complete(Building building) throws SignException {
		
		
		super.complete(building);

		if(coins== null){
			SagaLogger.nullField(this, "coins");
			coins = 0.0;
		}
		
		if(maxCoins== null){
			SagaLogger.nullField(this, "maxCoins");
			maxCoins = 0.0;
		}
		
		if(pending== null){
			SagaLogger.nullField(this, "pending");
			pending = 0.0;
		}
		
		initialiseFields();
		
		return true;
		
		
	}
	
	/**
	 * Initialises fields.
	 * 
	 */
	private void initialiseFields() {

		
		Material type = null;
		Short data = null;
		Double amount = null;
		
		// First parameter:
		String[] firstParameter = getFirstParameter().split(AMOUNT_DIV);

		String sAmount = null;
		String sMaterial = null;
		String sData = null;
		String sPrice = null;
		String sMaxCoins = null;
		
		// Amount, type and data:
		if(firstParameter.length == 2){
			
			String[] sMatData = firstParameter[1].split(DATA_DIV);
			
			// Material:
			sMaterial = sMatData[0];
			type = Material.matchMaterial(sMaterial);
			if(type == null){
				try {
					type = Material.getMaterial(Integer.parseInt(sMaterial));
				} catch (NumberFormatException e) { }
			}
			
			// Data:
			if(sMatData.length > 1){
				sData = sMatData[1];
				try {
					data = Short.parseShort(sData);
				} catch (NumberFormatException e) { }
			}
			
			// Amount:
			sAmount = firstParameter[0];
			try {
				amount = Double.parseDouble(sAmount);
			} catch (NumberFormatException e) {}
			
		}else{

			String[] sMatData = firstParameter[0].split(DATA_DIV);
			
			// Material:
			sMaterial = sMatData[0];
			type = Material.matchMaterial(sMaterial);
			if(type == null){
				try {
					type = Material.getMaterial(Integer.parseInt(sMaterial));
				} catch (NumberFormatException e) { }
			}
			if(type == null) return;

			// Data:
			if(sMatData.length > 1){
				sData = sMatData[1];
				try {
					data = Short.parseShort(sData);
				} catch (NumberFormatException e) { }
			}
			
			amount = (double)type.getMaxStackSize();
			
		}

		// Price:
		sPrice = getSecondParameter();
		try {
			price = Double.parseDouble(sPrice);
		} catch (NumberFormatException e) {}
		
		// Coins:
		sMaxCoins = getThirdParameter();
		try {
			maxCoins = Double.parseDouble(sMaxCoins);
		} catch (NumberFormatException e) {}
		
		// Fix amount:
		if(amount != null && amount < 0) amount = 0.0;
		
		// Fix data:
		if(data == null) data = 0;
		
		// Fix max coins:
		if(maxCoins == null) maxCoins = 64.0;
		if(maxCoins <= 0.0) maxCoins = 1.0;
		
		// Round coins:
		maxCoins = (double)maxCoins.intValue();
		
		this.item = new SagaItem(type, amount.doubleValue(), data);
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#getName()
	 */
	@Override
	public String getName() {
		return SIGN_NAME;
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#getStatus()
	 */
	@Override
	public SignStatus getStatus() {

		if(maxCoins == null || item.getType() == null || item.getType() == Material.AIR || item.getData() == null || item.getAmount() == null || price == null) return SignStatus.INVALIDATED;
		
		if(coins <= 0 || !EconomyConfiguration.config().isEnabled()) return SignStatus.DISABLED;
		
		return SignStatus.ENABLED;
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#getLine(int, org.saga.buildings.signs.BuildingSign.SignStatus)
	 */
	@Override
	public String getLine(int index, SignStatus status) {
	
		
		switch (status) {
				
			case ENABLED:
				
				if(index == 1) return item.getAmount().intValue() + AMOUNT_DIV_DISPLAY + GeneralMessages.materialAbrev(item.getType());
				if(index == 2) return "price: " + EconomyMessages.coins(price);
				if(index == 3) return coins.intValue() + "/" + EconomyMessages.coins((double)maxCoins.intValue());
				
				break;
				
			case DISABLED:
				
				if(index == 1) return item.getAmount().intValue() + AMOUNT_DIV_DISPLAY + GeneralMessages.materialAbrev(item.getType());
				if(index == 2) return "price: " + EconomyMessages.coins(price);
				if(index == 3) return "come back later";
				break;
			
			case INVALIDATED:
				
				if(index == 1) return SettlementConfiguration.config().invalidSignColor + "amt" + AMOUNT_DIV_DISPLAY + "item/ID";
				if(index == 2) return SettlementConfiguration.config().invalidSignColor + "price";
				if(index == 3) return SettlementConfiguration.config().invalidSignColor + "max coins";
				
			break;
				
			default:
				
				return "-";

		}

		return "";
		
		
	}
	
	
	
	// Operation:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#onRightClick(org.saga.player.SagaPlayer)
	 */
	@Override
	protected void onRightClick(SagaPlayer sagaPlayer) {

		
		SagaItem request = new SagaItem(item);
		
		// Available coins:
		if(price > 0 && request.getAmount()*price > coins) request.setAmount(coins/price);
		
		// Transaction::
		SagaItem takenItem = sagaPlayer.takeItem(request);
		pending+= takenItem.getAmount();
		Double cost = price*takenItem.getAmount();
		EconomyDependency.addCoins(sagaPlayer, cost);
		coins-= cost;
		
		// Inform:
		sagaPlayer.message(EconomyMessages.sold(item.getType(), takenItem.getAmount().intValue(), price));
		
		// Statistics:
		StatisticsManager.manager().addSell(sagaPlayer, item.getType(), takenItem.getAmount().intValue(), cost);
		
		// Update sign:
		refresh();

		
	}
	
	/**
	 * Collects coins and puts all collected resources to warehouses.
	 * 
	 * @param warehouses warehouses
	 */
	public void collect(Collection<Warehouse> warehouses) {
		
		
		// Request coins:
		Settlement settlement = getBuilding().getSettlement();
		if(settlement != null){
			double ret = settlement.requestCoins(maxCoins - coins);
			coins+= ret;
		}
		
		// Give to warehouse:
		if(pending > 0.0){

			SagaItem remaining = new SagaItem(item);
			remaining.setAmount(pending);
			
			for (Warehouse warehouse : warehouses) {
				warehouse.store(remaining);
				if(remaining.getAmount() <= 0.0) break;
			}
			
			pending = remaining.getAmount();
			
		}

		refresh();
		
		
	}
	
	
	// Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if(item != null)return "{" + item.getAmount() + "," + item.getType() + ":" + item.getData() + "," + price + "}";
		return super.toString();
	}

	
}
