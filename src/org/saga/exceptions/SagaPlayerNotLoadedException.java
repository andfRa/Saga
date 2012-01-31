/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.saga.exceptions;

/**
 *
 * @author Cory
 */
public class SagaPlayerNotLoadedException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String playerName;

    public SagaPlayerNotLoadedException(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

}
