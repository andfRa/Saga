package org.saga;

import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListener;


public class SagaServerListener extends ServerListener {

	
	@Override
	public void onServerCommand(ServerCommandEvent event) {
		
		
		// Admin chat:
		String adminCommand = "a ";
		if(event.getCommand().toLowerCase().startsWith(adminCommand)){
			
			String message = event.getCommand().substring(adminCommand.length());
			
			SagaCommands.sendAdminMessage("SERVER", message);
			
		}
		
		
	}
	
	
	
}
