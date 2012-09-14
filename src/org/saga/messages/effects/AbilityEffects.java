package org.saga.messages.effects;

import net.minecraft.server.DataWatcher;
import net.minecraft.server.EntityLiving;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.abilities.Ability;
import org.saga.player.SagaPlayer;

public class AbilityEffects {

	
	public static void playMinorAbility(SagaPlayer sagaPlayer, Integer level) {

		sagaPlayer.playGlobalEffect(Effect.MOBSPAWNER_FLAMES, 6, sagaPlayer.getLocation());
		sagaPlayer.playGlobalEffect(Effect.MOBSPAWNER_FLAMES, 6, sagaPlayer.getLocation().add(0.0, 1.0, 0.0));
		sagaPlayer.playGlobalEffect(Effect.MOBSPAWNER_FLAMES, 6, sagaPlayer.getLocation().add(0.0, 2.0, 0.0));

		for (int i = 0; i < level; i++) {
			sagaPlayer.playGlobalEffect(Effect.MOBSPAWNER_FLAMES, 6, sagaPlayer.getLocation().add(0.0, i, 0.0));
		}

	}
	
	public static void playAbilityEffect(SagaPlayer sagaPlayer, Ability ability) {
		
		
		Integer colour = ability.getDefinition().getColour();
		if(colour < 1) return;
		
		try {
			addPotionGraphicalEffect(sagaPlayer.getPlayer(), colour, 30);
		}
		catch (Throwable e) {
			SagaLogger.severe(AbilityEffects.class, "failed to play ability effect: " + e.getClass().getSimpleName() + ":" + e.getMessage());
		}
		
		
	}
	
	
	public static void playSpellCast(SagaPlayer sagaPlayer) {
		
		
		// Smoke:
		for (int i = 5; i <= 12; i++) {
			sagaPlayer.playGlobalEffect(Effect.SMOKE, i);
		}
		
		// Sound:
		sagaPlayer.playGlobalEffect(Effect.GHAST_SHOOT, 0);
		
		
	}
	
	public static void playRecharge(SagaPlayer sagaPlayer) {
	
	
		// Flames:
		sagaPlayer.playGlobalEffect(Effect.BLAZE_SHOOT, 0);
		sagaPlayer.playGlobalEffect(Effect.MOBSPAWNER_FLAMES, 6);
		
		// Sound:
		sagaPlayer.playGlobalEffect(Effect.GHAST_SHOOT, 0);
	
	
	}
	
	public static void playSign(SagaPlayer sagaPlayer) {
		
		
		// Flames:
//		sagaPlayer.playGlobalEffect(Effect.BLAZE_SHOOT, 0);
		sagaPlayer.playGlobalEffect(Effect.MOBSPAWNER_FLAMES, 6);
		
		// Sound:
		sagaPlayer.playEffect(Effect.CLICK1, 0);
	
	
	}
	
	/**
	 * Adds a potion graphical effect to the entity.
	 * 
	 * @author nisovin
	 * 
	 * @param entity entity
	 * @param color colour
	 * @param duration duration in 1/20 seconds
	 */
	public static void addPotionGraphicalEffect(LivingEntity entity, int color, int duration) {
		
		
		final EntityLiving el = ((CraftLivingEntity)entity).getHandle();
		final DataWatcher dw = el.getDataWatcher();
		
		dw.watch(8, Integer.valueOf(color));
		 
		Bukkit.getScheduler().scheduleSyncDelayedTask(Saga.plugin(), new Runnable() {
		
			public void run() {
			
				int c = 0;
				if (!el.effects.isEmpty()) {
					c = net.minecraft.server.PotionBrewer.a(el.effects.values());
				}
				dw.watch(8, Integer.valueOf(c));
				
			}
			
		}, duration);
		
		
	}
	
	
}
