package org.saga.listeners;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Fireball;
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
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.saga.Saga;
import org.saga.config.GeneralConfiguration;
import org.saga.config.SettlementConfiguration;
import org.saga.config.VanillaConfiguration;
import org.saga.factions.Faction;
import org.saga.listeners.events.SagaDamageEvent;
import org.saga.listeners.events.SagaDeathEvent;
import org.saga.listeners.events.SagaEventHandler;
import org.saga.metadata.SpawnerTag;
import org.saga.player.GuardianRune;
import org.saga.player.SagaLiving;
import org.saga.player.SagaPlayer;
import org.saga.settlements.BundleManager;
import org.saga.settlements.SagaChunk;

public class EntityListener implements Listener{

	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		

		// Saga disabled:
		if(GeneralConfiguration.isDisabled(event.getEntity().getWorld())) return;
		
		
		// Creative or cancelled:
		if(event.getEntity() instanceof Player && ((Player)event.getEntity()).getGameMode() == GameMode.CREATIVE || event.isCancelled()) return;
		
		// Damage ticks:
		if(VanillaConfiguration.checkNoDamage(event.getCause(), event.getEntity())){
			event.setCancelled(true);
			return;
		}
		
		// Dead:
		if(event.getEntity().isDead()) return;

		// Saga damage event:
		SagaDamageEvent damageEvent = new SagaDamageEvent(event);
		SagaEventHandler.handleDamage(damageEvent);
		if(damageEvent.isCancelled()) return;
		
		// Forward to managers:
		SagaLiving sagaAttacker = damageEvent.sagaAttacker;
		SagaLiving sagaDefender = damageEvent.sagaDefender;
		if(sagaDefender != null){
			
			sagaDefender.getAttributeManager().handleDefend(damageEvent);
			sagaDefender.getAbilityManager().onDefend(damageEvent);
			
		}
		
		if(sagaAttacker != null){
			
			sagaAttacker.getAttributeManager().handleAttack(damageEvent);
			sagaAttacker.getAbilityManager().onAttack(damageEvent);
			
		}
		
		// Apply:
		damageEvent.apply();
		
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onProjectileHit(ProjectileHitEvent event) {
		
		
		// Saga disabled:
		if(GeneralConfiguration.isDisabled(event.getEntity().getWorld())) return;
		
		
		// Shot by player:
		Projectile projectile = event.getEntity();
		if((projectile.getShooter() instanceof Player)){
			
			Player player = (Player) projectile.getShooter();
			
	    	SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(player.getName());
	    	if(sagaPlayer == null) return;
	    	
	    	sagaPlayer.getAbilityManager().onProjectileHit(event);
			
		}
		
		
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplode(EntityExplodeEvent event) {
		
		
		// Saga disabled:
		if(GeneralConfiguration.isDisabled(event.getLocation().getWorld())) return;
		
		
		// Limit player fireball destruction:
		if(event.getEntity() instanceof Fireball && ((Fireball) event.getEntity()).getShooter() instanceof Player){
			
			int remaining = (int) (GeneralConfiguration.config().getPlayerFireballDestruction() * event.blockList().size());
			List<Block> blocks = event.blockList();
			while (remaining < blocks.size()){
				event.blockList().remove(blocks.size() - 1);
			}
			
		}
		
		// Stop creeper terrain damage.
		if(GeneralConfiguration.config().stopCreeperExplosions && event.getEntity() instanceof Creeper){
			event.blockList().clear();
		}
		
		// Stop explosion damage:
		if(SettlementConfiguration.config().getExplosionProtection()){
			List<Block> blocks = event.blockList();
			for (Iterator<Block> it = blocks.iterator(); it.hasNext();) {
				Block block = it.next();
				if(BundleManager.manager().getSagaChunk(block.getLocation()) != null) it.remove();
			}
		}
		
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityBlockForm(EntityBlockFormEvent event) {
		
		
		// Saga disabled:
		if(GeneralConfiguration.isDisabled(event.getEntity().getWorld())) return;
    	
		
		// Get saga chunk:
		SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(event.getBlock().getLocation());
		
		// Forward to saga chunk:
		if(sagaChunk != null) sagaChunk.onEntityBlockForm(event);
		
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		
		
		// Saga disabled:
		if(GeneralConfiguration.isDisabled(event.getEntity().getWorld())) return;
    	
		
    	// Unnatural tag:
		LivingEntity entity = event.getEntity();
		if(event.getSpawnReason() == SpawnReason.SPAWNER && !entity.hasMetadata(SpawnerTag.METADATA_KEY)){
    		entity.setMetadata(SpawnerTag.METADATA_KEY, SpawnerTag.METADATA_VALUE);
    	}
		
		// Get Saga chunk:
		SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(event.getLocation());
		
		// Forward to Saga chunk:
		if(sagaChunk != null) sagaChunk.onCreatureSpawn(event);
		
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent event) {


		// Saga disabled:
		if(GeneralConfiguration.isDisabled(event.getEntity().getWorld())) return;
    	
		
		// Death event:
		SagaDeathEvent deathEvent = new SagaDeathEvent(event);
		
		SagaLiving sagaDefender = null;
		SagaLiving sagaAttacker = null;
		Creature deadCreature = null;
		
		if(deathEvent.getLastDamageEvent() != null){
			sagaDefender = deathEvent.getLastDamageEvent().sagaDefender;
			sagaAttacker = deathEvent.getLastDamageEvent().sagaAttacker;
			deadCreature =  deathEvent.getLastDamageEvent().creatureDefender;
		}
		
		// Player got killed by a player:
		if(sagaDefender instanceof SagaPlayer && sagaAttacker instanceof SagaPlayer){
			
			SagaPlayer attackerPlayer = (SagaPlayer) sagaAttacker;
			SagaPlayer defenderPlayer = (SagaPlayer) sagaDefender;
			
			// Get saga chunk:
			Location location = sagaAttacker.getLocation();
			Chunk chunk = location.getWorld().getChunkAt(location);
			SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(chunk);
			
			// Forward to chunk:
			if(sagaChunk != null) sagaChunk.onPvpKill(attackerPlayer, defenderPlayer);
			
			// Forward to faction:
			Faction attackerFaction = attackerPlayer.getFaction();
			Faction deadFaction = defenderPlayer.getFaction();
			if(attackerFaction != null) attackerFaction.onPvpKill(attackerPlayer, defenderPlayer);
			if(deadFaction != null && deadFaction != attackerFaction) deadFaction.onPvpKill(attackerPlayer, defenderPlayer);
			
			
		}
		
		// Creature got killed by a player:
		else if(sagaAttacker != null && deadCreature != null){
			
		}
		
		// Player got killed:
		if(sagaDefender instanceof SagaPlayer){
			
			SagaPlayer defenderPlayer = (SagaPlayer) sagaDefender;
			
			// Guardian rune:
			GuardianRune rune = defenderPlayer.getGuardRune();
			if(rune.isEnabled()) GuardianRune.handleAbsorb(defenderPlayer, event);
			
		}
		
		// Apply event:
		deathEvent.apply();
		
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityTarget(EntityTargetEvent event) {
		
		
		// Saga disabled:
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
