package org.saga.abilities;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.statistics.StatisticsManager;

public class Repair extends Ability{

	
	// Initialization:
	/**
	 * Initializes using definition.
	 * 
	 * @param definition ability definition
	 */
	public Repair(AbilityDefinition definition) {
		
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
		
		
		if(event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.IRON_BLOCK){
			return false;
		}

		// Check pre use:
		if(!handlePreUse()){
			return false;
		}
		
		// Get item:
		ItemStack item = event.getPlayer().getItemInHand();
		if(item == null || item.getType() == Material.AIR){
			return false;
		}
		
		// Already repaired:
		if(item.getDurability() <= 0) return false;
		
		// Repair amount:
		Integer skillLevel = getSkillLevel();
		Short repair = (short) ((double)getDefinition().getPrimaryFunction().calculateRandomIntValue(skillLevel) * (double)item.getType().getMaxDurability() / (double)getValueMaterials(item.getType()));
		if(item.getDurability() - repair < 0){
			repair = item.getDurability();
		}
		
		// Repair:
		item.setDurability((short) (item.getDurability() - repair));

		// Play effect:
		Location location = event.getClickedBlock().getLocation();
		Random random = new Random();
		getSagaPlayer().playGlobalEffect(Effect.STEP_SOUND, Material.IRON_BLOCK.getId(), location);
		for (int i = 5; i <= 12; i++) {
			
			if(random.nextBoolean()) continue;
			
			getSagaPlayer().playGlobalEffect(Effect.SMOKE, i, location);
			
		}
		
		// Award exp:
		Double awardedExp = awardExperience(repair.intValue());
		
		// Statistics:
		StatisticsManager.manager().onAbilityUse(getName(), awardedExp);
		
		return true;
		
		
	}
	
	
	/**
	 * Gets the amount on valuable materials.
	 * 
	 * @param type type
	 * @return item amount
	 */
	private static int getValueMaterials(Material type) {

		
		// Pick axe:
		if(type == Material.STONE_PICKAXE || type == Material.IRON_PICKAXE || type == Material.GOLD_PICKAXE || type == Material.DIAMOND_PICKAXE){
			return 3;
		}
		
		// Axe:
		if(type == Material.STONE_AXE || type == Material.IRON_AXE || type == Material.GOLD_AXE || type == Material.DIAMOND_AXE){
			return 3;
		}

		// Hoe:
		if(type == Material.STONE_HOE || type == Material.IRON_HOE || type == Material.GOLD_HOE || type == Material.DIAMOND_HOE){
			return 2;
		}

		// Shovel:
		if(type == Material.STONE_SPADE || type == Material.IRON_SPADE || type == Material.GOLD_SPADE || type == Material.DIAMOND_SPADE){
			return 1;
		}

		// Sword:
		if(type == Material.STONE_SWORD || type == Material.IRON_SWORD || type == Material.GOLD_SWORD || type == Material.DIAMOND_SWORD){
			return 2;
		}
		
		// Helmet:
		if(type == Material.LEATHER_HELMET || type == Material.CHAINMAIL_HELMET || type == Material.IRON_HELMET || type == Material.GOLD_HELMET || type == Material.DIAMOND_HELMET){
			return 5;
		}

		// Chest plate:
		if(type == Material.LEATHER_CHESTPLATE || type == Material.CHAINMAIL_CHESTPLATE || type == Material.IRON_CHESTPLATE || type == Material.GOLD_CHESTPLATE || type == Material.DIAMOND_CHESTPLATE){
			return 8;
		}

		// Leggings:
		if(type == Material.LEATHER_LEGGINGS || type == Material.CHAINMAIL_LEGGINGS || type == Material.IRON_LEGGINGS || type == Material.GOLD_LEGGINGS || type == Material.DIAMOND_LEGGINGS){
			return 7;
		}

		// Boots:
		if(type == Material.LEATHER_BOOTS || type == Material.CHAINMAIL_BOOTS || type == Material.IRON_BOOTS || type == Material.GOLD_BOOTS || type == Material.DIAMOND_BOOTS){
			return 4;
		}

		// Bow:
		if(type == Material.BOW){
			return 3;
		}
		
		return 1;
		
	}
	
}
