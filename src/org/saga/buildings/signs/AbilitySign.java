package org.saga.buildings.signs;

import org.bukkit.block.Sign;
import org.saga.buildings.Building;
import org.saga.config.AbilityConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.messages.BuildingMessages;
import org.saga.player.SagaPlayer;


public class AbilitySign extends BuildingSign{

	
	/**
	 * Name for the sign.
	 */
	public static String SIGN_NAME = "=[UPGRADE]=";
	
	
	// Initialisation:
	/**
	 * Creates a stone sign.
	 * 
	 * @param type transaction type
	 * @param sign sign
	 * @param secondLine second line
	 * @param thirdLine third line
	 * @param fourthLine fourth line
	 * @param event event that created the sign
	 * @param building building
	 */
	private AbilitySign(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building){
	
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
	public static AbilitySign create(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building) {
		return new AbilitySign(sign, secondLine, thirdLine, fourthLine, building);
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
	
		
//		String ability = getFirstParameter();
//		
//		if(!getBuilding().getDefinition().hasAbility(ability)) return SignStatus.INVALIDATED;

		return SignStatus.INVALIDATED;
		
		
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
				if(index == 3) return "lclick for cost";
				break;
				
			case DISABLED:

				if(index == 1) return getFirstParameter();
				if(index == 3) return "-";
				break;
				
			default:
				
				if(index == 1) return "-";
				if(index == 3) return "-";
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

//		
//		String abilityName = getFirstParameter();
//		Integer abilityScore = sagaPlayer.getAbilityScore(abilityName) + 1;
//		
//		// Already maximum:
//		if(abilityScore > AbilityConfiguration.config().maxAbilityScore){
//			sagaPlayer.message(BuildingMessages.abilityMaxReached(abilityName));
//			return;
//		}
//		
//		// Ability:
//		Ability ability = sagaPlayer.getAbility(abilityName);
//		if(ability == null){
//			SagaLogger.severe(this, "failed to retrieve " + abilityName + " ability from " + sagaPlayer.getName());
//			sagaPlayer.error("Failed to retrieve " + abilityName + " ability.");
//			return;
//		}
//		
//		// Requirements:
//		if(!ability.getDefinition().checkAttributes(sagaPlayer, abilityScore)){
//			sagaPlayer.message(BuildingMessages.abilityReqNotMet(ability, abilityScore));
//			return;
//		}
//
//		// Enough coins:
//		Double cost = EconomyConfiguration.config().getAbilityUpgradeCost(abilityName, abilityScore);
//		if(sagaPlayer.getCoins() < cost){
//			sagaPlayer.message(EconomyMessages.notEnoughCoins(cost, sagaPlayer.getCoins()));
//			return;
//		}
//		
//		// Take coins:
//		sagaPlayer.removeCoins(cost);
//		sagaPlayer.message(GeneralMessages.coinsSpent(cost));
//		
//		// Upgrade:
//		sagaPlayer.setAbilityScore(abilityName, abilityScore);
//		
//		// Inform:
//		sagaPlayer.message(BuildingMessages.abilityUpgraded(abilityName, abilityScore));
//		
//		// Play effect:
//		AbilityEffects.playSign(sagaPlayer);
//		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#onLeftClick(org.saga.player.SagaPlayer)
	 */
	@Override
	protected void onLeftClick(SagaPlayer sagaPlayer) {
	
		
		String abilityName = getFirstParameter();
		Integer abilityScore = sagaPlayer.getAbilityScore(abilityName) + 1;
		
		// Already maximum:
		if(abilityScore > AbilityConfiguration.config().maxAbilityScore){
			sagaPlayer.message(BuildingMessages.abilityMaxReached(abilityName));
			return;
		}
		
		// Cost:
		Double cost = EconomyConfiguration.config().getAbilityUpgradeCost(abilityName, abilityScore);
		
		// Inform:
		sagaPlayer.message(BuildingMessages.abilityCost(abilityName, abilityScore, cost));
		
		
	}
	
	
}
