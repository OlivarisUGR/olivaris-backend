package com.olivaris.olivaris_app.fixtures;

import java.util.Arrays;

import com.olivaris.olivaris_app.models.Role;
import com.olivaris.olivaris_app.models.User;

public class UserFixtures {

    public static User createBasicUser(Role role) {
        return new User(
            "jorge",
            "cano melero",
            "canomelero1@gmail.com",
            "Prueba123",
            null,
            Arrays.asList(role),
            true,
            "77012264Q",
            null,
            null
        );
    }

    public static User createBasicUser2(Role role) {
        return new User(
            "carlos",
            "cano melero",
            "carlos@gmail.com",
            "Prueba123",
            null,
            Arrays.asList(role),
            true,
            "77012264C",
            null,
            null
        );
    }
}
