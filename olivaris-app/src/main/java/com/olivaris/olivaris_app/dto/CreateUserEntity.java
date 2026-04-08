package com.olivaris.olivaris_app.dto;

import com.olivaris.olivaris_app.models.enums.EntityRoleTypes;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CreateUserEntity {

    @NotBlank(message = "{user.email.notblank}")
    @Email(message = "{user.email.invalid}")
    private String email;

    @NotNull(message = "{entity.role.notnull}")
    private EntityRoleTypes entityRole;

    private Boolean writeCue;
    private Boolean writeRea;
    private Boolean readCue;
    private Boolean readRea;
}
