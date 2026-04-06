package com.olivaris.olivaris_app.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.olivaris.olivaris_app.dto.ActivityCreatedResponse;
import com.olivaris.olivaris_app.dto.ActivityDto;
import com.olivaris.olivaris_app.dto.CreateActivityRequest;
import com.olivaris.olivaris_app.dto.CreatePhytoActReq;
import com.olivaris.olivaris_app.dto.UpdateActRequest;
import com.olivaris.olivaris_app.services.ActivityService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping(value = "/api/activity")
@AllArgsConstructor
public class ActivityController {

    private final ActivityService actService;

    @PostMapping("/user/{userId}/enclosure/{enclosureId}")
    public ResponseEntity<ActivityCreatedResponse> create(
        @PathVariable Long userId,
        @PathVariable Long enclosureId,
        @RequestParam(required = false) Long entityId,
        @Valid @RequestBody CreateActivityRequest request
    ) {
        return actService.create(userId, enclosureId, entityId, request);
    }

    @PostMapping("/{activityId}")
    public ResponseEntity<ActivityCreatedResponse> addNewPhytoActivity(
        @PathVariable Long activityId,
        @Valid @RequestBody CreatePhytoActReq request
    ) {
        return actService.addNewPhytoActivity(activityId, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return actService.delete(id);
    }

    @PutMapping("/{activityId}")
    public ResponseEntity<ActivityDto> updateActivity(
        @PathVariable Long activityId,
        @Valid @RequestBody UpdateActRequest request
    ) {
        return actService.updateActivity(activityId, request);
    }

    // If season is passed -> get enclosure activities from that season
    @GetMapping("/user/{userId}/enclosure/{enclosureId}")
    public ResponseEntity<List<ActivityDto>> getEnclosuresAct(
        @PathVariable Long userId,
        @PathVariable Long enclosureId,
        @RequestParam(required = false) Long entityId,
        @RequestParam(required = false) String season
    ) {
        return actService.getEnclosuresActByUser(userId, enclosureId, entityId, season);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivityDto>> getUserActivities(@PathVariable Long userId) {
        return actService.getUserActivities(userId);
    }
    
}
