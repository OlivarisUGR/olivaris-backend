package com.olivaris.olivaris_app.dto;

import java.util.List;

import com.olivaris.olivaris_app.models.EntityPermission;
import com.olivaris.olivaris_app.models.EntityRole;
import com.olivaris.olivaris_app.models.UserEntityRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserEntityDto {
    private String userNif;
    private String entityNif;
    private EntityRole entityRole;
    private List<EntityPermission> entityPermission;

    public static UserEntityDto fromEntity(UserEntityRole userEntityRole) {
        return new UserEntityDto(
            userEntityRole.getUser().getNif(),
            userEntityRole.getEnabledEntity().getNif(),
            userEntityRole.getEntityRole(),
            userEntityRole.getPermissions()
        );
    }
}
