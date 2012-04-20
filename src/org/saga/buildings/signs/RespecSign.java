package org.saga.buildings.signs;

import org.bukkit.block.Sign;
import org.saga.buildings.Building;
import org.saga.config.BalanceConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.economy.EconomyMessages;
import org.saga.messages.PlayerMessages;
import org.saga.messages.SagaMessages;
import org.saga.player.SagaPlayer;


public class RespecSign extends BuildingSign{

	
	/**
	 * Name for the sign.
	 */
	public static String SIGN_NAME = "=[RESET]=";
	
	
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
	protected RespecSign(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building){
	
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
	 * @see org.saga.buildings.signs.BuildingSign#enable()
	 */
	@Override
	public void enable() {

		
		super.enable();
		
		Sign sign = getSign();
		
		sign.setLine(1, "stats");
		
		if(EconomyConfiguration.config().getRespecCost(BalanceConfiguration.config().maximumLevel) > 0){
			sign.setLine(2, "for ?" + EconomyMessages.coins());
		}else{
			sign.setLine(2, "");
		}
		
		sign.setLine(3, "lclick for info");
			
		// Update:
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

		sign.setLine(1, "-");
		
		sign.setLine(2, "");

		sign.setLine(3, "");
		
		
	}
	
	
	// Events:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#onRightClick(org.saga.player.SagaPlayer)
	 */
	@Override
	protected void onRightClick(SagaPlayer sagaPlayer) {
		

		// Permission:
		if(!getBuilding().canRespec(sagaPlayer)){
			sagaPlayer.message(SagaMessages.noPermission(getBuilding()));
			return;
		}
		
		// Enough coins:
		Double requiredCoins = EconomyConfiguration.config().getRespecCost(sagaPlayer.getLevel());
		if(requiredCoins > sagaPlayer.getCoins()){
			sagaPlayer.message(PlayerMessages.coinsNeeded(requiredCoins));
			return;
		}
		
		boolean classRespec = false;
		boolean profRespec = false;
		boolean skillRespec = false;

		if(sagaPlayer.getClazz() != null){
			sagaPlayer.clearClass();
			classRespec = true;
		}

		if(sagaPlayer.getProfession() != null){
			sagaPlayer.clearProfession();
			profRespec = true;
		}
		
		if(sagaPlayer.getSkillPoints() > 0){
			sagaPlayer.clearSkills();
			skillRespec = true;
		}
		
		if(profRespec || classRespec || skillRespec){

			// Remove coins:
			sagaPlayer.removeCoins(requiredCoins);
			
		}
		
		// Inform:
		sagaPlayer.info(PlayerMessages.respec(profRespec, classRespec, skillRespec, requiredCoins));
		
		
	}
	
	@Override
	protected void onLeftClick(SagaPlayer sagaPlayer) {

		sagaPlayer.message(PlayerMessages.respecInfo(sagaPlayer));
	
	}
	
	
	
	
}
