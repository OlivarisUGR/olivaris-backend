package com.olivaris.olivaris_app.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.olivaris.olivaris_app.dto.LoginRequest;
import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.dto.TokenResponse;
import com.olivaris.olivaris_app.dto.UserDto;
import com.olivaris.olivaris_app.services.AuthService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping(value = "/api/auth")
@AllArgsConstructor
public class AuthController {

    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }
    
    @GetMapping("/confirm")
    public ResponseEntity<UserDto> confirm(@RequestParam String token) {
        return authService.confirm(token);
    }

    @GetMapping("/confirmEntityAdmin")
    public ResponseEntity<UserDto> confirmEntityAdmin(
        @RequestParam String token,
        @RequestParam Long entityId
    ) {
        return authService.confirmEntityAdmin(token, entityId);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return authService.refresh(authHeader);
    }
}
