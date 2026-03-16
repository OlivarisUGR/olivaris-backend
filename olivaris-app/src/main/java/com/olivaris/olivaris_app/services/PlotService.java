package com.olivaris.olivaris_app.services;

import org.springframework.http.ResponseEntity;

import com.olivaris.olivaris_app.dto.CreatePlot;
import com.olivaris.olivaris_app.dto.PlotDto;

public interface PlotService {
    ResponseEntity<PlotDto> create(CreatePlot request);
    ResponseEntity<Void> delete(Long id);
}
