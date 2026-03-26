package com.olivaris.olivaris_app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.dto.UserDto;
import com.olivaris.olivaris_app.services.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/api/admin")
@AllArgsConstructor
public class AdminController {

    private final UserService userService;

    // TODO: permitir poner el rol del usuario que quieres crear (basic o admin)
    @PostMapping("/user")
    public ResponseEntity<UserDto> createBasicUser(@Valid @RequestBody RegisterRequest request) {
        return userService.createBasicUser(request);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return userService.delete(id);
    }
}
