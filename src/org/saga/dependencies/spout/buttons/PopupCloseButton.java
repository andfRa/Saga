package org.saga.dependencies.spout.buttons;

import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.WidgetAnchor;

public class PopupCloseButton extends GenericButton {

	
	/**
	 * Creates a close button and sets default parameters.
	 * 
	 */
	public PopupCloseButton() {
		
		super("x");
		
		// Defaults:
		setAnchor(WidgetAnchor.BOTTOM_RIGHT);
		setHeight(40);
		setWidth(40);
		
	}
	
	
}
