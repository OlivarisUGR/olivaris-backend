package com.olivaris.olivaris_app.services;

import com.olivaris.olivaris_app.models.CustomUserDetails;

public interface JwtService {
    String createToken(CustomUserDetails user);
    String createRefreshToken(CustomUserDetails user);
}
