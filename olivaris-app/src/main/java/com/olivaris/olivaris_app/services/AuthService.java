package com.olivaris.olivaris_app.services;

import org.springframework.http.ResponseEntity;

import com.olivaris.olivaris_app.dto.LoginRequest;
import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.dto.TokenResponse;
import com.olivaris.olivaris_app.dto.UserDto;

public interface AuthService {
    ResponseEntity<UserDto> register(RegisterRequest request);
    ResponseEntity<UserDto> confirm(String confirmToken);
    ResponseEntity<TokenResponse> login(LoginRequest request);
}
