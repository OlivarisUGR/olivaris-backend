package com.olivaris.olivaris_app.services;

import java.util.List;

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
        CreateUserEntity body
    );

    UserEntityDto assignUserToEntity(
        Long entityId,
        CreateUserEntity body
    );

    ResponseEntity<UserEntityDto> updateUserToEntityData(
        Long entityId,
        Long userId,
        UpdateUserEntity body
    );

    ResponseEntity<Void> delete(Long entityId, Long userId);
    ResponseEntity<List<EntityDto>> getUserEntities(Long userId);
}
