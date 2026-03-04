package com.olivaris.olivaris_app.dto;

import java.util.List;

import com.olivaris.olivaris_app.models.enums.EntityPermissionTypes;
import com.olivaris.olivaris_app.models.enums.EntityRoleTypes;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CreateUserEntity {
    @NotNull(message = "{entity.role.notblank}")
    private EntityRoleTypes entityRole;

    @NotNull(message = "{entity.permission.notblank}")
    private List<EntityPermissionTypes> entityPermissions;
}
