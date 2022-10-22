package de.xenadu.learningcards.exceptions;

import lombok.Getter;

@Getter
public class ApiError {

    private String message;
    private int status;

    public ApiError(String message, int status) {
        this.message = message;
        this.status = status;
    }
}
