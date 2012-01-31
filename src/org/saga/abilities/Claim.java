package org.saga.abilities;

import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.Saga;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.ChunkGroupCommands;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.TwoPointFunction;
import org.sk89q.CommandContext;

public class Claim extends Ability{

	
	// Initialization:
	/**
	 * Initializes using definition.
	 * 
	 * @param definition ability definition
	 */
	public Claim(AbilityDefinition definition) {
		
        super(definition);
	
	}

	
	// Usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#trigger()
	 */
	@Override
	public boolean instant(PlayerInteractEvent event) {
		

		// Check pre use:
		if(!handlePreUse()){
			return false;
		}

		// Claim command:
		CommandContext args = new CommandContext("sclaim");
		ChunkGroupCommands.claim(args, Saga.plugin(), getSagaPlayer());

		// Statistics:
		StatisticsManager.manager().onAbilityUse(getName(), 0);
		
		return true;
		
		
	}
	
	
}
