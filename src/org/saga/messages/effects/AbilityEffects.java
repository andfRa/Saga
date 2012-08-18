package org.saga.messages.effects;

import org.bukkit.Effect;
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
	
}
