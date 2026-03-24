package com.olivaris.olivaris_app.services;

import org.springframework.http.ResponseEntity;

import com.olivaris.olivaris_app.dto.CreateActivityRequest;
import com.olivaris.olivaris_app.dto.PhytoActivityDto;
import com.olivaris.olivaris_app.dto.UpdatePhytoActReq;
import com.olivaris.olivaris_app.dto.ActivityDto;

public interface ActivityService {
    ResponseEntity<ActivityDto> create(
        Long userId,
        Long enclosureId,
        Long entityId,
        CreateActivityRequest body
    );

    ResponseEntity<Void> delete(Long id);

    ResponseEntity<PhytoActivityDto> update(
        Long activityId, 
        Long phytoActId,
        UpdatePhytoActReq body
    );
}
