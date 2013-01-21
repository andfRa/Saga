package org.saga;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.jline.Terminal;
import org.bukkit.craftbukkit.libs.jline.console.ConsoleReader;
import org.bukkit.craftbukkit.v1_4_R1.CraftServer;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;

public class SagaLogger{

	
	private static SagaLogger instance = null;
	
	private final ConsoleReader reader;
	
    private final Terminal terminal;
    
    private final Map<ChatColor, String> replacements = new EnumMap<ChatColor, String>(ChatColor.class);
    
    private final ChatColor[] colors = ChatColor.values();
    
    private Logger logger = Logger.getLogger("Saga");
    
    
	/**
	 * Creates the logger
	 * 
	 */
	private SagaLogger() {
		
		this.reader = ((CraftServer) Saga.plugin().getServer()).getReader();
        this.terminal = reader.getTerminal();
		
        replacements.put(ChatColor.BLACK, Ansi.ansi().fg(Ansi.Color.BLACK).toString());
        replacements.put(ChatColor.DARK_BLUE, Ansi.ansi().fg(Ansi.Color.BLUE).toString());
        replacements.put(ChatColor.DARK_GREEN, Ansi.ansi().fg(Ansi.Color.GREEN).toString());
        replacements.put(ChatColor.DARK_AQUA, Ansi.ansi().fg(Ansi.Color.CYAN).toString());
        replacements.put(ChatColor.DARK_RED, Ansi.ansi().fg(Ansi.Color.RED).toString());
        replacements.put(ChatColor.DARK_PURPLE, Ansi.ansi().fg(Ansi.Color.MAGENTA).toString());
        replacements.put(ChatColor.GOLD, Ansi.ansi().fg(Ansi.Color.YELLOW).bold().toString());
        replacements.put(ChatColor.GRAY, Ansi.ansi().fg(Ansi.Color.WHITE).toString());
        replacements.put(ChatColor.DARK_GRAY, Ansi.ansi().fg(Ansi.Color.BLACK).bold().toString());
        replacements.put(ChatColor.BLUE, Ansi.ansi().fg(Ansi.Color.BLUE).bold().toString());
        replacements.put(ChatColor.GREEN, Ansi.ansi().fg(Ansi.Color.GREEN).bold().toString());
        replacements.put(ChatColor.AQUA, Ansi.ansi().fg(Ansi.Color.CYAN).bold().toString());
        replacements.put(ChatColor.RED, Ansi.ansi().fg(Ansi.Color.RED).bold().toString());
        replacements.put(ChatColor.LIGHT_PURPLE, Ansi.ansi().fg(Ansi.Color.MAGENTA).bold().toString());
        replacements.put(ChatColor.YELLOW, Ansi.ansi().fg(Ansi.Color.YELLOW).bold().toString());
        replacements.put(ChatColor.WHITE, Ansi.ansi().fg(Ansi.Color.WHITE).bold().toString());
        replacements.put(ChatColor.MAGIC, Ansi.ansi().a(Attribute.BLINK_SLOW).toString());
        replacements.put(ChatColor.BOLD, Ansi.ansi().a(Attribute.UNDERLINE_DOUBLE).toString());
        replacements.put(ChatColor.STRIKETHROUGH, Ansi.ansi().a(Attribute.STRIKETHROUGH_ON).toString());
        replacements.put(ChatColor.UNDERLINE, Ansi.ansi().a(Attribute.UNDERLINE).toString());
        replacements.put(ChatColor.ITALIC, Ansi.ansi().a(Attribute.ITALIC).toString());
        replacements.put(ChatColor.RESET, Ansi.ansi().a(Attribute.RESET).fg(Ansi.Color.DEFAULT).toString());
        
	}

	
	/**
	 * Formats the message.
	 * 
	 * @param message message
	 * @return formated message
	 */
	public String format(String message) {
       
		
		if(terminal.isAnsiSupported()) {
                
			String result = message;
               
			for (ChatColor color : colors) {
                   
				if (replacements.containsKey(color)) {
					result = result.replaceAll("(?i)" + color.toString(), replacements.get(color));
				} else {
					result = result.replaceAll("(?i)" + color.toString(), "");
				}
                
			}
                
            return result + Ansi.ansi().reset().toString();
                
        } else {
        	
        	return ChatColor.stripColor(message);
        	
        }
		
		
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
	 * Message.
	 * 
	 * @param msg message
	 */
	public static void message(String msg) {

		instance.logger.info(instance.format(msg));
		
	}



    /**
     * Info message.
     * 
     * @param instance instance
     * @param message message
     */
	public static void info(Object instance, String message) {
		info(instance.getClass().getSimpleName() + "{" + instance + "} " + message + ".");
    }
    
    /**
     * Info message.
     * 
     * @param tClass class
     * @param message message
     */
    public static void info(Class<?> tClass, String message) {
    	info(tClass.getSimpleName() + ": " + message + ".");
    }
    
	
    /**
     * Severe message.
     * 
     * @param instance instance
     * @param message message
     */
	public static void severe(Object instance, String message) {
        severe(instance.getClass().getSimpleName() + "{" + instance + "} " + message + ".");
    }
    
    /**
     * Severe message.
     * 
     * @param tClass class
     * @param message message
     */
    public static void severe(Class<?> tClass, String message) {
    	severe(tClass.getSimpleName() + ": " + message + ".");
    }
    

    /**
     * Warning message.
     * 
     * @param instance instance
     * @param message message
     */
    public static void warning(Object instance, String message) {
    	warning(instance.getClass().getSimpleName() + "{" + instance + "} " + message + ".");
    }
    
    /**
     * Warning message.
     * 
     * @param tClass class
     * @param message message
     */
    public static void warning(Class<?> tClass, String message) {
    	warning(tClass.getSimpleName() + ": " + message + ".");
    }


    /**
     * Null field message.
     * 
     * @param instance instance
     * @param field field
     */
	public static void nullField(Object instance, String field) {
        severe(instance.getClass().getSimpleName() + "{" + instance + "} " + field + " field failed to initialise.");
    }
	
	/**
     * Null field message.
     * 
     * @param classs class
     * @param field field
     */
	public static void nullField(Class<?> classs, String field) {
        severe(classs.getSimpleName() + ": field " + field + " failed to initialise.");
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
