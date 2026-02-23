package com.olivaris.olivaris_app.exceptions;

public class AuthHeaderNotValidException extends RuntimeException {
    public AuthHeaderNotValidException(String message) {
        super(message);
    }
}
