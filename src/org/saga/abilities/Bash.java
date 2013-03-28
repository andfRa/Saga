package org.saga.abilities;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.saga.attributes.DamageType;
import org.saga.listeners.events.SagaDamageEvent;
import org.saga.player.SagaPlayer;

public class Bash extends Ability{

	
	/**
	 * Melee disarm chance key.
	 */
	private static String MELEE_DISARM_CHANCE_KEY = "melee disarm chance";
	
	/**
	 * Ranged disarm chance key.
	 */
	private static String RANGED_DISARM_CHANCE_KEY = "ranged disarm chance";
	
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Bash(AbilityDefinition definition) {
		
        super(definition);
	
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#useSilentPreTrigger()
	 */
	@Override
	public boolean useSilentPreTrigger() {
		return true;
	}
	
	
	
	// Ability usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#handleAttackPreTrigger(org.saga.listeners.events.SagaEntityDamageEvent)
	 */
	@Override
	public boolean handleAttackPreTrigger(SagaDamageEvent event) {
		return handlePreTrigger();
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#triggerAttack(org.saga.listeners.events.SagaEntityDamageEvent)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean triggerAttack(SagaDamageEvent event) {

		
		// Defender must be player:
		if(!(event.sagaDefender instanceof SagaPlayer)) return false;
		Player defenderPlayer = (Player) event.sagaDefender.getWrapped();
		
		// Only melee and ranged:
		if(event.type != DamageType.MELEE && event.type != DamageType.RANGED) return false;
		
		// Only if the target is holding a sword:
		Material targetsItem = event.sagaDefender.getHandItem().getType();
		if(targetsItem != Material.DIAMOND_SWORD && targetsItem != Material.GOLD_SWORD && targetsItem != Material.IRON_SWORD && targetsItem != Material.STONE_SWORD && targetsItem != Material.WOOD_SWORD && targetsItem != Material.BOW) return false;
		
		// Determine disarm:
		Random random = new Random();
		double disarm = 0.0;
		if(event.type == DamageType.MELEE){
			disarm = getDefinition().getFunction(MELEE_DISARM_CHANCE_KEY).value(getScore());
		}else{
			disarm = getDefinition().getFunction(RANGED_DISARM_CHANCE_KEY).value(getScore());
		}
		if(random.nextDouble() > disarm) return false;
		
		// Slots:
		int firstSlot = defenderPlayer.getInventory().getHeldItemSlot();
		int secondSlot = firstSlot + 1;
		if(firstSlot == 0){
			secondSlot = 2;
		}else if(firstSlot == 8){
			secondSlot = 7;
		}else{
			if(random.nextBoolean()){
				secondSlot = firstSlot + 1;
			}else{
				secondSlot = firstSlot - 1;
			}
		}
		
		ItemStack firstStack = defenderPlayer.getInventory().getItem(firstSlot);
		ItemStack secondStack = defenderPlayer.getInventory().getItem(secondSlot);
		
		// Disarm:
		defenderPlayer.getInventory().clear(firstSlot);
		defenderPlayer.getInventory().clear(secondSlot);
		
		if(secondStack != null && !secondStack.getType().equals(Material.AIR)) defenderPlayer.getInventory().setItem(firstSlot, secondStack);
		if(firstStack != null && !firstStack.getType().equals(Material.AIR)) defenderPlayer.getInventory().setItem(secondSlot, firstStack);
		
		// Update inventory:
		defenderPlayer.updateInventory();
		
		return true;
		
		
	}
	
	
}
