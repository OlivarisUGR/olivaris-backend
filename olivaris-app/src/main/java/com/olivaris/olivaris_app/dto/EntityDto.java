package com.olivaris.olivaris_app.dto;

import com.olivaris.olivaris_app.models.EnabledEntity;
import com.olivaris.olivaris_app.models.UserEntityRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EntityDto {
    private Long id;
    private String name;
    private String nif;
    private String phone;
    private String email;
    private Boolean active;
    private String userEntityRole;
    
    public static EntityDto fromEntity(EnabledEntity entity) {
        return new EntityDto(
            entity.getId(),
            entity.getName(),
            entity.getNif(),
            entity.getPhone() != null ? entity.getPhone() : "",
            entity.getEmail(),
            entity.getActive(),
            null
        );
    }

    public static EntityDto fromUserEntityRole(UserEntityRole userEntityRole) {
        EnabledEntity entity = userEntityRole.getEnabledEntity();

        return new EntityDto(
            entity.getId(),
            entity.getName(),
            entity.getNif(),
            entity.getPhone() != null ? entity.getPhone() : "",
            entity.getEmail(),
            entity.getActive(),
            userEntityRole.getEntityRole().getName()
        );
    }
}
