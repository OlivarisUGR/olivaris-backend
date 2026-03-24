package com.olivaris.olivaris_app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.olivaris.olivaris_app.models.EntityRole;

@Repository
public interface EntityRoleRepository extends CrudRepository<EntityRole, Long> {
    Optional<EntityRole> findByName(String entityRole);

    @Query("""
       SELECT er.id FROM EntityRole er
       WHERE er.name = :name
    """)
    Optional<Long> getRoleIdByName(String name);
}
