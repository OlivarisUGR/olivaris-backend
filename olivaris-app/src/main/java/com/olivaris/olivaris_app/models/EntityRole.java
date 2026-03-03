package com.olivaris.olivaris_app.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "entity_role")
@Getter
@Setter
@NoArgsConstructor
public class EntityRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{entity.role.notblank}")
    @Column(unique = true, nullable = false)
    private String name;

    public EntityRole(String name) {
        this.name = name;
    }
}
