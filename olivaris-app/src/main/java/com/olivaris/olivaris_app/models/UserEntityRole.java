package com.olivaris.olivaris_app.models;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_entity_role")
@IdClass(UserEntityRoleId.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserEntityRole {
    
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "enabled_entity_id", nullable = false)
    private EnabledEntity enabledEntity;

    @ManyToOne
    @JoinColumn(name = "entity_role_id", nullable = false)
    private EntityRole entityRole;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_entity_permission",
        joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false),
            @JoinColumn(name = "enabled_entity_id", referencedColumnName = "enabled_entity_id", nullable = false)
        },
        inverseJoinColumns = @JoinColumn(name = "entity_permission_id", nullable = false),
        uniqueConstraints = { 
            @UniqueConstraint(
                columnNames = {"user_id", "enabled_entity_id", "entity_permission_id"}
            ) 
        }
    )
    private List<EntityPermission> permissions;
}
