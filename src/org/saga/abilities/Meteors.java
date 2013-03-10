package org.saga.abilities;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.saga.Saga;
import org.saga.messages.effects.StatsEffectHandler;
import org.saga.player.SagaPlayer;
import org.saga.shape.Point;
import org.saga.shape.TrapezoidGrid;
import org.saga.utility.TwoPointFunction;

public class Meteors extends Ability{

	
	/**
	 * Shoot height.
	 */
	private static Double SHOOT_HEIGHT = 10.0;

	/**
	 * Shoot delay.
	 */
	private static Long SHOOT_TICKS_DELAY = 2L;
	
	
	/**
	 * Fireball density key.
	 */
	private static String DENSITY_KEY = "density";

	/**
	 * Fireball density multiplier key.
	 */
	private static String DENSITY_MULTIPLIER_KEY = "density multiplier";
	
	
	/**
	 * Speed range key.
	 */
	private static String SPEED_KEY = "speed";
	
	
	
	// Initialisation:
	/**
	 * Initialises using definition.
	 * 
	 * @param definition ability definition
	 */
	public Meteors(AbilityDefinition definition) {
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
		
		
		int width = 3;
		int height = 6;
		int bottom = 1;
		double step = 0.5;
		double shift = 2.5;
		
		Location loc = event.getPlayer().getLocation();
		World world = loc.getWorld();
		final Player player = event.getPlayer();
		
		TrapezoidGrid trapezoid = new TrapezoidGrid(1.0, width, height, bottom, step);
		
		// Find angle:
		Vector dir = loc.getDirection();
		double rot = Math.atan(dir.getZ() / dir.getX());
		if(dir.getX() < 0.0) rot-= Math.PI;
		
		// Create position grid:
		ArrayList<ArrayList<Point>> grid = trapezoid.create(shift, loc.getX(), loc.getZ(), rot);
		
		// Number of fireballs:
		int[] fireballs = new int[grid.size()];
		double densMult = getDefinition().getFunction(DENSITY_MULTIPLIER_KEY).value(getScore());
		TwoPointFunction densityFunction = getDefinition().getFunction(DENSITY_KEY);
		for (int r = 0; r < grid.size(); r++) {
			
			double density = densityFunction.value(r) * densMult;
			fireballs[r] = (int) (density * grid.get(r).size());
			
		}
		
		// Create shoot order:
		final Deque<Point> shoorOrder = new ArrayDeque<Point>();
		for (int r = 0; r < fireballs.length; r++) {
			
			while (fireballs[r] > 0){
				
				// Find points:
				ArrayList<Point> column = grid.get(r);
				if(column.size() == 0){
					fireballs[r] = 0;
					continue;
				}
				
				// Take fireball:
				fireballs[r]--;
				
				// Take point:
				int index = Saga.RANDOM.nextInt(column.size());
				shoorOrder.push(column.remove(index));
				
			}
			
		}
		
		// Fire the balls balls balls:
		final double speed = getDefinition().getFunction(SPEED_KEY).value(getScore());
		int count = 0;
		boolean trigger = false;
		
		while(!shoorOrder.isEmpty()){
			
			count++;
			
			Point point = shoorOrder.pollLast();
			final Location target = new Location(world, point.getX(), world.getHighestBlockYAt(loc) + SHOOT_HEIGHT, point.getZ());
			
			if(Math.abs(target.getY() - loc.getY()) > SHOOT_HEIGHT) continue;
			trigger = true;
			
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Saga.plugin(), new Runnable() {
				@Override
				public void run() {
					shootFireball(player, target, speed);
				}
			}, count * SHOOT_TICKS_DELAY);
			
		}
		
		if(!trigger) return false;
		
		// Effect:
		StatsEffectHandler.playSpellCast(getSagaLiving());
		
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
		fireball.setYield(0.65f);
		return fireball;
		
		
	}
	
	
}
