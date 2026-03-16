package com.olivaris.olivaris_app.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.olivaris.olivaris_app.models.Enclosure;

@Repository
public interface EnclosureRepository extends CrudRepository<Enclosure, Long>{

}
