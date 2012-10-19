package org.saga.config;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Fireball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.saga.player.SagaPlayer;

public class VanillaConfiguration {

	
	/**
	 * Random generator.
	 */
	public static Random random = new Random();
	
	
	
	/**
	 * Gets the damage amount armour multiplier.
	 * 
	 * Thanks, gamerzap.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 * @return armour multiplier
	 */
	public static double getArmourMultiplier(EntityDamageEvent event, SagaPlayer sagaPlayer) {
		
		
		// Armour not effective:
		if(
			event.getCause() != DamageCause.ENTITY_ATTACK &&
			event.getCause() != DamageCause.PROJECTILE &&
			event.getCause() != DamageCause.FIRE &&
			event.getCause() != DamageCause.LAVA &&
			event.getCause() != DamageCause.CONTACT &&
			event.getCause() != DamageCause.BLOCK_EXPLOSION
		) return 1.0;

		if(sagaPlayer.getPlayer() == null) return 1.0;
		
		PlayerInventory inventory = sagaPlayer.getPlayer().getInventory();

		ItemStack boots = inventory.getBoots();
		ItemStack helmet = inventory.getHelmet();
		ItemStack chestplate = inventory.getChestplate();
		ItemStack leggings = inventory.getLeggings();
		double reduction = 0.0;
		
		// Helmet:
		if(helmet != null)
			if (helmet.getType() == Material.LEATHER_HELMET) reduction = reduction + 0.04;
			else if (helmet.getType() == Material.GOLD_HELMET) reduction = reduction + 0.08;
			else if (helmet.getType() == Material.CHAINMAIL_HELMET) reduction = reduction + 0.08;
			else if (helmet.getType() == Material.IRON_HELMET) reduction = reduction + 0.08;
			else if (helmet.getType() == Material.DIAMOND_HELMET) reduction = reduction + 0.12;
		
		// Boots:
		if(boots != null)
			if (boots.getType() == Material.LEATHER_BOOTS) reduction = reduction + 0.04;
			else if (boots.getType() == Material.GOLD_BOOTS) reduction = reduction + 0.04;
			else if (boots.getType() == Material.CHAINMAIL_BOOTS) reduction = reduction + 0.04;
			else if (boots.getType() == Material.IRON_BOOTS) reduction = reduction + 0.08;
			else if (boots.getType() == Material.DIAMOND_BOOTS) reduction = reduction + 0.12;
		
		// Leggings:
		if(leggings != null)
			if (leggings.getType() == Material.LEATHER_LEGGINGS) reduction = reduction + 0.08;
			else if (leggings.getType() == Material.GOLD_LEGGINGS) reduction = reduction + 0.12;
			else if (leggings.getType() == Material.CHAINMAIL_LEGGINGS) reduction = reduction + 0.16;
			else if (leggings.getType() == Material.IRON_LEGGINGS) reduction = reduction + 0.20;
			else if (leggings.getType() == Material.DIAMOND_LEGGINGS) reduction = reduction + 0.24;
		
		// Chestplate:
		if(chestplate != null)
			if (chestplate.getType() == Material.LEATHER_CHESTPLATE) reduction = reduction + 0.12;
			else if (chestplate.getType() == Material.GOLD_CHESTPLATE) reduction = reduction + 0.20;
			else if (chestplate.getType() == Material.CHAINMAIL_CHESTPLATE) reduction = reduction + 0.20;
			else if (chestplate.getType() == Material.IRON_CHESTPLATE) reduction = reduction + 0.24;
			else if (chestplate.getType() == Material.DIAMOND_CHESTPLATE) reduction = reduction + 0.32;
			
		return 1-reduction;
		
		
	}
	
	
	/**
	 * Gets the blocking multiplier.
	 * 
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 * @return blocking multiplier
	 */
	public static double getBlockingMultiplier(EntityDamageEvent event, SagaPlayer sagaPlayer) {
		
		DamageCause cause = event.getCause();
		if(cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.PROJECTILE || cause == DamageCause.ENTITY_EXPLOSION || cause == DamageCause.BLOCK_EXPLOSION) return 0.5;
			
		return 1.0;
		
	}
	
	/**
	 * Gets the damage amount EPF multiplier for enchantments.
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 * @return EPF multiplier
	 */
	public static double getEPFMultiplier(EntityDamageEvent event, SagaPlayer sagaPlayer) {
		
		
		if(sagaPlayer.getPlayer() == null) return 1.0;
		
		PlayerInventory inventory = sagaPlayer.getPlayer().getInventory();

		ItemStack boots = inventory.getBoots();
		ItemStack helmet = inventory.getHelmet();
		ItemStack chestplate = inventory.getChestplate();
		ItemStack leggings = inventory.getLeggings();
		
		int epf = 0;
		
		DamageCause cause = event.getCause();
		if((event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) event).getDamager() instanceof Fireball)){
			cause = DamageCause.FIRE;
		}
		
		boolean protection = false;
		switch (cause) {
			case ENTITY_ATTACK:
				
				protection = true;
				break;
				
			case FIRE:
				
				// Fire protection:
				if(helmet != null) epf+= getEPF(helmet.getEnchantmentLevel(Enchantment.PROTECTION_FIRE));
				if(chestplate != null) epf+= getEPF(chestplate.getEnchantmentLevel(Enchantment.PROTECTION_FIRE));
				if(leggings != null) epf+= getEPF(leggings.getEnchantmentLevel(Enchantment.PROTECTION_FIRE));
				if(boots != null) epf+= getEPF(boots.getEnchantmentLevel(Enchantment.PROTECTION_FIRE));
				
				protection = true;
				break;

			case LAVA:

				// Fire protection:
				if(helmet != null) epf+= getEPF(helmet.getEnchantmentLevel(Enchantment.PROTECTION_FIRE));
				if(chestplate != null) epf+= getEPF(chestplate.getEnchantmentLevel(Enchantment.PROTECTION_FIRE));
				if(leggings != null) epf+= getEPF(leggings.getEnchantmentLevel(Enchantment.PROTECTION_FIRE));
				if(boots != null) epf+= getEPF(boots.getEnchantmentLevel(Enchantment.PROTECTION_FIRE));
				
				protection = true;
				break;

			case BLOCK_EXPLOSION:

				// Explosion protection:
				if(helmet != null) epf+= getEPF(helmet.getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS));
				if(chestplate != null) epf+= getEPF(chestplate.getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS));
				if(leggings != null) epf+= getEPF(leggings.getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS));
				if(boots != null) epf+= getEPF(boots.getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS));
				
				protection = true;
				break;
				
			case ENTITY_EXPLOSION:

				// Explosion protection:
				if(helmet != null) epf+= getEPF(helmet.getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS));
				if(chestplate != null) epf+= getEPF(chestplate.getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS));
				if(leggings != null) epf+= getEPF(leggings.getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS));
				if(boots != null) epf+= getEPF(boots.getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS));
				
				protection = true;
				break;
				
			case PROJECTILE:

				// Explosion protection:
				if(helmet != null) epf+= getEPF(helmet.getEnchantmentLevel(Enchantment.PROTECTION_PROJECTILE));
				if(chestplate != null) epf+= getEPF(chestplate.getEnchantmentLevel(Enchantment.PROTECTION_PROJECTILE));
				if(leggings != null) epf+= getEPF(leggings.getEnchantmentLevel(Enchantment.PROTECTION_PROJECTILE));
				if(boots != null) epf+= getEPF(boots.getEnchantmentLevel(Enchantment.PROTECTION_PROJECTILE));
				
				protection = true;
				break;
				
			case FALL:

				// Fall protection:
				if(helmet != null) epf+= 2*getEPF(helmet.getEnchantmentLevel(Enchantment.PROTECTION_FALL));
				if(chestplate != null) epf+= 2*getEPF(chestplate.getEnchantmentLevel(Enchantment.PROTECTION_FALL));
				if(leggings != null) epf+= 2*getEPF(leggings.getEnchantmentLevel(Enchantment.PROTECTION_FALL));
				if(boots != null) epf+= 2*getEPF(boots.getEnchantmentLevel(Enchantment.PROTECTION_FALL));
				
				protection = true;
				break;
				
			default:
				
				break;
				
		}
		
		if(protection){

			// Protection:
			if(helmet != null) epf+= getEPF(helmet.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL));
			if(chestplate != null) epf+= getEPF(chestplate.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL));
			if(leggings != null) epf+= getEPF(leggings.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL));
			if(boots != null) epf+= getEPF(boots.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL));
			
		}
		
		// Cap I:
		if(epf > 25) epf = 25;

		// Randomise:
		epf = (int) Math.ceil(((random.nextDouble() * epf + epf) / 2.0));
		
		// Cap II:
		if(epf > 20) epf = 20;
		
		return 1.0 - epf * 0.04;

		
	}
	
	/**
	 * Gets the EPF value for given enchantment level
	 * 
	 * @param enchLevel enchantment level
	 * @return EPF value
	 */
	public static int getEPF(int enchLevel) {

		if(enchLevel == 0) return 0;
		
		return (int) ( (6 + enchLevel * enchLevel) / 2 );
		
	}

	
	/**
	 * Check is the given damage cause listens to no damage ticks
	 * 
	 * @param cause event cause
	 * @return true if no damage ticks are involved
	 */
	public static boolean hasTicks(DamageCause cause) {

		return cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.FIRE || cause == DamageCause.CONTACT || cause == DamageCause.SUFFOCATION || cause == DamageCause.LAVA;
		
	}
	
	
	/**
	 * Gets item base damage.
	 * 
	 * @param material item material
	 * @return base damage
	 */
	public static int getBaseDamage(Material material) {
		
		switch (material) {
			
			case DIAMOND_SWORD: return 7;
			case IRON_SWORD: return 6;
			case STONE_SWORD: return 5;
			case GOLD_SWORD: return 4;
			case WOOD_SWORD: return 4;

			case DIAMOND_AXE: return 6;
			case IRON_AXE: return 5;
			case STONE_AXE: return 4;
			case GOLD_AXE: return 3;
			case WOOD_AXE: return 3;

			case DIAMOND_PICKAXE: return 5;
			case IRON_PICKAXE: return 4;
			case STONE_PICKAXE: return 3;
			case GOLD_PICKAXE: return 2;
			case WOOD_PICKAXE: return 2;

			case DIAMOND_SPADE: return 4;
			case IRON_SPADE: return 3;
			case STONE_SPADE: return 2;
			case GOLD_SPADE: return 1;
			case WOOD_SPADE: return 1;
			default: return 1;
			
		}
		
	}
	
}
