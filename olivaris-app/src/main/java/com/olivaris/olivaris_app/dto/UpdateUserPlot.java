package com.olivaris.olivaris_app.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UpdateUserPlot {
    private String plotName;

    @Column(name = "land_register", length = 20)
    private String landRegister;
}
