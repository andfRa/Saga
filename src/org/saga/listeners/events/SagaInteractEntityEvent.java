package org.saga.listeners.events;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.saga.Saga;
import org.saga.SagaLogger;
import org.saga.player.SagaPlayer;

public class SagaInteractEntityEvent {

	
	/**
	 * Saga player.
	 */
	private SagaPlayer sagaPlayer;
	
	/**
	 * Target player.
	 */
	private SagaPlayer targetSagaPlayer = null;
	
	/**
	 * Target creature.
	 */
	private Creature targetCreature = null;
	
	
	/**
	 * Sets targets
	 * 
	 * @param event event
	 * @param sagaPlayer saga player
	 */
	public SagaInteractEntityEvent(PlayerInteractEntityEvent event, SagaPlayer sagaPlayer) {


    	this.sagaPlayer = sagaPlayer;

    	// Target player:
    	if(event.getRightClicked() instanceof Player){
    		
    		SagaPlayer targetSagaPlayer = Saga.plugin().getLoadedPlayer(((Player) event.getRightClicked()).getName());
        	if(targetSagaPlayer == null){
        		SagaLogger.warning(getClass(), "failed to retrieve saga player for " + ((Player) event.getRightClicked()).getName());
        		return;
        	}

    	}else if(event.getRightClicked() instanceof Creature){
    		targetCreature = (Creature) event.getRightClicked();
    	}
		

	}
	
	
	/**
	 * Checks if the interaction was with a creature.
	 * 
	 * @return true if creature
	 */
	public boolean isInteractPlayer() {

		return targetSagaPlayer != null;

	}
	
	/**
	 * Checks if the interaction was with a creature.
	 * 
	 * @return true if creature
	 */
	public boolean isInteractCreature() {

		return targetCreature != null;

	}


	
	
	/**
	 * Gets the sagaPlayer.
	 * 
	 * @return the sagaPlayer
	 */
	public SagaPlayer getSagaPlayer() {
	
	
		return sagaPlayer;
	}


	
	/**
	 * Gets the targetSagaPlayer.
	 * 
	 * @return the targetSagaPlayer
	 */
	public SagaPlayer getTargetSagaPlayer() {
	
	
		return targetSagaPlayer;
	}


	
	/**
	 * Gets the targetCreature.
	 * 
	 * @return the targetCreature
	 */
	public Creature getTargetCreature() {
	
	
		return targetCreature;
	}
	
	
}
