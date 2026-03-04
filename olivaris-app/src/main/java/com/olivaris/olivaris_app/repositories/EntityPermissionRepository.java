package com.olivaris.olivaris_app.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.olivaris.olivaris_app.models.EntityPermission;

@Repository
public interface EntityPermissionRepository extends CrudRepository<EntityPermission, Long> {
    Optional<EntityPermission> findByName(String entityPermission);
}
