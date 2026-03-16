package com.olivaris.olivaris_app.services;

import java.util.List;
import java.util.Map;

import com.olivaris.olivaris_app.models.Enclosure;
import com.olivaris.olivaris_app.models.Plot;

public interface EnclosureService {
    List<Enclosure> createPlotEnclosures(List<Map<String, Object>> enclosuresCoord, Plot plot);
}
