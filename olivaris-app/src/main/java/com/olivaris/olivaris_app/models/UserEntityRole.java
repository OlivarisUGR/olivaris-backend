package com.olivaris.olivaris_app.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "user_entity_role",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"user_id", "enabled_entity_id", "entity_role_id"}
    )
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

    // One to one ??
    @ManyToOne
    @JoinColumn(name = "entity_role_id", nullable = false)
    private EntityRole entityRole;

    private Boolean writeCue;
    private Boolean writeRea;
    private Boolean readCue;
    private Boolean readRea;

    public UserEntityRole(User user, EnabledEntity enabledEntity, EntityRole entityRole,
        Boolean writeCue, Boolean writeRea, Boolean readCue, Boolean readRea
    ) {
        this.user = user;
        this.enabledEntity = enabledEntity;
        this.entityRole = entityRole;
        this.writeCue = writeCue;
        this.writeRea = writeRea;
        this.readCue = readCue;
        this.readRea = readRea;
    }
}
