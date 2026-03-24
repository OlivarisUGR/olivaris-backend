package com.olivaris.olivaris_app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.olivaris.olivaris_app.dto.ActivityDto;
import com.olivaris.olivaris_app.dto.CreateActivityRequest;
import com.olivaris.olivaris_app.dto.PhytoActivityDto;
import com.olivaris.olivaris_app.dto.UpdatePhytoActReq;
import com.olivaris.olivaris_app.services.ActivityService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/api/activity")
@AllArgsConstructor
public class ActivityController {

    private final ActivityService actService;

    @PostMapping("/user/{userId}/enclosure/{enclosureId}")
    public ResponseEntity<ActivityDto> create(
        @PathVariable Long userId,
        @PathVariable Long enclosureId,
        @RequestParam(required = false) Long entityId,
        @Valid @RequestBody CreateActivityRequest request
    ) {
        return actService.create(userId, enclosureId, entityId, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return actService.delete(id);
    }

    @PutMapping("/{activityId}")
    public ResponseEntity<PhytoActivityDto> update(
        @PathVariable Long activityId,
        @RequestParam(required = true) Long phytoActId,
        @Valid @RequestBody UpdatePhytoActReq request
    ) {
        return actService.update(activityId, phytoActId, request);
    }
}
