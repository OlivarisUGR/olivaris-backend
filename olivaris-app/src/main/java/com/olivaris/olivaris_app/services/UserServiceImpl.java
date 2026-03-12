package com.olivaris.olivaris_app.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.dto.UserDto;
import com.olivaris.olivaris_app.exceptions.FieldIsNecessaryException;
import com.olivaris.olivaris_app.exceptions.RoleNotExistsException;
import com.olivaris.olivaris_app.exceptions.UserNotFoundException;
import com.olivaris.olivaris_app.models.EnabledEntity;
import com.olivaris.olivaris_app.models.Role;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.enums.RoleTypes;
import com.olivaris.olivaris_app.repositories.EntityRepository;
import com.olivaris.olivaris_app.repositories.RoleRepository;
import com.olivaris.olivaris_app.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;


@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRep;
    private final Long confirmTokenExpiresHours;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final String urlConfirm;
    private final String urlConfirmAdmin;
    private final RoleRepository roleRep;
    private final EntityRepository entityRep;

    // Using this constructor because @Value does not injected automatically by Springboot
    // if @AllArgsConstructor is used
    public UserServiceImpl(
        UserRepository userRep,
        @Value("${confirmation-token.expiration-hours}") Long confirmTokenExpiresHours,
        PasswordEncoder passwordEncoder,
        EmailService emailService,
        @Value("${spring.mail.urlConfirm}") String urlConfirm,
        @Value("${spring.mail.urlConfirmAdmin}") String urlConfirmAdmin,
        RoleRepository roleRep,
        EntityRepository entityRep
    ) {
        this.userRep = userRep;
        this.confirmTokenExpiresHours = confirmTokenExpiresHours;
        this.passwordEncoder = passwordEncoder;     
        this.emailService = emailService;    
        this.urlConfirm = urlConfirm;   
        this.urlConfirmAdmin = urlConfirmAdmin; 
        this.roleRep = roleRep;   
        this.entityRep = entityRep;  
    }

    @Transactional
    @Override
    public User register(RegisterRequest request) {
        // Create the user and save him on database
        String phone = request.getPhone() != null ? 
                            request.getPhone() : null;
        List<Role> roles = new ArrayList<>();

        // If there isn't a rol on request list, farmer role will given by default
        if(request.getRoles() == null) {
            Role farmerRole = roleRep.findByName(RoleTypes.ROLE_FARMER.toString())
                                .orElseThrow(() -> new RoleNotExistsException(RoleTypes.ROLE_FARMER.toString()));

            roles.add(farmerRole);
        } else {
            request.getRoles().stream()
            .forEach(r -> {
                Role roleDb = roleRep.findByName(r.toString())
                                .orElseThrow(() -> new RoleNotExistsException(r.toString()));
                
                roles.add(roleDb);
            });
        }
                
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

        User savedUser = userRep.save(newUser);

        // Send the email confirmation to the farmer user email.
        // If the user to create will be an entity_admin, send the email to the admins system and others
        // entity_admin that belong to the same entity
        Boolean isEntityAdmin = roles.stream().anyMatch(r -> 
            r.getName().equals(RoleTypes.ROLE_ENTITY_ADMIN.toString())
        );

        if(!isEntityAdmin) {
            String url = urlConfirm + token;
            emailService.sendEmailToOthers(savedUser.getEmail(), url, savedUser.getFirstname());
        } else if(request.getEntityNif() == null) {
            throw new FieldIsNecessaryException("El NIF de la entidad es necesario");
        } else {
            EnabledEntity entity = entityRep.findByNif(request.getEntityNif())
                            .orElseThrow(() -> new EntityNotFoundException(
                                            "No existe una entidad para el NIF especificado"
                                        ));

            String url = urlConfirmAdmin + token + "&entityId=" + entity.getId();
            emailService.sendEmailToAdmins(url, request.getEntityNif(), savedUser.getEmail());
        }

        return savedUser;
    }

    @Transactional
    @Override
    @PreAuthorize("@userSecurityValidator.canCreateUser(#request)")
    public ResponseEntity<UserDto> create(RegisterRequest request) {
        // Create the user and saved him on database
        String phone = request.getPhone() != null ? 
                            request.getPhone() : null;
        List<Role> roles = request.getRoles().stream()
                            .map(roleType -> roleRep.findByName(roleType.toString())
                                        .orElseThrow(() -> new RoleNotExistsException(roleType.toString()))
                            )
                            .toList();

        User newUser = new User(
            request.getFirstname(),
            request.getLastname(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            phone,
            roles,
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
    @PreAuthorize("@userSecurityValidator.canDeleteUser(#id)")
    public ResponseEntity<Void> delete(Long id) {
        User userToDelete = userRep.findById(id)
                        .orElseThrow(() -> new UserNotFoundException(id.toString()));
                        
        userRep.delete(userToDelete);

        return ResponseEntity.noContent().build();
    }
}
