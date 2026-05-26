package com.olivaris.olivaris_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CreatePlot {
    @NotBlank(message = "{plot.name.notblank}")
    private String name;

    @NotBlank(message = "{plot.province.notblank}")
    private String province;

    @NotBlank(message = "{plot.city.notblank}")
    private String city;

    @NotBlank(message = "{plot.polygonCode.notblank}")
    private String polygonCode;

    @NotBlank(message = "{plot.plotNum.notblank}")
    private String plotNum;
}
