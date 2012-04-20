package org.saga.buildings.signs;

import org.bukkit.block.Sign;
import org.saga.buildings.Building;
import org.saga.config.BalanceConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.economy.EconomyMessages;
import org.saga.messages.PlayerMessages;
import org.saga.messages.SagaMessages;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;


public class SkillSign extends BuildingSign{

	
	/**
	 * Name for the sign.
	 */
	public static String SIGN_NAME = "=[TRAIN]=";
	
	
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
	protected SkillSign(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building){
	
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
	public static SkillSign create(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building) {
		return new SkillSign(sign, secondLine, thirdLine, fourthLine, building);
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
		
		if(getBuilding().getDefinition().hasSkill(getFirstParameter())){
			
			sign.setLine(1, getFirstParameter());
			
			if(EconomyConfiguration.config().getSkillCoinCost(BalanceConfiguration.config().maximumSkillLevel) > 0){
				sign.setLine(2, "for ?" + EconomyMessages.coins());
			}else{
				sign.setLine(2, "");
			}
			
			sign.setLine(3, "lclick for info");
			
		}else{
			
			invalidate();
			
		}
		
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
		
		sign.setLine(2, "-");

		sign.setLine(3, "-");
		
		
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
		if(!getBuilding().canTrain(sagaPlayer)){
			sagaPlayer.message(SagaMessages.noPermission(getBuilding()));
			return;
		}
		
		String skillName = getFirstParameter();
		Integer multiplier = sagaPlayer.getSkillMultiplier(skillName);
		
		// Limit reached:
		if(multiplier >= sagaPlayer.getSkillMaximum(skillName)){
			sagaPlayer.message(PlayerMessages.limitReached(skillName));
			return;
		}
		
		// Enough skill points:
		Integer remainSkills = sagaPlayer.getRemainingSkillPoints();
		if(remainSkills < 1){
			sagaPlayer.message(PlayerMessages.skillPointsNeeded());
			return;
		}

		// Enough coins:
		Double requiredCoins = EconomyConfiguration.config().getSkillCoinCost(multiplier);
		if(requiredCoins > sagaPlayer.getCoins()){
			sagaPlayer.message(PlayerMessages.coinsNeeded(requiredCoins));
			return;
		}
		
		// Remove coins:
		sagaPlayer.removeCoins(requiredCoins);
		
		// Increase skill:
		sagaPlayer.increaseSkill(skillName, 1);
		
		// Inform:
		sagaPlayer.info(PlayerMessages.trained(skillName, sagaPlayer.getSkillMultiplier(skillName), requiredCoins));
		
		// Statistics:
		StatisticsManager.manager().onSkillUpgrade(skillName, requiredCoins);
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#onLeftClick(org.saga.player.SagaPlayer)
	 */
	@Override
	protected void onLeftClick(SagaPlayer sagaPlayer) {
	
		
		// Not enabled:
		if(!isEnabled()) return;
		
		String skillName = getFirstParameter();
		
		// Inform:
		sagaPlayer.message(PlayerMessages.trainInfo(skillName, sagaPlayer));
		
	
	}
	
	
}
