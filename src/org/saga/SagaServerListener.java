package org.saga;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;


public class SagaServerListener implements Listener {

	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onServerCommand(ServerCommandEvent event) {
		
		
		// Admin chat:
		String adminCommand = "a ";
		if(event.getCommand().toLowerCase().startsWith(adminCommand)){
			
			String message = event.getCommand().substring(adminCommand.length());
			
			SagaCommands.sendAdminMessage("SERVER", message);
			
		}
		
		
	}
	
	
	
}
