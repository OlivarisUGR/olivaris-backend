package com.olivaris.olivaris_app.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olivaris.olivaris_app.dto.CreatePhytoProductReq;
import com.olivaris.olivaris_app.dto.PhytoProductDto;
import com.olivaris.olivaris_app.models.PhytoProduct;
import com.olivaris.olivaris_app.repositories.PhytoProductRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PhytoProductServiceImpl implements PhytoProductService {

    private final PhytoProductRepository phytoProductRep;

    @Transactional
    @Override
    public ResponseEntity<PhytoProductDto> create(CreatePhytoProductReq body) {
        PhytoProduct phytoProduct = PhytoProduct.builder()
            .name(body.getName())
            .oficialRegister(body.getOficialRegister())
            .activeSubstance(body.getActiveSubstance())
            .type(body.getType())
            .build();
        
        PhytoProduct productDb = phytoProductRep.save(phytoProduct);

        PhytoProductDto productDto = new PhytoProductDto(
            productDb.getId(),
            productDb.getName(),
            productDb.getOficialRegister()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(productDto);
    }
}
