package com.olivaris.olivaris_app.dto;

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

    private Boolean writeCue;
    private Boolean writeRea;
    private Boolean readCue;
    private Boolean readRea;
}
