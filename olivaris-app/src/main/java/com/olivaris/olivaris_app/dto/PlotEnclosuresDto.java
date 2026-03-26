package com.olivaris.olivaris_app.dto;

import java.util.List;

import com.olivaris.olivaris_app.models.Plot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PlotEnclosuresDto {

    // Plot information
    private Long id;
    private String provinceCode;
    private String cityCode;
    private String polygonCode;
    private String plotNum;
    private String landRegister;
    private Double area;

    // Enclosures information
    private List<EnclosureDto> enclosuresDto;

    public static PlotEnclosuresDto fromEntity(Plot plot) {
        return new PlotEnclosuresDto(
            plot.getId(),
            plot.getProvinceCode(),
            plot.getCityCode(),
            plot.getPolygonCode(),
            plot.getPlotNum(),
            plot.getLandRegister(),
            plot.getArea(),
            plot.getEnclosures().stream()
                .map(EnclosureDto::fromEntity)
                .toList()
        );
    }
}
