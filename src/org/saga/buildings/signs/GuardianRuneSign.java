package org.saga.buildings.signs;

import org.bukkit.block.Sign;
import org.saga.buildings.Building;
import org.saga.config.EconomyConfiguration;
import org.saga.dependencies.EconomyDependency;
import org.saga.messages.EconomyMessages;
import org.saga.messages.PlayerMessages;
import org.saga.messages.effects.StatsEffectHandler;
import org.saga.player.GuardianRune;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;


public class GuardianRuneSign extends BuildingSign{

	
	/**
	 * Name for the 
	 */
	public static String SIGN_NAME = "=[RECHARGE]=";
	
	/**
	 * Parameter for the 
	 */
	public static String RUNE_TYPE = "guardian rune";
	
	
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
	private GuardianRuneSign(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building){
	
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
	public static GuardianRuneSign create(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building) {
		return new GuardianRuneSign(sign, secondLine, thirdLine, fourthLine, building);
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
	
		return SignStatus.ENABLED;
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#getLine(int, org.saga.buildings.signs.BuildingSign.SignStatus)
	 */
	@Override
	public String getLine(int index, SignStatus status) {
	
		
		Double price = EconomyConfiguration.config().guardianRuneRechargeCost;
				
		switch (status) {
			
			case ENABLED:

				
				if(index == 1) return RUNE_TYPE;
				
				if(index == 2){
					if(price > 0.0){
						return "for " + EconomyMessages.coins(price);
					}else{
						return "";
					}
				}

				break;
				
			case DISABLED:

				if(index == 1) return RUNE_TYPE;
				
				if(index == 2){
					if(price > 0.0){
						return "for " + EconomyMessages.coins(price);
					}else{
						return "";
					}
				}

				break;
			
			case INVALIDATED:
				
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

		
		GuardianRune rune = sagaPlayer.getGuardRune();
		
		// Already charged:
		if(rune.isCharged()){
			sagaPlayer.message(PlayerMessages.alreadyRecharged(rune));
			return;
		}

		// Enough coins:
		Double price = EconomyConfiguration.config().guardianRuneRechargeCost;
		if(EconomyDependency.getCoins(sagaPlayer) < price){
			sagaPlayer.message(EconomyMessages.notEnoughCoins());
			return;
		}

		// Take coins:
		if(price > 0){
			EconomyDependency.removeCoins(sagaPlayer, price);
		}
		
		// Recharges rune:
		rune.recharge();
		
		// Inform:
		sagaPlayer.message(PlayerMessages.recharged(rune, price));

		// Statistics:
		StatisticsManager.manager().addGuardRuneRecharge(sagaPlayer);
		
		// Play effect:
		StatsEffectHandler.playRecharge(sagaPlayer);
		
		
	}
	
	
}
