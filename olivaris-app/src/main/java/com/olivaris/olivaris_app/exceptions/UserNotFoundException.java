package com.olivaris.olivaris_app.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String id) {
        super("El usuario " + id + " no existe en la base de datos");
    }
}
