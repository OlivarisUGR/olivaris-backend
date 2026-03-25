package com.olivaris.olivaris_app.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ActivityCreatedResponse {
    private Long activityId;
    private List<Long> phytoActId;
    private LocalDateTime createdAt;
    private String message;
}
