package org.saga.abilities;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Sheep;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Shearing extends Ability{

	
	/**
	 * Bonus wool key.
	 */
	transient private static String BONUS_WOOL_KEY = "bonus wool";

	/**
	 * Bonus mushrooms key.
	 */
	transient private static String BONUS_MUSHROOMS_KEY = "bonus mushrooms";

	
	
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Shearing(AbilityDefinition definition) {
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
	
	
	
	// Usage:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#handleShearPreTrigger(org.bukkit.event.player.PlayerShearEntityEvent)
	 */
	@Override
	public boolean handleShearPreTrigger(PlayerShearEntityEvent event) {
		return handlePreTrigger();
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.abilities.Ability#onPlayerInteractPlayer(org.bukkit.event.player.PlayerInteractEntityEvent, org.saga.player.SagaPlayer)
	 */
	@Override
	public boolean triggerShear(PlayerShearEntityEvent event) {
		
		boolean triggered = false;
		
		// Sheep:
		if(event.getEntity() instanceof Sheep){
			
			Sheep sheep = (Sheep) event.getEntity();
			Location location = sheep.getLocation();
			
			Integer bonus = getDefinition().getFunction(BONUS_WOOL_KEY).randomIntValue(getScore());
			if(bonus < 1) return false;
			
			for (int i = 0; i < bonus; i++) {
				location.getWorld().dropItemNaturally(location, new ItemStack(Material.WOOL, 1, sheep.getColor().getWoolData()));
				triggered = true;
			}
			
		}
		
		// Mushroom cow:
		else if(event.getEntity() instanceof MushroomCow){
			
			MushroomCow mushroomCow = (MushroomCow) event.getEntity();
			Location location = mushroomCow.getLocation();
			
			Integer bonus = getDefinition().getFunction(BONUS_MUSHROOMS_KEY).randomIntValue(getScore());
			if(bonus < 1) return false;
			
			for (int i = 0; i < bonus; i++) {
				location.getWorld().dropItemNaturally(location, new ItemStack(Material.RED_MUSHROOM, 1));
				triggered = true;
			}
			
		}
		
		return triggered;
		
	}

	
}
