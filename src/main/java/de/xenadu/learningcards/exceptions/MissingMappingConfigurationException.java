package de.xenadu.learningcards.exceptions;

public class MissingMappingConfigurationException extends RuntimeException {


    public MissingMappingConfigurationException() {
        super("Maybe the mapper uses further beans that must me set manually");
    }

    public MissingMappingConfigurationException(String message) {
        super(message);
    }

    public MissingMappingConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
