package org.saga.buildings.signs;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.saga.buildings.Building;
import org.saga.buildings.TradingPost;
import org.saga.config.EconomyConfiguration;
import org.saga.messages.EconomyMessages;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;


public class BuySign extends BuildingSign {


	/**
	 * Name for the sign.
	 */
	public static String SIGN_NAME = "=[BUY]=";
	
	/**
	 * Material and amount division.
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
	public static BuySign create(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building) {
		return new BuySign(sign, secondLine, thirdLine, fourthLine, building);
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
			} catch (NumberFormatException e) {}
			
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
		

		if(material == null || amount == null) return SignStatus.INVALIDATED;
		
		if(EconomyConfiguration.config().getPrice(material) == null) return SignStatus.INVALIDATED;
		
		// Buy limit:
		if(getBuilding() instanceof TradingPost){
			
			if(((TradingPost) getBuilding()).checkOverBuyLimit()) return SignStatus.DISABLED;

		}
		
		return SignStatus.ENABLED;
	
		
	}
	
	@Override
	public String getLine(int index, SignStatus status) {
	
		
		Double price = null;
		if(material != null) price = EconomyConfiguration.config().getPrice(material);
		if(price != null) price*= EconomyConfiguration.config().getBuyMult();
		
		switch (status) {
			
				
			case ENABLED:
				
				
				if(index == 1) return amount + MATERIAL_VALUE_DIV + EconomyMessages.materialShort(material);
				if(index == 2) return "price: " + EconomyMessages.coins(price);
				
				Double perc = 1.0;
				if(getBuilding() instanceof TradingPost){
					
					TradingPost tpost = (TradingPost) getBuilding();
					
					perc = (1 - tpost.getBuyCoins() / tpost.getBuyLimit()) * 100;
					
				}
				if(index == 3) return "goods: " + perc.intValue() + "%";
				
				break;
				
			case DISABLED:
				
				if(index == 1) return amount + MATERIAL_VALUE_DIV + EconomyMessages.materialShort(material);
				if(index == 2) return "price: " + EconomyMessages.coins(price);
				if(index == 3) return "come back later";
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
		Integer usedAmount = amount;
		
		// Used coins:
		Double price = EconomyConfiguration.config().getPrice(material);
		if(price == null) return;
		price*= EconomyConfiguration.config().getBuyMult();
		
		while(sagaPlayer.getCoins() < price * usedAmount && usedAmount > 0){
			usedAmount--; 
		}
		
		if(usedAmount < 1){
			sagaPlayer.message(EconomyMessages.insufCoins());
			return;
		}
		
		ItemStack item = new ItemStack(material, usedAmount);
		
		// Transaction:
		sagaPlayer.removeCoins(price * usedAmount);
		sagaPlayer.addItem(item);
		
		// Inform:
		sagaPlayer.message(EconomyMessages.bought(material, usedAmount, price));
		
		// Notify transaction:
		if(getBuilding() instanceof TradingPost){
			((TradingPost) getBuilding()).notifyBuy(price * usedAmount);
		}
		
		// Statistics:
		StatisticsManager.manager().onPlayerBuy(sagaPlayer, material, usedAmount, price * usedAmount);
		
		
	}

	
	
}
