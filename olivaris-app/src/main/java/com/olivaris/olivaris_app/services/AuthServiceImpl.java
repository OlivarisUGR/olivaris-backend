package com.olivaris.olivaris_app.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olivaris.olivaris_app.dto.LoginRequest;
import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.dto.TokenResponse;
import com.olivaris.olivaris_app.dto.UserDto;
import com.olivaris.olivaris_app.exceptions.AuthHeaderNotValidException;
import com.olivaris.olivaris_app.exceptions.ConfirmTokenNotExistsException;
import com.olivaris.olivaris_app.exceptions.TokenExpiredException;
import com.olivaris.olivaris_app.exceptions.UserAlreadyExistsException;
import com.olivaris.olivaris_app.exceptions.UserNotEnabledException;
import com.olivaris.olivaris_app.exceptions.UserNotFoundException;
import com.olivaris.olivaris_app.models.ConfirmationToken;
import com.olivaris.olivaris_app.models.CustomUserDetails;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.repositories.ConfirmationTokenRepository;
import com.olivaris.olivaris_app.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRep;
    private final UserService userService;
    private final ConfirmationTokenRepository tokenRep;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Transactional
    @Override
    public ResponseEntity<UserDto> register(RegisterRequest request) {
        // Check if the user exists on database
        Optional<User> userDb = userRep.findByEmail(request.getEmail());

        if(userDb.isPresent()) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        // Register the user
        User newUser = userService.register(request);

        // Create the user DTO 
        UserDto userDto = UserDto.fromEntity(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @Transactional
    @Override
    public ResponseEntity<UserDto> confirm(String confirmToken) {
        // Check if the token exists
        Optional<ConfirmationToken> optionalConfirmToken = tokenRep.findByToken(confirmToken);

        if(optionalConfirmToken.isEmpty()) {
            throw new ConfirmTokenNotExistsException(confirmToken);
        }

        // Check if token has not expires
        ConfirmationToken token = optionalConfirmToken.get();

        if(token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException(confirmToken);
        }

        // Get the user from token and change the enabled value to true
        User userDb = token.getUser();
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
                            .orElseThrow(() -> new UserNotFoundException(userEmail));

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
