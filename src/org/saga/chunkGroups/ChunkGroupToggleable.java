package org.saga.chunkGroups;

public enum ChunkGroupToggleable {

	
	OPEN_STORAGE_AREAS,
	PVP_PROTECTION,
	UNLIMITED_CLAIMS;

	
	// Matching:
	/**
	 * Gets the option corresponding to the given name.
	 * 
	 * @param name option name
	 * @return option corresponding to name, null if none
	 */
	public static ChunkGroupToggleable fullMatch(String name) {

		
		try {
			return valueOf(name.toUpperCase().replace(" ", "_")); 
		}
		catch (IllegalArgumentException e) {
			return null;
		}
		
		
	}
	
	/**
	 * Matches the name with a corresponding option.
	 * 
	 * @param name option name
	 * @return option corresponding to name, null if none
	 */
	public static ChunkGroupToggleable match(String name) {

		
		// Try full match:
		ChunkGroupToggleable option = fullMatch(name);
		if(option != null) return option;
		
		// Try limited match:
		ChunkGroupToggleable[] values = values(); 
		
		name = name.toUpperCase().replace(" ", "_");
		
		for (int i = 0; i < values.length; i++) {
			
			if(values[i].name().startsWith(name)) return values[i];
			
		}
		
		return null;

		
	}	

	

	// Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		
		return super.toString().replace("_", " ").toLowerCase();

	}


}
