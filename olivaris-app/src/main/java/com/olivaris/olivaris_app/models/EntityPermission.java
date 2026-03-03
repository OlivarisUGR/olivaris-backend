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
@Table(name = "entity_permission")
@NoArgsConstructor
@Getter
@Setter
public class EntityPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{entity.permission.notblank}")
    @Column(unique = true, nullable = false)
    private String name;

    public EntityPermission(String name) {
        this.name = name;
    }
}
