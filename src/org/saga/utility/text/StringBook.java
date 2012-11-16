package org.saga.utility.text;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.saga.messages.PlayerMessages;
import org.saga.messages.PlayerMessages.ColourLoop;

public class StringBook {

	
	/**
	 * Page break.
	 */
	public final static String PAGE_BREAK = "\\p";
	
	
	/**
	 * Book title.
	 */
	private String title;

	/**
	 * Book page width.
	 */
	private double width;
	
	/**
	 * Book lines.
	 */
	private ArrayList<String> lines = new ArrayList<String>();
	
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
	 * @param colours book line colours
	 */
	public StringBook(String title, Double width, ColourLoop colours) {
		
		this.title = title;
		this.width = width;
		this.colours = colours;
		
	}
	
	/**
	 * Creates a book.
	 * 
	 * @param title book title
	 * @param colours book line colours
	 */
	public StringBook(String title, ColourLoop colours) {
		this(title, 1.0, colours);
	}
	
	
	
	// Content:
	/**
	 * Adds line.
	 * 
	 * @param line line
	 * @param allowBreak true if breaking into lines additional lines is allowed
	 */
	public void addLine(String line, boolean allowBreak) {

		
		// Multiple lines:
		if(line.contains("\n")){
			
			String[] lines = line.split("\n");
			for (int i = 0; i < lines.length; i++) {
				addLine(lines[i]);
			}
			
			return;
			
		}
		
		// Long line:
		if(allowBreak && StringFiller.MAX_LENGTH * line.length() > StringFramer.MAX_CONTENTS_WIDTH){
			
			String[] words = line.split(" ");
			StringBuffer text = new StringBuffer();
			double length = 0.0;
			
			for (int i = 0; i < words.length; i++) {
				
				double wordLength = StringFiller.calcLength(words[i]);
				if(i != 0) wordLength+= 1.0;
				
				// Flush:
				if(length + wordLength > StringFramer.MAX_CONTENTS_WIDTH){
					
					lines.add(colours.nextColour() + text.toString());
					
					length = 0.0;
					text = new StringBuffer();
					
				}
				
				// Add words:
				length+= wordLength;
				if(i != 0) text.append(" ");
				text.append(words[i]);
				
			}
			
			// Flush remaining:
			if(length > 0) lines.add(colours.nextColour() + text.toString());
			
		}
		
		// Short line:
		else{
			lines.add(colours.nextColour() + line);
		}
		
		
	}
	
	/**
	 * Adds line. The line can be broken in to additional lines.
	 * 
	 * @param line line
	 */
	public void addLine(String line) {
		addLine(line, true);
	}


	/**
	 * Adds a table to the book.
	 * 
	 * @param table table
	 */
	public void addTable(StringTable table) {

		String[][] contents = table.getTable();
		
		for (int row = 0; row < contents.length; row++) {
			
			StringBuffer line = new StringBuffer();
			ChatColor colour = colours.nextColour();
			
			for (int col = 0; col < contents[row].length; col++) {
				
				line.append(colour + contents[row][col]);
				
			}
			
			lines.add(line.toString());
			
		}
		
	}

	/**
	 * Adds a page break.
	 */
	public void nextPage() {

		lines.add(PAGE_BREAK);
		
	}
	
	
	
	// Creation:
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
		
		for (String line : lines) {
			
			if(line.equals(PAGE_BREAK)){
				currPage++;
				continue;
			}
			
			if(currPage != page) continue;
			if(currPage > page) break;
			
			if(result.length() > 0) result.append("\n");
			
			result.append(line);
			
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
		if(lastPage != 0) title+= ", page " + (page+1) + " of " + (lastPage+1);
		
		return StringFramer.frame(title, content, PlayerMessages.normal1, width);
		
	}

	/**
	 * Gets the last page.
	 * 
	 * @return last page
	 */
	private Integer getLastPage() {
		
		
		Integer last = 0;
		
		for (String line : lines) {
			if(line.equals(PAGE_BREAK)) last++;
		}
		
		return last;
		
		
	}
	
	/**
	 * Gets the line count
	 * 
	 * @return line count
	 */
	public Integer lines() {
		return lines.size();
	}

	
}
