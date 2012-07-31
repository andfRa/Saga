package org.saga.utility.text;

import java.util.ArrayList;

import org.saga.messages.GeneralMessages.CustomColour;
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
	 * Book pages.
	 */
	private ArrayList<String> lines = new ArrayList<String>();
	
	/**
	 * Message colours.
	 */
	private ColourLoop colours;

	
	
	// Initialisation:
	/**
	 * Sets message colour and lines.
	 * 
	 */
	public StringBook(String title, ColourLoop colours) {
		
		this.title = title;
		this.colours = colours;
		
	}
	
	/**
	 * Adds a line.
	 * 
	 * @param line line
	 */
	public void addLine(String line) {

		lines.add(line);
		
	}
	
	/**
	 * Adds a page break.
	 */
	public void nextPage() {

		addLine(PAGE_BREAK);
		
	}
	
	/**
	 * Adds a table.
	 * 
	 * @param table table
	 */
	public void addTable(StringTable table) {

		ArrayList<String> lines = table.getLines();
		for (String line : lines) {
			addLine(line);
		}
		
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
			
			result.append(CustomColour.processMessage(colours.nextColour() + line));
			
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


		// Normalise:
		if(page < 0 ) page = 0;
		if(page  > getLastPage()) page = getLastPage();
		
		// Create page:
		String content = page(page);
		
		return TextUtil.frame(title + " " + (page+1) + "/" + (getLastPage()+1), content, PlayerMessages.normal1);
		
		
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
	
	
	
	// Other:
	public static void main(String[] args) {
		
		
		System.out.println(21 % 10);
		
		
		
	}
	
	
}
