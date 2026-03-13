package com.olivaris.olivaris_app.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.olivaris.olivaris_app.fixtures.EntityFixtures;
import com.olivaris.olivaris_app.fixtures.UserFixtures;
import com.olivaris.olivaris_app.models.EnabledEntity;
import com.olivaris.olivaris_app.models.EntityRole;
import com.olivaris.olivaris_app.models.Role;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.UserEntityRole;

import jakarta.persistence.EntityNotFoundException;

@DataJpaTest
public class UserEntityRoleRepoTest {

    @Autowired
    private RoleRepository roleRep;

    @Autowired
    private EntityRoleRepository entityRoleRep;

    @Autowired
    private UserEntityRoleRepository userEntRoleRep;

    @Autowired
    private UserRepository userRep;

    @Autowired
    private EntityRepository entityRep;

    @Test
    public void createAssignmentUserToEntity() {
        Role adminRole = roleRep.findByName("ROLE_ADMIN")
                                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));
        
        User user = UserFixtures.createBasicUser(adminRole);              
        userRep.save(user);

        EnabledEntity entity = EntityFixtures.createBasicEntity();
        entityRep.save(entity);

        EntityRole entityRoleDb = entityRoleRep.findByName("ROLE_FARMER")
                                    .orElseThrow(() -> new EntityNotFoundException(
                                    "El rol dentro de la entidad no existe en la base de datos"
                                    ));

        UserEntityRole userToEntity = new UserEntityRole(
            user,
            entity,
            entityRoleDb,
            true,
            true,
            false,
            false
        );

        UserEntityRole saved = userEntRoleRep.save(userToEntity);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUser().getId()).isEqualTo(user.getId());
        assertThat(saved.getEnabledEntity().getId()).isEqualTo(entity.getId());
        assertThat(saved.getEntityRole().getName()).isEqualTo("ROLE_FARMER");

        UserEntityRole found = userEntRoleRep
            .findByUserIdAndEnabledEntityId(user.getId(), entity.getId())
            .orElseThrow();

        assertThat(found.getId()).isEqualTo(saved.getId());
    }
}
