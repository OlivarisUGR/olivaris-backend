package com.olivaris.olivaris_app.services;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.dto.UserDto;
import com.olivaris.olivaris_app.exceptions.UserAlreadyExistsException;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRep;
    private final UserService userService;

    @Override
    public ResponseEntity<UserDto> register(RegisterRequest request) {
        // Check if the user exists on database
        Optional<User> userDb = userRep.findByEmail(request.getEmail());

        if(userDb.isPresent()) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        // Register the user
        User newUser = userService.register(request);

        // Create the user DTO and return it
        String phone = newUser.getPhone() != null ? 
                        newUser.getPhone() : "";

        UserDto userDto = new UserDto(
            newUser.getFirstname(),
            newUser.getLastname(),
            newUser.getEmail(),
            phone
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

}
