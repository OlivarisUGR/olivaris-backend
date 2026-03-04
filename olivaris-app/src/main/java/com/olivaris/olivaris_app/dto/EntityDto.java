package com.olivaris.olivaris_app.dto;

import com.olivaris.olivaris_app.models.EnabledEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EntityDto {
    private String name;
    private String nif;
    private String phone;
    private String email;
    private Boolean active;
    
    public static EntityDto fromEntity(EnabledEntity entity) {
        return new EntityDto(
            entity.getName(),
            entity.getNif(),
            entity.getPhone() != null ? entity.getPhone() : "",
            entity.getEmail(),
            entity.getActive()
        );
    }
}
