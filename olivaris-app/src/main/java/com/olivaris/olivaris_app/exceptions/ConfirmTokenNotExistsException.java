package com.olivaris.olivaris_app.exceptions;

public class ConfirmTokenNotExistsException extends RuntimeException {
    public ConfirmTokenNotExistsException(String token) {
        super("El token de confirmación " + token + " no existe en la base de datos");
    }
}
