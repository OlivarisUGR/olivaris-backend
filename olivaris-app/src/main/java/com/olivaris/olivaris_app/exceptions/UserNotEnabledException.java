package com.olivaris.olivaris_app.exceptions;

public class UserNotEnabledException extends RuntimeException {
    public UserNotEnabledException(String email) {
        super("El usuario " + email + " no ha confirmado su registro");
    }
}
