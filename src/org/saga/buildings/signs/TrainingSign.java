package org.saga.buildings.signs;

import org.bukkit.block.Sign;
import org.saga.buildings.Building;
import org.saga.chunkGroups.ChunkGroupMessages;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.ProficiencyConfiguration.InvalidProficiencyException;
import org.saga.economy.EconomyMessages;
import org.saga.player.PlayerMessages;
import org.saga.player.Proficiency;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.player.ProficiencyDefinition;
import org.saga.player.SagaPlayer;


public class TrainingSign extends BuildingSign{

	
	/**
	 * Name for the sign.
	 */
	public static String SIGN_NAME = "=[SELECT]=";
	
	
	// Initialization:
	/**
	 * Creates a economy sign.
	 * 
	 * @param type transaction type
	 * @param sign sign
	 * @param secondLine second line
	 * @param thirdLine third line
	 * @param fourthLine fourth line
	 * @param event event that created the sign
	 * @param building building
	 */
	protected TrainingSign(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building){
	
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
	public static TrainingSign create(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building) {
		return new TrainingSign(sign, secondLine, thirdLine, fourthLine, building);
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
		ProficiencyDefinition profDefinition = ProficiencyConfiguration.config().getDefinition(getFirstParameter());
		
		if(profDefinition != null){
			
			// Profession:
			if(profDefinition.getType().equals(ProficiencyType.PROFESSION) && getBuilding().getDefinition().hasProfession(getFirstParameter())){
				
				// Name:
				sign.setLine(1, sign.getLine(1) + " " + ProficiencyType.PROFESSION.getName());

				// Levels cost:
				sign.setLine(2, "for " + profDefinition.getLevelsCost() + " levels");

				// Currency cost:
				sign.setLine(3, "and " + EconomyMessages.coins(profDefinition.getCurrencyCost()));
				
			}
			// Class:
			else if(profDefinition.getType().equals(ProficiencyType.CLASS) && getBuilding().getDefinition().hasClass(getFirstParameter())){
				
				// Name:
				sign.setLine(1, sign.getLine(1) + " " + ProficiencyType.CLASS.getName());

				// Levels cost:
				sign.setLine(2, "for " + profDefinition.getLevelsCost() + " levels");

				// Currency cost:
				sign.setLine(3, "and " + EconomyMessages.coins(profDefinition.getCurrencyCost()));
				
				
			}else{
				
				invalidate();
				
			}
			
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

		sign.setLine(2, "for " + "-" + " levels");

		sign.setLine(3, "and " + "-" + EconomyMessages.coins());
		
		
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
//		if(!getBuilding().canTrain(sagaPlayer)){
//			sagaPlayer.message(ChunkGroupMessages.noPermission(getBuilding()));
//			return;
//		}
//		
//		// Get proficiency:
//		Proficiency proficiency = null;
//		try {
//			proficiency = ProficiencyConfiguration.config().createProficiency(getFirstParameter());
//		} catch (InvalidProficiencyException e) {
//			sagaPlayer.message(PlayerMessages.invalidProficiency(getFirstParameter()));
//			return;
//		}
//		
//		// Only professions and classes:
//		if(!proficiency.getType().equals(ProficiencyType.CLASS) && !proficiency.getType().equals(ProficiencyType.PROFESSION)) return;
//		
//		// Already an existing proficiency:
//		if(proficiency.getType().equals(ProficiencyType.PROFESSION) && sagaPlayer.getProfession() != null){
//			sagaPlayer.message(PlayerMessages.oneProficiencyAllowed(proficiency));
//			return;
//		}
//		if(proficiency.getType().equals(ProficiencyType.CLASS) && sagaPlayer.getClazz() != null){
//			sagaPlayer.message(PlayerMessages.oneProficiencyAllowed(proficiency));
//			return;
//		}
//		
//		// Enough levels:
//		Integer requiredLevels = proficiency.getDefinition().getLevelsCost();
//		if(requiredLevels > sagaPlayer.getLevel()){
//			sagaPlayer.message(PlayerMessages.levelsNeeded(requiredLevels, proficiency));
//			return;
//		}
//
//		// Enough currency:
//		Double requiredCurrency = proficiency.getDefinition().getCurrencyCost();
//		if(requiredCurrency > sagaPlayer.getCoins()){
//			sagaPlayer.message(PlayerMessages.coinsNeeded(requiredCurrency, proficiency));
//			return;
//		}
//		
//		// Decrease currency and level:
//		sagaPlayer.removeCoins(requiredCurrency);
//		sagaPlayer.decreaseLevel(requiredLevels);
//		
//		// Set:
//		if(proficiency.getType().equals(ProficiencyType.PROFESSION)){
//			
//			sagaPlayer.setProfession(proficiency);
//			
//		}
//		else if(proficiency.getType().equals(ProficiencyType.CLASS)){
//			
//			sagaPlayer.setClass(proficiency);
//			
//		}
//		
//		// Inform:
//		sagaPlayer.info(PlayerMessages.proficiencySelected(proficiency, requiredCurrency, requiredLevels));
//		
		
	}
	
	
}
