package org.saga.buildings;

import java.util.ArrayList;

import org.saga.Saga;
import org.saga.buildings.signs.BuildingSign;
import org.saga.buildings.signs.BuildingSign.SignException;

public class Inn extends Building{

	/**
	 * Blacklisted players.
	 */
	private ArrayList<BuildingSign> signs;

	
	// Initialization:
	/**
	 * Initializes
	 * 
	 * @param pointCost point cost
	 * @param moneyCost money cost
	 * @param proficiencies proficiencies
	 */
	private Inn(String name) {
		
		super("");
		signs = new ArrayList<BuildingSign>();
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#completeExtended()
	 */
	@Override
	public boolean completeExtended() {
		

		boolean integrity = true;
		
		if(signs == null){
			signs = new ArrayList<BuildingSign>();
			Saga.severe(this, "failed to initialize signs field", "setting default");
			integrity = false;
		}
		for (int i = 0; i < signs.size(); i++) {
			
			try {
				signs.get(i).complete(this);
			} catch (SignException e) {
				Saga.severe(this, "failed to initialize signs field element: " + e.getClass().getSimpleName() + ":" + e.getMessage(), "removing element");
				signs.remove(i);
				i--;
				continue;
			}
			
		}
		
		// Transient:
		
		return integrity;
		
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#blueprint()
	 */
	@Override
	public Building blueprint() {
		return new Inn("");
	}
	
	
}
