package org.saga.abilities;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.Saga;
import org.saga.listeners.events.SagaEventHandler;
import org.saga.listeners.events.SagaPvpEvent;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;

public class Bash extends Ability{

	
	/**
	 * Initializes using definition.
	 * 
	 * @param definition ability definition
	 */
	public Bash(AbilityDefinition definition) {
		
        super(definition);
	
	}

	
	// Ability usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#onPlayerInteractPlayer(org.bukkit.event.player.PlayerInteractEntityEvent, org.saga.player.SagaPlayer)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean onPlayerInteractPlayer(PlayerInteractEntityEvent event, SagaPlayer targetPlayer) {

		
		// Check preuse:
		if(!handlePreUse()){
			return false;
		}
		
		// Only player vs player:
		if(!(event.getRightClicked() instanceof Player)){
			return false;
		}
		Player defender = (Player)event.getRightClicked();
		
		// Damage:
		int damage = getDefinition().getPrimaryFunction().randomIntValue(getSkillLevel());
		
		// Pvp event:
		EntityDamageByEntityEvent edbeevent = new EntityDamageByEntityEvent(event.getPlayer(), event.getRightClicked(), DamageCause.ENTITY_ATTACK, damage);
		
		// Handle pvp:
		SagaEventHandler.handlePvp(new SagaPvpEvent(getSagaPlayer(), targetPlayer, getSagaPlayer().getSagaChunk(), targetPlayer.getSagaChunk(), edbeevent));

		if(edbeevent.isCancelled()){
			return false;
		}
		
		// Determine disarm:
		Random random = new Random();
		int disarm = random.nextInt(getDefinition().getSecondaryFunction().randomIntValue(getSkillLevel())) +1;
		
		// Normalize disarm:
		if(disarm < 1){
			disarm = 1;
			Saga.severe(this, "disarm below 1", "using minimum");
		}
		if(disarm > 35){
			disarm = 35;
			Saga.severe(this, "disarm above 35", "using maximum");
		}
		
		// Slots:
		int firstSlot = defender.getInventory().getHeldItemSlot();
		int secondSlot = firstSlot;
		
		if(random.nextBoolean() && firstSlot != 0){
			disarm *= -1;
		}
		
		// Add disarm:
		secondSlot += disarm;
		
		// Normalize:
		if(secondSlot < 0) secondSlot = 0;
		if(secondSlot > 35) secondSlot = 35;
		
		
		ItemStack firstStack = defender.getInventory().getItem(firstSlot);
		ItemStack secondStack = defender.getInventory().getItem(secondSlot);
		
		// Disarm:
		defender.getInventory().clear(firstSlot);
		defender.getInventory().clear(secondSlot);

		if(secondStack != null && !secondStack.getType().equals(Material.AIR)) defender.getInventory().setItem(firstSlot, secondStack);
		if(firstStack != null && !firstStack.getType().equals(Material.AIR)) defender.getInventory().setItem(secondSlot, firstStack);
		
		// Update inventory:
		defender.updateInventory();
		
		// Damage:
		defender.damage(damage, event.getPlayer());

		// Award exp:
		Double awardedExp = awardExperience();
		
		// Statistics:
		StatisticsManager.manager().onAbilityUse(getName(), awardedExp);
		
		return true;
		
		
	}
	
	
}
