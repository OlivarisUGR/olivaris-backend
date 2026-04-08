package com.olivaris.olivaris_app.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.olivaris.olivaris_app.dto.UserDto;
import com.olivaris.olivaris_app.services.UserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        return userService.getCurrentUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping("")
    public ResponseEntity<UserDto> getByEmail(
        @RequestParam(required = true) String email
    ) {
        return userService.getByEmail(email);
    }

    @GetMapping("/all/entity/{entityId}")
    public ResponseEntity<List<UserDto>> getEntityUsers(@PathVariable Long entityId) {
        return userService.getEntityUsers(entityId);
    }
}
