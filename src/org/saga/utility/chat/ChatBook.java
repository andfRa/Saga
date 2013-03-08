package org.saga.utility.chat;

import java.util.ArrayList;
import java.util.Hashtable;

import org.bukkit.ChatColor;
import org.saga.messages.GeneralMessages;
import org.saga.messages.colours.Colour;
import org.saga.messages.colours.ColourLoop;

public class ChatBook {

	
	/**
	 * Page break.
	 */
	public final static String PAGE_BREAK = "\\p";
	
	
	/**
	 * Table indication.
	 */
	public final static Character TABLE = '\u03a4';
	
	
	/**
	 * Book title.
	 */
	private String title;

	/**
	 * Book page width.
	 */
	private double width;
	
	/**
	 * Section.
	 */
	private ArrayList<String> sections = new ArrayList<String>();
	
	/**
	 * All tables.
	 */
	private Hashtable<String, ChatTable> tables = new Hashtable<String, ChatTable>();
	
	/**
	 * Message colours.
	 */
	private ColourLoop colours;

	
	
	// Initialisation:
	/**
	 * Creates a book.
	 * 
	 * @param title book title
	 * @param width book width of total chat width
	 * @param colours book sect colours
	 */
	public ChatBook(String title, Double width, ColourLoop colours) {
		
		this.title = title;
		this.width = width;
		this.colours = colours;
		
	}
	
	/**
	 * Creates a book.
	 * 
	 * @param title book title
	 * @param colours book sect colours
	 */
	public ChatBook(String title, ColourLoop colours) {
		this(title, 1.0, colours);
	}
	
	
	
	// Fill:
	/**
	 * Adds a section.
	 * 
	 * @param line line
	 * @param allowBreak true if breaking into sections is allowed
	 */
	public void add(String line, boolean allowBreak) {

		
		// Multiple sections:
		if(line.contains("\n")){
			
			String[] sects = line.split("\n");
			for (int i = 0; i < sects.length; i++) {
				add(sects[i]);
			}
			
			return;
			
		}
		
		// Long sections:
		if(allowBreak && ChatFiller.MAX_LENGTH * line.length() > ChatFramer.MAX_CONTENTS_WIDTH){
			
			String[] words = line.split(" ");
			StringBuffer text = new StringBuffer();
			double length = 0.0;
			
			for (int i = 0; i < words.length; i++) {
				
				double wordLength = ChatFiller.calcLength(words[i]);
				if(i != 0) wordLength+= 1.0;
				
				// Flush:
				if(length + wordLength > ChatFramer.MAX_CONTENTS_WIDTH){
					
					sections.add(colours.nextColour() + text.toString());
					
					length = 0.0;
					text = new StringBuffer();
					
				}
				
				// Add words:
				length+= wordLength;
				if(i != 0) text.append(" ");
				text.append(words[i]);
				
			}
			
			// Flush remaining:
			if(length > 0) sections.add(colours.nextColour() + text.toString());
			
		}
		
		// Short sect:
		else{
			sections.add(colours.nextColour() + line);
		}
		
		
	}
	
	/**
	 * Adds a section.
	 * 
	 * @param sect section
	 */
	public void add(String sect) {
		add(sect, true);
	}
	
	/**
	 * Adds a table to the book.
	 * 
	 * @param table table
	 */
	public void add(ChatTable table) {

		int index = tables.size();
		String key = TABLE.toString() + index;
		
		sections.add(key);
		tables.put(key, table);
		
	}

	/**
	 * Adds a page break.
	 */
	public void nextPage() {

		sections.add(PAGE_BREAK);
		
	}
	
	
	
	// Create:
	/**
	 * Creates a page.
	 * 
	 * @param page page number
	 * @return page
	 */
	public String page(Integer page) {

		
		// Normalise:
		if(page < 0 ) page = 0;
		if(page  > getLastPage()) page = getLastPage();
		
		// Create page:
		StringBuffer result = new StringBuffer();
		
		int currPage = 0;
		
		for (String sect : sections) {
			
			if(sect.equals(PAGE_BREAK)){
				currPage++;
				continue;
			}
			
			if(currPage != page) continue;
			if(currPage > page) break;
			
			if(result.length() > 0) result.append("\n");
			
			// Table:
			if(sect.startsWith(TABLE.toString())){
				
				ChatTable table = tables.get(sect);
				if(table == null) result.append(sect);
				result.append(createTable(table));
				
			}
			// Section:
			else{
				result.append(sect);
			}
			
		}
		
		return result.toString();
		
		
	}
	
	/**
	 * Creates a framed page.
	 * 
	 * @param page page number
	 * @param width width
	 * @return framed page
	 */
	public String framedPage(Integer page) {

		// Trim:
		if(page < 0 ) page = 0;
		if(page  > getLastPage()) page = getLastPage();
		
		// Create page:
		String content = page(page);
		
		String title = this.title;
		int lastPage = getLastPage();
		if(lastPage != 0) title+= ", " + GeneralMessages.page(page, lastPage);
		
		return ChatFramer.frame(title, content, Colour.frame, width);
		
	}
	
	/**
	 * Gets the section count
	 * 
	 * @return section count
	 */
	public Integer sections() {
		return sections.size();
	}
	
	/**
	 * Gets the last page.
	 * 
	 * @return last page
	 */
	private Integer getLastPage() {
		
		
		Integer last = 0;
		
		for (String sect : sections) {
			if(sect.equals(PAGE_BREAK)) last++;
		}
		
		return last;
		
		
	}
	
	/**
	 * Converts a table to a String.
	 * 
	 * @param table table
	 * @return table in string form
	 */
	private String createTable(ChatTable table) {
		
		
		StringBuffer rows = new StringBuffer();
		
		String[][] contents = table.getContents();
		
		for (int row = 0; row < contents.length; row++) {
			
			StringBuffer sect = new StringBuffer();
			ChatColor colour = colours.nextColour();
			
			for (int col = 0; col < contents[row].length; col++) {
				
				sect.append(colour + contents[row][col]);
				
			}
			
			if(row != 0) rows.append("\n");
			rows.append(sect.toString());
			
		}
		
		return rows.toString();
		
		
	}
	
	
	// Elements:
	/**
	 * Gets the title.
	 * 
	 * @return title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Gets a table with the given key.
	 * 
	 * @param key key, format '{@link #TABLE} + integer'
	 * @return
	 */
	public ChatTable getTable(String key) {
		return tables.get(key);
	}
	
	/**
	 * Gets a section.
	 * 
	 * @param index index
	 * @return section
	 * @throws IndexOutOfBoundsException if index is out of bounds
	 */
	public String getSection(int index) {
		return sections.get(index);
	}
	
	
}
