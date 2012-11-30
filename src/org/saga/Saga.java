package org.saga;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.saga.Clock.MinuteTicker;
import org.saga.commands.AdminCommands;
import org.saga.commands.BuildingCommands;
import org.saga.commands.EconomyCommands;
import org.saga.commands.FactionCommands;
import org.saga.commands.PlayerCommands;
import org.saga.commands.SettlementCommands;
import org.saga.commands.StatisticsCommands;
import org.saga.config.AbilityConfiguration;
import org.saga.config.AttributeConfiguration;
import org.saga.config.EconomyConfiguration;
import org.saga.config.ExperienceConfiguration;
import org.saga.config.FactionConfiguration;
import org.saga.config.GeneralConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.config.SettlementConfiguration;
import org.saga.config.VanillaConfiguration;
import org.saga.dependencies.ChatDependency;
import org.saga.dependencies.EconomyDependency;
import org.saga.dependencies.PermissionsDependency;
import org.saga.dependencies.spout.ClientManager;
import org.saga.exceptions.NonExistantSagaPlayerException;
import org.saga.exceptions.SagaPlayerNotLoadedException;
import org.saga.factions.FactionClaimManager;
import org.saga.factions.FactionManager;
import org.saga.listeners.BlockListener;
import org.saga.listeners.EntityListener;
import org.saga.listeners.PlayerListener;
import org.saga.listeners.ServerListener;
import org.saga.listeners.WorldListener;
import org.saga.player.SagaPlayer;
import org.saga.settlements.BundleManager;
import org.saga.statistics.StatisticsManager;
import org.sk89q.CommandPermissionsException;
import org.sk89q.CommandUsageException;
import org.sk89q.CommandsManager;
import org.sk89q.MissingNestedCommandException;
import org.sk89q.UnhandledCommandException;
import org.sk89q.WrappedCommandException;

public class Saga extends JavaPlugin implements MinuteTicker {

	
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
	private Hashtable<String, SagaPlayer> loadedPlayers;


	/**
	 * Minutes left before save.
	 */
	private Integer saveMinutes;

	
	
	// Plugin:
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
	 */
	@Override
	public void onDisable() {

		
		// Messages:
		SagaLogger.info("Disabling Saga.");

		// NOTE: All registered events are automatically unregistered when a plugin is disabled

		// Unload all saga players:
		unloadAllSagaPlayers();

		// Remove instances:
		loadedPlayers = null;

		// Managers:
		FactionClaimManager.unload(); // Needs access to factions and bundles.
		BundleManager.unload(); // Needs building manager.
		FactionManager.unload(); // Needs access to chunk group manager.
		StatisticsManager.unload(); // Needs access to clock.

		// Dependencies:
		PermissionsDependency.disable();
		EconomyDependency.disable();
		ChatDependency.disable();

		// Configuration:
		ExperienceConfiguration.unload();
		ProficiencyConfiguration.unload();
		AttributeConfiguration.load();
		AbilityConfiguration.unload();
		GeneralConfiguration.unload();
		SettlementConfiguration.unload();
		EconomyConfiguration.unload();
		VanillaConfiguration.unload();
		FactionConfiguration.unload();
		Clock.unload(); // Needs access to Saga.pluging().

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

		
		// Set global instance:
		Saga.plugin = this;

		// Logger:
		SagaLogger.load();

		// Messages:
		SagaLogger.info("Enabling Saga.");

		// Plugin manager:
		final PluginManager pluginManager = getServer().getPluginManager();

		// Players:
		loadedPlayers = new Hashtable<String, SagaPlayer>();

		// Enable client manager:
		ClientManager.enable();

		// Configuration:
		Clock.load(); // Needs access to Saga.pluging().
		GeneralConfiguration.load();
		ExperienceConfiguration.load();
		AbilityConfiguration.load();
		AttributeConfiguration.load();
		ProficiencyConfiguration.load(); // Needs access to experience info.
		SettlementConfiguration.load();
		EconomyConfiguration.load();
		FactionConfiguration.load();
		VanillaConfiguration.load();

		// Dependencies:
		PermissionsDependency.enable();
		EconomyDependency.enable();
		ChatDependency.enable();
		
		// Managers:
		StatisticsManager.load(); // Needs access to clock.
		BundleManager.load();
		FactionManager.load(); // Needs access to chunk group manager.
		FactionClaimManager.load(); // Needs access to factions and bundles.

		// Register events:
		pluginManager.registerEvents(new PlayerListener(), this);
		pluginManager.registerEvents(new EntityListener(), this);
		pluginManager.registerEvents(new BlockListener(), this);
		pluginManager.registerEvents(new ServerListener(), this);
		pluginManager.registerEvents(new WorldListener(), this);

		// Commands map:
		CommandsManager<Player> commandMap = PermissionsDependency.getCommandMap();

		// Register Command Classes to the command map
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
		saveMinutes = GeneralConfiguration.config().saveInterval;
		Clock.clock().enableMinuteTick(this);

		
	}

	
	
	// Players:
	/**
	 * Puts a saga player from the loaded list.
	 * 
	 * @param name player name
	 */
	private void putSagaPlayer(String name, SagaPlayer sagaPlayer) {
		loadedPlayers.put(name.toLowerCase(), sagaPlayer);
	}

	/**
	 * Removes a saga player from the loaded list.
	 * 
	 * @param name player name
	 * @return removed saga player, null if none
	 */
	private SagaPlayer removeSagaPlayer(String name) {
		return loadedPlayers.remove(name.toLowerCase());
	}

	/**
	 * Loads a saga player.
	 * If the player doesn't exists, then a new one is created.
	 * 
	 * @param name player name
	 * @return loaded player, null if not loaded
	 */
	public SagaPlayer loadSagaPlayer(String name) {

		
		SagaPlayer sagaPlayer = getLoadedPlayer(name);

		// Check if already loaded:
		if (sagaPlayer != null) {
			SagaLogger.severe(getClass(), "tried to load already loadaded Saga player for " + name);
			return sagaPlayer;
		}

		// Load:
		sagaPlayer = SagaPlayer.load(name);
		SagaLogger.info("Loading Saga player for " + name + ".");
		putSagaPlayer(name, sagaPlayer);

		// Synchronise:
		FactionManager.manager().syncFaction(sagaPlayer);
		BundleManager.manager().syncBundle(sagaPlayer);

		// Update:
		sagaPlayer.update();

		return sagaPlayer;

		
	}

	/**
	 * Unloads a saga player as offline.
	 * 
	 * @param name player name
	 * @return unloaded player, null if not loaded
	 */
	public SagaPlayer unloadSagaPlayer(String name) {

		
		SagaPlayer sagaPlayer = getLoadedPlayer(name);

		// Ignore if already unloaded:
		if (sagaPlayer == null) {
			SagaLogger.severe(getClass(), "tried unload a non-loaded player for " + name);
			return sagaPlayer;
		}

		// Unload:
		SagaLogger.info("Unloading saga player for " + name + ".");
		removeSagaPlayer(name);

		// Unload:
		sagaPlayer.unload();

		return sagaPlayer;

		
	}

	/**
	 * Unloads all saga players.
	 */
	private void unloadAllSagaPlayers() {

		
		Enumeration<String> names = loadedPlayers.keys();

		while (names.hasMoreElements()) {
			String name = names.nextElement();
			SagaPlayer sagaPlayer = unloadSagaPlayer(name);
			if (sagaPlayer != null) {
				sagaPlayer.removePlayer();
			}
		}

		// Empty the table
		loadedPlayers.clear();

		
	}

	/**
	 * Loads all saga players.
	 */
	private void loadAllSagaPlayers() {

		
		Player[] players = getServer().getOnlinePlayers();

		for (int i = 0; i < players.length; i++) {
			
			SagaPlayer sagaPlayer = loadSagaPlayer(players[i].getName());
			
			if (sagaPlayer != null) sagaPlayer.setPlayer(players[i]);
		
		}

		
	}

	/**
	 * Gets a saga player from the loaded list.
	 * 
	 * @param name player name
	 * @return saga player, null if not loaded
	 */
	public SagaPlayer getLoadedPlayer(String name) {
		return loadedPlayers.get(name.toLowerCase());
	}

	/**
	 * Gets all loaded saga players.
	 * 
	 * @return all loaded saga players
	 */
	public Collection<SagaPlayer> getLoadedPlayers() {

		Collection<SagaPlayer> sagaPlayers = new ArrayList<SagaPlayer>(loadedPlayers.values());

		return sagaPlayers;

	}

	/**
	 * Checks if the player is loaded.
	 * 
	 * @param name player name
	 * @return true if loaded
	 */
	public boolean isSagaPlayerLoaded(String name) {
		return getLoadedPlayer(name) != null;
	}

	/**
	 * Checks if the player exists by checking player information file.
	 * 
	 * @param name player name
	 * @return true if the player exists
	 */
	public boolean isSagaPlayerExistant(String name) {
		return SagaPlayer.checkExists(name);
	}

	/**
	 * Forces the player. Retrieves both online and offline players.
	 * 
	 * @param name player name
	 * @throws NonExistantSagaPlayerException if the player doesn't exist
	 */
	public SagaPlayer forceSagaPlayer(String name) throws NonExistantSagaPlayerException {

		
		SagaPlayer sagaPlayer;

		// Check in loaded list:
		sagaPlayer = getLoadedPlayer(name);
		if (sagaPlayer != null)
			return sagaPlayer;

		// Check if the player exists:
		if (!isSagaPlayerExistant(name))
			throw new NonExistantSagaPlayerException(name);

		// Load:
		return SagaPlayer.load(name);

		
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

		BundleManager.save();
		FactionManager.save();
		FactionClaimManager.save();
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
	public boolean clockMinuteTick() {

		
		saveMinutes--;

		if (saveMinutes <= 0) {

			saveMinutes = GeneralConfiguration.config().saveInterval;

			save();

		}

		return true;

		
	}

	
	
	// Commands:
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender
	 * , org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		return super.onCommand(sender, command, label, args);

	}

	/**
	 * Handles a command
	 * 
	 * @param player
	 *            player
	 * @param split
	 *            split arguments
	 * @param command
	 *            command
	 * @return true if successful
	 */
	public boolean handleCommand(Player player, String[] split, String command) {

		
		CommandsManager<Player> commandMap = PermissionsDependency.getCommandMap();
		
		// Handle:
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
			if (!commandMap.hasCommand(split[0])) return false;

			try {

				commandMap.execute(split, player, this, getLoadedPlayer(player.getName()));
				SagaLogger.info("[Saga Command] " + player.getName() + ": " + command);

			}
			catch (CommandPermissionsException e) {

				player.sendMessage(ChatColor.RED + "You don't have permission to do that!");

			}
			catch (MissingNestedCommandException e) {

				player.sendMessage(e.getUsage());

			}
			catch (CommandUsageException e) {

				if (e.getMessage() != null) player.sendMessage(e.getMessage());
				player.sendMessage(e.getUsage());

			}
			catch (WrappedCommandException e) {

				if (e.getMessage() != null) player.sendMessage(ChatColor.RED + e.getMessage());
				throw e;

			}
			catch (UnhandledCommandException e) {

				player.sendMessage(ChatColor.RED + "Unhandled command exception");
				return false;

			}
			finally {

			}

		}
		catch (Throwable t) {

			player.sendMessage("Failed to handle command: " + command);
			if (t.getMessage() != null) player.sendMessage(t.getMessage());
			t.printStackTrace();
			return false;

		}

		return true;

		
	}

	
	
	// Messages:
	/**
	 * Broadcast a message.
	 * 
	 * @param message
	 *            message
	 */
	public static void broadcast(String message) {

		Saga.plugin().getServer().broadcastMessage(message);

	}

	
	
	// Other:
	/**
	 * Matches a player to his name.
	 * 
	 * @param name player name
	 * @return matched player
	 * @throws SagaPlayerNotLoadedException when the player isn't loaded
	 */
	public SagaPlayer matchPlayer(String name) throws SagaPlayerNotLoadedException {

		
		Enumeration<String> players = loadedPlayers.keys();
		String lcaseName = name.toLowerCase();

		while (players.hasMoreElements()) {

			String playerName = players.nextElement();

			if (playerName.toLowerCase().contains(lcaseName)) {

				SagaPlayer sagaPlayer = getLoadedPlayer(playerName);

				if (sagaPlayer == null)
					continue;

				return sagaPlayer;

			}

		}

		throw new SagaPlayerNotLoadedException(name);

		
	}

	
}
