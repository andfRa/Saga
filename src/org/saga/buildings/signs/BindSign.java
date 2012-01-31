package org.saga.buildings.signs;

import org.bukkit.block.Sign;
import org.saga.abilities.AbilityDefinition.ActivationAction;
import org.saga.buildings.Building;
import org.saga.player.SagaPlayer;

public class BindSign extends BuildingSign{

	
	/**
	 * Name for the sign.
	 */
	public static String SIGN_NAME = "=[BIND]=";
	
	
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
	protected BindSign(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building){
	
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
	public static BindSign create(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building) {
		return new BindSign(sign, secondLine, thirdLine, fourthLine, building);
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
	
	
	// Selection:
	/**
	 * Makes the selection.
	 * 
	 * @param sagaPlayer saga player
	 * @param action action
	 */
	private void selection(SagaPlayer sagaPlayer, ActivationAction action) {

//
//		Material material = sagaPlayer.getItemInHand().getType();
//		
//		// Permission:
//		if(!getBuilding().canSelect(sagaPlayer)){
//			sagaPlayer.message(ChunkGroupMessages.noPermission(getBuilding()));
//			return;
//		}
//		
//		String abilityName = getFirstParameter();
//		
//		// Get ability:
//		Ability ability = sagaPlayer.getAbility(abilityName);
//
//		// Deselect:
//		if(sagaPlayer.isAbilityBinded(abilityName, action, material)){
//			
//			// Deselect ability:
//			sagaPlayer.unbindAbility(abilityName);
//
//			// Inform:
//			sagaPlayer.message(PlayerMessages.unbinded(ability, action, material));
//			
//		}
//		
//		// Select:
//		else{
//
//			// Not learned:
//			if(ability == null){
//				sagaPlayer.message(PlayerMessages.notLearned(abilityName));
//				return;
//			}
//			
//			// Unbind previous:
//			sagaPlayer.unbindAbility(abilityName);
//
//			// Remove target bind:
//			sagaPlayer.unbindtAbility(action, material);
//			
//			// Limit reached:
//			if(sagaPlayer.countBindedAbilities() >= BalanceConfiguration.config().abilitiesLimit){
//					
//				sagaPlayer.message(PlayerMessages.selectionLimit());
//					
//				// Inform:
//				sagaPlayer.message(PlayerMessages.selectionLimitInfo());
//					
//				return;
//					
//			}
//			
//			// Invalid materials:
//			if(!sagaPlayer.canBind(abilityName, material)){
//				
//				sagaPlayer.message(PlayerMessages.invalidBind(ability, material));
//				
//				// Info:
//				sagaPlayer.message(PlayerMessages.invalidBindInfo(ability));
//				
//				return;
//				
//			}
////			
////			// Available:
////			if(!sagaPlayer.getLearnableAbilities().contains(abilityName)){
////				sagaPlayer.sendMessage(PlayerMessages.notAvailable(ability));
////				return;
////			}
//			
//			// Select ability:
//			sagaPlayer.bindAbility(abilityName, action, material);
//				
//			// Inform:
//			sagaPlayer.message(PlayerMessages.binded(ability, action, material));
//			
//		}
		
		
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

		selection(sagaPlayer, ActivationAction.RIGHT_CLICK);
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#onLeftClick(org.saga.player.SagaPlayer)
	 */
	@Override
	protected void onLeftClick(SagaPlayer sagaPlayer) {

		selection(sagaPlayer, ActivationAction.LEFT_CLICK);
		
	}
	
	
}
