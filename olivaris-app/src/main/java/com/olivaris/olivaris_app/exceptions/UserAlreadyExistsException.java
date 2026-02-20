package com.olivaris.olivaris_app.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("El usuario con el email " + email + " ya existe en la base de datos");
    }
}
