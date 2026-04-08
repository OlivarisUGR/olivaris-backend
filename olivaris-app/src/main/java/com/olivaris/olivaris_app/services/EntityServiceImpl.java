package com.olivaris.olivaris_app.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olivaris.olivaris_app.dto.CreateEntity;
import com.olivaris.olivaris_app.dto.CreateUserEntity;
import com.olivaris.olivaris_app.dto.EntityDto;
import com.olivaris.olivaris_app.dto.UpdateUserEntity;
import com.olivaris.olivaris_app.dto.UserEntityDto;
import com.olivaris.olivaris_app.exceptions.UserNotFoundException;
import com.olivaris.olivaris_app.models.EnabledEntity;
import com.olivaris.olivaris_app.models.EntityRole;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.UserEntityRole;
import com.olivaris.olivaris_app.models.enums.EntityRoleTypes;
import com.olivaris.olivaris_app.repositories.EntityRepository;
import com.olivaris.olivaris_app.repositories.EntityRoleRepository;
import com.olivaris.olivaris_app.repositories.UserEntityRoleRepository;
import com.olivaris.olivaris_app.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EntityServiceImpl implements EntityService {

    private final EntityRepository entityRep;
    private final UserRepository userRep;
    private final EntityRoleRepository entityRoleRep;
    private final UserEntityRoleRepository userEntityRoleRep;

    @Transactional
    @Override
    public ResponseEntity<EntityDto> create(CreateEntity request) {
        EnabledEntity newEntity = new EnabledEntity(
            request.getName(),
            request.getNif(),
            request.getPhone() == null ? null : request.getPhone(),
            request.getEmail(),
            true
        );

        EnabledEntity entityDb = entityRep.save(newEntity);

        return ResponseEntity.status(HttpStatus.CREATED).body(EntityDto.fromEntity(entityDb));
    }

    @Transactional
    @Override
    @PreAuthorize("@entityValidator.canOperateWithEntity(#entityId)")
    public ResponseEntity<UserEntityDto> createAssignment(
        Long entityId, 
        CreateUserEntity body
    ) {
        UserEntityDto userEntityDto = this.assignUserToEntity(entityId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(userEntityDto);
    }

    @Transactional
    @Override
    public UserEntityDto assignUserToEntity(
        Long entityId, 
        CreateUserEntity body
    ) {
        User userDb = userRep.findByEmail(body.getEmail())
                        .orElseThrow(() -> new UserNotFoundException(
                            "El usuario no existe en el sistema"
                        ));

        EnabledEntity entityDb = entityRep.findById(entityId)
                                    .orElseThrow(() -> new EntityNotFoundException(
                                    "La entidad habilitada no existe en la base de datos"
                                    )); 
        
        EntityRole entityRoleDb = entityRoleRep.findByName(body.getEntityRole().toString())
                                    .orElseThrow(() -> new EntityNotFoundException(
                                    "El rol dentro de la entidad no existe en la base de datos"
                                    ));
        
        Boolean writeCue = null;
        Boolean writeRea = null;
        Boolean readCue = null;
        Boolean readRea = null;

        // If role is farmer -> check the permissions from request body
        // Else if admin role -> permissions will be null
        if(entityRoleDb.getName().equals(EntityRoleTypes.ROLE_FARMER.toString())) {
            writeCue = body.getWriteCue() != null ? body.getWriteCue() : false;
            writeRea = body.getWriteRea() != null ? body.getWriteRea() : false;
            readCue = body.getReadCue() != null ? body.getReadCue() : false;
            readRea = body.getReadRea() != null ? body.getReadRea() : false;
        } 

        UserEntityRole newUserEntityRole = new UserEntityRole(
            userDb,
            entityDb,
            entityRoleDb,
            writeCue,
            writeRea,
            readCue,
            readRea
        );

        UserEntityRole userEntityRoleDb = userEntityRoleRep.save(newUserEntityRole);

        return UserEntityDto.fromEntity(userEntityRoleDb);
    }

    @Transactional
    @Override
    @PreAuthorize("@entityValidator.canOperateWithEntity(#entityId)")
    public ResponseEntity<UserEntityDto> updateUserToEntityData(
        Long entityId, 
        Long userId, 
        UpdateUserEntity body
    ) {
        UserEntityRole userEntityRoleDb = userEntityRoleRep.findByUserIdAndEnabledEntityId(userId, entityId)
                                    .orElseThrow(() -> new EntityNotFoundException(
                                    "La relación entre usuario y entidad no existe en la base de datos"
                                    )); 
        
        if(body.getEntityRole() != null) {
            EntityRole entityRoleDb = entityRoleRep.findByName(body.getEntityRole().toString())
                                        .orElseThrow(() -> new EntityNotFoundException(
                                        "El rol dentro de la entidad no existe en la base de datos"
                                        ));

            userEntityRoleDb.setEntityRole(entityRoleDb);
        }

        if(body.getWriteCue() != null) {
            userEntityRoleDb.setWriteCue(body.getWriteCue());
        }

        if(body.getWriteRea() != null) {
            userEntityRoleDb.setWriteRea(body.getWriteRea());
        }

        if(body.getReadCue() != null) {
            userEntityRoleDb.setReadCue(body.getReadCue());
        }

        if(body.getReadRea() != null) {
            userEntityRoleDb.setReadRea(body.getReadRea());
        }

        userEntityRoleDb = userEntityRoleRep.save(userEntityRoleDb);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(UserEntityDto.fromEntity(userEntityRoleDb));
    }

    @Transactional
    @Override
    @PreAuthorize("@entityValidator.canOperateWithEntity(#entityId)")
    public ResponseEntity<Void> delete(Long entityId, Long userId) {
        UserEntityRole userEntityRoleDb = userEntityRoleRep.findByUserIdAndEnabledEntityId(userId, entityId)
                                            .orElseThrow(() -> new EntityNotFoundException(
                                                "No existe la relación entre usuario y entidad habilitada"
                                            ));

        userEntityRoleRep.delete(userEntityRoleDb);

        return ResponseEntity.noContent().build();
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<List<EntityDto>> getUserEntities(Long userId) {
        // Check if user exists
        User userDb = userRep.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(
                "El usuario no existe en el sistema"
            ));

        // Get the user entities that belong to him
        List<UserEntityRole> entitiesList = userEntityRoleRep.findEntityByUserId(userId);

        List<EntityDto> entitiesDto = entitiesList.stream()
            .map(EntityDto::fromUserEntityRole)
            .toList();
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(entitiesDto);
    }
}
