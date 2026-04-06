package com.olivaris.olivaris_app.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UpdatePhytoActReq {
    private Long phytoActId;
    private String reason;

    @Positive(message = "{phytoAct.dose.positive}")
    private Double dose;
    private String doseUnit;

    @PositiveOrZero(message = "{phytoAct.totalAmount.positiveOrZero}")
    private Double totalAmount;
    private String applicationMethod;
    private String applicationMachinery;

    @Pattern(
        message = "{phytoAct.appNif.format}",
        regexp = "^[0-9]{8}[A-Za-z]$"
    )
    private String applicatorNif;

    @Positive(message = "{phytoAct.area.positive}")
    private Double area;
}
