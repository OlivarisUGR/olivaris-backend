package com.olivaris.olivaris_app.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.dto.UserDto;
import com.olivaris.olivaris_app.exceptions.RoleNotExistsException;
import com.olivaris.olivaris_app.exceptions.UserNotFoundException;
import com.olivaris.olivaris_app.models.Role;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.enums.RoleTypes;
import com.olivaris.olivaris_app.repositories.EntityRepository;
import com.olivaris.olivaris_app.repositories.RoleRepository;
import com.olivaris.olivaris_app.repositories.UserRepository;


@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRep;
    private final Long confirmTokenExpiresHours;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final String urlConfirm;
    private final RoleRepository roleRep;

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
        this.roleRep = roleRep;   
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

        User savedUser = userRep.save(newUser);

        // TODO: sacar de aqui el send to email y devolver directamente el usuario creado. El auth service ahora
        // tiene dos metodos segun lo que se vaya a registrar (entity admin o basic) y esos metodos llaman a 
        // este registro, teniendo cada uno su lógica
        // TODO: hacer una nueva migración que quite el ENTITY_ADMIN role y los actualice por ROLE_BASIC
        // Send the email confirmation to the user.
        String url = urlConfirm + token;
        emailService.sendEmailToOthers(savedUser.getEmail(), url, savedUser.getFirstname());

        return savedUser;
    }

    @Transactional
    @Override
    public ResponseEntity<UserDto> createBasicUser(RegisterRequest request) {
        // Create the user and saved him on database
        String phone = request.getPhone() != null ? 
                            request.getPhone() : null;
        Role roles = roleRep.findByName(RoleTypes.ROLE_BASIC.toString())
                                .orElseThrow(() -> new RoleNotExistsException(
                                    RoleTypes.ROLE_BASIC.toString()
                                ));

        User newUser = new User(
            request.getFirstname(),
            request.getLastname(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            phone,
            List.of(roles),
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
    public ResponseEntity<UserDto> getById(Long id) {
        User userDb = userRep.findById(id)
            .orElseThrow(() -> new UserNotFoundException(
                "El usuario no existe en el sistema"
            ));
        
        UserDto userDto = UserDto.fromEntity(userDb);
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userDto);
    }

    // TODO: add a validation (email != null, user corrects)
    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<UserDto> getByEmail(String email) {
        User userDb = userRep.findByEmail(email)    
            .orElseThrow(() -> new UserNotFoundException(
                "El usuario no existe en el sistema"
            ));
        
        UserDto userDto = UserDto.fromEntity(userDb);
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userDto);
    }
}
