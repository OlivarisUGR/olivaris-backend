package com.olivaris.olivaris_app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.olivaris.olivaris_app.models.EntityRole;
import com.olivaris.olivaris_app.models.User;
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

    @Query("""
        SELECT COUNT(uer1) > 0 FROM UserEntityRole uer1, UserEntityRole uer2  
        WHERE uer1.user.id = :userId1 AND uer2.user.id = :userId2
            AND uer1.enabledEntity.id = uer2.enabledEntity.id
    """)
    boolean usersBelongToSameEntity(Long userId1, Long userId2);

    @Query("""
        SELECT COUNT(uer) > 0
        FROM UserEntityRole uer
        WHERE uer.user.id = :userId
            AND uer.enabledEntity.id = :entityId
            AND uer.writeCue = true
            AND uer.writeRea = true
            AND uer.readCue = true
            AND uer.readRea = true
    """)
    boolean givesAllPermToEntity(Long userId, Long entityId);

    @Query("""
        SELECT COUNT(uer) > 0
        FROM UserEntityRole uer
        WHERE uer.user.id = :userId
    """)
    boolean userBelongToAnyEntity(Long userId);

    @Query("""
        SELECT COUNT(uer) > 0 FROM UserEntityRole uer  
        WHERE uer.user.id = :userId 
            AND uer.enabledEntity.id = :entityId   
    """)
    boolean userBelongToEntity(Long userId, Long entityId);

    @Query("""
        SELECT COUNT(uer) > 0
        FROM UserEntityRole uer
        JOIN uer.entityRole er
        WHERE er.name = :roleName
            AND uer.user.id = :userId
            AND uer.enabledEntity.id = :entityId
    """)
    boolean userRoleOnEnt(String roleName, Long userId, Long entityId);

    @Query("""
        SELECT uer.enabledEntity.id 
        FROM UserEntityRole uer
        WHERE uer.user.id = :userId   
    """)
    List<Long> getEntityIdByUserId(Long userId);

    @Query("""
        SELECT DISTINCT uer
        FROM UserEntityRole uer
        WHERE uer.user.id = :userId     
    """)
    List<UserEntityRole> findEntityByUserId(Long userId);

    @Query("""
        SELECT DISTINCT uer.user 
        FROM UserEntityRole uer
        WHERE uer.enabledEntity.id = :entityId    
    """)
    List<User> findUserByEntityId(Long entityId);
}
