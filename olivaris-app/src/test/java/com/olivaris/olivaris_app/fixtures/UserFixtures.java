package com.olivaris.olivaris_app.fixtures;

import java.util.Arrays;

import com.olivaris.olivaris_app.models.Role;
import com.olivaris.olivaris_app.models.User;

public class UserFixtures {

    public static User createBasicUser(Role adminRole) {
        return new User(
            "jorge",
            "cano melero",
            "canomelero1@gmail.com",
            "Prueba123",
            null,
            Arrays.asList(adminRole),
            true,
            "77012264Q"
        );
    }
}
