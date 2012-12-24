package org.saga.abilities;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.saga.attributes.DamageType;
import org.saga.listeners.events.SagaEntityDamageEvent;

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
	public boolean handleAttackPreTrigger(SagaEntityDamageEvent event) {
		return handlePreTrigger();
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#triggerAttack(org.saga.listeners.events.SagaEntityDamageEvent)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean triggerAttack(SagaEntityDamageEvent event) {

		
		// Only player vs player:
		if(!event.isPvP()) return false;
		
		// Only melee and ranged:
		if(event.type != DamageType.MELEE && event.type != DamageType.RANGED) return false;
		
		// Only if the target is holding a sword:
		Material targetsItem = event.defenderPlayer.getItemInHand().getType();
		if(targetsItem != Material.DIAMOND_SWORD && targetsItem != Material.GOLD_SWORD && targetsItem != Material.IRON_SWORD && targetsItem != Material.STONE_SWORD && targetsItem != Material.WOOD_SWORD && targetsItem != Material.BOW) return false;
		
		Player defender = event.defenderPlayer.getPlayer();
		
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
		int firstSlot = defender.getInventory().getHeldItemSlot();
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
		
		ItemStack firstStack = defender.getInventory().getItem(firstSlot);
		ItemStack secondStack = defender.getInventory().getItem(secondSlot);
		
		// Disarm:
		defender.getInventory().clear(firstSlot);
		defender.getInventory().clear(secondSlot);
		
		if(secondStack != null && !secondStack.getType().equals(Material.AIR)) defender.getInventory().setItem(firstSlot, secondStack);
		if(firstStack != null && !firstStack.getType().equals(Material.AIR)) defender.getInventory().setItem(secondSlot, firstStack);
		
		// Update inventory:
		defender.updateInventory();
		
		return true;
		
		
	}
	
	
}
