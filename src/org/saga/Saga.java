/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.saga.Clock.MinuteTicker;
import org.saga.buildings.Building;
import org.saga.chunkGroups.ChunkGroupCommands;
import org.saga.chunkGroups.ChunkGroupManager;
import org.saga.config.AbilityConfiguration;
import org.saga.config.SkillConfiguration;
import org.saga.config.BalanceConfiguration;
import org.saga.config.ChunkGroupConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.config.ExperienceConfiguration;
import org.saga.config.FactionConfiguration;
import org.saga.config.GuildConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.economy.EconomyCommands;
import org.saga.economy.EconomyManager;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.exceptions.SagaPlayerNotLoadedException;
import org.saga.factions.FactionCommands;
import org.saga.factions.FactionManager;
import org.saga.guilds.GuildsManager;
import org.saga.player.PlayerCommands;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsCommands;
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

	
    //Static Members
    private static Saga instance;

    //Instance Members
    private static CommandsManager<Player> commandMap;
    
    private WorldsHolder worldsHolder;
    
    private boolean playerInformationLoadingDisabled;
    
    private boolean playerInformationSavingDisabled;

    private Hashtable<String,SagaPlayer> loadedPlayers;
    
    private static SagaPlayerListener playerListener;
    
    private static SagaEntityListener entityListener;
    
    private static SagaBlockListener blockListener;
    
    private static SagaServerListener serverListener;

    
    // Saving:
    /**
     * Minutes left before save.
     */
    private Integer saveMinutes;
    
    
    static public Saga plugin() {
        return instance;
    }
    
    @Override
    public void onDisable() {


    	// Messages:
        Saga.info("Disabling Saga.");
    	
    	// Disable automatic saving:
    	Clock.clock().unregisterMinuteTick(this);
        
        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // Unload all saga players:
        unloadAllSagaPlayers();
        
    	// Remove instances:
    	loadedPlayers = null;
    	
    	// Listeners:
        Saga.playerListener = null;
        Saga.blockListener = null;
        Saga.serverListener = null;
        Saga.entityListener = null;
        
        // Managers:
        StatisticsManager.unload(); // Needs access to clock.
        ChunkGroupManager.unload(); // Needs building manager.
        FactionManager.unload(); // Needs access to chunk group manager.
        EconomyManager.unload();
        GuildsManager.unload(); // Needs access to the clock.
        
        // Configuration:
        ExperienceConfiguration.unload();
        ProficiencyConfiguration.unload();
        SkillConfiguration.load();
        AbilityConfiguration.unload();
        BalanceConfiguration.unload();
        ChunkGroupConfiguration.unload();
        EconomyConfiguration.unload();
        FactionConfiguration.unload();
        GuildConfiguration.load();  // Needs access to clock.
        Clock.unload(); // Needs access to Saga.pluging()
        
        // Other:
        commandMap = null;
        
        Saga.instance = null;

        Saga.info("Saga disabled.");
        
        // Logger:
        SagaLogger.unload();
        
        
    }

    @Override
    public void onEnable() {
        

    	//Set Global Plugin Instance Variable
    	Saga.instance = this;

        // Logger:
        SagaLogger.load();
    	
    	// Messages:
    	Saga.info("Enabling Saga.");

        //Allocate Instance Variables
        loadedPlayers = new Hashtable<String, SagaPlayer>();
        
        // Get GroupManager:
        PluginManager pluginManager = getServer().getPluginManager();
        try {
    	   Plugin groupManager = null;
           groupManager = pluginManager.getPlugin("GroupManager");
           if ( groupManager != null ) {
               Saga.info("Using GroupManager.");
               worldsHolder = ((GroupManager)groupManager).getWorldsHolder();
           } else {
               Saga.warning("GroupManager not found. Using Op only permissions.");
           }
       } catch (Throwable e) {
    	   Saga.severe("GroupManager failed to load: " + e.getClass().getSimpleName() + ":" + e.getMessage() +". Using Op only permissions.");
       }

        //Setup Command Manager
        commandMap = new CommandsManager<Player>() {
            @Override
            public boolean hasPermission(Player player, String perm) {
            	
            	
                if ( worldsHolder != null ) {
                    OverloadedWorldHolder world = worldsHolder.getWorldData(player);
                    User user = world.getUser(player.getName());
                    return world.getPermissionsHandler().checkUserPermission(user, perm);
                } else  {
                    return player.isOp();
                }
                
                
            }
            
        };

        // Configuration:
        Clock.load(); // Needs access to Saga.pluging().
        GuildConfiguration.load();
        BalanceConfiguration.load();
        ExperienceConfiguration.load();
        AbilityConfiguration.load();
        SkillConfiguration.load();
        ProficiencyConfiguration.load(); // Needs access to experience info.
        ChunkGroupConfiguration.load();
        EconomyConfiguration.load();
        FactionConfiguration.load();
        
        // Managers:
        GuildsManager.load(); // Needs access to the clock.
        ChunkGroupManager.load();
        FactionManager.load(); // Needs access to chunk group manager.
        EconomyManager.load(); // Needs access to clock.
        StatisticsManager.load(); // Needs access to clock.
        
        //Create listeners:
      	playerListener = new SagaPlayerListener(this);
      	entityListener = new SagaEntityListener();
      	blockListener = new SagaBlockListener();
      	serverListener = new SagaServerListener();
      	
        // Register events
        pluginManager.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.ENTITY_COMBUST, entityListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Priority.Normal, this);
       
        pluginManager.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.BLOCK_DAMAGE, blockListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.BLOCK_SPREAD, blockListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.BLOCK_FROMTO, blockListener, Priority.Normal, this);
        
        pluginManager.registerEvent(Event.Type.ENDERMAN_PICKUP, entityListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.ENDERMAN_PLACE, entityListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
         
        pluginManager.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_INTERACT_ENTITY, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_PORTAL, playerListener, Priority.Normal, this);
        
        pluginManager.registerEvent(Event.Type.SERVER_COMMAND, serverListener, Priority.Normal, this);
        
        //Register Command Classes to the command map
        commandMap.register(SagaCommands.class);
        commandMap.register(FactionCommands.class);
        commandMap.register(ChunkGroupCommands.class);
        commandMap.register(EconomyCommands.class);
        commandMap.register(PlayerCommands.class);
        commandMap.register(StatisticsCommands.class);
        
        // Register all buildings as command classes:
        ArrayList<Building> buildings = ChunkGroupConfiguration.config().getBuildings();
		for (Building building : buildings) {
			commandMap.register(building.getClass());
		}

        // Load all saga players:
        loadAllSagaPlayers();
		
        Saga.info("Saga enabled.");

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
            Saga.severe("Saga player was supposed to have been loaded!!");
        }

    	// Check if online:
    	if( sagaPlayer.isOnline() ) {
            severe("Cant wrap player for " + player.getName() + ", because sagaPlayer is already set to online. Wrapping ignored.");
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
            severe(getClass(), "Cant remove player wrap form " + name + ", because the saga player isnt loaded", "player information not saved");
            return;
    	}
    	
    	// Remove if online:
    	if( !sagaPlayer.isOnline() ) {
    		severe(getClass(), "Cant remove player wrap form " + name + ", because the saga player isn't online", "ignoring request");
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
            severe(getClass(), "tried to load already loadaded and forced saga player for " + name, "loading ignored");
            return sagaPlayer;
    	}
    	
    	// Check if already loaded:
    	if( sagaPlayer != null ){
    		severe(getClass(), "tried to load already loadaded saga player for " + name, "loading ignored");
            return sagaPlayer;
    	}

    	// Load:
    	sagaPlayer = SagaPlayer.load(name);
    	Saga.info("Loading saga player for " + name + ".");
    	putSagaPlayer(name, sagaPlayer);
    	
    	// Register factions:
    	FactionManager.manager().playerLoaded(sagaPlayer);
    	
    	// Register chunk groups:
    	ChunkGroupManager.manager().playerLoaded(sagaPlayer);
    	
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
    		severe(getClass(), "tried unload a non-loaded player for " + name, "loading ignored");
            return sagaPlayer;
    	}
    	
    	// Ignore if forced:
    	if( sagaPlayer.isForced() ){
    		info("Denied unloading for a forced saga player " + sagaPlayer.getName() + ".");
            return sagaPlayer;
    	}
    	
    	// Unload:
    	Saga.info("Unloading saga player for " + name + ".");
    	removeSagaPlayer(name);
    	
    	// Unregister factions:
    	FactionManager.manager().playerUnloaded(sagaPlayer);
    	
    	// Register chunk groups:
    	ChunkGroupManager.manager().playerUnloaded(sagaPlayer);
    	
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
    	return SagaPlayer.checkExistance(playerName);
	}
    
    /**
     * Gets a loaded saga player.
     * 
     * @param player player
     * @return saga player
     * @throws SagaPlayerNotLoadedException  if saga player is not loaded
     */
    @Deprecated
    public SagaPlayer getLoadedSagaPlayer(String name) throws SagaPlayerNotLoadedException {
    	
    	
    	// Search from loaded list:
    	SagaPlayer sagaPlayer = getSagaPlayer(name);
    	
    	// Throw an exception if player not loaded:
    	if(sagaPlayer == null){
    		throw new SagaPlayerNotLoadedException(name);
    	}
    	
        return sagaPlayer;
        
        
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
    		Saga.info("Forcing saga player for " + name + ".");
    		sagaPlayer.increaseForceLevel();
    		return sagaPlayer;
    	}
    	
    	// Check if the player exists:
    	if(!isSagaPlayerExistant(name)){
    		throw new NonExistantSagaPlayerException(name);
    	}
    	
    	// Load:
    	sagaPlayer = loadSagaPlayer(name);
    	Saga.info("Forcing saga player for " + name + ".");
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
    		Saga.severe(getClass(), "tried to unforce a non-loaded player for " + name, "ignoring request");
    		return;
    	}
    	
    	// Decrease force level:
    	Saga.info("Unforcing saga player for " + name + ".");
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
     * @return saga player. null if not loaded
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
        ChunkGroupManager.save();
        FactionManager.save();
        EconomyManager.save();
        GuildsManager.save();
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
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	
    	System.out.println("SENDER=" + sender.getName());
    	
    	// TODO Auto-generated method stub
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

    	
        try {

            split[0] = split[0].substring(1);

            // Quick script shortcut
            if (split[0].matches("^[^/].*\\.js$")) {
                String[] newSplit = new String[split.length + 1];
                System.arraycopy(split, 0, newSplit, 1, split.length);
                newSplit[0] = "cs";
                newSplit[1] = newSplit[1];
                split = newSplit;
            }

            // No command found!
            if (!commandMap.hasCommand(split[0])) {
                return false;
            }

            try {
                commandMap.execute(split, player, this, getLoadedSagaPlayer(player.getName()));
                String logString = "[Saga Command] " + player.getName() + ": " + command;
                Saga.info(logString);
            } catch (CommandPermissionsException e) {
                player.sendMessage(ChatColor.RED + "You don't have permission to do that!");
            } catch (MissingNestedCommandException e) {
                player.sendMessage(e.getUsage());
            } catch (CommandUsageException e) {
                player.sendMessage(e.getMessage());
                player.sendMessage(e.getUsage());
            } catch (WrappedCommandException e) {
                player.sendMessage(ChatColor.RED + e.getMessage());
                throw e;
            } catch (UnhandledCommandException e) {
                player.sendMessage(ChatColor.RED + "Unhandled command exception");
                return false;
            } finally {

            }

        } catch (Throwable excp) {

            player.sendMessage("Problem handling command: " + command);
            player.sendMessage(excp.getMessage());
            excp.printStackTrace();
            return false;

        }

        return true;

    }

    /**
     * True, if player information loading is disabled.
     *
     * @return the playerInformationLoadingDisabled
     */
    public boolean isPlayerInformationLoadingDisabled() {
            return playerInformationLoadingDisabled;
    }

    /**
     * True, if player information saving is disabled.
     *
     * @return the playerInformationSavingDisabled
     */
    public boolean isPlayerInformationSavingDisabled() {
            return playerInformationSavingDisabled;
    }

    /**
     * Disables the loading and saving of player information.
     *
     */
    public void disablePlayerInformationSavingLoading() {

            if( playerInformationLoadingDisabled && playerInformationSavingDisabled ){
                    return;
            }

            playerInformationLoadingDisabled = true;
            playerInformationSavingDisabled = true;

            Saga.warning("Disabling player information saving and loading.");

    }

    
    // Permissions:
    /**
     * Check if the player has permission.
     * 
     * @param sagaPlayer saga player
     * @param permission permission
     * @return true if has permission
     */
    public boolean hasPermission(SagaPlayer sagaPlayer, String permission) {
    	
    	try {
    		
    		if(commandMap != null && sagaPlayer.isOnline() && commandMap.hasPermission(sagaPlayer.getPlayer(), permission)) return true;
    		
		} catch (Throwable e) {
			
			Saga.severe(getClass(), "failed to check GroupManager permission for " + sagaPlayer.getName() + ", " + e.getClass().getSimpleName() + ":" + e.getMessage(), "ignoring GroupManager permission");
		
		}
    	
    	return false;
    	
	}
    
    /**
     * Checks if the player has access to admin mode.
     * 
     * @param sagaPlayer saga player
     * @return true if has access
     */
    public boolean canAdminMode(SagaPlayer sagaPlayer) {

    	return hasPermission(sagaPlayer, "saga.admin.adminmode");
    	
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
    
    
    // Log:
    static public void info(String msg) {
    	SagaLogger.info(msg);
    }

    static public void info(Object instance, String message, String result) {
        info(instance.getClass().getSimpleName() + ":{" + instance + "} " + message + ". " + result + ".");
    }

    static public void info(Class<?> tClass, String message, String result) {
    	info(tClass.getSimpleName() + ": " + message + ". " + result + ".");
    }
    
    static public void severe(String msg) {
    	SagaLogger.severe(msg);
    }
    
    static public void severe(Object instance, String message, String result) {
        severe(instance.getClass().getSimpleName() + ":{" + instance + "} " + message + ". " + result + ".");
    }
    
    static public void severe(Class<?> tClass, String message, String result) {
    	severe(tClass.getSimpleName() + ": " + message + ". " + result + ".");
    }

    static public void warning(String msg) {
    	SagaLogger.warning(msg);
    }
    
    static public void warning(Object instance, String message, String result) {
        warning(instance.getClass().getSimpleName() + ":{" + instance + "} " + message + ". " + result + ".");
    }
    
    static public void warning(Class<?> tClass, String message, String result) {
        warning(tClass.getSimpleName() + ": " + message + ". " + result + ".");
    }

    
    
}
