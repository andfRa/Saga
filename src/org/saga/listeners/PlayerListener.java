/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.ChunkGroupManager;
import org.saga.chunkGroups.SagaChunk;
import org.saga.config.FactionConfiguration;
import org.saga.factions.SagaFaction;
import org.saga.listeners.events.SagaBuildEvent;
import org.saga.listeners.events.SagaEventHandler;
import org.saga.player.GuardianRune;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;

public class PlayerListener implements Listener {

	
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

    	
    	SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(event.getPlayer().getName());
    	
    	// Invalid player:
    	if(sagaPlayer == null){
    		SagaLogger.severe(BlockListener.class, "can't continue with onPlayerCommandPreprocess, because the saga player for "+ event.getPlayer().getName() + " isn't loaded");
    		return;
    	}

    	// Get saga chunk:
    	Location location = event.getPlayer().getLocation();
    	SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(location);

    	// Forward to chunk:
    	if(sagaChunk != null) sagaChunk.onPlayerCommandPreprocess(sagaPlayer, event);
    	
    	if(event.isCancelled()) return;
    	
        String[] split = event.getMessage().split(" ");

        if (Saga.plugin().handleCommand(event.getPlayer(), split, event.getMessage())) {
            event.setCancelled(true);
        }

    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {

    	
    	Player player = event.getPlayer();

    	SagaPlayer sagaPlayer = Saga.plugin().loadSagaPlayer(player.getName());
    	
    	// Invalid player:
    	if(sagaPlayer == null){
    		SagaLogger.severe(BlockListener.class, "can't continue with onPlayerJoin, because the saga player for "+ event.getPlayer().getName() + " isn't loaded");
    		return;
    	}

    	// Set player:
    	sagaPlayer.setPlayer(player);
    	
        
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {


    	Player player = event.getPlayer();

    	// Forward to Saga:
    	SagaPlayer sagaPlayer = Saga.plugin().unloadSagaPlayer(player.getName());
    	
    	// Invalid player:
    	if(sagaPlayer == null){
    		SagaLogger.severe(BlockListener.class, "can't continue with onPlayerQuit, because the saga player for "+ event.getPlayer().getName() + " isn't loaded");
    		return;
    	}

		// Statistics:
    	StatisticsManager.manager().setLevel(sagaPlayer);
    	StatisticsManager.manager().setAttributes(sagaPlayer);
    	
    	// Remove player:
    	sagaPlayer.removePlayer();
    	
        
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	
    	
    	SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(event.getPlayer().getName());
    	
    	// Invalid player:
    	if(sagaPlayer == null){
    		SagaLogger.severe(BlockListener.class, "can't continue with onPlayerRespawn, because the saga player for "+ event.getPlayer().getName() + " isn't loaded");
    		return;
    	}

    	// Get chunk group:
    	ChunkGroup chunkGroup = sagaPlayer.getChunkGroup();
    	
    	// Forward to chunk group:
    	if(chunkGroup != null) chunkGroup.onMemberRespawn(sagaPlayer, event);

    	// Restore rune:
		if(!sagaPlayer.getGuardRune().isEmpty()){
			
			GuardianRune.handleRestore(sagaPlayer);
			
		}

		
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {

    	
    	SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(event.getPlayer().getName());
    	
    	// Invalid player:
    	if(sagaPlayer == null){
    		SagaLogger.severe(BlockListener.class, "can't continue with onPlayerMove, because the saga player for "+ event.getPlayer().getName() + " isn't loaded");
    		return;
    	}
    	
    	// Handle chunk change:
    	handleChunkChange(sagaPlayer, event);
    	
        
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
    	

    	SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(event.getPlayer().getName());
    	
    	// Invalid player:
    	if(sagaPlayer == null){
    		SagaLogger.severe(BlockListener.class, "can't continue with onPlayerTeleport, because the saga player for "+ event.getPlayer().getName() + " isn't loaded");
    		return;
    	}
    	
    	// Handle chunk change:
    	handleChunkChange(sagaPlayer, event);
    	

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {

    	
    	SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(event.getPlayer().getName());
    	
    	// Bukkit bug workaround: 
    	if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_AIR)){
    		event.setCancelled(false);
    	}

    	// Get Saga chunk:
    	Location location = null;
    	if(event.getClickedBlock() != null){
    		location = event.getClickedBlock().getLocation();
    	}else{
    		location = event.getPlayer().getLocation();
    	}
    	SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(location.getWorld().getChunkAt(location));
    	
    	// Invalid player:
    	if(sagaPlayer == null){
    		
    		SagaLogger.severe(BlockListener.class, "can't continue with onPlayerInteract, because the saga player for "+ event.getPlayer().getName() + " isn't loaded");
    		event.setCancelled(true);
    		
    		return;
    		
    	}
    	
    	// Build event:
    	if(isBuildEvent(event)){

        	SagaBuildEvent eventB = new SagaBuildEvent(event, sagaPlayer, sagaChunk);
        	SagaEventHandler.onBuild(eventB);
        	if(eventB.isCancelled()) return;
        	
    	}

    	// Forward to saga chunk:
    	if(sagaChunk != null) sagaChunk.onPlayerInteract(event, sagaPlayer);
    	
    	if(event.isCancelled()) return;
    	
    	// Forward to managers:
    	sagaPlayer.getAbilityManager().onInteract(event);
    	
    	
	}
	
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(PlayerChatEvent event) {

    	
    	SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(event.getPlayer().getName());
    	
    	// Invalid player:
    	if(sagaPlayer == null){
    		SagaLogger.severe(BlockListener.class, "can't continue with onPlayerChat, because the saga player for "+ event.getPlayer().getName() + " isn't loaded");
    		return;
    	}
    	
    	// No faction or not formed yet:
    	SagaFaction sagaFaction = sagaPlayer.getFaction();
    	if(sagaFaction == null || !sagaFaction.isFormed()) return;
    	
    	ChatColor primaryColor = sagaFaction.getPrimaryColor();
    	ChatColor secondaryColor = sagaFaction.getSecondaryColor();
    	ChatColor normalColor = ChatColor.WHITE;
    	
    	String formatString = event.getFormat();
    	
    	formatString = formatString.replace("<", "<" + primaryColor + sagaFaction.getName() + FactionConfiguration.config().prefixNameSeparator + secondaryColor);
    	formatString = formatString.replace(">", normalColor.toString() + ">");
    	
    	try {
    		event.setFormat(formatString);
		} catch (Exception e) {
			SagaLogger.severe(PlayerListener.class, "onPlayerChat failed to set format: " +e.getClass().getSimpleName() + ":" + e.getMessage());
		}
    	
    	
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

    	
    	SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(event.getPlayer().getName());
    	
    	// Invalid player:
    	if(sagaPlayer == null){
    		SagaLogger.severe(BlockListener.class, "can't continue with onPlayerInteractEntity, because the saga player for "+ event.getPlayer().getName() + " isn't loaded");
    		return;
    	}
    	
    	
    	
    }
    
    
    private void handleChunkChange(SagaPlayer sagaPlayer, PlayerMoveEvent event){
    	
    	
    	Location l1 = event.getFrom();
    	Location l2 = event.getTo();
    	
    	int x1 = (int)l1.getX();
    	int y1 = (int)l1.getY();
    	int z1 = (int)l1.getZ();
    	
    	int x2 = (int)l2.getX();
    	int y2 = (int)l2.getY();
    	int z2 = (int)l2.getZ();
    	
    	// Coordinates didn't change much:
    	if(x1 == x2 && y1 == y2 && z1 == z2) return;
    	
    	SagaChunk sagaChunk1 = sagaPlayer.lastSagaChunk;
    	SagaChunk sagaChunk2 = ChunkGroupManager.manager().getSagaChunk(l2);
    	
    	// No chunk change:
    	if(sagaChunk1 == sagaChunk2) return;
    	
    	sagaPlayer.lastSagaChunk = sagaChunk2;
    	
    	// Forward to chunk:
    	if(sagaChunk2 != null) sagaChunk2.onPlayerEnter(sagaPlayer, sagaChunk1);
    	if(sagaChunk1 != null) sagaChunk1.onPlayerLeave(sagaPlayer, sagaChunk2);
    	
    	
    }
    
    private boolean isBuildEvent(PlayerInteractEvent event) {

		
		ItemStack item = event.getPlayer().getItemInHand();
		Block block = event.getClickedBlock();
		
		switch (item.getType()) {
			
			case LAVA_BUCKET:
				
				return true;
			
			case FLINT_AND_STEEL:
				
				return true;
				
			case FIREBALL:
				
				return true;

			case WATER_BUCKET:
				
				return true;
				
			case BUCKET:

				return true;
				
			case INK_SACK:
				
				if(item.getData().getData() != 15) break;
				return true;
				
			case PAINTING:
	
				return true;

			default:
				break;
			
		}
		
		// Fire:
		if(block != null && block.getRelative(BlockFace.UP) != null && block.getRelative(BlockFace.UP).getType() == Material.FIRE){
			return true;
		}
		
		// Trample:
		if(event.getAction() == Action.PHYSICAL && block != null && block.getType() == Material.SOIL){
			return true;
		}
		
		return false;
		
		
	}
    
}
