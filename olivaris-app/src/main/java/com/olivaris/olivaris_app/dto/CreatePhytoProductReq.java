package com.olivaris.olivaris_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreatePhytoProductReq {
    @NotBlank(message = "{phytoProduct.name.notblank}")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "{phytoProduct.oficialRegister.notblank}")
    @Size(max = 100)
    @Pattern(
        regexp = "^[A-Z]{2,3}-[0-9]{3,10}(-[0-9]{1,5})?$",
        message = "{phytoProduct.oficialRegister.format}"
    )
    private String oficialRegister;

    @NotBlank(message = "{phytoProduct.activeSubstance.notblank}")
    @Size(max = 255)
    private String activeSubstance;

    @NotBlank(message = "{phytoProduct.type.notblank}")
    private String type;
}
