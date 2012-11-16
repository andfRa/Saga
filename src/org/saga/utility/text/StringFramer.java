package org.saga.utility.text;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.server.SharedConstants;

import org.bukkit.ChatColor;

public class StringFramer {

	/**
	 * Title top left character.
	 */
	public static char TITLE_TOP_LEFT = '\0';
	
	/**
	 * Title top right character.
	 */
	public static char TITLE_TOP_RIGHT = '\0';
	
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
	
	
	/**
	 * Maximum width of contents.
	 */
	public final static Double MAX_CONTENTS_WIDTH = StringFiller.CHAT_WIDTH - StringFiller.calcLength(" " + " " + FRAME_VERTICAL + FRAME_VERTICAL);
	

	
	// Frame:
	public static String frame(String title, String message, ChatColor colour, double width) {

		
		if(width > 1.0) width = 1.0;
		if(width < 0.0) width = 0.0;
		
		double length = (StringFiller.CHAT_WIDTH - StringFiller.calcLength(FRAME_VERTICAL + " " + " " + FRAME_VERTICAL)) * width;

		StringBuffer result = new StringBuffer();
		
		// Frame bottom:
		StringBuffer frameBottom = new StringBuffer();
		while(StringFiller.calcLength(frameBottom.toString() + FRAME_HORIZONTAL) <= length){
			frameBottom.append(FRAME_HORIZONTAL);
		}
		frameBottom.insert(0, FRAME_BOTTOM_LEFT);
		frameBottom.insert(0, colour);
		frameBottom.append(FRAME_BOTTOM_RIGHT);
		
		// Frame upper:
		StringBuffer frameUp = new StringBuffer();
		while(StringFiller.calcLength(frameUp.toString() + FRAME_HORIZONTAL) <= length){
			frameUp.append(FRAME_HORIZONTAL);
		}
		frameUp.insert(0, FRAME_TOP_LEFT);
		frameUp.insert(0, colour);
		frameUp.append(FRAME_TOP_RIGHT);
		frameUp.append("\n");

		// Label upper:
		StringBuffer labelUp = new StringBuffer();
		while(StringFiller.calcLength(labelUp.toString() + FRAME_HORIZONTAL) <= length){
			labelUp.append(FRAME_HORIZONTAL);
		}
		labelUp.insert(0, TITLE_TOP_LEFT);
		labelUp.append(TITLE_TOP_RIGHT);
		labelUp.insert(0, colour);
		labelUp.append("\n");
		
		// Adjust width:
		length = StringFiller.calcLength(frameBottom.toString()) - StringFiller.calcLength(" " + " " + FRAME_VERTICAL + FRAME_VERTICAL);
		
		// Label:
		title = title.toUpperCase();
		StringBuffer label = new StringBuffer();
		int labelShift = (int)(length/2.0 - StringFiller.calcLength(title)/2.0);
		label.append(FRAME_VERTICAL + " " + StringFiller.fillString(StringFiller.fillString("", (double)labelShift) + title, length) + " " + colour + FRAME_VERTICAL + "\n");
		label.insert(0, colour);
		
		// Content:
		String[] lines = message.split("\n");
		for (int i = 0; i < lines.length; i++) {
			result.append(colour.toString() + FRAME_VERTICAL + " " + StringFiller.fillString(lines[i], length) + " " + colour + FRAME_VERTICAL + "\n");
		}
		
		// Add up and down:
		result.insert(0, frameUp);
		result.insert(0, label);
		result.insert(0, labelUp);
		result.append(frameBottom);
		
		return StringFiller.adjustFillers(result.toString());
		
		
	}

	public static String frame(String title, String message, ChatColor color) {

		return frame(title, message, color, 1.0);
		
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
		
		TITLE_TOP_LEFT = '\u250C';
		TITLE_TOP_RIGHT = '\u2510';
		FRAME_TOP_LEFT = '\u251C';
		FRAME_TOP_RIGHT = '\u2524';
		FRAME_BOTTOM_LEFT = '\u2514';
		FRAME_BOTTOM_RIGHT = '\u2518';
		FRAME_HORIZONTAL = '\u2500';
		FRAME_VERTICAL = '\u2502';
				
	}
	
	
}
