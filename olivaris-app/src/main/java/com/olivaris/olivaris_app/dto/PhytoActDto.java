package com.olivaris.olivaris_app.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.olivaris.olivaris_app.models.PhytoAct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PhytoActDto {

    private Long id;
    private String phytoProductName;
    private LocalDate applicationDate;
    private String reason;
    private Double dose;
    private String doseUnit;
    private Double totalAmount;
    private String applicationMethod;
    private String applicationMachinery;
    private String applicatorName;
    private String applicatorNif;
    private String crops;
    private Double area;
    private LocalDateTime createdAt;

    public static PhytoActDto fromEntity(PhytoAct phytoAct) {
        return new PhytoActDto(
            phytoAct.getId(), 
            phytoAct.getPhytoProduct().getName(),
            phytoAct.getApplicationDate(),
            phytoAct.getReason(),
            phytoAct.getDose(),
            phytoAct.getDoseUnit(),
            phytoAct.getTotalAmount(),
            phytoAct.getApplicationMethod(),
            phytoAct.getApplicationMachinery(),
            phytoAct.getApplicatorName(),
            phytoAct.getApplicatorNif(),
            phytoAct.getCrops(),
            phytoAct.getArea(),
            phytoAct.getCreatedAt());
    }
}
