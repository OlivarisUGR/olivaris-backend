package com.olivaris.olivaris_app.dto;

import com.olivaris.olivaris_app.models.PhytoProduct;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PhytoProductDto {
    private Long id;
    private String name;
    private String oficialRegister;

    public static PhytoProductDto fromEntity(PhytoProduct phytoProduct) {
        return new PhytoProductDto(
            phytoProduct.getId(),
            phytoProduct.getName(),
            phytoProduct.getOficialRegister()
        );
    }
}
