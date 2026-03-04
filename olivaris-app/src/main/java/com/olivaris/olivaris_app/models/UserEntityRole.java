package com.olivaris.olivaris_app.models;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "user_entity_role",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "enabled_entity_id"})
)
@NoArgsConstructor
@Getter
@Setter
public class UserEntityRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "enabled_entity_id", nullable = false)
    private EnabledEntity enabledEntity;

    @ManyToOne
    @JoinColumn(name = "entity_role_id", nullable = false)
    private EntityRole entityRole;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_entity_permission",
        joinColumns = @JoinColumn(name = "user_entity_role_id", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "entity_permission_id", nullable = false)
    )
    private List<EntityPermission> permissions;

    public UserEntityRole(User user, EnabledEntity enabledEntity, EntityRole entityRole,
            List<EntityPermission> permissions) {
        this.user = user;
        this.enabledEntity = enabledEntity;
        this.entityRole = entityRole;
        this.permissions = permissions;
    }
}
