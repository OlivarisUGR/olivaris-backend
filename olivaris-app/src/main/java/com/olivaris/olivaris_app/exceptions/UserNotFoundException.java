package com.olivaris.olivaris_app.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("El usuario " + email + " no existe en la base de datos");
    }
}
