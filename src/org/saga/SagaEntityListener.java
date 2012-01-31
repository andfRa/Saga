package org.saga;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEnderDragon;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EndermanPickupEvent;
import org.bukkit.event.entity.EndermanPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.chunkGroups.ChunkGroupManager;
import org.saga.chunkGroups.SagaChunk;
import org.saga.config.BalanceConfiguration;
import org.saga.guilds.GuildsManager;
import org.saga.player.GuardianRune;
import org.saga.player.PlayerMessages;
import org.saga.player.SagaEntityDamageManager;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;

public class SagaEntityListener extends EntityListener{

	
	/**
	 * Constructs the listener.
	 * 
	 * @param pMainPlugin Main plugin.
	 */
	public SagaEntityListener() {

	
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.event.entity.EntityListener#onEntityDamage(org.bukkit.event.entity.EntityDamageEvent)
	 */
	public void onEntityDamage(EntityDamageEvent event) {
		
		
		
		// Damaged by entity:
		if(event instanceof EntityDamageByEntityEvent){
			onEntityDamageByEntity((EntityDamageByEntityEvent)event);
		}else{
		}
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.event.entity.EntityListener#onEntityDamage(org.bukkit.event.entity.EntityDamageEvent)
	 */
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		
		
		SagaPlayer sagaAttacker = null;
		SagaPlayer sagaDefender = null;
		
		// Not a living:
		if(!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity defender= (LivingEntity) event.getEntity();
		
		// Damage ticks:
		if(defender.getNoDamageTicks() > defender.getMaximumNoDamageTicks()/2F) return;
		
		// Dead:
		if(defender.getHealth() <= 0) return;
		
		// Damaged by another entity:
		EntityDamageByEntityEvent cEvent = (EntityDamageByEntityEvent)event;
		Projectile projectile = null;
		Entity attacker = cEvent.getDamager();
		
		if(event.getEntity() instanceof CraftEnderDragon && cEvent.getDamager() instanceof CraftEnderDragon){
			((LivingEntity)event.getEntity()).remove();
			Saga.severe(getClass(), "dragon-dragon collision", "removed dragon");
		}
		
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
				sagaAttacker.getLevelManager().onHitPlayer(cEvent, sagaDefender);
				sagaDefender.getLevelManager().onHitByPlayer(cEvent, sagaAttacker);

				// Handle pvp:
				SagaEntityDamageManager.handlePvp(sagaAttacker, sagaDefender, cEvent);
				
			}
			// Archery:
			else if(projectile instanceof Arrow){
				
				sagaAttacker.getLevelManager().onShotPlayer(cEvent, sagaDefender, projectile);
				sagaDefender.getLevelManager().onShotByPlayer(cEvent, sagaAttacker, projectile);

				// Handle pvp:
				SagaEntityDamageManager.handlePvp(sagaAttacker, sagaDefender, cEvent);
			
			}
			// Magic:
			else if(projectile instanceof Fireball){
				
				// Set base damage:
				cEvent.setDamage(BalanceConfiguration.config().pvpBaseFireballDamage);
				
				sagaAttacker.getLevelManager().onSpelledPlayer(cEvent, sagaDefender, projectile);
				sagaDefender.getLevelManager().onSpelledByPlayer(cEvent, sagaAttacker, projectile);

				// Handle pvp:
				SagaEntityDamageManager.handlePvp(sagaAttacker, sagaDefender, cEvent);
				
				// Handle magic:
				SagaEntityDamageManager.handleMagicDamage(cEvent, sagaAttacker, sagaDefender);
				
			}
			
			
		}
		
		// Player versus creature:
		else if(defender instanceof Creature && sagaAttacker != null){
			
			// Close combat:
			if(projectile == null){
				sagaAttacker.getLevelManager().onHitCreature(cEvent, (Creature)defender);
			}
			// Archery:
			else if(projectile instanceof Arrow){
				sagaAttacker.getLevelManager().onShotCreature(cEvent, (Creature)defender, projectile);
			}
			// Magic:
			else if(projectile instanceof Fireball){
				
				// Set base damage:
				cEvent.setDamage(BalanceConfiguration.config().pvcBaseFireballDamage);
				
				sagaAttacker.getLevelManager().onSpelledCreature(cEvent, (Creature)defender, projectile);
				
				// Handle magic:
				SagaEntityDamageManager.handleMagicDamage(cEvent, sagaAttacker, (Creature)defender);
				
			}
			
			// Get saga chunk:
			SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(defender.getLocation());
			if(sagaChunk == null){
				return;
			}
			
			// Forward to saga chunk group:
			sagaChunk.onPlayerDamagedCreature(cEvent, sagaAttacker, (Creature)defender);
			
			
		}
		
		// Defend from a creature:
		else if(attacker instanceof Creature && sagaDefender != null){

			// Close combat:
			if(projectile == null){
				sagaDefender.getLevelManager().onHitByCreature(cEvent, (Creature)attacker);
			}
			// Archery:
			else{
				sagaDefender.getLevelManager().onShotByCreature(cEvent, (Creature)attacker, projectile);
			}
			
		}
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.event.entity.EntityListener#onProjectileHit(org.bukkit.event.entity.ProjectileHitEvent)
	 */
	@Override
	public void onProjectileHit(ProjectileHitEvent event) {
		
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.event.entity.EntityListener#onEntityExplode(org.bukkit.event.entity.EntityExplodeEvent)
	 */
	@Override
	public void onEntityExplode(EntityExplodeEvent event) {
		
		
		// Stop creeper land damage.
		if(BalanceConfiguration.config().stopCreeperExplosions && event.getEntity() instanceof Creeper){
			event.blockList().clear();
		}
		
		// Get saga chunk:
		Location location = event.getLocation();
		Chunk chunk = location.getWorld().getChunkAt(location);
		SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(chunk);
		if(sagaChunk == null){
			return;
		}
		
		// Forward to saga chunk:
		sagaChunk.onEntityExplode(event);
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.event.entity.EntityListener#onEndermanPickup(org.bukkit.event.entity.EndermanPickupEvent)
	 */
	@Override
	public void onEndermanPickup(EndermanPickupEvent event) {
		

		// Get saga chunk:
		Location location = event.getBlock().getLocation();
		Chunk chunk = location.getWorld().getChunkAt(location);
		SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(chunk);
		if(sagaChunk == null){
			return;
		}
		
		// Forward to saga chunk group:
		sagaChunk.getChunkGroup().onEndermanPickup(event, sagaChunk);
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.event.entity.EntityListener#onEndermanPlace(org.bukkit.event.entity.EndermanPlaceEvent)
	 */
	@Override
	public void onEndermanPlace(EndermanPlaceEvent event) {


		// Get saga chunk:
		Location location = event.getLocation();
		Chunk chunk = location.getWorld().getChunkAt(location);
		SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(chunk);
		if(sagaChunk == null){
			return;
		}
		
		// Forward to saga chunk group:
		sagaChunk.getChunkGroup().onEndermanPlace(event, sagaChunk);
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.event.entity.EntityListener#onCreatureSpawn(org.bukkit.event.entity.CreatureSpawnEvent)
	 */
	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		
		
		// Get saga chunk:
		Location location = event.getLocation();
		Chunk chunk = location.getWorld().getChunkAt(location);
		SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(chunk);
		if(sagaChunk == null){
			return;
		}
		
		// Forward to saga chunk:
		sagaChunk.onCreatureSpawn(event);
		
		
	}
	
	@Override
	public void onEntityDeath(EntityDeathEvent event) {

		
		Entity deadEntity = event.getEntity();
		Entity killerEntity = null;
		
		Player deadPlayer = null;
		Player attackerPlayer = null;
		
		SagaPlayer sagaDead = null;
		SagaPlayer sagaAttacker = null;
		
		if(deadEntity instanceof Player){
			
			deadPlayer = (Player) deadEntity;

			sagaDead = Saga.plugin().getSagaPlayer( deadPlayer.getName() );
			
			// No player:
	    	if(sagaDead == null){
	    		Saga.warning("Can't continue with onEntityDeath, because saga player for " + deadPlayer.getName() + " isn't loaded.");
	    		return;
	    	}
			
			EntityDamageEvent damageEvent = deadPlayer.getLastDamageCause();
			
			if(damageEvent instanceof EntityDamageByEntityEvent){
				killerEntity = ((EntityDamageByEntityEvent) damageEvent).getDamager();
			}
			
			if(killerEntity instanceof Player){
				attackerPlayer = (Player) killerEntity;
				
				sagaAttacker = Saga.plugin().getSagaPlayer( attackerPlayer.getName() );

				// No player:
		    	if(sagaAttacker == null){
		    		Saga.warning("Can't continue with onEntityDeath, because saga player for " + attackerPlayer.getName() + " isn't loaded.");
		    		return;
		    	}
				
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
				
				// TODO Workaround for bukkit not doping correct experience:
				if(sagaDead.getLevel() > 0){
					droppedExp = 1;
				}else{
					droppedExp = 0;
				}

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
