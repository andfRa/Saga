package org.saga.buildings;

import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.saga.buildings.signs.AbilitySign;
import org.saga.buildings.signs.AttributeSign;
import org.saga.buildings.signs.BuildingSign;
import org.saga.buildings.signs.ResetAbilitySign;
import org.saga.buildings.signs.ResetAttributeSign;

public class TrainingCamp extends Building{

	
	
	// Initialisation:
	/**
	 * Creates a building from the definition.
	 * 
	 * @param definition building definition
	 */
	public TrainingCamp(BuildingDefinition definition) {
		super(definition);
	}
	
	
	
	// Signs:
	@Override
	protected boolean isBuildingSign(String firstLine) {
	
		if(firstLine.equalsIgnoreCase(AttributeSign.SIGN_NAME)) return true;
		if(firstLine.equalsIgnoreCase(AbilitySign.SIGN_NAME)) return true;
		if(firstLine.equalsIgnoreCase(ResetAttributeSign.SIGN_NAME)) return true;
		if(firstLine.equalsIgnoreCase(ResetAbilitySign.SIGN_NAME)) return true;
		
		return super.isBuildingSign(firstLine);
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#createBuildingSign(org.bukkit.block.Sign, org.bukkit.event.block.SignChangeEvent)
	 */
	@Override
	protected BuildingSign createBuildingSign(Sign sign, SignChangeEvent event) {
	

		// Attribute sign:
		if(event.getLine(0).equalsIgnoreCase(AttributeSign.SIGN_NAME)){
			return AttributeSign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
		}

		// Ability sign:
		if(event.getLine(0).equalsIgnoreCase(AbilitySign.SIGN_NAME)){
			return AbilitySign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
		}

		// Reset attribute sign:
		if(event.getLine(0).equalsIgnoreCase(ResetAttributeSign.SIGN_NAME)){
			return ResetAttributeSign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
		}

		// Reset ability sign:
		if(event.getLine(0).equalsIgnoreCase(ResetAbilitySign.SIGN_NAME)){
			return ResetAbilitySign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
		}
		
		return super.createBuildingSign(sign, event);
		
	
	}

	
}
