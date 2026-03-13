package com.olivaris.olivaris_app.dto;

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
    private Boolean writeCue;
    private Boolean writeRea;
    private Boolean readCue;
    private Boolean readRea;

    public static UserEntityDto fromEntity(UserEntityRole userEntityRole) {
        return new UserEntityDto(
            userEntityRole.getUser().getNif(),
            userEntityRole.getEnabledEntity().getNif(),
            userEntityRole.getEntityRole(),
            userEntityRole.getWriteCue(),
            userEntityRole.getWriteRea(),
            userEntityRole.getReadCue(),
            userEntityRole.getReadRea()
        );
    }
}
