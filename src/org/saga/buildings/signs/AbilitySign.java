package org.saga.buildings.signs;

import org.bukkit.block.Sign;
import org.saga.SagaLogger;
import org.saga.abilities.Ability;
import org.saga.abilities.AbilityDefinition;
import org.saga.buildings.Building;
import org.saga.config.AbilityConfiguration;
import org.saga.messages.BuildingMessages;
import org.saga.messages.effects.StatsEffectHandler;
import org.saga.player.SagaPlayer;


public class AbilitySign extends BuildingSign{

	
	/**
	 * Name for the sign.
	 */
	public static String SIGN_NAME = "=TRAIN_ABIL=";
	
	
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
		
		String abilName = getFirstParameter();
		
		AbilityDefinition definition = AbilityConfiguration.config().getDefinition(abilName);
		if(definition == null) return SignStatus.INVALIDATED;
		
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

		
		String abilName = getFirstParameter();
		Integer nextScore = sagaPlayer.getAbilityScore(abilName) + 1;
		
		// Already maximum:
		if(nextScore > AbilityConfiguration.config().maxAbilityScore){
			sagaPlayer.message(BuildingMessages.abilityMaxReached(abilName));
			return;
		}
		
		// Ability:
		Ability ability = sagaPlayer.getAbility(abilName);
		if(ability == null){
			SagaLogger.severe(this, "failed to retrieve " + abilName + " ability from " + sagaPlayer.getName());
			sagaPlayer.error("Failed to find " + abilName + " ability.");
			return;
		}
		
		// Ability points:
		if(sagaPlayer.getRemainingAbilityPoints() < 1){
			sagaPlayer.message(BuildingMessages.abilityPointsRequired(abilName));
			return;
		}
		
		// Requirements:
		if(!ability.getDefinition().checkRequirements(sagaPlayer, nextScore)){
			sagaPlayer.message(BuildingMessages.abilityRequirementsNotMet(ability, nextScore));
			return;
		}

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
		
		// Upgrade:
		sagaPlayer.setAblityScore(abilName, nextScore);
		
		// Inform:
		sagaPlayer.message(BuildingMessages.abilityTrained(ability, nextScore));
		
		// Play effect:
		StatsEffectHandler.playSign(sagaPlayer);
		
		
	}
	
	
}
