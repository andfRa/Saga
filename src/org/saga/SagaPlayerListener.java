/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
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
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.saga.chunkGroups.ChunkGroup;
import org.saga.chunkGroups.ChunkGroupManager;
import org.saga.chunkGroups.SagaChunk;
import org.saga.config.FactionConfiguration;
import org.saga.factions.SagaFaction;
import org.saga.player.SagaPlayer;

/**
 *
 * @author Cory
 */
public class SagaPlayerListener implements Listener {

    protected Saga plugin;

    private HashSet<String> bigBoots = new HashSet<String>();
    
    
    public SagaPlayerListener(Saga plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

    	
    	// Get player:
    	SagaPlayer sagaPlayer = plugin.getSagaPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null){
    		Saga.warning("Can't continue with onPlayerCommandPreprocess, because the saga player for "+ event.getPlayer().getName() + " isn't loaded.");
    		return;
    	}

    	// Get saga chunk:
    	Location location = event.getPlayer().getLocation();
    	SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(location);

    	// Forward to chunk:
    	if(sagaChunk != null) sagaChunk.onPlayerCommandPreprocess(sagaPlayer, event);
    	
    	if(event.isCancelled()) return;
    	
        String[] split = event.getMessage().split(" ");

        if (plugin.handleCommand(event.getPlayer(), split, event.getMessage())) {
            event.setCancelled(true);
        }

    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {

    	
    	Player player = event.getPlayer();

    	// Load saga player:
    	SagaPlayer sagaPlayer = Saga.plugin().loadSagaPlayer(player.getName());
    	
    	// No player:
    	if(sagaPlayer == null){
    		Saga.warning("Can't continue with onPlayerJoin, because the saga player for "+ event.getPlayer().getName() + " isn't loaded.");
    		return;
    	}

    	// Set player:
    	sagaPlayer.setPlayer(player);
    	
        
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {


    	Player player = event.getPlayer();

    	// Unload saga player:
    	SagaPlayer sagaPlayer = Saga.plugin().unloadSagaPlayer(player.getName());
    	
    	// No player:
    	if(sagaPlayer == null){
    		Saga.warning("Can't continue with onPlayerQuit, because the saga player for "+ event.getPlayer().getName() + " isn't loaded.");
    		return;
    	}

    	// Send event:
    	sagaPlayer.playerQuitEvent(event);
    	
    	// Remove player:
    	sagaPlayer.removePlayer();
    	
        
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	

    	SagaPlayer sagaPlayer = plugin.getSagaPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null){
    		Saga.warning("Can't continue with onPlayerRespawn, because the saga player for "+ event.getPlayer().getName() + " isn't loaded.");
    		return;
    	}

    	// Get chunk group:
    	ChunkGroup chunkGroup = sagaPlayer.getRegisteredChunkGroup();
    	if(chunkGroup != null){
    		chunkGroup.onMemberRespawn(sagaPlayer, event);
    	}
//    	
//    	// Guardian rune:
//    	GuardianRune.handleRestore(sagaPlayer, event);

    	
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {

    	
    	SagaPlayer sagaPlayer = plugin.getSagaPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null){
    		Saga.warning("Can't continue with onPlayerMove, because the saga player for "+ event.getPlayer().getName() + " isn't loaded.");
    		return;
    	}
    	
    	// Handle chunk change:
    	handleChunkChange(sagaPlayer, event);
    	
        
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
    	
    	
//    	if(event.getTo().getWorld().getName().equals("world_the_end") || event.getTo().getWorld().getName().equals("world_skylands")){
//    		event.setCancelled(true);
//    		event.getPlayer().sendMessage(ChatColor.DARK_RED  + "skylands is not accessible until bukkit is fully updated!");
//    		Saga.warning(event.getPlayer().getName() + " tried to teleport to world_the_end");
//    		event.getPlayer().teleport(Saga.plugin().getServer().getWorld("world").getSpawnLocation());
//    		return;
//    	}
    	
    	SagaPlayer sagaPlayer = plugin.getSagaPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null){
    		Saga.warning("Can't continue with onPlayerTeleport, because the saga player for "+ event.getPlayer().getName() + " isn't loaded.");
    		return;
    	}
    	
    	// Handle chunk change:
    	handleChunkChange(sagaPlayer, event);
    	

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {

    	
    	// Bukkit bug workaround: 
    	if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_AIR)){
    		event.setCancelled(false);
    	}

    	// Get saga chunk:
    	Block clickedBlock = event.getClickedBlock();
    	Location location = event.getPlayer().getLocation();
    	if(clickedBlock != null){
    		location = clickedBlock.getLocation();
    	}
    	SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(location.getWorld().getChunkAt(location));

		// Get player:
    	SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null){
    		Saga.warning("Can't continue with onPlayerInteract, because the saga player for "+ event.getPlayer().getName() + " isn't loaded.");
    		if(sagaChunk != null){
    			Saga.info("Found saga chunk. Canceling event.");
    			event.setCancelled(true);
    		}
    		return;
    	}
    	
    	// Forward to saga chunk:
    	if(sagaChunk != null) sagaChunk.onPlayerInteract(event, sagaPlayer);
    	
    	// Check if canceled:
    	if(event.isCancelled()){
    		return;
    	}
    	
    	// Forward to level manager:
    	sagaPlayer.getLevelManager().onPlayerInteract(event);
    	
    	
	
	}
	
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(PlayerChatEvent event) {


		// Get player:
    	SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null){
    		Saga.warning("Can't continue with onPlayerChat, because the saga player for "+ event.getPlayer().getName() + " isn't loaded.");
    		return;
    	}
    	
    	// No faction or not formed yet:
    	SagaFaction sagaFaction = sagaPlayer.getRegisteredFaction();
    	if(sagaFaction == null || !sagaFaction.isFormed()){
    		return;
    	}
    	
    	ChatColor primaryColor = sagaFaction.getPrimaryColor();
    	ChatColor secondaryColor = sagaFaction.getSecondaryColor();
    	ChatColor normalColor = ChatColor.WHITE;
    	
    	String formatString = event.getFormat();
    	
    	formatString = formatString.replace("<", "<" + primaryColor + sagaFaction.getName() + FactionConfiguration.config().prefixNameSeparator + secondaryColor);
    	formatString = formatString.replace(">", normalColor.toString() + ">");
    	
    	try {
    		event.setFormat(formatString);
		} catch (Exception e) {
			Saga.severe("onPlayerChat failed to set format: " +e.getClass().getSimpleName() + ":" + e.getMessage());
		}
    	
    	
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {

    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {


		// Get player:
    	SagaPlayer sagaPlayer = Saga.plugin().getSagaPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null){
    		Saga.warning("Can't continue with onPlayerInteractEntity, because the saga player for "+ event.getPlayer().getName() + " isn't loaded.");
    		return;
    	}
    	
    	// Target is a player:
    	if(event.getRightClicked() instanceof Player){
    		
    		SagaPlayer targetSagaPlayer = Saga.plugin().getSagaPlayer(((Player) event.getRightClicked()).getName());
        	if(targetSagaPlayer == null){
        		Saga.warning("Can't continue with onPlayerInteractEntity, because the target saga player for "+ event.getPlayer().getName() + " isn't loaded.");
        		return;
        	}

        	// Forward to level manager:
        	sagaPlayer.getLevelManager().onPlayerInteractPlayer(event, targetSagaPlayer);
        	
    	}else if(event.getRightClicked() instanceof Creature){
    		
        	// Forward to level manager:
        	sagaPlayer.getLevelManager().onPlayerInteractCreature(event, (Creature) event.getRightClicked());
    		
    	}
    	
    	// Get saga chunk:
    	Location location = event.getRightClicked().getLocation();
    	SagaChunk sagaChunk = ChunkGroupManager.manager().getSagaChunk(location.getWorld().getChunkAt(location));
    	
    	// Forward to chunk group:
    	if(sagaChunk != null){
    		sagaChunk.getChunkGroup().onPlayerInteractEntity(event, sagaPlayer, sagaChunk);
    	}
    	
    	
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerPortal(PlayerPortalEvent event) {
    	
    }
    

    
    public void enableBigBoot(String playerName) {
    	bigBoots.add(playerName);
	}
    
    public void disableBigBoot(String playerName) {
    	bigBoots.remove(playerName);
	}
    
    public boolean isBigBoot(String playerName) {
    	return bigBoots.contains(playerName);
	}
    
    /**
     * Handles a chunk change.
     * 
     * @param sagaPlayer saga player
     * @param event event
     */
    private void handleChunkChange(SagaPlayer sagaPlayer, PlayerMoveEvent event){
    	

    	SagaChunk previousSagaChunk = sagaPlayer.getLastSagaChunk();
    	Location previousLocation = sagaPlayer.getLastLocation();
    	SagaChunk nextSagaChunk = null;
    	Location nextLocation = event.getTo();
    	
    	// Check if the saga chunk changed:
    	if(previousSagaChunk != null && previousSagaChunk.represents(nextLocation)){
    		nextSagaChunk = previousSagaChunk;
    	}else{
    		nextSagaChunk = ChunkGroupManager.manager().getSagaChunk(nextLocation);
    	}
    	
    	// Forward chunk move on chunk change:
    	if(previousSagaChunk != nextSagaChunk){
    		
    		if(nextSagaChunk != null){
    			nextSagaChunk.getChunkGroup().onPlayerSagaChunkChange(sagaPlayer, previousSagaChunk, nextSagaChunk, previousLocation, nextLocation, event);
    		}else if(previousSagaChunk != null){
    			previousSagaChunk.getChunkGroup().onPlayerSagaChunkChange(sagaPlayer, previousSagaChunk, nextSagaChunk, previousLocation, nextLocation, event);
    		}
    		
    	}
    	
    	// Don't update if canceled:
    	if(event.isCancelled()){
    		return;
    	}
    	
    	sagaPlayer.setLastSagaChunk(nextSagaChunk);
    	sagaPlayer.setLastLocation(nextLocation);
    	
    	
    }
    
}
