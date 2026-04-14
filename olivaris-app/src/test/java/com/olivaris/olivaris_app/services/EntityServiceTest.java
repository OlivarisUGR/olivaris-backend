package com.olivaris.olivaris_app.services;

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

    private User basicUser1;
    private User basicUser2;
    private EnabledEntity entityA;
    private EnabledEntity entityB;
    private EntityRole entityAdminRole;

    @BeforeEach
    public void setUp() {
        // Create the first user (canomelero1@gmail.com)
        Role entityAdminSystemRole = roleRepository.findByName("ROLE_BASIC")
            .orElseThrow(() -> new RuntimeException("ROLE_BASIC not found"));
        
        basicUser1 = userRepository.save(UserFixtures.createBasicUser(entityAdminSystemRole));
         
        // Create the second user (carlos@gmail.com)
        Role farmerRole = roleRepository.findByName("ROLE_BASIC")
            .orElseThrow(() -> new RuntimeException("ROLE_BASIC not found"));
        
        basicUser2 = userRepository.save(UserFixtures.createBasicUser2(farmerRole));
        
        // Create entityA
        entityA = entityRepository.save(EntityFixtures.createBasicEntity());

        // Create entityB 
        entityB = entityRepository.save(EntityFixtures.createBasicEntity2());

        // Get entity role for entity admin user
        entityAdminRole = entityRoleRepository.findByName(EntityRoleTypes.ROLE_ADMIN.toString())
            .orElseThrow(() -> new RuntimeException("Entity ROLE_ADMIN not found"));

        // Assign basicUser1 to entityA
        UserEntityRole userEntityRoleA = new UserEntityRole(
            basicUser1,
            entityA,
            entityAdminRole,
            null,
            null,
            null,
            null
        );

        userEntityRoleRepository.save(userEntityRoleA);
    }

    // Code expects an UserDetails object instead of User (for Spring Security User)
    // It is necessary that WithUserDetails runs after BeforeEach method (because it is before,
    // the user does not exists yet)
    @Test
    @WithUserDetails(value = "canomelero1@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void adminCannotAssignUserToForeignEntity() throws Exception {
        // Logged in as entityA admin, try to assign basicUser2 to entityB 
        // Create the assignment object between basicUser2 to entityB
        CreateUserEntity assignmentRequest = new CreateUserEntity(
            basicUser2.getEmail(),
            EntityRoleTypes.ROLE_FARMER,
            true,
            true,
            true,
            true
        );

        assertThatThrownBy(() -> {
            entityService.createAssignment(
                entityB.getId(),
                assignmentRequest
            );
        })
        .isInstanceOf(EntityNotFoundException.class);
    }
}
