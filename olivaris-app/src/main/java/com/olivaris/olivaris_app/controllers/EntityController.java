package com.olivaris.olivaris_app.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olivaris.olivaris_app.dto.CreateEntity;
import com.olivaris.olivaris_app.dto.CreateUserEntity;
import com.olivaris.olivaris_app.dto.EntityDto;
import com.olivaris.olivaris_app.dto.UpdateUserEntity;
import com.olivaris.olivaris_app.dto.UserEntityDto;
import com.olivaris.olivaris_app.services.EntityService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/api/entity")
@AllArgsConstructor
public class EntityController {

    private final EntityService entityServ;

    @PostMapping("/")
    public ResponseEntity<EntityDto> create(@Valid @RequestBody CreateEntity request) {
        return entityServ.create(request);
    }

    @PostMapping("/{entityId}")
    public ResponseEntity<UserEntityDto> assignUser(
        @PathVariable Long entityId,
        @Valid @RequestBody CreateUserEntity body
    ) {
        return entityServ.createAssignment(entityId, body);
    }

    @PutMapping("/{entityId}/user/{userId}")
    public ResponseEntity<UserEntityDto> updateEntityUserData(
        @PathVariable Long entityId,
        @PathVariable Long userId,
        @Valid @RequestBody UpdateUserEntity body
    ) {
        return entityServ.updateUserToEntityData(entityId, userId, body);
    }

    @DeleteMapping("/{entityId}/user/{userId}")
    public ResponseEntity<Void> deleteAssignment(
        @PathVariable Long entityId,
        @PathVariable Long userId
    ) {
        return entityServ.delete(entityId, userId);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EntityDto>> getUserEntities(@PathVariable Long userId) {
        return entityServ.getUserEntities(userId);
    }
}
