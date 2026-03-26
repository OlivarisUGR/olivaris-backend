package com.olivaris.olivaris_app.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GeoJsonPolygonDto {
    private String type;
    private List<List<List<Double>>> coordinates;
}
