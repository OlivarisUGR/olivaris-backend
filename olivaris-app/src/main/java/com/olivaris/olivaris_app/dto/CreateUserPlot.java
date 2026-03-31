package com.olivaris.olivaris_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CreateUserPlot {
    @NotBlank(message = "{plot.name.notblank}")
    private String name;
}
