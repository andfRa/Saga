package org.saga.buildings;

import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.saga.buildings.production.ProductionBuilding;
import org.saga.buildings.signs.BuildingSign;
import org.saga.buildings.signs.GuardianRuneSign;
import org.saga.config.GeneralConfiguration;


public class Academy extends ProductionBuilding {

	
	// Initialisation:
	/**
	 * Creates a building from the definition.
	 * 
	 * @param definition building definition
	 */
	public Academy(BuildingDefinition definition) {
		super(definition);
	}

	
	
	// Signs:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#isBuildingSign(java.lang.String)
	 */
	@Override
	public boolean isBuildingSign(String firstLine) {
		
		if(GeneralConfiguration.config().isRuneEnabled() && firstLine.equalsIgnoreCase(GuardianRuneSign.SIGN_NAME)) return true;
		
		return super.isBuildingSign(firstLine);
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#createBuildingSign2(org.bukkit.block.Sign, org.bukkit.event.block.SignChangeEvent)
	 */
	@Override
	protected BuildingSign createBuildingSign(Sign sign, SignChangeEvent event) {
		
		// Rune recharge sign:
		if(event.getLine(0).equalsIgnoreCase(GuardianRuneSign.SIGN_NAME)){
			
			return GuardianRuneSign.create(sign, event.getLine(1), event.getLine(2), event.getLine(3), this);
			
		}else{
			
		}
		
		return super.createBuildingSign(sign, event);
		
	}
	
	
}
