package org.saga.messages.colours;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.saga.messages.PlayerMessages;

public class ColourLoop {

	
	/**
	 * All colours.
	 */
	private ArrayList<ChatColor> colours = new ArrayList<ChatColor>();
	
	/**
	 * Current index.
	 */
	private int index = 0;
	
	
	
	/**
	 * Initialises the colour loop.
	 * 
	 */
	public ColourLoop() {
	}
	
	
	
	/**
	 * Adds a colour to the circle.
	 * 
	 * @param colour colour
	 * @return instance
	 */
	public ColourLoop addColor(ChatColor colour){
		colours.add(colour);
		return this;
	}
	
	/**
	 * Gets the next colour in the loop.
	 * 
	 * @return next colour, {@link PlayerMessages#normal1} if none
	 */
	public ChatColor nextColour() {
		
		
		if(colours.size() == 0) return Colour.normal1;
		
		index++;
		
		if(index >= colours.size()){
			index = 0;
		}
		
		return colours.get(index);
		
		
	}
	
	/**
	 * Gets the first colour in the loop.
	 * 
	 * @return first colour, {@link PlayerMessages#normal1} if none
	 */
	public ChatColor firstColour() {
		
		
		if(colours.size() == 0) return Colour.normal1;
		
		return colours.get(0);
		
		
	}
	
	/**
	 * Resets the loop.
	 * 
	 */
	public void reset() {

		index = 0;

	}
	
	
}
