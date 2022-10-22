package de.xenadu.learningcards.exceptions;

public class RestBadRequestException extends RuntimeException {

    public RestBadRequestException() {
    }

    public RestBadRequestException(String message) {
        super(message);
    }

    public RestBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
