package com.olivaris.olivaris_app.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SigpacGeoJsonResponse {
    private String type;
    private List<Map<String, Object>> features;
}
