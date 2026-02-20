package com.olivaris.olivaris_app.services;

import org.springframework.http.ResponseEntity;

import com.olivaris.olivaris_app.dto.RegisterRequest;

public interface AuthService {
    ResponseEntity<?> register(RegisterRequest request);
}
