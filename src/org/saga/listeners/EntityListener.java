package org.saga.listeners;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.saga.Saga;
import org.saga.chunkGroups.ChunkGroupManager;
import org.saga.chunkGroups.SagaChunk;
import org.saga.config.BalanceConfiguration;
import org.saga.guilds.GuildsManager;
import org.saga.listeners.events.SagaEventHandler;
import org.saga.listeners.events.SagaPvpEvent;
import org.saga.player.GuardianRune;
import org.saga.player.PlayerMessages;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;

public class EntityListener implements Listener{

	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent event) {
		
		
		// Damaged by entity:
		if(event instanceof EntityDamageByEntityEvent){
			
			onEntityDamageByEntity((EntityDamageByEntityEvent)event);
			
		}
		
		// Player damage:
		else if(event.getCause() == DamageCause.FIRE_TICK && event.getEntity() instanceof Player){

			Player player = (Player) event.getEntity();
			SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(player.getName());
			
			// No player:
	    	if(sagaPlayer == null){
	    		Saga.warning("Can't continue with onEntityDamage, because saga player for " + player.getName() + " isn't loaded.");
	    		return;
	    	}

	    	sagaPlayer.getLevelManager().onEntityDamage(event);
	    	
		}
		
		
	}
	
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		
		
		SagaPlayer sagaAttacker = null;
		SagaPlayer sagaDefender = null;
		
		// Not a living:
		if(!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity defender= (LivingEntity) event.getEntity();
		
		// Damage ticks:
		if(defender.getNoDamageTicks() > defender.getMaximumNoDamageTicks()/2F){
			event.setCancelled(true);
			return;
		}
		
		// Dead:
		if(defender.getHealth() <= 0) return;
		
		// Damaged by another entity:
		Projectile projectile = null;
		Entity attacker = event.getDamager();
		
		// Damaged by an arrow:
		if(attacker instanceof Projectile){
			
			projectile = (Projectile) attacker;
			attacker = projectile.getShooter();
			
		}
		
		// Get attacker saga player:
		if(attacker instanceof Player){

			sagaAttacker = Saga.plugin().getSagaPlayer( ((Player) attacker).getName() );

	    	// No player:
	    	if(sagaAttacker == null){
	    		Saga.warning("Can't continue with onEntityDamageByEntity, because the saga player for "+ ((Player) attacker).getName() + " isn't loaded.");
	    		return;
	    	}
	    	
		}
		
		// Get defender saga player:
		if(defender instanceof Player){

			sagaDefender = Saga.plugin().getSagaPlayer( ((Player) defender).getName() );

	    	// No player:
	    	if(sagaDefender == null){
	    		Saga.warning("Can't continue with onEntityDamageByEntity, because the saga player for "+ ((Player) defender).getName() + " isn't loaded.");
	    		return;
	    	}
			
		}
		
		// Player versus player:
		if(sagaAttacker != null && sagaDefender != null){
			
			// Close combat:
			if(projectile == null){

				// Handle pvp:
				SagaEventHandler.handlePvp(new SagaPvpEvent(sagaAttacker, sagaDefender, sagaAttacker.getSagaChunk(), sagaDefender.getSagaChunk(), event));

				if(event.isCancelled()) return;
				
				sagaAttacker.getLevelManager().onHitPlayer(event, sagaDefender);
				sagaDefender.getLevelManager().onHitByPlayer(event, sagaAttacker);
				
			}
			// Archery:
			else if(projectile instanceof Arrow){
				
				// Handle pvp:
				SagaEventHandler.handlePvp(new SagaPvpEvent(sagaAttacker, sagaDefender, sagaAttacker.getSagaChunk(), sagaDefender.getSagaChunk(), event));

				if(event.isCancelled()) return;
				
				sagaAttacker.getLevelManager().onShotPlayer(event, sagaDefender, projectile);
				sagaDefender.getLevelManager().onShotByPlayer(event, sagaAttacker, projectile);

			}
			// Magic:
			else if(projectile instanceof Fireball){
				
				// Handle pvp:
				SagaEventHandler.handlePvp(new SagaPvpEvent(sagaAttacker, sagaDefender, sagaAttacker.getSagaChunk(), sagaDefender.getSagaChunk(), event));

				if(event.isCancelled()) return;
				
				// Set base damage:
				event.setDamage(BalanceConfiguration.config().pvpBaseFireballDamage);
				
				sagaAttacker.getLevelManager().onSpelledPlayer(event, sagaDefender, projectile);
				sagaDefender.getLevelManager().onSpelledByPlayer(event, sagaAttacker, projectile);

			}
			
			
		}
		
		// Player versus creature:
		else if(defender instanceof Creature && sagaAttacker != null){
			
			// Forward to saga chunk:
			SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(defender.getLocation());
			if(sagaChunk != null) sagaChunk.onPlayerDamagedCreature(event, sagaAttacker, (Creature)defender);
			
			// Forward to saga chunk group:
			
			if(event.isCancelled()) return;
			
			// Close combat:
			if(projectile == null){
				sagaAttacker.getLevelManager().onHitCreature(event, (Creature)defender);
			}
			// Archery:
			else if(projectile instanceof Arrow){
				sagaAttacker.getLevelManager().onShotCreature(event, (Creature)defender, projectile);
			}
			// Magic:
			else if(projectile instanceof Fireball){
				
				// Set base damage:
				event.setDamage(BalanceConfiguration.config().pvcBaseFireballDamage);
				
				sagaAttacker.getLevelManager().onSpelledCreature(event, (Creature)defender, projectile);
				
			}
			
		}
		
		// Defend from a creature:
		else if(attacker instanceof Creature && sagaDefender != null){

			// Close combat:
			if(projectile == null){
				sagaDefender.getLevelManager().onHitByCreature(event, (Creature)attacker);
			}
			// Archery:
			else{
				sagaDefender.getLevelManager().onShotByCreature(event, (Creature)attacker, projectile);
			}
			
		}
		
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onProjectileHit(ProjectileHitEvent event) {
		
		
		if(!(event.getEntity() instanceof Projectile)) return;
		Projectile projectile = (Projectile) event.getEntity();
		
		if(!(projectile.getShooter() instanceof Player)) return;
		Player player = (Player) projectile.getShooter();
		
		// Get player:
    	SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(player.getName());
    	if(sagaPlayer == null){
    		Saga.warning("Can't continue with onProjectileHit, because the saga player for "+ player.getName() + " isn't loaded.");
    		return;
    	}

    	// Forward to level manager:
    	sagaPlayer.getLevelManager().onProjectileHit(event);
    	
		
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplode(EntityExplodeEvent event) {
		
		
		// Stop creeper terrain damage.
		if(BalanceConfiguration.config().stopCreeperExplosions && event.getEntity() instanceof Creeper){
			event.blockList().clear();
		}
		
		// Get saga chunk:
		SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(event.getLocation());
		
		// Forward to saga chunk:
		if(sagaChunk != null) sagaChunk.onEntityExplode(event);
		
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityBlockForm(EntityBlockFormEvent event) {
		

		// Get saga chunk:
		SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(event.getBlock().getLocation());
		
		// Forward to saga chunk:
		if(sagaChunk != null) sagaChunk.onEntityBlockForm(event);
		
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		
		
		// Get saga chunk:
		SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(event.getLocation());
		
		// Forward to saga chunk:
		if(sagaChunk != null) sagaChunk.onCreatureSpawn(event);
		
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent event) {

		
		Entity deadEntity = event.getEntity();
		Entity attackerEntity = null;
		
		Player deadPlayer = null;
		Player attackerPlayer = null;
		
		SagaPlayer sagaDead = null;
		SagaPlayer sagaAttacker = null;
		
		// Defender player:
		if(deadEntity instanceof Player){
			
			deadPlayer = (Player) deadEntity;

			sagaDead = Saga.plugin().getSagaPlayer( deadPlayer.getName() );
			
			// No player:
	    	if(sagaDead == null){
	    		Saga.warning("Can't continue with onEntityDeath, because saga player for " + deadPlayer.getName() + " isn't loaded.");
	    		return;
	    	}
			
		}

		// Attacker entity:
		EntityDamageEvent damageEvent = deadEntity.getLastDamageCause();
		
		if(damageEvent instanceof EntityDamageByEntityEvent){
			
			attackerEntity = ((EntityDamageByEntityEvent) damageEvent).getDamager();

			// Projectiles:
			if(attackerEntity instanceof Projectile){
				attackerEntity = ((Projectile) attackerEntity).getShooter();
			}
			
		}
		
		// Attacker player:
		if(attackerEntity instanceof Player){
		
			attackerPlayer = (Player) attackerEntity;
			
			sagaAttacker = Saga.plugin().getSagaPlayer( attackerPlayer.getName() );

			// No player:
	    	if(sagaAttacker == null){
	    		Saga.warning("Can't continue with onEntityDeath, because saga player for " + attackerPlayer.getName() + " isn't loaded.");
	    		return;
	    	}
			
		}
		
		// Player versus player
		if(sagaDead != null && sagaAttacker != null){
			
	    	// Statistics:
	    	StatisticsManager.manager().onPlayerKillPlayer(sagaAttacker, sagaDead, event);
			
			// Get saga chunk:
			Location location = attackerPlayer.getLocation();
			Chunk chunk = location.getWorld().getChunkAt(location);
			SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(chunk);
			if(sagaChunk == null){
				return;
			}
			
			// Send to chunk group:
			sagaChunk.onPlayerKillPlayer(sagaAttacker, sagaDead);
			
	    	// Send to guild manager:
	    	GuildsManager.manager().onKilledPlayer(sagaAttacker, sagaDead);
	    	
	    	// Experience:
	    	sagaAttacker.onPlayerExp(event, sagaDead);
	    	
	    	
		}
		
		// Player versus creature:
		else if(sagaAttacker != null && deadEntity instanceof Creature){
			
			// Experience:
	    	sagaAttacker.onCreatureExp(event, (Creature) deadEntity);
	    	
		}
		
		// Modify experience:
		if(sagaDead != null && event instanceof PlayerDeathEvent){
			
			PlayerDeathEvent cEvent = (PlayerDeathEvent) event;
			
			// Guardian rune:
			boolean runeAbsorb = false;
			if(sagaDead.getGuardianRune().isEnabled()){
				
				runeAbsorb = GuardianRune.handleAbsorb(sagaDead, cEvent);
				
			}
			
			// Experience regeneration:
			if(!runeAbsorb){

				// Calculate experience:
				int droppedExp = new Double(sagaDead.getTotalExperience() * BalanceConfiguration.config().experienceDrop).intValue();
				int remainExp = new Double(sagaDead.getTotalExperience() * BalanceConfiguration.config().experienceRemain).intValue();
				
				// Modify dropped experience:
				cEvent.setDroppedExp(droppedExp);
				
				// Add experience regeneration:
				sagaDead.addRegenExp(remainExp);
				
				
			}
			
			// Info exp regen:
			if(sagaDead.getExpRegen() >= 7){
				sagaDead.message(PlayerMessages.deathExpInfo());
			}
			
		}
		
		
	}
	
	
}
