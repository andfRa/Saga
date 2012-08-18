package org.saga.buildings.signs;

import org.bukkit.block.Sign;
import org.saga.buildings.Building;
import org.saga.buildings.TrainingCamp;
import org.saga.config.AttributeConfiguration;
import org.saga.messages.BuildingMessages;
import org.saga.messages.effects.AbilityEffects;
import org.saga.player.SagaPlayer;


public class AttributeSign extends BuildingSign{

	
	/**
	 * Name for the sign.
	 */
	public static String SIGN_NAME = "=[TRAIN]=";
	
	
	
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
	private AttributeSign(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building){
	
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
	public static AttributeSign create(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building) {
		return new AttributeSign(sign, secondLine, thirdLine, fourthLine, building);
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
		Integer trainLimit = getTrainLimit();
		
		if(!getBuilding().getDefinition().hasAttribute(attribute) || trainLimit <= 0) return SignStatus.INVALIDATED;

		return SignStatus.ENABLED;
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#getLine(int, org.saga.buildings.signs.BuildingSign.SignStatus)
	 */
	@Override
	public String getLine(int index, SignStatus status) {
	
		
		Integer trainLimit = getTrainLimit();
		
		switch (status) {
			
			case ENABLED:

				if(index == 1) return getFirstParameter();
				if(index == 3 && trainLimit > 0 && trainLimit < AttributeConfiguration.config().maxAttributeScore) return "limit: " + trainLimit;
				break;
				
			case DISABLED:

				if(index == 1) return getFirstParameter();
				if(index == 3 && trainLimit > 0 && trainLimit < AttributeConfiguration.config().maxAttributeScore) return "limit: " + trainLimit;
				break;
				
			default:
				
				if(index == 1) return "-";
				if(index == 3 && trainLimit > 0 && trainLimit < AttributeConfiguration.config().maxAttributeScore) return "limit: " + "-";
				break;

		}

		return "";
		
		
	}
	
	/**
	 * Gets the training limit for attributes.
	 * 
	 * @return training limit for attributes
	 */
	private Integer getTrainLimit() {
		
		
		Integer trainLimit = AttributeConfiguration.config().maxAttributeScore;
		
		if(getBuilding() instanceof TrainingCamp){
			trainLimit = ((TrainingCamp) getBuilding()).getTrainLimit();
		}
		
		return trainLimit;
		
		
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
		Integer attributeScore = sagaPlayer.getRawAttributeScore(attribute) + 1;
		
		// Already maximum:
		if(attributeScore > AttributeConfiguration.config().maxAttributeScore){
			sagaPlayer.message(BuildingMessages.attributeMaxReached(attribute));
			return;
		}
		
		// Available points:
		if(sagaPlayer.getRemainingAttributePoints() < 1){
			sagaPlayer.message(BuildingMessages.attributePointsRequired(attribute));
			return;
		}
		
		// Train limit:
		if(attributeScore > getTrainLimit()){
			sagaPlayer.message(BuildingMessages.trainLimitReached(attribute));
			return;
		}
		
		// Increase:
		sagaPlayer.setAttributeScore(attribute, attributeScore);
		
		// Inform:
		sagaPlayer.message(BuildingMessages.attributeIncreased(attribute, attributeScore));
		
		// Play effect:
		AbilityEffects.playSign(sagaPlayer);
		
		
	}
	
	
}
