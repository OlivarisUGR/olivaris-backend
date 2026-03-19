package com.olivaris.olivaris_app.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.olivaris.olivaris_app.models.PhytoProduct;

@Repository
public interface PhytoProductRepository extends CrudRepository<PhytoProduct, Long> {

}
