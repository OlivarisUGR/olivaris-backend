package com.olivaris.olivaris_app.services;

import org.springframework.http.ResponseEntity;

import com.olivaris.olivaris_app.dto.CreateActivityRequest;
import com.olivaris.olivaris_app.dto.ActivityDto;

public interface ActivityService {
    ResponseEntity<ActivityDto> create(CreateActivityRequest body);
    ResponseEntity<Void> delete(Long id);
}
