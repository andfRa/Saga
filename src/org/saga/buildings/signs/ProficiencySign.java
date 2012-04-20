package org.saga.buildings.signs;

import org.bukkit.block.Sign;
import org.saga.buildings.Building;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.ProficiencyConfiguration.InvalidProficiencyException;
import org.saga.economy.EconomyMessages;
import org.saga.messages.PlayerMessages;
import org.saga.messages.SagaMessages;
import org.saga.player.Proficiency;
import org.saga.player.Proficiency.ProficiencyType;
import org.saga.player.ProficiencyDefinition;
import org.saga.player.SagaPlayer;


public class ProficiencySign extends BuildingSign{

	
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
	protected ProficiencySign(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building){
	
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
	public static ProficiencySign create(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building) {
		return new ProficiencySign(sign, secondLine, thirdLine, fourthLine, building);
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
		
		// Invalidate:
		if(profDefinition == null){
			invalidate();
			return;
		}
		
		// Check, for correct class and profession:
		ProficiencyType type = profDefinition.getType();
		if( (type == ProficiencyType.PROFESSION || type == ProficiencyType.CLASS) && profDefinition != null){
		
			Double coinCost = profDefinition.getCoinCost();
			
			// Name:
			sign.setLine(1, sign.getLine(1) + " " + type.getName());

			// Coin cost:
			if(coinCost > 0){
				sign.setLine(2, "for " + EconomyMessages.coins(coinCost));
			}else{
				sign.setLine(2, "");
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

		sign.setLine(2, "-");
		
		sign.setLine(2, "-");
		
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
		if(!getBuilding().canTrain(sagaPlayer)){
			sagaPlayer.message(SagaMessages.noPermission(getBuilding()));
			return;
		}
		
		// Get proficiency:
		Proficiency proficiency = null;
		try {
			proficiency = ProficiencyConfiguration.config().createProficiency(getFirstParameter());
		} catch (InvalidProficiencyException e) {
			sagaPlayer.message(PlayerMessages.invalidProficiency(getFirstParameter()));
			return;
		}
		
		// Only professions and classes:
		ProficiencyType type = proficiency.getType();
		if(type != ProficiencyType.CLASS && type != ProficiencyType.PROFESSION) return;
		
		// Already existing proficiency:
		if(type == ProficiencyType.CLASS && sagaPlayer.getClazz() != null){
			
			sagaPlayer.message(PlayerMessages.oneProficAllowed(type));
			sagaPlayer.message(PlayerMessages.oneProficAllowedInfo(type));
			return;
			
		}
		else if(type == ProficiencyType.PROFESSION && sagaPlayer.getProfession() != null){
			
			sagaPlayer.message(PlayerMessages.oneProficAllowed(type));
			sagaPlayer.message(PlayerMessages.oneProficAllowedInfo(type));
			return;
			
		}

		// Enough coins:
		Double requiredCoins = proficiency.getDefinition().getCoinCost();
		if(requiredCoins > sagaPlayer.getCoins()){
			sagaPlayer.message(PlayerMessages.coinsNeeded(requiredCoins, proficiency));
			return;
		}

		// Already an existing proficiency:
		if(proficiency.getType().equals(ProficiencyType.PROFESSION) && sagaPlayer.getProfession() != null){
			
			if(proficiency.getName().equals(sagaPlayer.getProfession().getName())){
				sagaPlayer.message(PlayerMessages.alreadySelected(proficiency));
				return;
			}
		
			sagaPlayer.clearProfession();
			
		}
		if(proficiency.getType().equals(ProficiencyType.CLASS) && sagaPlayer.getClazz() != null){
			
			if(proficiency.getName().equals(sagaPlayer.getClazz().getName())){
				sagaPlayer.message(PlayerMessages.alreadySelected(proficiency));
				return;
			}
			
			sagaPlayer.clearClass();

		}
		
		// Decrease coins:
		sagaPlayer.removeCoins(requiredCoins);
		
		// Set:
		if(proficiency.getType().equals(ProficiencyType.PROFESSION)){
			
			sagaPlayer.setProfession(proficiency);
			
		}
		else if(proficiency.getType().equals(ProficiencyType.CLASS)){
			
			sagaPlayer.setClass(proficiency);
			
		}
		
		// Inform:
		sagaPlayer.info(PlayerMessages.proficiencySelected(proficiency, requiredCoins));
		
		
	}
	
	
}
