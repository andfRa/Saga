package org.saga.buildings;


public class WoodsmanCamp extends Building{

	
	// Initialization:
	/**
	 * Initializes
	 * 
	 * @param pointCost point cost
	 * @param moneyCost money cost
	 * @param proficiencies proficiencies
	 */
	private WoodsmanCamp(String name) {
		
		super("");
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#completeExtended()
	 */
	@Override
	public boolean completeExtended() {
		

		boolean integrity = true;
		
//		if(signs == null){
//			signs = new ArrayList<BuildingSign>();
//			Saga.severe(this, "signs field failed to initialize", "setting default");
//			integrity = false;
//		}
		
		return integrity;
		
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.Building#blueprint()
	 */
	@Override
	public Building blueprint() {
		return new WoodsmanCamp("");
	}
	
	
}
