package com.olivaris.olivaris_app.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.olivaris.olivaris_app.dto.ErrorDto;
import com.olivaris.olivaris_app.exceptions.UserAlreadyExistsException;

@RestControllerAdvice
public class ErrorResponseHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> userExists(Exception ex) {
        ErrorDto error = new ErrorDto(
            "El usuario ya existe en la base de datos", 
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
