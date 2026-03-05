package com.olivaris.olivaris_app.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.olivaris.olivaris_app.models.EnabledEntity;

@Repository
public interface EntityRepository extends CrudRepository<EnabledEntity, Long> {

    Optional<EnabledEntity> findByNif(String nif);
}
