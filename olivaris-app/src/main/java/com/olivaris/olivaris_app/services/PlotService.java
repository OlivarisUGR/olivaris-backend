package com.olivaris.olivaris_app.services;

import org.springframework.http.ResponseEntity;

import com.olivaris.olivaris_app.dto.CreatePlot;
import com.olivaris.olivaris_app.dto.PlotDto;
import com.olivaris.olivaris_app.models.Plot;

public interface PlotService {
    ResponseEntity<PlotDto> create(CreatePlot request);
    Plot createPlotAndEnclosures(CreatePlot request);
    ResponseEntity<Void> delete(Long id);
}
