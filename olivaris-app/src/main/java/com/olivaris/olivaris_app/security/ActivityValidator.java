package com.olivaris.olivaris_app.security;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.olivaris.olivaris_app.dto.CreateActivityRequest;
import com.olivaris.olivaris_app.models.enums.ActivityStatus;

@Component("activityValidator")
public class ActivityValidator {

    private static final int LIMIT_DATE = 1;

    public boolean checkDateAndStatus(CreateActivityRequest body) {
        if(body.getDate().isAfter(LocalDate.now().plusYears(LIMIT_DATE))) {
            throw new IllegalArgumentException("No puedes planificar actividades a más de un año vista");
        } else if(body.getDate().isBefore(LocalDate.now()) && body.getStatus() != null &&
                    body.getStatus().equals(ActivityStatus.PLANNED)) {
            throw new IllegalArgumentException("No puedes planificar actividades para días pasados");
        } else if(body.getDate().isAfter(LocalDate.now()) && body.getStatus() != null &&
                    body.getStatus().equals(ActivityStatus.COMPLETED)) {
            throw new IllegalArgumentException("No puedes completar actividades para días futuros");
        }

        return true;
    }
}
