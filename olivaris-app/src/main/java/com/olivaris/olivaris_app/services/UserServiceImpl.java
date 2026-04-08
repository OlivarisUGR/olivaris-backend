package com.olivaris.olivaris_app.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.dto.UserDto;
import com.olivaris.olivaris_app.exceptions.RoleNotExistsException;
import com.olivaris.olivaris_app.exceptions.UserNotFoundException;
import com.olivaris.olivaris_app.models.CustomUserDetails;
import com.olivaris.olivaris_app.models.EnabledEntity;
import com.olivaris.olivaris_app.models.Role;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.enums.RoleTypes;
import com.olivaris.olivaris_app.repositories.EntityRepository;
import com.olivaris.olivaris_app.repositories.RoleRepository;
import com.olivaris.olivaris_app.repositories.UserEntityRoleRepository;
import com.olivaris.olivaris_app.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;


@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRep;
    private final Long confirmTokenExpiresHours;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRep;
    private final EntityRepository entityRep;
    private final UserEntityRoleRepository userEntityRoleRep;

    // Using this constructor because @Value does not injected automatically by Springboot
    // if @AllArgsConstructor is used
    public UserServiceImpl(
        UserRepository userRep,
        @Value("${confirmation-token.expiration-hours}") Long confirmTokenExpiresHours,
        PasswordEncoder passwordEncoder,
        RoleRepository roleRep,
        EntityRepository entityRep,
        UserEntityRoleRepository userEntityRoleRep
    ) {
        this.userRep = userRep;
        this.confirmTokenExpiresHours = confirmTokenExpiresHours;
        this.passwordEncoder = passwordEncoder;       
        this.roleRep = roleRep;  
        this.entityRep = entityRep;
        this.userEntityRoleRep =userEntityRoleRep; 
    }

    @Transactional
    @Override
    public User register(RegisterRequest request) {
        // Create the user and save him on database
        String phone = request.getPhone() != null ? 
                            request.getPhone() : null;
        List<Role> roles = new ArrayList<>();

        // Basic role will given by default
        Role basicRole = roleRep.findByName(RoleTypes.ROLE_BASIC.toString())
                                .orElseThrow(() -> new RoleNotExistsException(RoleTypes.ROLE_BASIC.toString()));

        roles.add(basicRole);
                
        // Create an admin (TODO: add a script)
        // optionalRole = roleRep.findByName(RoleTypes.ROLE_ADMIN.toString());
        // roles.add(optionalRole.get());
        // optionalRole = roleRep.findByName(RoleTypes.ROLE_ENTITY_ADMIN.toString());
        // roles.add(optionalRole.get());

        // Create the confirm token, user and save both
        String token = UUID.randomUUID().toString();
        LocalDateTime tokenExpiresAt = LocalDateTime.now().plusHours(confirmTokenExpiresHours);

        User newUser = new User(
            request.getFirstname(),
            request.getLastname(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            phone,
            roles,
            false,
            request.getNif(),
            token,
            tokenExpiresAt
        );

        return userRep.save(newUser);
    }

    @Transactional
    @Override
    public ResponseEntity<UserDto> createSystemUser(RegisterRequest request) {
        // Create the user and saved him on database
        String phone = request.getPhone() != null ? 
                            request.getPhone() : null;
        
        // Get the roles from request; else if null -> roleBasic by default
        List<Role> rolesList = new ArrayList<>();

        if(request.getRoles() != null) {
            rolesList = request.getRoles().stream()
                .map(roleType -> {
                    Role role = roleRep.findByName(roleType.toString())
                                    .orElseThrow(() -> new RoleNotExistsException(
                                        roleType.toString()
                                    ));
                    return role;
                })
                .toList(); 
        } else {
            Role role = roleRep.findByName(RoleTypes.ROLE_BASIC.toString())
                                    .orElseThrow(() -> new RoleNotExistsException(
                                        RoleTypes.ROLE_BASIC.toString()
                                    ));
            rolesList.add(role);
        }

        User newUser = new User(
            request.getFirstname(),
            request.getLastname(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            phone,
            rolesList,
            true,
            request.getNif(),
            null,
            null
        );

        User savedUser = userRep.save(newUser);
        UserDto userDto = UserDto.fromEntity(savedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @Transactional
    @Override
    @PreAuthorize("@userValidator.canDeleteSystemUser(#id)")
    public ResponseEntity<Void> delete(Long id) {
        User userToDelete = userRep.findById(id)
                        .orElseThrow(() -> new UserNotFoundException(
                            "El usuario no existe en el sistema"
                        ));
                        
        userRep.delete(userToDelete);

        return ResponseEntity.noContent().build();
    }

    // TODO: add a validation (id != null, user corrects)
    @Transactional(readOnly = true)
    @Override
    @PreAuthorize("@userValidator.canGetSystemUser()")
    public ResponseEntity<UserDto> getById(Long id) {
        User userDb = userRep.findById(id)
            .orElseThrow(() -> new UserNotFoundException(
                "El usuario no existe en el sistema"
            ));
        
        UserDto userDto = UserDto.fromEntity(userDb);
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userDto);
    }

    @Transactional(readOnly = true)
    @Override
    @PreAuthorize("@userValidator.canGetSystemUser()")
    public ResponseEntity<UserDto> getByEmail(String email) {
        User userDb = userRep.findByEmail(email)    
            .orElseThrow(() -> new UserNotFoundException(
                "El usuario no existe en el sistema"
            ));
        
        UserDto userDto = UserDto.fromEntity(userDb);
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userDto);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<UserDto> getCurrentUser() {
        // Get user logued on system
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        // Get current user database info
        User userDb = userDetails.getUser();
        UserDto userDto = UserDto.fromEntity(userDb);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userDto);
    }

    // TODO: validation -> can execute it only an user with admin role entity
    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<List<UserDto>> getEntityUsers(Long entityId) {
        // Get the entity
        EnabledEntity entDb = entityRep.findById(entityId)
            .orElseThrow(() -> new EntityNotFoundException(
                "La entidad habilitada no existe en el sistema"
            ));

        // Get the users that belong to the entity 
        List<User> userList = userEntityRoleRep.findUserByEntityId(entityId);

        List<UserDto> userListDto = userList.stream()
            .map(UserDto::fromUserWEntities)
            .toList();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userListDto);
    }

    @Transactional(readOnly = true)
    @Override
    @PreAuthorize("@userValidator.canGetSystemUser()")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> userList = (List<User>) userRep.findAll();

        List<UserDto> userDtoList = userList.stream()
            .map(UserDto::fromEntity)
            .toList();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userDtoList);
    }
}
