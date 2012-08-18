package org.saga.buildings.signs;

import org.bukkit.block.Sign;
import org.saga.buildings.Building;
import org.saga.config.AttributeConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.messages.BuildingMessages;
import org.saga.messages.EconomyMessages;
import org.saga.messages.effects.AbilityEffects;
import org.saga.player.SagaPlayer;


public class ResetSign extends BuildingSign{

	
	/**
	 * Name for the 
	 */
	public static String SIGN_NAME = "=[RESET]=";

	/**
	 * Amount division string.
	 */
	public static String AMOUNT_DIV = "\\*";
	
	/**
	 * Displayed amount division string.
	 */
	public static String AMOUNT_DIV_DISPLAY = "*";
	
	
	
	// Initialisation:
	/**
	 * Creates a stone 
	 * 
	 * @param type transaction type
	 * @param sign sign
	 * @param secondLine second line
	 * @param thirdLine third line
	 * @param fourthLine fourth line
	 * @param event event that created the sign
	 * @param building building
	 */
	private ResetSign(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building){
	
		super(sign, SIGN_NAME, secondLine, thirdLine, fourthLine, building);
		
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
	public static ResetSign create(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building) {
		return new ResetSign(sign, secondLine, thirdLine, fourthLine, building);
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
	
		
		String attribute = getAttribute();
		Integer amount = getAmount();
		
		if(!AttributeConfiguration.config().getAttributeNames().contains(attribute) || amount == -1) return SignStatus.INVALIDATED;
		
		return SignStatus.ENABLED;
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#getLine(int, org.saga.buildings.signs.BuildingSign.SignStatus)
	 */
	@Override
	public String getLine(int index, SignStatus status) {
	
		
		Double cost = getResetCost();
		
		switch (status) {

			case ENABLED:

				if(index == 1) return getAmount() + AMOUNT_DIV_DISPLAY + getAttribute();
				if(index == 3 && cost > 0.0) return "cost: " + EconomyMessages.coins(cost);
				break;

			case DISABLED:

				if(index == 1) return getFirstParameter();
				if(index == 3 && cost > 0.0) return "cost: " + EconomyMessages.coins(cost);
				break;
				
			default:
				
				if(index == 1) return "-";
				if(index == 3) return "-";
				break;

		}

		return "";
		
		
	}
	
	/**
	 * Gets amount to reset.
	 * 
	 * @return amount to reset, -1 if invalid
	 */
	private Integer getAmount() {

		
		String[] firstParam = getFirstParameter().split(AMOUNT_DIV);
		if(firstParam.length < 2) return 5;
		
		try {
			return Integer.parseInt(firstParam[0]);
		}
		catch (NumberFormatException e) {
			return -1;
		}

		
	}
	
	/**
	 * Gets the attribute name.
	 * 
	 * @return attribute
	 */
	private String getAttribute() {

		
		String[] firstParam = getFirstParameter().split(AMOUNT_DIV);
		if(firstParam.length < 2) return firstParam[0];
		
		return firstParam[1];

		
	}
	
	/**
	 * Gets reset cost.
	 * 
	 * @return reset cost, negative if invalid
	 */
	private Double getResetCost() {
		
		return getResetCost(getAmount());
		
	}
	
	/**
	 * Gets reset cost.
	 * 
	 * @param amount amount to reset
	 * @return reset cost
	 */
	private Double getResetCost(Integer amount) {
		
		return EconomyConfiguration.config().getResetCost() * amount;
		
	}
	
	
	
	// Events:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#onRightClick(org.saga.player.SagaPlayer)
	 */
	@Override
	protected void onRightClick(SagaPlayer sagaPlayer) {

		
		String attribute = getAttribute();
		Integer attributeScore = sagaPlayer.getRawAttributeScore(attribute);
		
		// Already minimum:
		if(attributeScore <= 0){
			sagaPlayer.message(BuildingMessages.attrAlreadyReset(attribute));
			return;
		}

		// Amount to reset:
		Integer amount = getAmount();
		if(amount > attributeScore) amount = attributeScore;
		
		// Enough coins:
		Double cost = getResetCost(amount);
		if(sagaPlayer.getCoins() < cost){
			sagaPlayer.message(EconomyMessages.notEnoughCoins());
			return;
		}

		// Take coins:
		sagaPlayer.removeCoins(cost);
		sagaPlayer.message(EconomyMessages.coinsSpent(cost));
		
		// Reset:
		sagaPlayer.setAttributeScore(attribute, attributeScore - amount);
		
		// Inform:
		sagaPlayer.message(BuildingMessages.resetAttr(attribute, attributeScore - amount));
		
		// Play effect:
		AbilityEffects.playSign(sagaPlayer);
		
		
	}
	
	
}
