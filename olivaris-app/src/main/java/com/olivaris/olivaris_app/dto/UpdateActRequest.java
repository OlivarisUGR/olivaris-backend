package com.olivaris.olivaris_app.dto;

import java.util.List;

import com.olivaris.olivaris_app.models.enums.ActivityStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UpdateActRequest {
    private String description;
    private ActivityStatus status;
    private List<UpdatePhytoActReq> phytoActs;
}