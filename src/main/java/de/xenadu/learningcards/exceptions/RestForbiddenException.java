package de.xenadu.learningcards.exceptions;

public class RestForbiddenException extends RuntimeException {
    public RestForbiddenException() {
        super("Not allowed");
    }

    public RestForbiddenException(String message) {
        super(message);
    }

    public RestForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
