package org.saga.abilities;

import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.Saga;
import org.saga.commands.SettlementCommands;
import org.saga.messages.effects.StatsEffectHandler;
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


	
	// Usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#handleInteractPreTrigger(org.bukkit.event.player.PlayerInteractEvent)
	 */
	@Override
	public boolean handleInteractPreTrigger(PlayerInteractEvent event) {
		return handlePreTrigger();
	}
	
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
		
		// Effect:
		if(getSagaLiving() instanceof SagaPlayer) StatsEffectHandler.playAnimateArm((SagaPlayer) getSagaLiving());
		
		return true;
		
	}
	
	
	
	
}
