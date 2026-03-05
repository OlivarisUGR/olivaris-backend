package com.olivaris.olivaris_app.services;

import java.util.ArrayList;
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
import com.olivaris.olivaris_app.models.EntityPermission;
import com.olivaris.olivaris_app.models.EntityRole;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.UserEntityRole;
import com.olivaris.olivaris_app.repositories.EntityPermissionRepository;
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
    private final EntityPermissionRepository entityPermRep;
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
        Long userId, 
        CreateUserEntity body
    ) {
        UserEntityDto userEntityDto = this.assignUserToEntity(entityId, userId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(userEntityDto);
    }

    @Transactional
    @Override
    public UserEntityDto assignUserToEntity(
        Long entityId, 
        Long userId, 
        CreateUserEntity body
    ) {
        User userDb = userRep.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        EnabledEntity entityDb = entityRep.findById(entityId)
                                    .orElseThrow(() -> new EntityNotFoundException(
                                    "La entidad habilitada no existe en la base de datos"
                                    )); 
        
        EntityRole entityRoleDb = entityRoleRep.findByName(body.getEntityRole().toString())
                                    .orElseThrow(() -> new EntityNotFoundException(
                                    "El rol dentro de la entidad no existe en la base de datos"
                                    ));

        List<EntityPermission> entityPermList = new ArrayList<>();
        body.getEntityPermissions().stream()
            .forEach(p -> {
                EntityPermission permDb = entityPermRep.findByName(p.toString())
                                            .orElseThrow(() -> new EntityNotFoundException(
                                            "El permiso dentro de la entidad no existe en la base de datos"
                                            ));
                
                entityPermList.add(permDb);
            });

        UserEntityRole newUserEntityRole = new UserEntityRole(
            userDb,
            entityDb,
            entityRoleDb,
            entityPermList
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

        if(body.getEntityPermissions() != null) {
            List<EntityPermission> entityPermList = new ArrayList<>();
            body.getEntityPermissions().stream()
                .forEach(p -> {
                    EntityPermission permDb = entityPermRep.findByName(p.toString())
                                                .orElseThrow(() -> new EntityNotFoundException(
                                                "El permiso dentro de la entidad no existe en la base de datos"
                                                ));
                    
                    entityPermList.add(permDb);
                });
            
            userEntityRoleDb.setPermissions(entityPermList);
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
}
