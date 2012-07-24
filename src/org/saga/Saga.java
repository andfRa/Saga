package org.saga;

import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.saga.Clock.MinuteTicker;
import org.saga.chunks.ChunkBundleManager;
import org.saga.commands.AdminCommands;
import org.saga.commands.BuildingCommands;
import org.saga.commands.SettlementCommands;
import org.saga.commands.EconomyCommands;
import org.saga.commands.FactionCommands;
import org.saga.commands.PlayerCommands;
import org.saga.commands.StatisticsCommands;
import org.saga.config.AbilityConfiguration;
import org.saga.config.AttributeConfiguration;
import org.saga.config.BalanceConfiguration;
import org.saga.config.SettlementConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.config.ExperienceConfiguration;
import org.saga.config.FactionConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.dependencies.PermissionsManager;
import org.saga.dependencies.spout.ClientManager;
import org.saga.economy.EconomyManager;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.exceptions.SagaPlayerNotLoadedException;
import org.saga.factions.FactionManager;
import org.saga.listeners.BlockListener;
import org.saga.listeners.EntityListener;
import org.saga.listeners.PlayerListener;
import org.saga.listeners.ServerListener;
import org.saga.listeners.WorldListener;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;
import org.sk89q.CommandPermissionsException;
import org.sk89q.CommandUsageException;
import org.sk89q.CommandsManager;
import org.sk89q.MissingNestedCommandException;
import org.sk89q.UnhandledCommandException;
import org.sk89q.WrappedCommandException;

/**
 *
 * @author Cory
 */
public class Saga extends JavaPlugin implements MinuteTicker{

	
    /**
     * Plugin instance.
     */
    private static Saga plugin;

    /**
     * Gets Saga plugin.
     * 
     * @return Saga plugin
     */
    public static Saga plugin() {
        return plugin;
    }
    
    
    /**
     * All saga players.
     */
    private Hashtable<String,SagaPlayer> loadedPlayers;

    
    // Saving:
    /**
     * Minutes left before save.
     */
    private Integer saveMinutes;
    
    
    /* 
     * (non-Javadoc)
     * 
     * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
     */
    @Override
    public void onDisable() {


    	// Messages:
    	SagaLogger.info("Disabling Saga.");
    	
    	// Disable automatic saving:
    	Clock.clock().unregisterMinuteTick(this);
        
        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // Unload all saga players:
        unloadAllSagaPlayers();
        
    	// Remove instances:
    	loadedPlayers = null;
    	
        // Managers:
        StatisticsManager.unload(); // Needs access to clock.
        ChunkBundleManager.unload(); // Needs building manager.
        FactionManager.unload(); // Needs access to chunk group manager.
        EconomyManager.unload();
        
        // Configuration:
        ExperienceConfiguration.unload();
        ProficiencyConfiguration.unload();
        AttributeConfiguration.load();
        AbilityConfiguration.unload();
        BalanceConfiguration.unload();
        SettlementConfiguration.unload();
        EconomyConfiguration.unload();
        FactionConfiguration.unload();
        Clock.unload(); // Needs access to Saga.pluging()

        // Disable permissions:
        PermissionsManager.disable();

        // Disable client manager:
        ClientManager.disable();
        
        Saga.plugin = null;

        SagaLogger.info("Saga disabled.");
        
        // Logger:
        SagaLogger.unload();
        
        
    }

    /* 
     * (non-Javadoc)
     * 
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {
        

    	//Set Global Plugin Instance Variable
    	Saga.plugin = this;

        // Logger:
        SagaLogger.load();
    	
    	// Messages:
        SagaLogger.info("Enabling Saga.");

    	// Plugin manager:
    	final PluginManager pluginManager = getServer().getPluginManager();
    	
        // Players:
        loadedPlayers = new Hashtable<String, SagaPlayer>();
        
        // Enable permissions:
        PermissionsManager.enable();
        
        // Enable client manager:
        ClientManager.enable();
        
        // Configuration:
        Clock.load(); // Needs access to Saga.pluging().
        BalanceConfiguration.load();
        ExperienceConfiguration.load();
        AbilityConfiguration.load();
        AttributeConfiguration.load();
        ProficiencyConfiguration.load(); // Needs access to experience info.
        SettlementConfiguration.load();
        EconomyConfiguration.load();
        FactionConfiguration.load();
        
        // Managers:
        ChunkBundleManager.load();
        FactionManager.load(); // Needs access to chunk group manager.
        EconomyManager.load(); // Needs access to clock.
        StatisticsManager.load(); // Needs access to clock.
        
        // Register events:
      	pluginManager.registerEvents(new PlayerListener(), this);
      	pluginManager.registerEvents(new EntityListener(), this);
      	pluginManager.registerEvents(new BlockListener(), this);
      	pluginManager.registerEvents(new ServerListener(), this);
      	pluginManager.registerEvents(new WorldListener(), this);

      	
      	// Commands map:
      	CommandsManager<Player> commandMap = PermissionsManager.getCommandMap();
      	
        //Register Command Classes to the command map
        commandMap.register(AdminCommands.class);
        commandMap.register(FactionCommands.class);
        commandMap.register(SettlementCommands.class);
        commandMap.register(EconomyCommands.class);
        commandMap.register(PlayerCommands.class);
        commandMap.register(StatisticsCommands.class);
        commandMap.register(BuildingCommands.class);

        // Load all saga players:
        loadAllSagaPlayers();
		
        SagaLogger.info("Saga enabled.");

        // Enable automatic saving:
    	saveMinutes = BalanceConfiguration.config().saveInterval;
    	Clock.clock().registerMinuteTick(this);
        
    	
	}
    
    
    // Player management:
    /**
     * Adds the player. Loads saga player if necessary.
	 * If sagaPlayer is already set to online, then the add will be ignored.
     * 
     * @param player player
     */
    public void addPlayer(Player player) {
    	
    	
    	SagaPlayer sagaPlayer = loadSagaPlayer(player.getName());

    	// Load if saga player isn't already loaded:
    	if( sagaPlayer != null ) {
            sagaPlayer.setPlayer(player);
            return;
    	}

        //Player isn't loaded, so try loading
        sagaPlayer = loadSagaPlayer(player.getName());

        if ( sagaPlayer == null ) {
        	SagaLogger.severe(getClass(), "failed to load saga player for " + player.getName());
        }

    	// Check if online:
    	if( sagaPlayer.isOnline() ) {
    		SagaLogger.severe(getClass(), "cant wrap player for " + player.getName() + ", because sagaPlayer is already set to online");
            return;
    	}
    	
    	// Add the player and set sagaPlayer status to online:
    	sagaPlayer.setPlayer(player);

    	
    }

    /**
     * Unwraps the player and unloads saga player from the list.
     * 
     * @param player
     */
    public void removePlayer(String name) {

    	
    	SagaPlayer sagaPlayer = getSagaPlayer(name);
    	// Check if loaded:
    	if( sagaPlayer == null ) {
            SagaLogger.severe(getClass(), "Cant remove player wrap form " + name + ", because the saga player isnt loaded");
            return;
    	}
    	
    	// Remove if online:
    	if( !sagaPlayer.isOnline() ) {
    		SagaLogger.severe(getClass(), "Cant remove player wrap form " + name + ", because the saga player isn't online");
    	} else {
            sagaPlayer.removePlayer();
    	}

        // Unload saga player:
        unloadSagaPlayer(name);

        
    }

    /**
     * Unloads all saga players.
     */
    private void unloadAllSagaPlayers() {

    	
        Enumeration<String> names= loadedPlayers.keys();
        
        while ( names.hasMoreElements() ) {
        	String name = names.nextElement();
            SagaPlayer sagaPlayer = unloadSagaPlayer(name);
            if(sagaPlayer != null){
            	sagaPlayer.removePlayer();
            }
        }

        //Empty the table
        loadedPlayers.clear();

        
    }

    /**
     * Unloads all saga players.
     */
    private void loadAllSagaPlayers() {

    	
        Player[] players= getServer().getOnlinePlayers();
        
        for (int i = 0; i < players.length; i++) {
        	SagaPlayer sagaPlayer = loadSagaPlayer(players[i].getName());
        	if(sagaPlayer != null){
        		sagaPlayer.setPlayer(players[i]);
        	}
		}
        
        
    }
    
    /**
     * Loads a saga player. The minecraft player needs to be set separately.
     * If no player exists, then a new one is created.
     * 
     * @param name player name
     * @return loaded player, null if not failed to load
     */
    public SagaPlayer loadSagaPlayer(String name) {
    	
    	
    	SagaPlayer sagaPlayer = getSagaPlayer(name);

    	// Check if forced:
    	if( sagaPlayer != null && sagaPlayer.isForced() ){
    		SagaLogger.severe(getClass(), "tried to load already loadaded and forced saga player for " + name);
            return sagaPlayer;
    	}
    	
    	// Check if already loaded:
    	if( sagaPlayer != null ){
    		SagaLogger.severe(getClass(), "tried to load already loadaded saga player for " + name);
            return sagaPlayer;
    	}

    	// Load:
    	sagaPlayer = SagaPlayer.load(name);
    	SagaLogger.info("Loading saga player for " + name + ".");
    	putSagaPlayer(name, sagaPlayer);
    	
    	// Register factions:
    	FactionManager.manager().playerLoaded(sagaPlayer);
    	
    	// Register chunk groups:
    	ChunkBundleManager.manager().playerLoaded(sagaPlayer);
    	
    	// Update:
    	sagaPlayer.update();
    	
    	return sagaPlayer;
    	
    	
    }

    /**
     * Unloads a saga player as offline.
     * Wrapped player must be removed first or this method will ignore unloading.
     * 
     * @param name player name
     * @return unloaded player, null if not loaded
     */
    public SagaPlayer unloadSagaPlayer(String name) {
    	
    	
    	SagaPlayer sagaPlayer = getSagaPlayer(name);
    	
    	// Ignore if already unloaded:
    	if( sagaPlayer == null ) {
    		SagaLogger.severe(getClass(), "tried unload a non-loaded player for " + name);
            return sagaPlayer;
    	}
    	
    	// Ignore if forced:
    	if( sagaPlayer.isForced() ){
    		SagaLogger.info("Denied unloading for a forced saga player " + sagaPlayer.getName() + ".");
            return sagaPlayer;
    	}
    	
    	// Unload:
    	SagaLogger.info("Unloading saga player for " + name + ".");
    	removeSagaPlayer(name);
    	
    	// Unregister factions:
    	FactionManager.manager().playerUnloaded(sagaPlayer);
    	
    	// Register chunk groups:
    	ChunkBundleManager.manager().playerUnloaded(sagaPlayer);
    	
    	// Unload:
        sagaPlayer.unload();
        
        return sagaPlayer;
        
    	
    }

    /**
     * Checks if the player is loaded.
     * 
     * @param name name
     * @return true if loaded
     */
    public boolean isSagaPlayerLoaded(String name) {
    	return getSagaPlayer(name)!=null;
	}
    
    /**
     * Checks if the player exists by checking player information file.
     * 
     * @param playerName player name
     * @return true if the player exists
     */
    public boolean isSagaPlayerExistant(String playerName) {
    	return SagaPlayer.checkExists(playerName);
	}
    
    /**
     * Forces the player to get loaded in the forced list.
     * Loads if necessary.
     * 
     * @param name player name
     * @throws NonExistantSagaPlayerException if the player doesn't exist.
     */
    public SagaPlayer forceSagaPlayer(String name) throws NonExistantSagaPlayerException {

    	
    	SagaPlayer sagaPlayer;
    	
    	// Check in loaded list:
    	sagaPlayer = getSagaPlayer(name);
    	if(sagaPlayer != null){
    		SagaLogger.info("Forcing saga player for " + name + ".");
    		sagaPlayer.increaseForceLevel();
    		return sagaPlayer;
    	}
    	
    	// Check if the player exists:
    	if(!isSagaPlayerExistant(name)){
    		throw new NonExistantSagaPlayerException(name);
    	}
    	
    	// Load:
    	sagaPlayer = loadSagaPlayer(name);
    	SagaLogger.info("Forcing saga player for " + name + ".");
    	sagaPlayer.increaseForceLevel();
		return sagaPlayer;
    	
    	
	}

    /**
     * Unforces the player to get loaded in the forced list.
     * Saves if necessary.
     * 
     * @param name player name
     * @throws NonExistantSagaPlayerException if the player doesn't exist.
     */
    public void unforceSagaPlayer(String name) {

    	
    	// Check in loaded list:
    	SagaPlayer sagaPlayer = getSagaPlayer(name);
    	if(sagaPlayer == null){
    		SagaLogger.severe(getClass(), "tried to unforce a non-loaded player for " + name);
    		return;
    	}
    	
    	// Decrease force level:
    	SagaLogger.info("Unforcing saga player for " + name + ".");
    	sagaPlayer.decreaseForceLevel();
    	
    	// Unload if possible:
		if(!sagaPlayer.isForced() && !sagaPlayer.isOnline()){
			unloadSagaPlayer(name);
		}
    	
    	
	}
    
    /**
     * Gets a saga player from the loaded list.
     * 
     * @param name name
     * @return saga player, null if not loaded
     */
    public SagaPlayer getSagaPlayer(String name) {
    	return loadedPlayers.get(name.toLowerCase());
	}

    /**
     * Puts a saga player from the loaded list.
     * 
     * @param name name
     */
    public void putSagaPlayer(String name, SagaPlayer sagaPlayer) {
    	loadedPlayers.put(name.toLowerCase(), sagaPlayer);
	}
    
    /**
     * Removes a saga player from the loaded list.
     * 
     * @param name name
     * @return saga player. null if failed
     */
    public SagaPlayer removeSagaPlayer(String name) {
    	return loadedPlayers.remove(name.toLowerCase());
	}
    
    /**
     * Gets all loaded saga players.
     * 
     * @return all loaded saga players
     */
    public Enumeration<SagaPlayer> getLoadedPlayers() {

    	Enumeration<SagaPlayer> sagaPlayers = loadedPlayers.elements();
    	
    	return sagaPlayers;
    	
	}
    
    
    // Saving:
    /**
     * Saves all players.
     * 
     */
    private void saveAllPlayers() {

    	
       Enumeration<SagaPlayer> sagaPlayers = loadedPlayers.elements();
       while (sagaPlayers.hasMoreElements()) {
    	   SagaPlayer sagaPlayer = sagaPlayers.nextElement();
    	   sagaPlayer.save();
       }

        
	}
    
    /**
     * Saves all managers.
     * 
     */
    private void saveManagers() {


        // Save managers:
        ChunkBundleManager.save();
        FactionManager.save();
        EconomyManager.save();
    	StatisticsManager.save();
    	
	}
    
    /**
     * Saves everything.
     * 
     */
    public void save() {

    	saveManagers();
    	saveAllPlayers();
    	
	}
    
    
    // Clock:
    /* 
     * (non-Javadoc)
     * 
     * @see org.saga.Clock.MinuteTicker#clockMinuteTick()
     */
    @Override
    public void clockMinuteTick() {

    	saveMinutes --;
    	
    	if(saveMinutes <= 0){
    		
    		saveMinutes = BalanceConfiguration.config().saveInterval;
    		
    		save();
    		
    	}
    	
    }
    
    
    // Commands:
    /* 
     * (non-Javadoc)
     * 
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	
    	return super.onCommand(sender, command, label, args);
    	
    }
    
    /**
     * Handles a command
     * 
     * @param player player
     * @param split split arguments
     * @param command command
     * @return true if successful
     */
    public boolean handleCommand(Player player, String[] split, String command) {

    	
    	CommandsManager<Player> commandMap = PermissionsManager.getCommandMap();
    	
        try {

            split[0] = split[0].substring(1);

            // Quick script shortcut:
            if (split[0].matches("^[^/].*\\.js$")) {
            	
                String[] newSplit = new String[split.length + 1];
                System.arraycopy(split, 0, newSplit, 1, split.length);
                newSplit[0] = "cs";
                newSplit[1] = newSplit[1];
                split = newSplit;
                
            }

            // Check for command:
            if (!commandMap.hasCommand(split[0]))  return false;

            try {
            	
                commandMap.execute(split, player, this, getSagaPlayer(player.getName()));
                SagaLogger.info("[Saga Command] " + player.getName() + ": " + command);
                
            } catch (CommandPermissionsException e) {
               
            	player.sendMessage(ChatColor.RED + "You don't have permission to do that!");
            	
            } catch (MissingNestedCommandException e) {
                
            	player.sendMessage(e.getUsage());
            	
            } catch (CommandUsageException e) {
                
            	if(e.getMessage() != null) player.sendMessage(e.getMessage());
                player.sendMessage(e.getUsage());
                
            } catch (WrappedCommandException e) {
                
            	if(e.getMessage() != null) player.sendMessage(ChatColor.RED + e.getMessage());
                throw e;
                
            } catch (UnhandledCommandException e) {
               
            	player.sendMessage(ChatColor.RED + "Unhandled command exception");
                return false;
                
            } finally {

            }

        } catch (Throwable t) {

            player.sendMessage("Failed to handle command: " + command);
            if(t.getMessage() != null) player.sendMessage(t.getMessage());
            t.printStackTrace();
            return false;

        }

        return true;

        
    }

    
    // Permissions:
    /**
     * Check if the player has permission.
     * 
     * @param sagaPlayer saga player
     * @param permission permission
     * @return true if has permission
     */
    public boolean hasPermission2(SagaPlayer sagaPlayer, String permission) {
    	
//    	try {
//    		
//    		if(commandMap != null && sagaPlayer.isOnline() && commandMap.hasPermission(sagaPlayer.getPlayer(), permission)) return true;
//    		
//		} catch (Throwable e) {
//			
//			Saga.severe(getClass(), "failed to check GroupManager permission for " + sagaPlayer.getName() + ", " + e.getClass().getSimpleName() + ":" + e.getMessage(), "ignoring GroupManager permission");
//		
//		}
//    	
//    	return false;

    	return false;
    	
	}
    
    
    // Messages:
    /**
     * Broadcast a message.
     * 
     * @param message message
     */
    public static void broadcast(String message){
    	
    	Saga.plugin().getServer().broadcastMessage(message);
    	
    }
    
    
    // Other:
    /**
     * Matches a player to his name.
     * 
     * @param name name
     * @return matched player
     * @throws SagaPlayerNotLoadedException when the player isn't loaded
     */
    public SagaPlayer matchPlayer(String name) throws SagaPlayerNotLoadedException{

    	
    	Enumeration<String> players = loadedPlayers.keys();
    	String lcaseName = name.toLowerCase();
    	
    	while (players.hasMoreElements()) {
			
    		String playerName = players.nextElement();
			
			if(playerName.toLowerCase().contains(lcaseName)){
				
				SagaPlayer sagaPlayer = getSagaPlayer(playerName);
				
				if(sagaPlayer == null) continue;
				
    			return sagaPlayer;
    			
    		}
			
		}
    	
    	throw new SagaPlayerNotLoadedException(name);
    	
    	
	}
    
    
}
