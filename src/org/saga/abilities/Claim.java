package org.saga.abilities;

import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.Saga;
import org.saga.commands.SettlementCommands;
import org.sk89q.CommandContext;

public class Claim extends Ability{

	
	// Initialisation:
	/**
	 * Initialises using definition.
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
	public boolean trigger(PlayerInteractEvent event) {
		

		// Claim command:
		CommandContext args = new CommandContext("sclaim");
		SettlementCommands.claim(args, Saga.plugin(), getSagaPlayer());

		return true;
		
		
	}
	
	
}
