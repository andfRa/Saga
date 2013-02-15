package org.saga.listeners;

import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.saga.Saga;
import org.saga.config.GeneralConfiguration;
import org.saga.config.VanillaConfiguration;
import org.saga.factions.Faction;
import org.saga.listeners.events.SagaEntityDamageEvent;
import org.saga.listeners.events.SagaEntityDeathEvent;
import org.saga.listeners.events.SagaEventHandler;
import org.saga.metadata.SpawnerTag;
import org.saga.player.GuardianRune;
import org.saga.player.SagaPlayer;
import org.saga.settlements.BundleManager;
import org.saga.settlements.SagaChunk;

public class EntityListener implements Listener{

	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		
		// Creative or cancelled:
		if(event.getEntity() instanceof Player && ((Player)event.getEntity()).getGameMode() == GameMode.CREATIVE || event.isCancelled()) return;
		
		// Not a living:
		if(!(event.getEntity() instanceof LivingEntity)) return;
		LivingEntity defender= (LivingEntity) event.getEntity();
		
		// Damage ticks:
		if(VanillaConfiguration.hasTicks(event.getCause()) && defender.getNoDamageTicks() > defender.getMaximumNoDamageTicks()/2F){
			event.setCancelled(true);
			return;
		}
		
		if(GeneralConfiguration.isDisabled(event.getEntity().getWorld())) return;
    	
		
		// Dead:
		if(defender.getHealth() <= 0) return;

		// Saga event:
		SagaEntityDamageEvent damageEvent = new SagaEntityDamageEvent(event, defender);
		SagaEventHandler.handleEntityDamage(damageEvent);
		if(damageEvent.isCancelled()) return;
		
		// Forward to managers:
		SagaPlayer attackerPlayer = damageEvent.attackerPlayer;
		SagaPlayer defenderPlayer = damageEvent.defenderPlayer;
		if(defenderPlayer != null){
			
			defenderPlayer.getAttributeManager().handleDefend(damageEvent);
			defenderPlayer.getAbilityManager().onDefend(damageEvent);
			
		}
		
		if(attackerPlayer != null){
			
			attackerPlayer.getAttributeManager().handleAttack(damageEvent);
			attackerPlayer.getAbilityManager().onAttack(damageEvent);
			
		}
		
		// Apply:
		damageEvent.apply();
		
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onProjectileHit(ProjectileHitEvent event) {
		
		if(GeneralConfiguration.isDisabled(event.getEntity().getWorld())) return;
    	
		
		if(!(event.getEntity() instanceof Projectile)) return;
		Projectile projectile = (Projectile) event.getEntity();
	
		// Shot by player:
		if((projectile.getShooter() instanceof Player)){
			
			Player player = (Player) projectile.getShooter();
			
	    	SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(player.getName());
	    	if(sagaPlayer == null) return;
	    	
	    	sagaPlayer.getAbilityManager().onProjectileHit(event);
			
		}
		
		
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplode(EntityExplodeEvent event) {
		
		if(GeneralConfiguration.isDisabled(event.getLocation().getWorld())) return;
    	
		
		// Stop creeper terrain damage.
		if(GeneralConfiguration.config().stopCreeperExplosions && event.getEntity() instanceof Creeper){
			event.blockList().clear();
		}
		
		// Get saga chunk:
		SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(event.getLocation());
		
		// Forward to saga chunk:
		if(sagaChunk != null) sagaChunk.onEntityExplode(event);
		
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityBlockForm(EntityBlockFormEvent event) {
		
		if(GeneralConfiguration.isDisabled(event.getEntity().getWorld())) return;
    	
		
		// Get saga chunk:
		SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(event.getBlock().getLocation());
		
		// Forward to saga chunk:
		if(sagaChunk != null) sagaChunk.onEntityBlockForm(event);
		
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		
		if(GeneralConfiguration.isDisabled(event.getEntity().getWorld())) return;
    	
		
		LivingEntity entity = event.getEntity();
		
    	// Unnatural tag:
    	if(event.getSpawnReason() == SpawnReason.SPAWNER && !entity.hasMetadata(SpawnerTag.METADATA_KEY)){
    		entity.setMetadata(SpawnerTag.METADATA_KEY, SpawnerTag.METADATA_VALUE);
    	}
		
		// Get saga chunk:
		SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(event.getLocation());
		
		// Forward to saga chunk:
		if(sagaChunk != null) sagaChunk.onCreatureSpawn(event);
		
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent event) {

		if(GeneralConfiguration.isDisabled(event.getEntity().getWorld())) return;
    	
		
		SagaEntityDeathEvent sagaEvent = new SagaEntityDeathEvent(event, event.getEntity());
		
		SagaPlayer sagaDead = null;
		SagaPlayer sagaAttacker = null;
		Creature deadCreature = null;
		
		if(sagaEvent.getLastDamageEvent() != null){
			sagaDead = sagaEvent.getLastDamageEvent().defenderPlayer;
			sagaAttacker = sagaEvent.getLastDamageEvent().attackerPlayer;
			deadCreature =  sagaEvent.getLastDamageEvent().defenderCreature;
		}
		
		
		// Player got killed by a player:
		if(sagaDead != null && sagaAttacker != null){
			
			// Get saga chunk:
			Location location = sagaAttacker.getLocation();
			Chunk chunk = location.getWorld().getChunkAt(location);
			SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(chunk);
			
			// Forward to chunk:
			if(sagaChunk != null) sagaChunk.onPvpKill(sagaAttacker, sagaDead);
			
			// Forward to faction:
			Faction attackerFaction = sagaAttacker.getFaction();
			Faction deadFaction = sagaDead.getFaction();
			if(attackerFaction != null) attackerFaction.onPvpKill(sagaAttacker, sagaDead);
			if(deadFaction != null && deadFaction != attackerFaction) deadFaction.onPvpKill(sagaAttacker, sagaDead);
			
			
		}
		
		// Creature got killed by a player:
		else if(sagaAttacker != null && deadCreature != null){
			
		}
		
		// Player got killed:
		if(sagaDead != null){
			
			// Guardian rune:
			GuardianRune rune = sagaDead.getGuardRune();
			if(rune.isEnabled()) GuardianRune.handleAbsorb(sagaDead, event);
			
		}
		
		// Apply event:
		sagaEvent.apply();
		
		
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {

		if(GeneralConfiguration.isDisabled(event.getEntity().getWorld())) return;
    	
		
		// Players:
		if(event.getEntity() instanceof Player){

			// Get player:
			Player player = (Player) event.getEntity();
	    	final SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(player.getName());
	    	if(sagaPlayer == null) return;

			// Synchronise:
			sagaPlayer.synchHealth();
	    	
	    	int prevHearths = sagaPlayer.getHalfHearts();
	    	
	    	// Heal:
	    	sagaPlayer.heal((double)event.getAmount());
			
	    	int nextHearths = sagaPlayer.getHalfHearts();
	    	
	    	event.setAmount(nextHearths - prevHearths);
	    	
		}
		
		
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityTarget(EntityTargetEvent event) {

		if(GeneralConfiguration.isDisabled(event.getEntity().getWorld())) return;
    	
		
		// Target Player:
		if(event.getTarget() instanceof Player){

			// Get player:
			Player player = (Player) event.getTarget();
	    	SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(player.getName());
	    	if(sagaPlayer == null) return;

	    	// Forward to managers:
	    	sagaPlayer.getAbilityManager().onTargeted(event);
	    	
		}
		
		
	}
	
	

}
