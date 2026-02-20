package com.olivaris.olivaris_app.services;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.exceptions.UserAlreadyExistsException;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private UserRepository userRep;
    private UserService userService;

    @Override
    public ResponseEntity<?> register(RegisterRequest request) {
        // Check if the user exists on database
        Optional<User> userDb = userRep.findByEmail(request.getEmail());

        if(userDb.isPresent()) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        // Register the user
        User newUser = userService.register(request);

        // Create user dto and return it

        return null;
    }

}
