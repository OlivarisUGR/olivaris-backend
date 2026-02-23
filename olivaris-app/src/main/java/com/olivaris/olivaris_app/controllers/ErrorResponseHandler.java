package com.olivaris.olivaris_app.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.olivaris.olivaris_app.dto.ErrorDto;
import com.olivaris.olivaris_app.exceptions.AuthHeaderNotValidException;
import com.olivaris.olivaris_app.exceptions.ConfirmTokenNotExistsException;
import com.olivaris.olivaris_app.exceptions.MailSenderException;
import com.olivaris.olivaris_app.exceptions.RoleNotExistsException;
import com.olivaris.olivaris_app.exceptions.TokenExpiredException;
import com.olivaris.olivaris_app.exceptions.UserAlreadyExistsException;
import com.olivaris.olivaris_app.exceptions.UserNotEnabledException;
import com.olivaris.olivaris_app.exceptions.UserNotFoundException;

@RestControllerAdvice
public class ErrorResponseHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> userExists(Exception ex) {
        ErrorDto error = new ErrorDto(
            "El usuario ya existe en la base de datos", 
            ex.getMessage(), 
            HttpStatus.CONFLICT.value()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MailSenderException.class)
    public ResponseEntity<ErrorDto> mailSenderError(Exception ex) {
        ErrorDto error = new ErrorDto(
            "Error durante el envío del email de confirmación", 
            ex.getMessage(), 
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler({
        ConfirmTokenNotExistsException.class,
        RoleNotExistsException.class,
        UserNotFoundException.class
    })
    public ResponseEntity<ErrorDto> entityNotExists(Exception ex) {
        ErrorDto error = new ErrorDto(
            "La entidad no existe en la base de datos", 
            ex.getMessage(), 
            HttpStatus.NOT_FOUND.value()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorDto> tokenExpired(Exception ex) {
        ErrorDto error = new ErrorDto(
            "El token ha expirado", 
            ex.getMessage(), 
            HttpStatus.GONE.value()
        );

        return ResponseEntity.status(HttpStatus.GONE).body(error);
    }

    @ExceptionHandler(UserNotEnabledException.class)
    public ResponseEntity<ErrorDto> notEnabled(Exception ex) {
        ErrorDto error = new ErrorDto(
            "Error en el registro de usuario", 
            ex.getMessage(), 
            HttpStatus.FORBIDDEN.value()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    @ExceptionHandler(AuthHeaderNotValidException.class)
    public ResponseEntity<ErrorDto> authHeaderNotValid(Exception ex) {
        ErrorDto error = new ErrorDto(
            "Error en la cabecera de autorización", 
            ex.getMessage(), 
            HttpStatus.UNAUTHORIZED.value()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorDto> handleMissingHeader(MissingRequestHeaderException ex) {
        ErrorDto error = new ErrorDto(
            "Error en la cabecera de autorización",
            "Falta la cabecera Authorization",
            HttpStatus.UNAUTHORIZED.value()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // This method will manage the validation from input request data
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> argumentNotValid(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> {
           errors.put(error.getField(), error.getDefaultMessage());
        });

        ErrorDto errorDto = new ErrorDto(
            "Error in argument validation",
            errors.toString(),
            HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }
}
