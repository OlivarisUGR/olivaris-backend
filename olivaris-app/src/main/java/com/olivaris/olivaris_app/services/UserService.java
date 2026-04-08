package com.olivaris.olivaris_app.services;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.dto.UserDto;
import com.olivaris.olivaris_app.models.User;

public interface UserService {
    User register(RegisterRequest request);
    ResponseEntity<UserDto> createSystemUser(RegisterRequest request);
    ResponseEntity<Void> delete(Long id);
    ResponseEntity<UserDto> getById(Long id);
    ResponseEntity<UserDto> getByEmail(String email);
    ResponseEntity<UserDto> getCurrentUser();
    ResponseEntity<List<UserDto>> getEntityUsers(Long entityId);
}
