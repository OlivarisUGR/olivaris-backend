package com.olivaris.olivaris_app.dto;

import com.olivaris.olivaris_app.models.enums.ActivityStatus;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdatePhytoActReq {
    private String description;
    private ActivityStatus status;
    private String reason;
    private Double dose;
    private String doseUnit;
    private Double totalAmount;
    private String applicationMehtod;
    private String applicationMachinery;

    @Pattern(
        message = "{phytoAct.appNif.format}",
        regexp = "^[0-9]{8}[A-Za-z]$"
    )
    private String applicatorNif;

    private Double area;
}
