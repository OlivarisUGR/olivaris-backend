package com.olivaris.olivaris_app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.olivaris.olivaris_app.models.EntityRole;
import com.olivaris.olivaris_app.models.UserEntityRole;
import com.olivaris.olivaris_app.models.UserEntityRoleId;

@Repository
public interface UserEntityRoleRepository extends CrudRepository<UserEntityRole, UserEntityRoleId> {
    Optional<UserEntityRole> findByUserId(Long userId);

    @Query("""
        SELECT uer.entityRole FROM UserEntityRole uer
        WHERE uer.user.id = :userId AND uer.enabledEntity.id = :entityId
    """)
    Optional<EntityRole> getEntityRole(Long userId, Long entityId);
}
