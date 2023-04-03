package de.xenadu.learningcards.exceptions;

/**
 * Thrown by persistence or service layer, when the entity was not found in DB.
 */
public class EntityNotFoundException extends RestNotFoundException {

    public EntityNotFoundException() {
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
