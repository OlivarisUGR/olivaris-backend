package com.olivaris.olivaris_app.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.olivaris.olivaris_app.dto.CreatePhytoActReq;
import com.olivaris.olivaris_app.dto.UpdatePhytoActReq;
import com.olivaris.olivaris_app.models.Activity;
import com.olivaris.olivaris_app.models.PhytoAct;
import com.olivaris.olivaris_app.models.PhytoProduct;
import com.olivaris.olivaris_app.repositories.PhytoActRepository;
import com.olivaris.olivaris_app.repositories.PhytoProductRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PhytoActServiceImpl implements PhytoActService {

    private final PhytoProductRepository phytoProductRep;
    private final PhytoActRepository phytoActRep;

    @Transactional(readOnly = true)
    @Override
    @PreAuthorize("@phytoActivityValidator.canCreatePhytoAct(#activity, #body)")
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
            .dose(body.getDose() != null ? body.getDose() : null)
            .doseUnit(body.getDoseUnit() != null ? body.getDoseUnit() : null)
            .totalAmount(body.getTotalAmount() != null ? body.getTotalAmount() : null)
            .applicationMethod(body.getApplicationMethod() != null ? body.getApplicationMethod() : null)
            .applicationMachinery(body.getApplicationMachinery() != null ? body.getApplicationMachinery() : null)
            .applicatorName(body.getApplicatorName() != null ? body.getApplicatorName() : null)
            .applicatorNif(body.getApplicatorNif() != null ? body.getApplicatorNif() : null)
            .crops(body.getCrops())
            .area(body.getArea() != null ? body.getArea() : null)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .build();
    }

    @Transactional
    @Override
    public PhytoAct updatePhytoAct(UpdatePhytoActReq body) {
        PhytoAct phytoActDb = phytoActRep.findById(body.getPhytoActId())
            .orElseThrow(() -> new EntityNotFoundException("La actividad fitosanitaria no existe"));
        
        if(body.getReason() != null) phytoActDb.setReason(body.getReason());
        if(body.getDose() != null) phytoActDb.setDose(body.getDose());
        if(body.getDoseUnit() != null) phytoActDb.setDoseUnit(body.getDoseUnit());
        if(body.getTotalAmount() != null) phytoActDb.setTotalAmount(body.getTotalAmount());
        if(body.getApplicationMehtod() != null) phytoActDb.setApplicationMethod(body.getApplicationMehtod());
        if(body.getApplicationMachinery() != null) phytoActDb.setApplicationMachinery(body.getApplicationMachinery());
        if(body.getApplicatorNif() != null) phytoActDb.setApplicatorNif(body.getApplicatorNif());
        if(body.getArea() != null) phytoActDb.setArea(body.getArea());

        return phytoActRep.save(phytoActDb);
    }
}
