package com.olivaris.olivaris_app.models;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// This class is to use the user_id and enabled_entity_id as PK in UserEntityAssignment table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserEntityRoleId implements Serializable {

    private Long user;
    private Long enabledEntity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntityRoleId that = (UserEntityRoleId) o;
        return Objects.equals(user, that.user) && 
               Objects.equals(enabledEntity, that.enabledEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, enabledEntity);
    }
}
