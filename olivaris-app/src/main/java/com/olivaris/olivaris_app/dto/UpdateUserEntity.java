package com.olivaris.olivaris_app.dto;

import com.olivaris.olivaris_app.models.enums.EntityRoleTypes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UpdateUserEntity {
    private EntityRoleTypes entityRole;
    private Boolean writeCue;
    private Boolean writeRea;
    private Boolean readCue;
    private Boolean readRea;
}
