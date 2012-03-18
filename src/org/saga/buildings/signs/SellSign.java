package org.saga.buildings.signs;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.saga.Saga;
import org.saga.buildings.Building;
import org.saga.buildings.TradingPost;
import org.saga.economy.EconomyMessages;
import org.saga.economy.Trader;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;


public class SellSign extends BuildingSign {


	/**
	 * Name for the sign.
	 */
	public static String SIGN_NAME = "=[SELL]=";
	
	/**
	 * Name for the sign.
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

	
	// Initialization:
	/**
	 * Creates a learning sign.
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
		
	}
	
	/**
	 * Creates the training sign.
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
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#enable()
	 */
	@Override
	public void enable() {

		
		super.enable();
		
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
			if(material == null){
				invalidate();
				return;
			}
			
			// Amount:
			sAmount = firstParameter[0];
			try {
				amount = Integer.parseInt(sAmount);
			} catch (NumberFormatException e) {
				invalidate();
				return;
			}
			
		}else{

			// Material:
			sMaterial = firstParameter[0];
			material = Material.matchMaterial(sMaterial);
			if(material == null){
				try {
					material = Material.getMaterial(Integer.parseInt(sMaterial));
				} catch (NumberFormatException e) { }
			}
			if(material == null){
				invalidate();
				return;
			}
			
			amount = material.getMaxStackSize();
			
		}
		
		// Trader:
		if(!(getBuilding() instanceof Trader)){
			Saga.severe(this, TradingPost.class.getSimpleName() + " required", "ignoring transaction and invalidating the sign");
			invalidate();
			return;
		}
		trader = (Trader) getBuilding();
		
		// Fix amount:
		if(amount < 0) amount = 0;
		
		// Refresh:
		refresh();
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#disable()
	 */
	@Override
	public void disable() {
	
	
		super.disable();

		if(material == null || amount == null || trader == null) return;
		
		// Sign:
		Sign sign = getSign();

		sign.setLine(1, amount + MATERIAL_VALUE_DIV + EconomyMessages.materialShort(material));
		
		sign.setLine(2, "price: " + "-" + EconomyMessages.coins());

		sign.setLine(3, "bank: " + EconomyMessages.coins(trader.getCoins()));

		sign.update();
		
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#refresh()
	 */
	@Override
	public void refresh() {
	
		
		super.refresh();
		
		if(material == null || amount == null || trader == null) return;
		
		// Price:
		Double price = trader.getSellPrice(material);
		if(price == null){
			disable();
			return;
		}else if(!isEnabled()){
			enable();
		}

		// Sign:
		Sign sign = getSign();

		sign.setLine(1, amount + MATERIAL_VALUE_DIV + EconomyMessages.materialShort(material));
		
		sign.setLine(2, "price: " + EconomyMessages.coins(price));

		sign.setLine(3, "bank: " + EconomyMessages.coins(trader.getCoins()));

		sign.update();
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#invalidate()
	 */
	@Override
	public void invalidate() {
		
		
		super.invalidate();
		
		Sign sign = getSign();

		sign.setLine(1, "-" + MATERIAL_VALUE_DIV + "-");
		
		sign.setLine(2, "price: " + "-" + EconomyMessages.coins());

		sign.setLine(3, "bank: " + "-" + EconomyMessages.coins());

		sign.update();
		
		
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
			disable();
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
