package com.olivaris.olivaris_app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.olivaris.olivaris_app.models.User;
import java.util.List;


@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("""
            SELECT u.email FROM User u
            JOIN u.roles r
            WHERE r.name = :roleName
        """)
    List<String> getEmailByRol(String roleName);

    @Query("""
           SELECT DISTINCT u.email FROM User u
           JOIN u.roles sr
           JOIN u.userEntity uer
           JOIN uer.enabledEntity ee
           JOIN uer.entityRole er
           WHERE sr.name = :systemRole
                AND ee.nif = :entityNif
                AND er.name = :entityRole
        """)
    List<String> getEmailEntityAdmins(String systemRole, String entityRole, String entityNif);
}
