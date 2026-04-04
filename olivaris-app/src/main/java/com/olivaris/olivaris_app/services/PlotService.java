package com.olivaris.olivaris_app.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.olivaris.olivaris_app.dto.CreatePlot;
// import com.olivaris.olivaris_app.dto.CreateUserPlot;
import com.olivaris.olivaris_app.dto.PlotDto;
import com.olivaris.olivaris_app.dto.PlotEnclosureDto;
import com.olivaris.olivaris_app.models.Plot;

public interface PlotService {
    ResponseEntity<List<PlotEnclosureDto>> createUserPlot(Long userId, CreatePlot request);
    Plot createPlotAndEnclosures(CreatePlot request);
    ResponseEntity<Void> delete(Long id);
    ResponseEntity<Void> deleteUserPlot(Long plotId, Long userId);
    ResponseEntity<PlotDto> getPlot(Long plotId);
    ResponseEntity<PlotEnclosureDto> getPlotEnclosures(Long plotId);
    ResponseEntity<List<PlotEnclosureDto>> getUserPlots(Long userId);
    ResponseEntity<Map<String, Long>> getEnclosureId(
        String plotName, 
        String enclosureName,
        Long userId
    );
    // ResponseEntity<Void> createUserPlot(Long plotId, Long userId, CreateUserPlot request);
}
