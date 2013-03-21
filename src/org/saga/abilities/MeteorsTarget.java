package org.saga.abilities;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.saga.Saga;
import org.saga.config.VanillaConfiguration;
import org.saga.messages.AbilityMessages;
import org.saga.messages.effects.StatsEffectHandler;
import org.saga.player.SagaLiving;
import org.saga.player.SagaPlayer;

public class MeteorsTarget extends Ability{

	
	/**
	 * Shoot height.
	 */
	private static Double SHOOT_HEIGHT = 10.0;

	/**
	 * Shoot delay.
	 */
	private static Long SHOOT_TICKS_DELAY = 5L;
	
	/**
	 * Shoot shift vectors.
	 */
	@SuppressWarnings("serial")
	transient private final static ArrayList<Vector> SHOOT_SHIFTS = new ArrayList<Vector>(){
		{
			add(new Vector(1.25, SHOOT_HEIGHT, 0));
			add(new Vector(-1.25, SHOOT_HEIGHT, 0));
			add(new Vector(0.0, SHOOT_HEIGHT, 1.25));
			add(new Vector(0.0, SHOOT_HEIGHT, -1.25));
			add(new Vector(1.25, SHOOT_HEIGHT, 1.25));
			add(new Vector(-1.25, SHOOT_HEIGHT, -1.25));
			add(new Vector(1.25, SHOOT_HEIGHT,- 1.25));
			add(new Vector(-1.25, SHOOT_HEIGHT, 1.25));
			
			add(new Vector(2.5, SHOOT_HEIGHT, 0));
			add(new Vector(-2.5, SHOOT_HEIGHT, 0));
			add(new Vector(0.0, SHOOT_HEIGHT, 2.5));
			add(new Vector(0.0, SHOOT_HEIGHT, -2.5));
			add(new Vector(2.5, SHOOT_HEIGHT, 2.5));
			add(new Vector(-2.5, SHOOT_HEIGHT, -2.5));
			add(new Vector(2.5, SHOOT_HEIGHT,- 2.5));
			add(new Vector(-2.5, SHOOT_HEIGHT, 2.5));
		}
	};
	
	
	/**
	 * Distance key.
	 */
	private static String DISTANCE = "distance";
	
	/**
	 * Speed range key.
	 */
	private static String SPEED = "speed";
	
	/**
	 * Number of additional fireballs key.
	 */
	private static String BONUS_FIREBALLS = "bonus fireballs";
	
	
	
	// Initialisation:
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public MeteorsTarget(AbilityDefinition definition) {
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
		final LivingEntity shooter = sagaLiving.getWrapped();
		
		// Target:
		Block block = sagaLiving.getWrapped().getTargetBlock(null, getDefinition().getFunction(DISTANCE).intValue(getScore()));
		if(block.getType() == Material.AIR){
			sagaLiving.message(AbilityMessages.targetTooFar(this));
			return false;
		}
		final Location target = block.getLocation().add(0.5, 0.5, 0.5);;
		
		// Check if not underground:
		if(!checkSky(block)){
			sagaLiving.message(AbilityMessages.cantUseUnderground(this));
			return false;
		}
		
		// Fire the balls balls balls:
		Random random = new Random();
		int bonusFireballs = getDefinition().getFunction(BONUS_FIREBALLS).intValue(getScore());;
		final double speed = getDefinition().getFunction(SPEED).value(getScore());
		
		long delay = SHOOT_TICKS_DELAY;
		
		shootFireball(shooter, target.clone().add(0.0, SHOOT_HEIGHT, 0.0), speed);
		ArrayList<Vector> shootShifts = new ArrayList<Vector>(SHOOT_SHIFTS);
		while(shootShifts.size() > 0 && bonusFireballs > 0){
			
			int i = random.nextInt(shootShifts.size());
			final Location shootLocation = target.clone().add(shootShifts.get(i));
			
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Saga.plugin(), new Runnable() {
				@Override
				public void run() {
					shootFireball(shooter, shootLocation, speed);
				}
			}, delay);
			
			delay+= SHOOT_TICKS_DELAY;
			
			shootShifts.remove(i);
			bonusFireballs--;
			
		}
		
		// Effect:
		StatsEffectHandler.playSpellCast(sagaLiving);
		
		if(getSagaLiving() instanceof SagaPlayer) StatsEffectHandler.playAnimateArm((SagaPlayer) getSagaLiving());
		
		return true;
		
		
	}
	
	/**
	 * Shoots a fireball straight down.
	 * 
	 * @param shooter shooter
	 * @param shootLocation shoot location
	 * @param speed speed
	 * @return shot fireball
	 */
	public Fireball shootFireball(LivingEntity shooter, Location shootLocation, Double speed) {

		
		// Direction vector:
		Vector directionVector = new Vector(0.0, -1.0, 0.0);
		
		// Create the fireball:
		Fireball fireball = shootLocation.getWorld().spawn(shootLocation, Fireball.class);
		fireball.setDirection(directionVector.clone());
		fireball.setVelocity(directionVector.multiply(speed));
		
		// Set shooter:
		fireball.setShooter(shooter);
		
		// Remove fire:
		fireball.setIsIncendiary(false);
		return fireball;
		
		
	}
	
	/**
	 * Checks if the target block or nearby blocks see the sky.
	 * 
	 * @param block block
	 * @return true if sky is visible
	 */
	private static boolean checkSky(Block block) {

		
		byte skyLight = VanillaConfiguration.getSkyLightLevel();
		for (int x = -1; x <= 1; x++) {
			for (int y = - 1; y <= 1; y++) {
				for (int z = - 1; z <= 1; z++) {
					if(block.getRelative(x, y, z).getLightFromSky() == skyLight){
						return true;
					}
				}
			}
		}
		
		return false;
		
		
	}
	
	
}
