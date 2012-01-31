package org.saga.buildings.signs;

import org.bukkit.block.Sign;
import org.saga.buildings.Building;
import org.saga.player.SagaPlayer;


public class LearningSign extends BuildingSign{

	
	/**
	 * Name for the sign.
	 */
	public static String SIGN_NAME = "=[LEARN]=";
	
	
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
	protected LearningSign(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building){
	
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
	public static LearningSign create(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building) {
		return new LearningSign(sign, secondLine, thirdLine, fourthLine, building);
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
//		AbilityDefinition abilDefinition = AbilityConfiguration.config().getDefinition(getFirstParameter());
//		
//		if(abilDefinition != null && getBuilding().getDefinition().hasAbility(getFirstParameter())){
//			
//			sign.setLine(1, sign.getLine(1) + " " + "ability");
//			
//			// Levels cost:
//			sign.setLine(2, "for " + abilDefinition.getLevelsCost() + " levels");
//
//			// Currency cost:
//			sign.setLine(3, "and " + EconomyMessages.currency(abilDefinition.getCurrencyCost()));
//			
//		}else{
//			
//			invalidate();
//			
//		}
		
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
//		// Permission:
//		if(!getBuilding().canLearn(sagaPlayer)){
//			sagaPlayer.message(ChunkGroupMessages.noPermission(getBuilding()));
//			return;
//		}
//		
//		// Get ability:
//		Ability ability = null;
//		try {
//			ability = AbilityConfiguration.config().createAbility(getFirstParameter());
//		} catch (InvalidAbilityException e) {
//			sagaPlayer.message(PlayerMessages.invalidAbility(getFirstParameter()));
//			return;
//		}
//
//		// Can be learned:
//		if(!sagaPlayer.canLearnAbility(ability.getName())){
//			sagaPlayer.message(PlayerMessages.cantLearn(ability));
//			sagaPlayer.message(PlayerMessages.canLearn(sagaPlayer.getLearnableAbilities()));
//			return;
//		}
//
//		// Already learned:
//		if(sagaPlayer.hasAbility(ability.getName())){
//			sagaPlayer.message(PlayerMessages.alreadyLearned(ability));
//			return;
//		}
//		
//		// Enough levels:
//		Integer requiredLevels = ability.getDefinition().getLevelsCost();
//		if(requiredLevels > sagaPlayer.getLevel()){
//			sagaPlayer.message(PlayerMessages.notEnoughLevels(requiredLevels, ability));
//			return;
//		}
//
//		// Enough currency:
//		Double requiredCurrency = ability.getDefinition().getCurrencyCost();
//		if(requiredCurrency > sagaPlayer.getCurrency()){
//			sagaPlayer.message(PlayerMessages.notEnoughCurrency(requiredCurrency, ability));
//			return;
//		}
//		
//		// Decrease currency and level:
//		sagaPlayer.removeCoins(requiredCurrency);
//		sagaPlayer.decreaseLevel(requiredLevels);
//		
//		// Add ability:
//		sagaPlayer.addAbility(ability);
//		
//		// Select:
//		ActivationAction action = ability.getDefinition().getDefaultAction();
//		Material material =  ability.getDefinition().getDefaultMaterial();
//		if(sagaPlayer.canBindAbilities() && !sagaPlayer.isBinded(action, material)){
//			sagaPlayer.bindAbility(ability.getName(), action, material);
//		}
//		
//		// Inform:
//		sagaPlayer.info(PlayerMessages.abilityLearned(ability, requiredCurrency, requiredLevels));
//		
		
	}
	
	
}
