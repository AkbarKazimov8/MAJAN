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
public class AjanMathInputException extends Exception {
    
    public AjanMathInputException(final String message) {
        super(message);
    }

    public AjanMathInputException(final String message, final Throwable cause) {
	super(message, cause);
    }

    public AjanMathInputException(final Throwable cause) {
	super(cause);
    } 
    
}
