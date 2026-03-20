package com.olivaris.olivaris_app.dto;

import java.time.LocalDate;
import java.util.List;

import com.olivaris.olivaris_app.models.enums.ActivityStatus;
import com.olivaris.olivaris_app.models.enums.ActivityType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateActivityRequest {

    @NotNull(message = "{activity.userId.notnull}")
    private Long userId;

    @NotNull(message = "{activity.enclosureId.notnull}")
    private Long enclosureId;

    @NotNull(message = "{activity.date.notnull}")
    private LocalDate date;

    @NotBlank(message = "{activity.season.notblank}")
    @Pattern(regexp = "^(19|20)\\d{2}$", message = "{activity.season.format}")
    private String season;

    private String description;

    @NotNull(message = "{activity.type.notnull}")
    @Enumerated(EnumType.STRING)
    private ActivityType type;

    @Enumerated(EnumType.STRING)
    private ActivityStatus status;

    // Phytosanitary activity info
    @Valid
    @NotNull(message = "{activity.phytoAct.notnull}")
    private List<CreatePhytoActReq> phytoAct;
}
