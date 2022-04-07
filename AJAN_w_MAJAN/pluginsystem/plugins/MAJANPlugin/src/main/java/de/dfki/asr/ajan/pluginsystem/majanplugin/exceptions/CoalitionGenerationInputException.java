/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.asr.ajan.pluginsystem.majanplugin.exceptions;

/**
 *
 * @author akka02
 */
public class CoalitionGenerationInputException extends Exception {
    
    public CoalitionGenerationInputException(final String message) {
        super(message);
    }

    public CoalitionGenerationInputException(final String message, final Throwable cause) {
	super(message, cause);
    }

    public CoalitionGenerationInputException(final Throwable cause) {
	super(cause);
    } 
    
}
