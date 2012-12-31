package org.saga.utility.chat;

import org.bukkit.ChatColor;

public class ChatFramer {

	
	/**
	 * Title top left character.
	 */
	public static char TITLE_TOP_LEFT = 'o';
	
	/**
	 * Title top right character.
	 */
	public static char TITLE_TOP_RIGHT = 'o';
	
	/**
	 * Frame top left character.
	 */
	public static char FRAME_TOP_LEFT = 'o';
	
	/**
	 * Frame top right character.
	 */
	public static char FRAME_TOP_RIGHT = 'o';
	
	/**
	 * Frame bottom left character.
	 */
	public static char FRAME_BOTTOM_LEFT = 'o';
	
	/**
	 * Frame bottom right character.
	 */
	public static char FRAME_BOTTOM_RIGHT = 'o';

	/**
	 * Frame horizontal character.
	 */
	public static char FRAME_HORIZONTAL = '-';

	/**
	 * Frame character.
	 */
	public static char FRAME_VERTICAL = '|';
	
	/**
	 * Maximum width of contents.
	 */
	public final static Double MAX_CONTENTS_WIDTH = ChatFiller.CHAT_WIDTH - ChatFiller.calcLength(" " + " " + FRAME_VERTICAL + FRAME_VERTICAL);

	/**
	 * Fix in content length to account for centring.
	 */
	private static Double CONTENT_CENTRING_FIX = -1.0/4.0;
	

	
	// Frame:
	public static String frame(String title, String message, ChatColor colour, double width) {

		
		if(width > 1.0) width = 1.0;
		if(width < 0.0) width = 0.0;
		
		double length = (ChatFiller.CHAT_WIDTH - ChatFiller.calcLength(FRAME_VERTICAL + " " + " " + FRAME_VERTICAL)) * width;

		StringBuffer result = new StringBuffer();
		
		// Frame bottom:
		StringBuffer frameBottom = new StringBuffer();
		while(ChatFiller.calcLength(frameBottom.toString() + FRAME_HORIZONTAL) <= length){
			frameBottom.append(FRAME_HORIZONTAL);
		}
		frameBottom.insert(0, FRAME_BOTTOM_LEFT);
		frameBottom.insert(0, colour);
		frameBottom.append(FRAME_BOTTOM_RIGHT);
		
		// Frame upper:
		StringBuffer frameUp = new StringBuffer();
		while(ChatFiller.calcLength(frameUp.toString() + FRAME_HORIZONTAL) <= length){
			frameUp.append(FRAME_HORIZONTAL);
		}
		frameUp.insert(0, FRAME_TOP_LEFT);
		frameUp.insert(0, colour);
		frameUp.append(FRAME_TOP_RIGHT);
		frameUp.append("\n");

		// Label upper:
		StringBuffer labelUp = new StringBuffer();
		while(ChatFiller.calcLength(labelUp.toString() + FRAME_HORIZONTAL) <= length){
			labelUp.append(FRAME_HORIZONTAL);
		}
		labelUp.insert(0, TITLE_TOP_LEFT);
		labelUp.append(TITLE_TOP_RIGHT);
		labelUp.insert(0, colour);
		labelUp.append("\n");
		
		// Adjust width:
		length = ChatFiller.calcLength(frameBottom.toString()) - ChatFiller.calcLength(" " + " " + FRAME_VERTICAL + FRAME_VERTICAL) + CONTENT_CENTRING_FIX;
		
		// Label:
		title = title.toUpperCase();
		StringBuffer label = new StringBuffer();
		int labelShift = (int)(length/2.0 - ChatFiller.calcLength(title)/2.0);
		label.append(FRAME_VERTICAL + " " + ChatFiller.fillString(ChatFiller.fillString("", (double)labelShift) + title, length) + " " + colour + FRAME_VERTICAL + "\n");
		label.insert(0, colour);
		
		// Content:
		String[] lines = message.split("\n");
		for (int i = 0; i < lines.length; i++) {
			result.append(colour.toString() + FRAME_VERTICAL + " " + ChatFiller.fillString(lines[i], length) + " " + colour + FRAME_VERTICAL + "\n");
		}
		
		// Add up and down:
		result.insert(0, frameUp);
		result.insert(0, label);
		result.insert(0, labelUp);
		result.append(frameBottom);
		
		return ChatFiller.adjustFillers(result.toString());
		
		
	}

	public static String frame(String title, String message, ChatColor color) {

		return frame(title, message, color, 1.0);
		
	}
	

	/**
	 * Enables bonus characters.
	 * 
	 */
	public static void enableBonusCharacters(){
		
//		TITLE_TOP_LEFT = '\u250C';
//		TITLE_TOP_RIGHT = '\u2510';
//		FRAME_TOP_LEFT = '\u251C';
//		FRAME_TOP_RIGHT = '\u2524';
//		FRAME_BOTTOM_LEFT = '\u2514';
//		FRAME_BOTTOM_RIGHT = '\u2518';
		FRAME_HORIZONTAL = '\u2500';
		FRAME_VERTICAL = '\u2502';
		
		TITLE_TOP_LEFT = '\u2554';
		TITLE_TOP_RIGHT = '\u2557';
		FRAME_TOP_LEFT = '\u2560';
		FRAME_TOP_RIGHT = '\u2563';
		FRAME_BOTTOM_LEFT = '\u255A';
		FRAME_BOTTOM_RIGHT = '\u255D';
//		FRAME_HORIZONTAL = '\u2550';
//		FRAME_VERTICAL = '\u2551';

	}
	
	
}
