package com.olivaris.olivaris_app.services;

import org.springframework.http.ResponseEntity;

import com.olivaris.olivaris_app.dto.CreateEntity;
import com.olivaris.olivaris_app.dto.CreateUserEntity;
import com.olivaris.olivaris_app.dto.EntityDto;
import com.olivaris.olivaris_app.dto.UpdateUserEntity;
import com.olivaris.olivaris_app.dto.UserEntityDto;

public interface EntityService {
    ResponseEntity<EntityDto> create(CreateEntity request);

    ResponseEntity<UserEntityDto> createAssignment(
        Long entityId,
        Long userId,
        CreateUserEntity body
    );

    UserEntityDto assignUserToEntity(
        Long entityId,
        Long userId,
        CreateUserEntity body
    );

    ResponseEntity<UserEntityDto> updateUserToEntityData(
        Long entityId,
        Long userId,
        UpdateUserEntity body
    );

    ResponseEntity<Void> delete(Long entityId, Long userId);
}
