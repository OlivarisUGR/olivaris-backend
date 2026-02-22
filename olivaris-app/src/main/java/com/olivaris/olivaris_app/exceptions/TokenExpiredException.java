package com.olivaris.olivaris_app.exceptions;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String token) {
        super("El token " + token + " ha expirado");
    }
}
