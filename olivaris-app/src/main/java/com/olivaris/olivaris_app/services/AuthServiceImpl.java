package com.olivaris.olivaris_app.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olivaris.olivaris_app.dto.CreateUserEntity;
import com.olivaris.olivaris_app.dto.LoginRequest;
import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.dto.TokenResponse;
import com.olivaris.olivaris_app.dto.UserDto;
import com.olivaris.olivaris_app.exceptions.AuthHeaderNotValidException;
import com.olivaris.olivaris_app.exceptions.ConfirmTokenNotExistsException;
import com.olivaris.olivaris_app.exceptions.TokenExpiredException;
import com.olivaris.olivaris_app.exceptions.UserAlreadyExistsException;
import com.olivaris.olivaris_app.exceptions.UserIsEnabledException;
import com.olivaris.olivaris_app.exceptions.UserNotEnabledException;
import com.olivaris.olivaris_app.exceptions.UserNotFoundException;
import com.olivaris.olivaris_app.models.CustomUserDetails;
import com.olivaris.olivaris_app.models.EnabledEntity;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.enums.EntityRoleTypes;
import com.olivaris.olivaris_app.repositories.EntityRepository;
import com.olivaris.olivaris_app.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRep;
    private final UserService userService;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final EntityService entityService;
    private final EmailService emailService;
    private final String urlConfirm;
    private final EntityRepository entityRep;
    
    public AuthServiceImpl(
        UserRepository userRep, 
        UserService userService, 
        AuthenticationManager authManager,
        JwtService jwtService, 
        EntityService entityService, 
        EmailService emailService, 
        @Value("${spring.mail.urlConfirm}") String urlConfirm,
        EntityRepository entityRep
    ) {
        this.userRep = userRep;
        this.userService = userService;
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.entityService = entityService;
        this.emailService = emailService;
        this.urlConfirm = urlConfirm;
        this.entityRep = entityRep;
    }

    @Transactional
    @Override
    public ResponseEntity<UserDto> registerSystemUser(RegisterRequest request) {
        // Check if the user exists on database
        Optional<User> userDb = userRep.findByEmail(request.getEmail());

        if(userDb.isPresent()) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        // Register the user
        User savedUser = userService.register(request);

        // Send email confirmation to the user
        String url = urlConfirm + savedUser.getConfirmationToken();
        emailService.sendEmailToUser(savedUser.getEmail(), url, savedUser.getFirstname());

        // Create the user DTO 
        UserDto userDto = UserDto.fromEntity(savedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @Transactional
    @Override
    @PreAuthorize("@userValidator.canRegisterEntityAdminUser(#request)")
    public ResponseEntity<UserDto> registerEntityAdminUser(RegisterRequest request) {
        // Check if the user exists on database
        Optional<User> userDb = userRep.findByEmail(request.getEmail());

        if(userDb.isPresent()) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        // Register the user
        User savedUser = userService.register(request);

        // Create the relationship between entity admin user and the entity
        CreateUserEntity createUserEntity = new CreateUserEntity(
            savedUser.getEmail(),
            EntityRoleTypes.ROLE_ADMIN, 
            null,
            null,
            null,
            null
        );

        EnabledEntity entity = entityRep.findByNif(request.getEntityNif())
            .orElseThrow(() -> new EntityNotFoundException(
                "La entidad no existe en el sistema"
            ));
        
        entityService.assignUserToEntity(
            entity.getId(), 
            createUserEntity
        );

        // Send email confirmation to the user
        String url = urlConfirm + savedUser.getConfirmationToken();
        emailService.sendEmailToUser(savedUser.getEmail(), url, savedUser.getFirstname());

        // Create the user DTO 
        UserDto userDto = UserDto.fromEntity(savedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @Transactional
    @Override
    public ResponseEntity<UserDto> confirm(String confirmToken) {
        // Check if the token exists
        User userDb = userRep.findByConfirmationToken(confirmToken)
                            .orElseThrow(() -> new ConfirmTokenNotExistsException(confirmToken));

        // Check if token has not expires
        LocalDateTime tokenExpiresAt = userDb.getTokenExpiresAt();

        if(tokenExpiresAt.isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException(confirmToken);
        }

        // Change user enabled value to true
        if(userDb.getEnabled()) {
            throw new UserIsEnabledException("El usuario ya está habilitado");
        } 
        
        userDb.setEnabled(true);
        userRep.save(userDb);

        // Create the user DTO 
        UserDto userDto = UserDto.fromEntity(userDb);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userDto);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<TokenResponse> login(LoginRequest request) {
        // Do an authentication for the user (info inside the request)
        // SpringBoot will get the user from UserDetailsService and check 
        // if the email and password are correct. If the user can not be
        // authenticated, it will throw an exception
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(), 
                request.getPassword()
            )
        );

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        // Check if the user is enabled (confirmed register)
        if(!user.isEnabled()) {
            throw new UserNotEnabledException(user.getUsername());
        }

        // Create the token and refresh token
        String token = jwtService.createToken(user.getUser());
        String refreshToken = jwtService.createRefreshToken(user.getUser());

        // Create the token response and return it
        TokenResponse tokenRes = new TokenResponse(
            token, 
            refreshToken
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(tokenRes);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<TokenResponse> refresh(String authHeader) {
        if(authHeader == null || !authHeader.contains("Bearer ")) {
            throw new AuthHeaderNotValidException("Error en la cabecera de la petición");
        }

        // Get the user and check if exists
        String refreshToken = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(refreshToken);
        
        if(userEmail == null) {
            throw new TokenExpiredException(refreshToken);
        }

        User userDb = userRep.findByEmail(userEmail)
                            .orElseThrow(() -> new UserNotFoundException(
                                "El usuario no existe en el sistema"
                            ));

        // Check if the token is valid
        if(!jwtService.isValid(refreshToken, userDb)) {
            throw new TokenExpiredException(refreshToken);
        }

        // Create the token and refresh token
        TokenResponse newTokens = new TokenResponse(
            jwtService.createToken(userDb), 
            jwtService.createRefreshToken(userDb)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(newTokens);
    }
}
