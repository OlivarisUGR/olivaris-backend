package com.olivaris.olivaris_app.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

import com.olivaris.olivaris_app.models.Enclosure;
import com.olivaris.olivaris_app.models.Plot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PlotEnclosureDto {
    
    private Long id;
    private String provinceCode;
    private String cityCode;
    private String polygonCode;
    private String plotNum;
    private String plotName;
    private String landRegister;
    private Double area;
    private GeoJsonPolygonDto polygon;
    private List<EnclosureDto> enclosures;

    public static PlotEnclosureDto fromEntity(Plot plot) {
        return fromEntity(plot, null);
    }

    public static PlotEnclosureDto fromEntity(Plot plot, String plotName) {
        return new PlotEnclosureDto(
            plot.getId(),
            plot.getProvinceCode(),
            plot.getCityCode(),
            plot.getPolygonCode(),
            plot.getPlotNum(),
            plotName,
            plot.getLandRegister(),
            plot.getArea(),
            toGeoJson(plot.getGeometry()),
            (plot.getEnclosures() == null ? Collections.<Enclosure>emptyList() : plot.getEnclosures()).stream()
                .map(EnclosureDto::fromEntity)
                .toList()
        );
    }

    private static GeoJsonPolygonDto toGeoJson(Polygon polygon) {
        if(polygon == null) return null;

        List<List<List<Double>>> rings = new ArrayList<>();
        rings.add(toRing(polygon.getExteriorRing().getCoordinates()));

        return new GeoJsonPolygonDto(
            "Polygon", 
            rings
        );
    }

    private static List<List<Double>> toRing(Coordinate[] coords) {
        List<List<Double>> ring = new ArrayList<>();
        
        for (Coordinate c : coords) {
            // GeoJSON -> [longitud, latitud]
            ring.add(List.of(c.getX(), c.getY()));
        }

        return ring;
    }
}
