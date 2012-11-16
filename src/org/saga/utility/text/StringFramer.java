package org.saga.utility.text;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.server.SharedConstants;

import org.bukkit.ChatColor;

public class StringFramer {

	/**
	 * Frame top left character.
	 */
	public static char FRAME_TOP_LEFT = '\0';
	
	/**
	 * Frame top right character.
	 */
	public static char FRAME_TOP_RIGHT = '\0';
	
	/**
	 * Frame bottom left character.
	 */
	public static char FRAME_BOTTOM_LEFT = '\0';
	
	/**
	 * Frame bottom right character.
	 */
	public static char FRAME_BOTTOM_RIGHT = '\0';

	/**
	 * Frame horizontal character.
	 */
	public static char FRAME_HORIZONTAL = '-';

	/**
	 * Frame character.
	 */
	public static char FRAME_VERTICAL = '-';
	
	

	// Frame:
	public static String frame(String title, String message, ChatColor colour, double width) {

		
		if(width > 1.0) width = 1.0;
		if(width < 0.0) width = 0.0;
		
		double length = 78.5*width;

		StringBuffer result = new StringBuffer();
		
		// Lower:
		StringBuffer down = new StringBuffer();
		while(StringFiller.calcLength(down.toString() + FRAME_HORIZONTAL) < length){
			down.append(FRAME_HORIZONTAL);
		}
		down.insert(0, FRAME_BOTTOM_LEFT);
		down.insert(0, colour);
		down.append(FRAME_BOTTOM_RIGHT);
		
		// Upper:
		StringBuffer up = new StringBuffer();
		while(StringFiller.calcLength(up.toString() + FRAME_HORIZONTAL) < length){
			up.append(FRAME_HORIZONTAL);
		}
		up.insert(0, FRAME_TOP_LEFT);
		up.insert(0, colour);
		up.append(FRAME_TOP_RIGHT);
		up.append("\n");

		// Adjust width:
		length = StringFiller.calcLength(down.toString()) - StringFiller.calcLength(" " + " " + FRAME_VERTICAL + FRAME_VERTICAL);
		
		// Label:
		StringBuffer label = new StringBuffer();
		label.append("=[ " + title.toUpperCase() + " ]=" + "\n");
		int labelShift = (int)(length/2.0 - StringFiller.calcLength(label.toString())/2.0);
		label.insert(0, StringFiller.fillString("", (double)labelShift));
		label.insert(0, colour);
		
		// Content:
		String[] lines = message.split("\n");
		for (int i = 0; i < lines.length; i++) {
			result.append(colour.toString() + FRAME_VERTICAL + " " + StringFiller.fillString(lines[i], length) + " " + colour + FRAME_VERTICAL + "\n");
		}
		
		// Add up and down:
		result.insert(0, up);
		result.insert(0, label);
		result.append(down);
		
		return StringFiller.adjustFillers(result.toString());
		
		
	}

	public static String frame(String title, String message, ChatColor color) {

		return frame(title, message, color, 1.0);
		
	}
	
	public static String smallFrame(String title, String message, ChatColor colour) {

		return frame(title, message, colour, 0.75);
		
	}
	
	

	/**
	 * Enables bonus characters.
	 * http://forums.bukkit.org/threads/printing-special-characters-%E2%99%A0-%E2%99%A3-%E2%99%A5-%E2%99%A6-in-chat.72293/
	 * thanks, Father Of Time
	 * 
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void enableBonusCharacters() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		
		Field field = SharedConstants.class.getDeclaredField("allowedCharacters");
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField( "modifiers" );
		modifiersField.setAccessible( true );
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		String oldallowedchars = (String)field.get(null);
		String custom = "\u2500\u2510\u2514\u2518\u250C\u2502";
		StringBuilder sb = new StringBuilder();
		sb.append(oldallowedchars);
		sb.append(custom);
		field.set( null, sb.toString() );
		
		FRAME_TOP_LEFT = '\u250C';
		FRAME_TOP_RIGHT = '\u2510';
		FRAME_BOTTOM_LEFT = '\u2514';
		FRAME_BOTTOM_RIGHT = '\u2518';
		FRAME_HORIZONTAL = '\u2500';
		FRAME_VERTICAL = '\u2502';
				
	}
	
	
}
