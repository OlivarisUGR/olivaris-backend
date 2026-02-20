package com.olivaris.olivaris_app.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.olivaris.olivaris_app.dto.RegisterRequest;
import com.olivaris.olivaris_app.models.Role;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.enums.RoleTypes;
import com.olivaris.olivaris_app.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    private UserRepository userRep;

    @Override
    public User register(RegisterRequest request) {
        // Create the user and save him on database
        String phone = request.getPhone() != null ? 
                            request.getPhone() : null;

        List<Role> roles = new ArrayList<>();
        roles.add(new Role(RoleTypes.ROLE_USER.toString()));

        User newUser = new User(
            request.getFirstname(),
            request.getLastname(),
            request.getEmail(),
            request.getPassword(),
            phone,
            roles
        );

        User savedUser = userRep.save(newUser);

        // Create the confirm token and save it

        // Send the email confirmation

        return savedUser;
    }
}
