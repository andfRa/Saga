package org.saga.utility;

import java.util.ArrayList;

import org.saga.messages.PlayerMessages;
import org.saga.messages.PlayerMessages.ColorCircle;

public class StringBook {

	/**
	 * Book title.
	 */
	private String title;
	
	/**
	 * Book pages.
	 */
	private ArrayList<String> lines = new ArrayList<String>();
	
	/**
	 * Message colors.
	 */
	private ColorCircle messageColor;

	/**
	 * Page size.
	 */
	private int pageSize;
	
	
	/**
	 * Sets message color and lines.
	 * 
	 */
	public StringBook(String title, ColorCircle messageColor, int pageSize) {
		
		this.title = title;
		this.messageColor = messageColor;
		this.pageSize = pageSize;
		
		if(pageSize <= 0) pageSize = 1;
		
	}
	
	/**
	 * Adds a line.
	 * 
	 * @param page page
	 */
	public void addLine(String page) {

		lines.add(page);
		
	}
	
	/**
	 * Adds a page break.
	 */
	public void nextPage() {

		while(lines.size() % pageSize != 0){
			addLine("");
		}
		
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
	
	
	/**
	 * Creates a page.
	 * 
	 * @param page page number
	 * @return page
	 */
	public String createPage(Integer page) {

		
		// Normalize:
		if(page < 0 ) page = 0;
		if(page  > getLastPage()) page = getLastPage();
		
		// Create page:
		StringBuffer rString = new StringBuffer();
		
		int linesLeft = pageSize;
		int currentLine = page * pageSize;
		boolean firstLine = true;
		while (linesLeft > 0 && currentLine < lines.size()) {
			
			if(firstLine){
				firstLine = false;
			}else{
				rString.append("\n");
			}
			
			rString.append(messageColor.nextColor());
			rString.append(lines.get(currentLine));
			
			linesLeft--;
			currentLine ++;
			
		}
		
		return rString.toString();
		
		
	}
	
	/**
	 * Creates a framed page.
	 * 
	 * @param page page number
	 * @param width width
	 * @return framed page
	 */
	public String framed(Integer page, Double width) {


		// Normalize:
		if(page < 0 ) page = 0;
		if(page  > getLastPage()) page = getLastPage();
		
		// Create page:
		String content = createPage(page);
		
		return TextUtil.frame(title + " " + (page+1) + "/" + (getLastPage()+1), PlayerMessages.normal1, content, width);
		
		
	}

	/**
	 * Creates a framed page.
	 * 
	 * @param page page number
	 * @return framed page
	 */
	public String framed(Integer page) {

		return framed(page, 70.0);
		
	}
	
	/**
	 * Gets the last page.
	 * 
	 * @return last page
	 */
	private Integer getLastPage() {
		return new Double(Math.ceil( (lines.size() -1) / pageSize)).intValue();
	}
	
	/**
	 * Gets the line count
	 * 
	 * @return line count
	 */
	public Integer lines() {
		return lines.size();
	}
	
	
	public static void main(String[] args) {
		
		
		System.out.println(21 % 10);
		
		
		
	}
	
	
}
