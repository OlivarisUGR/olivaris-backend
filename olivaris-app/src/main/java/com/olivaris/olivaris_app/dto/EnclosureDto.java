package com.olivaris.olivaris_app.dto;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

import com.olivaris.olivaris_app.models.Enclosure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EnclosureDto {

    private Long id;
    private String name;
    private Double area;
    private String sigpacUse;
    private GeoJsonPolygonDto polygon;

    public static EnclosureDto fromEntity(Enclosure enclosure) {
        return new EnclosureDto(
            enclosure.getId(),
            enclosure.getName(),
            enclosure.getArea(),
            enclosure.getSigpacUse(),
            toGeoJson(enclosure.getGeometry())
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
