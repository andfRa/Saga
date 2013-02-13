package org.saga.buildings.signs;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
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
import org.saga.statistics.StatisticsManager;


public class BuySign extends BuildingSign {


	/**
	 * Name for the sign.
	 */
	public static String SIGN_NAME = "=[BUY]=";

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
	 * Amount of items stored.
	 */
	private Double stored;

	/**
	 * Maximum amount of items stored.
	 */
	private Double maxStored;
	
	
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
	protected BuySign(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building){
	
		super(sign, SIGN_NAME, secondLine, thirdLine, fourthLine, building);
		
		initialiseFields();
		stored = 0.0;
		
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
	public static BuySign create(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building) {
		return new BuySign(sign, secondLine, thirdLine, fourthLine, building);
	}

	@Override
	public boolean complete(Building building) throws SignException {
		
		
		super.complete(building);

		if(stored== null){
			SagaLogger.nullField(this, "stored");
			stored = 0.0;
		}
		
		if(maxStored== null){
			SagaLogger.nullField(this, "maxStored");
			maxStored = 0.0;
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
		String sMaxStored = null;
		
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
		if(sPrice.length() > 0)
			try {
				price = Double.parseDouble(sPrice);
			} catch (NumberFormatException e) {}
		
		// Stored:
		sMaxStored = getThirdParameter();
		if(sMaxStored.length() > 0)
			try {
				maxStored = Double.parseDouble(sMaxStored);
			} catch (NumberFormatException e) {}

		// Check price:
		if(price != null && price < 0) price = null;
		
		// Check amount:
		if(amount != null && amount < 0) amount = null;
		
		// Check maxStored:
		if(maxStored != null && maxStored <= 0.0) maxStored = null;
		
		// Fix data:
		if(data == null) data = 0;
		
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

		if(maxStored == null || item.getType() == null || item.getType() == Material.AIR || item.getData() == null || item.getAmount() == null || price == null) return SignStatus.INVALIDATED;
		
		if(stored <= 0 || !EconomyConfiguration.config().isEnabled()) return SignStatus.DISABLED;
		
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
				if(index == 3) return stored.intValue() + "/" + maxStored.intValue();
				
				break;
				
			case DISABLED:
				
				if(index == 1) return item.getAmount().intValue() + AMOUNT_DIV_DISPLAY + GeneralMessages.materialAbrev(item.getType());
				if(index == 2) return "price: " + EconomyMessages.coins(price);
				if(index == 3) return "come back later";
				break;
			
			case INVALIDATED:
				
				if(index == 1) return SettlementConfiguration.config().invalidSignColor + "amt" + AMOUNT_DIV_DISPLAY + "item/ID";
				if(index == 2) return SettlementConfiguration.config().invalidSignColor + "price";
				if(index == 3) return SettlementConfiguration.config().invalidSignColor + "max amount";
				
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

		
		// Available amount:
		Integer availAmount = item.getAmount().intValue();
		
		// Available coins:
		if(price == null) return;
		
		Double coins = EconomyDependency.getCoins(sagaPlayer);
		while(coins < price * availAmount && availAmount > 0){
			availAmount--; 
		}
		
		if(availAmount < 1){
			sagaPlayer.message(EconomyMessages.insufCoins());
			return;
		}
		
		// Available goods:
		if(availAmount > stored) availAmount = stored.intValue();
		
		if(availAmount < 1){
			sagaPlayer.message(EconomyMessages.insufItems(getBuilding(), item.getType()));
			return;
		}
		
		// Create item:
		ItemStack item = this.item.createItem();
		item.setAmount(availAmount);
		
		// Transaction:
		Double cost = price * availAmount;
		EconomyDependency.removeCoins(sagaPlayer, cost);
		getBuilding().getSettlement().payCoins(cost);
		sagaPlayer.addItem(item);
		stored-= availAmount;
		
		// Inform:
		sagaPlayer.message(EconomyMessages.bought(item.getType(), availAmount, price));
		
		// Statistics:
		StatisticsManager.manager().addBuy(sagaPlayer, item.getType(), availAmount, price * availAmount);
		
		// Update sign:
		refresh();

		
	}
	
	/**
	 * Collects all needed resources from warehouses.
	 * 
	 * @param warehouses warehouses
	 */
	public void collect(Collection<Warehouse> warehouses) {

		double req = maxStored - stored;
		if(req <= 0) return;
		
		SagaItem reqItem = new SagaItem(item);
		reqItem.setAmount(req);
		
		SagaItem colItem = new SagaItem(item);
		colItem.setAmount(0.0);
		
		for (Warehouse warehouse : warehouses) {
			warehouse.withdraw(reqItem, colItem);
		}
		
		stored+= colItem.getAmount();
		
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
