/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.config.GeneralConfiguration;
import org.saga.dependencies.ChatDependency;
import org.saga.listeners.events.SagaBuildEvent;
import org.saga.listeners.events.SagaEventHandler;
import org.saga.player.GuardianRune;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Bundle;
import org.saga.settlements.BundleManager;
import org.saga.settlements.SagaChunk;

public class PlayerListener implements Listener {

	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

		if(GeneralConfiguration.isDisabled(event.getPlayer().getWorld())) return;
		
		SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(event.getPlayer().getName());
		if(sagaPlayer == null) return;


		String[] split = event.getMessage().split(" ");

		// Command override:
		if(GeneralConfiguration.config().checkOverride(split[0])) return;
		split[0] = GeneralConfiguration.config().getCommand(split[0]);
		
		// Get saga chunk:
		Location location = event.getPlayer().getLocation();
		SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(location);

		// Forward to chunk:
		if(sagaChunk != null) sagaChunk.onPlayerCommandPreprocess(sagaPlayer, event);
		
		if(event.isCancelled()) return;
		
		// Handle command:
		if (Saga.plugin().handleCommand(event.getPlayer(), split, event.getMessage())) {
			event.setCancelled(true);
		}

		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {

		
		Player player = event.getPlayer();

		SagaPlayer sagaPlayer = Saga.plugin().loadSagaPlayer(player.getName());
		
		if(sagaPlayer == null){
			SagaLogger.severe(PlayerListener.class, "can't continue with onPlayerJoin, because the saga player for "+ event.getPlayer().getName() + " isn't loaded");
			return;
		}

		// Set player:
		sagaPlayer.setPlayer(player);
		
		
		if(GeneralConfiguration.isDisabled(event.getPlayer().getWorld())) return;
		
		// Forward to chunk group:
		if(sagaPlayer.getBundle() != null) sagaPlayer.getBundle().onMemberJoin(event, sagaPlayer);
		
		
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {

		
		Player player = event.getPlayer();
		SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(player.getName());
		
		if(sagaPlayer == null){
			SagaLogger.severe(PlayerListener.class, "can't continue with onPlayerQuit, because the saga player for "+ event.getPlayer().getName() + " isn't loaded");
			return;
		}

		// Forward to bundle:
		if(sagaPlayer.getBundle() != null) sagaPlayer.getBundle().onMemberQuit(event, sagaPlayer);
		
		// Unload player:
		Saga.plugin().unloadSagaPlayer(player.getName());

		
		if(GeneralConfiguration.isDisabled(event.getPlayer().getWorld())) return;
		
		// Statistics:
		sagaPlayer.updateStatistics();
		
		// Remove player:
		sagaPlayer.removePlayer();
		
		
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		
		if(GeneralConfiguration.isDisabled(event.getPlayer().getWorld())) return;
		
		SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(event.getPlayer().getName());
		if(sagaPlayer == null) return;
		
		// Get chunk group:
		Bundle bundle = sagaPlayer.getBundle();
		
		// Forward to chunk group:
		if(bundle != null) bundle.onMemberRespawn(sagaPlayer, event);

		// Restore rune:
		if(!sagaPlayer.getGuardRune().isEmpty()){
			
			GuardianRune.handleRestore(sagaPlayer);
			
		}

		
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent event) {

		if(event.getFrom().getChunk() == event.getTo().getChunk()) return;
		if(GeneralConfiguration.isDisabled(event.getPlayer().getWorld())) return;
		
		SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(event.getPlayer().getName());
		if(sagaPlayer == null) return;

		
		// Handle chunk change:
		handleChunkChange(sagaPlayer, event);
		
		
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerTeleport(PlayerTeleportEvent event) {

		if(GeneralConfiguration.isDisabled(event.getPlayer().getWorld())) return;
		
		SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(event.getPlayer().getName());
		if(sagaPlayer == null) return;
		
		
		// Handle chunk change:
		handleChunkChange(sagaPlayer, event);
		

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {

		
		if(GeneralConfiguration.isDisabled(event.getPlayer().getWorld())) return;
		
		SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(event.getPlayer().getName());
		if(sagaPlayer == null) return;
		
		// Forward to ability manager:
		sagaPlayer.getAbilityManager().onInteract(event);
		
		if(event.isCancelled()) return;
		
		// Get Saga chunk:
		Location location = event.getClickedBlock() != null ? event.getClickedBlock().getLocation() : event.getPlayer().getLocation();
		SagaChunk sagaChunk = BundleManager.manager().getSagaChunk(location.getWorld().getChunkAt(location));
		
		// Build event:
		if(SagaBuildEvent.isBuildEvent(event)){

			SagaBuildEvent bldEvent = new SagaBuildEvent(event, sagaPlayer, sagaChunk);
			SagaEventHandler.handleBuild(bldEvent);
			if(bldEvent.isCancelled()) return;
			
		}

		// Forward to saga chunk:
		if(sagaChunk != null) sagaChunk.onPlayerInteract(event, sagaPlayer);
		
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChat(AsyncPlayerChatEvent event) {

		if(GeneralConfiguration.isDisabled(event.getPlayer().getWorld())) return;
		
		SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(event.getPlayer().getName());
		if(sagaPlayer == null) return;
		
		
		event.setFormat(ChatDependency.format(event.getFormat(), sagaPlayer));
		
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

		if(GeneralConfiguration.isDisabled(event.getPlayer().getWorld())) return;
		
		SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(event.getPlayer().getName());
		if(sagaPlayer == null) return;
		
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {

		SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(event.getPlayer().getName());
		if(sagaPlayer == null) return;
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerShearEntity(PlayerShearEntityEvent event) {

		if(GeneralConfiguration.isDisabled(event.getPlayer().getWorld())) return;
		
		SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(event.getPlayer().getName());
		if(sagaPlayer == null) return;
		
		// Forward to managers:
		sagaPlayer.getAbilityManager().onShear(event);

		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {

		if(GeneralConfiguration.isDisabled(event.getEntity().getWorld())) return;
		
		
		if(event.getEntity() instanceof Player){

			SagaPlayer sagaPlayer = Saga.plugin().getLoadedPlayer(((Player)event.getEntity()).getName());
			if(sagaPlayer == null) return;
			
			// Handle energy regeneration:
			sagaPlayer.handleEnergyRegen();
			
			// Forward to managers:
			sagaPlayer.getAbilityManager().onFoodLevelChange(event);

		}
		
		
	}
	
	
	private void handleChunkChange(SagaPlayer sagaPlayer, PlayerMoveEvent event){
		
		
		Location l1 = event.getFrom();
		Location l2 = event.getTo();
		
		int x1 = l1.getBlockX();
		int y1 = l1.getBlockY();
		int z1 = l1.getBlockZ();
		
		int x2 = l2.getBlockX();
		int y2 = l2.getBlockY();
		int z2 = l2.getBlockZ();
		
		// Coordinates didn't change much:
		if(x1 == x2 && y1 == y2 && z1 == z2) return;
		
		SagaChunk sagaChunk1 = sagaPlayer.lastSagaChunk;
		SagaChunk sagaChunk2 = BundleManager.manager().getSagaChunk(l2);
		
		// No chunk change:
		if(sagaChunk1 == sagaChunk2) return;
		
		sagaPlayer.lastSagaChunk = sagaChunk2;
		
		// Forward to chunk:
		if(sagaChunk2 != null) sagaChunk2.onPlayerEnter(sagaPlayer, sagaChunk1);
		if(sagaChunk1 != null) sagaChunk1.onPlayerLeave(sagaPlayer, sagaChunk2);
		
		
	}
	
	
}
