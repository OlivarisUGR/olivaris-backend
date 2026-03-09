package com.olivaris.olivaris_app.services;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import com.olivaris.olivaris_app.dto.CreateUserEntity;
import com.olivaris.olivaris_app.fixtures.EntityFixtures;
import com.olivaris.olivaris_app.fixtures.UserFixtures;
import com.olivaris.olivaris_app.models.EnabledEntity;
import com.olivaris.olivaris_app.models.EntityRole;
import com.olivaris.olivaris_app.models.Role;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.UserEntityRole;
import com.olivaris.olivaris_app.models.enums.EntityPermissionTypes;
import com.olivaris.olivaris_app.models.enums.EntityRoleTypes;
import com.olivaris.olivaris_app.repositories.EntityRepository;
import com.olivaris.olivaris_app.repositories.EntityRoleRepository;
import com.olivaris.olivaris_app.repositories.RoleRepository;
import com.olivaris.olivaris_app.repositories.UserEntityRoleRepository;
import com.olivaris.olivaris_app.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class EntityServiceTest {

    @Autowired
    private EntityService entityService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EntityRoleRepository entityRoleRepository;

    @Autowired
    private UserEntityRoleRepository userEntityRoleRepository;

    private User entityAdminUser;
    private User farmerUser;
    private EnabledEntity entityA;
    private EnabledEntity entityB;
    private EntityRole entityAdminRole;

    @BeforeEach
    public void setUp() {
        // Create the entity admin user
        Role entityAdminSystemRole = roleRepository.findByName("ROLE_ENTITY_ADMIN")
            .orElseThrow(() -> new RuntimeException("ROLE_ENTITY_ADMIN not found"));
        
        entityAdminUser = userRepository.save(UserFixtures.createBasicUser(entityAdminSystemRole));
         
        // Create the farmer user
        Role farmerRole = roleRepository.findByName("ROLE_FARMER")
            .orElseThrow(() -> new RuntimeException("ROLE_FARMER not found"));
        
        farmerUser = userRepository.save(UserFixtures.createBasicUser2(farmerRole));
        
        // Create entityA
        entityA = entityRepository.save(EntityFixtures.createBasicEntity());

        // Create entityB 
        entityB = entityRepository.save(EntityFixtures.createBasicEntity2());

        // Get entity role for entity admin user
        entityAdminRole = entityRoleRepository.findByName(EntityRoleTypes.ROLE_ADMIN.toString())
            .orElseThrow(() -> new RuntimeException("Entity ROLE_ADMIN not found"));

        // Assign entityAdminUser as admin to entityA
        UserEntityRole userEntityRoleA = new UserEntityRole(
            entityAdminUser,
            entityA,
            entityAdminRole,
            List.of()
        );

        userEntityRoleRepository.save(userEntityRoleA);
    }

    // Code expects an UserDetails object instead of User (for Spring Security User)
    // It is necessary that WithUserDetails runs after BeforeEach method (because it is before,
    // the user does not exists yet)
    @Test
    @WithUserDetails(value = "canomelero1@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void adminCannotAssignUserToForeignEntity() throws Exception {
       CreateUserEntity assignmentRequest = new CreateUserEntity(
            EntityRoleTypes.ROLE_FARMER,
            Arrays.asList(EntityPermissionTypes.WRITE_CUE)
        );

        assertThatThrownBy(() -> {
            entityService.createAssignment(
                entityB.getId(),
                farmerUser.getId(),
                assignmentRequest
            );
        })
        .isInstanceOf(EntityNotFoundException.class);
    }
}
