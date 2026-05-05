package com.olivaris.olivaris_app.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.olivaris.olivaris_app.dto.ErrorDto;
import com.olivaris.olivaris_app.exceptions.ActivityException;
import com.olivaris.olivaris_app.exceptions.AuthHeaderNotValidException;
import com.olivaris.olivaris_app.exceptions.ConfirmTokenNotExistsException;
import com.olivaris.olivaris_app.exceptions.EntityExistsException;
import com.olivaris.olivaris_app.exceptions.FieldIsNecessaryException;
import com.olivaris.olivaris_app.exceptions.MailSenderException;
import com.olivaris.olivaris_app.exceptions.PhytoActArgumentException;
import com.olivaris.olivaris_app.exceptions.RoleNotExistsException;
import com.olivaris.olivaris_app.exceptions.TokenExpiredException;
import com.olivaris.olivaris_app.exceptions.UserAlreadyExistsException;
import com.olivaris.olivaris_app.exceptions.UserIsEnabledException;
import com.olivaris.olivaris_app.exceptions.UserNotEnabledException;
import com.olivaris.olivaris_app.exceptions.UserNotFoundException;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ErrorResponseHandler {

    @ExceptionHandler({
        UserAlreadyExistsException.class,
        UserIsEnabledException.class
    })
    public ResponseEntity<ErrorDto> userException(Exception ex) {
        ErrorDto error = new ErrorDto(
            "Excepción relacionada con el estado del usuario almacenado", 
            ex.getMessage(), 
            HttpStatus.CONFLICT.value()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDto> integrityViolation(Exception ex) {
        ErrorDto error = new ErrorDto(
            "Error al ejecutar una operación SQL", 
            ex.getMessage(), 
            HttpStatus.CONFLICT.value()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> messageNotReadable(Exception ex) {
        ErrorDto error = new ErrorDto(
            "Error en la lectura del mensaje", 
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
        UserNotFoundException.class,
        EntityNotFoundException.class
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

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ErrorDto> entityExists(Exception ex) {
        ErrorDto error = new ErrorDto(
            "Entidad existente en el sistema", 
            ex.getMessage(), 
            HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ActivityException.class)
    public ResponseEntity<ErrorDto> activity(Exception ex) {
        ErrorDto error = new ErrorDto(
            "Error al trabajar con la actividad", 
            ex.getMessage(), 
            HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
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

    @ExceptionHandler({
        AccessDeniedException.class,
        ExpiredJwtException.class
    })
    public ResponseEntity<ErrorDto> accessDenied(Exception ex) {
        ErrorDto error = new ErrorDto(
            "Acceso denegado al servicio",
            ex.getMessage(),
            HttpStatus.FORBIDDEN.value()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler({
        AuthenticationException.class,
        BadCredentialsException.class
    })
    public ResponseEntity<ErrorDto> authenticationError(Exception ex) {
        ErrorDto error = new ErrorDto(
            "Error de autenticación",
            "Usuario o contraseña inválidos",
            HttpStatus.UNAUTHORIZED.value()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(FieldIsNecessaryException.class)
    public ResponseEntity<ErrorDto> fieldIsNecessary(Exception ex) {
        ErrorDto error = new ErrorDto(
            "Campo necesario en el body de la petición", 
            ex.getMessage(), 
            HttpStatus.UNAUTHORIZED.value()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDto> runtimeException(Exception ex) {
        ErrorDto error = new ErrorDto(
            "Error durante la ejecución de una funcionalidad", 
            ex.getMessage(), 
            HttpStatus.SERVICE_UNAVAILABLE.value()
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler({
        IllegalArgumentException.class,
        PhytoActArgumentException.class
    })
    public ResponseEntity<ErrorDto> illegalArgument(Exception ex) {
        ErrorDto error = new ErrorDto(
            "Dato pasado no válido", 
            ex.getMessage(), 
            HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
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
