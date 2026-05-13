package com.olivaris.olivaris_app.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.olivaris.olivaris_app.dto.CreatePlot;
import com.olivaris.olivaris_app.dto.PlotDto;
import com.olivaris.olivaris_app.dto.PlotEnclosureDto;
import com.olivaris.olivaris_app.dto.UpdateUserPlot;
import com.olivaris.olivaris_app.services.PlotService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;


@RestController
@RequestMapping(value = "/api/plot")
@AllArgsConstructor
public class PlotController {

    private final PlotService plotService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<List<PlotEnclosureDto>> createUserPlot(
        @PathVariable Long userId,
        @Valid @RequestBody CreatePlot request
    ) {
        return plotService.createUserPlot(userId, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return plotService.delete(id);
    }

    // @PostMapping("/{plotId}/user/{userId}")
    // public ResponseEntity<Void> createUserPlot(
    //     @PathVariable Long plotId, 
    //     @PathVariable Long userId,
    //     @RequestBody CreateUserPlot request
    // ) {
    //     return plotService.createUserPlot(plotId, userId, request);
    // }

    @DeleteMapping("/{plotId}/user/{userId}")
    public ResponseEntity<Void> deleteUserPlot(@PathVariable Long plotId, @PathVariable Long userId) {
        return plotService.deleteUserPlot(plotId, userId);
    }

    @GetMapping("/{plotId}")
    public ResponseEntity<PlotDto> getPlot(@PathVariable Long plotId) {
        return plotService.getPlot(plotId);
    }

    @GetMapping("/{plotId}/enclosures")
    public ResponseEntity<PlotEnclosureDto> getPlotEnclosures(@PathVariable Long plotId) {
        return plotService.getPlotEnclosures(plotId);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlotEnclosureDto>> getUserPlots(@PathVariable Long userId) {
        return plotService.getUserPlots(userId);
    }

    @GetMapping("")
    public ResponseEntity<Map<String, Long>> getEnclosureId(
        @RequestParam(required = true) String plotName,
        @RequestParam(required = true) String enclosureName,
        @RequestParam(required = true) Long userId
    ) {
        return plotService.getEnclosureId(plotName, enclosureName, userId);
    }

    @PutMapping("/{plotId}/user/{userId}")
    public ResponseEntity<PlotDto> updateUserPlot(
        @PathVariable Long plotId,
        @PathVariable Long userId,
        @Valid @RequestBody UpdateUserPlot request
    ) {
        return plotService.updateUserPlot(plotId, userId, request);
    }
    
}
