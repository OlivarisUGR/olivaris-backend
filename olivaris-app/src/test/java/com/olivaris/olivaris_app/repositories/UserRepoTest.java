package com.olivaris.olivaris_app.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.olivaris.olivaris_app.models.Role;
import com.olivaris.olivaris_app.models.User;

@DataJpaTest
public class UserRepoTest {

    @Autowired
    private UserRepository userRep;

    @Autowired
    private RoleRepository roleRep;

    @Test
    public void createUserTest() {
        // Fetch existing role from database instead of creating a new one
        Role adminRole = roleRep.findByName("ROLE_ADMIN")
                                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));
        
        User newUser = User.builder()
                        .firstname("jorge")
                        .lastname("cano melero")
                        .email("canomelero1@gmail.com")
                        .password("Prueba123")
                        .roles(Arrays.asList(adminRole))
                        .enabled(true)
                        .nif("77021264E")
                        .build();
        
        User savedUser = userRep.save(newUser);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isGreaterThan(0);
    }
}
