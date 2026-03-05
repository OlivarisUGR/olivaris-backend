package com.olivaris.olivaris_app.exceptions;

public class UserIsEnabledException extends RuntimeException {
    public UserIsEnabledException(String message) {
        super(message);
    }
}
