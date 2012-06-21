package org.saga.dependencies.spout.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.PopupScreen;
import org.saga.dependencies.spout.buttons.PopupCloseButton;

public class SpoutScreenListener implements Listener{

	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onButtonPress(ButtonClickEvent event) {
	
		
		// Close button:
		if(event.getButton() instanceof PopupCloseButton){
			
			PopupScreen popup = event.getPlayer().getMainScreen().getActivePopup();
			if(popup != null) popup.close();
			
			return;
			
		}

	}
	
}
