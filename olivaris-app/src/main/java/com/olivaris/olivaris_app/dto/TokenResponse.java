package com.olivaris.olivaris_app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TokenResponse {
    private String accessToken;
    private String resfreshToken;
}
