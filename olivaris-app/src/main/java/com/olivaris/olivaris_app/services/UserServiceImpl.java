package com.olivaris.olivaris_app.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.models.ConfirmationToken;
import com.olivaris.olivaris_app.models.Role;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.enums.RoleTypes;
import com.olivaris.olivaris_app.repositories.ConfirmationTokenRepository;
import com.olivaris.olivaris_app.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRep;
    private final ConfirmationTokenRepository confirmTokenRep;
    private final Long confirmTokenExpiresHours;

    // Using this constructor because @Value does not injected automatically by Springboot
    // if @AllArgsConstructor is used
    public UserServiceImpl(UserRepository userRep,
                           ConfirmationTokenRepository confirmTokenRep,
                           @Value("${confirmation-token.expiration-hours}") Long confirmTokenExpiresHours) {
        this.userRep = userRep;
        this.confirmTokenRep = confirmTokenRep;
        this.confirmTokenExpiresHours = confirmTokenExpiresHours;
    }

    @Override
    public User register(RegisterRequest request) {
        // Create the user and save him on database
        String phone = request.getPhone() != null ? 
                            request.getPhone() : null;

        List<Role> roles = new ArrayList<>();
        roles.add(new Role(RoleTypes.ROLE_USER.toString()));

        // TODO: encrypt the password
        User newUser = new User(
            request.getFirstname(),
            request.getLastname(),
            request.getEmail(),
            request.getPassword(),
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

        // TODO: Send the email confirmation

        return savedUser;
    }
}
