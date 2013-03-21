package org.saga.abilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.Saga;
import org.saga.config.VanillaConfiguration;
import org.saga.messages.AbilityMessages;
import org.saga.messages.effects.StatsEffectHandler;
import org.saga.player.SagaLiving;
import org.saga.player.SagaPlayer;

public class Lightning extends Ability{

	/**
	 * Damage key.
	 */
	private static String DAMAGE = "damage";
	
	/**
	 * Distance key.
	 */
	private static String DISTANCE = "distance";
	
	/**
	 * Damage range key.
	 */
	private static String RANGE = "damage range";
	
	
	
	// Initialisation:
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Lightning(AbilityDefinition definition) {
		
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
	 * @see org.saga.abilities.Ability#triggerInteract(org.bukkit.event.player.PlayerInteractEvent)
	 */
	@Override
	public boolean triggerInteract(PlayerInteractEvent event) {
		
		
		SagaLiving sagaLiving = getSagaLiving();
		LivingEntity shooter = sagaLiving.getWrapped();
		Location target = null;
		
		// Target:
		Block block = sagaLiving.getWrapped().getTargetBlock(null, getDefinition().getFunction(DISTANCE).intValue(getScore()));
		if(block.getType() == Material.AIR){
			sagaLiving.message(AbilityMessages.targetTooFar(this));
			return false;
		}
		
		byte skyLight = VanillaConfiguration.getSkyLightLevel();
		for (int x = -1; x <= 1; x++) {
			for (int y = - 1; y <= 1; y++) {
				for (int z = - 1; z <= 1; z++) {
					if(block.getRelative(x, y, z).getLightFromSky() == skyLight){
						target = block.getLocation().add(0.5, 0.5, 0.5);
						break;
					}
				}
			}
		}
		
		if(target == null){
			sagaLiving.message(AbilityMessages.cantUseUnderground(this));
			return false;
		}
		
		LightningStrike lightning = target.getWorld().strikeLightningEffect(target);

		// Nearby entities:
		double range = getDefinition().getFunction(RANGE).value(getScore());
		double range2 = range*range;
		List<Entity> nearby = lightning.getNearbyEntities(range, range, range);
		ArrayList<LivingEntity> filteredNearby = new ArrayList<LivingEntity>();
		for (Entity entity : nearby) {
			
			if(entity == shooter) continue;
			
			if(entity instanceof LivingEntity && entity.getLocation().distanceSquared(target) <= range2){
				filteredNearby.add((LivingEntity) entity);
			}
			
		}
		
		// Apply damage:
		int damage = getDefinition().getFunction(DAMAGE).intValue(getScore());
		for (LivingEntity living : filteredNearby) {
			
			EntityDamageByEntityEvent bevent = new EntityDamageByEntityEvent(shooter, living, DamageCause.LIGHTNING, damage);
			Saga.plugin().getServer().getPluginManager().callEvent(bevent);
			
			if(bevent.isCancelled()) continue;
			living.damage(bevent.getDamage(), shooter);
			
			// Play effect:
			living.getLocation().getWorld().playEffect(living.getLocation(), Effect.SMOKE, 4);
			
		}
		
		// Effect:
		StatsEffectHandler.playSpellCast(sagaLiving);
		
		if(getSagaLiving() instanceof SagaPlayer) StatsEffectHandler.playAnimateArm((SagaPlayer) getSagaLiving());
		
		return true;
		
		
	}
	
}
