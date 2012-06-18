package org.saga.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import org.saga.commands.AdminCommands;


public class ServerListener implements Listener {

	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onServerCommand(ServerCommandEvent event) {
		
		
		String adminCommand = "a ";
		
		// Administrator chat:
		if(event.getCommand().toLowerCase().startsWith(adminCommand)){
			
			String message = event.getCommand().substring(adminCommand.length());
			
			AdminCommands.chatServer(message);
			
		}
		
		
	}
	
	
	
}
