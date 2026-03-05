package com.olivaris.olivaris_app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.olivaris.olivaris_app.models.EntityRole;
import com.olivaris.olivaris_app.models.UserEntityRole;

@Repository
public interface UserEntityRoleRepository extends CrudRepository<UserEntityRole, Long> {
    Optional<UserEntityRole> findByUserId(Long userId);

    @Query("""
        SELECT uer FROM UserEntityRole uer
        WHERE uer.user.id = :userId AND uer.enabledEntity.id = :entityId
    """)
    Optional<UserEntityRole> findByUserIdAndEnabledEntityId(Long userId, Long entityId);

    @Query("""
        SELECT uer.entityRole FROM UserEntityRole uer
        WHERE uer.user.id = :userId AND uer.enabledEntity.id = :entityId
    """)
    Optional<EntityRole> getEntityRole(Long userId, Long entityId);

    @Query("""
        SELECT uer FROM UserEntityRole uer 
        WHERE uer.user.id = :userId AND uer.enabledEntity.id = :entityId
        """)
    Optional<UserEntityRole> getByUserIdAndEntityId(Long userId, Long entityId);
}
