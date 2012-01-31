package org.saga.buildings.signs;

import org.bukkit.block.Sign;
import org.saga.buildings.Building;
import org.saga.config.EconomyConfiguration;
import org.saga.economy.EconomyMessages;
import org.saga.player.SagaPlayer;


public class BreakStoneSign extends BuildingSign{

	
	/**
	 * Name for the sign.
	 */
	public static String SIGN_NAME = "=[BREAK]=";

	/**
	 * Parameter for the sign.
	 */
	public static String STONE_TYPE = "guardian stone";
	
	
	// Initialization:
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
	protected BreakStoneSign(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building){
	
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
	public static BreakStoneSign create(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building) {
		return new BreakStoneSign(sign, secondLine, thirdLine, fourthLine, building);
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
	
	
	// Enabling and disabling:
	@Override
	public void enable() {

		super.enable();
		
		Sign sign = getSign();
		
		
		// Check parameters:
		if(getFirstParameter().equalsIgnoreCase(STONE_TYPE)){
			
			sign.setLine(1, STONE_TYPE);
			
			Double price = EconomyConfiguration.config().guardianRuneRechargeCost;
			
			if(price > 0.0){
				sign.setLine(2, "for " + EconomyMessages.coins(price));
			}else{
				sign.setLine(2, "");
			}
			
		}else{
			
			invalidate();
			
		}
		
		invalidate();
		
		// Update:
		sign.update();
		
		
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
//		GuardianRune rune = sagaPlayer.getGuardianRune();
//
//		// Empty:
//		if(rune.getStatus().equals(GuardianRuneStatus.EMPTY)){
//			sagaPlayer.message(PlayerMessages.empty(rune));
//			return;
//		}
//		
//		// Enough currency:
//		Double price = EconomyConfiguration.config().guardianStoneBreakPrice;
//		if(sagaPlayer.getCurrency() < price){
//			sagaPlayer.message(EconomyMessages.notEnoughCoins());
//			return;
//		}
//		
//		// Cooldown:
//		Building building = getBuilding();
//		Cooldown cBuilding = null;
//		if(building != null && building instanceof Cooldown){
//			
//			cBuilding = (Cooldown) getBuilding();
//			
//		}
//		if(cBuilding != null && cBuilding.isOnCooldown()){
//			sagaPlayer.message(BuildingMessages.cooldown(building.getName(), cBuilding.getCooldown()));
//			return;
//		}
//		
//		// Broken:
//		if(rune.getStatus().equals(GuardianRuneStatus.BROKEN)){
//			sagaPlayer.message(PlayerMessages.alreadyBroken(rune));
//			return;
//		}
//		
//		// Empty:
//		if(!rune.getStatus().equals(GuardianRuneStatus.FULL)){
//			sagaPlayer.message(PlayerMessages.empty(rune));
//			return;
//		}
//
//		// Take currency:
//		if(price > 0){
//			sagaPlayer.removeCoins(price);
//		}
//		
//		// Inform:
//		sagaPlayer.message(PlayerMessages.restored(rune, price));
//		sagaPlayer.message(PlayerMessages.brokeInfo(rune));
//		
//		// Brake stone:
//		sagaPlayer.guardianRuneRestore();
//		
//		// Statistics:
//		StatisticsManager.manager().onGuardanRuneRestore();
//		
		
	}
	
	
}
