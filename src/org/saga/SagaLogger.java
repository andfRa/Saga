package org.saga;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;

import jline.ANSIBuffer.ANSICodes;
import jline.ConsoleReader;
import jline.Terminal;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.CraftServer;

public class SagaLogger{

	/**
	 * Instance
	 */
	private static SagaLogger instance = null;
	
	/**
	 * Logger.
	 */
	private Logger logger = Logger.getLogger("Saga");
	
	/**
	 * Chat color replacements.
	 */
	private final Map<ChatColor, String> replacements = new EnumMap<ChatColor, String>(ChatColor.class);
	
	/**
	 * Reader.
	 */
	private final ConsoleReader reader;
	
    /**
     * Terminal.
     */
    private final Terminal terminal;

    /**
     * Colors.
     */
    private final ChatColor[] colors = ChatColor.values();
    
	/**
	 * Creates the logger
	 * 
	 */
	private SagaLogger() {
		
		
		this.reader = ((CraftServer) Saga.plugin().getServer()).getReader();
        this.terminal = reader.getTerminal();
		
		// Add colors:
		replacements.put(ChatColor.BLACK, ANSICodes.attrib(0));
        replacements.put(ChatColor.DARK_BLUE, ANSICodes.attrib(34));
        replacements.put(ChatColor.DARK_GREEN, ANSICodes.attrib(32));
        replacements.put(ChatColor.DARK_AQUA, ANSICodes.attrib(36));
        replacements.put(ChatColor.DARK_RED, ANSICodes.attrib(31));
        replacements.put(ChatColor.DARK_PURPLE, ANSICodes.attrib(35));
        replacements.put(ChatColor.GOLD, ANSICodes.attrib(33));
        replacements.put(ChatColor.GRAY, ANSICodes.attrib(37));
        replacements.put(ChatColor.DARK_GRAY, ANSICodes.attrib(0));
        replacements.put(ChatColor.BLUE, ANSICodes.attrib(34));
        replacements.put(ChatColor.GREEN, ANSICodes.attrib(32));
        replacements.put(ChatColor.AQUA, ANSICodes.attrib(36));
        replacements.put(ChatColor.RED, ANSICodes.attrib(31));
        replacements.put(ChatColor.LIGHT_PURPLE, ANSICodes.attrib(35));
        replacements.put(ChatColor.YELLOW, ANSICodes.attrib(33));
        replacements.put(ChatColor.WHITE, ANSICodes.attrib(37));
		
		
	}

	/**
	 * Formats the message.
	 * 
	 * @param message message
	 * @return formated message
	 */
	private String format(String message) {

		if (terminal.isANSISupported()) {
            String result = message;

            for (ChatColor color : colors) {
                if (replacements.containsKey(color)) {
                    result = result.replaceAll(color.toString(), replacements.get(color));
                } else {
                    result = result.replaceAll(color.toString(), "");
                }
            }
            
            return result + ANSICodes.attrib(0);
            
        }
		
		return ChatColor.stripColor(message);
		
		
	}
	
	/**
	 * Severe message.
	 * 
	 * @param msg message
	 */
	public static void severe(String msg) {
		
		instance.logger.severe(instance.format(ChatColor.RED + msg));
		
	}
	
	/**
	 * Warning message.
	 * 
	 * @param msg message
	 */
	public static void warning(String msg) {
		
		instance.logger.warning(instance.format(ChatColor.YELLOW + msg));
		
	}
	
	/**
	 * Info message.
	 * 
	 * @param msg message
	 */
	public static void info(String msg) {
		
		instance.logger.info(instance.format(msg));
		
	}
	
	
	/**
	 * Loads the logger.
	 * 
	 */
	public static void load() {
		
		instance = new SagaLogger();
		
	}
	
	/**
	 * Unloads the logger.
	 * 
	 */
	public static void unload() {
		
		instance = null;
		
	}
	
	
}
