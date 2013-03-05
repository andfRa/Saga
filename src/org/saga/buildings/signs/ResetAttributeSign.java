package org.saga.buildings.signs;

import org.bukkit.block.Sign;
import org.saga.buildings.Building;
import org.saga.config.AttributeConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.config.SettlementConfiguration;
import org.saga.dependencies.EconomyDependency;
import org.saga.messages.BuildingMessages;
import org.saga.messages.EconomyMessages;
import org.saga.messages.effects.StatsEffectHandler;
import org.saga.player.SagaPlayer;


public class ResetAttributeSign extends BuildingSign{

	
	/**
	 * Name for the sign.
	 */
	public static String SIGN_NAME = "=RESET_ATTR=";

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
	private ResetAttributeSign(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building){
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
	public static ResetAttributeSign create(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building) {
		return new ResetAttributeSign(sign, secondLine.toLowerCase(), thirdLine, fourthLine, building);
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
				if(EconomyConfiguration.config().isEnabled())
					if(index == 3 && cost > 0.0) return "cost: " + EconomyMessages.coins(cost);
				break;

			case DISABLED:

				if(index == 1) return getFirstParameter();
				if(EconomyConfiguration.config().isEnabled())
					if(index == 3 && cost > 0.0) return "cost: " + EconomyMessages.coins(cost);
				break;
				
			case INVALIDATED:

				if(index == 1) return SettlementConfiguration.config().invalidSignColor + "amt" + AMOUNT_DIV + "attribute";
				break;
				
			default:
				
				if(index == 1) return "-";
				if(EconomyConfiguration.config().isEnabled())
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
			return Math.abs(Integer.parseInt(firstParam[0]));
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
		return EconomyConfiguration.config().getAttributeResetCost() * amount;
	}
	
	
	
	// Events:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#onRightClick(org.saga.player.SagaPlayer)
	 */
	@Override
	protected void onRightClick(SagaPlayer sagaPlayer) {

		
		String attrName = getAttribute();
		Integer score = sagaPlayer.getRawAttributeScore(attrName);
		Integer nextScore = score - getAmount();
		
		// Already minimum:
		if(score <= 0){
			sagaPlayer.message(BuildingMessages.attrAlreadyReset(attrName));
			return;
		}

		// Normalise next score:
		if(nextScore < 0) nextScore = 0;
		
		// Economy:
		if(EconomyConfiguration.config().isEnabled()){

			// Enough coins:
			Double cost = getResetCost();
			if(EconomyDependency.getCoins(sagaPlayer) < cost){
				sagaPlayer.message(EconomyMessages.insufficient());
				return;
			}

			// Take coins:
			EconomyDependency.removeCoins(sagaPlayer, cost);
			sagaPlayer.message(EconomyMessages.spent(cost));
			
		}
		
		// Reset:
		sagaPlayer.setAttributeScore(attrName, nextScore);
		
		// Inform:
		sagaPlayer.message(BuildingMessages.resetAttr(attrName, nextScore));
		
		// Play effect:
		StatsEffectHandler.playSign(sagaPlayer);
		
		
	}
	
	
}
