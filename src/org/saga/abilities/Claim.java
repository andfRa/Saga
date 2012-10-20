package org.saga.abilities;

import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.Saga;
import org.saga.commands.SettlementCommands;
import org.saga.player.SagaPlayer;
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
	
	/* 
	 * Trigger indication.
	 * 
	 * @see org.saga.abilities.Ability#hasAttackPreTrigger()
	 */
	@Override
	public boolean hasInteractPreTrigger() {
		return true;
	}


	
	// Usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#trigger()
	 */
	@Override
	public boolean triggerInteract(PlayerInteractEvent event) {
		
		if(!(getSagaLiving() instanceof SagaPlayer)) return false;

		// Claim command:
		CommandContext args = new CommandContext("sclaim");
		SettlementCommands.claim(args, Saga.plugin(), (SagaPlayer)getSagaLiving());

		return true;
		
	}
	
	
	
	
}
