package com.olivaris.olivaris_app.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olivaris.olivaris_app.dto.CreatePhytoActReq;
import com.olivaris.olivaris_app.models.Activity;
import com.olivaris.olivaris_app.models.PhytoAct;
import com.olivaris.olivaris_app.models.PhytoProduct;
import com.olivaris.olivaris_app.repositories.PhytoProductRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PhytoActServiceImpl implements PhytoActService {

    private final PhytoProductRepository phytoProductRep;

    @Transactional(readOnly = true)
    @Override
    public PhytoAct createPhytoActivity(Activity activity, CreatePhytoActReq body) {
        // Find the product and activity
        PhytoProduct productDb = phytoProductRep.findById(body.getPhytoProductId())
            .orElseThrow(() -> new EntityNotFoundException("El producto con ese ID no existe"));

        // Create the phytosanitary activity and save it
        return PhytoAct.builder()
            .activity(activity)
            .phytoProduct(productDb)
            .applicationDate(body.getApplicationDate())
            .reason(body.getReason() != null ? body.getReason() : null)
            .dose(body.getDose())
            .doseUnit(body.getDoseUnit())
            .totalAmount(body.getTotalAmount() != null ? body.getTotalAmount() : null)
            .applicationMethod(body.getApplicationMethod() != null ? body.getApplicationMethod() : null)
            .applicationMachinery(body.getApplicationMachinery() != null ? body.getApplicationMachinery() : null)
            .applicatorName(body.getApplicatorName() != null ? body.getApplicatorName() : null)
            .applicatorNif(body.getApplicatorNif())
            .crops(body.getCrops())
            .area(body.getArea())
            .createdAt(LocalDateTime.now())
            .build();
    }
}
