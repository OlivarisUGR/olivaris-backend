package com.olivaris.olivaris_app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.dto.UserDto;
import com.olivaris.olivaris_app.services.AuthService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(value = "/auth")
@AllArgsConstructor
public class AuthController {

    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }
    


}
