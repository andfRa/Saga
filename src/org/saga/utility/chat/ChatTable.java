package org.saga.utility.chat;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.saga.messages.colours.ColourLoop;

public class ChatTable {

	
	/**
	 * Book pages.
	 */
	public ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();
	
	/**
	 * Selected column.
	 */
	private int columnIndex;
	
	/**
	 * Message colours.
	 */
	private ColourLoop colours;

	/**
	 * Custom column widths.
	 */
	private ArrayList<Double> customWidths = null;

	
	
	// Initialisation:
	/**
	 * Sets message colour and lines.
	 * 
	 */
	public ChatTable(ColourLoop colours) {

		this.colours = colours;
		
		this.columnIndex = 0;
		table.add(new ArrayList<String>());
		
	}
	
	
	
	// Size:
	/**
	 * Gets a column width.
	 * 
	 * @param i column index
	 * @return column width
	 */
	private Double getColumnWidth(int i) {

		if(customWidths == null || i >= customWidths.size() || i < 0) return ChatFiller.CHAT_WIDTH / table.size();
		
		return customWidths.get(i);
		
	}
	
	/**
	 * Gets column widths.
	 * 
	 * @return column widths
	 */
	public Double[] getColumnWidths() {

		Double[] widths = new Double[table.size()];
		
		for (int i = 0; i < widths.length; i++) {
			widths[i] = getColumnWidth(i);
		}
		
		return widths;
		
	}
	
	/**
	 * Sets custom widths.
	 * 
	 * @param customWidths custom widths
	 */
	public void setCustomWidths(ArrayList<Double> customWidths) {
		this.customWidths = customWidths;
	}
	
	/**
	 * Sets custom widths.
	 * 
	 * @param customWidths custom widths
	 */
	public void setCustomWidths(Double[] customWidths) {
		
		this.customWidths = new ArrayList<Double>();
		
		for (int i = 0; i < customWidths.length; i++) {
			this.customWidths.add(customWidths[i]);
		}
		
	}
	
	/**
	 * Sets custom width.
	 * 
	 * @param customWidth custom width
	 */
	public void setCustomWidths(Double customWidth) {
		
		customWidths = new ArrayList<Double>();
		
		for (int i = 0; i <= table.size(); i++) {
			customWidths.add(customWidth/table.size());
		}

	}
	
	/**
	 * Calculates total width.
	 * 
	 * @return total width
	 */
	public Double calcTotalWidth() {

		Double[] widths = getColumnWidths();
		
		Double width = 0.0;
		for (int i = 0; i < widths.length; i++) {
			width += widths[i];
		}
		
		return width;
		
	}
	
	/**
	 * Collapses the tables width to optimal value
	 * 
	 */
	public void collapse() {

		
		customWidths = new ArrayList<Double>();
		
		for (ArrayList<String> column : table) {
			
			double maxWidth = 0;
			
			for (String cell : column) {
				
				Double width = ChatFiller.calcLength(cell);

				if(width > maxWidth) maxWidth = width;
				
			}
			
			customWidths.add(maxWidth + 2);
			
		}
		
		
	}
	
	
	
	// Contents:
	/**
	 * Adds a line to a column.
	 * 
	 * @param line line
	 */
	public void addLine(String line) {
		
		ArrayList<String> column = table.get(columnIndex);
		
		column.add(line);
		
	}

	/**
	 * Adds a line to a column.
	 * 
	 * @param line line
	 * @param index column index
	 */
	public void addLine(String line, Integer index) {
		
		while(table.size() <= index){
			table.add(new ArrayList<String>());
		}
		
		ArrayList<String> column = table.get(index);
		
		column.add(line);
		
	}

	/**
	 * Adds a line to a column.
	 * 
	 * @param value1 first value
	 * @param value2 second value
	 * @param index column index
	 */
	public void addLine(String value1, String value2, Integer index) {
		
		addLine(value1, index);
		addLine(value2, index + 1);
		
	}

	/**
	 * Adds a line to a column.
	 * 
	 * @param line line
	 */
	public void addLine(String[] lines) {
		
		for (int i = 0; i < lines.length; i++) {
		
			addLine(lines[i], i);
			
		}
		
	}
	
	/**
	 * Gets the table size.
	 * 
	 * @return table size
	 */
	public int size() {

		int size = 0;
		for (ArrayList<String> column : table) {
			size += column.size();
		}
		
		return size;
		
	}

	/**
	 * Gets table lines.
	 * 
	 * @return table lines
	 */
	public ArrayList<String> getLines() {
		

		Double[] widths = getColumnWidths();
		
		ArrayList<String> result = new ArrayList<String>();
		
		// Table size:
		int rows = 0;
		for (ArrayList<String> column : table) {
			if(column.size() > rows) rows = column.size();
		}
		
		// Create table:
		for (int rowind = 0; rowind < rows; rowind++) {
			
			String row = "";
			
			// All columns:
			for (int colind = 0; colind < table.size(); colind++) {
				
				ArrayList<String> column = table.get(colind);
				
				if(rowind < column.size()){
					
					row += ChatFiller.fillString(column.get(rowind), widths[colind]);
					
				}else{
					
					row += ChatFiller.fillString("", widths[colind]);
					
				}
				
			}
			
			result.add(row);
			
		}
		
		return result;
		
		
	}
	
	/**
	 * Gets all cells with correct widths.
	 * 
	 * @return table
	 */
	public String[][] getTable() {
		
		
		int rows = 0;
		int cols = table.size();
		
		for (ArrayList<String> column : table) {
			if(rows < column.size()) rows = column.size();
		}
		
		String[][] result = new String[rows][cols];
		
		for (int col = 0; col < cols; col++) {
			
			double width = getColumnWidth(col);
			
			for (int row = 0; row < rows; row++) {
				
				if(row < table.get(col).size()){
					result[row][col] = ChatFiller.fillString(table.get(col).get(row), width);
				}else{
					result[row][col] = ChatFiller.fillString("", width);
				}
				
			}
			
		}
		
		return result;
		
		
	}
	
	
	
	// Creation:
	/**
	 * Creates a table from the data.
	 * 
	 * @return table
	 */
	public String createTable() {

		Double[] widths = getColumnWidths();
		
		StringBuffer result = new StringBuffer();
		
		// Table size:
		int rows = 0;
		for (ArrayList<String> column : table) {
			if(column.size() > rows) rows = column.size();
		}
		
		// Create table:
		for (int row = 0; row < rows; row++) {
			
			// Next row:
			if(row != 0) result.append("\n");
			
			// All columns:
			ChatColor elementColor = colours.nextColour();
			for (int colInd = 0; colInd < table.size(); colInd++) {
				
				ArrayList<String> column = table.get(colInd);
				
				if(row < column.size()){
					
					result.append(elementColor);
					result.append(ChatFiller.fillString(column.get(row), widths[colInd]));
					
				}else{
					
					result.append(ChatFiller.fillString("", widths[colInd]));
					
				}
				
			}
			
		}
		
		return result.toString();
		
		
	}
	
	
}
