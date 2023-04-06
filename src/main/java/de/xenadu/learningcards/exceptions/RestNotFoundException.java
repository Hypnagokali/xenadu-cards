package de.xenadu.learningcards.exceptions;

/**
 * Simple not found Exception.
 */
public class RestNotFoundException extends RuntimeException {

    public RestNotFoundException() {
    }

    public RestNotFoundException(String message) {
        super(message);
    }
}
