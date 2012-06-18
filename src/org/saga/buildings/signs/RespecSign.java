package org.saga.buildings.signs;

import org.bukkit.block.Sign;
import org.saga.buildings.Building;
import org.saga.config.AttributeConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.messages.AbilityEffects;
import org.saga.messages.BuildingMessages;
import org.saga.messages.EconomyMessages;
import org.saga.messages.GeneralMessages;
import org.saga.player.SagaPlayer;


public class RespecSign extends BuildingSign{

	
	/**
	 * Name for the 
	 */
	public static String SIGN_NAME = "=[RESET]=";
	
	
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
	private RespecSign(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building){
	
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
	public static RespecSign create(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building) {
		return new RespecSign(sign, secondLine, thirdLine, fourthLine, building);
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
	
		
		String attribute = getFirstParameter();
		
		if(!AttributeConfiguration.config().getAttributeNames().contains(attribute)) return SignStatus.INVALIDATED;
		
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

				if(index == 1) return getFirstParameter();
				break;

			case DISABLED:

				if(index == 1) return getFirstParameter();
				break;
				
			default:
				
				if(index == 1) return "-";
				break;

		}

		return "";
		
		
	}
	
	
	// Events:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#onRightClick(org.saga.player.SagaPlayer)
	 */
	@Override
	protected void onRightClick(SagaPlayer sagaPlayer) {

		String attribute = getFirstParameter();
		Integer attributeScore = sagaPlayer.getAttributeScore(attribute);
		
		// Already minimum:
		if(attributeScore <= 0){
			sagaPlayer.message(BuildingMessages.alreadyRespec(attribute));
			return;
		}

		// Enough coins:
		Double cost = EconomyConfiguration.config().getRespecCost(attributeScore);
		if(sagaPlayer.getCoins() < cost){
			sagaPlayer.message(EconomyMessages.notEnoughCoins(cost, sagaPlayer.getCoins()));
			return;
		}

		// Take coins:
		sagaPlayer.removeCoins(cost);
		sagaPlayer.message(GeneralMessages.coinsSpent(cost));
		
		// Reset:
		sagaPlayer.setAttributeScore(attribute, 0);
		
		// Inform:
		sagaPlayer.message(BuildingMessages.respec(attribute));
		
		// Play effect:
		AbilityEffects.playSign(sagaPlayer);
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#onLeftClick(org.saga.player.SagaPlayer)
	 */
	@Override
	protected void onLeftClick(SagaPlayer sagaPlayer) {
	
		
		String attrName = getFirstParameter();
		Integer attrScore = sagaPlayer.getAbilityScore(attrName);
		
		// Cost:
		Double cost = EconomyConfiguration.config().getRespecCost(attrScore);
		
		// Inform:
		sagaPlayer.message(BuildingMessages.respecCost(attrName, attrScore, cost));
		
		
	}
	
	
}
