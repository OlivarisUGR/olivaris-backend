package com.olivaris.olivaris_app.services;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.olivaris.olivaris_app.dto.ActivityDto;
import com.olivaris.olivaris_app.dto.CreateActivityRequest;
import com.olivaris.olivaris_app.dto.CreatePhytoActReq;
import com.olivaris.olivaris_app.dto.UpdateActRequest;
import com.olivaris.olivaris_app.dto.ActivityCreatedResponse;

public interface ActivityService {
    ResponseEntity<ActivityCreatedResponse> create(
        Long userId,
        Long enclosureId,
        Long entityId,
        CreateActivityRequest body
    );

    ResponseEntity<Void> delete(Long id);

    ResponseEntity<ActivityDto> updateActivity(
        Long activityId, 
        UpdateActRequest body
    );

    ResponseEntity<List<ActivityDto>> getEnclosuresActByUser(
        Long userId, 
        Long enclosureId, 
        Long entityId,
        String season
    );

    ResponseEntity<List<ActivityDto>> getUserActivities(Long userId);
    ResponseEntity<ActivityCreatedResponse> addNewPhytoActivity(
        Long activityId, 
        CreatePhytoActReq phytoActInfo
    );
}
