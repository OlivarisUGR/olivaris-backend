package com.olivaris.olivaris_app.fixtures;

import java.util.Arrays;

import com.olivaris.olivaris_app.models.EnabledEntity;
import com.olivaris.olivaris_app.models.EntityRole;
import com.olivaris.olivaris_app.models.Role;
import com.olivaris.olivaris_app.models.User;
import com.olivaris.olivaris_app.models.UserEntityRole;
import com.olivaris.olivaris_app.models.enums.EntityRoleTypes;

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

    public static UserEntityRole createFarmerAssignmentWithAllPermissions(Long userId, Long entityId) {
        User user = UserFixtures.createBasicUser(new Role("ROLE_BASIC"));
        user.setId(userId);

        EnabledEntity entity = EntityFixtures.createBasicEntity();
        entity.setId(entityId);

        EntityRole farmerRole = new EntityRole(EntityRoleTypes.ROLE_FARMER.toString());

        return new UserEntityRole(
            user,
            entity,
            farmerRole,
            true,
            true,
            true,
            true
        );
    }
}
