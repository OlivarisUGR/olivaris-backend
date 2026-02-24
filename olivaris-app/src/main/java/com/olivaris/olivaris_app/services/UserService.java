package com.olivaris.olivaris_app.services;

import org.springframework.http.ResponseEntity;

import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.dto.UserDto;
import com.olivaris.olivaris_app.models.User;

public interface UserService {
    User register(RegisterRequest request);
    ResponseEntity<UserDto> create(RegisterRequest request);
    ResponseEntity<Void> delete(Long id);
}
