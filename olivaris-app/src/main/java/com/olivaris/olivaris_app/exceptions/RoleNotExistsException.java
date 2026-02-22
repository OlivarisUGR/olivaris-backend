package com.olivaris.olivaris_app.exceptions;

public class RoleNotExistsException extends RuntimeException {
    public RoleNotExistsException(String roleName) {
        super("El rol " + roleName + " no existe en la base de datos");
    }
}
