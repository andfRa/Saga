/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
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
public class SagaPlayerListener extends PlayerListener {

    protected Saga plugin;

    private HashSet<String> bigBoots = new HashSet<String>();
    
    
    public SagaPlayerListener(Saga plugin) {
        this.plugin = plugin;
    }

    @Override
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
    
    @Override
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

    @Override
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

    @Override
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

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {

    	
    	SagaPlayer sagaPlayer = plugin.getSagaPlayer(event.getPlayer().getName());
    	if(sagaPlayer == null){
    		Saga.warning("Can't continue with onPlayerMove, because the saga player for "+ event.getPlayer().getName() + " isn't loaded.");
    		return;
    	}
    	
    	// Handle chunk change:
    	handleChunkChange(sagaPlayer, event);
    	
        
    }

    @Override
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

    @Override
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
    	
    	// Forward to chunk group:
    	if(sagaChunk != null){
    		sagaChunk.getChunkGroup().onPlayerInteract(event, sagaPlayer, sagaChunk);
    	}
    	
    	// Check if canceled:
    	if(event.isCancelled()){
    		return;
    	}
    	
    	// Forward to level manager:
    	sagaPlayer.getLevelManager().onPlayerInteract(event);
    	
    	
	
	}
	
    @Override
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
    
    @Override
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {

    }
    
    @Override
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
    
    @Override
    public void onPlayerPortal(PlayerPortalEvent event) {
    	
    	
//    	if(event.getTo().getWorld().getName().equals("world_the_end") || event.getTo().getWorld().getName().equals("world_skylands")){
//    		event.setCancelled(true);
//    		event.getPlayer().sendMessage(ChatColor.DARK_RED  + "skylands is not accessible until bukkit is fully updated!");
//    		Saga.warning(event.getPlayer().getName() + " tried to portal to world_the_end");
//    		event.getPlayer().teleport(Saga.plugin().getServer().getWorld("world").getSpawnLocation());
//    		return;
//    	}
    	
    	
    }
    
    /**
     * Used when a saga player uses its projectile shoot methods.
     * 
     * @author andf
     *
     */
    public static class SagaPlayerProjectileShotEvent extends Event implements Cancellable{

    	
    	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * Projectile type.
		 */
		private ProjectileType projectileType;
		
		/**
    	 * Projectile speed.
    	 */
    	private double speed;
    	
    	/**
    	 * Saga player.
    	 */
    	private final SagaPlayer shooter;
    	
    	/**
    	 * If true, the event will be canceled.
    	 */
    	private boolean isCanceled = false;
    	
    	
		/**
		 * Sets a projectile and a saga player.
		 * 
		 * @param who saga player
		 * @param projectileType projectile type
		 * @param speed projectile speed
		 */
		public SagaPlayerProjectileShotEvent(SagaPlayer who, ProjectileType projectileType, double speed) {
			
			
			super("saga player projectile shot");
			this.speed = speed;
			this.projectileType = projectileType;
			this.shooter = who;
			
			
		}
		
		
		/**
		 * Returns the projectile type.
		 * 
		 * @return projectile type
		 */
		public ProjectileType getProjectileType() {
			return projectileType;
		}
		
		/**
		 * Gets projectile speed.
		 * 
		 * @return projectile speed
		 */
		public double getSpeed() {
			return speed;
		}
		
		/**
		 * Sets projectile speed
		 * 
		 * @param speed projectile speed
		 */
		public void setSpeed(double speed) {
			this.speed = speed;
		}
		
		/**
		 * Increases projectile speed
		 * 
		 * @param amount amount
		 */
		public void increaseSpeed(double amount) {
			this.speed += amount;
		}
		
		/**
		 * Decrease projectile speed
		 * 
		 * @param amount amount
		 */
		public void decreaseSpeed(double amount) {
			this.speed -= amount;
		}

		/**
		 * Gets the saga player that shot the projectile.
		 * 
		 * @return saga player
		 */
		public SagaPlayer getShooter() {
			return shooter;
		}
		
		/* 
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.event.Cancellable#isCancelled()
		 */
		@Override
		public boolean isCancelled() {
			return isCanceled;
		}

		/* 
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.event.Cancellable#setCancelled(boolean)
		 */
		@Override
		public void setCancelled(boolean cancel) {
			isCanceled = cancel;
		}
    	
		
		 /**
		 * Check if the event launches a projectile.
		 * 
		 * @param event
		 * @return
		 */
		public static boolean checkIfProjectile(PlayerInteractEvent event){
		    	
		    	
		    	Player player = event.getPlayer();
		    	Material itemInHand = player.getItemInHand().getType();
		    	if(itemInHand.equals(Material.BOW) && player.getInventory().contains(Material.ARROW)){
		    		return true;
		    	}
		    	
		    	return false;
		    	
		    	
		    }
		
		
		/**
		 * Projectile type.
		 * 
		 * @author andf
		 *
		 */
		public enum ProjectileType{
			
			NONE,
			ARROW,
			FIREBALL,
			
		}
    	
		
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
