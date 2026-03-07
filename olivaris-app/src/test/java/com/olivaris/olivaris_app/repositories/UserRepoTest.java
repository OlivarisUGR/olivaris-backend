package com.olivaris.olivaris_app.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.olivaris.olivaris_app.fixtures.UserFixtures;
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
        Role adminRole = roleRep.findByName("ROLE_ADMIN")
                                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

        User savedUser = userRep.save(UserFixtures.createBasicUser(adminRole));

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isGreaterThan(0);
    }
}
