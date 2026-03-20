package com.olivaris.olivaris_app.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreatePhytoActReq {

    @NotNull(message = "{phytoAct.productId.notnull}")
    private Long phytoProductId;

    @NotNull(message = "{phytoAct.date.notnull}")
    private LocalDate applicationDate;

    private String reason;

    @Positive(message = "{phytoAct.dose.positive}")
    private Double dose;

    private String doseUnit;

    @PositiveOrZero(message = "{phytoAct.totalAmount.positiveOrZero}")
    private Double totalAmount;

    private String applicationMethod;

    private String applicationMachinery;

    private String applicatorName;

    @Pattern(
        message = "{phytoAct.appNif.format}",
        regexp = "^[0-9]{8}[A-Za-z]$"
    )
    private String applicatorNif;

    @NotBlank(message = "{phytoAct.crops.notblank}")
    private String crops;

    @Positive(message = "{phytoAct.area.positive}")
    private Double area;
}
