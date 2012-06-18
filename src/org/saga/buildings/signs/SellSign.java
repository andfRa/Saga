package org.saga.buildings.signs;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.saga.SagaLogger;
import org.saga.buildings.Building;
import org.saga.buildings.TradingPost;
import org.saga.economy.Trader;
import org.saga.messages.EconomyMessages;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;


public class SellSign extends BuildingSign {


	/**
	 * Name for the 
	 */
	public static String SIGN_NAME = "=[SELL]=";
	
	/**
	 * Name for the 
	 */
	public static String MATERIAL_VALUE_DIV = "x";
	
	
	/**
	 * Material.
	 */
	transient private Material material = null;

	/**
	 * Amount.
	 */
	transient private Integer amount = null;

	/**
	 * Trader.
	 */
	transient private Trader trader = null;

	
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
		
		initialiseFields();
		
	}
	
	/**
	 * Creates the training 
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
		
		initialiseFields();
		
		return true;
		
	}
	

	/**
	 * Initialises fields.
	 * 
	 */
	private void initialiseFields() {

		
		// First parameter:
		String[] firstParameter = getFirstParameter().split(MATERIAL_VALUE_DIV);
		
		String sAmount = null;
		String sMaterial = null;
		
		// Arguments:
		if(firstParameter.length == 2){
			
			// Material:
			sMaterial = firstParameter[1];
			material = Material.matchMaterial(sMaterial);
			if(material == null){
				try {
					material = Material.getMaterial(Integer.parseInt(sMaterial));
				} catch (NumberFormatException e) { }
			}
			
			// Amount:
			sAmount = firstParameter[0];
			try {
				amount = Integer.parseInt(sAmount);
			} catch (NumberFormatException e) { }
			
		}else{

			// Material:
			sMaterial = firstParameter[0];
			material = Material.matchMaterial(sMaterial);
			if(material == null){
				try {
					material = Material.getMaterial(Integer.parseInt(sMaterial));
				} catch (NumberFormatException e) { }
			}
			if(material == null) return;
			
			amount = material.getMaxStackSize();
			
		}
		
		// Trader:
		if(!(getBuilding() instanceof Trader)){
			SagaLogger.severe(this, TradingPost.class.getSimpleName() + " building required");
		}else{
			trader = (Trader) getBuilding();
		}
		
		// Fix amount:
		if(amount != null && amount < 0) amount = 0;
			
		
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
		

		if(material == null || amount == null || trader == null) return SignStatus.INVALIDATED;
		
		if(trader.getSellPrice(material) == null) return SignStatus.DISABLED;
		
		return SignStatus.ENABLED;
	
		
	}
	
	@Override
	public String getLine(int index, SignStatus status) {
	
		
		switch (status) {
			
			case ENABLED:
				
				Double price = trader.getSellPrice(material);
				
				if(index == 1) return amount + MATERIAL_VALUE_DIV + EconomyMessages.materialShort(material);
				if(index == 2) return "price: " + EconomyMessages.coins(price);
				if(index == 3) return "bank: " + EconomyMessages.coins(trader.getCoins());
				break;
			
			case DISABLED:
				
				if(index == 1) return amount + MATERIAL_VALUE_DIV + EconomyMessages.materialShort(material);
				if(index == 2) return "price: -";
				if(index == 3) return "bank: " + EconomyMessages.coins(trader.getCoins());
				break;
				
			default:
				
				return "-";

		}

		return "";
		
		
	}

	
	// Custom parameters:
	/**
	 * Gets the material.
	 * 
	 * @return the material, null if none
	 */
	public Material getMaterial() {
		return material;
	}
	
	
	// Interaction:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#onRightClick(org.saga.player.SagaPlayer)
	 */
	@Override
	protected void onRightClick(SagaPlayer sagaPlayer) {

		
		// Used amount:
		Integer usedAmount = null;
		if(sagaPlayer.getAmount(material) < amount){
			usedAmount = sagaPlayer.getAmount(material);
		}else{
			usedAmount = amount;
		}
		if(usedAmount < 1){
			sagaPlayer.message(EconomyMessages.insufItems(material));
			return;
		}
		
		// Used coins:
		Double price = trader.getSellPrice(material);
		if(price == null){
			return;
		}
		
		while(trader.getCoins() < price * usedAmount && usedAmount > 0){
			usedAmount--; 
		}
		
		if(usedAmount < 1){
			sagaPlayer.message(EconomyMessages.insufCoins(trader));
			return;
		}
		
		ItemStack item = new ItemStack(material, usedAmount);
		
		// Transaction:
		sagaPlayer.addCoins(price * usedAmount);
		sagaPlayer.removeItem(item);
		trader.addItem(item);
		trader.removeCoins(price * usedAmount);
		
		// Inform:
		sagaPlayer.message(EconomyMessages.sold(material, usedAmount, price));
		
		// Notify transaction:
		trader.notifyTransaction();

		// Statistics:
		StatisticsManager.manager().onPlayerSell(sagaPlayer, material, usedAmount, price * usedAmount);
		
		
	}
	
	
}
