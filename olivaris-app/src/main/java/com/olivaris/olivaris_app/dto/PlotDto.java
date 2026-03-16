package com.olivaris.olivaris_app.dto;

import java.util.List;

import com.olivaris.olivaris_app.models.Enclosure;
import com.olivaris.olivaris_app.models.Plot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PlotDto {
    private String provinceCode;
    private String cityCode;
    private String polygonCode;
    private String plotNum;
    private String landRegister;
    private Double area;
    private List<String> enclosuresNames;

    public static PlotDto fromEntity(Plot plot) {
        return new PlotDto(
            plot.getProvinceCode(),
            plot.getCityCode(),
            plot.getPolygonCode(),
            plot.getPlotNum(),
            plot.getLandRegister(),
            plot.getArea(),
            plot.getEnclosures().stream()
                .map(Enclosure::getName)
                .toList()
        );
    }
}
