package com.olivaris.olivaris_app.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.olivaris.olivaris_app.fixtures.EntityFixtures;
import com.olivaris.olivaris_app.fixtures.UserFixtures;
import com.olivaris.olivaris_app.models.EnabledEntity;
import com.olivaris.olivaris_app.models.EntityRole;
import com.olivaris.olivaris_app.models.Role;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.UserEntityRole;
import com.olivaris.olivaris_app.models.enums.EntityRoleTypes;
import com.olivaris.olivaris_app.repositories.UserRepository;

@SpringBootTest
public class ActivityServiceTest {

    @Autowired
    private UserRepository userRepository;

    private User userBasic;
    private EnabledEntity entity;
    private UserEntityRole userEntityRole;
    private Long userId = 1L;
    private Long entityId = 1L;

    @BeforeEach
    public void setUp() {
        userBasic = UserFixtures.createBasicUser(new Role("ROLE_BASIC"));
        userBasic.setId(userId);

        entity = EntityFixtures.createBasicEntity();
        entity.setId(entityId); 

        userEntityRole = new UserEntityRole(
            userBasic,
            entity,
            new EntityRole(EntityRoleTypes.ROLE_FARMER.toString()),
            true,
            true,
            true,
            true
        );
    }

    @Test
    public void linkedFarmerWithAllPermissionsCannotCreateActivity() throws Exception {
        


    }
}
