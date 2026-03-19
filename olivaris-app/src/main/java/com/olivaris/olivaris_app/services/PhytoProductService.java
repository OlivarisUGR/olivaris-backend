package com.olivaris.olivaris_app.services;

import org.springframework.http.ResponseEntity;

import com.olivaris.olivaris_app.dto.CreatePhytoProductReq;
import com.olivaris.olivaris_app.dto.PhytoProductDto;

public interface PhytoProductService {
    ResponseEntity<PhytoProductDto> create(CreatePhytoProductReq body);
}
