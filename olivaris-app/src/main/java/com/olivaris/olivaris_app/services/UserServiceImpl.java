package com.olivaris.olivaris_app.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.dto.UserDto;
import com.olivaris.olivaris_app.exceptions.AccessDeniedException;
import com.olivaris.olivaris_app.exceptions.RoleNotExistsException;
import com.olivaris.olivaris_app.exceptions.UserNotFoundException;
import com.olivaris.olivaris_app.models.ConfirmationToken;
import com.olivaris.olivaris_app.models.Role;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.enums.RoleTypes;
import com.olivaris.olivaris_app.repositories.ConfirmationTokenRepository;
import com.olivaris.olivaris_app.repositories.RoleRepository;
import com.olivaris.olivaris_app.repositories.UserRepository;
import com.olivaris.olivaris_app.security.SecurityUtils;


@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRep;
    private final ConfirmationTokenRepository confirmTokenRep;
    private final Long confirmTokenExpiresHours;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final String urlMailHost;
    private final RoleRepository roleRep;

    // Using this constructor because @Value does not injected automatically by Springboot
    // if @AllArgsConstructor is used
    public UserServiceImpl(
        UserRepository userRep,
        ConfirmationTokenRepository confirmTokenRep,
        @Value("${confirmation-token.expiration-hours}") Long confirmTokenExpiresHours,
        PasswordEncoder passwordEncoder,
        EmailService emailService,
        @Value("${spring.mail.urlServerHost}") String urlMailHost,
        RoleRepository roleRep
    ) {
        this.userRep = userRep;
        this.confirmTokenRep = confirmTokenRep;
        this.confirmTokenExpiresHours = confirmTokenExpiresHours;
        this.passwordEncoder = passwordEncoder;     
        this.emailService = emailService;    
        this.urlMailHost = urlMailHost;    
        this.roleRep = roleRep;     
    }

    @Transactional
    @Override
    public User register(RegisterRequest request) {
        // Create the user and save him on database
        String phone = request.getPhone() != null ? 
                            request.getPhone() : null;
        List<Role> roles = new ArrayList<>();
        
        // Find the role user and save on the list
        Optional<Role> optionalRole = roleRep.findByName(RoleTypes.ROLE_ADMIN.toString());

        if(optionalRole.isEmpty()) {
            throw new RoleNotExistsException(RoleTypes.ROLE_ADMIN.toString());
        }

        roles.add(optionalRole.get());

        optionalRole = roleRep.findByName(RoleTypes.ROLE_COOPER_ADMIN.toString());
        roles.add(optionalRole.get());
        optionalRole = roleRep.findByName(RoleTypes.ROLE_FARMER.toString());
        roles.add(optionalRole.get());

        User newUser = new User(
            request.getFirstname(),
            request.getLastname(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            phone,
            roles,
            false
        );

        User savedUser = userRep.save(newUser);

        // Create the confirm token and save it
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(confirmTokenExpiresHours);
        ConfirmationToken confirmToken = new ConfirmationToken(token, expiresAt, savedUser, true);
        confirmTokenRep.save(confirmToken);

        // Send the email confirmation
        String url = urlMailHost + token;
        emailService.sendEmail(savedUser.getEmail(), url, savedUser.getFirstname());

        return savedUser;
    }

    @Transactional
    @Override
    public ResponseEntity<UserDto> create(RegisterRequest request) {
        // Only admin can create others admin
        if(SecurityUtils.requestHasRole(request.getRoles(), RoleTypes.ROLE_ADMIN) &&
            !SecurityUtils.currentUserHasRole(RoleTypes.ROLE_ADMIN)) {
                throw new AccessDeniedException("Solo administradores pueden crear otros admins");
        }

        // Only cooper admin or admin can create others cooper admin
        if(SecurityUtils.requestHasRole(request.getRoles(), RoleTypes.ROLE_COOPER_ADMIN) &&
            !SecurityUtils.currentUserHasRole(RoleTypes.ROLE_ADMIN) &&
                !SecurityUtils.currentUserHasRole(RoleTypes.ROLE_COOPER_ADMIN)) {
                    throw new AccessDeniedException(
                        "Solo admins de cooperativa o admins pueden crear otros admins de cooperativa"
                    );
        }

        // Only farmer cand be created by admin or cooper admin
        if(SecurityUtils.requestHasRole(request.getRoles(), RoleTypes.ROLE_FARMER) &&
            !SecurityUtils.currentUserHasRole(RoleTypes.ROLE_ADMIN) &&
                !SecurityUtils.currentUserHasRole(RoleTypes.ROLE_COOPER_ADMIN)) {
                    throw new AccessDeniedException(
                        "Solo admins y admins de cooperativa pueden crear agricultores"
                );
        }

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
            true
        );

        User savedUser = userRep.save(newUser);
        UserDto userDto = UserDto.fromEntity(savedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @Transactional
    @Override
    public ResponseEntity<Void> delete(Long id) {
        User userDb = userRep.findById(id)
                        .orElseThrow(() -> new UserNotFoundException(id.toString()));

        // TODO: no eliminarme a mi mismo, si soy agricultor no puedo eliminar a nadie, si soy cooper admin
        // solo puedo eliminar a otro cooper admin o agricultor, si soy admin puedo eliminar a cualquiera
            
                        
        userRep.delete(userDb);

        return ResponseEntity.noContent().build();
    }
}
